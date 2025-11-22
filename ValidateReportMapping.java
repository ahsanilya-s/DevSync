import com.devsync.utils.ReportValidator;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ValidateReportMapping {
    public static void main(String[] args) {
        try {
            // Test with existing comprehensive report
            String reportPath = "uploads/smell_project1/smell_project1_comprehensive.txt";
            
            if (!Files.exists(Paths.get(reportPath))) {
                System.out.println("Report file not found: " + reportPath);
                System.out.println("Please run an analysis first to generate a report.");
                return;
            }
            
            System.out.println("=== Validating Report Data Mapping ===");
            System.out.println("Report: " + reportPath);
            
            // Read report content
            String reportContent = new String(Files.readAllBytes(Paths.get(reportPath)));
            
            // Validate the report
            ReportValidator.ValidationResult result = ReportValidator.validateReport(reportContent);
            
            // Print results
            ReportValidator.printValidationReport(result);
            
            if (result.isValid) {
                System.out.println("\n✅ Report data mapping is CORRECT!");
                System.out.println("The 'View Detailed Report' functionality should work properly.");
            } else {
                System.out.println("\n❌ Report data mapping has ISSUES!");
                System.out.println("The 'View Detailed Report' functionality may not display data correctly.");
                System.out.println("\nRecommendations:");
                System.out.println("1. Check the ReportGenerator.java for consistency issues");
                System.out.println("2. Verify the CodeAnalysisEngine.java severity counting");
                System.out.println("3. Test the VisualReport.jsx parsing logic");
            }
            
        } catch (Exception e) {
            System.err.println("Validation failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}