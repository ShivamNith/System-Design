package org.example.designPatterns.commandPattern;

import java.util.*;

/**
 * Text Editor Command Pattern Example
 * 
 * This example demonstrates the Command Pattern implementation for a text editor
 * with undo/redo functionality. It shows how commands can encapsulate text operations
 * and provide reversible actions.
 * 
 * Features:
 * - Insert text command with undo
 * - Delete text command with undo
 * - Replace text command with undo
 * - Undo/Redo manager with command history
 * - Macro commands for complex operations
 */
public class TextEditorCommandExample {

    // Command interface
    interface Command {
        void execute();
        void undo();
        String getDescription();
    }

    // Receiver - Text Document
    static class TextDocument {
        private StringBuilder content;
        
        public TextDocument() {
            this.content = new StringBuilder();
        }
        
        public void insert(int position, String text) {
            if (position >= 0 && position <= content.length()) {
                content.insert(position, text);
            }
        }
        
        public String delete(int position, int length) {
            if (position >= 0 && position + length <= content.length()) {
                String deleted = content.substring(position, position + length);
                content.delete(position, position + length);
                return deleted;
            }
            return "";
        }
        
        public String replace(int position, int length, String newText) {
            String oldText = "";
            if (position >= 0 && position + length <= content.length()) {
                oldText = content.substring(position, position + length);
                content.delete(position, position + length);
                content.insert(position, newText);
            }
            return oldText;
        }
        
        public String getContent() {
            return content.toString();
        }
        
        public int length() {
            return content.length();
        }
        
        public void clear() {
            content.setLength(0);
        }
    }

    // Concrete Commands
    static class InsertCommand implements Command {
        private TextDocument document;
        private int position;
        private String text;
        
        public InsertCommand(TextDocument document, int position, String text) {
            this.document = document;
            this.position = position;
            this.text = text;
        }
        
        @Override
        public void execute() {
            document.insert(position, text);
        }
        
        @Override
        public void undo() {
            document.delete(position, text.length());
        }
        
        @Override
        public String getDescription() {
            return "Insert '" + text + "' at position " + position;
        }
    }

    static class DeleteCommand implements Command {
        private TextDocument document;
        private int position;
        private int length;
        private String deletedText;
        
        public DeleteCommand(TextDocument document, int position, int length) {
            this.document = document;
            this.position = position;
            this.length = length;
        }
        
        @Override
        public void execute() {
            deletedText = document.delete(position, length);
        }
        
        @Override
        public void undo() {
            if (deletedText != null) {
                document.insert(position, deletedText);
            }
        }
        
        @Override
        public String getDescription() {
            return "Delete " + length + " characters at position " + position;
        }
    }

    static class ReplaceCommand implements Command {
        private TextDocument document;
        private int position;
        private int length;
        private String newText;
        private String oldText;
        
        public ReplaceCommand(TextDocument document, int position, int length, String newText) {
            this.document = document;
            this.position = position;
            this.length = length;
            this.newText = newText;
        }
        
        @Override
        public void execute() {
            oldText = document.replace(position, length, newText);
        }
        
        @Override
        public void undo() {
            if (oldText != null) {
                document.replace(position, newText.length(), oldText);
            }
        }
        
        @Override
        public String getDescription() {
            return "Replace " + length + " characters at position " + position + " with '" + newText + "'";
        }
    }

    // Macro Command - Composite Command
    static class MacroCommand implements Command {
        private List<Command> commands;
        private String description;
        
        public MacroCommand(String description) {
            this.commands = new ArrayList<>();
            this.description = description;
        }
        
        public void addCommand(Command command) {
            commands.add(command);
        }
        
        @Override
        public void execute() {
            for (Command command : commands) {
                command.execute();
            }
        }
        
        @Override
        public void undo() {
            // Undo in reverse order
            for (int i = commands.size() - 1; i >= 0; i--) {
                commands.get(i).undo();
            }
        }
        
        @Override
        public String getDescription() {
            return description + " (" + commands.size() + " operations)";
        }
    }

    // Invoker - Text Editor
    static class TextEditor {
        private TextDocument document;
        private Stack<Command> undoStack;
        private Stack<Command> redoStack;
        private int maxHistorySize;
        
        public TextEditor() {
            this.document = new TextDocument();
            this.undoStack = new Stack<>();
            this.redoStack = new Stack<>();
            this.maxHistorySize = 100;
        }
        
        public void executeCommand(Command command) {
            command.execute();
            undoStack.push(command);
            redoStack.clear(); // Clear redo stack when new command is executed
            
            // Limit history size
            if (undoStack.size() > maxHistorySize) {
                undoStack.removeElementAt(0);
            }
            
            System.out.println("Executed: " + command.getDescription());
        }
        
