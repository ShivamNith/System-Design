package org.example.solidPrinciples.dependencyInversion;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;

/**
 * DEPENDENCY INVERSION PRINCIPLE (DIP) - MESSAGING SYSTEM EXAMPLE
 *
 * Principle: High-level modules should not depend on low-level modules.
 * Both should depend on abstractions. Abstractions should not depend on details.
 * Details should depend on abstractions.
 *
 * This example demonstrates a messaging system where business logic depends
 * on abstractions rather than concrete implementations of message brokers.
 */

public class MessagingSystemExample {

    // ================== BEFORE - VIOLATING DIP ==================
    // High-level modules directly depend on low-level modules
    // Tightly coupled to specific messaging implementations

    public static class BeforeExample {

        // Message class
        static class Message {
            private final String id;
            private final String topic;
            private final String payload;
            private final LocalDateTime timestamp;

            public Message(String topic, String payload) {
                this.id = UUID.randomUUID().toString();
                this.topic = topic;
                this.payload = payload;
                this.timestamp = LocalDateTime.now();
            }

            // Getters
            public String getId() { return id; }
            public String getTopic() { return topic; }
            public String getPayload() { return payload; }
            public LocalDateTime getTimestamp() { return timestamp; }
        }

        // Low-level module - RabbitMQ implementation
        static class RabbitMQClient {
            private final Map<String, List<String>> queues = new HashMap<>();
            private final String host;
            private final int port;

            public RabbitMQClient(String host, int port) {
                this.host = host;
                this.port = port;
                System.out.println("Connected to RabbitMQ at " + host + ":" + port);
            }

            public void publishToExchange(String exchange, String routingKey, String message) {
                String queueName = exchange + "." + routingKey;
                queues.computeIfAbsent(queueName, k -> new ArrayList<>()).add(message);
                System.out.println("RabbitMQ: Published to " + queueName + ": " + message);
            }

            public String consumeFromQueue(String queue) {
                List<String> messages = queues.get(queue);
                if (messages != null && !messages.isEmpty()) {
                    return messages.remove(0);
                }
                return null;
            }

            public void declareQueue(String queue) {
                queues.putIfAbsent(queue, new ArrayList<>());
                System.out.println("RabbitMQ: Declared queue " + queue);
            }
        }

        // Low-level module - Kafka implementation
        static class KafkaClient {
            private final Map<String, List<String>> topics = new HashMap<>();
            private final String bootstrapServers;

            public KafkaClient(String bootstrapServers) {
                this.bootstrapServers = bootstrapServers;
                System.out.println("Connected to Kafka at " + bootstrapServers);
            }

            public void sendToTopic(String topic, String key, String value) {
                topics.computeIfAbsent(topic, k -> new ArrayList<>()).add(value);
                System.out.println("Kafka: Sent to topic " + topic + ": " + value);
            }

            public List<String> pollFromTopic(String topic) {
                List<String> messages = topics.get(topic);
                if (messages != null) {
                    List<String> result = new ArrayList<>(messages);
                    messages.clear();
                    return result;
                }
                return new ArrayList<>();
            }

            public void createTopic(String topic) {
                topics.putIfAbsent(topic, new ArrayList<>());
                System.out.println("Kafka: Created topic " + topic);
            }
        }

        // High-level module - Order Service (depends on concrete implementations)
        static class OrderService {
            private final RabbitMQClient rabbitMQ;  // Direct dependency on RabbitMQ
            private final KafkaClient kafka;        // Direct dependency on Kafka

            public OrderService() {
                // Creating concrete implementations directly
                this.rabbitMQ = new RabbitMQClient("localhost", 5672);
                this.kafka = new KafkaClient("localhost:9092");

                // Setup specific to each broker
                rabbitMQ.declareQueue("order.created");
                kafka.createTopic("order-events");
            }

