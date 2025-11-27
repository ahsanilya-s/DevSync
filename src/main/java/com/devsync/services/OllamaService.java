package com.devsync.services;

import org.springframework.stereotype.Service;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

@Service
public class OllamaService {
    
    private static final String OLLAMA_URL = "http://localhost:11434/api/chat";
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    
    public OllamaService() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(java.time.Duration.ofSeconds(30))
                .build();
        this.objectMapper = new ObjectMapper();
    }
    
    public String sendToOllama(String reportContent) throws IOException, InterruptedException {
        // First check if Ollama is available
        if (!isOllamaAvailable()) {
            return generateFallbackAnalysis(reportContent);
        }
        
        String prompt;
        if (reportContent.contains("No issues found")) {
            prompt = "Great job! Your code analysis shows no issues. Here are 3 quick tips to keep your Java code excellent:\n" +
                    "1. Keep methods under 20 lines\n" +
                    "2. Use meaningful variable names\n" +
                    "3. Add unit tests for critical methods";
        } else {
            // Limit report content to avoid timeout
            String limitedContent = reportContent.length() > 2000 ? 
                reportContent.substring(0, 2000) + "\n..." : reportContent;
            prompt = "Code issues found:\n" + limitedContent + "\n\nProvide 3 specific fixes for the main issues.";
        }
        
        String requestBody = String.format(
            "{\"model\": \"llama3.1:latest\", \"messages\": [{\"role\": \"system\", \"content\": \"You are a helpful assistant.\"}, {\"role\": \"user\", \"content\": \"%s\"}], \"stream\": false}",
            prompt.replace("\"", "\\\"").replace("\n", "\\n")
        );
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(OLLAMA_URL))
                .header("Content-Type", "application/json")
                .timeout(java.time.Duration.ofMinutes(2))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
        
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() == 200) {
            JsonNode jsonResponse = objectMapper.readTree(response.body());
            JsonNode messageNode = jsonResponse.get("message");
            if (messageNode != null && messageNode.get("content") != null) {
                return messageNode.get("content").asText();
            } else {
                return "AI analysis completed but no response content available.";
            }
        } else {
            throw new IOException("Ollama request failed with status: " + response.statusCode() + ", body: " + response.body());
        }
    }
    
    private boolean isOllamaAvailable() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:11434/api/tags"))
                    .timeout(java.time.Duration.ofSeconds(5))
                    .GET()
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 200;
        } catch (Exception e) {
            return false;
        }
    }
    
    public String generateResponse(String prompt) throws IOException, InterruptedException {
        if (!isOllamaAvailable()) {
            return "AI service is not available. Please ensure Ollama is running.";
        }
        
        String requestBody = String.format(
            "{\"model\": \"llama3.1:latest\", \"messages\": [{\"role\": \"system\", \"content\": \"You are a code refactoring assistant. Always respond with valid JSON only.\"}, {\"role\": \"user\", \"content\": \"%s\"}], \"stream\": false}",
            prompt.replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "")
        );
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(OLLAMA_URL))
                .header("Content-Type", "application/json")
                .timeout(java.time.Duration.ofMinutes(2))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
        
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() == 200) {
            JsonNode jsonResponse = objectMapper.readTree(response.body());
            JsonNode messageNode = jsonResponse.get("message");
            if (messageNode != null && messageNode.get("content") != null) {
                return messageNode.get("content").asText();
            }
        }
        
        throw new IOException("Failed to get response from Ollama");
    }
    
    private String generateFallbackAnalysis(String reportContent) {
        if (reportContent.contains("No issues found")) {
            return "\n\n=== AI ANALYSIS (Fallback) ===\n" +
                   "Great job! Your code analysis shows no issues. Here are 3 quick tips:\n" +
                   "1. Keep methods under 20 lines for better readability\n" +
                   "2. Use meaningful variable names that describe their purpose\n" +
                   "3. Add unit tests for critical methods to ensure reliability";
        }
        
        int issueCount = reportContent.split("🚨").length - 1;
        StringBuilder analysis = new StringBuilder();
        analysis.append("\n\n=== AI ANALYSIS (Fallback) ===\n");
        analysis.append(String.format("Found %d code issues. Here are the top recommendations:\n\n", issueCount));
        
        if (reportContent.contains("MagicNumber")) {
            analysis.append("1. MAGIC NUMBERS: Replace numeric literals with named constants\n");
            analysis.append("   Example: final int MAX_RETRIES = 3; instead of using 3 directly\n\n");
        }
        
        if (reportContent.contains("LongMethod")) {
            analysis.append("2. LONG METHODS: Break down large methods into smaller, focused ones\n");
            analysis.append("   Aim for methods under 20 lines with single responsibility\n\n");
        }
        
        if (reportContent.contains("LongIdentifier")) {
            analysis.append("3. LONG IDENTIFIERS: Use concise but descriptive names\n");
            analysis.append("   Balance clarity with brevity in variable/method names\n\n");
        }
        
        analysis.append("Note: Install Ollama with deepseek-coder model for detailed AI analysis.");
        return analysis.toString();
    }
}