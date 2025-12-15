# Memory Leak Detector - Quick Reference Guide

## 🚀 Quick Start

### Enable/Disable Detection
1. Open DevSync Dashboard
2. Click **Settings** in sidebar
3. Scroll to **Simple Detectors**
4. Toggle **🔴 Memory Leak Detection**
5. Click **Save Settings**

### Run Analysis
1. Click **New Analysis** or **Start Analysis**
2. Upload your Java project (ZIP file)
3. Wait for analysis to complete
4. View memory leak issues in report

---

## 🔍 What It Detects

### 1. Unclosed Resources (🔴 Critical)
**Problem:**
```java
public void readFile(String path) {
    FileInputStream fis = new FileInputStream(path);
    int data = fis.read();
    // fis never closed - MEMORY LEAK!
}
```

**Solution:**
```java
public void readFile(String path) {
    try (FileInputStream fis = new FileInputStream(path)) {
        int data = fis.read();
    } // Automatically closed
}
```

**Detected Types:**
- FileInputStream/OutputStream
- BufferedReader/Writer
- Connection (JDBC)
- Socket
- Scanner
- ResultSet
- Statement

---

### 2. Static Collections (🟡 High)
**Problem:**
```java
private static Map<String, User> userCache = new HashMap<>();

public void addUser(User user) {
    userCache.put(user.getId(), user);
    // Cache grows forever - MEMORY LEAK!
}
```

**Solution:**
```java
private static Map<String, User> userCache = new HashMap<>();
private static final int MAX_SIZE = 1000;

public void addUser(User user) {
    if (userCache.size() >= MAX_SIZE) {
        userCache.clear(); // or remove oldest
    }
    userCache.put(user.getId(), user);
}
```

**Detected Types:**
- HashMap, TreeMap, LinkedHashMap
- ArrayList, LinkedList
- HashSet, TreeSet
- Any Collection type

---

### 3. Listener Leaks (🟡 High)
**Problem:**
```java
public void setupListener() {
    eventManager.addEventListener(this.listener);
    // Listener never removed - MEMORY LEAK!
}
```

**Solution:**
```java
public void setupListener() {
    eventManager.addEventListener(this.listener);
}

public void cleanup() {
    eventManager.removeEventListener(this.listener);
}
```

**Detected Patterns:**
- addEventListener / removeEventListener
- addListener / removeListener
- register / unregister
- subscribe / unsubscribe

---

### 4. Thread Leaks (🟠 Medium)
**Problem:**
```java
public void startTask() {
    Thread thread = new Thread(() -> {
        while (true) {
            // Do work
        }
    });
    thread.start();
    // Thread never stopped - MEMORY LEAK!
}
```

**Solution:**
```java
private ExecutorService executor = Executors.newFixedThreadPool(10);

public void startTask() {
    executor.submit(() -> {
        // Do work
    });
}

public void shutdown() {
    executor.shutdown();
}
```

**Detected Types:**
- Thread
- ExecutorService
- ThreadPoolExecutor
- ScheduledExecutorService

---

## 📊 Severity Levels

| Severity | Icon | Description | Action Required |
|----------|------|-------------|-----------------|
| Critical | 🔴 | Unclosed resources | Fix immediately |
| High | 🟡 | Static collections, listeners | Fix soon |
| Medium | 🟠 | Thread management | Review and fix |

---

## 🛠️ Common Fixes

### Fix 1: Use Try-With-Resources
```java
// BEFORE
FileReader reader = new FileReader("file.txt");
// ... use reader
reader.close(); // May not execute if exception occurs

// AFTER
try (FileReader reader = new FileReader("file.txt")) {
    // ... use reader
} // Always closed, even if exception occurs
```

### Fix 2: Implement Cleanup Methods
```java
public class MyComponent {
    private EventListener listener;
    
    public void init() {
        listener = new EventListener();
        eventManager.addEventListener(listener);
    }
    
    public void destroy() {
        eventManager.removeEventListener(listener);
        listener = null;
    }
}
```

### Fix 3: Use WeakHashMap for Caches
```java
// BEFORE
private static Map<String, Object> cache = new HashMap<>();

// AFTER
private static Map<String, Object> cache = new WeakHashMap<>();
// Objects can be garbage collected when no longer referenced
```

