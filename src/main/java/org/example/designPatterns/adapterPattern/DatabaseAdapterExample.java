package org.example.designPatterns.adapterPattern;

import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Demonstration of Adapter Pattern with Database Integration System
 * 
 * This example shows how to use the Adapter pattern to integrate
 * different database systems with incompatible interfaces into
 * a unified data access layer.
 */

// Target interface - what the application expects
interface DatabaseConnection {
    void connect();
    void disconnect();
    QueryResult executeQuery(String sql);
    int executeUpdate(String sql);
    boolean executeTransaction(List<String> sqlStatements);
    boolean isConnected();
    DatabaseInfo getInfo();
}

// Common classes used across adapters
class QueryResult {
    private List<Map<String, Object>> rows;
    private int rowCount;
    private long executionTime;
    
    public QueryResult(List<Map<String, Object>> rows, long executionTime) {
        this.rows = rows != null ? new ArrayList<>(rows) : new ArrayList<>();
        this.rowCount = this.rows.size();
        this.executionTime = executionTime;
    }
    
    public List<Map<String, Object>> getRows() { return new ArrayList<>(rows); }
    public int getRowCount() { return rowCount; }
    public long getExecutionTime() { return executionTime; }
    
    public Object getValue(int rowIndex, String column) {
        if (rowIndex >= 0 && rowIndex < rows.size()) {
            return rows.get(rowIndex).get(column);
        }
        return null;
    }
    
    @Override
    public String toString() {
        return String.format("QueryResult{rows=%d, executionTime=%dms}", rowCount, executionTime);
    }
}

class DatabaseInfo {
    private String type;
    private String version;
    private String host;
    private String database;
    private int maxConnections;
    
    public DatabaseInfo(String type, String version, String host, String database, int maxConnections) {
        this.type = type;
        this.version = version;
        this.host = host;
        this.database = database;
        this.maxConnections = maxConnections;
    }
    
    // Getters
    public String getType() { return type; }
    public String getVersion() { return version; }
    public String getHost() { return host; }
    public String getDatabase() { return database; }
    public int getMaxConnections() { return maxConnections; }
    
    @Override
    public String toString() {
        return String.format("%s %s on %s/%s (max connections: %d)", 
                           type, version, host, database, maxConnections);
    }
}

// Adaptee 1 - MySQL database library (legacy interface)
class MySQLDatabase {
    private String host;
    private int port;
    private String database;
    private String username;
    private boolean connected = false;
    
    public MySQLDatabase(String host, int port, String database, String username) {
        this.host = host;
        this.port = port;
        this.database = database;
        this.username = username;
    }
    
    public void openConnection() {
        System.out.println("MySQL: Opening connection to " + host + ":" + port + "/" + database);
        connected = true;
    }
    
    public void closeConnection() {
        System.out.println("MySQL: Closing connection");
        connected = false;
    }
    
    public MySQLResultSet runSelectQuery(String query) {
        if (!connected) {
            throw new IllegalStateException("MySQL connection not open");
        }
        
        System.out.println("MySQL: Executing SELECT query: " + query);
        
        // Simulate query execution
        long startTime = System.currentTimeMillis();
        List<MySQLRow> rows = new ArrayList<>();
        
        // Simulate some data
        MySQLRow row1 = new MySQLRow();
        row1.addColumn("id", 1);
        row1.addColumn("name", "John Doe");
        row1.addColumn("email", "john@example.com");
        rows.add(row1);
        
        MySQLRow row2 = new MySQLRow();
        row2.addColumn("id", 2);
        row2.addColumn("name", "Jane Smith");
        row2.addColumn("email", "jane@example.com");
        rows.add(row2);
        
        long executionTime = System.currentTimeMillis() - startTime;
        return new MySQLResultSet(rows, executionTime);
    }
    
    public int runUpdateQuery(String query) {
        if (!connected) {
            throw new IllegalStateException("MySQL connection not open");
        }
        
        System.out.println("MySQL: Executing UPDATE query: " + query);
        return 2; // Simulate affected rows
    }
    
