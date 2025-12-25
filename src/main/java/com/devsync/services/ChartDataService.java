package com.devsync.services;

import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class ChartDataService {

    public Map<String, Object> generateChartData(Map<String, Object> analysisResults, List<String> issues) {
        Map<String, Object> chartData = new HashMap<>();
        
        chartData.put("codeDistribution", generateCodeDistributionData(analysisResults, issues));
        chartData.put("smellTypes", generateSmellTypesData(issues));
        chartData.put("severityDistribution", generateSeverityData(issues));
        
        return chartData;
    }

    private Map<String, Object> generateCodeDistributionData(Map<String, Object> analysisResults, List<String> issues) {
        int totalFiles = (Integer) analysisResults.getOrDefault("totalFiles", 0);
        
        Set<String> filesWithSmells = new HashSet<>();
        for (String issue : issues) {
            String fileName = extractFileName(issue);
            if (fileName != null) {
                filesWithSmells.add(fileName);
            }
        }
        
        int cleanFiles = totalFiles - filesWithSmells.size();
        int filesWithIssues = filesWithSmells.size();
        
        Map<String, Object> data = new HashMap<>();
        data.put("labels", Arrays.asList("Clean Files", "Files with Smells"));
        data.put("values", Arrays.asList(cleanFiles, filesWithIssues));
        data.put("colors", Arrays.asList("#10b981", "#ef4444"));
        data.put("totalFiles", totalFiles);
        data.put("cleanFiles", cleanFiles);
        data.put("filesWithSmells", filesWithIssues);
        
        return data;
    }

    private Map<String, Object> generateSmellTypesData(List<String> issues) {
        Map<String, Integer> typeCounts = new HashMap<>();
        
        for (String issue : issues) {
            String type = extractIssueType(issue);
            typeCounts.merge(type, 1, Integer::sum);
        }
        
        List<String> labels = new ArrayList<>(typeCounts.keySet());
        List<Integer> values = new ArrayList<>();
        for (String label : labels) {
            values.add(typeCounts.get(label));
        }
        
        Map<String, Object> data = new HashMap<>();
        data.put("labels", labels);
        data.put("values", values);
        data.put("colors", generateColors(labels.size()));
        
        return data;
    }

    private Map<String, Object> generateSeverityData(List<String> issues) {
        Map<String, Integer> severityCounts = new HashMap<>();
        severityCounts.put("Critical", 0);
        severityCounts.put("High", 0);
        severityCounts.put("Medium", 0);
        severityCounts.put("Low", 0);
        
        for (String issue : issues) {
            String severity = extractSeverity(issue);
            severityCounts.merge(severity, 1, Integer::sum);
        }
        
        Map<String, Object> data = new HashMap<>();
        data.put("labels", Arrays.asList("Critical", "High", "Medium", "Low"));
        data.put("values", Arrays.asList(
            severityCounts.get("Critical"),
            severityCounts.get("High"),
            severityCounts.get("Medium"),
            severityCounts.get("Low")
        ));
        data.put("colors", Arrays.asList("#ef4444", "#eab308", "#f97316", "#3b82f6"));
        
        return data;
    }

    private String extractFileName(String issue) {
        if (issue.contains("] ") && issue.contains(":")) {
            String afterBracket = issue.substring(issue.indexOf("] ") + 2);
            if (afterBracket.contains(":")) {
                String fullPath = afterBracket.substring(0, afterBracket.indexOf(":"));
                String fileName = fullPath.contains("/") ? fullPath.substring(fullPath.lastIndexOf("/") + 1) : fullPath;
                return fileName.contains("\\") ? fileName.substring(fileName.lastIndexOf("\\") + 1) : fileName;
            }
        }
        return null;
    }

    private String extractIssueType(String issue) {
        if (issue.contains("[") && issue.contains("]")) {
            return issue.substring(issue.indexOf("[") + 1, issue.indexOf("]"));
        }
        return "Unknown";
    }

    private String extractSeverity(String issue) {
        String cleanIssue = issue.startsWith("🚨 ") ? issue.substring(2).trim() : issue;
        
        if (cleanIssue.startsWith("🔴")) return "Critical";
        if (cleanIssue.startsWith("🟡")) return "High";
        if (cleanIssue.startsWith("🟠")) return "Medium";
        if (cleanIssue.startsWith("⚪") || cleanIssue.startsWith("⚠️")) return "Low";
        
        return "Low";
    }

    private List<String> generateColors(int count) {
        String[] colorPalette = {
            "#3b82f6", "#ef4444", "#10b981", "#f59e0b", "#8b5cf6",
            "#ec4899", "#14b8a6", "#f97316", "#6366f1", "#84cc16"
        };
        
        List<String> colors = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            colors.add(colorPalette[i % colorPalette.length]);
        }
        return colors;
    }
}