            public void createOrder(String orderId, String customerId, double amount) {
                // Business logic
                System.out.println("Creating order: " + orderId);

                // Tightly coupled to RabbitMQ for notifications
                String notification = "Order " + orderId + " created for customer " + customerId;
                rabbitMQ.publishToExchange("notifications", "order.created", notification);

                // Tightly coupled to Kafka for events
                String event = "{\"orderId\":\"" + orderId + "\",\"amount\":" + amount + "}";
                kafka.sendToTopic("order-events", orderId, event);

                // What if we want to switch to AWS SQS or Azure Service Bus?
                // We'd have to modify this high-level business logic!
            }

            public void processNotifications() {
                // Coupled to RabbitMQ's way of consuming
                String message = rabbitMQ.consumeFromQueue("order.created");
                if (message != null) {
                    System.out.println("Processing notification: " + message);
                }
            }

            public void processEvents() {
                // Coupled to Kafka's way of consuming
                List<String> events = kafka.pollFromTopic("order-events");
                for (String event : events) {
                    System.out.println("Processing event: " + event);
                }
            }
        }

        // High-level module - Inventory Service (also depends on concrete implementations)
        static class InventoryService {
            private final KafkaClient kafka;  // Direct dependency

            public InventoryService() {
                this.kafka = new KafkaClient("localhost:9092");
                kafka.createTopic("inventory-updates");
            }

            public void updateInventory(String productId, int quantity) {
                // Business logic
                System.out.println("Updating inventory for product: " + productId);

                // Tightly coupled to Kafka
                String update = "{\"productId\":\"" + productId + "\",\"quantity\":" + quantity + "}";
                kafka.sendToTopic("inventory-updates", productId, update);

                // Switching messaging systems requires changing business logic
            }
        }

        public static void demonstrate() {
            System.out.println("=== BEFORE - Violating DIP ===\n");

            OrderService orderService = new OrderService();
            InventoryService inventoryService = new InventoryService();

            // Services are tightly coupled to specific messaging implementations
            orderService.createOrder("ORD-123", "CUST-456", 99.99);
            inventoryService.updateInventory("PROD-789", 10);

            orderService.processNotifications();
            orderService.processEvents();

            System.out.println("\nProblems:");
            System.out.println("1. High-level business logic depends on low-level messaging details");
            System.out.println("2. Cannot easily switch messaging systems");
            System.out.println("3. Cannot test without real messaging infrastructure");
            System.out.println("4. Code duplication across services");
        }
    }

    // ================== AFTER - FOLLOWING DIP ==================
    // High-level and low-level modules depend on abstractions
    // Easy to switch implementations and test

    public static class AfterExample {

        // Core message abstraction
        static class Message {
            private final String id;
            private final String topic;
            private final Map<String, Object> headers;
            private final String payload;
            private final LocalDateTime timestamp;

            public Message(String topic, String payload) {
                this(topic, new HashMap<>(), payload);
            }

            public Message(String topic, Map<String, Object> headers, String payload) {
                this.id = UUID.randomUUID().toString();
                this.topic = topic;
                this.headers = headers;
                this.payload = payload;
                this.timestamp = LocalDateTime.now();
            }

            // Getters
            public String getId() { return id; }
            public String getTopic() { return topic; }
            public Map<String, Object> getHeaders() { return headers; }
            public String getPayload() { return payload; }
            public LocalDateTime getTimestamp() { return timestamp; }
        }

        // Abstraction - Message Publisher
        interface MessagePublisher {
            void publish(Message message);
            void publish(String topic, String payload);
            CompletableFuture<Void> publishAsync(Message message);
        }

        // Abstraction - Message Consumer
        interface MessageConsumer {
            void subscribe(String topic, Consumer<Message> handler);
            void unsubscribe(String topic);
            List<Message> poll(String topic, int maxMessages);
        }

        // Abstraction - Message Broker (combines publisher and consumer)
        interface MessageBroker extends MessagePublisher, MessageConsumer {
            void connect();
            void disconnect();
            boolean isConnected();
            void createTopic(String topic);
            void deleteTopic(String topic);
        }