    public boolean runBatchQuery(String[] queries) {
        if (!connected) {
            throw new IllegalStateException("MySQL connection not open");
        }
        
        System.out.println("MySQL: Executing batch transaction with " + queries.length + " queries");
        try {
            for (String query : queries) {
                System.out.println("  - " + query);
            }
            return true;
        } catch (Exception e) {
            System.out.println("MySQL: Batch execution failed: " + e.getMessage());
            return false;
        }
    }
    
    public String getServerInfo() {
        return "MySQL Server version: 8.0.33, Host: " + host + ", Database: " + database;
    }
    
    public boolean isActive() { return connected; }
}

// MySQL specific classes
class MySQLResultSet {
    private List<MySQLRow> rows;
    private long executionTime;
    
    public MySQLResultSet(List<MySQLRow> rows, long executionTime) {
        this.rows = rows;
        this.executionTime = executionTime;
    }
    
    public List<MySQLRow> getRows() { return rows; }
    public long getExecutionTime() { return executionTime; }
    public int getRowCount() { return rows.size(); }
}

class MySQLRow {
    private Map<String, Object> columns = new HashMap<>();
    
    public void addColumn(String name, Object value) {
        columns.put(name, value);
    }
    
    public Object getColumn(String name) {
        return columns.get(name);
    }
    
    public Map<String, Object> getAllColumns() {
        return new HashMap<>(columns);
    }
}

// Adaptee 2 - PostgreSQL database library (different interface)
class PostgreSQLConnector {
    private String connectionString;
    private boolean established = false;
    
    public PostgreSQLConnector(String connectionString) {
        this.connectionString = connectionString;
    }
    
    public void establish() {
        System.out.println("PostgreSQL: Establishing connection - " + connectionString);
        established = true;
    }
    
    public void terminate() {
        System.out.println("PostgreSQL: Terminating connection");
        established = false;
    }
    
    public PostgreSQLQueryResponse query(String sql) {
        if (!established) {
            throw new IllegalStateException("PostgreSQL connection not established");
        }
        
        System.out.println("PostgreSQL: Querying - " + sql);
        
        long startTime = System.nanoTime();
        Map<String, List<Object>> columnData = new HashMap<>();
        
        // Simulate data
        columnData.put("id", Arrays.asList(10, 20, 30));
        columnData.put("title", Arrays.asList("Product A", "Product B", "Product C"));
        columnData.put("price", Arrays.asList(19.99, 29.99, 39.99));
        
        long executionTime = (System.nanoTime() - startTime) / 1_000_000; // Convert to milliseconds
        
        return new PostgreSQLQueryResponse(columnData, executionTime);
    }
    
    public int execute(String sql) {
        if (!established) {
            throw new IllegalStateException("PostgreSQL connection not established");
        }
        
        System.out.println("PostgreSQL: Executing - " + sql);
        return 3; // Simulate affected rows
    }
    
    public boolean executeTransaction(PostgreSQLTransaction transaction) {
        if (!established) {
            throw new IllegalStateException("PostgreSQL connection not established");
        }
        
        System.out.println("PostgreSQL: Executing transaction with " + transaction.getStatements().size() + " statements");
        try {
            for (String statement : transaction.getStatements()) {
                System.out.println("  - " + statement);
            }
            return true;
        } catch (Exception e) {
            System.out.println("PostgreSQL: Transaction failed: " + e.getMessage());
            return false;
        }
    }
    
    public PostgreSQLServerInfo getServerInfo() {
        return new PostgreSQLServerInfo("PostgreSQL", "14.9", connectionString);
    }
    
    public boolean isEstablished() { return established; }
}

// PostgreSQL specific classes
class PostgreSQLQueryResponse {
    private Map<String, List<Object>> columnData;
    private long executionTimeMs;
    
    public PostgreSQLQueryResponse(Map<String, List<Object>> columnData, long executionTimeMs) {
        this.columnData = columnData;
        this.executionTimeMs = executionTimeMs;
    }
    
    public Map<String, List<Object>> getColumnData() { return columnData; }
    public long getExecutionTimeMs() { return executionTimeMs; }
    
    public int getRowCount() {
        return columnData.isEmpty() ? 0 : columnData.values().iterator().next().size();
    }
}

class PostgreSQLTransaction {
    private List<String> statements = new ArrayList<>();
    
