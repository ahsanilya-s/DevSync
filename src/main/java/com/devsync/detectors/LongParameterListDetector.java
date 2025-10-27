package com.devsync.detectors;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class LongParameterListDetector {

    // Thresholds
    private static final int MAX_PARAMS = 5;
    private static final int MAX_DISTINCT_TYPES = 3;

    public static List<String> detect(File file) {
        List<String> issues = new ArrayList<>();
        try {
            CompilationUnit cu = StaticJavaParser.parse(file);

            for (MethodDeclaration method : cu.findAll(MethodDeclaration.class)) {
                int paramCount = method.getParameters().size();

                // Count distinct parameter types
                long distinctTypes = method.getParameters().stream()
                        .map(p -> p.getType().asString())
                        .distinct()
                        .count();

                if (paramCount > MAX_PARAMS || distinctTypes > MAX_DISTINCT_TYPES) {
                    issues.add(String.format("Long Parameter List in %s: method=%s, params=%d, distinctTypes=%d",
                            file.getName(), method.getNameAsString(), paramCount, distinctTypes));
                }
            }
        } catch (Exception e) {
            issues.add("Error parsing for LongParameterListDetector: " + e.getMessage());
        }
        return issues;
    }
}
