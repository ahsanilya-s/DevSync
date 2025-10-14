package com.devsync.detectors;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Detects magic numbers (literals) except allowed small values 0,1,-1
 */
public class MagicNumberDetector {

    // pattern for numbers (integers and decimals), not inside quotes (simple approach)
    private static final Pattern NUMBER_PATTERN = Pattern.compile("(?<![\\w\\.\"])\\b-?\\d+(?:\\.\\d+)?\\b");

    public static List<String> detect(File file) throws IOException {
        List<String> issues = new ArrayList<>();
        List<String> lines = Files.readAllLines(file.toPath());
        int lineNo = 0;
        for (String raw : lines) {
            lineNo++;
            String line = raw;
            // skip comments roughly
            String clean = line.split("//")[0];
            Matcher m = NUMBER_PATTERN.matcher(clean);
            while (m.find()) {
                String num = m.group();
                if ("0".equals(num) || "1".equals(num) || "-1".equals(num)) continue;
                issues.add(String.format("MagicNumber in %s:%d → literal=%s ; context=\"%s\"",
                        file.getName(), lineNo, num, clean.trim()));
            }
        }
        return issues;
    }
}
