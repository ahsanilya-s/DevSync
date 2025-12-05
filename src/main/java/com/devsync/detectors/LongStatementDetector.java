package com.devsync.detectors;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import java.util.*;

public class LongStatementDetector {

    private static final int BASE_TOKEN_THRESHOLD = 40;
    private static final int CRITICAL_TOKEN_THRESHOLD = 80;
    private static final int BASE_CHAR_THRESHOLD = 250;
    private static final int CRITICAL_CHAR_THRESHOLD = 500;
    private static final int MAX_METHOD_CHAIN = 5;

    // Keywords that indicate anonymous class/lambda patterns
    private static final Set<String> ANONYMOUS_INDICATORS = Set.of(
            "new ", "@Override", "public void ", "protected void ", "private void "
    );

    // Patterns that typically indicate anonymous classes
    private static final Set<String> ANONYMOUS_PATTERNS = Set.of(
            "ChildEventListener", "ValueEventListener", "OnClickListener",
            "OnItemClickListener", "AdapterView.OnItemClickListener"
    );

    public List<String> detect(CompilationUnit cu) {
        List<String> issues = new ArrayList<>();
        Set<String> processedLines = new HashSet<>();

        StatementAnalyzer analyzer = new StatementAnalyzer();
        cu.accept(analyzer, null);

        analyzer.getLongStatements().forEach(stmtInfo -> {
            String lineKey = stmtInfo.fileName + ":" + stmtInfo.lineNumber;
            if (processedLines.contains(lineKey)) {
                return;
            }
            processedLines.add(lineKey);

            // CRITICAL: Skip all statements that contain anonymous classes or lambdas
            if (stmtInfo.containsAnonymousClass || stmtInfo.isLambdaOrMethodRef) {
                return;
            }

            // Skip statements that are simple object creations or common patterns
            if (shouldSkipCommonPattern(stmtInfo)) {
                return;
            }

            // Enhanced threshold logic - require BOTH thresholds to be exceeded
            // OR one of them to be extremely high
            boolean exceedsThresholds =
                    (stmtInfo.tokenCount >= BASE_TOKEN_THRESHOLD &&
                            stmtInfo.charLength >= BASE_CHAR_THRESHOLD) ||
                            (stmtInfo.tokenCount >= CRITICAL_TOKEN_THRESHOLD) ||
                            (stmtInfo.charLength >= CRITICAL_CHAR_THRESHOLD);

            // Also check for very long method chains
            boolean hasLongMethodChain = stmtInfo.methodChainLength >= MAX_METHOD_CHAIN &&
                    stmtInfo.tokenCount >= 20;

            // Check for complex expressions
            boolean hasComplexExpression = stmtInfo.expressionComplexity >= 15 &&
                    stmtInfo.tokenCount >= 20;

            if (!exceedsThresholds && !hasLongMethodChain && !hasComplexExpression) {
                return;
            }

            double score = calculateScore(stmtInfo);

            // Don't flag low-severity items
            if (score < 0.3) {
                return;
            }

            String severity = getSeverity(score);

            issues.add(String.format(
                    "%s [LongStatement] %s:%d - %s (%d tokens, %d chars) - %s | Suggestions: %s | DetailedReason: %s | ThresholdDetails: {\"tokenCount\":%d,\"tokenThreshold\":%d,\"charLength\":%d,\"charThreshold\":%d,\"expressionComplexity\":%d,\"methodChainLength\":%d,\"exceedsTokenThreshold\":%b,\"exceedsCharThreshold\":%b,\"containsAnonymousClass\":%b,\"isLambdaOrMethodRef\":%b}",
                    severity,
                    stmtInfo.fileName,
                    stmtInfo.lineNumber,
                    stmtInfo.type,
                    stmtInfo.tokenCount,
                    stmtInfo.charLength,
                    generateAnalysis(stmtInfo),
                    generateSuggestions(stmtInfo),
                    generateDetailedReason(stmtInfo, score),
                    stmtInfo.tokenCount, BASE_TOKEN_THRESHOLD, stmtInfo.charLength, BASE_CHAR_THRESHOLD,
                    stmtInfo.expressionComplexity, stmtInfo.methodChainLength,
                    stmtInfo.tokenCount >= BASE_TOKEN_THRESHOLD, stmtInfo.charLength >= BASE_CHAR_THRESHOLD,
                    stmtInfo.containsAnonymousClass, stmtInfo.isLambdaOrMethodRef
            ));
        });

        return issues;
    }

