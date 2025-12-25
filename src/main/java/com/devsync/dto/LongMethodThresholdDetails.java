package com.devsync.dto;

public class LongMethodThresholdDetails {
    private int statementCount;
    private int baseThreshold;
    private int criticalThreshold;
    private boolean exceedsStatementCount;
    
    private int cyclomaticComplexity;
    private int maxCyclomaticComplexity;
    private boolean exceedsCyclomaticComplexity;
    
    private int cognitiveComplexity;
    private int maxCognitiveComplexity;
    private boolean exceedsCognitiveComplexity;
    
    private int nestingDepth;
    private int maxNestingDepth;
    private boolean exceedsNestingDepth;
    
    private int responsibilityCount;
    private int maxResponsibilityCount;
    private boolean exceedsResponsibilityCount;
    
    private String summary;

    public LongMethodThresholdDetails() {}

    public int getStatementCount() { return statementCount; }
    public void setStatementCount(int statementCount) { this.statementCount = statementCount; }

    public int getBaseThreshold() { return baseThreshold; }
    public void setBaseThreshold(int baseThreshold) { this.baseThreshold = baseThreshold; }

    public int getCriticalThreshold() { return criticalThreshold; }
    public void setCriticalThreshold(int criticalThreshold) { this.criticalThreshold = criticalThreshold; }

    public boolean isExceedsStatementCount() { return exceedsStatementCount; }
    public void setExceedsStatementCount(boolean exceedsStatementCount) { this.exceedsStatementCount = exceedsStatementCount; }

    public int getCyclomaticComplexity() { return cyclomaticComplexity; }
    public void setCyclomaticComplexity(int cyclomaticComplexity) { this.cyclomaticComplexity = cyclomaticComplexity; }

    public int getMaxCyclomaticComplexity() { return maxCyclomaticComplexity; }
    public void setMaxCyclomaticComplexity(int maxCyclomaticComplexity) { this.maxCyclomaticComplexity = maxCyclomaticComplexity; }

    public boolean isExceedsCyclomaticComplexity() { return exceedsCyclomaticComplexity; }
    public void setExceedsCyclomaticComplexity(boolean exceedsCyclomaticComplexity) { this.exceedsCyclomaticComplexity = exceedsCyclomaticComplexity; }

    public int getCognitiveComplexity() { return cognitiveComplexity; }
    public void setCognitiveComplexity(int cognitiveComplexity) { this.cognitiveComplexity = cognitiveComplexity; }

    public int getMaxCognitiveComplexity() { return maxCognitiveComplexity; }
    public void setMaxCognitiveComplexity(int maxCognitiveComplexity) { this.maxCognitiveComplexity = maxCognitiveComplexity; }

    public boolean isExceedsCognitiveComplexity() { return exceedsCognitiveComplexity; }
    public void setExceedsCognitiveComplexity(boolean exceedsCognitiveComplexity) { this.exceedsCognitiveComplexity = exceedsCognitiveComplexity; }

    public int getNestingDepth() { return nestingDepth; }
    public void setNestingDepth(int nestingDepth) { this.nestingDepth = nestingDepth; }

    public int getMaxNestingDepth() { return maxNestingDepth; }
    public void setMaxNestingDepth(int maxNestingDepth) { this.maxNestingDepth = maxNestingDepth; }

    public boolean isExceedsNestingDepth() { return exceedsNestingDepth; }
    public void setExceedsNestingDepth(boolean exceedsNestingDepth) { this.exceedsNestingDepth = exceedsNestingDepth; }

    public int getResponsibilityCount() { return responsibilityCount; }
    public void setResponsibilityCount(int responsibilityCount) { this.responsibilityCount = responsibilityCount; }

    public int getMaxResponsibilityCount() { return maxResponsibilityCount; }
    public void setMaxResponsibilityCount(int maxResponsibilityCount) { this.maxResponsibilityCount = maxResponsibilityCount; }

    public boolean isExceedsResponsibilityCount() { return exceedsResponsibilityCount; }
    public void setExceedsResponsibilityCount(boolean exceedsResponsibilityCount) { this.exceedsResponsibilityCount = exceedsResponsibilityCount; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
}
