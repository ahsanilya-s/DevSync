# Unused Variable Detector - Feature Documentation

## Overview
The UnusedVariableDetector is a new code smell detector that identifies variables declared but never used in Java code. This helps improve code quality by removing dead code and reducing memory waste.

## What It Detects

### 1. Unused Local Variables
Variables declared within a method but never referenced:
```java
void example() {
    int unused = 5;  // ⚠️ Flagged: declared but never used
    int used = 10;
    System.out.println(used);  // ✓ OK: variable is used
}
```

### 2. Unused Method Parameters
Parameters defined in method signatures but never used in the method body:
```java
void process(int unusedParam, int usedParam) {  // ⚠️ unusedParam flagged
    System.out.println(usedParam);  // ✓ usedParam is OK
}
```

## Severity Levels
- **🟡 High (0.7+)**: Unused parameters in public methods or variables with initializers
- **🟠 Medium (< 0.7)**: Other unused variables

## Risk Scoring Factors
- **+0.2**: Variable is a method parameter
- **+0.1**: Variable is in a public method
- **+0.1**: Variable has an initializer (wasted computation)

## Integration

### 1. Detector Class
Location: `src/main/java/com/devsync/detectors/UnusedVariableDetector.java`

### 2. Test Class
Location: `src/test/java/com/devsync/detectors/UnusedVariableDetectorTest.java`
- All 4 tests passing ✓

### 3. Analysis Engine Integration
The detector is automatically registered in `CodeAnalysisEngine.java`:
```java
detectors.put("UnusedVariableDetector", new UnusedVariableDetector());
```

### 4. User Settings
Added to `UserSettings.java` model:
- Field: `unusedVariableEnabled` (Boolean, default: true)
- Database column: `unused_variable_enabled`

Users can enable/disable this detector through the settings interface.

## Output Format
```
🟡 [UnusedVariable] FileName.java:15 - Variable 'unused' declared but never used in method calculate - Variable initialized but never used | Suggestions: Remove unused variable declaration to improve code clarity | DetailedReason: This variable is flagged because: variable 'unused' is declared, it has an initializer value that is computed but never used, it is never read or referenced anywhere in its scope. Unused variables waste memory, reduce code readability, and may indicate incomplete implementation or refactoring artifacts.
```

## Benefits
1. **Code Clarity**: Removes confusing unused declarations
2. **Memory Efficiency**: Eliminates unnecessary variable allocations
3. **Maintenance**: Identifies incomplete refactoring or dead code
4. **Best Practices**: Encourages clean, minimal code

## Technical Implementation
- Uses JavaParser AST traversal
- Tracks variable declarations vs. usages per method scope
- Excludes variable names in declarations from usage count
- Handles both local variables and method parameters
- Provides contextual analysis (public methods, initializers, etc.)

## Testing
All test cases pass:
- ✓ Detects unused local variables
- ✓ Detects unused parameters
- ✓ Does not report used variables
- ✓ Handles multiple variables correctly

## Configuration
Enable/disable via user settings:
```java
settings.setUnusedVariableEnabled(true/false);
```

Default: **Enabled**
