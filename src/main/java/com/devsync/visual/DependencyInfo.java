package com.devsync.visual;

public class DependencyInfo {
    private String fromClass;
    private String toClass;
    private DependencyType type;
    private String description;
    
    public enum DependencyType {
        EXTENDS,
        IMPLEMENTS,
        USES,
        IMPORTS,
        COMPOSITION,
        AGGREGATION
    }
    
    public DependencyInfo(String fromClass, String toClass, DependencyType type) {
        this.fromClass = fromClass;
        this.toClass = toClass;
        this.type = type;
    }
    
    public DependencyInfo(String fromClass, String toClass, DependencyType type, String description) {
        this(fromClass, toClass, type);
        this.description = description;
    }
    
    // Getters and Setters
    public String getFromClass() { return fromClass; }
    public void setFromClass(String fromClass) { this.fromClass = fromClass; }
    
    public String getToClass() { return toClass; }
    public void setToClass(String toClass) { this.toClass = toClass; }
    
    public DependencyType getType() { return type; }
    public void setType(DependencyType type) { this.type = type; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    @Override
    public String toString() {
        return fromClass + " " + getArrow() + " " + toClass;
    }
    
    private String getArrow() {
        switch (type) {
            case EXTENDS: return "--|>";
            case IMPLEMENTS: return "..|>";
            case USES: return "-->";
            case COMPOSITION: return "*--";
            case AGGREGATION: return "o--";
            default: return "-->";
        }
    }
}