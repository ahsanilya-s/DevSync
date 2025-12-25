package com.devsync.analyzer;

import com.devsync.detectors.*;
import com.devsync.config.AnalysisConfig;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ParseResult;
import org.springframework.stereotype.Component;
import java.io.File;
import java.util.*;
import java.util.logging.Logger;

@Component
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
        enabledDetectors.put("LongMethodDetector", settings.getLongMethodEnabled());
        enabledDetectors.put("LongParameterListDetector", settings.getLongParameterEnabled());
        enabledDetectors.put("LongIdentifierDetector", settings.getLongIdentifierEnabled());
        enabledDetectors.put("MagicNumberDetector", settings.getMagicNumberEnabled());
        enabledDetectors.put("MissingDefaultDetector", settings.getMissingDefaultEnabled());
        enabledDetectors.put("EmptyCatchDetector", settings.getEmptyCatchEnabled());
        enabledDetectors.put("ComplexConditionalDetector", settings.getComplexConditionalEnabled());
        enabledDetectors.put("LongStatementDetector", settings.getLongStatementEnabled());
        enabledDetectors.put("BrokenModularizationDetector", settings.getBrokenModularizationEnabled());
        enabledDetectors.put("DeficientEncapsulationDetector", settings.getDeficientEncapsulationEnabled());
        enabledDetectors.put("UnnecessaryAbstractionDetector", settings.getUnnecessaryAbstractionEnabled());
        enabledDetectors.put("MemoryLeakDetector", settings.getMemoryLeakEnabled() != null ? settings.getMemoryLeakEnabled() : true);
        enabledDetectors.put("UnusedVariableDetector", settings.getUnusedVariableEnabled() != null ? settings.getUnusedVariableEnabled() : true);
        
        System.out.println("=== CodeAnalysisEngine Configuration ===");
        enabledDetectors.forEach((name, enabled) -> 
            System.out.println(name + ": " + enabled));
        
        // Configure Long Method Detector
        if (detectors.get("LongMethodDetector") instanceof LongMethodDetector) {
            LongMethodDetector detector = (LongMethodDetector) detectors.get("LongMethodDetector");
            detector.setMaxLength(settings.getMaxMethodLength());
            detector.setMaxComplexity(settings.getMaxMethodComplexity());
        }
        
        // Configure Long Parameter List Detector
        if (detectors.get("LongParameterListDetector") instanceof LongParameterListDetector) {
            ((LongParameterListDetector) detectors.get("LongParameterListDetector"))
                .setMaxParameters(settings.getMaxParameterCount());
        }
        
        // Configure Long Identifier Detector
        if (detectors.get("LongIdentifierDetector") instanceof LongIdentifierDetector) {
            LongIdentifierDetector detector = (LongIdentifierDetector) detectors.get("LongIdentifierDetector");
            detector.setMaxLength(settings.getMaxIdentifierLength());
            detector.setMinLength(settings.getMinIdentifierLength());
        }
        
        // Configure Magic Number Detector
        if (detectors.get("MagicNumberDetector") instanceof MagicNumberDetector) {
            ((MagicNumberDetector) detectors.get("MagicNumberDetector"))
                .setThreshold(settings.getMagicNumberThreshold());
        }
        
        // Configure Complex Conditional Detector
        if (detectors.get("ComplexConditionalDetector") instanceof ComplexConditionalDetector) {
            ComplexConditionalDetector detector = (ComplexConditionalDetector) detectors.get("ComplexConditionalDetector");
            detector.setMaxOperators(settings.getMaxConditionalOperators());
            detector.setMaxNestingDepth(settings.getMaxNestingDepth());
        }
        
        // Configure Long Statement Detector
        if (detectors.get("LongStatementDetector") instanceof LongStatementDetector) {
            LongStatementDetector detector = (LongStatementDetector) detectors.get("LongStatementDetector");
            detector.setMaxTokens(settings.getMaxStatementTokens());
            detector.setMaxChars(settings.getMaxStatementChars());
            detector.setMaxChainLength(settings.getMaxMethodChainLength());
        }
        
        // Configure Broken Modularization Detector
        if (detectors.get("BrokenModularizationDetector") instanceof BrokenModularizationDetector) {
            BrokenModularizationDetector detector = (BrokenModularizationDetector) detectors.get("BrokenModularizationDetector");
            detector.setMaxResponsibilities(settings.getMaxResponsibilities());
            detector.setMinCohesion(settings.getMinCohesionIndex());
            detector.setMaxCoupling(settings.getMaxCouplingCount());
        }
        
        // Configure Unnecessary Abstraction Detector
        if (detectors.get("UnnecessaryAbstractionDetector") instanceof UnnecessaryAbstractionDetector) {
            ((UnnecessaryAbstractionDetector) detectors.get("UnnecessaryAbstractionDetector"))
                .setMaxUsage(settings.getMaxAbstractionUsage());
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
        detectors.put("MemoryLeakDetector", new MemoryLeakDetector());
        detectors.put("UnusedVariableDetector", new UnusedVariableDetector());
    }
    
    public Map<String, Object> analyzeProject(String projectPath) {
        System.out.println("\n========================================");
        System.out.println("🔍 STARTING PROJECT ANALYSIS");
        System.out.println("Project Path: " + projectPath);
        System.out.println("========================================\n");
        
        Map<String, Object> results = new HashMap<>();
        List<String> allIssues = new ArrayList<>();
        Map<String, Integer> severityCounts = new HashMap<>();
        Map<String, Integer> detectorCounts = new HashMap<>();
        
        JavaFileCollector collector = new JavaFileCollector();
        List<File> javaFiles = collector.collectJavaFiles(projectPath);
        
        System.out.println("📁 Found " + javaFiles.size() + " Java files");
        
        int processedFiles = 0;
        int totalLOC = 0;
        int totalClasses = 0;
        int totalMethods = 0;
        int largeClasses = 0;
        int totalComplexity = 0;
        Set<String> packages = new HashSet<>();
        
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
                    int fileLOC = LOCCounter.countLinesOfCode(cu);
                    totalLOC += fileLOC;
                    
                    // Count classes and methods
                    int fileClasses = cu.findAll(com.github.javaparser.ast.body.ClassOrInterfaceDeclaration.class).size();
                    totalClasses += fileClasses;
                    
                    int fileMethods = cu.findAll(com.github.javaparser.ast.body.MethodDeclaration.class).size();
                    totalMethods += fileMethods;
                    
                    // Count large classes (>500 LOC)
                    if (fileLOC > 500 && fileClasses > 0) {
                        largeClasses++;
                    }
                    
                    // Calculate complexity
                    totalComplexity += cu.findAll(com.github.javaparser.ast.stmt.IfStmt.class).size() +
                                      cu.findAll(com.github.javaparser.ast.stmt.ForStmt.class).size() +
                                      cu.findAll(com.github.javaparser.ast.stmt.WhileStmt.class).size();
                    
                    // Extract package
                    cu.getPackageDeclaration().ifPresent(pkg -> packages.add(pkg.getNameAsString()));
                    
                    processedFiles++;
                } else {
                    String errors = parseResult.getProblems().toString();
                    allIssues.add("❌ [ParseError] " + file.getName() + " - Parse errors: " + errors);
                }
            } catch (Exception e) {
                allIssues.add("❌ [ParseError] " + file.getName() + " - Exception: " + e.getMessage());
            }
        }
        
        System.out.println("\n========================================");
        System.out.println("✅ ANALYSIS COMPLETE");
        System.out.println("Total Files: " + javaFiles.size());
        System.out.println("Processed Files: " + processedFiles);
        System.out.println("Total Issues Found: " + allIssues.size());
        System.out.println("Severity Breakdown: " + severityCounts);
        System.out.println("Detector Breakdown: " + detectorCounts);
        System.out.println("========================================\n");
        
        results.put("issues", allIssues);
        results.put("totalFiles", javaFiles.size());
        results.put("processedFiles", processedFiles);
        results.put("totalIssues", allIssues.size());
        results.put("severityCounts", severityCounts);
        results.put("detectorCounts", detectorCounts);
        results.put("totalLOC", totalLOC);
        results.put("totalClasses", totalClasses);
        results.put("totalMethods", totalMethods);
        results.put("totalPackages", packages.size());
        results.put("largeClasses", largeClasses);
        results.put("avgComplexity", totalClasses > 0 ? (double) totalComplexity / totalClasses : 0.0);
        results.put("summary", generateSummary(severityCounts, processedFiles));
        
        return results;
    }
    
    private List<String> analyzeFile(CompilationUnit cu, String fileName, Map<String, Integer> detectorCounts) {
        List<String> issues = new ArrayList<>();
        
        for (Map.Entry<String, Object> entry : detectors.entrySet()) {
            String detectorName = entry.getKey();
            Object detector = entry.getValue();
            
            // Check if detector is enabled
            if (enabledDetectors != null && enabledDetectors.containsKey(detectorName)) {
                Boolean isEnabled = enabledDetectors.get(detectorName);
                if (isEnabled == null || !isEnabled) {
                    System.out.println("⏭️ Skipping " + detectorName + " (disabled by user)");
                    continue;
                }
            } else {
                // Detector not in settings - enable by default
                System.out.println("✅ Running " + detectorName + " (enabled by default)");
            }
            
            System.out.println("🔍 Running detector: " + detectorName + " on file: " + fileName);
            
            try {
                List<String> detectorIssues = (List<String>) detector.getClass()
                    .getMethod("detect", CompilationUnit.class)
                    .invoke(detector, cu);
                
                if (detectorIssues != null && !detectorIssues.isEmpty()) {
                    System.out.println("✅ " + detectorName + " found " + detectorIssues.size() + " issues in " + fileName);
                    issues.addAll(detectorIssues);
                    detectorCounts.merge(detectorName, detectorIssues.size(), Integer::sum);
                } else {
                    System.out.println("✅ " + detectorName + " found 0 issues in " + fileName);
                }
            } catch (Exception e) {
                System.err.println("❌ " + detectorName + " FAILED on " + fileName + ": " + e.getMessage());
                e.printStackTrace();
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