    public void addStatement(String sql) {
        statements.add(sql);
    }
    
    public List<String> getStatements() { return statements; }
}

class PostgreSQLServerInfo {
    private String type;
    private String version;
    private String connectionString;
    
    public PostgreSQLServerInfo(String type, String version, String connectionString) {
        this.type = type;
        this.version = version;
        this.connectionString = connectionString;
    }
    
    public String getType() { return type; }
    public String getVersion() { return version; }
    public String getConnectionString() { return connectionString; }
}

// Adaptee 3 - MongoDB database library (document-based, very different interface)
class MongoDBClient {
    private String databaseName;
    private boolean connected = false;
    
    public MongoDBClient(String databaseName) {
        this.databaseName = databaseName;
    }
    
    public void connect() {
        System.out.println("MongoDB: Connecting to database - " + databaseName);
        connected = true;
    }
    
    public void disconnect() {
        System.out.println("MongoDB: Disconnecting from database");
        connected = false;
    }
    
    public MongoResult find(String collection, String filter) {
        if (!connected) {
            throw new IllegalStateException("MongoDB not connected");
        }
        
        System.out.println("MongoDB: Finding documents in " + collection + " with filter: " + filter);
        
        long startTime = System.currentTimeMillis();
        List<MongoDocument> documents = new ArrayList<>();
        
        // Simulate documents
        MongoDocument doc1 = new MongoDocument();
        doc1.put("_id", "507f1f77bcf86cd799439011");
        doc1.put("name", "Alice Johnson");
        doc1.put("age", 30);
        documents.add(doc1);
        
        MongoDocument doc2 = new MongoDocument();
        doc2.put("_id", "507f1f77bcf86cd799439012");
        doc2.put("name", "Bob Wilson");
        doc2.put("age", 25);
        documents.add(doc2);
        
        long executionTime = System.currentTimeMillis() - startTime;
        return new MongoResult(documents, executionTime);
    }
    
    public int updateMany(String collection, String filter, String update) {
        if (!connected) {
            throw new IllegalStateException("MongoDB not connected");
        }
        
        System.out.println("MongoDB: Updating documents in " + collection + 
                          " with filter: " + filter + " and update: " + update);
        return 2; // Simulate modified count
    }
    
    public boolean runTransaction(List<String> operations) {
        if (!connected) {
            throw new IllegalStateException("MongoDB not connected");
        }
        
        System.out.println("MongoDB: Running transaction with " + operations.size() + " operations");
        try {
            for (String operation : operations) {
                System.out.println("  - " + operation);
            }
            return true;
        } catch (Exception e) {
            System.out.println("MongoDB: Transaction failed: " + e.getMessage());
            return false;
        }
    }
    
    public MongoServerInfo getServerInfo() {
        return new MongoServerInfo("MongoDB", "6.0", databaseName);
    }
    
    public boolean isConnected() { return connected; }
}

// MongoDB specific classes
class MongoResult {
    private List<MongoDocument> documents;
    private long executionTime;
    
    public MongoResult(List<MongoDocument> documents, long executionTime) {
        this.documents = documents;
        this.executionTime = executionTime;
    }
    
    public List<MongoDocument> getDocuments() { return documents; }
    public long getExecutionTime() { return executionTime; }
    public int getCount() { return documents.size(); }
}

class MongoDocument {
    private Map<String, Object> fields = new HashMap<>();
    
    public void put(String key, Object value) {
        fields.put(key, value);
    }
    
    public Object get(String key) {
        return fields.get(key);
    }
    
    public Map<String, Object> toMap() {
        return new HashMap<>(fields);
    }
}

class MongoServerInfo {
    private String type;
    private String version;
    private String database;
    
    public MongoServerInfo(String type, String version, String database) {
        this.type = type;
        this.version = version;
        this.database = database;
    }
    
    public String getType() { return type; }
    public String getVersion() { return version; }
    public String getDatabase() { return database; }
}

// Adapter for MySQL
class MySQLAdapter implements DatabaseConnection {
    private MySQLDatabase mysqlDb;
    
    public MySQLAdapter(String host, int port, String database, String username) {
        this.mysqlDb = new MySQLDatabase(host, port, database, username);
    }
    
