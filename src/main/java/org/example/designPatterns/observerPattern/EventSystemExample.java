package org.example.designPatterns.observerPattern;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Event System Observer Pattern Example
 * 
 * This example demonstrates a sophisticated event-driven system using the Observer pattern.
 * It shows how different types of events can be published and consumed by various listeners,
 * similar to modern event systems in microservices, GUI frameworks, and game engines.
 * 
 * Key Features:
 * - Generic event system supporting any event type
 * - Asynchronous and synchronous event processing
 * - Event filtering and priority handling
 * - Metrics and monitoring
 * - Thread-safe operations
 * - Event replay and history
 * 
 * Components:
 * - Event: Base interface for all events
 * - EventBus: Central event distribution system
 * - EventListener: Generic listener interface
 * - Various concrete events and listeners
 */

// Base event interface
interface Event {
    String getEventId();
    LocalDateTime getTimestamp();
    String getEventType();
    Map<String, Object> getMetadata();
}

// Abstract base event class
abstract class BaseEvent implements Event {
    private final String eventId;
    private final LocalDateTime timestamp;
    private final Map<String, Object> metadata;
    
    public BaseEvent() {
        this.eventId = UUID.randomUUID().toString();
        this.timestamp = LocalDateTime.now();
        this.metadata = new HashMap<>();
    }
    
    @Override
    public String getEventId() {
        return eventId;
    }
    
    @Override
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    @Override
    public Map<String, Object> getMetadata() {
        return new HashMap<>(metadata);
    }
    
    public void addMetadata(String key, Object value) {
        metadata.put(key, value);
    }
    
    @Override
    public String toString() {
        return String.format("%s[id=%s, time=%s]", 
            getEventType(), 
            eventId.substring(0, 8), 
            timestamp.format(DateTimeFormatter.ofPattern("HH:mm:ss.SSS")));
    }
}

// Concrete event types
class UserLoginEvent extends BaseEvent {
    private String username;
    private String ipAddress;
    private boolean successful;
    
    public UserLoginEvent(String username, String ipAddress, boolean successful) {
        super();
        this.username = username;
        this.ipAddress = ipAddress;
        this.successful = successful;
        
        addMetadata("username", username);
        addMetadata("ipAddress", ipAddress);
        addMetadata("successful", successful);
    }
    
    @Override
    public String getEventType() {
        return "USER_LOGIN";
    }
    
    // Getters
    public String getUsername() { return username; }
    public String getIpAddress() { return ipAddress; }
    public boolean isSuccessful() { return successful; }
}

class OrderPlacedEvent extends BaseEvent {
    private String orderId;
    private String customerId;
    private double amount;
    private String product;
    
    public OrderPlacedEvent(String orderId, String customerId, double amount, String product) {
        super();
        this.orderId = orderId;
        this.customerId = customerId;
        this.amount = amount;
        this.product = product;
        
        addMetadata("orderId", orderId);
        addMetadata("customerId", customerId);
        addMetadata("amount", amount);
        addMetadata("product", product);
    }
    
    @Override
    public String getEventType() {
        return "ORDER_PLACED";
    }
    
    // Getters
    public String getOrderId() { return orderId; }
    public String getCustomerId() { return customerId; }
    public double getAmount() { return amount; }
    public String getProduct() { return product; }
}

class SystemErrorEvent extends BaseEvent {
    private String errorCode;
    private String errorMessage;
    private String component;
    private Exception exception;
    
    public SystemErrorEvent(String errorCode, String errorMessage, String component, Exception exception) {
        super();
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.component = component;
        this.exception = exception;
        
        addMetadata("errorCode", errorCode);
        addMetadata("errorMessage", errorMessage);
        addMetadata("component", component);
        addMetadata("severity", "HIGH");
    }
    
    @Override
    public String getEventType() {
        return "SYSTEM_ERROR";
    }
    
    // Getters
    public String getErrorCode() { return errorCode; }
    public String getErrorMessage() { return errorMessage; }
    public String getComponent() { return component; }
    public Exception getException() { return exception; }
}

