package com.devsync.config;

import com.devsync.model.UserSettings;
import java.util.*;

public class AnalysisConfig {
    
    public static final Map<String, Double> SEVERITY_THRESHOLDS = Map.of(
        "CRITICAL", 0.8,
        "HIGH", 0.6,
        "MEDIUM", 0.4,
        "LOW", 0.2
    );
    
    public static final Set<String> EXCLUDED_PATTERNS = Set.of(
        "test", "Test", "tests", "target", "build", ".git"
    );
    
    // Default values when no user settings available
    // Default thresholds
    public static final int DEFAULT_MAX_METHOD_LENGTH = 50;
    public static final int DEFAULT_MAX_METHOD_COMPLEXITY = 10;
    public static final int DEFAULT_MAX_PARAMETER_COUNT = 5;
    public static final int DEFAULT_MAX_IDENTIFIER_LENGTH = 30;
    public static final int DEFAULT_MIN_IDENTIFIER_LENGTH = 3;
    public static final int DEFAULT_MAGIC_NUMBER_THRESHOLD = 3;
    public static final int DEFAULT_MAX_CONDITIONAL_OPERATORS = 4;
    public static final int DEFAULT_MAX_NESTING_DEPTH = 3;
    public static final int DEFAULT_MAX_STATEMENT_TOKENS = 40;
    public static final int DEFAULT_MAX_STATEMENT_CHARS = 250;
    public static final int DEFAULT_MAX_METHOD_CHAIN_LENGTH = 5;
    public static final int DEFAULT_MAX_RESPONSIBILITIES = 3;
    public static final double DEFAULT_MIN_COHESION_INDEX = 0.4;
    public static final int DEFAULT_MAX_COUPLING_COUNT = 6;
    public static final int DEFAULT_MAX_ABSTRACTION_USAGE = 1;
    
    // Detector enabled checks
    public static boolean isDetectorEnabled(String detectorName, UserSettings settings) {
        if (settings == null) return true;
        
        return switch (detectorName) {
            case "LongMethodDetector" -> settings.getLongMethodEnabled();
            case "LongParameterListDetector" -> settings.getLongParameterEnabled();
            case "LongIdentifierDetector" -> settings.getLongIdentifierEnabled();
            case "MagicNumberDetector" -> settings.getMagicNumberEnabled();
            case "MissingDefaultDetector" -> settings.getMissingDefaultEnabled();
            case "EmptyCatchDetector" -> settings.getEmptyCatchEnabled();
            case "ComplexConditionalDetector" -> settings.getComplexConditionalEnabled();
            case "LongStatementDetector" -> settings.getLongStatementEnabled();
            case "BrokenModularizationDetector" -> settings.getBrokenModularizationEnabled();
            case "DeficientEncapsulationDetector" -> settings.getDeficientEncapsulationEnabled();
            case "UnnecessaryAbstractionDetector" -> settings.getUnnecessaryAbstractionEnabled();
            default -> true;
        };
    }
    
    // Long Method parameters
    public static int getMaxMethodLength(UserSettings settings) {
        return settings != null && settings.getMaxMethodLength() != null ? 
            settings.getMaxMethodLength() : DEFAULT_MAX_METHOD_LENGTH;
    }
    
    public static int getMaxMethodComplexity(UserSettings settings) {
        return settings != null && settings.getMaxMethodComplexity() != null ? 
            settings.getMaxMethodComplexity() : DEFAULT_MAX_METHOD_COMPLEXITY;
    }
    
    // Long Parameter List parameters
    public static int getMaxParameterCount(UserSettings settings) {
        return settings != null && settings.getMaxParameterCount() != null ? 
            settings.getMaxParameterCount() : DEFAULT_MAX_PARAMETER_COUNT;
    }
    
    // Long Identifier parameters
    public static int getMaxIdentifierLength(UserSettings settings) {
        return settings != null && settings.getMaxIdentifierLength() != null ? 
            settings.getMaxIdentifierLength() : DEFAULT_MAX_IDENTIFIER_LENGTH;
    }
    
    public static int getMinIdentifierLength(UserSettings settings) {
        return settings != null && settings.getMinIdentifierLength() != null ? 
            settings.getMinIdentifierLength() : DEFAULT_MIN_IDENTIFIER_LENGTH;
    }
    
    // Magic Number parameters
    public static int getMagicNumberThreshold(UserSettings settings) {
        return settings != null && settings.getMagicNumberThreshold() != null ? 
            settings.getMagicNumberThreshold() : DEFAULT_MAGIC_NUMBER_THRESHOLD;
    }
    
    // Complex Conditional parameters
    public static int getMaxConditionalOperators(UserSettings settings) {
        return settings != null && settings.getMaxConditionalOperators() != null ? 
            settings.getMaxConditionalOperators() : DEFAULT_MAX_CONDITIONAL_OPERATORS;
    }
    
    public static int getMaxNestingDepth(UserSettings settings) {
        return settings != null && settings.getMaxNestingDepth() != null ? 
            settings.getMaxNestingDepth() : DEFAULT_MAX_NESTING_DEPTH;
    }
    
    // Long Statement parameters
    public static int getMaxStatementTokens(UserSettings settings) {
        return settings != null && settings.getMaxStatementTokens() != null ? 
            settings.getMaxStatementTokens() : DEFAULT_MAX_STATEMENT_TOKENS;
    }
    
    public static int getMaxStatementChars(UserSettings settings) {
        return settings != null && settings.getMaxStatementChars() != null ? 
            settings.getMaxStatementChars() : DEFAULT_MAX_STATEMENT_CHARS;
    }
    
    public static int getMaxMethodChainLength(UserSettings settings) {
        return settings != null && settings.getMaxMethodChainLength() != null ? 
            settings.getMaxMethodChainLength() : DEFAULT_MAX_METHOD_CHAIN_LENGTH;
    }
    
    // Broken Modularization parameters
    public static int getMaxResponsibilities(UserSettings settings) {
        return settings != null && settings.getMaxResponsibilities() != null ? 
            settings.getMaxResponsibilities() : DEFAULT_MAX_RESPONSIBILITIES;
    }
    
    public static double getMinCohesionIndex(UserSettings settings) {
        return settings != null && settings.getMinCohesionIndex() != null ? 
            settings.getMinCohesionIndex() : DEFAULT_MIN_COHESION_INDEX;
    }
    
    public static int getMaxCouplingCount(UserSettings settings) {
        return settings != null && settings.getMaxCouplingCount() != null ? 
            settings.getMaxCouplingCount() : DEFAULT_MAX_COUPLING_COUNT;
    }
    
    // Unnecessary Abstraction parameters
    public static int getMaxAbstractionUsage(UserSettings settings) {
        return settings != null && settings.getMaxAbstractionUsage() != null ? 
            settings.getMaxAbstractionUsage() : DEFAULT_MAX_ABSTRACTION_USAGE;
    }
    
    public static boolean shouldExclude(String path) {
        return EXCLUDED_PATTERNS.stream()
            .anyMatch(pattern -> path.toLowerCase().contains(pattern.toLowerCase()));
    }
}