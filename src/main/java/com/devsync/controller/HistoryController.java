package com.devsync.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

@RestController
@RequestMapping("/api/history")
@CrossOrigin(origins = "http://localhost:5173")
public class HistoryController {

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getHistory() {
        System.out.println("History endpoint called");
        List<Map<String, Object>> history = new ArrayList<>();
        
        File uploadsDir = new File("uploads");
        System.out.println("Uploads dir exists: " + uploadsDir.exists());
        System.out.println("Uploads dir path: " + uploadsDir.getAbsolutePath());
        if (!uploadsDir.exists() || !uploadsDir.isDirectory()) {
            return ResponseEntity.ok(history);
        }
        
        File[] folders = uploadsDir.listFiles(File::isDirectory);
        System.out.println("Found folders: " + (folders != null ? folders.length : 0));
        if (folders != null) {
            for (File folder : folders) {
                System.out.println("Processing folder: " + folder.getName());
                Map<String, Object> item = new HashMap<>();
                item.put("folderName", folder.getName());
                item.put("folderPath", folder.getPath());
                
                // Find report file
                File[] reportFiles = folder.listFiles((dir, name) -> name.endsWith(".txt"));
                if (reportFiles != null && reportFiles.length > 0) {
                    item.put("reportFile", reportFiles[0].getName());
                    item.put("reportPath", reportFiles[0].getPath());
                }
                
                item.put("lastModified", folder.lastModified());
                history.add(item);
            }
        }
        
        // Sort by last modified (newest first)
        history.sort((a, b) -> Long.compare((Long)b.get("lastModified"), (Long)a.get("lastModified")));
        
        System.out.println("Returning history with " + history.size() + " items");
        return ResponseEntity.ok(history);
    }
    
    @GetMapping("/report/{folderName}")
    public ResponseEntity<String> getReportContent(@PathVariable String folderName) {
        try {
            File folder = new File("uploads/" + folderName);
            if (!folder.exists()) {
                return ResponseEntity.notFound().build();
            }
            
            File[] reportFiles = folder.listFiles((dir, name) -> name.endsWith(".txt"));
            if (reportFiles == null || reportFiles.length == 0) {
                return ResponseEntity.notFound().build();
            }
            
            String content = new String(Files.readAllBytes(Paths.get(reportFiles[0].getPath())));
            return ResponseEntity.ok(content);
            
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Error reading report: " + e.getMessage());
        }
    }
}