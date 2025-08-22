package org.example.designPatterns.visitorPattern;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * File System Visitor Example
 * 
 * This example demonstrates the Visitor Pattern applied to a file system structure.
 * Different visitors can perform various operations like calculating total size,
 * searching for files, counting files, generating reports, etc.
 * 
 * Key Features:
 * - Composite structure (files and directories)
 * - Multiple operations without modifying file system classes
 * - Clean separation between data structure and operations
 */

// Visitor interface
interface FileSystemVisitor {
    void visitFile(File file);
    void visitDirectory(Directory directory);
}

// Element interface
interface FileSystemElement {
    void accept(FileSystemVisitor visitor);
    String getName();
    long getSize();
    LocalDateTime getLastModified();
}

// Concrete Element - File
class File implements FileSystemElement {
    private String name;
    private long size;
    private LocalDateTime lastModified;
    private String extension;
    private String content;
    
    public File(String name, long size, String extension) {
        this.name = name;
        this.size = size;
        this.extension = extension;
        this.lastModified = LocalDateTime.now();
        this.content = "Sample content for " + name;
    }
    
    public File(String name, long size, String extension, LocalDateTime lastModified) {
        this.name = name;
        this.size = size;
        this.extension = extension;
        this.lastModified = lastModified;
        this.content = "Sample content for " + name;
    }
    
    @Override
    public void accept(FileSystemVisitor visitor) {
        visitor.visitFile(this);
    }
    
    @Override
    public String getName() { return name; }
    
    @Override
    public long getSize() { return size; }
    
    @Override
    public LocalDateTime getLastModified() { return lastModified; }
    
    public String getExtension() { return extension; }
    
    public String getContent() { return content; }
    
    @Override
    public String toString() {
        return "File{" + name + ", " + size + " bytes, " + extension + "}";
    }
}

// Concrete Element - Directory
class Directory implements FileSystemElement {
    private String name;
    private List<FileSystemElement> children;
    private LocalDateTime lastModified;
    
    public Directory(String name) {
        this.name = name;
        this.children = new ArrayList<>();
        this.lastModified = LocalDateTime.now();
    }
    
    public Directory(String name, LocalDateTime lastModified) {
        this.name = name;
        this.children = new ArrayList<>();
        this.lastModified = lastModified;
    }
    
    public void add(FileSystemElement element) {
        children.add(element);
    }
    
    public void remove(FileSystemElement element) {
        children.remove(element);
    }
    
    public List<FileSystemElement> getChildren() {
        return new ArrayList<>(children);
    }
    
    @Override
    public void accept(FileSystemVisitor visitor) {
        visitor.visitDirectory(this);
        // Visit all children
        for (FileSystemElement child : children) {
            child.accept(visitor);
        }
    }
    
    @Override
    public String getName() { return name; }
    
    @Override
    public long getSize() {
        long totalSize = 0;
        for (FileSystemElement child : children) {
            totalSize += child.getSize();
        }
        return totalSize;
    }
    
    @Override
    public LocalDateTime getLastModified() { return lastModified; }
    
    @Override
    public String toString() {
        return "Directory{" + name + ", " + children.size() + " items}";
    }
}

// Concrete Visitor - Size Calculator
class SizeCalculatorVisitor implements FileSystemVisitor {
    private long totalSize = 0;
    private int fileCount = 0;
    private int directoryCount = 0;
    
    @Override
    public void visitFile(File file) {
        totalSize += file.getSize();
        fileCount++;
        System.out.println("File: " + file.getName() + " - " + file.getSize() + " bytes");
    }
    
    @Override
    public void visitDirectory(Directory directory) {
        directoryCount++;
        System.out.println("Directory: " + directory.getName());
    }
    
    public long getTotalSize() { return totalSize; }
    public int getFileCount() { return fileCount; }
    public int getDirectoryCount() { return directoryCount; }
    
    public void printSummary() {
        System.out.println("\n=== Size Summary ===");
        System.out.println("Total size: " + totalSize + " bytes (" + 
                          (totalSize / 1024.0) + " KB)");
        System.out.println("Files: " + fileCount);
        System.out.println("Directories: " + directoryCount);
    }
}

// Concrete Visitor - File Search
class FileSearchVisitor implements FileSystemVisitor {
    private Pattern searchPattern;
    private List<File> foundFiles;
    private String currentPath = "";
    
    public FileSearchVisitor(String pattern) {
        this.searchPattern = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
        this.foundFiles = new ArrayList<>();
    }
    
    @Override
    public void visitFile(File file) {
        if (searchPattern.matcher(file.getName()).find() || 
            searchPattern.matcher(file.getExtension()).find()) {
            foundFiles.add(file);
            System.out.println("Found: " + currentPath + "/" + file.getName());
        }
    }
    
