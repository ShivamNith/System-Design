package org.example.designPatterns.bridgePattern;

import java.util.*;

/**
 * Database Connection Example using Bridge Pattern
 * 
 * This example demonstrates how the Bridge Pattern can be used to create
 * database operations that work across different database systems while
 * maintaining database-specific implementations.
 * 
 * The abstraction represents database operations, while implementations handle
 * database-specific connection and query execution (MySQL, PostgreSQL, MongoDB).
 */

// Implementation interface
interface DatabaseImplementation {
    void connect(String connectionString);
    void disconnect();
    ResultSet executeQuery(String query);
    int executeUpdate(String query);
    void beginTransaction();
    void commit();
    void rollback();
    String getDatabaseInfo();
}

// Mock ResultSet for demonstration
class ResultSet {
    private String description;
    private List<Map<String, Object>> data;
    
    public ResultSet(String description) {
        this.description = description;
        this.data = new ArrayList<>();
    }
    
    public void addRow(Map<String, Object> row) {
        data.add(new HashMap<>(row));
    }
    
    public List<Map<String, Object>> getAllRows() {
        return new ArrayList<>(data);
    }
    
    public boolean hasData() {
        return !data.isEmpty();
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("ResultSet: " + description + "\n");
        if (data.isEmpty()) {
            sb.append("  No data returned");
        } else {
            sb.append("  Rows: ").append(data.size()).append("\n");
            for (int i = 0; i < Math.min(3, data.size()); i++) {
                sb.append("    Row ").append(i + 1).append(": ").append(data.get(i)).append("\n");
            }
            if (data.size() > 3) {
                sb.append("    ... and ").append(data.size() - 3).append(" more rows");
            }
        }
        return sb.toString();
    }
}

// Concrete Implementations
class MySQLImplementation implements DatabaseImplementation {
    private boolean connected = false;
    private boolean inTransaction = false;
    
    @Override
    public void connect(String connectionString) {
        System.out.println("Connecting to MySQL database...");
        System.out.println("Connection string: " + connectionString);
        System.out.println("Loading MySQL JDBC driver: com.mysql.cj.jdbc.Driver");
        System.out.println("Establishing connection to MySQL server");
        connected = true;
        System.out.println("Connected to MySQL successfully!");
    }
    
    @Override
    public void disconnect() {
        if (connected) {
            System.out.println("Closing MySQL connection...");
            if (inTransaction) {
                rollback();
            }
            connected = false;
            System.out.println("MySQL connection closed");
        }
    }
    
    @Override
    public ResultSet executeQuery(String query) {
        if (!connected) throw new RuntimeException("Not connected to database");
        
        System.out.println("Executing MySQL query: " + query);
        System.out.println("Using MySQL-specific optimizations:");
        System.out.println("  - InnoDB storage engine features");
        System.out.println("  - MySQL query optimizer");
        
        // Simulate MySQL-specific query execution
        ResultSet result = new ResultSet("MySQL Query Result");
        
        // Add sample data based on query type
        if (query.toLowerCase().contains("select")) {
            Map<String, Object> row1 = new HashMap<>();
            row1.put("id", 1);
            row1.put("name", "MySQL User 1");
            row1.put("email", "user1@mysql.com");
            result.addRow(row1);
            
            Map<String, Object> row2 = new HashMap<>();
            row2.put("id", 2);
            row2.put("name", "MySQL User 2");
            row2.put("email", "user2@mysql.com");
            result.addRow(row2);
        }
        
        return result;
    }
    
    @Override
    public int executeUpdate(String query) {
        if (!connected) throw new RuntimeException("Not connected to database");
        
        System.out.println("Executing MySQL update: " + query);
        System.out.println("Using MySQL features:");
        System.out.println("  - Auto-increment handling");
        System.out.println("  - Foreign key constraint checking");
        System.out.println("  - Row-level locking with InnoDB");
        
        // Simulate rows affected
        int rowsAffected = 1;
        System.out.println("MySQL update completed. Rows affected: " + rowsAffected);
        return rowsAffected;
    }
    
