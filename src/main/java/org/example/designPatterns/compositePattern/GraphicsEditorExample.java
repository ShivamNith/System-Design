package org.example.designPatterns.compositePattern;

import java.util.*;

/**
 * Graphics Editor Composite Pattern Example
 * 
 * This example demonstrates the Composite Pattern using a graphics editor application.
 * Basic shapes (Circle, Rectangle, Line, Text) are leaf objects, Groups are composite objects,
 * and both implement the DrawingComponent interface uniformly.
 * 
 * Features:
 * - Hierarchical drawing structure with groups
 * - Uniform operations on shapes and groups
 * - Transformations (move, scale, rotate)
 * - Layer management and selection
 * - Export and styling operations
 */

// Component interface
interface DrawingComponent {
    void draw();
    void move(int deltaX, int deltaY);
    void scale(double factor);
    void rotate(double angle);
    Rectangle getBounds();
    DrawingComponent copy();
    String getDescription();
    void setColor(Color color);
    Color getColor();
    String getId();
    void setVisible(boolean visible);
    boolean isVisible();
}

// Basic utility classes
class Point {
    public int x, y;
    
    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    public Point copy() {
        return new Point(x, y);
    }
    
    public void move(int deltaX, int deltaY) {
        x += deltaX;
        y += deltaY;
    }
    
    public void rotate(Point center, double angle) {
        double rad = Math.toRadians(angle);
        double cos = Math.cos(rad);
        double sin = Math.sin(rad);
        
        int dx = x - center.x;
        int dy = y - center.y;
        
        x = center.x + (int) (dx * cos - dy * sin);
        y = center.y + (int) (dx * sin + dy * cos);
    }
    
    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}

class Rectangle {
    public int x, y, width, height;
    
    public Rectangle(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
    
    public Point getCenter() {
        return new Point(x + width / 2, y + height / 2);
    }
    
    public boolean contains(Point point) {
        return point.x >= x && point.x <= x + width && 
               point.y >= y && point.y <= y + height;
    }
    
    @Override
    public String toString() {
        return "Rectangle[x=" + x + ", y=" + y + ", w=" + width + ", h=" + height + "]";
    }
}

class Color {
    public int r, g, b, a; // RGBA
    
    public Color(int r, int g, int b) {
        this(r, g, b, 255);
    }
    
    public Color(int r, int g, int b, int a) {
        this.r = Math.max(0, Math.min(255, r));
        this.g = Math.max(0, Math.min(255, g));
        this.b = Math.max(0, Math.min(255, b));
        this.a = Math.max(0, Math.min(255, a));
    }
    
    public static final Color BLACK = new Color(0, 0, 0);
    public static final Color WHITE = new Color(255, 255, 255);
    public static final Color RED = new Color(255, 0, 0);
    public static final Color GREEN = new Color(0, 255, 0);
    public static final Color BLUE = new Color(0, 0, 255);
    public static final Color YELLOW = new Color(255, 255, 0);
    public static final Color CYAN = new Color(0, 255, 255);
    public static final Color MAGENTA = new Color(255, 0, 255);
    
    @Override
    public String toString() {
        return "Color(r=" + r + ", g=" + g + ", b=" + b + (a < 255 ? ", a=" + a : "") + ")";
    }
}

// Leaf implementations - Basic Shapes
class Circle implements DrawingComponent {
    private String id;
    private Point center;
    private int radius;
    private Color color;
    private boolean visible;
    private String style;
    private int strokeWidth;
    
    public Circle(Point center, int radius) {
        this.id = "Circle-" + System.currentTimeMillis() + "-" + (int)(Math.random() * 1000);
        this.center = center.copy();
        this.radius = radius;
        this.color = Color.BLACK;
        this.visible = true;
        this.style = "solid";
        this.strokeWidth = 1;
    }
    
    @Override
    public String getId() {
        return id;
    }
    
    @Override
    public void draw() {
        if (!visible) return;
        
        System.out.println("🔵 Drawing Circle [" + id + "]: center=" + center + 
                         ", radius=" + radius + ", color=" + color + 
                         ", style=" + style + ", stroke=" + strokeWidth + "px");
    }
    
    @Override
    public void move(int deltaX, int deltaY) {
        center.move(deltaX, deltaY);
        System.out.println("📦 Moved circle " + id + " to center=" + center);
    }
    
