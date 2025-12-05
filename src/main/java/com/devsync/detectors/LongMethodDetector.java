package com.devsync.detectors;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import java.util.*;

public class LongMethodDetector {

    private int baseLineThreshold = 35;
    private int criticalLineThreshold = 50;

    private static final int MAX_CYCLOMATIC_COMPLEXITY = 10;
    private static final int MAX_COGNITIVE_COMPLEXITY = 15;
    private static final int MAX_NESTING_DEPTH = 4;

    public void setMaxLength(int maxLength) {
        this.baseLineThreshold = Math.max(10, maxLength / 2);
        this.criticalLineThreshold = maxLength;
    }

    private static final Map<String, Double> METHOD_TYPE_WEIGHTS = Map.of(
            "getter", 0.3,
            "setter", 0.3,
            "constructor", 0.7,
            "main", 0.8,
            "test", 0.6,
            "utility", 0.5,
            "business", 1.0
    );

    public List<String> detect(CompilationUnit cu) {
        List<String> issues = new ArrayList<>();
        Set<String> processed = new HashSet<>();

        MethodAnalyzer analyzer = new MethodAnalyzer();
        cu.accept(analyzer, null);

        for (MethodInfo m : analyzer.getLongMethods()) {
            String key = m.fileName + ":" + m.methodName + ":" + m.lineNumber;
            if (processed.contains(key)) continue;
            processed.add(key);

            // THRESHOLD CHECK FIRST - binary detection
            if (m.lineCount < baseLineThreshold && 
                m.cyclomaticComplexity <= MAX_CYCLOMATIC_COMPLEXITY && 
                m.cognitiveComplexity <= MAX_COGNITIVE_COMPLEXITY && 
                m.nestingDepth <= MAX_NESTING_DEPTH) {
                continue; // NO SMELL - exit immediately
            }

            // THRESHOLD EXCEEDED - now calculate score for severity only
            double score = calculateScore(m);
            String severity = getSeverity(score);

            issues.add(String.format(
                    "%s [LongMethod] %s:%d - '%s' (%d statements) - %s | Suggestions: %s | DetailedReason: %s",
                    severity,
                    m.fileName,
                    m.lineNumber,
                    m.methodName,
                    m.lineCount,
                    generateAnalysis(m),
                    generateSuggestions(m),
                    generateDetailedReason(m)
            ));
        }

        return issues;
    }

    private double calculateScore(MethodInfo m) {

        double lineScore = Math.min(1.0, (double) m.lineCount / criticalLineThreshold);
        double complexityScore = Math.min(1.0, (double) m.cyclomaticComplexity / MAX_CYCLOMATIC_COMPLEXITY);
        double cognitiveScore = Math.min(1.0, (double) m.cognitiveComplexity / MAX_COGNITIVE_COMPLEXITY);
        double responsibilityScore = Math.min(1.0, (double) m.responsibilityCount / 3);
        double nestingScore = Math.min(1.0, (double) m.nestingDepth / MAX_NESTING_DEPTH);

        String type = determineMethodType(m);
        double weight = METHOD_TYPE_WEIGHTS.getOrDefault(type, 1.0);

        return weight * (
                lineScore * 0.35 +
                        complexityScore * 0.25 +
                        cognitiveScore * 0.20 +
                        nestingScore * 0.10 +
                        responsibilityScore * 0.10
        );
    }



    private String determineMethodType(MethodInfo m) {
        String name = m.methodName.toLowerCase();

        if (name.startsWith("get") && m.parameterCount == 0) return "getter";
        if (name.startsWith("set") && m.parameterCount == 1) return "setter";
        if (name.equals(m.className.toLowerCase())) return "constructor";
        if (name.equals("main")) return "main";
        if (name.startsWith("test")) return "test";
        if (m.isStatic && m.isPublic) return "utility";

        return "business";
    }

    private String getSeverity(double s) {
        if (s >= 0.8) return "🔴";
        if (s >= 0.5) return "🟡";
        return "🟠";
    }

    private String generateAnalysis(MethodInfo m) {
        if (m.lineCount > criticalLineThreshold) return "Extremely long method";
        if (m.cyclomaticComplexity > MAX_CYCLOMATIC_COMPLEXITY) return "High logical complexity";
        if (m.cognitiveComplexity > MAX_COGNITIVE_COMPLEXITY) return "Too many nested branches";
        return "Method too long";
    }

