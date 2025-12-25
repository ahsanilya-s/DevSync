package com.devsync.services;

import com.devsync.model.CodeIssue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Files;
import java.util.*;

@Service
public class DetailedReportService {

    @Autowired
    private HighlightMapperService highlightMapperService;
    
    @Autowired
    private ChartDataService chartDataService;

    public String generateDetailedHTMLReport(String reportPath, String projectPath) throws Exception {
        File reportFile = new File(reportPath);
        String reportContent = new String(Files.readAllBytes(reportFile.toPath()));
        
        List<CodeIssue> issues = highlightMapperService.parseIssues(reportContent);
        
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>\n<html lang=\"en\">\n<head>\n");
        html.append("<meta charset=\"UTF-8\">\n");
        html.append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n");
        html.append("<title>Detailed Code Quality Report</title>\n");
        html.append("<style>\n");
        appendStyles(html);
        html.append("</style>\n</head>\n<body>\n");
        
        // Header
        html.append("<div class=\"header\">\n");
        html.append("<h1>Detailed Code Quality Report</h1>\n");
        html.append("<p>Generated: ").append(new Date()).append("</p>\n");
        html.append("</div>\n");
        
        // Charts Section
        html.append("<div class=\"charts-section\">\n");
        appendCharts(html, issues);
        html.append("</div>\n");
        
        // Summary from original report
        html.append("<div class=\"summary\">\n");
        appendSummary(html, reportContent, issues.size());
        html.append("</div>\n");
        
        // Detailed Issues with Code
        html.append("<div class=\"issues-section\">\n");
        html.append("<h2>Detailed Issues with Code Analysis</h2>\n");
        
        for (CodeIssue issue : issues) {
            appendIssueDetail(html, issue, projectPath);
        }
        
        html.append("</div>\n");
        html.append("<script src=\"https://cdn.jsdelivr.net/npm/chart.js\"></script>\n");
        html.append("<script>\n");
        appendChartScripts(html, reportContent, issues);
        html.append("</script>\n");
        html.append("</body>\n</html>");
        
