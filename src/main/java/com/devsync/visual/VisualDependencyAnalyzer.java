package com.devsync.visual;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

public class VisualDependencyAnalyzer {
    
    private Map<String, ClassInfo> classes = new HashMap<>();
    private List<DependencyInfo> dependencies = new ArrayList<>();
    private Set<String> projectPackages = new HashSet<>();
    
    public Map<String, Object> analyzeProject(String projectPath) {
        try {
            // Collect all Java files
            List<File> javaFiles = collectJavaFiles(projectPath);
            
            // First pass: collect all classes and their basic info
            for (File javaFile : javaFiles) {
                analyzeFile(javaFile);
            }
            
            // Determine project packages
            determineProjectPackages();
            
            // Second pass: analyze dependencies
            for (File javaFile : javaFiles) {
                analyzeDependencies(javaFile);
            }
            
            Map<String, Object> result = new HashMap<>();
            result.put("classes", classes);
            result.put("dependencies", dependencies);
            result.put("projectPackages", projectPackages);
            result.put("totalClasses", classes.size());
            result.put("totalDependencies", dependencies.size());
            
            return result;
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to analyze project: " + e.getMessage(), e);
        }
    }
    
    private List<File> collectJavaFiles(String projectPath) throws IOException {
        List<File> javaFiles = new ArrayList<>();
        
        try (Stream<Path> paths = Files.walk(Paths.get(projectPath))) {
            paths.filter(Files::isRegularFile)
                 .filter(path -> path.toString().endsWith(".java"))
                 .forEach(path -> javaFiles.add(path.toFile()));
        }
        
        return javaFiles;
    }
    
    private void analyzeFile(File javaFile) {
        try {
            JavaParser parser = new JavaParser();
            CompilationUnit cu = parser.parse(javaFile).getResult().orElse(null);
            
            if (cu == null) return;
            
            String packageName = cu.getPackageDeclaration()
                .map(pd -> pd.getNameAsString())
                .orElse("");
            
            // Analyze classes and interfaces
            cu.findAll(ClassOrInterfaceDeclaration.class).forEach(classDecl -> {
                String className = classDecl.getNameAsString();
                ClassInfo classInfo = new ClassInfo(className, packageName, javaFile.getAbsolutePath());
                
                classInfo.setInterface(classDecl.isInterface());
                classInfo.setAbstract(classDecl.isAbstract());
                classInfo.setLinesOfCode(countLines(javaFile));
                classInfo.setComplexity(calculateComplexity(classDecl));
                
                // Collect imports
                cu.getImports().forEach(imp -> 
                    classInfo.getImports().add(imp.getNameAsString()));
                
                // Collect extends/implements
                classDecl.getExtendedTypes().forEach(ext -> 
                    classInfo.setExtendsClass(ext.getNameAsString()));
                
                classDecl.getImplementedTypes().forEach(impl -> 
                    classInfo.getImplementsInterfaces().add(impl.getNameAsString()));
                
                classes.put(classInfo.getFullName(), classInfo);
            });
            
        } catch (Exception e) {
            System.err.println("Failed to analyze file: " + javaFile.getName() + " - " + e.getMessage());
        }
    }
    
    private void analyzeDependencies(File javaFile) {
        try {
            JavaParser parser = new JavaParser();
            CompilationUnit cu = parser.parse(javaFile).getResult().orElse(null);
            
            if (cu == null) return;
            
            String packageName = cu.getPackageDeclaration()
                .map(pd -> pd.getNameAsString())
                .orElse("");
            
            cu.findAll(ClassOrInterfaceDeclaration.class).forEach(classDecl -> {
                String fromClass = packageName.isEmpty() ? classDecl.getNameAsString() : 
                    packageName + "." + classDecl.getNameAsString();
                
                // Analyze extends relationships
                classDecl.getExtendedTypes().forEach(ext -> {
                    String toClass = resolveClassName(ext.getNameAsString(), cu, packageName);
                    dependencies.add(new DependencyInfo(fromClass, toClass, DependencyInfo.DependencyType.EXTENDS));
                });
                
                // Analyze implements relationships
                classDecl.getImplementedTypes().forEach(impl -> {
                    String toClass = resolveClassName(impl.getNameAsString(), cu, packageName);
                    dependencies.add(new DependencyInfo(fromClass, toClass, DependencyInfo.DependencyType.IMPLEMENTS));
                });
                
                // Analyze field dependencies
                classDecl.getFields().forEach(field -> {
                    field.getVariables().forEach(var -> {
                        String typeName = field.getElementType().asString();
                        String toClass = resolveClassName(typeName, cu, packageName);
                        if (isProjectClass(toClass) || isExternalLibrary(toClass)) {
                            dependencies.add(new DependencyInfo(fromClass, toClass, DependencyInfo.DependencyType.USES));
                        }
                    });
                });
                
                // Analyze method dependencies
                DependencyVisitor visitor = new DependencyVisitor(fromClass, cu, packageName);
                classDecl.accept(visitor, null);
                dependencies.addAll(visitor.getDependencies());
            });
            
        } catch (Exception e) {
            System.err.println("Failed to analyze dependencies in file: " + javaFile.getName() + " - " + e.getMessage());
        }
    }
    
