package com.devsync.controller;

import com.devsync.dto.DetectorConfigDTO;
import com.devsync.model.UserSettings;
import com.devsync.repository.UserSettingsRepository;
import com.devsync.services.AIAssistantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/settings")
@CrossOrigin(origins = "*")
public class SettingsController {
    
    @Autowired
    private UserSettingsRepository userSettingsRepository;
    
    @Autowired
    private AIAssistantService aiAssistantService;
    
    @GetMapping("/{userId}")
    public ResponseEntity<UserSettings> getSettings(@PathVariable String userId) {
        UserSettings settings = userSettingsRepository.findByUserId(userId)
            .orElse(new UserSettings(userId));
        return ResponseEntity.ok(settings);
    }
    
    @PostMapping("/{userId}")
    public ResponseEntity<UserSettings> saveSettings(@PathVariable String userId, @RequestBody UserSettings settings) {
        UserSettings existingSettings = userSettingsRepository.findByUserId(userId)
            .orElse(new UserSettings(userId));
        
        existingSettings.setUserId(userId);
        
        // Long Method
        existingSettings.setLongMethodEnabled(settings.getLongMethodEnabled());
        existingSettings.setMaxMethodLength(settings.getMaxMethodLength());
        existingSettings.setMaxMethodComplexity(settings.getMaxMethodComplexity());
        
        // Long Parameter List
        existingSettings.setLongParameterEnabled(settings.getLongParameterEnabled());
        existingSettings.setMaxParameterCount(settings.getMaxParameterCount());
        
        // Long Identifier
        existingSettings.setLongIdentifierEnabled(settings.getLongIdentifierEnabled());
        existingSettings.setMaxIdentifierLength(settings.getMaxIdentifierLength());
        existingSettings.setMinIdentifierLength(settings.getMinIdentifierLength());
        
        // Magic Number
        existingSettings.setMagicNumberEnabled(settings.getMagicNumberEnabled());
        existingSettings.setMagicNumberThreshold(settings.getMagicNumberThreshold());
        
        // Missing Default
        existingSettings.setMissingDefaultEnabled(settings.getMissingDefaultEnabled());
        
        // Empty Catch
        existingSettings.setEmptyCatchEnabled(settings.getEmptyCatchEnabled());
        
        // Complex Conditional
        existingSettings.setComplexConditionalEnabled(settings.getComplexConditionalEnabled());
        existingSettings.setMaxConditionalOperators(settings.getMaxConditionalOperators());
        existingSettings.setMaxNestingDepth(settings.getMaxNestingDepth());
        
        // Long Statement
        existingSettings.setLongStatementEnabled(settings.getLongStatementEnabled());
        existingSettings.setMaxStatementTokens(settings.getMaxStatementTokens());
        existingSettings.setMaxStatementChars(settings.getMaxStatementChars());
        existingSettings.setMaxMethodChainLength(settings.getMaxMethodChainLength());
        
        // Broken Modularization
        existingSettings.setBrokenModularizationEnabled(settings.getBrokenModularizationEnabled());
        existingSettings.setMaxResponsibilities(settings.getMaxResponsibilities());
        existingSettings.setMinCohesionIndex(settings.getMinCohesionIndex());
        existingSettings.setMaxCouplingCount(settings.getMaxCouplingCount());
        
        // Deficient Encapsulation
        existingSettings.setDeficientEncapsulationEnabled(settings.getDeficientEncapsulationEnabled());
        
        // Unnecessary Abstraction
        existingSettings.setUnnecessaryAbstractionEnabled(settings.getUnnecessaryAbstractionEnabled());
        existingSettings.setMaxAbstractionUsage(settings.getMaxAbstractionUsage());
        
        // AI Settings
        existingSettings.setAiProvider(settings.getAiProvider());
        existingSettings.setAiApiKey(settings.getAiApiKey());
        existingSettings.setAiModel(settings.getAiModel());
        existingSettings.setAiEnabled(settings.getAiEnabled());
        
        UserSettings saved = userSettingsRepository.save(existingSettings);
        System.out.println("✅ Settings saved for user: " + userId);
        
        return ResponseEntity.ok(saved);
    }
    
