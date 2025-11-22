package com.devsync.analyzer;

import com.devsync.config.AnalysisConfig;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class JavaFileCollector {

    public List<File> collectJavaFiles(String directoryPath) {
        List<File> javaFiles = new ArrayList<>();
        File directory = new File(directoryPath);
        
        if (!directory.exists() || !directory.isDirectory()) {
            return javaFiles;
        }
        
        collectRecursively(directory, javaFiles);
        return javaFiles;
    }

    private void collectRecursively(File folder, List<File> javaFiles) {
        File[] files = folder.listFiles();
        if (files == null) return;

        for (File file : files) {
            // Skip excluded directories and files
            if (AnalysisConfig.shouldExclude(file.getPath())) {
                continue;
            }
            
            if (file.isDirectory()) {
                collectRecursively(file, javaFiles);
            } else if (file.getName().endsWith(".java")) {
                javaFiles.add(file);
            }
        }
    }
}
