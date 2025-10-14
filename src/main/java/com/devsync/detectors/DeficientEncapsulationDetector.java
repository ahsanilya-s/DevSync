package com.devsync.detectors;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

/**
 * Detect classes exposing too many public members.
 * Thresholds: >30% public members OR many getter/setter-only fields.
 */
public class DeficientEncapsulationDetector {

    public static List<String> detect(File file) throws IOException {
        List<String> issues = new ArrayList<>();
        List<String> lines = Files.readAllLines(file.toPath());
        int totalMembers = 0;
        int publicCount = 0;
        int getters = 0;
        int setters = 0;
        for (String l : lines) {
            String t = l.trim();
            if (t.isEmpty()) continue;
            // count member-like lines
            if (t.endsWith(";") || t.endsWith("{") || t.endsWith("}")) {
                totalMembers++;
                if (t.startsWith("public ")) publicCount++;
            }
            if (t.matches("public\\s+[\\w\\<\\>\\[\\]]+\\s+get[A-Z]\\w*\\s*\\(\\s*\\)\\s*\\{?")) getters++;
            if (t.matches("public\\s+void\\s+set[A-Z]\\w*\\s*\\(.*\\)\\s*\\{?")) setters++;
        }
        if (totalMembers > 0) {
            double publicRatio = (double) publicCount / totalMembers;
            if (publicRatio > 0.30) {
                issues.add(String.format("DeficientEncapsulation in %s: publicRatio=%.2f (%d/%d)", file.getName(), publicRatio, publicCount, totalMembers));
            }
            if ((getters + setters) > 10 && (double)(getters + setters) / totalMembers > 0.5) {
                issues.add(String.format("DeficientEncapsulation in %s: many getters/setters → %d", file.getName(), getters + setters));
            }
        }
        return issues;
    }
}
