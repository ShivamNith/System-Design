# SOLID Principles in System Design - Complete Guide

The SOLID principles are five fundamental design principles that guide object-oriented programming and system design. They were introduced by Robert C. Martin (Uncle Bob) and form the foundation for writing maintainable, flexible, and robust software systems.

## 1. Single Responsibility Principle (SRP)

### Definition
**"A class should have only one reason to change"** - meaning each class should have only one job or responsibility.

### Why It Matters
- **Maintainability**: When a class has multiple responsibilities, changes to one responsibility can affect others
- **Testing**: Classes with single responsibilities are easier to test
- **Reusability**: Focused classes are more likely to be reusable
- **Understanding**: Code is easier to read and understand

### Real-World Analogy
Think of a Swiss Army knife vs. specialized tools. While a Swiss Army knife can do many things, a specialized screwdriver is better at driving screws. In software, specialized classes are more maintainable.

### Detailed Example

**Violating SRP:**
```java
public class Employee {
    private String name;
    private String position;
    private double salary;
    
    // Employee data management
    public Employee(String name, String position, double salary) {
        this.name = name;
        this.position = position;
        this.salary = salary;
    }
    
    // Responsibility 1: Data access
    public String getName() { return name; }
    public String getPosition() { return position; }
    public double getSalary() { return salary; }
    
    // Responsibility 2: Business logic (salary calculation)
    public double calculatePay() {
        // Complex salary calculation logic
        if (position.equals("Manager")) {
            return salary * 1.2;
        } else if (position.equals("Developer")) {
            return salary * 1.1;
        }
        return salary;
    }
    
    // Responsibility 3: Data persistence
    public void saveToDatabase() {
        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/db");
        // Database saving logic
        System.out.println("Saving employee to database");
    }
    
    // Responsibility 4: Report generation
    public void generateReport() {
        System.out.println("=== Employee Report ===");
        System.out.println("Name: " + name);
        System.out.println("Position: " + position);
        System.out.println("Salary: " + calculatePay());
    }
    
    // Responsibility 5: Email notifications
    public void sendWelcomeEmail() {
        System.out.println("Sending welcome email to " + name);
        // Email sending logic
    }
}
```

**Problems with the above:**
- If email format changes, we modify Employee class
- If database schema changes, we modify Employee class
- If salary calculation rules change, we modify Employee class
- If report format changes, we modify Employee class
- Class becomes massive and hard to test

**Following SRP:**
```java
// Responsibility 1: Employee data model
public class Employee {
    private final String name;
    private final String position;
    private final double baseSalary;
    
    public Employee(String name, String position, double baseSalary) {
        this.name = name;
        this.position = position;
        this.baseSalary = baseSalary;
    }
    
    public String getName() { return name; }
    public String getPosition() { return position; }
    public double getBaseSalary() { return baseSalary; }
}

// Responsibility 2: Salary calculation business logic
public class SalaryCalculator {
    private final Map<String, Double> positionMultipliers;
    
    public SalaryCalculator() {
        positionMultipliers = Map.of(
            "Manager", 1.2,
            "Developer", 1.1,
            "Tester", 1.05
        );
    }
    
    public double calculatePay(Employee employee) {
        double multiplier = positionMultipliers.getOrDefault(employee.getPosition(), 1.0);
        return employee.getBaseSalary() * multiplier;
    }
}

// Responsibility 3: Data persistence
public class EmployeeRepository {
    private final Connection connection;
    
    public EmployeeRepository(Connection connection) {
        this.connection = connection;
    }
    
    public void save(Employee employee) {
        // Database saving logic
        System.out.println("Saving employee " + employee.getName() + " to database");
    }
    
    public Employee findById(Long id) {
        // Database retrieval logic
        return null; // Placeholder
    }
}

// Responsibility 4: Report generation
public class EmployeeReportGenerator {
    private final SalaryCalculator salaryCalculator;
    
    public EmployeeReportGenerator(SalaryCalculator salaryCalculator) {
        this.salaryCalculator = salaryCalculator;
    }
    
    public String generateReport(Employee employee) {
        StringBuilder report = new StringBuilder();
        report.append("=== Employee Report ===\n");
        report.append("Name: ").append(employee.getName()).append("\n");
        report.append("Position: ").append(employee.getPosition()).append("\n");
        report.append("Salary: ").append(salaryCalculator.calculatePay(employee)).append("\n");
        return report.toString();
    }
}

// Responsibility 5: Email notifications
public class EmailService {
    public void sendWelcomeEmail(Employee employee) {
        System.out.println("Sending welcome email to " + employee.getName());
        // Email sending logic with proper templates, SMTP configuration, etc.
    }
    
    public void sendPayslipEmail(Employee employee, double salary) {
        System.out.println("Sending payslip to " + employee.getName() + " for amount: " + salary);
    }
}
```

**Benefits of the refactored approach:**
- Each class has a clear, single purpose
- Changes to email templates only affect EmailService
- Changes to database schema only affect EmployeeRepository
- Salary calculation changes only affect SalaryCalculator
- Easy to unit test each component independently
- Classes can be reused in different contexts

## 2. Open/Closed Principle (OCP)

### Definition
**"Software entities should be open for extension but closed for modification"** - You should be able to add new functionality without changing existing code.

### Why It Matters
- **Stability**: Existing, tested code remains unchanged
- **Flexibility**: Easy to add new features
- **Risk Reduction**: No risk of breaking existing functionality
- **Maintainability**: New features don't require understanding entire codebase

### Key Techniques
- **Inheritance**: Extend base classes
- **Composition**: Use interfaces and dependency injection
- **Strategy Pattern**: Encapsulate algorithms
- **Template Method**: Define skeleton, let subclasses fill details

### Detailed Example

**Violating OCP:**
```java
public class PaymentProcessor {
    public void processPayment(String paymentType, double amount) {
        if (paymentType.equals("CREDIT_CARD")) {
            System.out.println("Processing credit card payment of $" + amount);
            // Credit card processing logic
            validateCreditCard();
            chargeCreditCard(amount);
            
        } else if (paymentType.equals("PAYPAL")) {
            System.out.println("Processing PayPal payment of $" + amount);
            // PayPal processing logic
            authenticatePayPal();
            chargePayPal(amount);
            
        } else if (paymentType.equals("BANK_TRANSFER")) {
            System.out.println("Processing bank transfer of $" + amount);
            // Bank transfer logic
            validateBankAccount();
            transferFunds(amount);
        }
        // What happens when we need to add cryptocurrency, Apple Pay, Google Pay?
        // We'd have to modify this method every time!
    }
    
    private void validateCreditCard() { /* implementation */ }
    private void chargeCreditCard(double amount) { /* implementation */ }
    private void authenticatePayPal() { /* implementation */ }
    private void chargePayPal(double amount) { /* implementation */ }
    private void validateBankAccount() { /* implementation */ }
    private void transferFunds(double amount) { /* implementation */ }
}
```

