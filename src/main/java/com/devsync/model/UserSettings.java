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
    
    // 1. Long Method Detector
    @Column(name = "long_method_enabled")
    private Boolean longMethodEnabled = true;
    @Column(name = "max_method_length")
    private Integer maxMethodLength = 50;
    @Column(name = "max_method_complexity")
    private Integer maxMethodComplexity = 10;
    
    // 2. Long Parameter List Detector
    @Column(name = "long_parameter_enabled")
    private Boolean longParameterEnabled = true;
    @Column(name = "max_parameter_count")
    private Integer maxParameterCount = 5;
    
    // 3. Long Identifier Detector
    @Column(name = "long_identifier_enabled")
    private Boolean longIdentifierEnabled = true;
    @Column(name = "max_identifier_length")
    private Integer maxIdentifierLength = 30;
    @Column(name = "min_identifier_length")
    private Integer minIdentifierLength = 3;
    
    // 4. Magic Number Detector
    @Column(name = "magic_number_enabled")
    private Boolean magicNumberEnabled = true;
    @Column(name = "magic_number_threshold")
    private Integer magicNumberThreshold = 3;
    
    // 5. Missing Default Detector
    @Column(name = "missing_default_enabled")
    private Boolean missingDefaultEnabled = true;
    
    // 6. Empty Catch Detector
    @Column(name = "empty_catch_enabled")
    private Boolean emptyCatchEnabled = true;
    
    // 7. Complex Conditional Detector
    @Column(name = "complex_conditional_enabled")
    private Boolean complexConditionalEnabled = true;
    @Column(name = "max_conditional_operators")
    private Integer maxConditionalOperators = 4;
    @Column(name = "max_nesting_depth")
    private Integer maxNestingDepth = 3;
    
    // 8. Long Statement Detector
    @Column(name = "long_statement_enabled")
    private Boolean longStatementEnabled = true;
    @Column(name = "max_statement_tokens")
    private Integer maxStatementTokens = 40;
    @Column(name = "max_statement_chars")
    private Integer maxStatementChars = 250;
    @Column(name = "max_method_chain_length")
    private Integer maxMethodChainLength = 5;
    
    // 9. Broken Modularization Detector
    @Column(name = "broken_modularization_enabled")
    private Boolean brokenModularizationEnabled = true;
    @Column(name = "max_responsibilities")
    private Integer maxResponsibilities = 3;
    @Column(name = "min_cohesion_index")
    private Double minCohesionIndex = 0.4;
    @Column(name = "max_coupling_count")
    private Integer maxCouplingCount = 6;
    
    // 10. Deficient Encapsulation Detector
    @Column(name = "deficient_encapsulation_enabled")
    private Boolean deficientEncapsulationEnabled = true;
    
    // 11. Unnecessary Abstraction Detector
    @Column(name = "unnecessary_abstraction_enabled")
    private Boolean unnecessaryAbstractionEnabled = true;
    @Column(name = "max_abstraction_usage")
    private Integer maxAbstractionUsage = 1;
    
    // 12. Memory Leak Detector
    @Column(name = "memory_leak_enabled")
    private Boolean memoryLeakEnabled = true;
    
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
    
    // Long Method
    public Boolean getLongMethodEnabled() { return longMethodEnabled; }
    public void setLongMethodEnabled(Boolean longMethodEnabled) { this.longMethodEnabled = longMethodEnabled; }
    public Integer getMaxMethodLength() { return maxMethodLength; }
    public void setMaxMethodLength(Integer maxMethodLength) { this.maxMethodLength = maxMethodLength; }
    public Integer getMaxMethodComplexity() { return maxMethodComplexity; }
    public void setMaxMethodComplexity(Integer maxMethodComplexity) { this.maxMethodComplexity = maxMethodComplexity; }
    
    // Long Parameter List
    public Boolean getLongParameterEnabled() { return longParameterEnabled; }
    public void setLongParameterEnabled(Boolean longParameterEnabled) { this.longParameterEnabled = longParameterEnabled; }
    public Integer getMaxParameterCount() { return maxParameterCount; }
    public void setMaxParameterCount(Integer maxParameterCount) { this.maxParameterCount = maxParameterCount; }
    
    // Long Identifier
    public Boolean getLongIdentifierEnabled() { return longIdentifierEnabled; }
    public void setLongIdentifierEnabled(Boolean longIdentifierEnabled) { this.longIdentifierEnabled = longIdentifierEnabled; }
    public Integer getMaxIdentifierLength() { return maxIdentifierLength; }
    public void setMaxIdentifierLength(Integer maxIdentifierLength) { this.maxIdentifierLength = maxIdentifierLength; }
    public Integer getMinIdentifierLength() { return minIdentifierLength; }
    public void setMinIdentifierLength(Integer minIdentifierLength) { this.minIdentifierLength = minIdentifierLength; }
    
    // Magic Number
    public Boolean getMagicNumberEnabled() { return magicNumberEnabled; }
    public void setMagicNumberEnabled(Boolean magicNumberEnabled) { this.magicNumberEnabled = magicNumberEnabled; }
    public Integer getMagicNumberThreshold() { return magicNumberThreshold; }
    public void setMagicNumberThreshold(Integer magicNumberThreshold) { this.magicNumberThreshold = magicNumberThreshold; }
    
    // Missing Default
    public Boolean getMissingDefaultEnabled() { return missingDefaultEnabled; }
    public void setMissingDefaultEnabled(Boolean missingDefaultEnabled) { this.missingDefaultEnabled = missingDefaultEnabled; }
    
    // Empty Catch
    public Boolean getEmptyCatchEnabled() { return emptyCatchEnabled; }
    public void setEmptyCatchEnabled(Boolean emptyCatchEnabled) { this.emptyCatchEnabled = emptyCatchEnabled; }
    
    // Complex Conditional
    public Boolean getComplexConditionalEnabled() { return complexConditionalEnabled; }
    public void setComplexConditionalEnabled(Boolean complexConditionalEnabled) { this.complexConditionalEnabled = complexConditionalEnabled; }
    public Integer getMaxConditionalOperators() { return maxConditionalOperators; }
    public void setMaxConditionalOperators(Integer maxConditionalOperators) { this.maxConditionalOperators = maxConditionalOperators; }
    public Integer getMaxNestingDepth() { return maxNestingDepth; }
    public void setMaxNestingDepth(Integer maxNestingDepth) { this.maxNestingDepth = maxNestingDepth; }
    
    // Long Statement
    public Boolean getLongStatementEnabled() { return longStatementEnabled; }
    public void setLongStatementEnabled(Boolean longStatementEnabled) { this.longStatementEnabled = longStatementEnabled; }
    public Integer getMaxStatementTokens() { return maxStatementTokens; }
    public void setMaxStatementTokens(Integer maxStatementTokens) { this.maxStatementTokens = maxStatementTokens; }
    public Integer getMaxStatementChars() { return maxStatementChars; }
    public void setMaxStatementChars(Integer maxStatementChars) { this.maxStatementChars = maxStatementChars; }
    public Integer getMaxMethodChainLength() { return maxMethodChainLength; }
    public void setMaxMethodChainLength(Integer maxMethodChainLength) { this.maxMethodChainLength = maxMethodChainLength; }
    
    // Broken Modularization
    public Boolean getBrokenModularizationEnabled() { return brokenModularizationEnabled; }
    public void setBrokenModularizationEnabled(Boolean brokenModularizationEnabled) { this.brokenModularizationEnabled = brokenModularizationEnabled; }
    public Integer getMaxResponsibilities() { return maxResponsibilities; }
    public void setMaxResponsibilities(Integer maxResponsibilities) { this.maxResponsibilities = maxResponsibilities; }
    public Double getMinCohesionIndex() { return minCohesionIndex; }
    public void setMinCohesionIndex(Double minCohesionIndex) { this.minCohesionIndex = minCohesionIndex; }
    public Integer getMaxCouplingCount() { return maxCouplingCount; }
    public void setMaxCouplingCount(Integer maxCouplingCount) { this.maxCouplingCount = maxCouplingCount; }
    
    // Deficient Encapsulation
    public Boolean getDeficientEncapsulationEnabled() { return deficientEncapsulationEnabled; }
    public void setDeficientEncapsulationEnabled(Boolean deficientEncapsulationEnabled) { this.deficientEncapsulationEnabled = deficientEncapsulationEnabled; }
    
    // Unnecessary Abstraction
    public Boolean getUnnecessaryAbstractionEnabled() { return unnecessaryAbstractionEnabled; }
    public void setUnnecessaryAbstractionEnabled(Boolean unnecessaryAbstractionEnabled) { this.unnecessaryAbstractionEnabled = unnecessaryAbstractionEnabled; }
    public Integer getMaxAbstractionUsage() { return maxAbstractionUsage; }
    public void setMaxAbstractionUsage(Integer maxAbstractionUsage) { this.maxAbstractionUsage = maxAbstractionUsage; }
    
    // Memory Leak
    public Boolean getMemoryLeakEnabled() { return memoryLeakEnabled; }
    public void setMemoryLeakEnabled(Boolean memoryLeakEnabled) { this.memoryLeakEnabled = memoryLeakEnabled; }
    
    public String getAiProvider() { return aiProvider; }
    public void setAiProvider(String aiProvider) { this.aiProvider = aiProvider; }
    
    public String getAiApiKey() { return aiApiKey; }
    public void setAiApiKey(String aiApiKey) { this.aiApiKey = aiApiKey; }
    
    public String getAiModel() { return aiModel; }
    public void setAiModel(String aiModel) { this.aiModel = aiModel; }
    
    public Boolean getAiEnabled() { return aiEnabled; }
    public void setAiEnabled(Boolean aiEnabled) { this.aiEnabled = aiEnabled; }
}