# DevSync Troubleshooting Guide

## Quick Fixes Applied

### 1. Port Configuration Issues ✅ FIXED
- **Problem**: Conflicting ports in application.properties (8081 and 8080)
- **Solution**: Standardized to port 8080 for backend
- **Files Modified**: 
  - `application.properties` - Set server.port=8080
  - `frontend/vite.config.js` - Updated proxy to target localhost:8080

### 2. CORS Configuration ✅ FIXED
- **Problem**: Restrictive CORS settings causing frontend-backend communication issues
- **Solution**: Updated CORS to allow all origins during development
- **Files Modified**: `CorsConfig.java`

### 3. Dependency Conflicts ✅ FIXED
- **Problem**: Duplicate JavaParser dependencies with different versions
- **Solution**: Removed duplicate dependency, kept version 3.25.9
- **Files Modified**: `pom.xml`

### 4. Authentication Error Handling ✅ IMPROVED
- **Problem**: Poor error messages for login/signup failures
- **Solution**: Added comprehensive validation and error handling
- **Files Modified**: `AuthController.java`

### 5. File Upload Issues ✅ IMPROVED
- **Problem**: Missing directory creation and poor error handling
- **Solution**: Added directory creation and better exception handling
- **Files Modified**: `UploadController.java`

## Common Issues and Solutions

### Backend Won't Start

**Symptoms**: 
- Status 500 errors
- "Connection refused" messages
- Application fails to start

**Solutions**:
1. **Check MySQL Connection**:
   ```bash
   # Make sure MySQL is running
   # Check XAMPP Control Panel or Windows Services
   ```

2. **Verify Database Exists**:
   ```sql
   -- Run this in MySQL Workbench or phpMyAdmin
   CREATE DATABASE IF NOT EXISTS devsyncdb;
   ```

3. **Check Port Availability**:
   ```bash
   # Check if port 8080 is in use
   netstat -ano | findstr :8080
   ```

4. **Run Database Init Script**:
   - Execute `src/main/resources/db/init.sql` in your MySQL client

### Frontend Connection Issues

**Symptoms**:
- "Network Error" in browser console
- API calls failing
- CORS errors

**Solutions**:
1. **Verify Backend is Running**:
   - Check http://localhost:8080/api/upload in browser
   - Should return "✅ Upload endpoint ready"

2. **Clear Browser Cache**:
   - Hard refresh (Ctrl+F5)
   - Clear localStorage: `localStorage.clear()`

3. **Check Vite Dev Server**:
   ```bash
   cd frontend
   npm run dev
   ```

### Authentication Problems

**Symptoms**:
- "Invalid credentials" for valid users
- Signup fails with 500 error
- Users can't login after signup

**Solutions**:
1. **Check Database Tables**:
   ```sql
   USE devsyncdb;
   SHOW TABLES;
   SELECT * FROM users LIMIT 5;
   ```

2. **Password Encoding Issues**:
   - Passwords are BCrypt encoded
   - Check UserService.java for encoding logic

3. **Email Validation**:
   - Ensure email format is valid
   - Check for duplicate emails

### File Analysis Errors

**Symptoms**:
- "Status 500" when uploading files
- Analysis fails silently
- No report generated

**Solutions**:
1. **Check File Format**:
   - Only ZIP files are supported
   - File must contain Java source files

2. **File Size Limits**:
   - Default limit: 50MB
   - Check admin settings in database

3. **Uploads Directory**:
   - Ensure `uploads/` directory exists
   - Check write permissions

4. **Java File Structure**:
   ```
   project.zip
   └── src/
       └── main/
           └── java/
               └── com/
                   └── example/
                       └── MyClass.java
   ```

## Startup Checklist

1. ✅ **MySQL Running**: Check XAMPP/Services
2. ✅ **Database Created**: Run init.sql script
3. ✅ **Backend Started**: `mvn spring-boot:run`
4. ✅ **Frontend Started**: `npm run dev` in frontend folder
5. ✅ **Ports Available**: 8080 (backend), 5173 (frontend)

## Quick Start Commands

### Using the Startup Script:
```bash
# Run the automated startup script
start-app.bat
```

### Manual Startup:
```bash
# Terminal 1 - Backend
mvn spring-boot:run

# Terminal 2 - Frontend
cd frontend
npm run dev
```

## Testing the Application

1. **Backend Health Check**:
   - Visit: http://localhost:8080/api/upload
   - Should show: "✅ Upload endpoint ready"

2. **Frontend Access**:
   - Visit: http://localhost:5173
   - Should load the landing page

3. **Authentication Test**:
   - Create a new account
   - Login with created credentials
   - Should redirect to dashboard

4. **File Analysis Test**:
   - Upload a Java project ZIP file
   - Should generate analysis report
   - Check uploads/ directory for extracted files

## Log Files and Debugging

### Backend Logs:
- Console output shows Spring Boot logs
- Database queries logged (if enabled)
- Error stack traces for debugging

### Frontend Logs:
- Browser Developer Tools → Console
- Network tab for API call inspection
- React error boundaries for component errors

### Database Logs:
- MySQL error log (usually in MySQL data directory)
- Check for connection issues or query errors

## Contact and Support

If issues persist after following this guide:
1. Check the console logs for specific error messages
2. Verify all dependencies are installed correctly
3. Ensure database connection parameters are correct
4. Test with a simple Java project first

## Version Information

- **Spring Boot**: 3.5.6
- **Java**: 21
- **React**: 18.3.1
- **Vite**: 7.1.7
- **MySQL**: 8.0+
- **JavaParser**: 3.25.9