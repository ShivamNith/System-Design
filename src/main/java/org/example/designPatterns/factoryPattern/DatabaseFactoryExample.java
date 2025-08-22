package org.example.designPatterns.factoryPattern;

import java.util.*;
import java.sql.Timestamp;

interface DatabaseConnection {
    void connect();
    void executeQuery(String query);
    void executeUpdate(String query);
    void beginTransaction();
    void commit();
    void rollback();
    void disconnect();
    String getDatabaseType();
    boolean isConnected();
}

class MySQLConnection implements DatabaseConnection {
    private String host;
    private int port;
    private String database;
    private String username;
    private boolean connected = false;
    private boolean inTransaction = false;
    
    public MySQLConnection(String host, int port, String database, String username) {
        this.host = host;
        this.port = port;
        this.database = database;
        this.username = username;
    }
    
    @Override
    public void connect() {
        System.out.println("Connecting to MySQL database...");
        System.out.println("Host: " + host + ":" + port);
        System.out.println("Database: " + database);
        System.out.println("User: " + username);
        // Simulate connection delay
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        connected = true;
        System.out.println("MySQL connection established successfully!");
    }
    
    @Override
    public void executeQuery(String query) {
        if (!connected) {
            throw new IllegalStateException("Not connected to database");
        }
        System.out.println("[MySQL] Executing query: " + query);
        System.out.println("[MySQL] Query executed, returning ResultSet");
    }
    
    @Override
    public void executeUpdate(String query) {
        if (!connected) {
            throw new IllegalStateException("Not connected to database");
        }
        System.out.println("[MySQL] Executing update: " + query);
        System.out.println("[MySQL] Update executed, rows affected: " + (int)(Math.random() * 10));
    }
    
    @Override
    public void beginTransaction() {
        if (!connected) {
            throw new IllegalStateException("Not connected to database");
        }
        System.out.println("[MySQL] Beginning transaction");
        System.out.println("[MySQL] SET AUTOCOMMIT=0");
        inTransaction = true;
    }
    
    @Override
    public void commit() {
        if (!inTransaction) {
            throw new IllegalStateException("No transaction in progress");
        }
        System.out.println("[MySQL] COMMIT");
        System.out.println("[MySQL] Transaction committed successfully");
        inTransaction = false;
    }
    
    @Override
    public void rollback() {
        if (!inTransaction) {
            throw new IllegalStateException("No transaction in progress");
        }
        System.out.println("[MySQL] ROLLBACK");
        System.out.println("[MySQL] Transaction rolled back");
        inTransaction = false;
    }
    
    @Override
    public void disconnect() {
        if (inTransaction) {
            rollback();
        }
        System.out.println("[MySQL] Closing connection to " + host);
        connected = false;
    }
    
    @Override
    public String getDatabaseType() {
        return "MySQL";
    }
    
    @Override
    public boolean isConnected() {
        return connected;
    }
}

class PostgreSQLConnection implements DatabaseConnection {
    private String host;
    private int port;
    private String database;
    private String schema;
    private boolean connected = false;
    private boolean inTransaction = false;
    
    public PostgreSQLConnection(String host, int port, String database, String schema) {
        this.host = host;
        this.port = port;
        this.database = database;
        this.schema = schema;
    }
    
    @Override
    public void connect() {
        System.out.println("Connecting to PostgreSQL database...");
        System.out.println("Host: " + host + ":" + port);
        System.out.println("Database: " + database);
        System.out.println("Schema: " + schema);
        // Simulate connection delay
        try {
            Thread.sleep(150);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        connected = true;
        System.out.println("PostgreSQL connection established successfully!");
    }
    
    @Override
    public void executeQuery(String query) {
        if (!connected) {
            throw new IllegalStateException("Not connected to database");
        }
        System.out.println("[PostgreSQL] Executing query: " + query);
        System.out.println("[PostgreSQL] Query plan: Seq Scan");
        System.out.println("[PostgreSQL] Query executed, returning ResultSet");
    }
    
    @Override
    public void executeUpdate(String query) {
        if (!connected) {
            throw new IllegalStateException("Not connected to database");
        }
        System.out.println("[PostgreSQL] Executing update: " + query);
        System.out.println("[PostgreSQL] Update executed, rows affected: " + (int)(Math.random() * 10));
    }
    
    @Override
    public void beginTransaction() {
        if (!connected) {
            throw new IllegalStateException("Not connected to database");
        }
        System.out.println("[PostgreSQL] BEGIN TRANSACTION");
        inTransaction = true;
    }
    
    @Override
    public void commit() {
        if (!inTransaction) {
            throw new IllegalStateException("No transaction in progress");
        }
        System.out.println("[PostgreSQL] COMMIT TRANSACTION");
        System.out.println("[PostgreSQL] Transaction committed successfully");
        inTransaction = false;
    }
    
    @Override
    public void rollback() {
        if (!inTransaction) {
            throw new IllegalStateException("No transaction in progress");
        }
        System.out.println("[PostgreSQL] ROLLBACK TRANSACTION");
        System.out.println("[PostgreSQL] Transaction rolled back");
        inTransaction = false;
    }
    
    @Override
    public void disconnect() {
        if (inTransaction) {
            rollback();
        }
        System.out.println("[PostgreSQL] Closing connection to " + host);
        connected = false;
    }
    
    @Override
    public String getDatabaseType() {
        return "PostgreSQL";
    }
    
    @Override
    public boolean isConnected() {
        return connected;
    }
}

class MongoDBConnection implements DatabaseConnection {
    private String connectionString;
    private String database;
    private boolean connected = false;
    private boolean inTransaction = false;
    