    @Override
    public void beginTransaction() {
        System.out.println("BEGIN - MySQL transaction started");
        System.out.println("Setting isolation level: REPEATABLE READ");
        inTransaction = true;
    }
    
    @Override
    public void commit() {
        if (inTransaction) {
            System.out.println("COMMIT - MySQL transaction committed");
            System.out.println("InnoDB redo log synchronized");
            inTransaction = false;
        }
    }
    
    @Override
    public void rollback() {
        if (inTransaction) {
            System.out.println("ROLLBACK - MySQL transaction rolled back");
            System.out.println("InnoDB undo log applied");
            inTransaction = false;
        }
    }
    
    @Override
    public String getDatabaseInfo() {
        return "MySQL 8.0.28 - InnoDB Storage Engine, UTF-8 charset";
    }
}

class PostgreSQLImplementation implements DatabaseImplementation {
    private boolean connected = false;
    private boolean inTransaction = false;
    
    @Override
    public void connect(String connectionString) {
        System.out.println("Connecting to PostgreSQL database...");
        System.out.println("Connection string: " + connectionString);
        System.out.println("Loading PostgreSQL JDBC driver: org.postgresql.Driver");
        System.out.println("Establishing connection to PostgreSQL server");
        connected = true;
        System.out.println("Connected to PostgreSQL successfully!");
    }
    
    @Override
    public void disconnect() {
        if (connected) {
            System.out.println("Closing PostgreSQL connection...");
            if (inTransaction) {
                rollback();
            }
            connected = false;
            System.out.println("PostgreSQL connection closed");
        }
    }
    
    @Override
    public ResultSet executeQuery(String query) {
        if (!connected) throw new RuntimeException("Not connected to database");
        
        System.out.println("Executing PostgreSQL query: " + query);
        System.out.println("Using PostgreSQL advanced features:");
        System.out.println("  - Advanced indexing (GIN, GiST, BRIN)");
        System.out.println("  - Common Table Expressions (CTEs)");
        System.out.println("  - Window functions");
        
        ResultSet result = new ResultSet("PostgreSQL Query Result");
        
        if (query.toLowerCase().contains("select")) {
            Map<String, Object> row1 = new HashMap<>();
            row1.put("id", 1);
            row1.put("name", "PostgreSQL User 1");
            row1.put("email", "user1@postgresql.com");
            row1.put("created_at", "2023-01-15 10:30:00");
            result.addRow(row1);
            
            Map<String, Object> row2 = new HashMap<>();
            row2.put("id", 2);
            row2.put("name", "PostgreSQL User 2");
            row2.put("email", "user2@postgresql.com");
            row2.put("created_at", "2023-01-16 14:45:00");
            result.addRow(row2);
        }
        
        return result;
    }
    
    @Override
    public int executeUpdate(String query) {
        if (!connected) throw new RuntimeException("Not connected to database");
        
        System.out.println("Executing PostgreSQL update: " + query);
        System.out.println("Using PostgreSQL features:");
        System.out.println("  - MVCC (Multi-Version Concurrency Control)");
        System.out.println("  - Advanced constraint checking");
        System.out.println("  - Partial indexes");
        
        int rowsAffected = 1;
        System.out.println("PostgreSQL update completed. Rows affected: " + rowsAffected);
        return rowsAffected;
    }
    
    @Override
    public void beginTransaction() {
        System.out.println("BEGIN - PostgreSQL transaction started");
        System.out.println("Setting isolation level: READ COMMITTED");
        inTransaction = true;
    }
    
    @Override
    public void commit() {
        if (inTransaction) {
            System.out.println("COMMIT - PostgreSQL transaction committed");
            System.out.println("WAL (Write-Ahead Logging) synchronized");
            inTransaction = false;
        }
    }
    
