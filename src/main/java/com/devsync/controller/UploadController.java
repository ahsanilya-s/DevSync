package com.devsync.controller;

import com.devsync.utils.ZipExtractor;
import com.devsync.utils.FolderNamingUtil;
import com.devsync.analyzer.JavaFileCollector;
import com.devsync.reports.ReportGenerator;
import com.devsync.services.AIAssistantService;
import com.devsync.services.AdminSettingsService;
import com.devsync.model.AnalysisHistory;
import com.devsync.model.UserSettings;
import com.devsync.repository.AnalysisHistoryRepository;
import com.devsync.repository.UserSettingsRepository;
import com.devsync.config.AnalysisConfig;
import com.devsync.visual.*;

import com.devsync.analyzer.CodeAnalysisEngine;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/api/upload")
@CrossOrigin(origins = "*")
public class UploadController {

    @Autowired
    private AIAssistantService aiAssistantService;
    
    @Autowired
    private AnalysisHistoryRepository analysisHistoryRepository;
    
    @Autowired
    private UserSettingsRepository userSettingsRepository;
    
    @Autowired
    private AdminSettingsService adminSettingsService;

    @GetMapping
    public ResponseEntity<String> getUploadInfo() {
        return ResponseEntity.ok("✅ Upload endpoint ready. Use POST with multipart/form-data.");
    }

    @GetMapping("/report")
    public ResponseEntity<String> getReport(@RequestParam("path") String reportPath, 
                                           @RequestParam("userId") String userId) {
        try {
            System.out.println("📄 Report request - Path: " + reportPath + ", UserId: " + userId);
            
            // Verify user owns this report
            List<AnalysisHistory> userHistory = analysisHistoryRepository.findByUserIdOrderByAnalysisDateDesc(userId);
            System.out.println("📋 User has " + userHistory.size() + " reports in history");
            
            boolean hasAccess = userHistory.stream()
                .anyMatch(history -> {
                    System.out.println("  Checking: " + history.getReportPath());
                    return history.getReportPath().equals(reportPath);
                });
            
            if (!hasAccess) {
                System.err.println("❌ Access denied - report not found in user history");
                return ResponseEntity.status(403).body("❌ Access denied to this report");
            }
            
            System.out.println("✅ Access granted, reading report...");
            String reportContent = ReportGenerator.readReportContent(reportPath);
            System.out.println("✅ Report content loaded: " + reportContent.length() + " characters");
            return ResponseEntity.ok(reportContent);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("❌ Failed to read report: " + e.getMessage());
        }
    }
    
