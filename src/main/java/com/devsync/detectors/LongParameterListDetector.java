package com.devsync.detectors;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import java.util.*;
import java.util.stream.Collectors;

public class LongParameterListDetector {
    
    private int baseParameterThreshold = 4;
    private int criticalParameterThreshold = 7;
    private int constructorThreshold = 5;
    
    public void setMaxParameters(int maxParameters) {
        this.baseParameterThreshold = Math.max(2, maxParameters - 1);
        this.criticalParameterThreshold = maxParameters + 2;
        this.constructorThreshold = maxParameters;
    }
    
    private static final Set<String> PRIMITIVE_TYPES = Set.of(
        "int", "long", "double", "float", "boolean", "char", "byte", "short"
    );
    
    private static final Set<String> COMMON_TYPES = Set.of(
        "String", "Integer", "Long", "Double", "Float", "Boolean", "Date", "List", "Map", "Set"
    );
    
    private static final Map<String, Double> METHOD_TYPE_WEIGHTS = Map.of(
        "constructor", 1.2,
        "builder", 0.8,
        "factory", 0.9,
        "utility", 1.0,
        "business", 1.1,
        "test", 0.7
    );

    public List<String> detect(CompilationUnit cu) {
        List<String> issues = new ArrayList<>();
        
        ParameterAnalyzer analyzer = new ParameterAnalyzer();
        cu.accept(analyzer, null);
        
        analyzer.getProblematicMethods().forEach(paramInfo -> {
            double complexityScore = calculateComplexityScore(paramInfo);
            
            if (shouldReport(paramInfo, complexityScore)) {
                String severity = getSeverity(paramInfo, complexityScore);
                String analysis = generateAnalysis(paramInfo);
                String suggestions = generateSuggestions(paramInfo);
                
                issues.add(String.format(
                    "%s [LongParameterList] %s:%d - %s '%s' (%d params, Complexity: %.2f) - %s | Suggestions: %s | DetailedReason: %s",
                    severity,
                    cu.getStorage().map(s -> s.getFileName()).orElse("UnknownFile"),
                    paramInfo.lineNumber,
                    paramInfo.isConstructor ? "Constructor" : "Method",
                    paramInfo.methodName,
                    paramInfo.parameterCount,
                    complexityScore,
                    analysis,
                    suggestions,
                    generateDetailedReason(paramInfo, complexityScore)
                ));
            }
        });
        
        return issues;
    }
    
    private double calculateComplexityScore(ParameterInfo paramInfo) {
        double baseScore = (double) paramInfo.parameterCount / criticalParameterThreshold;
        
        double typeComplexityScore = calculateTypeComplexity(paramInfo);
        double cohesionScore = calculateParameterCohesion(paramInfo);
        double semanticScore = calculateSemanticComplexity(paramInfo);
        
        String methodType = determineMethodType(paramInfo);
        double typeWeight = METHOD_TYPE_WEIGHTS.getOrDefault(methodType, 1.0);
        
        return Math.min(1.5, (baseScore * 0.4 + typeComplexityScore * 0.3 + 
                             (1.0 - cohesionScore) * 0.2 + semanticScore * 0.1) * typeWeight);
    }
    
    private double calculateTypeComplexity(ParameterInfo paramInfo) {
        double complexity = 0.0;
        
        for (String type : paramInfo.parameterTypes) {
            if (PRIMITIVE_TYPES.contains(type)) {
                complexity += 0.1;
            } else if (COMMON_TYPES.contains(type)) {
                complexity += 0.2;
            } else if (type.contains("[]") || type.contains("List") || type.contains("Map")) {
                complexity += 0.4;
            } else {
                complexity += 0.3;
            }
        }
        
        return Math.min(1.0, complexity / paramInfo.parameterCount);
    }
    
    private static double calculateParameterCohesion(ParameterInfo paramInfo) {
        if (paramInfo.parameterCount <= 2) return 1.0;
        
        Map<String, Integer> typeGroups = paramInfo.parameterTypes.stream()
            .collect(Collectors.groupingBy(
                type -> getTypeCategory(type),
                Collectors.summingInt(e -> 1)
            ));
        
        int maxGroupSize = typeGroups.values().stream().mapToInt(Integer::intValue).max().orElse(1);
        return (double) maxGroupSize / paramInfo.parameterCount;
    }
    
