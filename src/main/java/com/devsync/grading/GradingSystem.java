package com.devsync.grading;

import java.util.Map;

public class GradingSystem {
    
    // Industry-standard thresholds for issues per KLOC (1000 lines of code)
    private static final double EXCELLENT_THRESHOLD = 0.5;   // < 0.5 issues/KLOC = A
    private static final double GOOD_THRESHOLD = 2.0;        // < 2.0 issues/KLOC = B
    private static final double ACCEPTABLE_THRESHOLD = 5.0;  // < 5.0 issues/KLOC = C
    private static final double POOR_THRESHOLD = 10.0;       // < 10.0 issues/KLOC = D
    // >= 10.0 issues/KLOC = F
    
    // Severity weights for calculating quality impact
    private static final double CRITICAL_WEIGHT = 10.0;
    private static final double HIGH_WEIGHT = 5.0;
    private static final double MEDIUM_WEIGHT = 2.0;
    private static final double LOW_WEIGHT = 0.5;
    
    public static class GradeResult {
        private String letterGrade;
        private double numericScore;
        private double issueDensity;
        private int totalLOC;
        private int totalIssues;
        private String qualityLevel;
        private String recommendation;
        
        public GradeResult(String letterGrade, double numericScore, double issueDensity, 
                          int totalLOC, int totalIssues, String qualityLevel, String recommendation) {
            this.letterGrade = letterGrade;
            this.numericScore = numericScore;
            this.issueDensity = issueDensity;
            this.totalLOC = totalLOC;
            this.totalIssues = totalIssues;
            this.qualityLevel = qualityLevel;
            this.recommendation = recommendation;
        }
        
        public String getLetterGrade() { return letterGrade; }
        public double getNumericScore() { return numericScore; }
        public double getIssueDensity() { return issueDensity; }
        public int getTotalLOC() { return totalLOC; }
        public int getTotalIssues() { return totalIssues; }
        public String getQualityLevel() { return qualityLevel; }
        public String getRecommendation() { return recommendation; }
        
        @Override
        public String toString() {
            return String.format("Grade: %s (%.1f%%) | Quality: %s | Density: %.2f issues/KLOC | LOC: %d | Issues: %d",
                letterGrade, numericScore, qualityLevel, issueDensity, totalLOC, totalIssues);
        }
    }
    
    /**
     * Calculate fair grade based on issue density (issues per KLOC)
     * This ensures large and small projects are graded fairly
     */
    public static GradeResult calculateGrade(Map<String, Integer> severityCounts, int totalLOC) {
        if (totalLOC <= 0) {
            return new GradeResult("N/A", 0, 0, 0, 0, "Unknown", "Cannot grade: No code to analyze");
        }
        
        int critical = severityCounts.getOrDefault("Critical", 0);
        int high = severityCounts.getOrDefault("High", 0);
        int medium = severityCounts.getOrDefault("Medium", 0);
        int low = severityCounts.getOrDefault("Low", 0);
        
        int totalIssues = critical + high + medium + low;
        
        // Handle perfect code
        if (totalIssues == 0) {
            return new GradeResult("A+", 100.0, 0.0, totalLOC, 0, "Excellent", 
                "Perfect! No issues detected. Continue following best practices.");
        }
        
        // Calculate weighted issue score
        double weightedIssues = (critical * CRITICAL_WEIGHT) + 
                               (high * HIGH_WEIGHT) + 
                               (medium * MEDIUM_WEIGHT) + 
                               (low * LOW_WEIGHT);
        
        // Calculate issue density per KLOC (1000 lines)
        double kloc = totalLOC / 1000.0;
        double issueDensity = totalIssues / kloc;
        double weightedDensity = weightedIssues / kloc;
        
        // Calculate base score from density
        double baseScore = calculateBaseScore(issueDensity);
        
        // Apply severity penalties
        double finalScore = applyPenalties(baseScore, critical, high, totalLOC, issueDensity);
        
        // Determine letter grade
        String letterGrade = mapScoreToGrade(finalScore);
        String qualityLevel = getQualityLevel(letterGrade);
        String recommendation = getRecommendation(letterGrade, critical, high, issueDensity);
        
        return new GradeResult(letterGrade, finalScore, issueDensity, totalLOC, totalIssues, 
                              qualityLevel, recommendation);
    }
    
