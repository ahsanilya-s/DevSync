# Long Method Threshold Display Implementation

## Overview
This feature displays detailed threshold information when users click "Why?" on Long Method code smells in the File Viewer.

## Components Modified

### 1. Backend - DTO Layer
**File**: `src/main/java/com/devsync/dto/LongMethodThresholdDetails.java`
- New DTO class to structure threshold data
- Contains all 5 threshold metrics with their values and status

### 2. Backend - Model Layer
**File**: `src/main/java/com/devsync/model/CodeIssue.java`
- Added `thresholdDetails` field to store structured threshold information
- Linked to `LongMethodThresholdDetails` DTO

### 3. Backend - Service Layer
**File**: `src/main/java/com/devsync/services/HighlightMapperService.java`
- Added `parseThresholdDetails()` method to extract threshold data from detailed reason text
- Automatically populates `thresholdDetails` for LongMethod issues
- Uses regex patterns to parse all 5 metrics

### 4. Frontend - UI Layer
**File**: `frontend/src/pages/FileViewer.jsx`
- Enhanced "Why?" section to display structured threshold cards
- Shows 5 metrics in a responsive grid layout:
  - Statement Count (Base & Critical thresholds)
  - Cyclomatic Complexity
  - Cognitive Complexity
  - Nesting Depth
  - Responsibility Count
- Color-coded cards (red for exceeded, green for within limits)
- Visual indicators (✅/❌) for each metric

## Threshold Metrics Displayed

### 1. Statement Count
- **Current Value**: Actual number of statements in the method
- **Base Threshold**: 20 (default)
- **Critical Threshold**: 50 (default)
- **Status**: Exceeded or Within limits

### 2. Cyclomatic Complexity
- **Current Value**: Number of decision points
- **Max Threshold**: 10
- **Description**: Measures if/for/while/switch statements
- **Status**: Exceeded or Within limits

### 3. Cognitive Complexity
- **Current Value**: How hard the code is to understand
- **Max Threshold**: 15
- **Description**: Measures mental effort to understand code
- **Status**: Exceeded or Within limits

### 4. Nesting Depth
- **Current Value**: Maximum nesting level
- **Max Threshold**: 4 levels
- **Description**: Deeply nested code is hard to follow
- **Status**: Exceeded or Within limits

### 5. Responsibility Count
- **Current Value**: Number of different responsibilities
- **Max Threshold**: 3
- **Description**: Single Responsibility Principle compliance
- **Status**: Exceeded or Within limits

## User Experience

1. User opens File Viewer for a Java file
2. Sees list of issues at the bottom
3. Clicks "❓ Why?" button on a Long Method issue
4. System displays:
   - 5 threshold metric cards in a grid
   - Each card shows: current value, max value, status, description
   - Color coding: Red (exceeded), Green (within limits)
   - Summary message explaining detection logic
5. User understands exactly why the method was flagged

## Technical Flow

```
LongMethodDetector.generateDetailedReason()
    ↓
Report file with DetailedReason text
    ↓
HighlightMapperService.parseIssueLine()
    ↓
HighlightMapperService.parseThresholdDetails()
    ↓
CodeIssue with thresholdDetails populated
    ↓
FileViewController.getIssues() API
    ↓
Frontend FileViewer displays structured cards
```

## Benefits

1. **Transparency**: Users see exact threshold values
2. **Educational**: Helps developers understand code quality metrics
3. **Actionable**: Clear indication of which metrics need improvement
4. **Visual**: Color-coded cards make it easy to scan
5. **Comprehensive**: All 5 metrics displayed in one view

## Future Enhancements

- Add similar threshold displays for other code smell types
- Allow users to customize thresholds via settings
- Add trend charts showing metric improvements over time
- Export threshold data for reporting
