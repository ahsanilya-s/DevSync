package com.devsync.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:5173")
public class AdminAuthController {

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> adminLogin(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");
        
        // TODO: For FYP demo - Change these credentials before production!
        // Recommended: Use environment variables and encrypted passwords
        String adminUsername = System.getenv("ADMIN_USERNAME") != null ? System.getenv("ADMIN_USERNAME") : "admin";
        String adminPassword = System.getenv("ADMIN_PASSWORD") != null ? System.getenv("ADMIN_PASSWORD") : "DevSync@2024";
        
        if (adminUsername.equals(username) && adminPassword.equals(password)) {
            return ResponseEntity.ok(Map.of("success", true, "message", "Admin login successful"));
        }
        
        return ResponseEntity.status(401).body(Map.of("success", false, "message", "Invalid credentials"));
    }
}