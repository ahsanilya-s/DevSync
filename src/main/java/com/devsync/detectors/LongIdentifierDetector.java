package com.devsync.detectors;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import java.util.*;
import java.util.regex.Pattern;

public class LongIdentifierDetector {
    
    private int baseLengthThreshold = 25;
    private int criticalLengthThreshold = 40;
    private int variableThreshold = 20;
    private int methodThreshold = 30;
    private int classThreshold = 35;
    
    public void setMaxLength(int maxLength) {
        this.baseLengthThreshold = maxLength;
        this.criticalLengthThreshold = maxLength + 15;
        this.variableThreshold = Math.max(10, maxLength - 10);
        this.methodThreshold = maxLength;
        this.classThreshold = maxLength + 5;
    }
    
    private static final Pattern CAMEL_CASE_PATTERN = Pattern.compile("([a-z]+[A-Z]+\\w*)+");
    private static final Pattern SNAKE_CASE_PATTERN = Pattern.compile("([a-z]+_[a-z_]*)+");
    private static final Pattern ABBREVIATION_PATTERN = Pattern.compile(".*[A-Z]{2,}.*");
    
    private static final Set<String> COMMON_PREFIXES = Set.of(
        "get", "set", "is", "has", "can", "should", "will", "create", "build", "make", "find", "search"
    );
    
    private static final Set<String> COMMON_SUFFIXES = Set.of(
        "Manager", "Handler", "Service", "Controller", "Repository", "Factory", "Builder", "Validator", "Processor"
    );
    
    private static final Map<String, Double> IDENTIFIER_TYPE_WEIGHTS = Map.of(
        "class", 1.0,
        "interface", 1.0,
        "method", 0.9,
        "field", 0.8,
        "variable", 0.7,
        "parameter", 0.6,
        "constant", 0.5
    );

    public List<String> detect(CompilationUnit cu) {
        List<String> issues = new ArrayList<>();
        
        IdentifierAnalyzer analyzer = new IdentifierAnalyzer();
        cu.accept(analyzer, null);
        
        analyzer.getLongIdentifiers().forEach(identifierInfo -> {
            double complexityScore = calculateComplexityScore(identifierInfo);
            
            if (shouldReport(identifierInfo, complexityScore)) {
                String severity = getSeverity(identifierInfo, complexityScore);
                String analysis = generateAnalysis(identifierInfo);
                String suggestions = generateSuggestions(identifierInfo);
                
                issues.add(String.format(
                    "%s [LongIdentifier] %s:%d - %s '%s' (%d chars, Complexity: %.2f) - %s | Suggestions: %s",
                    severity,
                    cu.getStorage().map(s -> s.getFileName()).orElse("UnknownFile"),
                    identifierInfo.lineNumber,
                    identifierInfo.type,
                    identifierInfo.name,
                    identifierInfo.length,
                    complexityScore,
                    analysis,
                    suggestions
                ));
            }
        });
        
        return issues;
    }
    
    private double calculateComplexityScore(IdentifierInfo identifierInfo) {
        double lengthScore = Math.min(1.0, (double) identifierInfo.length / criticalLengthThreshold);
        double readabilityScore = calculateReadabilityScore(identifierInfo);
        double semanticScore = calculateSemanticScore(identifierInfo);
        double contextScore = calculateContextScore(identifierInfo);
        
        String type = identifierInfo.type;
        double typeWeight = IDENTIFIER_TYPE_WEIGHTS.getOrDefault(type, 0.8);
        
        return (lengthScore * 0.4 + readabilityScore * 0.3 + semanticScore * 0.2 + contextScore * 0.1) * typeWeight;
    }
    
    private double calculateReadabilityScore(IdentifierInfo identifierInfo) {
        double score = 0.0;
        String name = identifierInfo.name;
        
        if (!CAMEL_CASE_PATTERN.matcher(name).matches() && 
            !SNAKE_CASE_PATTERN.matcher(name).matches()) {
            score += 0.3;
        }
        
        if (ABBREVIATION_PATTERN.matcher(name).matches()) {
            score += 0.2;
        }
        
        if (hasRepeatedWords(name)) {
            score += 0.25;
        }
        
        if (hasUnnecessaryWords(name)) {
            score += 0.15;
        }
        
        if (identifierInfo.wordCount > 6) {
            score += 0.1;
        }
        
        return Math.min(1.0, score);
    }
    