**Following OCP:**
```java
// Base abstraction
public interface PaymentMethod {
    void processPayment(double amount);
    boolean isValid();
    String getPaymentType();
}

// Concrete implementations
public class CreditCardPayment implements PaymentMethod {
    private String cardNumber;
    private String expiryDate;
    private String cvv;
    
    public CreditCardPayment(String cardNumber, String expiryDate, String cvv) {
        this.cardNumber = cardNumber;
        this.expiryDate = expiryDate;
        this.cvv = cvv;
    }
    
    @Override
    public void processPayment(double amount) {
        if (!isValid()) {
            throw new IllegalStateException("Invalid credit card");
        }
        System.out.println("Processing credit card payment of $" + amount);
        validateCard();
        chargeCard(amount);
        sendConfirmation();
    }
    
    @Override
    public boolean isValid() {
        return cardNumber != null && cardNumber.length() == 16 && 
               expiryDate != null && cvv != null;
    }
    
    @Override
    public String getPaymentType() {
        return "CREDIT_CARD";
    }
    
    private void validateCard() { /* Credit card validation */ }
    private void chargeCard(double amount) { /* Charging logic */ }
    private void sendConfirmation() { /* Send confirmation */ }
}

public class PayPalPayment implements PaymentMethod {
    private String email;
    private String password;
    
    public PayPalPayment(String email, String password) {
        this.email = email;
        this.password = password;
    }
    
    @Override
    public void processPayment(double amount) {
        if (!isValid()) {
            throw new IllegalStateException("Invalid PayPal credentials");
        }
        System.out.println("Processing PayPal payment of $" + amount);
        authenticateWithPayPal();
        chargePayPal(amount);
        sendReceipt();
    }
    
    @Override
    public boolean isValid() {
        return email != null && email.contains("@") && password != null;
    }
    
    @Override
    public String getPaymentType() {
        return "PAYPAL";
    }
    
    private void authenticateWithPayPal() { /* PayPal authentication */ }
    private void chargePayPal(double amount) { /* PayPal charging */ }
    private void sendReceipt() { /* Send receipt */ }
}

public class BankTransferPayment implements PaymentMethod {
    private String accountNumber;
    private String routingNumber;
    
    public BankTransferPayment(String accountNumber, String routingNumber) {
        this.accountNumber = accountNumber;
        this.routingNumber = routingNumber;
    }
    
    @Override
    public void processPayment(double amount) {
        if (!isValid()) {
            throw new IllegalStateException("Invalid bank account details");
        }
        System.out.println("Processing bank transfer of $" + amount);
        validateBankAccount();
        initializeTransfer(amount);
        waitForConfirmation();
    }
    
    @Override
    public boolean isValid() {
        return accountNumber != null && routingNumber != null &&
               accountNumber.length() >= 8 && routingNumber.length() == 9;
    }
    
    @Override
    public String getPaymentType() {
        return "BANK_TRANSFER";
    }
    
    private void validateBankAccount() { /* Bank validation */ }
    private void initializeTransfer(double amount) { /* Transfer logic */ }
    private void waitForConfirmation() { /* Wait for confirmation */ }
}

// The processor is now closed for modification but open for extension
public class PaymentProcessor {
    private final List<PaymentMethod> supportedMethods;
    
    public PaymentProcessor() {
        this.supportedMethods = new ArrayList<>();
    }
    
    public void addPaymentMethod(PaymentMethod method) {
        supportedMethods.add(method);
    }
    
    public void processPayment(PaymentMethod paymentMethod, double amount) {
        try {
            paymentMethod.processPayment(amount);
            logTransaction(paymentMethod.getPaymentType(), amount, "SUCCESS");
        } catch (Exception e) {
            logTransaction(paymentMethod.getPaymentType(), amount, "FAILED: " + e.getMessage());
            throw e;
        }
    }
    
    public List<String> getSupportedPaymentTypes() {
        return supportedMethods.stream()
                .map(PaymentMethod::getPaymentType)
                .collect(Collectors.toList());
    }
    
    private void logTransaction(String type, double amount, String status) {
        System.out.println("Transaction Log: " + type + " - $" + amount + " - " + status);
    }
}

// Adding new payment methods (like cryptocurrency) doesn't require modifying existing code
public class CryptocurrencyPayment implements PaymentMethod {
    private String walletAddress;
    private String privateKey;
    
    public CryptocurrencyPayment(String walletAddress, String privateKey) {
        this.walletAddress = walletAddress;
        this.privateKey = privateKey;
    }
    
    @Override
    public void processPayment(double amount) {
        if (!isValid()) {
            throw new IllegalStateException("Invalid cryptocurrency wallet");
        }
        System.out.println("Processing cryptocurrency payment of $" + amount);
        validateWallet();
        broadcastTransaction(amount);
        waitForBlockConfirmation();
    }
    
    @Override
    public boolean isValid() {
        return walletAddress != null && privateKey != null && 
               walletAddress.length() > 20;
    }
    
    @Override
    public String getPaymentType() {
        return "CRYPTOCURRENCY";
    }
    
    private void validateWallet() { /* Wallet validation */ }
    private void broadcastTransaction(double amount) { /* Blockchain transaction */ }
    private void waitForBlockConfirmation() { /* Wait for blockchain confirmation */ }
}
```

**Usage Example:**
```java
public class PaymentExample {
    public static void main(String[] args) {
        PaymentProcessor processor = new PaymentProcessor();
        
        // Process different payment types
        PaymentMethod creditCard = new CreditCardPayment("1234567890123456", "12/25", "123");
        PaymentMethod paypal = new PayPalPayment("user@email.com", "password");
        PaymentMethod crypto = new CryptocurrencyPayment("1A2B3C4D5E6F7G8H9I0J", "privatekey123");
        
        processor.processPayment(creditCard, 100.00);
        processor.processPayment(paypal, 250.00);
        processor.processPayment(crypto, 500.00);
    }
}
```

## 3. Liskov Substitution Principle (LSP)

### Definition
**"Objects of a superclass should be replaceable with objects of a subclass without breaking the application"** - Subclasses must be substitutable for their base classes.

