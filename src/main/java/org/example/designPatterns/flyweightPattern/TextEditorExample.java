package org.example.designPatterns.flyweightPattern;

import java.util.*;

/**
 * Text Editor Example demonstrating Flyweight Pattern
 * 
 * This example shows how character formatting can be shared among
 * multiple character instances in a text editor to save memory.
 */

// Flyweight interface
interface CharacterFlyweight {
    void render(int position, char character, String context);
}

// Concrete Flyweight - stores intrinsic state (font, size, color)
class CharacterFormat implements CharacterFlyweight {
    private final String font;
    private final int size;
    private final String color;
    
    public CharacterFormat(String font, int size, String color) {
        this.font = font;
        this.size = size;
        this.color = color;
    }
    
    @Override
    public void render(int position, char character, String context) {
        System.out.println(String.format(
            "Rendering '%c' at position %d with font: %s, size: %d, color: %s in context: %s",
            character, position, font, size, color, context
        ));
    }
    
    public String getDetails() {
        return String.format("Font: %s, Size: %d, Color: %s", font, size, color);
    }
}

// Flyweight Factory
class CharacterFormatFactory {
    private static final Map<String, CharacterFormat> formats = new HashMap<>();
    
    public static CharacterFormat getFormat(String font, int size, String color) {
        String key = font + "_" + size + "_" + color;
        
        if (!formats.containsKey(key)) {
            formats.put(key, new CharacterFormat(font, size, color));
            System.out.println("Creating new format: " + key);
        }
        
        return formats.get(key);
    }
    
    public static void printCreatedFormats() {
        System.out.println("\nTotal formats created: " + formats.size());
        for (String key : formats.keySet()) {
            System.out.println("Format: " + key);
        }
    }
}

// Context class - stores extrinsic state
class TextCharacter {
    private char character;
    private int position;
    private CharacterFormat format;
    
    public TextCharacter(char character, int position, CharacterFormat format) {
        this.character = character;
        this.position = position;
        this.format = format;
    }
    
    public void render(String context) {
        format.render(position, character, context);
    }
}

// Document class that manages characters
class Document {
    private List<TextCharacter> characters;
    private String name;
    
    public Document(String name) {
        this.name = name;
        this.characters = new ArrayList<>();
    }
    
    public void addCharacter(char ch, int position, String font, int size, String color) {
        CharacterFormat format = CharacterFormatFactory.getFormat(font, size, color);
        characters.add(new TextCharacter(ch, position, format));
    }
    
    public void render() {
        System.out.println("\n=== Rendering Document: " + name + " ===");
        for (TextCharacter character : characters) {
            character.render(name);
        }
    }
    
    public int getCharacterCount() {
        return characters.size();
    }
}

// Client code
public class TextEditorExample {
    public static void main(String[] args) {
        System.out.println("=== Flyweight Pattern: Text Editor Example ===\n");
        
        // Create documents
        Document doc1 = new Document("Resume.doc");
        Document doc2 = new Document("Letter.doc");
        
        // Add characters to first document
        String text1 = "Hello World";
        for (int i = 0; i < text1.length(); i++) {
            if (i < 5) {
                // First word in Arial, 12pt, Black
                doc1.addCharacter(text1.charAt(i), i, "Arial", 12, "Black");
            } else {
                // Second word in Arial, 12pt, Red (demonstrating format reuse)
                doc1.addCharacter(text1.charAt(i), i, "Arial", 12, "Red");
            }
        }
        
        // Add characters to second document (reusing formats)
        String text2 = "Dear Sir";
        for (int i = 0; i < text2.length(); i++) {
            if (i < 4) {
                // "Dear" in Arial, 12pt, Black (reusing existing format)
                doc2.addCharacter(text2.charAt(i), i, "Arial", 12, "Black");
            } else {
                // " Sir" in Times, 14pt, Blue (new format)
                doc2.addCharacter(text2.charAt(i), i, "Times", 14, "Blue");
            }
        }
        
        // Render documents
        doc1.render();
        doc2.render();
        
        // Show memory efficiency
        System.out.println("\n=== Memory Efficiency Demonstration ===");
        System.out.println("Total characters created: " + 
            (doc1.getCharacterCount() + doc2.getCharacterCount()));
        
        CharacterFormatFactory.printCreatedFormats();
        
        System.out.println("\nWithout Flyweight: Each character would store its own format");
        System.out.println("With Flyweight: Multiple characters share the same format objects");
        System.out.println("Memory saved by sharing format objects!");
        
        // Demonstrate adding more characters with existing formats
        System.out.println("\n=== Adding More Characters ===");
        Document doc3 = new Document("Newsletter.doc");
        String text3 = "BREAKING NEWS";
        for (int i = 0; i < text3.length(); i++) {
            // Reusing existing Arial, 12pt, Red format
            doc3.addCharacter(text3.charAt(i), i, "Arial", 12, "Red");
        }
        
        doc3.render();
        CharacterFormatFactory.printCreatedFormats();
        System.out.println("Notice: No new formats created when reusing existing ones!");
    }
}