    private static String getTypeCategory(String type) {
        if (PRIMITIVE_TYPES.contains(type)) return "primitive";
        if (type.equals("String")) return "string";
        if (type.contains("List") || type.contains("Set") || type.contains("[]")) return "collection";
        if (type.contains("Map")) return "map";
        if (type.endsWith("DTO") || type.endsWith("Entity") || type.endsWith("Model")) return "data";
        return "object";
    }
    
    private double calculateSemanticComplexity(ParameterInfo paramInfo) {
        Set<String> semanticGroups = new HashSet<>();
        
        for (String paramName : paramInfo.parameterNames) {
            String lower = paramName.toLowerCase();
            if (lower.contains("id") || lower.contains("key")) {
                semanticGroups.add("identifier");
            } else if (lower.contains("name") || lower.contains("title") || lower.contains("label")) {
                semanticGroups.add("naming");
            } else if (lower.contains("date") || lower.contains("time") || lower.contains("timestamp")) {
                semanticGroups.add("temporal");
            } else if (lower.contains("config") || lower.contains("setting") || lower.contains("option")) {
                semanticGroups.add("configuration");
            } else {
                semanticGroups.add("other");
            }
        }
        
        return 1.0 - ((double) semanticGroups.size() / Math.max(1, paramInfo.parameterCount));
    }
    
    private boolean shouldReport(ParameterInfo paramInfo, double complexityScore) {
        int threshold = paramInfo.isConstructor ? constructorThreshold : baseParameterThreshold;
        
        if (paramInfo.parameterCount < threshold && complexityScore < 0.7) {
            return false;
        }
        
        if (isExcludedMethod(paramInfo)) {
            return false;
        }
        
        return paramInfo.parameterCount >= threshold || complexityScore > 0.8;
    }
    
    private boolean isExcludedMethod(ParameterInfo paramInfo) {
        String name = paramInfo.methodName.toLowerCase();
        
        if (name.startsWith("test") && paramInfo.parameterCount <= 6) return true;
        if (name.equals("main") && paramInfo.parameterCount == 1) return true;
        if (name.startsWith("builder") && paramInfo.parameterCount <= 8) return true;
        
        return false;
    }
    
    private String determineMethodType(ParameterInfo paramInfo) {
        if (paramInfo.isConstructor) return "constructor";
        
        String name = paramInfo.methodName.toLowerCase();
        if (name.contains("builder") || name.contains("build")) return "builder";
        if (name.contains("create") || name.contains("factory") || name.contains("getInstance")) return "factory";
        if (paramInfo.isStatic && paramInfo.isPublic) return "utility";
        if (name.startsWith("test")) return "test";
        
        return "business";
    }
    
    private String getSeverity(ParameterInfo paramInfo, double complexityScore) {
        if (paramInfo.parameterCount > criticalParameterThreshold || complexityScore > 1.2) {
            return "🔴";
        }
        if (paramInfo.parameterCount > 5 || complexityScore > 0.9) {
            return "🟡";
        }
        return "🟠";
    }
    
    private String generateAnalysis(ParameterInfo paramInfo) {
        List<String> issues = new ArrayList<>();
        
        if (paramInfo.parameterCount > criticalParameterThreshold) {
            issues.add("Excessive parameters");
        } else if (paramInfo.parameterCount > 5) {
            issues.add("Too many parameters");
        }
        
        if (paramInfo.primitiveCount > paramInfo.parameterCount * 0.7) {
            issues.add("Primitive obsession");
        }
        
        if (paramInfo.hasConsecutiveSameTypes) {
            issues.add("Parameter confusion risk");
        }
        
        if (paramInfo.lacksCohesion) {
            issues.add("Unrelated parameters");
        }
        
        if (paramInfo.hasComplexTypes) {
            issues.add("Complex parameter types");
        }
        
        return issues.isEmpty() ? "Long parameter list" : String.join(", ", issues);
    }
    
    private String generateSuggestions(ParameterInfo paramInfo) {
        List<String> suggestions = new ArrayList<>();
        
        if (paramInfo.parameterCount > 5) {
            suggestions.add("Create parameter object");
        }
        
        if (paramInfo.primitiveCount > 3) {
            suggestions.add("Use value objects for primitives");
        }
        
        if (paramInfo.hasConsecutiveSameTypes) {
            suggestions.add("Use builder pattern or method overloading");
        }
        
        if (paramInfo.lacksCohesion) {
            suggestions.add("Split into multiple methods");
        }
        
        if (paramInfo.isConstructor && paramInfo.parameterCount > constructorThreshold) {
            suggestions.add("Use builder pattern for construction");
        }
        
        suggestions.add("Consider dependency injection");
        
        return String.join(", ", suggestions);
    }
    
