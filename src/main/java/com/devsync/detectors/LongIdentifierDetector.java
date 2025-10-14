package com.devsync.detectors;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

/**
 * Detects overly long identifiers: threshold > 30 chars or >5 words using camelCase/underscore split.
 */
public class LongIdentifierDetector {

    private static final int CHAR_THRESHOLD = 32;
    private static final int WORD_THRESHOLD = 5;

    public static List<String> detect(File file) throws IOException {
        List<String> issues = new ArrayList<>();
        List<String> lines = Files.readAllLines(file.toPath());
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            // rough find tokens that look like identifiers: variable or method names
            String[] tokens = line.split("[^A-Za-z0-9_]");
            for (String tok : tokens) {
                if (tok.length() == 0) continue;
                // skip if token looks like a keyword or type
                if (tok.matches("\\d+")) continue;
                // count words in camelCase or underscore
                String[] parts = tok.split("(?=[A-Z])|_");
                int words = parts.length;
                if (tok.length() > CHAR_THRESHOLD || words > WORD_THRESHOLD) {
                    issues.add(String.format("LongIdentifier in %s:%d → '%s' length=%d words=%d",
                            file.getName(), i + 1, tok, tok.length(), words));
                }
            }
        }
        return issues;
    }
}