    public MongoDBConnection(String connectionString, String database) {
        this.connectionString = connectionString;
        this.database = database;
    }
    
    @Override
    public void connect() {
        System.out.println("Connecting to MongoDB...");
        System.out.println("Connection string: " + connectionString);
        System.out.println("Database: " + database);
        // Simulate connection delay
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        connected = true;
        System.out.println("MongoDB connection established successfully!");
    }
    
    @Override
    public void executeQuery(String query) {
        if (!connected) {
            throw new IllegalStateException("Not connected to database");
        }
        System.out.println("[MongoDB] Executing query: " + query);
        System.out.println("[MongoDB] Using index: _id_");
        System.out.println("[MongoDB] Documents returned: " + (int)(Math.random() * 100));
    }
    
    @Override
    public void executeUpdate(String query) {
        if (!connected) {
            throw new IllegalStateException("Not connected to database");
        }
        System.out.println("[MongoDB] Executing update: " + query);
        System.out.println("[MongoDB] Documents modified: " + (int)(Math.random() * 10));
    }
    
    @Override
    public void beginTransaction() {
        if (!connected) {
            throw new IllegalStateException("Not connected to database");
        }
        System.out.println("[MongoDB] Starting session with transaction");
        inTransaction = true;
    }
    
    @Override
    public void commit() {
        if (!inTransaction) {
            throw new IllegalStateException("No transaction in progress");
        }
        System.out.println("[MongoDB] Committing transaction");
        System.out.println("[MongoDB] Transaction committed successfully");
        inTransaction = false;
    }
    
    @Override
    public void rollback() {
        if (!inTransaction) {
            throw new IllegalStateException("No transaction in progress");
        }
        System.out.println("[MongoDB] Aborting transaction");
        System.out.println("[MongoDB] Transaction aborted");
        inTransaction = false;
    }
    
    @Override
    public void disconnect() {
        if (inTransaction) {
            rollback();
        }
        System.out.println("[MongoDB] Closing connection");
        connected = false;
    }
    
    @Override
    public String getDatabaseType() {
        return "MongoDB";
    }
    
    @Override
    public boolean isConnected() {
        return connected;
    }
}

class RedisConnection implements DatabaseConnection {
    private String host;
    private int port;
    private int database;
    private boolean connected = false;
    
    public RedisConnection(String host, int port, int database) {
        this.host = host;
        this.port = port;
        this.database = database;
    }
    
    @Override
    public void connect() {
        System.out.println("Connecting to Redis...");
        System.out.println("Host: " + host + ":" + port);
        System.out.println("Database: " + database);
        // Simulate connection delay
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        connected = true;
        System.out.println("Redis connection established successfully!");
    }
    
    @Override
    public void executeQuery(String query) {
        if (!connected) {
            throw new IllegalStateException("Not connected to database");
        }
        System.out.println("[Redis] Executing command: " + query);
        System.out.println("[Redis] Response: OK");
    }
    
    @Override
    public void executeUpdate(String query) {
        if (!connected) {
            throw new IllegalStateException("Not connected to database");
        }
        System.out.println("[Redis] Executing command: " + query);
        System.out.println("[Redis] Keys affected: " + (int)(Math.random() * 10));
    }
    
    @Override
    public void beginTransaction() {
        if (!connected) {
            throw new IllegalStateException("Not connected to database");
        }
        System.out.println("[Redis] MULTI - Starting transaction");
    }
    