        // Abstraction - Message Router for routing patterns
        interface MessageRouter {
            void route(Message message, String... destinations);
            void addRoute(String source, String destination);
            void removeRoute(String source, String destination);
        }

        // Concrete implementation - RabbitMQ Adapter
        static class RabbitMQMessageBroker implements MessageBroker {
            private final String host;
            private final int port;
            private final Map<String, List<Message>> queues = new ConcurrentHashMap<>();
            private final Map<String, List<Consumer<Message>>> subscribers = new ConcurrentHashMap<>();
            private boolean connected = false;

            public RabbitMQMessageBroker(String host, int port) {
                this.host = host;
                this.port = port;
            }

            @Override
            public void connect() {
                System.out.println("Connecting to RabbitMQ at " + host + ":" + port);
                connected = true;
            }

            @Override
            public void disconnect() {
                System.out.println("Disconnecting from RabbitMQ");
                connected = false;
            }

            @Override
            public boolean isConnected() {
                return connected;
            }

            @Override
            public void createTopic(String topic) {
                queues.putIfAbsent(topic, new CopyOnWriteArrayList<>());
                System.out.println("RabbitMQ: Created queue " + topic);
            }

            @Override
            public void deleteTopic(String topic) {
                queues.remove(topic);
                subscribers.remove(topic);
                System.out.println("RabbitMQ: Deleted queue " + topic);
            }

            @Override
            public void publish(Message message) {
                if (!connected) throw new IllegalStateException("Not connected");
                
                List<Message> queue = queues.computeIfAbsent(message.getTopic(), 
                    k -> new CopyOnWriteArrayList<>());
                queue.add(message);
                
                // Notify subscribers
                List<Consumer<Message>> handlers = subscribers.get(message.getTopic());
                if (handlers != null) {
                    handlers.forEach(handler -> handler.accept(message));
                }
                
                System.out.println("RabbitMQ: Published to " + message.getTopic());
            }

            @Override
            public void publish(String topic, String payload) {
                publish(new Message(topic, payload));
            }

            @Override
            public CompletableFuture<Void> publishAsync(Message message) {
                return CompletableFuture.runAsync(() -> publish(message));
            }

            @Override
            public void subscribe(String topic, Consumer<Message> handler) {
                subscribers.computeIfAbsent(topic, k -> new CopyOnWriteArrayList<>())
                    .add(handler);
                System.out.println("RabbitMQ: Subscribed to " + topic);
            }

            @Override
            public void unsubscribe(String topic) {
                subscribers.remove(topic);
                System.out.println("RabbitMQ: Unsubscribed from " + topic);
            }

            @Override
            public List<Message> poll(String topic, int maxMessages) {
                List<Message> queue = queues.get(topic);
                if (queue == null || queue.isEmpty()) {
                    return new ArrayList<>();
                }
                
                List<Message> result = new ArrayList<>();
                for (int i = 0; i < Math.min(maxMessages, queue.size()); i++) {
                    result.add(queue.remove(0));
                }
                return result;
            }
        }

        // Concrete implementation - Kafka Adapter
        static class KafkaMessageBroker implements MessageBroker {
            private final String bootstrapServers;
            private final Map<String, List<Message>> topics = new ConcurrentHashMap<>();
            private final Map<String, List<Consumer<Message>>> subscribers = new ConcurrentHashMap<>();
            private boolean connected = false;

            public KafkaMessageBroker(String bootstrapServers) {
                this.bootstrapServers = bootstrapServers;
            }

            @Override
            public void connect() {
                System.out.println("Connecting to Kafka at " + bootstrapServers);
                connected = true;
            }

            @Override
            public void disconnect() {
                System.out.println("Disconnecting from Kafka");
                connected = false;
            }

            @Override
            public boolean isConnected() {
                return connected;
            }

