-- DevSync Settings Enhancement Migration Script
-- This script adds new columns to the user_settings table for all 11 detectors

-- Add Long Method Detector parameters
ALTER TABLE user_settings 
ADD COLUMN IF NOT EXISTS max_method_complexity INT DEFAULT 10 AFTER max_method_length;

-- Add Long Identifier Detector parameters
ALTER TABLE user_settings 
ADD COLUMN IF NOT EXISTS min_identifier_length INT DEFAULT 3 AFTER max_identifier_length;

-- Add Complex Conditional Detector parameters
ALTER TABLE user_settings 
ADD COLUMN IF NOT EXISTS complex_conditional_enabled BOOLEAN DEFAULT TRUE AFTER long_identifier_enabled;

ALTER TABLE user_settings 
ADD COLUMN IF NOT EXISTS max_conditional_operators INT DEFAULT 4 AFTER complex_conditional_enabled;

ALTER TABLE user_settings 
ADD COLUMN IF NOT EXISTS max_nesting_depth INT DEFAULT 3 AFTER max_conditional_operators;

-- Add Long Statement Detector parameters
ALTER TABLE user_settings 
ADD COLUMN IF NOT EXISTS long_statement_enabled BOOLEAN DEFAULT TRUE AFTER max_nesting_depth;

ALTER TABLE user_settings 
ADD COLUMN IF NOT EXISTS max_statement_tokens INT DEFAULT 40 AFTER long_statement_enabled;

ALTER TABLE user_settings 
ADD COLUMN IF NOT EXISTS max_statement_chars INT DEFAULT 250 AFTER max_statement_tokens;

ALTER TABLE user_settings 
ADD COLUMN IF NOT EXISTS max_method_chain_length INT DEFAULT 5 AFTER max_statement_chars;

-- Add Broken Modularization Detector parameters
ALTER TABLE user_settings 
ADD COLUMN IF NOT EXISTS broken_modularization_enabled BOOLEAN DEFAULT TRUE AFTER max_method_chain_length;

ALTER TABLE user_settings 
ADD COLUMN IF NOT EXISTS max_responsibilities INT DEFAULT 3 AFTER broken_modularization_enabled;

ALTER TABLE user_settings 
ADD COLUMN IF NOT EXISTS min_cohesion_index DOUBLE DEFAULT 0.4 AFTER max_responsibilities;

ALTER TABLE user_settings 
ADD COLUMN IF NOT EXISTS max_coupling_count INT DEFAULT 6 AFTER min_cohesion_index;

-- Add Deficient Encapsulation Detector parameters
ALTER TABLE user_settings 
ADD COLUMN IF NOT EXISTS deficient_encapsulation_enabled BOOLEAN DEFAULT TRUE AFTER max_coupling_count;

-- Add Unnecessary Abstraction Detector parameters
ALTER TABLE user_settings 
ADD COLUMN IF NOT EXISTS unnecessary_abstraction_enabled BOOLEAN DEFAULT TRUE AFTER deficient_encapsulation_enabled;

ALTER TABLE user_settings 
ADD COLUMN IF NOT EXISTS max_abstraction_usage INT DEFAULT 1 AFTER unnecessary_abstraction_enabled;

-- Verify the changes
SELECT 
    COLUMN_NAME, 
    DATA_TYPE, 
    COLUMN_DEFAULT, 
    IS_NULLABLE
FROM 
    INFORMATION_SCHEMA.COLUMNS
WHERE 
    TABLE_NAME = 'user_settings'
    AND TABLE_SCHEMA = DATABASE()
ORDER BY 
    ORDINAL_POSITION;

-- Sample query to check existing data
SELECT 
    user_id,
    long_method_enabled,
    max_method_length,
    max_method_complexity,
    complex_conditional_enabled,
    max_conditional_operators,
    broken_modularization_enabled,
    max_responsibilities
FROM 
    user_settings
LIMIT 5;
