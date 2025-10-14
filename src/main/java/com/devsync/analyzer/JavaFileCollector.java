package com.devsync.analyzer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class JavaFileCollector {

    public static List<File> collectJavaFiles(String directoryPath) {
        List<File> javaFiles = new ArrayList<>();
        File directory = new File(directoryPath);
        collectRecursively(directory, javaFiles);
        return javaFiles;
    }

    private static void collectRecursively(File folder, List<File> javaFiles) {
        File[] files = folder.listFiles();
        if (files == null) return;

        for (File file : files) {
            if (file.isDirectory()) {
                collectRecursively(file, javaFiles);
            } else if (file.getName().endsWith(".java")) {
                javaFiles.add(file);
            }
        }
    }
}
