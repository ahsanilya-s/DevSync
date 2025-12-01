package com.devsync.detectors;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import java.util.*;

public class MagicNumberDetector {
    
    private static final Set<String> ACCEPTABLE_NUMBERS = Set.of(
        "0", "1", "-1", "0.0", "1.0", "-1.0", "2", "100", "1000"
    );
    
    private static final Set<String> SAFE_CONTEXTS = Set.of(
        "test", "constant", "final", "static"
    );

    public List<String> detect(CompilationUnit cu) {
        List<String> issues = new ArrayList<>();
        
        MagicNumberAnalyzer analyzer = new MagicNumberAnalyzer();
        cu.accept(analyzer, null);
        
        for (MagicNumberInfo magicInfo : analyzer.getMagicNumbers()) {
            // THRESHOLD CHECK FIRST - binary detection
            if (ACCEPTABLE_NUMBERS.contains(magicInfo.value) || 
                magicInfo.isInTestMethod || 
                magicInfo.isConstant) {
                continue; // NO SMELL - exit immediately
            }
            
            // THRESHOLD EXCEEDED - now calculate score for severity only
            double riskScore = calculateRiskScore(magicInfo);
            String severity = getSeverity(riskScore);
            
            issues.add(String.format(
                "%s [MagicNumber] %s:%d - Magic number '%s' in %s - %s | Suggestions: %s | DetailedReason: %s",
                severity,
                magicInfo.fileName,
                magicInfo.lineNumber,
                magicInfo.value,
                magicInfo.context,
                generateAnalysis(magicInfo),
                generateSuggestions(magicInfo),
                generateDetailedReason(magicInfo, riskScore)
            ));
        }
        
        return issues;
    }
    

    
    private double calculateRiskScore(MagicNumberInfo magicInfo) {
        double score = 0.6;
        
        // Context scoring
        if (magicInfo.isInPublicMethod) score += 0.2;
        if (magicInfo.isInBusinessLogic) score += 0.3;
        if (magicInfo.isRepeated) score += 0.2;
        
        // Value analysis
        try {
            double numValue = Double.parseDouble(magicInfo.value);
            if (Math.abs(numValue) > 1000) score += 0.1;
            if (numValue % 1 != 0 && Math.abs(numValue) > 1) score += 0.1; // Decimal
        } catch (NumberFormatException e) {
            // Ignore
        }
        
        return Math.min(1.0, score);
    }
    
    private String getSeverity(double score) {
        if (score >= 0.8) return "🔴";
        if (score >= 0.6) return "🟡";
        return "🟠";
    }
    
    private String generateAnalysis(MagicNumberInfo magicInfo) {
        List<String> issues = new ArrayList<>();
        
        if (magicInfo.isRepeated) {
            issues.add("Repeated magic number");
        }
        
        if (magicInfo.isInBusinessLogic) {
            issues.add("Business logic constant");
        }
        
        if (magicInfo.isInPublicMethod) {
            issues.add("Public API magic number");
        }
        
        return issues.isEmpty() ? "Hardcoded numeric literal" : String.join(", ", issues);
    }
    
    private String generateSuggestions(MagicNumberInfo magicInfo) {
        return "Extract to named constant, use enum for discrete values, document meaning";
    }
    
