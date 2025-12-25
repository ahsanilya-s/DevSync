package com.devsync.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "commit_analysis")
public class CommitAnalysis {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String userId;
    private String repoOwner;
    private String repoName;
    private String commitSha;
    private String commitMessage;
    private LocalDateTime commitDate;
    private LocalDateTime analysisDate;
    
    private Integer totalIssues;
    private Integer criticalIssues;
    private Integer warnings;
    private Integer suggestions;
    
    @Column(length = 1000)
    private String reportPath;

    public CommitAnalysis() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getRepoOwner() { return repoOwner; }
    public void setRepoOwner(String repoOwner) { this.repoOwner = repoOwner; }

    public String getRepoName() { return repoName; }
    public void setRepoName(String repoName) { this.repoName = repoName; }

    public String getCommitSha() { return commitSha; }
    public void setCommitSha(String commitSha) { this.commitSha = commitSha; }

    public String getCommitMessage() { return commitMessage; }
    public void setCommitMessage(String commitMessage) { this.commitMessage = commitMessage; }

    public LocalDateTime getCommitDate() { return commitDate; }
    public void setCommitDate(LocalDateTime commitDate) { this.commitDate = commitDate; }

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

    public String getReportPath() { return reportPath; }
    public void setReportPath(String reportPath) { this.reportPath = reportPath; }
}