    @Override
    public void connect() {
        mysqlDb.openConnection();
    }
    
    @Override
    public void disconnect() {
        mysqlDb.closeConnection();
    }
    
    @Override
    public QueryResult executeQuery(String sql) {
        MySQLResultSet result = mysqlDb.runSelectQuery(sql);
        
        // Convert MySQL result to standard format
        List<Map<String, Object>> rows = new ArrayList<>();
        for (MySQLRow mysqlRow : result.getRows()) {
            rows.add(mysqlRow.getAllColumns());
        }
        
        return new QueryResult(rows, result.getExecutionTime());
    }
    
    @Override
    public int executeUpdate(String sql) {
        return mysqlDb.runUpdateQuery(sql);
    }
    
    @Override
    public boolean executeTransaction(List<String> sqlStatements) {
        String[] queries = sqlStatements.toArray(new String[0]);
        return mysqlDb.runBatchQuery(queries);
    }
    
    @Override
    public boolean isConnected() {
        return mysqlDb.isActive();
    }
    
    @Override
    public DatabaseInfo getInfo() {
        String serverInfo = mysqlDb.getServerInfo();
        // Parse server info to extract details
        return new DatabaseInfo("MySQL", "8.0.33", "localhost", "testdb", 100);
    }
}

// Adapter for PostgreSQL
class PostgreSQLAdapter implements DatabaseConnection {
    private PostgreSQLConnector pgConnector;
    
    public PostgreSQLAdapter(String connectionString) {
        this.pgConnector = new PostgreSQLConnector(connectionString);
    }
    
    @Override
    public void connect() {
        pgConnector.establish();
    }
    
    @Override
    public void disconnect() {
        pgConnector.terminate();
    }
    
    @Override
    public QueryResult executeQuery(String sql) {
        PostgreSQLQueryResponse response = pgConnector.query(sql);
        
        // Convert PostgreSQL column-based result to row-based format
        List<Map<String, Object>> rows = new ArrayList<>();
        Map<String, List<Object>> columnData = response.getColumnData();
        
        if (!columnData.isEmpty()) {
            int rowCount = response.getRowCount();
            for (int i = 0; i < rowCount; i++) {
                Map<String, Object> row = new HashMap<>();
                for (Map.Entry<String, List<Object>> column : columnData.entrySet()) {
                    row.put(column.getKey(), column.getValue().get(i));
                }
                rows.add(row);
            }
        }
        
        return new QueryResult(rows, response.getExecutionTimeMs());
    }
    
    @Override
    public int executeUpdate(String sql) {
        return pgConnector.execute(sql);
    }
    
    @Override
    public boolean executeTransaction(List<String> sqlStatements) {
        PostgreSQLTransaction transaction = new PostgreSQLTransaction();
        for (String statement : sqlStatements) {
            transaction.addStatement(statement);
        }
        return pgConnector.executeTransaction(transaction);
    }
    
    @Override
    public boolean isConnected() {
        return pgConnector.isEstablished();
    }
    
    @Override
    public DatabaseInfo getInfo() {
        PostgreSQLServerInfo serverInfo = pgConnector.getServerInfo();
        return new DatabaseInfo(serverInfo.getType(), serverInfo.getVersion(), 
                               "localhost", "testdb", 200);
    }
}

// Adapter for MongoDB (translating document queries to SQL-like interface)
class MongoDBAdapter implements DatabaseConnection {
    private MongoDBClient mongoClient;
    private String defaultCollection;
    
    public MongoDBAdapter(String databaseName, String defaultCollection) {
        this.mongoClient = new MongoDBClient(databaseName);
        this.defaultCollection = defaultCollection;
    }
    
    @Override
    public void connect() {
        mongoClient.connect();
    }
    
    @Override
    public void disconnect() {
        mongoClient.disconnect();
    }
    
    @Override
    public QueryResult executeQuery(String sql) {
        // Simple SQL to MongoDB translation (very basic)
        String filter = translateSQLToMongoFilter(sql);
        MongoResult result = mongoClient.find(defaultCollection, filter);
        
        // Convert MongoDB documents to row format
        List<Map<String, Object>> rows = new ArrayList<>();
        for (MongoDocument doc : result.getDocuments()) {
            rows.add(doc.toMap());
        }
        
        return new QueryResult(rows, result.getExecutionTime());
    }
    
