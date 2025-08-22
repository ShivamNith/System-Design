# Visitor Pattern - Practice Problems

## Basic Level Problems

### Problem 1: Shape Visitor
**Difficulty: ⭐⭐☆☆☆**

Create a visitor pattern implementation for geometric shapes with the following requirements:

**Shapes:**
- Circle (radius)
- Rectangle (width, height)
- Triangle (base, height)

**Visitors:**
- AreaCalculatorVisitor
- PerimeterCalculatorVisitor

**Requirements:**
- Calculate area and perimeter for each shape type
- Display results with proper formatting
- Handle edge cases (negative dimensions)

**Expected Output:**
```
Circle (radius=5): Area=78.54, Perimeter=31.42
Rectangle (4x6): Area=24.00, Perimeter=20.00
Triangle (base=3, height=4): Area=6.00, Perimeter=12.00
```

---

### Problem 2: Employee Hierarchy Visitor
**Difficulty: ⭐⭐☆☆☆**

Design a visitor pattern for an employee hierarchy:

**Employee Types:**
- Manager (salary, bonus, teamSize)
- Developer (salary, programmingLanguages)
- Designer (salary, designTools)

**Visitors:**
- SalaryCalculatorVisitor (including bonuses and benefits)
- SkillsReportVisitor

**Requirements:**
- Calculate total compensation for each employee type
- Generate skills report showing tools/languages
- Handle different bonus structures for managers

---

## Intermediate Level Problems

### Problem 3: Document Processing System
**Difficulty: ⭐⭐⭐☆☆**

Create a document processing system using the visitor pattern:

**Document Elements:**
- TextParagraph (content, font, size)
- Image (path, width, height, caption)
- Table (rows, columns, data)
- Header (level, text)

**Visitors:**
- HTMLExportVisitor
- PDFExportVisitor
- WordCountVisitor
- ValidationVisitor

**Requirements:**
- Export documents to different formats
- Count words excluding images and tables
- Validate document structure (headers in order, valid image paths)
- Handle nested elements and formatting

**Sample Document Structure:**
```
Document
├── Header(1, "Introduction")
├── TextParagraph("This is the introduction...")
├── Image("chart.png", 400, 300, "Sales Chart")
├── Header(2, "Analysis")
├── Table(3x4, sales_data)
└── TextParagraph("Conclusion...")
```

---

### Problem 4: Mathematical Expression Evaluator
**Difficulty: ⭐⭐⭐☆☆**

Build an expression evaluator using the visitor pattern:

**Expression Types:**
- Number (value)
- Variable (name)
- BinaryOperation (left, operator, right)
- UnaryOperation (operator, operand)
- FunctionCall (name, arguments)

**Visitors:**
- EvaluatorVisitor (with variable context)
- SimplifierVisitor
- DerivativeVisitor
- ToStringVisitor

**Requirements:**
- Evaluate expressions with variables
- Simplify expressions (e.g., 0+x → x, 1*x → x)
- Calculate derivatives for basic functions
- Convert expressions to readable string format

**Example:**
```
Expression: 2*x + 3*x^2
Evaluation (x=2): 2*2 + 3*4 = 16
Simplified: 2*x + 3*x^2
Derivative: 2 + 6*x
String: "(2 * x) + (3 * (x ^ 2))"
```

---

### Problem 5: File System Operations
**Difficulty: ⭐⭐⭐☆☆**

Extend a file system visitor with advanced operations:

**Elements:**
- File (name, size, type, permissions, owner)
- Directory (name, children, permissions, owner)
- SymbolicLink (name, target, broken)

**Visitors:**
- SecurityAuditVisitor
- BackupVisitor
- CleanupVisitor
- DuplicateFinderVisitor

**Requirements:**
- Audit file permissions and ownership
- Create backup strategy based on file types and modification dates
- Find and clean temporary files, empty directories
- Identify duplicate files by content hash
- Handle symbolic links and broken links

---

## Advanced Level Problems

### Problem 6: Database Query Optimizer
**Difficulty: ⭐⭐⭐⭐☆**

Create a database query optimizer using the visitor pattern:

**Query Elements:**
- Table (name, columns, indices)
- SelectClause (columns, aggregations)
- WhereClause (conditions)
- JoinClause (type, table, condition)
- OrderByClause (columns, direction)
- GroupByClause (columns)

**Visitors:**
- CostEstimatorVisitor
- IndexSuggestionVisitor
- QueryOptimizationVisitor
- ExecutionPlanVisitor

**Requirements:**
- Estimate query execution cost
- Suggest optimal indices
- Reorder joins and conditions for performance
- Generate execution plan with steps
- Handle complex nested queries and subqueries

**Sample Query:**
```sql
SELECT c.name, COUNT(o.id) as order_count
FROM customers c
LEFT JOIN orders o ON c.id = o.customer_id
WHERE c.created_date > '2023-01-01'
GROUP BY c.name
ORDER BY order_count DESC
```

---

### Problem 7: Compiler AST Processor
**Difficulty: ⭐⭐⭐⭐☆**

Build a comprehensive compiler AST processor:

**AST Nodes:**
- Program (statements)
- VariableDeclaration (type, name, initializer)
- FunctionDeclaration (returnType, name, parameters, body)
- IfStatement (condition, thenBranch, elseBranch)
- WhileLoop (condition, body)
- ForLoop (init, condition, increment, body)
- BinaryExpression (left, operator, right)
- FunctionCall (name, arguments)

