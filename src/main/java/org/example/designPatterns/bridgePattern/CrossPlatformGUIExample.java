package org.example.designPatterns.bridgePattern;

import java.util.*;

/**
 * Cross-Platform GUI Example using Bridge Pattern
 * 
 * This example demonstrates how the Bridge Pattern can be used to create
 * GUI components that work across different operating systems while maintaining
 * platform-specific rendering and behavior.
 * 
 * The abstraction represents GUI components, while implementations handle
 * platform-specific rendering (Windows, macOS, Linux).
 */

// Implementation interface
interface UIRenderer {
    void renderButton(String text, int x, int y, int width, int height);
    void renderTextBox(String text, int x, int y, int width, int height);
    void renderLabel(String text, int x, int y);
    void renderWindow(String title, int width, int height);
}

// Concrete Implementations
class WindowsRenderer implements UIRenderer {
    @Override
    public void renderButton(String text, int x, int y, int width, int height) {
        System.out.println("Rendering Windows-style button:");
        System.out.println("  Text: " + text);
        System.out.println("  Position: (" + x + ", " + y + ")");
        System.out.println("  Size: " + width + "x" + height);
        System.out.println("  Style: Windows 11 flat design with rounded corners");
    }
    
    @Override
    public void renderTextBox(String text, int x, int y, int width, int height) {
        System.out.println("Rendering Windows TextBox:");
        System.out.println("  Content: " + text);
        System.out.println("  Position: (" + x + ", " + y + ")");
        System.out.println("  Style: Windows native textbox with system font");
    }
    
    @Override
    public void renderLabel(String text, int x, int y) {
        System.out.println("Rendering Windows Label: '" + text + "' at (" + x + ", " + y + ")");
        System.out.println("  Font: Segoe UI");
    }
    
    @Override
    public void renderWindow(String title, int width, int height) {
        System.out.println("Creating Windows window: " + title);
        System.out.println("  Size: " + width + "x" + height);
        System.out.println("  Features: Title bar, minimize/maximize/close buttons");
    }
}

class MacOSRenderer implements UIRenderer {
    @Override
    public void renderButton(String text, int x, int y, int width, int height) {
        System.out.println("Rendering macOS-style button:");
        System.out.println("  Text: " + text);
        System.out.println("  Position: (" + x + ", " + y + ")");
        System.out.println("  Size: " + width + "x" + height);
        System.out.println("  Style: macOS Big Sur design with subtle shadows");
    }
    
    @Override
    public void renderTextBox(String text, int x, int y, int width, int height) {
        System.out.println("Rendering macOS TextBox:");
        System.out.println("  Content: " + text);
        System.out.println("  Position: (" + x + ", " + y + ")");
        System.out.println("  Style: macOS native textfield with system font");
    }
    
    @Override
    public void renderLabel(String text, int x, int y) {
        System.out.println("Rendering macOS Label: '" + text + "' at (" + x + ", " + y + ")");
        System.out.println("  Font: San Francisco");
    }
    
    @Override
    public void renderWindow(String title, int width, int height) {
        System.out.println("Creating macOS window: " + title);
        System.out.println("  Size: " + width + "x" + height);
        System.out.println("  Features: Traffic light buttons (red/yellow/green)");
    }
}

class LinuxRenderer implements UIRenderer {
    @Override
    public void renderButton(String text, int x, int y, int width, int height) {
        System.out.println("Rendering Linux GTK button:");
        System.out.println("  Text: " + text);
        System.out.println("  Position: (" + x + ", " + y + ")");
        System.out.println("  Size: " + width + "x" + height);
        System.out.println("  Style: GTK3 Adwaita theme");
    }
    
    @Override
    public void renderTextBox(String text, int x, int y, int width, int height) {
        System.out.println("Rendering Linux GTK TextBox:");
        System.out.println("  Content: " + text);
        System.out.println("  Position: (" + x + ", " + y + ")");
        System.out.println("  Style: GTK Entry widget with system theme");
    }
    
    @Override
    public void renderLabel(String text, int x, int y) {
        System.out.println("Rendering Linux GTK Label: '" + text + "' at (" + x + ", " + y + ")");
        System.out.println("  Font: Liberation Sans");
    }
    
    @Override
    public void renderWindow(String title, int width, int height) {
        System.out.println("Creating Linux GTK window: " + title);
        System.out.println("  Size: " + width + "x" + height);
        System.out.println("  Features: GTK window decorations");
    }
}

// Abstraction
abstract class UIComponent {
    protected UIRenderer renderer;
    protected int x, y, width, height;
    
