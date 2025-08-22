package org.example.designPatterns.commandPattern;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Restaurant Order Management Command Pattern Example
 * 
 * This example demonstrates the Command Pattern implementation for a restaurant
 * order management system. It shows how commands can be used for order processing,
 * modification, cancellation, and kitchen workflow management.
 * 
 * Features:
 * - Order creation and modification commands
 * - Kitchen preparation commands
 * - Order cancellation with refund handling
 * - Order history and tracking
 * - Table management commands
 * - Payment processing commands
 */
public class RestaurantOrderExample {

    // Command interface
    interface Command {
        void execute();
        boolean canUndo();
        void undo();
        String getDescription();
        LocalDateTime getTimestamp();
    }

    // Base Command implementation
    abstract static class BaseCommand implements Command {
        protected LocalDateTime timestamp;
        
        public BaseCommand() {
            this.timestamp = LocalDateTime.now();
        }
        
        @Override
        public LocalDateTime getTimestamp() {
            return timestamp;
        }
        
        @Override
        public boolean canUndo() {
            return false; // Override in subclasses if undo is supported
        }
        
        @Override
        public void undo() {
            throw new UnsupportedOperationException("Undo not supported for this command");
        }
    }

    // Domain Models
    static class MenuItem {
        private String name;
        private double price;
        private String category;
        private int preparationTimeMinutes;
        
        public MenuItem(String name, double price, String category, int preparationTimeMinutes) {
            this.name = name;
            this.price = price;
            this.category = category;
            this.preparationTimeMinutes = preparationTimeMinutes;
        }
        
        // Getters
        public String getName() { return name; }
        public double getPrice() { return price; }
        public String getCategory() { return category; }
        public int getPreparationTimeMinutes() { return preparationTimeMinutes; }
        
        @Override
        public String toString() {
            return name + " ($" + String.format("%.2f", price) + ")";
        }
    }

    static class OrderItem {
        private MenuItem menuItem;
        private int quantity;
        private String specialInstructions;
        
        public OrderItem(MenuItem menuItem, int quantity, String specialInstructions) {
            this.menuItem = menuItem;
            this.quantity = quantity;
            this.specialInstructions = specialInstructions;
        }
        
        public double getTotal() {
            return menuItem.getPrice() * quantity;
        }
        
        // Getters
        public MenuItem getMenuItem() { return menuItem; }
        public int getQuantity() { return quantity; }
        public String getSpecialInstructions() { return specialInstructions; }
        
        @Override
        public String toString() {
            String result = quantity + "x " + menuItem.getName();
            if (specialInstructions != null && !specialInstructions.isEmpty()) {
                result += " (" + specialInstructions + ")";
            }
            return result;
        }
    }

    enum OrderStatus {
        PENDING, CONFIRMED, PREPARING, READY, SERVED, CANCELLED
    }

    static class Order {
        private static AtomicInteger orderCounter = new AtomicInteger(1);
        
        private int orderId;
        private int tableNumber;
        private List<OrderItem> items;
        private OrderStatus status;
        private LocalDateTime orderTime;
        private double total;
        private String customerName;
        
        public Order(int tableNumber, String customerName) {
            this.orderId = orderCounter.getAndIncrement();
            this.tableNumber = tableNumber;
            this.customerName = customerName;
            this.items = new ArrayList<>();
            this.status = OrderStatus.PENDING;
            this.orderTime = LocalDateTime.now();
            this.total = 0.0;
        }
        
        public void addItem(OrderItem item) {
            items.add(item);
            calculateTotal();
        }
        
        public void removeItem(OrderItem item) {
            items.remove(item);
            calculateTotal();
        }
        
        public void clearItems() {
            items.clear();
            calculateTotal();
        }
        
        private void calculateTotal() {
            total = items.stream().mapToDouble(OrderItem::getTotal).sum();
        }
        
        // Getters and setters
        public int getOrderId() { return orderId; }
        public int getTableNumber() { return tableNumber; }
        public List<OrderItem> getItems() { return new ArrayList<>(items); }
        public OrderStatus getStatus() { return status; }
        public void setStatus(OrderStatus status) { this.status = status; }
        public LocalDateTime getOrderTime() { return orderTime; }
        public double getTotal() { return total; }
        public String getCustomerName() { return customerName; }
        
