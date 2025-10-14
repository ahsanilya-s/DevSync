package com.devsync.detectors;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Detects methods with many parameters:
 * - Threshold: > 4 parameters
 * - Distinct types > 3 flagged (simple heuristic by checking type tokens)
 */
public class LongParameterListDetector {

    private static final int PARAM_THRESHOLD = 4;
    private static final int DISTINCT_TYPE_THRESHOLD = 3;

    private static final Pattern METHOD_SIG = Pattern.compile(
            "(public|protected|private|static|\\s)*[\\w\\<\\>\\[\\]]+\\s+\\w+\\s*\\(([^)]*)\\)\\s*\\{?");

    public static List<String> detect(File file) throws IOException {
        List<String> issues = new ArrayList<>();
        List<String> lines = Files.readAllLines(file.toPath());
        String content = String.join("\n", lines);
        Matcher m = METHOD_SIG.matcher(content);
        while (m.find()) {
            String params = m.group(2).trim();
            if (params.isEmpty()) continue;
            String[] parts = params.split(",");
            int paramCount = parts.length;
            Set<String> types = new HashSet<>();
            for (String p : parts) {
                String[] tokens = p.trim().split("\\s+");
                if (tokens.length >= 2) {
                    types.add(tokens[0]);
                } else if (tokens.length == 1) {
                    // if only one token, assume type missing (varargs or inference) -- count as distinct
                    types.add(tokens[0]);
                }
            }
            if (paramCount > PARAM_THRESHOLD || types.size() > DISTINCT_TYPE_THRESHOLD) {
                String sigPreview = m.group(0).replace("\n", " ").trim();
                issues.add(String.format("LongParameterList in %s: signature=\"%s\", params=%d, distinctTypes=%d",
                        file.getName(), sigPreview, paramCount, types.size()));
            }
        }
        return issues;
    }
}
