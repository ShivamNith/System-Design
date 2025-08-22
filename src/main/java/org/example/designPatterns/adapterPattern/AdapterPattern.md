# Adapter Design Pattern - Complete Guide

## Table of Contents
1. [Introduction](#introduction)
2. [Core Concepts](#core-concepts)
3. [When to Use Adapter Pattern](#when-to-use-adapter-pattern)
4. [Structure and Components](#structure-and-components)
5. [Implementation Types](#implementation-types)
6. [Detailed Examples](#detailed-examples)
7. [Real-World Applications](#real-world-applications)
8. [Advantages and Disadvantages](#advantages-and-disadvantages)
9. [Adapter vs Other Patterns](#adapter-vs-other-patterns)
10. [Best Practices](#best-practices)

## Introduction

The **Adapter Pattern** is a structural design pattern that allows objects with incompatible interfaces to work together. It acts as a bridge between two incompatible interfaces by wrapping an existing class with a new interface.

### Definition
> "Convert the interface of a class into another interface clients expect. Adapter lets classes work together that couldn't otherwise because of incompatible interfaces." - Gang of Four

### Problem It Solves
Imagine you have existing code that works with a specific interface, but you need to integrate with a third-party library or legacy system that has a different interface. Without the Adapter Pattern, you might face:
- Interface incompatibility between components
- Need to modify existing working code
- Inability to reuse existing functionality
- Tight coupling with specific implementations
- Difficulty integrating third-party libraries

## Core Concepts

### Key Principles
1. **Interface Translation**: Converts one interface to another
2. **Encapsulation**: Hides the complexity of interface conversion
3. **Reusability**: Allows reuse of existing incompatible components
4. **Flexibility**: Enables integration without modifying existing code
5. **Composition over Inheritance**: Uses composition to achieve adaptation

### Real-World Analogy
Think of a power adapter for your laptop:
- Your laptop expects a specific voltage and plug type (Target Interface)
- The wall outlet provides different voltage and plug type (Adaptee)
- The power adapter (Adapter) converts between them
- You can use your laptop in different countries without modification
- The adapter handles all the conversion complexity

## When to Use Adapter Pattern

Use the Adapter Pattern when:
1. **Incompatible interfaces** need to work together
2. **Integrating third-party libraries** with different interfaces
3. **Legacy system integration** without modifying existing code
4. **Multiple data sources** with different formats need uniform access
5. **Different versions** of APIs need to coexist
6. **Client expects specific interface** but available class has different interface

### Red Flags Indicating Need for Adapter Pattern
- Interface mismatch between components
- Need to use legacy code with new systems
- Third-party library integration challenges
- Multiple similar classes with different interfaces
- Converting between different data formats
- Making incompatible APIs work together

## Structure and Components

### Object Adapter Pattern Structure
```
┌─────────────────┐         ┌──────────────────┐
│     Client      │────────>│     Target       │
└─────────────────┘         │   (Interface)    │
                            ├──────────────────┤
                            │ + request()      │
                            └──────────────────┘
                                      △
                                      │ implements
                            ┌─────────┴──────────┐
                            │     Adapter        │
                            ├────────────────────┤
                            │ - adaptee: Adaptee │
                            │ + request()        │
                            └────┬───────────────┘
                                 │ has-a
                                 ▼
                            ┌────────────────────┐
                            │     Adaptee        │
                            ├────────────────────┤
                            │ + specificRequest()│
                            └────────────────────┘
```

### Class Adapter Pattern Structure (Using Multiple Inheritance)
```
┌─────────────────┐         ┌──────────────────┐
│     Client      │────────>│     Target       │
└─────────────────┘         │   (Interface)    │
                            ├──────────────────┤
                            │ + request()      │
                            └──────────────────┘
                                      △
                                      │ implements
                            ┌─────────┴──────────┐
                            │     Adapter        │  extends  ┌────────────────────┐
                            ├────────────────────┤─────────>│     Adaptee        │
                            │ + request()        │          ├────────────────────┤
                            └────────────────────┘          │ + specificRequest()│
                                                            └────────────────────┘
```

### Components
1. **Target**: The interface that clients expect
2. **Client**: Uses objects through the Target interface
3. **Adaptee**: The existing interface that needs adapting
4. **Adapter**: Converts Adaptee interface to Target interface

## Implementation Types

### 1. Object Adapter (Composition-based)
Uses composition to hold an instance of the adaptee.

### 2. Class Adapter (Inheritance-based)
Uses multiple inheritance to adapt interfaces (not possible in Java).

### 3. Two-Way Adapter
Can work as both Target and Adaptee.

### 4. Pluggable Adapter
Uses abstract operations to support adaptation of multiple classes.

## Detailed Examples

### Example 1: Media Player Adapter System

```java
// Target interface - what the client expects
public interface MediaPlayer {
    void play(String audioType, String fileName);
}

// Adaptee interfaces - external libraries with different interfaces
public interface Mp3Player {
    void playMp3(String fileName);
}

public interface Mp4Player {
    void playMp4(String fileName);
}

public interface VlcPlayer {
    void playVlc(String fileName);
}

// Concrete Adaptee implementations - third-party libraries
public class AdvancedMp3Player implements Mp3Player {
    @Override
    public void playMp3(String fileName) {
        System.out.println("Playing MP3 file: " + fileName);
    }
}

public class AdvancedMp4Player implements Mp4Player {
    @Override
    public void playMp4(String fileName) {
        System.out.println("Playing MP4 file: " + fileName);
    }
}

public class AdvancedVlcPlayer implements VlcPlayer {
    @Override
    public void playVlc(String fileName) {
        System.out.println("Playing VLC file: " + fileName);
    }
}

// Adapter - converts different media player interfaces to common interface
public class MediaAdapter implements MediaPlayer {
    private Mp3Player mp3Player;
    private Mp4Player mp4Player;
    private VlcPlayer vlcPlayer;
    
    public MediaAdapter(String audioType) {
        switch (audioType.toLowerCase()) {
            case "mp3":
                mp3Player = new AdvancedMp3Player();
                break;
            case "mp4":
                mp4Player = new AdvancedMp4Player();
                break;
            case "vlc":
                vlcPlayer = new AdvancedVlcPlayer();
                break;
            default:
                throw new IllegalArgumentException("Unsupported audio type: " + audioType);
        }
    }
    
    @Override
    public void play(String audioType, String fileName) {
        switch (audioType.toLowerCase()) {
            case "mp3":
                mp3Player.playMp3(fileName);
                break;
            case "mp4":
                mp4Player.playMp4(fileName);
                break;
            case "vlc":
                vlcPlayer.playVlc(fileName);
                break;
            default:
                System.out.println("Invalid media. " + audioType + " format not supported");
        }
    }
}

// Client class - uses the Target interface
public class AudioPlayer implements MediaPlayer {
    private MediaAdapter mediaAdapter;
    
    @Override
    public void play(String audioType, String fileName) {
        // Built-in support for mp3 format
        if ("mp3".equalsIgnoreCase(audioType)) {
            System.out.println("Playing MP3 file (built-in): " + fileName);
        }
        // Use adapter for other formats
        else {
            try {
                mediaAdapter = new MediaAdapter(audioType);
                mediaAdapter.play(audioType, fileName);
            } catch (IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }
    
    // Enhanced version with caching adapters
    public void playEnhanced(String audioType, String fileName) {
        if ("mp3".equalsIgnoreCase(audioType)) {
            System.out.println("Playing MP3 file (built-in): " + fileName);
            return;
        }
        
        // Create adapter only if needed (lazy initialization)
        if (mediaAdapter == null) {
            try {
                mediaAdapter = new MediaAdapter(audioType);
            } catch (IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
                return;
            }
        }
        
        mediaAdapter.play(audioType, fileName);
    }
}
```

### Example 2: Database Adapter for Multiple Database Types

```java
// Target interface - common database operations
public interface DatabaseInterface {
    void connect();
    void disconnect();
    ResultSet executeQuery(String query);
    int executeUpdate(String query);
    void beginTransaction();
    void commit();
    void rollback();
}

// Result set abstraction
public class ResultSet {
    private List<Map<String, Object>> data;
    private int currentRow = -1;
    
    public ResultSet(List<Map<String, Object>> data) {
        this.data = data != null ? data : new ArrayList<>();
    }
    
    public boolean next() {
        currentRow++;
        return currentRow < data.size();
    }
    
    public Object getValue(String columnName) {
        if (currentRow >= 0 && currentRow < data.size()) {
            return data.get(currentRow).get(columnName);
        }
        return null;
    }
    
    public int getRowCount() {
        return data.size();
    }
}

// Adaptee 1 - MySQL database (third-party library simulation)
public class MySQLDatabase {
    private boolean connected = false;
    private boolean inTransaction = false;
    
    public void connectToMySQL(String host, int port, String database) {
        System.out.println("Connecting to MySQL: " + host + ":" + port + "/" + database);
        connected = true;
    }
    
    public void closeConnection() {
        System.out.println("Closing MySQL connection");
        connected = false;
    }
    
    public List<Map<String, Object>> runQuery(String sql) {
        System.out.println("Executing MySQL query: " + sql);
        // Simulate query result
        List<Map<String, Object>> result = new ArrayList<>();
        Map<String, Object> row = new HashMap<>();
        row.put("id", 1);
        row.put("name", "MySQL Result");
        result.add(row);
        return result;
    }
    
    public int runUpdate(String sql) {
        System.out.println("Executing MySQL update: " + sql);
        return 1; // Simulated affected rows
    }
    
    public void startTransaction() {
        System.out.println("Starting MySQL transaction");
        inTransaction = true;
    }
    
    public void commitTransaction() {
        System.out.println("Committing MySQL transaction");
        inTransaction = false;
    }
    
    public void rollbackTransaction() {
        System.out.println("Rolling back MySQL transaction");
        inTransaction = false;
    }
    
    public boolean isConnected() { return connected; }
    public boolean isInTransaction() { return inTransaction; }
}

// Adaptee 2 - PostgreSQL database (different interface)
public class PostgreSQLDatabase {
    private boolean isActive = false;
    private boolean hasOpenTx = false;
    
    public void establishConnection(String url) {
        System.out.println("Establishing PostgreSQL connection: " + url);
        isActive = true;
    }
    
    public void terminate() {
        System.out.println("Terminating PostgreSQL connection");
        isActive = false;
    }
    
    public List<Map<String, Object>> query(String sql) {
        System.out.println("Executing PostgreSQL query: " + sql);
        List<Map<String, Object>> result = new ArrayList<>();
        Map<String, Object> row = new HashMap<>();
        row.put("id", 2);
        row.put("name", "PostgreSQL Result");
        result.add(row);
        return result;
    }
    
    public int update(String sql) {
        System.out.println("Executing PostgreSQL update: " + sql);
        return 1;
    }
    
    public void beginTx() {
        System.out.println("Beginning PostgreSQL transaction");
        hasOpenTx = true;
    }
    
    public void commitTx() {
        System.out.println("Committing PostgreSQL transaction");
        hasOpenTx = false;
    }
    
    public void abortTx() {
        System.out.println("Aborting PostgreSQL transaction");
        hasOpenTx = false;
    }
    
    public boolean isActive() { return isActive; }
    public boolean hasOpenTx() { return hasOpenTx; }
}

// Adapter for MySQL
public class MySQLAdapter implements DatabaseInterface {
    private MySQLDatabase mysqlDatabase;
    private String host;
    private int port;
    private String database;
    
    public MySQLAdapter(String host, int port, String database) {
        this.host = host;
        this.port = port;
        this.database = database;
        this.mysqlDatabase = new MySQLDatabase();
    }
    
    @Override
    public void connect() {
        mysqlDatabase.connectToMySQL(host, port, database);
    }
    
    @Override
    public void disconnect() {
        mysqlDatabase.closeConnection();
    }
    
    @Override
    public ResultSet executeQuery(String query) {
        List<Map<String, Object>> data = mysqlDatabase.runQuery(query);
        return new ResultSet(data);
    }
    
    @Override
    public int executeUpdate(String query) {
        return mysqlDatabase.runUpdate(query);
    }
    
    @Override
    public void beginTransaction() {
        mysqlDatabase.startTransaction();
    }
    
    @Override
    public void commit() {
        mysqlDatabase.commitTransaction();
    }
    
    @Override
    public void rollback() {
        mysqlDatabase.rollbackTransaction();
    }
}

// Adapter for PostgreSQL
public class PostgreSQLAdapter implements DatabaseInterface {
    private PostgreSQLDatabase postgresDatabase;
    private String url;
    
    public PostgreSQLAdapter(String url) {
        this.url = url;
        this.postgresDatabase = new PostgreSQLDatabase();
    }
    
    @Override
    public void connect() {
        postgresDatabase.establishConnection(url);
    }
    
    @Override
    public void disconnect() {
        postgresDatabase.terminate();
    }
    
    @Override
    public ResultSet executeQuery(String query) {
        List<Map<String, Object>> data = postgresDatabase.query(query);
        return new ResultSet(data);
    }
    
    @Override
    public int executeUpdate(String query) {
        return postgresDatabase.update(query);
    }
    
    @Override
    public void beginTransaction() {
        postgresDatabase.beginTx();
    }
    
    @Override
    public void commit() {
        postgresDatabase.commitTx();
    }
    
    @Override
    public void rollback() {
        postgresDatabase.abortTx();
    }
}

// Factory to create appropriate database adapter
public class DatabaseAdapterFactory {
    public static DatabaseInterface createDatabase(String type, String connectionString) {
        switch (type.toLowerCase()) {
            case "mysql":
                // Parse connection string: mysql://host:port/database
                String[] parts = connectionString.replace("mysql://", "").split("/");
                String[] hostPort = parts[0].split(":");
                String host = hostPort[0];
                int port = hostPort.length > 1 ? Integer.parseInt(hostPort[1]) : 3306;
                String database = parts.length > 1 ? parts[1] : "default";
                return new MySQLAdapter(host, port, database);
                
            case "postgresql":
                return new PostgreSQLAdapter(connectionString);
                
            default:
                throw new IllegalArgumentException("Unsupported database type: " + type);
        }
    }
}

// Client code using the common interface
public class DatabaseManager {
    private DatabaseInterface database;
    
    public DatabaseManager(String dbType, String connectionString) {
        this.database = DatabaseAdapterFactory.createDatabase(dbType, connectionString);
    }
    
    public void performDatabaseOperations() {
        try {
            // Common operations regardless of database type
            database.connect();
            
            database.beginTransaction();
            
            ResultSet results = database.executeQuery("SELECT * FROM users");
            System.out.println("Query results:");
            while (results.next()) {
                System.out.println("ID: " + results.getValue("id") + 
                                 ", Name: " + results.getValue("name"));
            }
            
            int affected = database.executeUpdate("UPDATE users SET last_login = NOW()");
            System.out.println("Updated " + affected + " rows");
            
            database.commit();
            
        } catch (Exception e) {
            database.rollback();
            System.out.println("Error occurred, transaction rolled back: " + e.getMessage());
        } finally {
            database.disconnect();
        }
    }
}
```

### Example 3: Payment Gateway Adapter System

```java
// Target interface - standardized payment interface
public interface PaymentProcessor {
    PaymentResult processPayment(PaymentRequest request);
    PaymentResult refundPayment(String transactionId, double amount);
    PaymentStatus checkPaymentStatus(String transactionId);
    boolean isServiceAvailable();
}

// Common classes used across all payment systems
public class PaymentRequest {
    private String customerName;
    private String customerEmail;
    private double amount;
    private String currency;
    private String cardNumber;
    private String expiryDate;
    private String cvv;
    private Map<String, String> metadata;
    
    // Constructor and getters/setters
    public PaymentRequest(String customerName, String customerEmail, double amount, 
                         String currency, String cardNumber, String expiryDate, String cvv) {
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.amount = amount;
        this.currency = currency;
        this.cardNumber = cardNumber;
        this.expiryDate = expiryDate;
        this.cvv = cvv;
        this.metadata = new HashMap<>();
    }
    
    // Getters and setters
    public String getCustomerName() { return customerName; }
    public String getCustomerEmail() { return customerEmail; }
    public double getAmount() { return amount; }
    public String getCurrency() { return currency; }
    public String getCardNumber() { return cardNumber; }
    public String getExpiryDate() { return expiryDate; }
    public String getCvv() { return cvv; }
    public Map<String, String> getMetadata() { return metadata; }
    
    public void addMetadata(String key, String value) {
        metadata.put(key, value);
    }
}

public class PaymentResult {
    private boolean success;
    private String transactionId;
    private String message;
    private PaymentStatus status;
    private Map<String, Object> additionalData;
    
    public PaymentResult(boolean success, String transactionId, String message, PaymentStatus status) {
        this.success = success;
        this.transactionId = transactionId;
        this.message = message;
        this.status = status;
        this.additionalData = new HashMap<>();
    }
    
    // Getters and setters
    public boolean isSuccess() { return success; }
    public String getTransactionId() { return transactionId; }
    public String getMessage() { return message; }
    public PaymentStatus getStatus() { return status; }
    public Map<String, Object> getAdditionalData() { return additionalData; }
    
    public void addData(String key, Object value) {
        additionalData.put(key, value);
    }
}

public enum PaymentStatus {
    PENDING, COMPLETED, FAILED, CANCELLED, REFUNDED, PARTIAL_REFUND
}

// Adaptee 1 - PayPal API (different interface)
public class PayPalAPI {
    public PayPalResponse charge(PayPalPayment payment) {
        System.out.println("Processing PayPal payment: $" + payment.getTotal() + 
                          " for " + payment.getPayerName());
        
        // Simulate PayPal processing
        boolean success = Math.random() > 0.1; // 90% success rate
        String paypalId = "PP_" + System.currentTimeMillis();
        
        return new PayPalResponse(success, paypalId, 
                                success ? "Payment successful" : "Payment failed", 
                                success ? "COMPLETED" : "FAILED");
    }
    
    public PayPalResponse refund(String paypalTransactionId, double amount) {
        System.out.println("Processing PayPal refund: $" + amount + " for transaction " + paypalTransactionId);
        return new PayPalResponse(true, "REF_" + System.currentTimeMillis(), "Refund processed", "REFUNDED");
    }
    
    public PayPalResponse getTransactionStatus(String paypalTransactionId) {
        System.out.println("Checking PayPal transaction status: " + paypalTransactionId);
        return new PayPalResponse(true, paypalTransactionId, "Transaction found", "COMPLETED");
    }
    
    public boolean isOnline() {
        return true; // Simulate service availability
    }
}

// PayPal specific classes
public class PayPalPayment {
    private String payerName;
    private String payerEmail;
    private double total;
    private String currencyCode;
    private String creditCardNumber;
    private String expirationDate;
    private String securityCode;
    
    public PayPalPayment(String payerName, String payerEmail, double total, String currencyCode,
                        String creditCardNumber, String expirationDate, String securityCode) {
        this.payerName = payerName;
        this.payerEmail = payerEmail;
        this.total = total;
        this.currencyCode = currencyCode;
        this.creditCardNumber = creditCardNumber;
        this.expirationDate = expirationDate;
        this.securityCode = securityCode;
    }
    
    // Getters
    public String getPayerName() { return payerName; }
    public String getPayerEmail() { return payerEmail; }
    public double getTotal() { return total; }
    public String getCurrencyCode() { return currencyCode; }
    public String getCreditCardNumber() { return creditCardNumber; }
    public String getExpirationDate() { return expirationDate; }
    public String getSecurityCode() { return securityCode; }
}

public class PayPalResponse {
    private boolean isSuccessful;
    private String transactionId;
    private String message;
    private String status;
    
    public PayPalResponse(boolean isSuccessful, String transactionId, String message, String status) {
        this.isSuccessful = isSuccessful;
        this.transactionId = transactionId;
        this.message = message;
        this.status = status;
    }
    
    // Getters
    public boolean isSuccessful() { return isSuccessful; }
    public String getTransactionId() { return transactionId; }
    public String getMessage() { return message; }
    public String getStatus() { return status; }
}

// Adaptee 2 - Stripe API (different interface)
public class StripeAPI {
    public StripeCharge createCharge(StripeChargeRequest request) {
        System.out.println("Processing Stripe charge: $" + request.getAmountInCents() / 100.0 + 
                          " for " + request.getDescription());
        
        boolean success = Math.random() > 0.05; // 95% success rate
        String chargeId = "ch_" + System.currentTimeMillis();
        
        return new StripeCharge(chargeId, request.getAmountInCents(), request.getCurrency(),
                               success ? "succeeded" : "failed", success ? "Charge successful" : "Charge failed");
    }
    
    public StripeRefund createRefund(String chargeId, int amountInCents) {
        System.out.println("Processing Stripe refund: $" + amountInCents / 100.0 + " for charge " + chargeId);
        return new StripeRefund("re_" + System.currentTimeMillis(), amountInCents, "succeeded");
    }
    
    public StripeCharge retrieveCharge(String chargeId) {
        System.out.println("Retrieving Stripe charge: " + chargeId);
        return new StripeCharge(chargeId, 5000, "usd", "succeeded", "Retrieved charge");
    }
    
    public boolean isApiReachable() {
        return true;
    }
}

// Stripe specific classes
public class StripeChargeRequest {
    private int amountInCents;
    private String currency;
    private String source; // token representing payment method
    private String description;
    private Map<String, String> metadata;
    
    public StripeChargeRequest(int amountInCents, String currency, String source, String description) {
        this.amountInCents = amountInCents;
        this.currency = currency;
        this.source = source;
        this.description = description;
        this.metadata = new HashMap<>();
    }
    
    // Getters
    public int getAmountInCents() { return amountInCents; }
    public String getCurrency() { return currency; }
    public String getSource() { return source; }
    public String getDescription() { return description; }
    public Map<String, String> getMetadata() { return metadata; }
}

public class StripeCharge {
    private String id;
    private int amountInCents;
    private String currency;
    private String status;
    private String outcome;
    
    public StripeCharge(String id, int amountInCents, String currency, String status, String outcome) {
        this.id = id;
        this.amountInCents = amountInCents;
        this.currency = currency;
        this.status = status;
        this.outcome = outcome;
    }
    
    // Getters
    public String getId() { return id; }
    public int getAmountInCents() { return amountInCents; }
    public String getCurrency() { return currency; }
    public String getStatus() { return status; }
    public String getOutcome() { return outcome; }
}

public class StripeRefund {
    private String id;
    private int amountInCents;
    private String status;
    
    public StripeRefund(String id, int amountInCents, String status) {
        this.id = id;
        this.amountInCents = amountInCents;
        this.status = status;
    }
    
    // Getters
    public String getId() { return id; }
    public int getAmountInCents() { return amountInCents; }
    public String getStatus() { return status; }
}

// Adapter for PayPal
public class PayPalAdapter implements PaymentProcessor {
    private PayPalAPI paypalAPI;
    
    public PayPalAdapter() {
        this.paypalAPI = new PayPalAPI();
    }
    
    @Override
    public PaymentResult processPayment(PaymentRequest request) {
        // Convert PaymentRequest to PayPalPayment
        PayPalPayment paypalPayment = new PayPalPayment(
            request.getCustomerName(),
            request.getCustomerEmail(),
            request.getAmount(),
            request.getCurrency(),
            request.getCardNumber(),
            request.getExpiryDate(),
            request.getCvv()
        );
        
        PayPalResponse response = paypalAPI.charge(paypalPayment);
        
        // Convert PayPalResponse to PaymentResult
        PaymentStatus status = convertPayPalStatus(response.getStatus());
        PaymentResult result = new PaymentResult(
            response.isSuccessful(),
            response.getTransactionId(),
            response.getMessage(),
            status
        );
        
        result.addData("provider", "PayPal");
        result.addData("paypal_transaction_id", response.getTransactionId());
        
        return result;
    }
    
    @Override
    public PaymentResult refundPayment(String transactionId, double amount) {
        PayPalResponse response = paypalAPI.refund(transactionId, amount);
        
        PaymentStatus status = convertPayPalStatus(response.getStatus());
        PaymentResult result = new PaymentResult(
            response.isSuccessful(),
            response.getTransactionId(),
            response.getMessage(),
            status
        );
        
        result.addData("provider", "PayPal");
        result.addData("refund_id", response.getTransactionId());
        
        return result;
    }
    
    @Override
    public PaymentStatus checkPaymentStatus(String transactionId) {
        PayPalResponse response = paypalAPI.getTransactionStatus(transactionId);
        return convertPayPalStatus(response.getStatus());
    }
    
    @Override
    public boolean isServiceAvailable() {
        return paypalAPI.isOnline();
    }
    
    private PaymentStatus convertPayPalStatus(String paypalStatus) {
        switch (paypalStatus.toUpperCase()) {
            case "COMPLETED": return PaymentStatus.COMPLETED;
            case "PENDING": return PaymentStatus.PENDING;
            case "FAILED": return PaymentStatus.FAILED;
            case "CANCELLED": return PaymentStatus.CANCELLED;
            case "REFUNDED": return PaymentStatus.REFUNDED;
            default: return PaymentStatus.FAILED;
        }
    }
}

// Adapter for Stripe
public class StripeAdapter implements PaymentProcessor {
    private StripeAPI stripeAPI;
    
    public StripeAdapter() {
        this.stripeAPI = new StripeAPI();
    }
    
    @Override
    public PaymentResult processPayment(PaymentRequest request) {
        // Convert PaymentRequest to StripeChargeRequest
        int amountInCents = (int) Math.round(request.getAmount() * 100);
        String tokenizedCard = tokenizeCard(request.getCardNumber(), request.getExpiryDate(), request.getCvv());
        String description = "Payment for " + request.getCustomerName();
        
        StripeChargeRequest chargeRequest = new StripeChargeRequest(
            amountInCents,
            request.getCurrency().toLowerCase(),
            tokenizedCard,
            description
        );
        
        StripeCharge charge = stripeAPI.createCharge(chargeRequest);
        
        // Convert StripeCharge to PaymentResult
        PaymentStatus status = convertStripeStatus(charge.getStatus());
        PaymentResult result = new PaymentResult(
            "succeeded".equals(charge.getStatus()),
            charge.getId(),
            charge.getOutcome(),
            status
        );
        
        result.addData("provider", "Stripe");
        result.addData("stripe_charge_id", charge.getId());
        result.addData("amount_cents", charge.getAmountInCents());
        
        return result;
    }
    
    @Override
    public PaymentResult refundPayment(String transactionId, double amount) {
        int amountInCents = (int) Math.round(amount * 100);
        StripeRefund refund = stripeAPI.createRefund(transactionId, amountInCents);
        
        PaymentStatus status = convertStripeStatus(refund.getStatus());
        PaymentResult result = new PaymentResult(
            "succeeded".equals(refund.getStatus()),
            refund.getId(),
            "Refund processed",
            status
        );
        
        result.addData("provider", "Stripe");
        result.addData("refund_id", refund.getId());
        result.addData("original_charge_id", transactionId);
        
        return result;
    }
    
    @Override
    public PaymentStatus checkPaymentStatus(String transactionId) {
        StripeCharge charge = stripeAPI.retrieveCharge(transactionId);
        return convertStripeStatus(charge.getStatus());
    }
    
    @Override
    public boolean isServiceAvailable() {
        return stripeAPI.isApiReachable();
    }
    
    private String tokenizeCard(String cardNumber, String expiryDate, String cvv) {
        // In real implementation, this would securely tokenize the card
        return "tok_" + cardNumber.hashCode();
    }
    
    private PaymentStatus convertStripeStatus(String stripeStatus) {
        switch (stripeStatus.toLowerCase()) {
            case "succeeded": return PaymentStatus.COMPLETED;
            case "pending": return PaymentStatus.PENDING;
            case "failed": return PaymentStatus.FAILED;
            case "canceled": return PaymentStatus.CANCELLED;
            default: return PaymentStatus.FAILED;
        }
    }
}

// Payment service that works with any payment provider
public class PaymentService {
    private PaymentProcessor primaryProcessor;
    private PaymentProcessor fallbackProcessor;
    
    public PaymentService(PaymentProcessor primaryProcessor, PaymentProcessor fallbackProcessor) {
        this.primaryProcessor = primaryProcessor;
        this.fallbackProcessor = fallbackProcessor;
    }
    
    public PaymentResult processPayment(PaymentRequest request) {
        // Try primary processor first
        if (primaryProcessor.isServiceAvailable()) {
            try {
                PaymentResult result = primaryProcessor.processPayment(request);
                if (result.isSuccess()) {
                    return result;
                }
            } catch (Exception e) {
                System.out.println("Primary processor failed: " + e.getMessage());
            }
        }
        
        // Fallback to secondary processor
        if (fallbackProcessor.isServiceAvailable()) {
            System.out.println("Using fallback payment processor");
            return fallbackProcessor.processPayment(request);
        }
        
        return new PaymentResult(false, null, "All payment processors unavailable", PaymentStatus.FAILED);
    }
    
    public PaymentResult processRefund(String transactionId, double amount, String provider) {
        PaymentProcessor processor = getProcessorByProvider(provider);
        if (processor != null && processor.isServiceAvailable()) {
            return processor.refundPayment(transactionId, amount);
        }
        return new PaymentResult(false, null, "Payment processor unavailable for refund", PaymentStatus.FAILED);
    }
    
    private PaymentProcessor getProcessorByProvider(String provider) {
        // In a real system, you'd maintain a registry of processors
        if ("PayPal".equalsIgnoreCase(provider)) {
            return new PayPalAdapter();
        } else if ("Stripe".equalsIgnoreCase(provider)) {
            return new StripeAdapter();
        }
        return null;
    }
}
```

## Real-World Applications

### 1. **Legacy System Integration**
- Adapting old systems to work with modern APIs
- Database migration and integration
- Mainframe integration with modern applications

### 2. **Third-Party Library Integration**
- Payment gateways (PayPal, Stripe, Square)
- Cloud services (AWS, Azure, Google Cloud)
- Authentication providers (OAuth, SAML, LDAP)

### 3. **Data Format Conversion**
- XML to JSON adapters
- CSV to database record adapters
- Different API response format adapters

### 4. **Hardware Abstraction**
- Device drivers
- Printer adapters for different manufacturers
- Camera and scanner interfaces

### 5. **Web Services Integration**
- SOAP to REST adapters
- GraphQL to REST adapters
- Different API version adapters

## Advantages and Disadvantages

### Advantages
1. **Reusability**: Allows reuse of existing incompatible classes
2. **Separation of Concerns**: Separates interface conversion from business logic
3. **Open/Closed Principle**: Can add new adapters without modifying existing code
4. **Single Responsibility**: Each adapter has one clear purpose
5. **Flexibility**: Easy to switch between different implementations
6. **Legacy Integration**: Enables integration without modifying legacy code

### Disadvantages
1. **Complexity**: Adds an extra layer of abstraction
2. **Performance**: May introduce slight performance overhead
3. **Code Volume**: Increases the number of classes and interfaces
4. **Maintenance**: More components to maintain and test
5. **Two-way Adaptation**: Can be complex for bidirectional adaptation

## Adapter vs Other Patterns

### Adapter vs Bridge
- **Adapter**: Makes incompatible interfaces work together (after design)
- **Bridge**: Separates interface from implementation (during design)

### Adapter vs Decorator
- **Adapter**: Changes interface to match expectations
- **Decorator**: Adds new functionality to existing interface

### Adapter vs Facade
- **Adapter**: Adapts one interface to another
- **Facade**: Provides simplified interface to complex subsystem

### Adapter vs Proxy
- **Adapter**: Changes interface of target object
- **Proxy**: Provides same interface but controls access

## Best Practices

1. **Use Composition**: Prefer object adapter over class adapter
2. **Single Responsibility**: Each adapter should adapt one specific interface
3. **Interface Consistency**: Keep adapted interface consistent and intuitive
4. **Error Handling**: Handle conversion errors gracefully
5. **Documentation**: Document what interfaces are being adapted and why
6. **Performance**: Consider caching if adaptation is expensive
7. **Validation**: Validate input before adaptation
8. **Bidirectional Adaptation**: Consider if two-way adaptation is needed
9. **Factory Pattern**: Use factory to create appropriate adapters
10. **Testing**: Test both successful adaptation and error scenarios

## Common Pitfalls to Avoid

1. **Overuse**: Don't use adapter for every interface difference
2. **Leaky Abstraction**: Don't expose adaptee-specific details
3. **Complex Adaptation**: Keep adaptation logic simple and focused
4. **Ignoring Performance**: Consider performance implications of adaptation
5. **Tight Coupling**: Don't tightly couple adapter to specific implementations
6. **Missing Validation**: Validate data during interface conversion
7. **Error Propagation**: Handle errors appropriately during adaptation

## Conclusion

The Adapter Pattern is essential for integrating incompatible components without modifying existing code. It enables reuse of existing functionality and provides a clean way to work with third-party libraries and legacy systems.

Use the Adapter Pattern when you need to make incompatible interfaces work together, especially when integrating external libraries or legacy systems. The pattern promotes loose coupling and follows the Open/Closed Principle, making systems more maintainable and extensible.

Remember to keep adapters simple and focused on interface conversion, and consider using factory patterns to manage adapter creation and selection.