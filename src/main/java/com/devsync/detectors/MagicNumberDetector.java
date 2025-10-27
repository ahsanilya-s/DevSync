package com.devsync.detectors;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.DoubleLiteralExpr;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.LongLiteralExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.nodeTypes.NodeWithRange;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class MagicNumberDetector {

    // Allowed trivial numbers (0,1,-1) and counts for reuse
    private static final Set<String> ALLOWED = Set.of("0", "1", "-1");
    private static final int REUSE_THRESHOLD = 2; // repeated occurrences considered suspicious

    public static List<String> detect(File file) {
        List<String> issues = new ArrayList<>();
        try {
            CompilationUnit cu = StaticJavaParser.parse(file);

            // Collect numeric and string literals with their textual value
            List<String> literals = new ArrayList<>();

            cu.findAll(IntegerLiteralExpr.class).forEach(n -> literals.add(n.getValue()));
            cu.findAll(LongLiteralExpr.class).forEach(n -> literals.add(n.getValue().replaceAll("[lL]$", "")));
            cu.findAll(DoubleLiteralExpr.class).forEach(n -> literals.add(n.getValue().replaceAll("[dD]$", "")));
            cu.findAll(StringLiteralExpr.class).forEach(n -> literals.add("\"" + n.getValue() + "\""));

            // Filter out allowed values (0,1,-1) and very short strings like empty string maybe allowed?
            List<String> suspect = literals.stream()
                    .filter(l -> !ALLOWED.contains(l))
                    .collect(Collectors.toList());

            // Count reuse
            Map<String, Long> counts = suspect.stream()
                    .collect(Collectors.groupingBy(s -> s, Collectors.counting()));

            // Report any literal that appears and is not obviously a constant
            counts.forEach((lit, cnt) -> {
                if (cnt >= 1) { // any unexplained literal is flagged
                    issues.add(String.format("Magic Literal in %s: value=%s, occurrences=%d", file.getName(), lit, cnt));
                }
            });

        } catch (Exception e) {
            issues.add("Error parsing for MagicNumberDetector: " + e.getMessage());
        }
        return issues;
    }
}