    @Override
    public void commit() {
        System.out.println("[Redis] EXEC - Executing transaction");
        System.out.println("[Redis] Transaction executed successfully");
    }
    
    @Override
    public void rollback() {
        System.out.println("[Redis] DISCARD - Discarding transaction");
        System.out.println("[Redis] Transaction discarded");
    }
    
    @Override
    public void disconnect() {
        System.out.println("[Redis] Closing connection to " + host);
        connected = false;
    }
    
    @Override
    public String getDatabaseType() {
        return "Redis";
    }
    
    @Override
    public boolean isConnected() {
        return connected;
    }
}

// Database Configuration
class DatabaseConfig {
    String type;
    String host;
    int port;
    String database;
    String username;
    String password;
    Map<String, String> additionalParams;
    
    public DatabaseConfig(String type, String host, int port, String database) {
        this.type = type;
        this.host = host;
        this.port = port;
        this.database = database;
        this.additionalParams = new HashMap<>();
    }
    
    public DatabaseConfig withCredentials(String username, String password) {
        this.username = username;
        this.password = password;
        return this;
    }
    
    public DatabaseConfig withParam(String key, String value) {
        additionalParams.put(key, value);
        return this;
    }
}

// Simple Factory
class DatabaseConnectionFactory {
    private static final Map<String, DatabaseConfig> configs = new HashMap<>();
    
    static {
        // Predefined configurations
        configs.put("mysql-prod", new DatabaseConfig("mysql", "prod.db.com", 3306, "proddb")
            .withCredentials("admin", "secret"));
        configs.put("postgres-dev", new DatabaseConfig("postgresql", "dev.db.com", 5432, "devdb")
            .withCredentials("developer", "dev123")
            .withParam("schema", "public"));
        configs.put("mongo-test", new DatabaseConfig("mongodb", "test.db.com", 27017, "testdb"));
        configs.put("redis-cache", new DatabaseConfig("redis", "cache.db.com", 6379, "0"));
    }
    
    public static DatabaseConnection createConnection(String configName) {
        DatabaseConfig config = configs.get(configName);
        if (config == null) {
            throw new IllegalArgumentException("Unknown configuration: " + configName);
        }
        
        return createConnectionFromConfig(config);
    }
    
    public static DatabaseConnection createConnectionFromConfig(DatabaseConfig config) {
        switch (config.type.toLowerCase()) {
            case "mysql":
                return new MySQLConnection(config.host, config.port, config.database, config.username);
            case "postgresql":
            case "postgres":
                String schema = config.additionalParams.getOrDefault("schema", "public");
                return new PostgreSQLConnection(config.host, config.port, config.database, schema);
            case "mongodb":
            case "mongo":
                String connString = "mongodb://" + config.host + ":" + config.port;
                return new MongoDBConnection(connString, config.database);
            case "redis":
                int dbNum = Integer.parseInt(config.database);
                return new RedisConnection(config.host, config.port, dbNum);
            default:
                throw new UnsupportedOperationException("Database type not supported: " + config.type);
        }
    }
    
    public static DatabaseConnection createConnectionFromUrl(String url) {
        if (url.startsWith("jdbc:mysql://")) {
            return parseMySQLUrl(url);
        } else if (url.startsWith("jdbc:postgresql://")) {
            return parsePostgreSQLUrl(url);
        } else if (url.startsWith("mongodb://")) {
            return parseMongoDBUrl(url);
        } else if (url.startsWith("redis://")) {
            return parseRedisUrl(url);
        } else {
            throw new IllegalArgumentException("Unknown database URL format: " + url);
        }
    }
    
    private static DatabaseConnection parseMySQLUrl(String url) {
        // Format: jdbc:mysql://host:port/database
        String cleanUrl = url.replace("jdbc:mysql://", "");
        String[] parts = cleanUrl.split("/");
        String[] hostPort = parts[0].split(":");
        return new MySQLConnection(hostPort[0], Integer.parseInt(hostPort[1]), parts[1], "user");
    }
    
    private static DatabaseConnection parsePostgreSQLUrl(String url) {
        // Format: jdbc:postgresql://host:port/database
        String cleanUrl = url.replace("jdbc:postgresql://", "");
        String[] parts = cleanUrl.split("/");
        String[] hostPort = parts[0].split(":");
        return new PostgreSQLConnection(hostPort[0], Integer.parseInt(hostPort[1]), parts[1], "public");
    }
    
