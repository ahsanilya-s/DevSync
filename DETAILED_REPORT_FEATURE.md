# Detailed Report Export Feature

## Overview
Enhanced the "Export Report" button to generate detailed downloadable reports that include:
- All source code snippets where code smells are detected
- Highlighted problematic lines
- Detailed explanations of WHY each issue is a code smell
- Specific refactoring suggestions for each issue

## Changes Made

### Frontend Changes
**File: `frontend/src/components/EnhancedVisualReport.jsx`**

1. **Added new export format option**: "HTML (Detailed with Code)"
   - Users can now choose between:
     - PDF (Summary)
     - HTML (Summary) 
     - HTML (Detailed with Code) ← NEW

2. **New function: `handleExportDetailedReport()`**
   - Generates comprehensive HTML report with embedded code snippets
   - Fetches actual source code for each issue
   - Includes line numbers and highlights the problematic line with → marker

3. **New function: `generateDetailedIssuesHTML()`**
   - Iterates through all issues
   - Fetches code snippet for each issue
   - Generates formatted HTML with:
     - Issue severity badge
     - Issue type and location
     - Source code block (5 lines before + 5 lines after)
     - "Why is this a code smell?" explanation box
     - "How to fix it" suggestion box

4. **New function: `fetchCodeSnippet()`**
   - Calls existing `/api/fileview/content` endpoint
   - Extracts relevant code lines around the issue
   - Formats with line numbers and markers

5. **Helper functions**:
   - `getWhyExplanation()`: Provides detailed explanations for each smell type
   - `getFixSuggestion()`: Provides specific refactoring guidance
   - `escapeHtml()`: Safely escapes HTML in code snippets

### Backend Changes (Optional - Not Required)
**Files Created:**
- `src/main/java/com/devsync/services/DetailedReportService.java`
- `src/main/java/com/devsync/controller/DetailedReportController.java`

These backend files were created but are NOT required for the feature to work. The frontend implementation is self-contained and uses existing APIs.

## How It Works

1. User clicks "Export Report" button in the code quality report modal
2. User selects "HTML (Detailed with Code)" from dropdown
3. System:
   - Parses all issues from the report
   - For each issue, fetches the source file content via existing API
   - Extracts code snippet (11 lines: 5 before + issue line + 5 after)
   - Generates HTML with:
     - Styled issue cards
     - Syntax-highlighted code blocks
     - Explanation boxes
     - Suggestion boxes
4. Downloads complete HTML file that can be:
   - Opened in any browser
   - Printed to PDF
   - Shared with team members
   - Archived for compliance

## Report Contents

### For Each Issue:
1. **Header Section**
   - Severity badge (Critical/High/Medium/Low)
   - Issue type (e.g., LongMethod, LargeClass)
   - File location (filename:line)

2. **Issue Description**
   - Clear description of the problem

3. **Source Code Block**
   - Actual code from the file
   - Line numbers
   - Arrow (→) marking the problematic line
   - Syntax highlighting (dark theme)

4. **Why Box (Blue)**
   - Explains WHY this is a code smell
   - Educational content about the issue
   - Impact on maintainability

5. **How to Fix Box (Green)**
   - Specific refactoring suggestions
   - Best practices to apply
   - Design patterns to consider

## Supported Code Smell Types

The feature provides detailed explanations and suggestions for:
- LongMethod
- LargeClass
- LongParameterList
- DuplicatedCode
- DeadCode
- ComplexConditional
- MagicNumber
- EmptyCatchBlock
- GodClass

## Usage

1. Analyze a project
2. View the code quality report
3. Click the dropdown next to "Export Report"
4. Select "HTML (Detailed with Code)"
5. Click "Export Report" button
6. HTML file downloads automatically
7. Open in browser to view detailed report with all code snippets

## Benefits

- **For Developers**: See exact code that needs refactoring with context
- **For Code Reviews**: Share detailed findings with explanations
- **For Learning**: Understand WHY issues matter and HOW to fix them
- **For Documentation**: Archive detailed analysis results
- **For Compliance**: Maintain records of code quality assessments

## Technical Notes

- Uses existing `/api/fileview/content` endpoint (no new backend required)
- Async code fetching for better performance
- Responsive HTML design
- Print-friendly CSS
- Self-contained HTML (no external dependencies)
- Works offline once downloaded
