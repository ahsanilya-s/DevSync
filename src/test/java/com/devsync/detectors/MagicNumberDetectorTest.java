package com.devsync.detectors;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class MagicNumberDetectorTest extends DetectorTestBase {
    private final MagicNumberDetector detector = new MagicNumberDetector();

    @Test
    void shouldReportBusinessMagicNumber() {
        String code = "class C{ int calculate(){ return 42 * 2; } }";
        List<String> issues = detector.detect(parseCode(code));
        assertTrue(hasIssue(issues, "MagicNumber"));
    }

    @Test
    void shouldNotReportSimpleArrayIndex() {
        String code = "class C{ void m(){ int[] a = new int[5]; int x = a[1]; } }";
        List<String> issues = detector.detect(parseCode(code));
        assertFalse(hasIssue(issues, "MagicNumber"));
    }

    @Test
    void shouldReportRepeatedMagicNumber() {
        StringBuilder sb = new StringBuilder();
        sb.append("class C{ void m(){\n");
        for (int i = 0; i < 4; i++) sb.append("int a" + i + " = 999;\n");
        sb.append("}\n}");

        List<String> issues = detector.detect(parseCode(sb.toString()));
        assertTrue(hasIssue(issues, "MagicNumber"));
    }
}

