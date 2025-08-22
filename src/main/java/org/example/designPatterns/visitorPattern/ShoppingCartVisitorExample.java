package org.example.designPatterns.visitorPattern;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Shopping Cart Visitor Example
 * 
 * This example demonstrates the Visitor Pattern applied to an e-commerce shopping cart.
 * Different visitors can handle pricing strategies, discounts, tax calculations, 
 * shipping costs, and promotional offers without modifying the product classes.
 * 
 * Key Features:
 * - Multiple product types with different pricing rules
 * - Various discount strategies applied via visitors
 * - Tax calculation with different rates per product type
 * - Shipping cost calculation based on product attributes
 * - Report generation for analytics
 */

// Visitor interface
interface ShoppingCartVisitor {
    void visit(Book book);
    void visit(Electronics electronics);
    void visit(Clothing clothing);
    void visit(Food food);
}

// Element interface
interface CartItem {
    void accept(ShoppingCartVisitor visitor);
    String getName();
    double getBasePrice();
    int getQuantity();
    String getCategory();
}

// Abstract base class for common functionality
abstract class Product implements CartItem {
    protected String name;
    protected double basePrice;
    protected int quantity;
    protected String category;
    protected String brand;
    
    public Product(String name, double basePrice, int quantity, String category, String brand) {
        this.name = name;
        this.basePrice = basePrice;
        this.quantity = quantity;
        this.category = category;
        this.brand = brand;
    }
    
    @Override
    public String getName() { return name; }
    
    @Override
    public double getBasePrice() { return basePrice; }
    
    @Override
    public int getQuantity() { return quantity; }
    
    @Override
    public String getCategory() { return category; }
    
    public String getBrand() { return brand; }
}

// Concrete Products
class Book extends Product {
    private String author;
    private int pages;
    private boolean isEbook;
    private String genre;
    
    public Book(String name, double basePrice, int quantity, String author, 
                int pages, boolean isEbook, String genre) {
        super(name, basePrice, quantity, "Books", "Various");
        this.author = author;
        this.pages = pages;
        this.isEbook = isEbook;
        this.genre = genre;
    }
    
    @Override
    public void accept(ShoppingCartVisitor visitor) {
        visitor.visit(this);
    }
    
    public String getAuthor() { return author; }
    public int getPages() { return pages; }
    public boolean isEbook() { return isEbook; }
    public String getGenre() { return genre; }
    
    @Override
    public String toString() {
        return "Book{" + name + " by " + author + ", $" + basePrice + " x " + quantity + "}";
    }
}

class Electronics extends Product {
    private int warrantyMonths;
    private double weight;
    private boolean isFragile;
    private String model;
    
    public Electronics(String name, double basePrice, int quantity, String brand,
                      int warrantyMonths, double weight, boolean isFragile, String model) {
        super(name, basePrice, quantity, "Electronics", brand);
        this.warrantyMonths = warrantyMonths;
        this.weight = weight;
        this.isFragile = isFragile;
        this.model = model;
    }
    
    @Override
    public void accept(ShoppingCartVisitor visitor) {
        visitor.visit(this);
    }
    
    public int getWarrantyMonths() { return warrantyMonths; }
    public double getWeight() { return weight; }
    public boolean isFragile() { return isFragile; }
    public String getModel() { return model; }
    
    @Override
    public String toString() {
        return "Electronics{" + name + " " + model + ", $" + basePrice + " x " + quantity + "}";
    }
}

class Clothing extends Product {
    private String size;
    private String color;
    private String material;
    private boolean isOnSale;
    
    public Clothing(String name, double basePrice, int quantity, String brand,
                   String size, String color, String material, boolean isOnSale) {
        super(name, basePrice, quantity, "Clothing", brand);
        this.size = size;
        this.color = color;
        this.material = material;
        this.isOnSale = isOnSale;
    }
    
    @Override
    public void accept(ShoppingCartVisitor visitor) {
        visitor.visit(this);
    }
    
    public String getSize() { return size; }
    public String getColor() { return color; }
    public String getMaterial() { return material; }
    public boolean isOnSale() { return isOnSale; }
    
    @Override
    public String toString() {
        return "Clothing{" + name + " " + size + " " + color + ", $" + basePrice + " x " + quantity + "}";
    }
}

class Food extends Product {
    private LocalDate expiryDate;
    private boolean isPerishable;
    private double weight;
    private boolean isOrganic;
    