        @Override
        public String toString() {
            return "Order #" + orderId + " (Table " + tableNumber + ", " + customerName + 
                   ") - $" + String.format("%.2f", total) + " [" + status + "]";
        }
    }

    // Receivers
    static class Kitchen {
        private Queue<Order> preparationQueue;
        private Set<Integer> preparingOrders;
        
        public Kitchen() {
            this.preparationQueue = new LinkedList<>();
            this.preparingOrders = new HashSet<>();
        }
        
        public void addToQueue(Order order) {
            preparationQueue.offer(order);
            System.out.println("Kitchen: Order #" + order.getOrderId() + " added to preparation queue");
        }
        
        public void startPreparing(int orderId) {
            preparingOrders.add(orderId);
            System.out.println("Kitchen: Started preparing order #" + orderId);
        }
        
        public void finishPreparing(int orderId) {
            preparingOrders.remove(orderId);
            System.out.println("Kitchen: Finished preparing order #" + orderId);
        }
        
        public void cancelOrder(int orderId) {
            preparationQueue.removeIf(order -> order.getOrderId() == orderId);
            preparingOrders.remove(orderId);
            System.out.println("Kitchen: Cancelled order #" + orderId);
        }
        
        public void showQueue() {
            System.out.println("Kitchen Queue: " + preparationQueue.size() + " orders waiting");
            System.out.println("Currently preparing: " + preparingOrders.size() + " orders");
        }
    }

    static class PaymentSystem {
        private Map<Integer, Double> payments;
        private Map<Integer, Double> refunds;
        
        public PaymentSystem() {
            this.payments = new HashMap<>();
            this.refunds = new HashMap<>();
        }
        
        public boolean processPayment(int orderId, double amount) {
            payments.put(orderId, amount);
            System.out.println("Payment: Processed $" + String.format("%.2f", amount) + 
                             " for order #" + orderId);
            return true;
        }
        
        public boolean processRefund(int orderId, double amount) {
            refunds.put(orderId, amount);
            System.out.println("Payment: Processed refund $" + String.format("%.2f", amount) + 
                             " for order #" + orderId);
            return true;
        }
        
        public double getTotalPayments() {
            return payments.values().stream().mapToDouble(Double::doubleValue).sum();
        }
        
        public double getTotalRefunds() {
            return refunds.values().stream().mapToDouble(Double::doubleValue).sum();
        }
    }

    static class RestaurantManager {
        private Map<Integer, Order> orders;
        private Kitchen kitchen;
        private PaymentSystem paymentSystem;
        private Map<Integer, String> tableStatus; // tableNumber -> status
        
        public RestaurantManager() {
            this.orders = new HashMap<>();
            this.kitchen = new Kitchen();
            this.paymentSystem = new PaymentSystem();
            this.tableStatus = new HashMap<>();
        }
        
        public void addOrder(Order order) {
            orders.put(order.getOrderId(), order);
        }
        
        public Order getOrder(int orderId) {
            return orders.get(orderId);
        }
        
        public Kitchen getKitchen() { return kitchen; }
        public PaymentSystem getPaymentSystem() { return paymentSystem; }
        
        public void setTableStatus(int tableNumber, String status) {
            tableStatus.put(tableNumber, status);
            System.out.println("Table " + tableNumber + " status: " + status);
        }
        
        public void showAllOrders() {
            System.out.println("\n=== All Orders ===");
            for (Order order : orders.values()) {
                System.out.println(order);
                for (OrderItem item : order.getItems()) {
                    System.out.println("  - " + item);
                }
            }
        }
        
        public void showDailySummary() {
            System.out.println("\n=== Daily Summary ===");
            System.out.println("Total Orders: " + orders.size());
            System.out.println("Total Payments: $" + 
                String.format("%.2f", paymentSystem.getTotalPayments()));
            System.out.println("Total Refunds: $" + 
                String.format("%.2f", paymentSystem.getTotalRefunds()));
            
            Map<OrderStatus, Long> statusCounts = new HashMap<>();
            for (Order order : orders.values()) {
                statusCounts.merge(order.getStatus(), 1L, Long::sum);
            }
            
            System.out.println("Orders by Status:");
            for (Map.Entry<OrderStatus, Long> entry : statusCounts.entrySet()) {
                System.out.println("  " + entry.getKey() + ": " + entry.getValue());
            }
        }
    }

