package com.devsync.analyzer;

import com.devsync.detectors.*;
import com.devsync.config.AnalysisConfig;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ParseResult;
import java.io.File;
import java.util.*;
import java.util.logging.Logger;

public class CodeAnalysisEngine {
    
    private static final Logger logger = Logger.getLogger(CodeAnalysisEngine.class.getName());
    
    private final Map<String, Object> detectors = new LinkedHashMap<>();
    
    private Map<String, Boolean> enabledDetectors;
    private Integer maxMethodLength;
    private Integer maxParameterCount;
    private Integer maxIdentifierLength;
    
    public CodeAnalysisEngine() {
        initializeDetectors();
    }
    
    public void configureFromSettings(com.devsync.model.UserSettings settings) {
        if (settings == null) {
            logger.warning("Settings is null, using defaults");
            return;
        }
        
        this.enabledDetectors = new HashMap<>();
        enabledDetectors.put("MissingDefaultDetector", settings.getMissingDefaultEnabled());
        enabledDetectors.put("EmptyCatchDetector", settings.getEmptyCatchEnabled());
        enabledDetectors.put("LongMethodDetector", settings.getLongMethodEnabled());
        enabledDetectors.put("LongParameterListDetector", settings.getLongParameterEnabled());
        enabledDetectors.put("MagicNumberDetector", settings.getMagicNumberEnabled());
        enabledDetectors.put("LongIdentifierDetector", settings.getLongIdentifierEnabled());
        
        System.out.println("=== CodeAnalysisEngine Configuration ===");
        System.out.println("MissingDefaultDetector: " + settings.getMissingDefaultEnabled());
        System.out.println("EmptyCatchDetector: " + settings.getEmptyCatchEnabled());
        System.out.println("LongMethodDetector: " + settings.getLongMethodEnabled());
        System.out.println("LongParameterListDetector: " + settings.getLongParameterEnabled());
        System.out.println("MagicNumberDetector: " + settings.getMagicNumberEnabled());
        System.out.println("LongIdentifierDetector: " + settings.getLongIdentifierEnabled());
        
        this.maxMethodLength = settings.getMaxMethodLength();
        this.maxParameterCount = settings.getMaxParameterCount();
        this.maxIdentifierLength = settings.getMaxIdentifierLength();
        
        // Update detector configurations
        if (detectors.get("LongMethodDetector") instanceof LongMethodDetector) {
            ((LongMethodDetector) detectors.get("LongMethodDetector")).setMaxLength(maxMethodLength);
        }
        if (detectors.get("LongParameterListDetector") instanceof LongParameterListDetector) {
            ((LongParameterListDetector) detectors.get("LongParameterListDetector")).setMaxParameters(maxParameterCount);
        }
        if (detectors.get("LongIdentifierDetector") instanceof LongIdentifierDetector) {
            ((LongIdentifierDetector) detectors.get("LongIdentifierDetector")).setMaxLength(maxIdentifierLength);
        }
    }
    
    private void initializeDetectors() {
        detectors.put("MissingDefaultDetector", new MissingDefaultDetector());
        detectors.put("EmptyCatchDetector", new EmptyCatchDetector());
        
        LongMethodDetector longMethodDetector = new LongMethodDetector();
        longMethodDetector.setMaxLength(AnalysisConfig.DEFAULT_MAX_METHOD_LENGTH);
        detectors.put("LongMethodDetector", longMethodDetector);
        
        LongParameterListDetector paramDetector = new LongParameterListDetector();
        paramDetector.setMaxParameters(AnalysisConfig.DEFAULT_MAX_PARAMETER_COUNT);
        detectors.put("LongParameterListDetector", paramDetector);
        
        detectors.put("MagicNumberDetector", new MagicNumberDetector());
        
        LongIdentifierDetector identifierDetector = new LongIdentifierDetector();
        identifierDetector.setMaxLength(AnalysisConfig.DEFAULT_MAX_IDENTIFIER_LENGTH);
        detectors.put("LongIdentifierDetector", identifierDetector);
        
        detectors.put("BrokenModularizationDetector", new BrokenModularizationDetector());
        detectors.put("ComplexConditionalDetector", new ComplexConditionalDetector());
        detectors.put("DeficientEncapsulationDetector", new DeficientEncapsulationDetector());
        detectors.put("LongStatementDetector", new LongStatementDetector());
        detectors.put("UnnecessaryAbstractionDetector", new UnnecessaryAbstractionDetector());
    }
    
