# Quick Start: File Viewer Feature

## 🚀 Getting Started

### 1. Start the Application

#### Backend (Spring Boot)
```bash
cd "e:\FYP\V.2 - LTS\BackEnd\devsync - stable - version 2.1 - Copy"
mvnw spring-boot:run
```

#### Frontend (React + Vite)
```bash
cd frontend
npm run dev
```

### 2. Test the Feature

#### Step 1: Upload a Project
1. Login to DevSync
2. Go to Dashboard
3. Upload a Java project (ZIP file)
4. Wait for analysis to complete

#### Step 2: View Visual Report
1. Click "View Report" button
2. Visual report opens with statistics
3. Scroll to "Files with Issues" section

#### Step 3: Open File Viewer
1. Click any file name in the table (they're now blue and clickable!)
2. File viewer opens in new page
3. See your code with syntax highlighting

#### Step 4: Explore Features
1. **Filter by Smell**: Click tabs at top (MagicNumber, LongMethod, etc.)
2. **View All**: Click "All Issues" tab
3. **See Details**: Scroll down to see issue list with suggestions
4. **Toggle Theme**: Click Light/Dark button
5. **Go Back**: Click "← Back to Report"

---

## 🎯 Quick Test with Existing Data

If you have existing analyzed projects:

1. **From History**:
   - Click History icon in dashboard
   - Select any past analysis
   - Click "Visual Report" button
   - Click any file name
   - File viewer opens!

2. **Direct URL** (for testing):
   ```
   http://localhost:5173/fileviewer?project=uploads/DiaryApp_V.2&file=DiaryApp.java
   ```
   Replace with your actual project path and file name.

---

## 🔍 What to Look For

### In File Viewer:
- ✅ Java code with syntax highlighting
- ✅ Line numbers on the left
- ✅ Highlighted lines (colored background)
- ✅ Severity icons (🔴🟡🟠⚪)
- ✅ Tabs for each smell type
- ✅ Issue count in each tab
- ✅ Issue details below code

### In Visual Report:
- ✅ File names are now clickable (blue text)
- ✅ Hover shows pointer cursor
- ✅ Click navigates to file viewer

---

## 🐛 Troubleshooting

### Issue: "File not found"
**Solution**: 
- Check if project folder exists in `uploads/`
- Verify file name is correct (case-sensitive)
- Ensure you're logged in with correct user

### Issue: "Access denied"
**Solution**:
- Make sure you're logged in
- Verify you own the project (uploaded by you)
- Check userId in localStorage

### Issue: No highlights showing
**Solution**:
- Check if report has issues
- Verify report format is correct
- Look at browser console for errors

### Issue: Syntax highlighting not working
**Solution**:
- Check if `react-syntax-highlighter` is installed
- Run `npm install` in frontend folder
- Restart frontend server

---

## 📊 Test Data

### Sample Projects in uploads/:
- `DiaryApp_V.2/` - Good test project with multiple smells
- `smell_project_1_/` - Contains all smell types
- `Hospital-Management-Using-Servlets-master/` - Large project

### Sample Files to Test:
- `DiaryApp.java` - Has MagicNumber, LongMethod
- `MagicNumberExample.java` - Multiple magic numbers
- `LongMethodExample.java` - Long methods
- `EmptyCatchExample.java` - Empty catch blocks

---

## 🎨 UI Elements to Test

### Tabs:
- [ ] "All Issues" tab shows all highlights
- [ ] Individual smell tabs filter correctly
- [ ] Tab counts are accurate
- [ ] Active tab is highlighted

### Code Display:
- [ ] Line numbers are correct
- [ ] Syntax highlighting works
- [ ] Highlighted lines have colored background
- [ ] Severity colors are correct (red, yellow, orange, gray)

### Issue List:
- [ ] Shows all issues for file
- [ ] Filters when tab changes
- [ ] Shows severity icon
- [ ] Shows line number
- [ ] Shows message and suggestion

### Navigation:
- [ ] Back button works
- [ ] Theme toggle works
- [ ] Clicking file in report opens viewer
- [ ] URL parameters are correct

---

## 🔧 API Testing (Optional)

### Using Browser or Postman:

#### 1. Get File Content
```
GET http://localhost:8080/api/fileview/content?projectPath=uploads/DiaryApp_V.2&fileName=DiaryApp.java&userId=YOUR_USER_ID
```

#### 2. Get Highlights
```
GET http://localhost:8080/api/fileview/highlights?projectPath=uploads/DiaryApp_V.2&userId=YOUR_USER_ID
```

#### 3. Get Issues
```
GET http://localhost:8080/api/fileview/issues?projectPath=uploads/DiaryApp_V.2&fileName=DiaryApp.java&userId=YOUR_USER_ID
```

Replace `YOUR_USER_ID` with actual userId from localStorage.

---

## ✅ Feature Checklist

### Basic Functionality:
- [ ] File viewer page loads
- [ ] Code is displayed with syntax highlighting
- [ ] Line numbers are shown
- [ ] Issues are highlighted
- [ ] Tabs work for filtering
- [ ] Issue list is displayed

### Navigation:
- [ ] Can open from Visual Report
- [ ] Can open from History
- [ ] Back button returns to report
- [ ] URL parameters work

### Visual:
- [ ] Dark mode works
- [ ] Light mode works
- [ ] Colors are correct
- [ ] Layout is responsive
- [ ] Icons are displayed

### Security:
- [ ] Can't access other users' files
- [ ] Can't access files outside uploads/
- [ ] Only .java files are allowed
- [ ] Error messages are shown

---

## 🎓 Learning the Code

### Key Files to Understand:

1. **Backend**:
   - `FileViewController.java` - API endpoints
   - `HighlightMapperService.java` - Report parsing
   - `CodeIssue.java` - Data model

2. **Frontend**:
   - `FileViewer.jsx` - Main component
   - `VisualReport.jsx` - Integration point
   - `History.jsx` - Alternative entry point

### Key Concepts:

1. **Report Parsing**: 
   - Reads comprehensive report
   - Extracts issues using regex
   - Groups by file and smell type

2. **Highlight Mapping**:
   - Maps file → smell → line numbers
   - Used for filtering and highlighting

3. **Syntax Highlighting**:
   - Uses `react-syntax-highlighter`
   - Prism theme for Java
   - Custom line props for highlights

---

## 🚀 Next Steps

1. **Test thoroughly** with different projects
2. **Gather user feedback** on UX
3. **Monitor performance** with large files
4. **Consider enhancements** from future roadmap
5. **Document any issues** found

---

## 📞 Need Help?

- Check `FILE_VIEWER_IMPLEMENTATION.md` for detailed docs
- Look at browser console for errors
- Check backend logs for API issues
- Review `FEATURE_ANALYSIS.md` for architecture

---

## 🎉 Success!

If you can:
1. ✅ Click a file name in the report
2. ✅ See the file with syntax highlighting
3. ✅ See highlighted issue lines
4. ✅ Filter by smell type using tabs
5. ✅ View issue details below code

**Then the feature is working perfectly!** 🎊

Enjoy exploring your code with the new File Viewer! 🚀
