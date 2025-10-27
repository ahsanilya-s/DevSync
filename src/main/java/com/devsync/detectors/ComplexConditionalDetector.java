package com.devsync.detectors;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.EnclosedExpr;
import com.github.javaparser.ast.stmt.IfStmt;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ComplexConditionalDetector {

    private static final int MAX_LOGICAL_OPERATORS = 3;
    private static final int MAX_NESTING = 2;

    public static List<String> detect(File file) {
        List<String> issues = new ArrayList<>();
        try {
            CompilationUnit cu = StaticJavaParser.parse(file);

            for (IfStmt ifStmt : cu.findAll(IfStmt.class)) {
                AtomicInteger opCount = new AtomicInteger();
                countLogicalOperators(ifStmt.getCondition(), opCount);

                int nesting = computeIfNestingDepth(ifStmt);

                if (opCount.get() > MAX_LOGICAL_OPERATORS || nesting > MAX_NESTING) {
                    issues.add(String.format("Complex conditional in %s at line %d -> operators=%d, nesting=%d",
                            file.getName(),
                            ifStmt.getBegin().map(p -> p.line).orElse(-1),
                            opCount.get(), nesting));
                }
            }
        } catch (Exception e) {
            issues.add("Error parsing for ComplexConditionalDetector: " + e.getMessage());
        }
        return issues;
    }

    private static void countLogicalOperators(com.github.javaparser.ast.Node node, AtomicInteger counter) {
        if (node == null) return;
        if (node instanceof BinaryExpr) {
            BinaryExpr bin = (BinaryExpr) node;
            BinaryExpr.Operator op = bin.getOperator();
            if (op == BinaryExpr.Operator.AND || op == BinaryExpr.Operator.OR) {
                counter.incrementAndGet();
            }
            countLogicalOperators(bin.getLeft(), counter);
            countLogicalOperators(bin.getRight(), counter);
        } else if (node instanceof EnclosedExpr) {
            countLogicalOperators(((EnclosedExpr) node).getInner(), counter);
        } else {
            for (com.github.javaparser.ast.Node child : node.getChildNodes()) {
                countLogicalOperators(child, counter);
            }
        }
    }

    private static int computeIfNestingDepth(IfStmt ifStmt) {
        int depth = 0;
        com.github.javaparser.ast.stmt.Statement thenStmt = ifStmt.getThenStmt();
        while (thenStmt.isIfStmt()) {
            depth++;
            thenStmt = thenStmt.asIfStmt().getThenStmt();
        }
        return depth;
    }
}