    @GetMapping("/history")
    public ResponseEntity<List<AnalysisHistory>> getUserHistory(@RequestParam("userId") String userId) {
        try {
            List<AnalysisHistory> history = analysisHistoryRepository.findByUserIdOrderByAnalysisDateDesc(userId);
            return ResponseEntity.ok(history);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
    
    @PostMapping("/fix-counts")
    public ResponseEntity<String> fixExistingCounts() {
        try {
            List<AnalysisHistory> allReports = analysisHistoryRepository.findAll();
            int fixed = 0;
            
            for (AnalysisHistory report : allReports) {
                try {
                    String content = ReportGenerator.readReportContent(report.getReportPath());
                    
                    // Count issues by parsing the report content
                    int critical = countIssuesInReport(content, "🔴");
                    int high = countIssuesInReport(content, "🟡");
                    int medium = countIssuesInReport(content, "🟠");
                    int total = critical + high + medium;
                    
                    // Update if counts are different
                    if (report.getTotalIssues() != total || report.getCriticalIssues() != critical || 
                        report.getWarnings() != high || report.getSuggestions() != medium) {
                        
                        report.setTotalIssues(total);
                        report.setCriticalIssues(critical);
                        report.setWarnings(high);
                        report.setSuggestions(medium);
                        analysisHistoryRepository.save(report);
                        fixed++;
                    }
                } catch (Exception e) {
                    System.err.println("Failed to fix report: " + report.getReportPath() + " - " + e.getMessage());
                }
            }
            
            return ResponseEntity.ok("Fixed " + fixed + " reports out of " + allReports.size());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to fix counts: " + e.getMessage());
        }
    }
    
    private int countIssuesInReport(String content, String emoji) {
        return (int) content.lines()
            .filter(line -> line.contains("🚨 " + emoji))
            .count();
    }

    @PostMapping
    public ResponseEntity<String> handleFileUpload(@RequestParam("file") MultipartFile file, 
                                                  @RequestParam("userId") String userId) {
        // Check admin filters
        if (adminSettingsService.isMaintenanceMode()) {
            return ResponseEntity.status(503).body("❌ System is under maintenance");
        }
        
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("❌ No file uploaded");
        }
        
        // Check file size limit
        long maxSizeBytes = adminSettingsService.getMaxFileSize() * 1024 * 1024L;
        if (file.getSize() > maxSizeBytes) {
            return ResponseEntity.badRequest().body("❌ File too large. Maximum size: " + adminSettingsService.getMaxFileSize() + "MB");
        }
        
        // Check file type
        String filename = file.getOriginalFilename();
        if (filename != null) {
            String extension = filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
            String[] allowedTypes = adminSettingsService.getAllowedFileTypes();
            boolean isAllowed = false;
            for (String type : allowedTypes) {
                if (type.trim().equals(extension)) {
                    isAllowed = true;
                    break;
                }
            }
            if (!isAllowed) {
                return ResponseEntity.badRequest().body("❌ File type not allowed. Allowed types: " + String.join(", ", allowedTypes));
            }
        }

        try {
            // 1) unzip to a unique folder with original name
            String originalFileName = file.getOriginalFilename();
            if (originalFileName == null || originalFileName.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("❌ Invalid file name");
            }
            
            String uniqueFolderName = FolderNamingUtil.generateUniqueFolderName(originalFileName, "uploads");
            String targetDir = "uploads/" + uniqueFolderName;
            
            // Create uploads directory if it doesn't exist
            java.io.File uploadsDir = new java.io.File("uploads");
            if (!uploadsDir.exists()) {
                uploadsDir.mkdirs();
            }
            
            ZipExtractor.extractZip(file.getInputStream(), targetDir);

            // 2) Get user settings
            UserSettings settings = userSettingsRepository.findByUserId(userId).orElse(new UserSettings(userId));
            System.out.println("=== User Settings Loaded ===");
            System.out.println("User ID: " + userId);
            System.out.println("Magic Number Enabled: " + settings.getMagicNumberEnabled());
            System.out.println("Long Method Enabled: " + settings.getLongMethodEnabled());
            System.out.println("Empty Catch Enabled: " + settings.getEmptyCatchEnabled());
            
            // 3) Use centralized analysis engine with user settings
            CodeAnalysisEngine analysisEngine = new CodeAnalysisEngine();
            analysisEngine.configureFromSettings(settings);
            Map<String, Object> analysisResults = analysisEngine.analyzeProject(targetDir);
            
            @SuppressWarnings("unchecked")
            List<String> allIssues = (List<String>) analysisResults.get("issues");
            
            // Get file count from results
            int javaFileCount = (Integer) analysisResults.get("totalFiles");

            // 4) generate comprehensive report
            ReportGenerator reportGen = new ReportGenerator();
            String comprehensiveReport = reportGen.generateComprehensiveReport(analysisResults);
            
            String reportPath = targetDir + "/" + new java.io.File(targetDir).getName() + "_comprehensive.txt";
            try (java.io.FileWriter writer = new java.io.FileWriter(reportPath)) {
                writer.write(comprehensiveReport);
            }
            
            // 5) save analysis to history - use severity counts from analysis engine
            @SuppressWarnings("unchecked")
            Map<String, Integer> severityCounts = (Map<String, Integer>) analysisResults.get("severityCounts");
            
            int criticalCount = severityCounts.getOrDefault("Critical", 0);
            int warningCount = severityCounts.getOrDefault("High", 0);
            int suggestionCount = severityCounts.getOrDefault("Medium", 0);
            int lowCount = severityCounts.getOrDefault("Low", 0);
            
            // Verify total matches
            int calculatedTotal = criticalCount + warningCount + suggestionCount + lowCount;
            int actualTotal = Math.max(allIssues.size(), calculatedTotal);
            
            // Get LOC and calculate grade
            int totalLOC = (Integer) analysisResults.getOrDefault("totalLOC", 0);
            com.devsync.grading.GradingSystem.GradeResult gradeResult = 
                com.devsync.grading.GradingSystem.calculateGrade(severityCounts, totalLOC);
            
            AnalysisHistory history = new AnalysisHistory(userId, originalFileName, reportPath, 
                                                         actualTotal, criticalCount, warningCount, suggestionCount,
                                                         totalLOC, gradeResult.getLetterGrade(), gradeResult.getIssueDensity());
            analysisHistoryRepository.save(history);
            
            // Debug logging
            System.out.println("=== Analysis Summary ===");
            System.out.println("Project: " + originalFileName);
            System.out.println("Total Issues: " + actualTotal + " (from list: " + allIssues.size() + ")");
            System.out.println("Critical: " + criticalCount);
            System.out.println("High: " + warningCount);
            System.out.println("Medium: " + suggestionCount);
            System.out.println("Low: " + lowCount);
            System.out.println("Lines of Code: " + totalLOC);
            System.out.println("Grade: " + gradeResult.getLetterGrade() + " (" + String.format("%.1f", gradeResult.getNumericScore()) + "%)");
            System.out.println("Issue Density: " + String.format("%.2f", gradeResult.getIssueDensity()) + " issues/KLOC");
            System.out.println("Report Path: " + reportPath);
            
            // 6) get AI analysis using user settings and admin filters
            String aiStatus = "Disabled";
            if (settings.getAiEnabled() && adminSettingsService.isAiAnalysisEnabled()) {
                try {
                    String reportContent = ReportGenerator.readReportContent(reportPath);
                    String aiAnalysis = aiAssistantService.analyzeWithAI(reportContent, settings);
                    ReportGenerator.appendAIAnalysis(reportPath, aiAnalysis);
                    aiStatus = "Added (" + settings.getAiProvider() + ")";
                } catch (Exception aiEx) {
                    aiStatus = "Failed - " + aiEx.getMessage();
                    System.err.println("AI analysis failed: " + aiEx.getMessage());
                }
            }
            

            
            // 7) response summary with report path
            String reportFileName = new File(reportPath).getName();
            String summary = String.format("✅ Advanced Analysis Complete!\n📂 Extracted to: %s\n📄 Java files: %d\n📏 Lines of Code: %,d\n📝 Report: %s\n🔍 Issues detected: %d\n📊 Grade: %s (%.1f%%)\n📈 Issue Density: %.2f issues/KLOC\n⭐ Quality: %s\n🤖 AI analysis: %s\n🧠 Advanced algorithms: Cyclomatic complexity, Cognitive complexity, Semantic analysis, Pattern recognition\n📋 Report path: %s",
                    targetDir, javaFileCount, totalLOC, reportFileName, allIssues.size(), 
                    gradeResult.getLetterGrade(), gradeResult.getNumericScore(), gradeResult.getIssueDensity(),
                    gradeResult.getQualityLevel(), aiStatus, reportPath);

            return ResponseEntity.ok(summary);

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body("❌ Failed to process file: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body("❌ Unexpected error during analysis: " + e.getMessage());
        }
    }
    
    @GetMapping("/visual")
    public ResponseEntity<String> testVisualEndpoint() {
        System.out.println("✅ GET /api/upload/visual endpoint hit");
        return ResponseEntity.ok("✅ Visual report endpoint is working!");
    }
    
    @PostMapping("/visual")
    public ResponseEntity<Map<String, Object>> generateVisualReport(@RequestParam("file") MultipartFile file) {
        System.out.println("📊 POST /api/upload/visual endpoint called with file: " + (file != null ? file.getOriginalFilename() : "null"));
        
        if (adminSettingsService.isMaintenanceMode()) {
            return ResponseEntity.status(503).build();
        }
        
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        long maxSizeBytes = adminSettingsService.getMaxFileSize() * 1024 * 1024L;
        if (file.getSize() > maxSizeBytes) {
            return ResponseEntity.badRequest().build();
        }
        
        String filename = file.getOriginalFilename();
        if (filename != null) {
            String extension = filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
            String[] allowedTypes = adminSettingsService.getAllowedFileTypes();
            boolean isAllowed = false;
            for (String type : allowedTypes) {
                if (type.trim().equals(extension)) {
                    isAllowed = true;
                    break;
                }
            }
            if (!isAllowed) {
                return ResponseEntity.badRequest().build();
            }
        }
        
        try {
            String originalFileName = file.getOriginalFilename();
            String uniqueFolderName = FolderNamingUtil.generateUniqueFolderName(originalFileName, "uploads");
            String targetDir = "uploads/" + uniqueFolderName;
            ZipExtractor.extractZip(file.getInputStream(), targetDir);
            
            VisualDependencyAnalyzer analyzer = new VisualDependencyAnalyzer();
            Map<String, Object> analysisResults = analyzer.analyzeProject(targetDir);
            
            PlantUMLGenerator plantUMLGenerator = new PlantUMLGenerator();
            String plantUMLText = plantUMLGenerator.generatePlantUMLText(analysisResults);
            
            // Convert PNG to base64 for JSON response
            byte[] diagramPNG = plantUMLGenerator.generateDiagramPNG(plantUMLText);
            String diagramBase64 = java.util.Base64.getEncoder().encodeToString(diagramPNG);
            
            // Prepare response data
            Map<String, Object> response = new HashMap<>();
            response.put("projectName", originalFileName != null ? originalFileName.replace(".zip", "") : "Java Project");
            response.put("analysisResults", analysisResults);
            response.put("diagramBase64", diagramBase64);
            response.put("plantUMLText", plantUMLText);
            
            return ResponseEntity.ok(response);
                
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to generate visual report: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
}
