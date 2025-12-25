package com.devsync.detectors;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import java.util.*;

public class UnusedVariableDetector {

    public List<String> detect(CompilationUnit cu) {
        List<String> issues = new ArrayList<>();
        VariableAnalyzer analyzer = new VariableAnalyzer();
        cu.accept(analyzer, null);

        for (VariableInfo varInfo : analyzer.getUnusedVariables()) {
            double riskScore = calculateRiskScore(varInfo);
            String severity = getSeverity(riskScore);

            issues.add(String.format(
                "%s [UnusedVariable] %s:%d - Variable '%s' declared but never used in %s - %s | Suggestions: %s | DetailedReason: %s",
                severity,
                varInfo.fileName,
                varInfo.lineNumber,
                varInfo.variableName,
                varInfo.context,
                generateAnalysis(varInfo),
                generateSuggestions(varInfo),
                generateDetailedReason(varInfo)
            ));
        }

        return issues;
    }

    private double calculateRiskScore(VariableInfo varInfo) {
        double score = 0.5;
        
        if (varInfo.isParameter) score += 0.2;
        if (varInfo.isInPublicMethod) score += 0.1;
        if (varInfo.hasInitializer) score += 0.1;
        
        return Math.min(1.0, score);
    }

    private String getSeverity(double score) {
        if (score >= 0.7) return "🟡";
        return "🟠";
    }

    private String generateAnalysis(VariableInfo varInfo) {
        if (varInfo.isParameter) return "Unused method parameter";
        if (varInfo.hasInitializer) return "Variable initialized but never used";
        return "Dead code - variable declared but never referenced";
    }

    private String generateSuggestions(VariableInfo varInfo) {
        if (varInfo.isParameter) return "Remove parameter or prefix with underscore if intentionally unused";
        return "Remove unused variable declaration to improve code clarity";
    }

    private String generateDetailedReason(VariableInfo varInfo) {
        StringBuilder reason = new StringBuilder();
        reason.append("This variable is flagged because: ");
        
        List<String> issues = new ArrayList<>();
        issues.add(String.format("variable '%s' is declared", varInfo.variableName));
        
        if (varInfo.hasInitializer) {
            issues.add("it has an initializer value that is computed but never used");
        }
        
        if (varInfo.isParameter) {
            issues.add("it's a method parameter that is never referenced in the method body");
        }
        
        issues.add("it is never read or referenced anywhere in its scope");
        
        reason.append(String.join(", ", issues));
        reason.append(". Unused variables waste memory, reduce code readability, and may indicate incomplete implementation or refactoring artifacts.");
        
        return reason.toString();
    }

    private static class VariableInfo {
        String fileName;
        String variableName;
        int lineNumber;
        String context;
        boolean isParameter;
        boolean isInPublicMethod;
        boolean hasInitializer;
        String methodName;
    }

    private static class VariableAnalyzer extends VoidVisitorAdapter<Void> {
        private final List<VariableInfo> unusedVariables = new ArrayList<>();
        private String fileName = "";
        private String currentMethodName = "";
        private boolean inPublicMethod = false;

        public List<VariableInfo> getUnusedVariables() {
            return unusedVariables;
        }

        @Override
        public void visit(CompilationUnit n, Void arg) {
            fileName = n.getStorage().map(s -> s.getFileName()).orElse("UnknownFile");
            super.visit(n, arg);
        }

        @Override
        public void visit(MethodDeclaration n, Void arg) {
            currentMethodName = n.getNameAsString();
            inPublicMethod = n.isPublic();
            
            Map<String, VariableDeclarator> varDeclarators = new HashMap<>();
            Set<String> paramNames = new HashSet<>();
            Set<String> usedNames = new HashSet<>();

            // Collect all variable declarations
            n.findAll(VariableDeclarator.class).forEach(vd -> {
                varDeclarators.put(vd.getNameAsString(), vd);
            });

            // Collect all parameters
            n.getParameters().forEach(param -> {
                paramNames.add(param.getNameAsString());
            });

            // Collect all name usages (excluding the declaration itself)
            n.findAll(NameExpr.class).forEach(nameExpr -> {
                String name = nameExpr.getNameAsString();
                // Check if this NameExpr is NOT part of a variable declaration
                boolean isDeclaration = nameExpr.getParentNode()
                    .filter(parent -> parent instanceof VariableDeclarator)
                    .map(parent -> {
                        VariableDeclarator vd = (VariableDeclarator) parent;
                        return vd.getName().equals(nameExpr.getName());
                    })
                    .orElse(false);
                
                if (!isDeclaration) {
                    usedNames.add(name);
                }
            });

            // Check for unused local variables
            for (Map.Entry<String, VariableDeclarator> entry : varDeclarators.entrySet()) {
                String varName = entry.getKey();
                if (!usedNames.contains(varName)) {
                    VariableDeclarator vd = entry.getValue();
                    VariableInfo info = new VariableInfo();
                    info.fileName = fileName;
                    info.variableName = varName;
                    info.lineNumber = vd.getBegin().map(pos -> pos.line).orElse(0);
                    info.context = "method " + currentMethodName;
                    info.isInPublicMethod = inPublicMethod;
                    info.methodName = currentMethodName;
                    info.isParameter = false;
                    info.hasInitializer = vd.getInitializer().isPresent();
                    unusedVariables.add(info);
                }
            }

            // Check for unused parameters
            for (String paramName : paramNames) {
                if (!usedNames.contains(paramName)) {
                    VariableInfo info = new VariableInfo();
                    info.fileName = fileName;
                    info.variableName = paramName;
                    info.lineNumber = n.getBegin().map(pos -> pos.line).orElse(0);
                    info.context = "method " + currentMethodName;
                    info.isInPublicMethod = inPublicMethod;
                    info.methodName = currentMethodName;
                    info.isParameter = true;
                    info.hasInitializer = false;
                    unusedVariables.add(info);
                }
            }

            super.visit(n, arg);
        }
    }
}