            @Override
            public void createTopic(String topic) {
                topics.putIfAbsent(topic, new CopyOnWriteArrayList<>());
                System.out.println("Kafka: Created topic " + topic);
            }

            @Override
            public void deleteTopic(String topic) {
                topics.remove(topic);
                subscribers.remove(topic);
                System.out.println("Kafka: Deleted topic " + topic);
            }

            @Override
            public void publish(Message message) {
                if (!connected) throw new IllegalStateException("Not connected");
                
                List<Message> topicMessages = topics.computeIfAbsent(message.getTopic(), 
                    k -> new CopyOnWriteArrayList<>());
                topicMessages.add(message);
                
                // Notify subscribers
                List<Consumer<Message>> handlers = subscribers.get(message.getTopic());
                if (handlers != null) {
                    handlers.forEach(handler -> handler.accept(message));
                }
                
                System.out.println("Kafka: Published to " + message.getTopic());
            }

            @Override
            public void publish(String topic, String payload) {
                publish(new Message(topic, payload));
            }

            @Override
            public CompletableFuture<Void> publishAsync(Message message) {
                return CompletableFuture.runAsync(() -> publish(message));
            }

            @Override
            public void subscribe(String topic, Consumer<Message> handler) {
                subscribers.computeIfAbsent(topic, k -> new CopyOnWriteArrayList<>())
                    .add(handler);
                System.out.println("Kafka: Subscribed to " + topic);
            }

            @Override
            public void unsubscribe(String topic) {
                subscribers.remove(topic);
                System.out.println("Kafka: Unsubscribed from " + topic);
            }

            @Override
            public List<Message> poll(String topic, int maxMessages) {
                List<Message> topicMessages = topics.get(topic);
                if (topicMessages == null || topicMessages.isEmpty()) {
                    return new ArrayList<>();
                }
                
                List<Message> result = new ArrayList<>();
                for (int i = 0; i < Math.min(maxMessages, topicMessages.size()); i++) {
                    result.add(topicMessages.remove(0));
                }
                return result;
            }
        }

        // Concrete implementation - In-Memory Broker for testing
        static class InMemoryMessageBroker implements MessageBroker {
            private final Map<String, List<Message>> topics = new ConcurrentHashMap<>();
            private final Map<String, List<Consumer<Message>>> subscribers = new ConcurrentHashMap<>();
            private boolean connected = false;

            @Override
            public void connect() {
                System.out.println("InMemory broker connected");
                connected = true;
            }

            @Override
            public void disconnect() {
                System.out.println("InMemory broker disconnected");
                connected = false;
            }

            @Override
            public boolean isConnected() {
                return connected;
            }

            @Override
            public void createTopic(String topic) {
                topics.putIfAbsent(topic, new CopyOnWriteArrayList<>());
                System.out.println("InMemory: Created topic " + topic);
            }

            @Override
            public void deleteTopic(String topic) {
                topics.remove(topic);
                subscribers.remove(topic);
            }

            @Override
            public void publish(Message message) {
                if (!connected) throw new IllegalStateException("Not connected");
                
                List<Message> topicMessages = topics.computeIfAbsent(message.getTopic(), 
                    k -> new CopyOnWriteArrayList<>());
                topicMessages.add(message);
                
                // Immediately notify subscribers (synchronous for testing)
                List<Consumer<Message>> handlers = subscribers.get(message.getTopic());
                if (handlers != null) {
                    handlers.forEach(handler -> handler.accept(message));
                }
                
                System.out.println("InMemory: Published to " + message.getTopic());
            }

            @Override
            public void publish(String topic, String payload) {
                publish(new Message(topic, payload));
            }

            @Override
            public CompletableFuture<Void> publishAsync(Message message) {
                return CompletableFuture.completedFuture(null)
                    .thenRun(() -> publish(message));
            }

