# Feature Implementation Analysis: File Viewer with Smell Highlighting

## ✅ FEATURE IS IMPLEMENTABLE

This feature is **fully implementable** in the current DevSync project with minimal architectural changes.

---

## 📋 Current Project Analysis

### Existing Infrastructure ✅
1. **Upload System**: Files are extracted to `uploads/` folder and preserved
2. **Detection System**: 11 detectors already working (MagicNumber, LongMethod, EmptyCatch, etc.)
3. **Report System**: Comprehensive reports with file:line information
4. **Frontend**: React + Vite with routing and component structure
5. **Backend**: Spring Boot with REST API endpoints

### Current Detector Output Format ✅
Detectors already output in this format:
```
🔴 [MagicNumber] DiaryApp.java:19 - Magic number '100' in method - Hardcoded numeric literal | Suggestions: Extract to named constant
🟡 [LongMethod] DiaryApp.java:124 - 'processData' (45 statements) - Method too long | Suggestions: Split logic into smaller methods
```

**Key Information Already Present:**
- ✅ Severity emoji (🔴🟡🟠⚠️)
- ✅ Type in brackets [MagicNumber]
- ✅ File name (DiaryApp.java)
- ✅ Line number (:19)
- ✅ Description
- ✅ Suggestions

---

## 🎯 Implementation Requirements

### 1. Backend Changes

#### A. Create Issue Model (NEW)
**File**: `src/main/java/com/devsync/model/CodeIssue.java`
```java
public class CodeIssue {
    private String type;        // "MagicNumber", "LongMethod", etc.
    private String file;        // "DiaryApp.java"
    private int line;           // 19
    private String severity;    // "Critical", "High", "Medium", "Low"
    private String message;     // Description
    private String suggestion;  // Fix suggestion
}
```

#### B. Create Highlight Mapper (NEW)
**File**: `src/main/java/com/devsync/services/HighlightMapperService.java`
```java
public class HighlightMapperService {
    public Map<String, Map<String, List<Integer>>> generateHighlightMap(List<String> issues) {
        // Parse issues and group by file → smell → lines
        // Return format:
        // {
        //   "DiaryApp.java": {
        //     "MagicNumber": [19, 27, 31],
        //     "LongMethod": [124]
        //   }
        // }
    }
}
```

#### C. Add New API Endpoints (NEW)
**File**: `src/main/java/com/devsync/controller/FileViewController.java`
```java
@RestController
@RequestMapping("/api/fileview")
public class FileViewController {
    
    // Get raw file content
    @GetMapping("/content")
    public ResponseEntity<String> getFileContent(
        @RequestParam String projectPath,
        @RequestParam String fileName,
        @RequestParam String userId
    ) {
        // Read file from uploads/{projectPath}/{fileName}
        // Verify user access
        // Return file content
    }
    
    // Get highlight mapping for a project
    @GetMapping("/highlights")
    public ResponseEntity<Map<String, Map<String, List<Integer>>>> getHighlights(
        @RequestParam String projectPath,
        @RequestParam String userId
    ) {
        // Read report file
        // Parse issues
        // Generate highlight map
        // Return mapping
    }
}
```

#### D. Update AnalysisHistory Model (OPTIONAL)
**File**: `src/main/java/com/devsync/model/AnalysisHistory.java`
Add field to store project folder path:
```java
private String projectPath; // e.g., "uploads/DiaryApp_V.2"
```

---

### 2. Frontend Changes

#### A. Create File Viewer Page (NEW)
**File**: `frontend/src/pages/FileViewer.jsx`
```jsx
export default function FileViewer() {
  const [fileContent, setFileContent] = useState('')
  const [highlights, setHighlights] = useState({})
  const [activeSmell, setActiveSmell] = useState('all')
  const [highlightLines, setHighlightLines] = useState([])
  
  // Fetch file content and highlights
  // Display code with line numbers
  // Show smell tabs
  // Highlight lines based on active smell
}
```

#### B. Add Syntax Highlighter
**Install**: `npm install react-syntax-highlighter`
```jsx
import { Prism as SyntaxHighlighter } from 'react-syntax-highlighter'
```

#### C. Update VisualReport Component (MODIFY)
**File**: `frontend/src/components/VisualReport.jsx`
Add click handler to file names in "Files with Issues" section:
```jsx
const handleFileClick = (fileName) => {
  navigate(`/fileviewer?project=${projectPath}&file=${fileName}`)
}
```

#### D. Add Route (MODIFY)
**File**: `frontend/src/App.jsx`
```jsx
<Route path="/fileviewer" element={<FileViewer />} />
```

---

## 📝 Refined Prompt (Implementation Ready)

### Updated Feature Request

**Add a File Viewer with Smell Highlighting feature:**

1. **Create Backend API Endpoints** (`FileViewController.java`):
   - `GET /api/fileview/content?projectPath={path}&fileName={file}&userId={id}`
     - Read Java file from uploads folder
     - Verify user access via AnalysisHistory
     - Return raw file content
   
   - `GET /api/fileview/highlights?projectPath={path}&userId={id}`
     - Read comprehensive report from project folder
     - Parse all issues using existing format: `🔴 [Type] file.java:line - description`
     - Group by file → smell type → line numbers
     - Return JSON: `{ "File.java": { "MagicNumber": [19, 27], "LongMethod": [124] } }`