    // Concrete Commands
    static class CreateOrderCommand extends BaseCommand {
        private RestaurantManager manager;
        private Order order;
        
        public CreateOrderCommand(RestaurantManager manager, Order order) {
            this.manager = manager;
            this.order = order;
        }
        
        @Override
        public void execute() {
            manager.addOrder(order);
            order.setStatus(OrderStatus.PENDING);
            System.out.println("Created: " + order);
        }
        
        @Override
        public String getDescription() {
            return "Create order #" + order.getOrderId() + " for table " + order.getTableNumber();
        }
    }

    static class AddItemCommand extends BaseCommand {
        private Order order;
        private OrderItem item;
        
        public AddItemCommand(Order order, OrderItem item) {
            this.order = order;
            this.item = item;
        }
        
        @Override
        public void execute() {
            order.addItem(item);
            System.out.println("Added to order #" + order.getOrderId() + ": " + item);
        }
        
        @Override
        public boolean canUndo() {
            return true;
        }
        
        @Override
        public void undo() {
            order.removeItem(item);
            System.out.println("Removed from order #" + order.getOrderId() + ": " + item);
        }
        
        @Override
        public String getDescription() {
            return "Add " + item + " to order #" + order.getOrderId();
        }
    }

    static class ConfirmOrderCommand extends BaseCommand {
        private RestaurantManager manager;
        private Order order;
        
        public ConfirmOrderCommand(RestaurantManager manager, Order order) {
            this.manager = manager;
            this.order = order;
        }
        
        @Override
        public void execute() {
            if (order.getStatus() == OrderStatus.PENDING) {
                order.setStatus(OrderStatus.CONFIRMED);
                manager.getKitchen().addToQueue(order);
                System.out.println("Confirmed: " + order);
            } else {
                System.out.println("Cannot confirm order #" + order.getOrderId() + 
                                 " - current status: " + order.getStatus());
            }
        }
        
        @Override
        public String getDescription() {
            return "Confirm order #" + order.getOrderId();
        }
    }

    static class StartPreparationCommand extends BaseCommand {
        private RestaurantManager manager;
        private Order order;
        
        public StartPreparationCommand(RestaurantManager manager, Order order) {
            this.manager = manager;
            this.order = order;
        }
        
        @Override
        public void execute() {
            if (order.getStatus() == OrderStatus.CONFIRMED) {
                order.setStatus(OrderStatus.PREPARING);
                manager.getKitchen().startPreparing(order.getOrderId());
                System.out.println("Started preparing: " + order);
            } else {
                System.out.println("Cannot start preparing order #" + order.getOrderId() + 
                                 " - current status: " + order.getStatus());
            }
        }
        
        @Override
        public String getDescription() {
            return "Start preparing order #" + order.getOrderId();
        }
    }

    static class CompletePreparationCommand extends BaseCommand {
        private RestaurantManager manager;
        private Order order;
        
        public CompletePreparationCommand(RestaurantManager manager, Order order) {
            this.manager = manager;
            this.order = order;
        }
        
        @Override
        public void execute() {
            if (order.getStatus() == OrderStatus.PREPARING) {
                order.setStatus(OrderStatus.READY);
                manager.getKitchen().finishPreparing(order.getOrderId());
                System.out.println("Order ready: " + order);
            } else {
                System.out.println("Cannot complete order #" + order.getOrderId() + 
                                 " - current status: " + order.getStatus());
            }
        }
        
        @Override
        public String getDescription() {
            return "Complete preparation of order #" + order.getOrderId();
        }
    }

    static class ServeOrderCommand extends BaseCommand {
        private Order order;
        
        public ServeOrderCommand(Order order) {
            this.order = order;
        }
        
        @Override
        public void execute() {
            if (order.getStatus() == OrderStatus.READY) {
                order.setStatus(OrderStatus.SERVED);
                System.out.println("Served: " + order);
            } else {
                System.out.println("Cannot serve order #" + order.getOrderId() + 
                                 " - current status: " + order.getStatus());
            }
        }
        
