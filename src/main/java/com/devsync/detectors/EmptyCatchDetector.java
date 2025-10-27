package com.devsync.detectors;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.stmt.CatchClause;
import com.github.javaparser.ast.stmt.BlockStmt;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class EmptyCatchDetector {

    public static List<String> detect(File file) {
        List<String> issues = new ArrayList<>();
        try {
            CompilationUnit cu = StaticJavaParser.parse(file);

            for (CatchClause cc : cu.findAll(CatchClause.class)) {
                BlockStmt body = cc.getBody();

                // If catch body has zero executable statements (ignoring comments/labels)
                if (body.isEmpty()) {
                    issues.add(String.format("Empty catch clause in %s at line %d: exception=%s",
                            file.getName(),
                            cc.getBegin().map(p -> p.line).orElse(-1),
                            cc.getParameter().getType().asString()));
                } else {
                    // If the body only contains simple logging with weak message, you can further flag.
                    // Example heuristic: if body contains only a single statement that is a logger call with simple message,
                    // flag it as weak handling. (Keep simple for now.)
                    if (body.getStatements().size() == 1) {
                        String stmt = body.getStatement(0).toString().toLowerCase();
                        if (stmt.contains("print") || stmt.contains("log") && stmt.length() < 60) {
                            issues.add(String.format("Weak logging in catch in %s at line %d: %s",
                                    file.getName(), cc.getBegin().map(p -> p.line).orElse(-1), stmt.trim()));
                        }
                    }
                }
            }
        } catch (Exception e) {
            issues.add("Error parsing for EmptyCatchDetector: " + e.getMessage());
        }
        return issues;
    }
}
