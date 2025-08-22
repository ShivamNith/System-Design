# Factory Design Pattern - Complete Guide

## Table of Contents
1. [Introduction](#introduction)
2. [Core Concepts](#core-concepts)
3. [Types of Factory Patterns](#types-of-factory-patterns)
4. [When to Use Factory Pattern](#when-to-use-factory-pattern)
5. [Structure and Components](#structure-and-components)
6. [SOLID Principles Applied](#solid-principles-applied)
7. [Implementation Examples](#implementation-examples)
8. [Real-World Applications](#real-world-applications)
9. [Advantages and Disadvantages](#advantages-and-disadvantages)
10. [Related Patterns](#related-patterns)
11. [Best Practices](#best-practices)

## Introduction

The **Factory Pattern** is a creational design pattern that provides an interface for creating objects without specifying their exact classes. It defines a method for creating objects, but lets subclasses decide which class to instantiate.

### Definition
> "Define an interface for creating an object, but let subclasses decide which class to instantiate. Factory Method lets a class defer instantiation to subclasses." - Gang of Four

### Problem It Solves
- Direct object creation using `new` operator creates tight coupling
- Client code needs to know about all concrete classes
- Adding new types requires modifying client code
- Violates Open/Closed Principle when adding new types

## Core Concepts

### Key Principles
1. **Encapsulation of Object Creation**: Creation logic is hidden from the client
2. **Loose Coupling**: Client depends on abstractions, not concrete classes
3. **Single Point of Creation**: Centralized object creation logic
4. **Runtime Decision**: Object type can be determined at runtime

### Real-World Analogy
Think of a restaurant where you order "pasta" without specifying how to make it. The kitchen (factory) decides whether to make spaghetti, fettuccine, or penne based on availability or chef's choice. You get pasta without knowing the preparation details.

## Types of Factory Patterns

### 1. Simple Factory
Not a GoF pattern but commonly used. A single factory class with a method that returns different object types based on input.

### 2. Factory Method Pattern
Defines an interface for creating objects, but lets subclasses decide which class to instantiate.

### 3. Abstract Factory Pattern
Provides an interface for creating families of related or dependent objects.

## When to Use Factory Pattern

Use the Factory Pattern when:
1. **Object creation is complex** and requires significant logic
2. **You don't know exact types** of objects beforehand
3. **You want to centralize** object creation logic
4. **You need to decouple** object creation from usage
5. **You want to support** multiple product lines
6. **Object creation involves** conditional logic

### Red Flags Indicating Need for Factory Pattern
- Multiple `if-else` or `switch` statements for object creation
- Duplicate object creation code
- Client code directly instantiating concrete classes
- Difficulty adding new types without modifying existing code

## Structure and Components

### Factory Method Pattern Structure
```
┌─────────────────┐         ┌──────────────────┐
│     Creator     │         │     Product      │
│   (Abstract)    │         │   (Interface)    │
├─────────────────┤         ├──────────────────┤
│ + factoryMethod()│────────>│ + operation()    │
│ + operation()   │         └──────────────────┘
└─────────────────┘                  △
         △                           │
         │                    ┌──────┴────────┬─────────────┐
┌────────┴────────┐     ┌─────────────┐ ┌─────────────┐ ┌─────────────┐
│ ConcreteCreator │     │ ConcreteA   │ │ ConcreteB   │ │ ConcreteC   │
├─────────────────┤     ├─────────────┤ ├─────────────┤ ├─────────────┤
│ + factoryMethod()│     │ + operation()│ │ + operation()│ │ + operation()│
└─────────────────┘     └─────────────┘ └─────────────┘ └─────────────┘
```

### Components
1. **Product**: Interface for objects the factory creates
2. **ConcreteProduct**: Specific implementations of Product
3. **Creator**: Abstract class/interface declaring factory method
4. **ConcreteCreator**: Implements factory method to return ConcreteProduct

## SOLID Principles Applied

### 🔹 Single Responsibility Principle (SRP)
- **Applied**: Factory class has single responsibility - creating objects
- **Benefit**: Object creation logic separated from business logic

### 🔹 Open/Closed Principle (OCP)
- **Applied**: Can add new products without modifying existing factory code
- **Benefit**: System open for extension, closed for modification

### 🔹 Liskov Substitution Principle (LSP)
- **Applied**: Factory returns Product interface, any ConcreteProduct can substitute
- **Benefit**: Client code works with any product type seamlessly

### 🔹 Interface Segregation Principle (ISP)
- **Applied**: Clients depend only on Product interface they use
- **Benefit**: No forced dependency on unused methods

### 🔹 Dependency Inversion Principle (DIP)
- **Applied**: High-level modules depend on abstractions (Product interface)
- **Benefit**: Decoupled from concrete implementations

## Implementation Examples

### Example 1: Vehicle Manufacturing System

```java
// Product Interface
public interface Vehicle {
    void manufacture();
    void testDrive();
    String getSpecifications();
    double getPrice();
}

// Concrete Products
public class Car implements Vehicle {
    private String model;
    private String engine;
    
    public Car(String model) {
        this.model = model;
        this.engine = "V6 Engine";
    }
    
    @Override
    public void manufacture() {
        System.out.println("Manufacturing car: " + model);
        System.out.println("Installing " + engine);
        System.out.println("Adding 4 wheels");
        System.out.println("Setting up car interior");
    }
    
    @Override
    public void testDrive() {
        System.out.println("Test driving car on highway");
    }
    
    @Override
    public String getSpecifications() {
        return "Car Model: " + model + ", Engine: " + engine + ", Seats: 5";
    }
    
    @Override
    public double getPrice() {
        return 25000.00;
    }
}

public class Motorcycle implements Vehicle {
    private String model;
    private int engineCC;
    
    public Motorcycle(String model) {
        this.model = model;
        this.engineCC = 600;
    }
    
    @Override
    public void manufacture() {
        System.out.println("Manufacturing motorcycle: " + model);
        System.out.println("Installing " + engineCC + "cc engine");
        System.out.println("Adding 2 wheels");
        System.out.println("Setting up motorcycle controls");
    }
    
    @Override
    public void testDrive() {
        System.out.println("Test driving motorcycle on test track");
    }
    
    @Override
    public String getSpecifications() {
        return "Motorcycle Model: " + model + ", Engine: " + engineCC + "cc";
    }
    
    @Override
    public double getPrice() {
        return 12000.00;
    }
}

public class Truck implements Vehicle {
    private String model;
    private int loadCapacity;
    
    public Truck(String model) {
        this.model = model;
        this.loadCapacity = 5000; // kg
    }
    
    @Override
    public void manufacture() {
        System.out.println("Manufacturing truck: " + model);
        System.out.println("Installing heavy-duty engine");
        System.out.println("Adding 6 wheels");
        System.out.println("Setting up cargo area");
    }
    
    @Override
    public void testDrive() {
        System.out.println("Test driving truck with load simulation");
    }
    
    @Override
    public String getSpecifications() {
        return "Truck Model: " + model + ", Load Capacity: " + loadCapacity + "kg";
    }
    
    @Override
    public double getPrice() {
        return 45000.00;
    }
}

// Simple Factory Implementation
public class VehicleFactory {
    public enum VehicleType {
        CAR, MOTORCYCLE, TRUCK, ELECTRIC_CAR
    }
    
    public static Vehicle createVehicle(VehicleType type, String model) {
        switch (type) {
            case CAR:
                return new Car(model);
            case MOTORCYCLE:
                return new Motorcycle(model);
            case TRUCK:
                return new Truck(model);
            case ELECTRIC_CAR:
                return new ElectricCar(model);
            default:
                throw new IllegalArgumentException("Unknown vehicle type: " + type);
        }
    }
}

// Factory Method Pattern Implementation
public abstract class VehicleManufacturer {
    // Factory Method
    public abstract Vehicle createVehicle(String model);
    
    // Template method using factory method
    public void deliverVehicle(String model, String customerName) {
        System.out.println("=== Vehicle Order Processing ===");
        System.out.println("Customer: " + customerName);
        
        Vehicle vehicle = createVehicle(model);
        
        System.out.println("\n1. Manufacturing:");
        vehicle.manufacture();
        
        System.out.println("\n2. Quality Testing:");
        vehicle.testDrive();
        
        System.out.println("\n3. Specifications:");
        System.out.println(vehicle.getSpecifications());
        
        System.out.println("\n4. Final Price: $" + vehicle.getPrice());
        System.out.println("Vehicle delivered successfully!\n");
    }
}

public class CarManufacturer extends VehicleManufacturer {
    @Override
    public Vehicle createVehicle(String model) {
        return new Car(model);
    }
}

public class MotorcycleManufacturer extends VehicleManufacturer {
    @Override
    public Vehicle createVehicle(String model) {
        return new Motorcycle(model);
    }
}

public class TruckManufacturer extends VehicleManufacturer {
    @Override
    public Vehicle createVehicle(String model) {
        return new Truck(model);
    }
}

// Abstract Factory Pattern Implementation
public interface VehicleComponentFactory {
    Engine createEngine();
    Wheels createWheels();
    Interior createInterior();
}

public class LuxuryVehicleFactory implements VehicleComponentFactory {
    @Override
    public Engine createEngine() {
        return new V8Engine();
    }
    
    @Override
    public Wheels createWheels() {
        return new AlloyWheels();
    }
    
    @Override
    public Interior createInterior() {
        return new LeatherInterior();
    }
}

public class StandardVehicleFactory implements VehicleComponentFactory {
    @Override
    public Engine createEngine() {
        return new V6Engine();
    }
    
    @Override
    public Wheels createWheels() {
        return new SteelWheels();
    }
    
    @Override
    public Interior createInterior() {
        return new FabricInterior();
    }
}
```

### Example 2: Database Connection Factory

```java
// Product Interface
public interface DatabaseConnection {
    void connect();
    void executeQuery(String query);
    void disconnect();
    String getDatabaseType();
}

// Concrete Products
public class MySQLConnection implements DatabaseConnection {
    private String host;
    private int port;
    private String database;
    
    public MySQLConnection(String host, int port, String database) {
        this.host = host;
        this.port = port;
        this.database = database;
    }
    
    @Override
    public void connect() {
        System.out.println("Connecting to MySQL database...");
        System.out.println("Host: " + host + ":" + port);
        System.out.println("Database: " + database);
        System.out.println("MySQL connection established");
    }
    
    @Override
    public void executeQuery(String query) {
        System.out.println("Executing MySQL query: " + query);
    }
    
    @Override
    public void disconnect() {
        System.out.println("Closing MySQL connection");
    }
    
    @Override
    public String getDatabaseType() {
        return "MySQL";
    }
}

public class PostgreSQLConnection implements DatabaseConnection {
    private String host;
    private int port;
    private String database;
    
    public PostgreSQLConnection(String host, int port, String database) {
        this.host = host;
        this.port = port;
        this.database = database;
    }
    
    @Override
    public void connect() {
        System.out.println("Connecting to PostgreSQL database...");
        System.out.println("Host: " + host + ":" + port);
        System.out.println("Database: " + database);
        System.out.println("PostgreSQL connection established");
    }
    
    @Override
    public void executeQuery(String query) {
        System.out.println("Executing PostgreSQL query: " + query);
    }
    
    @Override
    public void disconnect() {
        System.out.println("Closing PostgreSQL connection");
    }
    
    @Override
    public String getDatabaseType() {
        return "PostgreSQL";
    }
}

public class MongoDBConnection implements DatabaseConnection {
    private String connectionString;
    
    public MongoDBConnection(String connectionString) {
        this.connectionString = connectionString;
    }
    
    @Override
    public void connect() {
        System.out.println("Connecting to MongoDB...");
        System.out.println("Connection string: " + connectionString);
        System.out.println("MongoDB connection established");
    }
    
    @Override
    public void executeQuery(String query) {
        System.out.println("Executing MongoDB query: " + query);
    }
    
    @Override
    public void disconnect() {
        System.out.println("Closing MongoDB connection");
    }
    
    @Override
    public String getDatabaseType() {
        return "MongoDB";
    }
}

// Factory with configuration
public class DatabaseConnectionFactory {
    private static final Map<String, DatabaseConfig> configs = new HashMap<>();
    
    static {
        // Load configurations (in real app, from config file)
        configs.put("mysql-prod", new DatabaseConfig("mysql", "prod.db.com", 3306, "proddb"));
        configs.put("postgres-dev", new DatabaseConfig("postgres", "dev.db.com", 5432, "devdb"));
        configs.put("mongo-test", new DatabaseConfig("mongodb", "mongodb://test.db.com:27017/testdb", 0, ""));
    }
    
    public static DatabaseConnection createConnection(String configName) {
        DatabaseConfig config = configs.get(configName);
        if (config == null) {
            throw new IllegalArgumentException("Unknown configuration: " + configName);
        }
        
        switch (config.type.toLowerCase()) {
            case "mysql":
                return new MySQLConnection(config.host, config.port, config.database);
            case "postgres":
            case "postgresql":
                return new PostgreSQLConnection(config.host, config.port, config.database);
            case "mongodb":
            case "mongo":
                return new MongoDBConnection(config.host);
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
            return new MongoDBConnection(url);
        } else {
            throw new IllegalArgumentException("Unknown database URL format: " + url);
        }
    }
    
    private static DatabaseConnection parseMySQLUrl(String url) {
        // Parse MySQL JDBC URL
        // Format: jdbc:mysql://host:port/database
        String cleanUrl = url.replace("jdbc:mysql://", "");
        String[] parts = cleanUrl.split("/");
        String[] hostPort = parts[0].split(":");
        return new MySQLConnection(hostPort[0], Integer.parseInt(hostPort[1]), parts[1]);
    }
    
    private static DatabaseConnection parsePostgreSQLUrl(String url) {
        // Parse PostgreSQL JDBC URL
        String cleanUrl = url.replace("jdbc:postgresql://", "");
        String[] parts = cleanUrl.split("/");
        String[] hostPort = parts[0].split(":");
        return new PostgreSQLConnection(hostPort[0], Integer.parseInt(hostPort[1]), parts[1]);
    }
    
    private static class DatabaseConfig {
        String type;
        String host;
        int port;
        String database;
        
        DatabaseConfig(String type, String host, int port, String database) {
            this.type = type;
            this.host = host;
            this.port = port;
            this.database = database;
        }
    }
}
```

## Real-World Applications

### 1. **Framework Libraries**
- Spring Framework: BeanFactory, ApplicationContext
- JDBC: DriverManager.getConnection()
- Log4j: LoggerFactory

### 2. **GUI Libraries**
- Swing: UIManager for Look and Feel
- JavaFX: ControlFactory for UI controls
- Android: LayoutInflater for views

### 3. **Game Development**
- Character creation (different classes/races)
- Weapon/item generation
- Level/map generation

### 4. **Web Applications**
- Session factories
- Request/Response object creation
- API client generation

### 5. **Cloud Services**
- Resource provisioning (VMs, containers)
- Service instantiation
- Connection pooling

## Advantages and Disadvantages

### Advantages
1. **Loose Coupling**: Client code independent of concrete classes
2. **Code Reusability**: Factory logic can be reused
3. **Centralized Creation**: Single point for object creation logic
4. **Runtime Flexibility**: Object types determined at runtime
5. **Testability**: Easy to mock/substitute products
6. **Maintainability**: Changes isolated to factory

### Disadvantages
1. **Complexity**: Adds extra classes and interfaces
2. **Indirection**: Extra layer between client and products
3. **Overhead**: May be overkill for simple scenarios
4. **Learning Curve**: Pattern needs to be understood by team

## Related Patterns

### Factory vs Abstract Factory
- **Factory**: Creates single products
- **Abstract Factory**: Creates families of related products

### Factory vs Builder
- **Factory**: Creates objects in one shot
- **Builder**: Constructs objects step by step

### Factory vs Prototype
- **Factory**: Creates new instances from scratch
- **Prototype**: Creates by cloning existing instances

### Factory vs Singleton
- **Factory**: Creates multiple instances
- **Singleton**: Ensures single instance

## Best Practices

1. **Use Meaningful Names**: Factory methods should clearly indicate what they create
2. **Parameter Validation**: Validate factory method parameters
3. **Error Handling**: Handle unknown types gracefully
4. **Configuration**: Consider external configuration for flexibility
5. **Caching**: Cache created objects if appropriate
6. **Thread Safety**: Ensure factories are thread-safe if needed
7. **Documentation**: Document what each factory creates
8. **Testing**: Unit test each product creation path
9. **Avoid Complex Logic**: Keep factory methods simple
10. **Consider Dependency Injection**: Modern frameworks often replace factories

## Common Pitfalls

1. **Over-engineering**: Don't use factory for simple object creation
2. **God Factory**: Avoid factories that create too many unrelated types
3. **Type Explosion**: Too many product types can complicate design
4. **Static Methods**: Consider instance methods for testability
5. **Circular Dependencies**: Avoid products depending on their factory

## Conclusion

The Factory Pattern is a fundamental creational pattern that provides flexibility in object creation while maintaining loose coupling. By following SOLID principles, especially the Open/Closed Principle and Dependency Inversion Principle, it creates maintainable and extensible code. Use it when you need to decouple object creation from usage, but be mindful not to over-engineer simple scenarios.