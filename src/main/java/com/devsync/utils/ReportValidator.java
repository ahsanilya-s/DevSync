package com.devsync.utils;

import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class ReportValidator {
    
    private static final Pattern ISSUE_PATTERN = Pattern.compile("^🚨 ([🔴🟡🟠⚠️]) \\[(\\w+)\\] (.+?):(\\d+) - (.+)");
    private static final Pattern SEVERITY_PATTERN = Pattern.compile("^(\\w+)\\s*:\\s*(\\d+)$");
    private static final Pattern FILE_PATTERN = Pattern.compile("^File: (.+?) \\(Total: (\\d+)\\)$");
    
    public static class ValidationResult {
        public boolean isValid;
        public List<String> errors;
        public Map<String, Object> extractedData;
        
        public ValidationResult() {
            this.errors = new ArrayList<>();
            this.extractedData = new HashMap<>();
        }
    }
    
    public static ValidationResult validateReport(String reportContent) {
        ValidationResult result = new ValidationResult();
        result.isValid = true;
        
        try {
            // Extract data from report
            Map<String, Integer> severityCounts = extractSeverityCounts(reportContent);
            Map<String, Integer> typeCounts = extractTypeCounts(reportContent);
            Map<String, Map<String, Integer>> fileCounts = extractFileCounts(reportContent);
            List<Map<String, Object>> issues = extractIssues(reportContent);
            
            result.extractedData.put("severityCounts", severityCounts);
            result.extractedData.put("typeCounts", typeCounts);
            result.extractedData.put("fileCounts", fileCounts);
            result.extractedData.put("issues", issues);
            
            // Validate consistency
            validateSeverityConsistency(severityCounts, issues, result);
            validateTypeConsistency(typeCounts, issues, result);
            validateFileConsistency(fileCounts, issues, result);
            validateIssueFormat(issues, result);
            
        } catch (Exception e) {
            result.isValid = false;
            result.errors.add("Validation failed with exception: " + e.getMessage());
        }
        
        return result;
    }
    
    private static Map<String, Integer> extractSeverityCounts(String content) {
        Map<String, Integer> counts = new HashMap<>();
        String[] lines = content.split("\n");
        boolean inSeveritySection = false;
        
        for (String line : lines) {
            if (line.startsWith("SEVERITY BREAKDOWN")) {
                inSeveritySection = true;
                continue;
            }
            if (inSeveritySection && line.trim().isEmpty()) {
                break;
            }
            if (inSeveritySection && !line.startsWith("-")) {
                Matcher matcher = SEVERITY_PATTERN.matcher(line.trim());
                if (matcher.matches()) {
                    String severity = matcher.group(1);
                    int count = Integer.parseInt(matcher.group(2));
                    counts.put(severity, count);
                }
            }
        }
        
        return counts;
    }
    
    private static Map<String, Integer> extractTypeCounts(String content) {
        Map<String, Integer> counts = new HashMap<>();
        String[] lines = content.split("\n");
        boolean inTypeSection = false;
        
        for (String line : lines) {
            if (line.startsWith("ISSUE TYPE BREAKDOWN")) {
                inTypeSection = true;
                continue;
            }
            if (inTypeSection && line.trim().isEmpty()) {
                break;
            }
            if (inTypeSection && !line.startsWith("-")) {
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    String type = parts[0].trim();
                    try {
                        int count = Integer.parseInt(parts[1].trim());
                        counts.put(type, count);
                    } catch (NumberFormatException e) {
                        // Skip invalid lines
                    }
                }
            }
        }
        
        return counts;
    }
    
    private static Map<String, Map<String, Integer>> extractFileCounts(String content) {
        Map<String, Map<String, Integer>> fileCounts = new HashMap<>();
        String[] lines = content.split("\n");
        boolean inFileSection = false;
        String currentFile = null;
        
        for (String line : lines) {
            if (line.startsWith("FILE-WISE BREAKDOWN")) {
                inFileSection = true;
                continue;
            }
            if (inFileSection && line.startsWith("DETAILED ISSUES")) {
                break;
            }
            if (inFileSection && line.startsWith("File: ")) {
                Matcher matcher = FILE_PATTERN.matcher(line);
                if (matcher.matches()) {
                    currentFile = matcher.group(1);
                    int total = Integer.parseInt(matcher.group(2));
                    fileCounts.put(currentFile, new HashMap<>());
                    fileCounts.get(currentFile).put("total", total);
                }
            }
            if (inFileSection && line.startsWith("  ") && currentFile != null) {
                Matcher matcher = SEVERITY_PATTERN.matcher(line.trim());
                if (matcher.matches()) {
                    String severity = matcher.group(1);
                    int count = Integer.parseInt(matcher.group(2));
                    fileCounts.get(currentFile).put(severity.toLowerCase(), count);
                }
            }
        }
        
        return fileCounts;
    }
    
    private static List<Map<String, Object>> extractIssues(String content) {
        List<Map<String, Object>> issues = new ArrayList<>();
        String[] lines = content.split("\n");
        boolean inIssueSection = false;
        
        for (String line : lines) {
            if (line.startsWith("DETAILED ISSUES")) {
                inIssueSection = true;
                continue;
            }
            if (inIssueSection && line.startsWith("🚨 ")) {
                Matcher matcher = ISSUE_PATTERN.matcher(line);
                if (matcher.matches()) {
                    Map<String, Object> issue = new HashMap<>();
                    issue.put("severity", getSeverityName(matcher.group(1)));
                    issue.put("type", matcher.group(2));
                    issue.put("file", matcher.group(3));
                    issue.put("line", Integer.parseInt(matcher.group(4)));
                    issue.put("description", matcher.group(5));
                    issues.add(issue);
                }
            }
        }
        
        return issues;
    }
    
    private static String getSeverityName(String emoji) {
        switch (emoji) {
            case "🔴": return "Critical";
            case "🟡": return "High";
            case "🟠": return "Medium";
            case "⚠️": return "Low";
            default: return "Unknown";
        }
    }
    
    private static void validateSeverityConsistency(Map<String, Integer> severityCounts, 
                                                   List<Map<String, Object>> issues, 
                                                   ValidationResult result) {
        Map<String, Integer> actualCounts = new HashMap<>();
        
        for (Map<String, Object> issue : issues) {
            String severity = (String) issue.get("severity");
            actualCounts.merge(severity, 1, Integer::sum);
        }
        
        for (String severity : Arrays.asList("Critical", "High", "Medium", "Low")) {
            int expected = severityCounts.getOrDefault(severity, 0);
            int actual = actualCounts.getOrDefault(severity, 0);
            
            if (expected != actual) {
                result.isValid = false;
                result.errors.add(String.format("Severity count mismatch for %s: expected %d, found %d", 
                    severity, expected, actual));
            }
        }
    }
    
    private static void validateTypeConsistency(Map<String, Integer> typeCounts, 
                                              List<Map<String, Object>> issues, 
                                              ValidationResult result) {
        Map<String, Integer> actualCounts = new HashMap<>();
        
        for (Map<String, Object> issue : issues) {
            String type = (String) issue.get("type");
            actualCounts.merge(type, 1, Integer::sum);
        }
        
        for (Map.Entry<String, Integer> entry : typeCounts.entrySet()) {
            String type = entry.getKey();
            int expected = entry.getValue();
            int actual = actualCounts.getOrDefault(type, 0);
            
            if (expected != actual) {
                result.isValid = false;
                result.errors.add(String.format("Type count mismatch for %s: expected %d, found %d", 
                    type, expected, actual));
            }
        }
    }
    
    private static void validateFileConsistency(Map<String, Map<String, Integer>> fileCounts, 
                                              List<Map<String, Object>> issues, 
                                              ValidationResult result) {
        Map<String, Map<String, Integer>> actualFileCounts = new HashMap<>();
        
        for (Map<String, Object> issue : issues) {
            String file = (String) issue.get("file");
            String severity = ((String) issue.get("severity")).toLowerCase();
            
            actualFileCounts.computeIfAbsent(file, k -> new HashMap<>())
                           .merge(severity, 1, Integer::sum);
        }
        
        // Validate file totals
        for (Map.Entry<String, Map<String, Integer>> entry : fileCounts.entrySet()) {
            String file = entry.getKey();
            Map<String, Integer> expectedCounts = entry.getValue();
            Map<String, Integer> actualCounts = actualFileCounts.getOrDefault(file, new HashMap<>());
            
            int expectedTotal = expectedCounts.getOrDefault("total", 0);
            int actualTotal = actualCounts.values().stream().mapToInt(Integer::intValue).sum();
            
            if (expectedTotal != actualTotal) {
                result.isValid = false;
                result.errors.add(String.format("File total mismatch for %s: expected %d, found %d", 
                    file, expectedTotal, actualTotal));
            }
        }
    }
    
    private static void validateIssueFormat(List<Map<String, Object>> issues, ValidationResult result) {
        for (int i = 0; i < issues.size(); i++) {
            Map<String, Object> issue = issues.get(i);
            
            if (!issue.containsKey("severity") || !issue.containsKey("type") || 
                !issue.containsKey("file") || !issue.containsKey("line") || 
                !issue.containsKey("description")) {
                result.isValid = false;
                result.errors.add(String.format("Issue %d is missing required fields", i + 1));
            }
            
            if (issue.get("line") != null && !(issue.get("line") instanceof Integer)) {
                result.isValid = false;
                result.errors.add(String.format("Issue %d has invalid line number", i + 1));
            }
        }
    }
    
    public static void printValidationReport(ValidationResult result) {
        System.out.println("=== Report Validation Results ===");
        System.out.println("Valid: " + result.isValid);
        
        if (!result.errors.isEmpty()) {
            System.out.println("Errors:");
            for (String error : result.errors) {
                System.out.println("  - " + error);
            }
        }
        
        if (result.extractedData.containsKey("severityCounts")) {
            System.out.println("Extracted Severity Counts: " + result.extractedData.get("severityCounts"));
        }
        
        if (result.extractedData.containsKey("issues")) {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> issues = (List<Map<String, Object>>) result.extractedData.get("issues");
            System.out.println("Extracted Issues Count: " + issues.size());
        }
    }
}