    private boolean shouldSkipCommonPattern(StatementInfo stmtInfo) {
        // Skip common Android patterns that are often long but acceptable
        if (stmtInfo.type.equals("Method call") && stmtInfo.content != null) {
            String content = stmtInfo.content;

            // Skip findViewById calls - they're common and acceptable
            if (content.contains("findViewById")) {
                return true;
            }

            // Skip setOnClickListener with lambdas (already filtered by isLambdaOrMethodRef)
            if (content.contains("setOnClickListener")) {
                return true;
            }

            // Skip Firebase initialization patterns
            if (content.contains("FirebaseDatabase.getInstance()") &&
                    stmtInfo.tokenCount < 60) {
                return true;
            }
        }

        // Skip variable declarations with simple initializations
        if (stmtInfo.type.equals("Variable declaration") && stmtInfo.tokenCount < 30) {
            return true;
        }

        return false;
    }

    private double calculateScore(StatementInfo stmtInfo) {
        // Calculate normalized scores (0-1 range)
        double tokenScore = Math.max(0, Math.min(1.0,
                (double)(stmtInfo.tokenCount - BASE_TOKEN_THRESHOLD) /
                        (CRITICAL_TOKEN_THRESHOLD - BASE_TOKEN_THRESHOLD)));

        double charScore = Math.max(0, Math.min(1.0,
                (double)(stmtInfo.charLength - BASE_CHAR_THRESHOLD) /
                        (CRITICAL_CHAR_THRESHOLD - BASE_CHAR_THRESHOLD)));

        double complexityScore = Math.min(1.0,
                (double) stmtInfo.expressionComplexity / 25.0);

        double chainScore = Math.min(1.0,
                (double) stmtInfo.methodChainLength / 10.0);

        // Weighted scoring with emphasis on complexity and method chains
        return (tokenScore * 0.25 + charScore * 0.25 +
                complexityScore * 0.30 + chainScore * 0.20);
    }

    private String getSeverity(double score) {
        if (score >= 0.7) return "🔴";
        if (score >= 0.4) return "🟡";
        return "🟠";
    }

    private String generateAnalysis(StatementInfo stmtInfo) {
        List<String> issues = new ArrayList<>();

        if (stmtInfo.tokenCount >= CRITICAL_TOKEN_THRESHOLD) {
            issues.add("Very high token count");
        } else if (stmtInfo.tokenCount >= BASE_TOKEN_THRESHOLD) {
            issues.add("High token count");
        }

        if (stmtInfo.charLength >= CRITICAL_CHAR_THRESHOLD) {
            issues.add("Extremely long");
        } else if (stmtInfo.charLength >= BASE_CHAR_THRESHOLD) {
            issues.add("Long statement");
        }

        if (stmtInfo.expressionComplexity >= 20) {
            issues.add("Very complex expression");
        } else if (stmtInfo.expressionComplexity >= 12) {
            issues.add("Complex expression");
        }

        if (stmtInfo.methodChainLength >= 7) {
            issues.add("Very long method chain");
        } else if (stmtInfo.methodChainLength >= 4) {
            issues.add("Long method chain");
        }

        if (stmtInfo.nestingDepth >= 4) {
            issues.add("Deeply nested");
        }

        return issues.isEmpty() ? "Moderately complex" : String.join(", ", issues);
    }

    private String generateSuggestions(StatementInfo stmtInfo) {
        if (stmtInfo.methodChainLength >= 4) {
            return "Break method chain into intermediate variables with descriptive names";
        }
        if (stmtInfo.expressionComplexity >= 12) {
            return "Extract complex sub-expressions into variables or helper methods";
        }
        if (stmtInfo.tokenCount >= BASE_TOKEN_THRESHOLD) {
            return "Split into multiple focused statements with clear intent";
        }
        return "Consider refactoring to improve readability and maintainability";
    }

