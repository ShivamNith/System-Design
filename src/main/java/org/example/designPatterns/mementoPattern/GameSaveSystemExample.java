package org.example.designPatterns.mementoPattern;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Memento Pattern Example: Game Save System with Checkpoints
 * 
 * This example demonstrates the Memento Pattern in a game context where
 * players can save their progress at checkpoints and restore their state
 * if they die or want to replay a section.
 */

// Memento interface for type safety
interface GameMemento {
    // Marker interface - only GameState should access the actual state
}

// Player statistics and information
class PlayerStats {
    private int health;
    private int mana;
    private int experience;
    private int gold;
    private String playerClass;
    
    public PlayerStats(int health, int mana, int experience, int gold, String playerClass) {
        this.health = health;
        this.mana = mana;
        this.experience = experience;
        this.gold = gold;
        this.playerClass = playerClass;
    }
    
    // Copy constructor for memento
    public PlayerStats(PlayerStats other) {
        this.health = other.health;
        this.mana = other.mana;
        this.experience = other.experience;
        this.gold = other.gold;
        this.playerClass = other.playerClass;
    }
    
    // Getters and setters
    public int getHealth() { return health; }
    public void setHealth(int health) { this.health = Math.max(0, health); }
    
    public int getMana() { return mana; }
    public void setMana(int mana) { this.mana = Math.max(0, mana); }
    
    public int getExperience() { return experience; }
    public void setExperience(int experience) { this.experience = Math.max(0, experience); }
    
    public int getGold() { return gold; }
    public void setGold(int gold) { this.gold = Math.max(0, gold); }
    
    public String getPlayerClass() { return playerClass; }
    public void setPlayerClass(String playerClass) { this.playerClass = playerClass; }
    
    @Override
    public String toString() {
        return String.format("HP:%d MP:%d XP:%d Gold:%d Class:%s", 
                           health, mana, experience, gold, playerClass);
    }
}

// Game location information
class GameLocation {
    private String area;
    private int x, y, z;
    private String description;
    
    public GameLocation(String area, int x, int y, int z, String description) {
        this.area = area;
        this.x = x;
        this.y = y;
        this.z = z;
        this.description = description;
    }
    
    // Copy constructor
    public GameLocation(GameLocation other) {
        this.area = other.area;
        this.x = other.x;
        this.y = other.y;
        this.z = other.z;
        this.description = other.description;
    }
    
    // Getters and setters
    public String getArea() { return area; }
    public void setArea(String area) { this.area = area; }
    
    public int getX() { return x; }
    public void setX(int x) { this.x = x; }
    
    public int getY() { return y; }
    public void setY(int y) { this.y = y; }
    
    public int getZ() { return z; }
    public void setZ(int z) { this.z = z; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    @Override
    public String toString() {
        return String.format("%s (%d,%d,%d) - %s", area, x, y, z, description);
    }
}

// Originator class - Game State
class GameState {
    private PlayerStats playerStats;
    private GameLocation currentLocation;
    private List<String> inventory;
    private Set<String> completedQuests;
    private Map<String, Boolean> gameFlags;
    private int currentLevel;
    private long playTimeSeconds;
    private LocalDateTime lastSaved;
    private String difficulty;
    
    public GameState(String playerName, String playerClass, String difficulty) {
        this.playerStats = new PlayerStats(100, 50, 0, 100, playerClass);
        this.currentLocation = new GameLocation("Starting Village", 0, 0, 0, "A peaceful village");
        this.inventory = new ArrayList<>();
        this.completedQuests = new HashSet<>();
        this.gameFlags = new HashMap<>();
        this.currentLevel = 1;
        this.playTimeSeconds = 0;
        this.lastSaved = LocalDateTime.now();
        this.difficulty = difficulty;
        
        // Add some starting items
        inventory.add("Basic Sword");
        inventory.add("Health Potion");
        
        System.out.println("New game started for " + playerName + " (" + playerClass + ") on " + difficulty + " difficulty");
    }
    