    public UIComponent(UIRenderer renderer) {
        this.renderer = renderer;
    }
    
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }
    
    public abstract void render();
    
    public void setRenderer(UIRenderer renderer) {
        this.renderer = renderer;
        System.out.println("Switched to " + renderer.getClass().getSimpleName());
    }
}

// Refined Abstractions
class Button extends UIComponent {
    private String text;
    private boolean isPressed;
    
    public Button(UIRenderer renderer, String text) {
        super(renderer);
        this.text = text;
        this.isPressed = false;
    }
    
    @Override
    public void render() {
        System.out.println("\n=== Rendering Button ===");
        renderer.renderButton(text, x, y, width, height);
        if (isPressed) {
            System.out.println("  State: Pressed");
        }
    }
    
    public void click() {
        isPressed = true;
        System.out.println("Button '" + text + "' clicked!");
        render();
        isPressed = false;
    }
    
    public void setText(String text) {
        this.text = text;
    }
    
    public String getText() {
        return text;
    }
}

class TextBox extends UIComponent {
    private String content;
    private boolean isFocused;
    
    public TextBox(UIRenderer renderer, String initialText) {
        super(renderer);
        this.content = initialText;
        this.isFocused = false;
    }
    
    @Override
    public void render() {
        System.out.println("\n=== Rendering TextBox ===");
        renderer.renderTextBox(content, x, y, width, height);
        if (isFocused) {
            System.out.println("  State: Focused (cursor visible)");
        }
    }
    
    public void focus() {
        isFocused = true;
        System.out.println("TextBox focused");
        render();
    }
    
    public void blur() {
        isFocused = false;
        System.out.println("TextBox lost focus");
    }
    
    public void setText(String content) {
        this.content = content;
        render();
    }
    
    public String getText() {
        return content;
    }
}

class Label extends UIComponent {
    private String text;
    private String fontFamily;
    
    public Label(UIRenderer renderer, String text) {
        super(renderer);
        this.text = text;
    }
    
    @Override
    public void render() {
        System.out.println("\n=== Rendering Label ===");
        renderer.renderLabel(text, x, y);
        if (fontFamily != null) {
            System.out.println("  Custom font: " + fontFamily);
        }
    }
    
    public void setText(String text) {
        this.text = text;
    }
    
    public void setFontFamily(String fontFamily) {
        this.fontFamily = fontFamily;
    }
    
    public String getText() {
        return text;
    }
}

// Complex Abstraction
class Window {
    private UIRenderer renderer;
    private String title;
    private int width, height;
    private List<UIComponent> components;
    private boolean isVisible;
    
    public Window(UIRenderer renderer, String title, int width, int height) {
        this.renderer = renderer;
        this.title = title;
        this.width = width;
        this.height = height;
        this.components = new ArrayList<>();
        this.isVisible = false;
    }
    
    public void show() {
        isVisible = true;
        System.out.println("\n=== Showing Window ===");
        renderer.renderWindow(title, width, height);
        
        System.out.println("\nRendering child components:");
        for (UIComponent component : components) {
            component.render();
        }
    }
    
    public void hide() {
        isVisible = false;
        System.out.println("Window '" + title + "' hidden");
    }
    
    public void addComponent(UIComponent component) {
        components.add(component);
        System.out.println("Added component to window: " + component.getClass().getSimpleName());
    }
    
