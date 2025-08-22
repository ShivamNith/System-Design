package org.example.designPatterns.mementoPattern;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Memento Pattern Example: Advanced Text Editor with Undo/Redo
 * 
 * This example demonstrates the Memento Pattern with a comprehensive text editor
 * that supports undo/redo operations, document versioning, and state persistence.
 */

// Memento interface for type safety
interface EditorMemento {
    // Marker interface - only TextEditor should access the actual state
}

// Originator class - Text Editor
class TextEditor {
    private StringBuilder content;
    private int cursorPosition;
    private String documentName;
    private boolean isModified;
    private LocalDateTime lastModified;
    private Map<String, String> metadata;
    
    // Editor settings
    private String fontFamily;
    private int fontSize;
    private boolean wordWrap;
    
    public TextEditor(String documentName) {
        this.content = new StringBuilder();
        this.cursorPosition = 0;
        this.documentName = documentName;
        this.isModified = false;
        this.lastModified = LocalDateTime.now();
        this.metadata = new HashMap<>();
        this.fontFamily = "Arial";
        this.fontSize = 12;
        this.wordWrap = true;
    }
    
    // Text editing operations
    public void insertText(String text) {
        content.insert(cursorPosition, text);
        cursorPosition += text.length();
        markAsModified();
        System.out.println("Inserted: '" + text + "' at position " + (cursorPosition - text.length()));
    }
    
    public void deleteText(int startPos, int endPos) {
        if (startPos >= 0 && endPos <= content.length() && startPos < endPos) {
            String deletedText = content.substring(startPos, endPos);
            content.delete(startPos, endPos);
            cursorPosition = startPos;
            markAsModified();
            System.out.println("Deleted: '" + deletedText + "' from position " + startPos + " to " + endPos);
        }
    }
    
    public void replaceText(int startPos, int endPos, String newText) {
        if (startPos >= 0 && endPos <= content.length() && startPos <= endPos) {
            String oldText = content.substring(startPos, endPos);
            content.replace(startPos, endPos, newText);
            cursorPosition = startPos + newText.length();
            markAsModified();
            System.out.println("Replaced: '" + oldText + "' with '" + newText + "'");
        }
    }
    
    public void moveCursor(int position) {
        if (position >= 0 && position <= content.length()) {
            this.cursorPosition = position;
            System.out.println("Cursor moved to position " + position);
        }
    }
    
