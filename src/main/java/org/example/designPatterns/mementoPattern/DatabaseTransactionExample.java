package org.example.designPatterns.mementoPattern;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Memento Pattern Example: Database Transaction System with Rollback
 * 
 * This example demonstrates the Memento Pattern in a database context where
 * transactions can be rolled back to maintain data consistency and integrity.
 * It simulates database operations with automatic rollback capabilities.
 */

// Database record representation
class DatabaseRecord {
    private final String id;
    private Map<String, Object> fields;
    private LocalDateTime lastModified;
    private String modifiedBy;
    
    public DatabaseRecord(String id) {
        this.id = id;
        this.fields = new HashMap<>();
        this.lastModified = LocalDateTime.now();
        this.modifiedBy = "system";
    }
    
    // Copy constructor for memento
    public DatabaseRecord(DatabaseRecord other) {
        this.id = other.id;
        this.fields = new HashMap<>(other.fields);
        this.lastModified = other.lastModified;
        this.modifiedBy = other.modifiedBy;
    }
    
    public void setField(String fieldName, Object value, String user) {
        fields.put(fieldName, value);
        this.lastModified = LocalDateTime.now();
        this.modifiedBy = user;
    }
    
    public Object getField(String fieldName) {
        return fields.get(fieldName);
    }
    
    public void removeField(String fieldName, String user) {
        fields.remove(fieldName);
        this.lastModified = LocalDateTime.now();
        this.modifiedBy = user;
    }
    
    public String getId() { return id; }
    public Map<String, Object> getFields() { return new HashMap<>(fields); }
    public LocalDateTime getLastModified() { return lastModified; }
    public String getModifiedBy() { return modifiedBy; }
    
    @Override
    public String toString() {
        return String.format("Record[%s: %s, modified by %s at %s]", 
                           id, fields, modifiedBy, 
                           lastModified.format(DateTimeFormatter.ofPattern("HH:mm:ss")));
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        DatabaseRecord that = (DatabaseRecord) obj;
        return Objects.equals(id, that.id) && Objects.equals(fields, that.fields);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, fields);
    }
}

// Database table simulation
class DatabaseTable {
    private final String tableName;
    private final Map<String, DatabaseRecord> records;
    private final AtomicLong recordCounter;
    
    public DatabaseTable(String tableName) {
        this.tableName = tableName;
        this.records = new HashMap<>();
        this.recordCounter = new AtomicLong(1);
    }
    
    // Copy constructor for memento
    public DatabaseTable(DatabaseTable other) {
        this.tableName = other.tableName;
        this.records = new HashMap<>();
        for (Map.Entry<String, DatabaseRecord> entry : other.records.entrySet()) {
            this.records.put(entry.getKey(), new DatabaseRecord(entry.getValue()));
        }
        this.recordCounter = new AtomicLong(other.recordCounter.get());
    }
    
    public String insert(Map<String, Object> data, String user) {
        String id = tableName + "_" + recordCounter.getAndIncrement();
        DatabaseRecord record = new DatabaseRecord(id);
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            record.setField(entry.getKey(), entry.getValue(), user);
        }
        records.put(id, record);
        return id;
    }
    
    public boolean update(String id, Map<String, Object> data, String user) {
        DatabaseRecord record = records.get(id);
        if (record != null) {
            for (Map.Entry<String, Object> entry : data.entrySet()) {
                record.setField(entry.getKey(), entry.getValue(), user);
            }
            return true;
        }
        return false;
    }
    
    public boolean delete(String id) {
        return records.remove(id) != null;
    }
    
    public DatabaseRecord select(String id) {
        DatabaseRecord record = records.get(id);
        return record != null ? new DatabaseRecord(record) : null;
    }
    
    public List<DatabaseRecord> selectAll() {
        List<DatabaseRecord> result = new ArrayList<>();
        for (DatabaseRecord record : records.values()) {
            result.add(new DatabaseRecord(record));
        }
        return result;
    }
    
    public List<DatabaseRecord> selectWhere(String fieldName, Object value) {
        List<DatabaseRecord> result = new ArrayList<>();
        for (DatabaseRecord record : records.values()) {
            Object fieldValue = record.getField(fieldName);
            if (Objects.equals(fieldValue, value)) {
                result.add(new DatabaseRecord(record));
            }
        }
        return result;
    }
    
    public int getRecordCount() {
        return records.size();
    }
    
    public String getTableName() {
        return tableName;
    }
    
    @Override
    public String toString() {
        return String.format("Table[%s: %d records]", tableName, records.size());
    }
}

