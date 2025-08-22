# Factory Pattern Practice Problems

## Overview
These practice problems will help you master the Factory Pattern by implementing real-world scenarios. Start with basic problems and progress to more complex challenges.

---

## Problem 1: Document Processor Factory (Basic)
**Difficulty:** ⭐⭐☆☆☆

### Problem Statement
Create a document processing system that can handle different file formats:
- **PDF**: Generate PDF documents with formatting
- **Word**: Create Word documents with styles
- **Excel**: Generate spreadsheets with formulas
- **HTML**: Create web pages with CSS
- **Markdown**: Generate markdown files

### Requirements
1. Create a `Document` interface with methods:
   - `create(String content)`
   - `addSection(String title, String content)`
   - `save(String filename)`
   - `getFileExtension()`
2. Implement concrete document classes for each format
3. Create a `DocumentFactory` using Simple Factory pattern
4. Support document conversion between formats
5. Add document templates (Resume, Report, Letter)

### Test Cases
```java
// Create different document types
Document pdf = DocumentFactory.createDocument("PDF");
pdf.addSection("Introduction", "This is a PDF document");
pdf.save("output.pdf");

// Convert between formats
Document html = DocumentFactory.convertDocument(pdf, "HTML");
```

### Hints
- Use template patterns for common document structures
- Consider using builder pattern for complex documents
- Implement format-specific features (PDF encryption, Excel formulas)

---

## Problem 2: Game Character Factory (Intermediate)
**Difficulty:** ⭐⭐⭐☆☆

### Problem Statement
Design a character creation system for an RPG game with different character classes:
- **Warrior**: High health, melee attacks
- **Mage**: Magic abilities, low health
- **Archer**: Ranged attacks, medium health
- **Rogue**: Stealth abilities, critical strikes
- **Healer**: Support abilities, healing powers

### Requirements
1. Create `Character` interface with:
   - `attack(Character target)`
   - `useAbility(String ability)`
   - `levelUp()`
   - `getStats()`
2. Implement Factory Method pattern with:
   - Abstract `CharacterFactory` class
   - Concrete factories for each character type
3. Add character customization:
   - Race selection (Human, Elf, Dwarf, Orc)
   - Starting equipment
   - Skill trees
4. Implement character progression system

### Additional Features
- Character subclasses (Paladin from Warrior, Necromancer from Mage)
- Hybrid classes combining two base classes
- Random character generation
- Character templates for quick creation

### Test Scenarios
```java
CharacterFactory warriorFactory = new WarriorFactory();
Character warrior = warriorFactory.createCharacter("Thorin", Race.DWARF);
warrior.equipItem(new Sword("Excalibur"));
warrior.attack(enemy);

// Create hybrid class
Character spellblade = CharacterFactory.createHybrid("Warrior", "Mage");
```

---

## Problem 3: Cloud Resource Factory (Intermediate)
**Difficulty:** ⭐⭐⭐☆☆

### Problem Statement
Create a cloud resource provisioning system that can deploy resources across different cloud providers:
- **AWS**: EC2, S3, RDS, Lambda
- **Azure**: VMs, Blob Storage, SQL Database, Functions
- **Google Cloud**: Compute Engine, Cloud Storage, Cloud SQL
- **Private Cloud**: Custom VMs, Storage, Database

### Requirements
1. Create `CloudResource` interface:
   - `provision(ResourceConfig config)`
   - `configure(Map<String, String> settings)`
   - `start()`
   - `stop()`
   - `getStatus()`
   - `getCost()`
2. Implement Abstract Factory pattern:
   - `CloudProviderFactory` interface
   - Concrete factories for each provider
   - Resource families (Compute, Storage, Database)
3. Add resource orchestration:
   - Dependencies between resources
   - Resource templates
   - Auto-scaling rules

### Complex Features
- Multi-cloud deployment
- Cost optimization across providers
- Resource migration between clouds
- Disaster recovery setup