### Why It Matters
- **Polymorphism**: Enables true polymorphic behavior
- **Reliability**: Ensures contracts are maintained
- **Predictability**: Subclasses behave as expected
- **Testing**: Tests written for base class work for all subclasses

### Key Rules for LSP
1. **Preconditions cannot be strengthened** in subclasses
2. **Postconditions cannot be weakened** in subclasses
3. **Invariants must be preserved** in subclasses
4. **History constraint** - subclasses shouldn't change the expected behavior

### Detailed Example

**Violating LSP:**
```java
public class Bird {
    protected int altitude = 0;
    
    public void fly() {
        altitude = 100;
        System.out.println("Flying at altitude: " + altitude);
    }
    
    public int getAltitude() {
        return altitude;
    }
    
    public void makeSound() {
        System.out.println("Generic bird sound");
    }
}

public class Eagle extends Bird {
    @Override
    public void fly() {
        altitude = 1000; // Eagles fly high
        System.out.println("Eagle soaring at altitude: " + altitude);
    }
    
    @Override
    public void makeSound() {
        System.out.println("Eagle screech!");
    }
}

public class Penguin extends Bird {
    @Override
    public void fly() {
        // This violates LSP! Penguins can't fly
        throw new UnsupportedOperationException("Penguins cannot fly!");
    }
    
    @Override
    public void makeSound() {
        System.out.println("Penguin squawk!");
    }
    
    public void swim() {
        System.out.println("Penguin swimming gracefully");
    }
}

// This code will break when we use Penguin!
public class BirdWatcher {
    public void observeBird(Bird bird) {
        bird.makeSound();
        bird.fly(); // This will throw exception for Penguin!
        System.out.println("Observed bird flying at: " + bird.getAltitude());
    }
    
    public static void main(String[] args) {
        BirdWatcher watcher = new BirdWatcher();
        
        Bird eagle = new Eagle();
        Bird penguin = new Penguin();
        
        watcher.observeBird(eagle);   // Works fine
        watcher.observeBird(penguin); // Throws exception - LSP violation!
    }
}
```

**Following LSP:**
```java
// Base abstraction for all birds
public abstract class Bird {
    protected String species;
    
    public Bird(String species) {
        this.species = species;
    }
    
    public abstract void makeSound();
    public abstract void move();
    
    public String getSpecies() {
        return species;
    }
}

// Interface for flying behavior
public interface Flyable {
    void fly();
    int getAltitude();
    void land();
}

// Interface for swimming behavior  
public interface Swimmable {
    void swim();
    void dive();
}

// Flying birds
public abstract class FlyingBird extends Bird implements Flyable {
    protected int altitude = 0;
    
    public FlyingBird(String species) {
        super(species);
    }
    
    @Override
    public void move() {
        fly();
    }
    
    @Override
    public int getAltitude() {
        return altitude;
    }
    
    @Override
    public void land() {
        altitude = 0;
        System.out.println(species + " has landed");
    }
}

// Swimming birds
public abstract class SwimmingBird extends Bird implements Swimmable {
    protected int depth = 0;
    
    public SwimmingBird(String species) {
        super(species);
    }
    
    @Override
    public void move() {
        swim();
    }
    
    protected int getDepth() {
        return depth;
    }
}

// Concrete implementations
public class Eagle extends FlyingBird {
    public Eagle() {
        super("Eagle");
    }
    
    @Override
    public void fly() {
        altitude = 1000;
        System.out.println("Eagle soaring majestically at altitude: " + altitude);
    }
    
    @Override
    public void makeSound() {
        System.out.println("Eagle lets out a powerful screech!");
    }
}

public class Sparrow extends FlyingBird {
    public Sparrow() {
        super("Sparrow");
    }
    
    @Override
    public void fly() {
        altitude = 50;
        System.out.println("Sparrow fluttering at altitude: " + altitude);
    }
    
    @Override
    public void makeSound() {
        System.out.println("Sparrow chirps melodiously!");
    }
}

public class Penguin extends SwimmingBird {
    public Penguin() {
        super("Penguin");
    }
    
    @Override
    public void swim() {
        depth = 20;
        System.out.println("Penguin swimming gracefully at depth: " + depth);
    }
    
    @Override
    public void dive() {
        depth = 100;
        System.out.println("Penguin diving deep to depth: " + depth);
    }
    
    @Override
    public void makeSound() {
        System.out.println("Penguin makes a distinctive squawk!");
    }
}

public class Duck extends Bird implements Flyable, Swimmable {
    private int altitude = 0;
    private int depth = 0;
    
    public Duck() {
        super("Duck");
    }
    
    @Override
    public void fly() {
        altitude = 200;
        System.out.println("Duck flying at moderate altitude: " + altitude);
    }
    
    @Override
    public int getAltitude() {
        return altitude;
    }
    
    @Override
    public void land() {
        altitude = 0;
        System.out.println("Duck has landed on water");
    }
    
    @Override
    public void swim() {
        depth = 2;
        System.out.println("Duck paddling on water surface");
    }
    
    @Override
    public void dive() {
        depth = 5;
        System.out.println("Duck diving briefly for food");
    }
    
    @Override
    public void move() {
        // Ducks can choose how to move
        if (altitude > 0) {
            fly();
        } else {
            swim();
        }
    }
    
    @Override
    public void makeSound() {
        System.out.println("Duck quacks loudly!");
    }
}

// Now our observer classes work correctly with LSP
public class BirdWatcher {
    public void observeBird(Bird bird) {
        System.out.println("Observing: " + bird.getSpecies());
        bird.makeSound();
        bird.move(); // This will work for all Bird subtypes
    }
    
    public void observeFlyingBird(Flyable bird) {
        if (bird instanceof Bird) {
            System.out.println("Watching flying behavior of: " + ((Bird) bird).getSpecies());
        }
        bird.fly();
        System.out.println("Flying at altitude: " + bird.getAltitude());
    }
    
    public void observeSwimmingBird(Swimmable bird) {
        if (bird instanceof Bird) {
            System.out.println("Watching swimming behavior of: " + ((Bird) bird).getSpecies());
        }
        bird.swim();
        bird.dive();
    }
    
    public static void main(String[] args) {
        BirdWatcher watcher = new BirdWatcher();
        
        Bird eagle = new Eagle();
        Bird penguin = new Penguin();
        Bird duck = new Duck();
        
        // All of these work correctly now
        watcher.observeBird(eagle);
        watcher.observeBird(penguin);
        watcher.observeBird(duck);
        
        // Specialized observations
        watcher.observeFlyingBird((Flyable) eagle);
        watcher.observeFlyingBird((Flyable) duck);
        
        watcher.observeSwimmingBird((Swimmable) penguin);
        watcher.observeSwimmingBird((Swimmable) duck);
    }
}
```

