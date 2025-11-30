package com.devsync.model;

import com.devsync.dto.LongMethodThresholdDetails;

public class CodeIssue {
    private String type;
    private String file;
    private int line;
    private String severity;
    private String message;
    private String suggestion;
    private String detailedReason;
    private LongMethodThresholdDetails thresholdDetails;

    public CodeIssue() {}

    public CodeIssue(String type, String file, int line, String severity, String message, String suggestion) {
        this.type = type;
        this.file = file;
        this.line = line;
        this.severity = severity;
        this.message = message;
        this.suggestion = suggestion;
    }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getFile() { return file; }
    public void setFile(String file) { this.file = file; }

    public int getLine() { return line; }
    public void setLine(int line) { this.line = line; }

    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getSuggestion() { return suggestion; }
    public void setSuggestion(String suggestion) { this.suggestion = suggestion; }

    public String getDetailedReason() { return detailedReason; }
    public void setDetailedReason(String detailedReason) { this.detailedReason = detailedReason; }

    public LongMethodThresholdDetails getThresholdDetails() { return thresholdDetails; }
    public void setThresholdDetails(LongMethodThresholdDetails thresholdDetails) { this.thresholdDetails = thresholdDetails; }
}
