package com.devsync.detectors;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class UnusedVariableDetectorTest extends DetectorTestBase {
    private final UnusedVariableDetector detector = new UnusedVariableDetector();

    @Test
    void shouldDetectUnusedLocalVariable() {
        String code = "class C{ void m(){ int unused = 5; int used = 10; System.out.println(used); } }";
        List<String> issues = detector.detect(parseCode(code));
        assertTrue(hasIssue(issues, "Variable 'unused'"));
        assertFalse(hasIssue(issues, "Variable 'used'"));
    }

    @Test
    void shouldDetectUnusedParameter() {
        String code = "class C{ void m(int unusedParam, int usedParam){ System.out.println(usedParam); } }";
        List<String> issues = detector.detect(parseCode(code));
        assertTrue(hasIssue(issues, "Variable 'unusedParam'"));
        assertFalse(hasIssue(issues, "Variable 'usedParam'"));
    }

    @Test
    void shouldNotReportUsedVariable() {
        String code = "class C{ void m(){ int x = 5; int y = x + 10; System.out.println(y); } }";
        List<String> issues = detector.detect(parseCode(code));
        assertEquals(0, issues.size());
    }

    @Test
    void shouldDetectMultipleUnusedVariables() {
        String code = "class C{ void m(){ int a = 1; int b = 2; int c = 3; System.out.println(b); } }";
        List<String> issues = detector.detect(parseCode(code));
        assertTrue(hasIssue(issues, "Variable 'a'"));
        assertTrue(hasIssue(issues, "Variable 'c'"));
        assertFalse(hasIssue(issues, "Variable 'b'"));
    }
}
