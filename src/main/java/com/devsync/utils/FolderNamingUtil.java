package com.devsync.utils;

import java.io.File;

public class FolderNamingUtil {

    public static String generateUniqueFolderName(String baseName, String baseDir) {
        String folderName = sanitizeFolderName(baseName);
        String fullPath = baseDir + "/" + folderName;
        
        if (!new File(fullPath).exists()) {
            return folderName;
        }
        
        int counter = 1;
        while (new File(baseDir + "/" + folderName + counter).exists()) {
            counter++;
        }
        
        return folderName + counter;
    }
    
    private static String sanitizeFolderName(String name) {
        // Remove file extension if present
        if (name.contains(".")) {
            name = name.substring(0, name.lastIndexOf("."));
        }
        
        // Replace invalid characters with underscores
        return name.replaceAll("[^a-zA-Z0-9._-]", "_");
    }
}