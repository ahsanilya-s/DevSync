package com.devsync.detectors;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import java.util.*;

public class MissingDefaultDetector {
    
    private static final Set<String> ENUM_KEYWORDS = Set.of("enum", "Enum");
    private static final Set<String> SAFE_ENUM_PATTERNS = Set.of(
        "Status", "State", "Type", "Kind", "Mode", "Level"
    );
    
    private static final Map<String, Double> CONTEXT_WEIGHTS = Map.of(
        "public_method", 1.0,
        "private_method", 0.7,
        "constructor", 0.9,
        "nested_switch", 1.2,
        "return_value", 1.1,
        "assignment", 0.8
    );

    public List<String> detect(CompilationUnit cu) {
        List<String> issues = new ArrayList<>();
        
        SwitchAnalyzer analyzer = new SwitchAnalyzer();
        cu.accept(analyzer, null);
        
        analyzer.getMissingSwitches().forEach(switchInfo -> {
            double riskScore = calculateRiskScore(switchInfo);
            
            if (shouldReport(switchInfo, riskScore)) {
                String severity = getSeverity(switchInfo, riskScore);
                String analysis = generateAnalysis(switchInfo);
                String suggestions = generateSuggestions(switchInfo);
                
                issues.add(String.format(
                    "%s [MissingDefault] %s:%d - Switch on '%s' (%d cases, Risk: %.2f) - %s | Suggestions: %s",
                    severity,
                    cu.getStorage().map(s -> s.getFileName()).orElse("UnknownFile"),
                    switchInfo.lineNumber,
                    switchInfo.switchExpression,
                    switchInfo.caseCount,
                    riskScore,
                    analysis,
                    suggestions
                ));
            }
        });
        
        return issues;
    }
    
    private double calculateRiskScore(SwitchInfo switchInfo) {
        double baseScore = 0.6;
        
        double contextScore = calculateContextScore(switchInfo);
        double completenessScore = calculateCompletenessScore(switchInfo);
        double complexityScore = calculateComplexityScore(switchInfo);
        double safetyScore = calculateSafetyScore(switchInfo);
        
        return Math.min(1.5, baseScore + contextScore + completenessScore + complexityScore - safetyScore);
    }
    
    private double calculateContextScore(SwitchInfo switchInfo) {
        double score = 0.0;
        
        String context = determineContext(switchInfo);
        double contextWeight = CONTEXT_WEIGHTS.getOrDefault(context, 0.8);
        score += contextWeight * 0.3;
        
        if (switchInfo.isInPublicMethod) {
            score += 0.2;
        }
        
        if (switchInfo.hasReturnValue) {
            score += 0.25;
        }
        
        if (switchInfo.nestingLevel > 1) {
            score += 0.15;
        }
        
        return Math.min(0.5, score);
    }
    
    private double calculateCompletenessScore(SwitchInfo switchInfo) {
        double score = 0.0;
        
        if (switchInfo.isEnumSwitch) {
            double coverage = (double) switchInfo.caseCount / Math.max(1, switchInfo.enumValueCount);
            if (coverage < 0.8) {
                score += 0.3;
            } else if (coverage < 1.0) {
                score += 0.2;
            }
        } else {
            if (switchInfo.caseCount < 3) {
                score += 0.2;
            }
        }
        
        if (switchInfo.hasEmptyCases) {
            score += 0.1;
        }
        
        return Math.min(0.4, score);
    }
    
    private double calculateComplexityScore(SwitchInfo switchInfo) {
        double score = 0.0;
        
        if (switchInfo.hasComplexCases) {
            score += 0.15;
        }
        
        if (switchInfo.hasFallthrough) {
            score += 0.2;
        }
        
        if (switchInfo.caseCount > 10) {
            score += 0.1;
        }
        
        return Math.min(0.3, score);
    }
    
    private double calculateSafetyScore(SwitchInfo switchInfo) {
        double score = 0.0;
        
        if (switchInfo.isEnumSwitch && isSafeEnum(switchInfo.switchType)) {
            score += 0.2;
        }
        
        if (switchInfo.hasExhaustiveComments) {
            score += 0.15;
        }
        
        if (switchInfo.isInTestMethod) {
            score += 0.1;
        }
        
        return Math.min(0.3, score);
    }
    
