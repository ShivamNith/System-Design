# Singleton Pattern - Practice Problems

## Overview
This document contains 8 practice problems of varying difficulty levels to help you master the Singleton Design Pattern. Each problem builds upon the previous concepts and introduces new challenges.

## Problems

### Problem 1: Basic Singleton (Beginner)
**Difficulty**: ⭐ Beginner

**Description**: 
Implement a basic Singleton class called `PrinterManager` that manages access to a single printer resource. The class should have:
- A private constructor
- A static method `getInstance()` to get the singleton instance
- A method `print(String document)` that simulates printing
- A counter to track total documents printed

**Requirements**:
- Use eager initialization
- Thread safety is not required for this basic version
- Include a method to get the total print count

**Expected Output**:
```
Printer Manager initialized
Printing document: Hello World
Printing document: Test Document
Total documents printed: 2
```

**Sample Usage**:
```java
PrinterManager printer = PrinterManager.getInstance();
printer.print("Hello World");
printer.print("Test Document");
System.out.println("Total documents: " + printer.getTotalPrintCount());
```

---

### Problem 2: Thread-Safe Singleton (Beginner-Intermediate)
**Difficulty**: ⭐⭐ Beginner-Intermediate

**Description**:
Implement a thread-safe `CacheManager` singleton that stores key-value pairs. The class should handle concurrent access from multiple threads safely.

**Requirements**:
- Use double-checked locking pattern
- Implement methods: `put(String key, Object value)`, `get(String key)`, `remove(String key)`
- Include a method to get cache size
- Use `ConcurrentHashMap` for internal storage
- Handle null keys and values appropriately

**Test Scenario**:
- Create 5 threads that simultaneously add, retrieve, and remove cache entries
- Verify that all operations are thread-safe and no data is lost

**Expected Behavior**:
- All threads should get the same CacheManager instance
- No data corruption should occur during concurrent operations
- Cache operations should be atomic and consistent

---

### Problem 3: Serialization-Safe Singleton (Intermediate)
**Difficulty**: ⭐⭐⭐ Intermediate

**Description**:
Create a `SessionManager` singleton that maintains user session information and is safe from serialization attacks. The singleton should survive serialization and deserialization without creating multiple instances.

**Requirements**:
- Implement `Serializable` interface
- Override `readResolve()` method to maintain singleton property
- Store user sessions with timeout functionality
- Include methods: `createSession(String userId)`, `getSession(String sessionId)`, `invalidateSession(String sessionId)`
- Implement automatic session cleanup for expired sessions

**Challenge**:
- Serialize the singleton to a file
- Deserialize it back and verify it's the same instance
- Ensure no duplicate instances are created during the process

**Additional Features**:
- Session timeout mechanism
- Session statistics (active sessions, total created, expired)

---

### Problem 4: Enum-Based Singleton with State Management (Intermediate)
**Difficulty**: ⭐⭐⭐ Intermediate

**Description**:
Implement an `ApplicationStateManager` using enum-based singleton pattern. This manager should handle application state transitions and maintain global application configuration.

**Requirements**:
- Use enum singleton approach
- Define application states: `INITIALIZING`, `RUNNING`, `PAUSED`, `SHUTTING_DOWN`, `STOPPED`
- Implement state transition validation (define which transitions are valid)
- Store and manage application configuration properties
- Provide thread-safe state change notifications to observers

**State Transition Rules**:
- `INITIALIZING` → `RUNNING` or `STOPPED`
- `RUNNING` → `PAUSED` or `SHUTTING_DOWN`
- `PAUSED` → `RUNNING` or `SHUTTING_DOWN`
- `SHUTTING_DOWN` → `STOPPED`
- `STOPPED` → `INITIALIZING`

**Features to Implement**:
- Observer pattern for state change notifications
- Configuration property management
- State transition history logging
- Invalid transition error handling

---

### Problem 5: Registry Pattern with Singleton (Intermediate-Advanced)
**Difficulty**: ⭐⭐⭐⭐ Intermediate-Advanced

**Description**:
Create a `ServiceRegistry` singleton that manages service instances throughout the application. It should support service registration, lookup, and lifecycle management.

**Requirements**:
- Implement service registration with interface-based lookup
- Support singleton and prototype service scopes
- Implement dependency injection for services
- Handle circular dependency detection
- Provide service lifecycle callbacks (`@PostConstruct`, `@PreDestroy`)
- Thread-safe service creation and management

**Service Interface**:
```java
public interface Service {
    void initialize();
    void shutdown();
    String getServiceName();
}
```

**Features**:
- Register services by interface type
- Automatic dependency resolution
- Lazy vs eager service initialization
- Service health monitoring
- Service replacement and versioning

**Challenge**:
Implement a complex scenario with multiple services having dependencies on each other, and demonstrate proper initialization order and dependency resolution.

---

### Problem 6: Connection Pool Manager (Advanced)
**Difficulty**: ⭐⭐⭐⭐ Advanced

**Description**:
Design a sophisticated `ConnectionPoolManager` singleton that manages multiple types of connections (Database, Redis, HTTP) with different pool configurations and monitoring capabilities.

**Requirements**:
- Support multiple connection types with different pool configurations
- Implement connection health checking and automatic recovery
- Provide detailed monitoring and metrics
- Handle connection timeouts and retry mechanisms
- Implement connection pool scaling (dynamic resize based on load)
- Support multiple database vendors/connection types

**Advanced Features**:
- Connection leak detection
- Pool statistics and performance metrics
- Circuit breaker pattern for failed connections
- Connection validation before use
- Graceful shutdown with connection draining
- JMX monitoring support

