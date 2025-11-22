package com.devsync.detectors;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class DeficientEncapsulationDetectorTest extends DetectorTestBase {
    private final DeficientEncapsulationDetector detector = new DeficientEncapsulationDetector();

    @Test
    void shouldReportPublicMutableField() {
        String code = "class C{ public int x; }";
        List<String> issues = detector.detect(parseCode(code));
        assertTrue(hasIssue(issues, "DeficientEncapsulation"));
    }

    @Test
    void shouldNotReportPrivateFinalWithAccessor() {
        // private final field is immutable; risk should be low even if accessor exists
        String code = "class C{ private final int x = 1; public int getX(){return x;} }";
        List<String> issues = detector.detect(parseCode(code));
        assertFalse(hasIssue(issues, "DeficientEncapsulation"));
    }

    @Test
    void shouldReportPublicFinalField() {
        String code = "class C{ public final int CONST = 5; }";
        List<String> issues = detector.detect(parseCode(code));
        assertTrue(hasIssue(issues, "DeficientEncapsulation"));
    }
}