    public Food(String name, double basePrice, int quantity, String brand,
               LocalDate expiryDate, boolean isPerishable, double weight, boolean isOrganic) {
        super(name, basePrice, quantity, "Food", brand);
        this.expiryDate = expiryDate;
        this.isPerishable = isPerishable;
        this.weight = weight;
        this.isOrganic = isOrganic;
    }
    
    @Override
    public void accept(ShoppingCartVisitor visitor) {
        visitor.visit(this);
    }
    
    public LocalDate getExpiryDate() { return expiryDate; }
    public boolean isPerishable() { return isPerishable; }
    public double getWeight() { return weight; }
    public boolean isOrganic() { return isOrganic; }
    
    @Override
    public String toString() {
        return "Food{" + name + ", $" + basePrice + " x " + quantity + 
               ", expires: " + expiryDate + "}";
    }
}

// Concrete Visitor - Price Calculator
class PriceCalculatorVisitor implements ShoppingCartVisitor {
    private double totalPrice = 0;
    private double bookTotal = 0;
    private double electronicsTotal = 0;
    private double clothingTotal = 0;
    private double foodTotal = 0;
    
    @Override
    public void visit(Book book) {
        double itemTotal = book.getBasePrice() * book.getQuantity();
        // Apply book-specific pricing rules
        if (book.isEbook()) {
            itemTotal *= 0.85; // 15% discount for ebooks
        }
        if (book.getPages() > 500) {
            itemTotal *= 1.1; // 10% premium for thick books
        }
        
        bookTotal += itemTotal;
        totalPrice += itemTotal;
        System.out.println("Book: " + book.getName() + " = $" + 
                          String.format("%.2f", itemTotal));
    }
    
    @Override
    public void visit(Electronics electronics) {
        double itemTotal = electronics.getBasePrice() * electronics.getQuantity();
        // Apply electronics-specific pricing rules
        if (electronics.getWarrantyMonths() > 24) {
            itemTotal *= 1.15; // 15% premium for extended warranty
        }
        if ("Apple".equals(electronics.getBrand()) || "Samsung".equals(electronics.getBrand())) {
            itemTotal *= 1.05; // 5% premium for premium brands
        }
        
        electronicsTotal += itemTotal;
        totalPrice += itemTotal;
        System.out.println("Electronics: " + electronics.getName() + " = $" + 
                          String.format("%.2f", itemTotal));
    }
    
    @Override
    public void visit(Clothing clothing) {
        double itemTotal = clothing.getBasePrice() * clothing.getQuantity();
        // Apply clothing-specific pricing rules
        if (clothing.isOnSale()) {
            itemTotal *= 0.8; // 20% discount for sale items
        }
        if ("Premium".equals(clothing.getMaterial())) {
            itemTotal *= 1.25; // 25% premium for premium materials
        }
        
        clothingTotal += itemTotal;
        totalPrice += itemTotal;
        System.out.println("Clothing: " + clothing.getName() + " = $" + 
                          String.format("%.2f", itemTotal));
    }
    
    @Override
    public void visit(Food food) {
        double itemTotal = food.getBasePrice() * food.getQuantity();
        // Apply food-specific pricing rules
        if (food.isOrganic()) {
            itemTotal *= 1.2; // 20% premium for organic
        }
        
        // Check for near expiry discount
        long daysToExpiry = LocalDate.now().until(food.getExpiryDate()).getDays();
        if (daysToExpiry <= 3) {
            itemTotal *= 0.5; // 50% discount for items expiring soon
        }
        
        foodTotal += itemTotal;
        totalPrice += itemTotal;
        System.out.println("Food: " + food.getName() + " = $" + 
                          String.format("%.2f", itemTotal));
    }
    
    public double getTotalPrice() { return totalPrice; }
    public double getBookTotal() { return bookTotal; }
    public double getElectronicsTotal() { return electronicsTotal; }
    public double getClothingTotal() { return clothingTotal; }
    public double getFoodTotal() { return foodTotal; }
    
    public void printSummary() {
        System.out.println("\n=== Price Summary ===");
        System.out.println("Books: $" + String.format("%.2f", bookTotal));
        System.out.println("Electronics: $" + String.format("%.2f", electronicsTotal));
        System.out.println("Clothing: $" + String.format("%.2f", clothingTotal));
        System.out.println("Food: $" + String.format("%.2f", foodTotal));
        System.out.println("Total: $" + String.format("%.2f", totalPrice));
    }
}

