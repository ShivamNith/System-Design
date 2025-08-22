# Decorator Pattern - Practice Problems

## Problem 1: Basic Implementation (Beginner)
**Description:** Create a simple pizza ordering system using the Decorator Pattern.

**Requirements:**
- Create a `Pizza` interface with methods: `getDescription()`, `getCost()`
- Implement `BasicPizza` class as the base component
- Create decorators for toppings: `CheeseDecorator`, `PepperoniDecorator`, `MushroomDecorator`
- Each topping should add to both description and cost

**Example Usage:**
```java
Pizza pizza = new BasicPizza();
pizza = new CheeseDecorator(pizza);
pizza = new PepperoniDecorator(pizza);
System.out.println(pizza.getDescription()); // "Basic Pizza, Cheese, Pepperoni"
System.out.println(pizza.getCost()); // 15.50
```

**Skills Practiced:**
- Basic Decorator Pattern structure
- Interface implementation
- Simple composition
- Cost and description aggregation

---

## Problem 2: I/O Stream Processing (Beginner-Intermediate)
**Description:** Design a data stream processing system using the Decorator Pattern, similar to Java I/O streams.

**Requirements:**
- Create `DataStream` interface with methods: `read()`, `write(String data)`, `close()`
- Implement `FileDataStream` as base component
- Create decorators: `BufferedDecorator`, `CompressedDecorator`, `EncryptedDecorator`
- Each decorator should enhance the functionality while maintaining the same interface
- Handle proper resource cleanup in decorators

**Features to Include:**
- Buffering for performance optimization
- Compression for space efficiency
- Encryption for security
- Proper error handling
- Resource management (auto-closeable)

**Skills Practiced:**
- Resource management
- Performance optimization
- Security considerations
- Error handling in decorators

---

## Problem 3: Web Request Processing Middleware (Intermediate)
**Description:** Build a web request processing pipeline using the Decorator Pattern.

**Requirements:**
- Create `RequestHandler` interface with `handle(Request request, Response response)`
- Implement base `ApplicationHandler`
- Create middleware decorators: `AuthenticationDecorator`, `LoggingDecorator`, `RateLimitingDecorator`, `CachingDecorator`
- Support request/response modification
- Include proper error handling and short-circuiting

**Advanced Features:**
- Request/response transformation
- Conditional processing based on request properties
- Performance metrics collection
- Circuit breaker pattern integration
- Request tracing and correlation IDs

**Skills Practiced:**
- Web middleware patterns
- Request/response processing
- Performance monitoring
- Security implementation
- Complex pipeline composition

---

## Problem 4: Graphics Drawing System (Intermediate)
**Description:** Create a graphics drawing system using the Decorator Pattern for visual effects.

**Requirements:**
- Create `Drawable` interface with methods: `draw()`, `getBounds()`, `setPosition(x, y)`
- Implement basic shapes: `Circle`, `Rectangle`, `Text`
- Create visual effect decorators: `ShadowDecorator`, `BorderDecorator`, `ColorDecorator`, `BlurDecorator`
- Support transformation decorators: `ScaleDecorator`, `RotateDecorator`, `TranslateDecorator`

**Advanced Features:**
- Animation decorators (fade in/out, pulse, rotate)
- Composite effects (multiple decorators on same object)
- Performance optimization for complex effect chains
- Export to different formats (SVG, Canvas, etc.)
- Layer management and z-ordering

**Skills Practiced:**
- Graphics programming concepts
- Transformation matrices
- Complex decorator chains
- Performance optimization
- Visual effect composition

---

## Problem 5: Database Query Builder (Intermediate-Advanced)
**Description:** Design a flexible database query builder using the Decorator Pattern.

**Requirements:**
- Create `Query` interface with methods: `execute()`, `toSQL()`, `getParameters()`
- Implement base queries: `SelectQuery`, `InsertQuery`, `UpdateQuery`, `DeleteQuery`
- Create condition decorators: `WhereDecorator`, `JoinDecorator`, `OrderByDecorator`, `GroupByDecorator`
- Support query optimization decorators: `CacheDecorator`, `IndexHintDecorator`

**Advanced Features:**
- Subquery support
- Complex join types (INNER, LEFT, RIGHT, FULL OUTER)
- Aggregate functions and window functions
- Query plan optimization
- Connection pool management
- Transaction support
- Prepared statement optimization

**Skills Practiced:**
- SQL query construction
- Database optimization techniques
- Complex condition chaining
- Performance monitoring
- Connection management

---

## Problem 6: Event Processing System (Advanced)
**Description:** Build a real-time event processing system using the Decorator Pattern.

**Requirements:**
- Create `EventProcessor` interface with `process(Event event)`
- Implement base processors for different event types
- Create processing decorators: `FilterDecorator`, `TransformDecorator`, `AggregateDecorator`, `RouteDecorator`
- Support batch processing and stream processing modes
- Include error recovery and dead letter queue handling

**Advanced Features:**
- Event sourcing integration
- Complex event pattern detection
- Time window operations
- Backpressure handling
- Distributed processing
- Event replay capabilities
- Schema evolution support

**Skills Practiced:**
- Event-driven architecture
- Stream processing concepts
- Distributed systems
- Performance tuning
- Error recovery strategies