// Memento interface for type safety
interface DatabaseMemento {
    // Marker interface - only Database should access the actual state
}

// Originator class - Database System
class Database {
    private Map<String, DatabaseTable> tables;
    private Set<String> activeUsers;
    private Map<String, String> systemProperties;
    private LocalDateTime lastBackup;
    private long transactionCounter;
    private boolean autoCommit;
    
    public Database() {
        this.tables = new HashMap<>();
        this.activeUsers = new HashSet<>();
        this.systemProperties = new HashMap<>();
        this.lastBackup = LocalDateTime.now();
        this.transactionCounter = 0;
        this.autoCommit = true;
        
        // Initialize system properties
        systemProperties.put("version", "1.0");
        systemProperties.put("charset", "UTF-8");
        systemProperties.put("timezone", "UTC");
        
        System.out.println("Database system initialized");
    }
    
    // Table management
    public void createTable(String tableName) {
        if (!tables.containsKey(tableName)) {
            tables.put(tableName, new DatabaseTable(tableName));
            System.out.println("Table created: " + tableName);
        } else {
            System.out.println("Table already exists: " + tableName);
        }
    }
    
    public boolean dropTable(String tableName) {
        DatabaseTable removed = tables.remove(tableName);
        if (removed != null) {
            System.out.println("Table dropped: " + tableName);
            return true;
        } else {
            System.out.println("Table not found: " + tableName);
            return false;
        }
    }
    
    public boolean tableExists(String tableName) {
        return tables.containsKey(tableName);
    }
    
    // Record operations
    public String insertRecord(String tableName, Map<String, Object> data, String user) {
        DatabaseTable table = tables.get(tableName);
        if (table != null) {
            String id = table.insert(data, user);
            activeUsers.add(user);
            System.out.println("Record inserted: " + id + " by " + user);
            return id;
        } else {
            System.out.println("Table not found: " + tableName);
            return null;
        }
    }
    
    public boolean updateRecord(String tableName, String id, Map<String, Object> data, String user) {
        DatabaseTable table = tables.get(tableName);
        if (table != null) {
            boolean success = table.update(id, data, user);
            if (success) {
                activeUsers.add(user);
                System.out.println("Record updated: " + id + " by " + user);
            } else {
                System.out.println("Record not found: " + id);
            }
            return success;
        } else {
            System.out.println("Table not found: " + tableName);
            return false;
        }
    }
    
    public boolean deleteRecord(String tableName, String id, String user) {
        DatabaseTable table = tables.get(tableName);
        if (table != null) {
            boolean success = table.delete(id);
            if (success) {
                activeUsers.add(user);
                System.out.println("Record deleted: " + id + " by " + user);
            } else {
                System.out.println("Record not found: " + id);
            }
            return success;
        } else {
            System.out.println("Table not found: " + tableName);
            return false;
        }
    }
    
    public DatabaseRecord selectRecord(String tableName, String id) {
        DatabaseTable table = tables.get(tableName);
        if (table != null) {
            return table.select(id);
        } else {
            System.out.println("Table not found: " + tableName);
            return null;
        }
    }
    
    public List<DatabaseRecord> selectAllRecords(String tableName) {
        DatabaseTable table = tables.get(tableName);
        if (table != null) {
            return table.selectAll();
        } else {
            System.out.println("Table not found: " + tableName);
            return new ArrayList<>();
        }
    }
    
