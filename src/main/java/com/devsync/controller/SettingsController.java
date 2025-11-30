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
        settings.setUserId(userId);
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