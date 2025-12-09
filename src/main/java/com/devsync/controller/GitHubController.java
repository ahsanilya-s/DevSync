package com.devsync.controller;

import com.devsync.model.CommitAnalysis;
import com.devsync.repository.CommitAnalysisRepository;
import com.devsync.analyzer.CodeAnalysisEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.zip.*;

@RestController
@RequestMapping("/api/github")
@CrossOrigin(origins = "*")
public class GitHubController {

    @Autowired
    private CommitAnalysisRepository commitAnalysisRepository;

    @Autowired
    private CodeAnalysisEngine analysisEngine;

    @GetMapping("/test")
    public ResponseEntity<?> test() {
        return ResponseEntity.ok(Map.of("status", "GitHub API is working"));
    }

    @PostMapping("/repos")
    public ResponseEntity<?> getUserRepos(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        
        if (token == null || token.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Token is required"));
        }
        
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "token " + token);
            headers.set("Accept", "application/vnd.github.v3+json");
            headers.set("User-Agent", "DevSync-App");
            
            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<List> response = restTemplate.exchange(
                "https://api.github.com/user/repos?sort=updated&per_page=50",
                HttpMethod.GET,
                entity,
                List.class
            );
            
            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to fetch repos: " + e.getMessage()));
        }
    }

    @PostMapping("/commits")
    public ResponseEntity<?> getRepoCommits(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        String owner = request.get("owner");
        String repo = request.get("repo");
        
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "token " + token);
            headers.set("Accept", "application/vnd.github.v3+json");
            headers.set("User-Agent", "DevSync-App");
            
            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<List> response = restTemplate.exchange(
                "https://api.github.com/repos/" + owner + "/" + repo + "/commits?per_page=20",
                HttpMethod.GET,
                entity,
                List.class
            );
            
            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to fetch commits: " + e.getMessage()));
        }
    }

    @PostMapping("/download")
    public ResponseEntity<?> downloadRepo(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        String owner = request.get("owner");
        String repo = request.get("repo");
        String ref = request.getOrDefault("ref", "main");
        
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "token " + token);
            headers.set("User-Agent", "DevSync-App");
            
            HttpEntity<String> entity = new HttpEntity<>(headers);
            String url = "https://api.github.com/repos/" + owner + "/" + repo + "/zipball/" + ref;
            
            ResponseEntity<byte[]> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                byte[].class
            );
            
            return ResponseEntity.ok()
                .header("Content-Type", "application/zip")
                .body(response.getBody());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to download repo: " + e.getMessage()));
        }
    }

    @PostMapping("/analyze-commit")
    public ResponseEntity<?> analyzeCommit(@RequestBody Map<String, Object> request) {
        String token = (String) request.get("token");
        String owner = (String) request.get("owner");
        String repo = (String) request.get("repo");
        String sha = (String) request.get("sha");
        String userId = (String) request.get("userId");
        String commitMessage = (String) request.get("commitMessage");
        String commitDateStr = (String) request.get("commitDate");
        
        try {
            // Download repo at specific commit
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "token " + token);
            headers.set("User-Agent", "DevSync-App");
            
            HttpEntity<String> entity = new HttpEntity<>(headers);
            String url = "https://api.github.com/repos/" + owner + "/" + repo + "/zipball/" + sha;
            
            ResponseEntity<byte[]> response = restTemplate.exchange(url, HttpMethod.GET, entity, byte[].class);
            
            // Save and extract zip
            String extractPath = "uploads/github_" + owner + "_" + repo + "_" + sha.substring(0, 7);
            File extractDir = new File(extractPath);
            extractDir.mkdirs();
            
            File zipFile = new File(extractPath + ".zip");
            Files.write(zipFile.toPath(), response.getBody());
            
            // Extract zip
            unzip(zipFile.getAbsolutePath(), extractPath);
            zipFile.delete();
            
            // Run analysis
            Map<String, Object> analysisResult = analysisEngine.analyzeProject(extractPath);
            
            // Extract severity counts
            Map<String, Integer> severityCounts = (Map<String, Integer>) analysisResult.get("severityCounts");
            int critical = severityCounts != null ? severityCounts.getOrDefault("Critical", 0) : 0;
            int warnings = severityCounts != null ? severityCounts.getOrDefault("High", 0) : 0;
            int suggestions = severityCounts != null ? severityCounts.getOrDefault("Medium", 0) : 0;
            
            // Save to database
            CommitAnalysis analysis = new CommitAnalysis();
            analysis.setUserId(userId);
            analysis.setRepoOwner(owner);
            analysis.setRepoName(repo);
            analysis.setCommitSha(sha);
            analysis.setCommitMessage(commitMessage);
            analysis.setCommitDate(ZonedDateTime.parse(commitDateStr).toLocalDateTime());
            analysis.setAnalysisDate(LocalDateTime.now());
            analysis.setTotalIssues((Integer) analysisResult.get("totalIssues"));
            analysis.setCriticalIssues(critical);
            analysis.setWarnings(warnings);
            analysis.setSuggestions(suggestions);
            analysis.setReportPath(extractPath);
            
            commitAnalysisRepository.save(analysis);
            
            return ResponseEntity.ok(analysis);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to analyze commit: " + e.getMessage()));
        }
    }

    @GetMapping("/commit-history")
    public ResponseEntity<?> getCommitHistory(@RequestParam String userId, @RequestParam String owner, @RequestParam String repo) {
        List<CommitAnalysis> history = commitAnalysisRepository.findByUserIdAndRepoOwnerAndRepoNameOrderByCommitDateDesc(userId, owner, repo);
        return ResponseEntity.ok(history);
    }

    private void unzip(String zipFilePath, String destDir) throws IOException {
        File dir = new File(destDir);
        if (!dir.exists()) dir.mkdirs();
        
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFilePath))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                File file = new File(destDir, entry.getName());
                if (entry.isDirectory()) {
                    file.mkdirs();
                } else {
                    file.getParentFile().mkdirs();
                    try (FileOutputStream fos = new FileOutputStream(file)) {
                        byte[] buffer = new byte[1024];
                        int len;
                        while ((len = zis.read(buffer)) > 0) {
                            fos.write(buffer, 0, len);
                        }
                    }
                }
                zis.closeEntry();
            }
        }
    }
}
