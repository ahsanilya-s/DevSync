package com.devsync.analyzer;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.comments.Comment;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class LOCCounter {
    
    public static int countLinesOfCode(CompilationUnit cu) {
        if (cu == null) return 0;
        
        int totalLines = 0;
        if (cu.getStorage().isPresent()) {
            try {
                File file = cu.getStorage().get().getPath().toFile();
                List<String> lines = Files.readAllLines(file.toPath());
                
                for (String line : lines) {
                    String trimmed = line.trim();
                    if (!trimmed.isEmpty() && !trimmed.startsWith("//") && !trimmed.startsWith("/*") && !trimmed.startsWith("*")) {
                        totalLines++;
                    }
                }
            } catch (IOException e) {
                return 0;
            }
        }
        
        return totalLines;
    }
    
    public static int countPhysicalLines(File file) {
        try {
            return (int) Files.lines(file.toPath()).count();
        } catch (IOException e) {
            return 0;
        }
    }
}
