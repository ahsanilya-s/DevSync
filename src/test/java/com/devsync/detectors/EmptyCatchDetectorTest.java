package com.devsync.detectors;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class EmptyCatchDetectorTest extends DetectorTestBase {
    private final EmptyCatchDetector detector = new EmptyCatchDetector();

    @Test
    void shouldReportEmptyCatch() {
        String code = "class C{ void m(){ try{ } catch(IOException e){} } }";
        List<String> issues = detector.detect(parseCode(code));
        assertTrue(hasIssue(issues, "EmptyCatch"));
    }

    @Test
    void shouldNotReportCatchWithLogging() {
        String code = "class C{ void m(){ try{} catch(Exception e){ System.out.println(e); } } }";
        List<String> issues = detector.detect(parseCode(code));
        assertFalse(hasIssue(issues, "EmptyCatch"));
    }

    @Test
    void shouldNotReportCatchWithExplanatoryComment() {
        String code = "class C{ void m(){ try{} catch(Exception e){ /* ignore */ } } }";
        List<String> issues = detector.detect(parseCode(code));
        // Detector may consider comment as documentation; assert not reported
        assertFalse(hasIssue(issues, "EmptyCatch"));
    }
}