    // Game actions that modify state
    public void moveToLocation(String area, int x, int y, int z, String description) {
        this.currentLocation = new GameLocation(area, x, y, z, description);
        addPlayTime(5); // Movement takes time
        System.out.println("Moved to: " + currentLocation);
    }
    
    public void gainExperience(int exp) {
        playerStats.setExperience(playerStats.getExperience() + exp);
        checkLevelUp();
        System.out.println("Gained " + exp + " experience (Total: " + playerStats.getExperience() + ")");
    }
    
    public void takeDamage(int damage) {
        int newHealth = playerStats.getHealth() - damage;
        playerStats.setHealth(newHealth);
        System.out.println("Took " + damage + " damage (Health: " + playerStats.getHealth() + ")");
        
        if (playerStats.getHealth() <= 0) {
            System.out.println("Player has died!");
        }
    }
    
    public void heal(int amount) {
        int newHealth = Math.min(100, playerStats.getHealth() + amount);
        playerStats.setHealth(newHealth);
        System.out.println("Healed " + amount + " HP (Health: " + playerStats.getHealth() + ")");
    }
    
    public void useMana(int amount) {
        int newMana = playerStats.getMana() - amount;
        playerStats.setMana(newMana);
        System.out.println("Used " + amount + " mana (Mana: " + playerStats.getMana() + ")");
    }
    
    public void restoreMana(int amount) {
        int maxMana = 50 + (currentLevel * 10);
        int newMana = Math.min(maxMana, playerStats.getMana() + amount);
        playerStats.setMana(newMana);
        System.out.println("Restored " + amount + " mana (Mana: " + playerStats.getMana() + ")");
    }
    
    public void addGold(int amount) {
        playerStats.setGold(playerStats.getGold() + amount);
        System.out.println("Gained " + amount + " gold (Total: " + playerStats.getGold() + ")");
    }
    
    public void spendGold(int amount) {
        if (playerStats.getGold() >= amount) {
            playerStats.setGold(playerStats.getGold() - amount);
            System.out.println("Spent " + amount + " gold (Remaining: " + playerStats.getGold() + ")");
        } else {
            System.out.println("Not enough gold! (Have: " + playerStats.getGold() + ", Need: " + amount + ")");
        }
    }
    
    public void addToInventory(String item) {
        inventory.add(item);
        System.out.println("Added to inventory: " + item);
    }
    
    public boolean removeFromInventory(String item) {
        boolean removed = inventory.remove(item);
        if (removed) {
            System.out.println("Removed from inventory: " + item);
        } else {
            System.out.println("Item not found in inventory: " + item);
        }
        return removed;
    }
    
    public void completeQuest(String questName) {
        completedQuests.add(questName);
        System.out.println("Quest completed: " + questName);
    }
    
    public void setGameFlag(String flagName, boolean value) {
        gameFlags.put(flagName, value);
        System.out.println("Game flag set: " + flagName + " = " + value);
    }
    
    public void addPlayTime(long seconds) {
        this.playTimeSeconds += seconds;
    }
    
    private void checkLevelUp() {
        int requiredExp = currentLevel * 100;
        if (playerStats.getExperience() >= requiredExp) {
            currentLevel++;
            System.out.println("LEVEL UP! Now level " + currentLevel);
            
            // Increase stats on level up
            playerStats.setHealth(Math.min(100 + (currentLevel * 10), playerStats.getHealth() + 20));
            playerStats.setMana(Math.min(50 + (currentLevel * 10), playerStats.getMana() + 10));
        }
    }
    
    // Memento creation
    public GameMemento saveGame() {
        return new GameStateMemento(
            new PlayerStats(playerStats),
            new GameLocation(currentLocation),
            new ArrayList<>(inventory),
            new HashSet<>(completedQuests),
            new HashMap<>(gameFlags),
            currentLevel,
            playTimeSeconds,
            LocalDateTime.now(),
            difficulty
        );
    }
    
