package com.devsync;

import com.devsync.analyzer.CodeAnalysisEngine;
import com.devsync.reports.ReportGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class ReportMappingTest {
    
    private CodeAnalysisEngine analysisEngine;
    private ReportGenerator reportGenerator;
    
    @BeforeEach
    void setUp() {
        analysisEngine = new CodeAnalysisEngine();
        reportGenerator = new ReportGenerator();
    }
    
    @Test
    void testReportDataMapping() {
        // Use existing test project
        String testProjectPath = "uploads/smell_project1";
        File testDir = new File(testProjectPath);
        
        if (!testDir.exists()) {
            System.out.println("Test project not found, skipping test");
            return;
        }
        
        try {
            // 1. Run analysis engine
            Map<String, Object> analysisResults = analysisEngine.analyzeProject(testProjectPath);
            
            // Verify analysis results structure
            assertNotNull(analysisResults.get("issues"), "Issues list should not be null");
            assertNotNull(analysisResults.get("severityCounts"), "Severity counts should not be null");
            assertNotNull(analysisResults.get("totalFiles"), "Total files should not be null");
            assertNotNull(analysisResults.get("totalIssues"), "Total issues should not be null");
            
            @SuppressWarnings("unchecked")
            List<String> issues = (List<String>) analysisResults.get("issues");
            @SuppressWarnings("unchecked")
            Map<String, Integer> severityCounts = (Map<String, Integer>) analysisResults.get("severityCounts");
            
            System.out.println("=== Analysis Results ===");
            System.out.println("Total Files: " + analysisResults.get("totalFiles"));
            System.out.println("Total Issues: " + analysisResults.get("totalIssues"));
            System.out.println("Severity Counts: " + severityCounts);
            System.out.println("Sample Issues: " + issues.subList(0, Math.min(3, issues.size())));
            
            // 2. Generate comprehensive report
            String comprehensiveReport = reportGenerator.generateComprehensiveReport(analysisResults);
            assertNotNull(comprehensiveReport, "Comprehensive report should not be null");
            assertFalse(comprehensiveReport.isEmpty(), "Comprehensive report should not be empty");
            
            // 3. Verify report structure
            assertTrue(comprehensiveReport.contains("SEVERITY BREAKDOWN"), "Report should contain severity breakdown");
            assertTrue(comprehensiveReport.contains("ISSUE TYPE BREAKDOWN"), "Report should contain type breakdown");
            assertTrue(comprehensiveReport.contains("FILE-WISE BREAKDOWN"), "Report should contain file breakdown");
            assertTrue(comprehensiveReport.contains("DETAILED ISSUES"), "Report should contain detailed issues");
            
            // 4. Test data consistency
            testDataConsistency(analysisResults, comprehensiveReport);
            
            // 5. Test issue format consistency
            testIssueFormatConsistency(issues, comprehensiveReport);
            
            System.out.println("✅ All report mapping tests passed!");
            
        } catch (Exception e) {
            fail("Test failed with exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void testDataConsistency(Map<String, Object> analysisResults, String report) {
        @SuppressWarnings("unchecked")
        Map<String, Integer> severityCounts = (Map<String, Integer>) analysisResults.get("severityCounts");
        
        // Extract severity counts from report
        Map<String, Integer> reportSeverityCounts = extractSeverityCountsFromReport(report);
        
        System.out.println("=== Data Consistency Test ===");
        System.out.println("Analysis Severity Counts: " + severityCounts);
        System.out.println("Report Severity Counts: " + reportSeverityCounts);
        
        // Verify consistency
        for (String severity : Arrays.asList("Critical", "High", "Medium", "Low")) {
            int analysisCount = severityCounts.getOrDefault(severity, 0);
            int reportCount = reportSeverityCounts.getOrDefault(severity, 0);
            
            assertEquals(analysisCount, reportCount, 
                "Severity count mismatch for " + severity + ": analysis=" + analysisCount + ", report=" + reportCount);
        }
    }
    
    private void testIssueFormatConsistency(List<String> issues, String report) {
        System.out.println("=== Issue Format Consistency Test ===");
        
        // Count issues in detailed section
        String[] reportLines = report.split("\n");
        int detailedIssueCount = 0;
        boolean inDetailedSection = false;
        
        for (String line : reportLines) {
            if (line.startsWith("DETAILED ISSUES")) {
                inDetailedSection = true;
                continue;
            }
            if (inDetailedSection && line.startsWith("🚨 ")) {
                detailedIssueCount++;
                
                // Verify issue format: 🚨 🔴 [Type] file.java:123 - description
                String cleanLine = line.substring(2).trim(); // Remove 🚨 prefix
                Pattern issuePattern = Pattern.compile("^[🔴🟡🟠⚠️] \\[\\w+\\] .+?:\\d+ - .+");
                
                assertTrue(issuePattern.matcher(cleanLine).matches(), 
                    "Issue format is incorrect: " + line);
            }
        }
        
        System.out.println("Issues from analysis: " + issues.size());
        System.out.println("Issues in detailed section: " + detailedIssueCount);
        
        assertEquals(issues.size(), detailedIssueCount, 
            "Issue count mismatch between analysis and report detailed section");
    }
    
    private Map<String, Integer> extractSeverityCountsFromReport(String report) {
        Map<String, Integer> counts = new HashMap<>();
        
        String[] lines = report.split("\n");
        boolean inSeveritySection = false;
        
        for (String line : lines) {
            if (line.startsWith("SEVERITY BREAKDOWN")) {
                inSeveritySection = true;
                continue;
            }
            if (inSeveritySection && line.trim().isEmpty()) {
                break; // End of section
            }
            if (inSeveritySection && line.contains(":") && !line.startsWith("-")) {
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    String severity = parts[0].trim();
                    try {
                        int count = Integer.parseInt(parts[1].trim());
                        counts.put(severity, count);
                    } catch (NumberFormatException e) {
                        // Skip invalid lines
                    }
                }
            }
        }
        
        return counts;
    }
    
    @Test
    void testReportReadability() {
        String testProjectPath = "uploads/smell_project1";
        File testDir = new File(testProjectPath);
        
        if (!testDir.exists()) {
            System.out.println("Test project not found, skipping readability test");
            return;
        }
        
        try {
            Map<String, Object> analysisResults = analysisEngine.analyzeProject(testProjectPath);
            String report = reportGenerator.generateComprehensiveReport(analysisResults);
            
            // Write test report for manual inspection
            String testReportPath = testProjectPath + "/test_report_mapping.txt";
            try (java.io.FileWriter writer = new java.io.FileWriter(testReportPath)) {
                writer.write(report);
            }
            
            System.out.println("Test report written to: " + testReportPath);
            System.out.println("Report length: " + report.length() + " characters");
            
            // Basic readability checks
            assertTrue(report.length() > 100, "Report should have substantial content");
            assertTrue(report.contains("==="), "Report should have section headers");
            assertTrue(report.split("\n").length > 10, "Report should have multiple lines");
            
        } catch (Exception e) {
            fail("Readability test failed: " + e.getMessage());
        }
    }
}