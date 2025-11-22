package com.devsync.services;

import com.devsync.model.AdminSettings;
import com.devsync.repository.AdminSettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class AdminSettingsService {
    
    @Autowired
    private AdminSettingsRepository adminSettingsRepository;
    
    public String getSettingValue(String key, String defaultValue) {
        Optional<AdminSettings> setting = adminSettingsRepository.findBySettingKey(key);
        return setting.map(AdminSettings::getSettingValue).orElse(defaultValue);
    }
    
    public boolean getBooleanSetting(String key, boolean defaultValue) {
        String value = getSettingValue(key, String.valueOf(defaultValue));
        return Boolean.parseBoolean(value);
    }
    
    public int getIntSetting(String key, int defaultValue) {
        String value = getSettingValue(key, String.valueOf(defaultValue));
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
    
    public boolean isMaintenanceMode() {
        return getBooleanSetting("maintenance_mode", false);
    }
    
    public boolean isUserRegistrationEnabled() {
        return getBooleanSetting("user_registration_enabled", true);
    }
    
    public boolean isAiAnalysisEnabled() {
        return getBooleanSetting("enable_ai_analysis", true);
    }
    
    public int getMaxFileSize() {
        return getIntSetting("max_file_size_mb", 50);
    }
    
    public int getMaxAnalysisTime() {
        return getIntSetting("max_analysis_time_minutes", 10);
    }
    
    public int getGlobalMaxMethodLength() {
        return getIntSetting("global_max_method_length", 100);
    }
    
    public int getGlobalMaxParameterCount() {
        return getIntSetting("global_max_parameter_count", 10);
    }
    
    public String[] getAllowedFileTypes() {
        String types = getSettingValue("allowed_file_types", "zip,jar");
        return types.split(",");
    }
}