package com.devsync.detectors;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class LongIdentifierDetectorTest extends DetectorTestBase {
    private final LongIdentifierDetector detector = new LongIdentifierDetector();

    @Test
    void shouldNotReportShortNames() {
        String code = "class C{ void m(){ int x=0; } }";
        List<String> issues = detector.detect(parseCode(code));
        assertFalse(hasIssue(issues, "LongIdentifier"));
    }

    @Test
    void shouldReportLongVariableName() {
        String code = "class C{ void m(){ int thisIsAnExcessivelyLongVariableNameOverTwentyChars = 0; } }";
        List<String> issues = detector.detect(parseCode(code));
        assertTrue(hasIssue(issues, "LongIdentifier"));
    }

    @Test
    void shouldReportLongMethodName() {
        String code = "class C{ public void thisIsAnIncrediblyExcessiveMethodNameThatIsTooLong(){} }";
        List<String> issues = detector.detect(parseCode(code));
        assertTrue(hasIssue(issues, "LongIdentifier"));
    }
}

