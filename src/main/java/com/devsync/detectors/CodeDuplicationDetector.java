package com.devsync.detectors;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import java.util.*;
import java.util.stream.Collectors;

public class CodeDuplicationDetector {
    private static final int MIN_DUPLICATE_LINES = 6;
    private static final double SIMILARITY_THRESHOLD = 0.85;
    private static final int MIN_TOKENS = 50;
    
    public List<String> detect(CompilationUnit cu) {
        List<String> issues = new ArrayList<>();
        
        DuplicationAnalyzer analyzer = new DuplicationAnalyzer();
        cu.accept(analyzer, null);
        
        List<CodeBlock> codeBlocks = analyzer.getCodeBlocks();
        
        // Compare all pairs of code blocks
        for (int i = 0; i < codeBlocks.size(); i++) {
            for (int j = i + 1; j < codeBlocks.size(); j++) {
                CodeBlock block1 = codeBlocks.get(i);
                CodeBlock block2 = codeBlocks.get(j);
                
                double similarity = calculateSimilarity(block1, block2);
                
                if (similarity > SIMILARITY_THRESHOLD && 
                    block1.tokenCount >= MIN_TOKENS && 
                    block2.tokenCount >= MIN_TOKENS) {
                    
                    String severity = getSeverity(similarity, Math.max(block1.lineCount, block2.lineCount));
                    String analysis = generateAnalysis(block1, block2, similarity);
                    String suggestions = generateSuggestions(block1, block2);
                    
                    issues.add(String.format(
                        "%s [CodeDuplication] %s:%d-%d & %d-%d - [Similarity: %.2f] - %s | Suggestions: %s",
                        severity,
                        cu.getStorage().map(s -> s.getFileName()).orElse("UnknownFile"),
                        block1.startLine, block1.endLine,
                        block2.startLine, block2.endLine,
                        similarity,
                        analysis,
                        suggestions
                    ));
                }
            }
        }
        
        return issues;
    }
    
    private double calculateSimilarity(CodeBlock block1, CodeBlock block2) {
        // Tokenize and normalize code
        List<String> tokens1 = tokenizeCode(block1.normalizedCode);
        List<String> tokens2 = tokenizeCode(block2.normalizedCode);
        
        // Calculate Jaccard similarity
        Set<String> set1 = new HashSet<>(tokens1);
        Set<String> set2 = new HashSet<>(tokens2);
        
        Set<String> intersection = new HashSet<>(set1);
        intersection.retainAll(set2);
        
        Set<String> union = new HashSet<>(set1);
        union.addAll(set2);
        
        if (union.isEmpty()) return 0.0;
        
        double jaccardSimilarity = (double) intersection.size() / union.size();
        
        // Calculate sequence similarity using longest common subsequence
        double sequenceSimilarity = calculateLCS(tokens1, tokens2) / 
            (double) Math.max(tokens1.size(), tokens2.size());
        
        // Combine both metrics
        return (jaccardSimilarity * 0.6) + (sequenceSimilarity * 0.4);
    }
    
    private List<String> tokenizeCode(String code) {
        return Arrays.stream(code.split("\\W+"))
            .filter(token -> !token.isEmpty() && token.length() > 1)
            .map(String::toLowerCase)
            .collect(Collectors.toList());
    }
    
