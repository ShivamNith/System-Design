# Mediator Pattern

## Overview
The Mediator Pattern defines how a set of objects interact with each other. Instead of objects communicating directly, they communicate through a central mediator object. This reduces coupling between communicating objects and centralizes complex communications and control logic.

## Intent
- Define how objects interact without having them refer to each other explicitly
- Promote loose coupling by keeping objects from referring to each other directly
- Centralize complex communications and control logic between related objects
- Make the interaction easier to understand, maintain, and extend

## Problem
When you have:
- A set of objects that need to communicate in complex ways
- Objects that are tightly coupled due to direct references
- Complex communication logic scattered across multiple objects
- Difficulty in reusing objects because they depend on many other objects
- Hard-to-understand interaction patterns

## Solution
The Mediator Pattern introduces a mediator object that handles all communication between objects. Objects no longer communicate directly with each other; instead, they send messages to the mediator, which then coordinates the interaction with other objects.

## Structure

### Core Components
1. **Mediator Interface**: Defines the contract for communication between colleague objects
2. **ConcreteMediator**: Implements the mediator interface and coordinates communication
3. **Colleague**: Abstract class/interface for objects that communicate through mediator
4. **ConcreteColleague**: Specific objects that participate in the mediated communication

```
Mediator
└── notify(sender, event)

ConcreteMediator
├── notify(sender, event)
└── colleagues: List<Colleague>

Colleague
├── mediator: Mediator
└── send(event)

ConcreteColleagueA, ConcreteColleagueB
├── send(event)
└── receive(event)
```

## Key Characteristics

### Advantages
- **Reduced Coupling**: Objects don't need direct references to each other
- **Centralized Control**: All interaction logic is in one place
- **Reusability**: Colleagues can be reused with different mediators
- **Easier Maintenance**: Changes to interaction logic only affect the mediator
- **Single Responsibility**: Each colleague focuses on its core functionality

### Disadvantages
- **Mediator Complexity**: Mediator can become complex with many colleagues
- **Single Point of Failure**: All communication depends on the mediator
- **Performance**: Additional indirection may impact performance
- **God Object Risk**: Mediator might accumulate too much responsibility

## Implementation Guidelines

### When to Use
- When objects communicate in complex but well-defined ways
- When reusing objects is difficult due to their dependencies
- When behavior distributed between several objects should be customizable
- When you want to avoid tight coupling between interacting objects

### When Not to Use
- For simple object interactions that don't need coordination
- When direct communication is more efficient and coupling is acceptable
- When the mediator would be overly complex
- For one-to-one relationships that don't benefit from mediation

## Real-World Examples

### Air Traffic Control
```java
// Air traffic controller mediates between aircraft
AirTrafficController controller = new AirTrafficController();
Aircraft flight1 = new Boeing747(controller);
Aircraft flight2 = new Airbus380(controller);
flight1.requestLanding(); // Controller coordinates with other aircraft
```

### Chat Room
```java
// Chat room mediates between users
ChatRoom chatRoom = new ChatRoom();
User john = new ChatUser("John", chatRoom);
User jane = new ChatUser("Jane", chatRoom);
john.sendMessage("Hello everyone!"); // Broadcast through chat room
```

### UI Components
```java
// Dialog mediates between UI components
DialogMediator dialog = new LoginDialog();
Button loginButton = new Button(dialog);
TextField usernameField = new TextField(dialog);
// Button enables/disables based on field validation through dialog
```

## Related Patterns

### Observer Pattern
- Mediator centralizes communication; Observer distributes it
- Can be used together - mediator can notify observers

### Facade Pattern
- Both provide simplified interfaces
- Facade simplifies subsystem; Mediator coordinates peer objects

### Command Pattern
- Mediator can use Commands to encapsulate requests between colleagues

### Strategy Pattern
- Mediator can use different strategies for handling communication

## Common Variations

### Event-Driven Mediator
Uses events/messages for asynchronous communication

### Hierarchical Mediator
Multiple mediators organized in a hierarchy

### Typed Mediator
Uses generics to provide type-safe communication

### Aspect-Oriented Mediator
Uses AOP to intercept and mediate method calls

## Best Practices

1. **Keep Mediator Focused**: Don't let it become a god object
2. **Use Interfaces**: Define clear contracts for mediator and colleagues
3. **Handle Events Properly**: Ensure proper event handling and error recovery
4. **Consider Performance**: Be aware of the indirection overhead
5. **Document Interactions**: Clear documentation of mediated workflows
6. **Test Thoroughly**: Test complex interaction scenarios

## Code Example Structure
```java
// Mediator interface
interface ChatMediator {
    void sendMessage(String message, User user);
    void addUser(User user);
}

// Concrete mediator
class ChatRoom implements ChatMediator {
    private List<User> users = new ArrayList<>();
    
    public void addUser(User user) {
        users.add(user);
    }
    
    public void sendMessage(String message, User sender) {
        for (User user : users) {
            if (user != sender) {
                user.receive(message);
            }
        }
    }
}

// Colleague abstract class
abstract class User {
    protected ChatMediator mediator;
    protected String name;
    
    public User(ChatMediator mediator, String name) {
        this.mediator = mediator;
        this.name = name;
    }
    
    public abstract void send(String message);
    public abstract void receive(String message);
}

// Concrete colleague
class ChatUser extends User {
    public ChatUser(ChatMediator mediator, String name) {
        super(mediator, name);
        mediator.addUser(this);
    }
    
    public void send(String message) {
        mediator.sendMessage(message, this);
    }
    
    public void receive(String message) {
        System.out.println(name + " received: " + message);
    }
}
```

The Mediator Pattern is particularly useful in GUI applications, workflow systems, and any scenario where multiple objects need to interact in complex ways while maintaining loose coupling.