    private String generateSuggestions(MethodInfo m) {
        return "Split logic into smaller methods, reduce branching, simplify nested blocks";
    }

    private String generateDetailedReason(MethodInfo m) {
        StringBuilder reason = new StringBuilder();
        reason.append("This method was flagged because: ");
        
        List<String> violations = new ArrayList<>();
        
        // Check line count
        if (m.lineCount > criticalLineThreshold) {
            violations.add(String.format("Statement count is %d (exceeds critical threshold of %d)", m.lineCount, criticalLineThreshold));
        } else if (m.lineCount > baseLineThreshold) {
            violations.add(String.format("Statement count is %d (exceeds base threshold of %d)", m.lineCount, baseLineThreshold));
        } else {
            violations.add(String.format("Statement count is %d (within threshold of %d)", m.lineCount, baseLineThreshold));
        }
        
        // Check cyclomatic complexity
        if (m.cyclomaticComplexity > MAX_CYCLOMATIC_COMPLEXITY) {
            violations.add(String.format("Cyclomatic complexity is %d (exceeds max of %d - too many decision points like if/for/while)", m.cyclomaticComplexity, MAX_CYCLOMATIC_COMPLEXITY));
        } else {
            violations.add(String.format("Cyclomatic complexity is %d (within max of %d)", m.cyclomaticComplexity, MAX_CYCLOMATIC_COMPLEXITY));
        }
        
        // Check cognitive complexity
        if (m.cognitiveComplexity > MAX_COGNITIVE_COMPLEXITY) {
            violations.add(String.format("Cognitive complexity is %d (exceeds max of %d - too hard to understand)", m.cognitiveComplexity, MAX_COGNITIVE_COMPLEXITY));
        } else {
            violations.add(String.format("Cognitive complexity is %d (within max of %d)", m.cognitiveComplexity, MAX_COGNITIVE_COMPLEXITY));
        }
        
        // Check nesting depth
        if (m.nestingDepth > MAX_NESTING_DEPTH) {
            violations.add(String.format("Nesting depth is %d levels (exceeds max of %d - deeply nested code is hard to follow)", m.nestingDepth, MAX_NESTING_DEPTH));
        } else {
            violations.add(String.format("Nesting depth is %d levels (within max of %d)", m.nestingDepth, MAX_NESTING_DEPTH));
        }
        
        // Check responsibilities
        if (m.responsibilityCount > 3) {
            violations.add(String.format("Handles %d different responsibilities (exceeds max of 3 - violates Single Responsibility Principle)", m.responsibilityCount));
        } else {
            violations.add(String.format("Handles %d responsibilities (within max of 3)", m.responsibilityCount));
        }
        
        reason.append(String.join("; ", violations));
        reason.append(". A method is flagged when ANY of these thresholds is exceeded.");
        
        return reason.toString();
    }

    /*───────────────────────────────────────────────────────────────*/
    /*                          Method Analyzer                      */
    /*───────────────────────────────────────────────────────────────*/

    private static class MethodInfo {
        String fileName;
        String methodName;
        String className;
        int lineNumber;
        int lineCount; // AST-based # of statements
        int cyclomaticComplexity;
        int cognitiveComplexity;
        int responsibilityCount;
        int nestingDepth;
        boolean isStatic;
        boolean isPublic;
        int parameterCount;
    }

    private static class MethodAnalyzer extends VoidVisitorAdapter<Void> {
        private final List<MethodInfo> methods = new ArrayList<>();
        private String fileName = "";
        private String className = "UnknownClass";

        public List<MethodInfo> getLongMethods() { return methods; }

        @Override
        public void visit(CompilationUnit n, Void arg) {
            fileName = n.getStorage().map(s -> s.getFileName()).orElse("UnknownFile");
            super.visit(n, arg);
        }

        @Override
        public void visit(MethodDeclaration n, Void arg) {
            MethodInfo info = new MethodInfo();

            info.fileName = fileName;
            info.className = className;
            info.methodName = n.getNameAsString();
            info.lineNumber = n.getBegin().map(p -> p.line).orElse(0);

            info.lineCount = n.findAll(Statement.class).size(); // FIXED STATEMENT COUNT

            info.cyclomaticComplexity = getCyclomatic(n);
            info.cognitiveComplexity = getCognitive(n);
            info.nestingDepth = getNestingDepth(n);
            info.responsibilityCount = getResponsibilities(n);

            info.isStatic = n.isStatic();
            info.isPublic = n.isPublic();
            info.parameterCount = n.getParameters().size();

            methods.add(info);

            super.visit(n, arg);
        }