            @Override
            public void subscribe(String topic, Consumer<Message> handler) {
                subscribers.computeIfAbsent(topic, k -> new CopyOnWriteArrayList<>())
                    .add(handler);
            }

            @Override
            public void unsubscribe(String topic) {
                subscribers.remove(topic);
            }

            @Override
            public List<Message> poll(String topic, int maxMessages) {
                List<Message> topicMessages = topics.get(topic);
                if (topicMessages == null || topicMessages.isEmpty()) {
                    return new ArrayList<>();
                }
                
                List<Message> result = new ArrayList<>();
                for (int i = 0; i < Math.min(maxMessages, topicMessages.size()); i++) {
                    result.add(topicMessages.remove(0));
                }
                return result;
            }
        }

        // Message Router implementation
        static class DefaultMessageRouter implements MessageRouter {
            private final MessagePublisher publisher;
            private final Map<String, Set<String>> routes = new ConcurrentHashMap<>();

            public DefaultMessageRouter(MessagePublisher publisher) {
                this.publisher = publisher;
            }

            @Override
            public void route(Message message, String... destinations) {
                for (String destination : destinations) {
                    Message routedMessage = new Message(
                        destination,
                        message.getHeaders(),
                        message.getPayload()
                    );
                    publisher.publish(routedMessage);
                }
            }

            @Override
            public void addRoute(String source, String destination) {
                routes.computeIfAbsent(source, k -> new HashSet<>()).add(destination);
                System.out.println("Added route: " + source + " -> " + destination);
            }

            @Override
            public void removeRoute(String source, String destination) {
                Set<String> destinations = routes.get(source);
                if (destinations != null) {
                    destinations.remove(destination);
                }
            }
        }

        // High-level module - Order Service (depends on abstractions)
        static class OrderService {
            private final MessagePublisher publisher;  // Depends on abstraction
            private final MessageConsumer consumer;    // Depends on abstraction

            public OrderService(MessagePublisher publisher, MessageConsumer consumer) {
                this.publisher = publisher;
                this.consumer = consumer;
                
                // Subscribe to relevant topics
                consumer.subscribe("order-commands", this::handleOrderCommand);
            }

            public void createOrder(String orderId, String customerId, double amount) {
                // Business logic
                System.out.println("Creating order: " + orderId);

                // Publish using abstraction - not tied to any specific broker
                Map<String, Object> headers = new HashMap<>();
                headers.put("orderId", orderId);
                headers.put("customerId", customerId);
                headers.put("amount", amount);

                String payload = String.format(
                    "{\"orderId\":\"%s\",\"customerId\":\"%s\",\"amount\":%.2f}",
                    orderId, customerId, amount
                );

                publisher.publish(new Message("order-created", headers, payload));
                publisher.publish("notifications", 
                    "Order " + orderId + " created for customer " + customerId);
            }

            private void handleOrderCommand(Message message) {
                System.out.println("Order service handling command: " + message.getPayload());
            }

            public void processMessages() {
                List<Message> messages = consumer.poll("order-created", 10);
                for (Message message : messages) {
                    System.out.println("Processing order message: " + message.getPayload());
                }
            }
        }

        // High-level module - Inventory Service (depends on abstractions)
        static class InventoryService {
            private final MessagePublisher publisher;  // Depends on abstraction

            public InventoryService(MessagePublisher publisher) {
                this.publisher = publisher;
            }

            public void updateInventory(String productId, int quantity) {
                // Business logic
                System.out.println("Updating inventory for product: " + productId);

                // Publish using abstraction
                Map<String, Object> headers = new HashMap<>();
                headers.put("productId", productId);
                headers.put("quantity", quantity);

                String payload = String.format(
                    "{\"productId\":\"%s\",\"quantity\":%d}",
                    productId, quantity
                );

                publisher.publish(new Message("inventory-updated", headers, payload));
            }
        }

        // High-level module - Notification Service
        static class NotificationService {
            private final MessageConsumer consumer;

