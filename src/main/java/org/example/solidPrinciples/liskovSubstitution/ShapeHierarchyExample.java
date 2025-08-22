package org.example.solidPrinciples.liskovSubstitution;

import java.util.*;

/**
 * Shape Hierarchy - Liskov Substitution Principle Example
 * 
 * This example demonstrates the classic Rectangle-Square problem and shows how to
 * properly design class hierarchies that follow LSP.
 * 
 * Key Concepts:
 * - Behavioral subtyping
 * - Invariant preservation
 * - Precondition and postcondition rules
 * - Proper abstraction design
 * 
 * LSP Rules:
 * 1. Preconditions cannot be strengthened in a subtype
 * 2. Postconditions cannot be weakened in a subtype
 * 3. Invariants must be preserved
 * 4. History constraint (new methods shouldn't change expected behavior)
 */
public class ShapeHierarchyExample {
    
    /**
     * BEFORE: Classic Rectangle-Square problem violating LSP
     */
    public static class ViolatingExample {
        
        public static class Rectangle {
            protected int width;
            protected int height;
            
            public Rectangle(int width, int height) {
                this.width = width;
                this.height = height;
            }
            
            public void setWidth(int width) {
                this.width = width;
            }
            
            public void setHeight(int height) {
                this.height = height;
            }
            
            public int getWidth() {
                return width;
            }
            
            public int getHeight() {
                return height;
            }
            
            public int getArea() {
                return width * height;
            }
            
            public int getPerimeter() {
                return 2 * (width + height);
            }
        }
        
        // Square violates LSP when it extends Rectangle
        public static class Square extends Rectangle {
            
            public Square(int side) {
                super(side, side);
            }
            
            @Override
            public void setWidth(int width) {
                // Violates LSP - changes expected behavior
                // Client expects only width to change, but height changes too
                this.width = width;
                this.height = width; // Maintaining square invariant
            }
            
            @Override
            public void setHeight(int height) {
                // Violates LSP - changes expected behavior
                // Client expects only height to change, but width changes too
                this.width = height; // Maintaining square invariant
                this.height = height;
            }
        }
        
        // This function demonstrates the LSP violation
        public static void demonstrateViolation() {
            System.out.println("=== LSP VIOLATION DEMONSTRATION ===\n");
            
            Rectangle rect = new Rectangle(5, 10);
            testRectangleBehavior(rect);
            
            // Square breaks the expected behavior when used as Rectangle
            Rectangle square = new Square(5);
            testRectangleBehavior(square); // This will fail!
        }
        
        private static void testRectangleBehavior(Rectangle rect) {
            System.out.println("Testing rectangle behavior:");
            System.out.println("Initial: width=" + rect.getWidth() + ", height=" + rect.getHeight());
            
            rect.setWidth(20);
            System.out.println("After setWidth(20): width=" + rect.getWidth() + ", height=" + rect.getHeight());
            
            rect.setHeight(10);
            System.out.println("After setHeight(10): width=" + rect.getWidth() + ", height=" + rect.getHeight());
            
            // Expected: width=20, height=10, area=200
            // For Square: width=10, height=10, area=100 (WRONG!)
            int expectedArea = 20 * 10;
            int actualArea = rect.getArea();
            
            if (expectedArea == actualArea) {
                System.out.println("✓ Area calculation correct: " + actualArea);
            } else {
                System.out.println("✗ LSP VIOLATION! Expected area: " + expectedArea + ", Got: " + actualArea);
            }
            System.out.println();
        }
    }
    
    /**
     * AFTER: Properly designed shape hierarchy following LSP
     */
    
    // Immutable shape design - prevents LSP violations
    public interface Shape {
        double getArea();
        double getPerimeter();
        String getShapeType();
        Shape scale(double factor);
        boolean contains(Point point);
    }
    
    public static class Point {
        private final double x;
        private final double y;
        
        public Point(double x, double y) {
            this.x = x;
            this.y = y;
        }
        
        public double getX() { return x; }
        public double getY() { return y; }
        
        public double distanceTo(Point other) {
            double dx = x - other.x;
            double dy = y - other.y;
            return Math.sqrt(dx * dx + dy * dy);
        }
    }
    
