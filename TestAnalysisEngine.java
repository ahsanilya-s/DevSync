import com.devsync.analyzer.CodeAnalysisEngine;
import java.util.Map;
import java.util.List;

public class TestAnalysisEngine {
    public static void main(String[] args) {
        CodeAnalysisEngine engine = new CodeAnalysisEngine();
        
        // Test with the smell_project directory
        String testPath = "uploads/smell_project";
        Map<String, Object> results = engine.analyzeProject(testPath);
        
        System.out.println("=== ANALYSIS RESULTS ===");
        System.out.println("Total Files: " + results.get("totalFiles"));
        System.out.println("Total Issues: " + results.get("totalIssues"));
        System.out.println("Summary: " + results.get("summary"));
        
        @SuppressWarnings("unchecked")
        Map<String, Integer> severityCounts = (Map<String, Integer>) results.get("severityCounts");
        System.out.println("\nSeverity Breakdown:");
        severityCounts.forEach((severity, count) -> 
            System.out.println("  " + severity + ": " + count));
        
        @SuppressWarnings("unchecked")
        List<String> issues = (List<String>) results.get("issues");
        System.out.println("\nFirst 10 Issues:");
        issues.stream().limit(10).forEach(System.out::println);
        
        // Count issues by type
        System.out.println("\nIssue Types Found:");
        issues.stream()
            .filter(issue -> issue.contains("[") && issue.contains("]"))
            .map(issue -> issue.substring(issue.indexOf("[") + 1, issue.indexOf("]")))
            .distinct()
            .sorted()
            .forEach(type -> {
                long count = issues.stream()
                    .filter(issue -> issue.contains("[" + type + "]"))
                    .count();
                System.out.println("  " + type + ": " + count);
            });
    }
}