import com.devsync.analyzer.CodeAnalysisEngine;
import com.devsync.reports.ReportGenerator;
import java.util.*;

public class TestReportMapping {
    public static void main(String[] args) {
        try {
            // Test with existing project
            String testProject = "uploads/smell_project1";
            
            System.out.println("=== Testing Report Data Mapping ===");
            System.out.println("Project: " + testProject);
            
            // 1. Run analysis engine
            CodeAnalysisEngine engine = new CodeAnalysisEngine();
            Map<String, Object> results = engine.analyzeProject(testProject);
            
            System.out.println("\n=== Analysis Engine Results ===");
            System.out.println("Total Files: " + results.get("totalFiles"));
            System.out.println("Total Issues: " + results.get("totalIssues"));
            
            @SuppressWarnings("unchecked")
            Map<String, Integer> severityCounts = (Map<String, Integer>) results.get("severityCounts");
            System.out.println("Severity Counts: " + severityCounts);
            
            @SuppressWarnings("unchecked")
            List<String> issues = (List<String>) results.get("issues");
            System.out.println("Sample Issues:");
            issues.stream().limit(3).forEach(issue -> System.out.println("  " + issue));
            
            // 2. Generate comprehensive report
            ReportGenerator generator = new ReportGenerator();
            String report = generator.generateComprehensiveReport(results);
            
            System.out.println("\n=== Generated Report Structure ===");
            String[] sections = report.split("\n\n");
            for (String section : sections) {
                String[] lines = section.split("\n");
                if (lines.length > 0) {
                    System.out.println("Section: " + lines[0]);
                }
            }
            
            // 3. Test parsing logic (simulate frontend)
            System.out.println("\n=== Testing Frontend Parsing ===");
            testFrontendParsing(report);
            
            // 4. Write test report
            String testReportPath = testProject + "/test_mapping_report.txt";
            try (java.io.FileWriter writer = new java.io.FileWriter(testReportPath)) {
                writer.write(report);
            }
            System.out.println("Test report written to: " + testReportPath);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static void testFrontendParsing(String content) {
        String[] lines = content.split("\n");
        Map<String, Integer> parsedSeverity = new HashMap<>();
        Map<String, Integer> parsedTypes = new HashMap<>();
        Map<String, Map<String, Integer>> parsedFiles = new HashMap<>();
        List<Map<String, Object>> parsedIssues = new ArrayList<>();
        
        String currentSection = "";
        
        for (String line : lines) {
            // Track sections
            if (line.startsWith("SEVERITY BREAKDOWN")) {
                currentSection = "severity";
                continue;
            }
            if (line.startsWith("ISSUE TYPE BREAKDOWN")) {
                currentSection = "types";
                continue;
            }
            if (line.startsWith("FILE-WISE BREAKDOWN")) {
                currentSection = "files";
                continue;
            }
            if (line.startsWith("DETAILED ISSUES")) {
                currentSection = "issues";
                continue;
            }
            
            // Parse severity breakdown
            if (currentSection.equals("severity") && line.matches("\\w+\\s*:\\s*\\d+")) {
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    String severity = parts[0].trim();
                    int count = Integer.parseInt(parts[1].trim());
                    parsedSeverity.put(severity, count);
                }
            }
            
            // Parse type breakdown
            if (currentSection.equals("types") && line.matches("[\\w\\s]+\\s*:\\s*\\d+")) {
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    String type = parts[0].trim();
                    int count = Integer.parseInt(parts[1].trim());
                    parsedTypes.put(type, count);
                }
            }
            
            // Parse file breakdown
            if (currentSection.equals("files") && line.startsWith("File: ")) {
                String fileName = line.substring(6, line.indexOf(" (Total:"));
                parsedFiles.put(fileName, new HashMap<>());
            }
            
            // Parse detailed issues
            if (line.startsWith("🚨 ")) {
                String cleanLine = line.substring(2).trim();
                if (cleanLine.matches("^[🔴🟡🟠⚠️] \\[\\w+\\] .+?:\\d+ - .+")) {
                    Map<String, Object> issue = new HashMap<>();
                    issue.put("raw", cleanLine);
                    parsedIssues.add(issue);
                }
            }
        }
        
        System.out.println("Parsed Severity: " + parsedSeverity);
        System.out.println("Parsed Types: " + parsedTypes);
        System.out.println("Parsed Files: " + parsedFiles.keySet());
        System.out.println("Parsed Issues Count: " + parsedIssues.size());
        
        // Validate parsing
        boolean valid = true;
        if (parsedSeverity.isEmpty()) {
            System.out.println("❌ Failed to parse severity breakdown");
            valid = false;
        }
        if (parsedTypes.isEmpty()) {
            System.out.println("❌ Failed to parse type breakdown");
            valid = false;
        }
        if (parsedIssues.isEmpty()) {
            System.out.println("❌ Failed to parse detailed issues");
            valid = false;
        }
        
        if (valid) {
            System.out.println("✅ All parsing tests passed!");
        }
    }
}