    // Rectangle as an immutable shape
    public static class Rectangle implements Shape {
        private final double width;
        private final double height;
        private final Point origin;
        
        public Rectangle(double width, double height) {
            this(width, height, new Point(0, 0));
        }
        
        public Rectangle(double width, double height, Point origin) {
            if (width <= 0 || height <= 0) {
                throw new IllegalArgumentException("Width and height must be positive");
            }
            this.width = width;
            this.height = height;
            this.origin = origin;
        }
        
        public double getWidth() { return width; }
        public double getHeight() { return height; }
        
        @Override
        public double getArea() {
            return width * height;
        }
        
        @Override
        public double getPerimeter() {
            return 2 * (width + height);
        }
        
        @Override
        public String getShapeType() {
            return "Rectangle";
        }
        
        @Override
        public Rectangle scale(double factor) {
            if (factor <= 0) {
                throw new IllegalArgumentException("Scale factor must be positive");
            }
            return new Rectangle(width * factor, height * factor, origin);
        }
        
        @Override
        public boolean contains(Point point) {
            return point.getX() >= origin.getX() && 
                   point.getX() <= origin.getX() + width &&
                   point.getY() >= origin.getY() && 
                   point.getY() <= origin.getY() + height;
        }
        
        // Rectangle-specific method
        public Rectangle withWidth(double newWidth) {
            return new Rectangle(newWidth, height, origin);
        }
        
        public Rectangle withHeight(double newHeight) {
            return new Rectangle(width, newHeight, origin);
        }
    }
    
    // Square as an independent shape, not extending Rectangle
    public static class Square implements Shape {
        private final double side;
        private final Point origin;
        
        public Square(double side) {
            this(side, new Point(0, 0));
        }
        
        public Square(double side, Point origin) {
            if (side <= 0) {
                throw new IllegalArgumentException("Side must be positive");
            }
            this.side = side;
            this.origin = origin;
        }
        
        public double getSide() { return side; }
        
        @Override
        public double getArea() {
            return side * side;
        }
        
        @Override
        public double getPerimeter() {
            return 4 * side;
        }
        
        @Override
        public String getShapeType() {
            return "Square";
        }
        
        @Override
        public Square scale(double factor) {
            if (factor <= 0) {
                throw new IllegalArgumentException("Scale factor must be positive");
            }
            return new Square(side * factor, origin);
        }
        
        @Override
        public boolean contains(Point point) {
            return point.getX() >= origin.getX() && 
                   point.getX() <= origin.getX() + side &&
                   point.getY() >= origin.getY() && 
                   point.getY() <= origin.getY() + side;
        }
        
        // Square-specific method
        public Square withSide(double newSide) {
            return new Square(newSide, origin);
        }
    }
    
    public static class Circle implements Shape {
        private final double radius;
        private final Point center;
        
        public Circle(double radius) {
            this(radius, new Point(0, 0));
        }
        
        public Circle(double radius, Point center) {
            if (radius <= 0) {
                throw new IllegalArgumentException("Radius must be positive");
            }
            this.radius = radius;
            this.center = center;
        }
        
        public double getRadius() { return radius; }
        public Point getCenter() { return center; }
        
        @Override
        public double getArea() {
            return Math.PI * radius * radius;
        }
        
        @Override
        public double getPerimeter() {
            return 2 * Math.PI * radius;
        }
        
        @Override
        public String getShapeType() {
            return "Circle";
        }
        
        @Override
        public Circle scale(double factor) {
            if (factor <= 0) {
                throw new IllegalArgumentException("Scale factor must be positive");
            }
            return new Circle(radius * factor, center);
        }
        
        @Override
        public boolean contains(Point point) {
            return center.distanceTo(point) <= radius;
        }
        
        // Circle-specific method
        public Circle withRadius(double newRadius) {
            return new Circle(newRadius, center);
        }
    }
    
    public static class Triangle implements Shape {
        private final Point a, b, c;
        
