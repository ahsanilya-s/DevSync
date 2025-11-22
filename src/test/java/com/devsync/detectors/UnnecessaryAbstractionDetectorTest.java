package com.devsync.detectors;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class UnnecessaryAbstractionDetectorTest extends DetectorTestBase {
    private final UnnecessaryAbstractionDetector detector = new UnnecessaryAbstractionDetector();

    @Test
    void shouldReportSingleMethodInterface() {
        String code = "interface I{ void m(); } class A implements I{ public void m(){} }";
        List<String> issues = detector.detect(parseCode(code));
        assertTrue(hasIssue(issues, "UnnecessaryAbstraction"));
    }

    @Test
    void shouldNotReportMultiMethodInterface() {
        String code = "interface I{ void a(); void b(); void c(); }";
        List<String> issues = detector.detect(parseCode(code));
        assertFalse(hasIssue(issues, "UnnecessaryAbstraction"));
    }

    @Test
    void shouldReportInterfaceSimpleWrapper() {
        String code = "interface I{ void m(); } class A implements I{ public void m(){} } class B implements I{ public void m(){} }";
        List<String> issues = detector.detect(parseCode(code));
        // Detector flags single-method interfaces as potentially unnecessary
        assertTrue(hasIssue(issues, "UnnecessaryAbstraction"));
    }
}