            public NotificationService(MessageConsumer consumer) {
                this.consumer = consumer;
                
                // Subscribe to multiple topics
                consumer.subscribe("order-created", this::handleOrderCreated);
                consumer.subscribe("inventory-updated", this::handleInventoryUpdated);
                consumer.subscribe("notifications", this::handleNotification);
            }

            private void handleOrderCreated(Message message) {
                System.out.println("Sending order notification: " + message.getPayload());
            }

            private void handleInventoryUpdated(Message message) {
                System.out.println("Inventory update notification: " + message.getPayload());
            }

            private void handleNotification(Message message) {
                System.out.println("General notification: " + message.getPayload());
            }
        }

        // Dependency Injection Container for messaging
        static class MessagingContainer {
            private MessageBroker broker;
            private MessageRouter router;

            public void configureForProduction() {
                broker = new KafkaMessageBroker("production-kafka:9092");
                broker.connect();
                router = new DefaultMessageRouter(broker);
            }

            public void configureForDevelopment() {
                broker = new RabbitMQMessageBroker("localhost", 5672);
                broker.connect();
                router = new DefaultMessageRouter(broker);
            }

            public void configureForTesting() {
                broker = new InMemoryMessageBroker();
                broker.connect();
                router = new DefaultMessageRouter(broker);
            }

            public MessageBroker getBroker() {
                return broker;
            }

            public MessageRouter getRouter() {
                return router;
            }

            public void shutdown() {
                if (broker != null && broker.isConnected()) {
                    broker.disconnect();
                }
            }
        }

        public static void demonstrate() {
            System.out.println("=== AFTER - Following DIP ===\n");

            // Configure messaging infrastructure
            MessagingContainer container = new MessagingContainer();
            
            // Easy to switch between implementations
            System.out.println("Testing with in-memory broker:");
            container.configureForTesting();
            runDemo(container);
            
            System.out.println("\nSwitching to RabbitMQ:");
            container.shutdown();
            container.configureForDevelopment();
            runDemo(container);

            container.shutdown();

            System.out.println("\nBenefits:");
            System.out.println("1. High-level modules depend on abstractions, not concrete implementations");
            System.out.println("2. Easy to switch between different messaging systems");
            System.out.println("3. Can test with in-memory implementation");
            System.out.println("4. New messaging systems can be added without changing business logic");
            System.out.println("5. Follows Open/Closed Principle as well");
        }

        private static void runDemo(MessagingContainer container) {
            MessageBroker broker = container.getBroker();
            
            // Create topics
            broker.createTopic("order-created");
            broker.createTopic("inventory-updated");
            broker.createTopic("notifications");
            broker.createTopic("order-commands");

            // Create services with dependency injection
            OrderService orderService = new OrderService(broker, broker);
            InventoryService inventoryService = new InventoryService(broker);
            NotificationService notificationService = new NotificationService(broker);

            // Services work the same regardless of messaging implementation
            orderService.createOrder("ORD-456", "CUST-789", 149.99);
            inventoryService.updateInventory("PROD-321", 5);

            // Process messages
            orderService.processMessages();
        }
    }

    // ================== ADVANCED EXAMPLE - Event Sourcing with DIP ==================
    
    public static class EventSourcingExample {
        
        // Event abstraction
        interface Event {
            String getAggregateId();
            String getEventType();
            LocalDateTime getTimestamp();
            Map<String, Object> getData();
        }

        // Event Store abstraction
        interface EventStore {
            void append(Event event);
            List<Event> getEvents(String aggregateId);
            List<Event> getEventsByType(String eventType);
            void subscribe(String eventType, Consumer<Event> handler);
        }

        // Event Publisher abstraction
        interface EventPublisher {
            void publish(Event event);
        }

        // Snapshot Store abstraction
        interface SnapshotStore {
            void save(String aggregateId, Object snapshot);
            <T> Optional<T> load(String aggregateId, Class<T> type);
        }

