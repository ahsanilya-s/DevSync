package com.devsync.detectors;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import java.util.*;

public class MemoryLeakDetector {

    public List<String> detect(CompilationUnit cu) {
        List<String> issues = new ArrayList<>();
        MemoryLeakAnalyzer analyzer = new MemoryLeakAnalyzer();
        cu.accept(analyzer, null);
        issues.addAll(analyzer.getIssues());
        return issues;
    }

    private static class MemoryLeakAnalyzer extends VoidVisitorAdapter<Void> {
        private final List<String> issues = new ArrayList<>();
        private String fileName = "";
        private Set<String> unclosedResources = new HashSet<>();

        public List<String> getIssues() { return issues; }

        @Override
        public void visit(CompilationUnit n, Void arg) {
            fileName = n.getStorage().map(s -> s.getFileName()).orElse("UnknownFile");
            super.visit(n, arg);
        }

        @Override
        public void visit(MethodDeclaration method, Void arg) {
            unclosedResources.clear();
            
            // Check for unclosed streams/connections
            checkUnclosedResources(method);
            
            // Check for static collections that grow unbounded
            checkStaticCollections(method);
            
            // Check for listeners not removed
            checkListenerLeaks(method);
            
            // Check for thread leaks
            checkThreadLeaks(method);
            
            super.visit(method, arg);
        }

        private void checkUnclosedResources(MethodDeclaration method) {
            Set<String> openedResources = new HashSet<>();
            Set<String> closedResources = new HashSet<>();
            final boolean[] hasTryWithResources = {false};

            // Check for try-with-resources
            method.findAll(TryStmt.class).forEach(tryStmt -> {
                if (!tryStmt.getResources().isEmpty()) {
                    hasTryWithResources[0] = true;
                }
            });

            // Find resource allocations
            method.findAll(ObjectCreationExpr.class).forEach(creation -> {
                String type = creation.getType().getNameAsString();
                if (isResourceType(type)) {
                    String varName = getVariableName(creation);
                    if (varName != null) {
                        openedResources.add(varName);
                    }
                }
            });

            // Find close() calls
            method.findAll(MethodCallExpr.class).forEach(call -> {
                if (call.getNameAsString().equals("close")) {
                    call.getScope().ifPresent(scope -> {
                        if (scope instanceof NameExpr) {
                            closedResources.add(((NameExpr) scope).getNameAsString());
                        }
                    });
                }
            });

            // Report unclosed resources
            openedResources.removeAll(closedResources);
            if (!openedResources.isEmpty() && !hasTryWithResources[0]) {
                for (String resource : openedResources) {
                    issues.add(String.format(
                        "🔴 [MemoryLeak] %s:%d - Resource '%s' in method '%s' may not be closed | Suggestions: Use try-with-resources or ensure close() is called in finally block | DetailedReason: Unclosed resources like streams, connections, or readers can cause memory leaks as they hold references and prevent garbage collection",
                        fileName,
                        method.getBegin().map(p -> p.line).orElse(0),
                        resource,
                        method.getNameAsString()
                    ));
                }
            }
        }

        private void checkStaticCollections(MethodDeclaration method) {
            method.findAll(FieldDeclaration.class).forEach(field -> {
                if (field.isStatic() && isCollectionType(field)) {
                    boolean hasClearOrRemove = method.findAll(MethodCallExpr.class).stream()
                        .anyMatch(call -> call.getNameAsString().matches("clear|remove|removeAll"));
                    
                    if (!hasClearOrRemove) {
                        issues.add(String.format(
                            "🟡 [MemoryLeak] %s:%d - Static collection '%s' may grow unbounded | Suggestions: Implement size limits, use WeakHashMap, or periodically clear old entries | DetailedReason: Static collections persist for the application lifetime and can accumulate objects indefinitely, causing memory leaks",
                            fileName,
                            field.getBegin().map(p -> p.line).orElse(0),
                            field.getVariables().get(0).getNameAsString()
                        ));
                    }
                }
            });
        }

        private void checkListenerLeaks(MethodDeclaration method) {
            boolean hasAddListener = method.findAll(MethodCallExpr.class).stream()
                .anyMatch(call -> call.getNameAsString().matches("add.*Listener|register.*"));
            
            boolean hasRemoveListener = method.findAll(MethodCallExpr.class).stream()
                .anyMatch(call -> call.getNameAsString().matches("remove.*Listener|unregister.*"));
            
            if (hasAddListener && !hasRemoveListener) {
                issues.add(String.format(
                    "🟡 [MemoryLeak] %s:%d - Listener registered in '%s' but never removed | Suggestions: Remove listener in cleanup/dispose method or use weak references | DetailedReason: Registered listeners hold strong references to objects, preventing garbage collection even when objects are no longer needed",
                    fileName,
                    method.getBegin().map(p -> p.line).orElse(0),
                    method.getNameAsString()
                ));
            }
        }

        private void checkThreadLeaks(MethodDeclaration method) {
            method.findAll(ObjectCreationExpr.class).forEach(creation -> {
                String type = creation.getType().getNameAsString();
                if (type.equals("Thread") || type.contains("Executor")) {
                    boolean hasShutdown = method.findAll(MethodCallExpr.class).stream()
                        .anyMatch(call -> call.getNameAsString().matches("shutdown|shutdownNow|interrupt"));
                    
                    if (!hasShutdown) {
                        issues.add(String.format(
                            "🟠 [MemoryLeak] %s:%d - Thread/Executor created in '%s' without shutdown mechanism | Suggestions: Call shutdown() on executors, interrupt threads, or use daemon threads | DetailedReason: Threads that are not properly terminated continue running and hold references to objects, preventing garbage collection",
                            fileName,
                            method.getBegin().map(p -> p.line).orElse(0),
                            method.getNameAsString()
                        ));
                    }
                }
            });
        }

        private boolean isResourceType(String type) {
            return type.matches(".*(Stream|Reader|Writer|Connection|Socket|Channel|Statement|ResultSet|Scanner|Buffer)");
        }

        private boolean isCollectionType(FieldDeclaration field) {
            String type = field.getCommonType().asString();
            return type.matches(".*(List|Set|Map|Collection|Cache).*");
        }

        private String getVariableName(ObjectCreationExpr creation) {
            return creation.getParentNode()
                .filter(p -> p instanceof VariableDeclarator)
                .map(p -> ((VariableDeclarator) p).getNameAsString())
                .orElse(null);
        }
    }
}