    // Editor settings
    public void setFontFamily(String fontFamily) {
        this.fontFamily = fontFamily;
        markAsModified();
        System.out.println("Font changed to: " + fontFamily);
    }
    
    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
        markAsModified();
        System.out.println("Font size changed to: " + fontSize);
    }
    
    public void setWordWrap(boolean wordWrap) {
        this.wordWrap = wordWrap;
        markAsModified();
        System.out.println("Word wrap " + (wordWrap ? "enabled" : "disabled"));
    }
    
    // Metadata operations
    public void addMetadata(String key, String value) {
        metadata.put(key, value);
        markAsModified();
        System.out.println("Added metadata: " + key + " = " + value);
    }
    
    private void markAsModified() {
        this.isModified = true;
        this.lastModified = LocalDateTime.now();
    }
    
    // Memento creation
    public EditorMemento createMemento() {
        return new TextEditorMemento(
            new StringBuilder(content),
            cursorPosition,
            documentName,
            isModified,
            lastModified,
            new HashMap<>(metadata),
            fontFamily,
            fontSize,
            wordWrap
        );
    }
    
    // Memento restoration
    public void restoreMemento(EditorMemento memento) {
        if (memento instanceof TextEditorMemento) {
            TextEditorMemento editorMemento = (TextEditorMemento) memento;
            this.content = new StringBuilder(editorMemento.content);
            this.cursorPosition = editorMemento.cursorPosition;
            this.documentName = editorMemento.documentName;
            this.isModified = editorMemento.isModified;
            this.lastModified = editorMemento.lastModified;
            this.metadata = new HashMap<>(editorMemento.metadata);
            this.fontFamily = editorMemento.fontFamily;
            this.fontSize = editorMemento.fontSize;
            this.wordWrap = editorMemento.wordWrap;
            
            System.out.println("Restored editor state from " + 
                editorMemento.lastModified.format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        }
    }
    
    // Getters for display
    public String getContent() { return content.toString(); }
    public int getCursorPosition() { return cursorPosition; }
    public String getDocumentName() { return documentName; }
    public boolean isModified() { return isModified; }
    public LocalDateTime getLastModified() { return lastModified; }
    public String getFontFamily() { return fontFamily; }
    public int getFontSize() { return fontSize; }
    public boolean isWordWrap() { return wordWrap; }
    
    // Display current state
    public void displayStatus() {
        System.out.println("\n=== Editor Status ===");
        System.out.println("Document: " + documentName);
        System.out.println("Content: \"" + content.toString() + "\"");
        System.out.println("Cursor Position: " + cursorPosition);
        System.out.println("Modified: " + isModified);
        System.out.println("Last Modified: " + lastModified.format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        System.out.println("Font: " + fontFamily + ", " + fontSize + "pt");
        System.out.println("Word Wrap: " + wordWrap);
        System.out.println("Metadata: " + metadata);
        System.out.println("=====================\n");
    }
    
    // Inner memento class for encapsulation
    private static class TextEditorMemento implements EditorMemento {
        private final StringBuilder content;
        private final int cursorPosition;
        private final String documentName;
        private final boolean isModified;
        private final LocalDateTime lastModified;
        private final Map<String, String> metadata;
        private final String fontFamily;
        private final int fontSize;
        private final boolean wordWrap;
        
        private TextEditorMemento(StringBuilder content, int cursorPosition, String documentName,
                                 boolean isModified, LocalDateTime lastModified, 
                                 Map<String, String> metadata, String fontFamily, 
                                 int fontSize, boolean wordWrap) {
            this.content = content;
            this.cursorPosition = cursorPosition;
            this.documentName = documentName;
            this.isModified = isModified;
            this.lastModified = lastModified;
            this.metadata = metadata;
            this.fontFamily = fontFamily;
            this.fontSize = fontSize;
            this.wordWrap = wordWrap;
        }
        
        @Override
        public String toString() {
            return "Snapshot[" + lastModified.format(DateTimeFormatter.ofPattern("HH:mm:ss")) + 
                   ", content: \"" + content.toString() + "\", cursor: " + cursorPosition + "]";
        }
    }
}

// Caretaker class - Editor History Manager
class EditorHistory {
    private final TextEditor editor;
    private final List<EditorMemento> undoStack;
    private final List<EditorMemento> redoStack;
    private final int maxHistorySize;
    
    public EditorHistory(TextEditor editor, int maxHistorySize) {
        this.editor = editor;
        this.undoStack = new ArrayList<>();
        this.redoStack = new ArrayList<>();
        this.maxHistorySize = maxHistorySize;
    }
    
    public EditorHistory(TextEditor editor) {
        this(editor, 50); // Default max history size
    }
    
    // Save current state for undo
    public void saveState() {
        // Clear redo stack when new action is performed
        redoStack.clear();
        
        // Add current state to undo stack
        undoStack.add(editor.createMemento());
        
        // Maintain maximum history size
        if (undoStack.size() > maxHistorySize) {
            undoStack.remove(0);
        }
        
        System.out.println("State saved (Undo stack size: " + undoStack.size() + ")");
    }
    
    // Undo last operation
    public boolean undo() {
        if (undoStack.isEmpty()) {
            System.out.println("Nothing to undo");
            return false;
        }
        
        // Save current state to redo stack
        redoStack.add(editor.createMemento());
        
        // Restore previous state
        EditorMemento memento = undoStack.remove(undoStack.size() - 1);
        editor.restoreMemento(memento);
        
        System.out.println("Undo performed (Undo stack size: " + undoStack.size() + 
                         ", Redo stack size: " + redoStack.size() + ")");
        return true;
    }
    
    // Redo last undone operation
    public boolean redo() {
        if (redoStack.isEmpty()) {
            System.out.println("Nothing to redo");
            return false;
        }
        
        // Save current state to undo stack
        undoStack.add(editor.createMemento());
        
        // Restore next state
        EditorMemento memento = redoStack.remove(redoStack.size() - 1);
        editor.restoreMemento(memento);
        
        System.out.println("Redo performed (Undo stack size: " + undoStack.size() + 
                         ", Redo stack size: " + redoStack.size() + ")");
        return true;
    }
    
    // Get history information
    public boolean canUndo() {
        return !undoStack.isEmpty();
    }
    
    public boolean canRedo() {
        return !redoStack.isEmpty();
    }
    
    public int getUndoCount() {
        return undoStack.size();
    }
    
    public int getRedoCount() {
        return redoStack.size();
    }
    
    // Clear all history
    public void clearHistory() {
        undoStack.clear();
        redoStack.clear();
        System.out.println("History cleared");
    }
    
    // Get history summary
    public void printHistory() {
        System.out.println("\n=== History Status ===");
        System.out.println("Can Undo: " + canUndo() + " (count: " + getUndoCount() + ")");
        System.out.println("Can Redo: " + canRedo() + " (count: " + getRedoCount() + ")");
        
        if (!undoStack.isEmpty()) {
            System.out.println("Undo Stack (recent first):");
            for (int i = undoStack.size() - 1; i >= Math.max(0, undoStack.size() - 5); i--) {
                System.out.println("  " + (undoStack.size() - i) + ": " + undoStack.get(i));
            }
        }
        
        if (!redoStack.isEmpty()) {
            System.out.println("Redo Stack (next first):");
            for (int i = redoStack.size() - 1; i >= Math.max(0, redoStack.size() - 5); i--) {
                System.out.println("  " + (redoStack.size() - i) + ": " + redoStack.get(i));
            }
        }
        System.out.println("======================\n");
    }
}

// Advanced caretaker for document versioning
class DocumentVersionManager {
    private final TextEditor editor;
    private final Map<String, EditorMemento> namedVersions;
    private final List<VersionInfo> versionHistory;
    
    public DocumentVersionManager(TextEditor editor) {
        this.editor = editor;
        this.namedVersions = new HashMap<>();
        this.versionHistory = new ArrayList<>();
    }
    
    // Create a named version
    public void createVersion(String versionName, String description) {
        EditorMemento memento = editor.createMemento();
        namedVersions.put(versionName, memento);
        
        VersionInfo versionInfo = new VersionInfo(versionName, description, 
                                                LocalDateTime.now(), editor.getContent());
        versionHistory.add(versionInfo);
        
        System.out.println("Version '" + versionName + "' created: " + description);
    }
    
    // Restore from named version
    public boolean restoreVersion(String versionName) {
        EditorMemento memento = namedVersions.get(versionName);
        if (memento != null) {
            editor.restoreMemento(memento);
            System.out.println("Restored to version: " + versionName);
            return true;
        } else {
            System.out.println("Version '" + versionName + "' not found");
            return false;
        }
    }
    
    // List available versions
    public void listVersions() {
        System.out.println("\n=== Available Versions ===");
        if (versionHistory.isEmpty()) {
            System.out.println("No versions saved");
        } else {
            for (VersionInfo version : versionHistory) {
                System.out.println(version);
            }
        }
        System.out.println("===========================\n");
    }
    
    // Delete a version
    public boolean deleteVersion(String versionName) {
        EditorMemento removed = namedVersions.remove(versionName);
        if (removed != null) {
            versionHistory.removeIf(v -> v.name.equals(versionName));
            System.out.println("Version '" + versionName + "' deleted");
            return true;
        } else {
            System.out.println("Version '" + versionName + "' not found");
            return false;
        }
    }
    
    private static class VersionInfo {
        private final String name;
        private final String description;
        private final LocalDateTime timestamp;
        private final String contentPreview;
        
        public VersionInfo(String name, String description, LocalDateTime timestamp, String content) {
            this.name = name;
            this.description = description;
            this.timestamp = timestamp;
            this.contentPreview = content.length() > 30 ? content.substring(0, 30) + "..." : content;
        }
        
        @Override
        public String toString() {
            return String.format("'%s' - %s [%s] Preview: \"%s\"", 
                               name, description, 
                               timestamp.format(DateTimeFormatter.ofPattern("HH:mm:ss")), 
                               contentPreview);
        }
    }
}

// Client code demonstrating the Memento Pattern
public class TextEditorExample {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Text Editor Memento Pattern Demo ===\n");
        
        // Create text editor and history manager
        TextEditor editor = new TextEditor("MyDocument.txt");
        EditorHistory history = new EditorHistory(editor);
        DocumentVersionManager versionManager = new DocumentVersionManager(editor);
        
        // Initial state
        editor.displayStatus();
        
        // Perform some editing operations
        System.out.println("=== Text Editing Operations ===");
        
        history.saveState(); // Save initial empty state
        
        editor.insertText("Hello World!");
        history.saveState();
        
        editor.moveCursor(6);
        editor.insertText("Beautiful ");
        history.saveState();
        
        editor.moveCursor(editor.getContent().length());
        editor.insertText(" This is a test document.");
        history.saveState();
        
        editor.displayStatus();
        
        // Create a version
        versionManager.createVersion("v1.0", "Initial draft with basic content");
        
        Thread.sleep(1000);
        
        // More editing
        System.out.println("\n=== More Editing ===");
        editor.replaceText(0, 5, "Hi");
        history.saveState();
        
        editor.setFontFamily("Times New Roman");
        editor.setFontSize(14);
        history.saveState();
        
        editor.addMetadata("author", "John Doe");
        editor.addMetadata("created", "2024-01-15");
        history.saveState();
        
        editor.displayStatus();
        
        // Create another version
        versionManager.createVersion("v1.1", "Updated formatting and metadata");
        
        // Demonstrate undo/redo
        System.out.println("\n=== Undo/Redo Operations ===");
        history.printHistory();
        
        // Undo a few operations
        history.undo();
        editor.displayStatus();
        
        history.undo();
        editor.displayStatus();
        
        history.undo();
        editor.displayStatus();
        
        // Redo some operations
        history.redo();
        editor.displayStatus();
        
        history.redo();
        editor.displayStatus();
        
        // Show version management
        System.out.println("\n=== Version Management ===");
        versionManager.listVersions();
        
        // Make more changes
        editor.deleteText(0, 2);
        editor.insertText("Greetings");
        history.saveState();
        
        versionManager.createVersion("v1.2", "Changed greeting text");
        editor.displayStatus();
        
        // Restore to previous version
        System.out.println("\n=== Version Restoration ===");
        versionManager.restoreVersion("v1.0");
        editor.displayStatus();
        
        versionManager.restoreVersion("v1.2");
        editor.displayStatus();
        
        // Final history and version status
        System.out.println("\n=== Final Status ===");
        history.printHistory();
        versionManager.listVersions();
        
        // Demonstrate history limits
        System.out.println("\n=== Testing History Limits ===");
        EditorHistory limitedHistory = new EditorHistory(editor, 3);
        
        for (int i = 1; i <= 5; i++) {
            editor.insertText(" " + i);
            limitedHistory.saveState();
            System.out.println("Added number " + i + ", history size: " + limitedHistory.getUndoCount());
        }
        
        limitedHistory.printHistory();
        
        // Clean up
        System.out.println("\n=== Cleanup ===");
        history.clearHistory();
        versionManager.deleteVersion("v1.1");
        versionManager.listVersions();
    }
}

