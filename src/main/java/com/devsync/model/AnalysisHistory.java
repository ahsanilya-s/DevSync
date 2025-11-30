package com.devsync.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "analysis_history")
public class AnalysisHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String userId;
    
    @Column(nullable = false)
    private String projectName;
    
    @Column(nullable = false)
    private String reportPath;
    
    @Column(nullable = false)
    private LocalDateTime analysisDate;
    
    @Column(nullable = false)
    private Integer totalIssues;
    
    @Column(nullable = false)
    private Integer criticalIssues;
    
    @Column(nullable = false)
    private Integer warnings;
    
    @Column(nullable = false)
    private Integer suggestions;
    
    @Column
    private String projectPath;
    
    @Column
    private Integer totalLOC;
    
    @Column
    private String grade;
    
    @Column
    private Double issueDensity;

    public AnalysisHistory() {}

    public AnalysisHistory(String userId, String projectName, String reportPath, 
                          Integer totalIssues, Integer criticalIssues, Integer warnings, Integer suggestions) {
        this.userId = userId;
        this.projectName = projectName;
        this.reportPath = reportPath;
        this.analysisDate = LocalDateTime.now();
        this.totalIssues = totalIssues;
        this.criticalIssues = criticalIssues;
        this.warnings = warnings;
        this.suggestions = suggestions;
        // Extract project path from report path
        if (reportPath != null && reportPath.contains("/")) {
            this.projectPath = reportPath.substring(0, reportPath.lastIndexOf("/"));
        }
    }
    
    public AnalysisHistory(String userId, String projectName, String reportPath, 
                          Integer totalIssues, Integer criticalIssues, Integer warnings, Integer suggestions,
                          Integer totalLOC, String grade, Double issueDensity) {
        this(userId, projectName, reportPath, totalIssues, criticalIssues, warnings, suggestions);
        this.totalLOC = totalLOC;
        this.grade = grade;
        this.issueDensity = issueDensity;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    
    public String getProjectName() { return projectName; }
    public void setProjectName(String projectName) { this.projectName = projectName; }
    
    public String getReportPath() { return reportPath; }
    public void setReportPath(String reportPath) { this.reportPath = reportPath; }
    
    public LocalDateTime getAnalysisDate() { return analysisDate; }
    public void setAnalysisDate(LocalDateTime analysisDate) { this.analysisDate = analysisDate; }
    
    public Integer getTotalIssues() { return totalIssues; }
    public void setTotalIssues(Integer totalIssues) { this.totalIssues = totalIssues; }
    
    public Integer getCriticalIssues() { return criticalIssues; }
    public void setCriticalIssues(Integer criticalIssues) { this.criticalIssues = criticalIssues; }
    
    public Integer getWarnings() { return warnings; }
    public void setWarnings(Integer warnings) { this.warnings = warnings; }
    
    public Integer getSuggestions() { return suggestions; }
    public void setSuggestions(Integer suggestions) { this.suggestions = suggestions; }
    
    public String getProjectPath() { return projectPath; }
    public void setProjectPath(String projectPath) { this.projectPath = projectPath; }
    
    public Integer getTotalLOC() { return totalLOC; }
    public void setTotalLOC(Integer totalLOC) { this.totalLOC = totalLOC; }
    
    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }
    
    public Double getIssueDensity() { return issueDensity; }
    public void setIssueDensity(Double issueDensity) { this.issueDensity = issueDensity; }
}