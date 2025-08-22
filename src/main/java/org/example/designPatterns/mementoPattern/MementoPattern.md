# Memento Pattern

## Overview
The Memento Pattern provides the ability to restore an object to its previous state without violating encapsulation. It captures and externalizes an object's internal state so that it can be restored later, essentially implementing undo functionality.

## Intent
- Capture and externalize an object's internal state without violating encapsulation
- Allow the object to be restored to this state later
- Provide undo/redo functionality
- Create snapshots of object states for backup and recovery purposes

## Problem
You need to:
- Save snapshots of an object's state for later restoration
- Implement undo/redo functionality in your application
- Provide rollback capabilities without exposing object's internal structure
- Create backup and recovery mechanisms for complex objects

## Solution
The Memento Pattern uses three key participants:
1. **Originator**: Creates mementos containing snapshots of its current state
2. **Memento**: Stores the internal state of the Originator
3. **Caretaker**: Responsible for managing mementos but never examines their contents

## Structure

### Core Components
1. **Originator**: The object whose state needs to be saved and restored
2. **Memento**: Stores the state of the Originator (typically immutable)
3. **Caretaker**: Manages mementos and requests save/restore operations

```
Originator
├── createMemento(): Memento
├── restoreMemento(memento: Memento): void
└── state: Object

Memento
└── getState(): Object (usually private/protected)

Caretaker
├── mementos: List<Memento>
├── saveState(): void
└── restoreState(index): void
```

## Key Characteristics

### Advantages
- **Encapsulation Preservation**: Internal state is not exposed to external objects
- **Simplified Originator**: Originator doesn't need to manage multiple versions of state
- **Undo/Redo Support**: Easy implementation of undo/redo functionality
- **Snapshot Capability**: Can save and restore complex object states
- **History Management**: Can maintain history of state changes

### Disadvantages
- **Memory Overhead**: Storing multiple states can consume significant memory
- **Performance Impact**: Creating and storing mementos can be expensive
- **Caretaker Complexity**: Managing large numbers of mementos can become complex
- **Limited Memento Access**: Only originator should access memento internals

## Implementation Guidelines

### When to Use
- When you need to provide undo/redo functionality
- For implementing checkpoints and rollback mechanisms
- When you need to save and restore object states periodically
- For creating backup and recovery systems
- When implementing version control systems

### When Not to Use
- When object states are simple and don't justify the overhead
- If memory usage is critical and states are large
- When the cost of creating mementos is too high
- For objects that change frequently with minimal benefit from state saving

## Real-World Examples

### Text Editor Undo/Redo
```java
TextEditor editor = new TextEditor();
editor.type("Hello");
EditorMemento save1 = editor.createMemento();
editor.type(" World");
editor.restoreMemento(save1); // Back to "Hello"
```

### Game State Saving
```java
GameState game = new GameState();
game.setLevel(5);
game.setScore(1000);
GameMemento checkpoint = game.saveState();
// Player dies, restore from checkpoint
game.restoreState(checkpoint);
```

### Database Transaction Management
```java
Database db = new Database();
DatabaseMemento beforeTransaction = db.createSnapshot();
db.executeTransaction();
if (transactionFailed) {
    db.restore(beforeTransaction);
}
```

## Related Patterns

### Command Pattern
- Often used together - Commands can store mementos for undo operations
- Command encapsulates requests; Memento encapsulates state

### Prototype Pattern
- Both involve copying object state
- Prototype creates new objects; Memento saves state for restoration

### State Pattern
- State manages behavior changes; Memento manages state snapshots
- Can be used together for complex state management

### Caretaker Pattern
- Specialized application of Memento for managing object lifecycles

## Common Variations

### Token Memento
Uses a token/key instead of storing actual state

### Incremental Memento
Stores only changes since the last memento to save space

### Lazy Memento
Creates memento content only when needed

### Compressed Memento
Uses compression to reduce memory footprint

## Best Practices

1. **Keep Mementos Immutable**: Prevent accidental modification of saved states
2. **Limit Memento Access**: Only originator should access memento internals
3. **Manage Memory**: Implement cleanup strategies for old mementos
4. **Use Weak References**: For automatic cleanup of unused mementos
5. **Consider Serialization**: For persisting mementos to disk
6. **Batch Operations**: Group related state changes before creating mementos

## Code Example Structure
```java
// Originator
class TextEditor {
    private String content;
    
    public EditorMemento createMemento() {
        return new EditorMemento(content);
    }
    
    public void restoreMemento(EditorMemento memento) {
        this.content = memento.getContent();
    }
    
    // Inner class for encapsulation
    public static class EditorMemento {
        private final String content;
        
        private EditorMemento(String content) {
            this.content = content;
        }
        
        private String getContent() {
            return content;
        }
    }
}

// Caretaker
class EditorHistory {
    private List<EditorMemento> history = new ArrayList<>();
    private TextEditor editor;
    
    public void saveState() {
        history.add(editor.createMemento());
    }
    
    public void undo() {
        if (!history.isEmpty()) {
            EditorMemento memento = history.remove(history.size() - 1);
            editor.restoreMemento(memento);
        }
    }
}
```

## Design Considerations

### Memory Management
- Implement cleanup strategies for old mementos
- Consider using compression for large states
- Set limits on the number of stored mementos

### Performance
- Lazy creation of mementos when possible
- Incremental state saving for large objects
- Efficient serialization mechanisms

### Thread Safety
- Ensure mementos are immutable
- Synchronize access to caretaker operations
- Consider concurrent modification issues

The Memento Pattern is essential for applications requiring undo/redo functionality, state management, and backup/recovery capabilities while maintaining proper encapsulation principles.