    @Override
    public void scale(double factor) {
        radius = (int) (radius * factor);
        System.out.println("🔍 Scaled circle " + id + ": new radius=" + radius);
    }
    
    @Override
    public void rotate(double angle) {
        System.out.println("🔄 Circle rotation has no visual effect (angle=" + angle + " degrees)");
    }
    
    @Override
    public Rectangle getBounds() {
        return new Rectangle(center.x - radius, center.y - radius, 2 * radius, 2 * radius);
    }
    
    @Override
    public DrawingComponent copy() {
        Circle copy = new Circle(center, radius);
        copy.color = this.color;
        copy.style = this.style;
        copy.strokeWidth = this.strokeWidth;
        copy.visible = this.visible;
        return copy;
    }
    
    @Override
    public String getDescription() {
        return "Circle at " + center + " with radius " + radius;
    }
    
    @Override
    public void setColor(Color color) {
        this.color = color;
    }
    
    @Override
    public Color getColor() {
        return color;
    }
    
    @Override
    public void setVisible(boolean visible) {
        this.visible = visible;
        System.out.println(visible ? "👁️ Showed" : "🙈 Hidden" + " circle " + id);
    }
    
    @Override
    public boolean isVisible() {
        return visible;
    }
    
    public void setStyle(String style) {
        this.style = style;
    }
    
    public void setStrokeWidth(int width) {
        this.strokeWidth = width;
    }
    
    public Point getCenter() {
        return center.copy();
    }
    
    public int getRadius() {
        return radius;
    }
}

class RectangleShape implements DrawingComponent {
    private String id;
    private Point topLeft;
    private int width, height;
    private Color color;
    private boolean visible;
    private boolean filled;
    private int strokeWidth;
    
    public RectangleShape(Point topLeft, int width, int height) {
        this.id = "Rectangle-" + System.currentTimeMillis() + "-" + (int)(Math.random() * 1000);
        this.topLeft = topLeft.copy();
        this.width = width;
        this.height = height;
        this.color = Color.BLACK;
        this.visible = true;
        this.filled = false;
        this.strokeWidth = 1;
    }
    
    @Override
    public String getId() {
        return id;
    }
    
    @Override
    public void draw() {
        if (!visible) return;
        
        System.out.println("🔲 Drawing Rectangle [" + id + "]: topLeft=" + topLeft + 
                         ", size=" + width + "x" + height + ", color=" + color + 
                         ", filled=" + filled + ", stroke=" + strokeWidth + "px");
    }
    
    @Override
    public void move(int deltaX, int deltaY) {
        topLeft.move(deltaX, deltaY);
        System.out.println("📦 Moved rectangle " + id + " to topLeft=" + topLeft);
    }
    
    @Override
    public void scale(double factor) {
        width = (int) (width * factor);
        height = (int) (height * factor);
        System.out.println("🔍 Scaled rectangle " + id + ": new size=" + width + "x" + height);
    }
    
    @Override
    public void rotate(double angle) {
        System.out.println("🔄 Rotating rectangle " + id + " by " + angle + " degrees around center");
        // In a real implementation, this would transform the rectangle
    }
    
    @Override
    public Rectangle getBounds() {
        return new Rectangle(topLeft.x, topLeft.y, width, height);
    }
    
    @Override
    public DrawingComponent copy() {
        RectangleShape copy = new RectangleShape(topLeft, width, height);
        copy.color = this.color;
        copy.filled = this.filled;
        copy.strokeWidth = this.strokeWidth;
        copy.visible = this.visible;
        return copy;
    }
    
    @Override
    public String getDescription() {
        return "Rectangle at " + topLeft + " size " + width + "x" + height;
    }
    
    @Override
    public void setColor(Color color) {
        this.color = color;
    }
    
    @Override
    public Color getColor() {
        return color;
    }
    
    @Override
    public void setVisible(boolean visible) {
        this.visible = visible;
        System.out.println((visible ? "👁️ Showed" : "🙈 Hidden") + " rectangle " + id);
    }
    
    @Override
    public boolean isVisible() {
        return visible;
    }
    
    public void setFilled(boolean filled) {
        this.filled = filled;
    }
    
