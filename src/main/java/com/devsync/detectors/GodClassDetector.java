package com.devsync.detectors;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import java.util.*;
import java.util.stream.Collectors;

public class GodClassDetector {
    private static final int MAX_METHODS = 20;
    private static final int MAX_FIELDS = 15;
    private static final int MAX_LINES = 500;
    private static final double COHESION_THRESHOLD = 0.3;
    private static final double COMPLEXITY_THRESHOLD = 0.7;
    
    public List<String> detect(CompilationUnit cu) {
        List<String> issues = new ArrayList<>();
        
        cu.findAll(ClassOrInterfaceDeclaration.class).forEach(classDecl -> {
            if (!classDecl.isInterface()) {
                ClassMetrics metrics = analyzeClass(classDecl);
                double godClassScore = calculateGodClassScore(metrics);
                
                if (godClassScore > COMPLEXITY_THRESHOLD) {
                    String severity = getSeverity(godClassScore, metrics);
                    String analysis = generateAnalysis(metrics);
                    String suggestions = generateSuggestions(metrics);
                    
                    issues.add(String.format(
                        "%s [GodClass] %s:%d - Class '%s' [Score: %.2f] - %s | Suggestions: %s",
                        severity,
                        cu.getStorage().map(s -> s.getFileName()).orElse("UnknownFile"),
                        classDecl.getBegin().map(p -> p.line).orElse(-1),
                        classDecl.getNameAsString(),
                        godClassScore,
                        analysis,
                        suggestions
                    ));
                }
            }
        });
        
        return issues;
    }
    
    private ClassMetrics analyzeClass(ClassOrInterfaceDeclaration classDecl) {
        ClassMetrics metrics = new ClassMetrics();
        metrics.className = classDecl.getNameAsString();
        
        // Count methods and fields
        metrics.methodCount = (int) classDecl.getMethods().size();
        metrics.fieldCount = (int) classDecl.getFields().size();
        
        // Calculate lines of code
        metrics.linesOfCode = classDecl.getEnd().map(p -> p.line).orElse(0) - 
                             classDecl.getBegin().map(p -> p.line).orElse(0) + 1;
        
        // Analyze method complexity
        classDecl.getMethods().forEach(method -> {
            int methodComplexity = calculateMethodComplexity(method);
            metrics.totalMethodComplexity += methodComplexity;
            metrics.maxMethodComplexity = Math.max(metrics.maxMethodComplexity, methodComplexity);
        });
        
        // Calculate cohesion metrics
        metrics.cohesionScore = calculateClassCohesion(classDecl);
        
        // Analyze responsibilities
        metrics.responsibilities = identifyResponsibilities(classDecl);
        
        // Count dependencies
        metrics.dependencyCount = countDependencies(classDecl);
        
        return metrics;
    }
    
    private double calculateGodClassScore(ClassMetrics metrics) {
        double sizeScore = calculateSizeScore(metrics);
        double complexityScore = calculateComplexityScore(metrics);
        double cohesionScore = 1.0 - metrics.cohesionScore; // Invert cohesion (low cohesion = high god class score)
        double responsibilityScore = calculateResponsibilityScore(metrics);
        
        return (sizeScore * 0.3) + (complexityScore * 0.25) + (cohesionScore * 0.25) + (responsibilityScore * 0.2);
    }
    
    private double calculateSizeScore(ClassMetrics metrics) {
        double methodScore = Math.min(1.0, (double) metrics.methodCount / (MAX_METHODS * 2));
        double fieldScore = Math.min(1.0, (double) metrics.fieldCount / (MAX_FIELDS * 2));
        double lineScore = Math.min(1.0, (double) metrics.linesOfCode / (MAX_LINES * 2));
        
        return (methodScore + fieldScore + lineScore) / 3.0;
    }
    
    private double calculateComplexityScore(ClassMetrics metrics) {
        if (metrics.methodCount == 0) return 0.0;
        
        double avgComplexity = (double) metrics.totalMethodComplexity / metrics.methodCount;
        double maxComplexityScore = Math.min(1.0, metrics.maxMethodComplexity / 20.0);
        double avgComplexityScore = Math.min(1.0, avgComplexity / 10.0);
        
        return (maxComplexityScore + avgComplexityScore) / 2.0;
    }
    
    private double calculateResponsibilityScore(ClassMetrics metrics) {
        // More responsibilities = higher god class score
        return Math.min(1.0, metrics.responsibilities.size() / 5.0);
    }
    
