package org.example.solidPrinciples.singleResponsibility;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * E-commerce Order Processing System - Single Responsibility Principle Example
 * 
 * This example demonstrates how to decompose a complex order processing system
 * into focused, single-responsibility components.
 * 
 * Key Benefits Demonstrated:
 * - Modular testing
 * - Independent deployment
 * - Clear ownership and maintenance
 * - Easier debugging and troubleshooting
 * - Flexible system composition
 */
public class EcommerceOrderExample {
    
    /**
     * BEFORE: Monolithic Order class violating SRP
     * This class handles too many responsibilities making it difficult to maintain and test
     */
    public static class MonolithicOrder {
        private Long orderId;
        private List<OrderItem> items = new ArrayList<>();
        private String customerEmail;
        private String shippingAddress;
        private String status;
        private double totalAmount;
        private LocalDateTime orderDate;
        
        // Responsibility 1: Order management
        public void addItem(String productId, String name, double price, int quantity) {
            items.add(new OrderItem(productId, name, price, quantity));
            calculateTotal();
        }
        
        // Responsibility 2: Price calculation
        public void calculateTotal() {
            totalAmount = 0;
            for (OrderItem item : items) {
                double itemTotal = item.price * item.quantity;
                // Apply discounts
                if (item.quantity > 10) {
                    itemTotal *= 0.9; // 10% bulk discount
                }
                totalAmount += itemTotal;
            }
            // Apply tax
            totalAmount *= 1.08; // 8% tax
            // Apply shipping
            if (totalAmount < 50) {
                totalAmount += 10; // Shipping fee
            }
        }
        
        // Responsibility 3: Inventory management
        public boolean checkInventory() {
            System.out.println("Checking inventory for order items...");
            // Database queries to check stock
            for (OrderItem item : items) {
                System.out.println("Checking stock for " + item.name);
                // Simulate inventory check
                if (Math.random() < 0.1) {
                    System.out.println(item.name + " is out of stock!");
                    return false;
                }
            }
            return true;
        }
        
        public void updateInventory() {
            System.out.println("Updating inventory levels...");
            for (OrderItem item : items) {
                System.out.println("Reducing stock for " + item.name + " by " + item.quantity);
            }
        }
        
        // Responsibility 4: Payment processing
        public boolean processPayment(String cardNumber, String cvv) {
            System.out.println("Processing payment of $" + totalAmount);
            System.out.println("Charging card ending in " + cardNumber.substring(cardNumber.length() - 4));
            // Payment gateway integration
            if (cardNumber.length() != 16) {
                return false;
            }
            return true;
        }
        
        // Responsibility 5: Email notifications
        public void sendConfirmationEmail() {
            System.out.println("Sending email to " + customerEmail);
            System.out.println("Subject: Order Confirmation #" + orderId);
            System.out.println("Your order total: $" + totalAmount);
        }
        
        public void sendShippingEmail(String trackingNumber) {
            System.out.println("Sending shipping notification to " + customerEmail);
            System.out.println("Tracking number: " + trackingNumber);
        }
        
        // Responsibility 6: Order persistence
        public void saveToDatabase() {
            System.out.println("Saving order to database...");
            System.out.println("INSERT INTO orders VALUES (" + orderId + ", ...)");
        }
        
        // Responsibility 7: Invoice generation
        public String generateInvoice() {
            StringBuilder invoice = new StringBuilder();
            invoice.append("INVOICE #").append(orderId).append("\n");
            invoice.append("Date: ").append(orderDate).append("\n");
            invoice.append("Items:\n");
            for (OrderItem item : items) {
                invoice.append("- ").append(item.name).append(" x").append(item.quantity);
                invoice.append(" = $").append(item.price * item.quantity).append("\n");
            }
            invoice.append("Total: $").append(totalAmount).append("\n");
            return invoice.toString();
        }
        
        // Responsibility 8: Shipping calculation
        public double calculateShipping() {
            double weight = 0;
            for (OrderItem item : items) {
                weight += item.quantity * 0.5; // Assume 0.5kg per item
            }
            if (weight < 1) return 5;
            if (weight < 5) return 10;
            return 20;
        }
        
        // Responsibility 9: Order validation
        public boolean validateOrder() {
            if (items.isEmpty()) return false;
            if (customerEmail == null || !customerEmail.contains("@")) return false;
            if (shippingAddress == null || shippingAddress.isEmpty()) return false;
            return true;
        }
        