// Concrete Visitor - Discount Calculator
class DiscountCalculatorVisitor implements ShoppingCartVisitor {
    private double totalDiscount = 0;
    private String customerType;
    private boolean hasPremiumMembership;
    
    public DiscountCalculatorVisitor(String customerType, boolean hasPremiumMembership) {
        this.customerType = customerType;
        this.hasPremiumMembership = hasPremiumMembership;
    }
    
    @Override
    public void visit(Book book) {
        double itemTotal = book.getBasePrice() * book.getQuantity();
        double discount = 0;
        
        if ("Student".equals(customerType)) {
            discount = itemTotal * 0.15; // 15% student discount
        } else if (hasPremiumMembership) {
            discount = itemTotal * 0.10; // 10% premium member discount
        }
        
        if (book.getQuantity() >= 3) {
            discount += itemTotal * 0.05; // 5% bulk discount
        }
        
        totalDiscount += discount;
        if (discount > 0) {
            System.out.println("Book discount for " + book.getName() + ": $" + 
                             String.format("%.2f", discount));
        }
    }
    
    @Override
    public void visit(Electronics electronics) {
        double itemTotal = electronics.getBasePrice() * electronics.getQuantity();
        double discount = 0;
        
        if ("Employee".equals(customerType)) {
            discount = itemTotal * 0.20; // 20% employee discount
        } else if (hasPremiumMembership) {
            discount = itemTotal * 0.08; // 8% premium member discount
        }
        
        if (itemTotal > 500) {
            discount += 50; // $50 discount for purchases over $500
        }
        
        totalDiscount += discount;
        if (discount > 0) {
            System.out.println("Electronics discount for " + electronics.getName() + ": $" + 
                             String.format("%.2f", discount));
        }
    }
    
    @Override
    public void visit(Clothing clothing) {
        double itemTotal = clothing.getBasePrice() * clothing.getQuantity();
        double discount = 0;
        
        if (hasPremiumMembership) {
            discount = itemTotal * 0.12; // 12% premium member discount on clothing
        }
        
        if (clothing.getQuantity() >= 2) {
            discount += itemTotal * 0.10; // 10% buy-2-or-more discount
        }
        
        totalDiscount += discount;
        if (discount > 0) {
            System.out.println("Clothing discount for " + clothing.getName() + ": $" + 
                             String.format("%.2f", discount));
        }
    }
    
    @Override
    public void visit(Food food) {
        double itemTotal = food.getBasePrice() * food.getQuantity();
        double discount = 0;
        
        if (food.isOrganic() && hasPremiumMembership) {
            discount = itemTotal * 0.08; // 8% discount for organic food with membership
        }
        
        // Bulk discount for food
        if (food.getWeight() * food.getQuantity() > 5.0) {
            discount += itemTotal * 0.05; // 5% bulk discount
        }
        
        totalDiscount += discount;
        if (discount > 0) {
            System.out.println("Food discount for " + food.getName() + ": $" + 
                             String.format("%.2f", discount));
        }
    }
    
    public double getTotalDiscount() { return totalDiscount; }
    
    public void printSummary() {
        System.out.println("\n=== Discount Summary ===");
        System.out.println("Customer Type: " + customerType);
        System.out.println("Premium Membership: " + (hasPremiumMembership ? "Yes" : "No"));
        System.out.println("Total Discounts: $" + String.format("%.2f", totalDiscount));
    }
}

// Concrete Visitor - Tax Calculator
class TaxCalculatorVisitor implements ShoppingCartVisitor {
    private double totalTax = 0;
    private double salesTaxRate = 0.08; // 8% default sales tax
    
    public TaxCalculatorVisitor(double salesTaxRate) {
        this.salesTaxRate = salesTaxRate;
    }
    
    @Override
    public void visit(Book book) {
        double itemTotal = book.getBasePrice() * book.getQuantity();
        double tax = 0;
        
        if (!book.isEbook()) {
            tax = itemTotal * salesTaxRate; // Physical books are taxed
        }
        // E-books are tax-exempt in this example
        
        totalTax += tax;
        if (tax > 0) {
            System.out.println("Tax for " + book.getName() + ": $" + 
                             String.format("%.2f", tax));
        }
    }
    
    @Override
    public void visit(Electronics electronics) {
        double itemTotal = electronics.getBasePrice() * electronics.getQuantity();
        double tax = itemTotal * salesTaxRate; // All electronics are taxed
        
        totalTax += tax;
        System.out.println("Tax for " + electronics.getName() + ": $" + 
                          String.format("%.2f", tax));
    }
    
