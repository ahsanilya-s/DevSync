package com.devsync.controller;

import com.devsync.services.OllamaService;
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
            "You MUST follow this exact JSON schema. Do not add extra text. Do not add markdown. Do not add comments. " +
            "You MUST respond ONLY in valid JSON.\n\n" +
            "{\n" +
            "  \"refactoredCode\": \"<clean Java code only>\",\n" +
            "  \"explanation\": \"<one short sentence>\",\n" +
            "  \"howRemoved\": \"<one short sentence how %s was fixed>\"\n" +
            "}\n\n" +
            "Now refactor this Java code:\n\n%s",
            smellType, code
        );
    }

    private Map<String, String> parseAiResponse(String response) {
        Map<String, String> result = new HashMap<>();
        
        try {
            response = response.trim();
            int jsonStart = response.indexOf("{");
            int jsonEnd = response.lastIndexOf("}");
            
            if (jsonStart != -1 && jsonEnd != -1) {
                String jsonStr = response.substring(jsonStart, jsonEnd + 1);
                
                String refactoredCode = extractJsonValue(jsonStr, "refactoredCode");
                String explanation = extractJsonValue(jsonStr, "explanation");
                String howRemoved = extractJsonValue(jsonStr, "howRemoved");
                
                if (refactoredCode != null && !refactoredCode.isEmpty()) {
                    result.put("refactoredCode", refactoredCode);
                }
                if (explanation != null && !explanation.isEmpty()) {
                    result.put("explanation", explanation);
                }
                if (howRemoved != null && !howRemoved.isEmpty()) {
                    result.put("howRemoved", howRemoved);
                }
            }
            
            if (!result.containsKey("refactoredCode") || result.get("refactoredCode").isEmpty()) {
                result.put("refactoredCode", "// AI could not generate refactored code\n// Please try again");
            }
            if (!result.containsKey("explanation")) {
                result.put("explanation", "Code refactored to improve quality.");
            }
            if (!result.containsKey("howRemoved")) {
                result.put("howRemoved", "Applied best practices to eliminate code smell.");
            }
            
        } catch (Exception e) {
            result.put("refactoredCode", "// Error parsing AI response");
            result.put("explanation", "Failed to parse response.");
            result.put("howRemoved", "N/A");
        }
        
        return result;
    }
    
    private String extractJsonValue(String json, String key) {
        try {
            String searchKey = "\"" + key + "\":";
            int keyIndex = json.indexOf(searchKey);
            if (keyIndex == -1) return null;
            
            int valueStart = json.indexOf("\"", keyIndex + searchKey.length());
            if (valueStart == -1) return null;
            
            int valueEnd = valueStart + 1;
            while (valueEnd < json.length()) {
                if (json.charAt(valueEnd) == '\"' && json.charAt(valueEnd - 1) != '\\') {
                    break;
                }
                valueEnd++;
            }
            
            String value = json.substring(valueStart + 1, valueEnd);
            value = value.replace("\\n", "\n").replace("\\\"", "\"").replace("\\\\", "\\");
            return value;
        } catch (Exception e) {
            return null;
        }
    }
}
