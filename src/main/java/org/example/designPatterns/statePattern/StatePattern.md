# State Pattern

## Table of Contents
1. [Overview](#overview)
2. [Problem Statement](#problem-statement)
3. [Solution](#solution)
4. [UML Diagram](#uml-diagram)
5. [Implementation](#implementation)
6. [Key Components](#key-components)
7. [Benefits](#benefits)
8. [Drawbacks](#drawbacks)
9. [When to Use](#when-to-use)
10. [When Not to Use](#when-not-to-use)
11. [Real-World Examples](#real-world-examples)
12. [Best Practices](#best-practices)
13. [Common Pitfalls](#common-pitfalls)
14. [Related Patterns](#related-patterns)

## Overview

The State Pattern is a behavioral design pattern that allows an object to alter its behavior when its internal state changes. The object will appear to change its class. This pattern is particularly useful when you have an object that needs to change its behavior based on its internal state, and you want to avoid large conditional statements.

**Intent**: Allow an object to alter its behavior when its internal state changes. The object will appear to change its class.

**Also Known As**: Objects for States

## Problem Statement

Consider scenarios where:
- An object's behavior depends on its state
- Operations have large, multipart conditional statements that depend on the object's state
- State-specific behavior is scattered across multiple operations
- You need to avoid duplicate code across similar states

### Example Problem
```java
public class VendingMachine {
    private static final int NO_QUARTER = 0;
    private static final int HAS_QUARTER = 1;
    private static final int SOLD = 2;
    private static final int SOLD_OUT = 3;
    
    private int state = SOLD_OUT;
    private int count = 0;
    
    public void insertQuarter() {
        if (state == HAS_QUARTER) {
            System.out.println("You can't insert another quarter");
        } else if (state == NO_QUARTER) {
            state = HAS_QUARTER;
            System.out.println("You inserted a quarter");
        } else if (state == SOLD_OUT) {
            System.out.println("Machine is sold out");
        } else if (state == SOLD) {
            System.out.println("Please wait, we're already giving you a gum");
        }
    }
    
    public void ejectQuarter() {
        // More conditional logic...
    }
    
    // More methods with similar conditional structures
}
```

**Problems with this approach:**
- Difficult to add new states
- Conditional logic scattered across methods
- Violates Open-Closed Principle
- Hard to maintain and understand

## Solution

The State Pattern encapsulates state-specific behavior into separate classes and delegates state-specific requests to the current state object.

### Key Concepts
1. **Context**: Maintains a reference to a concrete state object and delegates state-specific requests to it
2. **State Interface**: Defines methods that concrete states must implement
3. **Concrete States**: Implement state-specific behavior and handle transitions

## UML Diagram

```
    ┌─────────────────┐
    │    Context      │
    ├─────────────────┤
    │ -state: State   │
    ├─────────────────┤
    │ +request()      │
    │ +setState()     │
    └─────────────────┘
            │
            │ delegates to
            ▼
    ┌─────────────────┐
    │    <<State>>    │
    ├─────────────────┤
    │ +handle()       │
    └─────────────────┘
            △
            │
    ┌───────┴───────┐
    │               │
┌───▼────┐    ┌────▼────┐
│State A │    │ State B │
├────────┤    ├─────────┤
│+handle()│    │+handle()│
└────────┘    └─────────┘
```

## Implementation

### Basic State Pattern Structure

```java
// State interface
public interface State {
    void handle(Context context);
}

// Context class
public class Context {
    private State state;
    
    public Context(State state) {
        this.state = state;
    }
    
    public void setState(State state) {
        this.state = state;
    }
    
    public void request() {
        state.handle(this);
    }
}

// Concrete states
public class ConcreteStateA implements State {
    @Override
    public void handle(Context context) {
        System.out.println("Handling request in State A");
        // Transition to State B
        context.setState(new ConcreteStateB());
    }
}

public class ConcreteStateB implements State {
    @Override
    public void handle(Context context) {
        System.out.println("Handling request in State B");
        // Transition to State A
        context.setState(new ConcreteStateA());
    }
}
```

### Advanced State Pattern with State Factory

```java
// State factory for managing state instances
public class StateFactory {
    private static final Map<Class<? extends State>, State> states = new HashMap<>();
    
    @SuppressWarnings("unchecked")
    public static <T extends State> T getState(Class<T> stateClass) {
        return (T) states.computeIfAbsent(stateClass, k -> {
            try {
                return k.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                throw new RuntimeException("Failed to create state instance", e);
            }
        });
    }
}

// Enhanced context with state factory
public class EnhancedContext {
    private State currentState;
    private final Map<String, Object> properties = new HashMap<>();
    
    public EnhancedContext(State initialState) {
        this.currentState = initialState;
    }
    
    public void setState(Class<? extends State> stateClass) {
        this.currentState = StateFactory.getState(stateClass);
    }
    
    public void request() {
        currentState.handle(this);
    }
    
    // Property management for state data
    public void setProperty(String key, Object value) {
        properties.put(key, value);
    }
    
    @SuppressWarnings("unchecked")
    public <T> T getProperty(String key, Class<T> type) {
        return (T) properties.get(key);
    }
}
```

## Key Components

### 1. Context
- **Role**: Maintains reference to current state and delegates requests
- **Responsibilities**:
  - Store current state
  - Provide interface for clients
  - Allow state transitions
  - Maintain state-related data

### 2. State Interface/Abstract Class
- **Role**: Defines common interface for all concrete states
- **Responsibilities**:
  - Declare methods that all states must implement
  - Optionally provide default implementations
  - Define state transition methods

### 3. Concrete States
- **Role**: Implement state-specific behavior
- **Responsibilities**:
  - Handle requests in state-specific manner
  - Decide when and how to transition to other states
  - Access context data as needed

## Benefits

### 1. Eliminates Conditional Logic
```java
// Before: Complex conditional logic
if (state == IDLE) {
    // Handle idle behavior
} else if (state == PROCESSING) {
    // Handle processing behavior
} else if (state == COMPLETED) {
    // Handle completed behavior
}

// After: Clean delegation
currentState.handle(request);
```

### 2. Easy to Add New States
- Simply create new concrete state class
- No need to modify existing code
- Follows Open-Closed Principle

### 3. State-Specific Behavior Encapsulation
- Each state class focuses on specific behavior
- Better code organization
- Easier to understand and maintain

### 4. Explicit State Transitions
- State transitions are clearly defined
- Easy to track state changes
- Better debugging and logging

## Drawbacks

### 1. Increased Number of Classes
- Each state requires separate class
- Can lead to class proliferation
- More complex project structure

### 2. State Management Complexity
- Need to manage state instances
- Potential memory overhead
- State synchronization issues

### 3. Context Dependency
- States often need access to context
- Can create tight coupling
- Circular dependencies possible

## When to Use

### Ideal Scenarios
1. **Complex State Machines**: Objects with multiple states and complex transitions
2. **Behavioral Variation**: Behavior changes significantly based on state
3. **Conditional Code Cleanup**: Large conditional statements based on state
4. **Protocol Implementation**: Network protocols, parsing, etc.

### Examples
- **User Interface**: Button states (enabled, disabled, pressed)
- **Game Development**: Character states (idle, running, jumping, attacking)
- **Workflow Systems**: Document approval, order processing
- **Connection Management**: Network connection states
- **Media Players**: Play, pause, stop, fast-forward states

## When Not to Use

### Avoid When
1. **Simple State Logic**: Only 2-3 simple states with minimal behavior differences
2. **Rare State Changes**: States change infrequently
3. **Performance Critical**: State transitions are in tight loops
4. **Simple Conditional Logic**: Basic if-else statements are sufficient

## Real-World Examples

### 1. TCP Connection States
```java
public interface TCPState {
    void open(TCPConnection connection);
    void close(TCPConnection connection);
    void acknowledge(TCPConnection connection);
}

public class TCPEstablished implements TCPState {
    public void open(TCPConnection connection) {
        // Already open, do nothing
    }
    
    public void close(TCPConnection connection) {
        // Change state to closed
        connection.setState(new TCPClosed());
    }
    
    public void acknowledge(TCPConnection connection) {
        // Handle acknowledgment
    }
}
```

### 2. Document Workflow
```java
public abstract class DocumentState {
    public abstract void edit(Document document);
    public abstract void review(Document document);
    public abstract void approve(Document document);
    public abstract void publish(Document document);
}

public class DraftState extends DocumentState {
    public void edit(Document document) {
        System.out.println("Editing document in draft state");
    }
    
    public void review(Document document) {
        document.setState(new ReviewState());
        System.out.println("Sending document for review");
    }
    
    // Other methods throw appropriate exceptions
}
```

### 3. Media Player
```java
public interface PlayerState {
    void play(MediaPlayer player);
    void pause(MediaPlayer player);
    void stop(MediaPlayer player);
}

public class PlayingState implements PlayerState {
    public void play(MediaPlayer player) {
        System.out.println("Already playing");
    }
    
    public void pause(MediaPlayer player) {
        player.setState(new PausedState());
        System.out.println("Pausing playback");
    }
    
    public void stop(MediaPlayer player) {
        player.setState(new StoppedState());
        System.out.println("Stopping playback");
    }
}
```

## Best Practices

### 1. State Instance Management
```java
// Singleton states for stateless behavior
public class IdleState implements State {
    private static IdleState instance;
    
    public static IdleState getInstance() {
        if (instance == null) {
            instance = new IdleState();
        }
        return instance;
    }
    
    private IdleState() {} // Private constructor
}

// Factory pattern for state creation
public class StateFactory {
    public static State createState(StateType type) {
        switch (type) {
            case IDLE: return IdleState.getInstance();
            case PROCESSING: return new ProcessingState();
            default: throw new IllegalArgumentException("Unknown state type");
        }
    }
}
```

### 2. Context Data Management
```java
public class StatefulContext {
    private State currentState;
    private final Map<String, Object> stateData = new HashMap<>();
    
    public void setStateData(String key, Object value) {
        stateData.put(key, value);
    }
    
    public <T> T getStateData(String key, Class<T> type) {
        return type.cast(stateData.get(key));
    }
    
    public void clearStateData() {
        stateData.clear();
    }
}
```

### 3. State Transition Validation
```java
public abstract class ValidatedState implements State {
    protected final Set<Class<? extends State>> allowedTransitions;
    
    protected ValidatedState(Set<Class<? extends State>> allowedTransitions) {
        this.allowedTransitions = allowedTransitions;
    }
    
    protected void validateTransition(Class<? extends State> newState) {
        if (!allowedTransitions.contains(newState)) {
            throw new IllegalStateTransitionException(
                "Cannot transition from " + getClass().getSimpleName() + 
                " to " + newState.getSimpleName());
        }
    }
}
```

### 4. State History and Rollback
```java
public class StatefulContextWithHistory {
    private State currentState;
    private final Stack<State> stateHistory = new Stack<>();
    
    public void setState(State newState) {
        stateHistory.push(currentState);
        currentState = newState;
    }
    
    public void rollbackToPreviousState() {
        if (!stateHistory.isEmpty()) {
            currentState = stateHistory.pop();
        }
    }
    
    public void clearHistory() {
        stateHistory.clear();
    }
}
```

## Common Pitfalls

### 1. Overusing State Pattern
```java
// Don't use State Pattern for simple boolean flags
public class BadExample {
    private State isEnabled; // Just use boolean!
}

// Better approach
public class GoodExample {
    private boolean enabled;
    
    public void performAction() {
        if (enabled) {
            // Perform action
        }
    }
}
```

### 2. Circular Dependencies
```java
// Problematic: States holding references to each other
public class StateA implements State {
    private StateB stateB; // Avoid this!
}

// Better: Use factory or context for transitions
public class StateA implements State {
    public void handle(Context context) {
        context.setState(StateFactory.getState(StateB.class));
    }
}
```

### 3. Not Handling Invalid Transitions
```java
// Always validate state transitions
public void setState(State newState) {
    if (!currentState.canTransitionTo(newState)) {
        throw new IllegalStateTransitionException();
    }
    currentState = newState;
}
```

### 4. Memory Leaks with State Data
```java
// Clean up state-specific data on transitions
public class StatefulContext {
    public void setState(State newState) {
        currentState.cleanup(); // Clean up current state
        currentState = newState;
        newState.initialize(this); // Initialize new state
    }
}
```

## Related Patterns

### 1. Strategy Pattern
- **Similarity**: Both encapsulate algorithms/behavior in separate classes
- **Difference**: State Pattern manages state transitions; Strategy focuses on algorithm selection
- **Usage**: State Pattern for state-dependent behavior; Strategy for interchangeable algorithms

### 2. Command Pattern
- **Integration**: Commands can trigger state transitions
- **Usage**: Commands encapsulate operations that might change state

### 3. Observer Pattern
- **Integration**: Observers can monitor state changes
- **Usage**: Notify interested parties when state transitions occur

### 4. Memento Pattern
- **Integration**: Save and restore state snapshots
- **Usage**: Implement undo/redo functionality for state changes

### 5. Template Method Pattern
- **Integration**: Define common state transition algorithm
- **Usage**: Standardize how states handle transitions

## Testing Strategies

### 1. State Transition Testing
```java
@Test
public void testStateTransitions() {
    Context context = new Context(new IdleState());
    
    context.request(); // Should transition to ProcessingState
    assertInstanceOf(ProcessingState.class, context.getState());
    
    context.request(); // Should transition to CompletedState
    assertInstanceOf(CompletedState.class, context.getState());
}
```

### 2. Invalid Transition Testing
```java
@Test
public void testInvalidTransition() {
    Context context = new Context(new CompletedState());
    
    assertThrows(IllegalStateTransitionException.class, () -> {
        context.setState(new IdleState()); // Invalid transition
    });
}
```

### 3. State Behavior Testing
```java
@Test
public void testStateBehavior() {
    State processingState = new ProcessingState();
    Context context = new Context(processingState);
    
    // Mock or verify specific behavior
    processingState.handle(context);
    
    // Verify expected outcomes
    verify(mockProcessor).process(any());
}
```

## Performance Considerations

### 1. State Instance Creation
- Use singleton pattern for stateless states
- Pool frequently used states
- Lazy initialization for expensive states

### 2. Memory Management
- Clean up state-specific resources
- Implement proper cleanup methods
- Monitor memory usage in long-running applications

### 3. Transition Optimization
- Cache transition validations
- Use efficient data structures for state storage
- Consider state compression for large state spaces

## Conclusion

The State Pattern is a powerful tool for managing complex state-dependent behavior. It promotes clean, maintainable code by encapsulating state-specific logic and eliminating complex conditional statements. While it introduces additional classes and complexity, the benefits in terms of maintainability, extensibility, and code organization make it invaluable for systems with complex state management requirements.

The pattern is particularly effective in scenarios like state machines, workflow systems, protocol implementations, and user interface components where behavior varies significantly based on internal state.