        private int getCyclomatic(MethodDeclaration n) {
            ComplexityCalculator c = new ComplexityCalculator();
            n.accept(c, null);
            return c.getComplexity();
        }

        private int getCognitive(MethodDeclaration n) {
            CognitiveComplexityCalculator c = new CognitiveComplexityCalculator();
            n.accept(c, null);
            return c.getComplexity();
        }

        private int getNestingDepth(MethodDeclaration n) {
            NestingCalculator c = new NestingCalculator();
            n.accept(c, null);
            return c.getMaxDepth();
        }

        private int getResponsibilities(MethodDeclaration method) {
            Set<String> types = new HashSet<>();

            method.findAll(MethodCallExpr.class).forEach(call -> {
                String name = call.getNameAsString().toLowerCase();

                if (name.matches(".*save.*|.*store.*|.*persist.*")) types.add("persistence");
                if (name.matches(".*validate.*|.*check.*|.*verify.*")) types.add("validation");
                if (name.matches(".*log.*|.*print.*")) types.add("logging");
                if (name.matches(".*calculate.*|.*compute.*|.*process.*")) types.add("compute");
                if (name.matches(".*format.*|.*convert.*|.*transform.*")) types.add("transform");
                if (name.matches(".*send.*|.*connect.*|.*request.*")) types.add("network");
            });

            return types.size();
        }
    }

    /*───────────────────────────────────────────────────────────────*/
    /*                      Complexity Calculators                    */
    /*───────────────────────────────────────────────────────────────*/

    private static class ComplexityCalculator extends VoidVisitorAdapter<Void> {
        private int complexity = 1;
        public int getComplexity() { return complexity; }

        @Override public void visit(IfStmt n, Void arg) { complexity++; super.visit(n,arg); }
        @Override public void visit(ForStmt n, Void arg) { complexity++; super.visit(n,arg); }
        @Override public void visit(WhileStmt n, Void arg) { complexity++; super.visit(n,arg); }
        @Override public void visit(DoStmt n, Void arg) { complexity++; super.visit(n,arg); }
        @Override public void visit(SwitchEntry n, Void arg) { if (!n.getLabels().isEmpty()) complexity++; super.visit(n,arg); }
        @Override public void visit(ConditionalExpr n, Void arg) { complexity++; super.visit(n,arg); }

        @Override
        public void visit(BinaryExpr n, Void arg) {
            if (n.getOperator() == BinaryExpr.Operator.AND ||
                    n.getOperator() == BinaryExpr.Operator.OR) {
                complexity++;
            }
            super.visit(n, arg);
        }
    }

    private static class CognitiveComplexityCalculator extends VoidVisitorAdapter<Void> {
        private int complexity = 0;
        private int nesting = 0;

        public int getComplexity() { return complexity; }

        private void nested(Runnable r) {
            nesting++;
            r.run();
            nesting--;
        }

        @Override public void visit(IfStmt n, Void arg) { complexity += (1 + nesting); nested(() -> super.visit(n,arg)); }
        @Override public void visit(ForStmt n, Void arg) { complexity += (1 + nesting); nested(() -> super.visit(n,arg)); }
        @Override public void visit(WhileStmt n, Void arg) { complexity += (1 + nesting); nested(() -> super.visit(n,arg)); }
        @Override public void visit(SwitchStmt n, Void arg) { complexity += (1 + nesting); nested(() -> super.visit(n,arg)); }

        @Override
        public void visit(BinaryExpr n, Void arg) {
            if (n.getOperator() == BinaryExpr.Operator.AND ||
                    n.getOperator() == BinaryExpr.Operator.OR) {
                complexity++;
            }
            super.visit(n, arg);
        }
    }

    private static class NestingCalculator extends VoidVisitorAdapter<Void> {
        private int current = 0;
        private int max = 0;

        public int getMaxDepth() { return max; }

        private void nested(Runnable r) {
            current++;
            max = Math.max(max, current);
            r.run();
            current--;
        }

        @Override public void visit(IfStmt n, Void a) { nested(() -> super.visit(n,a)); }
        @Override public void visit(ForStmt n, Void a) { nested(() -> super.visit(n,a)); }
        @Override public void visit(WhileStmt n, Void a) { nested(() -> super.visit(n,a)); }
        @Override public void visit(TryStmt n, Void a) { nested(() -> super.visit(n,a)); }
    }

}
