package org.example.designPatterns.compositePattern;

import java.util.*;
import java.text.SimpleDateFormat;

/**
 * File System Composite Pattern Example
 * 
 * This example demonstrates the Composite Pattern using a file system hierarchy.
 * Files are leaf objects, Directories are composite objects, and both implement
 * the FileSystemComponent interface uniformly.
 * 
 * Features:
 * - Hierarchical file/directory structure
 * - Uniform operations on files and directories
 * - Recursive operations (copy, move, delete)
 * - File system statistics and search
 * - Permission management
 */

// Component interface
interface FileSystemComponent {
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
class File implements FileSystemComponent {
    private String name;
    private long size;
    private String path;
    private Date lastModified;
    private String permissions;
    private String content;
    private String fileType;
    
    public File(String name, long size, String path) {
        this.name = name;
        this.size = size;
        this.path = path;
        this.lastModified = new Date();
        this.permissions = "rw-r--r--";
        this.content = "File content of " + name;
        this.fileType = getFileExtension(name);
    }
    
    private String getFileExtension(String fileName) {
        int lastDot = fileName.lastIndexOf('.');
        return lastDot > 0 ? fileName.substring(lastDot + 1).toLowerCase() : "unknown";
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
        String icon = getFileIcon();
        System.out.println(indent + icon + " " + name + " (" + formatSize(size) + ")");
        System.out.println(indent + "   Path: " + path);
        System.out.println(indent + "   Modified: " + formatDate(lastModified));
        System.out.println(indent + "   Permissions: " + permissions);
        System.out.println(indent + "   Type: " + fileType.toUpperCase());
    }
    
    private String getFileIcon() {
        switch (fileType) {
            case "txt": case "md": case "doc": return "📄";
            case "pdf": return "📋";
            case "jpg": case "png": case "gif": return "🖼️";
            case "mp3": case "wav": return "🎵";
            case "mp4": case "avi": return "🎬";
            case "zip": case "rar": return "📦";
            case "java": case "py": case "js": return "⚙️";
            default: return "📄";
        }
    }
    
    @Override
    public FileSystemComponent copy() {
        File copy = new File(name + "_copy", size, path + "_copy");
        copy.content = this.content;
        copy.permissions = this.permissions;
        copy.fileType = this.fileType;
        System.out.println("📋 Copied file: " + name + " to " + copy.getName());
        return copy;
    }
    
    @Override
    public void move(String newPath) {
        String oldPath = this.path;
        this.path = newPath;
        this.lastModified = new Date();
        System.out.println("📦 Moved file: " + name + " from " + oldPath + " to " + newPath);
    }
    
    @Override
    public void delete() {
        System.out.println("🗑️ Deleted file: " + name + " (" + formatSize(size) + ")");
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
        System.out.println("🔐 Changed permissions for " + name + " to " + permissions);
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
        this.size = content.getBytes().length;
        this.lastModified = new Date();
    }
    
    public String getFileType() {
        return fileType;
    }
    
    public void compress() {
        long originalSize = size;
        size = size / 2; // Simulate compression
        System.out.println("🗜️ Compressed " + name + ": " + formatSize(originalSize) + " → " + formatSize(size));
    }
    
    public void encrypt() {
        System.out.println("🔒 Encrypted file: " + name);
        // In real implementation, would encrypt the content
    }
    
    private String formatSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
        return String.format("%.1f MB", bytes / (1024.0 * 1024.0));
    }
    
    private String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(date);
    }
}

// Composite implementation - Directory
class Directory implements FileSystemComponent {
    private String name;
    private String path;
    private Date lastModified;
    private String permissions;
    private List<FileSystemComponent> children;
    private Map<String, String> metadata;
    
    public Directory(String name, String path) {
        this.name = name;
        this.path = path;
        this.lastModified = new Date();
        this.permissions = "rwxr-xr-x";
        this.children = new ArrayList<>();
        this.metadata = new HashMap<>();
    }
    
    public void add(FileSystemComponent component) {
        children.add(component);
        this.lastModified = new Date();
        System.out.println("➕ Added " + component.getName() + " to directory " + this.name);
    }
    
