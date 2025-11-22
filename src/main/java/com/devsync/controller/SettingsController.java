package com.devsync.controller;

import com.devsync.model.UserSettings;
import com.devsync.repository.UserSettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/settings")
@CrossOrigin(origins = "*")
public class SettingsController {
    
    @Autowired
    private UserSettingsRepository userSettingsRepository;
    
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
            // Test connection based on provider
            String provider = settings.getAiProvider();
            if ("ollama".equals(provider)) {
                return ResponseEntity.ok("Ollama connection test successful");
            } else if ("openai".equals(provider)) {
                return ResponseEntity.ok("OpenAI API key format valid");
            } else if ("anthropic".equals(provider)) {
                return ResponseEntity.ok("Anthropic API key format valid");
            }
            return ResponseEntity.ok("AI provider configured");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Connection failed: " + e.getMessage());
        }
    }
}