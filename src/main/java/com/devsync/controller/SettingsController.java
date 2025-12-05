package com.devsync.controller;

import com.devsync.model.UserSettings;
import com.devsync.repository.UserSettingsRepository;
import com.devsync.services.AIAssistantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        // Find existing settings or create new
        UserSettings existingSettings = userSettingsRepository.findByUserId(userId)
            .orElse(new UserSettings(userId));
        
        // Update all fields from the request
        existingSettings.setUserId(userId);
        existingSettings.setMaxMethodLength(settings.getMaxMethodLength());
        existingSettings.setMaxParameterCount(settings.getMaxParameterCount());
        existingSettings.setMaxIdentifierLength(settings.getMaxIdentifierLength());
        existingSettings.setMagicNumberThreshold(settings.getMagicNumberThreshold());
        existingSettings.setMissingDefaultEnabled(settings.getMissingDefaultEnabled());
        existingSettings.setEmptyCatchEnabled(settings.getEmptyCatchEnabled());
        existingSettings.setLongMethodEnabled(settings.getLongMethodEnabled());
        existingSettings.setLongParameterEnabled(settings.getLongParameterEnabled());
        existingSettings.setMagicNumberEnabled(settings.getMagicNumberEnabled());
        existingSettings.setLongIdentifierEnabled(settings.getLongIdentifierEnabled());
        existingSettings.setAiProvider(settings.getAiProvider());
        existingSettings.setAiApiKey(settings.getAiApiKey());
        existingSettings.setAiModel(settings.getAiModel());
        existingSettings.setAiEnabled(settings.getAiEnabled());
        
        UserSettings saved = userSettingsRepository.save(existingSettings);
        
        System.out.println("=== Settings Saved ===");
        System.out.println("User ID: " + userId);
        System.out.println("Magic Number Enabled: " + saved.getMagicNumberEnabled());
        System.out.println("Long Method Enabled: " + saved.getLongMethodEnabled());
        System.out.println("Empty Catch Enabled: " + saved.getEmptyCatchEnabled());
        System.out.println("Missing Default Enabled: " + saved.getMissingDefaultEnabled());
        System.out.println("Long Parameter Enabled: " + saved.getLongParameterEnabled());
        
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