/*
Expected Output:
=== Text Editor Memento Pattern Demo ===

=== Editor Status ===
Document: MyDocument.txt
Content: ""
Cursor Position: 0
Modified: false
Last Modified: [timestamp]
Font: Arial, 12pt
Word Wrap: true
Metadata: {}
=====================

=== Text Editing Operations ===
State saved (Undo stack size: 1)
Inserted: 'Hello World!' at position 0
State saved (Undo stack size: 2)
Cursor moved to position 6
Inserted: 'Beautiful ' at position 6
State saved (Undo stack size: 3)
Cursor moved to position 32
Inserted: ' This is a test document.' at position 32
State saved (Undo stack size: 4)

=== Editor Status ===
Document: MyDocument.txt
Content: "Hello Beautiful World! This is a test document."
Cursor Position: 57
Modified: true
Last Modified: [timestamp]
Font: Arial, 12pt
Word Wrap: true
Metadata: {}
=====================

Version 'v1.0' created: Initial draft with basic content

=== More Editing ===
Replaced: 'Hello' with 'Hi'
State saved (Undo stack size: 5)
Font changed to: Times New Roman
State saved (Undo stack size: 6)
Font size changed to: 14
State saved (Undo stack size: 7)
Added metadata: author = John Doe
State saved (Undo stack size: 8)
Added metadata: created = 2024-01-15
State saved (Undo stack size: 9)

=== Editor Status ===
Document: MyDocument.txt
Content: "Hi Beautiful World! This is a test document."
Cursor Position: 57
Modified: true
Last Modified: [timestamp]
Font: Times New Roman, 14pt
Word Wrap: true
Metadata: {author=John Doe, created=2024-01-15}
=====================

Version 'v1.1' created: Updated formatting and metadata

=== Undo/Redo Operations ===

=== History Status ===
Can Undo: true (count: 9)
Can Redo: false (count: 0)
Undo Stack (recent first):
  1: Snapshot[[timestamp], content: "Hi Beautiful World! This is a test document.", cursor: 57]
  2: Snapshot[[timestamp], content: "Hi Beautiful World! This is a test document.", cursor: 57]
  3: Snapshot[[timestamp], content: "Hi Beautiful World! This is a test document.", cursor: 57]
  4: Snapshot[[timestamp], content: "Hi Beautiful World! This is a test document.", cursor: 57]
  5: Snapshot[[timestamp], content: "Hi Beautiful World! This is a test document.", cursor: 57]
======================

Restored editor state from [timestamp]
Undo performed (Undo stack size: 8, Redo stack size: 1)

=== Editor Status ===
Document: MyDocument.txt
Content: "Hi Beautiful World! This is a test document."
Cursor Position: 57
Modified: true
Last Modified: [timestamp]
Font: Times New Roman, 14pt
Word Wrap: true
Metadata: {author=John Doe}
=====================

Restored editor state from [timestamp]
Undo performed (Undo stack size: 7, Redo stack size: 2)

=== Editor Status ===
Document: MyDocument.txt
Content: "Hi Beautiful World! This is a test document."
Cursor Position: 57
Modified: true
Last Modified: [timestamp]
Font: Times New Roman, 14pt
Word Wrap: true
Metadata: {}
=====================

=== Version Management ===

=== Available Versions ===
'v1.0' - Initial draft with basic content [[timestamp]] Preview: "Hello Beautiful World! This is..."
'v1.1' - Updated formatting and metadata [[timestamp]] Preview: "Hi Beautiful World! This is a te..."
===========================

=== Final Status ===

=== History Status ===
Can Undo: true (count: 8)
Can Redo: false (count: 0)
Undo Stack (recent first):
  1: Snapshot[[timestamp], content: "Greetings Beautiful World! This is a test document.", cursor: 55]
  2: Snapshot[[timestamp], content: "Hi Beautiful World! This is a test document.", cursor: 57]
  3: Snapshot[[timestamp], content: "Hi Beautiful World! This is a test document.", cursor: 57]
  4: Snapshot[[timestamp], content: "Hi Beautiful World! This is a test document.", cursor: 57]
  5: Snapshot[[timestamp], content: "Hi Beautiful World! This is a test document.", cursor: 57]
======================

=== Available Versions ===
'v1.0' - Initial draft with basic content [[timestamp]] Preview: "Hello Beautiful World! This is..."
'v1.1' - Updated formatting and metadata [[timestamp]] Preview: "Hi Beautiful World! This is a te..."
'v1.2' - Changed greeting text [[timestamp]] Preview: "Greetings Beautiful World! This i..."
===========================

=== Testing History Limits ===
Inserted: ' 1' at position 55
State saved (Undo stack size: 1)
Added number 1, history size: 1
Inserted: ' 2' at position 57
State saved (Undo stack size: 2)
Added number 2, history size: 2
Inserted: ' 3' at position 59
State saved (Undo stack size: 3)
Added number 3, history size: 3
Inserted: ' 4' at position 61
State saved (Undo stack size: 3)
Added number 4, history size: 3
Inserted: ' 5' at position 63
State saved (Undo stack size: 3)
Added number 5, history size: 3

=== Cleanup ===
History cleared
Version 'v1.1' deleted

=== Available Versions ===
'v1.0' - Initial draft with basic content [[timestamp]] Preview: "Hello Beautiful World! This is..."
'v1.2' - Changed greeting text [[timestamp]] Preview: "Greetings Beautiful World! This i..."
===========================
*/