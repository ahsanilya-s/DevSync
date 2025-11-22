package com.devsync.detectors;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import org.junit.jupiter.api.BeforeEach;
import java.util.List;

public abstract class DetectorTestBase {
    
    protected JavaParser parser;

    @BeforeEach
    void setUp() {
        parser = new JavaParser();
    }
    
    protected CompilationUnit parseCode(String code) {
        return parser.parse(code).getResult().orElse(null);
    }
    
    protected boolean hasIssue(List<String> issues, String pattern) {
        return issues.stream().anyMatch(issue -> issue.contains(pattern));
    }
    
    protected int countSeverity(List<String> issues, String severity) {
        return (int) issues.stream().filter(issue -> issue.startsWith(severity)).count();
    }
}