        @Override
        public String getDescription() {
            return "Serve order #" + order.getOrderId();
        }
    }

    static class ProcessPaymentCommand extends BaseCommand {
        private RestaurantManager manager;
        private Order order;
        
        public ProcessPaymentCommand(RestaurantManager manager, Order order) {
            this.manager = manager;
            this.order = order;
        }
        
        @Override
        public void execute() {
            if (order.getStatus() == OrderStatus.SERVED) {
                boolean success = manager.getPaymentSystem().processPayment(
                    order.getOrderId(), order.getTotal());
                if (success) {
                    System.out.println("Payment processed for: " + order);
                }
            } else {
                System.out.println("Cannot process payment for order #" + order.getOrderId() + 
                                 " - current status: " + order.getStatus());
            }
        }
        
        @Override
        public String getDescription() {
            return "Process payment for order #" + order.getOrderId() + 
                   " ($" + String.format("%.2f", order.getTotal()) + ")";
        }
    }

    static class CancelOrderCommand extends BaseCommand {
        private RestaurantManager manager;
        private Order order;
        private OrderStatus previousStatus;
        
        public CancelOrderCommand(RestaurantManager manager, Order order) {
            this.manager = manager;
            this.order = order;
            this.previousStatus = order.getStatus();
        }
        
        @Override
        public void execute() {
            if (order.getStatus() != OrderStatus.SERVED && order.getStatus() != OrderStatus.CANCELLED) {
                order.setStatus(OrderStatus.CANCELLED);
                manager.getKitchen().cancelOrder(order.getOrderId());
                
                // Process refund if payment was already made
                if (previousStatus == OrderStatus.SERVED) {
                    manager.getPaymentSystem().processRefund(order.getOrderId(), order.getTotal());
                }
                
                System.out.println("Cancelled: " + order);
            } else {
                System.out.println("Cannot cancel order #" + order.getOrderId() + 
                                 " - current status: " + order.getStatus());
            }
        }
        
        @Override
        public String getDescription() {
            return "Cancel order #" + order.getOrderId();
        }
    }

    // Macro Command for complete order workflow
    static class CompleteOrderWorkflowCommand extends BaseCommand {
        private List<Command> commands;
        private String description;
        
        public CompleteOrderWorkflowCommand(String description) {
            this.commands = new ArrayList<>();
            this.description = description;
        }
        
        public void addCommand(Command command) {
            commands.add(command);
        }
        
        @Override
        public void execute() {
            System.out.println("Executing workflow: " + description);
            for (Command command : commands) {
                command.execute();
            }
        }
        
        @Override
        public String getDescription() {
            return description + " (" + commands.size() + " steps)";
        }
    }

    // Order Management System (Invoker)
    static class OrderManagementSystem {
        private RestaurantManager manager;
        private List<Command> commandHistory;
        
        public OrderManagementSystem() {
            this.manager = new RestaurantManager();
            this.commandHistory = new ArrayList<>();
        }
        
        public void executeCommand(Command command) {
            command.execute();
            commandHistory.add(command);
        }
        
        public void undoLastCommand() {
            if (!commandHistory.isEmpty()) {
                Command lastCommand = commandHistory.get(commandHistory.size() - 1);
                if (lastCommand.canUndo()) {
                    lastCommand.undo();
                    commandHistory.remove(commandHistory.size() - 1);
                    System.out.println("Undone: " + lastCommand.getDescription());
                } else {
                    System.out.println("Cannot undo: " + lastCommand.getDescription());
                }
            } else {
                System.out.println("No commands to undo");
            }
        }
        
        public RestaurantManager getManager() {
            return manager;
        }
        
        public void showCommandHistory() {
            System.out.println("\n=== Command History ===");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
            for (int i = 0; i < commandHistory.size(); i++) {
                Command cmd = commandHistory.get(i);
                System.out.println((i + 1) + ". [" + cmd.getTimestamp().format(formatter) + 
                                 "] " + cmd.getDescription());
            }
        }
    }

