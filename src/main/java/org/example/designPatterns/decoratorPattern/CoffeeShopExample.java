package org.example.designPatterns.decoratorPattern;

import java.util.*;
import java.text.SimpleDateFormat;

/**
 * Coffee Shop Decorator Pattern Example
 * 
 * This example demonstrates the Decorator Pattern using a coffee ordering system.
 * Different coffee types are base components, and various add-ons (milk, sugar,
 * flavors, etc.) are decorators that can be combined in any order.
 * 
 * Features:
 * - Multiple base coffee types
 * - Various decorators for customization
 * - Dynamic pricing based on size and add-ons
 * - Nutritional information tracking
 * - Order management and receipts
 */

// Component interface
interface Coffee {
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
class SimpleCoffee implements Coffee {
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
            case "extra large": return 4.00;
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
class Espresso implements Coffee {
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

// Cappuccino as another base component
class Cappuccino implements Coffee {
    private String size;
    private List<String> ingredients;
    
    public Cappuccino() {
        this.size = "Medium";
        this.ingredients = Arrays.asList("Espresso", "Steamed milk", "Milk foam");
    }
    
    @Override
    public String getDescription() {
        return size + " Cappuccino";
    }
    
    @Override
    public double getCost() {
        switch (size.toLowerCase()) {
            case "small": return 3.75;
            case "medium": return 4.25;
            case "large": return 4.75;
            case "extra large": return 5.25;
            default: return 4.25;
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
        return 4; // Takes time to steam milk
    }
    
    @Override
    public int getCalories() {
        return 80; // Milk adds calories
    }
    
    @Override
    public boolean isAvailable() {
        return true;
    }
}

// Abstract decorator
abstract class CoffeeDecorator implements Coffee {
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
class MilkDecorator extends CoffeeDecorator {
    private MilkType milkType;
    
    public enum MilkType {
        WHOLE("Whole Milk", 0.50, 20, 1),
        SKIM("Skim Milk", 0.50, 10, 1),
        ALMOND("Almond Milk", 0.75, 15, 1),
        SOY("Soy Milk", 0.75, 18, 1),
        OAT("Oat Milk", 0.80, 25, 1),
        COCONUT("Coconut Milk", 0.70, 22, 1);
        
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

class SugarDecorator extends CoffeeDecorator {
    private int packets;
    private SugarType sugarType;
    
    public enum SugarType {
        WHITE("White Sugar", 0.0, 16, 0),
        BROWN("Brown Sugar", 0.10, 17, 0),
        HONEY("Honey", 0.25, 21, 1),
        STEVIA("Stevia", 0.15, 0, 0),
        ARTIFICIAL("Artificial Sweetener", 0.05, 0, 0),
        RAW("Raw Sugar", 0.05, 15, 0);
        
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

class FlavorDecorator extends CoffeeDecorator {
    private FlavorType flavorType;
    private double intensity; // 0.5 = light, 1.0 = normal, 1.5 = strong
    
    public enum FlavorType {
        VANILLA("Vanilla", 0.60, 15, 1),
        CARAMEL("Caramel", 0.70, 25, 1),
        HAZELNUT("Hazelnut", 0.65, 20, 1),
        CINNAMON("Cinnamon", 0.50, 5, 1),
        CHOCOLATE("Chocolate", 0.75, 30, 2),
        PEPPERMINT("Peppermint", 0.60, 10, 1),
        COCONUT("Coconut", 0.65, 18, 1),
        IRISH_CREAM("Irish Cream", 0.80, 28, 1),
        AMARETTO("Amaretto", 0.75, 22, 1);
        
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

class WhippedCreamDecorator extends CoffeeDecorator {
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

class ExtraShotDecorator extends CoffeeDecorator {
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

class SyrupDecorator extends CoffeeDecorator {
    private SyrupType syrupType;
    private int pumps;
    
    public enum SyrupType {
        SIMPLE("Simple Syrup", 0.30, 20, 0),
        VANILLA("Vanilla Syrup", 0.40, 25, 0),
        CARAMEL("Caramel Syrup", 0.45, 30, 0),
        HAZELNUT("Hazelnut Syrup", 0.40, 25, 0),
        CHOCOLATE("Chocolate Syrup", 0.45, 35, 1),
        RASPBERRY("Raspberry Syrup", 0.50, 28, 0),
        LAVENDER("Lavender Syrup", 0.60, 22, 0);
        