## 4. Interface Segregation Principle (ISP)

### Definition
**"Clients should not be forced to depend on interfaces they don't use"** - Create specific, focused interfaces rather than large, monolithic ones.

### Why It Matters
- **Decoupling**: Reduces dependencies between components
- **Flexibility**: Clients only depend on what they actually use
- **Maintainability**: Changes to unused interface methods don't affect clients
- **Implementation**: Easier to implement focused interfaces

### Key Strategies
- **Role-based interfaces**: Create interfaces based on client roles
- **Cohesive interfaces**: Group related methods together
- **Small interfaces**: Prefer multiple small interfaces over large ones
- **Composition**: Combine interfaces when needed

### Detailed Example

**Violating ISP:**
```java
// Fat interface - violates ISP
public interface MultiFunctionDevice {
    // Printing functionality
    void print(Document document);
    void printColor(Document document);
    void printDuplex(Document document);
    
    // Scanning functionality
    void scan(Document document);
    void scanToEmail(Document document, String email);
    void scanToPdf(Document document, String filename);
    
    // Faxing functionality
    void sendFax(Document document, String number);
    void receiveFax();
    void getFaxStatus();
    
    // Copying functionality
    void copy(Document document);
    void copyMultiple(Document document, int copies);
    
    // Advanced features
    void connectToWifi(String ssid, String password);
    void updateFirmware();
    void performMaintenance();
}

// Simple printer forced to implement everything
public class BasicPrinter implements MultiFunctionDevice {
    @Override
    public void print(Document document) {
        System.out.println("Printing document: " + document.getName());
    }
    
    @Override
    public void printColor(Document document) {
        throw new UnsupportedOperationException("Basic printer doesn't support color");
    }
    
    @Override
    public void printDuplex(Document document) {
        throw new UnsupportedOperationException("Basic printer doesn't support duplex");
    }
    
    // Forced to implement methods it doesn't need
    @Override
    public void scan(Document document) {
        throw new UnsupportedOperationException("Basic printer can't scan");
    }
    
    @Override
    public void scanToEmail(Document document, String email) {
        throw new UnsupportedOperationException("Basic printer can't scan");
    }
    
    @Override
    public void scanToPdf(Document document, String filename) {
        throw new UnsupportedOperationException("Basic printer can't scan");
    }
    
    @Override
    public void sendFax(Document document, String number) {
        throw new UnsupportedOperationException("Basic printer can't fax");
    }
    
    @Override
    public void receiveFax() {
        throw new UnsupportedOperationException("Basic printer can't fax");
    }
    
    @Override
    public void getFaxStatus() {
        throw new UnsupportedOperationException("Basic printer can't fax");
    }
    
    @Override
    public void copy(Document document) {
        throw new UnsupportedOperationException("Basic printer can't copy");
    }
    
    @Override
    public void copyMultiple(Document document, int copies) {
        throw new UnsupportedOperationException("Basic printer can't copy");
    }
    
    @Override
    public void connectToWifi(String ssid, String password) {
        throw new UnsupportedOperationException("Basic printer doesn't have WiFi");
    }
    
    @Override
    public void updateFirmware() {
        throw new UnsupportedOperationException("Basic printer can't update firmware");
    }
    
    @Override
    public void performMaintenance() {
        System.out.println("Basic maintenance check completed");
    }
}
```

