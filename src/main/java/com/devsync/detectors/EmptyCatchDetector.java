package com.devsync.detectors;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import java.util.*;
import java.util.stream.Collectors;

public class EmptyCatchDetector {
    
    private static final Set<String> CRITICAL_EXCEPTIONS = Set.of(
        "SecurityException", "IOException", "SQLException", "ClassNotFoundException",
        "IllegalArgumentException", "NullPointerException", "OutOfMemoryError"
    );
    
    private static final Set<String> ACCEPTABLE_EMPTY_PATTERNS = Set.of(
        "ignore", "expected", "intentional", "suppress", "todo", "fixme"
    );
    
    private static final Map<String, Double> EXCEPTION_SEVERITY_WEIGHTS = Map.of(
        "SecurityException", 1.0,
        "IOException", 0.8,
        "SQLException", 0.9,
        "RuntimeException", 0.7,
        "Exception", 0.6,
        "Throwable", 0.5
    );

    public List<String> detect(CompilationUnit cu) {
        List<String> issues = new ArrayList<>();
        
        ExceptionAnalyzer analyzer = new ExceptionAnalyzer();
        cu.accept(analyzer, null);
        
        analyzer.getProblematicCatches().forEach(catchInfo -> {
            double riskScore = calculateRiskScore(catchInfo);
            
            if (riskScore > 0.3) {
                String severity = getSeverity(riskScore, catchInfo.exceptionType);
                String analysis = generateAnalysis(catchInfo);
                String suggestions = generateSuggestions(catchInfo);
                
                issues.add(String.format(
                    "%s [EmptyCatch] %s:%d - %s [Risk: %.2f] - %s | Suggestions: %s",
                    severity,
                    cu.getStorage().map(s -> s.getFileName()).orElse("UnknownFile"),
                    catchInfo.lineNumber,
                    catchInfo.exceptionType,
                    riskScore,
                    analysis,
                    suggestions
                ));
            }
        });
        
        return issues;
    }
    
    private double calculateRiskScore(CatchInfo catchInfo) {
        double baseScore = 0.5;
        
        double severityWeight = EXCEPTION_SEVERITY_WEIGHTS.getOrDefault(
            catchInfo.exceptionType, 0.4
        );
        
        double contextScore = analyzeContext(catchInfo);
        double documentationScore = analyzeDocumentation(catchInfo);
        double methodScore = analyzeMethodCriticality(catchInfo);
        
        return Math.min(1.0, baseScore * severityWeight + contextScore + methodScore - documentationScore);
    }
    
    private double analyzeContext(CatchInfo catchInfo) {
        double contextScore = 0.0;
        
        if (isCriticalMethod(catchInfo.methodName)) {
            contextScore += 0.3;
        }
        
        if (catchInfo.hasResourceManagement) {
            contextScore += 0.2;
        }
        
        if (catchInfo.isInLoop) {
            contextScore += 0.25;
        }
        
        if (catchInfo.multipleCatchBlocks > 1) {
            contextScore += 0.1;
        }
        
        return Math.min(0.5, contextScore);
    }
    
    private double analyzeDocumentation(CatchInfo catchInfo) {
        double docScore = 0.0;
        
        if (catchInfo.hasExplanatoryComment) {
            docScore += 0.3;
        }
        
        if (hasAcceptablePattern(catchInfo.comments)) {
            docScore += 0.2;
        }
        
        return Math.min(0.4, docScore);
    }
    
    private double analyzeMethodCriticality(CatchInfo catchInfo) {
        double methodScore = 0.0;
        
        String methodName = catchInfo.methodName.toLowerCase();
        
        if (methodName.contains("auth") || methodName.contains("security") || 
            methodName.contains("login") || methodName.contains("password")) {
            methodScore += 0.3;
        }
        
        if (methodName.contains("save") || methodName.contains("persist") || 
            methodName.contains("update") || methodName.contains("delete")) {
            methodScore += 0.25;
        }
        
        if (methodName.contains("connect") || methodName.contains("send") || 
            methodName.contains("receive") || methodName.contains("transfer")) {
            methodScore += 0.2;
        }
        
        return Math.min(0.3, methodScore);
    }
    
    private boolean isCriticalMethod(String methodName) {
        String lower = methodName.toLowerCase();
        return lower.contains("main") || lower.contains("init") || 
               lower.contains("setup") || lower.contains("config");
    }
    
    private boolean hasAcceptablePattern(List<String> comments) {
        return comments.stream()
            .anyMatch(comment -> ACCEPTABLE_EMPTY_PATTERNS.stream()
                .anyMatch(pattern -> comment.toLowerCase().contains(pattern)));
    }
    
    private String getSeverity(double riskScore, String exceptionType) {
        if (CRITICAL_EXCEPTIONS.contains(exceptionType) || riskScore > 0.8) {
            return "🔴";
        }
        if (riskScore > 0.6) {
            return "🟡";
        }
        return "🟠";
    }
    
