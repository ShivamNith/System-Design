# Observer Pattern - Practice Problems

## Problem 1: Basic Newsletter Subscription (Beginner)
**Difficulty: ⭐**

Create a simple newsletter subscription system where:
- A `Newsletter` class can have multiple `Subscriber` objects
- When a new article is published, all subscribers are notified
- Subscribers should receive the article title and content
- Implement methods to add/remove subscribers

**Requirements:**
- `Newsletter` class (Subject)
- `Subscriber` interface (Observer)
- `EmailSubscriber` class that prints received articles
- Test with at least 3 subscribers

**Key Learning Points:**
- Basic Observer pattern implementation
- Subject-Observer relationship
- Simple notification mechanism

---

## Problem 2: Smart Home Temperature System (Beginner-Intermediate)
**Difficulty: ⭐⭐**

Design a smart home temperature monitoring system:
- A `TemperatureSensor` monitors temperature changes
- Multiple devices react to temperature changes: `Thermostat`, `Fan`, `Heater`
- Each device has different temperature thresholds for activation
- Include a `Display` that shows current temperature

**Requirements:**
- Implement temperature change notifications
- Each device should have configurable thresholds
- Devices should turn on/off based on temperature
- Handle both temperature increase and decrease scenarios

**Key Learning Points:**
- Multiple observers with different behaviors
- Conditional observer reactions
- State management in observers

---

## Problem 3: Chat Room with Multiple Channels (Intermediate)
**Difficulty: ⭐⭐⭐**

Create a chat room system with the following features:
- Multiple chat channels (e.g., "general", "tech", "sports")
- Users can join/leave specific channels
- When a message is posted in a channel, only users in that channel are notified
- Support different user types: `RegularUser`, `Moderator`, `Admin`
- Moderators receive all messages for monitoring

**Requirements:**
- `ChatRoom` class managing multiple channels
- `Channel` class for individual chat channels
- Different user types with different notification behaviors
- Message filtering and routing
- User status (online/offline) affects notifications

**Key Learning Points:**
- Multiple subjects (channels)
- Conditional notifications
- Observer categorization
- Complex subscription management

---

## Problem 4: Model-View-Controller with Multiple Views (Intermediate)
**Difficulty: ⭐⭐⭐**

Implement an MVC pattern for a simple calculator application:
- `CalculatorModel` maintains the current value and operations
- Multiple views: `ConsoleView`, `GraphicalView`, `HistoryView`
- Views should update automatically when the model changes
- Support operations: add, subtract, multiply, divide, clear
- Include undo/redo functionality

**Requirements:**
- Model notifies views of state changes
- Different views display information differently
- History view maintains operation log
- Error handling for invalid operations
- Views can be added/removed dynamically

**Key Learning Points:**
- MVC architecture with Observer pattern
- Multiple view types
- State change notifications
- Error propagation

---

## Problem 5: Auction System with Real-time Bidding (Intermediate-Advanced)
**Difficulty: ⭐⭐⭐⭐**

Design an online auction system:
- `Auction` items with bidding periods
- `Bidder` objects that can place bids
- Real-time notifications for: new bids, auction ending soon, auction ended
- Different notification preferences for each bidder
- Auction history and bid tracking
- Support for proxy bidding (automatic bidding up to a maximum)

**Requirements:**
- Time-based auction management
- Bid validation and history
- Multiple notification types
- Bidder preferences and filtering
- Automatic proxy bidding system
- Winner determination and notification

**Key Learning Points:**
- Time-based event systems
- Complex business logic in observers
- Event filtering and preferences
- Automated responses
- State-dependent behavior

---

## Problem 6: Stock Portfolio with Advanced Analytics (Advanced)
**Difficulty: ⭐⭐⭐⭐**

Create a comprehensive stock portfolio management system:
- Multiple `Stock` objects with real-time price updates
- `Portfolio` tracking multiple stocks with quantities
- Analytics engines: `MovingAverageCalculator`, `VolatilityAnalyzer`, `TrendAnalyzer`
- Alert systems: price alerts, percentage change alerts, volume alerts
- Performance tracking and reporting
- Support for different asset types (stocks, bonds, crypto)

**Requirements:**
- Complex data structures for portfolio management
- Multiple analytics running simultaneously
- Configurable alert thresholds
- Historical data management
- Performance metrics calculation
- Thread-safe operations for real-time updates

