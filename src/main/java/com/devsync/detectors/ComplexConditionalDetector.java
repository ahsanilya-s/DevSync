package com.devsync.detectors;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import java.util.*;

public class ComplexConditionalDetector {
    
    private static final int BASE_COMPLEXITY_THRESHOLD = 4;
    private static final int CRITICAL_COMPLEXITY_THRESHOLD = 8;
    private static final int MAX_NESTING_DEPTH = 3;

    public List<String> detect(CompilationUnit cu) {
        List<String> issues = new ArrayList<>();
        
        ConditionalAnalyzer analyzer = new ConditionalAnalyzer();
        cu.accept(analyzer, null);
        
        analyzer.getComplexConditionals().forEach(condInfo -> {
            double complexityScore = calculateComplexityScore(condInfo);
            
            if (shouldReport(condInfo, complexityScore)) {
                String severity = getSeverity(condInfo, complexityScore);
                String analysis = generateAnalysis(condInfo);
                String suggestions = generateSuggestions(condInfo);
                
                issues.add(String.format(
                    "%s [ComplexConditional] %s:%d - %s (Operators: %d, Depth: %d, Score: %.2f) - %s | Suggestions: %s | DetailedReason: %s",
                    severity,
                    cu.getStorage().map(s -> s.getFileName()).orElse("UnknownFile"),
                    condInfo.lineNumber,
                    condInfo.type,
                    condInfo.operatorCount,
                    condInfo.nestingDepth,
                    complexityScore,
                    analysis,
                    suggestions,
                    generateDetailedReason(condInfo, complexityScore)
                ));
            }
        });
        
        return issues;
    }
    
    private double calculateComplexityScore(ConditionalInfo condInfo) {
        double operatorScore = Math.min(1.0, (double) condInfo.operatorCount / CRITICAL_COMPLEXITY_THRESHOLD);
        double nestingScore = Math.min(1.0, (double) condInfo.nestingDepth / MAX_NESTING_DEPTH);
        double lengthScore = Math.min(1.0, (double) condInfo.expressionLength / 200);
        double readabilityScore = calculateReadabilityScore(condInfo);
        
        return operatorScore * 0.4 + nestingScore * 0.3 + lengthScore * 0.2 + readabilityScore * 0.1;
    }
    
    private double calculateReadabilityScore(ConditionalInfo condInfo) {
        double score = 0.0;
        
        if (condInfo.hasMethodCalls) {
            score += 0.2;
        }
        if (condInfo.hasNestedParentheses) {
            score += 0.3;
        }
        if (condInfo.hasMixedOperators) {
            score += 0.25;
        }
        if (condInfo.hasNegations > 1) {
            score += 0.15;
        }
        
        return Math.min(1.0, score);
    }
    
    private boolean shouldReport(ConditionalInfo condInfo, double complexityScore) {
        return condInfo.operatorCount >= BASE_COMPLEXITY_THRESHOLD || 
               condInfo.nestingDepth > MAX_NESTING_DEPTH || 
               complexityScore > 0.6;
    }
    
    private String getSeverity(ConditionalInfo condInfo, double complexityScore) {
        if (condInfo.operatorCount > CRITICAL_COMPLEXITY_THRESHOLD || complexityScore > 0.9) {
            return "🔴";
        }
        if (condInfo.operatorCount > 6 || complexityScore > 0.7) {
            return "🟡";
        }
        return "🟠";
    }
    
    private String generateAnalysis(ConditionalInfo condInfo) {
        List<String> issues = new ArrayList<>();
        
        if (condInfo.operatorCount > CRITICAL_COMPLEXITY_THRESHOLD) {
            issues.add("Too many logical operators");
        }
        if (condInfo.nestingDepth > MAX_NESTING_DEPTH) {
            issues.add("Deep nesting");
        }
        if (condInfo.hasNestedParentheses) {
            issues.add("Complex parentheses");
        }
        if (condInfo.hasMixedOperators) {
            issues.add("Mixed AND/OR operators");
        }
        if (condInfo.hasNegations > 1) {
            issues.add("Multiple negations");
        }
        
        return issues.isEmpty() ? "Complex conditional" : String.join(", ", issues);
    }
    
    private String generateSuggestions(ConditionalInfo condInfo) {
        List<String> suggestions = new ArrayList<>();
        
        if (condInfo.operatorCount > 5) {
            suggestions.add("Extract boolean methods");
        }
        if (condInfo.hasNestedParentheses) {
            suggestions.add("Simplify with intermediate variables");
        }
        if (condInfo.hasMixedOperators) {
            suggestions.add("Group related conditions");
        }
        if (condInfo.hasNegations > 1) {
            suggestions.add("Use positive logic");
        }
        
        return String.join(", ", suggestions);
    }
    
