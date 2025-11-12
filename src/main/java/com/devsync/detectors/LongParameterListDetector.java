package com.devsync.detectors;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.type.Type;
import java.util.*;
import java.util.stream.Collectors;

public class LongParameterListDetector {
    private static final int PARAM_THRESHOLD = 4;
    private static final int CONSTRUCTOR_THRESHOLD = 6;
    private static final double COHESION_THRESHOLD = 0.3;
    private static final double COMPLEXITY_WEIGHT = 0.4;
    private static final double COHESION_WEIGHT = 0.3;
    private static final double TYPE_DIVERSITY_WEIGHT = 0.3;
    
    private static final Set<String> PRIMITIVE_TYPES = Set.of(
        "int", "long", "double", "float", "boolean", "char", "byte", "short", "String"
    );
    
    private static final Set<String> BUILDER_PATTERNS = Set.of(
        "builder", "create", "newInstance", "factory", "construct"
    );

    public List<String> detect(CompilationUnit cu) {
        List<String> issues = new ArrayList<>();
        
        cu.findAll(MethodDeclaration.class).forEach(method -> {
            ParameterAnalysis analysis = analyzeParameters(method);
            
            if (shouldReport(analysis, false)) {
                String severity = getSeverity(analysis);
                String detailedAnalysis = generateDetailedAnalysis(analysis);
                String suggestions = generateSuggestions(analysis, method);
                
                issues.add(String.format(
                    "%s [LongParameterList] %s:%d - Method '%s' [Score: %.2f] - %s | Suggestions: %s",
                    severity,
                    cu.getStorage().map(s -> s.getFileName()).orElse("UnknownFile"),
                    method.getBegin().map(p -> p.line).orElse(-1),
                    method.getNameAsString(),
                    analysis.complexityScore,
                    detailedAnalysis,
                    suggestions
                ));
            }
        });
        
        cu.findAll(ConstructorDeclaration.class).forEach(constructor -> {
            ParameterAnalysis analysis = analyzeConstructorParameters(constructor);
            
            if (shouldReport(analysis, true)) {
                String severity = getSeverity(analysis);
                String detailedAnalysis = generateDetailedAnalysis(analysis);
                String suggestions = generateConstructorSuggestions(analysis, constructor);
                
                issues.add(String.format(
                    "%s [LongParameterList] %s:%d - Constructor [Score: %.2f] - %s | Suggestions: %s",
                    severity,
                    cu.getStorage().map(s -> s.getFileName()).orElse("UnknownFile"),
                    constructor.getBegin().map(p -> p.line).orElse(-1),
                    analysis.complexityScore,
                    detailedAnalysis,
                    suggestions
                ));
            }
        });
        
        return issues;
    }
    
    private ParameterAnalysis analyzeParameters(MethodDeclaration method) {
        ParameterAnalysis analysis = new ParameterAnalysis();
        analysis.parameterCount = method.getParameters().size();
        analysis.methodName = method.getNameAsString();
        analysis.isConstructor = false;
        
        if (analysis.parameterCount == 0) {
            return analysis;
        }
        
        // Analyze parameter types and relationships
        analyzeParameterTypes(method.getParameters(), analysis);
        
        // Calculate cohesion score
        analysis.cohesionScore = calculateCohesionScore(analysis);
        
        // Calculate overall complexity score
        analysis.complexityScore = calculateComplexityScore(analysis);
        
        return analysis;
    }
    
    private ParameterAnalysis analyzeConstructorParameters(ConstructorDeclaration constructor) {
        ParameterAnalysis analysis = new ParameterAnalysis();
        analysis.parameterCount = constructor.getParameters().size();
        analysis.methodName = "<constructor>";
        analysis.isConstructor = true;
        
        if (analysis.parameterCount == 0) {
            return analysis;
        }
        
        analyzeParameterTypes(constructor.getParameters(), analysis);
        analysis.cohesionScore = calculateCohesionScore(analysis);
        analysis.complexityScore = calculateComplexityScore(analysis);
        
        return analysis;
    }
    