**Following ISP:**
```java
// Document model
public class Document {
    private String name;
    private String content;
    
    public Document(String name, String content) {
        this.name = name;
        this.content = content;
    }
    
    public String getName() { return name; }
    public String getContent() { return content; }
}

// Segregated interfaces based on functionality
public interface Printable {
    void print(Document document);
}

public interface ColorPrintable extends Printable {
    void printColor(Document document);
}

public interface DuplexPrintable extends Printable {
    void printDuplex(Document document);
}

public interface Scannable {
    Document scan();
    void scanToEmail(Document document, String email);
    void scanToPdf(Document document, String filename);
}

public interface Faxable {
    void sendFax(Document document, String phoneNumber);
    void receiveFax();
    FaxStatus getFaxStatus();
}

public interface Copyable {
    void copy(Document document);
    void copyMultiple(Document document, int copies);
}

public interface NetworkEnabled {
    void connectToWifi(String ssid, String password);
    void connectToEthernet();
    boolean isConnected();
}

public interface Maintainable {
    void performMaintenance();
    void updateFirmware();
    MaintenanceStatus getStatus();
}

// Supporting classes
public enum FaxStatus {
    IDLE, SENDING, RECEIVING, ERROR
}

public class MaintenanceStatus {
    private boolean needsCleaning;
    private boolean needsTonerReplacement;
    private String lastMaintenanceDate;
    
    // Constructor and getters
    public MaintenanceStatus(boolean needsCleaning, boolean needsTonerReplacement, String lastMaintenanceDate) {
        this.needsCleaning = needsCleaning;
        this.needsTonerReplacement = needsTonerReplacement;
        this.lastMaintenanceDate = lastMaintenanceDate;
    }
    
    public boolean needsCleaning() { return needsCleaning; }
    public boolean needsTonerReplacement() { return needsTonerReplacement; }
    public String getLastMaintenanceDate() { return lastMaintenanceDate; }
}

// Now devices only implement what they actually support
public class BasicPrinter implements Printable, Maintainable {
    private String model;
    
    public BasicPrinter(String model) {
        this.model = model;
    }
    
    @Override
    public void print(Document document) {
        System.out.println(model + " printing document: " + document.getName());
        // Actual printing logic
    }
    
    @Override
    public void performMaintenance() {
        System.out.println(model + " performing basic maintenance check");
        // Check paper levels, clean print heads, etc.
    }
    
    @Override
    public void updateFirmware() {
        System.out.println(model + " firmware updated to latest version");
    }
    
    @Override
    public MaintenanceStatus getStatus() {
        return new MaintenanceStatus(false, false, "2024-01-15");
    }
}

public class ColorLaserPrinter implements ColorPrintable, DuplexPrintable, NetworkEnabled, Maintainable {
    private String model;
    private boolean connected;
    
    public ColorLaserPrinter(String model) {
        this.model = model;
        this.connected = false;
    }
    
    @Override
    public void print(Document document) {
        System.out.println(model + " laser printing document: " + document.getName());
    }
    
    @Override
    public void printColor(Document document) {
        System.out.println(model + " printing document in full color: " + document.getName());
    }
    
    @Override
    public void printDuplex(Document document) {
        System.out.println(model + " printing document double-sided: " + document.getName());
    }
    
    @Override
    public void connectToWifi(String ssid, String password) {
        System.out.println(model + " connecting to WiFi network: " + ssid);
        connected = true;
    }
    
    @Override
    public void connectToEthernet() {
        System.out.println(model + " connecting via Ethernet");
        connected = true;
    }
    
    @Override
    public boolean isConnected() {
        return connected;
    }
    
    @Override
    public void performMaintenance() {
        System.out.println(model + " performing comprehensive maintenance");
        // Clean laser assembly, calibrate colors, check toner levels
    }
    
    @Override
    public void updateFirmware() {
        System.out.println(model + " updating firmware with latest features");
    }
    
    @Override
    public MaintenanceStatus getStatus() {
        return new MaintenanceStatus(true, false, "2024-01-10");
    }
}

public class AllInOneDevice implements ColorPrintable, DuplexPrintable, Scannable, 
                                       Copyable, Faxable, NetworkEnabled, Maintainable {
    private String model;
    private boolean connected;
    private FaxStatus faxStatus;
    
    public AllInOneDevice(String model) {
        this.model = model;
        this.connected = false;
        this.faxStatus = FaxStatus.IDLE;
    }
    
    // Printing capabilities
    @Override
    public void print(Document document) {
        System.out.println(model + " printing: " + document.getName());
    }
    
    @Override
    public void printColor(Document document) {
        System.out.println(model + " printing in color: " + document.getName());
    }
    
    @Override
    public void printDuplex(Document document) {
        System.out.println(model + " printing duplex: " + document.getName());
    }
    
    // Scanning capabilities
    @Override
    public Document scan() {
        System.out.println(model + " scanning document");
        return new Document("Scanned_Document", "Scanned content");
    }
    
    @Override
    public void scanToEmail(Document document, String email) {
        System.out.println(model + " scanning and emailing to: " + email);
    }
    
    @Override
    public void scanToPdf(Document document, String filename) {
        System.out.println(model + " scanning to PDF: " + filename);
    }
    
    // Copying capabilities
    @Override
    public void copy(Document document) {
        System.out.println(model + " copying document: " + document.getName());
    }
    
    @Override
    public void copyMultiple(Document document, int copies) {
        System.out.println(model + " making " + copies + " copies of: " + document.getName());
    }
    
    // Fax capabilities
    @Override
    public void sendFax(Document document, String phoneNumber) {
        faxStatus = FaxStatus.SENDING;
        System.out.println(model + " sending fax to: " + phoneNumber);
        faxStatus = FaxStatus.IDLE;
    }
    
    @Override
    public void receiveFax() {
        faxStatus = FaxStatus.RECEIVING;
        System.out.println(model + " receiving fax");
        faxStatus = FaxStatus.IDLE;
    }
    
    @Override
    public FaxStatus getFaxStatus() {
        return faxStatus;
    }
    
    // Network capabilities
    @Override
    public void connectToWifi(String ssid, String password) {
        System.out.println(model + " connecting to WiFi: " + ssid);
        connected = true;
    }
    
    @Override
    public void connectToEthernet() {
        System.out.println(model + " connecting via Ethernet");
        connected = true;
    }
    
    @Override
    public boolean isConnected() {
        return connected;
    }
    
    // Maintenance capabilities
    @Override
    public void performMaintenance() {
        System.out.println(model + " performing full system maintenance");
    }
    
    @Override
    public void updateFirmware() {
        System.out.println(model + " updating firmware across all systems");
    }
    
    @Override
    public MaintenanceStatus getStatus() {
        return new MaintenanceStatus(true, true, "2024-01-05");
    }
}

// Client code that uses only what it needs
public class PrintJobManager {
    public void submitPrintJob(Printable printer, Document document) {
        System.out.println("Submitting print job...");
        printer.print(document);
    }
    
    public void submitColorPrintJob(ColorPrintable printer, Document document) {
        System.out.println("Submitting color print job...");
        printer.printColor(document);
    }
}

public class DocumentScanner {
    public Document scanDocument(Scannable scanner) {
        System.out.println("Starting scan operation...");
        return scanner.scan();
    }
    
    public void scanToEmail(Scannable scanner, Document document, String email) {
        System.out.println("Scanning document to email...");
        scanner.scanToEmail(document, email);
    }
}

public class NetworkManager {
    public void setupDevice(NetworkEnabled device, String ssid, String password) {
        if (!device.isConnected()) {
            System.out.println("Setting up network connection...");
            device.connectToWifi(ssid, password);
        } else {
            System.out.println("Device already connected");
        }
    }
}

public class MaintenanceManager {
    public void performScheduledMaintenance(Maintainable device) {
        MaintenanceStatus status = device.getStatus();
        
        if (status.needsCleaning() || status.needsTonerReplacement()) {
            System.out.println("Performing maintenance based on device status");
            device.performMaintenance();
        }
        
        System.out.println("Checking for firmware updates...");
        device.updateFirmware();
    }
}

// Usage example
public class OfficeManager {
    public static void main(String[] args) {
        // Different devices with different capabilities
        BasicPrinter basicPrinter = new BasicPrinter("HP LaserJet Basic");
        ColorLaserPrinter colorPrinter = new ColorLaserPrinter("Canon ColorMax Pro");
        AllInOneDevice multiDevice = new AllInOneDevice("Epson WorkForce All-in-One");
        
        // Managers only depend on the interfaces they need
        PrintJobManager printManager = new PrintJobManager();
        DocumentScanner scanManager = new DocumentScanner();
        NetworkManager networkManager = new NetworkManager();
        MaintenanceManager maintenanceManager = new MaintenanceManager();
        
        Document document = new Document("Report.pdf", "Monthly report content");
        
        // Each device is used according to its capabilities
        printManager.submitPrintJob(basicPrinter, document);
        printManager.submitColorPrintJob(colorPrinter, document);
        printManager.submitColorPrintJob(multiDevice, document);
        
        // Only devices with scanning capability can be used here
        scanManager.scanDocument(multiDevice);
        
        // Only network-enabled devices can be managed by network manager
        networkManager.setupDevice(colorPrinter, "OfficeWiFi", "password123");
        networkManager.setupDevice(multiDevice, "OfficeWiFi", "password123");
        
        // All devices support maintenance
        maintenanceManager.performScheduledMaintenance(basicPrinter);
        maintenanceManager.performScheduledMaintenance(colorPrinter);
        maintenanceManager.performScheduledMaintenance(multiDevice);
    }
}

## 5. Dependency Inversion Principle (DIP)

### Definition
**"High-level modules should not depend on low-level modules. Both should depend on abstractions. Abstractions should not depend on details. Details should depend on abstractions."**

### Why It Matters
- **Flexibility**: Easy to swap implementations
- **Testability**: Mock dependencies for unit testing
- **Maintainability**: Changes in low-level modules don't affect high-level logic
- **Reusability**: High-level modules can work with different implementations

### Key Concepts
- **Inversion of Control (IoC)**: Control flow is inverted
- **Dependency Injection**: Dependencies are provided externally
- **Abstractions**: Use interfaces/abstract classes instead of concrete classes
- **Composition over Inheritance**: Prefer composition with injected dependencies

### Detailed Example

**Violating DIP:**
```java
// Low-level modules (concrete implementations)
public class MySQLUserRepository {
    private Connection connection;
    