    @Override
    public void visit(Clothing clothing) {
        double itemTotal = clothing.getBasePrice() * clothing.getQuantity();
        double tax = 0;
        
        if (itemTotal > 100) {
            tax = itemTotal * salesTaxRate; // Luxury tax for expensive clothing
        }
        
        totalTax += tax;
        if (tax > 0) {
            System.out.println("Tax for " + clothing.getName() + ": $" + 
                             String.format("%.2f", tax));
        }
    }
    
    @Override
    public void visit(Food food) {
        double itemTotal = food.getBasePrice() * food.getQuantity();
        double tax = 0;
        
        if (!food.isPerishable()) {
            tax = itemTotal * (salesTaxRate * 0.5); // Reduced tax for non-perishable food
        }
        // Fresh/perishable food is tax-exempt
        
        totalTax += tax;
        if (tax > 0) {
            System.out.println("Tax for " + food.getName() + ": $" + 
                             String.format("%.2f", tax));
        }
    }
    
    public double getTotalTax() { return totalTax; }
    
    public void printSummary() {
        System.out.println("\n=== Tax Summary ===");
        System.out.println("Sales Tax Rate: " + (salesTaxRate * 100) + "%");
        System.out.println("Total Tax: $" + String.format("%.2f", totalTax));
    }
}

// Concrete Visitor - Shipping Calculator
class ShippingCalculatorVisitor implements ShoppingCartVisitor {
    private double totalShipping = 0;
    private String shippingZone;
    
    public ShippingCalculatorVisitor(String shippingZone) {
        this.shippingZone = shippingZone;
    }
    
    @Override
    public void visit(Book book) {
        double shipping = 0;
        
        if (book.isEbook()) {
            shipping = 0; // No shipping for e-books
        } else {
            shipping = book.getQuantity() * 2.5; // $2.50 per book
            if ("International".equals(shippingZone)) {
                shipping *= 3; // International shipping multiplier
            }
        }
        
        totalShipping += shipping;
        if (shipping > 0) {
            System.out.println("Shipping for " + book.getName() + ": $" + 
                             String.format("%.2f", shipping));
        }
    }
    
    @Override
    public void visit(Electronics electronics) {
        double baseShipping = electronics.getWeight() * 1.5; // $1.50 per pound
        
        if (electronics.isFragile()) {
            baseShipping *= 1.5; // 50% surcharge for fragile items
        }
        
        if ("International".equals(shippingZone)) {
            baseShipping *= 4; // Higher international shipping for electronics
        }
        
        double shipping = baseShipping * electronics.getQuantity();
        totalShipping += shipping;
        System.out.println("Shipping for " + electronics.getName() + ": $" + 
                          String.format("%.2f", shipping));
    }
    
    @Override
    public void visit(Clothing clothing) {
        double shipping = clothing.getQuantity() * 3.0; // $3.00 per item
        
        if ("International".equals(shippingZone)) {
            shipping *= 2.5;
        }
        
        totalShipping += shipping;
        System.out.println("Shipping for " + clothing.getName() + ": $" + 
                          String.format("%.2f", shipping));
    }
    
    @Override
    public void visit(Food food) {
        double shipping = 0;
        
        if (food.isPerishable()) {
            shipping = food.getWeight() * 5.0; // $5.00 per pound for perishable
            if ("International".equals(shippingZone)) {
                shipping = 0; // No international shipping for perishable food
                System.out.println("Cannot ship perishable food internationally: " + food.getName());
                return;
            }
        } else {
            shipping = food.getWeight() * 2.0; // $2.00 per pound for non-perishable
            if ("International".equals(shippingZone)) {
                shipping *= 2;
            }
        }
        
        shipping *= food.getQuantity();
        totalShipping += shipping;
        if (shipping > 0) {
            System.out.println("Shipping for " + food.getName() + ": $" + 
                             String.format("%.2f", shipping));
        }
    }
    
    public double getTotalShipping() { return totalShipping; }
    
    public void printSummary() {
        System.out.println("\n=== Shipping Summary ===");
        System.out.println("Shipping Zone: " + shippingZone);
        System.out.println("Total Shipping: $" + String.format("%.2f", totalShipping));
    }
}

