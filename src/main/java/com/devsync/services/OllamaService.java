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
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }
    
    public String sendToOllama(String reportContent) throws IOException, InterruptedException {
        String prompt;
        if (reportContent.contains("No issues found")) {
            prompt = "Great job! Your code analysis shows no issues. Here are 3 quick tips to keep your Java code excellent:\n" +
                    "1. Keep methods under 20 lines\n" +
                    "2. Use meaningful variable names\n" +
                    "3. Add unit tests for critical methods";
        } else {
            prompt = "Code issues found:\n" + reportContent + "\n\nProvide 3 specific fixes for these issues.";
        }
        
        String requestBody = String.format(
            "{\"model\": \"deepseek-coder:latest\", \"messages\": [{\"role\": \"user\", \"content\": \"%s\"}], \"stream\": false}",
            prompt.replace("\"", "\\\"").replace("\n", "\\n")
        );
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(OLLAMA_URL))
                .header("Content-Type", "application/json")
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
}