    @Override
    public void rollback() {
        if (inTransaction) {
            System.out.println("ROLLBACK - PostgreSQL transaction rolled back");
            System.out.println("MVCC cleanup initiated");
            inTransaction = false;
        }
    }
    
    @Override
    public String getDatabaseInfo() {
        return "PostgreSQL 14.2 - Advanced Open Source Database with JSON support";
    }
}

class MongoDBImplementation implements DatabaseImplementation {
    private boolean connected = false;
    private boolean inTransaction = false;
    
    @Override
    public void connect(String connectionString) {
        System.out.println("Connecting to MongoDB...");
        System.out.println("Connection string: " + connectionString);
        System.out.println("Establishing connection to MongoDB cluster");
        System.out.println("Authenticating with MongoDB Atlas/Self-hosted");
        connected = true;
        System.out.println("Connected to MongoDB successfully!");
    }
    
    @Override
    public void disconnect() {
        if (connected) {
            System.out.println("Closing MongoDB connection...");
            if (inTransaction) {
                rollback();
            }
            connected = false;
            System.out.println("MongoDB connection closed");
        }
    }
    
    @Override
    public ResultSet executeQuery(String query) {
        if (!connected) throw new RuntimeException("Not connected to database");
        
        System.out.println("Executing MongoDB query: " + query);
        System.out.println("Converting SQL-like query to MongoDB operations:");
        System.out.println("  - Using aggregation pipeline");
        System.out.println("  - Leveraging document structure");
        System.out.println("  - Index usage optimization");
        
        ResultSet result = new ResultSet("MongoDB Query Result");
        
        if (query.toLowerCase().contains("select") || query.toLowerCase().contains("find")) {
            Map<String, Object> doc1 = new HashMap<>();
            doc1.put("_id", "507f1f77bcf86cd799439011");
            doc1.put("name", "MongoDB User 1");
            doc1.put("email", "user1@mongodb.com");
            doc1.put("profile", Map.of("age", 25, "city", "San Francisco"));
            result.addRow(doc1);
            
            Map<String, Object> doc2 = new HashMap<>();
            doc2.put("_id", "507f1f77bcf86cd799439012");
            doc2.put("name", "MongoDB User 2");
            doc2.put("email", "user2@mongodb.com");
            doc2.put("profile", Map.of("age", 30, "city", "New York"));
            result.addRow(doc2);
        }
        
        return result;
    }
    
    @Override
    public int executeUpdate(String query) {
        if (!connected) throw new RuntimeException("Not connected to database");
        
        System.out.println("Executing MongoDB update: " + query);
        System.out.println("Converting to MongoDB operations:");
        System.out.println("  - Using updateOne/updateMany");
        System.out.println("  - Leveraging document-based operations");
        System.out.println("  - Atomic field updates");
        
        int documentsAffected = 1;
        System.out.println("MongoDB update completed. Documents affected: " + documentsAffected);
        return documentsAffected;
    }
    
    @Override
    public void beginTransaction() {
        System.out.println("Starting MongoDB transaction");
        System.out.println("Note: Requires replica set or sharded cluster");
        System.out.println("Session started with read/write concern");
        inTransaction = true;
    }
    
    @Override
    public void commit() {
        if (inTransaction) {
            System.out.println("Committing MongoDB transaction");
            System.out.println("Transaction committed across all replica set members");
            inTransaction = false;
        }
    }
    
    @Override
    public void rollback() {
        if (inTransaction) {
            System.out.println("Aborting MongoDB transaction");
            System.out.println("Rolling back changes across replica set");
            inTransaction = false;
        }
    }
    
    @Override
    public String getDatabaseInfo() {
        return "MongoDB 5.0.6 - NoSQL Document Database with ACID transactions";
    }
}

// Abstraction
abstract class Database {
    protected DatabaseImplementation implementation;
    protected String connectionString;
    
    public Database(DatabaseImplementation implementation) {
        this.implementation = implementation;
    }
    
