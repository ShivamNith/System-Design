# Strategy Design Pattern - Complete Guide

## Table of Contents
1. [Introduction](#introduction)
2. [Core Concepts](#core-concepts)
3. [When to Use Strategy Pattern](#when-to-use-strategy-pattern)
4. [Structure and Components](#structure-and-components)
5. [Implementation Steps](#implementation-steps)
6. [Detailed Examples](#detailed-examples)
7. [Real-World Applications](#real-world-applications)
8. [Advantages and Disadvantages](#advantages-and-disadvantages)
9. [Strategy vs Other Patterns](#strategy-vs-other-patterns)
10. [Best Practices](#best-practices)

## Introduction

The **Strategy Pattern** is a behavioral design pattern that defines a family of algorithms, encapsulates each one, and makes them interchangeable. It lets the algorithm vary independently from the clients that use it.

### Definition
> "Define a family of algorithms, encapsulate each one, and make them interchangeable. Strategy lets the algorithm vary independently from clients that use it." - Gang of Four

### Problem It Solves
Imagine you have a class that performs a specific operation, but this operation can be done in multiple ways. Without the Strategy Pattern, you might end up with:
- Large conditional statements (if-else or switch)
- Difficult to add new algorithms
- Violation of Open/Closed Principle
- Code duplication
- Tight coupling between algorithm implementation and client code

## Core Concepts

### Key Principles
1. **Encapsulation of Algorithms**: Each algorithm is encapsulated in its own class
2. **Interchangeability**: Algorithms can be swapped at runtime
3. **Composition over Inheritance**: Uses composition to change behavior
4. **Open/Closed Principle**: Open for extension, closed for modification

### Real-World Analogy
Think of transportation to reach a destination:
- You can drive a car
- You can take a bus
- You can ride a bicycle
- You can walk

Each is a different strategy to achieve the same goal (reaching the destination). You can choose the strategy based on factors like distance, cost, time, or weather.

## When to Use Strategy Pattern

Use the Strategy Pattern when:
1. **Multiple algorithms exist** for a specific task and the client can choose one
2. **You want to avoid conditional statements** for selecting behavior
3. **Algorithms need to be interchangeable** at runtime
4. **You have similar classes** that differ only in behavior
5. **An algorithm uses data** that clients shouldn't know about
6. **You need different variants** of an algorithm

### Red Flags Indicating Need for Strategy Pattern
- Large switch/if-else statements selecting algorithms
- Duplicate code across similar classes
- Need to add new algorithms frequently
- Runtime algorithm selection required
- Complex conditionals based on types or states

## Structure and Components

### UML Diagram Structure
```
┌─────────────────┐         ┌──────────────────┐
│     Context     │────────>│    Strategy      │
├─────────────────┤         │   <<interface>>  │
│ - strategy      │         ├──────────────────┤
├─────────────────┤         │ + execute()      │
│ + setStrategy() │         └──────────────────┘
│ + performAction()│                 △
└─────────────────┘                 │
                                    ├────────────┬────────────┬────────────┐
                          ┌─────────┴───┐ ┌──────┴──────┐ ┌──┴──────────┐
                          │ ConcreteA   │ │ ConcreteB   │ │ ConcreteC   │
                          ├─────────────┤ ├─────────────┤ ├─────────────┤
                          │ + execute() │ │ + execute() │ │ + execute() │
                          └─────────────┘ └─────────────┘ └─────────────┘
```

### Components

1. **Strategy Interface**: Declares the common interface for all supported algorithms
2. **Concrete Strategies**: Implement different variants of the algorithm
3. **Context**: Maintains a reference to a Strategy object and delegates algorithm execution
4. **Client**: Creates concrete strategy objects and passes them to the context

## Implementation Steps

1. **Identify the algorithm** that varies in your application
2. **Define the Strategy interface** with method(s) representing the algorithm
3. **Create Concrete Strategy classes** implementing the interface
4. **Create Context class** that:
   - Holds a reference to a Strategy
   - Provides a setter for the strategy
   - Delegates algorithm execution to the Strategy object
5. **Client code** creates and configures Context with appropriate Strategy

## Detailed Examples

### Example 1: Payment Processing System

```java
// Strategy Interface
public interface PaymentStrategy {
    boolean pay(double amount);
    String getPaymentType();
}

// Concrete Strategies
public class CreditCardPayment implements PaymentStrategy {
    private String cardNumber;
    private String cvv;
    private String expiryDate;
    
    public CreditCardPayment(String cardNumber, String cvv, String expiryDate) {
        this.cardNumber = cardNumber;
        this.cvv = cvv;
        this.expiryDate = expiryDate;
    }
    
    @Override
    public boolean pay(double amount) {
        if (validateCard()) {
            System.out.println("Processing credit card payment...");
            System.out.println("Card Number: ****" + cardNumber.substring(cardNumber.length() - 4));
            System.out.println("Amount: $" + amount);
            System.out.println("Payment successful!");
            return true;
        }
        System.out.println("Credit card validation failed!");
        return false;
    }
    
    @Override
    public String getPaymentType() {
        return "Credit Card";
    }
    
    private boolean validateCard() {
        // Simplified validation
        return cardNumber.length() == 16 && cvv.length() == 3;
    }
}

public class PayPalPayment implements PaymentStrategy {
    private String email;
    private String password;
    
    public PayPalPayment(String email, String password) {
        this.email = email;
        this.password = password;
    }
    
    @Override
    public boolean pay(double amount) {
        if (authenticate()) {
            System.out.println("Processing PayPal payment...");
            System.out.println("Account: " + email);
            System.out.println("Amount: $" + amount);
            System.out.println("PayPal payment successful!");
            return true;
        }
        System.out.println("PayPal authentication failed!");
        return false;
    }
    
    @Override
    public String getPaymentType() {
        return "PayPal";
    }
    
    private boolean authenticate() {
        // Simplified authentication
        return email.contains("@") && password.length() >= 8;
    }
}

public class CryptocurrencyPayment implements PaymentStrategy {
    private String walletAddress;
    private String privateKey;
    
    public CryptocurrencyPayment(String walletAddress, String privateKey) {
        this.walletAddress = walletAddress;
        this.privateKey = privateKey;
    }
    
    @Override
    public boolean pay(double amount) {
        if (validateWallet()) {
            System.out.println("Processing cryptocurrency payment...");
            System.out.println("Wallet: " + walletAddress.substring(0, 10) + "...");
            System.out.println("Amount: $" + amount + " (converted to crypto)");
            System.out.println("Broadcasting transaction to blockchain...");
            System.out.println("Transaction confirmed!");
            return true;
        }
        System.out.println("Wallet validation failed!");
        return false;
    }
    
    @Override
    public String getPaymentType() {
        return "Cryptocurrency";
    }
    
    private boolean validateWallet() {
        return walletAddress.length() > 20 && privateKey.length() > 10;
    }
}

// Context
public class ShoppingCart {
    private List<Item> items;
    private PaymentStrategy paymentStrategy;
    
    public ShoppingCart() {
        this.items = new ArrayList<>();
    }
    
    public void addItem(Item item) {
        items.add(item);
        System.out.println("Added " + item.getName() + " to cart");
    }
    
    public void removeItem(Item item) {
        items.remove(item);
        System.out.println("Removed " + item.getName() + " from cart");
    }
    
    public double calculateTotal() {
        return items.stream()
                .mapToDouble(Item::getPrice)
                .sum();
    }
    
    public void setPaymentStrategy(PaymentStrategy paymentStrategy) {
        this.paymentStrategy = paymentStrategy;
        System.out.println("Payment method set to: " + paymentStrategy.getPaymentType());
    }
    
    public boolean checkout() {
        if (paymentStrategy == null) {
            System.out.println("Please select a payment method!");
            return false;
        }
        
        double amount = calculateTotal();
        System.out.println("\n=== Checkout ===");
        System.out.println("Total amount: $" + amount);
        
        boolean paymentResult = paymentStrategy.pay(amount);
        
        if (paymentResult) {
            items.clear();
            System.out.println("Order completed successfully!");
        } else {
            System.out.println("Payment failed. Please try again.");
        }
        
        return paymentResult;
    }
}

// Supporting class
public class Item {
    private String name;
    private double price;
    
    public Item(String name, double price) {
        this.name = name;
        this.price = price;
    }
    
    public String getName() { return name; }
    public double getPrice() { return price; }
}
```

### Example 2: Data Compression System

```java
// Strategy Interface
public interface CompressionStrategy {
    byte[] compress(String data);
    String decompress(byte[] compressedData);
    String getAlgorithmName();
    double getCompressionRatio(String original, byte[] compressed);
}

// Concrete Strategies
public class ZipCompression implements CompressionStrategy {
    @Override
    public byte[] compress(String data) {
        System.out.println("Compressing using ZIP algorithm...");
        // Simplified compression simulation
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try (GZIPOutputStream gzip = new GZIPOutputStream(baos)) {
                gzip.write(data.getBytes("UTF-8"));
            }
            return baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Compression failed", e);
        }
    }
    
    @Override
    public String decompress(byte[] compressedData) {
        System.out.println("Decompressing using ZIP algorithm...");
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(compressedData);
            try (GZIPInputStream gzip = new GZIPInputStream(bais)) {
                byte[] buffer = new byte[1024];
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                int len;
                while ((len = gzip.read(buffer)) > 0) {
                    out.write(buffer, 0, len);
                }
                return out.toString("UTF-8");
            }
        } catch (IOException e) {
            throw new RuntimeException("Decompression failed", e);
        }
    }
    
    @Override
    public String getAlgorithmName() {
        return "ZIP (GZIP)";
    }
    
    @Override
    public double getCompressionRatio(String original, byte[] compressed) {
        return ((double) compressed.length / original.getBytes().length) * 100;
    }
}

public class RarCompression implements CompressionStrategy {
    @Override
    public byte[] compress(String data) {
        System.out.println("Compressing using RAR algorithm...");
        // Simulated RAR compression (in reality, would use a RAR library)
        String compressed = data.replaceAll("(.)\\1+", "$1");
        return compressed.getBytes();
    }
    
    @Override
    public String decompress(byte[] compressedData) {
        System.out.println("Decompressing using RAR algorithm...");
        // Simulated decompression
        return new String(compressedData);
    }
    
    @Override
    public String getAlgorithmName() {
        return "RAR";
    }
    
    @Override
    public double getCompressionRatio(String original, byte[] compressed) {
        return ((double) compressed.length / original.getBytes().length) * 100;
    }
}

public class LZ4Compression implements CompressionStrategy {
    @Override
    public byte[] compress(String data) {
        System.out.println("Compressing using LZ4 algorithm (fast compression)...");
        // Simulated LZ4 compression
        String compressed = data.replace("    ", "\t");
        return compressed.getBytes();
    }
    
    @Override
    public String decompress(byte[] compressedData) {
        System.out.println("Decompressing using LZ4 algorithm...");
        String compressed = new String(compressedData);
        return compressed.replace("\t", "    ");
    }
    
    @Override
    public String getAlgorithmName() {
        return "LZ4 (Fast)";
    }
    
    @Override
    public double getCompressionRatio(String original, byte[] compressed) {
        return ((double) compressed.length / original.getBytes().length) * 100;
    }
}

// Context
public class FileCompressor {
    private CompressionStrategy compressionStrategy;
    private Map<String, byte[]> compressedFiles;
    
    public FileCompressor() {
        this.compressedFiles = new HashMap<>();
    }
    
    public void setCompressionStrategy(CompressionStrategy strategy) {
        this.compressionStrategy = strategy;
        System.out.println("Compression algorithm set to: " + strategy.getAlgorithmName());
    }
    
    public void compressFile(String fileName, String content) {
        if (compressionStrategy == null) {
            throw new IllegalStateException("No compression strategy set!");
        }
        
        System.out.println("\nCompressing file: " + fileName);
        System.out.println("Original size: " + content.getBytes().length + " bytes");
        
        byte[] compressed = compressionStrategy.compress(content);
        compressedFiles.put(fileName, compressed);
        
        System.out.println("Compressed size: " + compressed.length + " bytes");
        double ratio = compressionStrategy.getCompressionRatio(content, compressed);
        System.out.println("Compression ratio: " + String.format("%.2f%%", ratio));
        System.out.println("Space saved: " + String.format("%.2f%%", 100 - ratio));
    }
    
    public String decompressFile(String fileName) {
        if (compressionStrategy == null) {
            throw new IllegalStateException("No compression strategy set!");
        }
        
        byte[] compressed = compressedFiles.get(fileName);
        if (compressed == null) {
            throw new IllegalArgumentException("File not found: " + fileName);
        }
        
        System.out.println("\nDecompressing file: " + fileName);
        return compressionStrategy.decompress(compressed);
    }
    
    public void compareStrategies(String fileName, String content) {
        CompressionStrategy[] strategies = {
            new ZipCompression(),
            new RarCompression(),
            new LZ4Compression()
        };
        
        System.out.println("\n=== Compression Comparison for: " + fileName + " ===");
        System.out.println("Original size: " + content.getBytes().length + " bytes\n");
        
        for (CompressionStrategy strategy : strategies) {
            byte[] compressed = strategy.compress(content);
            double ratio = strategy.getCompressionRatio(content, compressed);
            
            System.out.println(strategy.getAlgorithmName() + ":");
            System.out.println("  Compressed size: " + compressed.length + " bytes");
            System.out.println("  Compression ratio: " + String.format("%.2f%%", ratio));
            System.out.println("  Space saved: " + String.format("%.2f%%", 100 - ratio));
            System.out.println();
        }
    }
}
```

### Example 3: Sorting Algorithms

```java
// Strategy Interface
public interface SortingStrategy {
    void sort(int[] array);
    String getAlgorithmName();
    String getTimeComplexity();
    String getSpaceComplexity();
}

// Concrete Strategies
public class BubbleSortStrategy implements SortingStrategy {
    @Override
    public void sort(int[] array) {
        System.out.println("Sorting using Bubble Sort...");
        int n = array.length;
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (array[j] > array[j + 1]) {
                    int temp = array[j];
                    array[j] = array[j + 1];
                    array[j + 1] = temp;
                }
            }
        }
    }
    
    @Override
    public String getAlgorithmName() {
        return "Bubble Sort";
    }
    
    @Override
    public String getTimeComplexity() {
        return "O(n²)";
    }
    
    @Override
    public String getSpaceComplexity() {
        return "O(1)";
    }
}

public class QuickSortStrategy implements SortingStrategy {
    @Override
    public void sort(int[] array) {
        System.out.println("Sorting using Quick Sort...");
        quickSort(array, 0, array.length - 1);
    }
    
    private void quickSort(int[] array, int low, int high) {
        if (low < high) {
            int pi = partition(array, low, high);
            quickSort(array, low, pi - 1);
            quickSort(array, pi + 1, high);
        }
    }
    
    private int partition(int[] array, int low, int high) {
        int pivot = array[high];
        int i = (low - 1);
        
        for (int j = low; j < high; j++) {
            if (array[j] < pivot) {
                i++;
                int temp = array[i];
                array[i] = array[j];
                array[j] = temp;
            }
        }
        
        int temp = array[i + 1];
        array[i + 1] = array[high];
        array[high] = temp;
        
        return i + 1;
    }
    
    @Override
    public String getAlgorithmName() {
        return "Quick Sort";
    }
    
    @Override
    public String getTimeComplexity() {
        return "O(n log n) average, O(n²) worst";
    }
    
    @Override
    public String getSpaceComplexity() {
        return "O(log n)";
    }
}

public class MergeSortStrategy implements SortingStrategy {
    @Override
    public void sort(int[] array) {
        System.out.println("Sorting using Merge Sort...");
        mergeSort(array, 0, array.length - 1);
    }
    
    private void mergeSort(int[] array, int left, int right) {
        if (left < right) {
            int middle = left + (right - left) / 2;
            mergeSort(array, left, middle);
            mergeSort(array, middle + 1, right);
            merge(array, left, middle, right);
        }
    }
    
    private void merge(int[] array, int left, int middle, int right) {
        int n1 = middle - left + 1;
        int n2 = right - middle;
        
        int[] leftArray = new int[n1];
        int[] rightArray = new int[n2];
        
        System.arraycopy(array, left, leftArray, 0, n1);
        System.arraycopy(array, middle + 1, rightArray, 0, n2);
        
        int i = 0, j = 0, k = left;
        
        while (i < n1 && j < n2) {
            if (leftArray[i] <= rightArray[j]) {
                array[k++] = leftArray[i++];
            } else {
                array[k++] = rightArray[j++];
            }
        }
        
        while (i < n1) {
            array[k++] = leftArray[i++];
        }
        
        while (j < n2) {
            array[k++] = rightArray[j++];
        }
    }
    
    @Override
    public String getAlgorithmName() {
        return "Merge Sort";
    }
    
    @Override
    public String getTimeComplexity() {
        return "O(n log n)";
    }
    
    @Override
    public String getSpaceComplexity() {
        return "O(n)";
    }
}

// Context
public class DataSorter {
    private SortingStrategy sortingStrategy;
    
    public void setSortingStrategy(SortingStrategy strategy) {
        this.sortingStrategy = strategy;
        System.out.println("Sorting strategy set to: " + strategy.getAlgorithmName());
    }
    
    public void sort(int[] data) {
        if (sortingStrategy == null) {
            throw new IllegalStateException("No sorting strategy set!");
        }
        
        System.out.println("\nOriginal array: " + Arrays.toString(data));
        System.out.println("Algorithm: " + sortingStrategy.getAlgorithmName());
        System.out.println("Time Complexity: " + sortingStrategy.getTimeComplexity());
        System.out.println("Space Complexity: " + sortingStrategy.getSpaceComplexity());
        
        long startTime = System.nanoTime();
        sortingStrategy.sort(data);
        long endTime = System.nanoTime();
        
        System.out.println("Sorted array: " + Arrays.toString(data));
        System.out.println("Time taken: " + (endTime - startTime) / 1000 + " microseconds");
    }
    
    public void performanceComparison(int[] originalData) {
        SortingStrategy[] strategies = {
            new BubbleSortStrategy(),
            new QuickSortStrategy(),
            new MergeSortStrategy()
        };
        
        System.out.println("\n=== Performance Comparison ===");
        System.out.println("Array size: " + originalData.length);
        
        for (SortingStrategy strategy : strategies) {
            int[] data = originalData.clone();
            
            long startTime = System.nanoTime();
            strategy.sort(data);
            long endTime = System.nanoTime();
            
            System.out.println("\n" + strategy.getAlgorithmName() + ":");
            System.out.println("  Time: " + (endTime - startTime) / 1000 + " microseconds");
            System.out.println("  Time Complexity: " + strategy.getTimeComplexity());
            System.out.println("  Space Complexity: " + strategy.getSpaceComplexity());
            System.out.println("  Sorted correctly: " + isSorted(data));
        }
    }
    
    private boolean isSorted(int[] array) {
        for (int i = 0; i < array.length - 1; i++) {
            if (array[i] > array[i + 1]) {
                return false;
            }
        }
        return true;
    }
}
```

## Real-World Applications

### 1. **E-commerce Platforms**
- Payment methods (Credit Card, PayPal, Cryptocurrency)
- Shipping strategies (Standard, Express, Same-day)
- Pricing strategies (Regular, Discount, Premium)

### 2. **Navigation Systems**
- Route calculation (Fastest, Shortest, Scenic)
- Transportation modes (Driving, Walking, Public Transit)

### 3. **Game Development**
- AI behavior (Aggressive, Defensive, Balanced)
- Difficulty levels (Easy, Medium, Hard)
- Movement patterns (Walk, Run, Fly)

### 4. **Data Processing**
- Compression algorithms (ZIP, RAR, 7z)
- Encryption methods (AES, RSA, DES)
- Serialization formats (JSON, XML, Binary)

### 5. **Cloud Services**
- Storage strategies (Local, S3, Azure Blob)
- Caching strategies (In-memory, Redis, Memcached)
- Load balancing algorithms (Round-robin, Least connections)

## Advantages and Disadvantages

### Advantages
1. **Flexibility**: Easy to switch between algorithms at runtime
2. **Maintainability**: Each algorithm is encapsulated in its own class
3. **Testability**: Strategies can be tested independently
4. **Reusability**: Strategies can be reused across different contexts
5. **Clean Code**: Eliminates conditional statements for algorithm selection
6. **Open/Closed Principle**: New strategies can be added without modifying existing code

### Disadvantages
1. **Increased Number of Classes**: Each strategy requires its own class
2. **Client Awareness**: Clients must be aware of different strategies
3. **Communication Overhead**: Context and strategy communicate through interface
4. **Overkill for Simple Cases**: May be unnecessary for simple algorithms

## Strategy vs Other Patterns

### Strategy vs State Pattern
- **Strategy**: Focuses on interchangeable algorithms
- **State**: Focuses on changing object behavior based on internal state
- Strategy is chosen by client; State transitions internally

### Strategy vs Template Method
- **Strategy**: Uses composition and delegation
- **Template Method**: Uses inheritance
- Strategy allows runtime algorithm changes; Template Method is fixed at compile time

### Strategy vs Command Pattern
- **Command**: Encapsulates a request as an object
- **Strategy**: Encapsulates an algorithm
- Command focuses on "what" to do; Strategy focuses on "how" to do it

## Best Practices

1. **Use Interfaces**: Always define strategies through interfaces
2. **Dependency Injection**: Inject strategies rather than creating them internally
3. **Single Responsibility**: Each strategy should have one clear algorithm
4. **Meaningful Names**: Use descriptive names for strategies
5. **Default Strategy**: Consider providing a default strategy
6. **Strategy Factory**: Use factory pattern to create strategies when needed
7. **Immutable Strategies**: Make strategy objects stateless when possible
8. **Documentation**: Document the behavior and requirements of each strategy
9. **Performance Considerations**: Be aware of performance implications
10. **Testing**: Write unit tests for each strategy independently

## Common Pitfalls to Avoid

1. **Too Many Strategies**: Don't create strategies for trivial differences
2. **State in Strategies**: Avoid maintaining state in strategy objects
3. **Complex Interfaces**: Keep strategy interfaces simple and focused
4. **Forgetting Null Checks**: Always check if strategy is set before using
5. **Tight Coupling**: Don't let strategies depend on specific context implementations

## Conclusion

The Strategy Pattern is a powerful tool for managing algorithmic variations in your code. It promotes clean, maintainable, and flexible design by encapsulating algorithms and making them interchangeable. When used appropriately, it eliminates complex conditional logic and makes your system more extensible and testable.

Remember: Use Strategy Pattern when you have multiple ways to perform a task and want to choose the approach at runtime. It's particularly useful when you find yourself writing switch statements or if-else chains to select between different algorithms.