    public void setStrokeWidth(int width) {
        this.strokeWidth = width;
    }
}

class Line implements DrawingComponent {
    private String id;
    private Point start, end;
    private Color color;
    private boolean visible;
    private int thickness;
    private String style;
    
    public Line(Point start, Point end) {
        this.id = "Line-" + System.currentTimeMillis() + "-" + (int)(Math.random() * 1000);
        this.start = start.copy();
        this.end = end.copy();
        this.color = Color.BLACK;
        this.visible = true;
        this.thickness = 1;
        this.style = "solid";
    }
    
    @Override
    public String getId() {
        return id;
    }
    
    @Override
    public void draw() {
        if (!visible) return;
        
        System.out.println("📏 Drawing Line [" + id + "]: from=" + start + " to=" + end + 
                         ", color=" + color + ", thickness=" + thickness + "px, style=" + style);
    }
    
    @Override
    public void move(int deltaX, int deltaY) {
        start.move(deltaX, deltaY);
        end.move(deltaX, deltaY);
        System.out.println("📦 Moved line " + id + ": from=" + start + " to=" + end);
    }
    
    @Override
    public void scale(double factor) {
        // Scale around the midpoint
        Point mid = new Point((start.x + end.x) / 2, (start.y + end.y) / 2);
        start.x = mid.x + (int) ((start.x - mid.x) * factor);
        start.y = mid.y + (int) ((start.y - mid.y) * factor);
        end.x = mid.x + (int) ((end.x - mid.x) * factor);
        end.y = mid.y + (int) ((end.y - mid.y) * factor);
        System.out.println("🔍 Scaled line " + id + ": from=" + start + " to=" + end);
    }
    
    @Override
    public void rotate(double angle) {
        Point mid = new Point((start.x + end.x) / 2, (start.y + end.y) / 2);
        start.rotate(mid, angle);
        end.rotate(mid, angle);
        System.out.println("🔄 Rotated line " + id + " by " + angle + " degrees around midpoint");
    }
    
    @Override
    public Rectangle getBounds() {
        int minX = Math.min(start.x, end.x);
        int minY = Math.min(start.y, end.y);
        int maxX = Math.max(start.x, end.x);
        int maxY = Math.max(start.y, end.y);
        return new Rectangle(minX, minY, maxX - minX, maxY - minY);
    }
    
    @Override
    public DrawingComponent copy() {
        Line copy = new Line(start, end);
        copy.color = this.color;
        copy.thickness = this.thickness;
        copy.style = this.style;
        copy.visible = this.visible;
        return copy;
    }
    
    @Override
    public String getDescription() {
        return "Line from " + start + " to " + end;
    }
    
    @Override
    public void setColor(Color color) {
        this.color = color;
    }
    
    @Override
    public Color getColor() {
        return color;
    }
    
    @Override
    public void setVisible(boolean visible) {
        this.visible = visible;
        System.out.println((visible ? "👁️ Showed" : "🙈 Hidden") + " line " + id);
    }
    
    @Override
    public boolean isVisible() {
        return visible;
    }
    
    public void setThickness(int thickness) {
        this.thickness = thickness;
    }
    
    public void setStyle(String style) {
        this.style = style;
    }
}

class Text implements DrawingComponent {
    private String id;
    private Point position;
    private String content;
    private Color color;
    private boolean visible;
    private int fontSize;
    private String fontFamily;
    private String style;
    
    public Text(Point position, String content) {
        this.id = "Text-" + System.currentTimeMillis() + "-" + (int)(Math.random() * 1000);
        this.position = position.copy();
        this.content = content;
        this.color = Color.BLACK;
        this.visible = true;
        this.fontSize = 12;
        this.fontFamily = "Arial";
        this.style = "normal";
    }
    
    @Override
    public String getId() {
        return id;
    }
    
    @Override
    public void draw() {
        if (!visible) return;
        
        System.out.println("📝 Drawing Text [" + id + "]: \"" + content + "\" at " + position + 
                         ", font=" + fontFamily + ", size=" + fontSize + "px, color=" + color + 
                         ", style=" + style);
    }
    
    @Override
    public void move(int deltaX, int deltaY) {
        position.move(deltaX, deltaY);
        System.out.println("📦 Moved text " + id + " to position=" + position);
    }
    
