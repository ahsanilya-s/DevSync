package com.devsync.detectors;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import java.util.*;
import java.util.stream.Collectors;

public class LongMethodDetector {
    private static final int MAX_LINES = 30;
    private static final int MAX_CYCLOMATIC_COMPLEXITY = 10;
    private static final int MAX_COGNITIVE_COMPLEXITY = 15;
    private static final int MAX_NESTING_DEPTH = 4;
    private static final double COMPLEXITY_WEIGHT = 0.4;
    private static final double LENGTH_WEIGHT = 0.3;
    private static final double NESTING_WEIGHT = 0.3;

    public List<String> detect(CompilationUnit cu) {
        List<String> issues = new ArrayList<>();
        
        cu.findAll(MethodDeclaration.class).forEach(method -> {
            MethodMetrics metrics = analyzeMethod(method);
            
            // Multi-dimensional analysis
            List<String> violations = new ArrayList<>();
            double complexityScore = calculateComplexityScore(metrics);
            
            if (metrics.lineCount > MAX_LINES) {
                violations.add(String.format("lines=%d>%d", metrics.lineCount, MAX_LINES));
            }
            
            if (metrics.cyclomaticComplexity > MAX_CYCLOMATIC_COMPLEXITY) {
                violations.add(String.format("cyclomatic=%d>%d", metrics.cyclomaticComplexity, MAX_CYCLOMATIC_COMPLEXITY));
            }
            
            if (metrics.cognitiveComplexity > MAX_COGNITIVE_COMPLEXITY) {
                violations.add(String.format("cognitive=%d>%d", metrics.cognitiveComplexity, MAX_COGNITIVE_COMPLEXITY));
            }
            
            if (metrics.maxNestingDepth > MAX_NESTING_DEPTH) {
                violations.add(String.format("nesting=%d>%d", metrics.maxNestingDepth, MAX_NESTING_DEPTH));
            }
            
            if (!violations.isEmpty() || complexityScore > 0.7) {
                String severity = getSeverity(complexityScore, violations.size());
                String refactoringSuggestions = generateRefactoringSuggestions(metrics);
                
                issues.add(String.format(
                    "%s [LongMethod] %s:%d - Method '%s' complexity issues [Score: %.2f] - %s | Suggestions: %s",
                    severity,
                    cu.getStorage().map(s -> s.getFileName()).orElse("UnknownFile"),
                    method.getBegin().map(p -> p.line).orElse(-1),
                    method.getNameAsString(),
                    complexityScore,
                    String.join(", ", violations),
                    refactoringSuggestions
                ));
            }
        });
        
        return issues;
    }
    
    private MethodMetrics analyzeMethod(MethodDeclaration method) {
        MethodMetrics metrics = new MethodMetrics();
        
        // Calculate line count (excluding empty lines and comments)
        metrics.lineCount = method.getEnd().map(p -> p.line).orElse(0) - 
                           method.getBegin().map(p -> p.line).orElse(0) + 1;
        
        // Advanced complexity analysis
        ComplexityAnalyzer analyzer = new ComplexityAnalyzer();
        method.accept(analyzer, metrics);
        
        return metrics;
    }
    
    private double calculateComplexityScore(MethodMetrics metrics) {
        double lengthScore = Math.min(1.0, (double) metrics.lineCount / (MAX_LINES * 2));
        double cyclomaticScore = Math.min(1.0, (double) metrics.cyclomaticComplexity / (MAX_CYCLOMATIC_COMPLEXITY * 2));
        double nestingScore = Math.min(1.0, (double) metrics.maxNestingDepth / (MAX_NESTING_DEPTH * 2));
        
        return (lengthScore * LENGTH_WEIGHT) + 
               (cyclomaticScore * COMPLEXITY_WEIGHT) + 
               (nestingScore * NESTING_WEIGHT);
    }
    
    private String getSeverity(double score, int violationCount) {
        if (score > 0.9 || violationCount >= 3) return "🔴";
        if (score > 0.7 || violationCount >= 2) return "🟡";
        return "🟠";
    }
    