    private String generateDetailedReason(StatementInfo stmtInfo, double score) {
        StringBuilder reason = new StringBuilder();
        reason.append("This statement may benefit from refactoring because: ");

        List<String> factors = new ArrayList<>();

        if (stmtInfo.tokenCount >= BASE_TOKEN_THRESHOLD) {
            factors.add(String.format("it contains %d tokens (threshold: %d)",
                    stmtInfo.tokenCount, BASE_TOKEN_THRESHOLD));
        }

        if (stmtInfo.charLength >= BASE_CHAR_THRESHOLD) {
            factors.add(String.format("it is %d characters long (threshold: %d)",
                    stmtInfo.charLength, BASE_CHAR_THRESHOLD));
        }

        if (stmtInfo.expressionComplexity >= 12) {
            factors.add(String.format("the expression complexity score is %d (indicating nested operations)",
                    stmtInfo.expressionComplexity));
        }

        if (stmtInfo.methodChainLength >= 4) {
            factors.add(String.format("it has a method chain of %d calls",
                    stmtInfo.methodChainLength));
        }

        if (stmtInfo.nestingDepth >= 3) {
            factors.add(String.format("it's nested %d levels deep",
                    stmtInfo.nestingDepth));
        }

        if (factors.isEmpty()) {
            return "Statement is within acceptable limits.";
        }

        reason.append(String.join(", ", factors));
        reason.append(String.format(". Overall complexity score: %.2f. Long statements can reduce code readability, increase bug risk, and make maintenance more difficult.", score));

        return reason.toString();
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
        boolean containsAnonymousClass = false;
        boolean isLambdaOrMethodRef = false;
        String content; // Store the actual content for pattern matching
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
            Expression expr = n.getExpression();
            String content = expr.toString();
            int lineNumber = n.getBegin().map(pos -> pos.line).orElse(0);

            // Check if this expression contains an anonymous class or lambda
            boolean hasAnonymousClass = checkForAnonymousClass(expr, content);
            boolean isLambda = expr instanceof LambdaExpr;
            boolean isMethodRef = expr instanceof MethodReferenceExpr;

            // If it's an anonymous class or lambda, we should analyze it differently
            if (hasAnonymousClass || isLambda || isMethodRef) {
                StatementInfo info = createStatementInfo(
                        hasAnonymousClass ? "ExpressionWithAnonymousClass" :
                                isLambda ? "LambdaExpr" : "MethodReference",
                        content, lineNumber);
                info.containsAnonymousClass = hasAnonymousClass;
                info.isLambdaOrMethodRef = isLambda || isMethodRef;
                longStatements.add(info);
            } else {
                // Regular expression - analyze normally
                StatementInfo info = createStatementInfo("ExpressionStmt", content, lineNumber);
                longStatements.add(info);
            }

            super.visit(n, arg);
        }

        private boolean checkForAnonymousClass(Expression expr, String content) {
            // First check AST structure
            if (expr instanceof ObjectCreationExpr) {
                ObjectCreationExpr objectCreation = (ObjectCreationExpr) expr;
                return objectCreation.getAnonymousClassBody().isPresent();
            } else if (expr instanceof MethodCallExpr) {
                // Check if any argument is an anonymous class
                MethodCallExpr methodCall = (MethodCallExpr) expr;
                for (Expression arg : methodCall.getArguments()) {
                    if (arg instanceof ObjectCreationExpr) {
                        ObjectCreationExpr objectCreation = (ObjectCreationExpr) arg;
                        if (objectCreation.getAnonymousClassBody().isPresent()) {
                            return true;
                        }
                    }
                }
            }

            // String-based detection as fallback
            String normalized = content.trim();

            // Look for anonymous class patterns
            // Pattern 1: "new SomeClass() {"
            if (normalized.matches(".*new\\s+[A-Za-z_$][A-Za-z0-9_$]*\\s*\\([^)]*\\)\\s*\\{.*")) {
                return true;
            }

            // Pattern 2: Common anonymous class names in Android
            for (String pattern : ANONYMOUS_PATTERNS) {
                if (normalized.contains("new " + pattern + "(") &&
                        normalized.contains("{")) {
                    return true;
                }
            }

            // Pattern 3: Contains @Override annotation (common in anonymous classes)
            if (normalized.contains("@Override") && normalized.contains("new ")) {
                return true;
            }

            // Pattern 4: Very long content with method definitions (likely anonymous class)
            if (content.length() > 1000 &&
                    (content.contains("public void ") || content.contains("protected void ") ||
                            content.contains("private void "))) {
                return true;
            }

            return false;
        }

