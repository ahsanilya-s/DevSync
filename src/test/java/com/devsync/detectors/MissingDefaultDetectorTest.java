package com.devsync.detectors;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

class MissingDefaultDetectorTest extends DetectorTestBase {
    
    private final MissingDefaultDetector detector = new MissingDefaultDetector();
    
    @Test
    void shouldDetectMissingDefault() {
        String code = """
            public class Test {
                public void method(Status status) {
                    switch(status) {
                        case ACTIVE: break;
                        case INACTIVE: break;
                    }
                }
            }
            """;
        
        List<String> issues = detector.detect(parseCode(code));
        assertTrue(hasIssue(issues, "MissingDefault"));
    }
    
    @Test
    void shouldNotReportWhenDefaultExists() {
        String code = """
            public class Test {
                public void method(Status status) {
                    switch(status) {
                        case ACTIVE: break;
                        case INACTIVE: break;
                        default: throw new IllegalArgumentException();
                    }
                }
            }
            """;
        
        List<String> issues = detector.detect(parseCode(code));
        assertFalse(hasIssue(issues, "MissingDefault"));
    }
}