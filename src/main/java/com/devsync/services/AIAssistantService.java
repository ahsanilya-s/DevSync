package com.devsync.services;

import com.devsync.model.UserSettings;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

@Service
public class AIAssistantService {
    
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    
    public AIAssistantService() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(java.time.Duration.ofSeconds(30))
                .build();
        this.objectMapper = new ObjectMapper();
    }
    
    public String analyzeWithAI(String reportContent, UserSettings settings) throws IOException, InterruptedException {
        if (settings == null || !settings.getAiEnabled()) {
            return generateFallbackAnalysis(reportContent);
        }
        
        String provider = settings.getAiProvider();
        
        switch (provider) {
            case "ollama":
                return analyzeWithOllama(reportContent, settings);
            case "openai":
                return analyzeWithOpenAI(reportContent, settings);
            case "anthropic":
                return analyzeWithAnthropic(reportContent, settings);
            default:
                return generateFallbackAnalysis(reportContent);
        }
    }
    
    private String analyzeWithOllama(String reportContent, UserSettings settings) throws IOException, InterruptedException {
        if (!isOllamaAvailable()) {
            return generateFallbackAnalysis(reportContent);
        }
        
        String prompt = createPrompt(reportContent);
        String model = settings.getAiModel() != null ? settings.getAiModel() : "deepseek-coder:latest";
        
        String requestBody = String.format(
            "{\"model\": \"%s\", \"messages\": [{\"role\": \"user\", \"content\": \"%s\"}], \"stream\": false}",
            model, prompt.replace("\"", "\\\"").replace("\n", "\\n")
        );
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:11434/api/chat"))
                .header("Content-Type", "application/json")
                .timeout(java.time.Duration.ofMinutes(2))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
        
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() == 200) {
            JsonNode jsonResponse = objectMapper.readTree(response.body());
            JsonNode messageNode = jsonResponse.get("message");
            if (messageNode != null && messageNode.get("content") != null) {
                return "\n\n=== AI ANALYSIS (Ollama) ===\n" + messageNode.get("content").asText();
            }
        }
        
        throw new IOException("Ollama request failed with status: " + response.statusCode());
    }
    
    private String analyzeWithOpenAI(String reportContent, UserSettings settings) throws IOException, InterruptedException {
        String apiKey = settings.getAiApiKey();
        if (apiKey == null || apiKey.trim().isEmpty()) {
            return generateFallbackAnalysis(reportContent) + "\n\nNote: OpenAI API key not configured.";
        }
        
        String prompt = createPrompt(reportContent);
        String model = settings.getAiModel() != null ? settings.getAiModel() : "gpt-3.5-turbo";
        
        String requestBody = String.format(
            "{\"model\": \"%s\", \"messages\": [{\"role\": \"user\", \"content\": \"%s\"}], \"max_tokens\": 500}",
            model, prompt.replace("\"", "\\\"").replace("\n", "\\n")
        );
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.openai.com/v1/chat/completions"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .timeout(java.time.Duration.ofMinutes(1))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
        
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() == 200) {
            JsonNode jsonResponse = objectMapper.readTree(response.body());
            JsonNode choices = jsonResponse.get("choices");
            if (choices != null && choices.size() > 0) {
                JsonNode message = choices.get(0).get("message");
                if (message != null && message.get("content") != null) {
                    return "\n\n=== AI ANALYSIS (OpenAI) ===\n" + message.get("content").asText();
                }
            }
        }
        
        throw new IOException("OpenAI request failed with status: " + response.statusCode());
    }
    
    private String analyzeWithAnthropic(String reportContent, UserSettings settings) throws IOException, InterruptedException {
        String apiKey = settings.getAiApiKey();
        if (apiKey == null || apiKey.trim().isEmpty()) {
            return generateFallbackAnalysis(reportContent) + "\n\nNote: Anthropic API key not configured.";
        }
        
        String prompt = createPrompt(reportContent);
        String model = settings.getAiModel() != null ? settings.getAiModel() : "claude-3-haiku-20240307";
        
        String requestBody = String.format(
            "{\"model\": \"%s\", \"max_tokens\": 500, \"messages\": [{\"role\": \"user\", \"content\": \"%s\"}]}",
            model, prompt.replace("\"", "\\\"").replace("\n", "\\n")
        );
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.anthropic.com/v1/messages"))
                .header("Content-Type", "application/json")
                .header("x-api-key", apiKey)
                .header("anthropic-version", "2023-06-01")
                .timeout(java.time.Duration.ofMinutes(1))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
        
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() == 200) {
            JsonNode jsonResponse = objectMapper.readTree(response.body());
            JsonNode content = jsonResponse.get("content");
            if (content != null && content.size() > 0) {
                JsonNode text = content.get(0).get("text");
                if (text != null) {
                    return "\n\n=== AI ANALYSIS (Anthropic) ===\n" + text.asText();
                }
            }
        }
        
        throw new IOException("Anthropic request failed with status: " + response.statusCode());
    }
    
    private String createPrompt(String reportContent) {
        if (reportContent.contains("No issues found")) {
            return "Great job! Your code analysis shows no issues. Provide 3 quick tips to keep Java code excellent.";
        }
        
        String limitedContent = reportContent.length() > 2000 ? 
            reportContent.substring(0, 2000) + "\n..." : reportContent;
        return "Code issues found:\n" + limitedContent + "\n\nProvide 3 specific fixes for the main issues.";
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
        
        analysis.append("Note: Configure AI assistant in settings for detailed analysis.");
        return analysis.toString();
    }
}