    private String generateRefactoringSuggestions(MethodMetrics metrics) {
        List<String> suggestions = new ArrayList<>();
        
        if (metrics.lineCount > MAX_LINES) {
            suggestions.add("Extract methods");
        }
        if (metrics.cyclomaticComplexity > MAX_CYCLOMATIC_COMPLEXITY) {
            suggestions.add("Reduce branching");
        }
        if (metrics.maxNestingDepth > MAX_NESTING_DEPTH) {
            suggestions.add("Flatten nested structures");
        }
        if (metrics.cognitiveComplexity > MAX_COGNITIVE_COMPLEXITY) {
            suggestions.add("Simplify logic flow");
        }
        
        return suggestions.isEmpty() ? "Optimize structure" : String.join(", ", suggestions);
    }
    
    private static class MethodMetrics {
        int lineCount = 0;
        int cyclomaticComplexity = 1; // Base complexity
        int cognitiveComplexity = 0;
        int maxNestingDepth = 0;
        int currentNestingDepth = 0;
    }
    
    private static class ComplexityAnalyzer extends VoidVisitorAdapter<MethodMetrics> {
        
        @Override
        public void visit(IfStmt n, MethodMetrics metrics) {
            metrics.cyclomaticComplexity++;
            metrics.cognitiveComplexity += (1 + metrics.currentNestingDepth);
            
            metrics.currentNestingDepth++;
            metrics.maxNestingDepth = Math.max(metrics.maxNestingDepth, metrics.currentNestingDepth);
            super.visit(n, metrics);
            metrics.currentNestingDepth--;
        }
        
        @Override
        public void visit(WhileStmt n, MethodMetrics metrics) {
            metrics.cyclomaticComplexity++;
            metrics.cognitiveComplexity += (1 + metrics.currentNestingDepth);
            
            metrics.currentNestingDepth++;
            metrics.maxNestingDepth = Math.max(metrics.maxNestingDepth, metrics.currentNestingDepth);
            super.visit(n, metrics);
            metrics.currentNestingDepth--;
        }
        
        @Override
        public void visit(ForStmt n, MethodMetrics metrics) {
            metrics.cyclomaticComplexity++;
            metrics.cognitiveComplexity += (1 + metrics.currentNestingDepth);
            
            metrics.currentNestingDepth++;
            metrics.maxNestingDepth = Math.max(metrics.maxNestingDepth, metrics.currentNestingDepth);
            super.visit(n, metrics);
            metrics.currentNestingDepth--;
        }
        
        @Override
        public void visit(ForEachStmt n, MethodMetrics metrics) {
            metrics.cyclomaticComplexity++;
            metrics.cognitiveComplexity += (1 + metrics.currentNestingDepth);
            
            metrics.currentNestingDepth++;
            metrics.maxNestingDepth = Math.max(metrics.maxNestingDepth, metrics.currentNestingDepth);
            super.visit(n, metrics);
            metrics.currentNestingDepth--;
        }
        
        @Override
        public void visit(SwitchStmt n, MethodMetrics metrics) {
            metrics.cyclomaticComplexity += n.getEntries().size();
            metrics.cognitiveComplexity += (1 + metrics.currentNestingDepth);
            
            metrics.currentNestingDepth++;
            metrics.maxNestingDepth = Math.max(metrics.maxNestingDepth, metrics.currentNestingDepth);
            super.visit(n, metrics);
            metrics.currentNestingDepth--;
        }
        
        @Override
        public void visit(TryStmt n, MethodMetrics metrics) {
            metrics.cyclomaticComplexity += n.getCatchClauses().size();
            metrics.cognitiveComplexity += (1 + metrics.currentNestingDepth);
            
            metrics.currentNestingDepth++;
            metrics.maxNestingDepth = Math.max(metrics.maxNestingDepth, metrics.currentNestingDepth);
            super.visit(n, metrics);
            metrics.currentNestingDepth--;
        }
        
        @Override
        public void visit(ConditionalExpr n, MethodMetrics metrics) {
            metrics.cyclomaticComplexity++;
            metrics.cognitiveComplexity++;
            super.visit(n, metrics);
        }
        
        @Override
        public void visit(BinaryExpr n, MethodMetrics metrics) {
            if (n.getOperator() == BinaryExpr.Operator.AND || n.getOperator() == BinaryExpr.Operator.OR) {
                metrics.cyclomaticComplexity++;
            }
            super.visit(n, metrics);
        }
    }
}