2. **Create Highlight Mapper Service** (`HighlightMapperService.java`):
   - Parse report lines starting with `🚨`
   - Extract: type (from `[Type]`), file name, line number
   - Build nested map structure
   - Handle deduplication

3. **Create File Viewer Page** (`FileViewer.jsx`):
   - Accept URL params: `?project={projectPath}&file={fileName}`
   - Fetch file content from `/api/fileview/content`
   - Fetch highlights from `/api/fileview/highlights`
   - Display code with line numbers using `react-syntax-highlighter`
   - Show tabs for each smell type found in the file
   - Highlight lines based on selected tab
   - Style: Match DevSync dashboard (dark/light mode support)

4. **Update VisualReport Component**:
   - Make file names in "Files with Issues" section clickable
   - Navigate to `/fileviewer?project={projectPath}&file={fileName}`
   - Pass projectPath from report metadata

5. **Update AnalysisHistory Model** (Optional):
   - Add `projectPath` field to store upload folder path
   - Update UploadController to save projectPath when creating history

6. **Security**:
   - Verify user owns the project before serving files
   - Sanitize file paths to prevent directory traversal
   - Only allow reading .java files

---

## 🔧 Implementation Steps

### Phase 1: Backend (2-3 hours)
1. Create `CodeIssue.java` model
2. Create `HighlightMapperService.java`
3. Create `FileViewController.java` with 2 endpoints
4. Test endpoints with Postman
5. Update `AnalysisHistory.java` to include projectPath

### Phase 2: Frontend (3-4 hours)
1. Install `react-syntax-highlighter`
2. Create `FileViewer.jsx` page
3. Add route in `App.jsx`
4. Update `VisualReport.jsx` to add click handlers
5. Style to match dashboard theme

### Phase 3: Testing (1-2 hours)
1. Test with existing uploaded projects
2. Verify highlighting accuracy
3. Test security (user access control)
4. Test edge cases (files with no issues, missing files)

**Total Estimated Time**: 6-9 hours

---

## 🎨 UI/UX Design

### File Viewer Layout
```
┌─────────────────────────────────────────────────────┐
│ ← Back to Report    DiaryApp.java                   │
├─────────────────────────────────────────────────────┤
│ [All] [MagicNumber] [LongMethod] [EmptyCatch]      │ ← Tabs
├─────────────────────────────────────────────────────┤
│  1  public class DiaryApp {                         │
│  2      public static void main(String[] args) {    │
│  3          // ...                                   │
│ 19 🔴   int maxSize = 100;  ← Highlighted           │
│ 20      // ...                                       │
│124 🟡   public void processData() {  ← Highlighted  │
│125          // long method...                        │
└─────────────────────────────────────────────────────┘
```

### Features
- Line numbers on left
- Syntax highlighting (Java)
- Smell icons (🔴🟡🟠) on highlighted lines
- Smooth scroll to first highlighted line
- Tooltip on hover showing issue details
- Dark/light mode support

---

## ⚠️ Potential Challenges & Solutions

### Challenge 1: Large Files
**Problem**: Files with 1000+ lines may be slow to render
**Solution**: 
- Implement virtual scrolling
- Lazy load line numbers
- Add "Jump to issue" buttons

### Challenge 2: Multiple Issues on Same Line
**Problem**: One line might have multiple smells
**Solution**:
- Show multiple icons
- Tooltip shows all issues
- Highlight with mixed colors

### Challenge 3: File Path Variations
**Problem**: Reports might have full paths or relative paths
**Solution**:
- Normalize paths in HighlightMapperService
- Extract just filename for matching
- Store both full path and filename

### Challenge 4: Deleted Projects
**Problem**: User might delete uploaded project folder
**Solution**:
- Check file existence before serving
- Show friendly error message
- Suggest re-uploading project

---

## 🚀 Optional Enhancements (Future)

1. **Code Diff View**: Show before/after for suggestions
2. **Quick Fix Buttons**: Apply suggestions directly
3. **Export Highlighted Code**: Download with annotations
4. **Share Link**: Generate shareable link for specific file
5. **Search in File**: Find text within file
6. **Multi-file View**: Split screen for comparing files
7. **Issue Comments**: Add notes to specific issues
8. **Fix History**: Track which issues were fixed

---

## ✅ Conclusion

**This feature is 100% implementable** with the current DevSync architecture. The detectors already provide all necessary information (type, file, line, message, suggestion). The main work is:

1. Creating a parser to extract this info into structured format
2. Building API endpoints to serve file content and highlights
3. Creating a React component to display and highlight code

**No changes needed to existing detectors** - they already output everything required!

**Estimated Complexity**: Medium (6-9 hours)
**Risk Level**: Low
**Value**: High (significantly improves user experience)

---

## 📞 Next Steps

1. Review this analysis
2. Approve the refined prompt
3. Start with Phase 1 (Backend)
4. Test each phase before moving to next
5. Deploy and gather user feedback