    public List<DatabaseRecord> selectRecordsWhere(String tableName, String fieldName, Object value) {
        DatabaseTable table = tables.get(tableName);
        if (table != null) {
            return table.selectWhere(fieldName, value);
        } else {
            System.out.println("Table not found: " + tableName);
            return new ArrayList<>();
        }
    }
    
    // System management
    public void setSystemProperty(String key, String value) {
        systemProperties.put(key, value);
        System.out.println("System property set: " + key + " = " + value);
    }
    
    public String getSystemProperty(String key) {
        return systemProperties.get(key);
    }
    
    public void addUser(String user) {
        activeUsers.add(user);
        System.out.println("User logged in: " + user);
    }
    
    public void removeUser(String user) {
        activeUsers.remove(user);
        System.out.println("User logged out: " + user);
    }
    
    public void setAutoCommit(boolean autoCommit) {
        this.autoCommit = autoCommit;
        System.out.println("Auto-commit " + (autoCommit ? "enabled" : "disabled"));
    }
    
    // Memento creation (database snapshot)
    public DatabaseMemento createSnapshot() {
        transactionCounter++;
        return new DatabaseSnapshot(
            copyTables(),
            new HashSet<>(activeUsers),
            new HashMap<>(systemProperties),
            LocalDateTime.now(),
            transactionCounter,
            autoCommit
        );
    }
    
    // Memento restoration (rollback)
    public void restoreSnapshot(DatabaseMemento memento) {
        if (memento instanceof DatabaseSnapshot) {
            DatabaseSnapshot snapshot = (DatabaseSnapshot) memento;
            this.tables = copyTables(snapshot.tables);
            this.activeUsers = new HashSet<>(snapshot.activeUsers);
            this.systemProperties = new HashMap<>(snapshot.systemProperties);
            this.lastBackup = snapshot.snapshotTime;
            this.transactionCounter = snapshot.transactionId;
            this.autoCommit = snapshot.autoCommit;
            
            System.out.println("Database restored to snapshot from " + 
                lastBackup.format(DateTimeFormatter.ofPattern("HH:mm:ss")) + 
                " (Transaction ID: " + transactionCounter + ")");
        }
    }
    
    private Map<String, DatabaseTable> copyTables() {
        Map<String, DatabaseTable> copy = new HashMap<>();
        for (Map.Entry<String, DatabaseTable> entry : tables.entrySet()) {
            copy.put(entry.getKey(), new DatabaseTable(entry.getValue()));
        }
        return copy;
    }
    
    private Map<String, DatabaseTable> copyTables(Map<String, DatabaseTable> source) {
        Map<String, DatabaseTable> copy = new HashMap<>();
        for (Map.Entry<String, DatabaseTable> entry : source.entrySet()) {
            copy.put(entry.getKey(), new DatabaseTable(entry.getValue()));
        }
        return copy;
    }
    