    public MySQLUserRepository() {
        try {
            this.connection = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/userdb", "user", "password");
        } catch (SQLException e) {
            throw new RuntimeException("Failed to connect to MySQL", e);
        }
    }
    
    public void save(User user) {
        String sql = "INSERT INTO users (name, email) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.executeUpdate();
            System.out.println("User saved to MySQL: " + user.getName());
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save user", e);
        }
    }
    
    public User findByEmail(String email) {
        String sql = "SELECT name, email FROM users WHERE email = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new User(rs.getString("name"), rs.getString("email"));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find user", e);
        }
        return null;
    }
}

public class EmailService {
    private String smtpHost = "smtp.gmail.com";
    private int smtpPort = 587;
    
    public void sendWelcomeEmail(String to, String name) {
        // Direct SMTP implementation
        System.out.println("Connecting to " + smtpHost + ":" + smtpPort);
        System.out.println("Sending welcome email to " + to);
        System.out.println("Subject: Welcome " + name + "!");
        System.out.println("Body: Welcome to our service, " + name + "!");
        System.out.println("Email sent successfully via SMTP");
    }
    
    public void sendPasswordReset(String to, String resetToken) {
        System.out.println("Connecting to " + smtpHost + ":" + smtpPort);
        System.out.println("Sending password reset email to " + to);
        System.out.println("Reset token: " + resetToken);
        System.out.println("Password reset email sent via SMTP");
    }
}

public class Logger {
    public void log(String message) {
        // Direct file system dependency
        System.out.println("[LOG] " + new Date() + ": " + message);
        // Write to file system...
    }
}

// High-level module directly depending on low-level modules - VIOLATES DIP
public class UserService {
    private MySQLUserRepository userRepository; // Direct dependency on MySQL
    private EmailService emailService;          // Direct dependency on SMTP
    private Logger logger;                      // Direct dependency on file logging
    
    public UserService() {
        // Hard-coded dependencies - difficult to test and change
        this.userRepository = new MySQLUserRepository();
        this.emailService = new EmailService();
        this.logger = new Logger();
    }
    
    public void registerUser(String name, String email) {
        try {
            logger.log("Starting user registration for: " + email);
            
            // Check if user already exists
            User existingUser = userRepository.findByEmail(email);
            if (existingUser != null) {
                logger.log("User already exists: " + email);
                throw new IllegalArgumentException("User already exists");
            }
            
            // Create and save new user
            User newUser = new User(name, email);
            userRepository.save(newUser);
            
            // Send welcome email
            emailService.sendWelcomeEmail(email, name);
            
            logger.log("User registration completed: " + email);
            
        } catch (Exception e) {
            logger.log("User registration failed: " + e.getMessage());
            throw e;
        }
    }
    
    public void resetPassword(String email) {
        try {
            logger.log("Password reset requested for: " + email);
            
            User user = userRepository.findByEmail(email);
            if (user == null) {
                logger.log("User not found for password reset: " + email);
                throw new IllegalArgumentException("User not found");
            }
            
            String resetToken = generateResetToken();
            emailService.sendPasswordReset(email, resetToken);
            
            logger.log("Password reset email sent to: " + email);
            
        } catch (Exception e) {
            logger.log("Password reset failed: " + e.getMessage());
            throw e;
        }
    }
    
    private String generateResetToken() {
        return "reset_" + System.currentTimeMillis();
    }
}

// User model
class User {
    private String name;
    private String email;
    
    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }
    
    public String getName() { return name; }
    public String getEmail() { return email; }
}
```

**Problems with the above:**
- UserService is tightly coupled to MySQL, SMTP, and file logging
- Impossible to unit test without actual database and email server
- Cannot switch to different database or email provider without changing UserService
- Hard to add features like caching, retry logic, or different notification methods

**Following DIP:**
```java
// User model (unchanged)
public class User {
    private String name;
    private String email;
    
    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }
    
    public String getName() { return name; }
    public String getEmail() { return email; }
    
    @Override
    public String toString() {
        return "User{name='" + name + "', email='" + email + "'}";
    }
}

// High-level abstractions (interfaces)
public interface UserRepository {
    void save(User user);
    User findByEmail(String email);
    List<User> findAll();
    void delete(String email);
}

public interface NotificationService {
    void sendWelcomeNotification(User user);
    void sendPasswordResetNotification(User user, String resetToken);
    void sendGenericNotification(User user, String subject, String message);
}

public interface Logger {
    void info(String message);
    void error(String message, Throwable throwable);
    void debug(String message);
}

public interface TokenGenerator {
    String generateResetToken();
    boolean validateToken(String token);
}

// Low-level implementations
public class MySQLUserRepository implements UserRepository {
    private final Connection connection;
    
    public MySQLUserRepository(Connection connection) {
        this.connection = connection;
    }
    
    @Override
    public void save(User user) {
        String sql = "INSERT INTO users (name, email) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.executeUpdate();
            System.out.println("✓ User saved to MySQL: " + user.getName());
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save user to MySQL", e);
        }
    }
    
    @Override
    public User findByEmail(String email) {
        String sql = "SELECT name, email FROM users WHERE email = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new User(rs.getString("name"), rs.getString("email"));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find user in MySQL", e);
        }
        return null;
    }
    
    @Override
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT name, email FROM users";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                users.add(new User(rs.getString("name"), rs.getString("email")));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find all users", e);
        }
        return users;
    }
    
    @Override
    public void delete(String email) {
        String sql = "DELETE FROM users WHERE email = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, email);
            stmt.executeUpdate();
            System.out.println("✓ User deleted from MySQL: " + email);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete user", e);
        }
    }
}

