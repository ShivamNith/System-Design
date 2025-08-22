package org.example.designPatterns.proxyPattern;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Caching Proxy Example demonstrating database query caching
 * 
 * This example shows how a proxy can cache expensive database
 * operations to improve performance.
 */

// Data transfer objects
class User {
    private int id;
    private String username;
    private String email;
    private String department;
    
    public User(int id, String username, String email, String department) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.department = department;
    }
    
    // Getters
    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getDepartment() { return department; }
    
    @Override
    public String toString() {
        return String.format("User{id=%d, username='%s', email='%s', department='%s'}", 
                           id, username, email, department);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        User user = (User) obj;
        return id == user.id;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

// Subject interface
interface UserDatabase {
    User getUserById(int id);
    List<User> getUsersByDepartment(String department);
    List<User> getAllUsers();
    void addUser(User user);
    void updateUser(User user);
    void deleteUser(int id);
    List<User> searchUsers(String query);
    int getTotalUserCount();
}

// RealSubject - actual database implementation
class RealUserDatabase implements UserDatabase {
    private Map<Integer, User> database;
    private int nextId;
    
    public RealUserDatabase() {
        this.database = new HashMap<>();
        this.nextId = 1;
        initializeData();
    }
    
    private void initializeData() {
        // Initialize with some sample data
        addUser(new User(0, "alice", "alice@company.com", "Engineering"));
        addUser(new User(0, "bob", "bob@company.com", "Marketing"));
        addUser(new User(0, "charlie", "charlie@company.com", "Engineering"));
        addUser(new User(0, "diana", "diana@company.com", "HR"));
        addUser(new User(0, "eve", "eve@company.com", "Finance"));
        addUser(new User(0, "frank", "frank@company.com", "Engineering"));
    }
    
    @Override
    public User getUserById(int id) {
        simulateDbDelay("SELECT * FROM users WHERE id = " + id, 200);
        return database.get(id);
    }
    
    @Override
    public List<User> getUsersByDepartment(String department) {
        simulateDbDelay("SELECT * FROM users WHERE department = '" + department + "'", 300);
        return database.values().stream()
                .filter(user -> user.getDepartment().equals(department))
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }
    
    @Override
    public List<User> getAllUsers() {
        simulateDbDelay("SELECT * FROM users", 500);
        return new ArrayList<>(database.values());
    }
    
    @Override
    public void addUser(User user) {
        simulateDbDelay("INSERT INTO users", 250);
        User newUser = new User(nextId++, user.getUsername(), user.getEmail(), user.getDepartment());
        database.put(newUser.getId(), newUser);
        System.out.println("Database: User added - " + newUser);
    }
    
    @Override
    public void updateUser(User user) {
        simulateDbDelay("UPDATE users WHERE id = " + user.getId(), 200);
        if (database.containsKey(user.getId())) {
            database.put(user.getId(), user);
            System.out.println("Database: User updated - " + user);
        }
    }
    
    @Override
    public void deleteUser(int id) {
        simulateDbDelay("DELETE FROM users WHERE id = " + id, 150);
        User removed = database.remove(id);
        if (removed != null) {
            System.out.println("Database: User deleted - " + removed);
        }
    }
    
    @Override
    public List<User> searchUsers(String query) {
        simulateDbDelay("SELECT * FROM users WHERE username LIKE '%" + query + "%'", 400);
        return database.values().stream()
                .filter(user -> user.getUsername().toLowerCase().contains(query.toLowerCase()) ||
                              user.getEmail().toLowerCase().contains(query.toLowerCase()))
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }
    
    @Override
    public int getTotalUserCount() {
        simulateDbDelay("SELECT COUNT(*) FROM users", 100);
        return database.size();
    }
    
    private void simulateDbDelay(String query, int delayMs) {
        System.out.println("Database: Executing query - " + query);
        try {
            Thread.sleep(delayMs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

// Cache entry with expiration time
class CacheEntry<T> {
    private T data;
    private long timestamp;
    private long ttl; // time to live in milliseconds
    
    public CacheEntry(T data, long ttl) {
        this.data = data;
        this.timestamp = System.currentTimeMillis();
        this.ttl = ttl;
    }
    
    public boolean isExpired() {
        return System.currentTimeMillis() - timestamp > ttl;
    }
    
    public T getData() {
        return data;
    }
    
    public long getAge() {
        return System.currentTimeMillis() - timestamp;
    }
}

// Caching Proxy
class CachingUserDatabaseProxy implements UserDatabase {
    private RealUserDatabase realDatabase;
    private Map<String, CacheEntry<?>> cache;
    private long defaultTtl = 30000; // 30 seconds default TTL
    private Map<String, Integer> cacheHits;
    private Map<String, Integer> cacheMisses;
    
    public CachingUserDatabaseProxy() {
        this.realDatabase = new RealUserDatabase();
        this.cache = new ConcurrentHashMap<>();
        this.cacheHits = new ConcurrentHashMap<>();
        this.cacheMisses = new ConcurrentHashMap<>();
    }
    
    @Override
    public User getUserById(int id) {
        String cacheKey = "user_by_id_" + id;
        return getFromCacheOrDatabase(cacheKey, () -> realDatabase.getUserById(id));
    }
    
    @Override
    public List<User> getUsersByDepartment(String department) {
        String cacheKey = "users_by_dept_" + department;
        return getFromCacheOrDatabase(cacheKey, () -> realDatabase.getUsersByDepartment(department));
    }
    
    @Override
    public List<User> getAllUsers() {
        String cacheKey = "all_users";
        return getFromCacheOrDatabase(cacheKey, () -> realDatabase.getAllUsers());
    }
    
    @Override
    public void addUser(User user) {
        realDatabase.addUser(user);
        invalidateRelatedCaches();
        System.out.println("Cache: Invalidated related caches after user addition");
    }
    
    @Override
    public void updateUser(User user) {
        realDatabase.updateUser(user);
        invalidateRelatedCaches();
        cache.remove("user_by_id_" + user.getId());
        System.out.println("Cache: Invalidated related caches after user update");
    }
    
    @Override
    public void deleteUser(int id) {
        realDatabase.deleteUser(id);
        invalidateRelatedCaches();
        cache.remove("user_by_id_" + id);
        System.out.println("Cache: Invalidated related caches after user deletion");
    }
    
    @Override
    public List<User> searchUsers(String query) {
        String cacheKey = "search_" + query.toLowerCase();
        return getFromCacheOrDatabase(cacheKey, () -> realDatabase.searchUsers(query));
    }
    
    @Override
    public int getTotalUserCount() {
        String cacheKey = "total_count";
        return getFromCacheOrDatabase(cacheKey, () -> realDatabase.getTotalUserCount());
    }
    
    @SuppressWarnings("unchecked")
    private <T> T getFromCacheOrDatabase(String cacheKey, DatabaseOperation<T> operation) {
        // Check cache first
        CacheEntry<T> cacheEntry = (CacheEntry<T>) cache.get(cacheKey);
        
        if (cacheEntry != null && !cacheEntry.isExpired()) {
            // Cache hit
            incrementCounter(cacheHits, cacheKey);
            System.out.println("Cache: HIT for key '" + cacheKey + "' (age: " + cacheEntry.getAge() + "ms)");
            return cacheEntry.getData();
        } else {
            // Cache miss or expired
            incrementCounter(cacheMisses, cacheKey);
            if (cacheEntry != null) {
                System.out.println("Cache: EXPIRED for key '" + cacheKey + "'");
            } else {
                System.out.println("Cache: MISS for key '" + cacheKey + "'");
            }
            
            // Get from database
            T result = operation.execute();
            
            // Store in cache
            cache.put(cacheKey, new CacheEntry<>(result, defaultTtl));
            System.out.println("Cache: Stored result for key '" + cacheKey + "'");
            
            return result;
        }
    }
    
    private void incrementCounter(Map<String, Integer> counter, String key) {
        counter.put(key, counter.getOrDefault(key, 0) + 1);
    }
    
    private void invalidateRelatedCaches() {
        // Remove caches that might be affected by data changes
        cache.entrySet().removeIf(entry -> {
            String key = entry.getKey();
            return key.startsWith("all_users") || 
                   key.startsWith("users_by_dept_") || 
                   key.startsWith("total_count") ||
                   key.startsWith("search_");
        });
    }
    
    // Cache management methods
    public void clearCache() {
        cache.clear();
        System.out.println("Cache: All entries cleared");
    }
    
    public void setDefaultTtl(long ttl) {
        this.defaultTtl = ttl;
        System.out.println("Cache: Default TTL set to " + ttl + "ms");
    }
    
    public void printCacheStatistics() {
        System.out.println("\n=== Cache Statistics ===");
        System.out.println("Cache entries: " + cache.size());
        
        int totalHits = cacheHits.values().stream().mapToInt(Integer::intValue).sum();
        int totalMisses = cacheMisses.values().stream().mapToInt(Integer::intValue).sum();
        double hitRatio = totalHits + totalMisses > 0 ? (double) totalHits / (totalHits + totalMisses) * 100 : 0;
        
        System.out.println("Total hits: " + totalHits);
        System.out.println("Total misses: " + totalMisses);
        System.out.println("Hit ratio: " + String.format("%.1f%%", hitRatio));
        
        System.out.println("\nDetailed statistics:");
        Set<String> allKeys = new HashSet<>(cacheHits.keySet());
        allKeys.addAll(cacheMisses.keySet());
        
        for (String key : allKeys) {
            int hits = cacheHits.getOrDefault(key, 0);
            int misses = cacheMisses.getOrDefault(key, 0);
            System.out.println("  " + key + ": " + hits + " hits, " + misses + " misses");
        }
    }
    
    public void printCacheContents() {
        System.out.println("\n=== Cache Contents ===");
        if (cache.isEmpty()) {
            System.out.println("Cache is empty");
        } else {
            for (Map.Entry<String, CacheEntry<?>> entry : cache.entrySet()) {
                CacheEntry<?> cacheEntry = entry.getValue();
                System.out.println(entry.getKey() + ": " + 
                                 (cacheEntry.isExpired() ? "EXPIRED" : "VALID") +
                                 " (age: " + cacheEntry.getAge() + "ms)");
            }
        }
    }
}

// Functional interface for database operations
@FunctionalInterface
interface DatabaseOperation<T> {
    T execute();
}

// Client code
public class CachingDatabaseProxyExample {
    public static void main(String[] args) {
        System.out.println("=== Caching Proxy Pattern: Database Query Caching ===\n");
        
        CachingUserDatabaseProxy userDb = new CachingUserDatabaseProxy();
        userDb.setDefaultTtl(5000); // 5 seconds for demo purposes
        
        // Performance comparison
        System.out.println("=== Performance Comparison ===");
        
        // First query - cache miss
        long startTime = System.currentTimeMillis();
        List<User> engineers = userDb.getUsersByDepartment("Engineering");
        long firstQueryTime = System.currentTimeMillis() - startTime;
        System.out.println("First query result: " + engineers.size() + " engineers found");
        System.out.println("First query time: " + firstQueryTime + "ms");
        
        // Second query - cache hit
        startTime = System.currentTimeMillis();
        engineers = userDb.getUsersByDepartment("Engineering");
        long secondQueryTime = System.currentTimeMillis() - startTime;
        System.out.println("Second query result: " + engineers.size() + " engineers found");
        System.out.println("Second query time: " + secondQueryTime + "ms");
        System.out.println("Performance improvement: " + (firstQueryTime - secondQueryTime) + "ms saved");
        
        // Multiple queries to show caching benefits
        System.out.println("\n=== Multiple Query Testing ===");
        
        // Query different data
        User user1 = userDb.getUserById(1);
        System.out.println("Retrieved user: " + user1);
        
        List<User> allUsers = userDb.getAllUsers();
        System.out.println("All users count: " + allUsers.size());
        
        // Repeat queries (should be cached)
        System.out.println("\n--- Repeating Queries (Should be Cached) ---");
        userDb.getUserById(1);
        userDb.getUsersByDepartment("Engineering");
        userDb.getAllUsers();
        
        // Show cache statistics
        userDb.printCacheStatistics();
        userDb.printCacheContents();
        
        // Test cache invalidation
        System.out.println("\n=== Cache Invalidation Testing ===");
        User newUser = new User(0, "grace", "grace@company.com", "Engineering");
        userDb.addUser(newUser);
        
        // Query again (should miss cache due to invalidation)
        System.out.println("\n--- Querying After Data Modification ---");
        engineers = userDb.getUsersByDepartment("Engineering");
        System.out.println("Engineers after addition: " + engineers.size());
        
        userDb.printCacheStatistics();
        
        // Test cache expiration
        System.out.println("\n=== Cache Expiration Testing ===");
        userDb.setDefaultTtl(2000); // 2 seconds
        
        List<User> hrUsers = userDb.getUsersByDepartment("HR");
        System.out.println("HR users: " + hrUsers.size());
        
        System.out.println("Waiting for cache to expire...");
        try {
            Thread.sleep(3000); // Wait 3 seconds
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // This should miss cache due to expiration
        hrUsers = userDb.getUsersByDepartment("HR");
        System.out.println("HR users after expiration: " + hrUsers.size());
        
        // Search functionality with caching
        System.out.println("\n=== Search Caching ===");
        List<User> searchResults = userDb.searchUsers("a");
        System.out.println("Search results for 'a': " + searchResults.size() + " users");
        
        // Repeat search (should be cached)
        searchResults = userDb.searchUsers("a");
        System.out.println("Search results for 'a' (cached): " + searchResults.size() + " users");
        
        // Final statistics
        userDb.printCacheStatistics();
        
        System.out.println("\n=== Caching Proxy Benefits ===");
        System.out.println("1. Performance - Significant speed improvement for repeated queries");
        System.out.println("2. Reduced Database Load - Fewer actual database queries");
        System.out.println("3. Automatic Cache Management - TTL-based expiration");
        System.out.println("4. Smart Invalidation - Cache cleared when data changes");
        System.out.println("5. Transparent Operation - Client code unchanged");
        System.out.println("6. Statistics Tracking - Monitor cache effectiveness");
        System.out.println("7. Configurable TTL - Adjust cache lifetime as needed");
    }
}