    public void setImplementation(DatabaseImplementation implementation) {
        if (this.implementation != null) {
            this.implementation.disconnect();
        }
        this.implementation = implementation;
        if (connectionString != null) {
            this.implementation.connect(connectionString);
        }
        System.out.println("Switched to: " + implementation.getDatabaseInfo());
    }
    
    public void connect(String connectionString) {
        this.connectionString = connectionString;
        implementation.connect(connectionString);
    }
    
    public void disconnect() {
        implementation.disconnect();
    }
    
    public abstract void createTable(String tableName, String[] columns);
    public abstract void insertData(String tableName, Map<String, Object> data);
    public abstract ResultSet findAll(String tableName);
    public abstract ResultSet findById(String tableName, Object id);
    public abstract void deleteById(String tableName, Object id);
}

// Refined Abstractions
class SimpleDatabase extends Database {
    public SimpleDatabase(DatabaseImplementation implementation) {
        super(implementation);
    }
    
    @Override
    public void createTable(String tableName, String[] columns) {
        System.out.println("\n=== Creating Simple Table: " + tableName + " ===");
        StringBuilder sql = new StringBuilder("CREATE TABLE " + tableName + " (");
        for (int i = 0; i < columns.length; i++) {
            sql.append(columns[i]);
            if (i < columns.length - 1) sql.append(", ");
        }
        sql.append(")");
        
        implementation.executeUpdate(sql.toString());
        System.out.println("Table '" + tableName + "' created successfully");
    }
    
    @Override
    public void insertData(String tableName, Map<String, Object> data) {
        System.out.println("\n=== Inserting Data into " + tableName + " ===");
        StringBuilder sql = new StringBuilder("INSERT INTO " + tableName + " (");
        StringBuilder values = new StringBuilder(" VALUES (");
        
        int i = 0;
        for (String key : data.keySet()) {
            sql.append(key);
            values.append("'").append(data.get(key)).append("'");
            
            if (i < data.size() - 1) {
                sql.append(", ");
                values.append(", ");
            }
            i++;
        }
        
        sql.append(")").append(values).append(")");
        
        int rowsAffected = implementation.executeUpdate(sql.toString());
        System.out.println("Data inserted successfully. Rows affected: " + rowsAffected);
    }
    
    @Override
    public ResultSet findAll(String tableName) {
        System.out.println("\n=== Finding All Records in " + tableName + " ===");
        String sql = "SELECT * FROM " + tableName;
        return implementation.executeQuery(sql);
    }
    
    @Override
    public ResultSet findById(String tableName, Object id) {
        System.out.println("\n=== Finding Record by ID in " + tableName + " ===");
        String sql = "SELECT * FROM " + tableName + " WHERE id = '" + id + "'";
        return implementation.executeQuery(sql);
    }
    
    @Override
    public void deleteById(String tableName, Object id) {
        System.out.println("\n=== Deleting Record by ID from " + tableName + " ===");
        String sql = "DELETE FROM " + tableName + " WHERE id = '" + id + "'";
        int rowsAffected = implementation.executeUpdate(sql);
        System.out.println("Record deleted. Rows affected: " + rowsAffected);
    }
}

class AdvancedDatabase extends Database {
    public AdvancedDatabase(DatabaseImplementation implementation) {
        super(implementation);
    }
    
    @Override
    public void createTable(String tableName, String[] columns) {
        System.out.println("\n=== Creating Advanced Table: " + tableName + " ===");
        StringBuilder sql = new StringBuilder("CREATE TABLE " + tableName + " (");
        for (int i = 0; i < columns.length; i++) {
            sql.append(columns[i]);
            if (i < columns.length - 1) sql.append(", ");
        }
        sql.append(", PRIMARY KEY (id), created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, ");
        sql.append("updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP)");
        
        implementation.executeUpdate(sql.toString());
        
        // Create indexes for better performance
        String indexSql = "CREATE INDEX idx_" + tableName + "_created_at ON " + tableName + " (created_at)";
        implementation.executeUpdate(indexSql);
        
        System.out.println("Advanced table '" + tableName + "' created with constraints and indexes");
    }
    
