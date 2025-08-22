# Memento Pattern Practice Problems

This document contains 8 practice problems ranging from basic to expert level to help you master the Memento Pattern. Each problem includes requirements, hints, and implementation guidelines.

---

## Problem 1: Simple Calculator with Undo (Basic)

### Requirements
Create a simple calculator that can perform basic operations (add, subtract, multiply, divide) and supports undo functionality.

### Specifications
- Calculator should maintain current result
- Support operations: add, subtract, multiply, divide
- Implement undo to revert to previous result
- Handle at least 10 undo operations

### Key Classes to Implement
- `Calculator` (Originator)
- `CalculatorMemento` (Memento)
- `CalculatorHistory` (Caretaker)

### Hints
- Store the current result value in the memento
- Each operation should save state before executing
- Consider edge cases like division by zero

### Test Cases
```java
Calculator calc = new Calculator();
calc.add(5);        // Result: 5
calc.multiply(3);   // Result: 15
calc.subtract(2);   // Result: 13
calc.undo();        // Result: 15
calc.undo();        // Result: 5
```

---

## Problem 2: Drawing Canvas with Undo/Redo (Basic-Intermediate)

### Requirements
Implement a drawing canvas that allows adding shapes and supports undo/redo functionality.

### Specifications
- Canvas can contain circles, rectangles, and lines
- Support adding and removing shapes
- Implement both undo and redo operations
- Maintain color and position information for shapes

### Key Classes to Implement
- `Shape` (abstract or interface)
- `Circle`, `Rectangle`, `Line` (concrete shapes)
- `DrawingCanvas` (Originator)
- `CanvasMemento` (Memento)
- `CanvasHistory` (Caretaker)

### Hints
- Store complete list of shapes in memento
- Consider deep copying for shape objects
- Implement proper undo/redo stack management

### Test Cases
```java
DrawingCanvas canvas = new DrawingCanvas();
canvas.addShape(new Circle(10, 10, 5));
canvas.addShape(new Rectangle(20, 20, 10, 15));
canvas.undo(); // Remove rectangle
canvas.redo(); // Add rectangle back
```

---

## Problem 3: Music Playlist Manager (Intermediate)

### Requirements
Create a music playlist manager that tracks playback state and supports restoration to previous states.

### Specifications
- Playlist contains songs with metadata (title, artist, duration)
- Track current song, position, volume, shuffle mode
- Support saving/loading playlist states
- Implement named snapshots for favorite configurations

### Key Classes to Implement
- `Song` (data class)
- `PlaylistManager` (Originator)
- `PlaylistMemento` (Memento)
- `PlaylistStateManager` (Caretaker)

### Hints
- Consider what constitutes the "state" of a playlist
- Handle playback position and settings
- Implement named save/load functionality

### Test Cases
```java
PlaylistManager player = new PlaylistManager();
player.addSong(new Song("Song1", "Artist1", 180));
player.play();
player.setVolume(75);
player.saveState("myPlaylist");
// ... make changes ...
player.loadState("myPlaylist");
```

---

## Problem 4: Form Builder with Version Control (Intermediate)

### Requirements
Develop a form builder that allows creating forms with various field types and supports version control.

### Specifications
- Support text fields, checkboxes, radio buttons, dropdowns
- Track form structure, validation rules, and styling
- Implement branching versions (like git branches)
- Support merging compatible changes

### Key Classes to Implement
- `FormField` (abstract/interface)
- `TextField`, `Checkbox`, etc. (concrete fields)
- `Form` (Originator)
- `FormMemento` (Memento)
- `FormVersionControl` (Caretaker)

### Hints
- Consider complex state including field order and properties
- Implement version branching logic
- Handle merge conflicts appropriately

### Test Cases
```java
Form form = new Form("UserRegistration");
form.addField(new TextField("username", true));
form.addField(new TextField("email", true));
versionControl.createBranch("feature-validation");
form.addValidation("email", "email-format");
versionControl.merge("main", "feature-validation");
```

---