    @Override
    public int executeUpdate(String sql) {
        // Translate SQL UPDATE to MongoDB update
        String[] parts = translateSQLToMongoUpdate(sql);
        String filter = parts[0];
        String update = parts[1];
        
        return mongoClient.updateMany(defaultCollection, filter, update);
    }
    
    @Override
    public boolean executeTransaction(List<String> sqlStatements) {
        // Convert SQL statements to MongoDB operations
        List<String> operations = new ArrayList<>();
        for (String sql : sqlStatements) {
            operations.add("Mongo operation for: " + sql);
        }
        
        return mongoClient.runTransaction(operations);
    }
    
    @Override
    public boolean isConnected() {
        return mongoClient.isConnected();
    }
    
    @Override
    public DatabaseInfo getInfo() {
        MongoServerInfo serverInfo = mongoClient.getServerInfo();
        return new DatabaseInfo(serverInfo.getType(), serverInfo.getVersion(),
                               "localhost", serverInfo.getDatabase(), 1000);
    }
    
    private String translateSQLToMongoFilter(String sql) {
        // Very basic SQL to MongoDB filter translation
        if (sql.toLowerCase().contains("where")) {
            String condition = sql.toLowerCase().split("where")[1].trim();
            return "{" + condition.replace("=", ":") + "}";
        }
        return "{}";
    }
    
    private String[] translateSQLToMongoUpdate(String sql) {
        // Basic translation - in real implementation this would be much more sophisticated
        return new String[]{"{}", "{$set: {updated: true}}"};
    }
}

// Factory for creating database adapters
class DatabaseAdapterFactory {
    public static DatabaseConnection createConnection(String type, String connectionString) {
        switch (type.toLowerCase()) {
            case "mysql":
                // Parse MySQL connection string: mysql://host:port/database?user=username
                return parseMySQLConnection(connectionString);
            case "postgresql":
                return new PostgreSQLAdapter(connectionString);
            case "mongodb":
                // Parse MongoDB connection string: mongodb://host:port/database?collection=collectionName
                return parseMongoConnection(connectionString);
            default:
                throw new IllegalArgumentException("Unsupported database type: " + type);
        }
    }
    
    private static MySQLAdapter parseMySQLConnection(String connectionString) {
        // Simple parsing - in real implementation this would be more robust
        try {
            String cleaned = connectionString.replace("mysql://", "");
            String[] parts = cleaned.split("/");
            String[] hostPort = parts[0].split(":");
            String host = hostPort[0];
            int port = hostPort.length > 1 ? Integer.parseInt(hostPort[1]) : 3306;
            
            String[] dbAndParams = parts[1].split("\\?");
            String database = dbAndParams[0];
            String username = "root"; // Default
            
            if (dbAndParams.length > 1) {
                String[] params = dbAndParams[1].split("&");
                for (String param : params) {
                    String[] keyValue = param.split("=");
                    if ("user".equals(keyValue[0]) && keyValue.length > 1) {
                        username = keyValue[1];
                    }
                }
            }
            
            return new MySQLAdapter(host, port, database, username);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid MySQL connection string: " + connectionString, e);
        }
    }
    
    private static MongoDBAdapter parseMongoConnection(String connectionString) {
        // Simple parsing for MongoDB
        try {
            String cleaned = connectionString.replace("mongodb://", "");
            String[] parts = cleaned.split("/");
            String[] dbAndParams = parts[1].split("\\?");
            String database = dbAndParams[0];
            String collection = "documents"; // Default
            
            if (dbAndParams.length > 1) {
                String[] params = dbAndParams[1].split("&");
                for (String param : params) {
                    String[] keyValue = param.split("=");
                    if ("collection".equals(keyValue[0]) && keyValue.length > 1) {
                        collection = keyValue[1];
                    }
                }
            }
            
            return new MongoDBAdapter(database, collection);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid MongoDB connection string: " + connectionString, e);
        }
    }
}

// Database service that uses the common interface
class UnifiedDataService {
    private Map<String, DatabaseConnection> connections;
    
    public UnifiedDataService() {
        this.connections = new HashMap<>();
    }
    