    public void remove(FileSystemComponent component) {
        if (children.remove(component)) {
            this.lastModified = new Date();
            System.out.println("➖ Removed " + component.getName() + " from directory " + this.name);
        }
    }
    
    public void remove(String name) {
        children.removeIf(child -> child.getName().equals(name));
        this.lastModified = new Date();
        System.out.println("➖ Removed " + name + " from directory " + this.name);
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
        System.out.println(indent + "📁 " + name + "/ (" + formatSize(getSize()) + ")");
        System.out.println(indent + "   Path: " + path);
        System.out.println(indent + "   Modified: " + formatDate(lastModified));
        System.out.println(indent + "   Permissions: " + permissions);
        System.out.println(indent + "   Items: " + children.size());
        
        if (!metadata.isEmpty()) {
            System.out.println(indent + "   Metadata: " + metadata);
        }
        
        // Display children with increased indentation
        for (FileSystemComponent child : children) {
            child.display(indent + "  ");
        }
    }
    
    @Override
    public FileSystemComponent copy() {
        Directory copy = new Directory(name + "_copy", path + "_copy");
        copy.permissions = this.permissions;
        copy.metadata.putAll(this.metadata);
        
        // Recursively copy all children
        for (FileSystemComponent child : children) {
            copy.add(child.copy());
        }
        
        System.out.println("📋 Copied directory: " + name + " with " + children.size() + " items");
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
        
        System.out.println("📦 Moved directory: " + name + " from " + oldPath + " to " + newPath);
    }
    
    @Override
    public void delete() {
        System.out.println("🗑️ Deleting directory: " + name + " with " + children.size() + " items");
        
        // Recursively delete all children
        for (FileSystemComponent child : children) {
            child.delete();
        }
        
        children.clear();
        System.out.println("✅ Directory " + name + " deleted");
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
        System.out.println("🔐 Changed permissions for directory " + name + " to " + permissions);
    }
    
    public void setPermissionsRecursive(String permissions) {
        setPermissions(permissions);
        for (FileSystemComponent child : children) {
            child.setPermissions(permissions);
        }
        System.out.println("🔐 Applied permissions " + permissions + " recursively to " + name);
    }
    
    public List<FileSystemComponent> search(String pattern) {
        List<FileSystemComponent> results = new ArrayList<>();
        
        if (name.toLowerCase().contains(pattern.toLowerCase())) {
            results.add(this);
        }
        
        for (FileSystemComponent child : children) {
            if (child.getName().toLowerCase().contains(pattern.toLowerCase())) {
                results.add(child);
            }
            
            if (child instanceof Directory) {
                results.addAll(((Directory) child).search(pattern));
            }
        }
        
        return results;
    }
    
    public void compress(String archiveName) {
        System.out.println("🗜️ Creating archive: " + archiveName);
        System.out.println("📦 Compressing " + children.size() + " items from directory: " + name);
        
        long totalSize = getSize();
        long compressedSize = totalSize / 3; // Simulate compression
        
        for (FileSystemComponent child : children) {
            System.out.println("  📄 Adding to archive: " + child.getName());
        }
        
        System.out.println("✅ Archive created: " + archiveName);
        System.out.println("📊 Original size: " + formatSize(totalSize));
        System.out.println("📊 Compressed size: " + formatSize(compressedSize));
        System.out.println("📊 Compression ratio: " + String.format("%.1f%%", 
            ((double)(totalSize - compressedSize) / totalSize) * 100));
    }
    
    public void addMetadata(String key, String value) {
        metadata.put(key, value);
        System.out.println("🏷️ Added metadata to " + name + ": " + key + " = " + value);
    }
    
    public void createBackup() {
        String backupName = name + "_backup_" + System.currentTimeMillis();
        Directory backup = (Directory) this.copy();
        backup.name = backupName;
        System.out.println("💾 Created backup: " + backupName);
    }
    
    private String formatSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
        return String.format("%.1f MB", bytes / (1024.0 * 1024.0));
    }
    
    private String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(date);
    }
}