    // Database statistics and info
    public void displayDatabaseInfo() {
        System.out.println("\n=== Database Information ===");
        System.out.println("Tables: " + tables.size());
        for (DatabaseTable table : tables.values()) {
            System.out.println("  " + table);
        }
        System.out.println("Active Users: " + activeUsers);
        System.out.println("System Properties: " + systemProperties);
        System.out.println("Last Backup: " + lastBackup.format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        System.out.println("Transaction Counter: " + transactionCounter);
        System.out.println("Auto-commit: " + autoCommit);
        System.out.println("===============================\n");
    }
    
    public void displayTableContents(String tableName) {
        DatabaseTable table = tables.get(tableName);
        if (table != null) {
            System.out.println("\n=== Table: " + tableName + " ===");
            List<DatabaseRecord> records = table.selectAll();
            if (records.isEmpty()) {
                System.out.println("No records found");
            } else {
                for (DatabaseRecord record : records) {
                    System.out.println("  " + record);
                }
            }
            System.out.println("Total records: " + records.size());
            System.out.println("===========================\n");
        } else {
            System.out.println("Table not found: " + tableName);
        }
    }
    
    // Getters
    public Set<String> getTableNames() { return new HashSet<>(tables.keySet()); }
    public Set<String> getActiveUsers() { return new HashSet<>(activeUsers); }
    public Map<String, String> getSystemProperties() { return new HashMap<>(systemProperties); }
    public long getTransactionCounter() { return transactionCounter; }
    public boolean isAutoCommit() { return autoCommit; }
    
    // Inner memento class for encapsulation
    private static class DatabaseSnapshot implements DatabaseMemento {
        private final Map<String, DatabaseTable> tables;
        private final Set<String> activeUsers;
        private final Map<String, String> systemProperties;
        private final LocalDateTime snapshotTime;
        private final long transactionId;
        private final boolean autoCommit;
        
        private DatabaseSnapshot(Map<String, DatabaseTable> tables, Set<String> activeUsers,
                               Map<String, String> systemProperties, LocalDateTime snapshotTime,
                               long transactionId, boolean autoCommit) {
            this.tables = tables;
            this.activeUsers = activeUsers;
            this.systemProperties = systemProperties;
            this.snapshotTime = snapshotTime;
            this.transactionId = transactionId;
            this.autoCommit = autoCommit;
        }
        
        @Override
        public String toString() {
            return String.format("Snapshot[%s, TxnID:%d, Tables:%d, Users:%d]",
                               snapshotTime.format(DateTimeFormatter.ofPattern("HH:mm:ss")),
                               transactionId, tables.size(), activeUsers.size());
        }
    }
}

// Caretaker class - Transaction Manager
class TransactionManager {
    private final Database database;
    private final List<DatabaseMemento> transactionLog;
    private final Map<String, DatabaseMemento> savepoints;
    private final int maxTransactionHistory;
    private DatabaseMemento currentTransaction;
    private boolean inTransaction;
    private String transactionName;
    
    public TransactionManager(Database database, int maxTransactionHistory) {
        this.database = database;
        this.transactionLog = new ArrayList<>();
        this.savepoints = new HashMap<>();
        this.maxTransactionHistory = maxTransactionHistory;
        this.inTransaction = false;
    }
    
    public TransactionManager(Database database) {
        this(database, 100); // Default max history
    }
    
    // Transaction control
    public void beginTransaction(String transactionName) {
        if (inTransaction) {
            System.out.println("Warning: Already in transaction '" + this.transactionName + "'");
            return;
        }
        
        this.transactionName = transactionName;
        this.currentTransaction = database.createSnapshot();
        this.inTransaction = true;
        database.setAutoCommit(false);
        
        System.out.println("Transaction started: " + transactionName);
    }
    
    public void beginTransaction() {
        beginTransaction("Transaction_" + System.currentTimeMillis());
    }
    
    public boolean commitTransaction() {
        if (!inTransaction) {
            System.out.println("No active transaction to commit");
            return false;
        }
        
        // Add current state to transaction log
        transactionLog.add(database.createSnapshot());
        
        // Maintain maximum history size
        if (transactionLog.size() > maxTransactionHistory) {
            transactionLog.remove(0);
        }
        
        this.inTransaction = false;
        this.currentTransaction = null;
        database.setAutoCommit(true);
        
        System.out.println("Transaction committed: " + transactionName);
        return true;
    }
    
    public boolean rollbackTransaction() {
        if (!inTransaction || currentTransaction == null) {
            System.out.println("No active transaction to rollback");
            return false;
        }
        
        database.restoreSnapshot(currentTransaction);
        this.inTransaction = false;
        this.currentTransaction = null;
        database.setAutoCommit(true);
        
        System.out.println("Transaction rolled back: " + transactionName);
        return true;
    }
    