    private String generateDetailedReason(MagicNumberInfo magicInfo, double riskScore) {
        StringBuilder reason = new StringBuilder();
        reason.append("This magic number is flagged as a code smell because: ");
        
        List<String> issues = new ArrayList<>();
        
        issues.add(String.format("the literal value '%s' is hardcoded without explanation", magicInfo.value));
        
        if (magicInfo.isRepeated) {
            issues.add("this same number appears multiple times in the code, making updates error-prone");
        }
        
        if (magicInfo.isInPublicMethod) {
            issues.add("it's used in a public method, making the API harder to understand");
        }
        
        if (magicInfo.isInBusinessLogic) {
            issues.add("it's part of business logic where the meaning should be explicit");
        }
        
        try {
            double numValue = Double.parseDouble(magicInfo.value);
            if (Math.abs(numValue) > 1000) {
                issues.add(String.format("the value %.0f is large and likely represents a domain-specific constant", numValue));
            }
            if (numValue % 1 != 0 && Math.abs(numValue) > 1) {
                issues.add("decimal values like this often represent important thresholds or ratios");
            }
        } catch (NumberFormatException e) {
            // Ignore
        }
        
        reason.append(String.join(", ", issues));
        reason.append(String.format(". Risk score: %.2f. Magic numbers reduce code readability and make maintenance difficult.", riskScore));
        
        return reason.toString();
    }
    
    private static class MagicNumberInfo {
        String fileName;
        String value;
        int lineNumber;
        String context;
        boolean isInTestMethod;
        boolean isInPublicMethod;
        boolean isInBusinessLogic;
        boolean isConstant;
        boolean isRepeated;
        String methodName;
    }
    
    private static class MagicNumberAnalyzer extends VoidVisitorAdapter<Void> {
        private final List<MagicNumberInfo> magicNumbers = new ArrayList<>();
        private final Map<String, Integer> numberCounts = new HashMap<>();
        private String fileName = "";
        private String currentMethodName = "";
        private boolean inTestMethod = false;
        private boolean inPublicMethod = false;
        
        public List<MagicNumberInfo> getMagicNumbers() {
            // Mark repeated numbers
            for (MagicNumberInfo info : magicNumbers) {
                info.isRepeated = numberCounts.getOrDefault(info.value, 0) > 1;
            }
            return magicNumbers;
        }
        
        @Override
        public void visit(CompilationUnit n, Void arg) {
            fileName = n.getStorage().map(s -> s.getFileName()).orElse("UnknownFile");
            super.visit(n, arg);
        }
        
        @Override
        public void visit(MethodDeclaration n, Void arg) {
            currentMethodName = n.getNameAsString();
            inTestMethod = n.getNameAsString().toLowerCase().startsWith("test");
            inPublicMethod = n.isPublic();
            super.visit(n, arg);
        }
        
        @Override
        public void visit(FieldDeclaration n, Void arg) {
            // Skip constants
            if (n.isFinal() && n.isStatic()) {
                return;
            }
            super.visit(n, arg);
        }
        
        @Override
        public void visit(IntegerLiteralExpr n, Void arg) {
            addMagicNumber(n.getValue(), n.getBegin().map(pos -> pos.line).orElse(0));
            super.visit(n, arg);
        }
        
        @Override
        public void visit(DoubleLiteralExpr n, Void arg) {
            addMagicNumber(n.getValue(), n.getBegin().map(pos -> pos.line).orElse(0));
            super.visit(n, arg);
        }
        
        @Override
        public void visit(LongLiteralExpr n, Void arg) {
            addMagicNumber(n.getValue(), n.getBegin().map(pos -> pos.line).orElse(0));
            super.visit(n, arg);
        }
        
        private void addMagicNumber(String value, int lineNumber) {
            MagicNumberInfo info = new MagicNumberInfo();
            info.fileName = fileName;
            info.value = value;
            info.lineNumber = lineNumber;
            info.context = determineContext();
            info.isInTestMethod = inTestMethod;
            info.isInPublicMethod = inPublicMethod;
            info.isInBusinessLogic = isBusinessLogicContext();
            info.methodName = currentMethodName;
            
            magicNumbers.add(info);
            numberCounts.merge(value, 1, Integer::sum);
        }
        
        private String determineContext() {
            if (inTestMethod) return "test method";
            if (inPublicMethod) return "public method";
            return "method";
        }
        
        private boolean isBusinessLogicContext() {
            String method = currentMethodName.toLowerCase();
            return method.contains("calculate") || method.contains("compute") || 
                   method.contains("process") || method.contains("validate");
        }
    }
}