// File System Manager
class FileSystemManager {
    private Directory root;
    private Map<String, FileSystemComponent> index;
    
    public FileSystemManager(String rootName) {
        this.root = new Directory(rootName, "/" + rootName);
        this.index = new HashMap<>();
        buildIndex();
    }
    
    private void buildIndex() {
        index.clear();
        indexComponent(root);
    }
    
    private void indexComponent(FileSystemComponent component) {
        index.put(component.getPath(), component);
        if (component instanceof Directory) {
            Directory dir = (Directory) component;
            for (FileSystemComponent child : dir.getChildren()) {
                indexComponent(child);
            }
        }
    }
    
    public Directory getRoot() {
        return root;
    }
    
    public FileSystemComponent findByPath(String path) {
        return index.get(path);
    }
    
    public void displayFileSystem() {
        System.out.println("=== 🗂️ File System Structure ===");
        root.display("");
        System.out.println();
    }
    
    public void showStatistics() {
        System.out.println("=== 📊 File System Statistics ===");
        FileSystemStats stats = calculateStats(root);
        System.out.println("📄 Total files: " + stats.fileCount);
        System.out.println("📁 Total directories: " + stats.directoryCount);
        System.out.println("💾 Total size: " + formatSize(stats.totalSize));
        System.out.println("🏆 Largest file: " + stats.largestFileName + " (" + formatSize(stats.largestFileSize) + ")");
        System.out.println("📏 Average file size: " + formatSize(stats.totalSize / Math.max(stats.fileCount, 1)));
        System.out.println("🌲 Directory depth: " + stats.maxDepth);
        System.out.println();
    }
    
    public void searchFiles(String pattern) {
        System.out.println("=== 🔍 Search Results for '" + pattern + "' ===");
        List<FileSystemComponent> results = root.search(pattern);
        
        if (results.isEmpty()) {
            System.out.println("❌ No files or directories found matching: " + pattern);
        } else {
            System.out.println("✅ Found " + results.size() + " result(s):");
            for (FileSystemComponent result : results) {
                String type = result instanceof Directory ? "📁 DIR" : "📄 FILE";
                System.out.println("  " + type + " " + result.getName() + " (" + result.getPath() + ")");
            }
        }
        System.out.println();
    }
    
    public void cleanup() {
        System.out.println("=== 🧹 Cleanup Operations ===");
        cleanupEmptyDirectories(root);
        System.out.println();
    }
    
    private void cleanupEmptyDirectories(FileSystemComponent component) {
        if (component instanceof Directory) {
            Directory dir = (Directory) component;
            List<FileSystemComponent> toRemove = new ArrayList<>();
            
            for (FileSystemComponent child : dir.getChildren()) {
                cleanupEmptyDirectories(child);
                if (child instanceof Directory && ((Directory) child).getChildren().isEmpty()) {
                    toRemove.add(child);
                }
            }
            
            for (FileSystemComponent child : toRemove) {
                dir.remove(child);
                System.out.println("🧹 Removed empty directory: " + child.getName());
            }
        }
    }
    
    private FileSystemStats calculateStats(FileSystemComponent component) {
        return calculateStatsRecursive(component, 0);
    }
    
    private FileSystemStats calculateStatsRecursive(FileSystemComponent component, int depth) {
        FileSystemStats stats = new FileSystemStats();
        stats.maxDepth = depth;
        
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
                FileSystemStats childStats = calculateStatsRecursive(child, depth + 1);
                stats.fileCount += childStats.fileCount;
                stats.directoryCount += childStats.directoryCount;
                stats.totalSize += childStats.totalSize;
                stats.maxDepth = Math.max(stats.maxDepth, childStats.maxDepth);
                
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
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
        return String.format("%.1f MB", bytes / (1024.0 * 1024.0));
    }
    
    private static class FileSystemStats {
        int fileCount = 0;
        int directoryCount = 0;
        long totalSize = 0;
        long largestFileSize = 0;
        String largestFileName = "";
        int maxDepth = 0;
    }
}

