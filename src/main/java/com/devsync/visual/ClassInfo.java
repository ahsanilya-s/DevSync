package com.devsync.visual;

import java.util.*;

public class ClassInfo {
    private String className;
    private String packageName;
    private String filePath;
    private int linesOfCode;
    private int complexity;
    private Set<String> imports;
    private Set<String> internalDependencies;
    private Set<String> externalDependencies;
    private String extendsClass;
    private Set<String> implementsInterfaces;
    private boolean isInterface;
    private boolean isAbstract;
    
    public ClassInfo(String className, String packageName, String filePath) {
        this.className = className;
        this.packageName = packageName;
        this.filePath = filePath;
        this.imports = new HashSet<>();
        this.internalDependencies = new HashSet<>();
        this.externalDependencies = new HashSet<>();
        this.implementsInterfaces = new HashSet<>();
        this.linesOfCode = 0;
        this.complexity = 0;
    }
    
    // Getters and Setters
    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }
    
    public String getPackageName() { return packageName; }
    public void setPackageName(String packageName) { this.packageName = packageName; }
    
    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
    
    public int getLinesOfCode() { return linesOfCode; }
    public void setLinesOfCode(int linesOfCode) { this.linesOfCode = linesOfCode; }
    
    public int getComplexity() { return complexity; }
    public void setComplexity(int complexity) { this.complexity = complexity; }
    
    public Set<String> getImports() { return imports; }
    public void setImports(Set<String> imports) { this.imports = imports; }
    
    public Set<String> getInternalDependencies() { return internalDependencies; }
    public void setInternalDependencies(Set<String> internalDependencies) { this.internalDependencies = internalDependencies; }
    
    public Set<String> getExternalDependencies() { return externalDependencies; }
    public void setExternalDependencies(Set<String> externalDependencies) { this.externalDependencies = externalDependencies; }
    
    public String getExtendsClass() { return extendsClass; }
    public void setExtendsClass(String extendsClass) { this.extendsClass = extendsClass; }
    
    public Set<String> getImplementsInterfaces() { return implementsInterfaces; }
    public void setImplementsInterfaces(Set<String> implementsInterfaces) { this.implementsInterfaces = implementsInterfaces; }
    
    public boolean isInterface() { return isInterface; }
    public void setInterface(boolean isInterface) { this.isInterface = isInterface; }
    
    public boolean isAbstract() { return isAbstract; }
    public void setAbstract(boolean isAbstract) { this.isAbstract = isAbstract; }
    
    public String getFullName() {
        return packageName != null && !packageName.isEmpty() ? packageName + "." + className : className;
    }
}