**Key Learning Points:**
- Complex observer hierarchies
- Performance optimization
- Thread safety
- Data analytics integration
- Real-time processing

---

## Problem 7: Distributed Event System (Advanced)
**Difficulty: ⭐⭐⭐⭐⭐**

Design a distributed event system for microservices:
- `EventBus` that can handle different event types
- Remote observers that can subscribe to events over network
- Event persistence and replay capability
- Load balancing for event distribution
- Failure handling and retry mechanisms
- Event ordering and delivery guarantees

**Requirements:**
- Generic event system supporting any event type
- Network communication for remote observers
- Event persistence (database/file)
- Retry logic for failed deliveries
- Event ordering preservation
- Monitoring and metrics collection
- Circuit breaker pattern for resilience

**Key Learning Points:**
- Distributed systems design
- Network communication
- Persistence strategies
- Error handling and resilience
- Performance monitoring
- Scalability considerations

---

## Problem 8: Game Engine Event System (Expert)
**Difficulty: ⭐⭐⭐⭐⭐**

Create a comprehensive game engine event system:
- Multiple event types: `PlayerEvent`, `GameStateEvent`, `CollisionEvent`, `InputEvent`
- Entity-Component-System architecture integration
- Priority-based event processing
- Event bubbling and capturing phases
- Event filtering by spatial regions
- Performance optimization for high-frequency events
- Debug and profiling tools

**Requirements:**
- High-performance event processing (handle thousands of events per second)
- Spatial event filtering (only notify nearby entities)
- Event priority queues
- Memory pool management for events
- Integration with game loop timing
- Comprehensive debugging and profiling
- Support for event recording and playback
- Custom event serialization

**Advanced Features:**
- Event compression for network transmission
- Predictive event processing
- Event interpolation and extrapolation
- Custom event scripting language
- Visual event debugging tools

**Key Learning Points:**
- High-performance system design
- Memory management optimization
- Spatial algorithms
- Real-time constraints
- Profiling and debugging
- Domain-specific optimizations

---

## Implementation Guidelines

### For All Problems:
1. **Start with interfaces** - Define Observer and Subject interfaces first
2. **Keep it simple** - Don't over-engineer the initial solution
3. **Add features incrementally** - Build basic functionality first, then add complexity
4. **Test thoroughly** - Create comprehensive test cases
5. **Consider edge cases** - Handle null values, empty lists, concurrent access
6. **Document your code** - Include clear comments and documentation

### Testing Strategies:
1. **Unit tests** for individual components
2. **Integration tests** for Observer-Subject interactions
3. **Performance tests** for high-frequency scenarios
4. **Stress tests** for large numbers of observers
5. **Concurrency tests** for multi-threaded scenarios

### Common Patterns to Implement:
1. **WeakReference observers** to prevent memory leaks
2. **Event filtering** based on observer preferences
3. **Asynchronous notifications** for better performance
4. **Event batching** for efficiency
5. **Error isolation** to prevent cascading failures

### Extension Ideas:
- Add configuration files for observer settings
- Implement event persistence and replay
- Create web interfaces for monitoring
- Add network communication for distributed scenarios
- Implement event aggregation and analytics
- Create domain-specific event languages

## Solution Approach Tips

### Problem Analysis:
1. **Identify the Subject** - What object changes state?
2. **Identify the Observers** - What objects need to be notified?
3. **Define the Event** - What information needs to be passed?
4. **Consider the Lifecycle** - When are observers added/removed?
5. **Think about Edge Cases** - What can go wrong?

### Design Considerations:
1. **Loose Coupling** - Subjects shouldn't know observer details
2. **Flexibility** - Easy to add new observer types
3. **Performance** - Efficient notification mechanisms
4. **Thread Safety** - Handle concurrent access properly
5. **Error Handling** - Graceful degradation when observers fail

### Implementation Best Practices:
1. Use **generic types** for type safety
2. Implement **proper equals/hashCode** for observer collections
3. Consider **CopyOnWriteArrayList** for thread-safe observer lists
4. Use **try-catch blocks** to isolate observer failures
5. Implement **toString methods** for better debugging

These problems progress from basic Observer pattern implementation to complex, real-world distributed systems. Each problem builds upon concepts from previous problems while introducing new challenges and requirements.