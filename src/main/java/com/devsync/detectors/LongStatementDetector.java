package com.devsync.detectors;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import java.util.*;

public class LongStatementDetector {
    
    private static final int BASE_TOKEN_THRESHOLD = 20;
    private static final int CRITICAL_TOKEN_THRESHOLD = 30;
    private static final int BASE_CHAR_THRESHOLD = 150;
    private static final int CRITICAL_CHAR_THRESHOLD = 250;

    public List<String> detect(CompilationUnit cu) {
        List<String> issues = new ArrayList<>();
        Set<String> processedLines = new HashSet<>();
        
        StatementAnalyzer analyzer = new StatementAnalyzer();
        cu.accept(analyzer, null);
        
        analyzer.getLongStatements().forEach(stmtInfo -> {
            String lineKey = stmtInfo.fileName + ":" + stmtInfo.lineNumber;
            if (!processedLines.contains(lineKey) && shouldReport(stmtInfo)) {
                processedLines.add(lineKey);
                
                double score = calculateScore(stmtInfo);
                String severity = getSeverity(score);
                
                issues.add(String.format(
                    "%s [LongStatement] %s:%d - %s (%d tokens, %d chars) - %s | Suggestions: %s",
                    severity,
                    stmtInfo.fileName,
                    stmtInfo.lineNumber,
                    stmtInfo.type,
                    stmtInfo.tokenCount,
                    stmtInfo.charLength,
                    generateAnalysis(stmtInfo),
                    generateSuggestions(stmtInfo)
                ));
            }
        });
        
        return issues;
    }
    
    private double calculateScore(StatementInfo stmtInfo) {
        double tokenScore = Math.min(1.0, (double) stmtInfo.tokenCount / CRITICAL_TOKEN_THRESHOLD);
        double charScore = Math.min(1.0, (double) stmtInfo.charLength / CRITICAL_CHAR_THRESHOLD);
        double complexityScore = Math.min(1.0, (double) stmtInfo.expressionComplexity / 10);
        double chainScore = Math.min(1.0, (double) stmtInfo.methodChainLength / 5);
        
        return Math.max(tokenScore, charScore) * 0.5 + complexityScore * 0.3 + chainScore * 0.2;
    }
    
    private boolean shouldReport(StatementInfo stmtInfo) {
        // Only report truly long statements
        return (stmtInfo.tokenCount >= BASE_TOKEN_THRESHOLD && stmtInfo.charLength >= BASE_CHAR_THRESHOLD) || 
               stmtInfo.expressionComplexity >= 12 ||
               stmtInfo.methodChainLength >= 6;
    }
    
    private String getSeverity(double score) {
        if (score >= 0.8) return "🔴";
        if (score >= 0.5) return "🟡";
        return "🟠";
    }
    
    private String generateAnalysis(StatementInfo stmtInfo) {
        List<String> issues = new ArrayList<>();
        
        if (stmtInfo.tokenCount > CRITICAL_TOKEN_THRESHOLD) {
            issues.add("Too many tokens");
        }
        if (stmtInfo.charLength > CRITICAL_CHAR_THRESHOLD) {
            issues.add("Extremely long");
        }
        if (stmtInfo.expressionComplexity > 8) {
            issues.add("Complex expression");
        }
        if (stmtInfo.methodChainLength > 4) {
            issues.add("Long method chain");
        }
        
        return issues.isEmpty() ? "Statement too complex" : String.join(", ", issues);
    }
    
    private String generateSuggestions(StatementInfo stmtInfo) {
        if (stmtInfo.methodChainLength > 3) {
            return "Break method chain into intermediate variables";
        }
        if (stmtInfo.expressionComplexity > 6) {
            return "Extract sub-expressions to variables";
        }
        return "Split into multiple statements";
    }
    
    private static class StatementInfo {
        String fileName;
        String type;
        int lineNumber;
        int tokenCount;
        int charLength;
        int expressionComplexity;
        int methodChainLength;
        int nestingDepth;
    }
    
    private static class StatementAnalyzer extends VoidVisitorAdapter<Void> {
        private final List<StatementInfo> longStatements = new ArrayList<>();
        private String fileName = "";
        private int nestingDepth = 0;
        
        public List<StatementInfo> getLongStatements() {
            return longStatements;
        }
        
