# Bridge Design Pattern - Complete Guide

## Table of Contents
1. [Introduction](#introduction)
2. [Core Concepts](#core-concepts)
3. [When to Use Bridge Pattern](#when-to-use-bridge-pattern)
4. [Structure and Components](#structure-and-components)
5. [Implementation Steps](#implementation-steps)
6. [Detailed Examples](#detailed-examples)
7. [Real-World Applications](#real-world-applications)
8. [Advantages and Disadvantages](#advantages-and-disadvantages)
9. [Bridge vs Other Patterns](#bridge-vs-other-patterns)
10. [Best Practices](#best-practices)

## Introduction

The **Bridge Pattern** is a structural design pattern that decouples an abstraction from its implementation so that both can vary independently. It uses composition instead of inheritance to connect abstractions and implementations.

### Definition
> "Decouple an abstraction from its implementation so that the two can vary independently." - Gang of Four

### Problem It Solves
Imagine you have a class hierarchy where you need to support multiple platforms or implementations. Without the Bridge Pattern, you might face:
- Exponential class explosion (Cartesian product problem)
- Tight coupling between abstraction and implementation
- Difficulty in extending either abstraction or implementation independently
- Code duplication across similar implementations
- Violation of Single Responsibility Principle

## Core Concepts

### Key Principles
1. **Separation of Concerns**: Abstractions and implementations are separated
2. **Composition over Inheritance**: Uses composition to bridge abstraction and implementation
3. **Independent Variation**: Both sides can evolve independently
4. **Runtime Binding**: Implementation can be chosen at runtime

### Real-World Analogy
Think of a universal TV remote control:
- The **remote** is the abstraction (interface for controlling TVs)
- Different **TV brands** (Sony, Samsung, LG) are implementations
- The remote can work with any TV through a standard interface
- You can add new TV brands without changing the remote
- You can create different types of remotes (voice, app-based) without changing TV implementations

## When to Use Bridge Pattern

Use the Bridge Pattern when:
1. **You want to avoid permanent binding** between an abstraction and its implementation
2. **Both abstractions and implementations** should be extensible through inheritance
3. **Implementation changes** should not affect client code
4. **You need to share implementation** among multiple objects
5. **You have a hierarchy** that needs to work across multiple platforms
6. **Runtime implementation selection** is required

### Red Flags Indicating Need for Bridge Pattern
- Class explosion in inheritance hierarchy
- Need to support multiple platforms/databases/UI frameworks
- Switching between implementations at runtime
- Implementation details leaking to abstractions
- Duplicate code across similar implementations

## Structure and Components

### UML Diagram Structure
```
┌─────────────────────┐           ┌──────────────────────┐
│    Abstraction      │ ◇────────>│   Implementation     │
├─────────────────────┤           │   <<interface>>      │
│ - implementation    │           ├──────────────────────┤
├─────────────────────┤           │ + operationImpl()    │
│ + operation()       │           └──────────────────────┘
└─────────────────────┘                      △
           △                                 │
           │                        ┌───────┴───────┬─────────────┐
┌─────────────────────┐             │               │             │
│ RefinedAbstraction  │    ┌────────────────┐ ┌───────────────┐ ┌──────────────┐
├─────────────────────┤    │ ConcreteImplA  │ │ ConcreteImplB │ │ConcreteImplC │
│ + refinedOperation()│    ├────────────────┤ ├───────────────┤ ├──────────────┤
└─────────────────────┘    │+ operationImpl()│ │+operationImpl()│ │+operationImpl()│
                           └────────────────┘ └───────────────┘ └──────────────┘
```

### Components

1. **Abstraction**: Defines the abstraction's interface and maintains a reference to an Implementation object
2. **Refined Abstraction**: Extends the interface defined by Abstraction
3. **Implementation**: Defines the interface for implementation classes
4. **Concrete Implementation**: Implements the Implementation interface

## Implementation Steps

1. **Identify the abstraction** and implementation hierarchies
2. **Define the Implementation interface** with primitive operations
3. **Create Concrete Implementations** of the interface
4. **Create Abstraction class** that holds reference to Implementation
5. **Create Refined Abstractions** that extend the base abstraction
6. **Client code** creates abstraction with desired implementation

## Detailed Examples

### Example 1: Cross-Platform GUI Components

```java
// Implementation interface
public interface UIRenderer {
    void renderButton(String text, int x, int y, int width, int height);
    void renderTextBox(String text, int x, int y, int width, int height);
    void renderLabel(String text, int x, int y);
    void renderWindow(String title, int width, int height);
}

// Concrete Implementations
public class WindowsRenderer implements UIRenderer {
    @Override
    public void renderButton(String text, int x, int y, int width, int height) {
        System.out.println("Rendering Windows-style button:");
        System.out.println("  Text: " + text);
        System.out.println("  Position: (" + x + ", " + y + ")");
        System.out.println("  Size: " + width + "x" + height);
        System.out.println("  Style: Windows 11 flat design with rounded corners");
    }
    
    @Override
    public void renderTextBox(String text, int x, int y, int width, int height) {
        System.out.println("Rendering Windows TextBox:");
        System.out.println("  Content: " + text);
        System.out.println("  Position: (" + x + ", " + y + ")");
        System.out.println("  Style: Windows native textbox with system font");
    }
    
    @Override
    public void renderLabel(String text, int x, int y) {
        System.out.println("Rendering Windows Label: '" + text + "' at (" + x + ", " + y + ")");
        System.out.println("  Font: Segoe UI");
    }
    
    @Override
    public void renderWindow(String title, int width, int height) {
        System.out.println("Creating Windows window: " + title);
        System.out.println("  Size: " + width + "x" + height);
        System.out.println("  Features: Title bar, minimize/maximize/close buttons");
    }
}

public class MacOSRenderer implements UIRenderer {
    @Override
    public void renderButton(String text, int x, int y, int width, int height) {
        System.out.println("Rendering macOS-style button:");
        System.out.println("  Text: " + text);
        System.out.println("  Position: (" + x + ", " + y + ")");
        System.out.println("  Size: " + width + "x" + height);
        System.out.println("  Style: macOS Big Sur design with subtle shadows");
    }
    
    @Override
    public void renderTextBox(String text, int x, int y, int width, int height) {
        System.out.println("Rendering macOS TextBox:");
        System.out.println("  Content: " + text);
        System.out.println("  Position: (" + x + ", " + y + ")");
        System.out.println("  Style: macOS native textfield with system font");
    }
    
    @Override
    public void renderLabel(String text, int x, int y) {
        System.out.println("Rendering macOS Label: '" + text + "' at (" + x + ", " + y + ")");
        System.out.println("  Font: San Francisco");
    }
    
    @Override
    public void renderWindow(String title, int width, int height) {
        System.out.println("Creating macOS window: " + title);
        System.out.println("  Size: " + width + "x" + height);
        System.out.println("  Features: Traffic light buttons (red/yellow/green)");
    }
}

public class LinuxRenderer implements UIRenderer {
    @Override
    public void renderButton(String text, int x, int y, int width, int height) {
        System.out.println("Rendering Linux GTK button:");
        System.out.println("  Text: " + text);
        System.out.println("  Position: (" + x + ", " + y + ")");
        System.out.println("  Size: " + width + "x" + height);
        System.out.println("  Style: GTK3 Adwaita theme");
    }
    
    @Override
    public void renderTextBox(String text, int x, int y, int width, int height) {
        System.out.println("Rendering Linux GTK TextBox:");
        System.out.println("  Content: " + text);
        System.out.println("  Position: (" + x + ", " + y + ")");
        System.out.println("  Style: GTK Entry widget with system theme");
    }
    
    @Override
    public void renderLabel(String text, int x, int y) {
        System.out.println("Rendering Linux GTK Label: '" + text + "' at (" + x + ", " + y + ")");
        System.out.println("  Font: Liberation Sans");
    }
    
    @Override
    public void renderWindow(String title, int width, int height) {
        System.out.println("Creating Linux GTK window: " + title);
        System.out.println("  Size: " + width + "x" + height);
        System.out.println("  Features: GTK window decorations");
    }
}

// Abstraction
public abstract class UIComponent {
    protected UIRenderer renderer;
    protected int x, y, width, height;
    
    public UIComponent(UIRenderer renderer) {
        this.renderer = renderer;
    }
    
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }
    
    public abstract void render();
    
    public void setRenderer(UIRenderer renderer) {
        this.renderer = renderer;
        System.out.println("Switched to " + renderer.getClass().getSimpleName());
    }
}

// Refined Abstractions
public class Button extends UIComponent {
    private String text;
    private boolean isPressed;
    
    public Button(UIRenderer renderer, String text) {
        super(renderer);
        this.text = text;
        this.isPressed = false;
    }
    
    @Override
    public void render() {
        System.out.println("\n=== Rendering Button ===");
        renderer.renderButton(text, x, y, width, height);
        if (isPressed) {
            System.out.println("  State: Pressed");
        }
    }
    
    public void click() {
        isPressed = true;
        System.out.println("Button '" + text + "' clicked!");
        render();
        isPressed = false;
    }
    
    public void setText(String text) {
        this.text = text;
    }
    
    public String getText() {
        return text;
    }
}

public class TextBox extends UIComponent {
    private String content;
    private boolean isFocused;
    
    public TextBox(UIRenderer renderer, String initialText) {
        super(renderer);
        this.content = initialText;
        this.isFocused = false;
    }
    
    @Override
    public void render() {
        System.out.println("\n=== Rendering TextBox ===");
        renderer.renderTextBox(content, x, y, width, height);
        if (isFocused) {
            System.out.println("  State: Focused (cursor visible)");
        }
    }
    
    public void focus() {
        isFocused = true;
        System.out.println("TextBox focused");
        render();
    }
    
    public void blur() {
        isFocused = false;
        System.out.println("TextBox lost focus");
    }
    
    public void setText(String content) {
        this.content = content;
        render();
    }
    
    public String getText() {
        return content;
    }
}

public class Label extends UIComponent {
    private String text;
    private String fontFamily;
    
    public Label(UIRenderer renderer, String text) {
        super(renderer);
        this.text = text;
    }
    
    @Override
    public void render() {
        System.out.println("\n=== Rendering Label ===");
        renderer.renderLabel(text, x, y);
        if (fontFamily != null) {
            System.out.println("  Custom font: " + fontFamily);
        }
    }
    
    public void setText(String text) {
        this.text = text;
    }
    
    public void setFontFamily(String fontFamily) {
        this.fontFamily = fontFamily;
    }
    
    public String getText() {
        return text;
    }
}

// Complex Abstraction
public class Window {
    private UIRenderer renderer;
    private String title;
    private int width, height;
    private List<UIComponent> components;
    private boolean isVisible;
    
    public Window(UIRenderer renderer, String title, int width, int height) {
        this.renderer = renderer;
        this.title = title;
        this.width = width;
        this.height = height;
        this.components = new ArrayList<>();
        this.isVisible = false;
    }
    
    public void show() {
        isVisible = true;
        System.out.println("\n=== Showing Window ===");
        renderer.renderWindow(title, width, height);
        
        System.out.println("\nRendering child components:");
        for (UIComponent component : components) {
            component.render();
        }
    }
    
    public void hide() {
        isVisible = false;
        System.out.println("Window '" + title + "' hidden");
    }
    
    public void addComponent(UIComponent component) {
        components.add(component);
        System.out.println("Added component to window: " + component.getClass().getSimpleName());
    }
    
    public void setRenderer(UIRenderer renderer) {
        this.renderer = renderer;
        // Update all child components
        for (UIComponent component : components) {
            component.setRenderer(renderer);
        }
        System.out.println("Window and all components switched to " + renderer.getClass().getSimpleName());
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public boolean isVisible() {
        return isVisible;
    }
}
```

### Example 2: Database Connection Bridge

```java
// Implementation interface
public interface DatabaseImplementation {
    void connect(String connectionString);
    void disconnect();
    ResultSet executeQuery(String query);
    int executeUpdate(String query);
    void beginTransaction();
    void commit();
    void rollback();
    String getDatabaseInfo();
}

// Concrete Implementations
public class MySQLImplementation implements DatabaseImplementation {
    private boolean connected = false;
    private boolean inTransaction = false;
    
    @Override
    public void connect(String connectionString) {
        System.out.println("Connecting to MySQL database...");
        System.out.println("Connection string: " + connectionString);
        System.out.println("Loading MySQL JDBC driver");
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
        System.out.println("Using MySQL-specific optimizations");
        
        // Simulate MySQL-specific query execution
        return new MockResultSet("MySQL Result: " + query);
    }
    
    @Override
    public int executeUpdate(String query) {
        if (!connected) throw new RuntimeException("Not connected to database");
        
        System.out.println("Executing MySQL update: " + query);
        System.out.println("Using MySQL storage engine optimizations");
        
        return 1; // Simulated rows affected
    }
    
    @Override
    public void beginTransaction() {
        System.out.println("BEGIN - MySQL transaction started");
        inTransaction = true;
    }
    
    @Override
    public void commit() {
        if (inTransaction) {
            System.out.println("COMMIT - MySQL transaction committed");
            inTransaction = false;
        }
    }
    
    @Override
    public void rollback() {
        if (inTransaction) {
            System.out.println("ROLLBACK - MySQL transaction rolled back");
            inTransaction = false;
        }
    }
    
    @Override
    public String getDatabaseInfo() {
        return "MySQL 8.0 - InnoDB Storage Engine";
    }
}

public class PostgreSQLImplementation implements DatabaseImplementation {
    private boolean connected = false;
    private boolean inTransaction = false;
    
    @Override
    public void connect(String connectionString) {
        System.out.println("Connecting to PostgreSQL database...");
        System.out.println("Connection string: " + connectionString);
        System.out.println("Loading PostgreSQL JDBC driver");
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
        System.out.println("Using PostgreSQL advanced indexing");
        
        return new MockResultSet("PostgreSQL Result: " + query);
    }
    
    @Override
    public int executeUpdate(String query) {
        if (!connected) throw new RuntimeException("Not connected to database");
        
        System.out.println("Executing PostgreSQL update: " + query);
        System.out.println("Using PostgreSQL MVCC for concurrency");
        
        return 1; // Simulated rows affected
    }
    
    @Override
    public void beginTransaction() {
        System.out.println("BEGIN - PostgreSQL transaction started");
        inTransaction = true;
    }
    
    @Override
    public void commit() {
        if (inTransaction) {
            System.out.println("COMMIT - PostgreSQL transaction committed");
            inTransaction = false;
        }
    }
    
    @Override
    public void rollback() {
        if (inTransaction) {
            System.out.println("ROLLBACK - PostgreSQL transaction rolled back");
            inTransaction = false;
        }
    }
    
    @Override
    public String getDatabaseInfo() {
        return "PostgreSQL 14.0 - Advanced Open Source Database";
    }
}

public class MongoDBImplementation implements DatabaseImplementation {
    private boolean connected = false;
    private boolean inTransaction = false;
    
    @Override
    public void connect(String connectionString) {
        System.out.println("Connecting to MongoDB...");
        System.out.println("Connection string: " + connectionString);
        System.out.println("Establishing connection to MongoDB cluster");
        connected = true;
        System.out.println("Connected to MongoDB successfully!");
    }
    
    @Override
    public void disconnect() {
        if (connected) {
            System.out.println("Closing MongoDB connection...");
            connected = false;
            System.out.println("MongoDB connection closed");
        }
    }
    
    @Override
    public ResultSet executeQuery(String query) {
        if (!connected) throw new RuntimeException("Not connected to database");
        
        System.out.println("Executing MongoDB query: " + query);
        System.out.println("Converting SQL to MongoDB aggregation pipeline");
        
        return new MockResultSet("MongoDB Result: " + query);
    }
    
    @Override
    public int executeUpdate(String query) {
        if (!connected) throw new RuntimeException("Not connected to database");
        
        System.out.println("Executing MongoDB update: " + query);
        System.out.println("Using MongoDB document-based operations");
        
        return 1; // Simulated documents affected
    }
    
    @Override
    public void beginTransaction() {
        System.out.println("Starting MongoDB transaction (requires replica set)");
        inTransaction = true;
    }
    
    @Override
    public void commit() {
        if (inTransaction) {
            System.out.println("Committing MongoDB transaction");
            inTransaction = false;
        }
    }
    
    @Override
    public void rollback() {
        if (inTransaction) {
            System.out.println("Aborting MongoDB transaction");
            inTransaction = false;
        }
    }
    
    @Override
    public String getDatabaseInfo() {
        return "MongoDB 5.0 - NoSQL Document Database";
    }
}

// Abstraction
public abstract class Database {
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
}

// Refined Abstractions
public class SimpleDatabase extends Database {
    public SimpleDatabase(DatabaseImplementation implementation) {
        super(implementation);
    }
    
    @Override
    public void createTable(String tableName, String[] columns) {
        System.out.println("\n=== Creating Simple Table ===");
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
        System.out.println("\n=== Inserting Data ===");
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
        System.out.println("\n=== Finding All Records ===");
        String sql = "SELECT * FROM " + tableName;
        return implementation.executeQuery(sql);
    }
    
    @Override
    public ResultSet findById(String tableName, Object id) {
        System.out.println("\n=== Finding Record by ID ===");
        String sql = "SELECT * FROM " + tableName + " WHERE id = '" + id + "'";
        return implementation.executeQuery(sql);
    }
}

public class AdvancedDatabase extends Database {
    public AdvancedDatabase(DatabaseImplementation implementation) {
        super(implementation);
    }
    
    @Override
    public void createTable(String tableName, String[] columns) {
        System.out.println("\n=== Creating Advanced Table with Constraints ===");
        StringBuilder sql = new StringBuilder("CREATE TABLE " + tableName + " (");
        for (int i = 0; i < columns.length; i++) {
            sql.append(columns[i]);
            if (i < columns.length - 1) sql.append(", ");
        }
        sql.append(", PRIMARY KEY (id), created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");
        
        implementation.executeUpdate(sql.toString());
        
        // Create indexes for better performance
        String indexSql = "CREATE INDEX idx_" + tableName + "_created_at ON " + tableName + " (created_at)";
        implementation.executeUpdate(indexSql);
        
        System.out.println("Advanced table '" + tableName + "' created with indexes and constraints");
    }
    
    @Override
    public void insertData(String tableName, Map<String, Object> data) {
        System.out.println("\n=== Inserting Data with Transaction ===");
        
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
            
            // Log the insertion
            String logSql = "INSERT INTO audit_log (table_name, action, timestamp) VALUES ('" 
                + tableName + "', 'INSERT', NOW())";
            implementation.executeUpdate(logSql);
            
            implementation.commit();
            System.out.println("Data inserted with audit log. Rows affected: " + rowsAffected);
            
        } catch (Exception e) {
            implementation.rollback();
            System.out.println("Error inserting data. Transaction rolled back: " + e.getMessage());
        }
    }
    
    @Override
    public ResultSet findAll(String tableName) {
        System.out.println("\n=== Finding All Records with Pagination ===");
        String sql = "SELECT * FROM " + tableName + " ORDER BY created_at DESC LIMIT 100";
        return implementation.executeQuery(sql);
    }
    
    @Override
    public ResultSet findById(String tableName, Object id) {
        System.out.println("\n=== Finding Record by ID with Joins ===");
        String sql = "SELECT t.*, audit.action, audit.timestamp " +
                    "FROM " + tableName + " t " +
                    "LEFT JOIN audit_log audit ON audit.table_name = '" + tableName + "' " +
                    "WHERE t.id = '" + id + "'";
        return implementation.executeQuery(sql);
    }
    
    public void backup(String backupPath) {
        System.out.println("\n=== Creating Database Backup ===");
        System.out.println("Database info: " + implementation.getDatabaseInfo());
        System.out.println("Backing up to: " + backupPath);
        
        String backupSql = "BACKUP DATABASE TO '" + backupPath + "'";
        implementation.executeUpdate(backupSql);
        
        System.out.println("Backup completed successfully");
    }
    
    public void optimize() {
        System.out.println("\n=== Optimizing Database ===");
        System.out.println("Analyzing table statistics...");
        implementation.executeUpdate("ANALYZE TABLE");
        
        System.out.println("Rebuilding indexes...");
        implementation.executeUpdate("REINDEX");
        
        System.out.println("Database optimization completed");
    }
}

// Mock ResultSet for demonstration
class MockResultSet {
    private String description;
    
    public MockResultSet(String description) {
        this.description = description;
    }
    
    @Override
    public String toString() {
        return "ResultSet: " + description;
    }
}
```

### Example 3: Message Sending System

```java
// Implementation interface
public interface MessageSender {
    void sendMessage(String recipient, String subject, String content);
    void sendBulkMessage(List<String> recipients, String subject, String content);
    boolean isConnected();
    void connect();
    void disconnect();
    String getServiceInfo();
}

// Concrete Implementations
public class EmailSender implements MessageSender {
    private boolean connected = false;
    private String smtpServer;
    private int port;
    
    public EmailSender(String smtpServer, int port) {
        this.smtpServer = smtpServer;
        this.port = port;
    }
    
    @Override
    public void connect() {
        System.out.println("Connecting to SMTP server: " + smtpServer + ":" + port);
        System.out.println("Authenticating with email credentials...");
        connected = true;
        System.out.println("Connected to email server successfully!");
    }
    
    @Override
    public void disconnect() {
        if (connected) {
            System.out.println("Closing SMTP connection");
            connected = false;
        }
    }
    
    @Override
    public void sendMessage(String recipient, String subject, String content) {
        if (!connected) connect();
        
        System.out.println("Sending email via SMTP:");
        System.out.println("  To: " + recipient);
        System.out.println("  Subject: " + subject);
        System.out.println("  Content: " + content);
        System.out.println("  Format: HTML/Plain text");
        System.out.println("Email sent successfully!");
    }
    
    @Override
    public void sendBulkMessage(List<String> recipients, String subject, String content) {
        if (!connected) connect();
        
        System.out.println("Sending bulk email to " + recipients.size() + " recipients");
        for (String recipient : recipients) {
            sendMessage(recipient, subject, content);
            // Add small delay to avoid spam filters
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        System.out.println("Bulk email campaign completed");
    }
    
    @Override
    public boolean isConnected() {
        return connected;
    }
    
    @Override
    public String getServiceInfo() {
        return "SMTP Email Service - " + smtpServer + ":" + port;
    }
}

public class SMSSender implements MessageSender {
    private boolean connected = false;
    private String apiKey;
    private String serviceProvider;
    
    public SMSSender(String serviceProvider, String apiKey) {
        this.serviceProvider = serviceProvider;
        this.apiKey = apiKey;
    }
    
    @Override
    public void connect() {
        System.out.println("Connecting to SMS service: " + serviceProvider);
        System.out.println("Authenticating with API key: " + apiKey.substring(0, 4) + "****");
        connected = true;
        System.out.println("Connected to SMS service successfully!");
    }
    
    @Override
    public void disconnect() {
        if (connected) {
            System.out.println("Disconnecting from SMS service");
            connected = false;
        }
    }
    
    @Override
    public void sendMessage(String recipient, String subject, String content) {
        if (!connected) connect();
        
        // SMS doesn't use subjects, combine subject and content
        String smsContent = (subject != null && !subject.isEmpty()) ? 
            subject + ": " + content : content;
        
        // Truncate if too long (SMS limit)
        if (smsContent.length() > 160) {
            smsContent = smsContent.substring(0, 157) + "...";
        }
        
        System.out.println("Sending SMS via " + serviceProvider + ":");
        System.out.println("  To: " + recipient);
        System.out.println("  Content: " + smsContent);
        System.out.println("  Length: " + smsContent.length() + " characters");
        System.out.println("SMS sent successfully!");
    }
    
    @Override
    public void sendBulkMessage(List<String> recipients, String subject, String content) {
        if (!connected) connect();
        
        System.out.println("Sending bulk SMS to " + recipients.size() + " recipients");
        
        String smsContent = (subject != null && !subject.isEmpty()) ? 
            subject + ": " + content : content;
        if (smsContent.length() > 160) {
            smsContent = smsContent.substring(0, 157) + "...";
        }
        
        for (String recipient : recipients) {
            sendMessage(recipient, null, smsContent);
        }
        System.out.println("Bulk SMS campaign completed");
    }
    
    @Override
    public boolean isConnected() {
        return connected;
    }
    
    @Override
    public String getServiceInfo() {
        return "SMS Service - " + serviceProvider;
    }
}

public class SlackSender implements MessageSender {
    private boolean connected = false;
    private String workspaceUrl;
    private String botToken;
    
    public SlackSender(String workspaceUrl, String botToken) {
        this.workspaceUrl = workspaceUrl;
        this.botToken = botToken;
    }
    
    @Override
    public void connect() {
        System.out.println("Connecting to Slack workspace: " + workspaceUrl);
        System.out.println("Authenticating with bot token");
        connected = true;
        System.out.println("Connected to Slack successfully!");
    }
    
    @Override
    public void disconnect() {
        if (connected) {
            System.out.println("Disconnecting from Slack");
            connected = false;
        }
    }
    
    @Override
    public void sendMessage(String recipient, String subject, String content) {
        if (!connected) connect();
        
        System.out.println("Sending Slack message:");
        System.out.println("  Channel/User: " + recipient);
        if (subject != null && !subject.isEmpty()) {
            System.out.println("  Subject: *" + subject + "*");
        }
        System.out.println("  Content: " + content);
        System.out.println("  Format: Markdown supported");
        System.out.println("Slack message sent successfully!");
    }
    
    @Override
    public void sendBulkMessage(List<String> recipients, String subject, String content) {
        if (!connected) connect();
        
        System.out.println("Sending Slack messages to " + recipients.size() + " channels/users");
        for (String recipient : recipients) {
            sendMessage(recipient, subject, content);
        }
        System.out.println("Bulk Slack messaging completed");
    }
    
    @Override
    public boolean isConnected() {
        return connected;
    }
    
    @Override
    public String getServiceInfo() {
        return "Slack Messaging - " + workspaceUrl;
    }
}

// Abstraction
public abstract class NotificationService {
    protected MessageSender messageSender;
    protected List<String> templates;
    
    public NotificationService(MessageSender messageSender) {
        this.messageSender = messageSender;
        this.templates = new ArrayList<>();
    }
    
    public void setMessageSender(MessageSender messageSender) {
        if (this.messageSender != null && this.messageSender.isConnected()) {
            this.messageSender.disconnect();
        }
        this.messageSender = messageSender;
        System.out.println("Switched to: " + messageSender.getServiceInfo());
    }
    
    public abstract void sendWelcomeMessage(String recipient, String name);
    public abstract void sendPasswordReset(String recipient, String resetLink);
    public abstract void sendOrderConfirmation(String recipient, String orderNumber, double amount);
    
    protected String formatTemplate(String template, Map<String, String> variables) {
        String formatted = template;
        for (Map.Entry<String, String> entry : variables.entrySet()) {
            formatted = formatted.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return formatted;
    }
}

// Refined Abstractions
public class CustomerNotificationService extends NotificationService {
    public CustomerNotificationService(MessageSender messageSender) {
        super(messageSender);
        initializeTemplates();
    }
    
    private void initializeTemplates() {
        templates.add("Welcome to our service, {name}! We're excited to have you on board.");
        templates.add("Password reset requested. Click here: {resetLink}");
        templates.add("Order #{orderNumber} confirmed. Total: ${amount}. Thank you for your purchase!");
    }
    
    @Override
    public void sendWelcomeMessage(String recipient, String name) {
        System.out.println("\n=== Sending Customer Welcome Message ===");
        
        Map<String, String> variables = new HashMap<>();
        variables.put("name", name);
        
        String content = formatTemplate(templates.get(0), variables);
        messageSender.sendMessage(recipient, "Welcome to Our Service", content);
        
        // Send additional resources
        String resourcesContent = "Here are some helpful resources to get you started: " +
            "1. User Guide, 2. Video Tutorials, 3. Community Forum";
        messageSender.sendMessage(recipient, "Getting Started Resources", resourcesContent);
    }
    
    @Override
    public void sendPasswordReset(String recipient, String resetLink) {
        System.out.println("\n=== Sending Password Reset ===");
        
        Map<String, String> variables = new HashMap<>();
        variables.put("resetLink", resetLink);
        
        String content = formatTemplate(templates.get(1), variables);
        content += "\n\nFor security reasons, this link will expire in 1 hour.";
        
        messageSender.sendMessage(recipient, "Password Reset Request", content);
    }
    
    @Override
    public void sendOrderConfirmation(String recipient, String orderNumber, double amount) {
        System.out.println("\n=== Sending Order Confirmation ===");
        
        Map<String, String> variables = new HashMap<>();
        variables.put("orderNumber", orderNumber);
        variables.put("amount", String.format("%.2f", amount));
        
        String content = formatTemplate(templates.get(2), variables);
        content += "\n\nYour order will be processed within 24 hours. " +
                  "You'll receive tracking information once shipped.";
        
        messageSender.sendMessage(recipient, "Order Confirmation - #" + orderNumber, content);
    }
    
    public void sendBulkPromotion(List<String> customers, String promoCode, int discount) {
        System.out.println("\n=== Sending Bulk Promotion ===");
        
        String content = "Special offer! Use code " + promoCode + " for " + discount + "% off your next order. " +
                        "Valid until the end of this month!";
        
        messageSender.sendBulkMessage(customers, "Exclusive Discount - " + discount + "% Off!", content);
    }
}

public class AdminNotificationService extends NotificationService {
    private List<String> adminContacts;
    
    public AdminNotificationService(MessageSender messageSender) {
        super(messageSender);
        this.adminContacts = new ArrayList<>();
        initializeTemplates();
    }
    
    private void initializeTemplates() {
        templates.add("ALERT: System error detected - {errorType}");
        templates.add("New user registration: {userName} - {email}");
        templates.add("Security Alert: {alertType} from IP {ipAddress}");
    }
    
    public void addAdminContact(String contact) {
        adminContacts.add(contact);
        System.out.println("Added admin contact: " + contact);
    }
    
    @Override
    public void sendWelcomeMessage(String recipient, String name) {
        // Admin service doesn't send welcome messages to customers
        System.out.println("AdminNotificationService doesn't handle customer welcome messages");
    }
    
    @Override
    public void sendPasswordReset(String recipient, String resetLink) {
        // Admin handles password resets differently
        System.out.println("Admin password reset handled through secure channel");
    }
    
    @Override
    public void sendOrderConfirmation(String recipient, String orderNumber, double amount) {
        // Admin gets order notifications for monitoring
        sendOrderNotificationToAdmins(orderNumber, amount);
    }
    
    public void sendSystemAlert(String errorType, String details) {
        System.out.println("\n=== Sending System Alert ===");
        
        Map<String, String> variables = new HashMap<>();
        variables.put("errorType", errorType);
        
        String content = formatTemplate(templates.get(0), variables);
        content += "\n\nDetails: " + details;
        content += "\nTime: " + new Date();
        
        // Send to all admin contacts
        messageSender.sendBulkMessage(adminContacts, "URGENT: System Alert", content);
    }
    
    public void sendUserRegistrationNotification(String userName, String email) {
        System.out.println("\n=== Sending User Registration Notification ===");
        
        Map<String, String> variables = new HashMap<>();
        variables.put("userName", userName);
        variables.put("email", email);
        
        String content = formatTemplate(templates.get(1), variables);
        content += "\nRegistration time: " + new Date();
        
        messageSender.sendBulkMessage(adminContacts, "New User Registration", content);
    }
    
    public void sendSecurityAlert(String alertType, String ipAddress, String details) {
        System.out.println("\n=== Sending Security Alert ===");
        
        Map<String, String> variables = new HashMap<>();
        variables.put("alertType", alertType);
        variables.put("ipAddress", ipAddress);
        
        String content = formatTemplate(templates.get(2), variables);
        content += "\n\nDetails: " + details;
        content += "\nTime: " + new Date();
        
        messageSender.sendBulkMessage(adminContacts, "SECURITY ALERT: " + alertType, content);
    }
    
    private void sendOrderNotificationToAdmins(String orderNumber, double amount) {
        String content = "New order received: #" + orderNumber + " for $" + 
                        String.format("%.2f", amount);
        messageSender.sendBulkMessage(adminContacts, "New Order Alert", content);
    }
}
```

## Real-World Applications

### 1. **Cross-Platform Applications**
- GUI frameworks (Swing, JavaFX, SWT)
- Database drivers (JDBC implementations)
- Graphics APIs (OpenGL, DirectX, Vulkan)

### 2. **Multi-Cloud Services**
- Cloud storage (AWS S3, Azure Blob, Google Cloud)
- Message queues (RabbitMQ, Apache Kafka, AWS SQS)
- Container orchestration (Docker Swarm, Kubernetes)

### 3. **Media Processing**
- Video codecs (H.264, H.265, VP9)
- Image formats (JPEG, PNG, WebP)
- Audio processing (MP3, AAC, FLAC)

### 4. **Communication Systems**
- Messaging protocols (HTTP, WebSocket, TCP)
- Notification services (Email, SMS, Push notifications)
- Authentication providers (OAuth, LDAP, SAML)

### 5. **Gaming Engines**
- Rendering backends (OpenGL, DirectX, Metal)
- Physics engines (Box2D, Bullet Physics)
- Audio systems (OpenAL, DirectSound)

## Advantages and Disadvantages

### Advantages
1. **Decoupling**: Abstraction and implementation can vary independently
2. **Runtime Flexibility**: Implementation can be chosen or changed at runtime
3. **Platform Independence**: Same abstraction works across different platforms
4. **Extensibility**: Easy to add new abstractions or implementations
5. **Code Reuse**: Implementations can be shared across different abstractions
6. **Single Responsibility**: Each class has a focused responsibility

### Disadvantages
1. **Increased Complexity**: More classes and interfaces to manage
2. **Indirect Communication**: Communication through interfaces may add overhead
3. **Learning Curve**: Can be difficult to understand initially
4. **Potential Overkill**: May be unnecessary for simple scenarios

## Bridge vs Other Patterns

### Bridge vs Adapter Pattern
- **Bridge**: Designed up-front to let abstraction and implementation vary independently
- **Adapter**: Applied to incompatible classes after they're designed
- Bridge prevents class explosion; Adapter fixes compatibility issues

### Bridge vs Strategy Pattern
- **Bridge**: Structural pattern focusing on abstraction-implementation separation
- **Strategy**: Behavioral pattern focusing on algorithm selection
- Bridge uses composition at architectural level; Strategy at algorithmic level

### Bridge vs Factory Pattern
- **Factory**: Creates objects but doesn't manage their relationships
- **Bridge**: Manages relationship between abstraction and implementation
- Often used together - Factory can create Bridge implementations

## Best Practices

1. **Clear Separation**: Ensure clear separation between abstraction and implementation
2. **Stable Interfaces**: Keep Implementation interface stable once defined
3. **Dependency Injection**: Use DI to inject implementations into abstractions
4. **Interface Segregation**: Keep implementation interfaces focused and minimal
5. **Documentation**: Clearly document the contract between abstraction and implementation
6. **Testing**: Test abstractions independently of implementations
7. **Error Handling**: Handle cases where implementation is not available
8. **Performance**: Consider performance implications of indirection
9. **Memory Management**: Be careful with object lifecycle management
10. **Thread Safety**: Ensure thread safety when switching implementations

## Common Pitfalls to Avoid

1. **Over-Engineering**: Don't use Bridge for simple scenarios
2. **Leaky Abstractions**: Don't let implementation details leak to abstractions
3. **Too Many Layers**: Avoid excessive layers of abstraction
4. **Ignoring Performance**: Consider the cost of indirection
5. **Inconsistent Interfaces**: Ensure all implementations follow the same contract

## Conclusion

The Bridge Pattern is essential for creating flexible, extensible systems that need to support multiple platforms or implementations. It shines when you need to separate what something does (abstraction) from how it does it (implementation), allowing both to evolve independently.

Key takeaways:
- Use Bridge when you need platform independence
- It prevents class explosion in inheritance hierarchies  
- Composition is preferred over inheritance
- Both abstraction and implementation can vary independently
- Excellent for systems that need to support multiple backends or platforms

Remember: Bridge Pattern is about structural flexibility and preventing tight coupling between abstractions and their implementations. It's particularly valuable in systems that need to support multiple platforms, databases, or external services.