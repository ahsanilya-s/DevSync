package com.devsync.detectors;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

/**
 * Heuristic for broken modularization:
 * - Many imports from other packages (coupling)
 * - High number of public fields or many static references (very rough)
 * Thresholds: imports > 10 or many public fields (>5)
 */
public class BrokenModularizationDetector {

    public static List<String> detect(File file) throws IOException {
        List<String> issues = new ArrayList<>();
        List<String> lines = Files.readAllLines(file.toPath());
        int importCount = 0;
        int publicFieldCount = 0;
        for (String l : lines) {
            String t = l.trim();
            if (t.startsWith("import ")) importCount++;
            // public fields heuristic: "public Type name;"
            if (t.matches("public\\s+[\\w\\<\\>\\[\\]]+\\s+\\w+\\s*;")) publicFieldCount++;
        }
        if (importCount > 10) {
            issues.add(String.format("BrokenModularization in %s: imports=%d (high coupling)", file.getName(), importCount));
        }
        if (publicFieldCount > 5) {
            issues.add(String.format("BrokenModularization in %s: publicFields=%d (low encapsulation)", file.getName(), publicFieldCount));
        }
        return issues;
    }
}