        @Override
        public void visit(CompilationUnit n, Void arg) {
            fileName = n.getStorage().map(s -> s.getFileName()).orElse("UnknownFile");
            super.visit(n, arg);
        }
        
        @Override
        public void visit(ExpressionStmt n, Void arg) {
            analyzeStatement("Expression", n);
            super.visit(n, arg);
        }
        
        @Override
        public void visit(IfStmt n, Void arg) {
            nestingDepth++;
            // Analyze the condition expression
            if (n.getCondition() != null) {
                analyzeExpression("If condition", n.getCondition(), n.getBegin().map(pos -> pos.line).orElse(0));
            }
            super.visit(n, arg);
            nestingDepth--;
        }
        
        @Override
        public void visit(ForStmt n, Void arg) {
            nestingDepth++;
            // Analyze initialization, condition, and update expressions
            n.getInitialization().forEach(init -> analyzeExpression("For init", init, n.getBegin().map(pos -> pos.line).orElse(0)));
            n.getCompare().ifPresent(cond -> analyzeExpression("For condition", cond, n.getBegin().map(pos -> pos.line).orElse(0)));
            n.getUpdate().forEach(update -> analyzeExpression("For update", update, n.getBegin().map(pos -> pos.line).orElse(0)));
            super.visit(n, arg);
            nestingDepth--;
        }
        
        @Override
        public void visit(WhileStmt n, Void arg) {
            nestingDepth++;
            if (n.getCondition() != null) {
                analyzeExpression("While condition", n.getCondition(), n.getBegin().map(pos -> pos.line).orElse(0));
            }
            super.visit(n, arg);
            nestingDepth--;
        }
        
        @Override
        public void visit(AssignExpr n, Void arg) {
            analyzeExpression("Assignment", n, n.getBegin().map(pos -> pos.line).orElse(0));
            super.visit(n, arg);
        }
        
        @Override
        public void visit(MethodCallExpr n, Void arg) {
            analyzeExpression("Method call", n, n.getBegin().map(pos -> pos.line).orElse(0));
            super.visit(n, arg);
        }
        
        private void analyzeStatement(String type, Statement stmt) {
            String content = stmt.toString().trim();
            StatementInfo info = createStatementInfo(type, content, stmt.getBegin().map(pos -> pos.line).orElse(0));
            
            if (info.tokenCount >= BASE_TOKEN_THRESHOLD || info.charLength >= BASE_CHAR_THRESHOLD) {
                longStatements.add(info);
            }
        }
        
        private void analyzeExpression(String type, Expression expr, int lineNumber) {
            String content = expr.toString().trim();
            StatementInfo info = createStatementInfo(type, content, lineNumber);
            
            if (info.tokenCount >= BASE_TOKEN_THRESHOLD || info.charLength >= BASE_CHAR_THRESHOLD || info.expressionComplexity >= 8) {
                longStatements.add(info);
            }
        }
        
        private StatementInfo createStatementInfo(String type, String content, int lineNumber) {
            StatementInfo info = new StatementInfo();
            info.fileName = fileName;
            info.type = type;
            info.lineNumber = lineNumber;
            info.charLength = content.length();
            info.tokenCount = countTokens(content);
            info.expressionComplexity = calculateExpressionComplexity(content);
            info.methodChainLength = countMethodChain(content);
            info.nestingDepth = nestingDepth;
            
            return info;
        }
        
        private int countTokens(String content) {
            // Split by operators, whitespace, and punctuation to count meaningful tokens
            String[] tokens = content.split("[\\s+\\-*/=<>!&|(){}\\[\\],;.]+");
            return (int) Arrays.stream(tokens).filter(token -> !token.trim().isEmpty()).count();
        }
        
        private int calculateExpressionComplexity(String content) {
            int complexity = 0;
            
            // Count operators
            complexity += countOccurrences(content, "[+\\-*/=<>!&|]");
            
            // Count parentheses groups
            complexity += countOccurrences(content, "\\(");
            
            // Count method calls
            complexity += countOccurrences(content, "\\w+\\s*\\(");
            
            // Count array accesses
            complexity += countOccurrences(content, "\\[.*?\\]");
            
            // Count ternary operators
            complexity += countOccurrences(content, "\\?") * 2;
            
            return complexity;
        }
        
        private int countMethodChain(String content) {
            return countOccurrences(content, "\\.");
        }
        
        private int countOccurrences(String content, String regex) {
            return content.split(regex).length - 1;
        }
    }
}