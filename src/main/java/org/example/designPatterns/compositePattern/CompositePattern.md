# Composite Design Pattern - Complete Guide

## Table of Contents
1. [Introduction](#introduction)
2. [Core Concepts](#core-concepts)
3. [When to Use Composite Pattern](#when-to-use-composite-pattern)
4. [Structure and Components](#structure-and-components)
5. [Implementation Steps](#implementation-steps)
6. [Detailed Examples](#detailed-examples)
7. [Real-World Applications](#real-world-applications)
8. [Advantages and Disadvantages](#advantages-and-disadvantages)
9. [Composite vs Other Patterns](#composite-vs-other-patterns)
10. [Best Practices](#best-practices)

## Introduction

The **Composite Pattern** is a structural design pattern that allows you to compose objects into tree structures to represent part-whole hierarchies. It lets clients treat individual objects and compositions of objects uniformly.

### Definition
> "Compose objects into tree structures to represent part-whole hierarchies. Composite lets clients treat individual objects and compositions of objects uniformly." - Gang of Four

### Problem It Solves
When you need to work with tree structures where individual elements and groups of elements should be treated the same way, you might face:
- Complex client code that needs to distinguish between leaves and composites
- Difficulty in adding new types of components
- Code duplication for handling individual vs. group operations
- Fragile code that breaks when the tree structure changes

## Core Concepts

### Key Principles
1. **Uniform Interface**: Same interface for both leaf and composite objects
2. **Tree Structure**: Organizes objects in hierarchical tree structures
3. **Recursive Composition**: Composites can contain other composites
4. **Transparency**: Clients treat all objects uniformly

### Real-World Analogy
Think of a file system:
- **Files** are leaf objects (they don't contain other objects)
- **Folders** are composite objects (they can contain files and other folders)
- Both files and folders can be:
  - Named
  - Moved
  - Copied
  - Deleted
  - Have permissions set
- You can perform operations on a single file or an entire folder hierarchy

## When to Use Composite Pattern

Use the Composite Pattern when:
1. **You want to represent part-whole hierarchies** of objects
2. **You want clients to ignore the difference** between compositions and individual objects
3. **You have a tree structure** where leaves and branches should be treated uniformly
4. **You need to perform operations** on both individual objects and groups
5. **The structure is recursive** in nature

### Red Flags Indicating Need for Composite Pattern
- Code that checks object types before performing operations
- Different handling for individual items vs. collections
- Tree-like data structures with uniform operations
- Recursive algorithms that work on hierarchical data
- Need to treat single objects and groups of objects the same way

## Structure and Components

### UML Diagram Structure
```
┌─────────────────────┐
│     Component       │
│   <<interface>>     │
├─────────────────────┤
│ + operation()       │
│ + add(Component)    │
│ + remove(Component) │
│ + getChild(int)     │
└─────────────────────┘
           △
           │
    ┌──────┴──────┐
    │             │
┌───────────┐ ┌─────────────────────┐
│   Leaf    │ │     Composite       │
├───────────┤ ├─────────────────────┤
│+ operation│ │ - children: List    │
└───────────┘ ├─────────────────────┤
              │ + operation()       │
              │ + add(Component)    │
              │ + remove(Component) │
              │ + getChild(int)     │
              └─────────────────────┘
                        │
                        │ contains
                        ▼
                ┌─────────────────┐
                │   Component     │
                │    objects      │
                └─────────────────┘
```

### Components

1. **Component**: Declares interface for objects in composition; implements default behavior common to all classes
2. **Leaf**: Represents leaf objects with no children; defines behavior for primitive objects
3. **Composite**: Stores child components; implements child-related operations; delegates work to children
4. **Client**: Manipulates objects through the Component interface

## Implementation Steps

1. **Define the Component interface** with common operations
2. **Create Leaf classes** that implement the Component interface
3. **Create Composite class** that can contain other Components
4. **Implement operations** in Composite to delegate to children
5. **Client code** works with Component interface uniformly

## Detailed Examples

### Example 1: File System Hierarchy

```java
// Component interface
public interface FileSystemComponent {
    String getName();
    long getSize();
    void display(String indent);
    FileSystemComponent copy();
    void move(String newPath);
    void delete();
    String getPath();
    Date getLastModified();
    String getPermissions();
    void setPermissions(String permissions);
}

// Leaf implementation - File
public class File implements FileSystemComponent {
    private String name;
    private long size;
    private String path;
    private Date lastModified;
    private String permissions;
    private String content;
    
    public File(String name, long size, String path) {
        this.name = name;
        this.size = size;
        this.path = path;
        this.lastModified = new Date();
        this.permissions = "rw-r--r--";
        this.content = "File content of " + name;
    }
    
    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public long getSize() {
        return size;
    }
    
    @Override
    public void display(String indent) {
        System.out.println(indent + "📄 " + name + " (" + formatSize(size) + ")");
        System.out.println(indent + "   Path: " + path);
        System.out.println(indent + "   Modified: " + lastModified);
        System.out.println(indent + "   Permissions: " + permissions);
    }
    
    @Override
    public FileSystemComponent copy() {
        File copy = new File(name + "_copy", size, path + "_copy");
        copy.content = this.content;
        copy.permissions = this.permissions;
        System.out.println("Copied file: " + name + " to " + copy.getName());
        return copy;
    }
    
    @Override
    public void move(String newPath) {
        String oldPath = this.path;
        this.path = newPath;
        this.lastModified = new Date();
        System.out.println("Moved file: " + name + " from " + oldPath + " to " + newPath);
    }
    
    @Override
    public void delete() {
        System.out.println("Deleted file: " + name + " (" + formatSize(size) + ")");
    }
    
    @Override
    public String getPath() {
        return path;
    }
    
    @Override
    public Date getLastModified() {
        return lastModified;
    }
    
    @Override
    public String getPermissions() {
        return permissions;
    }
    
    @Override
    public void setPermissions(String permissions) {
        this.permissions = permissions;
        this.lastModified = new Date();
        System.out.println("Changed permissions for " + name + " to " + permissions);
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
        this.size = content.getBytes().length;
        this.lastModified = new Date();
    }
    
    private String formatSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return (bytes / 1024) + " KB";
        return (bytes / (1024 * 1024)) + " MB";
    }
}

// Composite implementation - Directory
public class Directory implements FileSystemComponent {
    private String name;
    private String path;
    private Date lastModified;
    private String permissions;
    private List<FileSystemComponent> children;
    
    public Directory(String name, String path) {
        this.name = name;
        this.path = path;
        this.lastModified = new Date();
        this.permissions = "rwxr-xr-x";
        this.children = new ArrayList<>();
    }
    
    public void add(FileSystemComponent component) {
        children.add(component);
        this.lastModified = new Date();
        System.out.println("Added " + component.getName() + " to directory " + this.name);
    }
    
    public void remove(FileSystemComponent component) {
        if (children.remove(component)) {
            this.lastModified = new Date();
            System.out.println("Removed " + component.getName() + " from directory " + this.name);
        }
    }
    
    public void remove(String name) {
        children.removeIf(child -> child.getName().equals(name));
        this.lastModified = new Date();
        System.out.println("Removed " + name + " from directory " + this.name);
    }
    
    public FileSystemComponent getChild(String name) {
        return children.stream()
                .filter(child -> child.getName().equals(name))
                .findFirst()
                .orElse(null);
    }
    
    public List<FileSystemComponent> getChildren() {
        return new ArrayList<>(children);
    }
    
    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public long getSize() {
        return children.stream()
                .mapToLong(FileSystemComponent::getSize)
                .sum();
    }
    
    @Override
    public void display(String indent) {
        System.out.println(indent + "📁 " + name + "/" + " (" + formatSize(getSize()) + ")");
        System.out.println(indent + "   Path: " + path);
        System.out.println(indent + "   Modified: " + lastModified);
        System.out.println(indent + "   Permissions: " + permissions);
        System.out.println(indent + "   Children: " + children.size());
        
        // Display children with increased indentation
        for (FileSystemComponent child : children) {
            child.display(indent + "  ");
        }
    }
    
    @Override
    public FileSystemComponent copy() {
        Directory copy = new Directory(name + "_copy", path + "_copy");
        copy.permissions = this.permissions;
        
        // Recursively copy all children
        for (FileSystemComponent child : children) {
            copy.add(child.copy());
        }
        
        System.out.println("Copied directory: " + name + " with " + children.size() + " items");
        return copy;
    }
    
    @Override
    public void move(String newPath) {
        String oldPath = this.path;
        this.path = newPath;
        this.lastModified = new Date();
        
        // Update path for all children
        for (FileSystemComponent child : children) {
            child.move(newPath + "/" + child.getName());
        }
        
        System.out.println("Moved directory: " + name + " from " + oldPath + " to " + newPath);
    }
    
    @Override
    public void delete() {
        System.out.println("Deleting directory: " + name + " with " + children.size() + " items");
        
        // Recursively delete all children
        for (FileSystemComponent child : children) {
            child.delete();
        }
        
        children.clear();
        System.out.println("Directory " + name + " deleted");
    }
    
    @Override
    public String getPath() {
        return path;
    }
    
    @Override
    public Date getLastModified() {
        return lastModified;
    }
    
    @Override
    public String getPermissions() {
        return permissions;
    }
    
    @Override
    public void setPermissions(String permissions) {
        this.permissions = permissions;
        this.lastModified = new Date();
        System.out.println("Changed permissions for directory " + name + " to " + permissions);
    }
    
    public void setPermissionsRecursive(String permissions) {
        setPermissions(permissions);
        for (FileSystemComponent child : children) {
            child.setPermissions(permissions);
        }
        System.out.println("Applied permissions " + permissions + " recursively to " + name);
    }
    
    public List<FileSystemComponent> search(String pattern) {
        List<FileSystemComponent> results = new ArrayList<>();
        
        if (name.contains(pattern)) {
            results.add(this);
        }
        
        for (FileSystemComponent child : children) {
            if (child.getName().contains(pattern)) {
                results.add(child);
            }
            
            if (child instanceof Directory) {
                results.addAll(((Directory) child).search(pattern));
            }
        }
        
        return results;
    }
    
    public void compress(String archiveName) {
        System.out.println("Creating archive: " + archiveName);
        System.out.println("Compressing " + children.size() + " items from directory: " + name);
        
        long totalSize = getSize();
        long compressedSize = totalSize / 3; // Simulate compression
        
        for (FileSystemComponent child : children) {
            System.out.println("  Adding to archive: " + child.getName());
        }
        
        System.out.println("Archive created: " + archiveName);
        System.out.println("Original size: " + formatSize(totalSize));
        System.out.println("Compressed size: " + formatSize(compressedSize));
        System.out.println("Compression ratio: " + String.format("%.1f%%", 
            ((double)(totalSize - compressedSize) / totalSize) * 100));
    }
    
    private String formatSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return (bytes / 1024) + " KB";
        return (bytes / (1024 * 1024)) + " MB";
    }
}

// File System Manager
public class FileSystemManager {
    private Directory root;
    
    public FileSystemManager(String rootName) {
        this.root = new Directory(rootName, "/" + rootName);
    }
    
    public Directory getRoot() {
        return root;
    }
    
    public void displayFileSystem() {
        System.out.println("=== File System Structure ===");
        root.display("");
    }
    
    public void showStatistics() {
        System.out.println("\n=== File System Statistics ===");
        FileSystemStats stats = calculateStats(root);
        System.out.println("Total files: " + stats.fileCount);
        System.out.println("Total directories: " + stats.directoryCount);
        System.out.println("Total size: " + formatSize(stats.totalSize));
        System.out.println("Largest file: " + stats.largestFileName + " (" + formatSize(stats.largestFileSize) + ")");
        System.out.println("Average file size: " + formatSize(stats.totalSize / Math.max(stats.fileCount, 1)));
    }
    
    private FileSystemStats calculateStats(FileSystemComponent component) {
        FileSystemStats stats = new FileSystemStats();
        
        if (component instanceof File) {
            stats.fileCount = 1;
            stats.totalSize = component.getSize();
            if (component.getSize() > stats.largestFileSize) {
                stats.largestFileSize = component.getSize();
                stats.largestFileName = component.getName();
            }
        } else if (component instanceof Directory) {
            stats.directoryCount = 1;
            Directory dir = (Directory) component;
            
            for (FileSystemComponent child : dir.getChildren()) {
                FileSystemStats childStats = calculateStats(child);
                stats.fileCount += childStats.fileCount;
                stats.directoryCount += childStats.directoryCount;
                stats.totalSize += childStats.totalSize;
                
                if (childStats.largestFileSize > stats.largestFileSize) {
                    stats.largestFileSize = childStats.largestFileSize;
                    stats.largestFileName = childStats.largestFileName;
                }
            }
        }
        
        return stats;
    }
    
    private String formatSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return (bytes / 1024) + " KB";
        return (bytes / (1024 * 1024)) + " MB";
    }
    
    private static class FileSystemStats {
        int fileCount = 0;
        int directoryCount = 0;
        long totalSize = 0;
        long largestFileSize = 0;
        String largestFileName = "";
    }
}
```

### Example 2: Organization Structure

```java
// Component interface
public interface OrganizationComponent {
    String getName();
    String getTitle();
    double getSalary();
    int getEmployeeCount();
    void displayHierarchy(String indent);
    void giveRaise(double percentage);
    List<OrganizationComponent> findByTitle(String title);
    double getTotalSalaryCost();
    void displayTeamSummary();
}

// Leaf implementation - Employee
public class Employee implements OrganizationComponent {
    private String name;
    private String title;
    private double salary;
    private String department;
    private Date hireDate;
    private List<String> skills;
    
    public Employee(String name, String title, double salary, String department) {
        this.name = name;
        this.title = title;
        this.salary = salary;
        this.department = department;
        this.hireDate = new Date();
        this.skills = new ArrayList<>();
    }
    
    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public String getTitle() {
        return title;
    }
    
    @Override
    public double getSalary() {
        return salary;
    }
    
    @Override
    public int getEmployeeCount() {
        return 1;
    }
    
    @Override
    public void displayHierarchy(String indent) {
        System.out.println(indent + "👤 " + name + " - " + title);
        System.out.println(indent + "   Department: " + department);
        System.out.println(indent + "   Salary: $" + String.format("%,.2f", salary));
        System.out.println(indent + "   Hire Date: " + hireDate);
        if (!skills.isEmpty()) {
            System.out.println(indent + "   Skills: " + String.join(", ", skills));
        }
    }
    
    @Override
    public void giveRaise(double percentage) {
        double oldSalary = salary;
        salary *= (1 + percentage / 100);
        System.out.println(name + " received a " + percentage + "% raise: $" + 
                         String.format("%,.2f", oldSalary) + " → $" + String.format("%,.2f", salary));
    }
    
    @Override
    public List<OrganizationComponent> findByTitle(String title) {
        List<OrganizationComponent> results = new ArrayList<>();
        if (this.title.toLowerCase().contains(title.toLowerCase())) {
            results.add(this);
        }
        return results;
    }
    
    @Override
    public double getTotalSalaryCost() {
        return salary;
    }
    
    @Override
    public void displayTeamSummary() {
        System.out.println("Individual Employee: " + name + " (" + title + ")");
        System.out.println("  Salary: $" + String.format("%,.2f", salary));
    }
    
    public String getDepartment() {
        return department;
    }
    
    public Date getHireDate() {
        return hireDate;
    }
    
    public void addSkill(String skill) {
        skills.add(skill);
    }
    
    public List<String> getSkills() {
        return new ArrayList<>(skills);
    }
    
    public void promote(String newTitle, double newSalary) {
        String oldTitle = this.title;
        double oldSalary = this.salary;
        
        this.title = newTitle;
        this.salary = newSalary;
        
        System.out.println("🎉 " + name + " promoted from " + oldTitle + " to " + newTitle);
        System.out.println("   Salary increased from $" + String.format("%,.2f", oldSalary) + 
                         " to $" + String.format("%,.2f", newSalary));
    }
}

// Composite implementation - Team
public class Team implements OrganizationComponent {
    private String name;
    private String title;
    private Employee manager;
    private List<OrganizationComponent> members;
    private String department;
    private double budget;
    
    public Team(String name, String title, Employee manager, String department) {
        this.name = name;
        this.title = title;
        this.manager = manager;
        this.department = department;
        this.members = new ArrayList<>();
        this.budget = 0.0;
        
        // Manager is part of the team
        if (manager != null) {
            this.members.add(manager);
        }
    }
    
    public void addMember(OrganizationComponent member) {
        members.add(member);
        System.out.println("Added " + member.getName() + " to team " + this.name);
    }
    
    public void removeMember(OrganizationComponent member) {
        if (members.remove(member)) {
            System.out.println("Removed " + member.getName() + " from team " + this.name);
        }
    }
    
    public void removeMember(String name) {
        members.removeIf(member -> member.getName().equals(name));
        System.out.println("Removed " + name + " from team " + this.name);
    }
    
    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public String getTitle() {
        return title;
    }
    
    @Override
    public double getSalary() {
        return manager != null ? manager.getSalary() : 0.0;
    }
    
    @Override
    public int getEmployeeCount() {
        return members.stream()
                .mapToInt(OrganizationComponent::getEmployeeCount)
                .sum();
    }
    
    @Override
    public void displayHierarchy(String indent) {
        System.out.println(indent + "🏢 " + name + " (" + title + ")");
        System.out.println(indent + "   Department: " + department);
        System.out.println(indent + "   Manager: " + (manager != null ? manager.getName() : "None"));
        System.out.println(indent + "   Total Members: " + getEmployeeCount());
        System.out.println(indent + "   Total Salary Cost: $" + String.format("%,.2f", getTotalSalaryCost()));
        
        if (budget > 0) {
            System.out.println(indent + "   Budget: $" + String.format("%,.2f", budget));
        }
        
        // Display team members with increased indentation
        for (OrganizationComponent member : members) {
            member.displayHierarchy(indent + "  ");
        }
    }
    
    @Override
    public void giveRaise(double percentage) {
        System.out.println("Giving " + percentage + "% raise to all members of team: " + name);
        for (OrganizationComponent member : members) {
            member.giveRaise(percentage);
        }
    }
    
    @Override
    public List<OrganizationComponent> findByTitle(String title) {
        List<OrganizationComponent> results = new ArrayList<>();
        
        if (this.title.toLowerCase().contains(title.toLowerCase())) {
            results.add(this);
        }
        
        for (OrganizationComponent member : members) {
            results.addAll(member.findByTitle(title));
        }
        
        return results;
    }
    
    @Override
    public double getTotalSalaryCost() {
        return members.stream()
                .mapToDouble(OrganizationComponent::getTotalSalaryCost)
                .sum();
    }
    
    @Override
    public void displayTeamSummary() {
        System.out.println("Team: " + name + " (" + title + ")");
        System.out.println("  Manager: " + (manager != null ? manager.getName() : "None"));
        System.out.println("  Members: " + getEmployeeCount());
        System.out.println("  Total Salary Cost: $" + String.format("%,.2f", getTotalSalaryCost()));
        System.out.println("  Average Salary: $" + String.format("%,.2f", 
            getTotalSalaryCost() / Math.max(getEmployeeCount(), 1)));
    }
    
    public Employee getManager() {
        return manager;
    }
    
    public void setManager(Employee manager) {
        this.manager = manager;
        System.out.println(manager.getName() + " is now the manager of team " + name);
    }
    
    public String getDepartment() {
        return department;
    }
    
    public double getBudget() {
        return budget;
    }
    
    public void setBudget(double budget) {
        this.budget = budget;
        System.out.println("Budget set for team " + name + ": $" + String.format("%,.2f", budget));
    }
    
    public List<OrganizationComponent> getMembers() {
        return new ArrayList<>(members);
    }
    
    public void conductTeamMeeting(String topic) {
        System.out.println("\n📅 Team Meeting: " + name);
        System.out.println("Topic: " + topic);
        System.out.println("Attendees: " + getEmployeeCount() + " team members");
        System.out.println("Led by: " + (manager != null ? manager.getName() : "Team"));
    }
    
    public void evaluatePerformance() {
        System.out.println("\n📊 Performance Evaluation for team: " + name);
        System.out.println("Team Size: " + getEmployeeCount() + " members");
        System.out.println("Total Salary Cost: $" + String.format("%,.2f", getTotalSalaryCost()));
        
        if (budget > 0) {
            double salaryBudgetRatio = (getTotalSalaryCost() / budget) * 100;
            System.out.println("Salary/Budget Ratio: " + String.format("%.1f%%", salaryBudgetRatio));
        }
        
        // Performance metrics would be calculated here
        System.out.println("Team Productivity: High");
        System.out.println("Collaboration Score: 8.5/10");
    }
}

// Organization Manager
public class OrganizationManager {
    private Team rootTeam;
    
    public OrganizationManager(String organizationName, Employee ceo) {
        this.rootTeam = new Team(organizationName, "Organization", ceo, "Executive");
    }
    
    public Team getRootTeam() {
        return rootTeam;
    }
    
    public void displayOrganizationChart() {
        System.out.println("=== Organization Chart ===");
        rootTeam.displayHierarchy("");
    }
    
    public void showOrganizationSummary() {
        System.out.println("\n=== Organization Summary ===");
        System.out.println("Total Employees: " + rootTeam.getEmployeeCount());
        System.out.println("Total Salary Cost: $" + String.format("%,.2f", rootTeam.getTotalSalaryCost()));
        System.out.println("Average Salary: $" + String.format("%,.2f", 
            rootTeam.getTotalSalaryCost() / Math.max(rootTeam.getEmployeeCount(), 1)));
    }
    
    public void findEmployeesByTitle(String title) {
        List<OrganizationComponent> results = rootTeam.findByTitle(title);
        System.out.println("\n=== Search Results for '" + title + "' ===");
        
        if (results.isEmpty()) {
            System.out.println("No employees found with title containing: " + title);
        } else {
            System.out.println("Found " + results.size() + " result(s):");
            for (OrganizationComponent result : results) {
                System.out.println("- " + result.getName() + " (" + result.getTitle() + ")");
            }
        }
    }
    
    public void giveCompanyWideRaise(double percentage) {
        System.out.println("\n🎉 Company-wide " + percentage + "% salary increase!");
        rootTeam.giveRaise(percentage);
        System.out.println("Raise applied to all " + rootTeam.getEmployeeCount() + " employees");
    }
}
```

### Example 3: Drawing Application

```java
// Component interface
public interface DrawingComponent {
    void draw();
    void move(int deltaX, int deltaY);
    void scale(double factor);
    void rotate(double angle);
    Rectangle getBounds();
    DrawingComponent copy();
    String getDescription();
    void setColor(Color color);
    Color getColor();
}

// Basic geometric shape classes
public class Point {
    public int x, y;
    
    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    public Point copy() {
        return new Point(x, y);
    }
    
    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}

public class Rectangle {
    public int x, y, width, height;
    
    public Rectangle(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
    
    @Override
    public String toString() {
        return "Rectangle[x=" + x + ", y=" + y + ", w=" + width + ", h=" + height + "]";
    }
}

public class Color {
    public int r, g, b;
    
    public Color(int r, int g, int b) {
        this.r = r;
        this.g = g;
        this.b = b;
    }
    
    public static final Color BLACK = new Color(0, 0, 0);
    public static final Color RED = new Color(255, 0, 0);
    public static final Color GREEN = new Color(0, 255, 0);
    public static final Color BLUE = new Color(0, 0, 255);
    
    @Override
    public String toString() {
        return "Color(r=" + r + ", g=" + g + ", b=" + b + ")";
    }
}

// Leaf implementations - Basic Shapes
public class Circle implements DrawingComponent {
    private Point center;
    private int radius;
    private Color color;
    
    public Circle(Point center, int radius) {
        this.center = center.copy();
        this.radius = radius;
        this.color = Color.BLACK;
    }
    
    @Override
    public void draw() {
        System.out.println("Drawing Circle: center=" + center + ", radius=" + radius + ", color=" + color);
    }
    
    @Override
    public void move(int deltaX, int deltaY) {
        center.x += deltaX;
        center.y += deltaY;
        System.out.println("Moved circle to center=" + center);
    }
    
    @Override
    public void scale(double factor) {
        radius = (int) (radius * factor);
        System.out.println("Scaled circle: new radius=" + radius);
    }
    
    @Override
    public void rotate(double angle) {
        System.out.println("Circle rotation has no visual effect (angle=" + angle + " degrees)");
    }
    
    @Override
    public Rectangle getBounds() {
        return new Rectangle(center.x - radius, center.y - radius, 2 * radius, 2 * radius);
    }
    
    @Override
    public DrawingComponent copy() {
        Circle copy = new Circle(center, radius);
        copy.color = this.color;
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
}

public class Line implements DrawingComponent {
    private Point start;
    private Point end;
    private Color color;
    private int thickness;
    
    public Line(Point start, Point end) {
        this.start = start.copy();
        this.end = end.copy();
        this.color = Color.BLACK;
        this.thickness = 1;
    }
    
    @Override
    public void draw() {
        System.out.println("Drawing Line: from=" + start + " to=" + end + 
                         ", thickness=" + thickness + ", color=" + color);
    }
    
    @Override
    public void move(int deltaX, int deltaY) {
        start.x += deltaX;
        start.y += deltaY;
        end.x += deltaX;
        end.y += deltaY;
        System.out.println("Moved line: from=" + start + " to=" + end);
    }
    
    @Override
    public void scale(double factor) {
        // Scale around the midpoint
        Point mid = new Point((start.x + end.x) / 2, (start.y + end.y) / 2);
        start.x = mid.x + (int) ((start.x - mid.x) * factor);
        start.y = mid.y + (int) ((start.y - mid.y) * factor);
        end.x = mid.x + (int) ((end.x - mid.x) * factor);
        end.y = mid.y + (int) ((end.y - mid.y) * factor);
        System.out.println("Scaled line: from=" + start + " to=" + end);
    }
    
    @Override
    public void rotate(double angle) {
        System.out.println("Rotating line by " + angle + " degrees around midpoint");
        // Rotation math would be implemented here
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
    
    public void setThickness(int thickness) {
        this.thickness = thickness;
    }
}

public class Text implements DrawingComponent {
    private Point position;
    private String content;
    private Color color;
    private int fontSize;
    private String fontFamily;
    
    public Text(Point position, String content) {
        this.position = position.copy();
        this.content = content;
        this.color = Color.BLACK;
        this.fontSize = 12;
        this.fontFamily = "Arial";
    }
    
    @Override
    public void draw() {
        System.out.println("Drawing Text: \"" + content + "\" at " + position + 
                         ", font=" + fontFamily + ", size=" + fontSize + ", color=" + color);
    }
    
    @Override
    public void move(int deltaX, int deltaY) {
        position.x += deltaX;
        position.y += deltaY;
        System.out.println("Moved text to position=" + position);
    }
    
    @Override
    public void scale(double factor) {
        fontSize = (int) (fontSize * factor);
        System.out.println("Scaled text: new font size=" + fontSize);
    }
    
    @Override
    public void rotate(double angle) {
        System.out.println("Rotating text \"" + content + "\" by " + angle + " degrees");
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
    
    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }
    
    public void setFontFamily(String fontFamily) {
        this.fontFamily = fontFamily;
    }
}

// Composite implementation - Drawing Group
public class DrawingGroup implements DrawingComponent {
    private List<DrawingComponent> components;
    private String name;
    private Color backgroundColor;
    
    public DrawingGroup(String name) {
        this.name = name;
        this.components = new ArrayList<>();
        this.backgroundColor = null;
    }
    
    public void add(DrawingComponent component) {
        components.add(component);
        System.out.println("Added " + component.getDescription() + " to group " + name);
    }
    
    public void remove(DrawingComponent component) {
        if (components.remove(component)) {
            System.out.println("Removed " + component.getDescription() + " from group " + name);
        }
    }
    
    public void clear() {
        components.clear();
        System.out.println("Cleared all components from group " + name);
    }
    
    @Override
    public void draw() {
        System.out.println("Drawing Group: " + name + " (" + components.size() + " components)");
        
        if (backgroundColor != null) {
            System.out.println("  Background color: " + backgroundColor);
        }
        
        for (DrawingComponent component : components) {
            component.draw();
        }
    }
    
    @Override
    public void move(int deltaX, int deltaY) {
        System.out.println("Moving group " + name + " by (" + deltaX + ", " + deltaY + ")");
        for (DrawingComponent component : components) {
            component.move(deltaX, deltaY);
        }
    }
    
    @Override
    public void scale(double factor) {
        System.out.println("Scaling group " + name + " by factor " + factor);
        for (DrawingComponent component : components) {
            component.scale(factor);
        }
    }
    
    @Override
    public void rotate(double angle) {
        System.out.println("Rotating group " + name + " by " + angle + " degrees");
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
        DrawingGroup copy = new DrawingGroup(name + "_copy");
        copy.backgroundColor = this.backgroundColor;
        
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
        System.out.println("Setting color " + color + " for all components in group " + name);
        for (DrawingComponent component : components) {
            component.setColor(color);
        }
    }
    
    @Override
    public Color getColor() {
        // Return the first component's color, or black if empty
        return components.isEmpty() ? Color.BLACK : components.get(0).getColor();
    }
    
    public void setBackgroundColor(Color color) {
        this.backgroundColor = color;
    }
    
    public List<DrawingComponent> getComponents() {
        return new ArrayList<>(components);
    }
    
    public int getComponentCount() {
        return components.size();
    }
    
    public void alignHorizontally() {
        if (components.size() < 2) return;
        
        System.out.println("Aligning " + components.size() + " components horizontally in group " + name);
        
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
        if (components.size() < 2) return;
        
        System.out.println("Aligning " + components.size() + " components vertically in group " + name);
        
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
}
```

## Real-World Applications

### 1. **User Interface Systems**
- Widget hierarchies (panels containing buttons, text fields, etc.)
- Menu systems (menus containing submenus and menu items)
- Scene graphs in 3D graphics

### 2. **Document Processing**
- Document structures (documents containing pages, paragraphs, words)
- Spreadsheet applications (workbooks, worksheets, cells)
- Drawing applications (groups of shapes and text)

### 3. **File Systems**
- Directory structures (folders containing files and subfolders)
- Archive files (ZIP files containing nested structures)
- Version control systems (repositories, branches, commits)

### 4. **Organization Structures**
- Company hierarchies (departments, teams, employees)
- Military chains of command
- Academic institutions (universities, colleges, departments)

### 5. **Game Development**
- Scene graphs (game objects containing child objects)
- Inventory systems (containers holding items and sub-containers)
- Skill trees (abilities containing sub-abilities)

## Advantages and Disadvantages

### Advantages
1. **Uniform Interface**: Clients can treat individual and composite objects uniformly
2. **Simplified Client Code**: No need to distinguish between leaf and composite objects
3. **Easy to Add New Components**: Both new leaf and composite types can be added easily
4. **Recursive Operations**: Operations can be performed on entire tree structures
5. **Flexible Structure**: Tree structures can be easily modified at runtime

### Disadvantages
1. **Overly General Design**: Interface may include operations that don't make sense for all components
2. **Type Safety Issues**: Hard to restrict components to specific types
3. **Complex Implementation**: Composite classes can become complex with many responsibilities
4. **Performance Overhead**: Recursive operations can be expensive for large trees

## Composite vs Other Patterns

### Composite vs Decorator Pattern
- **Composite**: Structural composition for part-whole hierarchies
- **Decorator**: Behavioral composition for adding responsibilities
- Composite manages children; Decorator enhances behavior

### Composite vs Strategy Pattern
- **Composite**: Structural pattern for tree structures
- **Strategy**: Behavioral pattern for algorithm selection
- Different purposes entirely

### Composite vs Chain of Responsibility
- **Composite**: All children are processed
- **Chain of Responsibility**: Processing stops when handled
- Composite is tree-based; Chain is linear

## Best Practices

1. **Define Clear Component Interface**: Include only operations that make sense for all components
2. **Handle Null Cases**: Check for empty composites and null children
3. **Implement Deep vs Shallow Copy**: Decide whether copy operations should be recursive
4. **Consider Performance**: Optimize recursive operations for large trees
5. **Error Handling**: Handle cases where operations fail on some children
6. **Memory Management**: Be careful with circular references
7. **Type Safety**: Consider using generics to improve type safety
8. **Documentation**: Clearly document which operations apply to which component types
9. **Testing**: Test with various tree structures and edge cases
10. **Lazy Loading**: Consider lazy loading for large composite structures

## Common Pitfalls to Avoid

1. **Inappropriate Operations**: Don't force operations that don't make sense for all components
2. **Memory Leaks**: Be careful with parent-child references
3. **Infinite Recursion**: Check for circular references in tree structures
4. **Type Checking**: Avoid excessive type checking in client code
5. **Poor Performance**: Optimize recursive operations for large trees
6. **Inconsistent State**: Ensure operations maintain tree consistency

## Conclusion

The Composite Pattern is powerful for creating flexible tree structures where individual objects and compositions can be treated uniformly. It's particularly useful when you need to perform operations on hierarchical data structures.

Key takeaways:
- Use Composite for part-whole hierarchies with uniform operations
- It simplifies client code by providing a uniform interface
- Both leaves and composites implement the same interface
- Operations can be recursive and work on entire tree structures
- Great for UI systems, file systems, and organizational structures

Remember: The Composite Pattern shines when you have tree-like structures and want to treat individual objects and groups of objects the same way. It's about structural uniformity, not behavioral modification.