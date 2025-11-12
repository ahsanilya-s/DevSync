package com.devsync.detectors;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class LongIdentifierDetector {
    private static final int MAX_CLASS_LENGTH = 25;
    private static final int MAX_METHOD_LENGTH = 30;
    private static final int MAX_VARIABLE_LENGTH = 20;
    private static final int MAX_CONSTANT_LENGTH = 35;
    
    private static final double READABILITY_THRESHOLD = 0.4;
    private static final double SEMANTIC_WEIGHT = 0.4;
    private static final double LENGTH_WEIGHT = 0.3;
    private static final double COMPLEXITY_WEIGHT = 0.3;
    
    private static final Set<String> ACCEPTABLE_ABBREVIATIONS = Set.of(
        "id", "url", "uri", "xml", "json", "http", "api", "db", "sql", "ui", "io",
        "dto", "dao", "vo", "bo", "po", "config", "util", "impl", "mgr", "svc"
    );
    
    private static final Map<String, Set<String>> SEMANTIC_CATEGORIES = Map.of(
        "action", Set.of("get", "set", "create", "delete", "update", "process", "handle", "execute", "run", "start", "stop"),
        "data", Set.of("list", "map", "array", "collection", "set", "queue", "stack", "tree", "graph"),
        "state", Set.of("is", "has", "can", "should", "will", "was", "enabled", "disabled", "active", "inactive"),
        "type", Set.of("string", "number", "integer", "double", "float", "boolean", "date", "time", "object")
    );
    
    private static final Pattern CAMEL_CASE_PATTERN = Pattern.compile("([a-z])([A-Z])");
    private static final Pattern UNDERSCORE_PATTERN = Pattern.compile("_+");

    public List<String> detect(CompilationUnit cu) {
        List<String> issues = new ArrayList<>();
        
        IdentifierAnalyzer analyzer = new IdentifierAnalyzer();
        cu.accept(analyzer, null);
        
        analyzer.getProblematicIdentifiers().forEach(identifier -> {
            double readabilityScore = calculateReadabilityScore(identifier);
            
            if (readabilityScore < READABILITY_THRESHOLD || isExcessivelyLong(identifier)) {
                String severity = getSeverity(readabilityScore, identifier.length);
                String analysis = generateAnalysis(identifier);
                String suggestions = generateSuggestions(identifier);
                
                issues.add(String.format(
                    "%s [LongIdentifier] %s:%d - %s '%s' [Score: %.2f, Length: %d] - %s | Suggestions: %s",
                    severity,
                    cu.getStorage().map(s -> s.getFileName()).orElse("UnknownFile"),
                    identifier.lineNumber,
                    identifier.type,
                    identifier.name,
                    readabilityScore,
                    identifier.length,
                    analysis,
                    suggestions
                ));
            }
        });
        
        return issues;
    }
    
    private double calculateReadabilityScore(IdentifierInfo identifier) {
        double lengthScore = calculateLengthScore(identifier);
        double semanticScore = calculateSemanticScore(identifier);
        double complexityScore = calculateComplexityScore(identifier);
        
        return (lengthScore * LENGTH_WEIGHT) + 
               (semanticScore * SEMANTIC_WEIGHT) + 
               (complexityScore * COMPLEXITY_WEIGHT);
    }
    
    private double calculateLengthScore(IdentifierInfo identifier) {
        int maxLength = getMaxLengthForType(identifier.type);
        
        if (identifier.length <= maxLength * 0.7) {
            return 1.0;
        } else if (identifier.length <= maxLength) {
            return 0.8;
        } else if (identifier.length <= maxLength * 1.5) {
            return 0.5;
        } else {
            return 0.2;
        }
    }
    
    private double calculateSemanticScore(IdentifierInfo identifier) {
        List<String> words = extractWords(identifier.name);
        
        if (words.isEmpty()) {
            return 0.1;
        }
        
        double semanticScore = 0.0;
        int meaningfulWords = 0;
        
        for (String word : words) {
            if (isMeaningfulWord(word)) {
                meaningfulWords++;
                semanticScore += 0.2;
            }
            
            if (belongsToSemanticCategory(word)) {
                semanticScore += 0.1;
            }
        }
        
        if (words.size() > 4) {
            semanticScore -= (words.size() - 4) * 0.1;
        }
        
        if (words.size() >= 2 && words.size() <= 3) {
            semanticScore += 0.2;
        }
        
        return Math.max(0.0, Math.min(1.0, semanticScore));
    }
    
    private double calculateComplexityScore(IdentifierInfo identifier) {
        double complexityScore = 1.0;
        
        if (hasConsistentCasing(identifier.name)) {
            complexityScore += 0.2;
        }
        
        List<String> words = extractWords(identifier.name);
        long abbreviationCount = words.stream()
            .mapToLong(word -> isAbbreviation(word) ? 1 : 0)
            .sum();
        
        if (abbreviationCount > words.size() * 0.5) {
            complexityScore -= 0.3;
        }
        
        if (hasRepetitivePatterns(identifier.name)) {
            complexityScore -= 0.2;
        }
        
        if (containsNumbers(identifier.name) && !identifier.type.equals("Constant")) {
            complexityScore -= 0.1;
        }
        
        return Math.max(0.0, Math.min(1.0, complexityScore));
    }
    
    private List<String> extractWords(String identifier) {
        String withSpaces = CAMEL_CASE_PATTERN.matcher(identifier).replaceAll("$1 $2");
        withSpaces = UNDERSCORE_PATTERN.matcher(withSpaces).replaceAll(" ");
        
        return Arrays.stream(withSpaces.split("\\s+"))
            .filter(word -> !word.isEmpty())
            .map(String::toLowerCase)
            .collect(Collectors.toList());
    }
    
    private boolean isMeaningfulWord(String word) {
        return word.length() >= 2 && 
               !word.matches("\\d+") &&
               !word.matches("[a-z]{1,2}");
    }
    
    private boolean belongsToSemanticCategory(String word) {
        return SEMANTIC_CATEGORIES.values().stream()
            .anyMatch(category -> category.contains(word.toLowerCase()));
    }
    
    private boolean hasConsistentCasing(String identifier) {
        return identifier.matches("[a-z][a-zA-Z0-9]*") ||
               identifier.matches("[A-Z][a-zA-Z0-9]*") ||
               identifier.matches("[A-Z][A-Z0-9_]*");
    }
    
    private boolean isAbbreviation(String word) {
        return word.length() <= 3 && 
               !ACCEPTABLE_ABBREVIATIONS.contains(word.toLowerCase()) &&
               !isMeaningfulWord(word);
    }
    
    private boolean hasRepetitivePatterns(String identifier) {
        String lower = identifier.toLowerCase();
        
        for (int len = 3; len <= lower.length() / 2; len++) {
            for (int i = 0; i <= lower.length() - len * 2; i++) {
                String pattern = lower.substring(i, i + len);
                if (lower.indexOf(pattern, i + len) != -1) {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    private boolean containsNumbers(String identifier) {
        return identifier.matches(".*\\d.*");
    }
    
    private int getMaxLengthForType(String type) {
        switch (type) {
            case "Class": return MAX_CLASS_LENGTH;
            case "Method": return MAX_METHOD_LENGTH;
            case "Constant": return MAX_CONSTANT_LENGTH;
            default: return MAX_VARIABLE_LENGTH;
        }
    }
    
    private boolean isExcessivelyLong(IdentifierInfo identifier) {
        int maxLength = getMaxLengthForType(identifier.type);
        return identifier.length > maxLength * 1.5;
    }
    
    private String getSeverity(double readabilityScore, int length) {
        if (readabilityScore < 0.2 || length > 50) {
            return "🔴";
        }
        if (readabilityScore < 0.4 || length > 35) {
            return "🟡";
        }
        return "🟠";
    }
    
    private String generateAnalysis(IdentifierInfo identifier) {
        List<String> analysis = new ArrayList<>();
        
        List<String> words = extractWords(identifier.name);
        analysis.add(String.format("words=%d", words.size()));
        
        long abbreviations = words.stream().mapToLong(w -> isAbbreviation(w) ? 1 : 0).sum();
        if (abbreviations > 0) {
            analysis.add(String.format("abbrev=%d", abbreviations));
        }
        
        if (!hasConsistentCasing(identifier.name)) {
            analysis.add("inconsistent-casing");
        }
        
        if (hasRepetitivePatterns(identifier.name)) {
            analysis.add("repetitive-pattern");
        }
        
        return String.join(", ", analysis);
    }
    
    private String generateSuggestions(IdentifierInfo identifier) {
        List<String> suggestions = new ArrayList<>();
        
        List<String> words = extractWords(identifier.name);
        
        if (words.size() > 4) {
            suggestions.add("Reduce word count");
        }
        
        long abbreviations = words.stream().mapToLong(w -> isAbbreviation(w) ? 1 : 0).sum();
        if (abbreviations > words.size() * 0.5) {
            suggestions.add("Expand abbreviations");
        }
        
        if (identifier.length > getMaxLengthForType(identifier.type) * 1.5) {
            suggestions.add("Extract to multiple identifiers");
        }
        
        if (!hasConsistentCasing(identifier.name)) {
            suggestions.add("Fix casing convention");
        }
        
        if (hasRepetitivePatterns(identifier.name)) {
            suggestions.add("Remove repetitive elements");
        }
        
        String betterName = generateBetterName(identifier);
        if (!betterName.isEmpty()) {
            suggestions.add("Consider: " + betterName);
        }
        
        return suggestions.isEmpty() ? "Simplify name" : String.join(", ", suggestions);
    }
    
    private String generateBetterName(IdentifierInfo identifier) {
        List<String> words = extractWords(identifier.name);
        
        if (words.size() > 3) {
            List<String> importantWords = new ArrayList<>();
            
            if (!words.isEmpty()) {
                importantWords.add(words.get(0));
            }
            
            words.stream()
                .filter(word -> SEMANTIC_CATEGORIES.get("action").contains(word))
                .findFirst()
                .ifPresent(importantWords::add);
            
            if (words.size() > 1 && !importantWords.contains(words.get(words.size() - 1))) {
                importantWords.add(words.get(words.size() - 1));
            }
            
            if (importantWords.size() < words.size()) {
                return String.join("", importantWords.stream()
                    .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1))
                    .collect(Collectors.toList()));
            }
        }
        
        return "";
    }
    
    private static class IdentifierInfo {
        String name;
        String type;
        int length;
        int lineNumber;
        
        IdentifierInfo(String name, String type, int lineNumber) {
            this.name = name;
            this.type = type;
            this.length = name.length();
            this.lineNumber = lineNumber;
        }
    }
    
    private static class IdentifierAnalyzer extends VoidVisitorAdapter<Void> {
        private final List<IdentifierInfo> problematicIdentifiers = new ArrayList<>();
        private final Set<String> seen = new HashSet<>();
        
        public List<IdentifierInfo> getProblematicIdentifiers() {
            return problematicIdentifiers;
        }
        
        @Override
        public void visit(ClassOrInterfaceDeclaration n, Void arg) {
            analyzeIdentifier(n.getNameAsString(), "Class", 
                n.getBegin().map(p -> p.line).orElse(-1));
            super.visit(n, arg);
        }
        
        @Override
        public void visit(MethodDeclaration n, Void arg) {
            analyzeIdentifier(n.getNameAsString(), "Method", 
                n.getBegin().map(p -> p.line).orElse(-1));
            super.visit(n, arg);
        }
        
        @Override
        public void visit(FieldDeclaration n, Void arg) {
            n.getVariables().forEach(v -> {
                String type = n.isStatic() && n.isFinal() ? "Constant" : "Field";
                analyzeIdentifier(v.getNameAsString(), type, 
                    v.getBegin().map(p -> p.line).orElse(-1));
            });
            super.visit(n, arg);
        }
        
        @Override
        public void visit(VariableDeclarationExpr n, Void arg) {
            n.getVariables().forEach(v -> {
                analyzeIdentifier(v.getNameAsString(), "Variable", 
                    v.getBegin().map(p -> p.line).orElse(-1));
            });
            super.visit(n, arg);
        }
        
        private void analyzeIdentifier(String name, String type, int lineNumber) {
            String key = name + ":" + lineNumber + ":" + type;
            
            if (seen.add(key)) {
                IdentifierInfo info = new IdentifierInfo(name, type, lineNumber);
                problematicIdentifiers.add(info);
            }
        }
    }
}