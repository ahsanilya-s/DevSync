package com.devsync.detectors;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.ImportDeclaration;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class BrokenModularizationDetector {

    // Heuristic thresholds
    private static final int MAX_COUPLING = 5; // number of different external classes referenced
    private static final int MAX_FAN_IN = 10; // not implemented fully, placeholder

    public static List<String> detect(File file) {
        List<String> issues = new ArrayList<>();
        try {
            CompilationUnit cu = StaticJavaParser.parse(file);

            // Collect imports to estimate coupling
            Set<String> imports = cu.findAll(ImportDeclaration.class).stream()
                    .map(ImportDeclaration::getNameAsString)
                    .collect(Collectors.toSet());

            // Also count type usages in this file
            Set<String> typeUsages = new HashSet<>();
            cu.findAll(NameExpr.class).forEach(ne -> typeUsages.add(ne.getNameAsString()));

            int couplingEstimate = imports.size();

            if (couplingEstimate > MAX_COUPLING) {
                issues.add(String.format("Potential broken modularization in %s: imports=%d, typeUsages=%d",
                        file.getName(), imports.size(), typeUsages.size()));
            }

            // More advanced checks (package cycles, fan-in/fan-out) need whole-project analysis.
        } catch (Exception e) {
            issues.add("Error parsing for BrokenModularizationDetector: " + e.getMessage());
        }
        return issues;
    }
}
