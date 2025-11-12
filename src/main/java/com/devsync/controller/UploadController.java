package com.devsync.controller;

import com.devsync.utils.ZipExtractor;
import com.devsync.utils.FolderNamingUtil;
import com.devsync.analyzer.JavaFileCollector;
import com.devsync.reports.ReportGenerator;
import com.devsync.services.OllamaService;
import com.devsync.model.AnalysisHistory;
import com.devsync.repository.AnalysisHistoryRepository;

import com.devsync.detectors.LongMethodDetector;
import com.devsync.detectors.LongParameterListDetector;
import com.devsync.detectors.MagicNumberDetector;
import com.devsync.detectors.EmptyCatchDetector;
import com.devsync.detectors.LongIdentifierDetector;
import com.devsync.detectors.CodeDuplicationDetector;
import com.devsync.detectors.GodClassDetector;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/api/upload")
public class UploadController {

    @Autowired
    private OllamaService ollamaService;
    
    @Autowired
    private AnalysisHistoryRepository analysisHistoryRepository;

    @GetMapping
    public ResponseEntity<String> getUploadInfo() {
        return ResponseEntity.ok("✅ Upload endpoint ready. Use POST with multipart/form-data.");
    }

    @GetMapping("/report")
    public ResponseEntity<String> getReport(@RequestParam("path") String reportPath, 
                                           @RequestParam("userId") String userId) {
        try {
            // Verify user owns this report
            boolean hasAccess = analysisHistoryRepository.findByUserIdOrderByAnalysisDateDesc(userId)
                .stream().anyMatch(history -> history.getReportPath().equals(reportPath));
            
            if (!hasAccess) {
                return ResponseEntity.status(403).body("❌ Access denied to this report");
            }
            
            String reportContent = ReportGenerator.readReportContent(reportPath);
            return ResponseEntity.ok(reportContent);
        } catch (Exception e) {
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

    @PostMapping
    public ResponseEntity<String> handleFileUpload(@RequestParam("file") MultipartFile file, 
                                                  @RequestParam("userId") String userId) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("❌ No file uploaded");
        }

        try {
            // 1) unzip to a unique folder with original name
            String originalFileName = file.getOriginalFilename();
            String uniqueFolderName = FolderNamingUtil.generateUniqueFolderName(originalFileName, "uploads");
            String targetDir = "uploads/" + uniqueFolderName;
            ZipExtractor.extractZip(file.getInputStream(), targetDir);

            // 2) collect .java files
            List<File> javaFiles = JavaFileCollector.collectJavaFiles(targetDir);

            // 3) run advanced detectors with sophisticated algorithms
            List<String> allIssues = new ArrayList<>();
            LongMethodDetector longMethodDetector = new LongMethodDetector();
            LongParameterListDetector longParamDetector = new LongParameterListDetector();
            MagicNumberDetector magicNumberDetector = new MagicNumberDetector();
            EmptyCatchDetector emptyCatchDetector = new EmptyCatchDetector();
            LongIdentifierDetector longIdDetector = new LongIdentifierDetector();
            CodeDuplicationDetector duplicationDetector = new CodeDuplicationDetector();
            GodClassDetector godClassDetector = new GodClassDetector();
            
            for (File f : javaFiles) {
                try {
                    CompilationUnit cu = StaticJavaParser.parse(f);
                    
                    // Advanced algorithm-based detection
                    allIssues.addAll(longMethodDetector.detect(cu));
                    allIssues.addAll(longParamDetector.detect(cu));
                    allIssues.addAll(magicNumberDetector.detect(cu));
                    allIssues.addAll(emptyCatchDetector.detect(cu));
                    allIssues.addAll(longIdDetector.detect(cu));
                    allIssues.addAll(duplicationDetector.detect(cu));
                    allIssues.addAll(godClassDetector.detect(cu));
                    
                } catch (Exception ex) {
                    // ensure one file failure doesn't break whole flow
                    allIssues.add(String.format("⚠️ Error analyzing %s: %s", f.getName(), ex.getMessage()));
                    ex.printStackTrace();
                }
            }

            // 4) generate report
            String reportPath = ReportGenerator.generateTextReport(allIssues, targetDir);
            
            // 5) save analysis to history
            int criticalCount = (int) allIssues.stream().filter(issue -> issue.contains("🔴")).count();
            int warningCount = (int) allIssues.stream().filter(issue -> issue.contains("🟡")).count();
            int suggestionCount = (int) allIssues.stream().filter(issue -> issue.contains("🟠")).count();
            
            AnalysisHistory history = new AnalysisHistory(userId, originalFileName, reportPath, 
                                                         allIssues.size(), criticalCount, warningCount, suggestionCount);
            analysisHistoryRepository.save(history);
            
            // 6) get AI analysis
            String aiStatus = "Failed";
            try {
                String reportContent = ReportGenerator.readReportContent(reportPath);
                String aiAnalysis = ollamaService.sendToOllama(reportContent);
                ReportGenerator.appendAIAnalysis(reportPath, aiAnalysis);
                aiStatus = "Added";
            } catch (java.net.ConnectException ce) {
                aiStatus = "Failed - Ollama not running";
                System.err.println("AI analysis failed: Ollama not running on localhost:11434");
            } catch (java.net.http.HttpTimeoutException te) {
                aiStatus = "Failed - Timeout";
                System.err.println("AI analysis failed: Request timed out");
            } catch (Exception aiEx) {
                aiStatus = "Failed - " + aiEx.getMessage();
                System.err.println("AI analysis failed: " + aiEx.getMessage());
            }

            // 7) response summary with report path
            String reportFileName = new File(reportPath).getName();
            String summary = String.format("✅ Advanced Analysis Complete!\n📂 Extracted to: %s\n📄 Java files: %d\n📝 Report: %s\n🔍 Issues detected: %d\n🤖 AI analysis: %s\n🧠 Advanced algorithms: Cyclomatic complexity, Cognitive complexity, Semantic analysis, Pattern recognition\n📋 Report path: %s",
                    targetDir, javaFiles.size(), reportFileName, allIssues.size(), aiStatus, reportPath);

            return ResponseEntity.ok(summary);

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body("❌ Failed to process file: " + e.getMessage());
        }
    }
}