    private void analyzeParameterTypes(List<Parameter> parameters, ParameterAnalysis analysis) {
        Map<String, Integer> typeFrequency = new HashMap<>();
        Set<String> parameterNames = new HashSet<>();
        
        for (Parameter param : parameters) {
            String typeName = param.getType().asString();
            String paramName = param.getNameAsString();
            
            analysis.parameterTypes.add(typeName);
            analysis.parameterNames.add(paramName);
            parameterNames.add(paramName.toLowerCase());
            
            typeFrequency.merge(typeName, 1, Integer::sum);
            
            // Analyze parameter characteristics
            if (PRIMITIVE_TYPES.contains(typeName)) {
                analysis.primitiveCount++;
            } else {
                analysis.objectCount++;
            }
            
            if (typeName.startsWith("List") || typeName.startsWith("Set") || 
                typeName.startsWith("Map") || typeName.endsWith("[]")) {
                analysis.collectionCount++;
            }
        }
        
        analysis.distinctTypes = typeFrequency.size();
        analysis.typeFrequency = typeFrequency;
        
        // Analyze naming patterns for cohesion
        analysis.namingCohesion = calculateNamingCohesion(parameterNames);
    }
    
    private double calculateCohesionScore(ParameterAnalysis analysis) {
        if (analysis.parameterCount <= 2) {
            return 1.0; // High cohesion for small parameter lists
        }
        
        double typeRatio = (double) analysis.distinctTypes / analysis.parameterCount;
        double primitiveRatio = (double) analysis.primitiveCount / analysis.parameterCount;
        
        // Lower cohesion if too many different types or too many primitives
        double cohesion = 1.0 - (typeRatio * 0.5) - (primitiveRatio * 0.3);
        
        // Factor in naming cohesion
        cohesion = (cohesion + analysis.namingCohesion) / 2.0;
        
        return Math.max(0.0, Math.min(1.0, cohesion));
    }
    
    private double calculateNamingCohesion(Set<String> parameterNames) {
        if (parameterNames.size() <= 2) {
            return 1.0;
        }
        
        // Look for common prefixes/suffixes that indicate related parameters
        List<String> names = new ArrayList<>(parameterNames);
        int relatedPairs = 0;
        int totalPairs = 0;
        
        for (int i = 0; i < names.size(); i++) {
            for (int j = i + 1; j < names.size(); j++) {
                totalPairs++;
                if (areNamesRelated(names.get(i), names.get(j))) {
                    relatedPairs++;
                }
            }
        }
        
        return totalPairs > 0 ? (double) relatedPairs / totalPairs : 0.0;
    }
    
    private boolean areNamesRelated(String name1, String name2) {
        // Check for common prefixes (length >= 3)
        int minLength = Math.min(name1.length(), name2.length());
        if (minLength >= 3) {
            String prefix1 = name1.substring(0, Math.min(3, minLength));
            String prefix2 = name2.substring(0, Math.min(3, minLength));
            if (prefix1.equals(prefix2)) {
                return true;
            }
        }
        
        // Check for semantic relationships
        return semanticallyRelated(name1, name2);
    }
    
    private boolean semanticallyRelated(String name1, String name2) {
        Set<Set<String>> relatedGroups = Set.of(
            Set.of("start", "end", "begin", "finish"),
            Set.of("min", "max", "minimum", "maximum"),
            Set.of("width", "height", "length", "size"),
            Set.of("x", "y", "z", "coordinate"),
            Set.of("first", "last", "initial", "final")
        );
        
        return relatedGroups.stream().anyMatch(group -> 
            group.stream().anyMatch(word -> name1.toLowerCase().contains(word)) &&
            group.stream().anyMatch(word -> name2.toLowerCase().contains(word))
        );
    }
    
