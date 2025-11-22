# DevSync API Documentation

## Authentication Endpoints

### POST /api/auth/signup
Register a new user account.

**Request Body:**
```json
{
  "username": "string",
  "email": "string", 
  "password": "string"
}
```

### POST /api/auth/login
Authenticate user and get session.

**Request Body:**
```json
{
  "username": "string",
  "password": "string"
}
```

## Analysis Endpoints

### POST /api/upload
Upload and analyze Java project.

**Request:** Multipart form data with ZIP file
**Response:**
```json
{
  "issues": ["string"],
  "totalFiles": "number",
  "totalIssues": "number", 
  "severityCounts": {
    "Critical": "number",
    "High": "number",
    "Medium": "number"
  },
  "summary": "string"
}
```

### GET /api/history
Get user's analysis history.

**Response:**
```json
[
  {
    "id": "number",
    "projectName": "string",
    "analysisDate": "datetime",
    "issueCount": "number",
    "reportPath": "string"
  }
]
```

## Admin Endpoints

### POST /api/admin/login
Admin authentication.

### GET /api/admin/stats
Get system statistics (admin only).

**Response:**
```json
{
  "totalUsers": "number",
  "totalAnalyses": "number",
  "avgIssuesPerProject": "number"
}
```

## Error Responses

All endpoints return standard HTTP status codes:
- 200: Success
- 400: Bad Request
- 401: Unauthorized
- 403: Forbidden
- 500: Internal Server Error

Error format:
```json
{
  "error": "string",
  "message": "string",
  "timestamp": "datetime"
}
```