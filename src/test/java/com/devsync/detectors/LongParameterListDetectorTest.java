package com.devsync.detectors;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class LongParameterListDetectorTest extends DetectorTestBase {
    private final LongParameterListDetector detector = new LongParameterListDetector();

    @Test
    void shouldNotReportShortParams() {
        String code = "class C{ void m(int a, String b){} }";
        List<String> issues = detector.detect(parseCode(code));
        assertFalse(hasIssue(issues, "LongParameterList"));
    }

    @Test
    void shouldReportManyParametersMethod() {
        String code = "class C{ void big(int a,int b,int c,int d,int e,int f,int g){} }";
        List<String> issues = detector.detect(parseCode(code));
        assertTrue(hasIssue(issues, "LongParameterList"));
    }

    @Test
    void shouldReportConstructorWithManyParams() {
        String code = "class C{ C(int a,int b,int c,int d,int e){} }";
        List<String> issues = detector.detect(parseCode(code));
        assertTrue(hasIssue(issues, "LongParameterList"));
    }
}