        public Triangle(Point a, Point b, Point c) {
            this.a = a;
            this.b = b;
            this.c = c;
            
            // Validate that points form a valid triangle
            if (isCollinear(a, b, c)) {
                throw new IllegalArgumentException("Points must not be collinear");
            }
        }
        
        private boolean isCollinear(Point p1, Point p2, Point p3) {
            // Check if area is zero (points are collinear)
            double area = Math.abs((p2.getX() - p1.getX()) * (p3.getY() - p1.getY()) - 
                                  (p3.getX() - p1.getX()) * (p2.getY() - p1.getY()));
            return area < 0.0001; // Small epsilon for floating point comparison
        }
        
        @Override
        public double getArea() {
            // Using Heron's formula
            double ab = a.distanceTo(b);
            double bc = b.distanceTo(c);
            double ca = c.distanceTo(a);
            double s = (ab + bc + ca) / 2; // Semi-perimeter
            return Math.sqrt(s * (s - ab) * (s - bc) * (s - ca));
        }
        
        @Override
        public double getPerimeter() {
            return a.distanceTo(b) + b.distanceTo(c) + c.distanceTo(a);
        }
        
        @Override
        public String getShapeType() {
            return "Triangle";
        }
        
        @Override
        public Triangle scale(double factor) {
            if (factor <= 0) {
                throw new IllegalArgumentException("Scale factor must be positive");
            }
            return new Triangle(
                new Point(a.getX() * factor, a.getY() * factor),
                new Point(b.getX() * factor, b.getY() * factor),
                new Point(c.getX() * factor, c.getY() * factor)
            );
        }
        
        @Override
        public boolean contains(Point point) {
            // Using barycentric coordinates
            double areaTotal = getArea();
            Triangle t1 = new Triangle(point, b, c);
            Triangle t2 = new Triangle(a, point, c);
            Triangle t3 = new Triangle(a, b, point);
            
            double areaSum = t1.getArea() + t2.getArea() + t3.getArea();
            return Math.abs(areaTotal - areaSum) < 0.0001;
        }
    }
    
    // Alternative hierarchy using composition for rectangles and squares
    public static class QuadrilateralProperties {
        public static class Dimensions {
            private final double width;
            private final double height;
            
            public Dimensions(double width, double height) {
                this.width = width;
                this.height = height;
            }
            
            public double getWidth() { return width; }
            public double getHeight() { return height; }
            
            public boolean isSquare() {
                return Math.abs(width - height) < 0.0001;
            }
        }
        
        public static class Quadrilateral implements Shape {
            private final Dimensions dimensions;
            private final Point origin;
            
            public Quadrilateral(Dimensions dimensions, Point origin) {
                this.dimensions = dimensions;
                this.origin = origin;
            }
            
            public static Quadrilateral createRectangle(double width, double height) {
                return new Quadrilateral(new Dimensions(width, height), new Point(0, 0));
            }
            
            public static Quadrilateral createSquare(double side) {
                return new Quadrilateral(new Dimensions(side, side), new Point(0, 0));
            }
            
            @Override
            public double getArea() {
                return dimensions.getWidth() * dimensions.getHeight();
            }
            
            @Override
            public double getPerimeter() {
                return 2 * (dimensions.getWidth() + dimensions.getHeight());
            }
            
            @Override
            public String getShapeType() {
                return dimensions.isSquare() ? "Square" : "Rectangle";
            }
            
            @Override
            public Quadrilateral scale(double factor) {
                return new Quadrilateral(
                    new Dimensions(dimensions.getWidth() * factor, 
                                 dimensions.getHeight() * factor),
                    origin
                );
            }
            
            @Override
            public boolean contains(Point point) {
                return point.getX() >= origin.getX() && 
                       point.getX() <= origin.getX() + dimensions.getWidth() &&
                       point.getY() >= origin.getY() && 
                       point.getY() <= origin.getY() + dimensions.getHeight();
            }
        }
    }
    
    // Shape processor that works correctly with all shapes (LSP compliant)
    public static class ShapeProcessor {
        