    private int calculateMethodComplexity(MethodDeclaration method) {
        ComplexityCalculator calculator = new ComplexityCalculator();
        method.accept(calculator, null);
        return calculator.getComplexity();
    }
    
    private double calculateClassCohesion(ClassOrInterfaceDeclaration classDecl) {
        List<FieldDeclaration> fields = classDecl.getFields();
        List<MethodDeclaration> methods = classDecl.getMethods();
        
        if (fields.isEmpty() || methods.isEmpty()) {
            return 1.0; // Perfect cohesion for trivial cases
        }
        
        // Calculate LCOM (Lack of Cohesion of Methods) metric
        Set<String> fieldNames = fields.stream()
            .flatMap(field -> field.getVariables().stream())
            .map(VariableDeclarator::getNameAsString)
            .collect(Collectors.toSet());
        
        int methodPairs = 0;
        int cohesivePairs = 0;
        
        for (int i = 0; i < methods.size(); i++) {
            for (int j = i + 1; j < methods.size(); j++) {
                methodPairs++;
                
                Set<String> fieldsUsedByMethod1 = getFieldsUsedByMethod(methods.get(i), fieldNames);
                Set<String> fieldsUsedByMethod2 = getFieldsUsedByMethod(methods.get(j), fieldNames);
                
                // Methods are cohesive if they share at least one field
                if (!Collections.disjoint(fieldsUsedByMethod1, fieldsUsedByMethod2)) {
                    cohesivePairs++;
                }
            }
        }
        
        return methodPairs > 0 ? (double) cohesivePairs / methodPairs : 1.0;
    }
    
    private Set<String> getFieldsUsedByMethod(MethodDeclaration method, Set<String> fieldNames) {
        Set<String> usedFields = new HashSet<>();
        String methodBody = method.toString().toLowerCase();
        
        for (String fieldName : fieldNames) {
            if (methodBody.contains(fieldName.toLowerCase())) {
                usedFields.add(fieldName);
            }
        }
        
        return usedFields;
    }
    
    private Set<String> identifyResponsibilities(ClassOrInterfaceDeclaration classDecl) {
        Set<String> responsibilities = new HashSet<>();
        
        // Analyze method names for different responsibilities
        Map<String, Set<String>> responsibilityPatterns = Map.of(
            "Data Management", Set.of("get", "set", "load", "save", "store", "retrieve", "fetch"),
            "Business Logic", Set.of("calculate", "compute", "process", "validate", "verify", "check"),
            "UI/Presentation", Set.of("display", "show", "render", "draw", "paint", "update"),
            "Communication", Set.of("send", "receive", "connect", "disconnect", "notify", "broadcast"),
            "Persistence", Set.of("persist", "delete", "insert", "update", "query", "find"),
            "Security", Set.of("authenticate", "authorize", "encrypt", "decrypt", "secure", "protect"),
            "Logging", Set.of("log", "trace", "debug", "info", "warn", "error"),
            "Configuration", Set.of("configure", "setup", "initialize", "config", "init")
        );
        
        classDecl.getMethods().forEach(method -> {
            String methodName = method.getNameAsString().toLowerCase();
            
            responsibilityPatterns.forEach((responsibility, patterns) -> {
                if (patterns.stream().anyMatch(methodName::contains)) {
                    responsibilities.add(responsibility);
                }
            });
        });
        
        // Analyze field types for additional responsibilities
        classDecl.getFields().forEach(field -> {
            String fieldType = field.getElementType().asString().toLowerCase();
            
            if (fieldType.contains("connection") || fieldType.contains("socket")) {
                responsibilities.add("Communication");
            }
            if (fieldType.contains("logger") || fieldType.contains("log")) {
                responsibilities.add("Logging");
            }
            if (fieldType.contains("config") || fieldType.contains("properties")) {
                responsibilities.add("Configuration");
            }
        });
        
        return responsibilities;
    }
    
    private int countDependencies(ClassOrInterfaceDeclaration classDecl) {
        Set<String> dependencies = new HashSet<>();
        
        // Count field dependencies
        classDecl.getFields().forEach(field -> {
            String fieldType = field.getElementType().asString();
            if (!isPrimitiveOrCommon(fieldType)) {
                dependencies.add(fieldType);
            }
        });
        
        // Count method parameter dependencies
        classDecl.getMethods().forEach(method -> {
            method.getParameters().forEach(param -> {
                String paramType = param.getType().asString();
                if (!isPrimitiveOrCommon(paramType)) {
                    dependencies.add(paramType);
                }
            });
        });
        
        return dependencies.size();
    }
    
