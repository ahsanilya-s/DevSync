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
    public static final Map<String, Boolean> DEFAULT_DETECTOR_ENABLED = Map.of(
        "MissingDefaultDetector", true,
        "EmptyCatchDetector", true,
        "LongMethodDetector", true,
        "LongParameterListDetector", true,
        "MagicNumberDetector", true,
        "LongIdentifierDetector", true
    );
    
    public static final int DEFAULT_MAX_METHOD_LENGTH = 50;
    public static final int DEFAULT_MAX_PARAMETER_COUNT = 5;
    public static final int DEFAULT_MAX_IDENTIFIER_LENGTH = 30;
    
    public static boolean isDetectorEnabled(String detectorName, UserSettings settings) {
        if (settings == null) {
            return DEFAULT_DETECTOR_ENABLED.getOrDefault(detectorName, false);
        }
        
        return switch (detectorName) {
            case "MissingDefaultDetector" -> settings.getMissingDefaultEnabled();
            case "EmptyCatchDetector" -> settings.getEmptyCatchEnabled();
            case "LongMethodDetector" -> settings.getLongMethodEnabled();
            case "LongParameterListDetector" -> settings.getLongParameterEnabled();
            case "MagicNumberDetector" -> settings.getMagicNumberEnabled();
            case "LongIdentifierDetector" -> settings.getLongIdentifierEnabled();
            default -> false;
        };
    }
    
    public static int getMaxMethodLength(UserSettings settings) {
        return settings != null ? settings.getMaxMethodLength() : DEFAULT_MAX_METHOD_LENGTH;
    }
    
    public static int getMaxParameterCount(UserSettings settings) {
        return settings != null ? settings.getMaxParameterCount() : DEFAULT_MAX_PARAMETER_COUNT;
    }
    
    public static int getMaxIdentifierLength(UserSettings settings) {
        return settings != null ? settings.getMaxIdentifierLength() : DEFAULT_MAX_IDENTIFIER_LENGTH;
    }
    
    public static boolean shouldExclude(String path) {
        return EXCLUDED_PATTERNS.stream()
            .anyMatch(pattern -> path.toLowerCase().contains(pattern.toLowerCase()));
    }
}