    private double calculateSemanticScore(IdentifierInfo identifierInfo) {
        double score = 0.0;
        String name = identifierInfo.name.toLowerCase();
        
        if (hasRedundantTypeInfo(name, identifierInfo.type)) {
            score += 0.3;
        }
        
        if (hasVagueNaming(name)) {
            score += 0.25;
        }
        
        if (hasBusinessJargon(name)) {
            score += 0.2;
        }
        
        if (identifierInfo.acronymCount > 2) {
            score += 0.15;
        }
        
        return Math.min(0.8, score);
    }
    
    private double calculateContextScore(IdentifierInfo identifierInfo) {
        double score = 0.0;
        
        if (identifierInfo.isPublic && identifierInfo.length > 30) {
            score += 0.2;
        }
        
        if (identifierInfo.isInInterface && identifierInfo.length > 25) {
            score += 0.15;
        }
        
        if (identifierInfo.scopeLevel > 2) {
            score += 0.1;
        }
        
        return Math.min(0.3, score);
    }
    
    private boolean hasRepeatedWords(String name) {
        List<String> words = extractWords(name);
        Set<String> uniqueWords = new HashSet<>(words);
        return words.size() != uniqueWords.size();
    }
    
    private boolean hasUnnecessaryWords(String name) {
        String lower = name.toLowerCase();
        return lower.contains("data") || lower.contains("info") || lower.contains("object") ||
               lower.contains("item") || lower.contains("element") || lower.contains("thing");
    }
    
    private boolean hasRedundantTypeInfo(String name, String type) {
        if (type.equals("class") && (name.endsWith("class") || name.endsWith("object"))) {
            return true;
        }
        if (type.equals("method") && (name.startsWith("method") || name.startsWith("function"))) {
            return true;
        }
        if (type.equals("variable") && (name.startsWith("var") || name.startsWith("variable"))) {
            return true;
        }
        return false;
    }
    
    private boolean hasVagueNaming(String name) {
        return name.contains("manager") || name.contains("handler") || name.contains("processor") ||
               name.contains("utility") || name.contains("helper") || name.contains("wrapper");
    }
    
    private boolean hasBusinessJargon(String name) {
        return name.contains("synergy") || name.contains("leverage") || name.contains("paradigm") ||
               name.contains("enterprise") || name.contains("solution") || name.contains("framework");
    }
    
    private List<String> extractWords(String identifier) {
        List<String> words = new ArrayList<>();
        
        if (identifier.contains("_")) {
            words.addAll(Arrays.asList(identifier.split("_")));
        } else {
            String[] camelCaseWords = identifier.split("(?=[A-Z])");
            words.addAll(Arrays.asList(camelCaseWords));
        }
        
        return words.stream()
            .filter(word -> !word.isEmpty())
            .map(String::toLowerCase)
            .collect(java.util.stream.Collectors.toList());
    }
    
    private boolean shouldReport(IdentifierInfo identifierInfo, double complexityScore) {
        int threshold = getThresholdForType(identifierInfo.type);
        
        if (identifierInfo.length < threshold && complexityScore < 0.7) {
            return false;
        }
        
        if (isExcludedIdentifier(identifierInfo)) {
            return false;
        }
        
        return identifierInfo.length >= threshold || complexityScore > 0.8;
    }
    
    private int getThresholdForType(String type) {
        switch (type) {
            case "class":
            case "interface":
                return classThreshold;
            case "method":
                return methodThreshold;
            case "variable":
            case "parameter":
                return variableThreshold;
            default:
                return baseLengthThreshold;
        }
    }
    
    private boolean isExcludedIdentifier(IdentifierInfo identifierInfo) {
        String name = identifierInfo.name.toLowerCase();
        
        if (name.startsWith("test") && identifierInfo.type.equals("method")) {
            return identifierInfo.length < 50;
        }
        
        if (identifierInfo.isGenerated || identifierInfo.isOverridden) {
            return true;
        }
        
        return false;
    }
    
    private String getSeverity(IdentifierInfo identifierInfo, double complexityScore) {
        if (identifierInfo.length > criticalLengthThreshold || complexityScore > 1.0) {
            return "🔴";
        }
        if (identifierInfo.length > 30 || complexityScore > 0.8) {
            return "🟡";
        }
        return "🟠";
    }
    
    private String generateAnalysis(IdentifierInfo identifierInfo) {
        List<String> issues = new ArrayList<>();
        
        if (identifierInfo.length > criticalLengthThreshold) {
            issues.add("Extremely long identifier");
        } else if (identifierInfo.length > 30) {
            issues.add("Long identifier");
        }
        
        if (hasRepeatedWords(identifierInfo.name)) {
            issues.add("Repeated words");
        }
        
        if (hasUnnecessaryWords(identifierInfo.name)) {
            issues.add("Unnecessary words");
        }
        
        if (identifierInfo.acronymCount > 2) {
            issues.add("Too many acronyms");
        }
        
        if (hasVagueNaming(identifierInfo.name.toLowerCase())) {
            issues.add("Vague naming");
        }
        
        return issues.isEmpty() ? "Long identifier detected" : String.join(", ", issues);
    }
    