## Problem 5: Multi-Level Game State System (Intermediate-Advanced)

### Requirements
Create a comprehensive game state system that handles multiple save slots, quick saves, and checkpoint systems.

### Specifications
- Support multiple player profiles
- Implement quick save/load system
- Automatic checkpoint creation at key events
- Save compression to reduce memory usage
- Handle save file corruption recovery

### Key Classes to Implement
- `GameState` (Originator)
- `PlayerProfile`, `GameLevel`, `Inventory` (state components)
- `CompressedGameMemento` (Memento with compression)
- `GameSaveManager` (Advanced Caretaker)

### Hints
- Implement save data compression/decompression
- Handle multiple save slots with metadata
- Consider incremental saves for large states
- Implement checksum validation

### Test Cases
```java
GameSaveManager saveManager = new GameSaveManager();
saveManager.quickSave();
saveManager.saveToSlot(1, "Before Boss Fight");
saveManager.createCheckpoint("Level 5 Start");
saveManager.compressOldSaves();
saveManager.validateSaveIntegrity();
```

---

## Problem 6: Collaborative Document Editor (Advanced)

### Requirements
Build a collaborative document editor that supports concurrent editing with conflict resolution and history tracking.

### Specifications
- Multiple users can edit simultaneously
- Track changes per user with timestamps
- Implement operational transformation for conflict resolution
- Support document branching and merging
- Maintain complete edit history with author information

### Key Classes to Implement
- `Document` (Originator)
- `DocumentOperation` (edit operations)
- `VersionedDocumentMemento` (Memento with metadata)
- `CollaborativeEditor` (Advanced Caretaker)
- `ConflictResolver` (handles merge conflicts)

### Hints
- Implement operational transformation algorithms
- Handle concurrent modifications gracefully
- Consider vector clocks for ordering operations
- Implement three-way merge algorithms

### Test Cases
```java
CollaborativeEditor editor = new CollaborativeEditor();
editor.applyOperation(new InsertOperation(0, "Hello", "user1"));
editor.applyOperation(new InsertOperation(5, " World", "user2"));
editor.resolveConflicts();
editor.createSnapshot("version_1.0");
```

---

## Problem 7: Database Migration System (Advanced)

### Requirements
Create a database migration system that can rollback schema and data changes with dependency management.

### Specifications
- Support schema changes (create/drop tables, add/remove columns)
- Handle data migrations and transformations
- Implement dependency tracking between migrations
- Support partial rollbacks and forward-only migrations
- Validate migration integrity and consistency

### Key Classes to Implement
- `DatabaseSchema` (Originator)
- `Migration` (abstract migration operation)
- `SchemaMigrationMemento` (Memento)
- `MigrationManager` (Advanced Caretaker)
- `DependencyResolver` (handles migration dependencies)

### Hints
- Implement both forward and reverse migration logic
- Handle data type changes and constraints
- Consider transaction boundaries for atomic migrations
- Implement migration validation and testing

### Test Cases
```java
MigrationManager migrator = new MigrationManager();
migrator.addMigration(new CreateTableMigration("users"));
migrator.addMigration(new AddColumnMigration("users", "email"));
migrator.migrate("v2.0");
migrator.rollback("v1.5");
migrator.validateMigrationPath();
```

---

## Problem 8: AI Model Training Checkpoint System (Expert)

### Requirements
Develop a sophisticated checkpoint system for machine learning model training with distributed computing support.

### Specifications
- Support distributed training across multiple nodes
- Implement incremental checkpointing for large models
- Handle gradient synchronization and model parameter updates
- Support experiment branching and hyperparameter optimization
- Implement automatic checkpoint compression and cleanup
- Handle node failures with automatic recovery

### Key Classes to Implement
- `NeuralNetworkModel` (Originator)
- `ModelWeights`, `TrainingState`, `OptimizerState` (state components)
- `DistributedModelMemento` (Complex Memento)
- `TrainingCheckpointManager` (Expert-level Caretaker)
- `DistributedSynchronizer` (handles distributed coordination)

