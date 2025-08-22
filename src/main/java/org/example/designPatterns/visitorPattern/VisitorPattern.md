# Visitor Pattern

## Table of Contents
1. [Introduction](#introduction)
2. [What is the Visitor Pattern?](#what-is-the-visitor-pattern)
3. [Problem Statement](#problem-statement)
4. [Solution](#solution)
5. [Structure](#structure)
6. [Implementation](#implementation)
7. [Real-World Examples](#real-world-examples)
8. [Advantages and Disadvantages](#advantages-and-disadvantages)
9. [When to Use](#when-to-use)
10. [When Not to Use](#when-not-to-use)
11. [Best Practices](#best-practices)
12. [Common Pitfalls](#common-pitfalls)
13. [Comparison with Other Patterns](#comparison-with-other-patterns)
14. [Advanced Concepts](#advanced-concepts)

## Introduction

The Visitor Pattern is a behavioral design pattern that allows you to add new operations to existing class hierarchies without modifying the classes. It achieves this by using a technique called double dispatch, where the operation to be performed depends on both the type of the visitor and the type of the element being visited.

This pattern is particularly useful when you have a complex object structure (like a tree) and want to perform various operations on the objects in that structure. Instead of cluttering the object classes with many methods, the Visitor pattern allows you to group related operations into separate visitor classes.

## What is the Visitor Pattern?

The Visitor Pattern represents an operation to be performed on the elements of an object structure. It lets you define a new operation without changing the classes of the elements on which it operates.

### Key Concepts:
- **Separation of Concerns**: Operations are separated from the object structure
- **Double Dispatch**: The method called depends on both the visitor type and element type
- **Open/Closed Principle**: Open for extension (new visitors) but closed for modification
- **External Operations**: Operations are defined outside the element classes

## Problem Statement

Consider these common scenarios:

### Scenario 1: Document Processing
```java
// Without Visitor Pattern - Violates Open/Closed Principle
class Document {
    public void print() { /* print logic */ }
    public void export() { /* export logic */ }
    public void validate() { /* validation logic */ }
    public void compress() { /* compression logic */ }
    // Adding new operations requires modifying this class
}
```

### Scenario 2: Compiler Abstract Syntax Tree
```java
// Without Visitor Pattern
abstract class ASTNode {
    public abstract void generateCode();
    public abstract void optimize();
    public abstract void typeCheck();
    // Adding new operations affects all subclasses
}
```

### Problems:
1. **Violation of Open/Closed Principle**: Adding new operations requires modifying existing classes
2. **Scattered Operations**: Related operations are spread across different classes
3. **Tight Coupling**: Classes become tightly coupled with operations
4. **Maintenance Issues**: Changes to operations affect multiple classes

## Solution

The Visitor Pattern solves these problems by:

1. **Externalizing Operations**: Moving operations out of element classes
2. **Using Double Dispatch**: Determining the correct method based on both visitor and element types
3. **Grouping Related Operations**: Each visitor contains related operations
4. **Enabling Extension**: New operations can be added without modifying existing code

### How it Works:
```java
// Element accepts a visitor
interface Element {
    void accept(Visitor visitor);
}

// Visitor defines operations
interface Visitor {
    void visit(ConcreteElementA element);
    void visit(ConcreteElementB element);
}
```

## Structure

### UML Class Diagram
```
┌─────────────────┐       ┌──────────────────┐
│     Visitor     │◄──────│     Element      │
│                 │       │                  │
│+visit(ElementA) │       │+accept(Visitor)  │
│+visit(ElementB) │       └─────────┬────────┘
└─────────┬───────┘                 │
          │                         │
          │                         │
┌─────────▼───────┐       ┌─────────▼────────┐
│ ConcreteVisitor │       │ ConcreteElement  │
│                 │       │                  │
│+visit(ElementA) │       │+accept(Visitor)  │
│+visit(ElementB) │       │+operationA()     │
└─────────────────┘       └──────────────────┘
```

### Components:
1. **Visitor**: Declares visit methods for each type of ConcreteElement
2. **ConcreteVisitor**: Implements operations to be performed on elements
3. **Element**: Declares accept method that takes a visitor
4. **ConcreteElement**: Implements accept method and may have additional methods
5. **ObjectStructure**: Can enumerate its elements and provide high-level interface

## Implementation

### Basic Implementation

```java
// Visitor interface
interface ShapeVisitor {
    void visit(Circle circle);
    void visit(Rectangle rectangle);
    void visit(Triangle triangle);
}

// Element interface
interface Shape {
    void accept(ShapeVisitor visitor);
}

// Concrete elements
class Circle implements Shape {
    private double radius;
    
    public Circle(double radius) {
        this.radius = radius;
    }
    
    public double getRadius() {
        return radius;
    }
    
    @Override
    public void accept(ShapeVisitor visitor) {
        visitor.visit(this);
    }
}

class Rectangle implements Shape {
    private double width, height;
    
    public Rectangle(double width, double height) {
        this.width = width;
        this.height = height;
    }
    
    public double getWidth() { return width; }
    public double getHeight() { return height; }
    
    @Override
    public void accept(ShapeVisitor visitor) {
        visitor.visit(this);
    }
}

class Triangle implements Shape {
    private double base, height;
    
    public Triangle(double base, double height) {
        this.base = base;
        this.height = height;
    }
    
    public double getBase() { return base; }
    public double getHeight() { return height; }
    
    @Override
    public void accept(ShapeVisitor visitor) {
        visitor.visit(this);
    }
}

// Concrete visitors
class AreaCalculator implements ShapeVisitor {
    private double totalArea = 0;
    
    @Override
    public void visit(Circle circle) {
        double area = Math.PI * circle.getRadius() * circle.getRadius();
        totalArea += area;
        System.out.println("Circle area: " + area);
    }
    
    @Override
    public void visit(Rectangle rectangle) {
        double area = rectangle.getWidth() * rectangle.getHeight();
        totalArea += area;
        System.out.println("Rectangle area: " + area);
    }
    
    @Override
    public void visit(Triangle triangle) {
        double area = 0.5 * triangle.getBase() * triangle.getHeight();
        totalArea += area;
        System.out.println("Triangle area: " + area);
    }
    
    public double getTotalArea() {
        return totalArea;
    }
}

class PerimeterCalculator implements ShapeVisitor {
    private double totalPerimeter = 0;
    
    @Override
    public void visit(Circle circle) {
        double perimeter = 2 * Math.PI * circle.getRadius();
        totalPerimeter += perimeter;
        System.out.println("Circle perimeter: " + perimeter);
    }
    
    @Override
    public void visit(Rectangle rectangle) {
        double perimeter = 2 * (rectangle.getWidth() + rectangle.getHeight());
        totalPerimeter += perimeter;
        System.out.println("Rectangle perimeter: " + perimeter);
    }
    
    @Override
    public void visit(Triangle triangle) {
        // Assuming equilateral triangle for simplicity
        double perimeter = 3 * triangle.getBase();
        totalPerimeter += perimeter;
        System.out.println("Triangle perimeter: " + perimeter);
    }
    
    public double getTotalPerimeter() {
        return totalPerimeter;
    }
}

// Object structure
class Drawing {
    private List<Shape> shapes = new ArrayList<>();
    
    public void addShape(Shape shape) {
        shapes.add(shape);
    }
    
    public void accept(ShapeVisitor visitor) {
        for (Shape shape : shapes) {
            shape.accept(visitor);
        }
    }
}

// Usage
public class VisitorPatternDemo {
    public static void main(String[] args) {
        Drawing drawing = new Drawing();
        drawing.addShape(new Circle(5));
        drawing.addShape(new Rectangle(4, 6));
        drawing.addShape(new Triangle(3, 8));
        
        System.out.println("Calculating areas:");
        AreaCalculator areaCalculator = new AreaCalculator();
        drawing.accept(areaCalculator);
        System.out.println("Total area: " + areaCalculator.getTotalArea());
        
        System.out.println("\nCalculating perimeters:");
        PerimeterCalculator perimeterCalculator = new PerimeterCalculator();
        drawing.accept(perimeterCalculator);
        System.out.println("Total perimeter: " + perimeterCalculator.getTotalPerimeter());
    }
}
```

## Real-World Examples

### 1. Compiler Design
```java
// AST nodes for different language constructs
interface ASTNode {
    void accept(ASTVisitor visitor);
}

class VariableDeclaration implements ASTNode {
    private String type, name;
    
    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}

class FunctionDeclaration implements ASTNode {
    private String returnType, name;
    private List<String> parameters;
    
    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}

// Different compilation phases as visitors
interface ASTVisitor {
    void visit(VariableDeclaration node);
    void visit(FunctionDeclaration node);
}

class TypeChecker implements ASTVisitor {
    @Override
    public void visit(VariableDeclaration node) {
        // Type checking logic
    }
    
    @Override
    public void visit(FunctionDeclaration node) {
        // Function type checking logic
    }
}

class CodeGenerator implements ASTVisitor {
    @Override
    public void visit(VariableDeclaration node) {
        // Generate code for variable declaration
    }
    
    @Override
    public void visit(FunctionDeclaration node) {
        // Generate code for function declaration
    }
}
```

### 2. Document Processing
```java
interface DocumentElement {
    void accept(DocumentVisitor visitor);
}

class TextElement implements DocumentElement {
    private String content;
    
    @Override
    public void accept(DocumentVisitor visitor) {
        visitor.visit(this);
    }
}

class ImageElement implements DocumentElement {
    private String imagePath;
    
    @Override
    public void accept(DocumentVisitor visitor) {
        visitor.visit(this);
    }
}

interface DocumentVisitor {
    void visit(TextElement element);
    void visit(ImageElement element);
}

class PDFExporter implements DocumentVisitor {
    @Override
    public void visit(TextElement element) {
        // Export text to PDF
    }
    
    @Override
    public void visit(ImageElement element) {
        // Export image to PDF
    }
}

class HTMLExporter implements DocumentVisitor {
    @Override
    public void visit(TextElement element) {
        // Export text to HTML
    }
    
    @Override
    public void visit(ImageElement element) {
        // Export image to HTML
    }
}
```

### 3. Tax Calculation System
```java
interface TaxableItem {
    void accept(TaxVisitor visitor);
}

class Product implements TaxableItem {
    private String name;
    private double price;
    private String category;
    
    @Override
    public void accept(TaxVisitor visitor) {
        visitor.visit(this);
    }
}

class Service implements TaxableItem {
    private String description;
    private double cost;
    
    @Override
    public void accept(TaxVisitor visitor) {
        visitor.visit(this);
    }
}

interface TaxVisitor {
    void visit(Product product);
    void visit(Service service);
}

class SalesTaxCalculator implements TaxVisitor {
    private double totalTax = 0;
    
    @Override
    public void visit(Product product) {
        double tax = product.getPrice() * 0.08; // 8% sales tax
        totalTax += tax;
    }
    
    @Override
    public void visit(Service service) {
        double tax = service.getCost() * 0.05; // 5% service tax
        totalTax += tax;
    }
    
    public double getTotalTax() {
        return totalTax;
    }
}
```

## Advantages and Disadvantages

### Advantages
1. **Easy to Add Operations**: New operations can be added without modifying existing element classes
2. **Gathering Related Operations**: Related operations are gathered in one visitor
3. **Working with Different Class Hierarchies**: Can work across unrelated classes
4. **Data Accumulation**: Visitors can accumulate state as they visit elements
5. **Separation of Concerns**: Operations are separated from data structure

### Disadvantages
1. **Adding New Elements**: Adding new concrete elements requires updating all visitors
2. **Circular Dependencies**: Visitors need to know about concrete element classes
3. **Breaking Encapsulation**: Elements may need to expose internal details to visitors
4. **Complexity**: Can make simple operations more complex
5. **Hard to Understand**: The double dispatch mechanism can be confusing

## When to Use

Use the Visitor Pattern when:

1. **Complex Object Structure**: You have a complex object structure with many different types
2. **Many Unrelated Operations**: You need to perform many unrelated operations on objects
3. **Operations Change Frequently**: Operations are added more frequently than new element types
4. **Clean Separation**: You want to keep operations separate from the object structure
5. **Different Algorithms**: You need to apply different algorithms to the same structure

### Good Use Cases:
- Compiler design (AST processing)
- Document processing systems
- Tax calculation systems
- File system operations
- Report generation
- Data export/import operations

## When Not to Use

Avoid the Visitor Pattern when:

1. **Simple Operations**: Operations are simple and don't justify the complexity
2. **Frequent Element Changes**: New element types are added frequently
3. **Privacy Concerns**: Elements contain sensitive data that shouldn't be exposed
4. **Performance Critical**: The double dispatch overhead is unacceptable
5. **Small Object Structures**: The object structure is small and unlikely to grow

## Best Practices

### 1. Use Meaningful Naming
```java
// Good
interface DocumentProcessor {
    void processText(TextElement element);
    void processImage(ImageElement element);
}

// Bad
interface Visitor {
    void visit(Element1 e1);
    void visit(Element2 e2);
}
```

### 2. Handle Return Values Properly
```java
// For operations that need return values
interface CalculationVisitor {
    void visit(Circle circle);
    void visit(Rectangle rectangle);
    double getResult();
}

// Or use generic visitors
interface GenericVisitor<T> {
    T visit(Circle circle);
    T visit(Rectangle rectangle);
}
```

### 3. Provide Default Implementations
```java
abstract class BaseShapeVisitor implements ShapeVisitor {
    @Override
    public void visit(Circle circle) {
        // Default implementation or throw exception
        throw new UnsupportedOperationException("Circle not supported");
    }
    
    @Override
    public void visit(Rectangle rectangle) {
        // Default implementation
    }
    
    @Override
    public void visit(Triangle triangle) {
        // Default implementation
    }
}
```

### 4. Use Visitor with Composite Pattern
```java
class CompositeShape implements Shape {
    private List<Shape> children = new ArrayList<>();
    
    @Override
    public void accept(ShapeVisitor visitor) {
        visitor.visit(this);
        for (Shape child : children) {
            child.accept(visitor);
        }
    }
}

interface ShapeVisitor {
    void visit(Circle circle);
    void visit(Rectangle rectangle);
    void visit(CompositeShape composite);
}
```

### 5. Thread Safety Considerations
```java
class ThreadSafeAreaCalculator implements ShapeVisitor {
    private final AtomicReference<Double> totalArea = new AtomicReference<>(0.0);
    
    @Override
    public void visit(Circle circle) {
        double area = Math.PI * circle.getRadius() * circle.getRadius();
        totalArea.updateAndGet(current -> current + area);
    }
    
    // Other visit methods...
    
    public double getTotalArea() {
        return totalArea.get();
    }
}
```

## Common Pitfalls

### 1. Forgetting to Update All Visitors
```java
// When adding new element type, all visitors must be updated
class Polygon implements Shape {
    @Override
    public void accept(ShapeVisitor visitor) {
        visitor.visit(this); // Compilation error if visitors don't support Polygon
    }
}

// All existing visitors need to be updated:
interface ShapeVisitor {
    void visit(Circle circle);
    void visit(Rectangle rectangle);
    void visit(Triangle triangle);
    void visit(Polygon polygon); // Must add this
}
```

### 2. Breaking Encapsulation
```java
// Avoid exposing too much internal state
class BadCircle implements Shape {
    private double radius;
    private Point center;
    private Color color;
    
    // Don't expose everything just for visitors
    public double getRadius() { return radius; }
    public Point getCenter() { return center; }
    public Color getColor() { return color; }
}

// Better approach - expose only what's needed
class GoodCircle implements Shape {
    private double radius;
    
    public double getRadius() { return radius; }
    
    // Let visitor work with limited interface
}
```

### 3. Not Handling Null Elements
```java
class SafeAreaCalculator implements ShapeVisitor {
    @Override
    public void visit(Circle circle) {
        if (circle != null && circle.getRadius() > 0) {
            // Calculate area
        }
    }
}
```

### 4. Overcomplicating Simple Operations
```java
// Don't use Visitor for simple operations
// Bad - unnecessary complexity
interface SimpleVisitor {
    void visit(String str);
}

class PrintVisitor implements SimpleVisitor {
    @Override
    public void visit(String str) {
        System.out.println(str);
    }
}

// Good - simple operation doesn't need visitor
public void printString(String str) {
    System.out.println(str);
}
```

## Comparison with Other Patterns

### Visitor vs Strategy Pattern
| Aspect | Visitor | Strategy |
|--------|---------|----------|
| Purpose | Add operations to object structure | Vary algorithm for single operation |
| Number of Operations | Many operations | One operation, multiple algorithms |
| Object Structure | Complex hierarchy | Usually single object |
| Adding Operations | Easy | Not applicable |
| Adding Elements | Difficult | Easy |

### Visitor vs Command Pattern
| Aspect | Visitor | Command |
|--------|---------|---------|
| Intent | Process object structure | Encapsulate requests |
| Structure | Hierarchical objects | Individual requests |
| Execution | Immediate | Can be deferred |
| Undo Support | Not inherent | Built-in |

### Visitor vs Observer Pattern
| Aspect | Visitor | Observer |
|--------|---------|----------|
| Coupling | Visitor knows elements | Subject knows observers |
| Communication | Active (visitor initiates) | Reactive (observer responds) |
| Purpose | Process structure | Handle events |
| Relationship | Temporary | Long-term |

## Advanced Concepts

### 1. Generic Visitors
```java
interface GenericVisitor<T> {
    T visit(Circle circle);
    T visit(Rectangle rectangle);
    T visit(Triangle triangle);
}

class AreaCalculatorGeneric implements GenericVisitor<Double> {
    @Override
    public Double visit(Circle circle) {
        return Math.PI * circle.getRadius() * circle.getRadius();
    }
    
    @Override
    public Double visit(Rectangle rectangle) {
        return rectangle.getWidth() * rectangle.getHeight();
    }
    
    @Override
    public Double visit(Triangle triangle) {
        return 0.5 * triangle.getBase() * triangle.getHeight();
    }
}
```

### 2. Reflective Visitor Pattern
```java
public abstract class ReflectiveVisitor {
    public void visit(Object obj) {
        String methodName = "visit" + obj.getClass().getSimpleName();
        try {
            Method method = this.getClass().getMethod(methodName, obj.getClass());
            method.invoke(this, obj);
        } catch (Exception e) {
            visitDefault(obj);
        }
    }
    
    protected void visitDefault(Object obj) {
        // Default behavior
    }
}

class ReflectiveShapeVisitor extends ReflectiveVisitor {
    public void visitCircle(Circle circle) {
        // Handle circle
    }
    
    public void visitRectangle(Rectangle rectangle) {
        // Handle rectangle
    }
}
```

### 3. Acyclic Visitor Pattern
```java
// Base visitor marker interface
interface Visitor {
}

// Specific visitor interfaces
interface CircleVisitor extends Visitor {
    void visit(Circle circle);
}

interface RectangleVisitor extends Visitor {
    void visit(Rectangle rectangle);
}

// Elements check visitor type
class Circle implements Shape {
    @Override
    public void accept(Visitor visitor) {
        if (visitor instanceof CircleVisitor) {
            ((CircleVisitor) visitor).visit(this);
        }
    }
}

// Concrete visitor implements only needed interfaces
class AreaCalculator implements CircleVisitor, RectangleVisitor {
    @Override
    public void visit(Circle circle) {
        // Calculate circle area
    }
    
    @Override
    public void visit(Rectangle rectangle) {
        // Calculate rectangle area
    }
}
```

### 4. Visitor with Builder Pattern
```java
class VisitorChain {
    private List<ShapeVisitor> visitors = new ArrayList<>();
    
    public VisitorChain add(ShapeVisitor visitor) {
        visitors.add(visitor);
        return this;
    }
    
    public void execute(Shape shape) {
        for (ShapeVisitor visitor : visitors) {
            shape.accept(visitor);
        }
    }
}

// Usage
VisitorChain.builder()
    .add(new AreaCalculator())
    .add(new PerimeterCalculator())
    .add(new DrawingVisitor())
    .execute(shape);
```

The Visitor Pattern is a powerful tool for processing complex object structures while maintaining separation between data and operations. When used appropriately, it provides excellent extensibility and maintainability benefits, though it should be applied judiciously due to its complexity.