package com.devsync.detectors;

import com.github.javaparser.*;
import com.github.javaparser.ast.*;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.stmt.*;
import java.io.*;
import java.util.*;

public class LongMethodDetector {

    private static final int MAX_LINES = 40; // threshold
    private static final int MAX_COMPLEXITY = 10;

    public static List<String> detect(File file) {
        List<String> results = new ArrayList<>();

        try {
            CompilationUnit cu = StaticJavaParser.parse(file);

            cu.findAll(MethodDeclaration.class).forEach(method -> {
                int lines = method.getEnd().get().line - method.getBegin().get().line;
                int complexity = countDecisionPoints(method);

                if (lines > MAX_LINES || complexity > MAX_COMPLEXITY) {
                    results.add(file.getName() + " → Method: " + method.getName() +
                            " | Lines: " + lines + " | Complexity: " + complexity);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
        return results;
    }

    private static int countDecisionPoints(MethodDeclaration method) {
        return method.findAll(IfStmt.class).size()
                + method.findAll(ForStmt.class).size()
                + method.findAll(WhileStmt.class).size()
                + method.findAll(SwitchEntry.class).size();
    }
}
