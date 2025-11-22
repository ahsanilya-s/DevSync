-- DevSync Database Initialization Script
-- Run this script if you encounter database issues

CREATE DATABASE IF NOT EXISTS devsyncdb;
USE devsyncdb;

-- Users table
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Analysis History table
CREATE TABLE IF NOT EXISTS analysis_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL,
    project_name VARCHAR(255) NOT NULL,
    report_path VARCHAR(500) NOT NULL,
    total_issues INT DEFAULT 0,
    critical_issues INT DEFAULT 0,
    warnings INT DEFAULT 0,
    suggestions INT DEFAULT 0,
    analysis_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- User Settings table
CREATE TABLE IF NOT EXISTS user_settings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL UNIQUE,
    ai_enabled BOOLEAN DEFAULT true,
    ai_provider VARCHAR(50) DEFAULT 'ollama',
    max_method_length INT DEFAULT 100,
    max_parameter_count INT DEFAULT 10,
    max_identifier_length INT DEFAULT 25,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Admin Settings table
CREATE TABLE IF NOT EXISTS admin_settings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    setting_key VARCHAR(255) NOT NULL UNIQUE,
    setting_value TEXT NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Insert default admin settings
INSERT IGNORE INTO admin_settings (setting_key, setting_value, description) VALUES
('maintenance_mode', 'false', 'Enable/disable maintenance mode'),
('user_registration_enabled', 'true', 'Allow new user registrations'),
('enable_ai_analysis', 'true', 'Enable AI-powered analysis'),
('max_file_size_mb', '50', 'Maximum file size for uploads in MB'),
('max_analysis_time_minutes', '10', 'Maximum time for analysis in minutes'),
('global_max_method_length', '100', 'Global maximum method length threshold'),
('global_max_parameter_count', '10', 'Global maximum parameter count threshold'),
('allowed_file_types', 'zip,jar', 'Comma-separated list of allowed file types');

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_analysis_history_user_id ON analysis_history(user_id);
CREATE INDEX IF NOT EXISTS idx_analysis_history_date ON analysis_history(analysis_date);
CREATE INDEX IF NOT EXISTS idx_user_settings_user_id ON user_settings(user_id);
CREATE INDEX IF NOT EXISTS idx_admin_settings_key ON admin_settings(setting_key);

SELECT 'Database initialization completed successfully!' as status;