    private String generateDetailedReason(ParameterInfo paramInfo, double complexityScore) {
        StringBuilder reason = new StringBuilder();
        reason.append(String.format("This %s is flagged as a code smell because: ", 
            paramInfo.isConstructor ? "constructor" : "method"));
        
        List<String> issues = new ArrayList<>();
        
        int threshold = paramInfo.isConstructor ? constructorThreshold : baseParameterThreshold;
        issues.add(String.format("it has %d parameters (threshold: %d)", 
            paramInfo.parameterCount, threshold));
        
        if (paramInfo.primitiveCount > paramInfo.parameterCount * 0.7) {
            issues.add(String.format("%d out of %d parameters are primitives, indicating primitive obsession", 
                paramInfo.primitiveCount, paramInfo.parameterCount));
        }
        
        if (paramInfo.hasConsecutiveSameTypes) {
            issues.add("consecutive parameters have the same type, increasing the risk of passing arguments in wrong order");
        }
        
        if (paramInfo.lacksCohesion) {
            issues.add("parameters appear unrelated, suggesting the method may have multiple responsibilities");
        }
        
        if (paramInfo.hasComplexTypes) {
            issues.add("some parameters have complex generic types, adding to cognitive load");
        }
        
        reason.append(String.join(", ", issues));
        reason.append(String.format(". Complexity score: %.2f. Long parameter lists make methods harder to understand, test, and maintain.", complexityScore));
        
        return reason.toString();
    }
    
    private static class ParameterInfo {
        String methodName;
        int lineNumber;
        int parameterCount;
        List<String> parameterTypes;
        List<String> parameterNames;
        boolean isConstructor;
        boolean isStatic;
        boolean isPublic;
        int primitiveCount;
        boolean hasConsecutiveSameTypes;
        boolean lacksCohesion;
        boolean hasComplexTypes;
        
        ParameterInfo() {
            this.parameterTypes = new ArrayList<>();
            this.parameterNames = new ArrayList<>();
        }
    }
    
    private static class ParameterAnalyzer extends VoidVisitorAdapter<Void> {
        private final List<ParameterInfo> problematicMethods = new ArrayList<>();
        
        public List<ParameterInfo> getProblematicMethods() {
            return problematicMethods;
        }
        
        @Override
        public void visit(MethodDeclaration n, Void arg) {
            ParameterInfo info = analyzeParameters(n.getParameters(), n.getNameAsString(), 
                n.getBegin().map(pos -> pos.line).orElse(0), false, n.isStatic(), n.isPublic());
            problematicMethods.add(info);
            super.visit(n, arg);
        }
        
        @Override
        public void visit(ConstructorDeclaration n, Void arg) {
            ParameterInfo info = analyzeParameters(n.getParameters(), n.getNameAsString(), 
                n.getBegin().map(pos -> pos.line).orElse(0), true, false, n.isPublic());
            problematicMethods.add(info);
            super.visit(n, arg);
        }
        
        private ParameterInfo analyzeParameters(List<Parameter> parameters, String methodName, 
                                              int lineNumber, boolean isConstructor, boolean isStatic, boolean isPublic) {
            ParameterInfo info = new ParameterInfo();
            info.methodName = methodName;
            info.lineNumber = lineNumber;
            info.parameterCount = parameters.size();
            info.isConstructor = isConstructor;
            info.isStatic = isStatic;
            info.isPublic = isPublic;
            
            for (Parameter param : parameters) {
                String typeName = param.getType().asString();
                String paramName = param.getNameAsString();
                
                info.parameterTypes.add(typeName);
                info.parameterNames.add(paramName);
                
                if (PRIMITIVE_TYPES.contains(typeName)) {
                    info.primitiveCount++;
                }
            }
            
            info.hasConsecutiveSameTypes = hasConsecutiveSameTypes(info.parameterTypes);
            info.lacksCohesion = calculateParameterCohesion(info) < 0.4;
            info.hasComplexTypes = hasComplexTypes(info.parameterTypes);
            
            return info;
        }
        
        private boolean hasConsecutiveSameTypes(List<String> types) {
            for (int i = 0; i < types.size() - 1; i++) {
                if (types.get(i).equals(types.get(i + 1))) {
                    return true;
                }
            }
            return false;
        }
        
        private boolean hasComplexTypes(List<String> types) {
            return types.stream().anyMatch(type -> 
                type.contains("Map<") || type.contains("List<") || 
                type.contains("Set<") || type.contains("Function<") ||
                type.split("<").length > 2
            );
        }
    }
}