    @Override
    public void scale(double factor) {
        fontSize = (int) (fontSize * factor);
        System.out.println("🔍 Scaled text " + id + ": new font size=" + fontSize + "px");
    }
    
    @Override
    public void rotate(double angle) {
        System.out.println("🔄 Rotating text \"" + content + "\" by " + angle + " degrees");
        // In a real implementation, this would set text rotation
    }
    
    @Override
    public Rectangle getBounds() {
        int width = content.length() * fontSize / 2; // Approximate width
        int height = fontSize;
        return new Rectangle(position.x, position.y - height, width, height);
    }
    
    @Override
    public DrawingComponent copy() {
        Text copy = new Text(position, content);
        copy.color = this.color;
        copy.fontSize = this.fontSize;
        copy.fontFamily = this.fontFamily;
        copy.style = this.style;
        copy.visible = this.visible;
        return copy;
    }
    
    @Override
    public String getDescription() {
        return "Text \"" + content + "\" at " + position;
    }
    
    @Override
    public void setColor(Color color) {
        this.color = color;
    }
    
    @Override
    public Color getColor() {
        return color;
    }
    
    @Override
    public void setVisible(boolean visible) {
        this.visible = visible;
        System.out.println((visible ? "👁️ Showed" : "🙈 Hidden") + " text " + id);
    }
    
    @Override
    public boolean isVisible() {
        return visible;
    }
    
    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }
    
    public void setFontFamily(String fontFamily) {
        this.fontFamily = fontFamily;
    }
    
    public void setStyle(String style) {
        this.style = style;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
}

// Composite implementation - Group
class Group implements DrawingComponent {
    private String id;
    private String name;
    private List<DrawingComponent> components;
    private Color backgroundColor;
    private boolean visible;
    private boolean locked;
    private Map<String, Object> properties;
    
    public Group(String name) {
        this.id = "Group-" + System.currentTimeMillis() + "-" + (int)(Math.random() * 1000);
        this.name = name;
        this.components = new ArrayList<>();
        this.backgroundColor = null;
        this.visible = true;
        this.locked = false;
        this.properties = new HashMap<>();
    }
    
    @Override
    public String getId() {
        return id;
    }
    
    public void add(DrawingComponent component) {
        components.add(component);
        System.out.println("➕ Added " + component.getDescription() + " to group " + name);
    }
    
    public void remove(DrawingComponent component) {
        if (components.remove(component)) {
            System.out.println("➖ Removed " + component.getDescription() + " from group " + name);
        }
    }
    
    public void clear() {
        components.clear();
        System.out.println("🧹 Cleared all components from group " + name);
    }
    
    @Override
    public void draw() {
        if (!visible) return;
        
        System.out.println("📁 Drawing Group [" + id + "]: " + name + " (" + components.size() + " components)");
        
        if (backgroundColor != null) {
            System.out.println("  🎨 Background color: " + backgroundColor);
        }
        
        if (locked) {
            System.out.println("  🔒 Group is locked");
        }
        
        for (DrawingComponent component : components) {
            component.draw();
        }
    }
    
    @Override
    public void move(int deltaX, int deltaY) {
        if (locked) {
            System.out.println("🔒 Cannot move locked group " + name);
            return;
        }
        
        System.out.println("📦 Moving group " + name + " by (" + deltaX + ", " + deltaY + ")");
        for (DrawingComponent component : components) {
            component.move(deltaX, deltaY);
        }
    }
    
    @Override
    public void scale(double factor) {
        if (locked) {
            System.out.println("🔒 Cannot scale locked group " + name);
            return;
        }
        
        System.out.println("🔍 Scaling group " + name + " by factor " + factor);
        for (DrawingComponent component : components) {
            component.scale(factor);
        }
    }
    
    @Override
    public void rotate(double angle) {
        if (locked) {
            System.out.println("🔒 Cannot rotate locked group " + name);
            return;
        }
        
        System.out.println("🔄 Rotating group " + name + " by " + angle + " degrees");
        for (DrawingComponent component : components) {
            component.rotate(angle);
        }
    }
    
