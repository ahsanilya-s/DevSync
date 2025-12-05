# 🎯 DETECTOR THRESHOLDS REFERENCE

## Quick Reference for All Code Smell Thresholds

---

## 📏 **CONFIGURABLE THRESHOLDS** (via UserSettings)

### **1. LongMethodDetector**
- **baseLineThreshold**: `35` statements
- **criticalLineThreshold**: `50` statements
- **MAX_CYCLOMATIC_COMPLEXITY**: `10`
- **MAX_COGNITIVE_COMPLEXITY**: `15`
- **MAX_NESTING_DEPTH**: `4`

**Detection**: ANY of these thresholds exceeded = smell detected

---

### **2. LongParameterListDetector**
- **baseParameterThreshold**: `4` parameters
- **criticalParameterThreshold**: `7` parameters
- **constructorThreshold**: `5` parameters

**Detection**: `paramCount >= threshold`

---

### **3. LongIdentifierDetector**
- **variableThreshold**: `20` characters
- **methodThreshold**: `30` characters
- **classThreshold**: `35` characters

**Detection**: `identifierLength >= threshold` (based on type)

---

## 🔒 **FIXED THRESHOLDS** (hardcoded)

### **4. MagicNumberDetector**
- **ACCEPTABLE_NUMBERS**: `0, 1, -1, 0.0, 1.0, -1.0, 2, 100, 1000`
- **Exclusions**: Test methods, constants (final static)

**Detection**: NOT in acceptable list AND NOT in test/constant

---

### **5. EmptyCatchDetector**
- **Threshold**: NONE (always detects empty catch blocks)

**Detection**: Catch block with 0 statements = smell

---

### **6. MissingDefaultDetector**
- **Threshold**: NONE (always detects missing default)

**Detection**: Switch without default case = smell

---

### **7. ComplexConditionalDetector**
- **BASE_COMPLEXITY_THRESHOLD**: `4` logical operators
- **CRITICAL_COMPLEXITY_THRESHOLD**: `8` logical operators
- **MAX_NESTING_DEPTH**: `3` levels

**Detection**: `operatorCount >= 4` OR `nestingDepth > 3`

---

### **8. BrokenModularizationDetector**
- **responsibilityThreshold**: `3` (exceeding = smell)
- **cohesionThreshold**: `0.4` (below = smell)
- **couplingThreshold**: `6` (exceeding = smell)

**Detection**: `responsibilities > 3` OR `cohesion < 0.4` OR `coupling > 6`

---

### **9. DeficientEncapsulationDetector**
- **Threshold**: `isPublic == true`

**Detection**: Public field = smell (always)

---

### **10. LongStatementDetector**
- **BASE_TOKEN_THRESHOLD**: `20` tokens
- **CRITICAL_TOKEN_THRESHOLD**: `30` tokens
- **BASE_CHAR_THRESHOLD**: `150` characters
- **CRITICAL_CHAR_THRESHOLD**: `250` characters

**Detection**: `tokenCount >= 20` AND `charLength >= 150`

---

### **11. UnnecessaryAbstractionDetector**
- **usageThreshold**: `1` (≤1 usage = smell)
- **implementationThreshold**: `1` (only 1 implementation = smell)

**Detection**: `hasOnlyOneImplementation == true` AND `usageCount <= 1`

---

## 🎨 **SEVERITY MAPPING**

All detectors use score to determine severity AFTER detection:

| Score Range | Emoji | Severity | Description |
|-------------|-------|----------|-------------|
| `>= 0.8` | 🔴 | Critical | Immediate attention required |
| `>= 0.5` | 🟡 | High | Should be addressed soon |
| `< 0.5` | 🟠 | Medium | Consider improving |

---

## 🔧 **HOW TO ADJUST THRESHOLDS**

### **Option 1: User Settings (Database)**
Update `user_settings` table:
```sql
UPDATE user_settings 
SET max_method_length = 40,
    max_parameter_count = 6,
    max_identifier_length = 35
WHERE user_id = 'your_user_id';
```

### **Option 2: Default Config (Code)**
Edit `AnalysisConfig.java`:
```java
public static final int DEFAULT_MAX_METHOD_LENGTH = 50;
public static final int DEFAULT_MAX_PARAMETER_COUNT = 5;
public static final int DEFAULT_MAX_IDENTIFIER_LENGTH = 30;
```

### **Option 3: Detector Directly (Code)**
Edit detector class:
```java
private int baseLineThreshold = 35; // Change this
```

---

## 📊 **THRESHOLD TESTING MATRIX**

| Your Test Case | Threshold | Value in Test | Will Detect? |
|----------------|-----------|---------------|--------------|
| **LongMethodExample** | 35 statements | 54 statements | ✅ YES |
| **LongParameterListExample** | 4 params | 7 params | ✅ YES |
| **LongIdentifierExample** (method) | 30 chars | 63 chars | ✅ YES |
| **LongIdentifierExample** (variable) | 20 chars | 53 chars | ✅ YES |
| **MagicNumberExample** | Not in [0,1,-1,2,100,1000] | 42,17,9,123 | ✅ YES |
| **EmptyCatchExample** | 0 statements | 0 statements | ✅ YES |
| **MissingDefaultExample** | No default | No default | ✅ YES |
| **ComplexConditionalExample** | 4 operators | 6 operators | ✅ YES |
| **BrokenModularizationExample** | 3 responsibilities | 4 responsibilities | ✅ YES |
| **DeficientEncapsulationExample** | Public field | Public field | ✅ YES |
| **LongStatementExample** | 20 tokens + 150 chars | ~30 tokens + ~200 chars | ✅ YES |
| **UnnecessaryAbstractionExample** | 1 impl + ≤1 usage | 1 impl + 1 usage | ✅ YES |

---

## 🚨 **COMMON THRESHOLD ISSUES**

### **Issue 1: Threshold Too High**
**Symptom**: No smells detected when they should be
**Solution**: Lower the threshold in UserSettings or AnalysisConfig

### **Issue 2: Too Many False Positives**
**Symptom**: Everything flagged as a smell
**Solution**: Raise the threshold slightly

### **Issue 3: Inconsistent Detection**
**Symptom**: Same code detected sometimes, not others
**Solution**: Check if user settings override defaults

---

## 💡 **BEST PRACTICES**

1. **Start Conservative**: Use higher thresholds initially
2. **Adjust Gradually**: Lower thresholds based on team feedback
3. **Team Consensus**: Agree on thresholds as a team
4. **Document Changes**: Track threshold adjustments
5. **Test Regularly**: Verify thresholds with known test cases

---

## 📝 **THRESHOLD TUNING GUIDE**

### **For Strict Analysis** (More Smells)
```java
max_method_length = 30        // Lower = stricter
max_parameter_count = 3       // Lower = stricter
max_identifier_length = 25    // Lower = stricter
```

### **For Lenient Analysis** (Fewer Smells)
```java
max_method_length = 60        // Higher = lenient
max_parameter_count = 7       // Higher = lenient
max_identifier_length = 40    // Higher = lenient
```

### **For Balanced Analysis** (Recommended)
```java
max_method_length = 50        // Default
max_parameter_count = 5       // Default
max_identifier_length = 30    // Default
```

---

**All thresholds are now clearly defined and consistently applied!** 🎯
