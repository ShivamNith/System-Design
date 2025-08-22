package org.example.designPatterns.proxyPattern;

/**
 * Virtual Proxy Example demonstrating lazy loading of expensive images
 * 
 * This example shows how a proxy can defer the creation and loading
 * of expensive image objects until they are actually needed.
 */

// Subject interface
interface Image {
    void display();
    void loadFromDisk();
    String getImageInfo();
    void applyFilter(String filterType);
}

// RealSubject - expensive to create and load
class RealImage implements Image {
    private String filename;
    private byte[] imageData;
    private int width, height;
    private boolean isLoaded = false;
    
    public RealImage(String filename) {
        this.filename = filename;
        loadFromDisk(); // Expensive operation
    }
    
    @Override
    public void loadFromDisk() {
        if (!isLoaded) {
            System.out.println("Loading image from disk: " + filename);
            // Simulate expensive loading operation
            try {
                Thread.sleep(2000); // Simulate 2-second load time
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            // Simulate loading image data
            this.width = 1920;
            this.height = 1080;
            this.imageData = new byte[width * height * 3]; // RGB data
            this.isLoaded = true;
            
            System.out.println("Image loaded: " + filename + " (" + width + "x" + height + ")");
        }
    }
    
    @Override
    public void display() {
        if (isLoaded) {
            System.out.println("Displaying image: " + filename + " (" + width + "x" + height + ")");
        } else {
            System.out.println("Image not loaded yet: " + filename);
        }
    }
    
    @Override
    public String getImageInfo() {
        if (isLoaded) {
            return String.format("Image: %s, Size: %dx%d, Data: %.1fMB", 
                               filename, width, height, (imageData.length / 1024.0 / 1024.0));
        } else {
            return "Image: " + filename + " (not loaded)";
        }
    }
    
    @Override
    public void applyFilter(String filterType) {
        if (isLoaded) {
            System.out.println("Applying " + filterType + " filter to " + filename);
            // Simulate filter processing
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            System.out.println(filterType + " filter applied successfully");
        } else {
            System.out.println("Cannot apply filter: image not loaded");
        }
    }
}

// Proxy - provides virtual proxy functionality
class ImageProxy implements Image {
    private RealImage realImage;
    private String filename;
    private boolean accessLogged = false;
    
    public ImageProxy(String filename) {
        this.filename = filename;
        System.out.println("ImageProxy created for: " + filename + " (image not loaded yet)");
    }
    
    @Override
    public void display() {
        logAccess("display");
        ensureImageLoaded();
        realImage.display();
    }
    
    @Override
    public void loadFromDisk() {
        logAccess("loadFromDisk");
        ensureImageLoaded();
    }
    
    @Override
    public String getImageInfo() {
        logAccess("getImageInfo");
        if (realImage == null) {
            return "Image: " + filename + " (not loaded - proxy)";
        } else {
            return realImage.getImageInfo();
        }
    }
    
    @Override
    public void applyFilter(String filterType) {
        logAccess("applyFilter");
        ensureImageLoaded();
        realImage.applyFilter(filterType);
    }
    
    private void ensureImageLoaded() {
        if (realImage == null) {
            System.out.println("Proxy: First access detected, loading real image...");
            realImage = new RealImage(filename);
        }
    }
    
    private void logAccess(String operation) {
        if (!accessLogged) {
            System.out.println("Proxy: First access to " + filename + " via " + operation + "()");
            accessLogged = true;
        }
    }
    
    // Additional proxy-specific functionality
    public boolean isImageLoaded() {
        return realImage != null;
    }
    
    public String getProxyStatus() {
        return String.format("Proxy for %s: %s", filename, 
                           isImageLoaded() ? "image loaded" : "image not loaded");
    }
}

// Client class demonstrating usage
class ImageGallery {
    private java.util.List<Image> images;
    
    public ImageGallery() {
        this.images = new java.util.ArrayList<>();
    }
    
    public void addImage(String filename) {
        // Using proxy instead of real image for lazy loading
        Image imageProxy = new ImageProxy(filename);
        images.add(imageProxy);
        System.out.println("Added image to gallery: " + filename);
    }
    