        return html.toString();
    }

    private void appendStyles(StringBuilder html) {
        html.append("* { margin: 0; padding: 0; box-sizing: border-box; }\n");
        html.append("body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background: #f5f5f5; padding: 20px; }\n");
        html.append(".header { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 30px; border-radius: 10px; margin-bottom: 20px; }\n");
        html.append(".header h1 { font-size: 32px; margin-bottom: 10px; }\n");
        html.append(".summary { background: white; padding: 20px; border-radius: 10px; margin-bottom: 20px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }\n");
        html.append(".issues-section { background: white; padding: 20px; border-radius: 10px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }\n");
        html.append(".issue-card { border-left: 4px solid; padding: 20px; margin-bottom: 20px; border-radius: 5px; background: #fafafa; }\n");
        html.append(".issue-critical { border-color: #ef4444; background: #fef2f2; }\n");
        html.append(".issue-high { border-color: #eab308; background: #fefce8; }\n");
        html.append(".issue-medium { border-color: #f97316; background: #fff7ed; }\n");
        html.append(".issue-low { border-color: #3b82f6; background: #eff6ff; }\n");
        html.append(".issue-header { display: flex; gap: 10px; margin-bottom: 15px; align-items: center; }\n");
        html.append(".badge { padding: 5px 10px; border-radius: 5px; font-size: 12px; font-weight: bold; }\n");
        html.append(".badge-critical { background: #ef4444; color: white; }\n");
        html.append(".badge-high { background: #eab308; color: white; }\n");
        html.append(".badge-medium { background: #f97316; color: white; }\n");
        html.append(".badge-low { background: #3b82f6; color: white; }\n");
        html.append(".code-section { margin: 15px 0; }\n");
        html.append(".code-section h4 { margin-bottom: 10px; color: #374151; }\n");
        html.append("pre { background: #1e1e1e; color: #d4d4d4; padding: 15px; border-radius: 5px; overflow-x: auto; font-size: 13px; line-height: 1.5; }\n");
        html.append(".highlight-line { background: rgba(239, 68, 68, 0.2); }\n");
        html.append(".explanation { background: #e0f2fe; padding: 15px; border-radius: 5px; margin: 10px 0; border-left: 3px solid #0284c7; }\n");
        html.append(".refactored { background: #dcfce7; padding: 15px; border-radius: 5px; margin: 10px 0; border-left: 3px solid #16a34a; }\n");
        html.append(".metrics { display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 15px; margin: 15px 0; }\n");
        html.append(".metric-card { background: white; padding: 15px; border-radius: 8px; border: 1px solid #e5e7eb; }\n");
        html.append(".metric-value { font-size: 24px; font-weight: bold; color: #1f2937; }\n");
        html.append(".metric-label { font-size: 12px; color: #6b7280; margin-top: 5px; }\n");
        html.append(".charts-section { margin-bottom: 20px; }\n");
        html.append(".chart-container { background: white; padding: 20px; border-radius: 10px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); margin-bottom: 20px; }\n");
        html.append(".chart-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(400px, 1fr)); gap: 20px; }\n");
        html.append("canvas { max-width: 100%; height: 300px !important; }\n");
    }

    private void appendSummary(StringBuilder html, String reportContent, int issueCount) {
        html.append("<h2>Summary</h2>\n");
        
        String[] lines = reportContent.split("\n");
        int critical = 0, high = 0, medium = 0, low = 0;
        
        for (String line : lines) {
            if (line.contains("Critical") && line.contains(":")) {
                try { critical = Integer.parseInt(line.split(":")[1].trim()); } catch (Exception e) {}
            } else if (line.contains("High") && line.contains(":")) {
                try { high = Integer.parseInt(line.split(":")[1].trim()); } catch (Exception e) {}
            } else if (line.contains("Medium") && line.contains(":")) {
                try { medium = Integer.parseInt(line.split(":")[1].trim()); } catch (Exception e) {}
            } else if (line.contains("Low") && line.contains(":")) {
                try { low = Integer.parseInt(line.split(":")[1].trim()); } catch (Exception e) {}
            }
        }
        
        html.append("<div class=\"metrics\">\n");
        html.append("<div class=\"metric-card\"><div class=\"metric-value\">").append(issueCount).append("</div><div class=\"metric-label\">Total Issues</div></div>\n");
        html.append("<div class=\"metric-card\"><div class=\"metric-value\" style=\"color: #ef4444;\">").append(critical).append("</div><div class=\"metric-label\">Critical</div></div>\n");
        html.append("<div class=\"metric-card\"><div class=\"metric-value\" style=\"color: #eab308;\">").append(high).append("</div><div class=\"metric-label\">High</div></div>\n");
        html.append("<div class=\"metric-card\"><div class=\"metric-value\" style=\"color: #f97316;\">").append(medium).append("</div><div class=\"metric-label\">Medium</div></div>\n");
        html.append("<div class=\"metric-card\"><div class=\"metric-value\" style=\"color: #3b82f6;\">").append(low).append("</div><div class=\"metric-label\">Low</div></div>\n");
        html.append("</div>\n");
    }

    private void appendIssueDetail(StringBuilder html, CodeIssue issue, String projectPath) {
        String severityClass = "issue-" + issue.getSeverity().toLowerCase();
        
        html.append("<div class=\"issue-card ").append(severityClass).append("\">\n");
        
        // Header
        html.append("<div class=\"issue-header\">\n");
        html.append("<span class=\"badge badge-").append(issue.getSeverity().toLowerCase()).append("\">")
            .append(issue.getSeverity().toUpperCase()).append("</span>\n");
        html.append("<strong>").append(issue.getType()).append("</strong>\n");
        html.append("<span style=\"color: #6b7280;\">").append(issue.getFile()).append(":").append(issue.getLine()).append("</span>\n");
        html.append("</div>\n");
        
        // Message
        html.append("<p style=\"margin-bottom: 15px;\"><strong>Issue:</strong> ").append(escapeHtml(issue.getMessage())).append("</p>\n");
        
        // Why this is a code smell
        if (issue.getDetailedReason() != null && !issue.getDetailedReason().isEmpty()) {
            html.append("<div class=\"explanation\">\n");
            html.append("<h4>❓ Why is this a code smell?</h4>\n");
            html.append("<p>").append(escapeHtml(issue.getDetailedReason())).append("</p>\n");
            html.append("</div>\n");
        }
        
        // Code snippet
        String codeSnippet = extractCodeSnippet(projectPath, issue.getFile(), issue.getLine(), issue.getType());
        if (codeSnippet != null) {
            html.append("<div class=\"code-section\">\n");
            html.append("<h4>📄 Source Code</h4>\n");
            html.append("<pre>").append(escapeHtml(codeSnippet)).append("</pre>\n");
            html.append("</div>\n");
        }
        
        // Suggestion
        if (issue.getSuggestion() != null && !issue.getSuggestion().isEmpty()) {
            html.append("<div class=\"refactored\">\n");
            html.append("<h4>💡 Refactoring Suggestion</h4>\n");
            html.append("<p>").append(escapeHtml(issue.getSuggestion())).append("</p>\n");
            html.append("</div>\n");
        }
        
        html.append("</div>\n");
    }

    private String extractCodeSnippet(String projectPath, String fileName, int lineNumber, String smellType) {
        try {
            File projectDir = new File(projectPath);
            File javaFile = findJavaFile(projectDir, fileName);
            
            if (javaFile == null || !javaFile.exists()) {
                return null;
            }
            
            List<String> lines = Files.readAllLines(javaFile.toPath());
            int startLine = Math.max(0, lineNumber - 6);
            int endLine = Math.min(lines.size(), lineNumber + 5);
            
            // For LongMethod, try to extract the entire method
            if ("LongMethod".equals(smellType)) {
                for (int i = lineNumber - 1; i >= 0; i--) {
                    if (lines.get(i).matches(".*\\b(public|private|protected)\\s+(static\\s+)?\\w+\\s+\\w+\\s*\\(.*")) {
                        startLine = i;
                        break;
                    }
                }
                
                int braceCount = 0;
                for (int i = startLine; i < lines.size(); i++) {
                    String line = lines.get(i);
                    braceCount += countChar(line, '{') - countChar(line, '}');
                    if (braceCount == 0 && i > startLine) {
                        endLine = i + 1;
                        break;
                    }
                }
            }
            
            StringBuilder snippet = new StringBuilder();
            for (int i = startLine; i < endLine; i++) {
                snippet.append(String.format("%4d | %s\n", i + 1, lines.get(i)));
            }
            
            return snippet.toString();
        } catch (Exception e) {
            return "// Unable to extract code snippet: " + e.getMessage();
        }
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

    private int countChar(String str, char ch) {
        return (int) str.chars().filter(c -> c == ch).count();
    }

    private String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&#39;");
    }
    
    private void appendCharts(StringBuilder html, List<CodeIssue> issues) {
        html.append("<h2>Code Quality Visualization</h2>\n");
        html.append("<div class=\"chart-grid\">\n");
        
        html.append("<div class=\"chart-container\">\n");
        html.append("<h3>Code Distribution</h3>\n");
        html.append("<canvas id=\"codeDistributionChart\"></canvas>\n");
        html.append("</div>\n");
        
        html.append("<div class=\"chart-container\">\n");
        html.append("<h3>Code Smell Types</h3>\n");
        html.append("<canvas id=\"smellTypesChart\"></canvas>\n");
        html.append("</div>\n");
        
        html.append("<div class=\"chart-container\">\n");
        html.append("<h3>Severity Distribution</h3>\n");
        html.append("<canvas id=\"severityChart\"></canvas>\n");
        html.append("</div>\n");
        
        html.append("</div>\n");
    }
    
    private void appendChartScripts(StringBuilder html, String reportContent, List<CodeIssue> issues) {
        // Calculate data
        Set<String> filesWithSmells = new HashSet<>();
        Map<String, Integer> smellTypes = new HashMap<>();
        Map<String, Integer> severityCounts = new HashMap<>();
        severityCounts.put("Critical", 0);
        severityCounts.put("High", 0);
        severityCounts.put("Medium", 0);
        severityCounts.put("Low", 0);
        
        for (CodeIssue issue : issues) {
            filesWithSmells.add(issue.getFile());
            smellTypes.merge(issue.getType(), 1, Integer::sum);
            severityCounts.merge(issue.getSeverity(), 1, Integer::sum);
        }
        
        // Extract total files from report
        int totalFiles = extractTotalFiles(reportContent);
        int cleanFiles = Math.max(0, totalFiles - filesWithSmells.size());
        
        html.append("\n// Code Distribution Chart\n");
        html.append("new Chart(document.getElementById('codeDistributionChart'), {\n");
        html.append("  type: 'pie',\n");
        html.append("  data: { labels: ['Clean Files', 'Files with Smells'], datasets: [{ data: [").append(cleanFiles).append(", ").append(filesWithSmells.size()).append("], backgroundColor: ['#10b981', '#ef4444'] }] },\n");
        html.append("  options: { responsive: true, maintainAspectRatio: false, plugins: { legend: { position: 'bottom' } } }\n");
        html.append("});\n\n");
        
        html.append("// Smell Types Chart\n");
        html.append("new Chart(document.getElementById('smellTypesChart'), {\n");
        html.append("  type: 'bar',\n");
        html.append("  data: { labels: [");
        smellTypes.keySet().forEach(type -> html.append("'").append(type).append("',"));
        html.append("], datasets: [{ label: 'Count', data: [");
        smellTypes.values().forEach(count -> html.append(count).append(","));
        html.append("], backgroundColor: '#3b82f6' }] },\n");
        html.append("  options: { responsive: true, maintainAspectRatio: false, plugins: { legend: { display: false } }, scales: { y: { beginAtZero: true } } }\n");
        html.append("});\n\n");
        
        html.append("// Severity Distribution Chart\n");
        html.append("new Chart(document.getElementById('severityChart'), {\n");
        html.append("  type: 'doughnut',\n");
        html.append("  data: { labels: ['Critical', 'High', 'Medium', 'Low'], datasets: [{ data: [");
        html.append(severityCounts.get("Critical")).append(", ");
        html.append(severityCounts.get("High")).append(", ");
        html.append(severityCounts.get("Medium")).append(", ");
        html.append(severityCounts.get("Low"));
        html.append("], backgroundColor: ['#ef4444', '#eab308', '#f97316', '#3b82f6'] }] },\n");
        html.append("  options: { responsive: true, maintainAspectRatio: false, plugins: { legend: { position: 'bottom' } } }\n");
        html.append("});\n");
    }
    
    private int extractTotalFiles(String reportContent) {
        String[] lines = reportContent.split("\n");
        for (String line : lines) {
            if (line.contains("Analyzed") && line.contains("files")) {
                try {
                    String[] parts = line.split(" ");
                    for (int i = 0; i < parts.length - 1; i++) {
                        if (parts[i + 1].equals("files") || parts[i + 1].equals("files,")) {
                            return Integer.parseInt(parts[i]);
                        }
                    }
                } catch (Exception e) {}
            }
        }
        return 0;
    }
}
