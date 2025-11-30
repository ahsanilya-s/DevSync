package com.devsync.services;

import com.devsync.dto.LongMethodThresholdDetails;
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
        // Format: 🚨 🔴 [Type] file.java:line - description | Suggestions: suggestion | DetailedReason: reason
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
            
            // Extract message, suggestion, and detailed reason
            String remaining = afterType.substring(fileLineMatcher.end()).trim();
            String message = "";
            String suggestion = "";
            String detailedReason = "";
            
            if (remaining.startsWith("-")) {
                remaining = remaining.substring(1).trim();
                String[] parts = remaining.split("\\|");
                message = parts[0].trim();
                
                for (int i = 1; i < parts.length; i++) {
                    String part = parts[i].trim();
                    if (part.startsWith("Suggestions:")) {
                        suggestion = part.substring(12).trim();
                    } else if (part.startsWith("DetailedReason:")) {
                        detailedReason = part.substring(15).trim();
                    }
                }
            }
            
            CodeIssue issue = new CodeIssue(type, fileName, lineNumber, severity, message, suggestion);
            issue.setDetailedReason(detailedReason);
            
            if ("LongMethod".equals(type) && detailedReason != null && !detailedReason.isEmpty()) {
                issue.setThresholdDetails(parseThresholdDetails(detailedReason));
            }
            
            return issue;
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
    
    private LongMethodThresholdDetails parseThresholdDetails(String detailedReason) {
        LongMethodThresholdDetails details = new LongMethodThresholdDetails();
        
        try {
            Pattern statementPattern = Pattern.compile("Statement count is (\\d+) \\((exceeds|within) (?:critical threshold|base threshold|threshold) of (\\d+)\\)");
            Matcher statementMatcher = statementPattern.matcher(detailedReason);
            if (statementMatcher.find()) {
                details.setStatementCount(Integer.parseInt(statementMatcher.group(1)));
                details.setExceedsStatementCount("exceeds".equals(statementMatcher.group(2)));
                int threshold = Integer.parseInt(statementMatcher.group(3));
                if (detailedReason.contains("critical threshold")) {
                    details.setCriticalThreshold(threshold);
                    details.setBaseThreshold(threshold / 2);
                } else {
                    details.setBaseThreshold(threshold);
                    details.setCriticalThreshold(threshold * 2);
                }
            }
            
            Pattern cyclomaticPattern = Pattern.compile("Cyclomatic complexity is (\\d+) \\((exceeds|within) max of (\\d+)");
            Matcher cyclomaticMatcher = cyclomaticPattern.matcher(detailedReason);
            if (cyclomaticMatcher.find()) {
                details.setCyclomaticComplexity(Integer.parseInt(cyclomaticMatcher.group(1)));
                details.setExceedsCyclomaticComplexity("exceeds".equals(cyclomaticMatcher.group(2)));
                details.setMaxCyclomaticComplexity(Integer.parseInt(cyclomaticMatcher.group(3)));
            }
            
            Pattern cognitivePattern = Pattern.compile("Cognitive complexity is (\\d+) \\((exceeds|within) max of (\\d+)");
            Matcher cognitiveMatcher = cognitivePattern.matcher(detailedReason);
            if (cognitiveMatcher.find()) {
                details.setCognitiveComplexity(Integer.parseInt(cognitiveMatcher.group(1)));
                details.setExceedsCognitiveComplexity("exceeds".equals(cognitiveMatcher.group(2)));
                details.setMaxCognitiveComplexity(Integer.parseInt(cognitiveMatcher.group(3)));
            }
            
            Pattern nestingPattern = Pattern.compile("Nesting depth is (\\d+) levels \\((exceeds|within) max of (\\d+)");
            Matcher nestingMatcher = nestingPattern.matcher(detailedReason);
            if (nestingMatcher.find()) {
                details.setNestingDepth(Integer.parseInt(nestingMatcher.group(1)));
                details.setExceedsNestingDepth("exceeds".equals(nestingMatcher.group(2)));
                details.setMaxNestingDepth(Integer.parseInt(nestingMatcher.group(3)));
            }
            
            Pattern responsibilityPattern = Pattern.compile("Handles (\\d+) (?:different )?responsibilit(?:ies|y) \\((exceeds|within) max of (\\d+)");
            Matcher responsibilityMatcher = responsibilityPattern.matcher(detailedReason);
            if (responsibilityMatcher.find()) {
                details.setResponsibilityCount(Integer.parseInt(responsibilityMatcher.group(1)));
                details.setExceedsResponsibilityCount("exceeds".equals(responsibilityMatcher.group(2)));
                details.setMaxResponsibilityCount(Integer.parseInt(responsibilityMatcher.group(3)));
            }
            
            details.setSummary("A method is flagged when ANY of these thresholds is exceeded.");
            
        } catch (Exception e) {
            System.err.println("Failed to parse threshold details: " + e.getMessage());
        }
        
        return details;
    }
}
