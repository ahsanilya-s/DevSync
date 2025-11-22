import java.nio.file.Files;
import java.nio.file.Paths;

public class QuickValidation {
    public static void main(String[] args) {
        try {
            String reportPath = "uploads/smell_project10/smell_project10_comprehensive.txt";
            String content = new String(Files.readAllBytes(Paths.get(reportPath)));
            
            System.out.println("=== Quick Validation Check ===");
            
            // Check severity parsing
            String severitySection = content.split("SEVERITY BREAKDOWN")[1].split("\n\n")[0];
            System.out.println("Severity Section:");
            System.out.println(severitySection);
            
            // Check type parsing  
            String typeSection = content.split("ISSUE TYPE BREAKDOWN")[1].split("\n\n")[0];
            System.out.println("\nType Section:");
            System.out.println(typeSection);
            
            // Check file parsing
            String fileSection = content.split("FILE-WISE BREAKDOWN")[1].split("DETAILED ISSUES")[0];
            System.out.println("\nFile Section (first 500 chars):");
            System.out.println(fileSection.substring(0, Math.min(500, fileSection.length())));
            
            // Check issue parsing
            String issueSection = content.split("DETAILED ISSUES")[1];
            long issueCount = issueSection.lines().filter(line -> line.startsWith("🚨")).count();
            System.out.println("\nDetailed Issues Count: " + issueCount);
            
            // Validate totals
            int criticalFromSection = extractCount(severitySection, "Critical");
            int highFromSection = extractCount(severitySection, "High");
            int mediumFromSection = extractCount(severitySection, "Medium");
            int totalFromSeverity = criticalFromSection + highFromSection + mediumFromSection;
            
            System.out.println("\n=== Validation Results ===");
            System.out.println("Critical: " + criticalFromSection);
            System.out.println("High: " + highFromSection);
            System.out.println("Medium: " + mediumFromSection);
            System.out.println("Total from severity: " + totalFromSeverity);
            System.out.println("Total from issues: " + issueCount);
            System.out.println("Match: " + (totalFromSeverity == issueCount ? "✅" : "❌"));
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static int extractCount(String section, String severity) {
        try {
            String[] lines = section.split("\n");
            for (String line : lines) {
                if (line.trim().startsWith(severity)) {
                    String[] parts = line.split(":");
                    if (parts.length == 2) {
                        return Integer.parseInt(parts[1].trim());
                    }
                }
            }
        } catch (Exception e) {
            // ignore
        }
        return 0;
    }
}