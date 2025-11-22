package com.devsync.model;

import jakarta.persistence.*;

@Entity
@Table(name = "admin_settings")
public class AdminSettings {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "setting_key", unique = true)
    private String settingKey;
    
    @Column(name = "setting_value", columnDefinition = "TEXT")
    private String settingValue;
    
    @Column(name = "description")
    private String description;
    
    @Column(name = "category")
    private String category;
    
    public AdminSettings() {}
    
    public AdminSettings(String settingKey, String settingValue, String description, String category) {
        this.settingKey = settingKey;
        this.settingValue = settingValue;
        this.description = description;
        this.category = category;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getSettingKey() { return settingKey; }
    public void setSettingKey(String settingKey) { this.settingKey = settingKey; }
    
    public String getSettingValue() { return settingValue; }
    public void setSettingValue(String settingValue) { this.settingValue = settingValue; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
}