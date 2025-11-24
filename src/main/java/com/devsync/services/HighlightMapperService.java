package com.devsync.services;

import com.devsync.model.CodeIssue;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class HighlightMapperService {

    public Map<String, Map<String, List<Integer>>> generateHighlightMap(String reportContent) {
        Map<String, Map<String, List<Integer>>> highlightMap = new HashMap<>();
        
        String[] lines = reportContent.split("\n");
        
        for (String line : lines) {
            if (line.startsWith("🚨")) {
                CodeIssue issue = parseIssueLine(line);
                if (issue != null) {
                    highlightMap
                        .computeIfAbsent(issue.getFile(), k -> new HashMap<>())
                        .computeIfAbsent(issue.getType(), k -> new ArrayList<>())
                        .add(issue.getLine());
                }
            }
        }
        
        // Remove duplicates and sort
        highlightMap.forEach((file, smells) -> 
            smells.forEach((smell, linesList) -> {
                Set<Integer> uniqueLines = new LinkedHashSet<>(linesList);
                linesList.clear();
                linesList.addAll(uniqueLines);
                Collections.sort(linesList);
            })
        );
        
        return highlightMap;
    }

    public List<CodeIssue> parseIssues(String reportContent) {
        List<CodeIssue> issues = new ArrayList<>();
        String[] lines = reportContent.split("\n");
        
        for (String line : lines) {
            if (line.startsWith("🚨")) {
                CodeIssue issue = parseIssueLine(line);
                if (issue != null) {
                    issues.add(issue);
                }
            }
        }
        
        return issues;
    }

    private CodeIssue parseIssueLine(String line) {
        // Format: 🚨 🔴 [Type] file.java:line - description | Suggestions: suggestion
        try {
            String cleanLine = line.substring(line.indexOf("🚨") + 1).trim();
            
            // Extract severity
            String severity = extractSeverity(cleanLine);
            cleanLine = cleanLine.substring(cleanLine.indexOf("[")).trim();
            
            // Extract type
            Pattern typePattern = Pattern.compile("\\[([^\\]]+)\\]");
            Matcher typeMatcher = typePattern.matcher(cleanLine);
            if (!typeMatcher.find()) return null;
            String type = typeMatcher.group(1);
            
            // Extract file and line
            String afterType = cleanLine.substring(typeMatcher.end()).trim();
            Pattern fileLinePattern = Pattern.compile("([^:]+):(\\d+)");
            Matcher fileLineMatcher = fileLinePattern.matcher(afterType);
            if (!fileLineMatcher.find()) return null;
            
            String filePath = fileLineMatcher.group(1).trim();
            String fileName = extractFileName(filePath);
            int lineNumber = Integer.parseInt(fileLineMatcher.group(2));
            
            // Extract message and suggestion
            String remaining = afterType.substring(fileLineMatcher.end()).trim();
            String message = "";
            String suggestion = "";
            
            if (remaining.startsWith("-")) {
                remaining = remaining.substring(1).trim();
                String[] parts = remaining.split("\\|");
                message = parts[0].trim();
                if (parts.length > 1 && parts[1].contains("Suggestions:")) {
                    suggestion = parts[1].substring(parts[1].indexOf("Suggestions:") + 12).trim();
                }
            }
            
            return new CodeIssue(type, fileName, lineNumber, severity, message, suggestion);
        } catch (Exception e) {
            System.err.println("Failed to parse issue line: " + line + " - " + e.getMessage());
            return null;
        }
    }

    private String extractSeverity(String line) {
        if (line.contains("🔴")) return "Critical";
        if (line.contains("🟡")) return "High";
        if (line.contains("🟠")) return "Medium";
        if (line.contains("⚠️")) return "Low";
        return "Low";
    }

    private String extractFileName(String path) {
        if (path.contains("/")) {
            return path.substring(path.lastIndexOf("/") + 1);
        }
        if (path.contains("\\")) {
            return path.substring(path.lastIndexOf("\\") + 1);
        }
        return path;
    }
}