    public void displayThumbnails() {
        System.out.println("\n=== Displaying Thumbnails (Info Only) ===");
        for (int i = 0; i < images.size(); i++) {
            System.out.println((i + 1) + ". " + images.get(i).getImageInfo());
        }
    }
    
    public void displayImage(int index) {
        if (index >= 0 && index < images.size()) {
            System.out.println("\n=== Displaying Full Image ===");
            images.get(index).display();
        } else {
            System.out.println("Invalid image index: " + index);
        }
    }
    
    public void applyFilterToImage(int index, String filterType) {
        if (index >= 0 && index < images.size()) {
            System.out.println("\n=== Applying Filter ===");
            images.get(index).applyFilter(filterType);
        } else {
            System.out.println("Invalid image index: " + index);
        }
    }
    
    public void showGalleryStatus() {
        System.out.println("\n=== Gallery Status ===");
        for (int i = 0; i < images.size(); i++) {
            Image img = images.get(i);
            if (img instanceof ImageProxy) {
                System.out.println((i + 1) + ". " + ((ImageProxy) img).getProxyStatus());
            } else {
                System.out.println((i + 1) + ". " + img.getImageInfo());
            }
        }
    }
}

// Client code
public class VirtualImageProxyExample {
    public static void main(String[] args) {
        System.out.println("=== Virtual Proxy Pattern: Image Loading Example ===\n");
        
        // Create image gallery
        ImageGallery gallery = new ImageGallery();
        
        // Add images to gallery (using proxies - no loading yet)
        System.out.println("=== Adding Images to Gallery ===");
        gallery.addImage("vacation_photo.jpg");
        gallery.addImage("family_portrait.png");
        gallery.addImage("landscape_sunset.jpg");
        gallery.addImage("city_skyline.png");
        
        // Show gallery status (no images loaded yet)
        gallery.showGalleryStatus();
        
        // Display thumbnails (minimal info, no loading required)
        gallery.displayThumbnails();
        
        System.out.println("\n=== User Interactions ===");
        
        // User clicks on first image - this triggers loading
        System.out.println("\n--- User clicks on first image ---");
        gallery.displayImage(0);
        
        // Show status after first image load
        gallery.showGalleryStatus();
        
        // User applies filter to second image - this triggers loading
        System.out.println("\n--- User applies filter to second image ---");
        gallery.applyFilterToImage(1, "blur");
        
        // User displays third image
        System.out.println("\n--- User views third image ---");
        gallery.displayImage(2);
        
        // Show final status
        gallery.showGalleryStatus();
        
        // Demonstrate proxy benefits
        System.out.println("\n=== Proxy Pattern Benefits Demonstrated ===");
        System.out.println("1. Fast gallery creation - images not loaded until needed");
        System.out.println("2. Memory efficient - only loaded images consume memory");
        System.out.println("3. Lazy loading - expensive operations deferred");
        System.out.println("4. Transparent usage - client code doesn't know about proxy");
        System.out.println("5. Additional functionality - access logging, status tracking");
        
        // Show performance comparison
        System.out.println("\n=== Performance Comparison ===");
        long startTime = System.currentTimeMillis();
        
        // Create gallery without proxy (direct RealImage creation)
        System.out.println("Creating gallery with direct image loading...");
        for (int i = 0; i < 2; i++) {
            new RealImage("direct_load_" + i + ".jpg");
        }
        
        long directTime = System.currentTimeMillis() - startTime;
        
        startTime = System.currentTimeMillis();
        
        // Create gallery with proxy (deferred loading)
        System.out.println("Creating gallery with proxy (deferred loading)...");
        for (int i = 0; i < 2; i++) {
            new ImageProxy("proxy_load_" + i + ".jpg");
        }
        
        long proxyTime = System.currentTimeMillis() - startTime;
        
        System.out.println("Direct loading time: " + directTime + "ms");
        System.out.println("Proxy creation time: " + proxyTime + "ms");
        System.out.println("Performance improvement: " + (directTime - proxyTime) + "ms saved");
    }
}