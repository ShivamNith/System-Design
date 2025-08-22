# Decorator Design Pattern - Complete Guide

## Table of Contents
1. [Introduction](#introduction)
2. [Core Concepts](#core-concepts)
3. [When to Use Decorator Pattern](#when-to-use-decorator-pattern)
4. [Structure and Components](#structure-and-components)
5. [Implementation Steps](#implementation-steps)
6. [Detailed Examples](#detailed-examples)
7. [Real-World Applications](#real-world-applications)
8. [Advantages and Disadvantages](#advantages-and-disadvantages)
9. [Decorator vs Other Patterns](#decorator-vs-other-patterns)
10. [Best Practices](#best-practices)

## Introduction

The **Decorator Pattern** is a structural design pattern that allows you to add new behaviors to objects by placing them inside special wrapper objects that contain the behaviors. It provides a flexible alternative to subclassing for extending functionality.

### Definition
> "Attach additional responsibilities to an object dynamically. Decorators provide a flexible alternative to subclassing for extending functionality." - Gang of Four

### Problem It Solves
When you need to add functionality to objects without modifying their structure, you might face:
- Class explosion from creating subclasses for every combination of features
- Inability to add/remove responsibilities at runtime
- Tight coupling between core functionality and additional features
- Difficulty in combining multiple enhancements
- Violation of the Single Responsibility Principle

## Core Concepts

### Key Principles
1. **Dynamic Composition**: Add behaviors at runtime rather than compile time
2. **Wrapper Objects**: Decorators wrap the original object and add functionality
3. **Transparency**: Decorators implement the same interface as the components they decorate
4. **Layered Enhancement**: Multiple decorators can be chained together
5. **Single Responsibility**: Each decorator adds one specific functionality

### Real-World Analogy
Think of getting dressed:
- You start with a **base layer** (your body/underwear) - the core component
- You add a **shirt** - first decorator adding warmth and style
- You add a **jacket** - second decorator adding weather protection
- You add **accessories** (watch, glasses) - additional decorators
- Each layer adds functionality while maintaining the basic "person" interface
- You can mix and match layers based on needs
- You can add or remove layers dynamically

## When to Use Decorator Pattern

Use the Decorator Pattern when:
1. **You want to add responsibilities to objects dynamically** without affecting other objects
2. **You want to extend functionality** without subclassing
3. **Subclassing would create too many classes** for all combinations of features
4. **You need to add/remove features at runtime**
5. **You want to combine multiple enhancements** in different ways
6. **Extension by inheritance is impractical** or impossible

### Red Flags Indicating Need for Decorator Pattern
- Multiple inheritance scenarios that your language doesn't support
- Exponential growth in subclasses for feature combinations
- Need to add optional features that can be combined
- Requirements to modify object behavior at runtime
- Wanting to follow the "prefer composition over inheritance" principle

## Structure and Components

### UML Diagram Structure
```
┌─────────────────────┐
│     Component       │
│   <<interface>>     │
├─────────────────────┤
│ + operation()       │
└─────────────────────┘
           △
           │
    ┌──────┴──────┐
    │             │
┌───────────┐ ┌─────────────────────┐
│ConcreteComponent│ │    Decorator        │
├───────────┤ │   <<abstract>>      │
│+ operation│ ├─────────────────────┤
└───────────┘ │ - component: Component│
              │ + operation()       │
              └─────────────────────┘
                        △
                        │
              ┌─────────────────────┐
              │ ConcreteDecorator   │
              ├─────────────────────┤
              │ + operation()       │
              │ + addedBehavior()   │
              └─────────────────────┘
```

### Components

1. **Component**: Defines the interface for objects that can have responsibilities added dynamically
2. **ConcreteComponent**: The original object to which additional responsibilities can be attached
3. **Decorator**: Maintains a reference to a Component object and defines an interface that conforms to Component's interface
4. **ConcreteDecorator**: Adds responsibilities to the component

## Implementation Steps

1. **Define the Component interface** with the operations that can be decorated
2. **Create ConcreteComponent** implementing the base functionality
3. **Create abstract Decorator class** that wraps a Component and delegates operations
4. **Implement ConcreteDecorators** that add specific functionality
5. **Client code** combines decorators as needed

## Detailed Examples

### Example 1: Coffee Shop Ordering System

```java
// Component interface
public interface Coffee {
    String getDescription();
    double getCost();
    String getSize();
    void setSize(String size);
    List<String> getIngredients();
    int getPreparationTime(); // in minutes
    int getCalories();
    boolean isAvailable();
}

// Base concrete component
public class SimpleCoffee implements Coffee {
    private String size;
    private List<String> ingredients;
    private boolean available;
    
    public SimpleCoffee() {
        this.size = "Medium";
        this.ingredients = Arrays.asList("Coffee beans", "Water");
        this.available = true;
    }
    
    @Override
    public String getDescription() {
        return size + " Simple Coffee";
    }
    
    @Override
    public double getCost() {
        switch (size.toLowerCase()) {
            case "small": return 2.50;
            case "medium": return 3.00;
            case "large": return 3.50;
            default: return 3.00;
        }
    }
    
    @Override
    public String getSize() {
        return size;
    }
    
    @Override
    public void setSize(String size) {
        this.size = size;
    }
    
    @Override
    public List<String> getIngredients() {
        return new ArrayList<>(ingredients);
    }
    
    @Override
    public int getPreparationTime() {
        return 3; // 3 minutes for basic coffee
    }
    
    @Override
    public int getCalories() {
        return 5; // Basic black coffee has minimal calories
    }
    
    @Override
    public boolean isAvailable() {
        return available;
    }
    
    public void setAvailable(boolean available) {
        this.available = available;
    }
}

// Alternative concrete component
public class Espresso implements Coffee {
    private String size;
    private List<String> ingredients;
    
    public Espresso() {
        this.size = "Small"; // Espresso is typically small
        this.ingredients = Arrays.asList("Espresso beans", "Water");
    }
    
    @Override
    public String getDescription() {
        return size + " Espresso";
    }
    
    @Override
    public double getCost() {
        switch (size.toLowerCase()) {
            case "small": return 2.00;
            case "medium": return 2.25; // Double shot
            case "large": return 2.50; // Triple shot
            default: return 2.00;
        }
    }
    
    @Override
    public String getSize() {
        return size;
    }
    
    @Override
    public void setSize(String size) {
        this.size = size;
    }
    
    @Override
    public List<String> getIngredients() {
        return new ArrayList<>(ingredients);
    }
    
    @Override
    public int getPreparationTime() {
        return 2; // Espresso is quicker
    }
    
    @Override
    public int getCalories() {
        return 3; // Pure espresso has very few calories
    }
    
    @Override
    public boolean isAvailable() {
        return true;
    }
}

// Abstract decorator
public abstract class CoffeeDecorator implements Coffee {
    protected Coffee coffee;
    
    public CoffeeDecorator(Coffee coffee) {
        this.coffee = coffee;
    }
    
    @Override
    public String getDescription() {
        return coffee.getDescription();
    }
    
    @Override
    public double getCost() {
        return coffee.getCost();
    }
    
    @Override
    public String getSize() {
        return coffee.getSize();
    }
    
    @Override
    public void setSize(String size) {
        coffee.setSize(size);
    }
    
    @Override
    public List<String> getIngredients() {
        return coffee.getIngredients();
    }
    
    @Override
    public int getPreparationTime() {
        return coffee.getPreparationTime();
    }
    
    @Override
    public int getCalories() {
        return coffee.getCalories();
    }
    
    @Override
    public boolean isAvailable() {
        return coffee.isAvailable();
    }
}

// Concrete decorators
public class MilkDecorator extends CoffeeDecorator {
    private MilkType milkType;
    
    public enum MilkType {
        WHOLE("Whole Milk", 0.50, 20, 1),
        SKIM("Skim Milk", 0.50, 10, 1),
        ALMOND("Almond Milk", 0.75, 15, 1),
        SOY("Soy Milk", 0.75, 18, 1),
        OAT("Oat Milk", 0.80, 25, 1);
        
        private final String name;
        private final double cost;
        private final int calories;
        private final int prepTime;
        
        MilkType(String name, double cost, int calories, int prepTime) {
            this.name = name;
            this.cost = cost;
            this.calories = calories;
            this.prepTime = prepTime;
        }
        
        public String getName() { return name; }
        public double getCost() { return cost; }
        public int getCalories() { return calories; }
        public int getPrepTime() { return prepTime; }
    }
    
    public MilkDecorator(Coffee coffee, MilkType milkType) {
        super(coffee);
        this.milkType = milkType;
    }
    
    public MilkDecorator(Coffee coffee) {
        this(coffee, MilkType.WHOLE);
    }
    
    @Override
    public String getDescription() {
        return coffee.getDescription() + ", " + milkType.getName();
    }
    
    @Override
    public double getCost() {
        return coffee.getCost() + milkType.getCost();
    }
    
    @Override
    public List<String> getIngredients() {
        List<String> ingredients = new ArrayList<>(coffee.getIngredients());
        ingredients.add(milkType.getName());
        return ingredients;
    }
    
    @Override
    public int getPreparationTime() {
        return coffee.getPreparationTime() + milkType.getPrepTime();
    }
    
    @Override
    public int getCalories() {
        return coffee.getCalories() + milkType.getCalories();
    }
    
    public MilkType getMilkType() {
        return milkType;
    }
}

public class SugarDecorator extends CoffeeDecorator {
    private int packets;
    private SugarType sugarType;
    
    public enum SugarType {
        WHITE("White Sugar", 0.0, 16, 0),
        BROWN("Brown Sugar", 0.10, 17, 0),
        HONEY("Honey", 0.25, 21, 1),
        STEVIA("Stevia", 0.15, 0, 0),
        ARTIFICIAL("Artificial Sweetener", 0.05, 0, 0);
        
        private final String name;
        private final double costPerPacket;
        private final int caloriesPerPacket;
        private final int prepTime;
        
        SugarType(String name, double cost, int calories, int prepTime) {
            this.name = name;
            this.costPerPacket = cost;
            this.caloriesPerPacket = calories;
            this.prepTime = prepTime;
        }
        
        public String getName() { return name; }
        public double getCostPerPacket() { return costPerPacket; }
        public int getCaloriesPerPacket() { return caloriesPerPacket; }
        public int getPrepTime() { return prepTime; }
    }
    
    public SugarDecorator(Coffee coffee, int packets, SugarType sugarType) {
        super(coffee);
        this.packets = Math.max(0, packets);
        this.sugarType = sugarType;
    }
    
    public SugarDecorator(Coffee coffee, int packets) {
        this(coffee, packets, SugarType.WHITE);
    }
    
    public SugarDecorator(Coffee coffee) {
        this(coffee, 1, SugarType.WHITE);
    }
    
    @Override
    public String getDescription() {
        if (packets == 0) return coffee.getDescription();
        
        String packetText = packets == 1 ? "packet" : "packets";
        return coffee.getDescription() + ", " + packets + " " + packetText + " of " + sugarType.getName();
    }
    
    @Override
    public double getCost() {
        return coffee.getCost() + (packets * sugarType.getCostPerPacket());
    }
    
    @Override
    public List<String> getIngredients() {
        List<String> ingredients = new ArrayList<>(coffee.getIngredients());
        if (packets > 0) {
            ingredients.add(packets + " packet(s) of " + sugarType.getName());
        }
        return ingredients;
    }
    
    @Override
    public int getPreparationTime() {
        return coffee.getPreparationTime() + (packets > 0 ? sugarType.getPrepTime() : 0);
    }
    
    @Override
    public int getCalories() {
        return coffee.getCalories() + (packets * sugarType.getCaloriesPerPacket());
    }
    
    public int getPackets() {
        return packets;
    }
    
    public SugarType getSugarType() {
        return sugarType;
    }
}

public class FlavorDecorator extends CoffeeDecorator {
    private FlavorType flavorType;
    private double intensity; // 0.5 = light, 1.0 = normal, 1.5 = strong
    
    public enum FlavorType {
        VANILLA("Vanilla", 0.60, 15, 1),
        CARAMEL("Caramel", 0.70, 25, 1),
        HAZELNUT("Hazelnut", 0.65, 20, 1),
        CINNAMON("Cinnamon", 0.50, 5, 1),
        CHOCOLATE("Chocolate", 0.75, 30, 2),
        PEPPERMINT("Peppermint", 0.60, 10, 1),
        COCONUT("Coconut", 0.65, 18, 1);
        
        private final String name;
        private final double baseCost;
        private final int baseCalories;
        private final int prepTime;
        
        FlavorType(String name, double cost, int calories, int prepTime) {
            this.name = name;
            this.baseCost = cost;
            this.baseCalories = calories;
            this.prepTime = prepTime;
        }
        
        public String getName() { return name; }
        public double getBaseCost() { return baseCost; }
        public int getBaseCalories() { return baseCalories; }
        public int getPrepTime() { return prepTime; }
    }
    
    public FlavorDecorator(Coffee coffee, FlavorType flavorType, double intensity) {
        super(coffee);
        this.flavorType = flavorType;
        this.intensity = Math.max(0.5, Math.min(2.0, intensity));
    }
    
    public FlavorDecorator(Coffee coffee, FlavorType flavorType) {
        this(coffee, flavorType, 1.0);
    }
    
    @Override
    public String getDescription() {
        String intensityDesc = intensity == 0.5 ? "Light " : 
                              intensity == 1.5 ? "Strong " : 
                              intensity == 2.0 ? "Extra Strong " : "";
        return coffee.getDescription() + ", " + intensityDesc + flavorType.getName() + " flavor";
    }
    
    @Override
    public double getCost() {
        return coffee.getCost() + (flavorType.getBaseCost() * intensity);
    }
    
    @Override
    public List<String> getIngredients() {
        List<String> ingredients = new ArrayList<>(coffee.getIngredients());
        String intensityDesc = intensity == 1.0 ? "" : String.format(" (%.1fx)", intensity);
        ingredients.add(flavorType.getName() + " flavor" + intensityDesc);
        return ingredients;
    }
    
    @Override
    public int getPreparationTime() {
        return coffee.getPreparationTime() + flavorType.getPrepTime();
    }
    
    @Override
    public int getCalories() {
        return coffee.getCalories() + (int)(flavorType.getBaseCalories() * intensity);
    }
    
    public FlavorType getFlavorType() {
        return flavorType;
    }
    
    public double getIntensity() {
        return intensity;
    }
}

public class WhippedCreamDecorator extends CoffeeDecorator {
    private CreamType creamType;
    private double amount; // 0.5 = light, 1.0 = normal, 1.5 = extra
    
    public enum CreamType {
        REGULAR("Regular Whipped Cream", 0.80, 50, 2),
        LIGHT("Light Whipped Cream", 0.85, 25, 2),
        COCONUT("Coconut Whipped Cream", 1.00, 45, 2),
        SUGAR_FREE("Sugar-Free Whipped Cream", 0.90, 20, 2);
        
        private final String name;
        private final double baseCost;
        private final int baseCalories;
        private final int prepTime;
        
        CreamType(String name, double cost, int calories, int prepTime) {
            this.name = name;
            this.baseCost = cost;
            this.baseCalories = calories;
            this.prepTime = prepTime;
        }
        
        public String getName() { return name; }
        public double getBaseCost() { return baseCost; }
        public int getBaseCalories() { return baseCalories; }
        public int getPrepTime() { return prepTime; }
    }
    
    public WhippedCreamDecorator(Coffee coffee, CreamType creamType, double amount) {
        super(coffee);
        this.creamType = creamType;
        this.amount = Math.max(0.5, Math.min(2.0, amount));
    }
    
    public WhippedCreamDecorator(Coffee coffee) {
        this(coffee, CreamType.REGULAR, 1.0);
    }
    
    @Override
    public String getDescription() {
        String amountDesc = amount == 0.5 ? "Light " : 
                           amount == 1.5 ? "Extra " : 
                           amount == 2.0 ? "Double " : "";
        return coffee.getDescription() + ", " + amountDesc + creamType.getName();
    }
    
    @Override
    public double getCost() {
        return coffee.getCost() + (creamType.getBaseCost() * amount);
    }
    
    @Override
    public List<String> getIngredients() {
        List<String> ingredients = new ArrayList<>(coffee.getIngredients());
        String amountDesc = amount == 1.0 ? "" : String.format(" (%.1fx)", amount);
        ingredients.add(creamType.getName() + amountDesc);
        return ingredients;
    }
    
    @Override
    public int getPreparationTime() {
        return coffee.getPreparationTime() + creamType.getPrepTime();
    }
    
    @Override
    public int getCalories() {
        return coffee.getCalories() + (int)(creamType.getBaseCalories() * amount);
    }
    
    public CreamType getCreamType() {
        return creamType;
    }
    
    public double getAmount() {
        return amount;
    }
}

public class ExtraShotDecorator extends CoffeeDecorator {
    private int extraShots;
    private ShotType shotType;
    
    public enum ShotType {
        ESPRESSO("Espresso", 0.75, 3, 2),
        DECAF("Decaf Espresso", 0.75, 3, 2),
        RISTRETTO("Ristretto", 0.85, 2, 3),
        LUNGO("Lungo", 0.80, 4, 2);
        
        private final String name;
        private final double costPerShot;
        private final int caloriesPerShot;
        private final int prepTimePerShot;
        
        ShotType(String name, double cost, int calories, int prepTime) {
            this.name = name;
            this.costPerShot = cost;
            this.caloriesPerShot = calories;
            this.prepTimePerShot = prepTime;
        }
        
        public String getName() { return name; }
        public double getCostPerShot() { return costPerShot; }
        public int getCaloriesPerShot() { return caloriesPerShot; }
        public int getPrepTimePerShot() { return prepTimePerShot; }
    }
    
    public ExtraShotDecorator(Coffee coffee, int extraShots, ShotType shotType) {
        super(coffee);
        this.extraShots = Math.max(1, Math.min(4, extraShots)); // 1-4 extra shots max
        this.shotType = shotType;
    }
    
    public ExtraShotDecorator(Coffee coffee, int extraShots) {
        this(coffee, extraShots, ShotType.ESPRESSO);
    }
    
    @Override
    public String getDescription() {
        String shotText = extraShots == 1 ? "shot" : "shots";
        return coffee.getDescription() + ", " + extraShots + " extra " + shotType.getName() + " " + shotText;
    }
    
    @Override
    public double getCost() {
        return coffee.getCost() + (extraShots * shotType.getCostPerShot());
    }
    
    @Override
    public List<String> getIngredients() {
        List<String> ingredients = new ArrayList<>(coffee.getIngredients());
        ingredients.add(extraShots + " extra " + shotType.getName() + " shot(s)");
        return ingredients;
    }
    
    @Override
    public int getPreparationTime() {
        return coffee.getPreparationTime() + (extraShots * shotType.getPrepTimePerShot());
    }
    
    @Override
    public int getCalories() {
        return coffee.getCalories() + (extraShots * shotType.getCaloriesPerShot());
    }
    
    public int getExtraShots() {
        return extraShots;
    }
    
    public ShotType getShotType() {
        return shotType;
    }
}

// Coffee shop service class
public class CoffeeShop {
    private String name;
    private Map<String, Double> sizePriceMultipliers;
    private Set<String> availableSizes;
    private boolean isOpen;
    private double taxRate;
    
    public CoffeeShop(String name) {
        this.name = name;
        this.sizePriceMultipliers = new HashMap<>();
        sizePriceMultipliers.put("small", 0.85);
        sizePriceMultipliers.put("medium", 1.0);
        sizePriceMultipliers.put("large", 1.25);
        sizePriceMultipliers.put("extra large", 1.5);
        
        this.availableSizes = sizePriceMultipliers.keySet();
        this.isOpen = true;
        this.taxRate = 0.08; // 8% tax
    }
    
    public Coffee createCustomCoffee(String size) {
        Coffee coffee = new SimpleCoffee();
        coffee.setSize(size);
        return coffee;
    }
    
    public Coffee createEspresso(String size) {
        Coffee coffee = new Espresso();
        coffee.setSize(size);
        return coffee;
    }
    
    public void printReceipt(Coffee coffee, String customerName) {
        if (!isOpen) {
            System.out.println("Sorry, " + name + " is currently closed.");
            return;
        }
        
        System.out.println("\n" + "=".repeat(40));
        System.out.println("         " + name.toUpperCase());
        System.out.println("=".repeat(40));
        System.out.println("Customer: " + customerName);
        System.out.println("Date: " + new Date());
        System.out.println("-".repeat(40));
        
        System.out.println("Order: " + coffee.getDescription());
        System.out.println("Size: " + coffee.getSize());
        
        System.out.println("\nIngredients:");
        for (String ingredient : coffee.getIngredients()) {
            System.out.println("  • " + ingredient);
        }
        
        double subtotal = coffee.getCost();
        double tax = subtotal * taxRate;
        double total = subtotal + tax;
        
        System.out.println("\n" + "-".repeat(40));
        System.out.printf("Subtotal: $%.2f%n", subtotal);
        System.out.printf("Tax (%.0f%%): $%.2f%n", taxRate * 100, tax);
        System.out.printf("TOTAL: $%.2f%n", total);
        
        System.out.println("\nPreparation time: " + coffee.getPreparationTime() + " minutes");
        System.out.println("Calories: " + coffee.getCalories());
        System.out.println("\nThank you for visiting " + name + "!");
        System.out.println("=".repeat(40));
    }
    
    public void showMenu() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("            " + name.toUpperCase() + " MENU");
        System.out.println("=".repeat(50));
        
        System.out.println("\nBASE COFFEE OPTIONS:");
        System.out.println("• Simple Coffee - Starting at $2.50");
        System.out.println("• Espresso - Starting at $2.00");
        
        System.out.println("\nAVAILABLE SIZES:");
        for (String size : availableSizes) {
            System.out.printf("• %s (%.0f%% of base price)%n", 
                size.substring(0, 1).toUpperCase() + size.substring(1),
                sizePriceMultipliers.get(size) * 100);
        }
        
        System.out.println("\nCUSTOMIZATIONS:");
        System.out.println("Milk Options:");
        for (MilkDecorator.MilkType milk : MilkDecorator.MilkType.values()) {
            System.out.printf("  • %s - $%.2f%n", milk.getName(), milk.getCost());
        }
        
        System.out.println("\nSweetener Options:");
        for (SugarDecorator.SugarType sugar : SugarDecorator.SugarType.values()) {
            System.out.printf("  • %s - $%.2f per packet%n", sugar.getName(), sugar.getCostPerPacket());
        }
        
        System.out.println("\nFlavor Options:");
        for (FlavorDecorator.FlavorType flavor : FlavorDecorator.FlavorType.values()) {
            System.out.printf("  • %s - $%.2f%n", flavor.getName(), flavor.getBaseCost());
        }
        
        System.out.println("\nExtra Options:");
        System.out.println("  • Extra Shot - $0.75 per shot");
        System.out.println("  • Whipped Cream - $0.80+");
        
        System.out.println("\n" + "=".repeat(50));
    }
    
    public boolean isValidSize(String size) {
        return availableSizes.contains(size.toLowerCase());
    }
    
    public void setOpen(boolean open) {
        this.isOpen = open;
    }
    
    public boolean isOpen() {
        return isOpen;
    }
}
```

### Example 2: Notification System

```java
// Component interface
public interface Notification {
    void send(String message);
    String getDeliveryMethod();
    boolean isDelivered();
    Date getTimestamp();
    String getRecipient();
    void setRecipient(String recipient);
    double getCost();
    int getPriority(); // 1 = low, 5 = high
    Map<String, Object> getMetadata();
}

// Base notification implementation
public abstract class BaseNotification implements Notification {
    protected String recipient;
    protected Date timestamp;
    protected boolean delivered;
    protected Map<String, Object> metadata;
    protected int priority;
    
    public BaseNotification(String recipient) {
        this.recipient = recipient;
        this.timestamp = new Date();
        this.delivered = false;
        this.metadata = new HashMap<>();
        this.priority = 3; // Medium priority by default
    }
    
    @Override
    public String getRecipient() {
        return recipient;
    }
    
    @Override
    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }
    
    @Override
    public Date getTimestamp() {
        return timestamp;
    }
    
    @Override
    public boolean isDelivered() {
        return delivered;
    }
    
    @Override
    public int getPriority() {
        return priority;
    }
    
    public void setPriority(int priority) {
        this.priority = Math.max(1, Math.min(5, priority));
    }
    
    @Override
    public Map<String, Object> getMetadata() {
        return new HashMap<>(metadata);
    }
    
    public void addMetadata(String key, Object value) {
        metadata.put(key, value);
    }
    
    protected void markAsDelivered() {
        this.delivered = true;
        this.timestamp = new Date();
    }
}

// Concrete components
public class EmailNotification extends BaseNotification {
    private String subject;
    private String emailAddress;
    
    public EmailNotification(String emailAddress) {
        super(emailAddress);
        this.emailAddress = emailAddress;
        this.subject = "Notification";
        addMetadata("email_address", emailAddress);
    }
    
    public EmailNotification(String emailAddress, String subject) {
        this(emailAddress);
        this.subject = subject;
    }
    
    @Override
    public void send(String message) {
        System.out.println("📧 Sending email to: " + emailAddress);
        System.out.println("Subject: " + subject);
        System.out.println("Message: " + message);
        System.out.println("Timestamp: " + timestamp);
        
        // Simulate email sending
        try {
            Thread.sleep(100); // Simulate network delay
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        markAsDelivered();
        System.out.println("✅ Email delivered successfully!");
    }
    
    @Override
    public String getDeliveryMethod() {
        return "Email";
    }
    
    @Override
    public double getCost() {
        return 0.01; // $0.01 per email
    }
    
    public String getSubject() {
        return subject;
    }
    
    public void setSubject(String subject) {
        this.subject = subject;
    }
}

public class SMSNotification extends BaseNotification {
    private String phoneNumber;
    private boolean international;
    
    public SMSNotification(String phoneNumber) {
        super(phoneNumber);
        this.phoneNumber = phoneNumber;
        this.international = phoneNumber.startsWith("+") && !phoneNumber.startsWith("+1");
        addMetadata("phone_number", phoneNumber);
        addMetadata("international", international);
    }
    
    @Override
    public void send(String message) {
        System.out.println("📱 Sending SMS to: " + phoneNumber);
        if (international) {
            System.out.println("🌍 International SMS");
        }
        System.out.println("Message: " + message);
        System.out.println("Length: " + message.length() + " characters");
        
        // Simulate SMS sending
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        markAsDelivered();
        System.out.println("✅ SMS delivered successfully!");
    }
    
    @Override
    public String getDeliveryMethod() {
        return international ? "International SMS" : "SMS";
    }
    
    @Override
    public double getCost() {
        return international ? 0.25 : 0.05; // More expensive for international
    }
    
    public boolean isInternational() {
        return international;
    }
}

public class PushNotification extends BaseNotification {
    private String deviceId;
    private String appId;
    private String platform; // iOS, Android, Web
    
    public PushNotification(String deviceId, String appId, String platform) {
        super(deviceId);
        this.deviceId = deviceId;
        this.appId = appId;
        this.platform = platform;
        addMetadata("device_id", deviceId);
        addMetadata("app_id", appId);
        addMetadata("platform", platform);
    }
    
    @Override
    public void send(String message) {
        System.out.println("🔔 Sending push notification");
        System.out.println("Platform: " + platform);
        System.out.println("App ID: " + appId);
        System.out.println("Device: " + deviceId.substring(0, 8) + "...");
        System.out.println("Message: " + message);
        
        // Simulate push notification
        try {
            Thread.sleep(25);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        markAsDelivered();
        System.out.println("✅ Push notification delivered!");
    }
    
    @Override
    public String getDeliveryMethod() {
        return platform + " Push Notification";
    }
    
    @Override
    public double getCost() {
        return 0.001; // Very cheap for push notifications
    }
    
    public String getPlatform() {
        return platform;
    }
}

// Abstract decorator
public abstract class NotificationDecorator implements Notification {
    protected Notification notification;
    
    public NotificationDecorator(Notification notification) {
        this.notification = notification;
    }
    
    @Override
    public void send(String message) {
        notification.send(message);
    }
    
    @Override
    public String getDeliveryMethod() {
        return notification.getDeliveryMethod();
    }
    
    @Override
    public boolean isDelivered() {
        return notification.isDelivered();
    }
    
    @Override
    public Date getTimestamp() {
        return notification.getTimestamp();
    }
    
    @Override
    public String getRecipient() {
        return notification.getRecipient();
    }
    
    @Override
    public void setRecipient(String recipient) {
        notification.setRecipient(recipient);
    }
    
    @Override
    public double getCost() {
        return notification.getCost();
    }
    
    @Override
    public int getPriority() {
        return notification.getPriority();
    }
    
    @Override
    public Map<String, Object> getMetadata() {
        return notification.getMetadata();
    }
}

// Concrete decorators
public class EncryptionDecorator extends NotificationDecorator {
    private EncryptionType encryptionType;
    private String encryptionKey;
    
    public enum EncryptionType {
        AES256("AES-256", 0.02, "🔐"),
        RSA("RSA-2048", 0.05, "🔒"),
        PGP("PGP", 0.03, "🛡️");
        
        private final String name;
        private final double additionalCost;
        private final String icon;
        
        EncryptionType(String name, double cost, String icon) {
            this.name = name;
            this.additionalCost = cost;
            this.icon = icon;
        }
        
        public String getName() { return name; }
        public double getAdditionalCost() { return additionalCost; }
        public String getIcon() { return icon; }
    }
    
    public EncryptionDecorator(Notification notification, EncryptionType encryptionType) {
        super(notification);
        this.encryptionType = encryptionType;
        this.encryptionKey = generateEncryptionKey();
    }
    
    public EncryptionDecorator(Notification notification) {
        this(notification, EncryptionType.AES256);
    }
    
    @Override
    public void send(String message) {
        System.out.println(encryptionType.getIcon() + " Encrypting message with " + encryptionType.getName());
        String encryptedMessage = encrypt(message);
        System.out.println("Original length: " + message.length() + " chars");
        System.out.println("Encrypted length: " + encryptedMessage.length() + " chars");
        
        super.send(encryptedMessage);
    }
    
    @Override
    public String getDeliveryMethod() {
        return notification.getDeliveryMethod() + " with " + encryptionType.getName() + " Encryption";
    }
    
    @Override
    public double getCost() {
        return notification.getCost() + encryptionType.getAdditionalCost();
    }
    
    private String encrypt(String message) {
        // Simulate encryption (in reality, would use proper encryption)
        StringBuilder encrypted = new StringBuilder();
        for (char c : message.toCharArray()) {
            encrypted.append((char) (c + 1)); // Simple Caesar cipher for demo
        }
        return "ENC:" + Base64.getEncoder().encodeToString(encrypted.toString().getBytes());
    }
    
    private String generateEncryptionKey() {
        return "KEY-" + System.currentTimeMillis() + "-" + (int)(Math.random() * 10000);
    }
    
    public EncryptionType getEncryptionType() {
        return encryptionType;
    }
}

public class CompressionDecorator extends NotificationDecorator {
    private CompressionType compressionType;
    private int threshold; // Minimum message length to compress
    
    public enum CompressionType {
        GZIP("GZIP", 0.005, 0.7),
        LZ4("LZ4", 0.003, 0.8),
        BROTLI("Brotli", 0.008, 0.6);
        
        private final String name;
        private final double additionalCost;
        private final double compressionRatio; // Estimated compression ratio
        
        CompressionType(String name, double cost, double ratio) {
            this.name = name;
            this.additionalCost = cost;
            this.compressionRatio = ratio;
        }
        
        public String getName() { return name; }
        public double getAdditionalCost() { return additionalCost; }
        public double getCompressionRatio() { return compressionRatio; }
    }
    
    public CompressionDecorator(Notification notification, CompressionType compressionType, int threshold) {
        super(notification);
        this.compressionType = compressionType;
        this.threshold = threshold;
    }
    
    public CompressionDecorator(Notification notification) {
        this(notification, CompressionType.GZIP, 100);
    }
    
    @Override
    public void send(String message) {
        if (message.length() >= threshold) {
            System.out.println("🗜️ Compressing message with " + compressionType.getName());
            String compressedMessage = compress(message);
            int originalSize = message.length();
            int compressedSize = compressedMessage.length();
            double savings = ((double)(originalSize - compressedSize) / originalSize) * 100;
            
            System.out.println("Original size: " + originalSize + " chars");
            System.out.println("Compressed size: " + compressedSize + " chars");
            System.out.println("Compression savings: " + String.format("%.1f%%", savings));
            
            super.send(compressedMessage);
        } else {
            System.out.println("📝 Message too short for compression (< " + threshold + " chars)");
            super.send(message);
        }
    }
    
    @Override
    public String getDeliveryMethod() {
        return notification.getDeliveryMethod() + " with " + compressionType.getName() + " Compression";
    }
    
    @Override
    public double getCost() {
        return notification.getCost() + compressionType.getAdditionalCost();
    }
    
    private String compress(String message) {
        // Simulate compression (in reality, would use proper compression algorithms)
        int compressedLength = (int)(message.length() * compressionType.getCompressionRatio());
        return "COMP:" + compressionType.name() + ":" + 
               message.substring(0, Math.min(compressedLength, message.length())) + "...";
    }
    
    public CompressionType getCompressionType() {
        return compressionType;
    }
}

public class RetryDecorator extends NotificationDecorator {
    private int maxRetries;
    private int retryDelay; // in milliseconds
    private int currentAttempt;
    private double failureRate; // Simulated failure rate for demo
    
    public RetryDecorator(Notification notification, int maxRetries, int retryDelay) {
        super(notification);
        this.maxRetries = Math.max(1, maxRetries);
        this.retryDelay = Math.max(100, retryDelay);
        this.currentAttempt = 0;
        this.failureRate = 0.2; // 20% simulated failure rate
    }
    
    public RetryDecorator(Notification notification) {
        this(notification, 3, 1000);
    }
    
    @Override
    public void send(String message) {
        currentAttempt = 0;
        boolean success = false;
        
        while (!success && currentAttempt < maxRetries) {
            currentAttempt++;
            
            if (currentAttempt > 1) {
                System.out.println("🔄 Retry attempt " + currentAttempt + "/" + maxRetries);
                try {
                    Thread.sleep(retryDelay * currentAttempt); // Exponential backoff
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
            
            try {
                // Simulate potential failure
                if (Math.random() < failureRate && currentAttempt < maxRetries) {
                    throw new RuntimeException("Simulated delivery failure");
                }
                
                super.send(message);
                success = true;
                
                if (currentAttempt > 1) {
                    System.out.println("✅ Delivery successful after " + currentAttempt + " attempts");
                }
                
            } catch (Exception e) {
                System.out.println("❌ Attempt " + currentAttempt + " failed: " + e.getMessage());
                
                if (currentAttempt >= maxRetries) {
                    System.out.println("🚫 Max retries exceeded. Delivery failed.");
                    throw new RuntimeException("Failed to deliver after " + maxRetries + " attempts", e);
                }
            }
        }
    }
    
    @Override
    public String getDeliveryMethod() {
        return notification.getDeliveryMethod() + " with Retry (max " + maxRetries + ")";
    }
    
    @Override
    public double getCost() {
        // Cost increases with potential retries
        return notification.getCost() * (1 + (maxRetries - 1) * 0.1);
    }
    
    public int getMaxRetries() {
        return maxRetries;
    }
    
    public int getCurrentAttempt() {
        return currentAttempt;
    }
    
    public void setFailureRate(double failureRate) {
        this.failureRate = Math.max(0.0, Math.min(1.0, failureRate));
    }
}

public class LoggingDecorator extends NotificationDecorator {
    private LogLevel logLevel;
    private String loggerName;
    private boolean includeMetadata;
    
    public enum LogLevel {
        DEBUG("DEBUG", "🐛"),
        INFO("INFO", "ℹ️"),
        WARN("WARN", "⚠️"),
        ERROR("ERROR", "❌");
        
        private final String name;
        private final String icon;
        
        LogLevel(String name, String icon) {
            this.name = name;
            this.icon = icon;
        }
        
        public String getName() { return name; }
        public String getIcon() { return icon; }
    }
    
    public LoggingDecorator(Notification notification, LogLevel logLevel, String loggerName) {
        super(notification);
        this.logLevel = logLevel;
        this.loggerName = loggerName;
        this.includeMetadata = true;
    }
    
    public LoggingDecorator(Notification notification) {
        this(notification, LogLevel.INFO, "NotificationService");
    }
    
    @Override
    public void send(String message) {
        logBefore(message);
        
        try {
            super.send(message);
            logSuccess(message);
        } catch (Exception e) {
            logError(message, e);
            throw e;
        }
    }
    
    private void logBefore(String message) {
        log(LogLevel.DEBUG, "Attempting to send notification to: " + getRecipient());
        log(LogLevel.DEBUG, "Delivery method: " + notification.getDeliveryMethod());
        log(LogLevel.DEBUG, "Message length: " + message.length() + " characters");
        log(LogLevel.DEBUG, "Priority: " + getPriority());
        
        if (includeMetadata && !getMetadata().isEmpty()) {
            log(LogLevel.DEBUG, "Metadata: " + getMetadata());
        }
    }
    
    private void logSuccess(String message) {
        log(LogLevel.INFO, "Notification delivered successfully");
        log(LogLevel.INFO, "Delivery method: " + getDeliveryMethod());
        log(LogLevel.INFO, "Cost: $" + String.format("%.3f", getCost()));
        log(LogLevel.INFO, "Timestamp: " + getTimestamp());
    }
    
    private void logError(String message, Exception e) {
        log(LogLevel.ERROR, "Failed to deliver notification: " + e.getMessage());
        log(LogLevel.ERROR, "Recipient: " + getRecipient());
        log(LogLevel.ERROR, "Delivery method: " + getDeliveryMethod());
    }
    
    private void log(LogLevel level, String message) {
        if (level.ordinal() >= this.logLevel.ordinal()) {
            String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());
            System.out.println(String.format("[%s] %s %s - %s: %s", 
                timestamp, level.getIcon(), level.getName(), loggerName, message));
        }
    }
    
    @Override
    public String getDeliveryMethod() {
        return notification.getDeliveryMethod() + " with Logging";
    }
    
    @Override
    public double getCost() {
        return notification.getCost() + 0.001; // Small cost for logging
    }
    
    public void setIncludeMetadata(boolean includeMetadata) {
        this.includeMetadata = includeMetadata;
    }
    
    public LogLevel getLogLevel() {
        return logLevel;
    }
    
    public void setLogLevel(LogLevel logLevel) {
        this.logLevel = logLevel;
    }
}

public class RateLimitingDecorator extends NotificationDecorator {
    private static final Map<String, Queue<Long>> recipientTimestamps = new ConcurrentHashMap<>();
    private int maxRequestsPerMinute;
    private long timeWindowMs;
    
    public RateLimitingDecorator(Notification notification, int maxRequestsPerMinute) {
        super(notification);
        this.maxRequestsPerMinute = maxRequestsPerMinute;
        this.timeWindowMs = 60 * 1000; // 1 minute in milliseconds
    }
    
    public RateLimitingDecorator(Notification notification) {
        this(notification, 10); // Default: 10 notifications per minute
    }
    
    @Override
    public void send(String message) {
        String recipient = getRecipient();
        
        if (isRateLimited(recipient)) {
            throw new RuntimeException("Rate limit exceeded for recipient: " + recipient + 
                                     ". Max " + maxRequestsPerMinute + " notifications per minute.");
        }
        
        recordRequest(recipient);
        System.out.println("⏱️ Rate limit check passed for: " + recipient);
        super.send(message);
    }
    
    private boolean isRateLimited(String recipient) {
        Queue<Long> timestamps = recipientTimestamps.computeIfAbsent(recipient, k -> new LinkedList<>());
        long currentTime = System.currentTimeMillis();
        
        // Remove old timestamps outside the time window
        while (!timestamps.isEmpty() && (currentTime - timestamps.peek()) > timeWindowMs) {
            timestamps.poll();
        }
        
        return timestamps.size() >= maxRequestsPerMinute;
    }
    
    private void recordRequest(String recipient) {
        Queue<Long> timestamps = recipientTimestamps.computeIfAbsent(recipient, k -> new LinkedList<>());
        timestamps.offer(System.currentTimeMillis());
    }
    
    @Override
    public String getDeliveryMethod() {
        return notification.getDeliveryMethod() + " with Rate Limiting (" + maxRequestsPerMinute + "/min)";
    }
    
    @Override
    public double getCost() {
        return notification.getCost() + 0.002; // Small cost for rate limiting
    }
    
    public int getMaxRequestsPerMinute() {
        return maxRequestsPerMinute;
    }
    
    public static void clearRateLimitHistory() {
        recipientTimestamps.clear();
    }
}

// Notification service
public class NotificationService {
    private String serviceName;
    private Map<String, NotificationTemplate> templates;
    private List<NotificationObserver> observers;
    
    public interface NotificationObserver {
        void onNotificationSent(Notification notification, String message);
        void onNotificationFailed(Notification notification, String message, Exception error);
    }
    
    public static class NotificationTemplate {
        private String name;
        private String template;
        private Map<String, String> defaultValues;
        
        public NotificationTemplate(String name, String template) {
            this.name = name;
            this.template = template;
            this.defaultValues = new HashMap<>();
        }
        
        public String getName() { return name; }
        public String getTemplate() { return template; }
        
        public void setDefaultValue(String key, String value) {
            defaultValues.put(key, value);
        }
        
        public String render(Map<String, String> values) {
            String result = template;
            
            // Apply default values first
            for (Map.Entry<String, String> entry : defaultValues.entrySet()) {
                result = result.replace("{{" + entry.getKey() + "}}", entry.getValue());
            }
            
            // Apply provided values
            for (Map.Entry<String, String> entry : values.entrySet()) {
                result = result.replace("{{" + entry.getKey() + "}}", entry.getValue());
            }
            
            return result;
        }
    }
    
    public NotificationService(String serviceName) {
        this.serviceName = serviceName;
        this.templates = new HashMap<>();
        this.observers = new ArrayList<>();
        initializeDefaultTemplates();
    }
    
    private void initializeDefaultTemplates() {
        NotificationTemplate welcome = new NotificationTemplate("welcome",
            "Welcome {{name}}! Thank you for joining {{service}}. Your account is now active.");
        welcome.setDefaultValue("service", serviceName);
        templates.put("welcome", welcome);
        
        NotificationTemplate reminder = new NotificationTemplate("reminder",
            "Hi {{name}}, this is a reminder about {{event}} scheduled for {{date}}.");
        templates.put("reminder", reminder);
        
        NotificationTemplate alert = new NotificationTemplate("alert",
            "ALERT: {{alertType}} detected. Message: {{message}}. Time: {{timestamp}}");
        templates.put("alert", alert);
    }
    
    public void sendNotification(Notification notification, String message) {
        try {
            System.out.println("\n" + "=".repeat(60));
            System.out.println("📬 " + serviceName + " - Sending Notification");
            System.out.println("=".repeat(60));
            
            notification.send(message);
            
            // Notify observers
            for (NotificationObserver observer : observers) {
                observer.onNotificationSent(notification, message);
            }
            
            System.out.println("=".repeat(60));
            
        } catch (Exception e) {
            System.out.println("🚫 Notification delivery failed: " + e.getMessage());
            
            // Notify observers
            for (NotificationObserver observer : observers) {
                observer.onNotificationFailed(notification, message, e);
            }
            
            throw e;
        }
    }
    
    public void sendTemplateNotification(Notification notification, String templateName, 
                                       Map<String, String> values) {
        NotificationTemplate template = templates.get(templateName);
        if (template == null) {
            throw new IllegalArgumentException("Template not found: " + templateName);
        }
        
        String message = template.render(values);
        sendNotification(notification, message);
    }
    
    public void addTemplate(NotificationTemplate template) {
        templates.put(template.getName(), template);
    }
    
    public void addObserver(NotificationObserver observer) {
        observers.add(observer);
    }
    
    public void removeObserver(NotificationObserver observer) {
        observers.remove(observer);
    }
    
    public Set<String> getAvailableTemplates() {
        return new HashSet<>(templates.keySet());
    }
    
    public double calculateTotalCost(List<Notification> notifications) {
        return notifications.stream()
                .mapToDouble(Notification::getCost)
                .sum();
    }
    
    public void showStatistics(List<Notification> notifications) {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("📊 " + serviceName + " Statistics");
        System.out.println("=".repeat(50));
        
        int totalNotifications = notifications.size();
        long deliveredCount = notifications.stream()
                .mapToLong(n -> n.isDelivered() ? 1 : 0)
                .sum();
        
        double totalCost = calculateTotalCost(notifications);
        double averageCost = totalNotifications > 0 ? totalCost / totalNotifications : 0;
        
        Map<String, Long> methodCounts = notifications.stream()
                .collect(Collectors.groupingBy(
                    Notification::getDeliveryMethod,
                    Collectors.counting()
                ));
        
        System.out.println("Total notifications: " + totalNotifications);
        System.out.println("Delivered: " + deliveredCount + " (" + 
                         String.format("%.1f%%", (double)deliveredCount / totalNotifications * 100) + ")");
        System.out.println("Total cost: $" + String.format("%.3f", totalCost));
        System.out.println("Average cost: $" + String.format("%.3f", averageCost));
        
        System.out.println("\nDelivery methods:");
        for (Map.Entry<String, Long> entry : methodCounts.entrySet()) {
            System.out.println("  " + entry.getKey() + ": " + entry.getValue());
        }
        
        System.out.println("=".repeat(50));
    }
}
```

### Example 3: Text Processing Pipeline

```java
// Component interface
public interface TextProcessor {
    String process(String text);
    String getDescription();
    Map<String, Object> getStatistics();
    boolean isEnabled();
    void setEnabled(boolean enabled);
    double getProcessingTime(); // in milliseconds
}

// Base implementation
public abstract class BaseTextProcessor implements TextProcessor {
    protected boolean enabled;
    protected Map<String, Object> statistics;
    protected String name;
    protected long lastProcessingTime;
    
    public BaseTextProcessor(String name) {
        this.name = name;
        this.enabled = true;
        this.statistics = new HashMap<>();
        this.lastProcessingTime = 0;
    }
    
    @Override
    public boolean isEnabled() {
        return enabled;
    }
    
    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    @Override
    public Map<String, Object> getStatistics() {
        return new HashMap<>(statistics);
    }
    
    @Override
    public double getProcessingTime() {
        return lastProcessingTime;
    }
    
    protected void updateStatistics(String key, Object value) {
        statistics.put(key, value);
    }
    
    protected void recordProcessingTime(long startTime) {
        lastProcessingTime = System.currentTimeMillis() - startTime;
        updateStatistics("last_processing_time_ms", lastProcessingTime);
    }
}

// Concrete components
public class PlainTextProcessor extends BaseTextProcessor {
    private String encoding;
    private int maxLength;
    
    public PlainTextProcessor() {
        super("Plain Text Processor");
        this.encoding = "UTF-8";
        this.maxLength = Integer.MAX_VALUE;
        updateStatistics("characters_processed", 0);
        updateStatistics("words_processed", 0);
        updateStatistics("lines_processed", 0);
    }
    
    @Override
    public String process(String text) {
        if (!enabled) return text;
        
        long startTime = System.currentTimeMillis();
        
        if (text == null) {
            text = "";
        }
        
        // Apply length limit
        if (text.length() > maxLength) {
            text = text.substring(0, maxLength) + "...";
        }
        
        // Update statistics
        int charCount = text.length();
        int wordCount = text.trim().isEmpty() ? 0 : text.trim().split("\\s+").length;
        int lineCount = text.split("\\r?\\n").length;
        
        updateStatistics("characters_processed", 
            (Integer) statistics.getOrDefault("characters_processed", 0) + charCount);
        updateStatistics("words_processed", 
            (Integer) statistics.getOrDefault("words_processed", 0) + wordCount);
        updateStatistics("lines_processed", 
            (Integer) statistics.getOrDefault("lines_processed", 0) + lineCount);
        
        recordProcessingTime(startTime);
        return text;
    }
    
    @Override
    public String getDescription() {
        return name + " (encoding: " + encoding + ", max length: " + 
               (maxLength == Integer.MAX_VALUE ? "unlimited" : maxLength) + ")";
    }
    
    public void setMaxLength(int maxLength) {
        this.maxLength = Math.max(0, maxLength);
    }
    
    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }
}

public class MarkdownProcessor extends BaseTextProcessor {
    private boolean enableExtensions;
    private Set<String> enabledExtensions;
    
    public MarkdownProcessor() {
        super("Markdown Processor");
        this.enableExtensions = true;
        this.enabledExtensions = new HashSet<>(Arrays.asList(
            "tables", "strikethrough", "autolinks", "task-lists"
        ));
        updateStatistics("markdown_elements_processed", 0);
    }
    
    @Override
    public String process(String text) {
        if (!enabled) return text;
        
        long startTime = System.currentTimeMillis();
        
        if (text == null) return "";
        
        String processed = text;
        int elementsProcessed = 0;
        
        // Process headers
        processed = processed.replaceAll("^(#{1,6})\\s+(.+)$", "<h$1>$2</h$1>");
        elementsProcessed += countMatches(text, "^(#{1,6})\\s+(.+)$");
        
        // Process bold text
        processed = processed.replaceAll("\\*\\*([^*]+)\\*\\*", "<strong>$1</strong>");
        processed = processed.replaceAll("__([^_]+)__", "<strong>$1</strong>");
        elementsProcessed += countMatches(text, "\\*\\*([^*]+)\\*\\*|__([^_]+)__");
        
        // Process italic text
        processed = processed.replaceAll("\\*([^*]+)\\*", "<em>$1</em>");
        processed = processed.replaceAll("_([^_]+)_", "<em>$1</em>");
        elementsProcessed += countMatches(text, "\\*([^*]+)\\*|_([^_]+)_");
        
        // Process links
        processed = processed.replaceAll("\\[([^\\]]+)\\]\\(([^)]+)\\)", "<a href=\"$2\">$1</a>");
        elementsProcessed += countMatches(text, "\\[([^\\]]+)\\]\\(([^)]+)\\)");
        
        // Process code blocks
        processed = processed.replaceAll("`([^`]+)`", "<code>$1</code>");
        elementsProcessed += countMatches(text, "`([^`]+)`");
        
        if (enableExtensions) {
            // Process strikethrough
            if (enabledExtensions.contains("strikethrough")) {
                processed = processed.replaceAll("~~([^~]+)~~", "<del>$1</del>");
                elementsProcessed += countMatches(text, "~~([^~]+)~~");
            }
            
            // Process task lists
            if (enabledExtensions.contains("task-lists")) {
                processed = processed.replaceAll("^- \\[ \\] (.+)$", "<input type=\"checkbox\"> $1");
                processed = processed.replaceAll("^- \\[x\\] (.+)$", "<input type=\"checkbox\" checked> $1");
                elementsProcessed += countMatches(text, "^- \\[[x ]\\] (.+)$");
            }
        }
        
        updateStatistics("markdown_elements_processed", 
            (Integer) statistics.getOrDefault("markdown_elements_processed", 0) + elementsProcessed);
        
        recordProcessingTime(startTime);
        return processed;
    }
    
    @Override
    public String getDescription() {
        return name + " (extensions: " + (enableExtensions ? enabledExtensions.toString() : "disabled") + ")";
    }
    
    private int countMatches(String text, String regex) {
        return text.split(regex).length - 1;
    }
    
    public void setEnableExtensions(boolean enableExtensions) {
        this.enableExtensions = enableExtensions;
    }
    
    public void addExtension(String extension) {
        enabledExtensions.add(extension);
    }
    
    public void removeExtension(String extension) {
        enabledExtensions.remove(extension);
    }
}

// Abstract decorator
public abstract class TextProcessorDecorator implements TextProcessor {
    protected TextProcessor processor;
    
    public TextProcessorDecorator(TextProcessor processor) {
        this.processor = processor;
    }
    
    @Override
    public String process(String text) {
        return processor.process(text);
    }
    
    @Override
    public String getDescription() {
        return processor.getDescription();
    }
    
    @Override
    public Map<String, Object> getStatistics() {
        return processor.getStatistics();
    }
    
    @Override
    public boolean isEnabled() {
        return processor.isEnabled();
    }
    
    @Override
    public void setEnabled(boolean enabled) {
        processor.setEnabled(enabled);
    }
    
    @Override
    public double getProcessingTime() {
        return processor.getProcessingTime();
    }
}

// Concrete decorators
public class UpperCaseDecorator extends TextProcessorDecorator {
    private boolean preserveWhitespace;
    
    public UpperCaseDecorator(TextProcessor processor, boolean preserveWhitespace) {
        super(processor);
        this.preserveWhitespace = preserveWhitespace;
    }
    
    public UpperCaseDecorator(TextProcessor processor) {
        this(processor, true);
    }
    
    @Override
    public String process(String text) {
        String processed = super.process(text);
        
        if (processor.isEnabled()) {
            long startTime = System.currentTimeMillis();
            
            if (preserveWhitespace) {
                processed = processed.toUpperCase();
            } else {
                processed = processed.toUpperCase().replaceAll("\\s+", " ");
            }
            
            // Update statistics
            Map<String, Object> stats = new HashMap<>(processor.getStatistics());
            stats.put("uppercase_applied", true);
            stats.put("uppercase_processing_time", System.currentTimeMillis() - startTime);
        }
        
        return processed;
    }
    
    @Override
    public String getDescription() {
        return processor.getDescription() + " + UpperCase";
    }
}

public class TrimDecorator extends TextProcessorDecorator {
    private TrimMode trimMode;
    
    public enum TrimMode {
        LEADING("Leading whitespace"),
        TRAILING("Trailing whitespace"),
        BOTH("Leading and trailing whitespace"),
        ALL_EXTRA("All extra whitespace");
        
        private final String description;
        
        TrimMode(String description) {
            this.description = description;
        }
        
        public String getDescription() { return description; }
    }
    
    public TrimDecorator(TextProcessor processor, TrimMode trimMode) {
        super(processor);
        this.trimMode = trimMode;
    }
    
    public TrimDecorator(TextProcessor processor) {
        this(processor, TrimMode.BOTH);
    }
    
    @Override
    public String process(String text) {
        String processed = super.process(text);
        
        if (processor.isEnabled()) {
            long startTime = System.currentTimeMillis();
            
            switch (trimMode) {
                case LEADING:
                    processed = processed.replaceAll("^\\s+", "");
                    break;
                case TRAILING:
                    processed = processed.replaceAll("\\s+$", "");
                    break;
                case BOTH:
                    processed = processed.trim();
                    break;
                case ALL_EXTRA:
                    processed = processed.trim().replaceAll("\\s+", " ");
                    break;
            }
            
            // Update statistics
            Map<String, Object> stats = new HashMap<>(processor.getStatistics());
            stats.put("trim_mode", trimMode.name());
            stats.put("trim_processing_time", System.currentTimeMillis() - startTime);
        }
        
        return processed;
    }
    
    @Override
    public String getDescription() {
        return processor.getDescription() + " + Trim(" + trimMode.getDescription() + ")";
    }
}

public class FilterDecorator extends TextProcessorDecorator {
    private Set<String> badWords;
    private String replacement;
    private boolean caseSensitive;
    private int wordsFiltered;
    
    public FilterDecorator(TextProcessor processor, Set<String> badWords, String replacement) {
        super(processor);
        this.badWords = new HashSet<>(badWords);
        this.replacement = replacement;
        this.caseSensitive = false;
        this.wordsFiltered = 0;
    }
    
    public FilterDecorator(TextProcessor processor) {
        this(processor, new HashSet<>(Arrays.asList("spam", "offensive", "inappropriate")), "***");
    }
    
    @Override
    public String process(String text) {
        String processed = super.process(text);
        
        if (processor.isEnabled()) {
            long startTime = System.currentTimeMillis();
            int initialWordsFiltered = wordsFiltered;
            
            for (String badWord : badWords) {
                String pattern = caseSensitive ? "\\b" + badWord + "\\b" : "(?i)\\b" + badWord + "\\b";
                String before = processed;
                processed = processed.replaceAll(pattern, replacement);
                
                // Count replacements
                int replacements = (before.length() - processed.length()) / 
                                 (badWord.length() - replacement.length());
                wordsFiltered += Math.max(0, replacements);
            }
            
            // Update statistics
            Map<String, Object> stats = new HashMap<>(processor.getStatistics());
            stats.put("words_filtered", wordsFiltered);
            stats.put("words_filtered_this_pass", wordsFiltered - initialWordsFiltered);
            stats.put("filter_processing_time", System.currentTimeMillis() - startTime);
        }
        
        return processed;
    }
    
    @Override
    public String getDescription() {
        return processor.getDescription() + " + Filter(" + badWords.size() + " words)";
    }
    
    public void addBadWord(String word) {
        badWords.add(word);
    }
    
    public void removeBadWord(String word) {
        badWords.remove(word);
    }
    
    public void setCaseSensitive(boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
    }
    
    public int getWordsFiltered() {
        return wordsFiltered;
    }
}

public class HtmlEscapeDecorator extends TextProcessorDecorator {
    private boolean escapeQuotes;
    private boolean escapeNewlines;
    private Map<String, String> customEscapes;
    
    public HtmlEscapeDecorator(TextProcessor processor, boolean escapeQuotes, boolean escapeNewlines) {
        super(processor);
        this.escapeQuotes = escapeQuotes;
        this.escapeNewlines = escapeNewlines;
        this.customEscapes = new HashMap<>();
    }
    
    public HtmlEscapeDecorator(TextProcessor processor) {
        this(processor, true, true);
    }
    
    @Override
    public String process(String text) {
        String processed = super.process(text);
        
        if (processor.isEnabled()) {
            long startTime = System.currentTimeMillis();
            
            // Basic HTML escaping
            processed = processed.replace("&", "&amp;");
            processed = processed.replace("<", "&lt;");
            processed = processed.replace(">", "&gt;");
            
            if (escapeQuotes) {
                processed = processed.replace("\"", "&quot;");
                processed = processed.replace("'", "&#39;");
            }
            
            if (escapeNewlines) {
                processed = processed.replace("\n", "<br>");
                processed = processed.replace("\r", "");
            }
            
            // Apply custom escapes
            for (Map.Entry<String, String> escape : customEscapes.entrySet()) {
                processed = processed.replace(escape.getKey(), escape.getValue());
            }
            
            // Update statistics
            Map<String, Object> stats = new HashMap<>(processor.getStatistics());
            stats.put("html_escaped", true);
            stats.put("escape_quotes", escapeQuotes);
            stats.put("escape_newlines", escapeNewlines);
            stats.put("html_escape_processing_time", System.currentTimeMillis() - startTime);
        }
        
        return processed;
    }
    
    @Override
    public String getDescription() {
        return processor.getDescription() + " + HtmlEscape";
    }
    
    public void addCustomEscape(String from, String to) {
        customEscapes.put(from, to);
    }
    
    public void removeCustomEscape(String from) {
        customEscapes.remove(from);
    }
}

public class ValidationDecorator extends TextProcessorDecorator {
    private int minLength;
    private int maxLength;
    private Pattern allowedPattern;
    private boolean throwOnFailure;
    
    public ValidationDecorator(TextProcessor processor, int minLength, int maxLength) {
        super(processor);
        this.minLength = minLength;
        this.maxLength = maxLength;
        this.allowedPattern = null;
        this.throwOnFailure = true;
    }
    
    public ValidationDecorator(TextProcessor processor) {
        this(processor, 0, Integer.MAX_VALUE);
    }
    
    @Override
    public String process(String text) {
        if (processor.isEnabled()) {
            long startTime = System.currentTimeMillis();
            
            // Validate before processing
            ValidationResult validation = validate(text);
            
            if (!validation.isValid()) {
                Map<String, Object> stats = new HashMap<>(processor.getStatistics());
                stats.put("validation_failed", true);
                stats.put("validation_errors", validation.getErrors());
                
                if (throwOnFailure) {
                    throw new IllegalArgumentException("Validation failed: " + validation.getErrors());
                } else {
                    System.out.println("⚠️ Validation warnings: " + validation.getErrors());
                }
            }
            
            String processed = super.process(text);
            
            // Update statistics
            Map<String, Object> stats = new HashMap<>(processor.getStatistics());
            stats.put("validation_passed", validation.isValid());
            stats.put("validation_processing_time", System.currentTimeMillis() - startTime);
            
            return processed;
        }
        
        return super.process(text);
    }
    
    private ValidationResult validate(String text) {
        List<String> errors = new ArrayList<>();
        
        if (text == null) {
            errors.add("Text cannot be null");
            return new ValidationResult(false, errors);
        }
        
        if (text.length() < minLength) {
            errors.add("Text too short (minimum: " + minLength + " characters)");
        }
        
        if (text.length() > maxLength) {
            errors.add("Text too long (maximum: " + maxLength + " characters)");
        }
        
        if (allowedPattern != null && !allowedPattern.matcher(text).matches()) {
            errors.add("Text does not match required pattern");
        }
        
        return new ValidationResult(errors.isEmpty(), errors);
    }
    
    @Override
    public String getDescription() {
        return processor.getDescription() + " + Validation(" + minLength + "-" + maxLength + " chars)";
    }
    
    public void setAllowedPattern(String regex) {
        this.allowedPattern = Pattern.compile(regex);
    }
    
    public void setThrowOnFailure(boolean throwOnFailure) {
        this.throwOnFailure = throwOnFailure;
    }
    
    private static class ValidationResult {
        private final boolean valid;
        private final List<String> errors;
        
        public ValidationResult(boolean valid, List<String> errors) {
            this.valid = valid;
            this.errors = new ArrayList<>(errors);
        }
        
        public boolean isValid() { return valid; }
        public List<String> getErrors() { return errors; }
    }
}

public class CacheDecorator extends TextProcessorDecorator {
    private Map<String, CacheEntry> cache;
    private int maxCacheSize;
    private long cacheExpiryMs;
    private int cacheHits;
    private int cacheMisses;
    
    private static class CacheEntry {
        private final String result;
        private final long timestamp;
        
        public CacheEntry(String result) {
            this.result = result;
            this.timestamp = System.currentTimeMillis();
        }
        
        public String getResult() { return result; }
        public boolean isExpired(long expiryMs) {
            return System.currentTimeMillis() - timestamp > expiryMs;
        }
    }
    
    public CacheDecorator(TextProcessor processor, int maxCacheSize, long cacheExpiryMs) {
        super(processor);
        this.cache = new LinkedHashMap<String, CacheEntry>(16, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<String, CacheEntry> eldest) {
                return size() > maxCacheSize;
            }
        };
        this.maxCacheSize = maxCacheSize;
        this.cacheExpiryMs = cacheExpiryMs;
        this.cacheHits = 0;
        this.cacheMisses = 0;
    }
    
    public CacheDecorator(TextProcessor processor) {
        this(processor, 100, 5 * 60 * 1000); // 100 entries, 5 minutes expiry
    }
    
    @Override
    public String process(String text) {
        if (!processor.isEnabled()) {
            return super.process(text);
        }
        
        String cacheKey = generateCacheKey(text);
        CacheEntry entry = cache.get(cacheKey);
        
        if (entry != null && !entry.isExpired(cacheExpiryMs)) {
            cacheHits++;
            System.out.println("💾 Cache hit for text processing");
            
            // Update statistics
            Map<String, Object> stats = new HashMap<>(processor.getStatistics());
            stats.put("cache_hits", cacheHits);
            stats.put("cache_hit_ratio", (double) cacheHits / (cacheHits + cacheMisses));
            
            return entry.getResult();
        }
        
        // Cache miss - process and cache result
        cacheMisses++;
        long startTime = System.currentTimeMillis();
        
        String result = super.process(text);
        cache.put(cacheKey, new CacheEntry(result));
        
        // Update statistics
        Map<String, Object> stats = new HashMap<>(processor.getStatistics());
        stats.put("cache_misses", cacheMisses);
        stats.put("cache_hit_ratio", (double) cacheHits / (cacheHits + cacheMisses));
        stats.put("cache_size", cache.size());
        stats.put("cache_processing_time", System.currentTimeMillis() - startTime);
        
        return result;
    }
    
    private String generateCacheKey(String text) {
        // Simple hash-based cache key
        return String.valueOf(text.hashCode()) + "_" + processor.getClass().getSimpleName();
    }
    
    @Override
    public String getDescription() {
        return processor.getDescription() + " + Cache(" + cache.size() + "/" + maxCacheSize + ")";
    }
    
    public void clearCache() {
        cache.clear();
        System.out.println("🧹 Cache cleared");
    }
    
    public double getCacheHitRatio() {
        if (cacheHits + cacheMisses == 0) return 0.0;
        return (double) cacheHits / (cacheHits + cacheMisses);
    }
    
    public int getCacheSize() {
        return cache.size();
    }
}

// Text processing service
public class TextProcessingService {
    private String serviceName;
    private List<TextProcessor> processors;
    private Map<String, Object> globalStatistics;
    
    public TextProcessingService(String serviceName) {
        this.serviceName = serviceName;
        this.processors = new ArrayList<>();
        this.globalStatistics = new HashMap<>();
    }
    
    public void addProcessor(TextProcessor processor) {
        processors.add(processor);
        System.out.println("➕ Added processor: " + processor.getDescription());
    }
    
    public void removeProcessor(TextProcessor processor) {
        if (processors.remove(processor)) {
            System.out.println("➖ Removed processor: " + processor.getDescription());
        }
    }
    
    public String processText(String text) {
        System.out.println("\n" + "=".repeat(70));
        System.out.println("📝 " + serviceName + " - Processing Text");
        System.out.println("=".repeat(70));
        System.out.println("Original text: \"" + text + "\"");
        System.out.println("Original length: " + text.length() + " characters");
        
        String result = text;
        long totalProcessingTime = 0;
        
        for (int i = 0; i < processors.size(); i++) {
            TextProcessor processor = processors.get(i);
            
            if (processor.isEnabled()) {
                System.out.println("\n--- Step " + (i + 1) + ": " + processor.getDescription() + " ---");
                
                long stepStart = System.currentTimeMillis();
                String before = result;
                result = processor.process(result);
                long stepTime = System.currentTimeMillis() - stepStart;
                
                totalProcessingTime += stepTime;
                
                System.out.println("Input:  \"" + before + "\"");
                System.out.println("Output: \"" + result + "\"");
                System.out.println("Step processing time: " + stepTime + "ms");
                
                if (!before.equals(result)) {
                    System.out.println("✅ Text modified");
                } else {
                    System.out.println("➡️ Text unchanged");
                }
            } else {
                System.out.println("\n--- Step " + (i + 1) + ": " + processor.getDescription() + " (DISABLED) ---");
            }
        }
        
        System.out.println("\n" + "=".repeat(70));
        System.out.println("Final result: \"" + result + "\"");
        System.out.println("Final length: " + result.length() + " characters");
        System.out.println("Total processing time: " + totalProcessingTime + "ms");
        System.out.println("Active processors: " + processors.stream().mapToLong(p -> p.isEnabled() ? 1 : 0).sum());
        System.out.println("=".repeat(70));
        
        updateGlobalStatistics(text, result, totalProcessingTime);
        return result;
    }
    
    private void updateGlobalStatistics(String original, String result, long processingTime) {
        globalStatistics.put("texts_processed", 
            (Integer) globalStatistics.getOrDefault("texts_processed", 0) + 1);
        globalStatistics.put("total_processing_time", 
            (Long) globalStatistics.getOrDefault("total_processing_time", 0L) + processingTime);
        globalStatistics.put("original_chars_total", 
            (Integer) globalStatistics.getOrDefault("original_chars_total", 0) + original.length());
        globalStatistics.put("processed_chars_total", 
            (Integer) globalStatistics.getOrDefault("processed_chars_total", 0) + result.length());
    }
    
    public void showProcessorChain() {
        System.out.println("\n📋 " + serviceName + " - Processor Chain");
        System.out.println("=".repeat(50));
        
        for (int i = 0; i < processors.size(); i++) {
            TextProcessor processor = processors.get(i);
            String status = processor.isEnabled() ? "✅ Enabled" : "❌ Disabled";
            System.out.println((i + 1) + ". " + processor.getDescription() + " [" + status + "]");
        }
        
        System.out.println("=".repeat(50));
    }
    
    public void showStatistics() {
        System.out.println("\n📊 " + serviceName + " - Statistics");
        System.out.println("=".repeat(60));
        
        // Global statistics
        System.out.println("Global Statistics:");
        for (Map.Entry<String, Object> entry : globalStatistics.entrySet()) {
            System.out.println("  " + entry.getKey() + ": " + entry.getValue());
        }
        
        if (globalStatistics.containsKey("texts_processed") && 
            (Integer) globalStatistics.get("texts_processed") > 0) {
            long avgTime = (Long) globalStatistics.get("total_processing_time") / 
                          (Integer) globalStatistics.get("texts_processed");
            System.out.println("  average_processing_time: " + avgTime + "ms");
        }
        
        // Individual processor statistics
        System.out.println("\nProcessor Statistics:");
        for (int i = 0; i < processors.size(); i++) {
            TextProcessor processor = processors.get(i);
            System.out.println((i + 1) + ". " + processor.getDescription());
            Map<String, Object> stats = processor.getStatistics();
            for (Map.Entry<String, Object> entry : stats.entrySet()) {
                System.out.println("    " + entry.getKey() + ": " + entry.getValue());
            }
        }
        
        System.out.println("=".repeat(60));
    }
    
    public void enableAllProcessors() {
        processors.forEach(p -> p.setEnabled(true));
        System.out.println("✅ All processors enabled");
    }
    
    public void disableAllProcessors() {
        processors.forEach(p -> p.setEnabled(false));
        System.out.println("❌ All processors disabled");
    }
    
    public void clearAllCaches() {
        for (TextProcessor processor : processors) {
            if (processor instanceof CacheDecorator) {
                ((CacheDecorator) processor).clearCache();
            }
        }
    }
}
```

## Real-World Applications

### 1. **Stream Processing**
- Java I/O streams (BufferedReader wrapping FileReader)
- Data transformation pipelines
- Audio/video processing filters

### 2. **User Interface Components**
- Scrollable panels wrapping other components
- Bordered components
- Themed components (dark mode, accessibility)

### 3. **Web Development**
- HTTP middleware (authentication, logging, compression)
- Response decorators (caching, formatting)
- Request processors

### 4. **Security Systems**
- Authentication decorators
- Encryption/decryption wrappers
- Access control layers

### 5. **Data Processing**
- Compression decorators
- Serialization wrappers
- Validation layers

## Advantages and Disadvantages

### Advantages
1. **Runtime Composition**: Add/remove behaviors dynamically
2. **Single Responsibility**: Each decorator has one responsibility
3. **Open/Closed Principle**: Extend functionality without modifying existing code
4. **Flexible Combinations**: Mix and match decorators in any order
5. **Alternative to Inheritance**: Avoid class explosion from subclassing

### Disadvantages
1. **Complexity**: Many small objects can make debugging difficult
2. **Order Dependency**: Decorator order can affect behavior
3. **Identity Problems**: Decorated object has different identity than original
4. **Performance Overhead**: Multiple layers of wrapping
5. **Interface Bloat**: All decorators must support the full interface

## Decorator vs Other Patterns

### Decorator vs Adapter Pattern
- **Decorator**: Adds responsibilities; same interface
- **Adapter**: Changes interface; different interface
- Decorator enhances; Adapter converts

### Decorator vs Composite Pattern
- **Decorator**: Single component with added behavior
- **Composite**: Multiple components in tree structure
- Decorator wraps one; Composite contains many

### Decorator vs Strategy Pattern
- **Decorator**: Adds layers of functionality
- **Strategy**: Selects one algorithm from many
- Decorator enhances behavior; Strategy replaces behavior

### Decorator vs Proxy Pattern
- **Decorator**: Adds functionality
- **Proxy**: Controls access or provides placeholder
- Both wrap objects but different purposes

## Best Practices

1. **Keep Decorators Lightweight**: Each decorator should add minimal functionality
2. **Maintain Interface Consistency**: All decorators should fully implement the component interface
3. **Consider Order Dependencies**: Document when decorator order matters
4. **Use Abstract Decorator**: Provide default implementations to reduce code duplication
5. **Avoid Deep Nesting**: Too many decorators can hurt performance and debugging
6. **Handle Null Cases**: Check for null components in decorators
7. **Provide Unwrapping**: Allow access to underlying components when needed
8. **Document Side Effects**: Clearly document what each decorator does
9. **Use Factory Methods**: Create common decorator combinations
10. **Test Combinations**: Test various decorator combinations thoroughly

## Common Pitfalls to Avoid

1. **Interface Pollution**: Don't force decorators to implement irrelevant methods
2. **Tight Coupling**: Decorators should not depend on specific concrete components
3. **State Management**: Be careful with stateful decorators in multi-threaded environments
4. **Memory Leaks**: Ensure proper cleanup of decorator chains
5. **Order Sensitivity**: Document and test decorator ordering requirements
6. **Performance Issues**: Monitor performance with deep decorator chains

## Conclusion

The Decorator Pattern provides a flexible way to extend object functionality through composition rather than inheritance. It's particularly powerful when you need to add responsibilities dynamically or when subclassing would create too many classes.

Key takeaways:
- Use Decorator to add behaviors to objects without modifying their structure
- It's about enhancing existing functionality, not replacing it
- Decorators should be composable and order-independent when possible
- Great for creating flexible, configurable systems
- Provides runtime flexibility that inheritance cannot match

Remember: The Decorator Pattern is about wrapping objects to add functionality while maintaining the same interface. It's composition-based enhancement that allows for flexible, runtime behavior modification.