        public static void processShapes(List<Shape> shapes) {
            System.out.println("\n=== Processing Shapes (LSP Compliant) ===\n");
            
            for (Shape shape : shapes) {
                System.out.println("Shape: " + shape.getShapeType());
                System.out.println("  Area: " + String.format("%.2f", shape.getArea()));
                System.out.println("  Perimeter: " + String.format("%.2f", shape.getPerimeter()));
                
                // All shapes can be scaled
                Shape scaled = shape.scale(2.0);
                System.out.println("  Scaled (2x) area: " + String.format("%.2f", scaled.getArea()));
                
                // All shapes can check point containment
                Point testPoint = new Point(1, 1);
                System.out.println("  Contains (1,1): " + shape.contains(testPoint));
                
                System.out.println();
            }
        }
        
        public static double calculateTotalArea(List<Shape> shapes) {
            return shapes.stream()
                .mapToDouble(Shape::getArea)
                .sum();
        }
        
        public static List<Shape> filterByMinArea(List<Shape> shapes, double minArea) {
            List<Shape> result = new ArrayList<>();
            for (Shape shape : shapes) {
                if (shape.getArea() >= minArea) {
                    result.add(shape);
                }
            }
            return result;
        }
        
        // This method works correctly with all shapes thanks to LSP
        public static void transformShapes(List<Shape> shapes) {
            System.out.println("=== Shape Transformations ===\n");
            
            for (Shape shape : shapes) {
                // Scale up
                Shape larger = shape.scale(1.5);
                System.out.println(shape.getShapeType() + ":");
                System.out.println("  Original area: " + String.format("%.2f", shape.getArea()));
                System.out.println("  Scaled area: " + String.format("%.2f", larger.getArea()));
                
                // Verify scaling postcondition
                double expectedArea = shape.getArea() * 1.5 * 1.5;
                if (Math.abs(larger.getArea() - expectedArea) < 0.01) {
                    System.out.println("  ✓ Scaling invariant preserved");
                } else {
                    System.out.println("  ✗ Scaling invariant violated!");
                }
            }
        }
    }
    
    // Advanced LSP example with behavioral subtyping
    public interface DrawableShape extends Shape {
        void draw(GraphicsContext context);
        void setColor(Color color);
        Color getColor();
    }
    
    public static class GraphicsContext {
        private final List<String> operations = new ArrayList<>();
        
        public void moveTo(double x, double y) {
            operations.add(String.format("MoveTo(%.1f, %.1f)", x, y));
        }
        
        public void lineTo(double x, double y) {
            operations.add(String.format("LineTo(%.1f, %.1f)", x, y));
        }
        
        public void arc(double cx, double cy, double radius, double startAngle, double endAngle) {
            operations.add(String.format("Arc(center=(%.1f,%.1f), r=%.1f, angles=%.1f-%.1f)", 
                                        cx, cy, radius, startAngle, endAngle));
        }
        
        public void fill(Color color) {
            operations.add("Fill(" + color + ")");
        }
        
        public void stroke(Color color) {
            operations.add("Stroke(" + color + ")");
        }
        
        public void render() {
            System.out.println("Graphics operations:");
            operations.forEach(op -> System.out.println("  " + op));
        }
    }
    
    public enum Color {
        RED, GREEN, BLUE, BLACK, WHITE, YELLOW
    }
    
    public static abstract class AbstractDrawableShape implements DrawableShape {
        protected Color color = Color.BLACK;
        
        @Override
        public void setColor(Color color) {
            if (color == null) {
                throw new IllegalArgumentException("Color cannot be null");
            }
            this.color = color;
        }
        
        @Override
        public Color getColor() {
            return color;
        }
    }
    
    public static class DrawableRectangle extends AbstractDrawableShape {
        private final Rectangle rectangle;
        
        public DrawableRectangle(double width, double height) {
            this.rectangle = new Rectangle(width, height);
        }
        
        @Override
        public void draw(GraphicsContext context) {
            context.moveTo(0, 0);
            context.lineTo(rectangle.getWidth(), 0);
            context.lineTo(rectangle.getWidth(), rectangle.getHeight());
            context.lineTo(0, rectangle.getHeight());
            context.lineTo(0, 0);
            context.fill(color);
        }
        