    private static DatabaseConnection parseMongoDBUrl(String url) {
        // Format: mongodb://host:port/database
        String[] parts = url.split("/");
        String database = parts[parts.length - 1];
        String hostPort = parts[2];
        return new MongoDBConnection("mongodb://" + hostPort, database);
    }
    
    private static DatabaseConnection parseRedisUrl(String url) {
        // Format: redis://host:port/database
        String cleanUrl = url.replace("redis://", "");
        String[] parts = cleanUrl.split("/");
        String[] hostPort = parts[0].split(":");
        int database = parts.length > 1 ? Integer.parseInt(parts[1]) : 0;
        return new RedisConnection(hostPort[0], Integer.parseInt(hostPort[1]), database);
    }
}

// Factory Method Pattern
abstract class DatabaseConnectionProvider {
    protected String environment;
    
    public DatabaseConnectionProvider(String environment) {
        this.environment = environment;
    }
    
    // Factory method
    public abstract DatabaseConnection createConnection();
    
    // Template method
    public void executeWithConnection(String query) {
        System.out.println("\n=== Executing in " + environment + " environment ===");
        
        DatabaseConnection conn = createConnection();
        
        try {
            conn.connect();
            System.out.println("Connected to: " + conn.getDatabaseType());
            
            conn.beginTransaction();
            conn.executeUpdate(query);
            conn.commit();
            
            System.out.println("Query executed successfully!");
        } catch (Exception e) {
            System.out.println("Error occurred: " + e.getMessage());
            if (conn.isConnected()) {
                conn.rollback();
            }
        } finally {
            if (conn.isConnected()) {
                conn.disconnect();
            }
        }
    }
}

class ProductionDatabaseProvider extends DatabaseConnectionProvider {
    public ProductionDatabaseProvider() {
        super("Production");
    }
    
    @Override
    public DatabaseConnection createConnection() {
        // In production, use MySQL with specific settings
        return new MySQLConnection("prod.db.server.com", 3306, "production_db", "prod_user");
    }
}

class DevelopmentDatabaseProvider extends DatabaseConnectionProvider {
    public DevelopmentDatabaseProvider() {
        super("Development");
    }
    
    @Override
    public DatabaseConnection createConnection() {
        // In development, use PostgreSQL
        return new PostgreSQLConnection("localhost", 5432, "dev_db", "public");
    }
}

class TestDatabaseProvider extends DatabaseConnectionProvider {
    public TestDatabaseProvider() {
        super("Test");
    }
    
    @Override
    public DatabaseConnection createConnection() {
        // For testing, use in-memory MongoDB
        return new MongoDBConnection("mongodb://localhost:27017", "test_db");
    }
}

// Connection Pool Manager using Factory
class ConnectionPoolManager {
    private Map<String, List<DatabaseConnection>> pools;
    private Map<String, DatabaseConfig> poolConfigs;
    private Map<String, Integer> poolSizes;
    
    public ConnectionPoolManager() {
        this.pools = new HashMap<>();
        this.poolConfigs = new HashMap<>();
        this.poolSizes = new HashMap<>();
    }
    
    public void createPool(String poolName, DatabaseConfig config, int size) {
        poolConfigs.put(poolName, config);
        poolSizes.put(poolName, size);
        
        List<DatabaseConnection> connections = new ArrayList<>();
        System.out.println("Creating connection pool: " + poolName + " (size: " + size + ")");
        
        for (int i = 0; i < size; i++) {
            DatabaseConnection conn = DatabaseConnectionFactory.createConnectionFromConfig(config);
            connections.add(conn);
        }
        
        pools.put(poolName, connections);
        System.out.println("Pool " + poolName + " created with " + size + " connections");
    }
    
    public DatabaseConnection getConnection(String poolName) {
        List<DatabaseConnection> pool = pools.get(poolName);
        if (pool == null || pool.isEmpty()) {
            throw new IllegalStateException("No available connections in pool: " + poolName);
        }
        
        DatabaseConnection conn = pool.remove(0);
        if (!conn.isConnected()) {
            conn.connect();
        }
        
        System.out.println("Connection acquired from pool: " + poolName + 
            " (remaining: " + pool.size() + ")");
        return conn;
    }
    
    public void returnConnection(String poolName, DatabaseConnection conn) {
        List<DatabaseConnection> pool = pools.get(poolName);
        if (pool == null) {
            throw new IllegalStateException("Pool does not exist: " + poolName);
        }
        
        pool.add(conn);
        System.out.println("Connection returned to pool: " + poolName + 
            " (available: " + pool.size() + ")");
    }
    
