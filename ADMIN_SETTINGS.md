# Admin Settings Panel

## Overview
The Admin Settings Panel allows administrators to configure global application filters and settings that apply to all users.

## Settings Categories

### 🔧 App Filters & Limits
- **Max File Size (MB)**: Maximum upload file size limit
- **Allowed File Types**: Comma-separated list of allowed file extensions
- **Max Analysis Time (Minutes)**: Maximum time allowed per analysis
- **Enable AI Analysis**: Global toggle for AI analysis features

### 🛡️ Detection Rules
- **Global Max Method Length**: Override user settings with global maximum
- **Global Max Parameter Count**: Override user settings with global maximum
- **Enable Security Scan**: Enable security vulnerability scanning

### ⚙️ System Settings
- **Maintenance Mode**: Disable uploads during maintenance
- **User Registration Enabled**: Allow new user registrations
- **Max Users**: Maximum number of users allowed

## How It Works

### Filter Application
1. **File Upload Filters**: Applied before processing
   - File size validation
   - File type validation
   - Maintenance mode check

2. **Analysis Filters**: Applied during processing
   - AI analysis global toggle
   - Detection rule overrides
   - Time limits

3. **System Filters**: Applied globally
   - User registration limits
   - Maintenance mode

### Priority Order
1. **Admin Settings** (highest priority)
2. **User Settings** (medium priority)
3. **Default Settings** (lowest priority)

## API Endpoints

### Get Admin Settings
```
GET /api/admin/settings
```
Returns settings grouped by category.

### Save Admin Settings
```
POST /api/admin/settings
Content-Type: application/json

[
  {
    "settingKey": "max_file_size_mb",
    "settingValue": "100",
    "description": "Maximum file size for uploads",
    "category": "filters"
  }
]
```

### Initialize Default Settings
```
POST /api/admin/settings/init
```
Creates default settings if none exist.

## Usage Examples

### Maintenance Mode
Set `maintenance_mode` to `true` to prevent all uploads:
```json
{
  "settingKey": "maintenance_mode",
  "settingValue": "true",
  "category": "system"
}
```

### File Size Limit
Limit uploads to 25MB:
```json
{
  "settingKey": "max_file_size_mb", 
  "settingValue": "25",
  "category": "filters"
}
```

### Disable AI Globally
Turn off AI analysis for all users:
```json
{
  "settingKey": "enable_ai_analysis",
  "settingValue": "false", 
  "category": "filters"
}
```

## Benefits

- **Centralized Control**: Manage app behavior from one location
- **Resource Management**: Control file sizes and analysis times
- **Security**: Filter file types and enable security scans
- **Maintenance**: Easy maintenance mode toggle
- **Scalability**: User limits and resource controls