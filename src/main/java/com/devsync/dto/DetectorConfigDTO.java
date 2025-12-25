package com.devsync.dto;

import java.util.*;

public class DetectorConfigDTO {
    private String name;
    private String description;
    private boolean enabled;
    private List<ParameterInfo> parameters;
    
    public DetectorConfigDTO(String name, String description, boolean enabled) {
        this.name = name;
        this.description = description;
        this.enabled = enabled;
        this.parameters = new ArrayList<>();
    }
    
    public void addParameter(String name, String type, Object value, Object min, Object max, String description) {
        parameters.add(new ParameterInfo(name, type, value, min, max, description));
    }
    
    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    
    public List<ParameterInfo> getParameters() { return parameters; }
    public void setParameters(List<ParameterInfo> parameters) { this.parameters = parameters; }
    
    public static class ParameterInfo {
        private String name;
        private String type;
        private Object value;
        private Object min;
        private Object max;
        private String description;
        
        public ParameterInfo(String name, String type, Object value, Object min, Object max, String description) {
            this.name = name;
            this.type = type;
            this.value = value;
            this.min = min;
            this.max = max;
            this.description = description;
        }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        
        public Object getValue() { return value; }
        public void setValue(Object value) { this.value = value; }
        
        public Object getMin() { return min; }
        public void setMin(Object min) { this.min = min; }
        
        public Object getMax() { return max; }
        public void setMax(Object max) { this.max = max; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }
}
