package com.devsync.detectors;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Detects long methods using simple heuristics:
 * - LOC threshold > 30 (configurable inside)
 * - Cyclomatic Complexity approximated by counting keywords (if, for, while, case, &&, ||, ? :)
 * - Nesting depth by counting braces inside method
 */
public class LongMethodDetector {

    private static final int LOC_THRESHOLD = 30;      // lines
    private static final int CC_THRESHOLD = 10;       // cyclomatic complexity
    private static final int NESTING_THRESHOLD = 3;   // nesting depth

    private static final Pattern METHOD_START = Pattern.compile(
            "^\\s*(public|protected|private|static|\\s)*[\\w\\<\\>\\[\\]]+\\s+\\w+\\s*\\([^\\)]*\\)\\s*\\{\\s*$");

    public static List<String> detect(File file) throws IOException {
        List<String> issues = new ArrayList<>();
        List<String> lines = Files.readAllLines(file.toPath());
        int n = lines.size();

        boolean inMethod = false;
        int methodStart = 0;
        int braceCount = 0;
        int nestingMax = 0;
        int cc = 1; // cyclomatic complexity baseline
        String methodSignature = null;

        for (int i = 0; i < n; i++) {
            String raw = lines.get(i);
            String line = raw.trim();

            if (!inMethod) {
                Matcher m = METHOD_START.matcher(line);
                if (m.find()) {
                    inMethod = true;
                    methodStart = i;
                    braceCount = 1;
                    nestingMax = 0;
                    cc = 1;
                    methodSignature = line.length() > 120 ? line.substring(0, 120) + "..." : line;
                    continue;
                }
            } else {
                // Count braces and nesting
                for (char c : raw.toCharArray()) {
                    if (c == '{') {
                        braceCount++;
                        nestingMax = Math.max(nestingMax, braceCount - 1);
                    } else if (c == '}') {
                        braceCount--;
                    }
                }
                // approximate cyclomatic complexity by keywords
                String low = line.toLowerCase();
                if (low.contains(" if ") || low.startsWith("if(") || low.contains(" if(")) cc++;
                if (low.contains(" else if ")) cc++;
                if (low.contains(" for ") || low.startsWith("for(")) cc++;
                if (low.contains(" while ") || low.startsWith("while(")) cc++;
                if (low.contains(" case ")) cc++;
                if (low.contains(" && ")) cc++;
                if (low.contains(" || ")) cc++;
                if (low.contains("?")) cc++;

                // method end detection
                if (braceCount == 0) {
                    inMethod = false;
                    int loc = i - methodStart + 1;
                    boolean longLoc = loc > LOC_THRESHOLD;
                    boolean highCC = cc > CC_THRESHOLD;
                    boolean deepNesting = nestingMax > NESTING_THRESHOLD;

                    if (longLoc || highCC || deepNesting) {
                        String issue = String.format("LongMethod in %s: signature=\"%s\", LOC=%d, CC~%d, nestingMax=%d",
                                file.getName(), methodSignature, loc, cc, nestingMax);
                        issues.add(issue);
                    }
                }
            }
        }
        return issues;
    }
}