    @Override
    public Rectangle getBounds() {
        if (components.isEmpty()) {
            return new Rectangle(0, 0, 0, 0);
        }
        
        Rectangle bounds = components.get(0).getBounds();
        int minX = bounds.x;
        int minY = bounds.y;
        int maxX = bounds.x + bounds.width;
        int maxY = bounds.y + bounds.height;
        
        for (int i = 1; i < components.size(); i++) {
            Rectangle componentBounds = components.get(i).getBounds();
            minX = Math.min(minX, componentBounds.x);
            minY = Math.min(minY, componentBounds.y);
            maxX = Math.max(maxX, componentBounds.x + componentBounds.width);
            maxY = Math.max(maxY, componentBounds.y + componentBounds.height);
        }
        
        return new Rectangle(minX, minY, maxX - minX, maxY - minY);
    }
    
    @Override
    public DrawingComponent copy() {
        Group copy = new Group(name + "_copy");
        copy.backgroundColor = this.backgroundColor;
        copy.locked = this.locked;
        copy.visible = this.visible;
        copy.properties.putAll(this.properties);
        
        for (DrawingComponent component : components) {
            copy.add(component.copy());
        }
        
        return copy;
    }
    
    @Override
    public String getDescription() {
        return "Group '" + name + "' with " + components.size() + " components";
    }
    
    @Override
    public void setColor(Color color) {
        System.out.println("🎨 Setting color " + color + " for all components in group " + name);
        for (DrawingComponent component : components) {
            component.setColor(color);
        }
    }
    
    @Override
    public Color getColor() {
        // Return the first component's color, or black if empty
        return components.isEmpty() ? Color.BLACK : components.get(0).getColor();
    }
    
    @Override
    public void setVisible(boolean visible) {
        this.visible = visible;
        System.out.println((visible ? "👁️ Showed" : "🙈 Hidden") + " group " + name);
        
        // Also apply to all children
        for (DrawingComponent component : components) {
            component.setVisible(visible);
        }
    }
    
    @Override
    public boolean isVisible() {
        return visible;
    }
    
    public void setBackgroundColor(Color color) {
        this.backgroundColor = color;
    }
    
    public void setLocked(boolean locked) {
        this.locked = locked;
        System.out.println((locked ? "🔒 Locked" : "🔓 Unlocked") + " group " + name);
    }
    
    public boolean isLocked() {
        return locked;
    }
    
    public List<DrawingComponent> getComponents() {
        return new ArrayList<>(components);
    }
    
    public int getComponentCount() {
        return components.size();
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public void setProperty(String key, Object value) {
        properties.put(key, value);
    }
    
    public Object getProperty(String key) {
        return properties.get(key);
    }
    
    public void alignHorizontally() {
        if (components.size() < 2 || locked) return;
        
        System.out.println("📐 Aligning " + components.size() + " components horizontally in group " + name);
        
        // Find the average Y position
        int totalY = 0;
        for (DrawingComponent component : components) {
            totalY += component.getBounds().y;
        }
        int averageY = totalY / components.size();
        
        // Move all components to the average Y position
        for (DrawingComponent component : components) {
            Rectangle bounds = component.getBounds();
            component.move(0, averageY - bounds.y);
        }
    }
    
    public void alignVertically() {
        if (components.size() < 2 || locked) return;
        
        System.out.println("📐 Aligning " + components.size() + " components vertically in group " + name);
        
        // Find the average X position
        int totalX = 0;
        for (DrawingComponent component : components) {
            totalX += component.getBounds().x;
        }
        int averageX = totalX / components.size();
        
        // Move all components to the average X position
        for (DrawingComponent component : components) {
            Rectangle bounds = component.getBounds();
            component.move(averageX - bounds.x, 0);
        }
    }
    
    public void distributeHorizontally() {
        if (components.size() < 3 || locked) return;
        
        System.out.println("📐 Distributing " + components.size() + " components horizontally in group " + name);
        
        // Sort components by X position
        List<DrawingComponent> sorted = new ArrayList<>(components);
        sorted.sort((a, b) -> Integer.compare(a.getBounds().x, b.getBounds().x));
        
        // Calculate spacing
        Rectangle firstBounds = sorted.get(0).getBounds();
        Rectangle lastBounds = sorted.get(sorted.size() - 1).getBounds();
        int totalWidth = (lastBounds.x + lastBounds.width) - firstBounds.x;
        int spacing = totalWidth / (sorted.size() - 1);
        
        // Distribute components
        for (int i = 1; i < sorted.size() - 1; i++) {
            DrawingComponent component = sorted.get(i);
            Rectangle bounds = component.getBounds();
            int targetX = firstBounds.x + i * spacing;
            component.move(targetX - bounds.x, 0);
        }
    }
}

// Graphics Editor Manager
class GraphicsEditor {
    private Group canvas;
    private List<Group> layers;
    private Group selectedComponents;
    private Map<String, DrawingComponent> componentIndex;
    private Stack<String> undoStack;
    private int zoomLevel;
    
