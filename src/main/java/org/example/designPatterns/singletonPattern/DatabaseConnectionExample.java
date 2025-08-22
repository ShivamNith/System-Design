package org.example.designPatterns.singletonPattern;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Database Connection Pool implementation using Singleton Pattern
 * 
 * This example demonstrates how to use the Singleton pattern to manage
 * a pool of database connections, ensuring that only one connection pool
 * exists throughout the application lifecycle.
 * 
 * Features:
 * - Thread-safe singleton implementation using Bill Pugh approach
 * - Connection pooling for efficient resource management
 * - Proper connection lifecycle management
 * - Configurable pool size
 */
public class DatabaseConnectionExample {
    
    /**
     * DatabaseConnectionPool - Singleton class managing database connections
     */
    public static class DatabaseConnectionPool {
        
        // Database configuration
        private static final String DB_URL = "jdbc:mysql://localhost:3306/testdb";
        private static final String DB_USERNAME = "admin";
        private static final String DB_PASSWORD = "password";
        private static final int INITIAL_POOL_SIZE = 10;
        private static final int MAX_POOL_SIZE = 20;
        
        // Connection pool using BlockingQueue for thread safety
        private final BlockingQueue<Connection> connectionPool;
        private final List<Connection> allConnections;
        private boolean isPoolInitialized = false;
        
        /**
         * Private constructor to prevent external instantiation
         */
        private DatabaseConnectionPool() {
            connectionPool = new LinkedBlockingQueue<>(MAX_POOL_SIZE);
            allConnections = new ArrayList<>(MAX_POOL_SIZE);
            initializePool();
        }
        
        /**
         * Bill Pugh Singleton implementation
         * Thread-safe, lazy loading without synchronization overhead
         */
        private static class SingletonHolder {
            private static final DatabaseConnectionPool INSTANCE = new DatabaseConnectionPool();
        }
        
        /**
         * Get the singleton instance of DatabaseConnectionPool
         * 
         * @return the singleton instance
         */
        public static DatabaseConnectionPool getInstance() {
            return SingletonHolder.INSTANCE;
        }
        
        /**
         * Initialize the connection pool with initial connections
         */
        private void initializePool() {
            try {
                for (int i = 0; i < INITIAL_POOL_SIZE; i++) {
                    Connection connection = createConnection();
                    connectionPool.offer(connection);
                    allConnections.add(connection);
                }
                isPoolInitialized = true;
                System.out.println("Database connection pool initialized with " + 
                                 INITIAL_POOL_SIZE + " connections");
            } catch (SQLException e) {
                System.err.println("Failed to initialize connection pool: " + e.getMessage());
                throw new RuntimeException("Unable to initialize database connection pool", e);
            }
        }
        
        /**
         * Create a new database connection
         * 
         * @return new database connection
         * @throws SQLException if connection creation fails
         */
        private Connection createConnection() throws SQLException {
            try {
                // Load the MySQL JDBC driver (for demonstration purposes)
                Class.forName("com.mysql.cj.jdbc.Driver");
                return DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            } catch (ClassNotFoundException e) {
                throw new SQLException("MySQL JDBC Driver not found", e);
            }
        }
        
        /**
         * Get a connection from the pool
         * This method blocks if no connections are available
         * 
         * @return database connection from the pool
         * @throws InterruptedException if thread is interrupted while waiting
         */
        public Connection getConnection() throws InterruptedException {
            if (!isPoolInitialized) {
                throw new IllegalStateException("Connection pool is not initialized");
            }
            
            Connection connection = connectionPool.take(); // Blocks if no connections available
            System.out.println("Connection retrieved from pool. Available connections: " + 
                             connectionPool.size());
            return connection;
        }
        
        /**
         * Return a connection to the pool
         * 
         * @param connection the connection to return
         */
        public void returnConnection(Connection connection) {
            if (connection != null && allConnections.contains(connection)) {
                try {
                    if (!connection.isClosed()) {
                        connectionPool.offer(connection);
                        System.out.println("Connection returned to pool. Available connections: " + 
                                         connectionPool.size());
                    } else {
                        // Connection is closed, remove from tracking and create a new one
                        allConnections.remove(connection);
                        try {
                            Connection newConnection = createConnection();
                            connectionPool.offer(newConnection);
                            allConnections.add(newConnection);
                            System.out.println("Replaced closed connection with new one");
                        } catch (SQLException e) {
                            System.err.println("Failed to create replacement connection: " + e.getMessage());
                        }
                    }
                } catch (SQLException e) {
                    System.err.println("Error checking connection status: " + e.getMessage());
                }
            }
        }
        
