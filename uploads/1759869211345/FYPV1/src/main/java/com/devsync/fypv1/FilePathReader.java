package com.devsync.fypv1;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FilePathReader {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: java FilePathReader <file-path1> [file-path2] ...");
            return;
        }
        
        for (String filePath : args) {
            Path path = Paths.get(filePath);
            if (Files.exists(path)) {
                System.out.println("Found: " + path.toAbsolutePath());
            } else {
                System.out.println("Not found: " + filePath);
            }
        }
    }
}