    private String generateDetailedReason(ConditionalInfo condInfo, double complexityScore) {
        StringBuilder reason = new StringBuilder();
        reason.append("This conditional is flagged as a code smell because: ");
        
        List<String> issues = new ArrayList<>();
        
        issues.add(String.format("it has %d logical operators (threshold: %d)", condInfo.operatorCount, BASE_COMPLEXITY_THRESHOLD));
        
        if (condInfo.nestingDepth > MAX_NESTING_DEPTH) {
            issues.add(String.format("nesting depth is %d levels (max: %d)", condInfo.nestingDepth, MAX_NESTING_DEPTH));
        }
        
        if (condInfo.hasNestedParentheses) {
            issues.add("it has nested parentheses, making it hard to parse");
        }
        
        if (condInfo.hasMixedOperators) {
            issues.add("it mixes AND and OR operators without clear grouping");
        }
        
        if (condInfo.hasNegations > 1) {
            issues.add(String.format("it has %d negations, making logic harder to follow", condInfo.hasNegations));
        }
        
        if (condInfo.hasMethodCalls) {
            issues.add("it includes method calls within the condition");
        }
        
        reason.append(String.join(", ", issues));
        reason.append(String.format(". Complexity score: %.2f. Complex conditionals are error-prone and hard to test.", complexityScore));
        
        return reason.toString();
    }
    
    private static class ConditionalInfo {
        String type;
        int lineNumber;
        int operatorCount;
        int nestingDepth;
        int expressionLength;
        boolean hasMethodCalls;
        boolean hasNestedParentheses;
        boolean hasMixedOperators;
        int hasNegations;
        
        ConditionalInfo(String type, int lineNumber, Expression expr) {
            this.type = type;
            this.lineNumber = lineNumber;
            this.expressionLength = expr.toString().length();
            analyzeExpression(expr);
        }
        
        private void analyzeExpression(Expression expr) {
            ComplexityCounter counter = new ComplexityCounter();
            expr.accept(counter, null);
            
            this.operatorCount = counter.operatorCount;
            this.nestingDepth = counter.maxDepth;
            this.hasMethodCalls = counter.hasMethodCalls;
            this.hasNestedParentheses = counter.hasNestedParentheses;
            this.hasMixedOperators = counter.hasMixedOperators;
            this.hasNegations = counter.negationCount;
        }
    }
    
    private static class ComplexityCounter extends VoidVisitorAdapter<Void> {
        int operatorCount = 0;
        int currentDepth = 0;
        int maxDepth = 0;
        boolean hasMethodCalls = false;
        boolean hasNestedParentheses = false;
        boolean hasMixedOperators = false;
        int negationCount = 0;
        boolean hasAndOperator = false;
        boolean hasOrOperator = false;
        
        @Override
        public void visit(BinaryExpr n, Void arg) {
            if (n.getOperator() == BinaryExpr.Operator.AND) {
                operatorCount++;
                hasAndOperator = true;
            } else if (n.getOperator() == BinaryExpr.Operator.OR) {
                operatorCount++;
                hasOrOperator = true;
            }
            
            currentDepth++;
            maxDepth = Math.max(maxDepth, currentDepth);
            super.visit(n, arg);
            currentDepth--;
            
            hasMixedOperators = hasAndOperator && hasOrOperator;
        }
        
        @Override
        public void visit(UnaryExpr n, Void arg) {
            if (n.getOperator() == UnaryExpr.Operator.LOGICAL_COMPLEMENT) {
                negationCount++;
            }
            super.visit(n, arg);
        }
        
        @Override
        public void visit(MethodCallExpr n, Void arg) {
            hasMethodCalls = true;
            super.visit(n, arg);
        }
        
        @Override
        public void visit(EnclosedExpr n, Void arg) {
            if (currentDepth > 0) {
                hasNestedParentheses = true;
            }
            super.visit(n, arg);
        }
    }
    
    private static class ConditionalAnalyzer extends VoidVisitorAdapter<Void> {
        private final List<ConditionalInfo> complexConditionals = new ArrayList<>();
        
        public List<ConditionalInfo> getComplexConditionals() {
            return complexConditionals;
        }
        
        @Override
        public void visit(IfStmt n, Void arg) {
            analyzeCondition("If statement", n.getCondition(), 
                n.getBegin().map(pos -> pos.line).orElse(0));
            super.visit(n, arg);
        }
        
        @Override
        public void visit(WhileStmt n, Void arg) {
            analyzeCondition("While loop", n.getCondition(), 
                n.getBegin().map(pos -> pos.line).orElse(0));
            super.visit(n, arg);
        }
        
        @Override
        public void visit(ConditionalExpr n, Void arg) {
            analyzeCondition("Ternary operator", n.getCondition(), 
                n.getBegin().map(pos -> pos.line).orElse(0));
            super.visit(n, arg);
        }
        
        private void analyzeCondition(String type, Expression condition, int lineNumber) {
            ConditionalInfo info = new ConditionalInfo(type, lineNumber, condition);
            
            if (info.operatorCount >= BASE_COMPLEXITY_THRESHOLD || 
                info.nestingDepth > MAX_NESTING_DEPTH) {
                complexConditionals.add(info);
            }
        }
    }
}