class NotificationEvent extends BaseEvent {
    private String recipient;
    private String message;
    private String channel; // email, sms, push
    private String priority;
    
    public NotificationEvent(String recipient, String message, String channel, String priority) {
        super();
        this.recipient = recipient;
        this.message = message;
        this.channel = channel;
        this.priority = priority;
        
        addMetadata("recipient", recipient);
        addMetadata("message", message);
        addMetadata("channel", channel);
        addMetadata("priority", priority);
    }
    
    @Override
    public String getEventType() {
        return "NOTIFICATION";
    }
    
    // Getters
    public String getRecipient() { return recipient; }
    public String getMessage() { return message; }
    public String getChannel() { return channel; }
    public String getPriority() { return priority; }
}

// Event listener interface
interface EventListener<T extends Event> {
    void onEvent(T event);
    Class<T> getEventType();
    String getListenerName();
    int getPriority(); // Higher number = higher priority
    boolean isAsync(); // Should this listener be called asynchronously?
}

// Abstract base listener
abstract class BaseEventListener<T extends Event> implements EventListener<T> {
    private final String listenerName;
    private final Class<T> eventType;
    private final int priority;
    private final boolean async;
    
    public BaseEventListener(String listenerName, Class<T> eventType, int priority, boolean async) {
        this.listenerName = listenerName;
        this.eventType = eventType;
        this.priority = priority;
        this.async = async;
    }
    
    @Override
    public Class<T> getEventType() {
        return eventType;
    }
    
    @Override
    public String getListenerName() {
        return listenerName;
    }
    
    @Override
    public int getPriority() {
        return priority;
    }
    
    @Override
    public boolean isAsync() {
        return async;
    }
}

// Event Bus - The main subject in our Observer pattern
class EventBus {
    private final Map<Class<? extends Event>, List<EventListener<? extends Event>>> listeners;
    private final List<Event> eventHistory;
    private final Map<String, Integer> eventCounts;
    private final ExecutorService asyncExecutor;
    private boolean isRecordingHistory;
    
    public EventBus() {
        this.listeners = new ConcurrentHashMap<>();
        this.eventHistory = new CopyOnWriteArrayList<>();
        this.eventCounts = new ConcurrentHashMap<>();
        this.asyncExecutor = Executors.newFixedThreadPool(5);
        this.isRecordingHistory = true;
    }
    
    // Subscribe a listener to events of a specific type
    public <T extends Event> void subscribe(EventListener<T> listener) {
        Class<T> eventType = listener.getEventType();
        listeners.computeIfAbsent(eventType, k -> new CopyOnWriteArrayList<>()).add(listener);
        
        // Sort by priority (higher priority first)
        listeners.get(eventType).sort((l1, l2) -> Integer.compare(l2.getPriority(), l1.getPriority()));
        
        System.out.println("📡 Subscribed: " + listener.getListenerName() + 
            " to " + eventType.getSimpleName() + " events (Priority: " + listener.getPriority() + 
            ", Async: " + listener.isAsync() + ")");
    }
    
    // Unsubscribe a listener
    public <T extends Event> void unsubscribe(EventListener<T> listener) {
        Class<T> eventType = listener.getEventType();
        List<EventListener<? extends Event>> eventListeners = listeners.get(eventType);
        if (eventListeners != null) {
            eventListeners.remove(listener);
            System.out.println("📡 Unsubscribed: " + listener.getListenerName() + 
                " from " + eventType.getSimpleName() + " events");
        }
    }
    
    // Publish an event to all interested listeners
    public void publish(Event event) {
        // Record event in history
        if (isRecordingHistory) {
            eventHistory.add(event);
            eventCounts.merge(event.getEventType(), 1, Integer::sum);
        }
        
        System.out.println("📢 Publishing: " + event);
        
        // Get listeners for this event type
        List<EventListener<? extends Event>> eventListeners = listeners.get(event.getClass());
        if (eventListeners != null) {
            for (EventListener<? extends Event> listener : eventListeners) {
                deliverEvent(event, listener);
            }
        }
        
        // Also notify listeners registered for parent types
        notifyParentTypeListeners(event);
    }
    
