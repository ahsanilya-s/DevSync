package com.devsync.controller;

import com.devsync.model.User;
import com.devsync.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody User user) {
        try {
            if (user.getUsername() == null || user.getUsername().trim().isEmpty() ||
                user.getEmail() == null || user.getEmail().trim().isEmpty() ||
                user.getPassword() == null || user.getPassword().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("All fields are required");
            }
            
            if (user.getPassword().length() < 6) {
                return ResponseEntity.badRequest().body("Password must be at least 6 characters long");
            }
            
            User savedUser = userService.registerUser(user);
            return ResponseEntity.ok("User registered successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Registration failed: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        try {
            if (user.getEmail() == null || user.getEmail().trim().isEmpty() ||
                user.getPassword() == null || user.getPassword().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Email and password are required");
            }
            
            User found = userService.login(user.getEmail(), user.getPassword());
            if (found != null) {
                String token = java.util.UUID.randomUUID().toString();
                return ResponseEntity.ok(new LoginResponse("Login successful", found.getId().toString(), found.getUsername(), token));
            } else {
                return ResponseEntity.badRequest().body("Invalid credentials");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Login failed: " + e.getMessage());
        }
    }
    
    public static class LoginResponse {
        private String message;
        private String userId;
        private String username;
        private String token;
        
        public LoginResponse(String message, String userId, String username, String token) {
            this.message = message;
            this.userId = userId;
            this.username = username;
            this.token = token;
        }
        
        public String getMessage() { return message; }
        public String getUserId() { return userId; }
        public String getUsername() { return username; }
        public String getToken() { return token; }
    }

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/profile/{userId}")
    public ResponseEntity<?> getUserProfile(@PathVariable String userId) {
        try {
            User user = userService.getUserById(Long.parseLong(userId));
            if (user == null) {
                return ResponseEntity.notFound().build();
            }
            
            Map<String, Object> profile = new HashMap<>();
            profile.put("username", user.getUsername());
            profile.put("email", user.getEmail());
            profile.put("userId", user.getId());
            
            return ResponseEntity.ok(profile);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to get profile: " + e.getMessage());
        }
    }
}