        static class OrderItem {
            String productId;
            String name;
            double price;
            int quantity;
            
            OrderItem(String productId, String name, double price, int quantity) {
                this.productId = productId;
                this.name = name;
                this.price = price;
                this.quantity = quantity;
            }
        }
    }
    
    /**
     * AFTER: Properly designed system following SRP
     * Each class has a single, well-defined responsibility
     */
    
    // Core domain models
    public static class Product {
        private final String id;
        private final String name;
        private final String description;
        private final double price;
        private final double weight;
        private final String category;
        
        public Product(String id, String name, String description, double price, double weight, String category) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.price = price;
            this.weight = weight;
            this.category = category;
        }
        
        public String getId() { return id; }
        public String getName() { return name; }
        public String getDescription() { return description; }
        public double getPrice() { return price; }
        public double getWeight() { return weight; }
        public String getCategory() { return category; }
    }
    
    public static class OrderItem {
        private final Product product;
        private final int quantity;
        private final double priceAtPurchase;
        
        public OrderItem(Product product, int quantity) {
            this.product = product;
            this.quantity = quantity;
            this.priceAtPurchase = product.getPrice();
        }
        
        public Product getProduct() { return product; }
        public int getQuantity() { return quantity; }
        public double getPriceAtPurchase() { return priceAtPurchase; }
        public double getSubtotal() { return priceAtPurchase * quantity; }
        public double getTotalWeight() { return product.getWeight() * quantity; }
    }
    
    public static class Order {
        private final Long id;
        private final String customerId;
        private final List<OrderItem> items;
        private final ShippingAddress shippingAddress;
        private final LocalDateTime orderDate;
        private OrderStatus status;
        
        public Order(Long id, String customerId, ShippingAddress shippingAddress) {
            this.id = id;
            this.customerId = customerId;
            this.items = new ArrayList<>();
            this.shippingAddress = shippingAddress;
            this.orderDate = LocalDateTime.now();
            this.status = OrderStatus.PENDING;
        }
        
        public void addItem(OrderItem item) {
            items.add(item);
        }
        
        public Long getId() { return id; }
        public String getCustomerId() { return customerId; }
        public List<OrderItem> getItems() { return new ArrayList<>(items); }
        public ShippingAddress getShippingAddress() { return shippingAddress; }
        public LocalDateTime getOrderDate() { return orderDate; }
        public OrderStatus getStatus() { return status; }
        public void setStatus(OrderStatus status) { this.status = status; }
        
        public enum OrderStatus {
            PENDING, VALIDATED, PAID, SHIPPED, DELIVERED, CANCELLED
        }
    }
    
    public static class ShippingAddress {
        private final String street;
        private final String city;
        private final String state;
        private final String zipCode;
        private final String country;
        
        public ShippingAddress(String street, String city, String state, String zipCode, String country) {
            this.street = street;
            this.city = city;
            this.state = state;
            this.zipCode = zipCode;
            this.country = country;
        }
        
        public String getFullAddress() {
            return String.format("%s, %s, %s %s, %s", street, city, state, zipCode, country);
        }
        
        public String getCountry() { return country; }
        public String getState() { return state; }
    }
    
    // Responsibility 1: Order validation
    public static class OrderValidator {
        private final InventoryService inventoryService;
        
        public OrderValidator(InventoryService inventoryService) {
            this.inventoryService = inventoryService;
        }
        
        public ValidationResult validate(Order order) {
            List<String> errors = new ArrayList<>();
            
            // Check if order has items
            if (order.getItems().isEmpty()) {
                errors.add("Order must contain at least one item");
            }
            
            // Validate each item
            for (OrderItem item : order.getItems()) {
                if (item.getQuantity() <= 0) {
                    errors.add("Invalid quantity for product: " + item.getProduct().getName());
                }
                
                // Check inventory
                if (!inventoryService.isAvailable(item.getProduct().getId(), item.getQuantity())) {
                    errors.add("Insufficient stock for: " + item.getProduct().getName());
                }
            }
            
            // Validate shipping address
            if (order.getShippingAddress() == null) {
                errors.add("Shipping address is required");
            }
            
            return new ValidationResult(errors.isEmpty(), errors);
        }
        
        public static class ValidationResult {
            private final boolean valid;
            private final List<String> errors;
            
            public ValidationResult(boolean valid, List<String> errors) {
                this.valid = valid;
                this.errors = errors;
            }
            
            public boolean isValid() { return valid; }
            public List<String> getErrors() { return errors; }
        }
    }
    
    // Responsibility 2: Price calculation
    public static class PricingService {
        private final DiscountCalculator discountCalculator;
        private final TaxCalculator taxCalculator;
        
        public PricingService(DiscountCalculator discountCalculator, TaxCalculator taxCalculator) {
            this.discountCalculator = discountCalculator;
            this.taxCalculator = taxCalculator;
        }
        
        public OrderPricing calculatePricing(Order order) {
            double subtotal = 0;
            double totalDiscount = 0;
            
            for (OrderItem item : order.getItems()) {
                double itemSubtotal = item.getSubtotal();
                double itemDiscount = discountCalculator.calculateDiscount(item);
                
                subtotal += itemSubtotal;
                totalDiscount += itemDiscount;
            }
            
            double discountedTotal = subtotal - totalDiscount;
            double tax = taxCalculator.calculateTax(discountedTotal, order.getShippingAddress());
            double total = discountedTotal + tax;
            
            return new OrderPricing(subtotal, totalDiscount, tax, total);
        }
        
        public static class OrderPricing {
            private final double subtotal;
            private final double discount;
            private final double tax;
            private final double total;
            
            public OrderPricing(double subtotal, double discount, double tax, double total) {
                this.subtotal = subtotal;
                this.discount = discount;
                this.tax = tax;
                this.total = total;
            }
            
            public double getSubtotal() { return subtotal; }
            public double getDiscount() { return discount; }
            public double getTax() { return tax; }
            public double getTotal() { return total; }
        }
    }
    
    // Responsibility 3: Discount calculation
    public static class DiscountCalculator {
        private final Map<String, DiscountRule> discountRules;
        
        public DiscountCalculator() {
            this.discountRules = new HashMap<>();
            initializeRules();
        }
        
        private void initializeRules() {
            // Bulk discount
            discountRules.put("BULK", item -> {
                if (item.getQuantity() >= 10) {
                    return item.getSubtotal() * 0.1; // 10% off
                }
                return 0;
            });
            
            // Category discount
            discountRules.put("ELECTRONICS", item -> {
                if ("Electronics".equals(item.getProduct().getCategory())) {
                    return item.getSubtotal() * 0.05; // 5% off electronics
                }
                return 0;
            });
        }
        
        public double calculateDiscount(OrderItem item) {
            double totalDiscount = 0;
            for (DiscountRule rule : discountRules.values()) {
                totalDiscount += rule.calculate(item);
            }
            return Math.min(totalDiscount, item.getSubtotal() * 0.5); // Max 50% discount
        }
        
        private interface DiscountRule {
            double calculate(OrderItem item);
        }
    }
    
    // Responsibility 4: Tax calculation
    public static class TaxCalculator {
        private final Map<String, Double> stateTaxRates;
        
        public TaxCalculator() {
            this.stateTaxRates = new HashMap<>();
            initializeTaxRates();
        }
        
        private void initializeTaxRates() {
            stateTaxRates.put("CA", 0.0725);
            stateTaxRates.put("NY", 0.08);
            stateTaxRates.put("TX", 0.0625);
            stateTaxRates.put("FL", 0.06);
            // Default rate for other states
            stateTaxRates.put("DEFAULT", 0.05);
        }
        
        public double calculateTax(double amount, ShippingAddress address) {
            Double rate = stateTaxRates.getOrDefault(address.getState(), 
                                                     stateTaxRates.get("DEFAULT"));
            return amount * rate;
        }
    }
    
    // Responsibility 5: Inventory management
    public static class InventoryService {
        private final Map<String, Integer> inventory;
        
        public InventoryService() {
            this.inventory = new HashMap<>();
            initializeInventory();
        }
        
        private void initializeInventory() {
            inventory.put("PROD001", 100);
            inventory.put("PROD002", 50);
            inventory.put("PROD003", 200);
        }
        
        public boolean isAvailable(String productId, int quantity) {
            Integer available = inventory.get(productId);
            return available != null && available >= quantity;
        }
        
        public boolean reserve(String productId, int quantity) {
            if (!isAvailable(productId, quantity)) {
                return false;
            }
            inventory.put(productId, inventory.get(productId) - quantity);
            System.out.println("✓ Reserved " + quantity + " units of " + productId);
            return true;
        }
        
        public void release(String productId, int quantity) {
            inventory.put(productId, inventory.getOrDefault(productId, 0) + quantity);
            System.out.println("✓ Released " + quantity + " units of " + productId);
        }
        
        public int getAvailableQuantity(String productId) {
            return inventory.getOrDefault(productId, 0);
        }
    }
    
    // Responsibility 6: Shipping calculation
    public static class ShippingCalculator {
        private final Map<String, ShippingMethod> shippingMethods;
        
        public ShippingCalculator() {
            this.shippingMethods = new HashMap<>();
            initializeShippingMethods();
        }
        
        private void initializeShippingMethods() {
            shippingMethods.put("STANDARD", new ShippingMethod("Standard", 5.0, 0.5, 7));
            shippingMethods.put("EXPRESS", new ShippingMethod("Express", 15.0, 1.0, 2));
            shippingMethods.put("OVERNIGHT", new ShippingMethod("Overnight", 30.0, 2.0, 1));
        }
        
        public ShippingQuote calculateShipping(Order order, String methodName) {
            ShippingMethod method = shippingMethods.get(methodName);
            if (method == null) {
                method = shippingMethods.get("STANDARD");
            }
            
            double totalWeight = order.getItems().stream()
                .mapToDouble(OrderItem::getTotalWeight)
                .sum();
            
            double cost = method.getBaseCost() + (totalWeight * method.getPerKgRate());
            
            // Free shipping for orders over $100
            PricingService pricingService = new PricingService(
                new DiscountCalculator(), new TaxCalculator());
            PricingService.OrderPricing pricing = pricingService.calculatePricing(order);
            
            if (pricing.getSubtotal() > 100) {
                cost = 0;
            }
            
            return new ShippingQuote(method.getName(), cost, method.getEstimatedDays());
        }
        
        public static class ShippingMethod {
            private final String name;
            private final double baseCost;
            private final double perKgRate;
            private final int estimatedDays;
            
            public ShippingMethod(String name, double baseCost, double perKgRate, int estimatedDays) {
                this.name = name;
                this.baseCost = baseCost;
                this.perKgRate = perKgRate;
                this.estimatedDays = estimatedDays;
            }
            
            public String getName() { return name; }
            public double getBaseCost() { return baseCost; }
            public double getPerKgRate() { return perKgRate; }
            public int getEstimatedDays() { return estimatedDays; }
        }
        
        public static class ShippingQuote {
            private final String method;
            private final double cost;
            private final int estimatedDays;
            
            public ShippingQuote(String method, double cost, int estimatedDays) {
                this.method = method;
                this.cost = cost;
                this.estimatedDays = estimatedDays;
            }
            
            public String getMethod() { return method; }
            public double getCost() { return cost; }
            public int getEstimatedDays() { return estimatedDays; }
        }
    }
    
    // Responsibility 7: Payment processing
    public static class PaymentProcessor {
        public PaymentResult processPayment(Order order, PaymentMethod paymentMethod, double amount) {
            System.out.println("💳 Processing payment of $" + String.format("%.2f", amount));
            
            boolean success = paymentMethod.charge(amount);
            String transactionId = generateTransactionId();
            
            if (success) {
                System.out.println("✓ Payment successful. Transaction ID: " + transactionId);
                return new PaymentResult(true, transactionId, "Payment processed successfully");
            } else {
                System.out.println("✗ Payment failed");
                return new PaymentResult(false, null, "Payment declined");
            }
        }
        
        private String generateTransactionId() {
            return "TXN" + System.currentTimeMillis();
        }
        
        public interface PaymentMethod {
            boolean charge(double amount);
            String getMethodType();
        }
        
        public static class CreditCardPayment implements PaymentMethod {
            private final String cardNumber;
            private final String cvv;
            private final String expiryDate;
            
            public CreditCardPayment(String cardNumber, String cvv, String expiryDate) {
                this.cardNumber = cardNumber;
                this.cvv = cvv;
                this.expiryDate = expiryDate;
            }
            
            @Override
            public boolean charge(double amount) {
                System.out.println("Charging credit card ending in " + 
                                 cardNumber.substring(cardNumber.length() - 4));
                return cardNumber.length() == 16 && cvv.length() == 3;
            }
            
            @Override
            public String getMethodType() {
                return "Credit Card";
            }
        }
        
        public static class PayPalPayment implements PaymentMethod {
            private final String email;
            private final String password;
            
            public PayPalPayment(String email, String password) {
                this.email = email;
                this.password = password;
            }
            
            @Override
            public boolean charge(double amount) {
                System.out.println("Processing PayPal payment for " + email);
                return email.contains("@") && !password.isEmpty();
            }
            
            @Override
            public String getMethodType() {
                return "PayPal";
            }
        }
        
        public static class PaymentResult {
            private final boolean success;
            private final String transactionId;
            private final String message;
            
            public PaymentResult(boolean success, String transactionId, String message) {
                this.success = success;
                this.transactionId = transactionId;
                this.message = message;
            }
            
            public boolean isSuccess() { return success; }
            public String getTransactionId() { return transactionId; }
            public String getMessage() { return message; }
        }
    }
    
    // Responsibility 8: Order persistence
    public static class OrderRepository {
        private final Map<Long, Order> database = new HashMap<>();
        private final AtomicLong idGenerator = new AtomicLong(1000);
        
        public Order save(Order order) {
            if (order.getId() == null) {
                Order newOrder = new Order(idGenerator.incrementAndGet(), 
                                          order.getCustomerId(), 
                                          order.getShippingAddress());
                for (OrderItem item : order.getItems()) {
                    newOrder.addItem(item);
                }
                newOrder.setStatus(order.getStatus());
                database.put(newOrder.getId(), newOrder);
                System.out.println("✓ Order saved with ID: " + newOrder.getId());
                return newOrder;
            } else {
                database.put(order.getId(), order);
                System.out.println("✓ Order updated: " + order.getId());
                return order;
            }
        }
        
        public Optional<Order> findById(Long id) {
            return Optional.ofNullable(database.get(id));
        }
        
        public List<Order> findByCustomerId(String customerId) {
            return database.values().stream()
                .filter(order -> order.getCustomerId().equals(customerId))
                .collect(ArrayList::new, (list, order) -> list.add(order), ArrayList::addAll);
        }
        
        public List<Order> findByStatus(Order.OrderStatus status) {
            return database.values().stream()
                .filter(order -> order.getStatus() == status)
                .collect(ArrayList::new, (list, order) -> list.add(order), ArrayList::addAll);
        }
    }
    
    // Responsibility 9: Email notifications
    public static class OrderNotificationService {
        private final EmailSender emailSender;
        private final InvoiceGenerator invoiceGenerator;
        
        public OrderNotificationService(EmailSender emailSender, InvoiceGenerator invoiceGenerator) {
            this.emailSender = emailSender;
            this.invoiceGenerator = invoiceGenerator;
        }
        
        public void sendOrderConfirmation(Order order, String customerEmail) {
            String subject = "Order Confirmation #" + order.getId();
            String body = buildOrderConfirmationBody(order);
            String invoice = invoiceGenerator.generate(order);
            
            emailSender.send(customerEmail, subject, body + "\n\n" + invoice);
        }
        
        public void sendShippingNotification(Order order, String customerEmail, String trackingNumber) {
            String subject = "Your Order #" + order.getId() + " Has Shipped!";
            String body = buildShippingNotificationBody(order, trackingNumber);
            
            emailSender.send(customerEmail, subject, body);
        }
        
        private String buildOrderConfirmationBody(Order order) {
            StringBuilder body = new StringBuilder();
            body.append("Thank you for your order!\n\n");
            body.append("Order ID: ").append(order.getId()).append("\n");
            body.append("Order Date: ").append(order.getOrderDate()
                .format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm"))).append("\n");
            body.append("Shipping to: ").append(order.getShippingAddress().getFullAddress()).append("\n");
            return body.toString();
        }
        
        private String buildShippingNotificationBody(Order order, String trackingNumber) {
            StringBuilder body = new StringBuilder();
            body.append("Good news! Your order has shipped.\n\n");
            body.append("Order ID: ").append(order.getId()).append("\n");
            body.append("Tracking Number: ").append(trackingNumber).append("\n");
            body.append("Track your package: https://tracking.example.com/").append(trackingNumber);
            return body.toString();
        }
        
        public static class EmailSender {
            public void send(String to, String subject, String body) {
                System.out.println("\n📧 Sending Email");
                System.out.println("To: " + to);
                System.out.println("Subject: " + subject);
                System.out.println("Body:\n" + body);
                System.out.println("✓ Email sent successfully\n");
            }
        }
    }
    
    // Responsibility 10: Invoice generation
    public static class InvoiceGenerator {
        private final PricingService pricingService;
        private final ShippingCalculator shippingCalculator;
        
        public InvoiceGenerator(PricingService pricingService, ShippingCalculator shippingCalculator) {
            this.pricingService = pricingService;
            this.shippingCalculator = shippingCalculator;
        }
        
        public String generate(Order order) {
            PricingService.OrderPricing pricing = pricingService.calculatePricing(order);
            ShippingCalculator.ShippingQuote shipping = shippingCalculator.calculateShipping(order, "STANDARD");
            
            StringBuilder invoice = new StringBuilder();
            invoice.append("┌─────────────────────────────────────────────┐\n");
            invoice.append("│                INVOICE                      │\n");
            invoice.append("├─────────────────────────────────────────────┤\n");
            invoice.append(String.format("│ Invoice #: %-33s│\n", "INV-" + order.getId()));
            invoice.append(String.format("│ Date: %-38s│\n", 
                order.getOrderDate().format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))));
            invoice.append("├─────────────────────────────────────────────┤\n");
            invoice.append("│ Items:                                      │\n");
            
            for (OrderItem item : order.getItems()) {
                String itemLine = String.format("%s x%d @ $%.2f", 
                    item.getProduct().getName(), 
                    item.getQuantity(), 
                    item.getPriceAtPurchase());
                invoice.append(String.format("│  %-43s│\n", itemLine));
            }
            
            invoice.append("├─────────────────────────────────────────────┤\n");
            invoice.append(String.format("│ Subtotal:    $%-30.2f│\n", pricing.getSubtotal()));
            if (pricing.getDiscount() > 0) {
                invoice.append(String.format("│ Discount:   -$%-30.2f│\n", pricing.getDiscount()));
            }
            invoice.append(String.format("│ Tax:         $%-30.2f│\n", pricing.getTax()));
            invoice.append(String.format("│ Shipping:    $%-30.2f│\n", shipping.getCost()));
            invoice.append("├─────────────────────────────────────────────┤\n");
            invoice.append(String.format("│ TOTAL:       $%-30.2f│\n", 
                pricing.getTotal() + shipping.getCost()));
            invoice.append("└─────────────────────────────────────────────┘\n");
            
            return invoice.toString();
        }
    }
    
    /**
     * Order Processing Orchestrator - Coordinates all services
     */
    public static class OrderProcessor {
        private final OrderValidator validator;
        private final InventoryService inventoryService;
        private final PricingService pricingService;
        private final PaymentProcessor paymentProcessor;
        private final OrderRepository orderRepository;
        private final OrderNotificationService notificationService;
        private final ShippingCalculator shippingCalculator;
        
        public OrderProcessor(OrderValidator validator,
                            InventoryService inventoryService,
                            PricingService pricingService,
                            PaymentProcessor paymentProcessor,
                            OrderRepository orderRepository,
                            OrderNotificationService notificationService,
                            ShippingCalculator shippingCalculator) {
            this.validator = validator;
            this.inventoryService = inventoryService;
            this.pricingService = pricingService;
            this.paymentProcessor = paymentProcessor;
            this.orderRepository = orderRepository;
            this.notificationService = notificationService;
            this.shippingCalculator = shippingCalculator;
        }
        
        public ProcessingResult processOrder(Order order, PaymentProcessor.PaymentMethod paymentMethod, 
                                            String customerEmail) {
            System.out.println("\n🛒 Processing Order...\n");
            
            // Step 1: Validate order
            OrderValidator.ValidationResult validation = validator.validate(order);
            if (!validation.isValid()) {
                return new ProcessingResult(false, "Order validation failed: " + validation.getErrors());
            }
            System.out.println("✓ Order validated");
            
            // Step 2: Reserve inventory
            for (OrderItem item : order.getItems()) {
                if (!inventoryService.reserve(item.getProduct().getId(), item.getQuantity())) {
                    return new ProcessingResult(false, "Failed to reserve inventory");
                }
            }
            
            // Step 3: Calculate pricing
            PricingService.OrderPricing pricing = pricingService.calculatePricing(order);
            ShippingCalculator.ShippingQuote shipping = shippingCalculator.calculateShipping(order, "STANDARD");
            double totalAmount = pricing.getTotal() + shipping.getCost();
            
            // Step 4: Process payment
            PaymentProcessor.PaymentResult paymentResult = paymentProcessor.processPayment(
                order, paymentMethod, totalAmount);
            
            if (!paymentResult.isSuccess()) {
                // Release inventory if payment fails
                for (OrderItem item : order.getItems()) {
                    inventoryService.release(item.getProduct().getId(), item.getQuantity());
                }
                return new ProcessingResult(false, "Payment failed: " + paymentResult.getMessage());
            }
            
            // Step 5: Update order status and save
            order.setStatus(Order.OrderStatus.PAID);
            Order savedOrder = orderRepository.save(order);
            
            // Step 6: Send confirmation email
            notificationService.sendOrderConfirmation(savedOrder, customerEmail);
            
            return new ProcessingResult(true, "Order processed successfully. Order ID: " + savedOrder.getId());
        }
        
        public static class ProcessingResult {
            private final boolean success;
            private final String message;
            
            public ProcessingResult(boolean success, String message) {
                this.success = success;
                this.message = message;
            }
            
            public boolean isSuccess() { return success; }
            public String getMessage() { return message; }
        }
    }
    
    /**
     * Demonstration of the E-commerce Order System following SRP
     */
    public static void main(String[] args) {
        System.out.println("=== E-commerce Order Processing - SRP Demo ===\n");
        
        // Initialize all services
        InventoryService inventoryService = new InventoryService();
        OrderValidator validator = new OrderValidator(inventoryService);
        DiscountCalculator discountCalculator = new DiscountCalculator();
        TaxCalculator taxCalculator = new TaxCalculator();
        PricingService pricingService = new PricingService(discountCalculator, taxCalculator);
        PaymentProcessor paymentProcessor = new PaymentProcessor();
        OrderRepository orderRepository = new OrderRepository();
        ShippingCalculator shippingCalculator = new ShippingCalculator();
        InvoiceGenerator invoiceGenerator = new InvoiceGenerator(pricingService, shippingCalculator);
        OrderNotificationService.EmailSender emailSender = new OrderNotificationService.EmailSender();
        OrderNotificationService notificationService = new OrderNotificationService(emailSender, invoiceGenerator);
        
        OrderProcessor orderProcessor = new OrderProcessor(
            validator, inventoryService, pricingService, paymentProcessor,
            orderRepository, notificationService, shippingCalculator
        );
        
        // Create products
        Product laptop = new Product("PROD001", "Gaming Laptop", "High-performance gaming laptop", 1499.99, 2.5, "Electronics");
        Product mouse = new Product("PROD002", "Wireless Mouse", "Ergonomic wireless mouse", 29.99, 0.1, "Electronics");
        Product keyboard = new Product("PROD003", "Mechanical Keyboard", "RGB mechanical keyboard", 89.99, 0.8, "Electronics");
        
        // Create shipping address
        ShippingAddress address = new ShippingAddress(
            "123 Main St", "San Francisco", "CA", "94105", "USA"
        );
        
        // Create order
        Order order = new Order(null, "CUST123", address);
        order.addItem(new OrderItem(laptop, 1));
        order.addItem(new OrderItem(mouse, 2));
        order.addItem(new OrderItem(keyboard, 1));
        
        // Create payment method
        PaymentProcessor.PaymentMethod payment = new PaymentProcessor.CreditCardPayment(
            "1234567812345678", "123", "12/25"
        );
        
        // Process the order
        OrderProcessor.ProcessingResult result = orderProcessor.processOrder(
            order, payment, "customer@example.com"
        );
        
        System.out.println("\n" + (result.isSuccess() ? "✅" : "❌") + " " + result.getMessage());
        
        System.out.println("\n\n📊 BENEFITS OF SRP IN THIS DESIGN:");
        System.out.println("════════════════════════════════════");
        System.out.println("✓ Each service has a single, clear responsibility");
        System.out.println("✓ Easy to test individual components in isolation");
        System.out.println("✓ Can change tax rates without touching payment logic");
        System.out.println("✓ Can add new discount rules without modifying order validation");
        System.out.println("✓ Email templates can be updated independently");
        System.out.println("✓ Different teams can own different services");
        System.out.println("✓ Services can be deployed and scaled independently");
        System.out.println("✓ Clear boundaries make the system easier to understand");
        
        System.out.println("\n=== Demo Complete ===");
    }
}