package com.devsync.model;

import jakarta.persistence.*;

@Entity
@Table(name = "user_settings")
public class UserSettings {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id")
    private String userId;
    
    // Code smell detection parameters
    @Column(name = "max_method_length")
    private Integer maxMethodLength = 50;
    
    @Column(name = "max_parameter_count")
    private Integer maxParameterCount = 5;
    
    @Column(name = "max_identifier_length")
    private Integer maxIdentifierLength = 30;
    
    @Column(name = "magic_number_threshold")
    private Integer magicNumberThreshold = 3;
    
    // Detector toggles
    @Column(name = "missing_default_enabled")
    private Boolean missingDefaultEnabled = true;
    
    @Column(name = "empty_catch_enabled")
    private Boolean emptyCatchEnabled = true;
    
    @Column(name = "long_method_enabled")
    private Boolean longMethodEnabled = true;
    
    @Column(name = "long_parameter_enabled")
    private Boolean longParameterEnabled = true;
    
    @Column(name = "magic_number_enabled")
    private Boolean magicNumberEnabled = true;
    
    @Column(name = "long_identifier_enabled")
    private Boolean longIdentifierEnabled = true;
    
    // AI Assistant settings
    @Column(name = "ai_provider")
    private String aiProvider = "ollama"; // ollama, openai, anthropic, none
    
    @Column(name = "ai_api_key")
    private String aiApiKey;
    
    @Column(name = "ai_model")
    private String aiModel = "deepseek-coder:latest";
    
    @Column(name = "ai_enabled")
    private Boolean aiEnabled = true;
    
    // Constructors
    public UserSettings() {}
    
    public UserSettings(String userId) {
        this.userId = userId;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    
    public Integer getMaxMethodLength() { return maxMethodLength; }
    public void setMaxMethodLength(Integer maxMethodLength) { this.maxMethodLength = maxMethodLength; }
    
    public Integer getMaxParameterCount() { return maxParameterCount; }
    public void setMaxParameterCount(Integer maxParameterCount) { this.maxParameterCount = maxParameterCount; }
    
    public Integer getMaxIdentifierLength() { return maxIdentifierLength; }
    public void setMaxIdentifierLength(Integer maxIdentifierLength) { this.maxIdentifierLength = maxIdentifierLength; }
    
    public Integer getMagicNumberThreshold() { return magicNumberThreshold; }
    public void setMagicNumberThreshold(Integer magicNumberThreshold) { this.magicNumberThreshold = magicNumberThreshold; }
    
    public Boolean getMissingDefaultEnabled() { return missingDefaultEnabled; }
    public void setMissingDefaultEnabled(Boolean missingDefaultEnabled) { this.missingDefaultEnabled = missingDefaultEnabled; }
    
    public Boolean getEmptyCatchEnabled() { return emptyCatchEnabled; }
    public void setEmptyCatchEnabled(Boolean emptyCatchEnabled) { this.emptyCatchEnabled = emptyCatchEnabled; }
    
    public Boolean getLongMethodEnabled() { return longMethodEnabled; }
    public void setLongMethodEnabled(Boolean longMethodEnabled) { this.longMethodEnabled = longMethodEnabled; }
    
    public Boolean getLongParameterEnabled() { return longParameterEnabled; }
    public void setLongParameterEnabled(Boolean longParameterEnabled) { this.longParameterEnabled = longParameterEnabled; }
    
    public Boolean getMagicNumberEnabled() { return magicNumberEnabled; }
    public void setMagicNumberEnabled(Boolean magicNumberEnabled) { this.magicNumberEnabled = magicNumberEnabled; }
    
    public Boolean getLongIdentifierEnabled() { return longIdentifierEnabled; }
    public void setLongIdentifierEnabled(Boolean longIdentifierEnabled) { this.longIdentifierEnabled = longIdentifierEnabled; }
    
    public String getAiProvider() { return aiProvider; }
    public void setAiProvider(String aiProvider) { this.aiProvider = aiProvider; }
    
    public String getAiApiKey() { return aiApiKey; }
    public void setAiApiKey(String aiApiKey) { this.aiApiKey = aiApiKey; }
    
    public String getAiModel() { return aiModel; }
    public void setAiModel(String aiModel) { this.aiModel = aiModel; }
    
    public Boolean getAiEnabled() { return aiEnabled; }
    public void setAiEnabled(Boolean aiEnabled) { this.aiEnabled = aiEnabled; }
}