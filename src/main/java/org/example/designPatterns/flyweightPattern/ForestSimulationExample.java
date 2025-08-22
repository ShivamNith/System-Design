package org.example.designPatterns.flyweightPattern;

import java.util.*;

/**
 * Forest Simulation Example demonstrating Flyweight Pattern
 * 
 * This example shows how different tree types can be shared
 * among many tree instances in a forest simulation.
 */

// Flyweight interface
interface TreeTypeFlyweight {
    void render(double x, double y, String season, String context);
    void performPhotosynthesis(double sunlight, double water);
    String getSpeciesInfo();
}

// Concrete Flyweight - stores intrinsic state (species characteristics)
class TreeType implements TreeTypeFlyweight {
    private final String species;
    private final String leafColor;
    private final String barkTexture;
    private final double maxHeight;
    private final String fruitType;
    
    public TreeType(String species, String leafColor, String barkTexture, 
                   double maxHeight, String fruitType) {
        this.species = species;
        this.leafColor = leafColor;
        this.barkTexture = barkTexture;
        this.maxHeight = maxHeight;
        this.fruitType = fruitType;
    }
    
    @Override
    public void render(double x, double y, String season, String context) {
        String seasonalColor = getSeasonalColor(season);
        System.out.println(String.format(
            "Rendering %s tree at (%.1f, %.1f) with %s leaves and %s bark in %s [%s]",
            species, x, y, seasonalColor, barkTexture, context, season
        ));
    }
    
    @Override
    public void performPhotosynthesis(double sunlight, double water) {
        double efficiency = calculatePhotosynthesisRate(sunlight, water);
        System.out.println(String.format(
            "%s performing photosynthesis at %.1f%% efficiency",
            species, efficiency * 100
        ));
    }
    
    @Override
    public String getSpeciesInfo() {
        return String.format("%s (max height: %.1fm, fruit: %s)", 
                           species, maxHeight, fruitType);
    }
    
    private String getSeasonalColor(String season) {
        switch (season.toLowerCase()) {
            case "spring": return "light " + leafColor;
            case "summer": return leafColor;
            case "autumn": return "golden";
            case "winter": return "bare branches";
            default: return leafColor;
        }
    }
    
    private double calculatePhotosynthesisRate(double sunlight, double water) {
        return Math.min(1.0, (sunlight * 0.7 + water * 0.3));
    }
    
    public double getMaxHeight() {
        return maxHeight;
    }
}

// Flyweight Factory
class TreeTypeFactory {
    private static final Map<String, TreeType> treeTypes = new HashMap<>();
    
    public static TreeType getTreeType(String species, String leafColor, String barkTexture, 
                                     double maxHeight, String fruitType) {
        String key = species + "_" + leafColor + "_" + barkTexture;
        
        if (!treeTypes.containsKey(key)) {
            treeTypes.put(key, new TreeType(species, leafColor, barkTexture, maxHeight, fruitType));
            System.out.println("Created new tree type: " + species);
        }
        
        return treeTypes.get(key);
    }
    
    // Predefined tree types for convenience
    public static TreeType getOakTree() {
        return getTreeType("Oak", "green", "rough", 25.0, "acorns");
    }
    
    public static TreeType getPineTree() {
        return getTreeType("Pine", "dark green", "scaly", 30.0, "pinecones");
    }
    
    public static TreeType getBirchTree() {
        return getTreeType("Birch", "light green", "smooth white", 20.0, "none");
    }
    
    public static TreeType getAppleTree() {
        return getTreeType("Apple", "green", "smooth", 8.0, "apples");
    }
    
    public static void printForestStatistics() {
        System.out.println("\n=== Forest Statistics ===");
        System.out.println("Tree types in forest: " + treeTypes.size());
        for (Map.Entry<String, TreeType> entry : treeTypes.entrySet()) {
            System.out.println("- " + entry.getValue().getSpeciesInfo());
        }
    }
}

// Context class - stores extrinsic state
class Tree {
    private double x, y;
    private double currentHeight;
    private int age;
    private double healthLevel;
    private TreeType treeType;
    
    public Tree(double x, double y, TreeType treeType) {
        this.x = x;
        this.y = y;
        this.treeType = treeType;
        this.currentHeight = 1.0; // Start as sapling
        this.age = 0;
        this.healthLevel = 1.0;
    }
    
    public void render(String season, String context) {
        treeType.render(x, y, season, context);
    }
    
    public void grow(double growthRate) {
        if (currentHeight < treeType.getMaxHeight() && healthLevel > 0.5) {
            currentHeight += growthRate;
            age++;
            // Simulate aging effects
            healthLevel = Math.max(0.3, healthLevel - 0.01);
        }
    }
    
    public void performPhotosynthesis(double sunlight, double water) {
        treeType.performPhotosynthesis(sunlight * healthLevel, water);
    }
    
    public String getStatus() {
        return String.format("Tree at (%.1f,%.1f): height=%.1fm, age=%d years, health=%.1f%%",
                           x, y, currentHeight, age, healthLevel * 100);
    }
    
    public double getX() { return x; }
    public double getY() { return y; }
    public double getCurrentHeight() { return currentHeight; }
    public TreeType getTreeType() { return treeType; }
}

