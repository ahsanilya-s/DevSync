package com.devsync.detectors;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import java.util.*;

public class LongIdentifierDetector {
    
    private int variableThreshold = 20;
    private int methodThreshold = 30;
    private int classThreshold = 35;
    
    public void setMaxLength(int maxLength) {
        this.variableThreshold = Math.max(10, maxLength - 10);
        this.methodThreshold = maxLength;
        this.classThreshold = maxLength + 5;
    }

    public List<String> detect(CompilationUnit cu) {
        List<String> issues = new ArrayList<>();
        
        IdentifierAnalyzer analyzer = new IdentifierAnalyzer();
        cu.accept(analyzer, null);
        
        analyzer.getLongIdentifiers().forEach(identifierInfo -> {
            // THRESHOLD CHECK FIRST - binary detection
            int threshold = getThresholdForType(identifierInfo.type);
            if (identifierInfo.length < threshold) {
                return; // NO SMELL - exit immediately
            }
            
            // THRESHOLD EXCEEDED - now calculate score for severity only
            double complexityScore = calculateComplexityScore(identifierInfo);
            String severity = getSeverity(complexityScore);
            String analysis = generateAnalysis(identifierInfo);
            String suggestions = generateSuggestions(identifierInfo);
            
            issues.add(String.format(
                "%s [LongIdentifier] %s:%d - %s '%s' (%d chars, Complexity: %.2f) - %s | Suggestions: %s | DetailedReason: %s | ThresholdDetails: {\"identifierLength\":%d,\"threshold\":%d,\"type\":\"%s\",\"wordCount\":%d,\"complexityScore\":%.2f,\"exceedsThreshold\":true,\"summary\":\"Identifiers are flagged when length >= threshold (method: 30, variable: 20, class: 35).\"}" ,
                severity,
                cu.getStorage().map(s -> s.getFileName()).orElse("UnknownFile"),
                identifierInfo.lineNumber,
                identifierInfo.type,
                identifierInfo.name,
                identifierInfo.length,
                complexityScore,
                analysis,
                suggestions,
                generateDetailedReason(identifierInfo, complexityScore),
                identifierInfo.length, threshold, identifierInfo.type, identifierInfo.wordCount, complexityScore
            ));
        });
        
        return issues;
    }
    
    private int getThresholdForType(String type) {
        return switch (type) {
            case "class", "interface" -> classThreshold;
            case "method" -> methodThreshold;
            case "variable", "parameter" -> variableThreshold;
            default -> methodThreshold;
        };
    }
    
    private double calculateComplexityScore(IdentifierInfo identifierInfo) {
        double lengthScore = Math.min(1.0, (double) identifierInfo.length / 50);
        double wordCountScore = Math.min(1.0, (double) identifierInfo.wordCount / 8);
        return lengthScore * 0.7 + wordCountScore * 0.3;
    }
    
    private String getSeverity(double score) {
        if (score >= 0.8) return "🔴";
        if (score >= 0.5) return "🟡";
        return "🟠";
    }
    
    private String generateAnalysis(IdentifierInfo identifierInfo) {
        if (identifierInfo.length > 50) return "Extremely long identifier";
        if (identifierInfo.wordCount > 6) return "Too many words";
        return "Identifier too long";
    }
    
    private String generateSuggestions(IdentifierInfo identifierInfo) {
        return "Use shorter, more concise names; remove redundant words; use abbreviations where appropriate";
    }
    
    private String generateDetailedReason(IdentifierInfo identifierInfo, double complexityScore) {
        int threshold = getThresholdForType(identifierInfo.type);
        return String.format(
            "This %s identifier is flagged because it has %d characters (threshold: %d) and %d words. " +
            "Complexity score: %.2f. Long identifiers reduce code readability and make maintenance difficult.",
            identifierInfo.type, identifierInfo.length, threshold, identifierInfo.wordCount, complexityScore
        );
    }
    
    private static class IdentifierInfo {
        String name;
        String type;
        int lineNumber;
        int length;
        int wordCount;
    }
    
    private static class IdentifierAnalyzer extends VoidVisitorAdapter<Void> {
        private final List<IdentifierInfo> longIdentifiers = new ArrayList<>();
        
        public List<IdentifierInfo> getLongIdentifiers() {
            return longIdentifiers;
        }
        
        @Override
        public void visit(ClassOrInterfaceDeclaration n, Void arg) {
            addIdentifier(n.getNameAsString(), n.isInterface() ? "interface" : "class", 
                n.getBegin().map(pos -> pos.line).orElse(0));
            super.visit(n, arg);
        }
        
        @Override
        public void visit(MethodDeclaration n, Void arg) {
            addIdentifier(n.getNameAsString(), "method", 
                n.getBegin().map(pos -> pos.line).orElse(0));
            super.visit(n, arg);
        }
        
        @Override
        public void visit(VariableDeclarator n, Void arg) {
            addIdentifier(n.getNameAsString(), "variable", 
                n.getBegin().map(pos -> pos.line).orElse(0));
            super.visit(n, arg);
        }
        
        @Override
        public void visit(Parameter n, Void arg) {
            addIdentifier(n.getNameAsString(), "parameter", 
                n.getBegin().map(pos -> pos.line).orElse(0));
            super.visit(n, arg);
        }
        
        private void addIdentifier(String name, String type, int lineNumber) {
            IdentifierInfo info = new IdentifierInfo();
            info.name = name;
            info.type = type;
            info.lineNumber = lineNumber;
            info.length = name.length();
            info.wordCount = countWords(name);
            longIdentifiers.add(info);
        }
        
        private int countWords(String identifier) {
            // Count camelCase words
            int count = 1;
            for (char c : identifier.toCharArray()) {
                if (Character.isUpperCase(c)) count++;
            }
            // Count snake_case words
            count += identifier.split("_").length - 1;
            return count;
        }
    }
}
