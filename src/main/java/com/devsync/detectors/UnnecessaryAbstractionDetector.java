package com.devsync.detectors;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class UnnecessaryAbstractionDetector {

    // Heuristic: an abstract class or interface with very few members and no subclass usage suggests unnecessary abstraction.
    private static final int MIN_MEMBERS = 2; // if below, considered suspicious
    private static final int MAX_ABSTRACTION_DEPTH = 3; // not used here but can be extended

    public static List<String> detect(File file) {
        List<String> issues = new ArrayList<>();
        try {
            CompilationUnit cu = StaticJavaParser.parse(file);

            for (ClassOrInterfaceDeclaration decl : cu.findAll(ClassOrInterfaceDeclaration.class)) {
                boolean isAbstract = decl.isInterface() || decl.isAbstract();
                int methodCount = decl.getMethods().size();
                int fieldCount = decl.getFields().stream().mapToInt(f -> f.getVariables().size()).sum();

                if (isAbstract && (methodCount + fieldCount) < MIN_MEMBERS) {
                    issues.add(String.format("Unnecessary abstraction in %s: %s at line %d (methods=%d, fields=%d)",
                            file.getName(), decl.getNameAsString(), decl.getBegin().map(p -> p.line).orElse(-1), methodCount, fieldCount));
                }
            }
        } catch (Exception e) {
            issues.add("Error parsing for UnnecessaryAbstractionDetector: " + e.getMessage());
        }
        return issues;
    }
}
