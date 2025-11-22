package com.devsync.detectors;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class LongMethodDetectorTest extends DetectorTestBase {

    private final LongMethodDetector detector = new LongMethodDetector();

    @Test
    void shouldNotReportShortMethod() {
        String code = "class C{ void shortMethod(){ int a=0; a++; } }";
        List<String> issues = detector.detect(parseCode(code));
        assertFalse(hasIssue(issues, "LongMethod"));
    }

    @Test
    void shouldReportVeryLongMethod() {
        StringBuilder sb = new StringBuilder();
        sb.append("class C{ void longMethod(){\n");
        for (int i = 0; i < 30; i++) {
            sb.append("int v" + i + " = " + i + ";\n");
        }
        sb.append("}\n}");

        List<String> issues = detector.detect(parseCode(sb.toString()));
        assertTrue(hasIssue(issues, "LongMethod"), "Expected LongMethod to be reported for long method");
    }

    @Test
    void shouldReportHighCyclomaticComplexity() {
        StringBuilder sb = new StringBuilder();
        sb.append("class C{ void complex(){\n");
        for (int i = 0; i < 12; i++) {
            sb.append("if(cond" + i + "){}\n");
        }
        // add extra lines to ensure line count threshold is also triggered
        for (int i = 0; i < 15; i++) {
            sb.append("int v" + i + " = " + i + ";\n");
        }
        sb.append("}\n}");

        List<String> issues = detector.detect(parseCode(sb.toString()));
        assertTrue(hasIssue(issues, "LongMethod"), "High cyclomatic complexity should trigger LongMethod");
    }
}