    private double calculateLCS(List<String> seq1, List<String> seq2) {
        int m = seq1.size();
        int n = seq2.size();
        int[][] dp = new int[m + 1][n + 1];
        
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (seq1.get(i - 1).equals(seq2.get(j - 1))) {
                    dp[i][j] = dp[i - 1][j - 1] + 1;
                } else {
                    dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]);
                }
            }
        }
        
        return dp[m][n];
    }
    
    private String normalizeCode(String code) {
        return code.replaceAll("\\s+", " ")
                  .replaceAll("\\b\\d+\\b", "NUM")
                  .replaceAll("\"[^\"]*\"", "STR")
                  .replaceAll("'[^']*'", "CHAR")
                  .toLowerCase()
                  .trim();
    }
    
    private String getSeverity(double similarity, int lineCount) {
        if (similarity > 0.95 || lineCount > 20) {
            return "🔴";
        }
        if (similarity > 0.90 || lineCount > 15) {
            return "🟡";
        }
        return "🟠";
    }
    
    private String generateAnalysis(CodeBlock block1, CodeBlock block2, double similarity) {
        List<String> analysis = new ArrayList<>();
        
        analysis.add(String.format("lines=%d,%d", block1.lineCount, block2.lineCount));
        analysis.add(String.format("tokens=%d,%d", block1.tokenCount, block2.tokenCount));
        
        if (block1.methodName.equals(block2.methodName)) {
            analysis.add("same-method");
        } else {
            analysis.add("cross-method");
        }
        
        if (similarity > 0.95) {
            analysis.add("near-identical");
        } else if (similarity > 0.90) {
            analysis.add("highly-similar");
        }
        
        return String.join(", ", analysis);
    }
    
    private String generateSuggestions(CodeBlock block1, CodeBlock block2) {
        List<String> suggestions = new ArrayList<>();
        
        if (block1.methodName.equals(block2.methodName)) {
            suggestions.add("Extract common logic to helper method");
        } else {
            suggestions.add("Extract to shared utility method");
        }
        
        suggestions.add("Consider template method pattern");
        suggestions.add("Use strategy pattern for variations");
        
        return String.join(", ", suggestions);
    }
    
    private static class CodeBlock {
        String code;
        String normalizedCode;
        String methodName;
        int startLine;
        int endLine;
        int lineCount;
        int tokenCount;
        
        CodeBlock(String code, String methodName, int startLine, int endLine) {
            this.code = code;
            this.methodName = methodName;
            this.startLine = startLine;
            this.endLine = endLine;
            this.lineCount = endLine - startLine + 1;
            this.normalizedCode = normalizeCode(code);
            this.tokenCount = tokenizeCode(this.normalizedCode).size();
        }
        
        private String normalizeCode(String code) {
            return code.replaceAll("\\s+", " ")
                      .replaceAll("\\b\\d+\\b", "NUM")
                      .replaceAll("\"[^\"]*\"", "STR")
                      .replaceAll("'[^']*'", "CHAR")
                      .toLowerCase()
                      .trim();
        }
        
        private List<String> tokenizeCode(String code) {
            return Arrays.stream(code.split("\\W+"))
                .filter(token -> !token.isEmpty() && token.length() > 1)
                .map(String::toLowerCase)
                .collect(Collectors.toList());
        }
    }
    
    private static class DuplicationAnalyzer extends VoidVisitorAdapter<Void> {
        private final List<CodeBlock> codeBlocks = new ArrayList<>();
        private String currentMethodName = "";
        
        public List<CodeBlock> getCodeBlocks() {
            return codeBlocks;
        }
        
        @Override
        public void visit(MethodDeclaration n, Void arg) {
            currentMethodName = n.getNameAsString();
            
            // Extract code blocks from method body
            if (n.getBody().isPresent()) {
                List<Statement> statements = n.getBody().get().getStatements();
                
                // Create sliding window of statements
                for (int i = 0; i <= statements.size() - MIN_DUPLICATE_LINES; i++) {
                    for (int j = i + MIN_DUPLICATE_LINES; j <= statements.size(); j++) {
                        List<Statement> blockStatements = statements.subList(i, j);
                        
                        if (blockStatements.size() >= MIN_DUPLICATE_LINES) {
                            String blockCode = blockStatements.stream()
                                .map(Statement::toString)
                                .collect(Collectors.joining("\n"));
                            
                            int startLine = blockStatements.get(0).getBegin().map(p -> p.line).orElse(-1);
                            int endLine = blockStatements.get(blockStatements.size() - 1).getEnd().map(p -> p.line).orElse(-1);
                            
                            CodeBlock block = new CodeBlock(blockCode, currentMethodName, startLine, endLine);
                            if (block.tokenCount >= MIN_TOKENS) {
                                codeBlocks.add(block);
                            }
                        }
                    }
                }
            }
            
            super.visit(n, arg);
        }
    }
}