    public GraphicsEditor() {
        this.canvas = new Group("Canvas");
        this.layers = new ArrayList<>();
        this.selectedComponents = new Group("Selection");
        this.componentIndex = new HashMap<>();
        this.undoStack = new Stack<>();
        this.zoomLevel = 100;
        
        // Create default layer
        Group defaultLayer = new Group("Layer 1");
        layers.add(defaultLayer);
        canvas.add(defaultLayer);
    }
    
    public void addComponent(DrawingComponent component) {
        if (!layers.isEmpty()) {
            layers.get(0).add(component); // Add to first layer
            componentIndex.put(component.getId(), component);
            System.out.println("✅ Added " + component.getDescription() + " to canvas");
        }
    }
    
    public void createLayer(String name) {
        Group layer = new Group(name);
        layers.add(layer);
        canvas.add(layer);
        System.out.println("📄 Created new layer: " + name);
    }
    
    public void selectComponent(String id) {
        DrawingComponent component = componentIndex.get(id);
        if (component != null) {
            selectedComponents.add(component);
            System.out.println("👆 Selected: " + component.getDescription());
        }
    }
    
    public void clearSelection() {
        selectedComponents.clear();
        System.out.println("🔄 Cleared selection");
    }
    
    public void groupSelected(String groupName) {
        if (selectedComponents.getComponentCount() > 1) {
            Group newGroup = new Group(groupName);
            
            for (DrawingComponent component : selectedComponents.getComponents()) {
                // Remove from current layer and add to new group
                removeFromLayers(component);
                newGroup.add(component);
            }
            
            // Add group to first layer
            if (!layers.isEmpty()) {
                layers.get(0).add(newGroup);
                componentIndex.put(newGroup.getId(), newGroup);
            }
            
            clearSelection();
            System.out.println("🗂️ Created group: " + groupName);
        }
    }
    
    private void removeFromLayers(DrawingComponent component) {
        for (Group layer : layers) {
            layer.remove(component);
        }
    }
    
    public void deleteSelected() {
        for (DrawingComponent component : selectedComponents.getComponents()) {
            removeFromLayers(component);
            componentIndex.remove(component.getId());
        }
        System.out.println("🗑️ Deleted " + selectedComponents.getComponentCount() + " selected components");
        clearSelection();
    }
    
    public void moveSelected(int deltaX, int deltaY) {
        if (selectedComponents.getComponentCount() > 0) {
            selectedComponents.move(deltaX, deltaY);
            System.out.println("📦 Moved " + selectedComponents.getComponentCount() + " selected components");
        }
    }
    
    public void scaleSelected(double factor) {
        if (selectedComponents.getComponentCount() > 0) {
            selectedComponents.scale(factor);
            System.out.println("🔍 Scaled " + selectedComponents.getComponentCount() + " selected components");
        }
    }
    
    public void rotateSelected(double angle) {
        if (selectedComponents.getComponentCount() > 0) {
            selectedComponents.rotate(angle);
            System.out.println("🔄 Rotated " + selectedComponents.getComponentCount() + " selected components");
        }
    }
    
    public void setSelectedColor(Color color) {
        if (selectedComponents.getComponentCount() > 0) {
            selectedComponents.setColor(color);
            System.out.println("🎨 Set color for " + selectedComponents.getComponentCount() + " selected components");
        }
    }
    
    public void renderCanvas() {
        System.out.println("=== 🎨 Canvas Render ===");
        System.out.println("🔍 Zoom: " + zoomLevel + "%");
        System.out.println("📄 Layers: " + layers.size());
        System.out.println("📊 Total components: " + componentIndex.size());
        canvas.draw();
        System.out.println();
    }
    
