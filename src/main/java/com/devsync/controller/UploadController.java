package com.devsync.controller;

import com.devsync.utils.ZipExtractor;
import com.devsync.analyzer.JavaFileCollector;
import com.devsync.reports.ReportGenerator;
import com.devsync.services.OllamaService;

import com.devsync.detectors.LongMethodDetector;
import com.devsync.detectors.LongParameterListDetector;
import com.devsync.detectors.MagicNumberDetector;
import com.devsync.detectors.EmptyCatchDetector;
import com.devsync.detectors.LongIdentifierDetector;
import com.devsync.detectors.MissingDefaultSwitchDetector;
import com.devsync.detectors.LongStatementDetector;
import com.devsync.detectors.ComplexConditionalDetector;
import com.devsync.detectors.UnnecessaryAbstractionDetector;
import com.devsync.detectors.BrokenModularizationDetector;
import com.devsync.detectors.DeficientEncapsulationDetector;

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

    @GetMapping
    public ResponseEntity<String> getUploadInfo() {
        return ResponseEntity.ok("✅ Upload endpoint ready. Use POST with multipart/form-data.");
    }

    @PostMapping
    public ResponseEntity<String> handleFileUpload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("❌ No file uploaded");
        }

        try {
            // 1) unzip to a unique folder
            String targetDir = "uploads/" + System.currentTimeMillis();
            ZipExtractor.extractZip(file.getInputStream(), targetDir);

            // 2) collect .java files
            List<File> javaFiles = JavaFileCollector.collectJavaFiles(targetDir);

            // 3) run detectors
            List<String> allIssues = new ArrayList<>();
            for (File f : javaFiles) {
                try {
                    allIssues.addAll(LongMethodDetector.detect(f));
                    allIssues.addAll(LongParameterListDetector.detect(f));
                    allIssues.addAll(MagicNumberDetector.detect(f));
                    allIssues.addAll(EmptyCatchDetector.detect(f));
                    allIssues.addAll(LongIdentifierDetector.detect(f));
                    allIssues.addAll(MissingDefaultSwitchDetector.detect(f));
                    allIssues.addAll(LongStatementDetector.detect(f));
                    allIssues.addAll(ComplexConditionalDetector.detect(f));
                    allIssues.addAll(UnnecessaryAbstractionDetector.detect(f));
                    allIssues.addAll(BrokenModularizationDetector.detect(f));
                    allIssues.addAll(DeficientEncapsulationDetector.detect(f));
                } catch (Exception ex) {
                    // ensure one file failure doesn't break whole flow
                    allIssues.add(String.format("Error analyzing %s: %s", f.getName(), ex.getMessage()));
                    ex.printStackTrace();
                }
            }

            // 4) generate report
            String reportPath = ReportGenerator.generateTextReport(allIssues, targetDir);
            
            // 5) get AI analysis
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

            // 6) response summary
            String summary = String.format("✅ Analysis complete!\n📂 Extracted to: %s\n📄 Java files: %d\n📝 Report: %s\n🔍 Issues found: %d\n🤖 AI analysis: %s",
                    targetDir, javaFiles.size(), reportPath, allIssues.size(), aiStatus);

            return ResponseEntity.ok(summary);

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body("❌ Failed to process file: " + e.getMessage());
        }
    }
}