    private boolean isPrimitiveOrCommon(String type) {
        Set<String> commonTypes = Set.of(
            "String", "Integer", "Long", "Double", "Float", "Boolean", "Character",
            "int", "long", "double", "float", "boolean", "char", "byte", "short",
            "List", "Set", "Map", "Collection", "ArrayList", "HashMap", "HashSet"
        );
        
        return commonTypes.contains(type) || type.startsWith("java.lang") || type.startsWith("java.util");
    }
    
    private String getSeverity(double godClassScore, ClassMetrics metrics) {
        if (godClassScore > 0.9 || metrics.linesOfCode > 1000 || metrics.methodCount > 30) {
            return "🔴";
        }
        if (godClassScore > 0.7 || metrics.linesOfCode > 500 || metrics.methodCount > 20) {
            return "🟡";
        }
        return "🟠";
    }
    
    private String generateAnalysis(ClassMetrics metrics) {
        List<String> analysis = new ArrayList<>();
        
        analysis.add(String.format("methods=%d", metrics.methodCount));
        analysis.add(String.format("fields=%d", metrics.fieldCount));
        analysis.add(String.format("lines=%d", metrics.linesOfCode));
        analysis.add(String.format("cohesion=%.2f", metrics.cohesionScore));
        analysis.add(String.format("responsibilities=%d", metrics.responsibilities.size()));
        
        if (metrics.maxMethodComplexity > 15) {
            analysis.add("high-method-complexity");
        }
        
        if (metrics.dependencyCount > 10) {
            analysis.add("high-coupling");
        }
        
        return String.join(", ", analysis);
    }
    
    private String generateSuggestions(ClassMetrics metrics) {
        List<String> suggestions = new ArrayList<>();
        
        if (metrics.responsibilities.size() > 3) {
            suggestions.add("Split by responsibilities: " + 
                String.join(", ", metrics.responsibilities.stream().limit(3).collect(Collectors.toList())));
        }
        
        if (metrics.methodCount > MAX_METHODS) {
            suggestions.add("Extract utility classes");
        }
        
        if (metrics.cohesionScore < COHESION_THRESHOLD) {
            suggestions.add("Improve cohesion by grouping related methods");
        }
        
        if (metrics.dependencyCount > 10) {
            suggestions.add("Reduce coupling through dependency injection");
        }
        
        suggestions.add("Apply Single Responsibility Principle");
        suggestions.add("Consider facade or adapter patterns");
        
        return String.join(", ", suggestions);
    }
    
    private static class ClassMetrics {
        String className;
        int methodCount = 0;
        int fieldCount = 0;
        int linesOfCode = 0;
        int totalMethodComplexity = 0;
        int maxMethodComplexity = 0;
        double cohesionScore = 0.0;
        Set<String> responsibilities = new HashSet<>();
        int dependencyCount = 0;
    }
    
    private static class ComplexityCalculator extends VoidVisitorAdapter<Void> {
        private int complexity = 1; // Base complexity
        
        public int getComplexity() {
            return complexity;
        }
        
        @Override
        public void visit(com.github.javaparser.ast.stmt.IfStmt n, Void arg) {
            complexity++;
            super.visit(n, arg);
        }
        
        @Override
        public void visit(com.github.javaparser.ast.stmt.WhileStmt n, Void arg) {
            complexity++;
            super.visit(n, arg);
        }
        
        @Override
        public void visit(com.github.javaparser.ast.stmt.ForStmt n, Void arg) {
            complexity++;
            super.visit(n, arg);
        }
        
        @Override
        public void visit(com.github.javaparser.ast.stmt.ForEachStmt n, Void arg) {
            complexity++;
            super.visit(n, arg);
        }
        
        @Override
        public void visit(com.github.javaparser.ast.stmt.SwitchStmt n, Void arg) {
            complexity += n.getEntries().size();
            super.visit(n, arg);
        }
        
        @Override
        public void visit(com.github.javaparser.ast.stmt.TryStmt n, Void arg) {
            complexity += n.getCatchClauses().size();
            super.visit(n, arg);
        }
    }
}