        /**
         * Get current pool status
         * 
         * @return pool status information
         */
        public PoolStatus getPoolStatus() {
            return new PoolStatus(
                connectionPool.size(),
                allConnections.size(),
                MAX_POOL_SIZE,
                isPoolInitialized
            );
        }
        
        /**
         * Close all connections and shutdown the pool
         * Should be called during application shutdown
         */
        public void shutdown() {
            System.out.println("Shutting down database connection pool...");
            
            // Close all connections
            for (Connection connection : allConnections) {
                try {
                    if (connection != null && !connection.isClosed()) {
                        connection.close();
                    }
                } catch (SQLException e) {
                    System.err.println("Error closing connection: " + e.getMessage());
                }
            }
            
            connectionPool.clear();
            allConnections.clear();
            isPoolInitialized = false;
            
            System.out.println("Database connection pool shutdown complete");
        }
        
        /**
         * Prevent cloning to maintain singleton property
         */
        @Override
        protected Object clone() throws CloneNotSupportedException {
            throw new CloneNotSupportedException("Singleton cannot be cloned");
        }
    }
    
    /**
     * Pool status information class
     */
    public static class PoolStatus {
        private final int availableConnections;
        private final int totalConnections;
        private final int maxPoolSize;
        private final boolean isInitialized;
        
        public PoolStatus(int availableConnections, int totalConnections, 
                         int maxPoolSize, boolean isInitialized) {
            this.availableConnections = availableConnections;
            this.totalConnections = totalConnections;
            this.maxPoolSize = maxPoolSize;
            this.isInitialized = isInitialized;
        }
        
        @Override
        public String toString() {
            return String.format(
                "PoolStatus{available=%d, total=%d, max=%d, initialized=%s}",
                availableConnections, totalConnections, maxPoolSize, isInitialized
            );
        }
        
        // Getters
        public int getAvailableConnections() { return availableConnections; }
        public int getTotalConnections() { return totalConnections; }
        public int getMaxPoolSize() { return maxPoolSize; }
        public boolean isInitialized() { return isInitialized; }
    }
    
    /**
     * Database operations class demonstrating usage of the connection pool
     */
    public static class DatabaseOperations {
        private final DatabaseConnectionPool pool;
        
        public DatabaseOperations() {
            this.pool = DatabaseConnectionPool.getInstance();
        }
        
        /**
         * Execute a sample database query
         * Demonstrates proper connection usage pattern
         */
        public void executeQuery(String sql) {
            Connection connection = null;
            try {
                // Get connection from pool
                connection = pool.getConnection();
                
                // Simulate database operation
                System.out.println("Executing query: " + sql);
                Thread.sleep(100); // Simulate query execution time
                
                System.out.println("Query executed successfully");
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Thread interrupted while getting connection");
            } catch (Exception e) {
                System.err.println("Error executing query: " + e.getMessage());
            } finally {
                // Always return connection to pool
                if (connection != null) {
                    pool.returnConnection(connection);
                }
            }
        }
        
        /**
         * Execute multiple queries concurrently
         * Demonstrates thread safety of the singleton pool
         */
        public void executeConcurrentQueries() {
            List<Thread> threads = new ArrayList<>();
            
            for (int i = 0; i < 5; i++) {
                final int queryId = i;
                Thread thread = new Thread(() -> {
                    executeQuery("SELECT * FROM users WHERE id = " + queryId);
                });
                threads.add(thread);
                thread.start();
            }
            
            // Wait for all threads to complete
            for (Thread thread : threads) {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
    
    /**
     * Demonstration of the Database Connection Pool Singleton
     */
    public static void main(String[] args) {
        System.out.println("=== Database Connection Pool Singleton Demo ===\n");
        
        // Get singleton instance
        DatabaseConnectionPool pool = DatabaseConnectionPool.getInstance();
        
        // Verify singleton property
        DatabaseConnectionPool pool2 = DatabaseConnectionPool.getInstance();
        System.out.println("Singleton verification: " + (pool == pool2));
        System.out.println("Pool status: " + pool.getPoolStatus() + "\n");
        
        // Create database operations instance
        DatabaseOperations dbOps = new DatabaseOperations();
        
        // Execute single query
        System.out.println("--- Single Query Execution ---");
        dbOps.executeQuery("SELECT COUNT(*) FROM products");
        System.out.println();
        
        // Execute concurrent queries
        System.out.println("--- Concurrent Query Execution ---");
        dbOps.executeConcurrentQueries();
        System.out.println();
        
        // Check pool status
        System.out.println("Final pool status: " + pool.getPoolStatus());
        
        // Cleanup
        pool.shutdown();
        
        System.out.println("\n=== Demo Complete ===");
    }
}