// Demo class
public class FileSystemExample {
    public static void main(String[] args) {
        System.out.println("=== 🗂️ Composite Pattern: File System Example ===\n");
        
        // Create file system
        FileSystemManager fsManager = new FileSystemManager("root");
        Directory root = fsManager.getRoot();
        
        // Create directories
        Directory documents = new Directory("Documents", "/root/Documents");
        Directory projects = new Directory("Projects", "/root/Projects");
        Directory pictures = new Directory("Pictures", "/root/Pictures");
        Directory javaProject = new Directory("JavaProject", "/root/Projects/JavaProject");
        
        // Create files
        File readme = new File("README.md", 2048, "/root/README.md");
        File config = new File("config.txt", 512, "/root/config.txt");
        File presentation = new File("presentation.pdf", 5242880, "/root/Documents/presentation.pdf");
        File notes = new File("notes.txt", 1024, "/root/Documents/notes.txt");
        File mainJava = new File("Main.java", 4096, "/root/Projects/JavaProject/Main.java");
        File utilsJava = new File("Utils.java", 2048, "/root/Projects/JavaProject/Utils.java");
        File photo1 = new File("vacation.jpg", 3145728, "/root/Pictures/vacation.jpg");
        File photo2 = new File("family.png", 2097152, "/root/Pictures/family.png");
        
        // Build file system hierarchy
        root.add(readme);
        root.add(config);
        root.add(documents);
        root.add(projects);
        root.add(pictures);
        
        documents.add(presentation);
        documents.add(notes);
        
        projects.add(javaProject);
        javaProject.add(mainJava);
        javaProject.add(utilsJava);
        
        pictures.add(photo1);
        pictures.add(photo2);
        
        // Add metadata
        documents.addMetadata("type", "user_documents");
        projects.addMetadata("language", "java");
        pictures.addMetadata("format", "mixed");
        
        // Display file system
        fsManager.displayFileSystem();
        
        // Show statistics
        fsManager.showStatistics();
        
        // Demonstrate uniform operations
        System.out.println("=== 🔄 Uniform Operations Demo ===");
        
        // Copy operations
        System.out.println("\n--- 📋 Copy Operations ---");
        FileSystemComponent readmeCopy = readme.copy();
        FileSystemComponent projectsCopy = projects.copy();
        
        // Move operations
        System.out.println("\n--- 📦 Move Operations ---");
        notes.move("/root/Documents/archived/notes.txt");
        
        // Permission operations
        System.out.println("\n--- 🔐 Permission Operations ---");
        readme.setPermissions("r--r--r--");
        documents.setPermissionsRecursive("rwxrwxr--");
        
        // Search operations
        System.out.println("\n--- 🔍 Search Operations ---");
        fsManager.searchFiles("java");
        fsManager.searchFiles("photo");
        fsManager.searchFiles("config");
        
        // Compression demo
        System.out.println("--- 🗜️ Compression Demo ---");
        projects.compress("projects_backup.zip");
        
        // File-specific operations
        System.out.println("\n--- ⚙️ File-Specific Operations ---");
        if (mainJava instanceof File) {
            ((File) mainJava).compress();
            ((File) mainJava).encrypt();
        }
        
        // Directory-specific operations
        System.out.println("\n--- 📁 Directory-Specific Operations ---");
        documents.createBackup();
        
        // Cleanup operations
        fsManager.cleanup();
        
        // Delete demonstration
        System.out.println("--- 🗑️ Delete Operations ---");
        config.delete();
        pictures.delete(); // This will recursively delete all photos
        
        // Final display
        System.out.println("\n=== 📊 Final File System State ===");
        fsManager.displayFileSystem();
        fsManager.showStatistics();
        
        System.out.println("=== ✅ Demo Complete ===");
        
        // Key benefits demonstrated:
        System.out.println("\n=== 🎯 Key Benefits Demonstrated ===");
        System.out.println("✅ Uniform interface for files and directories");
        System.out.println("✅ Recursive operations work on entire hierarchies");
        System.out.println("✅ Easy to add new file/directory types");
        System.out.println("✅ Client code treats individual files and directories the same");
        System.out.println("✅ Complex operations can be applied to entire tree structures");
    }
}