    public void addDatabase(String name, DatabaseConnection connection) {
        connections.put(name, connection);
        connection.connect();
        System.out.println("Added database connection: " + name + " - " + connection.getInfo());
    }
    
    public QueryResult queryAllDatabases(String sql) {
        System.out.println("\n=== Querying All Databases ===");
        List<Map<String, Object>> allResults = new ArrayList<>();
        long totalTime = 0;
        
        for (Map.Entry<String, DatabaseConnection> entry : connections.entrySet()) {
            String dbName = entry.getKey();
            DatabaseConnection db = entry.getValue();
            
            if (db.isConnected()) {
                try {
                    System.out.println("Querying " + dbName + "...");
                    QueryResult result = db.executeQuery(sql);
                    
                    // Add database source to each row
                    for (Map<String, Object> row : result.getRows()) {
                        row.put("_source_db", dbName);
                        allResults.add(row);
                    }
                    
                    totalTime += result.getExecutionTime();
                    System.out.println(dbName + " returned " + result.getRowCount() + " rows");
                    
                } catch (Exception e) {
                    System.out.println("Error querying " + dbName + ": " + e.getMessage());
                }
            } else {
                System.out.println(dbName + " is not connected");
            }
        }
        
        System.out.println("Total results: " + allResults.size() + " rows from " + connections.size() + " databases");
        return new QueryResult(allResults, totalTime);
    }
    
    public void executeTransactionAcrossAllDatabases(List<String> statements) {
        System.out.println("\n=== Executing Transaction Across All Databases ===");
        
        for (Map.Entry<String, DatabaseConnection> entry : connections.entrySet()) {
            String dbName = entry.getKey();
            DatabaseConnection db = entry.getValue();
            
            if (db.isConnected()) {
                try {
                    System.out.println("Executing transaction on " + dbName + "...");
                    boolean success = db.executeTransaction(statements);
                    System.out.println(dbName + " transaction: " + (success ? "SUCCESS" : "FAILED"));
                } catch (Exception e) {
                    System.out.println("Transaction failed on " + dbName + ": " + e.getMessage());
                }
            }
        }
    }
    
    public void printDatabaseInfo() {
        System.out.println("\n=== Database Information ===");
        for (Map.Entry<String, DatabaseConnection> entry : connections.entrySet()) {
            String name = entry.getKey();
            DatabaseConnection db = entry.getValue();
            DatabaseInfo info = db.getInfo();
            System.out.println(name + ": " + info + " (Connected: " + db.isConnected() + ")");
        }
    }
    
    public void disconnectAll() {
        System.out.println("\n=== Disconnecting All Databases ===");
        for (Map.Entry<String, DatabaseConnection> entry : connections.entrySet()) {
            try {
                entry.getValue().disconnect();
                System.out.println("Disconnected: " + entry.getKey());
            } catch (Exception e) {
                System.out.println("Error disconnecting " + entry.getKey() + ": " + e.getMessage());
            }
        }
        connections.clear();
    }
}

// Demonstration class
public class DatabaseAdapterExample {
    
