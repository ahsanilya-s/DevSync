package com.devsync.controller;

import com.devsync.services.OllamaService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = "*")
public class AIRefactorController {

    @Autowired
    private OllamaService ollamaService;
    
    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping("/refactor")
    public ResponseEntity<?> refactorCode(@RequestBody Map<String, Object> request) {
        try {
            String smellType = (String) request.get("smellType");
            String fileName = (String) request.get("fileName");
            Integer startLine = (Integer) request.get("startLine");
            Integer endLine = (Integer) request.get("endLine");
            String code = (String) request.get("code");
            String message = (String) request.get("message");

            // Build prompt for Ollama
            String prompt = buildRefactoringPrompt(smellType, fileName, startLine, endLine, code, message);

            // Call Ollama
            String aiResponse = ollamaService.generateResponse(prompt);

            // Parse AI response
            Map<String, String> parsed = parseAiResponse(aiResponse);

            return ResponseEntity.ok(parsed);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "Failed to generate refactoring: " + e.getMessage()));
        }
    }

    private String buildRefactoringPrompt(String smellType, String fileName, 
                                         Integer startLine, Integer endLine, 
                                         String code, String message) {
        return String.format(
            "Refactor this Java code to fix the %s smell.\n\n" +
            "Code:\n%s\n\n" +
            "Return ONLY a JSON object with these 3 fields (no other text):\n" +
            "{\"refactoredCode\":\"...\",\"explanation\":\"...\",\"howRemoved\":\"...\"}",
            smellType, code
        );
    }

    private Map<String, String> parseAiResponse(String response) {
        Map<String, String> result = new HashMap<>();
        
        try {
            response = response.trim();
            response = decodeHtmlEntities(response);
            response = response.replaceAll("```json\\s*", "").replaceAll("```\\s*", "");
            
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
        result.put("refactoredCode", "// AI did not return valid JSON format\n// Please try again or check if Ollama is running properly\n\n// AI Response:\n// " + 
                  aiResponse.substring(0, Math.min(300, aiResponse.length())).replace("\n", "\n// "));
        result.put("explanation", "The AI model did not follow the JSON format. Try regenerating or check your Ollama configuration.");
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