    // Savepoint management
    public void createSavepoint(String savepointName) {
        if (!inTransaction) {
            System.out.println("Cannot create savepoint: No active transaction");
            return;
        }
        
        DatabaseMemento savepoint = database.createSnapshot();
        savepoints.put(savepointName, savepoint);
        System.out.println("Savepoint created: " + savepointName);
    }
    
    public boolean rollbackToSavepoint(String savepointName) {
        if (!inTransaction) {
            System.out.println("Cannot rollback to savepoint: No active transaction");
            return false;
        }
        
        DatabaseMemento savepoint = savepoints.get(savepointName);
        if (savepoint != null) {
            database.restoreSnapshot(savepoint);
            System.out.println("Rolled back to savepoint: " + savepointName);
            return true;
        } else {
            System.out.println("Savepoint not found: " + savepointName);
            return false;
        }
    }
    
    public boolean releaseSavepoint(String savepointName) {
        DatabaseMemento removed = savepoints.remove(savepointName);
        if (removed != null) {
            System.out.println("Savepoint released: " + savepointName);
            return true;
        } else {
            System.out.println("Savepoint not found: " + savepointName);
            return false;
        }
    }
    
    // Historical rollback (outside transactions)
    public boolean rollbackToTransaction(int transactionIndex) {
        if (transactionIndex < 0 || transactionIndex >= transactionLog.size()) {
            System.out.println("Invalid transaction index: " + transactionIndex);
            return false;
        }
        
        if (inTransaction) {
            System.out.println("Cannot rollback: Active transaction in progress");
            return false;
        }
        
        DatabaseMemento transaction = transactionLog.get(transactionIndex);
        database.restoreSnapshot(transaction);
        System.out.println("Rolled back to transaction " + transactionIndex);
        return true;
    }
    
    public boolean rollbackToLatestTransaction() {
        if (transactionLog.isEmpty()) {
            System.out.println("No transaction history available");
            return false;
        }
        
        return rollbackToTransaction(transactionLog.size() - 1);
    }
    
    // Batch operations with automatic rollback on failure
    public boolean executeBatch(List<Runnable> operations, String batchName) {
        beginTransaction(batchName);
        
        try {
            System.out.println("Executing batch: " + batchName + " (" + operations.size() + " operations)");
            
            for (int i = 0; i < operations.size(); i++) {
                System.out.println("Executing operation " + (i + 1) + "/" + operations.size());
                operations.get(i).run();
            }
            
            commitTransaction();
            System.out.println("Batch completed successfully: " + batchName);
            return true;
            
        } catch (Exception e) {
            System.out.println("Batch failed: " + e.getMessage());
            rollbackTransaction();
            return false;
        }
    }
    
    // Information methods
    public void displayTransactionStatus() {
        System.out.println("\n=== Transaction Status ===");
        System.out.println("In Transaction: " + inTransaction);
        if (inTransaction) {
            System.out.println("Transaction Name: " + transactionName);
            System.out.println("Active Savepoints: " + savepoints.keySet());
        }
        System.out.println("Transaction History: " + transactionLog.size() + "/" + maxTransactionHistory);
        System.out.println("==========================\n");
    }
    
    public void displayTransactionHistory() {
        System.out.println("\n=== Transaction History ===");
        if (transactionLog.isEmpty()) {
            System.out.println("No transaction history");
        } else {
            for (int i = 0; i < transactionLog.size(); i++) {
                System.out.println("  " + i + ": " + transactionLog.get(i));
            }
        }
        System.out.println("============================\n");
    }
    
    public void displaySavepoints() {
        System.out.println("\n=== Active Savepoints ===");
        if (savepoints.isEmpty()) {
            System.out.println("No savepoints");
        } else {
            for (Map.Entry<String, DatabaseMemento> entry : savepoints.entrySet()) {
                System.out.println("  " + entry.getKey() + ": " + entry.getValue());
            }
        }
        System.out.println("==========================\n");
    }
    