### Example Usage
```java
CloudProviderFactory awsFactory = CloudFactoryProvider.getFactory("AWS");
ComputeResource ec2 = awsFactory.createCompute("t2.micro");
StorageResource s3 = awsFactory.createStorage("standard");
DatabaseResource rds = awsFactory.createDatabase("mysql");

// Deploy application stack
ApplicationStack stack = new ApplicationStack();
stack.addResource(ec2);
stack.addResource(s3);
stack.addResource(rds);
stack.deploy();
```

---

## Problem 4: Notification System Factory (Advanced)
**Difficulty:** ⭐⭐⭐⭐☆

### Problem Statement
Design a multi-channel notification system that can send notifications through various channels:
- **Email**: SMTP, SendGrid, AWS SES
- **SMS**: Twilio, AWS SNS, Nexmo
- **Push**: Firebase, OneSignal, AWS Pinpoint
- **In-App**: WebSocket, Server-Sent Events
- **Slack/Teams**: Webhook integrations

### Requirements
1. Create `Notification` interface:
   - `send(Message message, Recipient recipient)`
   - `sendBulk(Message message, List<Recipient> recipients)`
   - `schedule(Message message, DateTime sendTime)`
   - `getDeliveryStatus(String notificationId)`
2. Implement multiple factory patterns:
   - Simple Factory for channel selection
   - Factory Method for provider selection
   - Abstract Factory for notification families
3. Advanced features:
   - Message templates with variables
   - Delivery retry mechanism
   - Rate limiting
   - Priority queues
   - Delivery receipts

### Complex Requirements
- Fallback channels (if email fails, try SMS)
- A/B testing for notification content
- User preference management
- Notification aggregation (digest emails)
- Multi-language support
- Rich media support (images, videos)

### Test Implementation
```java
// Create notification factory
NotificationFactory factory = NotificationFactory.getInstance();

// Configure channels
factory.configureChannel("email", new EmailConfig("smtp.gmail.com", 587));
factory.configureChannel("sms", new SmsConfig("twilio", apiKey, apiSecret));

// Send notification with fallback
Notification notification = factory.createNotification("email")
    .withFallback("sms")
    .withTemplate("welcome_user")
    .withVariables(Map.of("username", "John"));

notification.send(recipient);

// Bulk send with rate limiting
BulkNotification bulkNotif = factory.createBulkNotification("push");
bulkNotif.setRateLimit(100); // 100 per second
bulkNotif.sendToSegment(userSegment);
```

---

## Problem 5: Payment Gateway Factory (Advanced)
**Difficulty:** ⭐⭐⭐⭐☆

### Problem Statement
Create a payment processing system supporting multiple payment gateways:
- **Stripe**: Credit cards, wallets
- **PayPal**: PayPal accounts, credit cards
- **Square**: In-person and online payments
- **Razorpay**: UPI, cards, wallets
- **Cryptocurrency**: Bitcoin, Ethereum

### Requirements
1. Create `PaymentGateway` interface:
   - `processPayment(Amount amount, PaymentMethod method)`
   - `refund(TransactionId id, Amount amount)`
   - `getTransactionStatus(TransactionId id)`
   - `validatePaymentMethod(PaymentMethod method)`
2. Implement sophisticated factory system:
   - Gateway selection based on region
   - Dynamic gateway switching
   - Load balancing between gateways
   - Failover mechanisms
3. Advanced payment features:
   - Recurring payments/subscriptions
   - Split payments
   - Escrow services
   - Currency conversion
   - Fraud detection

### Complex Scenarios
- Multi-gateway transactions (split across providers)
- Gateway health monitoring
- Transaction reconciliation
- PCI compliance handling
- Webhook management for async updates

---

## Problem 6: Database Query Builder Factory (Expert)
**Difficulty:** ⭐⭐⭐⭐⭐

### Problem Statement
Design a database-agnostic query builder that generates optimized queries for different databases:
- **MySQL**: MySQL-specific syntax and optimizations
- **PostgreSQL**: PostgreSQL features (CTEs, arrays)
- **MongoDB**: NoSQL document queries
- **Elasticsearch**: Search queries with aggregations
- **Redis**: Key-value operations