        private final String name;
        private final double costPerPump;
        private final int caloriesPerPump;
        private final int prepTime;
        
        SyrupType(String name, double cost, int calories, int prepTime) {
            this.name = name;
            this.costPerPump = cost;
            this.caloriesPerPump = calories;
            this.prepTime = prepTime;
        }
        
        public String getName() { return name; }
        public double getCostPerPump() { return costPerPump; }
        public int getCaloriesPerPump() { return caloriesPerPump; }
        public int getPrepTime() { return prepTime; }
    }
    
    public SyrupDecorator(Coffee coffee, SyrupType syrupType, int pumps) {
        super(coffee);
        this.syrupType = syrupType;
        this.pumps = Math.max(1, Math.min(6, pumps)); // 1-6 pumps
    }
    
    public SyrupDecorator(Coffee coffee, SyrupType syrupType) {
        this(coffee, syrupType, 2); // Default 2 pumps
    }
    
    @Override
    public String getDescription() {
        String pumpText = pumps == 1 ? "pump" : "pumps";
        return coffee.getDescription() + ", " + pumps + " " + pumpText + " of " + syrupType.getName();
    }
    
    @Override
    public double getCost() {
        return coffee.getCost() + (pumps * syrupType.getCostPerPump());
    }
    
    @Override
    public List<String> getIngredients() {
        List<String> ingredients = new ArrayList<>(coffee.getIngredients());
        ingredients.add(pumps + " pump(s) of " + syrupType.getName());
        return ingredients;
    }
    
    @Override
    public int getPreparationTime() {
        return coffee.getPreparationTime() + syrupType.getPrepTime();
    }
    
    @Override
    public int getCalories() {
        return coffee.getCalories() + (pumps * syrupType.getCaloriesPerPump());
    }
    
    public SyrupType getSyrupType() {
        return syrupType;
    }
    
    public int getPumps() {
        return pumps;
    }
}

// Coffee shop service class
class CoffeeShop {
    private String name;
    private Map<String, Double> sizePriceMultipliers;
    private Set<String> availableSizes;
    private boolean isOpen;
    private double taxRate;
    private List<Order> orders;
    private int orderCounter;
    
    public static class Order {
        private int orderNumber;
        private String customerName;
        private Coffee coffee;
        private Date orderTime;
        private boolean completed;
        
        public Order(int orderNumber, String customerName, Coffee coffee) {
            this.orderNumber = orderNumber;
            this.customerName = customerName;
            this.coffee = coffee;
            this.orderTime = new Date();
            this.completed = false;
        }
        
        public int getOrderNumber() { return orderNumber; }
        public String getCustomerName() { return customerName; }
        public Coffee getCoffee() { return coffee; }
        public Date getOrderTime() { return orderTime; }
        public boolean isCompleted() { return completed; }
        public void setCompleted(boolean completed) { this.completed = completed; }
    }
    
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
        this.orders = new ArrayList<>();
        this.orderCounter = 1;
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
    
    public Coffee createCappuccino(String size) {
        Coffee coffee = new Cappuccino();
        coffee.setSize(size);
        return coffee;
    }
    
    public Order placeOrder(String customerName, Coffee coffee) {
        if (!isOpen) {
            throw new IllegalStateException("Sorry, " + name + " is currently closed.");
        }
        
        Order order = new Order(orderCounter++, customerName, coffee);
        orders.add(order);
        
        System.out.println("📋 Order #" + order.getOrderNumber() + " placed for " + customerName);
        System.out.println("Estimated preparation time: " + coffee.getPreparationTime() + " minutes");
        
        return order;
    }
    
    public void completeOrder(int orderNumber) {
        Order order = orders.stream()
            .filter(o -> o.getOrderNumber() == orderNumber)
            .findFirst()
            .orElse(null);
            
        if (order != null) {
            order.setCompleted(true);
            System.out.println("✅ Order #" + orderNumber + " completed for " + order.getCustomerName());
        }
    }
    