// Concrete Visitor - Inventory Reporter
class InventoryReportVisitor implements ShoppingCartVisitor {
    private List<String> lowStockItems = new ArrayList<>();
    private List<String> highValueItems = new ArrayList<>();
    private int totalItems = 0;
    
    @Override
    public void visit(Book book) {
        totalItems += book.getQuantity();
        
        if (book.getQuantity() > 10) {
            lowStockItems.add(book.getName() + " (quantity: " + book.getQuantity() + ")");
        }
        
        double totalValue = book.getBasePrice() * book.getQuantity();
        if (totalValue > 100) {
            highValueItems.add(book.getName() + " ($" + String.format("%.2f", totalValue) + ")");
        }
    }
    
    @Override
    public void visit(Electronics electronics) {
        totalItems += electronics.getQuantity();
        
        if (electronics.getQuantity() > 5) {
            lowStockItems.add(electronics.getName() + " (quantity: " + electronics.getQuantity() + ")");
        }
        
        double totalValue = electronics.getBasePrice() * electronics.getQuantity();
        if (totalValue > 200) {
            highValueItems.add(electronics.getName() + " ($" + String.format("%.2f", totalValue) + ")");
        }
    }
    
    @Override
    public void visit(Clothing clothing) {
        totalItems += clothing.getQuantity();
        
        if (clothing.getQuantity() > 8) {
            lowStockItems.add(clothing.getName() + " (quantity: " + clothing.getQuantity() + ")");
        }
        
        double totalValue = clothing.getBasePrice() * clothing.getQuantity();
        if (totalValue > 150) {
            highValueItems.add(clothing.getName() + " ($" + String.format("%.2f", totalValue) + ")");
        }
    }
    
    @Override
    public void visit(Food food) {
        totalItems += food.getQuantity();
        
        // Check for expiry concerns
        long daysToExpiry = LocalDate.now().until(food.getExpiryDate()).getDays();
        if (daysToExpiry <= 5) {
            lowStockItems.add(food.getName() + " (expires in " + daysToExpiry + " days)");
        }
    }
    
    public void printReport() {
        System.out.println("\n=== Inventory Report ===");
        System.out.println("Total Items in Cart: " + totalItems);
        
        System.out.println("\nItems needing attention:");
        if (lowStockItems.isEmpty()) {
            System.out.println("No items need attention");
        } else {
            for (String item : lowStockItems) {
                System.out.println("- " + item);
            }
        }
        
        System.out.println("\nHigh-value items:");
        if (highValueItems.isEmpty()) {
            System.out.println("No high-value items");
        } else {
            for (String item : highValueItems) {
                System.out.println("- " + item);
            }
        }
    }
}

// Shopping Cart class
class ShoppingCart {
    private List<CartItem> items = new ArrayList<>();
    
    public void addItem(CartItem item) {
        items.add(item);
    }
    
    public void removeItem(CartItem item) {
        items.remove(item);
    }
    
    public void accept(ShoppingCartVisitor visitor) {
        for (CartItem item : items) {
            item.accept(visitor);
        }
    }
    
    public List<CartItem> getItems() {
        return new ArrayList<>(items);
    }
    
    public void printCart() {
        System.out.println("Shopping Cart Contents:");
        for (CartItem item : items) {
            System.out.println("- " + item);
        }
    }
}

// Demo class
public class ShoppingCartVisitorExample {
    public static void main(String[] args) {
        System.out.println("=== Shopping Cart Visitor Pattern Example ===\n");
        
        // Create shopping cart with various items
        ShoppingCart cart = createSampleCart();
        cart.printCart();
        
        System.out.println("\n" + "=".repeat(50));
        
        // Calculate prices
        System.out.println("1. Calculating prices:");
        PriceCalculatorVisitor priceCalculator = new PriceCalculatorVisitor();
        cart.accept(priceCalculator);
        priceCalculator.printSummary();
        
        System.out.println("\n" + "=".repeat(50));
        
        // Calculate discounts for premium member
        System.out.println("2. Calculating discounts (Premium Member):");
        DiscountCalculatorVisitor discountCalculator = 
            new DiscountCalculatorVisitor("Regular", true);
        cart.accept(discountCalculator);
        discountCalculator.printSummary();
        
        System.out.println("\n" + "=".repeat(50));
        
        // Calculate taxes
        System.out.println("3. Calculating taxes:");
        TaxCalculatorVisitor taxCalculator = new TaxCalculatorVisitor(0.08);
        cart.accept(taxCalculator);
        taxCalculator.printSummary();
        
        System.out.println("\n" + "=".repeat(50));
        
        // Calculate shipping
        System.out.println("4. Calculating shipping (Domestic):");
        ShippingCalculatorVisitor shippingCalculator = 
            new ShippingCalculatorVisitor("Domestic");
        cart.accept(shippingCalculator);
        shippingCalculator.printSummary();
        
        System.out.println("\n" + "=".repeat(50));
        
        // Generate inventory report
        System.out.println("5. Inventory report:");
        InventoryReportVisitor inventoryReport = new InventoryReportVisitor();
        cart.accept(inventoryReport);
        inventoryReport.printReport();
        
        System.out.println("\n" + "=".repeat(50));
        
        // Final checkout summary
        printCheckoutSummary(priceCalculator, discountCalculator, 
                           taxCalculator, shippingCalculator);
    }
    