    public void closePool(String poolName) {
        List<DatabaseConnection> pool = pools.get(poolName);
        if (pool != null) {
            for (DatabaseConnection conn : pool) {
                if (conn.isConnected()) {
                    conn.disconnect();
                }
            }
            pools.remove(poolName);
            System.out.println("Pool " + poolName + " closed");
        }
    }
}

public class DatabaseFactoryExample {
    public static void main(String[] args) {
        System.out.println("=== DATABASE FACTORY PATTERN EXAMPLE ===\n");
        
        // 1. Simple Factory Example
        System.out.println("1. SIMPLE FACTORY PATTERN:");
        System.out.println("-".repeat(50));
        
        // Create connections using predefined configs
        DatabaseConnection mysqlConn = DatabaseConnectionFactory.createConnection("mysql-prod");
        mysqlConn.connect();
        mysqlConn.executeQuery("SELECT * FROM users");
        mysqlConn.disconnect();
        
        System.out.println();
        
        // Create connection from URL
        DatabaseConnection postgresConn = DatabaseConnectionFactory
            .createConnectionFromUrl("jdbc:postgresql://localhost:5432/mydb");
        postgresConn.connect();
        postgresConn.executeUpdate("UPDATE products SET price = price * 1.1");
        postgresConn.disconnect();
        
        // 2. Factory Method Pattern Example
        System.out.println("\n2. FACTORY METHOD PATTERN:");
        System.out.println("-".repeat(50));
        
        DatabaseConnectionProvider prodProvider = new ProductionDatabaseProvider();
        prodProvider.executeWithConnection("INSERT INTO logs (message, timestamp) VALUES ('Test', NOW())");
        
        DatabaseConnectionProvider devProvider = new DevelopmentDatabaseProvider();
        devProvider.executeWithConnection("DELETE FROM temp_data WHERE created < NOW() - INTERVAL '1 day'");
        
        // 3. Connection Pool Example
        System.out.println("\n3. CONNECTION POOL MANAGER:");
        System.out.println("-".repeat(50));
        
        ConnectionPoolManager poolManager = new ConnectionPoolManager();
        
        // Create pools for different databases
        DatabaseConfig mysqlConfig = new DatabaseConfig("mysql", "localhost", 3306, "app_db")
            .withCredentials("app_user", "password");
        poolManager.createPool("mysql-pool", mysqlConfig, 5);
        
        DatabaseConfig redisConfig = new DatabaseConfig("redis", "localhost", 6379, "0");
        poolManager.createPool("redis-cache", redisConfig, 3);
        
        // Use connections from pool
        System.out.println("\nUsing connections from pool:");
        DatabaseConnection conn1 = poolManager.getConnection("mysql-pool");
        conn1.executeQuery("SELECT COUNT(*) FROM users");
        
        DatabaseConnection conn2 = poolManager.getConnection("redis-cache");
        conn2.executeUpdate("SET user:1:name 'John Doe'");
        
        // Return connections to pool
        poolManager.returnConnection("mysql-pool", conn1);
        poolManager.returnConnection("redis-cache", conn2);
        
        // Close pools
        poolManager.closePool("mysql-pool");
        poolManager.closePool("redis-cache");
        
        // 4. Database Migration Example
        System.out.println("\n4. DATABASE MIGRATION SCENARIO:");
        System.out.println("-".repeat(50));
        
        // Simulate migrating data from one database to another
        DatabaseConfig sourceConfig = new DatabaseConfig("mysql", "old.server.com", 3306, "legacy_db")
            .withCredentials("reader", "read_pass");
        DatabaseConfig targetConfig = new DatabaseConfig("postgresql", "new.server.com", 5432, "modern_db")
            .withCredentials("writer", "write_pass")
            .withParam("schema", "migrated");
        
        DatabaseConnection source = DatabaseConnectionFactory.createConnectionFromConfig(sourceConfig);
        DatabaseConnection target = DatabaseConnectionFactory.createConnectionFromConfig(targetConfig);
        
        System.out.println("Starting migration from " + source.getDatabaseType() + 
            " to " + target.getDatabaseType());
        
        source.connect();
        target.connect();
        
        source.executeQuery("SELECT * FROM old_table");
        System.out.println("Data extracted from source");
        
        target.beginTransaction();
        target.executeUpdate("INSERT INTO new_table SELECT * FROM temp_migration");
        target.commit();
        System.out.println("Data loaded to target");
        
        source.disconnect();
        target.disconnect();
        
        System.out.println("Migration completed successfully!");
    }
}