    @SuppressWarnings("unchecked")
    private void deliverEvent(Event event, EventListener<? extends Event> listener) {
        if (listener.isAsync()) {
            // Asynchronous delivery
            CompletableFuture.runAsync(() -> {
                try {
                    ((EventListener<Event>) listener).onEvent(event);
                } catch (Exception e) {
                    System.err.println("❌ Error in async listener " + listener.getListenerName() + ": " + e.getMessage());
                }
            }, asyncExecutor);
        } else {
            // Synchronous delivery
            try {
                ((EventListener<Event>) listener).onEvent(event);
            } catch (Exception e) {
                System.err.println("❌ Error in sync listener " + listener.getListenerName() + ": " + e.getMessage());
            }
        }
    }
    
    @SuppressWarnings("unchecked")
    private void notifyParentTypeListeners(Event event) {
        // Notify listeners registered for Event.class (catch-all listeners)
        List<EventListener<? extends Event>> catchAllListeners = listeners.get(Event.class);
        if (catchAllListeners != null) {
            for (EventListener<? extends Event> listener : catchAllListeners) {
                deliverEvent(event, listener);
            }
        }
    }
    
    // Replay events to a specific listener
    public <T extends Event> void replayEvents(EventListener<T> listener, int maxEvents) {
        Class<T> eventType = listener.getEventType();
        List<Event> matchingEvents = eventHistory.stream()
            .filter(event -> eventType.isAssignableFrom(event.getClass()))
            .collect(ArrayList::new, (list, event) -> {
                if (list.size() < maxEvents) list.add(event);
            }, ArrayList::addAll);
            
        System.out.println("🔄 Replaying " + matchingEvents.size() + " events to " + listener.getListenerName());
        for (Event event : matchingEvents) {
            deliverEvent(event, listener);
        }
    }
    
    // Get statistics
    public Map<String, Integer> getEventCounts() {
        return new HashMap<>(eventCounts);
    }
    
    public int getListenerCount() {
        return listeners.values().stream().mapToInt(List::size).sum();
    }
    
    public void clearHistory() {
        eventHistory.clear();
        eventCounts.clear();
        System.out.println("🗑️ Event history cleared");
    }
    
    public void setRecordingHistory(boolean recording) {
        this.isRecordingHistory = recording;
        System.out.println("📼 Event recording " + (recording ? "enabled" : "disabled"));
    }
    
    public void shutdown() {
        asyncExecutor.shutdown();
        System.out.println("⏹️ EventBus shutdown");
    }
}

// Concrete event listeners
class SecurityAuditListener extends BaseEventListener<UserLoginEvent> {
    private final Set<String> suspiciousIPs;
    
    public SecurityAuditListener() {
        super("SecurityAudit", UserLoginEvent.class, 100, false); // High priority, sync
        this.suspiciousIPs = new HashSet<>();
    }
    
    @Override
    public void onEvent(UserLoginEvent event) {
        System.out.println("🔒 SECURITY: " + event.getUsername() + 
            " login " + (event.isSuccessful() ? "successful" : "FAILED") + 
            " from " + event.getIpAddress());
            
        if (!event.isSuccessful()) {
            suspiciousIPs.add(event.getIpAddress());
            if (Collections.frequency(Arrays.asList(suspiciousIPs.toArray()), event.getIpAddress()) > 3) {
                System.out.println("🚨 SECURITY ALERT: Multiple failed logins from " + event.getIpAddress());
            }
        }
    }
}

class OrderProcessingListener extends BaseEventListener<OrderPlacedEvent> {
    public OrderProcessingListener() {
        super("OrderProcessing", OrderPlacedEvent.class, 90, true); // High priority, async
    }
    
