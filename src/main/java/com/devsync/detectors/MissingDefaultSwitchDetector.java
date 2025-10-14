package com.devsync.detectors;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

/**
 * Detect switch statements without a default case.
 * Threshold: any switch without default, especially if >5 cases.
 */
public class MissingDefaultSwitchDetector {

    public static List<String> detect(File file) throws IOException {
        List<String> issues = new ArrayList<>();
        List<String> lines = Files.readAllLines(file.toPath());
        int n = lines.size();
        boolean inSwitch = false;
        int braceCount = 0;
        int startLine = 0;
        int caseCount = 0;
        boolean hasDefault = false;
        for (int i = 0; i < n; i++) {
            String t = lines.get(i).trim();
            if (!inSwitch) {
                if (t.startsWith("switch(") || t.startsWith("switch (")) {
                    inSwitch = true;
                    startLine = i + 1;
                    braceCount = 0;
                    caseCount = 0;
                    hasDefault = false;
                }
            } else {
                for (char c : lines.get(i).toCharArray()) {
                    if (c == '{') braceCount++;
                    else if (c == '}') braceCount--;
                }
                if (t.startsWith("case ")) caseCount++;
                if (t.startsWith("default:") || t.startsWith("default ")) hasDefault = true;

                if (braceCount < 0 || (inSwitch && lines.get(i).contains("}"))) {
                    // heuristic end of switch when braces balanced or closing brace found
                    // evaluate
                }
                // better check: if we find a lone '}' closing the switch (approx)
                if (inSwitch && braceCount <= 0 && (t.endsWith("}") || t.equals("}"))) {
                    if (!hasDefault) {
                        issues.add(String.format("MissingDefaultSwitch in %s: switch at approx line %d has %d cases and no default",
                                file.getName(), startLine, caseCount));
                    } else if (caseCount > 5 && !hasDefault) {
                        issues.add(String.format("MissingDefaultSwitch (severe) in %s: many cases=%d no default", file.getName(), caseCount));
                    }
                    inSwitch = false;
                }
            }
        }
        return issues;
    }
}
