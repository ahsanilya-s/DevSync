# "Why?" Button Feature - Quick Start

## What's New?

A new **"Why?"** button has been added next to the **"AI Refactored Code"** button in the File Viewer. This button provides detailed, data-driven explanations for why specific code is flagged as a code smell.

## How to Use

1. **Upload and Analyze** a Java project
2. **View Detailed Report** and click on a file name
3. **Scroll to "Issues in this file"** section
4. **Click the "❓ Why?" button** next to any issue
5. **Read the detailed explanation** with actual metrics
6. **Click "❌ Close"** to hide the explanation

## What You'll See

### Example for LongMethod:
```
This method is flagged as a code smell because: it has 45 statements 
(threshold: 20), cyclomatic complexity is 12 (max: 10), cognitive 
complexity is 18 (max: 15), nesting depth is 5 levels (max: 4). 
Long methods are harder to understand, test, and maintain.
```

### Example for EmptyCatch:
```
This catch block is flagged as a code smell because: the catch block 
is completely empty with no error handling, IOException is a critical 
exception that should never be silently ignored, there is no comment 
explaining why the exception is being ignored.
```

### Example for MissingDefault:
```
This switch statement is flagged as a code smell because: it lacks a 
default case to handle unexpected values, only 3 out of 5 enum values 
are handled, it's in a public method, exposing the risk to external 
callers. Risk score: 0.85.
```

## Key Features

✅ **Data-Driven**: Shows actual metrics vs thresholds
✅ **Specific**: Explains exactly why code is flagged
✅ **Educational**: Helps learn code quality standards
✅ **Non-Generic**: Based on actual analysis data
✅ **Toggle**: Can open/close multiple explanations

## Supported Smells

- ✅ **LongMethod** - Shows line count, complexity metrics, nesting depth
- ✅ **EmptyCatch** - Shows exception type, comment status
- ✅ **MissingDefault** - Shows case coverage, risk score
- ✅ **MagicNumber** - Shows value, repetition, context
- ✅ **LongParameterList** - Shows parameter count, types, complexity

## Files Modified

### Backend
- `CodeIssue.java` - Added `detailedReason` field
- `LongMethodDetector.java` - Added detailed reason generation
- `EmptyCatchDetector.java` - Added detailed reason generation
- `MissingDefaultDetector.java` - Added detailed reason generation
- `MagicNumberDetector.java` - Added detailed reason generation
- `LongParameterListDetector.java` - Added detailed reason generation
- `HighlightMapperService.java` - Updated to parse detailed reasons

### Frontend
- `FileViewer.jsx` - Added Why? button and explanation display

## Testing Checklist

- [ ] Backend compiles successfully ✅
- [ ] Upload a project with code smells
- [ ] Open file viewer for a file with issues
- [ ] Click "Why?" button on each issue type
- [ ] Verify detailed explanation appears
- [ ] Verify explanation contains actual metrics
- [ ] Test in both light and dark modes
- [ ] Verify multiple explanations can be open
- [ ] Verify "Close" button works

## Next Steps

1. **Restart Backend**: `mvnw spring-boot:run`
2. **Restart Frontend**: `cd frontend && npm run dev`
3. **Test the Feature**: Upload a project and try the Why? button
4. **Read Full Documentation**: See `WHY_BUTTON_FEATURE.md`

## Benefits

🎯 **Transparency** - See exact metrics that triggered detection
📚 **Learning** - Understand code quality standards
🔍 **Trust** - Data-driven explanations build confidence
⚡ **Actionable** - Specific numbers help prioritize fixes
🎓 **Educational** - Learn best practices with examples

---

**Status**: ✅ Feature Complete and Ready to Test
**Version**: 2.1
**Date**: 2025-11-30
