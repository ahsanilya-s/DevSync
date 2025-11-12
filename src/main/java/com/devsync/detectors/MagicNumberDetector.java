package com.devsync.detectors;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.body.MethodDeclaration;
import java.util.*;

public class MagicNumberDetector {
    private static final Set<String> ALLOWED = Set.of("0", "1", "-1", "2", "10", "100");
    private static final int REUSE_THRESHOLD = 2;

    public List<String> detect(CompilationUnit cu) {
        List<String> issues = new ArrayList<>();
        Map<String, Integer> literalCount = new HashMap<>();

        cu.findAll(IntegerLiteralExpr.class).forEach(l -> literalCount.merge(l.getValue(), 1, Integer::sum));
        cu.findAll(DoubleLiteralExpr.class).forEach(l -> literalCount.merge(l.getValue(), 1, Integer::sum));
        cu.findAll(LongLiteralExpr.class).forEach(l -> literalCount.merge(l.getValue(), 1, Integer::sum));

        literalCount.forEach((literal, count) -> {
            if (!ALLOWED.contains(literal) && count >= REUSE_THRESHOLD) {
                double magicScore = calculateMagicScore(literal, count);
                String severity = getSeverity(magicScore);
                
                issues.add(String.format(
                        "%s [MagicNumber] %s - Literal '%s' occurs %d times [Score: %.2f] | Suggestions: Extract to constant",
                        severity,
                        cu.getStorage().map(s -> s.getFileName()).orElse("UnknownFile"),
                        literal,
                        count,
                        magicScore
                ));
            }
        });

        return issues;
    }
    
    private double calculateMagicScore(String literal, int count) {
        double baseScore = 0.5;
        
        try {
            double value = Math.abs(Double.parseDouble(literal));
            if (value > 1000) baseScore += 0.3;
            else if (value > 100) baseScore += 0.2;
            
            if (literal.contains(".") && literal.length() > 5) baseScore += 0.2;
        } catch (NumberFormatException e) {
            baseScore += 0.1;
        }
        
        baseScore += Math.min(0.3, count * 0.1);
        return Math.min(1.0, baseScore);
    }
    
    private String getSeverity(double score) {
        if (score > 0.8) return "🔴";
        if (score > 0.6) return "🟡";
        return "🟠";
    }
}