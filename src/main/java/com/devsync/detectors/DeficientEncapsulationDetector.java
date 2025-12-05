package com.devsync.detectors;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import java.util.*;

public class DeficientEncapsulationDetector {

    public List<String> detect(CompilationUnit cu) {
        List<String> issues = new ArrayList<>();
        
        EncapsulationAnalyzer analyzer = new EncapsulationAnalyzer();
        cu.accept(analyzer, null);
        
        analyzer.getEncapsulationIssues().forEach(encInfo -> {
            // THRESHOLD CHECK FIRST - binary detection
            // Only report if field is public (main threshold)
            if (!encInfo.isPublic) {
                return; // NO SMELL - exit immediately
            }
            
            // THRESHOLD EXCEEDED - now calculate score for severity only
            double riskScore = calculateRiskScore(encInfo);
            String severity = getSeverity(encInfo, riskScore);
            String analysis = generateAnalysis(encInfo);
            String suggestions = generateSuggestions(encInfo);
            
            issues.add(String.format(
                "%s [DeficientEncapsulation] %s:%d - %s '%s' (Risk: %.2f) - %s | Suggestions: %s | DetailedReason: This field breaks encapsulation because it is %s, %s, and %s. Risk score: %.2f. Exposing internal state makes the class fragile and hard to maintain. | ThresholdDetails: {\"isPublic\":%b,\"isMutable\":%b,\"lacksAccessors\":%b,\"riskScore\":%.2f,\"summary\":\"Fields are ALWAYS flagged when public.\"}" ,
                severity,
                cu.getStorage().map(s -> s.getFileName()).orElse("UnknownFile"),
                encInfo.lineNumber,
                encInfo.type,
                encInfo.name,
                riskScore,
                analysis,
                suggestions,
                encInfo.isPublic ? "publicly accessible" : "not properly protected",
                encInfo.isMutable ? "mutable" : "immutable",
                encInfo.lacksAccessors ? "lacks proper accessor methods" : "has accessors",
                riskScore,
                encInfo.isPublic, encInfo.isMutable, encInfo.lacksAccessors, riskScore
            ));
        });
        
        return issues;
    }
    
    private double calculateRiskScore(EncapsulationInfo encInfo) {
        double visibilityScore = encInfo.isPublic ? 0.8 : 0.0;
        double mutabilityScore = encInfo.isMutable ? 0.6 : 0.0;
        double accessorScore = encInfo.lacksAccessors ? 0.4 : 0.0;
        
        return visibilityScore + mutabilityScore + accessorScore;
    }
    

    
    private String getSeverity(EncapsulationInfo encInfo, double riskScore) {
        if (riskScore > 1.2) return "🔴";
        if (riskScore > 0.8) return "🟡";
        return "🟠";
    }
    
    private String generateAnalysis(EncapsulationInfo encInfo) {
        List<String> issues = new ArrayList<>();
        
        if (encInfo.isPublic) {
            issues.add("Public field exposure");
        }
        if (encInfo.isMutable) {
            issues.add("Mutable state");
        }
        if (encInfo.lacksAccessors) {
            issues.add("Missing accessors");
        }
        
        return issues.isEmpty() ? "Encapsulation issue" : String.join(", ", issues);
    }
    
    private String generateSuggestions(EncapsulationInfo encInfo) {
        List<String> suggestions = new ArrayList<>();
        
        if (encInfo.isPublic) {
            suggestions.add("Make field private");
        }
        if (encInfo.lacksAccessors) {
            suggestions.add("Add getter/setter methods");
        }
        if (encInfo.isMutable) {
            suggestions.add("Consider immutable design");
        }
        
        return String.join(", ", suggestions);
    }
    
    private static class EncapsulationInfo {
        String name;
        String type;
        int lineNumber;
        boolean isPublic;
        boolean isMutable;
        boolean lacksAccessors;
        
        EncapsulationInfo(String name, String type, int lineNumber) {
            this.name = name;
            this.type = type;
            this.lineNumber = lineNumber;
        }
    }
    
    private static class EncapsulationAnalyzer extends VoidVisitorAdapter<Void> {
        private final List<EncapsulationInfo> encapsulationIssues = new ArrayList<>();
        private final Set<String> accessorMethods = new HashSet<>();
        
        public List<EncapsulationInfo> getEncapsulationIssues() {
            return encapsulationIssues;
        }
        
        @Override
        public void visit(ClassOrInterfaceDeclaration n, Void arg) {
            collectAccessorMethods(n);
            super.visit(n, arg);
        }
        
        @Override
        public void visit(FieldDeclaration n, Void arg) {
            n.getVariables().forEach(var -> {
                EncapsulationInfo info = new EncapsulationInfo(var.getNameAsString(), "Field", 
                    n.getBegin().map(pos -> pos.line).orElse(0));
                
                info.isPublic = n.isPublic();
                info.isMutable = !n.isFinal();
                info.lacksAccessors = !hasAccessors(var.getNameAsString());
                
                encapsulationIssues.add(info);
            });
            
            super.visit(n, arg);
        }
        
        private void collectAccessorMethods(ClassOrInterfaceDeclaration classDecl) {
            classDecl.getMethods().forEach(method -> {
                String methodName = method.getNameAsString();
                if (methodName.startsWith("get") || methodName.startsWith("set") || 
                    methodName.startsWith("is")) {
                    accessorMethods.add(extractFieldName(methodName));
                }
            });
        }
        
        private boolean hasAccessors(String fieldName) {
            return accessorMethods.contains(fieldName.toLowerCase());
        }
        
        private String extractFieldName(String methodName) {
            if (methodName.startsWith("get") || methodName.startsWith("set")) {
                return methodName.substring(3).toLowerCase();
            }
            if (methodName.startsWith("is")) {
                return methodName.substring(2).toLowerCase();
            }
            return methodName.toLowerCase();
        }
    }
}