---

## Problem 7: Machine Learning Pipeline (Advanced)
**Description:** Create a machine learning data processing pipeline using the Decorator Pattern.

**Requirements:**
- Create `DataProcessor` interface with `transform(Dataset data)`
- Implement base processors: `DataLoader`, `FeatureExtractor`
- Create preprocessing decorators: `NormalizationDecorator`, `ScalingDecorator`, `EncodingDecorator`
- Add validation decorators: `DataValidationDecorator`, `OutlierDetectionDecorator`
- Support feature engineering decorators: `PolynomialFeaturesDecorator`, `PCADecorator`

**Advanced Features:**
- Pipeline serialization/deserialization
- Hyperparameter tuning integration
- Cross-validation support
- Feature importance analysis
- Model versioning and A/B testing
- GPU acceleration where applicable
- Distributed data processing

**Skills Practiced:**
- Machine learning workflows
- Data preprocessing techniques
- Mathematical transformations
- Performance optimization
- Scientific computing libraries integration

---

## Problem 8: Security Framework (Expert)
**Description:** Design a comprehensive security framework using the Decorator Pattern.

**Requirements:**
- Create `SecurityContext` interface with authentication and authorization methods
- Implement base security providers
- Create security decorators: `EncryptionDecorator`, `AuditDecorator`, `RoleBasedAccessDecorator`
- Support multiple authentication mechanisms
- Include threat detection and prevention

**Advanced Features:**
- Multi-factor authentication
- Single sign-on (SSO) integration
- OAuth/JWT token management
- Rate limiting and DDoS protection
- Intrusion detection system
- Security event correlation
- Compliance reporting (GDPR, HIPAA, etc.)
- Zero-trust security model

**Skills Practiced:**
- Security architecture
- Cryptography implementation
- Authentication protocols
- Authorization models
- Audit and compliance
- Threat modeling

---

## Bonus Challenge: Protocol Stack Implementation (Expert)
**Description:** Implement a network protocol stack using the Decorator Pattern.

**Requirements:**
- Create `NetworkLayer` interface representing each protocol layer
- Implement layers: Physical, Data Link, Network, Transport, Session, Presentation, Application
- Each layer should be a decorator that adds its own headers/processing
- Support multiple protocols at each layer (TCP/UDP, IPv4/IPv6, HTTP/HTTPS)
- Include error detection and correction

**Advanced Features:**
- Protocol negotiation
- Quality of Service (QoS) management
- Network monitoring and diagnostics
- Load balancing and failover
- Security protocol integration (TLS/SSL)
- Performance optimization
- Custom protocol development framework

**This challenge requires:**
- Deep understanding of networking concepts
- Low-level programming
- Protocol design
- Performance optimization
- Security implementation

---

## Tips for Success

### For Beginners:
1. Start with simple decorators that add single features
2. Focus on maintaining the same interface across all decorators
3. Practice proper object composition
4. Understand the difference between inheritance and composition

### For Intermediate:
1. Consider decorator order and dependencies
2. Implement proper error handling in decorator chains
3. Think about performance implications of deep decorator chains
4. Practice combining decorators with other design patterns

### For Advanced:
1. Optimize for specific use cases (memory usage, performance)
2. Implement decorator factories for common combinations
3. Consider thread safety in multi-threaded environments
4. Design for extensibility and maintainability

### Common Pitfalls to Avoid:
1. **Interface Bloat**: Don't add methods to the interface that don't make sense for all components
2. **Deep Nesting**: Avoid creating overly deep decorator chains that hurt performance
3. **Tight Coupling**: Decorators should not depend on specific concrete components
4. **Memory Leaks**: Be careful with decorator chains holding references
5. **Order Dependencies**: Minimize dependencies on decorator ordering

### Testing Strategies:
1. **Unit Testing**: Test each decorator in isolation
2. **Integration Testing**: Test decorator combinations
3. **Performance Testing**: Measure impact of decorator chains
4. **Boundary Testing**: Test with edge cases and invalid inputs
5. **Concurrency Testing**: Test thread safety where applicable

### Best Practices:
1. **Keep Decorators Simple**: Each decorator should have a single responsibility
2. **Use Abstract Decorator Base**: Reduce code duplication with abstract base classes
3. **Document Side Effects**: Clearly document what each decorator does
4. **Provide Unwrapping**: Allow access to wrapped components when needed
5. **Consider Fluent Interface**: Make decorator chaining more readable

### Performance Considerations:
1. **Lazy Evaluation**: Defer expensive operations until needed
2. **Caching**: Cache results of expensive decorator operations
3. **Pool Objects**: Reuse decorator instances where possible
4. **Profile Chains**: Measure performance impact of decorator combinations
5. **Optimize Hot Paths**: Special handling for frequently used decorator chains

### Design Patterns Integration:
- **Factory Pattern**: Create common decorator combinations
- **Builder Pattern**: Construct complex decorator chains
- **Strategy Pattern**: Choose decoration strategies dynamically
- **Observer Pattern**: Notify about decorator state changes
- **Command Pattern**: Encapsulate decorator operations

Remember: The Decorator Pattern is about adding behavior dynamically while maintaining interface compatibility. Focus on creating flexible, composable systems that can be easily extended without modifying existing code.