    // Getters
    public boolean isInTransaction() { return inTransaction; }
    public String getCurrentTransactionName() { return transactionName; }
    public int getTransactionHistorySize() { return transactionLog.size(); }
    public Set<String> getSavepointNames() { return new HashSet<>(savepoints.keySet()); }
    
    // Clear all history and savepoints
    public void clearHistory() {
        if (inTransaction) {
            System.out.println("Cannot clear history: Active transaction in progress");
            return;
        }
        
        transactionLog.clear();
        savepoints.clear();
        System.out.println("Transaction history and savepoints cleared");
    }
}

// Client code demonstrating the Database Transaction System
public class DatabaseTransactionExample {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Database Transaction Memento Pattern Demo ===\n");
        
        // Create database and transaction manager
        Database db = new Database();
        TransactionManager txnManager = new TransactionManager(db);
        
        // Initial setup
        db.createTable("users");
        db.createTable("orders");
        db.addUser("admin");
        db.addUser("user1");
        
        db.displayDatabaseInfo();
        
        // Insert some initial data
        System.out.println("=== Initial Data Setup ===");
        Map<String, Object> userData1 = new HashMap<>();
        userData1.put("name", "John Doe");
        userData1.put("email", "john@example.com");
        userData1.put("age", 30);
        String userId1 = db.insertRecord("users", userData1, "admin");
        
        Map<String, Object> userData2 = new HashMap<>();
        userData2.put("name", "Jane Smith");
        userData2.put("email", "jane@example.com");
        userData2.put("age", 25);
        String userId2 = db.insertRecord("users", userData2, "admin");
        
        db.displayTableContents("users");
        
        Thread.sleep(1000);
        
        // Demonstrate successful transaction
        System.out.println("\n=== Successful Transaction ===");
        txnManager.beginTransaction("UserUpdate");
        
        Map<String, Object> updateData = new HashMap<>();
        updateData.put("age", 31);
        updateData.put("last_login", "2024-01-15");
        db.updateRecord("users", userId1, updateData, "admin");
        
        Map<String, Object> orderData = new HashMap<>();
        orderData.put("user_id", userId1);
        orderData.put("product", "Laptop");
        orderData.put("amount", 999.99);
        String orderId1 = db.insertRecord("orders", orderData, "admin");
        
        txnManager.commitTransaction();
        
        db.displayTableContents("users");
        db.displayTableContents("orders");
        
        Thread.sleep(1000);
        
        // Demonstrate transaction rollback
        System.out.println("\n=== Transaction with Rollback ===");
        txnManager.beginTransaction("FailedUpdate");
        
        // Make some changes
        updateData = new HashMap<>();
        updateData.put("email", "john.doe.new@example.com");
        updateData.put("status", "premium");
        db.updateRecord("users", userId1, updateData, "user1");
        
        orderData = new HashMap<>();
        orderData.put("user_id", userId2);
        orderData.put("product", "Phone");
        orderData.put("amount", 599.99);
        String orderId2 = db.insertRecord("orders", orderData, "user1");
        
        db.displayTableContents("users");
        db.displayTableContents("orders");
        
        // Simulate error condition and rollback
        System.out.println("Simulating error condition...");
        txnManager.rollbackTransaction();
        
        db.displayTableContents("users");
        db.displayTableContents("orders");
        
        Thread.sleep(1000);
        
        // Demonstrate savepoints
        System.out.println("\n=== Savepoint Demo ===");
        txnManager.beginTransaction("SavepointDemo");
        
        // First set of changes
        updateData = new HashMap<>();
        updateData.put("department", "Engineering");
        db.updateRecord("users", userId1, updateData, "admin");
        
        updateData = new HashMap<>();
        updateData.put("department", "Marketing");
        db.updateRecord("users", userId2, updateData, "admin");
        
        txnManager.createSavepoint("after_departments");
        db.displayTableContents("users");
        
