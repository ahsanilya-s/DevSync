package com.devsync.detectors;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Detect classes/interfaces that might be unnecessary abstraction:
 * Heuristic: a class with <2 members and abstract/interface or inheritance depth high & no children in project (approx).
 * For simplicity we flag classes with <2 members and abstract/interface or classes with name ending with 'Adapter'/'Base' but few members.
 */
public class UnnecessaryAbstractionDetector {

    public static List<String> detect(File file) throws IOException {
        List<String> issues = new ArrayList<>();
        List<String> lines = Files.readAllLines(file.toPath());
        String content = String.join("\n", lines);
        // find class or interface blocks
        String[] tokens = content.split("\\b(class|interface|enum)\\b");
        // simple heuristic: count members by counting semicolons in class body
        // find occurrences of "class Name" and body
        Pattern p = Pattern.compile("(public\\s+)?(abstract\\s+)?(class|interface)\\s+(\\w+)\\s*([^\\{]*)\\{", Pattern.MULTILINE);
        Matcher m = p.matcher(content);
        while (m.find()) {
            String cls = m.group(4);
            int start = m.end();
            // find body until matching brace
            int brace = 1;
            int idx = start;
            int len = content.length();
            while (idx < len && brace > 0) {
                char c = content.charAt(idx++);
                if (c == '{') brace++;
                else if (c == '}') brace--;
            }
            String body = content.substring(start, Math.min(idx, len));
            int members = body.length() - body.replace(";", "").length();
            boolean isAbstract = m.group(2) != null && m.group(2).contains("abstract");
            if (members < 2 && (isAbstract || cls.toLowerCase().contains("base") || cls.toLowerCase().contains("adapter"))) {
                issues.add(String.format("UnnecessaryAbstraction in %s: class=%s members=%d", file.getName(), cls, members));
            }
        }
        return issues;
    }
}