    public static void main(String[] args) {
        System.out.println("=== Adapter Pattern Demonstration - Database Integration System ===\n");
        
        // Example 1: Basic adapter usage
        System.out.println("1. Basic Database Adapter Usage:");
        
        try {
            DatabaseConnection mysqlDb = DatabaseAdapterFactory.createConnection(
                "mysql", "mysql://localhost:3306/testdb?user=admin");
            
            mysqlDb.connect();
            System.out.println("MySQL Info: " + mysqlDb.getInfo());
            
            QueryResult result = mysqlDb.executeQuery("SELECT * FROM users");
            System.out.println("MySQL Query Result: " + result);
            
            int updated = mysqlDb.executeUpdate("UPDATE users SET last_login = NOW()");
            System.out.println("MySQL Update Result: " + updated + " rows affected");
            
            mysqlDb.disconnect();
            
        } catch (Exception e) {
            System.out.println("Error with MySQL adapter: " + e.getMessage());
        }
        System.out.println();
        
        // Example 2: Multiple database adapters
        System.out.println("2. Multiple Database Adapters:");
        
        UnifiedDataService dataService = new UnifiedDataService();
        
        // Add different database connections
        try {
            DatabaseConnection mysql = DatabaseAdapterFactory.createConnection(
                "mysql", "mysql://localhost:3306/app_db?user=app_user");
            
            DatabaseConnection postgres = DatabaseAdapterFactory.createConnection(
                "postgresql", "postgresql://localhost:5432/analytics_db?user=analytics_user");
            
            DatabaseConnection mongo = DatabaseAdapterFactory.createConnection(
                "mongodb", "mongodb://localhost:27017/logs_db?collection=access_logs");
            
            dataService.addDatabase("MySQL-App", mysql);
            dataService.addDatabase("PostgreSQL-Analytics", postgres);
            dataService.addDatabase("MongoDB-Logs", mongo);
            
        } catch (Exception e) {
            System.out.println("Error creating database connections: " + e.getMessage());
        }
        
        dataService.printDatabaseInfo();
        System.out.println();
        
        // Example 3: Unified querying across different databases
        System.out.println("3. Unified Querying:");
        QueryResult combinedResults = dataService.queryAllDatabases("SELECT * FROM users LIMIT 5");
        System.out.println("Combined results: " + combinedResults.getRowCount() + " rows");
        
        // Display some results
        for (int i = 0; i < Math.min(3, combinedResults.getRowCount()); i++) {
            Map<String, Object> row = combinedResults.getRows().get(i);
            System.out.println("Row " + i + " from " + row.get("_source_db") + ": " + row);
        }
        System.out.println();
        
        // Example 4: Transaction across multiple databases
        System.out.println("4. Transaction Execution:");
        List<String> transactionStatements = Arrays.asList(
            "INSERT INTO audit_log (action, timestamp) VALUES ('user_update', NOW())",
            "UPDATE statistics SET update_count = update_count + 1",
            "INSERT INTO notifications (message) VALUES ('Data updated')"
        );
        
        dataService.executeTransactionAcrossAllDatabases(transactionStatements);
        System.out.println();
        
        // Example 5: Error handling with invalid connection
        System.out.println("5. Error Handling:");
        try {
            DatabaseConnection invalidDb = DatabaseAdapterFactory.createConnection(
                "unsupported", "invalid://connection:string");
        } catch (IllegalArgumentException e) {
            System.out.println("Caught expected error: " + e.getMessage());
        }
        
        try {
            DatabaseConnection invalidMysql = DatabaseAdapterFactory.createConnection(
                "mysql", "invalid-connection-string");
        } catch (IllegalArgumentException e) {
            System.out.println("Caught MySQL parsing error: " + e.getMessage());
        }
        System.out.println();
        
        // Example 6: Performance comparison
        System.out.println("6. Performance Comparison:");
        performanceTest();
        System.out.println();
        
        // Clean up
        dataService.disconnectAll();
        
        System.out.println("=== Demonstration Complete ===");
    }
    
    private static void performanceTest() {
        System.out.println("Running performance test across database types...");
        
        String[] dbTypes = {"mysql", "postgresql", "mongodb"};
        String[] connectionStrings = {
            "mysql://localhost:3306/perf_test?user=perf_user",
            "postgresql://localhost:5432/perf_test?user=perf_user",  
            "mongodb://localhost:27017/perf_test?collection=perf_data"
        };
        
        for (int i = 0; i < dbTypes.length; i++) {
            try {
                DatabaseConnection db = DatabaseAdapterFactory.createConnection(dbTypes[i], connectionStrings[i]);
                db.connect();
                
                long startTime = System.nanoTime();
                QueryResult result = db.executeQuery("SELECT * FROM test_table LIMIT 100");
                long endTime = System.nanoTime();
                
                long totalTime = (endTime - startTime) / 1_000_000; // Convert to milliseconds
                System.out.printf("%s: %d rows, %dms total (%dms reported)%n", 
                                dbTypes[i].toUpperCase(), 
                                result.getRowCount(), 
                                totalTime,
                                result.getExecutionTime());
                
                db.disconnect();
                
            } catch (Exception e) {
                System.out.println(dbTypes[i].toUpperCase() + " performance test failed: " + e.getMessage());
            }
        }
    }
}