    private boolean isSafeEnum(String enumType) {
        return SAFE_ENUM_PATTERNS.stream()
            .anyMatch(pattern -> enumType.contains(pattern));
    }
    
    private String determineContext(SwitchInfo switchInfo) {
        if (switchInfo.isInPublicMethod) {
            return "public_method";
        }
        if (switchInfo.isInConstructor) {
            return "constructor";
        }
        if (switchInfo.nestingLevel > 1) {
            return "nested_switch";
        }
        if (switchInfo.hasReturnValue) {
            return "return_value";
        }
        return "private_method";
    }
    
    private boolean shouldReport(SwitchInfo switchInfo, double riskScore) {
        if (switchInfo.hasDefaultCase) {
            return false;
        }
        
        if (switchInfo.isInTestMethod && riskScore < 0.8) {
            return false;
        }
        
        if (switchInfo.isEnumSwitch && switchInfo.caseCount == switchInfo.enumValueCount && 
            isSafeEnum(switchInfo.switchType)) {
            return false;
        }
        
        return riskScore > 0.5;
    }
    
    private String getSeverity(SwitchInfo switchInfo, double riskScore) {
        if (switchInfo.isInPublicMethod && (riskScore > 1.0 || switchInfo.hasReturnValue)) {
            return "🔴";
        }
        if (riskScore > 0.8 || (switchInfo.isEnumSwitch && switchInfo.caseCount < switchInfo.enumValueCount * 0.8)) {
            return "🟡";
        }
        return "🟠";
    }
    
    private String generateAnalysis(SwitchInfo switchInfo) {
        List<String> issues = new ArrayList<>();
        
        if (switchInfo.hasReturnValue) {
            issues.add("Missing return path");
        }
        
        if (switchInfo.isEnumSwitch && switchInfo.caseCount < switchInfo.enumValueCount) {
            issues.add("Incomplete enum coverage");
        }
        
        if (switchInfo.isInPublicMethod) {
            issues.add("Public API risk");
        }
        
        if (switchInfo.hasFallthrough) {
            issues.add("Fallthrough complexity");
        }
        
        if (switchInfo.hasEmptyCases) {
            issues.add("Empty case blocks");
        }
        
        return issues.isEmpty() ? "Missing default case" : String.join(", ", issues);
    }
    
    private String generateSuggestions(SwitchInfo switchInfo) {
        List<String> suggestions = new ArrayList<>();
        
        if (switchInfo.hasReturnValue) {
            suggestions.add("Add default with appropriate return value");
        } else {
            suggestions.add("Add default case with error handling");
        }
        
        if (switchInfo.isEnumSwitch) {
            suggestions.add("Handle all enum values or document intentional omissions");
        }
        
        if (switchInfo.hasFallthrough) {
            suggestions.add("Add explicit break statements");
        }
        
        suggestions.add("Consider throwing IllegalArgumentException in default");
        suggestions.add("Add logging for unexpected values");
        
        return String.join(", ", suggestions);
    }
    
    private static class SwitchInfo {
        String switchExpression;
        String switchType;
        int lineNumber;
        int caseCount;
        int enumValueCount;
        boolean hasDefaultCase;
        boolean isEnumSwitch;
        boolean hasReturnValue;
        boolean isInPublicMethod;
        boolean isInConstructor;
        boolean isInTestMethod;
        boolean hasComplexCases;
        boolean hasFallthrough;
        boolean hasEmptyCases;
        boolean hasExhaustiveComments;
        int nestingLevel;
        String methodName;
        
        SwitchInfo() {
            this.enumValueCount = 0;
        }
    }
    
    private static class SwitchAnalyzer extends VoidVisitorAdapter<Void> {
        private final List<SwitchInfo> missingSwitches = new ArrayList<>();
        private String currentMethodName = "";
        private boolean inPublicMethod = false;
        private boolean inConstructor = false;
        private boolean inTestMethod = false;
        private int nestingLevel = 0;
        