    private double calculateComplexityScore(ParameterAnalysis analysis) {
        if (analysis.parameterCount == 0) {
            return 0.0;
        }
        
        // Normalize parameter count (0-1 scale)
        double countScore = Math.min(1.0, (double) analysis.parameterCount / 10.0);
        
        // Type diversity score (higher diversity = higher complexity)
        double diversityScore = (double) analysis.distinctTypes / analysis.parameterCount;
        
        // Cohesion score (lower cohesion = higher complexity)
        double cohesionComplexity = 1.0 - analysis.cohesionScore;
        
        return (countScore * COMPLEXITY_WEIGHT) + 
               (diversityScore * TYPE_DIVERSITY_WEIGHT) + 
               (cohesionComplexity * COHESION_WEIGHT);
    }
    
    private boolean shouldReport(ParameterAnalysis analysis, boolean isConstructor) {
        int threshold = isConstructor ? CONSTRUCTOR_THRESHOLD : PARAM_THRESHOLD;
        
        return analysis.parameterCount > threshold || 
               analysis.complexityScore > 0.6 ||
               (analysis.parameterCount > threshold - 1 && analysis.cohesionScore < COHESION_THRESHOLD);
    }
    
    private String getSeverity(ParameterAnalysis analysis) {
        if (analysis.complexityScore > 0.8 || analysis.parameterCount > 8) {
            return "🔴";
        }
        if (analysis.complexityScore > 0.6 || analysis.parameterCount > 6) {
            return "🟡";
        }
        return "🟠";
    }
    
    private String generateDetailedAnalysis(ParameterAnalysis analysis) {
        List<String> details = new ArrayList<>();
        
        details.add(String.format("params=%d", analysis.parameterCount));
        details.add(String.format("types=%d", analysis.distinctTypes));
        details.add(String.format("cohesion=%.2f", analysis.cohesionScore));
        
        if (analysis.primitiveCount > analysis.parameterCount * 0.7) {
            details.add("high-primitive-ratio");
        }
        
        if (analysis.collectionCount > 2) {
            details.add("multiple-collections");
        }
        
        return String.join(", ", details);
    }
    
    private String generateSuggestions(ParameterAnalysis analysis, MethodDeclaration method) {
        List<String> suggestions = new ArrayList<>();
        
        if (analysis.parameterCount > 6) {
            suggestions.add("Extract parameter object");
        }
        
        if (analysis.primitiveCount > 3) {
            suggestions.add("Group primitives into value objects");
        }
        
        if (analysis.cohesionScore < 0.4) {
            suggestions.add("Split method by responsibility");
        }
        
        if (BUILDER_PATTERNS.stream().anyMatch(pattern -> 
            method.getNameAsString().toLowerCase().contains(pattern))) {
            suggestions.add("Use Builder pattern");
        } else {
            suggestions.add("Consider Builder or Factory pattern");
        }
        
        return String.join(", ", suggestions);
    }
    
    private String generateConstructorSuggestions(ParameterAnalysis analysis, ConstructorDeclaration constructor) {
        List<String> suggestions = new ArrayList<>();
        
        suggestions.add("Use Builder pattern");
        
        if (analysis.primitiveCount > 4) {
            suggestions.add("Group related parameters into objects");
        }
        
        if (analysis.parameterCount > 8) {
            suggestions.add("Consider factory methods with different parameter sets");
        }
        
        suggestions.add("Provide multiple constructor overloads");
        
        return String.join(", ", suggestions);
    }
    
    private static class ParameterAnalysis {
        int parameterCount = 0;
        int distinctTypes = 0;
        int primitiveCount = 0;
        int objectCount = 0;
        int collectionCount = 0;
        double cohesionScore = 0.0;
        double complexityScore = 0.0;
        double namingCohesion = 0.0;
        String methodName = "";
        boolean isConstructor = false;
        
        List<String> parameterTypes = new ArrayList<>();
        List<String> parameterNames = new ArrayList<>();
        Map<String, Integer> typeFrequency = new HashMap<>();
    }
}