    private static ShoppingCart createSampleCart() {
        ShoppingCart cart = new ShoppingCart();
        
        // Add books
        cart.addItem(new Book("Java Programming", 45.99, 2, "John Smith", 
                             600, false, "Programming"));
        cart.addItem(new Book("Digital Design eBook", 29.99, 1, "Jane Doe", 
                             400, true, "Technology"));
        
        // Add electronics
        cart.addItem(new Electronics("Laptop", 999.99, 1, "Apple", 
                                   36, 3.5, true, "MacBook Pro"));
        cart.addItem(new Electronics("Wireless Headphones", 199.99, 2, "Sony", 
                                   12, 0.5, false, "WH-1000XM4"));
        
        // Add clothing
        cart.addItem(new Clothing("Designer Jacket", 299.99, 1, "Nike", 
                                 "L", "Black", "Premium", false));
        cart.addItem(new Clothing("Basic T-Shirt", 19.99, 3, "Generic", 
                                 "M", "Blue", "Cotton", true));
        
        // Add food
        cart.addItem(new Food("Organic Apples", 4.99, 2, "Fresh Farms", 
                             LocalDate.now().plusDays(7), true, 2.0, true));
        cart.addItem(new Food("Canned Beans", 2.49, 4, "Brand X", 
                             LocalDate.now().plusDays(365), false, 1.5, false));
        cart.addItem(new Food("Milk", 3.99, 1, "Dairy Co", 
                             LocalDate.now().plusDays(2), true, 1.0, false));
        
        return cart;
    }
    
    private static void printCheckoutSummary(PriceCalculatorVisitor priceCalc,
                                           DiscountCalculatorVisitor discountCalc,
                                           TaxCalculatorVisitor taxCalc,
                                           ShippingCalculatorVisitor shippingCalc) {
        System.out.println("=== CHECKOUT SUMMARY ===");
        
        double subtotal = priceCalc.getTotalPrice();
        double discounts = discountCalc.getTotalDiscount();
        double taxes = taxCalc.getTotalTax();
        double shipping = shippingCalc.getTotalShipping();
        double grandTotal = subtotal - discounts + taxes + shipping;
        
        System.out.println("Subtotal:  $" + String.format("%.2f", subtotal));
        System.out.println("Discounts: -$" + String.format("%.2f", discounts));
        System.out.println("Taxes:     $" + String.format("%.2f", taxes));
        System.out.println("Shipping:  $" + String.format("%.2f", shipping));
        System.out.println("" + "-".repeat(25));
        System.out.println("TOTAL:     $" + String.format("%.2f", grandTotal));
        System.out.println("You saved: $" + String.format("%.2f", discounts));
    }
}

/*
Key Benefits Demonstrated:

1. **Multiple Pricing Strategies**: Each visitor implements different pricing logic
2. **Easy Extension**: New discount strategies, tax rules, or shipping methods can be added
3. **Clean Separation**: Product classes remain focused on data, visitors handle operations
4. **Complex Business Logic**: Each visitor can implement sophisticated rules
5. **Maintainability**: Changes to pricing/discount logic only affect relevant visitors

Operations Implemented:
1. Price calculation with product-specific rules
2. Discount calculation based on customer type and membership
3. Tax calculation with category-specific rates
4. Shipping cost calculation with zone-based pricing
5. Inventory reporting and analytics

This example shows how the Visitor pattern excels in e-commerce scenarios where:
- Products have different pricing rules
- Multiple discount strategies exist
- Tax calculations vary by product type
- Shipping costs depend on product attributes
- Business rules change frequently
*/