// Forest class manages the trees
class Forest {
    private List<Tree> trees;
    private String forestName;
    private String currentSeason;
    
    public Forest(String forestName) {
        this.forestName = forestName;
        this.trees = new ArrayList<>();
        this.currentSeason = "spring";
    }
    
    public void plantTree(double x, double y, TreeType treeType) {
        trees.add(new Tree(x, y, treeType));
    }
    
    public void plantRandomForest(int treeCount) {
        Random random = new Random();
        
        for (int i = 0; i < treeCount; i++) {
            double x = random.nextDouble() * 1000;
            double y = random.nextDouble() * 1000;
            
            // Randomly select tree type
            TreeType type;
            int typeChoice = random.nextInt(4);
            switch (typeChoice) {
                case 0: type = TreeTypeFactory.getOakTree(); break;
                case 1: type = TreeTypeFactory.getPineTree(); break;
                case 2: type = TreeTypeFactory.getBirchTree(); break;
                default: type = TreeTypeFactory.getAppleTree(); break;
            }
            
            plantTree(x, y, type);
        }
    }
    
    public void renderForest() {
        System.out.println("\n=== Rendering " + forestName + " in " + currentSeason + " ===");
        for (int i = 0; i < Math.min(5, trees.size()); i++) {
            trees.get(i).render(currentSeason, forestName);
        }
        if (trees.size() > 5) {
            System.out.println("... and " + (trees.size() - 5) + " more trees");
        }
    }
    
    public void simulateGrowth(int seasons) {
        System.out.println("\n=== Simulating " + seasons + " seasons of growth ===");
        
        String[] seasonNames = {"spring", "summer", "autumn", "winter"};
        
        for (int s = 0; s < seasons; s++) {
            currentSeason = seasonNames[s % 4];
            System.out.println("\n--- " + currentSeason.substring(0, 1).toUpperCase() + 
                             currentSeason.substring(1) + " Season ---");
            
            // Simulate environmental conditions
            double sunlight = currentSeason.equals("summer") ? 1.0 : 0.7;
            double water = currentSeason.equals("spring") ? 1.0 : 0.6;
            double growthRate = currentSeason.equals("winter") ? 0.1 : 0.5;
            
            // All trees grow and photosynthesize
            for (Tree tree : trees) {
                tree.grow(growthRate);
                tree.performPhotosynthesis(sunlight, water);
            }
            
            System.out.println("Environmental conditions: sunlight=" + sunlight + ", water=" + water);
        }
    }
    
    public void printTreeStatus() {
        System.out.println("\n=== Tree Status in " + forestName + " ===");
        for (int i = 0; i < Math.min(3, trees.size()); i++) {
            System.out.println(trees.get(i).getStatus());
        }
        if (trees.size() > 3) {
            System.out.println("... and " + (trees.size() - 3) + " more trees");
        }
    }
    
    public int getTreeCount() {
        return trees.size();
    }
}

// Client code
public class ForestSimulationExample {
    public static void main(String[] args) {
        System.out.println("=== Flyweight Pattern: Forest Simulation ===\n");
        
        // Create forests
        Forest primaryForest = new Forest("Primary Forest");
        Forest orchardForest = new Forest("Orchard");
        
        // Plant specific trees in orchard
        System.out.println("=== Planting Orchard ===");
        for (int i = 0; i < 20; i++) {
            orchardForest.plantTree(i * 10, i * 5, TreeTypeFactory.getAppleTree());
        }
        
        // Plant diverse forest
        System.out.println("\n=== Planting Primary Forest ===");
        primaryForest.plantRandomForest(100);
        
        // Show initial state
        primaryForest.renderForest();
        orchardForest.renderForest();
        
        // Show memory efficiency
        System.out.println("\n=== Memory Efficiency Demonstration ===");
        System.out.println("Primary forest trees: " + primaryForest.getTreeCount());
        System.out.println("Orchard trees: " + orchardForest.getTreeCount());
        System.out.println("Total trees: " + (primaryForest.getTreeCount() + orchardForest.getTreeCount()));
        
        TreeTypeFactory.printForestStatistics();
        
        // Simulate growth
        primaryForest.simulateGrowth(4);
        
        // Show tree status after growth
        primaryForest.printTreeStatus();
        orchardForest.printTreeStatus();
        
        // Plant more trees to show flyweight reuse
        System.out.println("\n=== Expanding Forest ===");
        Forest newForest = new Forest("New Forest");
        newForest.plantRandomForest(50);
        
        TreeTypeFactory.printForestStatistics();
        System.out.println("\nNotice: No new tree types created - existing types were reused!");
        
        System.out.println("\n=== Flyweight Benefits ===");
        int totalTrees = primaryForest.getTreeCount() + orchardForest.getTreeCount() + newForest.getTreeCount();
        System.out.println("Total trees in all forests: " + totalTrees);
        System.out.println("Tree types needed: " + TreeTypeFactory.treeTypes.size());
        System.out.println("Memory savings: " + (totalTrees - TreeTypeFactory.treeTypes.size()) + 
                         " objects saved by sharing tree types!");
        
        System.out.println("\nWithout Flyweight: Each tree stores species, color, texture, etc.");
        System.out.println("With Flyweight: Trees share type objects, only store position and growth data.");
    }
}