        @Override
        public void visit(ObjectCreationExpr n, Void arg) {
            String content = n.toString();
            int lineNumber = n.getBegin().map(pos -> pos.line).orElse(0);

            if (n.getAnonymousClassBody().isPresent()) {
                // For anonymous classes, analyze only the creation part
                String creationPart = n.getType() + " new " + n.getType() + n.getArguments().toString();
                StatementInfo info = createStatementInfo("AnonymousClassCreation", creationPart, lineNumber);
                info.containsAnonymousClass = true;
                info.content = content;
                longStatements.add(info);
            } else {
                // Regular object creation
                StatementInfo info = createStatementInfo("ObjectCreation", content, lineNumber);
                longStatements.add(info);
            }

            super.visit(n, arg);
        }

        @Override
        public void visit(LambdaExpr n, Void arg) {
            StatementInfo info = createStatementInfo("LambdaExpr", n.toString(),
                    n.getBegin().map(pos -> pos.line).orElse(0));
            info.isLambdaOrMethodRef = true;
            longStatements.add(info);
            super.visit(n, arg);
        }

        @Override
        public void visit(MethodReferenceExpr n, Void arg) {
            StatementInfo info = createStatementInfo("MethodReference", n.toString(),
                    n.getBegin().map(pos -> pos.line).orElse(0));
            info.isLambdaOrMethodRef = true;
            longStatements.add(info);
            super.visit(n, arg);
        }

        @Override
        public void visit(IfStmt n, Void arg) {
            nestingDepth++;
            if (n.getCondition() != null) {
                analyzeExpression("If condition", n.getCondition(),
                        n.getBegin().map(pos -> pos.line).orElse(0));
            }
            super.visit(n, arg);
            nestingDepth--;
        }

        @Override
        public void visit(ForStmt n, Void arg) {
            nestingDepth++;
            n.getInitialization().forEach(init ->
                    analyzeExpression("For init", init, n.getBegin().map(pos -> pos.line).orElse(0)));
            n.getCompare().ifPresent(cond ->
                    analyzeExpression("For condition", cond, n.getBegin().map(pos -> pos.line).orElse(0)));
            n.getUpdate().forEach(update ->
                    analyzeExpression("For update", update, n.getBegin().map(pos -> pos.line).orElse(0)));
            super.visit(n, arg);
            nestingDepth--;
        }

        @Override
        public void visit(WhileStmt n, Void arg) {
            nestingDepth++;
            if (n.getCondition() != null) {
                analyzeExpression("While condition", n.getCondition(),
                        n.getBegin().map(pos -> pos.line).orElse(0));
            }
            super.visit(n, arg);
            nestingDepth--;
        }

