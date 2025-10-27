package com.devsync.detectors;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.Modifier;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DeficientEncapsulationDetector {

    // Thresholds
    private static final double MAX_PUBLIC_PERCENT = 0.3; // >30% public fields flagged
    private static final int MIN_IMMUTABLE_PERCENT = 1; // placeholder

    public static List<String> detect(File file) {
        List<String> issues = new ArrayList<>();
        try {
            CompilationUnit cu = StaticJavaParser.parse(file);

            // For each class, compute public field ratio
            cu.findAll(com.github.javaparser.ast.body.ClassOrInterfaceDeclaration.class).forEach(cls -> {
                List<FieldDeclaration> fields = cls.getFields();
                if (fields.isEmpty()) return;

                int total = 0, publicCount = 0, immutableCount = 0;
                for (FieldDeclaration fd : fields) {
                    total += fd.getVariables().size();
                    if (fd.hasModifier(Modifier.Keyword.PUBLIC)) publicCount += fd.getVariables().size();

                    // Simple immutable detection: final fields
                    if (fd.hasModifier(Modifier.Keyword.FINAL)) immutableCount += fd.getVariables().size();
                }

                double pubRatio = total == 0 ? 0.0 : (double) publicCount / total;
                double immRatio = total == 0 ? 0.0 : (double) immutableCount / total;

                if (pubRatio > MAX_PUBLIC_PERCENT) {
                    issues.add(String.format("Deficient encapsulation in %s: class=%s at line %d -> publicFields=%d/%d (%.0f%%)",
                            file.getName(), cls.getNameAsString(), cls.getBegin().map(p -> p.line).orElse(-1),
                            publicCount, total, pubRatio * 100));
                }

                if (immRatio < 0.2) { // less than 20% fields final -> suspicious
                    issues.add(String.format("Low immutability in %s: class=%s -> finalFields=%d/%d (%.0f%%)",
                            file.getName(), cls.getNameAsString(), immutableCount, total, immRatio * 100));
                }
            });

        } catch (Exception e) {
            issues.add("Error parsing for DeficientEncapsulationDetector: " + e.getMessage());
        }
        return issues;
    }
}
