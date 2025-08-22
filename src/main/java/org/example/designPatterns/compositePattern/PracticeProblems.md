# Composite Pattern - Practice Problems

## Problem 1: Basic Implementation (Beginner)
**Description:** Create a simple mathematical expression evaluator using the Composite Pattern.

**Requirements:**
- Create a `MathExpression` interface with an `evaluate()` method
- Implement `Number` class as a leaf component
- Implement `BinaryOperation` class as a composite that takes two expressions and an operator (+, -, *, /)
- Create expressions like: ((5 + 3) * 2) - 4

**Example Usage:**
```java
MathExpression expr = new BinaryOperation(
    new BinaryOperation(
        new BinaryOperation(new Number(5), new Number(3), "+"),
        new Number(2), "*"),
    new Number(4), "-");
System.out.println(expr.evaluate()); // Should output 12
```

**Skills Practiced:**
- Basic Composite Pattern implementation
- Recursive evaluation
- Tree structure navigation

---

## Problem 2: Menu System (Beginner-Intermediate)
**Description:** Design a restaurant menu system using the Composite Pattern.

**Requirements:**
- Create a `MenuComponent` interface with methods: `getName()`, `getPrice()`, `display()`
- Implement `MenuItem` class for individual items (leaf)
- Implement `Menu` class for categories/submenues (composite)
- Support nested menus (e.g., Drinks -> Hot Drinks -> Coffee Types)
- Calculate total prices for menu sections

**Features to Include:**
- Menu hierarchy (Main Menu -> Categories -> Subcategories -> Items)
- Price calculation for entire sections
- Dietary restrictions filtering (vegetarian, gluten-free)
- Search functionality

**Skills Practiced:**
- Hierarchical data structures
- Filtering and search operations
- Uniform interface implementation

---

## Problem 3: Computer File System with Permissions (Intermediate)
**Description:** Extend the basic file system example with advanced permission management and operations.

**Requirements:**
- Implement a comprehensive file system with Users, Groups, and Permissions
- Support UNIX-style permissions (read, write, execute for owner, group, others)
- Implement file operations that respect permissions
- Add support for symbolic links and shortcuts
- Include file system statistics and quota management

**Advanced Features:**
- Permission inheritance from parent directories
- Access control lists (ACL)
- File system events and monitoring
- Compression and encryption operations
- Backup and versioning

**Skills Practiced:**
- Complex permission systems
- Event handling in composite structures
- Advanced recursive operations

---

## Problem 4: HTML Document Builder (Intermediate)
**Description:** Create an HTML document builder using the Composite Pattern.

**Requirements:**
- Create `HTMLElement` interface with methods: `render()`, `addChild()`, `setAttribute()`
- Implement `HTMLTag` class for container elements (div, body, html)
- Implement `HTMLText` class for text content (leaf)
- Implement `HTMLVoidTag` class for self-closing tags (img, br, hr)
- Support CSS styling and JavaScript events

**Features to Include:**
- Nested HTML structure
- Attribute management
- CSS class and style handling
- Form elements with validation
- Template system with placeholders

**Skills Practiced:**
- Document object model (DOM) patterns
- Template and builder patterns integration
- Attribute and style management

---

## Problem 5: Game Object Hierarchy (Intermediate-Advanced)
**Description:** Design a game engine's object hierarchy using the Composite Pattern.

**Requirements:**
- Create `GameObject` interface with methods: `update()`, `render()`, `handleInput()`
- Implement scene graph with parent-child relationships
- Support transformation matrices (position, rotation, scale)
- Include collision detection and physics
- Implement component-based architecture within the pattern

**Game Objects to Include:**
- Scene (root composite)
- GameEntity (can contain other entities)
- RenderableComponent (leaf)
- CollisionComponent (leaf)
- Player, Enemy, PowerUp entities
- UI elements

**Advanced Features:**
- Level-of-detail (LOD) optimization
- Spatial partitioning for performance
- Animation system integration
- Resource management and loading

**Skills Practiced:**
- Game engine architecture
- Performance optimization in composite structures
- Real-time systems
- Matrix transformations

---

## Problem 6: SQL Query Builder (Advanced)
**Description:** Create a sophisticated SQL query builder using the Composite Pattern.

**Requirements:**
- Design `QueryComponent` interface for all SQL elements
- Implement various SQL components: SELECT, FROM, WHERE, JOIN, etc.
- Support complex nested queries and subqueries
- Include query optimization and validation
- Generate executable SQL from the composite structure