    @Override
    public void onEvent(OrderPlacedEvent event) {
        // Simulate processing time
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        System.out.println("📦 PROCESSING: Order " + event.getOrderId() + 
            " for customer " + event.getCustomerId() + 
            " ($" + String.format("%.2f", event.getAmount()) + ")");
    }
}

class NotificationListener extends BaseEventListener<Event> {
    public NotificationListener() {
        super("NotificationService", Event.class, 50, true); // Medium priority, async, catch-all
    }
    
    @Override
    public void onEvent(Event event) {
        // Create notifications based on different event types
        if (event instanceof OrderPlacedEvent) {
            OrderPlacedEvent orderEvent = (OrderPlacedEvent) event;
            System.out.println("📧 NOTIFICATION: Sending order confirmation to customer " + 
                orderEvent.getCustomerId());
        } else if (event instanceof SystemErrorEvent) {
            SystemErrorEvent errorEvent = (SystemErrorEvent) event;
            System.out.println("🚨 NOTIFICATION: Alerting admin about error in " + 
                errorEvent.getComponent());
        }
    }
}

class MetricsCollector extends BaseEventListener<Event> {
    private final Map<String, Integer> metrics;
    
    public MetricsCollector() {
        super("MetricsCollector", Event.class, 10, true); // Low priority, async, catch-all
        this.metrics = new ConcurrentHashMap<>();
    }
    
    @Override
    public void onEvent(Event event) {
        // Collect metrics for all events
        metrics.merge(event.getEventType(), 1, Integer::sum);
        metrics.merge("TOTAL_EVENTS", 1, Integer::sum);
        
        if (metrics.get("TOTAL_EVENTS") % 5 == 0) {
            System.out.println("📊 METRICS: " + metrics);
        }
    }
    
    public Map<String, Integer> getMetrics() {
        return new HashMap<>(metrics);
    }
}

class ErrorHandlingListener extends BaseEventListener<SystemErrorEvent> {
    public ErrorHandlingListener() {
        super("ErrorHandler", SystemErrorEvent.class, 95, false); // Very high priority, sync
    }
    
    @Override
    public void onEvent(SystemErrorEvent event) {
        System.out.println("🔧 ERROR HANDLER: " + event.getErrorCode() + 
            " in " + event.getComponent() + " - " + event.getErrorMessage());
            
        // Simulate error handling logic
        if ("CRITICAL".equals(event.getMetadata().get("severity"))) {
            System.out.println("🚨 CRITICAL ERROR: Initiating emergency procedures");
        }
    }
}

class DatabaseListener extends BaseEventListener<Event> {
    private int recordsInserted = 0;
    
    public DatabaseListener() {
        super("DatabasePersistence", Event.class, 20, true); // Low priority, async, catch-all
    }
    
    @Override
    public void onEvent(Event event) {
        // Simulate database persistence
        recordsInserted++;
        System.out.println("💾 DATABASE: Persisted " + event.getEventType() + 
            " event (Total records: " + recordsInserted + ")");
    }
    
    public int getRecordsInserted() {
        return recordsInserted;
    }
}

// Event generator for testing
class EventGenerator {
    private final EventBus eventBus;
    private final Random random;
    
    public EventGenerator(EventBus eventBus) {
        this.eventBus = eventBus;
        this.random = new Random();
    }
    
