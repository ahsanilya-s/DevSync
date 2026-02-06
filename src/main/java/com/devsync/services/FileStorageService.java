package com.devsync.services;

import org.springframework.stereotype.Service;
import java.io.File;

@Service
public class FileStorageService {
    
    private static final String UPLOADS_DIR = getUploadsDirectory();
    
    private static String getUploadsDirectory() {
        // Check if running on Railway with volume
        String volumePath = System.getenv("RAILWAY_VOLUME_MOUNT_PATH");
        if (volumePath != null && !volumePath.isEmpty()) {
            return volumePath + "/uploads";
        }
        // Default to local uploads folder
        return "uploads";
    }
    
    public String getUploadsPath() {
        return UPLOADS_DIR;
    }
    
    public void ensureUploadsDirectoryExists() {
        File uploadsDir = new File(UPLOADS_DIR);
        if (!uploadsDir.exists()) {
            uploadsDir.mkdirs();
        }
    }
    
    public boolean deleteDirectory(File directory) {
        if (directory == null || !directory.exists()) {
            return false;
        }
        
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectory(file);
                } else {
                    file.delete();
                }
            }
        }
        return directory.delete();
    }
}
