# Iterator Pattern

## Overview
The Iterator Pattern provides a way to access the elements of an aggregate object sequentially without exposing its underlying representation. It decouples algorithms from containers, allowing you to traverse different data structures using a uniform interface.

## Intent
- Provide a way to access elements of an aggregate object sequentially without exposing its internal structure
- Support multiple traversal algorithms for the same aggregate object
- Provide a uniform interface for traversing different aggregate structures (polymorphic iteration)

## Problem
You have a complex data structure (like a tree, graph, or custom collection) and you want to:
- Traverse its elements without exposing its internal structure
- Support multiple ways of traversing the same collection
- Allow clients to iterate over collections without knowing their implementation details

## Solution
The Iterator Pattern defines an iterator object that encapsulates the details of accessing and traversing elements. The iterator provides a standard interface for traversal, typically including methods like `hasNext()` and `next()`.

## Structure

### Core Components
1. **Iterator Interface**: Defines the interface for accessing and traversing elements
2. **ConcreteIterator**: Implements the iterator interface and maintains traversal state
3. **Aggregate Interface**: Defines interface for creating iterator objects
4. **ConcreteAggregate**: Implements the aggregate interface and returns appropriate iterator

```
Iterator
├── hasNext(): boolean
└── next(): Object

ConcreteIterator
├── hasNext(): boolean
├── next(): Object
└── currentPosition: int

Aggregate
└── createIterator(): Iterator

ConcreteAggregate
├── createIterator(): Iterator
└── items: Collection
```

## Key Characteristics

### Advantages
- **Separation of Concerns**: Separates traversal logic from the collection
- **Multiple Iterators**: Supports multiple simultaneous traversals of the same collection
- **Uniform Interface**: Provides consistent way to traverse different collections
- **Simplified Collection Interface**: Collections don't need to support multiple traversal methods
- **Flexibility**: Easy to add new traversal algorithms

### Disadvantages
- **Additional Complexity**: Introduces extra classes and interfaces
- **Performance Overhead**: May have slight performance cost compared to direct access
- **Memory Usage**: Each iterator maintains its own state

## Implementation Guidelines

### When to Use
- When you need to access elements of a collection without exposing its internal structure
- When you want to support multiple traversal methods for the same collection
- When you want to provide a uniform interface for traversing different types of collections
- When you need to iterate over a complex data structure like trees or graphs

### When Not to Use
- For simple arrays or lists where built-in iteration is sufficient
- When performance is critical and direct access is much faster
- When you only need one simple traversal method

## Real-World Examples

### Java Collections Framework
```java
List<String> list = new ArrayList<>();
Iterator<String> iterator = list.iterator();
while (iterator.hasNext()) {
    String item = iterator.next();
    // Process item
}
```

### Database Result Sets
```java
ResultSet rs = statement.executeQuery("SELECT * FROM users");
while (rs.next()) {
    String name = rs.getString("name");
    // Process record
}
```

### File System Traversal
- Directory iterators that traverse files and subdirectories
- XML/JSON parsers that iterate through document elements
- Graph traversal algorithms (BFS, DFS)

## Related Patterns

### Composite Pattern
Often used together - Iterator can traverse Composite structures

### Factory Method Pattern
Aggregate objects often use Factory Method to create appropriate iterators

### Visitor Pattern
Iterator can be used to traverse structures that Visitor operates on

### Command Pattern
Iterator operations can be encapsulated as Commands

## Common Variations

### External vs Internal Iterators
- **External**: Client controls iteration (more common in Java)
- **Internal**: Iterator controls iteration and calls client code

### Robust Iterators
Handle collection modifications during iteration

### Null Object Pattern
Use null iterators for empty collections

## Best Practices

1. **Make Iterator Fail-Fast**: Detect concurrent modifications
2. **Support Remove Operation**: Provide safe element removal during iteration
3. **Implement Iterable Interface**: Make collections work with enhanced for loops
4. **Consider Thread Safety**: Handle concurrent access appropriately
5. **Lazy Loading**: Load elements on demand when possible
6. **Multiple Iterator Types**: Provide different iterators for different traversal orders

## Code Example Structure
```java
// Iterator interface
interface Iterator<T> {
    boolean hasNext();
    T next();
    void remove();
}

// Aggregate interface
interface Iterable<T> {
    Iterator<T> iterator();
}

// Concrete implementation
class ConcreteCollection<T> implements Iterable<T> {
    private List<T> items = new ArrayList<>();
    
    public Iterator<T> iterator() {
        return new ConcreteIterator();
    }
    
    private class ConcreteIterator implements Iterator<T> {
        private int position = 0;
        
        public boolean hasNext() {
            return position < items.size();
        }
        
        public T next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            return items.get(position++);
        }
        
        public void remove() {
            items.remove(--position);
        }
    }
}
```

The Iterator Pattern is fundamental to modern programming and is built into most programming languages' standard libraries. It provides a clean, consistent way to traverse collections while maintaining encapsulation and supporting multiple traversal strategies.