package com.devsync.detectors;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class LongStatementDetectorTest extends DetectorTestBase {
    private final LongStatementDetector detector = new LongStatementDetector();

    @Test
    void shouldNotReportShortStatement() {
        String code = "class C{ void m(){ int a=1+2; } }";
        List<String> issues = detector.detect(parseCode(code));
        assertFalse(hasIssue(issues, "LongStatement"));
    }

    @Test
    void shouldReportLongExpressionStatement() {
        StringBuilder expr = new StringBuilder();
        expr.append("class C{ void m(){ String s = \"");
        for (int i = 0; i < 150; i++) expr.append("x");
        expr.append("\"; } }");

        List<String> issues = detector.detect(parseCode(expr.toString()));
        assertTrue(hasIssue(issues, "LongStatement"));
    }

    @Test
    void shouldReportLongMethodChain() {
        String code = "class C{ void m(){ a.b().c().d().e().f().g(); } }";
        List<String> issues = detector.detect(parseCode(code));
        assertTrue(hasIssue(issues, "LongStatement"));
    }
}

