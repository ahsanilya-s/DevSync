package com.devsync.detectors;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.Expression;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ComplexConditionalDetectorTest {

    @Test
    void testSimpleConditionNotReported() {
        String src = "class T{ void m(){ if(a && b){} } }";
        CompilationUnit cu = StaticJavaParser.parse(src);

        ComplexConditionalDetector detector = new ComplexConditionalDetector();
        List<String> issues = detector.detect(cu);
        assertNotNull(issues);
        assertTrue(issues.isEmpty(), "Simple condition should not be reported as complex");
    }

    @Test
    void testComplexConditionReported() {
        // Build a condition with multiple operators, parentheses, method calls and negations
        String src = "class T{ void m(){ if((a() && b()) || (!c && (d || e)) && f || g && h || i){} } }";
        CompilationUnit cu = StaticJavaParser.parse(src);

        ComplexConditionalDetector detector = new ComplexConditionalDetector();
        List<String> issues = detector.detect(cu);

        assertNotNull(issues);
        assertFalse(issues.isEmpty(), "Complex condition should be detected and reported");

        // Ensure reported message contains expected markers
        String msg = issues.get(0);
        assertTrue(msg.contains("ComplexConditional"), "Report should contain the detector label");
        assertTrue(msg.contains("Operators:"), "Report should include operator count");
        assertTrue(msg.contains("Suggestions:"), "Report should include suggestions");
    }

    @Test
    void testPrivateHelpersViaReflection() throws Exception {
        // Construct an expression with 7 binary operators (8 operands) -> operatorCount = 7
        Expression expr = StaticJavaParser.parseExpression("a && b && c && d && e && f && g && h");

        // Create ConditionalInfo instance via reflection (private static inner class)
        Class<?> outer = Class.forName("com.devsync.detectors.ComplexConditionalDetector");
        Class<?> conditionalInfoClass = Class.forName("com.devsync.detectors.ComplexConditionalDetector$ConditionalInfo");

        Constructor<?> ctor = conditionalInfoClass.getDeclaredConstructor(String.class, int.class, com.github.javaparser.ast.expr.Expression.class);
        ctor.setAccessible(true);
        Object condInfo = ctor.newInstance("If statement", 1, expr);

        // Instantiate detector and call private methods
        Object detector = outer.getDeclaredConstructor().newInstance();

        Method calcMethod = outer.getDeclaredMethod("calculateComplexityScore", conditionalInfoClass);
        calcMethod.setAccessible(true);
        double score = (double) calcMethod.invoke(detector, condInfo);

        Method severityMethod = outer.getDeclaredMethod("getSeverity", conditionalInfoClass, double.class);
        severityMethod.setAccessible(true);
        String severity = (String) severityMethod.invoke(detector, condInfo, score);

        Method analysisMethod = outer.getDeclaredMethod("generateAnalysis", conditionalInfoClass);
        analysisMethod.setAccessible(true);
        String analysis = (String) analysisMethod.invoke(detector, condInfo);

        Method suggestionsMethod = outer.getDeclaredMethod("generateSuggestions", conditionalInfoClass);
        suggestionsMethod.setAccessible(true);
        String suggestions = (String) suggestionsMethod.invoke(detector, condInfo);

        // Validate expectations based on the expression
        assertTrue(score >= 0.0 && score <= 1.0, "Complexity score should be normalized between 0 and 1");
        // operatorCount == 7 -> should be at least yellow (🟡) per detector logic (>6)
        assertEquals("🟡", severity, "Severity should be 🟡 for 7 operators");
        assertTrue(analysis.contains("Too many logical operators") || analysis.length() > 0, "Analysis should mention issues or be non-empty");
        assertTrue(suggestions.contains("Extract boolean methods"), "Suggestions should propose extracting boolean methods for many operators");
    }

    @Test
    void testReadabilityAndShouldReport() throws Exception {
        // Expression crafted to include method calls, nested parentheses, mixed operators and multiple negations
        Expression expr = StaticJavaParser.parseExpression("a() && (b || c) && (!d) && (!e)");

        Class<?> outer = Class.forName("com.devsync.detectors.ComplexConditionalDetector");
        Class<?> conditionalInfoClass = Class.forName("com.devsync.detectors.ComplexConditionalDetector$ConditionalInfo");

        Constructor<?> ctor = conditionalInfoClass.getDeclaredConstructor(String.class, int.class, com.github.javaparser.ast.expr.Expression.class);
        ctor.setAccessible(true);
        Object condInfo = ctor.newInstance("If statement", 10, expr);

        Object detector = outer.getDeclaredConstructor().newInstance();

        Method calcMethod = outer.getDeclaredMethod("calculateComplexityScore", conditionalInfoClass);
        calcMethod.setAccessible(true);
        double score = (double) calcMethod.invoke(detector, condInfo);

        Method readabilityMethod = outer.getDeclaredMethod("calculateReadabilityScore", conditionalInfoClass);
        readabilityMethod.setAccessible(true);
        double readability = (double) readabilityMethod.invoke(detector, condInfo);

        Method shouldReportMethod = outer.getDeclaredMethod("shouldReport", conditionalInfoClass, double.class);
        shouldReportMethod.setAccessible(true);
        boolean shouldReport = (boolean) shouldReportMethod.invoke(detector, condInfo, score);

        assertTrue(readability >= 0.0 && readability <= 1.0, "Readability score must be normalized");
        // operatorCount is 4 -> meets BASE_COMPLEXITY_THRESHOLD so shouldReport should be true
        assertTrue(shouldReport, "Detector should report this condition because operator count reaches the base threshold");
    }
}