    // Memento restoration
    public void loadGame(GameMemento memento) {
        if (memento instanceof GameStateMemento) {
            GameStateMemento gameMemento = (GameStateMemento) memento;
            this.playerStats = new PlayerStats(gameMemento.playerStats);
            this.currentLocation = new GameLocation(gameMemento.currentLocation);
            this.inventory = new ArrayList<>(gameMemento.inventory);
            this.completedQuests = new HashSet<>(gameMemento.completedQuests);
            this.gameFlags = new HashMap<>(gameMemento.gameFlags);
            this.currentLevel = gameMemento.currentLevel;
            this.playTimeSeconds = gameMemento.playTimeSeconds;
            this.lastSaved = gameMemento.lastSaved;
            this.difficulty = gameMemento.difficulty;
            
            System.out.println("Game loaded from save created at " + 
                lastSaved.format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        }
    }
    
    // Game state queries
    public boolean isAlive() {
        return playerStats.getHealth() > 0;
    }
    
    public boolean hasItem(String item) {
        return inventory.contains(item);
    }
    
    public boolean isQuestCompleted(String questName) {
        return completedQuests.contains(questName);
    }
    
    public boolean getGameFlag(String flagName) {
        return gameFlags.getOrDefault(flagName, false);
    }
    
    // Display methods
    public void displayGameState() {
        System.out.println("\n=== Game State ===");
        System.out.println("Level: " + currentLevel);
        System.out.println("Stats: " + playerStats);
        System.out.println("Location: " + currentLocation);
        System.out.println("Inventory (" + inventory.size() + " items): " + inventory);
        System.out.println("Completed Quests (" + completedQuests.size() + "): " + completedQuests);
        System.out.println("Play Time: " + formatPlayTime());
        System.out.println("Difficulty: " + difficulty);
        System.out.println("Last Saved: " + lastSaved.format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        if (!gameFlags.isEmpty()) {
            System.out.println("Game Flags: " + gameFlags);
        }
        System.out.println("==================\n");
    }
    
    private String formatPlayTime() {
        long hours = playTimeSeconds / 3600;
        long minutes = (playTimeSeconds % 3600) / 60;
        long seconds = playTimeSeconds % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
    
    // Getters
    public PlayerStats getPlayerStats() { return playerStats; }
    public GameLocation getCurrentLocation() { return currentLocation; }
    public List<String> getInventory() { return new ArrayList<>(inventory); }
    public Set<String> getCompletedQuests() { return new HashSet<>(completedQuests); }
    public int getCurrentLevel() { return currentLevel; }
    public long getPlayTimeSeconds() { return playTimeSeconds; }
    public String getDifficulty() { return difficulty; }
    
    // Inner memento class for encapsulation
    private static class GameStateMemento implements GameMemento {
        private final PlayerStats playerStats;
        private final GameLocation currentLocation;
        private final List<String> inventory;
        private final Set<String> completedQuests;
        private final Map<String, Boolean> gameFlags;
        private final int currentLevel;
        private final long playTimeSeconds;
        private final LocalDateTime lastSaved;
        private final String difficulty;
        
        private GameStateMemento(PlayerStats playerStats, GameLocation currentLocation,
                               List<String> inventory, Set<String> completedQuests,
                               Map<String, Boolean> gameFlags, int currentLevel,
                               long playTimeSeconds, LocalDateTime lastSaved, String difficulty) {
            this.playerStats = playerStats;
            this.currentLocation = currentLocation;
            this.inventory = inventory;
            this.completedQuests = completedQuests;
            this.gameFlags = gameFlags;
            this.currentLevel = currentLevel;
            this.playTimeSeconds = playTimeSeconds;
            this.lastSaved = lastSaved;
            this.difficulty = difficulty;
        }
        
        @Override
        public String toString() {
            return String.format("Save[%s, Lvl %d, %s, Items: %d]",
                               lastSaved.format(DateTimeFormatter.ofPattern("HH:mm:ss")),
                               currentLevel, currentLocation.getArea(), inventory.size());
        }
    }
}

// Caretaker class - Save Game Manager
class SaveGameManager {
    private final GameState gameState;
    private final Map<String, GameMemento> namedSaves;
    private final List<GameMemento> quickSaves;
    private final List<GameMemento> checkpoints;
    private final int maxQuickSaves;
    private final int maxCheckpoints;
    
    public SaveGameManager(GameState gameState, int maxQuickSaves, int maxCheckpoints) {
        this.gameState = gameState;
        this.namedSaves = new HashMap<>();
        this.quickSaves = new ArrayList<>();
        this.checkpoints = new ArrayList<>();
        this.maxQuickSaves = maxQuickSaves;
        this.maxCheckpoints = maxCheckpoints;
    }
    
    public SaveGameManager(GameState gameState) {
        this(gameState, 5, 10); // Default limits
    }
    
    // Named saves (manual saves)
    public void saveGame(String saveName) {
        GameMemento save = gameState.saveGame();
        namedSaves.put(saveName, save);
        System.out.println("Game saved as: " + saveName);
    }
    
    public boolean loadGame(String saveName) {
        GameMemento save = namedSaves.get(saveName);
        if (save != null) {
            gameState.loadGame(save);
            System.out.println("Loaded game: " + saveName);
            return true;
        } else {
            System.out.println("Save file not found: " + saveName);
            return false;
        }
    }
    
    public boolean deleteSave(String saveName) {
        GameMemento removed = namedSaves.remove(saveName);
        if (removed != null) {
            System.out.println("Deleted save: " + saveName);
            return true;
        } else {
            System.out.println("Save file not found: " + saveName);
            return false;
        }
    }
    
    // Quick saves (rotating saves for quick access)
    public void quickSave() {
        GameMemento save = gameState.saveGame();
        quickSaves.add(save);
        
        // Maintain maximum quick saves
        if (quickSaves.size() > maxQuickSaves) {
            quickSaves.remove(0);
        }
        
        System.out.println("Quick save created (slot " + quickSaves.size() + "/" + maxQuickSaves + ")");
    }
    
    public boolean quickLoad(int slot) {
        if (slot > 0 && slot <= quickSaves.size()) {
            GameMemento save = quickSaves.get(slot - 1);
            gameState.loadGame(save);
            System.out.println("Quick loaded from slot " + slot);
            return true;
        } else {
            System.out.println("Invalid quick save slot: " + slot);
            return false;
        }
    }
    
    public boolean quickLoadLatest() {
        if (!quickSaves.isEmpty()) {
            GameMemento save = quickSaves.get(quickSaves.size() - 1);
            gameState.loadGame(save);
            System.out.println("Quick loaded latest save");
            return true;
        } else {
            System.out.println("No quick saves available");
            return false;
        }
    }
    
    // Checkpoint saves (automatic saves at key points)
    public void createCheckpoint(String description) {
        GameMemento checkpoint = gameState.saveGame();
        checkpoints.add(checkpoint);
        
        // Maintain maximum checkpoints
        if (checkpoints.size() > maxCheckpoints) {
            checkpoints.remove(0);
        }
        
        System.out.println("Checkpoint created: " + description + 
                         " (checkpoint " + checkpoints.size() + "/" + maxCheckpoints + ")");
    }
    
    public boolean loadCheckpoint(int index) {
        if (index >= 0 && index < checkpoints.size()) {
            GameMemento checkpoint = checkpoints.get(index);
            gameState.loadGame(checkpoint);
            System.out.println("Loaded checkpoint " + (index + 1));
            return true;
        } else {
            System.out.println("Invalid checkpoint index: " + (index + 1));
            return false;
        }
    }
    
    public boolean loadLatestCheckpoint() {
        if (!checkpoints.isEmpty()) {
            GameMemento checkpoint = checkpoints.get(checkpoints.size() - 1);
            gameState.loadGame(checkpoint);
            System.out.println("Loaded latest checkpoint");
            return true;
        } else {
            System.out.println("No checkpoints available");
            return false;
        }
    }
    
    // Death recovery - restore from latest checkpoint or quick save
    public boolean recoverFromDeath() {
        System.out.println("Player died! Attempting recovery...");
        
        // Try latest checkpoint first
        if (!checkpoints.isEmpty()) {
            loadLatestCheckpoint();
            System.out.println("Recovered from latest checkpoint");
            return true;
        }
        
        // Fall back to latest quick save
        if (!quickSaves.isEmpty()) {
            quickLoadLatest();
            System.out.println("Recovered from latest quick save");
            return true;
        }
        
        System.out.println("No recovery saves available - game over!");
        return false;
    }
    
    // Information methods
    public void listSaves() {
        System.out.println("\n=== Save Files ===");
        
        if (namedSaves.isEmpty()) {
            System.out.println("No named saves");
        } else {
            System.out.println("Named Saves:");
            for (Map.Entry<String, GameMemento> entry : namedSaves.entrySet()) {
                System.out.println("  " + entry.getKey() + ": " + entry.getValue());
            }
        }
        
        if (quickSaves.isEmpty()) {
            System.out.println("No quick saves");
        } else {
            System.out.println("Quick Saves:");
            for (int i = 0; i < quickSaves.size(); i++) {
                System.out.println("  Slot " + (i + 1) + ": " + quickSaves.get(i));
            }
        }
        
        if (checkpoints.isEmpty()) {
            System.out.println("No checkpoints");
        } else {
            System.out.println("Checkpoints:");
            for (int i = 0; i < checkpoints.size(); i++) {
                System.out.println("  " + (i + 1) + ": " + checkpoints.get(i));
            }
        }
        
        System.out.println("==================\n");
    }
    
    public int getNamedSaveCount() { return namedSaves.size(); }
    public int getQuickSaveCount() { return quickSaves.size(); }
    public int getCheckpointCount() { return checkpoints.size(); }
    
    // Clear all saves
    public void clearAllSaves() {
        namedSaves.clear();
        quickSaves.clear();
        checkpoints.clear();
        System.out.println("All saves cleared");
    }
}

// Client code demonstrating the Game Save System
public class GameSaveSystemExample {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Game Save System Memento Pattern Demo ===\n");
        
        // Create game and save manager
        GameState game = new GameState("Hero", "Warrior", "Normal");
        SaveGameManager saveManager = new SaveGameManager(game);
        
        // Initial game state
        game.displayGameState();
        
        // Start playing - tutorial area
        System.out.println("=== Tutorial Area ===");
        game.moveToLocation("Tutorial Cave", 10, 5, 0, "A dark cave for learning");
        game.addToInventory("Tutorial Shield");
        game.gainExperience(25);
        game.addGold(50);
        
        // Create checkpoint after tutorial
        saveManager.createCheckpoint("Tutorial completed");
        game.displayGameState();
        
        Thread.sleep(1000);
        
        // Move to first real area
        System.out.println("\n=== First Adventure ===");
        game.moveToLocation("Goblin Forest", 25, 10, 0, "A forest infested with goblins");
        game.completeQuest("Kill 5 Goblins");
        game.gainExperience(75);
        game.addGold(100);
        game.addToInventory("Goblin Sword");
        game.setGameFlag("goblin_chief_defeated", true);
        
        // Quick save before dangerous area
        saveManager.quickSave();
        game.displayGameState();
        
        Thread.sleep(1000);
        
        // Dangerous encounter
        System.out.println("\n=== Dangerous Encounter ===");
        game.moveToLocation("Dragon Lair", 50, 20, 0, "Lair of an ancient dragon");
        game.takeDamage(80); // Almost die
        
        // Create a named save before attempting boss
        saveManager.saveGame("Before Dragon Boss");
        
        // Boss fight simulation
        System.out.println("\n=== Boss Fight ===");
        game.useMana(30);
        game.takeDamage(25); // Player dies
        
        if (!game.isAlive()) {
            saveManager.recoverFromDeath();
        }
        
        game.displayGameState();
        
        // Try boss fight again with better strategy
        System.out.println("\n=== Boss Fight - Take 2 ===");
        game.heal(50); // Use healing items
        game.restoreMana(40);
        game.useMana(20); // Use less mana
        game.takeDamage(15); // Take less damage
        game.gainExperience(200); // Victory!
        game.addGold(500);
        game.addToInventory("Dragon Scale");
        game.completeQuest("Defeat Ancient Dragon");
        game.setGameFlag("dragon_defeated", true);
        
        // Major checkpoint after boss
        saveManager.createCheckpoint("Dragon boss defeated");
        game.displayGameState();
        
        Thread.sleep(1000);
        
        // Continue adventure
        System.out.println("\n=== Post-Boss Adventure ===");
        game.moveToLocation("Crystal Caves", 75, 30, -10, "Underground crystal formations");
        game.addToInventory("Magic Crystal");
        game.addToInventory("Rare Gem");
        game.spendGold(200); // Buy equipment
        game.gainExperience(100);
        
        // Another quick save
        saveManager.quickSave();
        
        // Final area
        System.out.println("\n=== Final Area ===");
        game.moveToLocation("Ancient Temple", 100, 50, 5, "Temple of the ancients");
        game.completeQuest("Unlock Ancient Secrets");
        game.addToInventory("Ancient Artifact");
        game.gainExperience(300);
        game.addGold(1000);
        
        // Final save
        saveManager.saveGame("Game Complete");
        saveManager.createCheckpoint("Game finished");
        
        game.displayGameState();
        
        // Demonstrate save management
        System.out.println("\n=== Save Management Demo ===");
        saveManager.listSaves();
        
        // Load different saves to show state restoration
        System.out.println("\n=== Loading Previous Saves ===");
        
        // Load the save before dragon boss
        saveManager.loadGame("Before Dragon Boss");
        game.displayGameState();
        
        // Load from quick save
        saveManager.quickLoad(2);
        game.displayGameState();
        
        // Load from checkpoint
        saveManager.loadCheckpoint(0); // First checkpoint
        game.displayGameState();
        
        // Load final save
        saveManager.loadGame("Game Complete");
        game.displayGameState();
        
        // Demonstrate multiple quick saves
        System.out.println("\n=== Multiple Quick Saves Test ===");
        for (int i = 1; i <= 7; i++) {
            game.addGold(10);
            game.addPlayTime(30);
            saveManager.quickSave();
        }
        
        saveManager.listSaves();
        
        // Test save limits
        System.out.println("\n=== Testing Save Limits ===");
        SaveGameManager limitedManager = new SaveGameManager(game, 2, 3);
        
        for (int i = 1; i <= 5; i++) {
            limitedManager.createCheckpoint("Checkpoint " + i);
            limitedManager.quickSave();
        }
        
        limitedManager.listSaves();
        
        // Cleanup demonstration
        System.out.println("\n=== Cleanup ===");
        saveManager.deleteSave("Before Dragon Boss");
        saveManager.listSaves();
        
        System.out.println("\nGame Save System demonstration completed!");
    }
}

/*
Expected Output:
=== Game Save System Memento Pattern Demo ===

New game started for Hero (Warrior) on Normal difficulty

=== Game State ===
Level: 1
Stats: HP:100 MP:50 XP:0 Gold:100 Class:Warrior
Location: Starting Village (0,0,0) - A peaceful village
Inventory (2 items): [Basic Sword, Health Potion]
Completed Quests (0): []
Play Time: 00:00:00
Difficulty: Normal
Last Saved: [timestamp]
==================

=== Tutorial Area ===
Moved to: Tutorial Cave (10,5,0) - A dark cave for learning
Added to inventory: Tutorial Shield
Gained 25 experience (Total: 25)
Gained 50 gold (Total: 150)
Checkpoint created: Tutorial completed (checkpoint 1/10)

=== Game State ===
Level: 1
Stats: HP:100 MP:50 XP:25 Gold:150 Class:Warrior
Location: Tutorial Cave (10,5,0) - A dark cave for learning
Inventory (3 items): [Basic Sword, Health Potion, Tutorial Shield]
Completed Quests (0): []
Play Time: 00:00:05
Difficulty: Normal
Last Saved: [timestamp]
==================

=== First Adventure ===
Moved to: Goblin Forest (25,10,0) - A forest infested with goblins
Quest completed: Kill 5 Goblins
Gained 75 experience (Total: 100)
LEVEL UP! Now level 2
Gained 100 gold (Total: 250)
Added to inventory: Goblin Sword
Game flag set: goblin_chief_defeated = true
Quick save created (slot 1/5)

=== Game State ===
Level: 2
Stats: HP:100 MP:60 XP:100 Gold:250 Class:Warrior
Location: Goblin Forest (25,10,0) - A forest infested with goblins
Inventory (4 items): [Basic Sword, Health Potion, Tutorial Shield, Goblin Sword]
Completed Quests (1): [Kill 5 Goblins]
Play Time: 00:00:10
Difficulty: Normal
Last Saved: [timestamp]
Game Flags: {goblin_chief_defeated=true}
==================

=== Dangerous Encounter ===
Moved to: Dragon Lair (50,20,0) - Lair of an ancient dragon
Took 80 damage (Health: 20)
Game saved as: Before Dragon Boss

=== Boss Fight ===
Used 30 mana (Mana: 30)
Took 25 damage (Health: -5)
Player has died!
Player died! Attempting recovery...
Game loaded from save created at [timestamp]
Loaded latest checkpoint

=== Game State ===
Level: 1
Stats: HP:100 MP:50 XP:25 Gold:150 Class:Warrior
Location: Tutorial Cave (10,5,0) - A dark cave for learning
Inventory (3 items): [Basic Sword, Health Potion, Tutorial Shield]
Completed Quests (0): []
Play Time: 00:00:05
Difficulty: Normal
Last Saved: [timestamp]
==================

=== Boss Fight - Take 2 ===
Healed 50 HP (Health: 100)
Restored 40 mana (Mana: 50)
Used 20 mana (Mana: 30)
Took 15 damage (Health: 85)
Gained 200 experience (Total: 225)
LEVEL UP! Now level 2
LEVEL UP! Now level 3
Gained 500 gold (Total: 650)
Added to inventory: Dragon Scale
Quest completed: Defeat Ancient Dragon
Game flag set: dragon_defeated = true
Checkpoint created: Dragon boss defeated (checkpoint 2/10)

=== Save Management Demo ===

=== Save Files ===
Named Saves:
  Before Dragon Boss: Save[[timestamp], Lvl 2, Dragon Lair, Items: 4]
  Game Complete: Save[[timestamp], Lvl 4, Ancient Temple, Items: 7]

Quick Saves:
  Slot 1: Save[[timestamp], Lvl 2, Goblin Forest, Items: 4]
  Slot 2: Save[[timestamp], Lvl 3, Crystal Caves, Items: 6]

Checkpoints:
  1: Save[[timestamp], Lvl 1, Tutorial Cave, Items: 3]
  2: Save[[timestamp], Lvl 3, Dragon Lair, Items: 5]
  3: Save[[timestamp], Lvl 4, Ancient Temple, Items: 7]
==================

=== Loading Previous Saves ===
Loaded game: Before Dragon Boss

[Game state displays for each load operation...]

=== Multiple Quick Saves Test ===
Gained 10 gold (Total: 1660)
Quick save created (slot 1/5)
[Multiple quick saves created, oldest ones removed due to limit...]

=== Testing Save Limits ===
Checkpoint created: Checkpoint 1 (checkpoint 1/3)
Quick save created (slot 1/2)
[Additional checkpoints and quick saves created with limits enforced...]

=== Cleanup ===
Deleted save: Before Dragon Boss

Game Save System demonstration completed!
*/