    private String generateSuggestions(IdentifierInfo identifierInfo) {
        List<String> suggestions = new ArrayList<>();
        
        if (identifierInfo.wordCount > 4) {
            suggestions.add("Reduce word count");
        }
        
        if (hasUnnecessaryWords(identifierInfo.name)) {
            suggestions.add("Remove filler words");
        }
        
        if (identifierInfo.acronymCount > 1) {
            suggestions.add("Expand or reduce acronyms");
        }
        
        if (hasVagueNaming(identifierInfo.name.toLowerCase())) {
            suggestions.add("Use more specific terms");
        }
        
        suggestions.add("Consider context and scope");
        suggestions.add("Use domain-specific terminology");
        
        return String.join(", ", suggestions);
    }
    
    private static class IdentifierInfo {
        String name;
        String type;
        int lineNumber;
        int length;
        int wordCount;
        int acronymCount;
        boolean isPublic;
        boolean isInInterface;
        boolean isGenerated;
        boolean isOverridden;
        int scopeLevel;
        
        IdentifierInfo(String name, String type, int lineNumber) {
            this.name = name;
            this.type = type;
            this.lineNumber = lineNumber;
            this.length = name.length();
            this.wordCount = countWords(name);
            this.acronymCount = countAcronyms(name);
        }
        
        private int countWords(String identifier) {
            if (identifier.contains("_")) {
                return identifier.split("_").length;
            } else {
                return identifier.split("(?=[A-Z])").length;
            }
        }
        
        private int countAcronyms(String identifier) {
            return (int) Pattern.compile("[A-Z]{2,}")
                .matcher(identifier)
                .results()
                .count();
        }
    }
    
    private static class IdentifierAnalyzer extends VoidVisitorAdapter<Void> {
        private final List<IdentifierInfo> longIdentifiers = new ArrayList<>();
        private int currentScopeLevel = 0;
        private boolean inInterface = false;
        
        public List<IdentifierInfo> getLongIdentifiers() {
            return longIdentifiers;
        }
        
        @Override
        public void visit(ClassOrInterfaceDeclaration n, Void arg) {
            inInterface = n.isInterface();
            analyzeIdentifier(n.getNameAsString(), "class", 
                n.getBegin().map(pos -> pos.line).orElse(0), n.isPublic(), false, false);
            
            currentScopeLevel++;
            super.visit(n, arg);
            currentScopeLevel--;
        }
        
        @Override
        public void visit(MethodDeclaration n, Void arg) {
            analyzeIdentifier(n.getNameAsString(), "method", 
                n.getBegin().map(pos -> pos.line).orElse(0), n.isPublic(), 
                hasOverrideAnnotation(n), false);
            
            currentScopeLevel++;
            super.visit(n, arg);
            currentScopeLevel--;
        }
        
        @Override
        public void visit(FieldDeclaration n, Void arg) {
            n.getVariables().forEach(var -> 
                analyzeIdentifier(var.getNameAsString(), "field", 
                    n.getBegin().map(pos -> pos.line).orElse(0), n.isPublic(), false, false));
            
            super.visit(n, arg);
        }
        
        @Override
        public void visit(VariableDeclarationExpr n, Void arg) {
            n.getVariables().forEach(var -> 
                analyzeIdentifier(var.getNameAsString(), "variable", 
                    n.getBegin().map(pos -> pos.line).orElse(0), false, false, false));
            
            super.visit(n, arg);
        }
        
        @Override
        public void visit(Parameter n, Void arg) {
            analyzeIdentifier(n.getNameAsString(), "parameter", 
                n.getBegin().map(pos -> pos.line).orElse(0), false, false, false);
            
            super.visit(n, arg);
        }
        
        private void analyzeIdentifier(String name, String type, int lineNumber, 
                                     boolean isPublic, boolean isOverridden, boolean isGenerated) {
            IdentifierInfo info = new IdentifierInfo(name, type, lineNumber);
            info.isPublic = isPublic;
            info.isInInterface = inInterface;
            info.isOverridden = isOverridden;
            info.isGenerated = isGenerated;
            info.scopeLevel = currentScopeLevel;
            
            longIdentifiers.add(info);
        }
        
        private boolean hasOverrideAnnotation(MethodDeclaration method) {
            return method.getAnnotations().stream()
                .anyMatch(annotation -> annotation.getNameAsString().equals("Override"));
        }
    }
}