    private String generateAnalysis(CatchInfo catchInfo) {
        List<String> analysis = new ArrayList<>();
        
        if (CRITICAL_EXCEPTIONS.contains(catchInfo.exceptionType)) {
            analysis.add("Critical exception type");
        }
        
        if (catchInfo.isInLoop) {
            analysis.add("In loop context");
        }
        
        if (catchInfo.hasResourceManagement) {
            analysis.add("Resource management risk");
        }
        
        if (!catchInfo.hasExplanatoryComment) {
            analysis.add("No documentation");
        }
        
        return analysis.isEmpty() ? "Empty catch block" : String.join(", ", analysis);
    }
    
    private String generateSuggestions(CatchInfo catchInfo) {
        List<String> suggestions = new ArrayList<>();
        
        if (CRITICAL_EXCEPTIONS.contains(catchInfo.exceptionType)) {
            suggestions.add("Log and handle appropriately");
        } else {
            suggestions.add("Add logging or re-throw");
        }
        
        if (!catchInfo.hasExplanatoryComment) {
            suggestions.add("Add explanatory comment");
        }
        
        if (catchInfo.hasResourceManagement) {
            suggestions.add("Use try-with-resources");
        }
        
        if (catchInfo.isInLoop) {
            suggestions.add("Consider breaking loop on critical errors");
        }
        
        return String.join(", ", suggestions);
    }
    
    private static class CatchInfo {
        String exceptionType;
        int lineNumber;
        String methodName;
        boolean isEmpty;
        boolean hasExplanatoryComment;
        boolean isInLoop;
        boolean hasResourceManagement;
        int multipleCatchBlocks;
        List<String> comments;
        
        CatchInfo() {
            this.comments = new ArrayList<>();
        }
    }
    
    private static class ExceptionAnalyzer extends VoidVisitorAdapter<Void> {
        private final List<CatchInfo> problematicCatches = new ArrayList<>();
        private String currentMethodName = "";
        private boolean inLoop = false;
        
        public List<CatchInfo> getProblematicCatches() {
            return problematicCatches;
        }
        
        @Override
        public void visit(MethodDeclaration n, Void arg) {
            currentMethodName = n.getNameAsString();
            super.visit(n, arg);
        }
        
        @Override
        public void visit(ForStmt n, Void arg) {
            boolean wasInLoop = inLoop;
            inLoop = true;
            super.visit(n, arg);
            inLoop = wasInLoop;
        }
        
        @Override
        public void visit(WhileStmt n, Void arg) {
            boolean wasInLoop = inLoop;
            inLoop = true;
            super.visit(n, arg);
            inLoop = wasInLoop;
        }
        
        @Override
        public void visit(ForEachStmt n, Void arg) {
            boolean wasInLoop = inLoop;
            inLoop = true;
            super.visit(n, arg);
            inLoop = wasInLoop;
        }
        
        @Override
        public void visit(TryStmt n, Void arg) {
            int catchCount = n.getCatchClauses().size();
            
            n.getCatchClauses().forEach(catchClause -> {
                CatchInfo info = new CatchInfo();
                info.exceptionType = catchClause.getParameter().getType().asString();
                info.lineNumber = catchClause.getBegin().map(p -> p.line).orElse(-1);
                info.methodName = currentMethodName;
                info.isEmpty = !catchClause.getBody().getStatements().isNonEmpty();
                info.isInLoop = inLoop;
                info.multipleCatchBlocks = catchCount;
                info.hasResourceManagement = hasResourceManagement(n);
                
                analyzeComments(catchClause, info);
                
                if (info.isEmpty || isProblematicCatch(catchClause)) {
                    problematicCatches.add(info);
                }
            });
            
            super.visit(n, arg);
        }
        
        private boolean hasResourceManagement(TryStmt tryStmt) {
            return tryStmt.getResources().isNonEmpty() || 
                   tryStmt.getTryBlock().toString().contains("close()") ||
                   tryStmt.getTryBlock().toString().contains("FileInputStream") ||
                   tryStmt.getTryBlock().toString().contains("Connection");
        }
        
        private void analyzeComments(CatchClause catchClause, CatchInfo info) {
            catchClause.getAllContainedComments().forEach(comment -> {
                String commentText = comment.getContent().toLowerCase();
                info.comments.add(commentText);
                
                if (commentText.length() > 10) {
                    info.hasExplanatoryComment = true;
                }
            });
            
            catchClause.getComment().ifPresent(comment -> {
                info.comments.add(comment.getContent().toLowerCase());
                info.hasExplanatoryComment = true;
            });
        }
        
        private boolean isProblematicCatch(CatchClause catchClause) {
            String bodyContent = catchClause.getBody().toString().toLowerCase();
            
            return bodyContent.contains("printstacktrace") && bodyContent.length() < 50 ||
                   bodyContent.contains("return null") ||
                   bodyContent.contains("return false") && bodyContent.length() < 30;
        }
    }
}