        @Override
        public void visit(DoStmt n, Void arg) {
            nestingDepth++;
            if (n.getCondition() != null) {
                analyzeExpression("Do-while condition", n.getCondition(),
                        n.getBegin().map(pos -> pos.line).orElse(0));
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

        @Override
        public void visit(ReturnStmt n, Void arg) {
            if (n.getExpression().isPresent()) {
                analyzeExpression("Return", n.getExpression().get(),
                        n.getBegin().map(pos -> pos.line).orElse(0));
            }
            super.visit(n, arg);
        }

        @Override
        public void visit(VariableDeclarationExpr n, Void arg) {
            analyzeExpression("Variable declaration", n,
                    n.getBegin().map(pos -> pos.line).orElse(0));
            super.visit(n, arg);
        }

        @Override
        public void visit(SwitchStmt n, Void arg) {
            nestingDepth++;
            if (n.getSelector() != null) {
                analyzeExpression("Switch selector", n.getSelector(),
                        n.getBegin().map(pos -> pos.line).orElse(0));
            }
            super.visit(n, arg);
            nestingDepth--;
        }

        @Override
        public void visit(SynchronizedStmt n, Void arg) {
            nestingDepth++;
            if (n.getExpression() != null) {
                analyzeExpression("Synchronized expression", n.getExpression(),
                        n.getBegin().map(pos -> pos.line).orElse(0));
            }
            super.visit(n, arg);
            nestingDepth--;
        }

        private void analyzeExpression(String type, Expression expr, int lineNumber) {
            String content = expr.toString().trim();

            // Skip expressions that are clearly anonymous classes or lambdas
            if (checkForAnonymousClass(expr, content) ||
                    expr instanceof LambdaExpr ||
                    expr instanceof MethodReferenceExpr) {
                return; // These are handled separately
            }

            StatementInfo info = createStatementInfo(type, content, lineNumber);
            longStatements.add(info);
        }

        private StatementInfo createStatementInfo(String type, String content, int lineNumber) {
            StatementInfo info = new StatementInfo();
            info.fileName = fileName;
            info.type = type;
            info.lineNumber = lineNumber;
            info.charLength = content.length();
            info.content = content;

            // Don't calculate metrics for very long anonymous-looking content
            if (content.length() > 1000 &&
                    (content.contains("public void ") || content.contains("@Override"))) {
                info.tokenCount = 0;
                info.expressionComplexity = 0;
                info.methodChainLength = 0;
            } else {
                info.tokenCount = countTokens(content);
                info.expressionComplexity = calculateExpressionComplexity(content);
                info.methodChainLength = countMethodChain(content);
            }

            info.nestingDepth = nestingDepth;
            return info;
        }

        private int countTokens(String content) {
            // Remove string literals first as they can contain punctuation
            String withoutLiterals = content.replaceAll("\"[^\"]*\"", "STRING");

            // Split by operators, whitespace, and meaningful delimiters
            String[] tokens = withoutLiterals.split("[\\s+\\-*/=<>!&|(){}\\[\\],;.:]+");

            int count = 0;
            for (String token : tokens) {
                String trimmed = token.trim();
                if (!trimmed.isEmpty() && trimmed.length() > 0) {
                    // Don't count punctuation characters as separate tokens
                    if (!trimmed.matches("^[+\\-*/=<>!&|(){}\\[\\],;.:]$")) {
                        count++;
                    }
                }
            }
            return count;
        }

        private int calculateExpressionComplexity(String content) {
            // Skip for very long content (likely anonymous classes)
            if (content.length() > 500) {
                return 0;
            }

            int complexity = 0;
            String withoutLiterals = content.replaceAll("\"[^\"]*\"", "");

            // Count operators with weights
            complexity += countOccurrences(withoutLiterals, "[+\\-*/%]") * 1; // Arithmetic
            complexity += countOccurrences(withoutLiterals, "[=<>!]=?|&&|\\|\\|") * 2; // Comparison/logical
            complexity += countOccurrences(withoutLiterals, "\\?|:") * 3; // Ternary

            // Count parentheses
            complexity += countOccurrences(withoutLiterals, "\\(") * 2;

            // Count method calls (excluding patterns that look like anonymous classes)
            if (!content.contains("new ") || !content.contains("{")) {
                complexity += countOccurrences(withoutLiterals, "\\.[a-zA-Z_][a-zA-Z0-9_]*\\(") * 2;
            }

            // Count array/list accesses
            complexity += countOccurrences(withoutLiterals, "\\[.*?\\]") * 2;

            // Calculate nesting depth
            int maxNesting = 0;
            int currentNesting = 0;
            for (char c : withoutLiterals.toCharArray()) {
                if (c == '(' || c == '[' || c == '{') {
                    currentNesting++;
                    maxNesting = Math.max(maxNesting, currentNesting);
                } else if (c == ')' || c == ']' || c == '}') {
                    currentNesting--;
                }
            }
            complexity += maxNesting * 3;

            return complexity;
        }

        private int countMethodChain(String content) {
            // Skip if this looks like an anonymous class creation
            if (content.contains("new ") && content.contains("{")) {
                return 0;
            }

            // Count consecutive method calls separated by dots
            int maxChain = 0;
            int currentChain = 0;

            // Simplified detection: look for patterns like .method()
            String[] parts = content.split("\\.");
            for (String part : parts) {
                if (part.matches(".*\\([^)]*\\).*")) {
                    currentChain++;
                    maxChain = Math.max(maxChain, currentChain);
                } else {
                    currentChain = 0;
                }
            }

            return maxChain;
        }

        private int countOccurrences(String content, String regex) {
            try {
                return Math.max(0, content.split(regex).length - 1);
            } catch (Exception e) {
                return 0;
            }
        }
    }
}