    private void determineProjectPackages() {
        classes.values().forEach(classInfo -> {
            if (classInfo.getPackageName() != null && !classInfo.getPackageName().isEmpty()) {
                projectPackages.add(classInfo.getPackageName());
            }
        });
    }
    
    private String resolveClassName(String className, CompilationUnit cu, String currentPackage) {
        // Check if it's a fully qualified name
        if (className.contains(".")) {
            return className;
        }
        
        // Check imports
        for (ImportDeclaration imp : cu.getImports()) {
            String importName = imp.getNameAsString();
            if (importName.endsWith("." + className)) {
                return importName;
            }
        }
        
        // Check if it's in the same package
        String fullName = currentPackage.isEmpty() ? className : currentPackage + "." + className;
        if (classes.containsKey(fullName)) {
            return fullName;
        }
        
        // Default to java.lang if it's a common class
        if (isJavaLangClass(className)) {
            return "java.lang." + className;
        }
        
        return className;
    }
    
    private boolean isProjectClass(String className) {
        return classes.containsKey(className) || 
               projectPackages.stream().anyMatch(pkg -> className.startsWith(pkg + "."));
    }
    
    private boolean isExternalLibrary(String className) {
        return className.startsWith("java.") || 
               className.startsWith("javax.") ||
               className.startsWith("org.springframework.") ||
               className.startsWith("com.github.javaparser.");
    }
    
    private boolean isJavaLangClass(String className) {
        Set<String> javaLangClasses = Set.of("String", "Integer", "Long", "Double", "Float", 
            "Boolean", "Character", "Byte", "Short", "Object", "Class", "System");
        return javaLangClasses.contains(className);
    }
    
    private int countLines(File file) {
        try {
            return (int) Files.lines(file.toPath()).count();
        } catch (IOException e) {
            return 0;
        }
    }
    
    private int calculateComplexity(ClassOrInterfaceDeclaration classDecl) {
        ComplexityVisitor visitor = new ComplexityVisitor();
        classDecl.accept(visitor, null);
        return visitor.getComplexity();
    }
    
    private static class DependencyVisitor extends VoidVisitorAdapter<Void> {
        private final String fromClass;
        private final CompilationUnit cu;
        private final String packageName;
        private final List<DependencyInfo> dependencies = new ArrayList<>();
        
        public DependencyVisitor(String fromClass, CompilationUnit cu, String packageName) {
            this.fromClass = fromClass;
            this.cu = cu;
            this.packageName = packageName;
        }
        
        @Override
        public void visit(ObjectCreationExpr n, Void arg) {
            String typeName = n.getType().getNameAsString();
            String toClass = resolveClassName(typeName);
            dependencies.add(new DependencyInfo(fromClass, toClass, DependencyInfo.DependencyType.USES));
            super.visit(n, arg);
        }
        
        @Override
        public void visit(MethodCallExpr n, Void arg) {
            if (n.getScope().isPresent() && n.getScope().get() instanceof NameExpr) {
                String scopeName = ((NameExpr) n.getScope().get()).getNameAsString();
                if (Character.isUpperCase(scopeName.charAt(0))) {
                    String toClass = resolveClassName(scopeName);
                    dependencies.add(new DependencyInfo(fromClass, toClass, DependencyInfo.DependencyType.USES));
                }
            }
            super.visit(n, arg);
        }
        
        private String resolveClassName(String className) {
            if (className.contains(".")) return className;
            
            for (ImportDeclaration imp : cu.getImports()) {
                String importName = imp.getNameAsString();
                if (importName.endsWith("." + className)) {
                    return importName;
                }
            }
            
            return packageName.isEmpty() ? className : packageName + "." + className;
        }
        
        public List<DependencyInfo> getDependencies() {
            return dependencies;
        }
    }
    
    private static class ComplexityVisitor extends VoidVisitorAdapter<Void> {
        private int complexity = 1; // Base complexity
        
        @Override
        public void visit(IfStmt n, Void arg) {
            complexity++;
            super.visit(n, arg);
        }
        
        @Override
        public void visit(ForStmt n, Void arg) {
            complexity++;
            super.visit(n, arg);
        }
        
        @Override
        public void visit(WhileStmt n, Void arg) {
            complexity++;
            super.visit(n, arg);
        }
        
        @Override
        public void visit(SwitchStmt n, Void arg) {
            complexity += n.getEntries().size();
            super.visit(n, arg);
        }
        
        @Override
        public void visit(CatchClause n, Void arg) {
            complexity++;
            super.visit(n, arg);
        }
        
        public int getComplexity() {
            return complexity;
        }
    }
}