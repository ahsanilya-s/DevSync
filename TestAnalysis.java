import com.devsync.analyzer.CodeAnalysisEngine;
import java.util.Map;

public class TestAnalysis {
    public static void main(String[] args) {
        CodeAnalysisEngine engine = new CodeAnalysisEngine();
        
        // Test with our test file
        String testFilePath = "d:\\devsync - stable - eleven 1.5.1";
        
        try {
            Map<String, Object> results = engine.analyzeProject(testFilePath);
            
            System.out.println("=== ANALYSIS RESULTS ===");
            System.out.println("Summary: " + results.get("summary"));
            System.out.println("Total Files: " + results.get("totalFiles"));
            System.out.println("Processed Files: " + results.get("processedFiles"));
            System.out.println("Total Issues: " + results.get("totalIssues"));
            
            System.out.println("\n=== SEVERITY BREAKDOWN ===");
            Map<String, Integer> severityCounts = (Map<String, Integer>) results.get("severityCounts");
            severityCounts.forEach((severity, count) -> 
                System.out.println(severity + ": " + count));
            
            System.out.println("\n=== DETECTOR BREAKDOWN ===");
            Map<String, Integer> detectorCounts = (Map<String, Integer>) results.get("detectorCounts");
            detectorCounts.forEach((detector, count) -> 
                System.out.println(detector + ": " + count));
            
            System.out.println("\n=== SAMPLE ISSUES ===");
            java.util.List<String> issues = (java.util.List<String>) results.get("issues");
            issues.stream().limit(10).forEach(System.out::println);
            
        } catch (Exception e) {
            System.err.println("Analysis failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}