    @Override
    public void visitDirectory(Directory directory) {
        String previousPath = currentPath;
        currentPath += "/" + directory.getName();
        
        // Reset path after visiting directory
        if (directory.getChildren().isEmpty()) {
            currentPath = previousPath;
        }
    }
    
    public List<File> getFoundFiles() { 
        return new ArrayList<>(foundFiles); 
    }
    
    public void printResults() {
        System.out.println("\n=== Search Results ===");
        System.out.println("Found " + foundFiles.size() + " matching files:");
        for (File file : foundFiles) {
            System.out.println("- " + file.getName() + " (" + file.getSize() + " bytes)");
        }
    }
}

// Concrete Visitor - File Type Statistics
class FileTypeStatsVisitor implements FileSystemVisitor {
    private java.util.Map<String, Integer> fileTypeCounts = new java.util.HashMap<>();
    private java.util.Map<String, Long> fileTypeSizes = new java.util.HashMap<>();
    
    @Override
    public void visitFile(File file) {
        String extension = file.getExtension().toLowerCase();
        
        fileTypeCounts.merge(extension, 1, Integer::sum);
        fileTypeSizes.merge(extension, file.getSize(), Long::sum);
    }
    
    @Override
    public void visitDirectory(Directory directory) {
        // No specific action for directories in this visitor
    }
    
    public void printStatistics() {
        System.out.println("\n=== File Type Statistics ===");
        for (String extension : fileTypeCounts.keySet()) {
            int count = fileTypeCounts.get(extension);
            long size = fileTypeSizes.get(extension);
            System.out.println(extension + ": " + count + " files, " + 
                             size + " bytes (" + (size / 1024.0) + " KB)");
        }
    }
}

// Concrete Visitor - Directory Tree Printer
class TreePrinterVisitor implements FileSystemVisitor {
    private int depth = 0;
    private StringBuilder treeStructure = new StringBuilder();
    
    @Override
    public void visitFile(File file) {
        String indent = "  ".repeat(depth);
        String line = indent + "├── " + file.getName() + 
                     " (" + file.getSize() + " bytes)\n";
        treeStructure.append(line);
        System.out.print(line);
    }
    
    @Override
    public void visitDirectory(Directory directory) {
        String indent = "  ".repeat(depth);
        String line = indent + "└── " + directory.getName() + "/\n";
        treeStructure.append(line);
        System.out.print(line);
        depth++;
    }
    
    public String getTreeStructure() {
        return treeStructure.toString();
    }
}

// Concrete Visitor - Backup Validator
class BackupValidatorVisitor implements FileSystemVisitor {
    private List<String> issues = new ArrayList<>();
    private LocalDateTime cutoffDate;
    
    public BackupValidatorVisitor(LocalDateTime cutoffDate) {
        this.cutoffDate = cutoffDate;
    }
    
    @Override
    public void visitFile(File file) {
        // Check if file is too old
        if (file.getLastModified().isBefore(cutoffDate)) {
            issues.add("File " + file.getName() + " is older than backup cutoff date");
        }
        
        // Check for large files that might need special handling
        if (file.getSize() > 100 * 1024 * 1024) { // 100MB
            issues.add("Large file " + file.getName() + " (" + 
                      (file.getSize() / 1024 / 1024) + "MB) needs special backup handling");
        }
    }
    
    @Override
    public void visitDirectory(Directory directory) {
        // Check for empty directories
        if (directory.getChildren().isEmpty()) {
            issues.add("Empty directory: " + directory.getName());
        }
    }
    
    public void printValidationReport() {
        System.out.println("\n=== Backup Validation Report ===");
        if (issues.isEmpty()) {
            System.out.println("No issues found. Ready for backup.");
        } else {
            System.out.println("Found " + issues.size() + " issues:");
            for (String issue : issues) {
                System.out.println("- " + issue);
            }
        }
    }
}

// Concrete Visitor - File Content Indexer
class FileContentIndexerVisitor implements FileSystemVisitor {
    private java.util.Map<String, List<String>> wordIndex = new java.util.HashMap<>();
    
    @Override
    public void visitFile(File file) {
        if (file.getExtension().equals("txt") || file.getExtension().equals("java")) {
            indexFileContent(file);
        }
    }
    
    @Override
    public void visitDirectory(Directory directory) {
        // No specific action for directories
    }
    
    private void indexFileContent(File file) {
        String[] words = file.getContent().toLowerCase().split("\\s+");
        for (String word : words) {
            if (word.length() > 3) { // Index only words longer than 3 characters
                wordIndex.computeIfAbsent(word, k -> new ArrayList<>()).add(file.getName());
            }
        }
    }
    
    public void printIndex() {
        System.out.println("\n=== Content Index ===");
        for (java.util.Map.Entry<String, List<String>> entry : wordIndex.entrySet()) {
            System.out.println(entry.getKey() + " -> " + entry.getValue());
        }
    }
    
