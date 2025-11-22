package com.devsync.reports;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ReportGenerator {

    public static String generateTextReport(List<String> issues, String outputDir) throws IOException {
        String folderName = new java.io.File(outputDir).getName();
        String reportPath = outputDir + "/" + folderName + ".txt";

        try (FileWriter writer = new FileWriter(reportPath)) {
            if (issues.isEmpty()) {
                writer.write("🎉 No issues found in the code.\n");
            } else {
                for (String issue : issues) {
                    writer.write("🚨 " + issue + "\n");
                }
            }
        }

        return reportPath;
    }
    
    public String generateComprehensiveReport(Map<String, Object> analysisResults) {
        StringBuilder report = new StringBuilder();
        
        report.append("=== DevSync Code Analysis Report ===").append("\n");
        report.append("Generated: ").append(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)).append("\n\n");
        
        // Get deduplicated issues
        @SuppressWarnings("unchecked")
        List<String> rawIssues = (List<String>) analysisResults.get("issues");
        List<String> deduplicatedIssues = deduplicateIssues(rawIssues);
        
        // Recalculate counts based on deduplicated issues
        Map<String, Integer> severityCounts = calculateSeverityCounts(deduplicatedIssues);
        Map<String, Integer> typeCounts = generateTypeBreakdown(deduplicatedIssues);
        
        // Summary
        report.append("SUMMARY\n");
        report.append("-------\n");
        int totalIssues = deduplicatedIssues.size();
        report.append(String.format("Analyzed files, found %d issues (%d critical, %d high, %d medium, %d low)\n\n",
            totalIssues,
            severityCounts.getOrDefault("Critical", 0),
            severityCounts.getOrDefault("High", 0),
            severityCounts.getOrDefault("Medium", 0),
            severityCounts.getOrDefault("Low", 0)));
        
        // Severity breakdown - ensure consistent ordering
        report.append("SEVERITY BREAKDOWN\n");
        report.append("------------------\n");
        
        // Order: Critical, High, Medium, Low
        String[] severityOrder = {"Critical", "High", "Medium", "Low"};
        for (String severity : severityOrder) {
            int count = severityCounts.getOrDefault(severity, 0);
            if (count > 0) {
                report.append(String.format("%-10s: %d\n", severity, count));
            }
        }
        report.append("\n");
        
        // Issue type breakdown
        report.append("ISSUE TYPE BREAKDOWN\n");
        report.append("--------------------\n");
        
        // Sort by count descending
        typeCounts.entrySet().stream()
            .sorted((e1, e2) -> Integer.compare(e2.getValue(), e1.getValue()))
            .forEach(entry -> 
                report.append(String.format("%-20s: %d\n", entry.getKey(), entry.getValue())));
        report.append("\n");
        
        // File-wise breakdown
        Map<String, Map<String, Integer>> fileBreakdown = generateFileBreakdown(deduplicatedIssues);
        report.append("FILE-WISE BREAKDOWN\n");
        report.append("-------------------\n");
        fileBreakdown.entrySet().stream()
            .sorted((e1, e2) -> Integer.compare(
                e2.getValue().values().stream().mapToInt(Integer::intValue).sum(),
                e1.getValue().values().stream().mapToInt(Integer::intValue).sum()))
            .forEach(entry -> {
                String file = entry.getKey();
                Map<String, Integer> stats = entry.getValue();
                int total = stats.values().stream().mapToInt(Integer::intValue).sum();
                if (total > 0) {
                    report.append(String.format("File: %s (Total: %d)\n", file, total));
                    // Only show non-zero counts
                    stats.entrySet().stream()
                        .filter(e -> e.getValue() > 0)
                        .forEach(e -> report.append(String.format("  %-10s: %d\n", e.getKey(), e.getValue())));
                }
            });
        report.append("\n");
        
        // Detailed issues - sort by severity (Critical first)
        report.append("DETAILED ISSUES\n");
        report.append("---------------\n");
        
        // Sort issues by severity priority
        deduplicatedIssues.stream()
            .sorted(this::compareIssuesBySeverity)
            .forEach(issue -> report.append("🚨 ").append(issue).append("\n"));
        
        return report.toString();
    }
    
    private List<String> deduplicateIssues(List<String> issues) {
        Set<String> seen = new HashSet<>();
        List<String> deduplicated = new ArrayList<>();
        
        for (String issue : issues) {
            String key = extractDeduplicationKey(issue);
            if (!seen.contains(key)) {
                seen.add(key);
                deduplicated.add(issue);
            }
        }
        
        return deduplicated;
    }
    
    private String extractDeduplicationKey(String issue) {
        // Extract file:line:type for deduplication
        // Format: 🔴 [Type] filename.java:line - description
        if (issue.contains("] ") && issue.contains(":")) {
            String afterBracket = issue.substring(issue.indexOf("] ") + 2);
            if (afterBracket.contains(" - ")) {
                String fileAndLine = afterBracket.substring(0, afterBracket.indexOf(" - "));
                String type = issue.substring(issue.indexOf("[") + 1, issue.indexOf("]"));
                return fileAndLine + ":" + type;
            }
        }
        return issue; // Fallback to full issue
    }
    
    private Map<String, Integer> calculateSeverityCounts(List<String> issues) {
        Map<String, Integer> counts = new HashMap<>();
        counts.put("Critical", 0);
        counts.put("High", 0);
        counts.put("Medium", 0);
        counts.put("Low", 0);
        
        for (String issue : issues) {
            String severity = extractSeverity(issue);
            counts.merge(severity, 1, Integer::sum);
        }
        
        return counts;
    }
    
    private String extractSeverity(String issue) {
        String cleanIssue = issue.startsWith("🚨 ") ? issue.substring(2).trim() : issue;
        
        if (cleanIssue.startsWith("🔴")) return "Critical";
        if (cleanIssue.startsWith("🟡")) return "High";
        if (cleanIssue.startsWith("🟠")) return "Medium";
        if (cleanIssue.startsWith("⚠️")) return "Low";
        
        return "Low"; // Default
    }
    
    private int compareIssuesBySeverity(String issue1, String issue2) {
        int priority1 = getSeverityPriority(issue1);
        int priority2 = getSeverityPriority(issue2);
        return Integer.compare(priority1, priority2);
    }
    
    private int getSeverityPriority(String issue) {
        if (issue.startsWith("🔴")) return 1; // Critical
        if (issue.startsWith("🟡")) return 2; // High
        if (issue.startsWith("🟠")) return 3; // Medium
        if (issue.startsWith("⚠️")) return 4; // Low
        return 5; // Unknown
    }
    
    private Map<String, Integer> generateTypeBreakdown(List<String> issues) {
        Map<String, Integer> typeBreakdown = new HashMap<>();
        
        for (String issue : issues) {
            // Extract type from issue format: [DetectorType]
            if (issue.contains("[") && issue.contains("]")) {
                String type = issue.substring(issue.indexOf("[") + 1, issue.indexOf("]"));
                typeBreakdown.merge(type, 1, Integer::sum);
            }
        }
        
        return typeBreakdown;
    }
    
    private Map<String, Map<String, Integer>> generateFileBreakdown(List<String> issues) {
        Map<String, Map<String, Integer>> fileBreakdown = new HashMap<>();
        
        for (String issue : issues) {
            // Extract file and severity from issue format
            String fileName = "Unknown";
            String severity = "Low";
            
            // Remove 🚨 prefix if present and extract severity emoji
            String cleanIssue = issue.startsWith("🚨 ") ? issue.substring(2).trim() : issue;
            
            // Extract severity from emoji at start of clean issue
            if (cleanIssue.startsWith("🔴")) severity = "Critical";
            else if (cleanIssue.startsWith("🟡")) severity = "High";
            else if (cleanIssue.startsWith("🟠")) severity = "Medium";
            else if (cleanIssue.startsWith("⚠️")) severity = "Low";
            else severity = "Low";
            
            // Extract filename - format: 🔴 [Type] filename.java:line - description
            if (cleanIssue.contains("] ") && cleanIssue.contains(":")) {
                String afterBracket = cleanIssue.substring(cleanIssue.indexOf("] ") + 2);
                if (afterBracket.contains(":")) {
                    String fullPath = afterBracket.substring(0, afterBracket.indexOf(":"));
                    // Extract just the filename from path
                    fileName = fullPath.contains("/") ? fullPath.substring(fullPath.lastIndexOf("/") + 1) : fullPath;
                    fileName = fileName.contains("\\") ? fileName.substring(fileName.lastIndexOf("\\") + 1) : fileName;
                }
            }
            
            fileBreakdown.computeIfAbsent(fileName, k -> new HashMap<String, Integer>() {{
                put("Critical", 0);
                put("High", 0);
                put("Medium", 0);
                put("Low", 0);
            }}).merge(severity, 1, Integer::sum);
        }
        
        return fileBreakdown;
    }

    public static void appendAIAnalysis(String reportPath, String aiAnalysis) throws IOException {
        try (FileWriter writer = new FileWriter(reportPath, true)) {
            writer.write("\n\n=== AI ANALYSIS ===\n");
            writer.write(aiAnalysis);
            writer.write("\n\n=== END AI ANALYSIS ===\n");
        }
    }

    public static String readReportContent(String reportPath) throws IOException {
        return new String(Files.readAllBytes(Paths.get(reportPath)));
    }
}
