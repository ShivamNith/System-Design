package org.example.designPatterns.statePattern;

import java.util.*;

/**
 * Comprehensive Vending Machine Example using State Pattern
 * 
 * This example demonstrates a vending machine with multiple states:
 * - IdleState: Machine waiting for interaction
 * - CoinInsertedState: Money has been inserted
 * - ProductSelectedState: Product has been selected
 * - DispensingState: Product is being dispensed
 * - MaintenanceState: Machine is under maintenance
 * - OutOfOrderState: Machine is out of order
 */

// State interface defining all possible actions
interface VendingMachineState {
    void insertCoin(VendingMachine machine, double amount);
    void selectProduct(VendingMachine machine, String productCode);
    void dispenseProduct(VendingMachine machine);
    void returnMoney(VendingMachine machine);
    void refillProducts(VendingMachine machine, Map<String, Integer> products);
    void performMaintenance(VendingMachine machine);
    String getStateName();
}

// Product class to represent items in the vending machine
class Product {
    private final String code;
    private final String name;
    private final double price;
    private int quantity;

    public Product(String code, String name, double price, int quantity) {
        this.code = code;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    // Getters and setters
    public String getCode() { return code; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public int getQuantity() { return quantity; }
    
    public void setQuantity(int quantity) { this.quantity = quantity; }
    
    public boolean isAvailable() { return quantity > 0; }
    
    public void decrementQuantity() {
        if (quantity > 0) {
            quantity--;
        }
    }

    @Override
    public String toString() {
        return String.format("%s (%s) - $%.2f [%d available]", name, code, price, quantity);
    }
}

// Main vending machine context
class VendingMachine {
    private VendingMachineState currentState;
    private final Map<String, Product> products;
    private double insertedAmount;
    private String selectedProductCode;
    private final List<String> transactionLog;

    public VendingMachine() {
        this.products = new HashMap<>();
        this.transactionLog = new ArrayList<>();
        this.insertedAmount = 0.0;
        this.currentState = new IdleState();
        initializeProducts();
    }

    private void initializeProducts() {
        products.put("A1", new Product("A1", "Coca Cola", 1.50, 10));
        products.put("A2", new Product("A2", "Pepsi", 1.50, 8));
        products.put("B1", new Product("B1", "Chips", 2.00, 5));
        products.put("B2", new Product("B2", "Cookies", 1.75, 12));
        products.put("C1", new Product("C1", "Water", 1.00, 15));
        products.put("C2", new Product("C2", "Juice", 2.25, 6));
    }

    // State management
    public void setState(VendingMachineState state) {
        log("State transition: " + currentState.getStateName() + " -> " + state.getStateName());
        this.currentState = state;
    }

    public VendingMachineState getState() {
        return currentState;
    }

    // Public interface methods
    public void insertCoin(double amount) {
        currentState.insertCoin(this, amount);
    }

    public void selectProduct(String productCode) {
        currentState.selectProduct(this, productCode);
    }

    public void dispenseProduct() {
        currentState.dispenseProduct(this);
    }

    public void returnMoney() {
        currentState.returnMoney(this);
    }

    public void refillProducts(Map<String, Integer> newProducts) {
        currentState.refillProducts(this, newProducts);
    }

    public void performMaintenance() {
        currentState.performMaintenance(this);
    }

    // Utility methods
    public void displayProducts() {
        System.out.println("\n=== Available Products ===");
        products.values().forEach(System.out::println);
        System.out.println("==========================");
    }

    public void displayStatus() {
        System.out.println("\n=== Vending Machine Status ===");
        System.out.println("Current State: " + currentState.getStateName());
        System.out.println("Inserted Amount: $" + String.format("%.2f", insertedAmount));
        System.out.println("Selected Product: " + (selectedProductCode != null ? selectedProductCode : "None"));
        System.out.println("==============================");
    }

    // Internal methods used by states
    public void addMoney(double amount) {
        this.insertedAmount += amount;
        log("Added $" + String.format("%.2f", amount) + ", total: $" + String.format("%.2f", insertedAmount));
    }

    public double getInsertedAmount() {
        return insertedAmount;
    }

    public void setSelectedProduct(String productCode) {
        this.selectedProductCode = productCode;
    }

    public String getSelectedProduct() {
        return selectedProductCode;
    }

    public Product getProduct(String code) {
        return products.get(code);
    }

    public Map<String, Product> getAllProducts() {
        return new HashMap<>(products);
    }

    public boolean hasProduct(String code) {
        return products.containsKey(code);
    }

    public boolean isProductAvailable(String code) {
        Product product = products.get(code);
        return product != null && product.isAvailable();
    }

    public void consumeProduct(String code) {
        Product product = products.get(code);
        if (product != null) {
            product.decrementQuantity();
            log("Dispensed: " + product.getName());
        }
    }

    public void resetTransaction() {
        this.insertedAmount = 0.0;
        this.selectedProductCode = null;
    }

    public double returnInsertedMoney() {
        double amount = insertedAmount;
        insertedAmount = 0.0;
        log("Returned $" + String.format("%.2f", amount));
        return amount;
    }

    public void refillProduct(String code, int quantity) {
        Product product = products.get(code);
        if (product != null) {
            product.setQuantity(quantity);
            log("Refilled " + code + " with " + quantity + " units");
        }
    }

    public boolean needsMaintenance() {
        // Check if any product is running low (less than 3 items)
        return products.values().stream().anyMatch(p -> p.getQuantity() < 3);
    }

    public boolean isOutOfOrder() {
        // Machine is out of order if all products are out of stock
        return products.values().stream().allMatch(p -> p.getQuantity() == 0);
    }

    private void log(String message) {
        String logEntry = new Date() + ": " + message;
        transactionLog.add(logEntry);
        System.out.println("[LOG] " + message);
    }

    public List<String> getTransactionLog() {
        return new ArrayList<>(transactionLog);
    }
}

// Concrete State Classes

class IdleState implements VendingMachineState {
    @Override
    public void insertCoin(VendingMachine machine, double amount) {
        if (amount <= 0) {
            System.out.println("Please insert a valid amount!");
            return;
        }
        
        machine.addMoney(amount);
        machine.setState(new CoinInsertedState());
        System.out.println("Money accepted! Please select a product.");
    }

    @Override
    public void selectProduct(VendingMachine machine, String productCode) {
        System.out.println("Please insert money first!");
    }

    @Override
    public void dispenseProduct(VendingMachine machine) {
        System.out.println("Please insert money and select a product first!");
    }

    @Override
    public void returnMoney(VendingMachine machine) {
        System.out.println("No money to return.");
    }

    @Override
    public void refillProducts(VendingMachine machine, Map<String, Integer> products) {
        machine.setState(new MaintenanceState());
        machine.performMaintenance();
        // Refill after entering maintenance state
        for (Map.Entry<String, Integer> entry : products.entrySet()) {
            machine.refillProduct(entry.getKey(), entry.getValue());
        }
        machine.setState(new IdleState());
        System.out.println("Products refilled. Machine ready for use.");
    }

    @Override
    public void performMaintenance(VendingMachine machine) {
        machine.setState(new MaintenanceState());
        System.out.println("Entering maintenance mode...");
    }

    @Override
    public String getStateName() {
        return "Idle";
    }
}

class CoinInsertedState implements VendingMachineState {
    @Override
    public void insertCoin(VendingMachine machine, double amount) {
        if (amount <= 0) {
            System.out.println("Please insert a valid amount!");
            return;
        }
        
        machine.addMoney(amount);
        System.out.println("Additional money accepted! Total: $" + 
                         String.format("%.2f", machine.getInsertedAmount()));
    }

    @Override
    public void selectProduct(VendingMachine machine, String productCode) {
        if (!machine.hasProduct(productCode)) {
            System.out.println("Invalid product code: " + productCode);
            return;
        }

        if (!machine.isProductAvailable(productCode)) {
            System.out.println("Sorry, " + productCode + " is out of stock!");
            return;
        }

        Product product = machine.getProduct(productCode);
        if (machine.getInsertedAmount() < product.getPrice()) {
            System.out.println("Insufficient funds! Product costs $" + 
                             String.format("%.2f", product.getPrice()) + 
                             ", you have $" + String.format("%.2f", machine.getInsertedAmount()));
            return;
        }

        machine.setSelectedProduct(productCode);
        machine.setState(new ProductSelectedState());
        System.out.println("Product " + productCode + " selected. Dispensing...");
        machine.dispenseProduct(); // Automatically dispense
    }

    @Override
    public void dispenseProduct(VendingMachine machine) {
        System.out.println("Please select a product first!");
    }

    @Override
    public void returnMoney(VendingMachine machine) {
        double amount = machine.returnInsertedMoney();
        machine.setState(new IdleState());
        System.out.println("Returned $" + String.format("%.2f", amount));
    }

    @Override
    public void refillProducts(VendingMachine machine, Map<String, Integer> products) {
        System.out.println("Cannot refill while money is inserted. Please complete transaction or return money.");
    }

    @Override
    public void performMaintenance(VendingMachine machine) {
        System.out.println("Cannot perform maintenance while money is inserted. Please complete transaction or return money.");
    }

    @Override
    public String getStateName() {
        return "Coin Inserted";
    }
}

class ProductSelectedState implements VendingMachineState {
    @Override
    public void insertCoin(VendingMachine machine, double amount) {
        System.out.println("Product already selected. Dispensing in progress...");
    }

    @Override
    public void selectProduct(VendingMachine machine, String productCode) {
        System.out.println("Product already selected. Dispensing in progress...");
    }

    @Override
    public void dispenseProduct(VendingMachine machine) {
        String productCode = machine.getSelectedProduct();
        Product product = machine.getProduct(productCode);
        
        machine.setState(new DispensingState());
        
        // Simulate dispensing process
        try {
            System.out.println("Dispensing " + product.getName() + "...");
            Thread.sleep(2000); // Simulate dispensing delay
            
            machine.consumeProduct(productCode);
            
            // Calculate change
            double change = machine.getInsertedAmount() - product.getPrice();
            machine.resetTransaction();
            
            if (change > 0) {
                System.out.println("Dispensed " + product.getName() + 
                                 " and returned change: $" + String.format("%.2f", change));
            } else {
                System.out.println("Dispensed " + product.getName() + " - exact change!");
            }
            
            // Check machine status after dispensing
            if (machine.isOutOfOrder()) {
                machine.setState(new OutOfOrderState());
                System.out.println("Machine is now out of order - all products sold out!");
            } else if (machine.needsMaintenance()) {
                System.out.println("Warning: Machine needs maintenance (low stock)");
                machine.setState(new IdleState());
            } else {
                machine.setState(new IdleState());
            }
            
        } catch (InterruptedException e) {
            System.out.println("Dispensing interrupted!");
            machine.setState(new IdleState());
        }
    }

    @Override
    public void returnMoney(VendingMachine machine) {
        System.out.println("Cannot return money - dispensing in progress!");
    }

    @Override
    public void refillProducts(VendingMachine machine, Map<String, Integer> products) {
        System.out.println("Cannot refill during dispensing!");
    }

    @Override
    public void performMaintenance(VendingMachine machine) {
        System.out.println("Cannot perform maintenance during dispensing!");
    }

    @Override
    public String getStateName() {
        return "Product Selected";
    }
}

class DispensingState implements VendingMachineState {
    @Override
    public void insertCoin(VendingMachine machine, double amount) {
        System.out.println("Please wait, dispensing in progress...");
    }

    @Override
    public void selectProduct(VendingMachine machine, String productCode) {
        System.out.println("Please wait, dispensing in progress...");
    }

    @Override
    public void dispenseProduct(VendingMachine machine) {
        System.out.println("Already dispensing...");
    }

    @Override
    public void returnMoney(VendingMachine machine) {
        System.out.println("Cannot return money while dispensing!");
    }

    @Override
    public void refillProducts(VendingMachine machine, Map<String, Integer> products) {
        System.out.println("Cannot refill while dispensing!");
    }

    @Override
    public void performMaintenance(VendingMachine machine) {
        System.out.println("Cannot perform maintenance while dispensing!");
    }

    @Override
    public String getStateName() {
        return "Dispensing";
    }
}

class MaintenanceState implements VendingMachineState {
    @Override
    public void insertCoin(VendingMachine machine, double amount) {
        System.out.println("Machine is under maintenance. Please try again later.");
    }

    @Override
    public void selectProduct(VendingMachine machine, String productCode) {
        System.out.println("Machine is under maintenance. Please try again later.");
    }

    @Override
    public void dispenseProduct(VendingMachine machine) {
        System.out.println("Machine is under maintenance. Please try again later.");
    }

    @Override
    public void returnMoney(VendingMachine machine) {
        System.out.println("Machine is under maintenance. Please try again later.");
    }

    @Override
    public void refillProducts(VendingMachine machine, Map<String, Integer> products) {
        System.out.println("Performing maintenance and refilling products...");
        
        for (Map.Entry<String, Integer> entry : products.entrySet()) {
            machine.refillProduct(entry.getKey(), entry.getValue());
        }
        
        System.out.println("Maintenance complete!");
        machine.setState(new IdleState());
    }

    @Override
    public void performMaintenance(VendingMachine machine) {
        System.out.println("Maintenance already in progress...");
    }

    @Override
    public String getStateName() {
        return "Maintenance";
    }
}

class OutOfOrderState implements VendingMachineState {
    @Override
    public void insertCoin(VendingMachine machine, double amount) {
        System.out.println("Machine is out of order. Please contact customer service.");
    }

    @Override
    public void selectProduct(VendingMachine machine, String productCode) {
        System.out.println("Machine is out of order. Please contact customer service.");
    }

    @Override
    public void dispenseProduct(VendingMachine machine) {
        System.out.println("Machine is out of order. Please contact customer service.");
    }

    @Override
    public void returnMoney(VendingMachine machine) {
        if (machine.getInsertedAmount() > 0) {
            double amount = machine.returnInsertedMoney();
            System.out.println("Returned $" + String.format("%.2f", amount) + 
                             " due to machine being out of order.");
        } else {
            System.out.println("No money to return.");
        }
    }

    @Override
    public void refillProducts(VendingMachine machine, Map<String, Integer> products) {
        System.out.println("Refilling out-of-order machine...");
        machine.setState(new MaintenanceState());
        machine.refillProducts(products);
    }

    @Override
    public void performMaintenance(VendingMachine machine) {
        machine.setState(new MaintenanceState());
        System.out.println("Entering maintenance mode for out-of-order machine...");
    }

    @Override
    public String getStateName() {
        return "Out of Order";
    }
}

// Demonstration class
public class VendingMachineExample {
    public static void main(String[] args) {
        System.out.println("=== Vending Machine State Pattern Demo ===\n");
        
        VendingMachine machine = new VendingMachine();
        
        // Demo 1: Normal operation
        System.out.println("Demo 1: Normal Operation");
        System.out.println("------------------------");
        machine.displayProducts();
        machine.displayStatus();
        
        machine.insertCoin(2.00);
        machine.selectProduct("A1"); // Coca Cola - $1.50
        
        // Demo 2: Insufficient funds
        System.out.println("\nDemo 2: Insufficient Funds");
        System.out.println("---------------------------");
        machine.insertCoin(1.00);
        machine.selectProduct("B1"); // Chips - $2.00
        machine.returnMoney();
        
        // Demo 3: Invalid product
        System.out.println("\nDemo 3: Invalid Product");
        System.out.println("-----------------------");
        machine.insertCoin(3.00);
        machine.selectProduct("Z9"); // Invalid code
        machine.selectProduct("C2"); // Juice - $2.25
        
        // Demo 4: Multiple coins
        System.out.println("\nDemo 4: Multiple Coin Insertion");
        System.out.println("--------------------------------");
        machine.insertCoin(1.00);
        machine.insertCoin(0.75);
        machine.selectProduct("B2"); // Cookies - $1.75
        
        // Demo 5: Maintenance scenario
        System.out.println("\nDemo 5: Maintenance");
        System.out.println("-------------------");
        
        // Simulate low stock by buying multiple items
        for (int i = 0; i < 8; i++) {
            machine.insertCoin(2.00);
            machine.selectProduct("B1"); // Buy all chips
        }
        
        machine.displayStatus();
        
        // Refill products
        Map<String, Integer> refillMap = new HashMap<>();
        refillMap.put("B1", 10);
        refillMap.put("C2", 15);
        machine.refillProducts(refillMap);
        
        // Demo 6: Out of order scenario
        System.out.println("\nDemo 6: Out of Order Scenario");
        System.out.println("------------------------------");
        
        // Create a machine with limited stock and deplete it
        VendingMachine limitedMachine = new VendingMachine();
        
        // Simulate buying all products
        String[] products = {"A1", "A2", "B1", "B2", "C1", "C2"};
        for (String productCode : products) {
            Product product = limitedMachine.getProduct(productCode);
            for (int i = 0; i < product.getQuantity(); i++) {
                limitedMachine.insertCoin(product.getPrice());
                limitedMachine.selectProduct(productCode);
            }
        }
        
        limitedMachine.displayStatus();
        limitedMachine.insertCoin(1.00); // Try to use out-of-order machine
        
        // Demo 7: Error handling
        System.out.println("\nDemo 7: Error Handling");
        System.out.println("----------------------");
        machine.selectProduct("A1"); // Try without money
        machine.insertCoin(-1.00); // Invalid amount
        machine.insertCoin(0); // Zero amount
        
        // Final status
        System.out.println("\n=== Final Machine Status ===");
        machine.displayProducts();
        machine.displayStatus();
        
        // Show transaction log
        System.out.println("\n=== Transaction Log ===");
        List<String> log = machine.getTransactionLog();
        log.stream().limit(10).forEach(System.out::println); // Show last 10 entries
        if (log.size() > 10) {
            System.out.println("... and " + (log.size() - 10) + " more entries");
        }
    }
}

/**
 * Key Features Demonstrated:
 * 
 * 1. State Management:
 *    - IdleState: Initial state, accepts money
 *    - CoinInsertedState: Money inserted, can select product
 *    - ProductSelectedState: Product selected, initiates dispensing
 *    - DispensingState: Product being dispensed
 *    - MaintenanceState: Machine under maintenance
 *    - OutOfOrderState: Machine out of order
 * 
 * 2. State Transitions:
 *    - Natural flow: Idle -> CoinInserted -> ProductSelected -> Dispensing -> Idle
 *    - Error handling: Invalid operations in inappropriate states
 *    - Maintenance: Can enter from Idle or OutOfOrder states
 *    - OutOfOrder: Automatically triggered when all products sold out
 * 
 * 3. Business Logic:
 *    - Money handling and change calculation
 *    - Product inventory management
 *    - Transaction logging
 *    - Error validation and messaging
 * 
 * 4. Extensibility:
 *    - Easy to add new states (e.g., PaymentProcessingState)
 *    - State-specific behavior encapsulated
 *    - No complex conditional logic in main class
 * 
 * Benefits of State Pattern in this example:
 * - Clear separation of state-specific behavior
 * - Easy to understand and maintain
 * - Robust error handling
 * - Extensible for new features
 * - Eliminates complex conditional statements
 */