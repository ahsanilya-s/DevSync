package com.devsync.detectors;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.Expression;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class LongStatementDetector {

    private static final int MAX_CHARS = 120;
    private static final int MAX_CHAINING_DEPTH = 3; // method call chaining depth threshold

    public static List<String> detect(File file) {
        List<String> issues = new ArrayList<>();
        try {
            CompilationUnit cu = StaticJavaParser.parse(file);

            for (Statement stmt : cu.findAll(Statement.class)) {
                String text = stmt.toString();
                if (text.length() > MAX_CHARS) {
                    issues.add(String.format("LongStatement in %s at line %d -> length=%d chars",
                            file.getName(), stmt.getBegin().map(p -> p.line).orElse(-1), text.length()));
                }

                // Check method chaining depth: count nested MethodCallExpr chains
                cu.findAll(MethodCallExpr.class).forEach(call -> {
                    int depth = computeChainingDepth(call);
                    if (depth > MAX_CHAINING_DEPTH) {
                        issues.add(String.format("LongStatement (chaining) in %s at line %d -> chainingDepth=%d, expr=%s",
                                file.getName(), call.getBegin().map(p -> p.line).orElse(-1), depth, call.toString()));
                    }
                });
            }

        } catch (Exception e) {
            issues.add("Error parsing for LongStatementDetector: " + e.getMessage());
        }
        return issues;
    }

    private static int computeChainingDepth(MethodCallExpr call) {
        int depth = 0;
        Expression scope = call.getScope().orElse(null);
        while (scope != null) {
            if (scope.isMethodCallExpr()) {
                depth++;
                scope = scope.asMethodCallExpr().getScope().orElse(null);
            } else {
                break;
            }
        }
        return depth;
    }
}