    // Demonstration
    public static void main(String[] args) {
        System.out.println("=== Restaurant Order Management Command Pattern Example ===\n");
        
        // Setup system
        OrderManagementSystem orderSystem = new OrderManagementSystem();
        RestaurantManager manager = orderSystem.getManager();
        
        // Setup menu items
        MenuItem burger = new MenuItem("Cheeseburger", 12.99, "Main", 15);
        MenuItem fries = new MenuItem("French Fries", 4.99, "Side", 8);
        MenuItem soda = new MenuItem("Soft Drink", 2.99, "Beverage", 2);
        MenuItem salad = new MenuItem("Caesar Salad", 9.99, "Main", 10);
        
        System.out.println("1. Creating Orders:");
        
        // Create first order
        Order order1 = new Order(5, "John Smith");
        orderSystem.executeCommand(new CreateOrderCommand(manager, order1));
        
        // Add items to order
        orderSystem.executeCommand(new AddItemCommand(order1, 
            new OrderItem(burger, 1, "No onions")));
        orderSystem.executeCommand(new AddItemCommand(order1, 
            new OrderItem(fries, 1, "Extra crispy")));
        orderSystem.executeCommand(new AddItemCommand(order1, 
            new OrderItem(soda, 2, "")));
        
        // Create second order
        Order order2 = new Order(3, "Jane Doe");
        orderSystem.executeCommand(new CreateOrderCommand(manager, order2));
        orderSystem.executeCommand(new AddItemCommand(order2, 
            new OrderItem(salad, 1, "Dressing on the side")));
        orderSystem.executeCommand(new AddItemCommand(order2, 
            new OrderItem(soda, 1, "")));
        
        System.out.println("\n2. Order Processing Workflow:");
        
        // Process first order through complete workflow
        CompleteOrderWorkflowCommand workflow1 = new CompleteOrderWorkflowCommand(
            "Complete Order #" + order1.getOrderId() + " Workflow");
        workflow1.addCommand(new ConfirmOrderCommand(manager, order1));
        workflow1.addCommand(new StartPreparationCommand(manager, order1));
        workflow1.addCommand(new CompletePreparationCommand(manager, order1));
        workflow1.addCommand(new ServeOrderCommand(order1));
        workflow1.addCommand(new ProcessPaymentCommand(manager, order1));
        
        orderSystem.executeCommand(workflow1);
        
        System.out.println("\n3. Individual Command Processing:");
        
        // Process second order step by step
        orderSystem.executeCommand(new ConfirmOrderCommand(manager, order2));
        orderSystem.executeCommand(new StartPreparationCommand(manager, order2));
        
        // Demonstrate order modification (undo last item)
        System.out.println("\n4. Order Modification (Undo):");
        System.out.println("Before undo - Order #" + order2.getOrderId() + " total: $" + 
                         String.format("%.2f", order2.getTotal()));
        orderSystem.undoLastCommand(); // This should undo the start preparation
        
        // Add another item and continue
        orderSystem.executeCommand(new AddItemCommand(order2, 
            new OrderItem(fries, 1, "")));
        System.out.println("After adding fries - Order #" + order2.getOrderId() + " total: $" + 
                         String.format("%.2f", order2.getTotal()));
        
        // Continue processing order2
        orderSystem.executeCommand(new StartPreparationCommand(manager, order2));
        orderSystem.executeCommand(new CompletePreparationCommand(manager, order2));
        orderSystem.executeCommand(new ServeOrderCommand(order2));
        
        System.out.println("\n5. Order Cancellation:");
        
        // Create a third order and then cancel it
        Order order3 = new Order(7, "Bob Wilson");
        orderSystem.executeCommand(new CreateOrderCommand(manager, order3));
        orderSystem.executeCommand(new AddItemCommand(order3, 
            new OrderItem(burger, 2, "")));
        orderSystem.executeCommand(new ConfirmOrderCommand(manager, order3));
        
        // Cancel the order
        orderSystem.executeCommand(new CancelOrderCommand(manager, order3));
        
        System.out.println("\n6. Payment Processing:");
        orderSystem.executeCommand(new ProcessPaymentCommand(manager, order2));
        
        // Show system status
        System.out.println("\n7. System Status:");
        manager.getKitchen().showQueue();
        manager.showAllOrders();
        manager.showDailySummary();
        
        // Show command history
        orderSystem.showCommandHistory();
        
        System.out.println("\nDemonstration completed!");
    }
}