    public List<String> findFilesContaining(String word) {
        return wordIndex.getOrDefault(word.toLowerCase(), new ArrayList<>());
    }
}

// Demo class
public class FileSystemVisitorExample {
    public static void main(String[] args) {
        System.out.println("=== File System Visitor Pattern Example ===\n");
        
        // Create a sample file system structure
        Directory root = createSampleFileSystem();
        
        System.out.println("1. Calculating total size:");
        SizeCalculatorVisitor sizeCalculator = new SizeCalculatorVisitor();
        root.accept(sizeCalculator);
        sizeCalculator.printSummary();
        
        System.out.println("\n" + "=".repeat(50));
        
        System.out.println("2. Searching for Java files:");
        FileSearchVisitor javaSearch = new FileSearchVisitor("java");
        root.accept(javaSearch);
        javaSearch.printResults();
        
        System.out.println("\n" + "=".repeat(50));
        
        System.out.println("3. File type statistics:");
        FileTypeStatsVisitor statsVisitor = new FileTypeStatsVisitor();
        root.accept(statsVisitor);
        statsVisitor.printStatistics();
        
        System.out.println("\n" + "=".repeat(50));
        
        System.out.println("4. Directory tree structure:");
        TreePrinterVisitor treePrinter = new TreePrinterVisitor();
        root.accept(treePrinter);
        
        System.out.println("\n" + "=".repeat(50));
        
        System.out.println("5. Backup validation:");
        LocalDateTime weekAgo = LocalDateTime.now().minusDays(7);
        BackupValidatorVisitor backupValidator = new BackupValidatorVisitor(weekAgo);
        root.accept(backupValidator);
        backupValidator.printValidationReport();
        
        System.out.println("\n" + "=".repeat(50));
        
        System.out.println("6. Content indexing:");
        FileContentIndexerVisitor indexer = new FileContentIndexerVisitor();
        root.accept(indexer);
        indexer.printIndex();
        
        // Search for files containing specific words
        System.out.println("\nFiles containing 'sample':");
        List<String> files = indexer.findFilesContaining("sample");
        files.forEach(System.out::println);
    }
    
    private static Directory createSampleFileSystem() {
        // Create root directory
        Directory root = new Directory("root");
        
        // Create subdirectories
        Directory src = new Directory("src");
        Directory docs = new Directory("docs");
        Directory tests = new Directory("tests");
        Directory emptyDir = new Directory("empty");
        
        // Add files to src
        src.add(new File("Main.java", 2048, "java"));
        src.add(new File("Utils.java", 1024, "java"));
        src.add(new File("Config.properties", 512, "properties"));
        
        // Add files to docs
        docs.add(new File("README.txt", 1536, "txt"));
        docs.add(new File("Manual.pdf", 50 * 1024 * 1024, "pdf")); // 50MB file
        docs.add(new File("Architecture.md", 3072, "md"));
        
        // Add files to tests
        tests.add(new File("TestMain.java", 1800, "java"));
        tests.add(new File("TestUtils.java", 900, "java"));
        tests.add(new File("test-data.xml", 4096, "xml"));
        
        // Create nested structure
        Directory subSrc = new Directory("util");
        subSrc.add(new File("StringHelper.java", 1200, "java"));
        subSrc.add(new File("DateHelper.java", 800, "java"));
        src.add(subSrc);
        
        // Add some old files
        LocalDateTime oldDate = LocalDateTime.now().minusDays(30);
        Directory old = new Directory("old", oldDate);
        old.add(new File("OldFile.txt", 500, "txt", oldDate));
        old.add(new File("Legacy.java", 2000, "java", oldDate));
        
        // Build the structure
        root.add(src);
        root.add(docs);
        root.add(tests);
        root.add(old);
        root.add(emptyDir);
        
        // Add some files to root
        root.add(new File(".gitignore", 256, "gitignore"));
        root.add(new File("build.gradle", 1024, "gradle"));
        
        return root;
    }
}

/*
Key Benefits Demonstrated:

1. **Separation of Concerns**: File system structure is separate from operations
2. **Easy Extension**: New operations can be added without modifying file/directory classes
3. **Multiple Operations**: Different visitors perform completely different tasks
4. **Clean Code**: Each visitor focuses on one specific operation
5. **Composite Structure**: Works seamlessly with composite pattern (directories containing files/subdirectories)

Operations Implemented:
1. Size calculation with statistics
2. File search with pattern matching
3. File type analysis
4. Tree structure visualization
5. Backup validation
6. Content indexing

The example shows how the Visitor pattern excels when you have:
- A stable object structure (files and directories don't change often)
- Many different operations to perform on the structure
- Need to keep operations separate from the data structure
*/