        @Override
        public double getArea() { return rectangle.getArea(); }
        
        @Override
        public double getPerimeter() { return rectangle.getPerimeter(); }
        
        @Override
        public String getShapeType() { return "DrawableRectangle"; }
        
        @Override
        public DrawableRectangle scale(double factor) {
            Rectangle scaled = rectangle.scale(factor);
            DrawableRectangle result = new DrawableRectangle(scaled.getWidth(), scaled.getHeight());
            result.setColor(this.color);
            return result;
        }
        
        @Override
        public boolean contains(Point point) { return rectangle.contains(point); }
    }
    
    public static class DrawableCircle extends AbstractDrawableShape {
        private final Circle circle;
        
        public DrawableCircle(double radius) {
            this.circle = new Circle(radius);
        }
        
        @Override
        public void draw(GraphicsContext context) {
            context.arc(circle.getCenter().getX(), circle.getCenter().getY(), 
                       circle.getRadius(), 0, 360);
            context.fill(color);
        }
        
        @Override
        public double getArea() { return circle.getArea(); }
        
        @Override
        public double getPerimeter() { return circle.getPerimeter(); }
        
        @Override
        public String getShapeType() { return "DrawableCircle"; }
        
        @Override
        public DrawableCircle scale(double factor) {
            Circle scaled = circle.scale(factor);
            DrawableCircle result = new DrawableCircle(scaled.getRadius());
            result.setColor(this.color);
            return result;
        }
        
        @Override
        public boolean contains(Point point) { return circle.contains(point); }
    }
    
    /**
     * Demonstration of proper LSP implementation
     */
    public static void main(String[] args) {
        System.out.println("=== Shape Hierarchy - LSP Demo ===\n");
        
        // First, show the violation
        System.out.println("1. DEMONSTRATING LSP VIOLATION");
        System.out.println("─".repeat(50));
        ViolatingExample.demonstrateViolation();
        
        // Now show the correct implementation
        System.out.println("2. CORRECT LSP IMPLEMENTATION");
        System.out.println("─".repeat(50));
        
        List<Shape> shapes = Arrays.asList(
            new Rectangle(10, 5),
            new Square(7),
            new Circle(4),
            new Triangle(new Point(0, 0), new Point(4, 0), new Point(2, 3))
        );
        
        ShapeProcessor.processShapes(shapes);
        
        System.out.println("3. POLYMORPHIC OPERATIONS");
        System.out.println("─".repeat(50));
        ShapeProcessor.transformShapes(shapes);
        
        System.out.println("\n4. SHAPE STATISTICS");
        System.out.println("─".repeat(50));
        System.out.println("Total area of all shapes: " + 
                         String.format("%.2f", ShapeProcessor.calculateTotalArea(shapes)));
        
        List<Shape> largeShapes = ShapeProcessor.filterByMinArea(shapes, 50);
        System.out.println("Shapes with area >= 50: " + largeShapes.size());
        
        System.out.println("\n5. DRAWABLE SHAPES (Extended Hierarchy)");
        System.out.println("─".repeat(50));
        
        List<DrawableShape> drawableShapes = Arrays.asList(
            new DrawableRectangle(8, 4),
            new DrawableCircle(3)
        );
        
        GraphicsContext context = new GraphicsContext();
        for (DrawableShape shape : drawableShapes) {
            shape.setColor(Color.BLUE);
            System.out.println("\nDrawing " + shape.getShapeType() + ":");
            shape.draw(context);
            context.render();
        }
        
        System.out.println("\n6. LSP BENEFITS IN THIS DESIGN");
        System.out.println("─".repeat(50));
        System.out.println("✓ All shapes can be used interchangeably in collections");
        System.out.println("✓ Common operations work correctly for all shapes");
        System.out.println("✓ No unexpected behavior when substituting subtypes");
        System.out.println("✓ Immutability prevents state-related LSP violations");
        System.out.println("✓ Each shape maintains its own invariants");
        System.out.println("✓ Scaling preserves the shape type and proportions");
        System.out.println("✓ Client code doesn't need to know specific shape types");
        
        System.out.println("\n=== Demo Complete ===");
    }
}