    @Override
    public void insertData(String tableName, Map<String, Object> data) {
        System.out.println("\n=== Inserting Data with Transaction Support ===");
        
        implementation.beginTransaction();
        try {
            StringBuilder sql = new StringBuilder("INSERT INTO " + tableName + " (");
            StringBuilder values = new StringBuilder(" VALUES (");
            
            int i = 0;
            for (String key : data.keySet()) {
                sql.append(key);
                values.append("'").append(data.get(key)).append("'");
                
                if (i < data.size() - 1) {
                    sql.append(", ");
                    values.append(", ");
                }
                i++;
            }
            
            sql.append(")").append(values).append(")");
            
            int rowsAffected = implementation.executeUpdate(sql.toString());
            
            // Log the insertion for audit purposes
            String logSql = "INSERT INTO audit_log (table_name, action, timestamp) VALUES ('" 
                + tableName + "', 'INSERT', NOW())";
            implementation.executeUpdate(logSql);
            
            implementation.commit();
            System.out.println("Data inserted with audit trail. Rows affected: " + rowsAffected);
            
        } catch (Exception e) {
            implementation.rollback();
            System.out.println("Error inserting data. Transaction rolled back: " + e.getMessage());
        }
    }
    
    @Override
    public ResultSet findAll(String tableName) {
        System.out.println("\n=== Finding All Records with Pagination ===");
        String sql = "SELECT * FROM " + tableName + " ORDER BY created_at DESC LIMIT 100 OFFSET 0";
        return implementation.executeQuery(sql);
    }
    
    @Override
    public ResultSet findById(String tableName, Object id) {
        System.out.println("\n=== Finding Record with Audit Information ===");
        String sql = "SELECT t.*, audit.action, audit.timestamp " +
                    "FROM " + tableName + " t " +
                    "LEFT JOIN audit_log audit ON audit.table_name = '" + tableName + "' " +
                    "WHERE t.id = '" + id + "' " +
                    "ORDER BY audit.timestamp DESC";
        return implementation.executeQuery(sql);
    }
    
    @Override
    public void deleteById(String tableName, Object id) {
        System.out.println("\n=== Soft Delete with Audit Trail ===");
        
        implementation.beginTransaction();
        try {
            // Soft delete - mark as deleted instead of removing
            String sql = "UPDATE " + tableName + " SET is_deleted = TRUE, deleted_at = NOW() WHERE id = '" + id + "'";
            int rowsAffected = implementation.executeUpdate(sql);
            
            // Log the deletion
            String logSql = "INSERT INTO audit_log (table_name, action, record_id, timestamp) VALUES ('" 
                + tableName + "', 'SOFT_DELETE', '" + id + "', NOW())";
            implementation.executeUpdate(logSql);
            
            implementation.commit();
            System.out.println("Record soft deleted with audit trail. Rows affected: " + rowsAffected);
            
        } catch (Exception e) {
            implementation.rollback();
            System.out.println("Error deleting record. Transaction rolled back: " + e.getMessage());
        }
    }
    
    public void backup(String backupPath) {
        System.out.println("\n=== Creating Database Backup ===");
        System.out.println("Database info: " + implementation.getDatabaseInfo());
        System.out.println("Backing up to: " + backupPath);
        
        String backupSql = "BACKUP DATABASE TO '" + backupPath + "'";
        implementation.executeUpdate(backupSql);
        
        System.out.println("Backup completed successfully");
    }
    
    public void analyzePerformance() {
        System.out.println("\n=== Analyzing Database Performance ===");
        implementation.executeQuery("SHOW PROCESSLIST");
        implementation.executeQuery("EXPLAIN SELECT * FROM users WHERE email LIKE '%@example.com'");
        System.out.println("Performance analysis completed");
    }
}