    public Map<String, Object> analyzeProject(String projectPath) {
        Map<String, Object> results = new HashMap<>();
        List<String> allIssues = new ArrayList<>();
        Map<String, Integer> severityCounts = new HashMap<>();
        Map<String, Integer> detectorCounts = new HashMap<>();
        
        JavaFileCollector collector = new JavaFileCollector();
        List<File> javaFiles = collector.collectJavaFiles(projectPath);
        
        int processedFiles = 0;
        int totalLOC = 0;
        for (File file : javaFiles) {
            if (AnalysisConfig.shouldExclude(file.getPath())) {
                continue;
            }
            
            try {
                JavaParser parser = new JavaParser();
                ParseResult<CompilationUnit> parseResult = parser.parse(file);
                
                if (parseResult.isSuccessful() && parseResult.getResult().isPresent()) {
                    CompilationUnit cu = parseResult.getResult().get();
                    cu.setStorage(file.toPath());
                    
                    List<String> fileIssues = analyzeFile(cu, file.getName(), detectorCounts);
                    allIssues.addAll(fileIssues);
                    updateSeverityCounts(fileIssues, severityCounts);
                    
                    // Count lines of code
                    totalLOC += LOCCounter.countLinesOfCode(cu);
                    
                    processedFiles++;
                } else {
                    String errors = parseResult.getProblems().toString();
                    allIssues.add("❌ [ParseError] " + file.getName() + " - Parse errors: " + errors);
                }
            } catch (Exception e) {
                allIssues.add("❌ [ParseError] " + file.getName() + " - Exception: " + e.getMessage());
            }
        }
        
        results.put("issues", allIssues);
        results.put("totalFiles", javaFiles.size());
        results.put("processedFiles", processedFiles);
        results.put("totalIssues", allIssues.size());
        results.put("severityCounts", severityCounts);
        results.put("detectorCounts", detectorCounts);
        results.put("totalLOC", totalLOC);
        results.put("summary", generateSummary(severityCounts, processedFiles));
        
        return results;
    }
    
    private List<String> analyzeFile(CompilationUnit cu, String fileName, Map<String, Integer> detectorCounts) {
        List<String> issues = new ArrayList<>();
        
        for (Map.Entry<String, Object> entry : detectors.entrySet()) {
            String detectorName = entry.getKey();
            Object detector = entry.getValue();
            
            // Check if detector is enabled in user settings
            if (enabledDetectors != null && enabledDetectors.containsKey(detectorName)) {
                Boolean isEnabled = enabledDetectors.get(detectorName);
                if (isEnabled == null || !isEnabled) {
                    // User explicitly disabled this detector
                    continue;
                }
                // User enabled this detector, proceed with analysis
            } else {
                // No user settings for this detector, check default config
                if (!AnalysisConfig.isDetectorEnabled(detectorName, null)) {
                    continue;
                }
            }
            
            try {
                List<String> detectorIssues = (List<String>) detector.getClass()
                    .getMethod("detect", CompilationUnit.class)
                    .invoke(detector, cu);
                
                if (detectorIssues != null && !detectorIssues.isEmpty()) {
                    issues.addAll(detectorIssues);
                    detectorCounts.merge(detectorName, detectorIssues.size(), Integer::sum);
                }
            } catch (Exception e) {
                issues.add("⚠️ [DetectorError] " + fileName + " - " + detectorName + " failed: " + e.getMessage());
            }
        }
        
        return issues;
    }
    
    private void updateSeverityCounts(List<String> issues, Map<String, Integer> counts) {
        for (String issue : issues) {
            String severity;
            if (issue.startsWith("🔴")) {
                severity = "Critical";
            } else if (issue.startsWith("🟡")) {
                severity = "High";
            } else if (issue.startsWith("🟠")) {
                severity = "Medium";
            } else if (issue.startsWith("⚪")) {
                severity = "Low";
            } else {
                severity = "Error";
            }
            counts.merge(severity, 1, Integer::sum);
        }
    }
    
    private String generateSummary(Map<String, Integer> counts, int fileCount) {
        int total = counts.values().stream().mapToInt(Integer::intValue).sum();
        return String.format("Analyzed %d files, found %d issues (🔴 %d critical, 🟡 %d high, 🟠 %d medium, ⚪ %d low)", 
            fileCount, total, 
            counts.getOrDefault("Critical", 0),
            counts.getOrDefault("High", 0),
            counts.getOrDefault("Medium", 0),
            counts.getOrDefault("Low", 0));
    }
}