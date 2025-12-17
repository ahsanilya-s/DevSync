# DevSync - Complete User URL Guide (Localhost)

## 🌐 Base URLs
- **Backend API**: `http://localhost:8080`
- **Frontend**: `http://localhost:5173` (Vite/React development server)

---

## 👤 USER AUTHENTICATION ENDPOINTS

### 1. User Registration & Login
- **POST** `http://localhost:8080/api/auth/signup`
  - Register new user account
  - Body: `{ "username": "...", "email": "...", "password": "..." }`

- **POST** `http://localhost:8080/api/auth/login`
  - User login
  - Body: `{ "email": "...", "password": "..." }`

- **GET** `http://localhost:8080/api/auth/users`
  - Get all users list

- **GET** `http://localhost:8080/api/auth/profile/{userId}`
  - Get user profile details
  - Example: `http://localhost:8080/api/auth/profile/1`

---

## 📊 CODE ANALYSIS ENDPOINTS

### 2. Upload & Analysis
- **GET** `http://localhost:8080/api/upload`
  - Check upload endpoint status

- **POST** `http://localhost:8080/api/upload`
  - Upload ZIP file for code analysis
  - Form-data: `file` (ZIP file), `userId`

- **GET** `http://localhost:8080/api/upload/report?path={reportPath}&userId={userId}`
  - Get analysis report content
  - Example: `http://localhost:8080/api/upload/report?path=uploads/project_123/report.txt&userId=1`

- **GET** `http://localhost:8080/api/upload/history?userId={userId}`
  - Get user's analysis history
  - Example: `http://localhost:8080/api/upload/history?userId=1`

- **POST** `http://localhost:8080/api/upload/fix-counts`
  - Fix issue counts in existing reports

### 3. Visual Reports
- **GET** `http://localhost:8080/api/upload/visual`
  - Test visual report endpoint

- **POST** `http://localhost:8080/api/upload/visual`
  - Generate visual dependency diagram
  - Form-data: `file` (ZIP file)

---

## 📁 FILE VIEWER ENDPOINTS

### 4. Code File Viewing
- **GET** `http://localhost:8080/api/fileview/content?projectPath={path}&fileName={file}&userId={userId}`
  - View Java file content
  - Example: `http://localhost:8080/api/fileview/content?projectPath=uploads/project1&fileName=Main.java&userId=1`

- **GET** `http://localhost:8080/api/fileview/highlights?projectPath={path}&userId={userId}`
  - Get code highlights for issues
  - Example: `http://localhost:8080/api/fileview/highlights?projectPath=uploads/project1&userId=1`

- **GET** `http://localhost:8080/api/fileview/issues?projectPath={path}&fileName={file}&userId={userId}`
  - Get issues for specific file
  - Example: `http://localhost:8080/api/fileview/issues?projectPath=uploads/project1&fileName=Main.java&userId=1`

---

## ⚙️ USER SETTINGS ENDPOINTS

### 5. Detector Configuration
- **GET** `http://localhost:8080/api/settings/{userId}`
  - Get user's detector settings
  - Example: `http://localhost:8080/api/settings/1`

- **POST** `http://localhost:8080/api/settings/{userId}`
  - Save user's detector settings
  - Example: `http://localhost:8080/api/settings/1`

- **GET** `http://localhost:8080/api/settings/detectors/info`
  - Get all available detectors information

- **GET** `http://localhost:8080/api/settings/{userId}/defaults`
  - Get default settings
  - Example: `http://localhost:8080/api/settings/1/defaults`

- **POST** `http://localhost:8080/api/settings/{userId}/reset`
  - Reset settings to defaults
  - Example: `http://localhost:8080/api/settings/1/reset`

- **POST** `http://localhost:8080/api/settings/{userId}/test-ai`
  - Test AI connection (OpenAI/Gemini/Ollama)
  - Example: `http://localhost:8080/api/settings/1/test-ai`

---

## 📜 HISTORY ENDPOINTS

### 6. Analysis History
- **GET** `http://localhost:8080/api/history`
  - Get all analysis history