        public boolean undo() {
            if (!undoStack.isEmpty()) {
                Command command = undoStack.pop();
                command.undo();
                redoStack.push(command);
                System.out.println("Undone: " + command.getDescription());
                return true;
            }
            System.out.println("Nothing to undo");
            return false;
        }
        
        public boolean redo() {
            if (!redoStack.isEmpty()) {
                Command command = redoStack.pop();
                command.execute();
                undoStack.push(command);
                System.out.println("Redone: " + command.getDescription());
                return true;
            }
            System.out.println("Nothing to redo");
            return false;
        }
        
        public String getContent() {
            return document.getContent();
        }
        
        public void showHistory() {
            System.out.println("\nUndo History:");
            if (undoStack.isEmpty()) {
                System.out.println("  (empty)");
            } else {
                for (int i = undoStack.size() - 1; i >= 0; i--) {
                    System.out.println("  " + (undoStack.size() - i) + ". " + undoStack.get(i).getDescription());
                }
            }
            
            System.out.println("\nRedo History:");
            if (redoStack.isEmpty()) {
                System.out.println("  (empty)");
            } else {
                for (int i = redoStack.size() - 1; i >= 0; i--) {
                    System.out.println("  " + (redoStack.size() - i) + ". " + redoStack.get(i).getDescription());
                }
            }
        }
        
        // Helper methods for common operations
        public void insertText(String text) {
            insertText(document.length(), text);
        }
        
        public void insertText(int position, String text) {
            Command command = new InsertCommand(document, position, text);
            executeCommand(command);
        }
        
        public void deleteText(int position, int length) {
            Command command = new DeleteCommand(document, position, length);
            executeCommand(command);
        }
        
        public void replaceText(int position, int length, String newText) {
            Command command = new ReplaceCommand(document, position, length, newText);
            executeCommand(command);
        }
        
        public void performComplexEdit(String description, List<Command> commands) {
            MacroCommand macro = new MacroCommand(description);
            for (Command command : commands) {
                macro.addCommand(command);
            }
            executeCommand(macro);
        }
    }

    // Demonstration
    public static void main(String[] args) {
        System.out.println("=== Text Editor Command Pattern Example ===\n");
        
        TextEditor editor = new TextEditor();
        
        // Basic text operations
        System.out.println("1. Basic Text Operations:");
        editor.insertText("Hello World!");
        System.out.println("Content: '" + editor.getContent() + "'");
        
        editor.insertText(5, " Beautiful");
        System.out.println("Content: '" + editor.getContent() + "'");
        
        editor.replaceText(6, 9, "Amazing");
        System.out.println("Content: '" + editor.getContent() + "'");
        
        // Demonstrate undo/redo
        System.out.println("\n2. Undo/Redo Operations:");
        System.out.println("Content before undo: '" + editor.getContent() + "'");
        
        editor.undo();
        System.out.println("Content after undo: '" + editor.getContent() + "'");
        
        editor.undo();
        System.out.println("Content after 2nd undo: '" + editor.getContent() + "'");
        
        editor.redo();
        System.out.println("Content after redo: '" + editor.getContent() + "'");
        
        // Complex macro operation
        System.out.println("\n3. Macro Command Example:");
        List<Command> complexCommands = Arrays.asList(
            new DeleteCommand(editor.document, 0, 5), // Delete "Hello"
            new InsertCommand(editor.document, 0, "Hi"), // Insert "Hi"
            new InsertCommand(editor.document, editor.document.length(), " How are you?") // Append text
        );
        
        editor.performComplexEdit("Greeting Change", complexCommands);
        System.out.println("Content after macro: '" + editor.getContent() + "'");
        
        // Show history
        System.out.println("\n4. Command History:");
        editor.showHistory();
        
        // Undo macro (all operations reversed)
        System.out.println("\n5. Undo Macro:");
        editor.undo();
        System.out.println("Content after undoing macro: '" + editor.getContent() + "'");
        
        // Multiple operations and history management
        System.out.println("\n6. Multiple Operations:");
        editor.insertText(" Testing");
        editor.insertText(" More");
        editor.insertText(" Text");
        editor.deleteText(editor.getContent().length() - 5, 5);
        
        System.out.println("Final content: '" + editor.getContent() + "'");
        
        // Final history
        System.out.println("\n7. Final Command History:");
        editor.showHistory();
        
        System.out.println("\nDemonstration completed!");
    }
}