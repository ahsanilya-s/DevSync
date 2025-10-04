package com.devsync.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@RestController
@RequestMapping("/api/upload")
public class UploadController {

    @GetMapping
    public ResponseEntity<String> getUploadInfo() {
        return ResponseEntity.ok("Upload endpoint ready. Use POST with multipart/form-data.");
    }

    @PostMapping
    public ResponseEntity<String> handleFileUpload(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("❌ No file uploaded");
        }

        String fileName = file.getOriginalFilename();
        return ResponseEntity.ok("✅ File received: " + fileName);
    }
}