- **GET** `http://localhost:8080/api/history/report/{folderName}`
  - Get report content by folder name
  - Example: `http://localhost:8080/api/history/report/project_123`

---

## 🤖 AI REFACTORING ENDPOINTS

### 7. AI-Powered Code Refactoring
- **POST** `http://localhost:8080/api/ai/refactor`
  - Get AI refactoring suggestions
  - Body: `{ "smellType": "...", "fileName": "...", "startLine": 10, "endLine": 20, "code": "...", "message": "..." }`

---

## 📄 DETAILED REPORT ENDPOINTS

### 8. HTML Report Generation
- **GET** `http://localhost:8080/api/detailed-report/generate?reportPath={path}&projectPath={path}`
  - Generate detailed HTML report
  - Example: `http://localhost:8080/api/detailed-report/generate?reportPath=uploads/project1/report.txt&projectPath=uploads/project1`

---

## 🐙 GITHUB INTEGRATION ENDPOINTS

### 9. GitHub Repository Analysis
- **GET** `http://localhost:8080/api/github/test`
  - Test GitHub API connection

- **POST** `http://localhost:8080/api/github/repos`
  - Get user's GitHub repositories
  - Body: `{ "token": "github_token" }`

- **POST** `http://localhost:8080/api/github/commits`
  - Get repository commits
  - Body: `{ "token": "...", "owner": "...", "repo": "..." }`

- **POST** `http://localhost:8080/api/github/download`
  - Download repository as ZIP
  - Body: `{ "token": "...", "owner": "...", "repo": "...", "ref": "main" }`

- **POST** `http://localhost:8080/api/github/analyze-commit`
  - Analyze specific commit
  - Body: `{ "token": "...", "owner": "...", "repo": "...", "sha": "...", "userId": "...", "commitMessage": "...", "commitDate": "..." }`

- **GET** `http://localhost:8080/api/github/commit-history?userId={userId}&owner={owner}&repo={repo}`
  - Get commit analysis history
  - Example: `http://localhost:8080/api/github/commit-history?userId=1&owner=john&repo=myproject`

---

## 👨‍💼 ADMIN ENDPOINTS

### 10. Admin Authentication
- **POST** `http://localhost:8080/api/admin/login`
  - Admin login
  - Body: `{ "username": "admin", "password": "aaaa" }`

### 11. Admin Dashboard
- **GET** `http://localhost:8080/api/admin/dashboard`
  - Get admin dashboard statistics

- **GET** `http://localhost:8080/api/admin/users`
  - Get all users with project counts

- **GET** `http://localhost:8080/api/admin/projects`
  - Get all projects

- **GET** `http://localhost:8080/api/admin/reports`
  - Get all reports with statistics

- **POST** `http://localhost:8080/api/admin/fix-counts`
  - Fix report counts (admin utility)

### 12. Admin User Management
- **GET** `http://localhost:8080/api/admin/users/{userId}`
  - Get user details with analysis history
  - Example: `http://localhost:8080/api/admin/users/1`

- **PUT** `http://localhost:8080/api/admin/users/{userId}`
  - Update user information
  - Example: `http://localhost:8080/api/admin/users/1`

- **DELETE** `http://localhost:8080/api/admin/users/{userId}`
  - Delete user and their data
  - Example: `http://localhost:8080/api/admin/users/1`

- **DELETE** `http://localhost:8080/api/admin/users/{userId}/analyses/{analysisId}`
  - Delete specific user analysis
  - Example: `http://localhost:8080/api/admin/users/1/analyses/5`

### 13. Admin Settings Management
- **GET** `http://localhost:8080/api/admin/settings`
  - Get all admin settings grouped by category

- **POST** `http://localhost:8080/api/admin/settings`
  - Save admin settings
  - Body: Array of settings objects

- **POST** `http://localhost:8080/api/admin/settings/init`
  - Initialize default admin settings

---

## 🎨 FRONTEND PAGES (React/Vite)

