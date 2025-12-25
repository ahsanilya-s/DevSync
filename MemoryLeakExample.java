import java.io.*;
import java.sql.*;
import java.util.*;

public class MemoryLeakExample {
    
    // Static collection that grows unbounded
    private static Map<String, Object> cache = new HashMap<>();
    
    // Example 1: Unclosed resource (FileInputStream)
    public void readFileWithoutClosing(String filename) {
        try {
            FileInputStream fis = new FileInputStream(filename);
            int data = fis.read();
            // Missing fis.close() - Memory leak!
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    // Example 2: Unclosed database connection
    public void queryDatabaseWithoutClosing() {
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/test", "user", "pass");
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM users");
            // Missing rs.close(), stmt.close(), conn.close() - Memory leak!
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    // Example 3: Static collection without cleanup
    public void addToCache(String key, Object value) {
        cache.put(key, value);
        // No clear() or remove() - Static collection grows indefinitely!
    }
    
    // Example 4: Listener not removed
    public void registerListener(EventListener listener) {
        EventManager.getInstance().addEventListener(listener);
        // Missing removeEventListener() - Listener leak!
    }
    
    // Example 5: Thread without shutdown
    public void startBackgroundTask() {
        Thread thread = new Thread(() -> {
            while (true) {
                // Do work
            }
        });
        thread.start();
        // Missing thread.interrupt() or shutdown mechanism - Thread leak!
    }
    
    // GOOD EXAMPLE: Using try-with-resources
    public void readFileCorrectly(String filename) {
        try (FileInputStream fis = new FileInputStream(filename)) {
            int data = fis.read();
            // fis.close() called automatically - No leak!
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class EventListener {}
class EventManager {
    private static EventManager instance = new EventManager();
    public static EventManager getInstance() { return instance; }
    public void addEventListener(EventListener listener) {}
    public void removeEventListener(EventListener listener) {}
}