### Hints
- Implement efficient serialization for large tensors
- Handle partial checkpoint updates for memory efficiency
- Consider consistent hashing for distributed storage
- Implement Byzantine fault tolerance for node failures
- Use compression algorithms suitable for model weights

### Advanced Requirements
- Implement differential checkpointing (only save changes)
- Support checkpoint deduplication across experiments
- Handle checkpoint migration between different hardware
- Implement checkpoint verification and integrity checking
- Support hot checkpoint swapping during training

### Test Cases
```java
TrainingCheckpointManager manager = new TrainingCheckpointManager();
manager.initializeDistributedTraining(4); // 4 nodes
manager.configureCheckpointPolicy(every(100), keepLast(10));
manager.startTraining(model, dataset);
// Simulate node failure
manager.handleNodeFailure(nodeId);
manager.recoverFromLatestCheckpoint();
manager.resumeTraining();
```

---

## Implementation Guidelines

### For All Problems

#### 1. Memento Design Principles
- **Encapsulation**: Only the originator should access memento internals
- **Immutability**: Mementos should be immutable once created
- **Efficiency**: Consider memory usage and creation costs

#### 2. Error Handling
- Handle null references gracefully
- Validate memento integrity before restoration
- Provide meaningful error messages

#### 3. Memory Management
- Implement cleanup for old mementos
- Consider weak references for automatic cleanup
- Provide memory usage monitoring

#### 4. Thread Safety
- Ensure thread-safe operations where needed
- Consider concurrent access to caretaker
- Handle synchronization appropriately

### Testing Strategy

#### Unit Tests
- Test memento creation and restoration
- Verify state consistency after operations
- Test edge cases and error conditions

#### Integration Tests
- Test complete workflows
- Verify undo/redo functionality
- Test memory limits and cleanup

#### Performance Tests
- Measure memento creation time
- Test memory usage with large states
- Benchmark restoration performance

### Code Quality Guidelines

#### Documentation
- Document all public methods and classes
- Provide usage examples
- Explain design decisions

#### Code Style
- Follow consistent naming conventions
- Use meaningful variable names
- Keep methods focused and small

#### Design Patterns Integration
- Consider combining with Command Pattern
- Integrate with Observer Pattern for notifications
- Use Strategy Pattern for different memento types

---

## Bonus Challenges

### 1. Lazy Memento Loading
Implement mementos that load their state only when needed, reducing memory usage for large objects.

### 2. Encrypted Mementos
Add encryption/decryption support for sensitive state data in mementos.

### 3. Cross-Platform Serialization
Implement mementos that can be serialized across different platforms and programming languages.

### 4. Real-time Collaboration
Extend any of the intermediate problems to support real-time collaboration with conflict resolution.

### 5. Machine Learning Integration
Create a memento system that can checkpoint and restore machine learning model states during training.

---

## Evaluation Criteria

### Basic Level (Problems 1-2)
- Correct implementation of memento pattern structure
- Proper encapsulation of state
- Basic undo functionality

### Intermediate Level (Problems 3-5)
- Complex state management
- Efficient memory usage
- Advanced caretaker functionality
- Error handling and validation

### Advanced Level (Problems 6-7)
- Sophisticated state coordination
- Performance optimization
- Concurrent access handling
- Advanced algorithms implementation

### Expert Level (Problem 8)
- Distributed system design
- Fault tolerance
- Advanced optimization techniques
- Industry-grade robustness

---

## Additional Resources

### Design Pattern Resources
- Gang of Four Design Patterns book
- Head First Design Patterns
- Refactoring Guru Memento Pattern

### Implementation Tools
- Java Serialization API
- Apache Commons for utilities
- JUnit for testing
- JMH for performance benchmarking

### Performance Considerations
- Memory profiling tools
- Garbage collection analysis
- Concurrent programming best practices
- Distributed systems design principles

Happy coding! These problems will help you master the Memento Pattern from basic concepts to expert-level implementations.