### Fix 4: Proper Thread Shutdown
```java
public class TaskManager {
    private ExecutorService executor;
    
    public void start() {
        executor = Executors.newFixedThreadPool(10);
    }
    
    public void stop() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }
    }
}
```

---

## 📝 Report Format

```
🔴 [MemoryLeak] UserService.java:42 - Resource 'connection' in method 'getUser' may not be closed
Suggestions: Use try-with-resources or ensure close() is called in finally block
DetailedReason: Unclosed resources like streams, connections, or readers can cause memory leaks 
as they hold references and prevent garbage collection
```

**Components:**
1. **Severity Icon**: 🔴 🟡 🟠
2. **Type**: [MemoryLeak]
3. **Location**: File:Line
4. **Description**: What was detected
5. **Suggestions**: How to fix
6. **DetailedReason**: Why it's a problem

---

## ⚙️ Configuration

### Default Settings
- **Enabled**: Yes
- **Severity Thresholds**: Fixed (Critical/High/Medium)
- **Resource Types**: All common Java resources
- **Collection Types**: All standard collections

### Customization
Currently, the detector uses fixed patterns. Future versions may support:
- Custom resource types
- Whitelist specific patterns
- Adjustable severity levels
- Exclude specific methods

---

## 🧪 Testing Your Code

### Test Checklist
- [ ] All streams use try-with-resources
- [ ] Database connections are closed
- [ ] Static collections have size limits
- [ ] Listeners are removed in cleanup methods
- [ ] Threads/executors have shutdown logic
- [ ] No resources in finally blocks without null checks

### Example Test File
See `MemoryLeakExample.java` for comprehensive examples of:
- ✅ Good practices
- ❌ Bad practices (detected by analyzer)

---

## 🐛 Troubleshooting

### False Positives
**Issue**: Detector flags code that doesn't leak
**Solution**: 
- Review the code path
- Ensure cleanup happens in all scenarios
- Add comments explaining complex resource management

### Missed Leaks
**Issue**: Known leak not detected
**Solution**:
- Check if resource type is supported
- Verify detector is enabled in settings
- Report issue with code sample

### Performance Issues
**Issue**: Analysis takes too long
**Solution**:
- Analyze smaller projects
- Disable other detectors temporarily
- Check system resources

---

## 📚 Best Practices

### 1. Always Use Try-With-Resources
```java
try (Resource r = new Resource()) {
    // Use resource
}
```

### 2. Implement AutoCloseable
```java
public class MyResource implements AutoCloseable {
    @Override
    public void close() {
        // Cleanup logic
    }
}
```

### 3. Use Weak References for Caches
```java
private static Map<String, WeakReference<Object>> cache = new HashMap<>();
```

### 4. Implement Lifecycle Methods
```java
public interface Lifecycle {
    void init();
    void destroy();
}
```

### 5. Use Daemon Threads
```java
Thread thread = new Thread(task);
thread.setDaemon(true); // Won't prevent JVM shutdown
thread.start();
```

---

## 🔗 Related Resources

- [Java Memory Management](https://docs.oracle.com/javase/8/docs/technotes/guides/vm/gctuning/)
- [Try-With-Resources](https://docs.oracle.com/javase/tutorial/essential/exceptions/tryResourceClose.html)
- [WeakReference](https://docs.oracle.com/javase/8/docs/api/java/lang/ref/WeakReference.html)
- [ExecutorService](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ExecutorService.html)

---

## 💡 Tips

1. **Run regularly**: Analyze code after major changes
2. **Fix critical first**: Prioritize 🔴 issues
3. **Review patterns**: Learn from detected issues
4. **Use IDE plugins**: Complement with real-time detection
5. **Educate team**: Share findings and best practices

---

## 📞 Support

- **Documentation**: See `MEMORY_LEAK_DETECTOR_FEATURE.md`
- **Architecture**: See `MEMORY_LEAK_DETECTOR_ARCHITECTURE.md`
- **Issues**: Report via DevSync support
- **Questions**: Contact development team

---

## ✅ Quick Checklist

Before deploying code:
- [ ] All resources use try-with-resources
- [ ] No unclosed connections
- [ ] Static collections have cleanup
- [ ] Listeners are removed
- [ ] Threads have shutdown logic
- [ ] No memory leak warnings in report

---

**Last Updated**: 2025
**Version**: 1.0
**Detector**: MemoryLeakDetector.java