### Requirements
1. Create `QueryBuilder` interface:
   - `select(String... columns)`
   - `from(String table)`
   - `where(Condition condition)`
   - `join(JoinType type, String table, Condition on)`
   - `groupBy(String... columns)`
   - `having(Condition condition)`
   - `orderBy(String column, SortOrder order)`
   - `limit(int limit)`
   - `build()`
2. Implement complex factory system:
   - Database-specific optimizations
   - Query plan analysis
   - Index recommendations
   - Query caching strategies
3. Advanced features:
   - Subqueries and CTEs
   - Window functions
   - Full-text search
   - Geospatial queries
   - Time-series queries

### Expert-Level Requirements
- Query optimization hints
- Explain plan integration
- Cross-database migration queries
- Query performance profiling
- Automatic index creation suggestions

### Implementation Example
```java
QueryBuilderFactory factory = QueryBuilderFactory.getInstance();

// MySQL query with optimization
QueryBuilder mysqlBuilder = factory.createQueryBuilder("MySQL");
String query = mysqlBuilder
    .select("u.name", "COUNT(o.id) as order_count")
    .from("users u")
    .leftJoin("orders o", "u.id = o.user_id")
    .where("u.created_at > ?", lastMonth)
    .groupBy("u.id")
    .having("COUNT(o.id) > 5")
    .orderBy("order_count", DESC)
    .withHint("USE INDEX (idx_created_at)")
    .build();

// MongoDB aggregation pipeline
QueryBuilder mongoBuilder = factory.createQueryBuilder("MongoDB");
Document pipeline = mongoBuilder
    .match(Filters.gte("createdAt", lastMonth))
    .lookup("orders", "id", "userId", "userOrders")
    .group("$_id", Accumulators.sum("orderCount", 1))
    .match(Filters.gt("orderCount", 5))
    .sort(Sorts.descending("orderCount"))
    .buildAggregation();
```

---

## Problem 7: Machine Learning Model Factory (Expert)
**Difficulty:** ⭐⭐⭐⭐⭐

### Problem Statement
Create a machine learning model factory that can instantiate and manage different types of ML models:
- **Classification**: Logistic Regression, Random Forest, SVM, Neural Networks
- **Regression**: Linear, Polynomial, Ridge, Lasso
- **Clustering**: K-Means, DBSCAN, Hierarchical
- **Deep Learning**: CNN, RNN, Transformer
- **Ensemble**: Voting, Stacking, Boosting

### Requirements
1. Create `MLModel` interface:
   - `train(Dataset data)`
   - `predict(Features features)`
   - `evaluate(Dataset testData)`
   - `saveModel(String path)`
   - `loadModel(String path)`
   - `getHyperparameters()`
2. Implement sophisticated factory system:
   - Model selection based on data characteristics
   - Automatic hyperparameter tuning
   - Model versioning
   - A/B testing framework
   - Model monitoring and drift detection
3. Advanced ML features:
   - AutoML capabilities
   - Transfer learning
   - Model interpretability
   - Online learning
   - Distributed training

### Complex Implementation
```java
MLModelFactory factory = MLModelFactory.getInstance();

// Auto-select best model based on data
MLModel model = factory.autoCreateModel(dataset);
model.setHyperparameterTuning(new GridSearch());
model.train(trainingData);

// Create ensemble model
EnsembleModel ensemble = factory.createEnsemble();
ensemble.addModel(factory.createModel("RandomForest"));
ensemble.addModel(factory.createModel("GradientBoosting"));
ensemble.addModel(factory.createModel("NeuralNetwork"));
ensemble.setVotingStrategy(VotingStrategy.WEIGHTED);
ensemble.train(trainingData);

// Deploy with monitoring
ModelDeployment deployment = factory.deployModel(model);
deployment.enableDriftDetection();
deployment.setRetrainingTrigger(DriftThreshold.HIGH);
```

---

## Problem 8: Microservice Factory (Expert)
**Difficulty:** ⭐⭐⭐⭐⭐