        // Second set of changes
        updateData = new HashMap<>();
        updateData.put("salary", 75000);
        db.updateRecord("users", userId1, updateData, "admin");
        
        updateData = new HashMap<>();
        updateData.put("salary", 65000);
        db.updateRecord("users", userId2, updateData, "admin");
        
        txnManager.createSavepoint("after_salaries");
        db.displayTableContents("users");
        
        // Third set of changes (problematic)
        updateData = new HashMap<>();
        updateData.put("salary", -10000); // Invalid salary
        db.updateRecord("users", userId1, updateData, "admin");
        
        db.displayTableContents("users");
        
        // Rollback to savepoint
        System.out.println("Invalid salary detected, rolling back to savepoint...");
        txnManager.rollbackToSavepoint("after_departments");
        db.displayTableContents("users");
        
        txnManager.commitTransaction();
        
        Thread.sleep(1000);
        
        // Demonstrate batch operations
        System.out.println("\n=== Batch Operations ===");
        
        List<Runnable> successfulBatch = Arrays.asList(
            () -> {
                Map<String, Object> data = new HashMap<>();
                data.put("name", "Alice Johnson");
                data.put("email", "alice@example.com");
                data.put("age", 28);
                db.insertRecord("users", data, "admin");
            },
            () -> {
                Map<String, Object> data = new HashMap<>();
                data.put("name", "Bob Wilson");
                data.put("email", "bob@example.com");
                data.put("age", 35);
                db.insertRecord("users", data, "admin");
            },
            () -> {
                db.setSystemProperty("last_batch", "successful_batch");
            }
        );
        
        txnManager.executeBatch(successfulBatch, "AddNewUsers");
        db.displayTableContents("users");
        
        // Failing batch
        List<Runnable> failingBatch = Arrays.asList(
            () -> {
                Map<String, Object> data = new HashMap<>();
                data.put("name", "Charlie Brown");
                data.put("email", "charlie@example.com");
                data.put("age", 40);
                db.insertRecord("users", data, "admin");
            },
            () -> {
                // This will cause an exception
                throw new RuntimeException("Simulated database constraint violation");
            },
            () -> {
                db.setSystemProperty("last_batch", "failed_batch");
            }
        );
        
        System.out.println("\nAttempting batch that will fail...");
        txnManager.executeBatch(failingBatch, "FailingBatch");
        db.displayTableContents("users"); // Should not contain Charlie Brown
        
        Thread.sleep(1000);
        
        // Demonstrate historical rollback
        System.out.println("\n=== Historical Rollback ===");
        txnManager.displayTransactionHistory();
        
        // Make some more changes
        Map<String, Object> data = new HashMap<>();
        data.put("name", "David Lee");
        data.put("email", "david@example.com");
        data.put("age", 32);
        db.insertRecord("users", data, "admin");
        
        db.deleteRecord("users", userId2, "admin");
        db.setSystemProperty("maintenance_mode", "true");
        
        System.out.println("Current state after changes:");
        db.displayTableContents("users");
        db.displayDatabaseInfo();
        
        // Rollback to previous transaction
        System.out.println("Rolling back to previous transaction...");
        txnManager.rollbackToLatestTransaction();
        
        System.out.println("State after rollback:");
        db.displayTableContents("users");
        db.displayDatabaseInfo();
        
        // Final status
        System.out.println("\n=== Final Status ===");
        txnManager.displayTransactionStatus();
        txnManager.displayTransactionHistory();
        
        // Test transaction limits
        System.out.println("\n=== Testing Transaction Limits ===");
        TransactionManager limitedManager = new TransactionManager(db, 3);
        
        for (int i = 1; i <= 5; i++) {
            limitedManager.beginTransaction("Test_" + i);
            db.setSystemProperty("test_property_" + i, "value_" + i);
            limitedManager.commitTransaction();
        }
        
        limitedManager.displayTransactionHistory();
        
