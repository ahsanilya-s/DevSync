package com.devsync.reports;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class ReportGenerator {

    public static String generateTextReport(List<String> issues, String outputDir) throws IOException {
        String reportPath = outputDir + "/report.txt";

        try (FileWriter writer = new FileWriter(reportPath)) {
            if (issues.isEmpty()) {
                writer.write("🎉 No issues found in the code.\n");
            } else {
                for (String issue : issues) {
                    writer.write("🚨 " + issue + "\n");
                }
            }
        }

        return reportPath;
    }

    public static void appendAIAnalysis(String reportPath, String aiAnalysis) throws IOException {
        try (FileWriter writer = new FileWriter(reportPath, true)) {
            writer.write("\n\n=== AI ANALYSIS ===\n");
            writer.write(aiAnalysis);
            writer.write("\n\n=== END AI ANALYSIS ===\n");
        }
    }

    public static String readReportContent(String reportPath) throws IOException {
        return new String(Files.readAllBytes(Paths.get(reportPath)));
    }
}