### 14. User Interface Pages
- **Home/Landing**: `http://localhost:5173/`
- **Login**: `http://localhost:5173/login`
- **Signup**: `http://localhost:5173/signup`
- **Dashboard**: `http://localhost:5173/dashboard`
- **Upload Project**: `http://localhost:5173/upload`
- **Analysis History**: `http://localhost:5173/history`
- **View Report**: `http://localhost:5173/report/{reportId}`
- **File Viewer**: `http://localhost:5173/fileview`
- **Settings**: `http://localhost:5173/settings`
- **GitHub Integration**: `http://localhost:5173/github`
- **Visual Reports**: `http://localhost:5173/visual`
- **User Profile**: `http://localhost:5173/profile`

### 15. Admin Interface Pages
- **Admin Login**: `http://localhost:5173/admin/login`
- **Admin Dashboard**: `http://localhost:5173/admin/dashboard`
- **Admin Users**: `http://localhost:5173/admin/users`
- **Admin Projects**: `http://localhost:5173/admin/projects`
- **Admin Reports**: `http://localhost:5173/admin/reports`
- **Admin Settings**: `http://localhost:5173/admin/settings`

---

## 📋 TYPICAL USER JOURNEY

### New User Flow:
1. Visit `http://localhost:5173/`
2. Click Signup → `http://localhost:5173/signup`
3. Create account via `POST http://localhost:8080/api/auth/signup`
4. Login → `http://localhost:5173/login`
5. Authenticate via `POST http://localhost:8080/api/auth/login`
6. Redirected to Dashboard → `http://localhost:5173/dashboard`

### Code Analysis Flow:
1. Go to Upload → `http://localhost:5173/upload`
2. Upload ZIP file via `POST http://localhost:8080/api/upload`
3. View results on Dashboard → `http://localhost:5173/dashboard`
4. Check History → `http://localhost:5173/history`
5. View detailed report → `http://localhost:5173/report/{reportId}`
6. View file with issues → `http://localhost:5173/fileview`

### Settings Configuration:
1. Go to Settings → `http://localhost:5173/settings`
2. Configure detectors via `GET/POST http://localhost:8080/api/settings/{userId}`
3. Test AI connection via `POST http://localhost:8080/api/settings/{userId}/test-ai`
4. Save settings

### GitHub Integration:
1. Go to GitHub → `http://localhost:5173/github`
2. Enter GitHub token
3. Fetch repos via `POST http://localhost:8080/api/github/repos`
4. Select repo and commit
5. Analyze via `POST http://localhost:8080/api/github/analyze-commit`

### Admin Flow:
1. Visit `http://localhost:5173/admin/login`
2. Login with credentials (admin/aaaa)
3. Access Dashboard → `http://localhost:5173/admin/dashboard`
4. Manage users, projects, reports, and settings

---

## 🔧 CONFIGURATION DETAILS

### Supported Detectors:
1. Long Method Detector
2. Long Parameter List Detector
3. Long Identifier Detector
4. Magic Number Detector
5. Missing Default Detector
6. Empty Catch Detector
7. Complex Conditional Detector
8. Long Statement Detector
9. Broken Modularization Detector
10. Deficient Encapsulation Detector
11. Unnecessary Abstraction Detector

### AI Providers Supported:
- OpenAI (GPT models)
- Google Gemini
- Ollama (Local LLM)

### File Upload Limits:
- Max file size: 50MB
- Allowed types: ZIP, JAR
- Max analysis time: 10 minutes

---

## 📝 NOTES

- All API endpoints support CORS from `http://localhost:5173`
- Admin endpoints require authentication
- User endpoints require userId parameter
- File paths must be validated for security
- Reports are stored in `uploads/` directory
- Database: MySQL on `localhost:3306/devsyncdb`

---

## 🚀 QUICK START

1. Start MySQL database
2. Start backend: `mvn spring-boot:run` (runs on port 8080)
3. Start frontend: `npm run dev` (runs on port 5173)
4. Access application: `http://localhost:5173`
5. Admin access: username=`admin`, password=`aaaa`

---

**Generated for DevSync v2.1**
**Last Updated: 2024**