    public void showLayers() {
        System.out.println("=== 📄 Layer Management ===");
        for (int i = 0; i < layers.size(); i++) {
            Group layer = layers.get(i);
            System.out.println("Layer " + (i + 1) + ": " + layer.getName() + 
                             " (" + layer.getComponentCount() + " components)");
        }
        System.out.println();
    }
    
    public void exportToSVG(String filename) {
        System.out.println("💾 Exporting canvas to SVG: " + filename);
        System.out.println("📊 Exporting " + componentIndex.size() + " components across " + layers.size() + " layers");
        
        // In a real implementation, would generate SVG XML
        for (Group layer : layers) {
            System.out.println("  📄 Processing layer: " + layer.getName());
            for (DrawingComponent component : layer.getComponents()) {
                System.out.println("    📄 Exporting: " + component.getDescription());
            }
        }
        
        System.out.println("✅ Export completed: " + filename);
    }
    
    public void zoom(int percentage) {
        this.zoomLevel = Math.max(10, Math.min(500, percentage));
        System.out.println("🔍 Zoomed to " + zoomLevel + "%");
    }
    
    public void showStatistics() {
        System.out.println("=== 📊 Editor Statistics ===");
        System.out.println("📄 Total layers: " + layers.size());
        System.out.println("🎨 Total components: " + componentIndex.size());
        System.out.println("👆 Selected components: " + selectedComponents.getComponentCount());
        System.out.println("🔍 Zoom level: " + zoomLevel + "%");
        
        // Count component types
        Map<String, Integer> typeCounts = new HashMap<>();
        for (DrawingComponent component : componentIndex.values()) {
            String type = component.getClass().getSimpleName();
            typeCounts.put(type, typeCounts.getOrDefault(type, 0) + 1);
        }
        
        System.out.println("📈 Component breakdown:");
        for (Map.Entry<String, Integer> entry : typeCounts.entrySet()) {
            System.out.println("  " + entry.getKey() + ": " + entry.getValue());
        }
        System.out.println();
    }
}

// Demo class
public class GraphicsEditorExample {
    public static void main(String[] args) {
        System.out.println("=== 🎨 Composite Pattern: Graphics Editor Example ===\n");
        
        GraphicsEditor editor = new GraphicsEditor();
        
        // Create basic shapes
        Circle circle1 = new Circle(new Point(100, 100), 50);
        circle1.setColor(Color.RED);
        circle1.setStyle("filled");
        
        RectangleShape rect1 = new RectangleShape(new Point(200, 50), 100, 80);
        rect1.setColor(Color.BLUE);
        rect1.setFilled(true);
        
        Line line1 = new Line(new Point(50, 200), new Point(350, 200));
        line1.setColor(Color.GREEN);
        line1.setThickness(3);
        
        Text text1 = new Text(new Point(150, 250), "Hello Graphics!");
        text1.setColor(Color.MAGENTA);
        text1.setFontSize(18);
        text1.setFontFamily("Helvetica");
        
        // Add shapes to editor
        editor.addComponent(circle1);
        editor.addComponent(rect1);
        editor.addComponent(line1);
        editor.addComponent(text1);
        
        // Create additional shapes for grouping
        Circle circle2 = new Circle(new Point(400, 150), 30);
        circle2.setColor(Color.YELLOW);
        
        RectangleShape rect2 = new RectangleShape(new Point(450, 120), 60, 60);
        rect2.setColor(Color.CYAN);
        
        Text text2 = new Text(new Point(420, 220), "Group 1");
        text2.setColor(Color.BLACK);
        
        editor.addComponent(circle2);
        editor.addComponent(rect2);
        editor.addComponent(text2);
        
        // Create a second layer
        editor.createLayer("Layer 2");
        
        // Display initial state
        editor.renderCanvas();
        editor.showLayers();
        editor.showStatistics();
        
        // Demonstrate uniform operations
        System.out.println("=== 🔄 Uniform Operations Demo ===");
        
        // Selection operations
        System.out.println("\n--- 👆 Selection Operations ---");
        editor.selectComponent(circle2.getId());
        editor.selectComponent(rect2.getId());
        editor.selectComponent(text2.getId());
        
        // Group selected components
        System.out.println("\n--- 🗂️ Grouping Operations ---");
        editor.groupSelected("Logo Group");
        
        // Transform operations on group
        System.out.println("\n--- 🔄 Transform Operations ---");
        editor.moveSelected(50, -20);
        editor.scaleSelected(1.2);
        editor.rotateSelected(15);
        
        // Color operations
        System.out.println("\n--- 🎨 Styling Operations ---");
        editor.selectComponent(circle1.getId());
        editor.selectComponent(rect1.getId());
        editor.setSelectedColor(Color.RED);
        
        // Individual shape operations
        System.out.println("\n--- ⚙️ Individual Shape Operations ---");
        circle1.setStyle("dashed");
        rect1.setStrokeWidth(5);
        line1.setStyle("dotted");
        text1.setStyle("bold");
        
        // Alignment operations (create a manual group for demo)
        System.out.println("\n--- 📐 Alignment Operations ---");
        Group alignmentGroup = new Group("Alignment Demo");
        
        // Create shapes for alignment
        Circle c1 = new Circle(new Point(100, 300), 20);
        Circle c2 = new Circle(new Point(200, 350), 20);
        Circle c3 = new Circle(new Point(300, 320), 20);
        
        c1.setColor(Color.RED);
        c2.setColor(Color.GREEN);
        c3.setColor(Color.BLUE);
        
        alignmentGroup.add(c1);
        alignmentGroup.add(c2);
        alignmentGroup.add(c3);
        
        System.out.println("Before alignment:");
        alignmentGroup.draw();
        
        alignmentGroup.alignHorizontally();
        
        System.out.println("After horizontal alignment:");
        alignmentGroup.draw();
        
        // Distribution demo
        System.out.println("\n--- 📐 Distribution Operations ---");
        Group distributionGroup = new Group("Distribution Demo");
        
        RectangleShape r1 = new RectangleShape(new Point(50, 400), 40, 40);
        RectangleShape r2 = new RectangleShape(new Point(120, 400), 40, 40);
        RectangleShape r3 = new RectangleShape(new Point(250, 400), 40, 40);
        RectangleShape r4 = new RectangleShape(new Point(350, 400), 40, 40);
        
        distributionGroup.add(r1);
        distributionGroup.add(r2);
        distributionGroup.add(r3);
        distributionGroup.add(r4);
        
        System.out.println("Before distribution:");
        distributionGroup.draw();
        
        distributionGroup.distributeHorizontally();
        
        System.out.println("After horizontal distribution:");
        distributionGroup.draw();
        
        // Visibility operations
        System.out.println("\n--- 👁️ Visibility Operations ---");
        System.out.println("Hiding circle:");
        circle1.setVisible(false);
        
        System.out.println("Hiding entire group:");
        alignmentGroup.setVisible(false);
        
        // Locking operations
        System.out.println("\n--- 🔒 Locking Operations ---");
        alignmentGroup.setLocked(true);
        alignmentGroup.move(10, 10); // Should be prevented
        
        // Copy operations
        System.out.println("\n--- 📋 Copy Operations ---");
        DrawingComponent circleCopy = circle1.copy();
        DrawingComponent groupCopy = alignmentGroup.copy();
        
        System.out.println("Copied components:");
        circleCopy.draw();
        groupCopy.draw();
        
        // Editor operations
        System.out.println("\n--- 🔍 Editor Operations ---");
        editor.zoom(150);
        editor.zoom(75);
        
        // Export operations
        System.out.println("\n--- 💾 Export Operations ---");
        editor.exportToSVG("my_drawing.svg");
        
        // Final render
        System.out.println("\n=== 🎨 Final Canvas State ===");
        editor.renderCanvas();
        editor.showStatistics();
        
        System.out.println("=== ✅ Demo Complete ===");
        
        // Key benefits demonstrated
        System.out.println("\n=== 🎯 Key Benefits Demonstrated ===");
        System.out.println("✅ Uniform interface for shapes and groups");
        System.out.println("✅ Hierarchical composition with nested groups");
        System.out.println("✅ Recursive operations (transform, style, visibility)");
        System.out.println("✅ Easy manipulation of complex structures");
        System.out.println("✅ Layer management and organization");
        System.out.println("✅ Selection and bulk operations");
        System.out.println("✅ Complex graphics editor functionality");
    }
}