        // Cleanup
        System.out.println("\n=== Cleanup ===");
        txnManager.clearHistory();
        txnManager.displayTransactionHistory();
        
        System.out.println("\nDatabase Transaction System demonstration completed!");
    }
}

/*
Expected Output:
=== Database Transaction Memento Pattern Demo ===

Database system initialized
Table created: users
Table created: orders
User logged in: admin
User logged in: user1

=== Database Information ===
Tables: 2
  Table[users: 0 records]
  Table[orders: 0 records]
Active Users: [admin, user1]
System Properties: {version=1.0, charset=UTF-8, timezone=UTC}
Last Backup: [timestamp]
Transaction Counter: 0
Auto-commit: true
===============================

=== Initial Data Setup ===
Record inserted: users_1 by admin
Record inserted: users_2 by admin

=== Table: users ===
  Record[users_1: {name=John Doe, email=john@example.com, age=30}, modified by admin at [timestamp]]
  Record[users_2: {name=Jane Smith, email=jane@example.com, age=25}, modified by admin at [timestamp]]
Total records: 2
===========================

=== Successful Transaction ===
Transaction started: UserUpdate
Auto-commit disabled
Record updated: users_1 by admin
Record inserted: orders_1 by admin
Transaction committed: UserUpdate
Auto-commit enabled

=== Table: users ===
  Record[users_1: {name=John Doe, email=john@example.com, age=31, last_login=2024-01-15}, modified by admin at [timestamp]]
  Record[users_2: {name=Jane Smith, email=jane@example.com, age=25}, modified by admin at [timestamp]]
Total records: 2
===========================

=== Table: orders ===
  Record[orders_1: {user_id=users_1, product=Laptop, amount=999.99}, modified by admin at [timestamp]]
Total records: 1
===========================

=== Transaction with Rollback ===
Transaction started: FailedUpdate
Auto-commit disabled
Record updated: users_1 by user1
Record inserted: orders_2 by user1

=== Table: users ===
  Record[users_1: {name=John Doe, email=john.doe.new@example.com, age=31, last_login=2024-01-15, status=premium}, modified by user1 at [timestamp]]
  Record[users_2: {name=Jane Smith, email=jane@example.com, age=25}, modified by admin at [timestamp]]
Total records: 2
===========================

=== Table: orders ===
  Record[orders_1: {user_id=users_1, product=Laptop, amount=999.99}, modified by admin at [timestamp]]
  Record[orders_2: {user_id=users_2, product=Phone, amount=599.99}, modified by user1 at [timestamp]]
Total records: 2
===========================

Simulating error condition...
Database restored to snapshot from [timestamp] (Transaction ID: 1)
Transaction rolled back: FailedUpdate
Auto-commit enabled

=== Table: users ===
  Record[users_1: {name=John Doe, email=john@example.com, age=31, last_login=2024-01-15}, modified by admin at [timestamp]]
  Record[users_2: {name=Jane Smith, email=jane@example.com, age=25}, modified by admin at [timestamp]]
Total records: 2
===========================

=== Table: orders ===
  Record[orders_1: {user_id=users_1, product=Laptop, amount=999.99}, modified by admin at [timestamp]]
Total records: 1
===========================

=== Savepoint Demo ===
Transaction started: SavepointDemo
Auto-commit disabled
Record updated: users_1 by admin
Record updated: users_2 by admin
Savepoint created: after_departments

[Table contents and savepoint operations continue...]

=== Batch Operations ===
Transaction started: AddNewUsers
Auto-commit disabled
Executing batch: AddNewUsers (3 operations)
Executing operation 1/3
Record inserted: users_3 by admin
Executing operation 2/3
Record inserted: users_4 by admin
Executing operation 3/3
System property set: last_batch = successful_batch
Transaction committed: AddNewUsers
Auto-commit enabled
Batch completed successfully: AddNewUsers

[Additional demonstrations continue...]

Database Transaction System demonstration completed!
*/