    public void setRenderer(UIRenderer renderer) {
        this.renderer = renderer;
        // Update all child components
        for (UIComponent component : components) {
            component.setRenderer(renderer);
        }
        System.out.println("Window and all components switched to " + renderer.getClass().getSimpleName());
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public boolean isVisible() {
        return isVisible;
    }
}

// Demo Application
public class CrossPlatformGUIExample {
    public static void main(String[] args) {
        System.out.println("=== Cross-Platform GUI Bridge Pattern Demo ===\n");
        
        // Create different renderers
        UIRenderer windowsRenderer = new WindowsRenderer();
        UIRenderer macRenderer = new MacOSRenderer();
        UIRenderer linuxRenderer = new LinuxRenderer();
        
        // Create a login window
        Window loginWindow = new Window(windowsRenderer, "Login", 400, 300);
        
        // Create components
        Label titleLabel = new Label(windowsRenderer, "Welcome to Our Application");
        titleLabel.setPosition(50, 20);
        
        Label usernameLabel = new Label(windowsRenderer, "Username:");
        usernameLabel.setPosition(50, 80);
        
        TextBox usernameTextBox = new TextBox(windowsRenderer, "");
        usernameTextBox.setPosition(150, 75);
        usernameTextBox.setSize(200, 25);
        
        Label passwordLabel = new Label(windowsRenderer, "Password:");
        passwordLabel.setPosition(50, 120);
        
        TextBox passwordTextBox = new TextBox(windowsRenderer, "");
        passwordTextBox.setPosition(150, 115);
        passwordTextBox.setSize(200, 25);
        
        Button loginButton = new Button(windowsRenderer, "Login");
        loginButton.setPosition(150, 170);
        loginButton.setSize(100, 30);
        
        Button cancelButton = new Button(windowsRenderer, "Cancel");
        cancelButton.setPosition(260, 170);
        cancelButton.setSize(100, 30);
        
        // Add components to window
        loginWindow.addComponent(titleLabel);
        loginWindow.addComponent(usernameLabel);
        loginWindow.addComponent(usernameTextBox);
        loginWindow.addComponent(passwordLabel);
        loginWindow.addComponent(passwordTextBox);
        loginWindow.addComponent(loginButton);
        loginWindow.addComponent(cancelButton);
        
        // Show login window on Windows
        System.out.println("1. Showing login window on Windows:");
        loginWindow.show();
        
        // Simulate user interaction
        usernameTextBox.focus();
        usernameTextBox.setText("john.doe");
        passwordTextBox.focus();
        passwordTextBox.setText("********");
        loginButton.click();
        
        // Switch to macOS rendering
        System.out.println("\n" + "=".repeat(60));
        System.out.println("2. Switching to macOS rendering:");
        loginWindow.setRenderer(macRenderer);
        loginWindow.show();
        
        // Switch to Linux rendering
        System.out.println("\n" + "=".repeat(60));
        System.out.println("3. Switching to Linux rendering:");
        loginWindow.setRenderer(linuxRenderer);
        loginWindow.show();
        
        // Demonstrate runtime platform detection
        System.out.println("\n" + "=".repeat(60));
        System.out.println("4. Automatic platform detection:");
        demonstrateAutomaticPlatformDetection(loginWindow);
        
        // Demonstrate theme switching
        System.out.println("\n" + "=".repeat(60));
        System.out.println("5. Theme switching capability:");
        demonstrateThemeSwitching();
    }
    
    private static void demonstrateAutomaticPlatformDetection(Window window) {
        String osName = System.getProperty("os.name").toLowerCase();
        UIRenderer renderer;
        
        if (osName.contains("windows")) {
            renderer = new WindowsRenderer();
            System.out.println("Detected Windows OS - using Windows renderer");
        } else if (osName.contains("mac")) {
            renderer = new MacOSRenderer();
            System.out.println("Detected macOS - using macOS renderer");
        } else {
            renderer = new LinuxRenderer();
            System.out.println("Detected Linux/Unix OS - using Linux renderer");
        }
        
        window.setRenderer(renderer);
        window.show();
    }
    
    private static void demonstrateThemeSwitching() {
        // Create a settings window to show theme switching
        UIRenderer currentRenderer = new WindowsRenderer();
        Window settingsWindow = new Window(currentRenderer, "Settings", 500, 350);
        
        Label themeLabel = new Label(currentRenderer, "Select Theme:");
        themeLabel.setPosition(50, 50);
        
        Button windowsTheme = new Button(currentRenderer, "Windows Theme");
        windowsTheme.setPosition(50, 100);
        windowsTheme.setSize(130, 30);
        
        Button macTheme = new Button(currentRenderer, "macOS Theme");
        macTheme.setPosition(190, 100);
        macTheme.setSize(130, 30);
        
        Button linuxTheme = new Button(currentRenderer, "Linux Theme");
        linuxTheme.setPosition(330, 100);
        linuxTheme.setSize(130, 30);
        
        settingsWindow.addComponent(themeLabel);
        settingsWindow.addComponent(windowsTheme);
        settingsWindow.addComponent(macTheme);
        settingsWindow.addComponent(linuxTheme);
        
        System.out.println("Settings window with theme options:");
        settingsWindow.show();
        
        // Simulate theme changes
        System.out.println("\nSwitching to macOS theme:");
        macTheme.click();
        settingsWindow.setRenderer(new MacOSRenderer());
        settingsWindow.show();
        
        System.out.println("\nSwitching to Linux theme:");
        linuxTheme.click();
        settingsWindow.setRenderer(new LinuxRenderer());
        settingsWindow.show();
    }
}