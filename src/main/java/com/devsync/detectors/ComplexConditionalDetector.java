package com.devsync.detectors;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

/**
 * Detect complex boolean conditions:
 * - >3 logical operators (&&, ||)
 * - nesting >2 (by parentheses)
 */
public class ComplexConditionalDetector {

    private static final int OPERATOR_THRESHOLD = 3;
    private static final int NESTING_THRESHOLD = 2;

    public static List<String> detect(File file) throws IOException {
        List<String> issues = new ArrayList<>();
        List<String> lines = Files.readAllLines(file.toPath());
        int lineNo = 0;
        for (String l : lines) {
            lineNo++;
            String s = l.trim();
            if (!(s.contains("if(") || s.contains("if (") || s.contains("while(") || s.contains("while (") || s.contains("&&") || s.contains("||"))) continue;
            int opCount = 0;
            if (s.contains("&&")) opCount += s.length() - s.replace("&&", "").length();
            if (s.contains("||")) opCount += s.length() - s.replace("||", "").length();
            // we counted chars; divide by 2 since each operator has length 2
            opCount = opCount / 2;
            int parenNesting = 0;
            int maxNest = 0;
            for (char c : s.toCharArray()) {
                if (c == '(') parenNesting++;
                else if (c == ')') parenNesting--;
                maxNest = Math.max(maxNest, parenNesting);
            }
            if (opCount > OPERATOR_THRESHOLD || maxNest > NESTING_THRESHOLD) {
                issues.add(String.format("ComplexConditional in %s:%d → operators=%d, parenNesting=%d, stmt=\"%s\"",
                        file.getName(), lineNo, opCount, maxNest, s));
            }
        }
        return issues;
    }
}