public class InMemoryUserRepository implements UserRepository {
    private final Map<String, User> users = new HashMap<>();
    
    @Override
    public void save(User user) {
        users.put(user.getEmail(), user);
        System.out.println("✓ User saved to memory: " + user.getName());
    }
    
    @Override
    public User findByEmail(String email) {
        return users.get(email);
    }
    
    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }
    
    @Override
    public void delete(String email) {
        users.remove(email);
        System.out.println("✓ User deleted from memory: " + email);
    }
}

public class EmailNotificationService implements NotificationService {
    private final String smtpHost;
    private final int smtpPort;
    private final Logger logger;
    
    public EmailNotificationService(String smtpHost, int smtpPort, Logger logger) {
        this.smtpHost = smtpHost;
        this.smtpPort = smtpPort;
        this.logger = logger;
    }
    
    @Override
    public void sendWelcomeNotification(User user) {
        logger.info("Sending welcome email to: " + user.getEmail());
        sendEmail(user.getEmail(), 
                 "Welcome " + user.getName() + "!", 
                 "Welcome to our service, " + user.getName() + "!");
    }
    
    @Override
    public void sendPasswordResetNotification(User user, String resetToken) {
        logger.info("Sending password reset email to: " + user.getEmail());
        sendEmail(user.getEmail(), 
                 "Password Reset Request", 
                 "Your password reset token: " + resetToken);
    }
    
    @Override
    public void sendGenericNotification(User user, String subject, String message) {
        logger.info("Sending generic email to: " + user.getEmail());
        sendEmail(user.getEmail(), subject, message);
    }
    
    private void sendEmail(String to, String subject, String body) {
        System.out.println("📧 Connecting to " + smtpHost + ":" + smtpPort);
        System.out.println("📧 To: " + to);
        System.out.println("📧 Subject: " + subject);
        System.out.println("📧 Body: " + body);
        System.out.println("✓ Email sent successfully");
    }
}

public class SMSNotificationService implements NotificationService {
    private final String apiKey;
    private final Logger logger;
    
    public SMSNotificationService(String apiKey, Logger logger) {
        this.apiKey = apiKey;
        this.logger = logger;
    }
    
    @Override
    public void sendWelcomeNotification(User user) {
        logger.info("Sending welcome SMS to user: " + user.getName());
        sendSMS(extractPhoneNumber(user.getEmail()), 
               "Welcome " + user.getName() + "! Thanks for joining our service.");
    }
    
    @Override
    public void sendPasswordResetNotification(User user, String resetToken) {
        logger.info("Sending password reset SMS to user: " + user.getName());
        sendSMS(extractPhoneNumber(user.getEmail()), 
               "Your password reset code: " + resetToken);
    }
    
    @Override
    public void sendGenericNotification(User user, String subject, String message) {
        logger.info("Sending SMS to user: " + user.getName());
        sendSMS(extractPhoneNumber(user.getEmail()), subject + ": " + message);
    }
    
    private void sendSMS(String phoneNumber, String message) {
        System.out.println("📱 SMS API Key: " + apiKey);
        System.out.println("📱 To: " + phoneNumber);
        System.out.println("📱 Message: " + message);
        System.out.println("✓ SMS sent successfully");
    }
    
    private String extractPhoneNumber(String email) {
        // Simplified - in reality, you'd have a proper phone number mapping
        return "+1234567890";
    }
}

public class ConsoleLogger implements Logger {
    @Override
    public void info(String message) {
        System.out.println("[INFO] " + new Date() + ": " + message);
    }
    
    @Override
    public void error(String message, Throwable throwable) {
        System.out.println("[ERROR] " + new Date() + ": " + message);
        if (throwable != null) {
            throwable.printStackTrace();
        }
    }
    
    @Override
    public void debug(String message) {
        System.out.println("[DEBUG] " + new Date() + ": " + message);
    }
}

public class FileLogger implements Logger {
    private final String logFilePath;
    
    public FileLogger(String logFilePath) {
        this.logFilePath = logFilePath;
    }
    
    @Override
    public void info(String message) {
        writeToFile("[INFO] " + new Date() + ": " + message);
    }
    
    @Override
    public void error(String message, Throwable throwable) {
        writeToFile("[ERROR] " + new Date() + ": " + message);
        if (throwable != null) {
            writeToFile("Stack trace: " + throwable.getMessage());
        }
    }
    
    @Override
    public void debug(String message) {
        writeToFile("[DEBUG] " + new Date() + ": " + message);
    }
    
    private void writeToFile(String message) {
        System.out.println("📁 Writing to " + logFilePath + ": " + message);
        // Actual file writing logic would go here
    }
}

public class JWTTokenGenerator implements TokenGenerator {
    private final String secretKey;
    
    public JWTTokenGenerator(String secretKey) {
        this.secretKey = secretKey;
    }
    
    @Override
    public String generateResetToken() {
        String token = "jwt_reset_" + System.currentTimeMillis() + "_" + secretKey.hashCode();
        System.out.println("🔑 Generated JWT reset token: " + token);
        return token;
    }
    
    @Override
    public boolean validateToken(String token) {
        boolean isValid = token.startsWith("jwt_reset_") && token.contains(String.valueOf(secretKey.hashCode()));
        System.out.println("🔍 Token validation result: " + isValid);
        return isValid;
    }
}

// High-level module now depends on abstractions
public class UserService {
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final Logger logger;
    private final TokenGenerator tokenGenerator;
    
    // Dependencies injected through constructor
    public UserService(UserRepository userRepository, 
                      NotificationService notificationService,
                      Logger logger,
                      TokenGenerator tokenGenerator) {
        this.userRepository = userRepository;
        this.notificationService = notificationService;
        this.logger = logger;
        this.tokenGenerator = tokenGenerator;
    }
    
    public void registerUser(String name, String email) {
        try {
            logger.info("Starting user registration for: " + email);
            
            // Check if user already exists
            User existingUser = userRepository.findByEmail(email);
            if (existingUser != null) {
                logger.info("User already exists: " + email);
                throw new IllegalArgumentException("User with email " + email + " already exists");
            }
            
            // Create and save new user
            User newUser = new User(name, email);
            userRepository.save(newUser);
            
            // Send welcome notification
            notificationService.sendWelcomeNotification(newUser);
            
            logger.info("User registration completed successfully: " + email);
            
        } catch (Exception e) {
            logger.error("User registration failed for " + email, e);
            throw e;
        }
    }
    