    public void printReceipt(Order order) {
        if (!isOpen) {
            System.out.println("Sorry, " + name + " is currently closed.");
            return;
        }
        
        Coffee coffee = order.getCoffee();
        
        System.out.println("\n" + "=".repeat(45));
        System.out.println("         " + name.toUpperCase());
        System.out.println("=".repeat(45));
        System.out.println("Order #: " + order.getOrderNumber());
        System.out.println("Customer: " + order.getCustomerName());
        System.out.println("Date: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(order.getOrderTime()));
        System.out.println("-".repeat(45));
        
        System.out.println("Item: " + coffee.getDescription());
        System.out.println("Size: " + coffee.getSize());
        
        System.out.println("\nIngredients:");
        for (String ingredient : coffee.getIngredients()) {
            System.out.println("  • " + ingredient);
        }
        
        double subtotal = coffee.getCost();
        double tax = subtotal * taxRate;
        double total = subtotal + tax;
        
        System.out.println("\n" + "-".repeat(45));
        System.out.printf("Subtotal: $%.2f%n", subtotal);
        System.out.printf("Tax (%.0f%%): $%.2f%n", taxRate * 100, tax);
        System.out.printf("TOTAL: $%.2f%n", total);
        
        System.out.println("\nNutritional Information:");
        System.out.println("Calories: " + coffee.getCalories());
        System.out.println("Preparation time: " + coffee.getPreparationTime() + " minutes");
        System.out.println("Status: " + (order.isCompleted() ? "Ready" : "In preparation"));
        
        System.out.println("\nThank you for visiting " + name + "!");
        System.out.println("=".repeat(45));
    }
    
    public void showMenu() {
        System.out.println("\n" + "=".repeat(55));
        System.out.println("            " + name.toUpperCase() + " MENU");
        System.out.println("=".repeat(55));
        
        System.out.println("\n☕ BASE COFFEE OPTIONS:");
        System.out.println("• Simple Coffee - Starting at $2.50");
        System.out.println("• Espresso - Starting at $2.00");
        System.out.println("• Cappuccino - Starting at $3.75");
        
        System.out.println("\n📏 AVAILABLE SIZES:");
        for (String size : availableSizes) {
            System.out.printf("• %s (%.0f%% of base price)%n", 
                size.substring(0, 1).toUpperCase() + size.substring(1),
                sizePriceMultipliers.get(size) * 100);
        }
        
        System.out.println("\n🥛 MILK OPTIONS:");
        for (MilkDecorator.MilkType milk : MilkDecorator.MilkType.values()) {
            System.out.printf("  • %s - $%.2f%n", milk.getName(), milk.getCost());
        }
        
        System.out.println("\n🍯 SWEETENER OPTIONS:");
        for (SugarDecorator.SugarType sugar : SugarDecorator.SugarType.values()) {
            System.out.printf("  • %s - $%.2f per packet%n", sugar.getName(), sugar.getCostPerPacket());
        }
        
        System.out.println("\n🌟 FLAVOR OPTIONS:");
        for (FlavorDecorator.FlavorType flavor : FlavorDecorator.FlavorType.values()) {
            System.out.printf("  • %s - $%.2f%n", flavor.getName(), flavor.getBaseCost());
        }
        
        System.out.println("\n🍯 SYRUP OPTIONS:");
        for (SyrupDecorator.SyrupType syrup : SyrupDecorator.SyrupType.values()) {
            System.out.printf("  • %s - $%.2f per pump%n", syrup.getName(), syrup.getCostPerPump());
        }
        
        System.out.println("\n➕ EXTRA OPTIONS:");
        System.out.println("  • Extra Shot - $0.75 per shot");
        System.out.println("  • Whipped Cream - $0.80+");
        
        System.out.println("\n" + "=".repeat(55));
    }
    
    public void showDailyStats() {
        System.out.println("\n📊 Daily Statistics for " + name);
        System.out.println("=".repeat(40));
        
        int totalOrders = orders.size();
        int completedOrders = (int) orders.stream().mapToLong(o -> o.isCompleted() ? 1 : 0).sum();
        double totalRevenue = orders.stream().mapToDouble(o -> o.getCoffee().getCost()).sum();
        
        System.out.println("Total orders: " + totalOrders);
        System.out.println("Completed orders: " + completedOrders);
        System.out.println("Pending orders: " + (totalOrders - completedOrders));
        System.out.printf("Total revenue: $%.2f%n", totalRevenue);
        
        if (totalOrders > 0) {
            System.out.printf("Average order value: $%.2f%n", totalRevenue / totalOrders);
        }
        
        // Most popular size
        Map<String, Long> sizeCounts = orders.stream()
            .collect(java.util.stream.Collectors.groupingBy(
                o -> o.getCoffee().getSize(),
                java.util.stream.Collectors.counting()
            ));
        
        String popularSize = sizeCounts.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse("N/A");
        
        System.out.println("Most popular size: " + popularSize);
        System.out.println("=".repeat(40));
    }
    
    public boolean isValidSize(String size) {
        return availableSizes.contains(size.toLowerCase());
    }
    
    public void setOpen(boolean open) {
        this.isOpen = open;
        System.out.println(name + " is now " + (open ? "OPEN" : "CLOSED"));
    }
    
    public boolean isOpen() {
        return isOpen;
    }
    
    public List<Order> getOrders() {
        return new ArrayList<>(orders);
    }
}

// Demo class
public class CoffeeShopExample {
    public static void main(String[] args) {
        System.out.println("=== ☕ Decorator Pattern: Coffee Shop Example ===\n");
        
        CoffeeShop shop = new CoffeeShop("Bean There Coffee");
        
        // Show menu
        shop.showMenu();
        
        System.out.println("\n🎉 Welcome to Bean There Coffee!");
        System.out.println("Let's create some custom coffee orders using the Decorator Pattern!\n");
        
        // Example 1: Simple order
        System.out.println("--- 📋 Order 1: Simple Coffee ---");
        Coffee coffee1 = shop.createCustomCoffee("large");
        Order order1 = shop.placeOrder("Alice Johnson", coffee1);
        shop.printReceipt(order1);
        shop.completeOrder(order1.getOrderNumber());
        
        // Example 2: Decorated coffee
        System.out.println("\n--- 📋 Order 2: Customized Coffee ---");
        Coffee coffee2 = shop.createCustomCoffee("medium");
        coffee2 = new MilkDecorator(coffee2, MilkDecorator.MilkType.ALMOND);
        coffee2 = new SugarDecorator(coffee2, 2, SugarDecorator.SugarType.HONEY);
        coffee2 = new FlavorDecorator(coffee2, FlavorDecorator.FlavorType.VANILLA, 1.5);
        
        Order order2 = shop.placeOrder("Bob Smith", coffee2);
        shop.printReceipt(order2);
        shop.completeOrder(order2.getOrderNumber());
        
        // Example 3: Complex decorated espresso
        System.out.println("\n--- 📋 Order 3: Premium Espresso ---");
        Coffee coffee3 = shop.createEspresso("small");
        coffee3 = new ExtraShotDecorator(coffee3, 2, ExtraShotDecorator.ShotType.RISTRETTO);
        coffee3 = new MilkDecorator(coffee3, MilkDecorator.MilkType.OAT);
        coffee3 = new FlavorDecorator(coffee3, FlavorDecorator.FlavorType.HAZELNUT);
        coffee3 = new WhippedCreamDecorator(coffee3, WhippedCreamDecorator.CreamType.LIGHT, 0.5);
        
        Order order3 = shop.placeOrder("Carol Davis", coffee3);
        shop.printReceipt(order3);
        shop.completeOrder(order3.getOrderNumber());
        
        // Example 4: Cappuccino with multiple decorators
        System.out.println("\n--- 📋 Order 4: Designer Cappuccino ---");
        Coffee coffee4 = shop.createCappuccino("large");
        coffee4 = new SyrupDecorator(coffee4, SyrupDecorator.SyrupType.CARAMEL, 3);
        coffee4 = new FlavorDecorator(coffee4, FlavorDecorator.FlavorType.CINNAMON, 0.5);
        coffee4 = new WhippedCreamDecorator(coffee4, WhippedCreamDecorator.CreamType.REGULAR, 1.5);
        coffee4 = new SugarDecorator(coffee4, 1, SugarDecorator.SugarType.BROWN);
        
        Order order4 = shop.placeOrder("David Wilson", coffee4);
        shop.printReceipt(order4);
        shop.completeOrder(order4.getOrderNumber());
        
        // Example 5: Health-conscious order
        System.out.println("\n--- 📋 Order 5: Health-Conscious Coffee ---");
        Coffee coffee5 = shop.createCustomCoffee("medium");
        coffee5 = new MilkDecorator(coffee5, MilkDecorator.MilkType.SKIM);
        coffee5 = new SugarDecorator(coffee5, 2, SugarDecorator.SugarType.STEVIA);
        coffee5 = new FlavorDecorator(coffee5, FlavorDecorator.FlavorType.VANILLA, 0.5);
        
        Order order5 = shop.placeOrder("Eva Martinez", coffee5);
        shop.printReceipt(order5);
        shop.completeOrder(order5.getOrderNumber());
        
        // Example 6: Extreme customization
        System.out.println("\n--- 📋 Order 6: The Ultimate Custom Coffee ---");
        Coffee coffee6 = shop.createCappuccino("extra large");
        coffee6 = new ExtraShotDecorator(coffee6, 3, ExtraShotDecorator.ShotType.ESPRESSO);
        coffee6 = new MilkDecorator(coffee6, MilkDecorator.MilkType.COCONUT);
        coffee6 = new SyrupDecorator(coffee6, SyrupDecorator.SyrupType.VANILLA, 2);
        coffee6 = new SyrupDecorator(coffee6, SyrupDecorator.SyrupType.CARAMEL, 1);
        coffee6 = new FlavorDecorator(coffee6, FlavorDecorator.FlavorType.CHOCOLATE, 1.5);
        coffee6 = new WhippedCreamDecorator(coffee6, WhippedCreamDecorator.CreamType.COCONUT, 2.0);
        coffee6 = new SugarDecorator(coffee6, 1, SugarDecorator.SugarType.RAW);
        
        Order order6 = shop.placeOrder("Frank Thompson", coffee6);
        shop.printReceipt(order6);
        shop.completeOrder(order6.getOrderNumber());
        
        // Show daily statistics
        shop.showDailyStats();
        
        // Demonstrate order flexibility
        System.out.println("\n🔄 Demonstrating Decorator Pattern Flexibility:");
        System.out.println("=".repeat(60));
        
        Coffee baseCoffee = new SimpleCoffee();
        baseCoffee.setSize("medium");
        
        System.out.println("Base coffee: " + baseCoffee.getDescription() + " - $" + 
                         String.format("%.2f", baseCoffee.getCost()));
        
        // Add decorators one by one
        Coffee decorated = new MilkDecorator(baseCoffee, MilkDecorator.MilkType.ALMOND);
        System.out.println("+ Almond milk: " + decorated.getDescription() + " - $" + 
                         String.format("%.2f", decorated.getCost()));
        
        decorated = new FlavorDecorator(decorated, FlavorDecorator.FlavorType.VANILLA);
        System.out.println("+ Vanilla flavor: " + decorated.getDescription() + " - $" + 
                         String.format("%.2f", decorated.getCost()));
        
        decorated = new WhippedCreamDecorator(decorated);
        System.out.println("+ Whipped cream: " + decorated.getDescription() + " - $" + 
                         String.format("%.2f", decorated.getCost()));
        
        decorated = new ExtraShotDecorator(decorated, 1);
        System.out.println("+ Extra shot: " + decorated.getDescription() + " - $" + 
                         String.format("%.2f", decorated.getCost()));
        
        System.out.println("\nFinal decorated coffee:");
        System.out.println("Description: " + decorated.getDescription());
        System.out.println("Total cost: $" + String.format("%.2f", decorated.getCost()));
        System.out.println("Total calories: " + decorated.getCalories());
        System.out.println("Preparation time: " + decorated.getPreparationTime() + " minutes");
        System.out.println("Ingredients: " + String.join(", ", decorated.getIngredients()));
        
        System.out.println("\n=== ✅ Demo Complete ===");
        
        // Key benefits demonstrated
        System.out.println("\n🎯 Key Benefits Demonstrated:");
        System.out.println("✅ Dynamic composition - decorators added at runtime");
        System.out.println("✅ Multiple decorators can be combined in any order");
        System.out.println("✅ Each decorator adds specific functionality");
        System.out.println("✅ Base components remain unchanged");
        System.out.println("✅ Flexible customization without class explosion");
        System.out.println("✅ Easy to add new decorators without modifying existing code");
        System.out.println("✅ Transparent interface - decorated objects work like base objects");
    }
}