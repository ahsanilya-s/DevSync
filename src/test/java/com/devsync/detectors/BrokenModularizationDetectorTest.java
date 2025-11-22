package com.devsync.detectors;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class BrokenModularizationDetectorTest extends DetectorTestBase {
    private final BrokenModularizationDetector detector = new BrokenModularizationDetector();

    @Test
    void shouldReportLowCohesion() {
        // One field and many methods -> low cohesion
        StringBuilder sb = new StringBuilder();
        sb.append("class C{ int f; ");
        for (int i = 0; i < 9; i++) {
            sb.append("void m" + i + "(){} ");
        }
        sb.append("}");

        List<String> issues = detector.detect(parseCode(sb.toString()));
        assertTrue(hasIssue(issues, "BrokenModularization"));
    }

    @Test
    void shouldReportHighExternalDependencies() {
        // One field and some methods, with a method that calls many external scopes
        String code = "class C{ int f; void a(){} void b(){} void m(){ a1.foo(); b2.bar(); c3.baz(); d4.qux(); e5.f(); f6.g(); g7.h(); h8.i(); i9.j(); j10.k(); } }";
        List<String> issues = detector.detect(parseCode(code));
        assertTrue(hasIssue(issues, "BrokenModularization"));
    }

    @Test
    void shouldReportMultipleResponsibilities() {
        // Methods named to indicate multiple responsibilities
        String code = "class C{ int f; void saveData(){} void validateData(){} void compute(){} void format(){} void display(){} }";
        List<String> issues = detector.detect(parseCode(code));
        assertTrue(hasIssue(issues, "BrokenModularization"));
    }
}
