package com.devsync.detectors;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import java.util.*;

public class BrokenModularizationDetector {

    public List<String> detect(CompilationUnit cu) {
        List<String> issues = new ArrayList<>();
        Set<String> processedEntities = new HashSet<>();
        
        ModularizationAnalyzer analyzer = new ModularizationAnalyzer();
        cu.accept(analyzer, null);
        
        analyzer.getModularizationIssues().forEach(modInfo -> {
            String entityKey = modInfo.fileName + ":" + modInfo.name;
            if (!processedEntities.contains(entityKey) && shouldReport(modInfo)) {
                processedEntities.add(entityKey);
                
                double score = calculateScore(modInfo);
                String severity = getSeverity(score);
                
                issues.add(String.format(
                    "%s [BrokenModularization] %s:%d - %s '%s' - %s | Suggestions: %s",
                    severity,
                    modInfo.fileName,
                    modInfo.lineNumber,
                    modInfo.type,
                    modInfo.name,
                    generateAnalysis(modInfo),
                    generateSuggestions(modInfo)
                ));
            }
        });
        
        return issues;
    }
    
    private double calculateScore(ModularizationInfo modInfo) {
        double cohesionScore = 1.0 - modInfo.cohesionIndex;
        double couplingScore = Math.min(1.0, (double) modInfo.couplingCount / 8);
        double responsibilityScore = Math.min(1.0, (double) modInfo.responsibilityCount / 4);
        double mixedConcernScore = modInfo.hasMixedConcerns ? 0.4 : 0.0;
        
        return cohesionScore * 0.3 + couplingScore * 0.3 + responsibilityScore * 0.2 + mixedConcernScore;
    }
    
    private boolean shouldReport(ModularizationInfo modInfo) {
        // Only report significant modularization issues
        return (modInfo.responsibilityCount > 4) || 
               (modInfo.cohesionIndex < 0.3) || 
               (modInfo.couplingCount > 8) ||
               (modInfo.hasMixedConcerns && modInfo.responsibilityCount > 3);
    }
    
    private String getSeverity(double score) {
        if (score >= 0.8) return "🔴";
        if (score >= 0.5) return "🟡";
        return "🟠";
    }
    
    private String generateAnalysis(ModularizationInfo modInfo) {
        List<String> issues = new ArrayList<>();
        
        if (modInfo.hasMixedConcerns) {
            issues.add("Mixed unrelated concerns");
        }
        if (modInfo.cohesionIndex < 0.4) {
            issues.add("Low cohesion");
        }
        if (modInfo.couplingCount > 6) {
            issues.add("High coupling");
        }
        if (modInfo.responsibilityCount > 3) {
            issues.add("Multiple responsibilities");
        }
        
        return issues.isEmpty() ? "Modularization concern" : String.join(", ", issues);
    }
    
    private String generateSuggestions(ModularizationInfo modInfo) {
        if (modInfo.hasMixedConcerns) {
            return "Separate unrelated operations into different methods";
        }
        if (modInfo.responsibilityCount > 3) {
            return "Apply Single Responsibility Principle";
        }
        return "Improve cohesion and reduce coupling";
    }
    
    private static class ModularizationInfo {
        String fileName;
        String name;
        String type;
        int lineNumber;
        double cohesionIndex;
        int couplingCount;
        int responsibilityCount;
        boolean hasMixedConcerns;
        Set<String> responsibilities;
        
        ModularizationInfo(String name, String type, int lineNumber) {
            this.name = name;
            this.type = type;
            this.lineNumber = lineNumber;
            this.responsibilities = new HashSet<>();
        }
    }
    
    private static class ModularizationAnalyzer extends VoidVisitorAdapter<Void> {
        private final List<ModularizationInfo> modularizationIssues = new ArrayList<>();
        private String fileName = "";
        
        public List<ModularizationInfo> getModularizationIssues() {
            return modularizationIssues;
        }
        
        @Override
        public void visit(CompilationUnit n, Void arg) {
            fileName = n.getStorage().map(s -> s.getFileName()).orElse("UnknownFile");
            super.visit(n, arg);
        }
        
        @Override
        public void visit(ClassOrInterfaceDeclaration n, Void arg) {
            ModularizationInfo info = new ModularizationInfo(n.getNameAsString(), "Class", 
                n.getBegin().map(pos -> pos.line).orElse(0));
            
            info.fileName = fileName;
            info.cohesionIndex = calculateCohesion(n);
            info.couplingCount = calculateCoupling(n);
            info.responsibilityCount = analyzeResponsibilities(n, info);
            info.hasMixedConcerns = detectMixedConcerns(n);
            
            modularizationIssues.add(info);
            super.visit(n, arg);
        }
        
        @Override
        public void visit(MethodDeclaration n, Void arg) {
            if (detectMixedConcernsInMethod(n)) {
                ModularizationInfo info = new ModularizationInfo(n.getNameAsString(), "Method", 
                    n.getBegin().map(pos -> pos.line).orElse(0));
                
                info.fileName = fileName;
                info.hasMixedConcerns = true;
                info.responsibilityCount = analyzeMethodResponsibilities(n, info);
                info.cohesionIndex = 0.3; // Low cohesion for mixed concerns
                info.couplingCount = 0;
                
                modularizationIssues.add(info);
            }
            super.visit(n, arg);
        }
        