        public List<SwitchInfo> getMissingSwitches() {
            return missingSwitches;
        }
        
        @Override
        public void visit(CompilationUnit n, Void arg) {
            super.visit(n, arg);
        }
        
        @Override
        public void visit(MethodDeclaration n, Void arg) {
            currentMethodName = n.getNameAsString();
            inPublicMethod = n.isPublic();
            inConstructor = false;
            inTestMethod = n.getNameAsString().toLowerCase().startsWith("test");
            super.visit(n, arg);
        }
        
        @Override
        public void visit(ConstructorDeclaration n, Void arg) {
            currentMethodName = n.getNameAsString();
            inPublicMethod = n.isPublic();
            inConstructor = true;
            inTestMethod = false;
            super.visit(n, arg);
        }
        
        @Override
        public void visit(SwitchStmt n, Void arg) {
            nestingLevel++;
            
            SwitchInfo info = new SwitchInfo();
            info.switchExpression = n.getSelector().toString();
            info.lineNumber = n.getBegin().map(pos -> pos.line).orElse(0);
            info.isInPublicMethod = inPublicMethod;
            info.isInConstructor = inConstructor;
            info.isInTestMethod = inTestMethod;
            info.nestingLevel = nestingLevel;
            info.methodName = currentMethodName;
            
            // Analyze switch cases
            List<SwitchEntry> entries = n.getEntries();
            info.caseCount = (int) entries.stream().filter(e -> !e.getLabels().isEmpty()).count();
            info.hasDefaultCase = entries.stream().anyMatch(e -> e.getLabels().isEmpty());
            
            // Check for return values
            info.hasReturnValue = hasReturnStatements(entries);
            
            // Analyze switch type
            analyzeSwitchType(info, n.getSelector());
            
            // Check case complexity
            info.hasComplexCases = hasComplexCases(entries);
            info.hasFallthrough = hasFallthrough(entries);
            info.hasEmptyCases = hasEmptyCases(entries);
            
            if (!info.hasDefaultCase) {
                missingSwitches.add(info);
            }
            
            super.visit(n, arg);
            nestingLevel--;
        }
        
        private boolean hasReturnStatements(List<SwitchEntry> entries) {
            return entries.stream()
                .flatMap(e -> e.getStatements().stream())
                .anyMatch(stmt -> stmt instanceof ReturnStmt);
        }
        
        private void analyzeSwitchType(SwitchInfo info, Expression selector) {
            String selectorStr = selector.toString();
            
            // Try to determine if it's an enum
            if (selector instanceof NameExpr || selector instanceof FieldAccessExpr) {
                info.isEnumSwitch = true;
                info.switchType = extractTypeName(selectorStr);
                info.enumValueCount = estimateEnumValues(info.switchType);
            } else {
                info.isEnumSwitch = false;
                info.switchType = "unknown";
            }
        }
        
        private String extractTypeName(String selector) {
            if (selector.contains(".")) {
                String[] parts = selector.split("\\.");
                return parts[0];
            }
            return selector;
        }
        
        private int estimateEnumValues(String typeName) {
            // Simple heuristic - in real implementation, would use symbol resolution
            return 5; // Default estimate
        }
        
        private boolean hasComplexCases(List<SwitchEntry> entries) {
            return entries.stream()
                .anyMatch(e -> e.getStatements().size() > 3);
        }
        
        private boolean hasFallthrough(List<SwitchEntry> entries) {
            return entries.stream()
                .anyMatch(e -> !e.getLabels().isEmpty() && 
                         !e.getStatements().isEmpty() && 
                         e.getStatements().stream().noneMatch(stmt -> 
                             stmt instanceof BreakStmt || stmt instanceof ReturnStmt));
        }
        
        private boolean hasEmptyCases(List<SwitchEntry> entries) {
            return entries.stream()
                .anyMatch(e -> !e.getLabels().isEmpty() && e.getStatements().isEmpty());
        }
    }
}