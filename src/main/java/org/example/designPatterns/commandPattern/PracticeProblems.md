# Command Pattern Practice Problems

This document contains 8 practice problems ranging from basic to expert level to help you master the Command Pattern. Each problem includes a description, requirements, and implementation hints.

## Problem 1: Simple Calculator with Undo (Basic)

**Difficulty**: Basic  
**Estimated Time**: 30-45 minutes

### Description
Create a simple calculator that supports basic arithmetic operations (add, subtract, multiply, divide) with undo functionality.

### Requirements
- Implement commands for each arithmetic operation
- Support undo for the last operation
- Display current result after each operation
- Handle division by zero gracefully

### Implementation Hints
- Create a `Calculator` class as the receiver
- Implement `Command` interface with `execute()` and `undo()` methods
- Store the previous value before each operation for undo
- Use a `CalculatorInvoker` to manage commands

### Expected Output
```
Calculator: 0
Add 10: 10
Multiply 5: 50
Subtract 20: 30
Undo: 50
Divide 2: 25
```

---

## Problem 2: Media Player Control (Basic-Intermediate)

**Difficulty**: Basic-Intermediate  
**Estimated Time**: 45-60 minutes

### Description
Design a media player with remote control functionality using the Command Pattern. Support multiple audio devices.

### Requirements
- Commands: play, pause, stop, next track, previous track, volume up/down
- Support multiple audio devices (speakers, headphones, bluetooth)
- Implement a universal remote that can control any device
- Add macro commands for common scenarios (e.g., "start workout playlist")

### Implementation Hints
- Create separate device classes implementing a common interface
- Each command should work with any device type
- Implement macro commands that combine multiple operations
- Consider device-specific behaviors (e.g., bluetooth pairing)

### Test Scenarios
- Control different devices with the same remote
- Create and execute macro commands
- Handle device-specific limitations

---

## Problem 3: Bank Transaction System (Intermediate)

**Difficulty**: Intermediate  
**Estimated Time**: 60-90 minutes

### Description
Implement a banking system where all transactions are commands that can be logged, queued, and potentially reversed.

### Requirements
- Transaction types: deposit, withdraw, transfer between accounts
- All transactions must be logged with timestamps
- Support for batch transaction processing
- Implement transaction reversal (with proper validation)
- Add transaction limits and validation

### Implementation Hints
- Each transaction should be a command with detailed logging
- Implement a transaction manager to handle queuing and batch processing
- Consider failed transactions and error handling
- Implement proper validation for account balances and limits

### Advanced Features
- Transaction scheduling (future-dated transactions)
- Transaction templates for recurring operations
- Audit trail with detailed transaction history

---

## Problem 4: Drawing Application with Shapes (Intermediate)

**Difficulty**: Intermediate  
**Estimated Time**: 75-90 minutes

### Description
Create a drawing application where each drawing operation is a command that supports undo/redo functionality.

### Requirements
- Drawing commands: draw line, rectangle, circle, polygon
- Each command should store enough information for undo/redo
- Support for changing shape properties (color, line thickness)
- Implement a drawing canvas that maintains shape history
- Add clear canvas and save/load functionality

### Implementation Hints
- Commands should store both the drawing action and the canvas state
- Consider using memento pattern alongside command for complex undo operations
- Implement a command history manager with undo/redo stacks
- Handle canvas boundaries and shape validation

### Bonus Features
- Group/ungroup shapes as macro commands
- Copy/paste operations
- Shape transformation commands (rotate, scale, move)

---

## Problem 5: Workflow Engine (Intermediate-Advanced)

**Difficulty**: Intermediate-Advanced  
**Estimated Time**: 90-120 minutes

### Description
Design a workflow engine where each step in a business process is a command. Support parallel execution, conditional branching, and rollback capabilities.

### Requirements
- Define workflow steps as commands with dependencies
- Support sequential and parallel execution
- Implement conditional commands that execute based on previous results
- Add rollback capability when workflow fails
- Support workflow templates and reusability

### Implementation Hints
- Use composite pattern for workflow containers
- Implement a workflow executor that manages command dependencies
- Consider using observer pattern for workflow status updates
- Design proper error handling and rollback mechanisms

### Advanced Features
- Workflow persistence and resume capability
- Dynamic workflow modification during execution
- Performance monitoring and optimization
- Integration with external systems

---

## Problem 6: Game Command System (Advanced)

**Difficulty**: Advanced  
**Estimated Time**: 120-150 minutes

### Description
Implement a comprehensive command system for a turn-based strategy game with complex interactions, undo/redo, replay functionality, and AI integration.

### Requirements
- Game commands: move unit, attack, cast spell, build structure
- Support for complex command combinations and interactions
- Implement game state validation and command legality checking
- Add replay system for entire game sessions
- Support for AI decision making using command evaluation

### Implementation Hints
- Commands should validate game state before execution
- Implement command chains for complex multi-step actions
- Design a game state manager that works with the command system
- Consider command priority and conflict resolution