    @GetMapping("/detectors/info")
    public ResponseEntity<List<DetectorConfigDTO>> getDetectorInfo() {
        List<DetectorConfigDTO> detectors = new ArrayList<>();
        
        // 1. Long Method Detector
        DetectorConfigDTO longMethod = new DetectorConfigDTO(
            "LongMethodDetector",
            "Detects methods that are too long or complex, making them hard to understand and maintain",
            true
        );
        longMethod.addParameter("maxMethodLength", "integer", 50, 10, 200, "Maximum lines of code in a method");
        longMethod.addParameter("maxMethodComplexity", "integer", 10, 1, 50, "Maximum cyclomatic complexity");
        detectors.add(longMethod);
        
        // 2. Long Parameter List Detector
        DetectorConfigDTO longParam = new DetectorConfigDTO(
            "LongParameterListDetector",
            "Identifies methods with too many parameters, indicating poor design",
            true
        );
        longParam.addParameter("maxParameterCount", "integer", 5, 1, 15, "Maximum number of method parameters");
        detectors.add(longParam);
        
        // 3. Long Identifier Detector
        DetectorConfigDTO longId = new DetectorConfigDTO(
            "LongIdentifierDetector",
            "Finds identifiers (variables, methods, classes) that are too long or too short",
            true
        );
        longId.addParameter("maxIdentifierLength", "integer", 30, 10, 100, "Maximum character length for identifiers");
        longId.addParameter("minIdentifierLength", "integer", 3, 1, 10, "Minimum character length for identifiers");
        detectors.add(longId);
        
        // 4. Magic Number Detector
        DetectorConfigDTO magicNum = new DetectorConfigDTO(
            "MagicNumberDetector",
            "Detects hardcoded numeric literals that should be named constants",
            true
        );
        magicNum.addParameter("magicNumberThreshold", "integer", 3, 0, 20, "Minimum value to be considered a magic number");
        detectors.add(magicNum);
        
        // 5. Missing Default Detector
        DetectorConfigDTO missingDefault = new DetectorConfigDTO(
            "MissingDefaultDetector",
            "Identifies switch statements without default cases, which can lead to unhandled scenarios",
            true
        );
        detectors.add(missingDefault);
        
        // 6. Empty Catch Detector
        DetectorConfigDTO emptyCatch = new DetectorConfigDTO(
            "EmptyCatchDetector",
            "Finds empty catch blocks that silently swallow exceptions",
            true
        );
        detectors.add(emptyCatch);
        
        // 7. Complex Conditional Detector
        DetectorConfigDTO complexCond = new DetectorConfigDTO(
            "ComplexConditionalDetector",
            "Detects overly complex conditional expressions with many operators or deep nesting",
            true
        );
        complexCond.addParameter("maxConditionalOperators", "integer", 4, 1, 15, "Maximum logical operators in a condition");
        complexCond.addParameter("maxNestingDepth", "integer", 3, 1, 10, "Maximum nesting depth for conditionals");
        detectors.add(complexCond);
        
        // 8. Long Statement Detector
        DetectorConfigDTO longStmt = new DetectorConfigDTO(
            "LongStatementDetector",
            "Identifies statements that are too long or complex, reducing readability",
            true
        );
        longStmt.addParameter("maxStatementTokens", "integer", 40, 10, 150, "Maximum tokens in a statement");
        longStmt.addParameter("maxStatementChars", "integer", 250, 50, 1000, "Maximum characters in a statement");
        longStmt.addParameter("maxMethodChainLength", "integer", 5, 2, 20, "Maximum chained method calls");
        detectors.add(longStmt);
        
        // 9. Broken Modularization Detector
        DetectorConfigDTO brokenMod = new DetectorConfigDTO(
            "BrokenModularizationDetector",
            "Detects classes/methods with poor modularization: multiple responsibilities, low cohesion, high coupling",
            true
        );
        brokenMod.addParameter("maxResponsibilities", "integer", 3, 1, 10, "Maximum responsibilities per class/method");
        brokenMod.addParameter("minCohesionIndex", "double", 0.4, 0.0, 1.0, "Minimum cohesion index (0-1)");
        brokenMod.addParameter("maxCouplingCount", "integer", 6, 1, 20, "Maximum coupling to external types");
        detectors.add(brokenMod);
        
        // 10. Deficient Encapsulation Detector
        DetectorConfigDTO deficientEnc = new DetectorConfigDTO(
            "DeficientEncapsulationDetector",
            "Identifies public fields that break encapsulation principles",
            true
        );
        detectors.add(deficientEnc);
        
        // 11. Unnecessary Abstraction Detector
        DetectorConfigDTO unnecessaryAbs = new DetectorConfigDTO(
            "UnnecessaryAbstractionDetector",
            "Finds abstractions (interfaces/abstract classes) with only one implementation and minimal usage",
            true
        );
        unnecessaryAbs.addParameter("maxAbstractionUsage", "integer", 1, 0, 5, "Maximum usage count to be considered unnecessary");
        detectors.add(unnecessaryAbs);
        
        return ResponseEntity.ok(detectors);
    }
    
    @GetMapping("/{userId}/defaults")
    public ResponseEntity<UserSettings> getDefaultSettings(@PathVariable String userId) {
        UserSettings defaults = new UserSettings(userId);
        return ResponseEntity.ok(defaults);
    }
    
    @PostMapping("/{userId}/reset")
    public ResponseEntity<UserSettings> resetToDefaults(@PathVariable String userId) {
        UserSettings settings = userSettingsRepository.findByUserId(userId)
            .orElse(new UserSettings(userId));
        
        // Reset to defaults by creating new instance and copying ID
        Long id = settings.getId();
        settings = new UserSettings(userId);
        settings.setId(id);
        
        UserSettings saved = userSettingsRepository.save(settings);
        return ResponseEntity.ok(saved);
    }
    
    @PostMapping("/{userId}/test-ai")
    public ResponseEntity<String> testAiConnection(@PathVariable String userId, @RequestBody UserSettings settings) {
        try {
            String provider = settings.getAiProvider();
            
            if ("none".equals(provider)) {
                return ResponseEntity.ok("✅ AI Analysis is disabled");
            }
            
            String testReport = "Test connection: No issues found in sample code.";
            String result = aiAssistantService.analyzeWithAI(testReport, settings);
            
            if (result != null && !result.isEmpty()) {
                return ResponseEntity.ok("✅ " + provider.toUpperCase() + " connection successful!\n\nSample response received.");
            }
            
            return ResponseEntity.ok("⚠️ Connection established but no response received");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("❌ Connection failed: " + e.getMessage());
        }
    }
}