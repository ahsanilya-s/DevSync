package com.devsync.detectors;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

/**
 * Detects long single statements:
 * - >120 characters
 * - >5 operators count (simple estimate)
 * - method chaining depth via '.' occurrences >3 in single statement
 */
public class LongStatementDetector {

    private static final int CHAR_LIMIT = 120;
    private static final int OPERATOR_LIMIT = 5;
    private static final int CHAINING_LIMIT = 3;

    private static final String OPERATORS = "+-*/%=&|!<>^";

    public static List<String> detect(File file) throws IOException {
        List<String> issues = new ArrayList<>();
        List<String> lines = Files.readAllLines(file.toPath());
        int lineNo = 0;
        for (String l : lines) {
            lineNo++;
            String s = l.trim();
            if (s.length() > CHAR_LIMIT) {
                issues.add(String.format("LongStatement in %s:%d → length=%d chars", file.getName(), lineNo, s.length()));
                continue;
            }
            int opCount = 0;
            for (char c : s.toCharArray()) if (OPERATORS.indexOf(c) >= 0) opCount++;
            if (opCount > OPERATOR_LIMIT) {
                issues.add(String.format("LongStatement in %s:%d → operators=%d", file.getName(), lineNo, opCount));
            }
            int chaining = s.length() - s.replace(".", "").length();
            if (chaining > CHAINING_LIMIT) {
                issues.add(String.format("LongStatement (chaining) in %s:%d → chainingDepth=%d", file.getName(), lineNo, chaining));
            }
        }
        return issues;
    }
}
