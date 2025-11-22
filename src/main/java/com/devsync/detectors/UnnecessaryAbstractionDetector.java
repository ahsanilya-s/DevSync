package com.devsync.detectors;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import java.util.*;

public class UnnecessaryAbstractionDetector {

    public List<String> detect(CompilationUnit cu) {
        List<String> issues = new ArrayList<>();
        
        AbstractionAnalyzer analyzer = new AbstractionAnalyzer();
        cu.accept(analyzer, null);
        
        analyzer.getUnnecessaryAbstractions().forEach(absInfo -> {
            double complexityScore = calculateComplexityScore(absInfo);
            
            if (shouldReport(absInfo, complexityScore)) {
                String severity = getSeverity(absInfo, complexityScore);
                String analysis = generateAnalysis(absInfo);
                String suggestions = generateSuggestions(absInfo);
                
                issues.add(String.format(
                    "%s [UnnecessaryAbstraction] %s:%d - %s '%s' (Score: %.2f) - %s | Suggestions: %s",
                    severity,
                    cu.getStorage().map(s -> s.getFileName()).orElse("UnknownFile"),
                    absInfo.lineNumber,
                    absInfo.type,
                    absInfo.name,
                    complexityScore,
                    analysis,
                    suggestions
                ));
            }
        });
        
        return issues;
    }
    
    private double calculateComplexityScore(AbstractionInfo absInfo) {
        double usageScore = 1.0 - Math.min(1.0, (double) absInfo.usageCount / 3);
        double implementationScore = absInfo.hasOnlyOneImplementation ? 0.8 : 0.0;
        double simplicityScore = absInfo.isSimpleWrapper ? 0.6 : 0.0;
        
        return usageScore * 0.5 + implementationScore * 0.3 + simplicityScore * 0.2;
    }
    
    private boolean shouldReport(AbstractionInfo absInfo, double complexityScore) {
        return complexityScore > 0.6;
    }
    
    private String getSeverity(AbstractionInfo absInfo, double complexityScore) {
        if (complexityScore > 0.9) return "🔴";
        if (complexityScore > 0.7) return "🟡";
        return "🟠";
    }
    
    private String generateAnalysis(AbstractionInfo absInfo) {
        List<String> issues = new ArrayList<>();
        
        if (absInfo.usageCount <= 1) {
            issues.add("Single usage");
        }
        if (absInfo.hasOnlyOneImplementation) {
            issues.add("Only one implementation");
        }
        if (absInfo.isSimpleWrapper) {
            issues.add("Simple wrapper");
        }
        
        return issues.isEmpty() ? "Unnecessary abstraction" : String.join(", ", issues);
    }
    
    private String generateSuggestions(AbstractionInfo absInfo) {
        List<String> suggestions = new ArrayList<>();
        
        if (absInfo.hasOnlyOneImplementation) {
            suggestions.add("Remove interface, use concrete class");
        }
        if (absInfo.isSimpleWrapper) {
            suggestions.add("Inline the wrapper");
        }
        if (absInfo.usageCount <= 1) {
            suggestions.add("Consider removing abstraction");
        }
        
        return String.join(", ", suggestions);
    }
    
    private static class AbstractionInfo {
        String name;
        String type;
        int lineNumber;
        int usageCount;
        boolean hasOnlyOneImplementation;
        boolean isSimpleWrapper;
        
        AbstractionInfo(String name, String type, int lineNumber) {
            this.name = name;
            this.type = type;
            this.lineNumber = lineNumber;
        }
    }
    
    private static class AbstractionAnalyzer extends VoidVisitorAdapter<Void> {
        private final List<AbstractionInfo> unnecessaryAbstractions = new ArrayList<>();
        private final Map<String, Integer> interfaceUsage = new HashMap<>();
        
        public List<AbstractionInfo> getUnnecessaryAbstractions() {
            return unnecessaryAbstractions;
        }
        
        @Override
        public void visit(ClassOrInterfaceDeclaration n, Void arg) {
            if (n.isInterface()) {
                AbstractionInfo info = new AbstractionInfo(n.getNameAsString(), "Interface", 
                    n.getBegin().map(pos -> pos.line).orElse(0));
                
                info.usageCount = interfaceUsage.getOrDefault(n.getNameAsString(), 0);
                info.hasOnlyOneImplementation = hasOnlyOneImplementation(n);
                info.isSimpleWrapper = isSimpleWrapper(n);
                
                unnecessaryAbstractions.add(info);
            }
            super.visit(n, arg);
        }
        
        private boolean hasOnlyOneImplementation(ClassOrInterfaceDeclaration interfaceDecl) {
            return interfaceDecl.getMethods().size() <= 2;
        }
        
        private boolean isSimpleWrapper(ClassOrInterfaceDeclaration interfaceDecl) {
            return interfaceDecl.getMethods().size() == 1;
        }
    }
}