**SQL Components:**
- SelectClause, FromClause, WhereClause (composites)
- Column, Table, Condition (leaves)
- Join operations (INNER, LEFT, RIGHT, FULL OUTER)
- Aggregate functions (COUNT, SUM, AVG, etc.)
- Window functions and CTEs

**Advanced Features:**
- Query plan generation
- Parameter binding and SQL injection prevention
- Database-specific SQL dialect generation
- Query performance analysis
- Caching and memoization

**Skills Practiced:**
- Complex query building
- SQL optimization techniques
- Security considerations
- Multi-dialect support

---

## Problem 7: Workflow Engine (Advanced)
**Description:** Build a business workflow engine using the Composite Pattern.

**Requirements:**
- Create `WorkflowNode` interface with methods: `execute()`, `validate()`, `rollback()`
- Implement atomic tasks (leaf nodes) and composite workflows
- Support parallel execution, conditional branching, and loops
- Include error handling and transaction management
- Implement workflow versioning and migration

**Workflow Components:**
- Task (atomic operation)
- Sequence (serial execution)
- Parallel (concurrent execution)
- Condition (if-then-else logic)
- Loop (while/for iterations)
- SubWorkflow (nested workflows)

**Advanced Features:**
- Workflow persistence and recovery
- Human task integration (approvals, manual steps)
- Event-driven workflow triggers
- Performance monitoring and analytics
- Dynamic workflow modification

**Skills Practiced:**
- Business process modeling
- Concurrency and synchronization
- Error handling and recovery
- State management

---

## Problem 8: Neural Network Architecture (Expert)
**Description:** Design a neural network framework using the Composite Pattern.

**Requirements:**
- Create `NetworkComponent` interface for all network elements
- Implement layers, neurons, and connections as composite/leaf structures
- Support forward and backward propagation through the network
- Include various layer types (Dense, Convolutional, LSTM, etc.)
- Implement training algorithms and optimization

**Network Components:**
- NeuralNetwork (root composite)
- Layer (composite containing neurons)
- Neuron (leaf with activation function)
- Connection (weighted link between neurons)
- ActivationFunction (strategy pattern integration)

**Advanced Features:**
- Automatic differentiation for gradients
- GPU acceleration support
- Model serialization and loading
- Hyperparameter optimization
- Distributed training across multiple nodes
- Transfer learning and model fine-tuning

**Skills Practiced:**
- Mathematical computation frameworks
- Advanced algorithm implementation
- Performance optimization
- Memory management
- Parallel processing

---

## Bonus Challenge: Design Pattern Combinator (Expert)
**Description:** Create a framework that allows combining multiple design patterns using the Composite Pattern as the core structure.

**Requirements:**
- Design a meta-pattern system where design patterns themselves are components
- Support pattern composition and nesting
- Implement pattern validation and conflict detection
- Include pattern recommendation system
- Create a visual pattern designer interface

**Patterns to Support:**
- All Gang of Four patterns as composable components
- Custom pattern definitions
- Pattern variations and adaptations
- Anti-pattern detection
- Pattern metrics and analysis

**This challenge requires:**
- Deep understanding of all design patterns
- Meta-programming techniques
- Advanced software architecture
- AI/ML for pattern recommendation
- UI/UX design for visual tools

---

## Tips for Success

### For Beginners:
1. Start with simple tree structures
2. Focus on the uniform interface concept
3. Practice recursive operations
4. Draw out the component hierarchy before coding

### For Intermediate:
1. Consider performance implications of deep hierarchies
2. Implement proper error handling in recursive operations
3. Think about memory management and circular references
4. Add validation and constraints to your composite structures

### For Advanced:
1. Optimize for specific use cases (lazy loading, caching)
2. Integrate with other design patterns appropriately
3. Consider thread safety in concurrent environments
4. Implement comprehensive testing strategies for complex trees

### Common Pitfalls to Avoid:
1. **Over-complication**: Don't force the pattern where a simple list would suffice
2. **Memory leaks**: Be careful with parent-child references
3. **Deep recursion**: Implement iterative alternatives for very deep trees
4. **Type safety**: Use generics and proper validation
5. **Performance**: Monitor and optimize recursive operations

### Testing Strategies:
1. Test with empty composites
2. Test with single-element composites
3. Test with deeply nested structures
4. Test edge cases and error conditions
5. Performance test with large datasets
6. Test thread safety if applicable

Remember: The Composite Pattern is about treating individual objects and compositions uniformly. Focus on creating a clean, consistent interface that works seamlessly at any level of the hierarchy.