    public void resetPassword(String email) {
        try {
            logger.info("Password reset requested for: " + email);
            
            User user = userRepository.findByEmail(email);
            if (user == null) {
                logger.info("User not found for password reset: " + email);
                throw new IllegalArgumentException("User with email " + email + " not found");
            }
            
            String resetToken = tokenGenerator.generateResetToken();
            notificationService.sendPasswordResetNotification(user, resetToken);
            
            logger.info("Password reset notification sent to: " + email);
            
        } catch (Exception e) {
            logger.error("Password reset failed for " + email, e);
            throw e;
        }
    }
    
    public void sendBulkNotification(String subject, String message) {
        try {
            logger.info("Starting bulk notification: " + subject);
            
            List<User> allUsers = userRepository.findAll();
            for (User user : allUsers) {
                notificationService.sendGenericNotification(user, subject, message);
            }
            
            logger.info("Bulk notification completed. Sent to " + allUsers.size() + " users");
            
        } catch (Exception e) {
            logger.error("Bulk notification failed", e);
            throw e;
        }
    }
    
    public User getUser(String email) {
        logger.debug("Retrieving user: " + email);
        return userRepository.findByEmail(email);
    }
}

// Dependency injection container (simplified)
public class DIContainer {
    public static UserService createUserService(String profile) {
        Logger logger = new ConsoleLogger();
        TokenGenerator tokenGenerator = new JWTTokenGenerator("secret-key-123");
        
        switch (profile.toLowerCase()) {
            case "production":
                return createProductionUserService(logger, tokenGenerator);
            case "development":
                return createDevelopmentUserService(logger, tokenGenerator);
            case "testing":
                return createTestingUserService(logger, tokenGenerator);
            default:
                throw new IllegalArgumentException("Unknown profile: " + profile);
        }
    }
    
    private static UserService createProductionUserService(Logger logger, TokenGenerator tokenGenerator) {
        try {
            Connection connection = DriverManager.getConnection(
                "jdbc:mysql://prod-server:3306/userdb", "prod_user", "prod_password");
            UserRepository repository = new MySQLUserRepository(connection);
            NotificationService notificationService = new EmailNotificationService(
                "smtp.prod.com", 587, logger);
            
            return new UserService(repository, notificationService, logger, tokenGenerator);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create production user service", e);
        }
    }
    
    private static UserService createDevelopmentUserService(Logger logger, TokenGenerator tokenGenerator) {
        UserRepository repository = new InMemoryUserRepository();
        NotificationService notificationService = new EmailNotificationService(
            "smtp.dev.com", 1025, logger);
        
        return new UserService(repository, notificationService, logger, tokenGenerator);
    }
    
    private static UserService createTestingUserService(Logger logger, TokenGenerator tokenGenerator) {
        UserRepository repository = new InMemoryUserRepository();
        NotificationService notificationService = new SMSNotificationService("test-api-key", logger);
        
        return new UserService(repository, notificationService, logger, tokenGenerator);
    }
}

// Usage example demonstrating flexibility
public class UserManagementApp {
    public static void main(String[] args) {
        System.out.println("=== User Management System ===\n");
        
        // Easy to switch between different configurations
        String profile = args.length > 0 ? args[0] : "development";
        System.out.println("Running in " + profile + " mode\n");
        
        UserService userService = DIContainer.createUserService(profile);
        
        try {
            // Register some users
            userService.registerUser("John Doe", "john@example.com");
            userService.registerUser("Jane Smith", "jane@example.com");
            userService.registerUser("Bob Wilson", "bob@example.com");
            
            System.out.println();
            
            // Reset password
            userService.resetPassword("john@example.com");
            
            System.out.println();
            
            // Send bulk notification
            userService.sendBulkNotification("System Maintenance", 
                "The system will be under maintenance tonight from 2-4 AM.");
            
            System.out.println();
            
            // Try to register duplicate user
            try {
                userService.registerUser("John Duplicate", "john@example.com");
            } catch (IllegalArgumentException e) {
                System.out.println("❌ Expected error: " + e.getMessage());
            }
            
        } catch (Exception e) {
            System.out.println("❌ Application error: " + e.getMessage());
        }
    }
}
```

## Benefits of Following SOLID Principles

### 1. **Maintainability**
- Code is easier to understand and modify
- Changes in one area don't ripple through the entire system
- Bug fixes are localized and less risky

### 2. **Testability**
- Classes with single responsibilities are easier to unit test
- Dependencies can be mocked or stubbed
- Test coverage is more comprehensive and meaningful

### 3. **Flexibility and Extensibility**
- New features can be added without modifying existing code
- Different implementations can be swapped easily
- System can adapt to changing requirements

### 4. **Reusability**
- Well-designed components can be reused across projects
- Interfaces enable component composition
- Less code duplication

### 5. **Reduced Coupling**
- Components are less dependent on each other
- Changes in one component have minimal impact on others
- System is more modular and organized

### 6. **Better Collaboration**
- Teams can work on different components independently
- Clear interfaces define contracts between components
- Code reviews are more focused and effective

## Real-World Application

These principles are not just theoretical concepts but are actively used in:

- **Spring Framework**: Heavy use of DIP through dependency injection
- **Android Architecture Components**: ISP with specialized interfaces
- **Microservices**: SRP at the service level
- **Plugin Systems**: OCP for extending functionality
- **API Design**: LSP for consistent behavior across implementations

## Common Pitfalls to Avoid

1. **Over-engineering**: Don't create abstractions for everything from the start. Apply SOLID principles when complexity justifies them.

2. **Interface Bloat**: Avoid creating too many tiny interfaces that don't add real value.

3. **Premature Abstraction**: Don't abstract until you have at least 2-3 concrete implementations or a clear need for flexibility.

4. **Ignoring Performance**: While SOLID principles improve design, be mindful of performance implications of excessive layering.

5. **Religious Adherence**: These are guidelines, not absolute rules. Sometimes pragmatic solutions are better than perfect adherence.

## Conclusion

The SOLID principles work together to create a foundation for writing clean, maintainable, and robust software:

- **S**RP ensures each class has a clear purpose
- **O**CP makes systems extensible without modification
- **L**SP ensures consistent behavior in inheritance hierarchies
- **I**SP prevents clients from depending on unused functionality
- **D**IP creates flexible, testable architectures through abstraction

When applied thoughtfully, these principles lead to software systems that are easier to understand, modify, test, and extend. They form the backbone of modern software architecture patterns and frameworks.

Remember: Start simple and refactor toward SOLID principles as your system grows in complexity. The goal is maintainable code, not perfect adherence to every principle from day one.