### Advanced Features
- Network multiplayer support with command synchronization
- Command compression for network transmission
- Save/load game states with command history
- Performance optimization for large command histories

### Game Elements to Consider
- Unit movement with pathfinding
- Combat resolution with multiple participants
- Resource management and constraints
- Turn-based mechanics with time limits

---

## Problem 7: Distributed System Command Queue (Advanced)

**Difficulty**: Advanced  
**Estimated Time**: 150-180 minutes

### Description
Design a distributed command processing system that can handle high-throughput command execution across multiple nodes with fault tolerance and consistency guarantees.

### Requirements
- Distributed command queue with load balancing
- Command routing based on data partitioning
- Implement command acknowledgment and retry mechanisms
- Support for transaction-like command groups
- Add monitoring and metrics collection

### Implementation Hints
- Design commands to be serializable for network transmission
- Implement command versioning for system evolution
- Consider eventual consistency vs strong consistency requirements
- Design proper failure detection and recovery mechanisms

### Advanced Features
- Command deduplication to handle network retries
- Priority-based command scheduling
- Dynamic load balancing and node scaling
- Command result caching and optimization

### Technical Considerations
- Network partitioning and split-brain scenarios
- Command ordering guarantees
- Performance under high load
- Data consistency across nodes

---

## Problem 8: Enterprise Integration Command Bus (Expert)

**Difficulty**: Expert  
**Estimated Time**: 180-240 minutes

### Description
Build an enterprise-grade command bus system that integrates multiple systems, handles complex business rules, supports multiple execution contexts, and provides comprehensive monitoring and audit capabilities.

### Requirements
- Command routing to appropriate handlers based on business rules
- Support for synchronous and asynchronous command execution
- Implement command transformation and enrichment pipelines
- Add comprehensive security and authorization
- Support for multiple data formats and protocols

### Implementation Hints
- Design a plugin architecture for command handlers
- Implement command middleware pipeline for cross-cutting concerns
- Consider using dependency injection for handler registration
- Design proper abstraction layers for different execution contexts

### Enterprise Features
- Integration with message queues (RabbitMQ, Apache Kafka)
- Support for REST, SOAP, and GraphQL interfaces
- Command throttling and rate limiting
- Comprehensive logging and audit trails
- Performance monitoring and alerting

### Advanced Scenarios
- Command transformation between different system formats
- Saga pattern implementation for long-running transactions
- Event sourcing integration with command processing
- Multi-tenant command isolation and security

### Architecture Considerations
- Microservices integration patterns
- Circuit breaker and bulkhead patterns
- Command versioning and backward compatibility
- Horizontal scaling and performance optimization

---

## Problem Progression Guide

### For Beginners (Problems 1-2)
- Focus on understanding the basic command structure
- Practice implementing simple undo functionality
- Learn about receiver and invoker relationships

### For Intermediate Developers (Problems 3-5)
- Explore complex command interactions
- Practice implementing macro and composite commands
- Learn about command queuing and batch processing

### For Advanced Developers (Problems 6-8)
- Focus on performance and scalability concerns
- Practice implementing complex business logic with commands
- Learn about distributed systems and enterprise patterns

## Implementation Tips

### General Guidelines
1. **Start Simple**: Begin with basic command structure and gradually add complexity
2. **Test Thoroughly**: Write comprehensive tests for command execution and undo operations
3. **Consider Performance**: Think about memory usage and execution time for command histories
4. **Handle Errors**: Implement proper error handling and validation
5. **Document Well**: Maintain clear documentation for complex command interactions

### Design Patterns to Consider
- **Composite Pattern**: For macro commands and command hierarchies
- **Observer Pattern**: For command execution notifications
- **Strategy Pattern**: For different command execution strategies
- **Factory Pattern**: For command creation and registration
- **Memento Pattern**: For complex state management in undo operations

### Performance Considerations
- Command object pooling for high-frequency operations
- Lazy loading for command parameters
- Asynchronous execution for time-consuming commands
- Memory management for large command histories

### Testing Strategies
- Unit tests for individual commands
- Integration tests for command sequences
- Performance tests for high-load scenarios
- Chaos testing for distributed systems (advanced problems)

## Additional Resources

### Recommended Reading
- "Design Patterns" by Gang of Four (Command Pattern chapter)
- "Enterprise Integration Patterns" by Hohpe and Woolf
- "Building Event-Driven Microservices" by Bellemare

### Online Resources
- Command Pattern examples and variations
- Message queue and event sourcing documentation
- Distributed systems design patterns

### Tools and Frameworks
- Spring Framework (for dependency injection)
- Apache Kafka (for distributed messaging)
- Redis (for command queuing)
- Micrometer (for metrics collection)

Remember: The key to mastering the Command Pattern is understanding when and why to use it, not just how to implement it. Focus on the problems these exercises solve and how the pattern provides elegant solutions to complex requirements.