    private static double calculateBaseScore(double issueDensity) {
        // Map issue density to score (0-100)
        if (issueDensity < EXCELLENT_THRESHOLD) {
            // A range: 90-100
            return 100 - (issueDensity / EXCELLENT_THRESHOLD) * 10;
        } else if (issueDensity < GOOD_THRESHOLD) {
            // B range: 80-89
            double ratio = (issueDensity - EXCELLENT_THRESHOLD) / (GOOD_THRESHOLD - EXCELLENT_THRESHOLD);
            return 90 - (ratio * 10);
        } else if (issueDensity < ACCEPTABLE_THRESHOLD) {
            // C range: 70-79
            double ratio = (issueDensity - GOOD_THRESHOLD) / (ACCEPTABLE_THRESHOLD - GOOD_THRESHOLD);
            return 80 - (ratio * 10);
        } else if (issueDensity < POOR_THRESHOLD) {
            // D range: 60-69
            double ratio = (issueDensity - ACCEPTABLE_THRESHOLD) / (POOR_THRESHOLD - ACCEPTABLE_THRESHOLD);
            return 70 - (ratio * 10);
        } else {
            // F range: 0-59
            return Math.max(0, 60 - (issueDensity - POOR_THRESHOLD) * 2);
        }
    }
    
    private static double applyPenalties(double baseScore, int critical, int high, int totalLOC, double density) {
        double score = baseScore;
        
        // Critical issue penalty: -5 points per critical issue (scaled by project size)
        double criticalRatio = (critical * 1000.0) / totalLOC;
        if (criticalRatio > 1.0) {
            score -= 10; // More than 1 critical per KLOC
        } else if (critical > 0) {
            score -= 5;
        }
        
        // High density penalty
        if (density > 20) {
            score -= 15; // Extremely high density
        } else if (density > 15) {
            score -= 10;
        }
        
        // Cap grade if too many critical issues
        if (critical > 0 && density > 5) {
            score = Math.min(score, 79); // Cannot exceed C
        }
        
        return Math.max(0, Math.min(100, score));
    }
    
    private static String mapScoreToGrade(double score) {
        if (score >= 97) return "A+";
        if (score >= 93) return "A";
        if (score >= 90) return "A-";
        if (score >= 87) return "B+";
        if (score >= 83) return "B";
        if (score >= 80) return "B-";
        if (score >= 77) return "C+";
        if (score >= 73) return "C";
        if (score >= 70) return "C-";
        if (score >= 67) return "D+";
        if (score >= 63) return "D";
        if (score >= 60) return "D-";
        return "F";
    }
    
    private static String getQualityLevel(String grade) {
        char letter = grade.charAt(0);
        switch (letter) {
            case 'A': return "Excellent";
            case 'B': return "Good";
            case 'C': return "Acceptable";
            case 'D': return "Poor";
            default: return "Failing";
        }
    }
    
    private static String getRecommendation(String grade, int critical, int high, double density) {
        char letter = grade.charAt(0);
        
        if (letter == 'A') {
            return "Excellent code quality! Minor polish recommended.";
        } else if (letter == 'B') {
            return "Good quality. Address " + (critical + high) + " high-priority issues.";
        } else if (letter == 'C') {
            return "Acceptable but needs improvement. Focus on critical issues first.";
        } else if (letter == 'D') {
            return "Poor quality. Immediate refactoring required for " + critical + " critical issues.";
        } else {
            return "Failing quality standards. Major overhaul needed. Do not deploy.";
        }
    }
    
    /**
     * Generate detailed grading report
     */
    public static String generateGradingReport(GradeResult result) {
        StringBuilder report = new StringBuilder();
        
        report.append("╔════════════════════════════════════════════════════════════╗\n");
        report.append("║           CODE QUALITY GRADE REPORT                        ║\n");
        report.append("╚════════════════════════════════════════════════════════════╝\n\n");
        
        report.append(String.format("📊 Overall Grade: %s (%.1f%%)%n", result.letterGrade, result.numericScore));
        report.append(String.format("⭐ Quality Level: %s%n", result.qualityLevel));
        report.append(String.format("📏 Project Size: %,d lines of code%n", result.totalLOC));
        report.append(String.format("🐛 Total Issues: %d%n", result.totalIssues));
        report.append(String.format("📈 Issue Density: %.2f issues per 1000 lines (KLOC)%n", result.issueDensity));
        report.append(String.format("💡 Recommendation: %s%n%n", result.recommendation));
        
        report.append("═══════════════════════════════════════════════════════════\n");
        report.append("Industry Benchmarks (issues per KLOC):\n");
        report.append("  A (Excellent):  < 0.5 issues/KLOC\n");
        report.append("  B (Good):       < 2.0 issues/KLOC\n");
        report.append("  C (Acceptable): < 5.0 issues/KLOC\n");
        report.append("  D (Poor):       < 10.0 issues/KLOC\n");
        report.append("  F (Failing):    ≥ 10.0 issues/KLOC\n");
        report.append("═══════════════════════════════════════════════════════════\n");
        
        return report.toString();
    }
}