        // Concrete Event implementation
        static class DomainEvent implements Event {
            private final String aggregateId;
            private final String eventType;
            private final LocalDateTime timestamp;
            private final Map<String, Object> data;

            public DomainEvent(String aggregateId, String eventType, Map<String, Object> data) {
                this.aggregateId = aggregateId;
                this.eventType = eventType;
                this.timestamp = LocalDateTime.now();
                this.data = new HashMap<>(data);
            }

            @Override
            public String getAggregateId() { return aggregateId; }

            @Override
            public String getEventType() { return eventType; }

            @Override
            public LocalDateTime getTimestamp() { return timestamp; }

            @Override
            public Map<String, Object> getData() { return new HashMap<>(data); }
        }

        // Aggregate Root base class
        abstract static class AggregateRoot {
            protected final String id;
            protected final List<Event> uncommittedEvents = new ArrayList<>();
            protected int version = 0;

            protected AggregateRoot(String id) {
                this.id = id;
            }

            protected void raiseEvent(Event event) {
                uncommittedEvents.add(event);
                apply(event);
                version++;
            }

            public List<Event> getUncommittedEvents() {
                return new ArrayList<>(uncommittedEvents);
            }

            public void markEventsAsCommitted() {
                uncommittedEvents.clear();
            }

            public void loadFromHistory(List<Event> events) {
                for (Event event : events) {
                    apply(event);
                    version++;
                }
            }

            protected abstract void apply(Event event);
        }

        // Example Aggregate - Order
        static class Order extends AggregateRoot {
            private String customerId;
            private double totalAmount;
            private String status;
            private List<String> items = new ArrayList<>();

            public Order(String id) {
                super(id);
            }

            public void create(String customerId, double amount) {
                Map<String, Object> data = new HashMap<>();
                data.put("customerId", customerId);
                data.put("amount", amount);
                raiseEvent(new DomainEvent(id, "OrderCreated", data));
            }

            public void addItem(String itemId) {
                Map<String, Object> data = new HashMap<>();
                data.put("itemId", itemId);
                raiseEvent(new DomainEvent(id, "ItemAdded", data));
            }

            public void ship() {
                raiseEvent(new DomainEvent(id, "OrderShipped", new HashMap<>()));
            }

            @Override
            protected void apply(Event event) {
                switch (event.getEventType()) {
                    case "OrderCreated":
                        this.customerId = (String) event.getData().get("customerId");
                        this.totalAmount = (Double) event.getData().get("amount");
                        this.status = "Created";
                        break;
                    case "ItemAdded":
                        this.items.add((String) event.getData().get("itemId"));
                        break;
                    case "OrderShipped":
                        this.status = "Shipped";
                        break;
                }
            }

            public String getStatus() { return status; }
        }

        // Repository abstraction
        interface Repository<T extends AggregateRoot> {
            void save(T aggregate);
            Optional<T> load(String id);
        }

        // Concrete Repository implementation
        static class EventSourcedRepository<T extends AggregateRoot> implements Repository<T> {
            private final EventStore eventStore;
            private final EventPublisher eventPublisher;
            private final Class<T> aggregateClass;

            public EventSourcedRepository(EventStore eventStore, 
                                        EventPublisher eventPublisher,
                                        Class<T> aggregateClass) {
                this.eventStore = eventStore;
                this.eventPublisher = eventPublisher;
                this.aggregateClass = aggregateClass;
            }

            @Override
            public void save(T aggregate) {
                List<Event> events = aggregate.getUncommittedEvents();
                for (Event event : events) {
                    eventStore.append(event);
                    eventPublisher.publish(event);
                }
                aggregate.markEventsAsCommitted();
            }

