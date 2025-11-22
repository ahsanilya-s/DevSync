# User Management Feature

## Overview
The User Management feature in the Admin Panel allows administrators to view detailed user information, manage user accounts, and monitor user activity.

## Features

### 👤 **User Details View**
- Complete user profile information
- Account creation date
- Usage statistics
- Analysis history

### ✏️ **User Account Management**
- Edit username and email
- Update user information
- Account status management

### 📊 **User Analytics**
- Total analyses performed
- Total issues found across all projects
- Project history with detailed metrics
- Activity timeline

### 🗑️ **Data Management**
- Delete individual analyses
- Remove user accounts
- Cascade deletion of user data

## User Detail Modal

### Information Displayed
- **User ID**: Unique identifier
- **Username**: Display name
- **Email**: Contact information
- **Created At**: Account registration date
- **Total Analyses**: Number of projects analyzed
- **Total Issues**: Sum of all issues found

### Analysis History Table
- Project names
- Analysis dates
- Issue counts (Total, Critical, Warnings)
- Individual analysis deletion

## API Endpoints

### Get User Details
```
GET /api/admin/users/{userId}
```
Returns complete user information with analysis history.

**Response:**
```json
{
  "id": 1,
  "username": "john_doe",
  "email": "john@example.com",
  "createdAt": "2024-01-15T10:30:00",
  "totalAnalyses": 5,
  "totalIssuesFound": 23,
  "analyses": [
    {
      "id": 1,
      "projectName": "MyProject",
      "analysisDate": "2024-01-20T14:30:00",
      "totalIssues": 8,
      "criticalIssues": 2,
      "warnings": 4,
      "suggestions": 2
    }
  ]
}
```

### Update User
```
PUT /api/admin/users/{userId}
Content-Type: application/json

{
  "username": "new_username",
  "email": "new_email@example.com"
}
```

### Delete User
```
DELETE /api/admin/users/{userId}
```
Removes user and all associated data.

### Delete User Analysis
```
DELETE /api/admin/users/{userId}/analyses/{analysisId}
```
Removes specific analysis from user's history.

## Usage Workflow

### Viewing User Details
1. Navigate to Admin Panel → Users
2. Click "View Details" on any user row
3. Modal opens with complete user information
4. Browse analysis history and statistics

### Editing User Information
1. Open user detail modal
2. Click "Edit User" button
3. Modify username or email fields
4. Click "Save" to apply changes
5. Changes reflect immediately in the system

### Managing User Data
1. **Delete Analysis**: Click trash icon next to specific analysis
2. **Delete User**: Click "Delete User" button in modal footer
3. Confirmation prompts prevent accidental deletions
4. All related data is removed automatically

## Security Features

### Data Protection
- Confirmation dialogs for destructive actions
- Cascade deletion ensures data consistency
- Admin-only access to user management functions

### Audit Trail
- User creation timestamps
- Analysis history preservation
- Activity tracking through statistics

## Benefits

### For Administrators
- **Complete Visibility**: Full user activity overview
- **Easy Management**: Simple user account operations
- **Data Control**: Granular deletion capabilities
- **Quick Actions**: Streamlined user operations

### For System Maintenance
- **Data Cleanup**: Remove inactive users and old analyses
- **Account Management**: Update user information as needed
- **Usage Monitoring**: Track user engagement and activity
- **Resource Management**: Monitor system usage patterns

## Integration

### With Existing Features
- **Settings Management**: User-specific configuration access
- **Analysis History**: Direct integration with analysis records
- **Admin Dashboard**: Statistics feed into dashboard metrics
- **User Authentication**: Respects existing user system

### Database Relations
- Users table with created_at timestamp
- Analysis history linked by userId
- Cascade deletion maintains referential integrity
- Optimized queries for performance

## Best Practices

### User Management
- Regular cleanup of inactive accounts
- Backup important user data before deletion
- Verify user identity before major changes
- Monitor user activity for security

### Data Handling
- Confirm destructive operations
- Maintain data consistency
- Respect user privacy
- Follow data retention policies