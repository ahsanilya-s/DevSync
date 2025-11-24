package com.devsync.controller;

import com.devsync.model.CodeIssue;
import com.devsync.repository.AnalysisHistoryRepository;
import com.devsync.reports.ReportGenerator;
import com.devsync.services.HighlightMapperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@RestController
@RequestMapping("/api/fileview")
@CrossOrigin(origins = "*")
public class FileViewController {

    @Autowired
    private AnalysisHistoryRepository analysisHistoryRepository;

    @Autowired
    private HighlightMapperService highlightMapperService;

    @GetMapping("/content")
    public ResponseEntity<?> getFileContent(
            @RequestParam String projectPath,
            @RequestParam String fileName,
            @RequestParam String userId) {
        
        try {
            // Verify user access
            if (!verifyUserAccess(projectPath, userId)) {
                return ResponseEntity.status(403).body(Map.of("error", "Access denied"));
            }

            // Sanitize and validate file path
            if (!fileName.endsWith(".java")) {
                return ResponseEntity.badRequest().body(Map.of("error", "Only Java files are allowed"));
            }

            // Find the file recursively in project directory
            File projectDir = new File(projectPath);
            File javaFile = findJavaFile(projectDir, fileName);

            if (javaFile == null || !javaFile.exists()) {
                return ResponseEntity.status(404).body(Map.of("error", "File not found"));
            }

            // Read file content
            String content = Files.readString(javaFile.toPath());
            
            return ResponseEntity.ok(Map.of(
                "content", content,
                "fileName", fileName,
                "fullPath", javaFile.getAbsolutePath()
            ));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "Failed to read file: " + e.getMessage()));
        }
    }

    @GetMapping("/highlights")
    public ResponseEntity<?> getHighlights(
            @RequestParam String projectPath,
            @RequestParam String userId) {
        
        try {
            // Verify user access
            if (!verifyUserAccess(projectPath, userId)) {
                return ResponseEntity.status(403).body(Map.of("error", "Access denied"));
            }

            // Find report file
            File projectDir = new File(projectPath);
            File reportFile = findReportFile(projectDir);

            if (reportFile == null || !reportFile.exists()) {
                return ResponseEntity.status(404).body(Map.of("error", "Report not found"));
            }

            // Read and parse report
            String reportContent = ReportGenerator.readReportContent(reportFile.getAbsolutePath());
            Map<String, Map<String, List<Integer>>> highlightMap = 
                highlightMapperService.generateHighlightMap(reportContent);

            return ResponseEntity.ok(highlightMap);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "Failed to generate highlights: " + e.getMessage()));
        }
    }

    @GetMapping("/issues")
    public ResponseEntity<?> getIssues(
            @RequestParam String projectPath,
            @RequestParam String fileName,
            @RequestParam String userId) {
        
        try {
            // Verify user access
            if (!verifyUserAccess(projectPath, userId)) {
                return ResponseEntity.status(403).body(Map.of("error", "Access denied"));
            }

            // Find report file
            File projectDir = new File(projectPath);
            File reportFile = findReportFile(projectDir);

            if (reportFile == null || !reportFile.exists()) {
                return ResponseEntity.status(404).body(Map.of("error", "Report not found"));
            }

            // Read and parse report
            String reportContent = ReportGenerator.readReportContent(reportFile.getAbsolutePath());
            List<CodeIssue> allIssues = highlightMapperService.parseIssues(reportContent);

            // Filter issues for specific file
            List<CodeIssue> fileIssues = allIssues.stream()
                .filter(issue -> issue.getFile().equals(fileName))
                .toList();

            return ResponseEntity.ok(fileIssues);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "Failed to get issues: " + e.getMessage()));
        }
    }

    private boolean verifyUserAccess(String projectPath, String userId) {
        return analysisHistoryRepository.findByUserIdOrderByAnalysisDateDesc(userId)
            .stream()
            .anyMatch(history -> history.getReportPath().contains(projectPath) || 
                                projectPath.contains(new File(history.getReportPath()).getParent()));
    }

    private File findJavaFile(File directory, String fileName) {
        if (!directory.isDirectory()) return null;

        File[] files = directory.listFiles();
        if (files == null) return null;

        for (File file : files) {
            if (file.isFile() && file.getName().equals(fileName)) {
                return file;
            } else if (file.isDirectory()) {
                File found = findJavaFile(file, fileName);
                if (found != null) return found;
            }
        }
        return null;
    }

    private File findReportFile(File directory) {
        File[] files = directory.listFiles((dir, name) -> 
            name.endsWith("_comprehensive.txt") || name.endsWith(".txt"));
        
        if (files != null && files.length > 0) {
            // Return the comprehensive report if exists
            for (File file : files) {
                if (file.getName().contains("comprehensive")) {
                    return file;
                }
            }
            return files[0];
        }
        return null;
    }
}
