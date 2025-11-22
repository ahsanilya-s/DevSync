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
        
        if ("admin".equals(username) && "aaaa".equals(password)) {
            return ResponseEntity.ok(Map.of("success", true, "message", "Admin login successful"));
        }
        
        return ResponseEntity.status(401).body(Map.of("success", false, "message", "Invalid credentials"));
    }
}