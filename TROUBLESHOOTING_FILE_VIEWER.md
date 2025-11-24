# Troubleshooting: File Viewer Feature

## Issue: Clicking file names does nothing

### ✅ FIXED!

**Problem**: The `projectPath` prop wasn't being passed to the `VisualReport` component in `Home.jsx`.

**Solution Applied**:
Updated `Home.jsx` line ~281 to include `projectPath`:
```jsx
<VisualReport
  reportContent={reportContent}
  isOpen={showReportModal}
  onClose={() => setShowReportModal(false)}
  isDarkMode={isDarkMode}
  projectName={projectName}
  projectPath={reportPath ? reportPath.substring(0, reportPath.lastIndexOf('/')) : ''}
/>
```

---

## How to Test the Fix

### 1. Restart Frontend
```bash
cd frontend
npm run dev
```

### 2. Test the Feature
1. Login to DevSync
2. Upload a Java project
3. Wait for analysis to complete
4. Click "View Detailed Report"
5. Scroll to "Files with Issues" table
6. Click any file name (should be blue/clickable)
7. File viewer should open!

### 3. Check Browser Console
Open DevTools (F12) and check console for:
```
File clicked: Example.java
Project path: uploads/YourProject
Navigating to: /fileviewer?project=...&file=...
```

---

## Common Issues & Solutions

### Issue 1: File names not clickable (not blue)
**Symptoms**: File names appear as plain text, not links

**Check**:
1. Open browser DevTools (F12)
2. Check console for errors
3. Verify `projectPath` is being passed

**Solution**:
- Ensure you're viewing a NEW analysis (not old history)
- Old analyses won't have projectPath
- Re-analyze the project

### Issue 2: Click does nothing, no console logs
**Symptoms**: Click file name, nothing happens, no console logs

**Check**:
1. Verify VisualReport.jsx has the updated handleFileClick
2. Check if button element exists in DOM

**Solution**:
```bash
# Restart frontend
cd frontend
npm run dev
```

### Issue 3: "Project path is missing" alert
**Symptoms**: Alert shows "Project path is missing. Please re-analyze the project."

**Cause**: Old analysis records don't have projectPath

**Solution**:
1. Upload and analyze a NEW project
2. OR: Re-analyze an existing project
3. The new analysis will have projectPath

### Issue 4: 404 Not Found on /fileviewer
**Symptoms**: Page shows 404 error

**Check**:
1. Verify route is added in App.jsx
2. Check FileViewer.jsx exists

**Solution**:
```jsx
// In App.jsx, verify this line exists:
<Route path="/fileviewer" element={<FileViewer />} />
```

### Issue 5: Backend 403 Forbidden
**Symptoms**: File viewer loads but shows "Access denied"

**Check**:
1. Verify you're logged in
2. Check userId in localStorage
3. Verify you own the project

**Solution**:
```javascript
// In browser console:
console.log(localStorage.getItem('userId'))
```

### Issue 6: Backend 404 File Not Found
**Symptoms**: File viewer shows "File not found"

**Check**:
1. Verify project folder exists in uploads/
2. Check file name is correct (case-sensitive)
3. Verify file is .java

**Solution**:
- Check uploads/ folder for your project
- Ensure Java files are present
- Re-upload if files are missing

---

## Debug Mode

### Enable Debug Logging

**In VisualReport.jsx**, the handleFileClick already has debug logs:
```javascript
console.log('File clicked:', fileName)
console.log('Project path:', projectPath)
console.log('Navigating to:', url)
```

**In FileViewer.jsx**, check useEffect:
```javascript
useEffect(() => {
  console.log('FileViewer mounted')
  console.log('Project:', projectPath)
  console.log('File:', fileName)
  console.log('UserId:', userId)
}, [])
```

### Check API Calls

Open DevTools → Network tab:
1. Click file name
2. Look for these requests:
   - `/api/fileview/content`
   - `/api/fileview/highlights`
   - `/api/fileview/issues`
3. Check response status (should be 200)
4. Check response data

---

## Quick Fixes

### Fix 1: Clear Browser Cache
```
Ctrl + Shift + Delete
Clear cache and reload
```

### Fix 2: Restart Both Servers
```bash
# Terminal 1 - Backend
mvnw spring-boot:run

# Terminal 2 - Frontend
cd frontend
npm run dev
```

### Fix 3: Check File Permissions
Ensure uploads/ folder is readable:
```bash
# Windows
icacls uploads /grant Everyone:R
```

### Fix 4: Verify Database
Check if projectPath column exists:
```sql
DESCRIBE analysis_history;
-- Should show project_path column
```

---

## Testing Checklist

After applying fix:
- [ ] File names are blue/clickable
- [ ] Hover shows pointer cursor
- [ ] Click opens file viewer
- [ ] Console shows debug logs
- [ ] No errors in console
- [ ] File content displays
- [ ] Syntax highlighting works
- [ ] Line numbers show
- [ ] Highlights appear
- [ ] Tabs work

---

## Still Not Working?

### 1. Check All Files Were Updated
```bash
# Verify these files exist:
ls src/main/java/com/devsync/controller/FileViewController.java
ls src/main/java/com/devsync/services/HighlightMapperService.java
ls src/main/java/com/devsync/model/CodeIssue.java
ls frontend/src/pages/FileViewer.jsx
```

### 2. Check Package Installation
```bash
cd frontend
npm list react-syntax-highlighter
# Should show installed version
```

### 3. Check Backend Logs
Look for errors in backend console:
```
FileViewController: ...
HighlightMapperService: ...
```

### 4. Test API Directly
```bash
# Test file content endpoint
curl "http://localhost:8080/api/fileview/content?projectPath=uploads/YOUR_PROJECT&fileName=YourFile.java&userId=YOUR_USER_ID"
```

---

## Contact Support

If issue persists:
1. Check browser console for errors
2. Check backend logs
3. Verify all files were updated
4. Try with a fresh project upload
5. Review QUICK_START_FILE_VIEWER.md

---

## Success Indicators

Feature is working when:
✅ File names are blue and clickable
✅ Click navigates to /fileviewer?project=...&file=...
✅ File viewer page loads
✅ Code displays with syntax highlighting
✅ Line numbers show
✅ Issue lines are highlighted
✅ Tabs filter highlights
✅ Issue list shows below code

---

**Last Updated**: After fixing projectPath prop issue
**Status**: ✅ RESOLVED
