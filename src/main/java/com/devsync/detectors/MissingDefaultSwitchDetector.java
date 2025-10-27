package com.devsync.detectors;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.stmt.SwitchStmt;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MissingDefaultSwitchDetector {

    public static List<String> detect(File file) {
        List<String> issues = new ArrayList<>();
        try {
            CompilationUnit cu = StaticJavaParser.parse(file);

            for (SwitchStmt sw : cu.findAll(SwitchStmt.class)) {
                boolean hasDefault = sw.getEntries().stream()
                        .anyMatch(e -> e.getLabels().isEmpty()); // default has no label
                if (!hasDefault) {
                    issues.add(String.format("Switch without default in %s at line %d (cases=%d)",
                            file.getName(),
                            sw.getBegin().map(p -> p.line).orElse(-1),
                            sw.getEntries().size()));
                }
            }

        } catch (Exception e) {
            issues.add("Error parsing for MissingDefaultSwitchDetector: " + e.getMessage());
        }
        return issues;
    }
}