        private double calculateCohesion(ClassOrInterfaceDeclaration classDecl) {
            Set<String> fields = new HashSet<>();
            Set<String> usedFields = new HashSet<>();
            
            // Collect all fields
            classDecl.getFields().forEach(field -> 
                field.getVariables().forEach(var -> fields.add(var.getNameAsString())));
            
            // Count field usage in methods
            classDecl.getMethods().forEach(method -> 
                method.findAll(NameExpr.class).forEach(name -> {
                    if (fields.contains(name.getNameAsString())) {
                        usedFields.add(name.getNameAsString());
                    }
                }));
            
            return fields.isEmpty() ? 1.0 : (double) usedFields.size() / fields.size();
        }
        
        private int calculateCoupling(ClassOrInterfaceDeclaration classDecl) {
            Set<String> externalTypes = new HashSet<>();
            
            // Count external method calls
            classDecl.findAll(MethodCallExpr.class).forEach(call -> {
                if (call.getScope().isPresent()) {
                    String scope = call.getScope().get().toString();
                    if (!scope.equals("this") && !scope.equals("super")) {
                        externalTypes.add(scope);
                    }
                }
            });
            
            // Count field types
            classDecl.getFields().forEach(field -> {
                String type = field.getElementType().asString();
                if (!isPrimitiveType(type)) {
                    externalTypes.add(type);
                }
            });
            
            return externalTypes.size();
        }
        
        private int analyzeResponsibilities(ClassOrInterfaceDeclaration classDecl, ModularizationInfo info) {
            classDecl.getMethods().forEach(method -> {
                analyzeMethodResponsibilities(method, info);
            });
            
            return info.responsibilities.size();
        }
        
        private int analyzeMethodResponsibilities(MethodDeclaration method, ModularizationInfo info) {
            String methodName = method.getNameAsString().toLowerCase();
            
            // Data operations
            if (methodName.contains("save") || methodName.contains("persist") || methodName.contains("store")) {
                info.responsibilities.add("data_persistence");
            }
            if (methodName.contains("load") || methodName.contains("fetch") || methodName.contains("retrieve")) {
                info.responsibilities.add("data_retrieval");
            }
            
            // Business logic
            if (methodName.contains("calculate") || methodName.contains("compute") || methodName.contains("process")) {
                info.responsibilities.add("computation");
            }
            if (methodName.contains("validate") || methodName.contains("check") || methodName.contains("verify")) {
                info.responsibilities.add("validation");
            }
            
            // UI/Presentation
            if (methodName.contains("format") || methodName.contains("display") || methodName.contains("render")) {
                info.responsibilities.add("presentation");
            }
            if (methodName.contains("print") || methodName.contains("log") || methodName.contains("output")) {
                info.responsibilities.add("output");
            }
            
            // Communication
            if (methodName.contains("send") || methodName.contains("receive") || methodName.contains("connect")) {
                info.responsibilities.add("communication");
            }
            
            // Configuration
            if (methodName.contains("config") || methodName.contains("setup") || methodName.contains("init")) {
                info.responsibilities.add("configuration");
            }
            
            return info.responsibilities.size();
        }
        
        private boolean detectMixedConcerns(ClassOrInterfaceDeclaration classDecl) {
            Set<String> concerns = new HashSet<>();
            
            classDecl.getMethods().forEach(method -> {
                ModularizationInfo tempInfo = new ModularizationInfo("", "", 0);
                analyzeMethodResponsibilities(method, tempInfo);
                concerns.addAll(tempInfo.responsibilities);
            });
            
            return concerns.size() > 3;
        }
        
        private boolean detectMixedConcernsInMethod(MethodDeclaration method) {
            Set<String> operations = new HashSet<>();
            
            // Check for mixed operations in method calls
            method.findAll(MethodCallExpr.class).forEach(call -> {
                String methodName = call.getNameAsString().toLowerCase();
                
                if (methodName.contains("print") || methodName.contains("system.out")) {
                    operations.add("output");
                }
                if (methodName.contains("calculate") || methodName.contains("+") || methodName.contains("*")) {
                    operations.add("arithmetic");
                }
                if (methodName.contains("string") || methodName.contains("concat") || methodName.contains("format")) {
                    operations.add("string_manipulation");
                }
                if (methodName.contains("save") || methodName.contains("persist") || methodName.contains("db")) {
                    operations.add("persistence");
                }
                if (methodName.contains("http") || methodName.contains("api") || methodName.contains("request")) {
                    operations.add("api_call");
                }
            });
            
            // Check for mixed variable types
            Set<String> variableTypes = new HashSet<>();
            method.findAll(VariableDeclarationExpr.class).forEach(var -> {
                String type = var.getElementType().asString().toLowerCase();
                if (type.contains("string")) variableTypes.add("string");
                if (type.contains("int") || type.contains("double") || type.contains("float")) variableTypes.add("numeric");
                if (type.contains("list") || type.contains("array")) variableTypes.add("collection");
            });
            
            return operations.size() > 2 || (operations.size() > 1 && variableTypes.size() > 2);
        }
        
        private boolean isPrimitiveType(String type) {
            return Set.of("int", "long", "double", "float", "boolean", "char", "byte", "short", "String").contains(type);
        }
    }
}