    public void generateUserActivity() {
        String[] users = {"alice", "bob", "charlie", "diana", "eve"};
        String[] ips = {"192.168.1.100", "10.0.0.50", "172.16.0.25", "203.0.113.1"};
        
        for (int i = 0; i < 8; i++) {
            String user = users[random.nextInt(users.length)];
            String ip = ips[random.nextInt(ips.length)];
            boolean successful = random.nextFloat() > 0.2f; // 80% success rate
            
            eventBus.publish(new UserLoginEvent(user, ip, successful));
            
            try {
                Thread.sleep(50); // Small delay between events
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
    
    public void generateOrderActivity() {
        String[] customers = {"CUST001", "CUST002", "CUST003", "CUST004"};
        String[] products = {"Laptop", "Phone", "Tablet", "Watch", "Headphones"};
        
        for (int i = 0; i < 5; i++) {
            String orderId = "ORD" + String.format("%04d", random.nextInt(10000));
            String customer = customers[random.nextInt(customers.length)];
            String product = products[random.nextInt(products.length)];
            double amount = 50 + (random.nextDouble() * 950); // $50-$1000
            
            eventBus.publish(new OrderPlacedEvent(orderId, customer, amount, product));
            
            try {
                Thread.sleep(75);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
    
    public void generateSystemErrors() {
        String[] components = {"UserService", "OrderService", "PaymentGateway", "Database"};
        String[] errorCodes = {"SRV001", "DB002", "NET003", "AUTH004"};
        String[] messages = {"Connection timeout", "Invalid credentials", "Service unavailable", "Rate limit exceeded"};
        
        for (int i = 0; i < 3; i++) {
            String component = components[random.nextInt(components.length)];
            String errorCode = errorCodes[random.nextInt(errorCodes.length)];
            String message = messages[random.nextInt(messages.length)];
            
            eventBus.publish(new SystemErrorEvent(errorCode, message, component, 
                new RuntimeException("Simulated error")));
                
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
    
    public void generateNotifications() {
        String[] recipients = {"admin@company.com", "user1@email.com", "alerts@company.com"};
        String[] channels = {"email", "sms", "push"};
        String[] priorities = {"LOW", "MEDIUM", "HIGH"};
        String[] messages = {"System maintenance scheduled", "New feature available", "Security update required"};
        
        for (int i = 0; i < 4; i++) {
            String recipient = recipients[random.nextInt(recipients.length)];
            String channel = channels[random.nextInt(channels.length)];
            String priority = priorities[random.nextInt(priorities.length)];
            String message = messages[random.nextInt(messages.length)];
            
            eventBus.publish(new NotificationEvent(recipient, message, channel, priority));
            
            try {
                Thread.sleep(60);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}

// Demonstration class
public class EventSystemExample {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Event System Observer Pattern Example ===\n");
        
        // Create the event bus (our main subject)
        EventBus eventBus = new EventBus();
        
        // Create various event listeners (observers)
        SecurityAuditListener securityListener = new SecurityAuditListener();
        OrderProcessingListener orderListener = new OrderProcessingListener();
        NotificationListener notificationListener = new NotificationListener();
        MetricsCollector metricsCollector = new MetricsCollector();
        ErrorHandlingListener errorListener = new ErrorHandlingListener();
        DatabaseListener databaseListener = new DatabaseListener();
        
        // Subscribe listeners to the event bus
        System.out.println("--- Subscribing Listeners ---");
        eventBus.subscribe(securityListener);
        eventBus.subscribe(orderListener);
        eventBus.subscribe(notificationListener);
        eventBus.subscribe(metricsCollector);
        eventBus.subscribe(errorListener);
        eventBus.subscribe(databaseListener);
        
        System.out.println("Total listeners: " + eventBus.getListenerCount() + "\n");
        
        // Create event generator
        EventGenerator generator = new EventGenerator(eventBus);
        
        // Generate different types of events
        System.out.println("=== Generating User Activity Events ===");
        generator.generateUserActivity();
        Thread.sleep(500); // Allow async events to complete
        
        System.out.println("\n=== Generating Order Events ===");
        generator.generateOrderActivity();
        Thread.sleep(500);
        
        System.out.println("\n=== Generating System Error Events ===");
        generator.generateSystemErrors();
        Thread.sleep(500);
        
        System.out.println("\n=== Generating Notification Events ===");
        generator.generateNotifications();
        Thread.sleep(500);
        
        // Demonstrate dynamic listener management
        System.out.println("\n=== Dynamic Listener Management ===");
        
        // Add a new specialized listener
        EventListener<OrderPlacedEvent> vipOrderListener = new BaseEventListener<OrderPlacedEvent>(
            "VipOrderProcessor", OrderPlacedEvent.class, 95, false) {
            @Override
            public void onEvent(OrderPlacedEvent event) {
                if (event.getAmount() > 500) {
                    System.out.println("👑 VIP ORDER: High-value order " + event.getOrderId() + 
                        " ($" + String.format("%.2f", event.getAmount()) + ") - Priority processing!");
                }
            }
        };
        
        eventBus.subscribe(vipOrderListener);
        
        // Generate a high-value order
        eventBus.publish(new OrderPlacedEvent("VIP001", "PREMIUM_CUSTOMER", 1500.00, "Luxury Watch"));
        Thread.sleep(200);
        
        // Demonstrate event replay
        System.out.println("\n=== Event Replay Demonstration ===");
        EventListener<Event> auditListener = new BaseEventListener<Event>(
            "AuditReplay", Event.class, 1, false) {
            @Override
            public void onEvent(Event event) {
                System.out.println("🔍 AUDIT REPLAY: " + event);
            }
        };
        
        eventBus.replayEvents(auditListener, 5); // Replay last 5 events
        Thread.sleep(200);
        
        // Unsubscribe a listener
        System.out.println("\n=== Unsubscribing Notification Listener ===");
        eventBus.unsubscribe(notificationListener);
        
        // Generate one more event to show the difference
        eventBus.publish(new OrderPlacedEvent("FINAL001", "CUST999", 299.99, "Final Product"));
        Thread.sleep(200);
        
        // Print final statistics
        printStatistics(eventBus, metricsCollector, databaseListener);
        
        // Demonstrate error resilience
        demonstrateErrorResilience(eventBus);
        
        // Clean up
        System.out.println("\n=== Cleanup ===");
        eventBus.shutdown();
        
        demonstratePatternBenefits();
    }
    
    private static void printStatistics(EventBus eventBus, MetricsCollector metricsCollector, 
                                      DatabaseListener databaseListener) {
        System.out.println("\n=== Final Statistics ===");
        System.out.println("Event Bus Metrics:");
        System.out.println("  Event Counts: " + eventBus.getEventCounts());
        System.out.println("  Active Listeners: " + eventBus.getListenerCount());
        
        System.out.println("\nCollected Metrics:");
        System.out.println("  " + metricsCollector.getMetrics());
        
        System.out.println("\nDatabase Records:");
        System.out.println("  Total Persisted: " + databaseListener.getRecordsInserted());
    }
    
    private static void demonstrateErrorResilience(EventBus eventBus) throws InterruptedException {
        System.out.println("\n=== Error Resilience Demonstration ===");
        
        // Create a faulty listener
        EventListener<Event> faultyListener = new BaseEventListener<Event>(
            "FaultyListener", Event.class, 80, false) {
            @Override
            public void onEvent(Event event) {
                throw new RuntimeException("Simulated listener failure");
            }
        };
        
        eventBus.subscribe(faultyListener);
        
        // Publish an event - should show error but continue processing other listeners
        eventBus.publish(new UserLoginEvent("testuser", "127.0.0.1", true));
        Thread.sleep(200);
        
        eventBus.unsubscribe(faultyListener);
        System.out.println("✅ System continued processing despite faulty listener");
    }
    
    private static void demonstratePatternBenefits() {
        System.out.println("\n=== Observer Pattern Benefits in Event Systems ===");
        System.out.println("✓ Decoupled Architecture: Event publishers don't know about consumers");
        System.out.println("✓ Scalable: Can add unlimited event listeners without changing producers");
        System.out.println("✓ Flexible: Different listeners can react differently to same events");
        System.out.println("✓ Asynchronous Processing: Supports both sync and async event handling");
        System.out.println("✓ Priority Support: Critical listeners can be processed first");
        System.out.println("✓ Error Isolation: Failing listeners don't affect others");
        System.out.println("✓ Event Replay: Can replay historical events to new listeners");
        System.out.println("✓ Real-time Monitoring: Built-in metrics and statistics collection");
        System.out.println("✓ Dynamic Management: Listeners can be added/removed at runtime");
        System.out.println("✓ Generic Type Safety: Type-safe event handling with generics");
    }
}