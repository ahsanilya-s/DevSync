package com.devsync.detectors;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

/**
 * Detects catch blocks that contain zero executable statements or only comments/logging with no handling.
 */
public class EmptyCatchDetector {

    public static List<String> detect(File file) throws IOException {
        List<String> issues = new ArrayList<>();
        List<String> lines = Files.readAllLines(file.toPath());
        int n = lines.size();
        boolean inCatch = false;
        int startLine = 0;
        int braceCount = 0;
        int statements = 0;
        for (int i = 0; i < n; i++) {
            String l = lines.get(i);
            String t = l.trim();
            if (!inCatch) {
                if (t.matches("catch\\s*\\(.*\\)\\s*\\{\\s*")) {
                    inCatch = true;
                    startLine = i + 1;
                    braceCount = 1;
                    statements = 0;
                }
            } else {
                // inside catch
                if (t.contains("{")) braceCount += countChar(t, '{');
                if (t.contains("}")) braceCount -= countChar(t, '}');

                // count simple statements: semicolon and not just comment or logging
                String noComment = t.replaceAll("//.*", "").replaceAll("/\\*.*\\*/", "").trim();
                if (!noComment.isEmpty()) {
                    // consider logging (System.out / logger) as weak handling
                    if (noComment.startsWith("System.out") || noComment.toLowerCase().contains("logger")) {
                        // weak handling, count as 0 (we flag weak)
                    } else if (noComment.endsWith(";")) {
                        statements++;
                    }
                }

                if (braceCount == 0) {
                    // catch ended
                    if (statements == 0) {
                        issues.add(String.format("EmptyCatch in %s at line %d (no executable statements)", file.getName(), startLine));
                    }
                    inCatch = false;
                }
            }
        }
        return issues;
    }

    private static int countChar(String s, char c) {
        int count = 0;
        for (char ch : s.toCharArray()) if (ch == c) count++;
        return count;git checkout secondary

    }
}
