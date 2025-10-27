package com.devsync.detectors;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class LongIdentifierDetector {

    private static final int MAX_CHARS = 32;
    private static final int MAX_WORDS = 5;

    public static List<String> detect(File file) {
        List<String> issues = new ArrayList<>();
        try {
            CompilationUnit cu = StaticJavaParser.parse(file);

            // Check fields and variables
            for (FieldDeclaration fd : cu.findAll(FieldDeclaration.class)) {
                for (VariableDeclarator vd : fd.getVariables()) {
                    String name = vd.getNameAsString();
                    checkName(file.getName(), name, fd.getBegin().map(p -> p.line).orElse(-1), issues);
                }
            }

            for (VariableDeclarator vd : cu.findAll(VariableDeclarator.class)) {
                String name = vd.getNameAsString();
                checkName(file.getName(), name, vd.getBegin().map(p -> p.line).orElse(-1), issues);
            }

            // Also check method names
            for (MethodDeclaration md : cu.findAll(MethodDeclaration.class)) {
                String name = md.getNameAsString();
                checkName(file.getName(), name, md.getBegin().map(p -> p.line).orElse(-1), issues);
            }

        } catch (Exception e) {
            issues.add("Error parsing for LongIdentifierDetector: " + e.getMessage());
        }
        return issues;
    }

    private static void checkName(String fileName, String name, int line, List<String> issues) {
        if (name.length() > MAX_CHARS || name.split("(?=[A-Z])|_").length > MAX_WORDS) {
            issues.add(String.format("Long identifier in %s at line %d: name=%s (len=%d)",
                    fileName, line, name, name.length()));
        }
    }
}