            @Override
            public Optional<T> load(String id) {
                List<Event> events = eventStore.getEvents(id);
                if (events.isEmpty()) {
                    return Optional.empty();
                }

                try {
                    T aggregate = aggregateClass.getDeclaredConstructor(String.class)
                        .newInstance(id);
                    aggregate.loadFromHistory(events);
                    return Optional.of(aggregate);
                } catch (Exception e) {
                    throw new RuntimeException("Failed to create aggregate", e);
                }
            }
        }

        public static void demonstrate() {
            System.out.println("\n=== ADVANCED - Event Sourcing with DIP ===\n");

            // In-memory implementations for demo
            EventStore eventStore = new InMemoryEventStore();
            EventPublisher publisher = event -> 
                System.out.println("Publishing: " + event.getEventType());

            // Repository depends on abstractions
            Repository<Order> orderRepo = new EventSourcedRepository<>(
                eventStore, publisher, Order.class
            );

            // Create and save order
            Order order = new Order("ORDER-789");
            order.create("CUSTOMER-123", 299.99);
            order.addItem("ITEM-1");
            order.addItem("ITEM-2");
            order.ship();

            orderRepo.save(order);

            // Load order from events
            Optional<Order> loaded = orderRepo.load("ORDER-789");
            loaded.ifPresent(o -> 
                System.out.println("Loaded order with status: " + o.getStatus())
            );

            System.out.println("\nEvent sourcing benefits with DIP:");
            System.out.println("1. Event store implementation can be swapped");
            System.out.println("2. Publisher can be Kafka, RabbitMQ, or any other");
            System.out.println("3. Repository logic is independent of storage");
            System.out.println("4. Complete audit trail of all changes");
        }

        // Simple in-memory event store for demo
        static class InMemoryEventStore implements EventStore {
            private final List<Event> events = new ArrayList<>();
            private final Map<String, List<Consumer<Event>>> subscribers = new HashMap<>();

            @Override
            public void append(Event event) {
                events.add(event);
                System.out.println("Stored event: " + event.getEventType() + 
                                 " for " + event.getAggregateId());
                
                // Notify subscribers
                List<Consumer<Event>> handlers = subscribers.get(event.getEventType());
                if (handlers != null) {
                    handlers.forEach(h -> h.accept(event));
                }
            }

            @Override
            public List<Event> getEvents(String aggregateId) {
                return events.stream()
                    .filter(e -> e.getAggregateId().equals(aggregateId))
                    .collect(ArrayList::new, (list, e) -> list.add(e), ArrayList::addAll);
            }

            @Override
            public List<Event> getEventsByType(String eventType) {
                return events.stream()
                    .filter(e -> e.getEventType().equals(eventType))
                    .collect(ArrayList::new, (list, e) -> list.add(e), ArrayList::addAll);
            }

            @Override
            public void subscribe(String eventType, Consumer<Event> handler) {
                subscribers.computeIfAbsent(eventType, k -> new ArrayList<>())
                    .add(handler);
            }
        }
    }

    // Main method to run all examples
    public static void main(String[] args) {
        System.out.println("DEPENDENCY INVERSION PRINCIPLE - MESSAGING SYSTEM EXAMPLE");
        System.out.println("=" .repeat(60) + "\n");

        // Run before example
        BeforeExample.demonstrate();

        System.out.println("\n" + "=" .repeat(60) + "\n");

        // Run after example
        AfterExample.demonstrate();

        System.out.println("\n" + "=" .repeat(60) + "\n");

        // Run advanced example
        EventSourcingExample.demonstrate();

        System.out.println("\n" + "=" .repeat(60));
        System.out.println("\nKey Takeaways:");
        System.out.println("1. Depend on abstractions (interfaces), not concretions");
        System.out.println("2. High-level policy should not depend on low-level details");
        System.out.println("3. Both should depend on abstractions");
        System.out.println("4. Abstractions should be stable and shouldn't change often");
        System.out.println("5. Use dependency injection to provide concrete implementations");
        System.out.println("6. Makes testing much easier with mock implementations");
        System.out.println("7. Enables switching implementations without changing business logic");
    }
}