**Monitoring Requirements**:
- Track connection usage patterns
- Monitor connection creation/destruction rates
- Alert on connection pool exhaustion
- Provide real-time pool health status

---

### Problem 7: Multi-Tenant Configuration Manager (Advanced)
**Difficulty**: ⭐⭐⭐⭐⭐ Advanced

**Description**:
Implement a `MultiTenantConfigManager` singleton that handles configurations for multiple tenants in a SaaS application. Each tenant should have isolated configuration while sharing the same manager instance.

**Requirements**:
- Tenant-specific configuration isolation
- Configuration inheritance (global → tenant → user level)
- Real-time configuration updates without restart
- Configuration versioning and rollback capabilities
- Encrypted configuration storage for sensitive data
- Configuration audit logging

**Complex Scenarios**:
- Handle configuration conflicts and resolution strategies
- Implement configuration migration between versions
- Support configuration templates and profiles
- Dynamic tenant onboarding/offboarding
- Configuration backup and disaster recovery

**Security Requirements**:
- Encrypt sensitive configuration values
- Implement configuration access control
- Audit all configuration changes
- Support configuration signing and verification

**Performance Considerations**:
- Cache frequently accessed configurations
- Minimize database queries for configuration lookups
- Handle high-concurrency configuration access
- Optimize memory usage for large numbers of tenants

---

### Problem 8: Distributed Singleton with Leader Election (Expert)
**Difficulty**: ⭐⭐⭐⭐⭐ Expert

**Description**:
Create a `DistributedJobScheduler` that maintains singleton behavior across multiple JVM instances using leader election. Only one instance should be active at a time across the entire cluster.

**Requirements**:
- Implement leader election using a distributed coordination service (simulate with file locking or database)
- Handle leader failover and recovery
- Maintain job scheduling consistency across the cluster
- Implement heartbeat mechanism for leader health checking
- Handle split-brain scenarios gracefully

**Distributed Challenges**:
- Network partitions and recovery
- Node failures and automatic failover
- Consistent job execution across cluster
- Distributed state synchronization
- Cluster membership management

**Advanced Features**:
- Job queue persistence and recovery
- Distributed job execution coordination
- Load balancing across available nodes
- Real-time cluster health monitoring
- Automatic leader re-election on failure

**Implementation Considerations**:
- Use consensus algorithms (simplified Raft-like approach)
- Implement proper timeout and retry mechanisms
- Handle concurrent leader election attempts
- Ensure data consistency during leadership transitions
- Provide cluster status and monitoring APIs

**Testing Requirements**:
- Simulate network failures
- Test leader election under various failure scenarios
- Verify job execution continuity during failovers
- Test performance under high concurrency
- Validate data consistency across all cluster states

---

## Evaluation Criteria

For each problem, consider the following evaluation criteria:

### Correctness ✅
- Singleton property is maintained (only one instance)
- Thread safety (where required)
- Proper error handling
- Edge cases handled correctly

### Design Quality 🏗️
- Appropriate singleton implementation pattern chosen
- Clean separation of concerns
- Proper encapsulation and abstraction
- Extensible and maintainable code

### Performance 🚀
- Efficient memory usage
- Minimal synchronization overhead
- Scalable under load
- Appropriate use of caching and optimization

### Testing 🧪
- Comprehensive unit tests
- Thread safety verification
- Performance benchmarks (for advanced problems)
- Integration testing (for complex scenarios)

### Documentation 📝
- Clear code comments
- API documentation
- Usage examples
- Design decision rationale

## Solutions Approach

### For Beginners (Problems 1-2):
- Focus on understanding the basic singleton concept
- Practice different implementation approaches
- Learn about thread safety fundamentals

### For Intermediate (Problems 3-4):
- Explore advanced singleton concerns (serialization, enum approach)
- Understand design patterns integration
- Practice state management and validation

### For Advanced (Problems 5-6):
- Design complex systems using singleton pattern
- Consider performance and scalability aspects
- Implement sophisticated monitoring and management features

### For Experts (Problems 7-8):
- Handle enterprise-level complexity
- Consider distributed system challenges
- Implement advanced algorithms and patterns
- Focus on reliability and fault tolerance

## Additional Challenges

Once you've completed the main problems, try these additional challenges:

1. **Performance Benchmark**: Compare the performance of different singleton implementations under high concurrency
2. **Memory Analysis**: Analyze memory usage patterns of different singleton approaches
3. **Reflection Attack Prevention**: Implement protection against reflection-based attacks
4. **Spring Integration**: Integrate your singleton patterns with Spring Framework
5. **Microservices**: Adapt singleton patterns for microservices architecture

## Common Pitfalls to Avoid

1. **Lazy Initialization Race Conditions**: Always consider thread safety in lazy initialization
2. **Serialization Vulnerabilities**: Don't forget to implement `readResolve()`  
3. **Reflection Attacks**: Protect constructor from reflection-based instantiation
4. **Cloning Issues**: Override `clone()` method to prevent cloning
5. **Testing Difficulties**: Design for testability from the beginning
6. **Global State Problems**: Use singleton judiciously to avoid tight coupling
7. **Memory Leaks**: Ensure proper cleanup in singleton instances
8. **ClassLoader Issues**: Be aware of singleton behavior with multiple classloaders

Remember: The Singleton pattern should be used sparingly and only when you truly need exactly one instance of a class. In many cases, dependency injection frameworks provide better alternatives while maintaining the benefits of single instances.