# DevSync Settings Feature

## Overview
The Settings feature allows users to customize code smell detection parameters and configure AI assistant integration for personalized code analysis.

## Features

### Code Smell Detection Parameters
- **Max Method Length**: Configure the maximum allowed method length (default: 50 lines)
- **Max Parameter Count**: Set the maximum number of parameters per method (default: 5)
- **Max Identifier Length**: Define the maximum length for variable/method names (default: 30 characters)
- **Magic Number Threshold**: Set sensitivity for magic number detection (default: 3)

### Detector Toggles
Enable/disable specific code smell detectors:
- Missing Default Case detection
- Empty Catch Blocks detection
- Long Methods detection
- Long Parameter Lists detection
- Magic Numbers detection
- Long Identifiers detection

### AI Assistant Configuration
Configure AI providers for enhanced code analysis:

#### Supported Providers
1. **Ollama (Local)**
   - Models: deepseek-coder:latest, codellama:latest, llama2:latest
   - No API key required
   - Runs locally on port 11434

2. **OpenAI**
   - Models: gpt-4, gpt-3.5-turbo, gpt-4-turbo
   - Requires API key
   - Cloud-based service

3. **Anthropic Claude**
   - Models: claude-3-opus, claude-3-sonnet, claude-3-haiku
   - Requires API key
   - Cloud-based service

4. **Disabled**
   - No AI analysis
   - Fallback analysis only

## API Endpoints

### Get User Settings
```
GET /api/settings/{userId}
```
Returns user settings or defaults for new users.

### Save User Settings
```
POST /api/settings/{userId}
Content-Type: application/json

{
  "maxMethodLength": 40,
  "maxParameterCount": 4,
  "maxIdentifierLength": 25,
  "magicNumberThreshold": 2,
  "missingDefaultEnabled": true,
  "emptyCatchEnabled": true,
  "longMethodEnabled": true,
  "longParameterEnabled": true,
  "magicNumberEnabled": true,
  "longIdentifierEnabled": true,
  "aiProvider": "openai",
  "aiApiKey": "sk-...",
  "aiModel": "gpt-3.5-turbo",
  "aiEnabled": true
}
```

### Test AI Connection
```
POST /api/settings/{userId}/test-ai
Content-Type: application/json

{
  "aiProvider": "ollama",
  "aiModel": "deepseek-coder:latest"
}
```

## Database Schema

The `user_settings` table stores user preferences:

```sql
CREATE TABLE user_settings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL,
    max_method_length INT DEFAULT 50,
    max_parameter_count INT DEFAULT 5,
    max_identifier_length INT DEFAULT 30,
    magic_number_threshold INT DEFAULT 3,
    missing_default_enabled BOOLEAN DEFAULT TRUE,
    empty_catch_enabled BOOLEAN DEFAULT TRUE,
    long_method_enabled BOOLEAN DEFAULT TRUE,
    long_parameter_enabled BOOLEAN DEFAULT TRUE,
    magic_number_enabled BOOLEAN DEFAULT TRUE,
    long_identifier_enabled BOOLEAN DEFAULT TRUE,
    ai_provider VARCHAR(50) DEFAULT 'ollama',
    ai_api_key TEXT,
    ai_model VARCHAR(100) DEFAULT 'deepseek-coder:latest',
    ai_enabled BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY unique_user_settings (user_id)
);
```

## Usage

1. **Access Settings**: Click the Settings icon in the sidebar
2. **Configure Parameters**: Adjust code smell detection thresholds
3. **Enable/Disable Detectors**: Toggle specific code smell detectors
4. **Setup AI Assistant**: 
   - Choose provider (Ollama, OpenAI, Anthropic, or None)
   - Configure API key for cloud providers
   - Select appropriate model
   - Test connection
5. **Save Settings**: Click "Save Settings" to persist changes

## AI Provider Setup

### Ollama (Recommended for Local Development)
1. Install Ollama: https://ollama.ai
2. Pull a code model: `ollama pull deepseek-coder:latest`
3. Start Ollama service
4. Select "Ollama (Local)" in settings

### OpenAI
1. Get API key from https://platform.openai.com
2. Select "OpenAI" provider
3. Enter API key
4. Choose model (gpt-3.5-turbo recommended)
5. Test connection

### Anthropic Claude
1. Get API key from https://console.anthropic.com
2. Select "Anthropic Claude" provider
3. Enter API key
4. Choose model (claude-3-haiku recommended for speed)
5. Test connection

## Benefits

- **Personalized Analysis**: Tailor detection to your coding standards
- **Team Consistency**: Share settings across team members
- **AI-Enhanced Insights**: Get intelligent suggestions for code improvements
- **Flexible Configuration**: Enable only relevant detectors for your project
- **Multiple AI Options**: Choose between local and cloud AI providers