// Demo Application
public class DatabaseConnectionExample {
    public static void main(String[] args) {
        System.out.println("=== Database Bridge Pattern Demo ===\n");
        
        // Create different database implementations
        DatabaseImplementation mysql = new MySQLImplementation();
        DatabaseImplementation postgres = new PostgreSQLImplementation();
        DatabaseImplementation mongodb = new MongoDBImplementation();
        
        // Create database abstraction
        AdvancedDatabase database = new AdvancedDatabase(mysql);
        
        // Connect to MySQL
        System.out.println("1. Working with MySQL:");
        database.connect("jdbc:mysql://localhost:3306/testdb");
        
        // Create table and insert data
        String[] columns = {"id INT AUTO_INCREMENT", "name VARCHAR(100)", "email VARCHAR(100)"};
        database.createTable("users", columns);
        
        Map<String, Object> userData = new HashMap<>();
        userData.put("name", "John Doe");
        userData.put("email", "john.doe@example.com");
        database.insertData("users", userData);
        
        // Query data
        ResultSet allUsers = database.findAll("users");
        System.out.println(allUsers);
        
        ResultSet userById = database.findById("users", 1);
        System.out.println(userById);
        
        // Switch to PostgreSQL
        System.out.println("\n" + "=".repeat(60));
        System.out.println("2. Switching to PostgreSQL:");
        database.setImplementation(postgres);
        database.connect("jdbc:postgresql://localhost:5432/testdb");
        
        // Same operations with different implementation
        database.createTable("users", columns);
        database.insertData("users", userData);
        
        ResultSet pgUsers = database.findAll("users");
        System.out.println(pgUsers);
        
        // Switch to MongoDB
        System.out.println("\n" + "=".repeat(60));
        System.out.println("3. Switching to MongoDB:");
        database.setImplementation(mongodb);
        database.connect("mongodb://localhost:27017/testdb");
        
        // MongoDB operations (SQL-like interface)
        database.createTable("users", columns); // Creates collection
        database.insertData("users", userData); // Inserts document
        
        ResultSet mongoUsers = database.findAll("users");
        System.out.println(mongoUsers);
        
        // Demonstrate advanced features
        System.out.println("\n" + "=".repeat(60));
        System.out.println("4. Advanced Database Operations:");
        
        // Backup
        database.backup("/backup/mongodb_backup_" + System.currentTimeMillis());
        
        // Performance analysis
        database.analyzePerformance();
        
        // Soft delete
        database.deleteById("users", 1);
        
        // Demonstrate database selection based on requirements
        System.out.println("\n" + "=".repeat(60));
        System.out.println("5. Automatic Database Selection:");
        demonstrateAutomaticDatabaseSelection();
        
        // Clean up
        database.disconnect();
    }
    
    private static void demonstrateAutomaticDatabaseSelection() {
        // Simulate different application requirements
        String[] requirements = {"high_transactions", "analytics", "document_storage"};
        
        for (String requirement : requirements) {
            System.out.println("\nRequirement: " + requirement);
            
            DatabaseImplementation selectedDb;
            switch (requirement) {
                case "high_transactions":
                    selectedDb = new PostgreSQLImplementation();
                    System.out.println("Selected PostgreSQL for ACID compliance and high concurrency");
                    break;
                case "analytics":
                    selectedDb = new MySQLImplementation();
                    System.out.println("Selected MySQL for optimized read performance and reporting");
                    break;
                case "document_storage":
                    selectedDb = new MongoDBImplementation();
                    System.out.println("Selected MongoDB for flexible document structure");
                    break;
                default:
                    selectedDb = new MySQLImplementation();
                    System.out.println("Selected MySQL as default");
            }
            
            SimpleDatabase db = new SimpleDatabase(selectedDb);
            db.connect("connection_string_for_" + requirement);
            
            // Perform sample operation
            Map<String, Object> sampleData = new HashMap<>();
            sampleData.put("requirement", requirement);
            sampleData.put("timestamp", System.currentTimeMillis());
            
            db.insertData("requirements_test", sampleData);
            db.disconnect();
        }
    }
}