**Visitors:**
- SemanticAnalyzerVisitor
- CodeGeneratorVisitor
- OptimizationVisitor
- CFGGeneratorVisitor (Control Flow Graph)

**Requirements:**
- Perform semantic analysis (type checking, scope validation)
- Generate target code (assembly/bytecode)
- Apply optimizations (constant folding, dead code elimination)
- Generate control flow graph for analysis
- Handle nested scopes and function calls

---

### Problem 8: Game World Simulator
**Difficulty: ⭐⭐⭐⭐⭐**

Create a complex game world simulator using the visitor pattern:

**Game Objects:**
- Player (health, inventory, skills, position)
- NPC (dialogues, quests, behavior)
- Enemy (health, damage, AI, loot)
- Item (type, properties, effects)
- Building (type, resources, upgrades)
- Terrain (type, movement_cost, resources)

**Visitors:**
- PhysicsSimulatorVisitor
- AIBehaviorVisitor
- EconomySimulatorVisitor
- QuestManagerVisitor
- SaveGameVisitor

**Advanced Requirements:**
- Simulate physics interactions between objects
- Implement complex AI behaviors with state machines
- Handle economy simulation with supply/demand
- Manage quest dependencies and completion
- Serialize/deserialize entire game state
- Support modding through visitor extensions

**Complex Scenarios:**
```
Game World:
├── Player at (10, 20) with Iron Sword, 100 gold
├── Village
│   ├── Blacksmith NPC (upgrades weapons)
│   ├── Merchant NPC (trades items)
│   └── Quest Giver NPC (provides missions)
├── Forest Area
│   ├── Wolf Enemy (aggressive AI)
│   ├── Treasure Chest (contains rare items)
│   └── Ancient Tree (quest objective)
└── Mountain Pass
    ├── Dragon Boss (complex AI patterns)
    └── Mine (resource generation)
```

**Visitor Interactions:**
- PhysicsSimulator: Handle collision detection, movement
- AIBehavior: Control NPC dialogues, enemy combat
- EconomySimulator: Update prices, resource generation
- QuestManager: Track progress, unlock new quests
- SaveGame: Preserve all object states and relationships

---

## Expert Level Challenges

### Bonus Challenge 1: Multi-Visitor Coordination
**Difficulty: ⭐⭐⭐⭐⭐**

Design a system where multiple visitors need to coordinate and share information:

**Scenario:** Code analysis tool that requires multiple passes
- First pass: Symbol table builder
- Second pass: Type checker (uses symbol table)
- Third pass: Optimization (uses type information)
- Fourth pass: Code generator (uses optimization results)

**Requirements:**
- Visitors must execute in specific order
- Later visitors need results from earlier ones
- Handle circular dependencies
- Support visitor pipelines and branching

### Bonus Challenge 2: Dynamic Visitor Loading
**Difficulty: ⭐⭐⭐⭐⭐**

Create a plugin system using the visitor pattern:

**Requirements:**
- Load visitors dynamically at runtime
- Support visitor versioning and compatibility
- Handle visitor dependencies
- Provide visitor registry and discovery
- Support hot-swapping of visitors

### Bonus Challenge 3: Parallel Visitor Execution
**Difficulty: ⭐⭐⭐⭐⭐**

Implement thread-safe parallel visitor execution:

**Requirements:**
- Execute independent visitors in parallel
- Handle thread-safe state sharing
- Support visitor synchronization points
- Implement visitor result aggregation
- Handle exceptions in parallel execution

---

## Implementation Guidelines

### For Basic Problems (1-2):
- Focus on correct visitor pattern structure
- Implement basic double dispatch
- Handle simple data processing

### For Intermediate Problems (3-5):
- Add error handling and validation
- Implement complex business logic
- Consider performance implications
- Use appropriate data structures

### For Advanced Problems (6-8):
- Design for extensibility and maintainability
- Implement sophisticated algorithms
- Handle edge cases and error conditions
- Consider memory and performance optimization
- Use design patterns combinations

### General Best Practices:
1. **Interface Segregation**: Don't force visitors to implement unused methods
2. **Error Handling**: Provide meaningful error messages
3. **Testing**: Write comprehensive test cases for each visitor
4. **Documentation**: Document visitor behavior and usage
5. **Performance**: Consider the overhead of double dispatch
6. **Thread Safety**: Make visitors thread-safe when needed

### Common Pitfalls to Avoid:
1. **Breaking Encapsulation**: Don't expose too much internal state
2. **Tight Coupling**: Keep visitors independent when possible
3. **Method Explosion**: Use abstract base visitors for common functionality
4. **Memory Leaks**: Properly manage visitor state and resources
5. **Incomplete Updates**: Remember to update all visitors when adding new elements

---

## Solution Approach Tips

### Problem Analysis:
1. **Identify Elements**: What objects need to be visited?
2. **Define Operations**: What operations need to be performed?
3. **Consider Relationships**: How do elements relate to each other?
4. **Plan Extensions**: What future operations might be needed?

### Implementation Strategy:
1. **Start Simple**: Implement basic structure first
2. **Add Complexity**: Gradually add more sophisticated features
3. **Test Incrementally**: Test each visitor independently
4. **Refactor**: Improve design as understanding grows

### Testing Strategy:
1. **Unit Tests**: Test each visitor with simple inputs
2. **Integration Tests**: Test visitor combinations
3. **Edge Cases**: Test with boundary conditions
4. **Performance Tests**: Measure visitor execution time
5. **Error Cases**: Test exception handling