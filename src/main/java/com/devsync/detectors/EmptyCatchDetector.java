package com.devsync.detectors;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import java.util.*;

public class EmptyCatchDetector {
    
    private static final Set<String> CRITICAL_EXCEPTIONS = Set.of(
        "SecurityException", "IOException", "SQLException", "ClassNotFoundException",
        "IllegalArgumentException", "NullPointerException", "OutOfMemoryError"
    );
    
    private static final Set<String> ACCEPTABLE_PATTERNS = Set.of(
        "ignore", "expected", "intentional", "suppress"
    );

    public List<String> detect(CompilationUnit cu) {
        List<String> issues = new ArrayList<>();
        Set<String> processedLines = new HashSet<>();
        
        EmptyCatchAnalyzer analyzer = new EmptyCatchAnalyzer();
        cu.accept(analyzer, null);
        
        analyzer.getEmptyCatches().forEach(catchInfo -> {
            String lineKey = catchInfo.fileName + ":" + catchInfo.lineNumber;
            if (!processedLines.contains(lineKey)) {
                processedLines.add(lineKey);
                
                double score = calculateScore(catchInfo);
                String severity = getSeverity(score);
                
                issues.add(String.format(
                    "%s [EmptyCatch] %s:%d - Empty catch block for %s - %s | Suggestions: %s | DetailedReason: %s",
                    severity,
                    catchInfo.fileName,
                    catchInfo.lineNumber,
                    catchInfo.exceptionType,
                    generateAnalysis(catchInfo),
                    generateSuggestions(catchInfo),
                    generateDetailedReason(catchInfo)
                ));
            }
        });
        
        return issues;
    }
    
    private double calculateScore(CatchInfo catchInfo) {
        double score = 0.6; // Base score for empty catch
        
        if (CRITICAL_EXCEPTIONS.contains(catchInfo.exceptionType)) {
            score += 0.3;
        }
        
        if (catchInfo.hasComment && hasAcceptablePattern(catchInfo.comment)) {
            score -= 0.2;
        }
        
        return Math.min(1.0, score);
    }
    
    private boolean hasAcceptablePattern(String comment) {
        String lower = comment.toLowerCase();
        return ACCEPTABLE_PATTERNS.stream().anyMatch(lower::contains);
    }
    
    private String getSeverity(double score) {
        if (score >= 0.8) return "🔴";
        if (score >= 0.5) return "🟡";
        return "🟠";
    }
    
    private String generateAnalysis(CatchInfo catchInfo) {
        List<String> issues = new ArrayList<>();
        
        if (CRITICAL_EXCEPTIONS.contains(catchInfo.exceptionType)) {
            issues.add("Critical exception silently ignored");
        } else {
            issues.add("Exception silently ignored");
        }
        
        if (!catchInfo.hasComment) {
            issues.add("No explanation provided");
        }
        
        return String.join(", ", issues);
    }
    
    private String generateSuggestions(CatchInfo catchInfo) {
        return "Add logging, re-throw, or add explanatory comment";
    }
    
    private String generateDetailedReason(CatchInfo catchInfo) {
        StringBuilder reason = new StringBuilder();
        reason.append("This catch block is flagged as a code smell because: ");
        
        List<String> issues = new ArrayList<>();
        
        issues.add("the catch block is completely empty with no error handling");
        
        if (CRITICAL_EXCEPTIONS.contains(catchInfo.exceptionType)) {
            issues.add(String.format("%s is a critical exception that should never be silently ignored", catchInfo.exceptionType));
        } else {
            issues.add(String.format("catching %s without any action hides potential errors", catchInfo.exceptionType));
        }
        
        if (!catchInfo.hasComment) {
            issues.add("there is no comment explaining why the exception is being ignored");
        } else if (!hasAcceptablePattern(catchInfo.comment)) {
            issues.add("the comment does not clearly indicate intentional suppression");
        }
        
        reason.append(String.join(", ", issues));
        reason.append(". Empty catch blocks can hide bugs and make debugging extremely difficult.");
        
        return reason.toString();
    }
    
    private static class CatchInfo {
        String fileName;
        String exceptionType;
        int lineNumber;
        boolean hasComment;
        String comment;
    }
    
    private static class EmptyCatchAnalyzer extends VoidVisitorAdapter<Void> {
        private final List<CatchInfo> emptyCatches = new ArrayList<>();
        private String fileName = "";
        
        public List<CatchInfo> getEmptyCatches() {
            return emptyCatches;
        }
        
        @Override
        public void visit(CompilationUnit n, Void arg) {
            fileName = n.getStorage().map(s -> s.getFileName()).orElse("UnknownFile");
            super.visit(n, arg);
        }
        
        @Override
        public void visit(TryStmt n, Void arg) {
            n.getCatchClauses().forEach(catchClause -> {
                BlockStmt catchBody = catchClause.getBody();
                if (catchBody.getStatements().isEmpty()) {
                    CatchInfo info = new CatchInfo();
                    info.fileName = fileName;
                    info.exceptionType = catchClause.getParameter().getType().asString();
                    info.lineNumber = catchClause.getBegin().map(pos -> pos.line).orElse(0);
                    info.hasComment = catchClause.getComment().isPresent();
                    info.comment = catchClause.getComment().map(c -> c.getContent()).orElse("");
                    
                    emptyCatches.add(info);
                }
            });
            
            super.visit(n, arg);
        }
    }
}