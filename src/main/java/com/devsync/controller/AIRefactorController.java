package com.devsync.controller;

import com.devsync.model.UserSettings;
import com.devsync.repository.UserSettingsRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = "*")
public class AIRefactorController {

    @Autowired
    private UserSettingsRepository userSettingsRepository;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(java.time.Duration.ofSeconds(30))
            .build();

    @PostMapping("/refactor")
    public ResponseEntity<?> refactorCode(@RequestBody Map<String, Object> request) {
        try {
            String smellType = (String) request.get("smellType");
            String fileName = (String) request.get("fileName");
            Integer startLine = (Integer) request.get("startLine");
            Integer endLine = (Integer) request.get("endLine");
            String code = (String) request.get("code");
            String message = (String) request.get("message");
            String userId = (String) request.get("userId");

            System.out.println("[AI REFACTOR] Request received for " + fileName + " (" + smellType + ")");
            System.out.println("[AI REFACTOR] Lines " + startLine + "-" + endLine + ", code length: " + code.length());

            // Get user settings
            UserSettings settings = userSettingsRepository.findByUserId(userId != null ? userId : "anonymous")
                    .orElse(new UserSettings("anonymous"));

            // Build prompt for AI
            String prompt = buildRefactoringPrompt(smellType, fileName, startLine, endLine, code, message);

            // Call appropriate AI service based on settings
            String aiResponse = callAIService(prompt, settings);
            System.out.println("[AI REFACTOR] Received response: " + aiResponse.substring(0, Math.min(100, aiResponse.length())) + "...");

            // Parse AI response
            Map<String, String> parsed = parseAiResponse(aiResponse);
            System.out.println("[AI REFACTOR] Successfully parsed response");

            return ResponseEntity.ok(parsed);

        } catch (Exception e) {
            System.err.println("[AI REFACTOR ERROR] " + e.getClass().getSimpleName() + ": " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "Failed to generate refactoring: " + e.getMessage()));
        }
    }

    private String callAIService(String prompt, UserSettings settings) throws IOException, InterruptedException {
        if (!settings.getAiEnabled()) {
            throw new IOException("AI is disabled in settings");
        }

        String provider = settings.getAiProvider();
        System.out.println("[AI REFACTOR] Using provider: " + provider);

        switch (provider) {
            case "openai":
                return callOpenAI(prompt, settings);
            case "ollama":
                return callOllama(prompt, settings);
            case "anthropic":
                return callAnthropic(prompt, settings);
            default:
                throw new IOException("Unknown AI provider: " + provider);
        }
    }

    private String callOpenAI(String prompt, UserSettings settings) throws IOException, InterruptedException {
        String apiKey = settings.getAiApiKey();
        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new IOException("OpenAI API key not configured. Please add it in Settings.");
        }

        String model = settings.getAiModel() != null ? settings.getAiModel() : "gpt-3.5-turbo";
        System.out.println("[AI REFACTOR] Calling OpenAI with model: " + model);

        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("model", model);
        requestMap.put("temperature", 0.2);
        requestMap.put("max_tokens", 1000);
        
        Map<String, String> systemMessage = new HashMap<>();
        systemMessage.put("role", "system");
        systemMessage.put("content", "You are a code assistant. Always return results exactly as requested. If JSON is requested, return only valid JSON with no additional text.");
        
        Map<String, String> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", prompt);
        
        requestMap.put("messages", java.util.Arrays.asList(systemMessage, userMessage));
        
        String requestBody = objectMapper.writeValueAsString(requestMap);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.openai.com/v1/chat/completions"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .timeout(java.time.Duration.ofMinutes(2))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("[AI REFACTOR] OpenAI response status: " + response.statusCode());

        if (response.statusCode() == 200) {
            JsonNode jsonResponse = objectMapper.readTree(response.body());
            JsonNode choices = jsonResponse.get("choices");
            if (choices != null && choices.size() > 0) {
                JsonNode message = choices.get(0).get("message");
                if (message != null && message.get("content") != null) {
                    return message.get("content").asText();
                }
            }
        } else {
            String errorBody = response.body();
            System.err.println("[AI REFACTOR ERROR] OpenAI error: " + errorBody);
            throw new IOException("OpenAI API error (" + response.statusCode() + "): " + errorBody);
        }

        throw new IOException("Failed to get response from OpenAI");
    }

    private String callOllama(String prompt, UserSettings settings) throws IOException, InterruptedException {
        String model = settings.getAiModel() != null ? settings.getAiModel() : "llama3.1:latest";
        System.out.println("[AI REFACTOR] Calling Ollama with model: " + model);

        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("model", model);
        requestMap.put("stream", false);
        
        Map<String, String> systemMessage = new HashMap<>();
        systemMessage.put("role", "system");
        systemMessage.put("content", "You are a code assistant. Always return results exactly as requested. If JSON is requested, return only valid JSON with no additional text.");
        
        Map<String, String> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", prompt);
        
        requestMap.put("messages", java.util.Arrays.asList(systemMessage, userMessage));
        
        Map<String, Object> options = new HashMap<>();
        options.put("temperature", 0.2);
        options.put("num_predict", 500);
        requestMap.put("options", options);
        
        String requestBody = objectMapper.writeValueAsString(requestMap);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:11434/api/chat"))
                .header("Content-Type", "application/json")
                .timeout(java.time.Duration.ofMinutes(5))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("[AI REFACTOR] Ollama response status: " + response.statusCode());

        if (response.statusCode() == 200) {
            JsonNode jsonResponse = objectMapper.readTree(response.body());
            JsonNode messageNode = jsonResponse.get("message");
            if (messageNode != null && messageNode.get("content") != null) {
                return messageNode.get("content").asText();
            }
        } else {
            throw new IOException("Ollama error (" + response.statusCode() + "): " + response.body());
        }

        throw new IOException("Failed to get response from Ollama");
    }

    private String callAnthropic(String prompt, UserSettings settings) throws IOException, InterruptedException {
        String apiKey = settings.getAiApiKey();
        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new IOException("Anthropic API key not configured. Please add it in Settings.");
        }

        String model = settings.getAiModel() != null ? settings.getAiModel() : "claude-3-haiku-20240307";
        System.out.println("[AI REFACTOR] Calling Anthropic with model: " + model);

        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("model", model);
        requestMap.put("max_tokens", 1000);
        
        Map<String, String> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", prompt);
        
        requestMap.put("messages", java.util.Arrays.asList(userMessage));
        
        String requestBody = objectMapper.writeValueAsString(requestMap);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.anthropic.com/v1/messages"))
                .header("Content-Type", "application/json")
                .header("x-api-key", apiKey)
                .header("anthropic-version", "2023-06-01")
                .timeout(java.time.Duration.ofMinutes(2))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("[AI REFACTOR] Anthropic response status: " + response.statusCode());

        if (response.statusCode() == 200) {
            JsonNode jsonResponse = objectMapper.readTree(response.body());
            JsonNode content = jsonResponse.get("content");
            if (content != null && content.size() > 0) {
                JsonNode text = content.get(0).get("text");
                if (text != null) {
                    return text.asText();
                }
            }
        } else {
            throw new IOException("Anthropic error (" + response.statusCode() + "): " + response.body());
        }

        throw new IOException("Failed to get response from Anthropic");
    }

    private String buildRefactoringPrompt(String smellType, String fileName, 
                                         Integer startLine, Integer endLine, 
                                         String code, String message) {
        return String.format(
            "Return ONLY valid JSON. No text before or after the JSON object.\n\n" +
            "Refactor this Java code to fix: %s\n\n" +
            "Code:\n%s\n\n" +
            "Output format (JSON only):\n" +
            "{\"refactoredCode\":\"fixed code\",\"explanation\":\"what changed\",\"howRemoved\":\"how smell was fixed\"}",
            smellType, code
        );
    }

    private Map<String, String> parseAiResponse(String response) {
        Map<String, String> result = new HashMap<>();
        
        try {
            response = decodeHtmlEntities(response.trim());
            response = response.replaceAll("```json\\s*", "").replaceAll("```\\s*", "").trim();
            
            int jsonStart = response.indexOf("{");
            int jsonEnd = response.lastIndexOf("}");
            
            if (jsonStart == -1 || jsonEnd == -1 || jsonEnd <= jsonStart) {
                return createFallbackResponse(response);
            }
            
            String jsonStr = response.substring(jsonStart, jsonEnd + 1);
            JsonNode jsonNode = objectMapper.readTree(jsonStr);
            
            if (jsonNode.has("refactoredCode") && !jsonNode.get("refactoredCode").isNull()) {
                result.put("refactoredCode", jsonNode.get("refactoredCode").asText());
            }
            if (jsonNode.has("explanation") && !jsonNode.get("explanation").isNull()) {
                result.put("explanation", jsonNode.get("explanation").asText());
            }
            if (jsonNode.has("howRemoved") && !jsonNode.get("howRemoved").isNull()) {
                result.put("howRemoved", jsonNode.get("howRemoved").asText());
            }
            
            if (!result.containsKey("refactoredCode") || result.get("refactoredCode").trim().isEmpty()) {
                return createFallbackResponse(response);
            }
            if (!result.containsKey("explanation")) {
                result.put("explanation", "Code refactored to improve quality.");
            }
            if (!result.containsKey("howRemoved")) {
                result.put("howRemoved", "Applied best practices to eliminate code smell.");
            }
            
        } catch (Exception e) {
            System.err.println("Error parsing AI response: " + e.getMessage());
            return createFallbackResponse(response);
        }
        
        return result;
    }
    
    private Map<String, String> createFallbackResponse(String aiResponse) {
        Map<String, String> result = new HashMap<>();
        result.put("refactoredCode", "// AI did not return valid JSON format\n// Please try again or check if AI service is configured properly\n\n// AI Response:\n// " + 
                  aiResponse.substring(0, Math.min(300, aiResponse.length())).replace("\n", "\n// "));
        result.put("explanation", "The AI model did not follow the JSON format. Try regenerating or check your AI configuration.");
        result.put("howRemoved", "Unable to provide refactoring due to response format issue.");
        return result;
    }
    
    private String decodeHtmlEntities(String text) {
        return text
            .replace("&quot;", "\"")
            .replace("&#39;", "'")
            .replace("&lt;", "<")
            .replace("&gt;", ">")
            .replace("&amp;", "&");
    }
}