### Problem Statement
Design a microservice factory that can create and deploy different types of microservices:
- **REST API**: HTTP-based services
- **GraphQL**: Query-based API services
- **gRPC**: High-performance RPC services
- **WebSocket**: Real-time communication services
- **Event-Driven**: Message queue-based services

### Requirements
1. Create comprehensive service factory:
   - Service scaffolding
   - Dependency injection
   - Service discovery
   - Load balancing
   - Circuit breakers
   - Rate limiting
   - Authentication/Authorization
2. Implement deployment strategies:
   - Blue-green deployment
   - Canary deployment
   - Rolling updates
   - Feature flags
3. Service mesh integration:
   - Istio/Linkerd configuration
   - Observability (metrics, logs, traces)
   - Security policies
   - Traffic management

### Expert Features
- Service composition
- Saga pattern implementation
- CQRS/Event Sourcing
- Multi-tenancy support
- Service contracts and versioning

---

## Solution Structure Template

For each problem, structure your solution as follows:

```java
// 1. Define interfaces
public interface Product {
    // Core methods
}

// 2. Implement concrete products
public class ConcreteProductA implements Product {
    // Implementation
}

// 3. Create factory interface/abstract class
public interface Factory {
    Product createProduct(String type);
}

// 4. Implement concrete factories
public class ConcreteFactory implements Factory {
    @Override
    public Product createProduct(String type) {
        // Factory logic
    }
}

// 5. Add supporting classes
public class ProductRegistry {
    // Product registration and management
}

// 6. Client usage
public class Client {
    public static void main(String[] args) {
        Factory factory = new ConcreteFactory();
        Product product = factory.createProduct("TypeA");
        // Use product
    }
}
```

---

## Evaluation Criteria

Your solutions will be evaluated based on:

1. **Correct Implementation** (40%)
   - Proper use of Factory Pattern
   - All requirements met
   - Working code

2. **Design Quality** (25%)
   - SOLID principles adherence
   - Clean architecture
   - Appropriate pattern selection

3. **Extensibility** (20%)
   - Easy to add new products
   - Flexible factory configuration
   - Maintainable code

4. **Error Handling** (10%)
   - Graceful error handling
   - Input validation
   - Edge case handling

5. **Testing** (5%)
   - Unit tests for factories
   - Integration tests
   - Test coverage

---

## Tips for Success

1. **Start Simple**: Begin with basic factory implementation, then add complexity
2. **Consider Context**: Choose appropriate factory variant based on requirements
3. **Think Extensibility**: Design for future product additions
4. **Error Handling**: Handle unknown product types gracefully
5. **Configuration**: Consider external configuration for factory behavior
6. **Documentation**: Document factory methods and product types
7. **Testing**: Write comprehensive tests for each factory path
8. **Performance**: Consider caching and lazy initialization where appropriate
9. **Thread Safety**: Ensure factories are thread-safe if needed
10. **Clean Code**: Follow naming conventions and keep methods focused

---

## Bonus Challenges

After completing the main problems, try these additional challenges:

1. **Factory Registry**: Implement a registry system for dynamic factory registration
2. **Plugin System**: Create a plugin-based factory that loads products dynamically
3. **Factory Chain**: Implement chain of responsibility with factories
4. **Async Factory**: Create factories that return CompletableFuture/Promise
5. **Factory Pool**: Implement object pooling with factory pattern
6. **Reflection Factory**: Use reflection for automatic product discovery
7. **Annotation-Based Factory**: Use annotations to mark and create products
8. **Factory Metrics**: Add metrics collection to track factory usage

---

## Resources for Learning

- Design Patterns: Elements of Reusable Object-Oriented Software (Gang of Four)
- Head First Design Patterns
- Effective Java by Joshua Bloch
- Clean Code by Robert C. Martin
- Refactoring to Patterns by Joshua Kerievsky

Good luck with your practice! Remember, mastering design patterns takes time and practice. Start with simpler problems and gradually move to more complex ones.