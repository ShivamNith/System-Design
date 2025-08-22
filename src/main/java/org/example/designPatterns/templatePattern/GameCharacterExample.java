package org.example.designPatterns.templatePattern;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Template Method Pattern Example: Game Character Creation
 * 
 * This example demonstrates how different character classes (Warrior, Mage, Archer)
 * follow the same character creation process while implementing class-specific
 * attributes, skills, and equipment.
 */

// Supporting classes
enum Attribute {
    STRENGTH, INTELLIGENCE, DEXTERITY, CONSTITUTION, WISDOM, CHARISMA
}

enum SkillType {
    COMBAT, MAGIC, STEALTH, CRAFTING, SOCIAL
}

class Skill {
    private final String name;
    private final SkillType type;
    private final int level;
    private final String description;
    
    public Skill(String name, SkillType type, int level, String description) {
        this.name = name;
        this.type = type;
        this.level = level;
        this.description = description;
    }
    
    public String getName() { return name; }
    public SkillType getType() { return type; }
    public int getLevel() { return level; }
    public String getDescription() { return description; }
    
    @Override
    public String toString() {
        return name + " (Lv." + level + ") - " + description;
    }
}

class Equipment {
    private final String name;
    private final String type;
    private final Map<Attribute, Integer> bonuses;
    private final List<String> specialEffects;
    
    public Equipment(String name, String type) {
        this.name = name;
        this.type = type;
        this.bonuses = new HashMap<>();
        this.specialEffects = new ArrayList<>();
    }
    
    public Equipment addBonus(Attribute attribute, int bonus) {
        bonuses.put(attribute, bonus);
        return this;
    }
    
    public Equipment addSpecialEffect(String effect) {
        specialEffects.add(effect);
        return this;
    }
    
    public String getName() { return name; }
    public String getType() { return type; }
    public Map<Attribute, Integer> getBonuses() { return new HashMap<>(bonuses); }
    public List<String> getSpecialEffects() { return new ArrayList<>(specialEffects); }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(name + " (" + type + ")");
        if (!bonuses.isEmpty()) {
            sb.append(" [");
            sb.append(bonuses.entrySet().stream()
                .map(entry -> entry.getKey() + "+" + entry.getValue())
                .collect(Collectors.joining(", ")));
            sb.append("]");
        }
        if (!specialEffects.isEmpty()) {
            sb.append(" {").append(String.join(", ", specialEffects)).append("}");
        }
        return sb.toString();
    }
}

class GameCharacter {
    private String name;
    private String characterClass;
    private int level;
    private Map<Attribute, Integer> attributes;
    private List<Skill> skills;
    private List<Equipment> equipment;
    private Map<String, Object> specialAbilities;
    private int hitPoints;
    private int manaPoints;
    private String background;
    
    public GameCharacter(String name, String characterClass) {
        this.name = name;
        this.characterClass = characterClass;
        this.level = 1;
        this.attributes = new HashMap<>();
        this.skills = new ArrayList<>();
        this.equipment = new ArrayList<>();
        this.specialAbilities = new HashMap<>();
    }
    
    // Getters and setters
    public String getName() { return name; }
    public String getCharacterClass() { return characterClass; }
    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }
    
    public Map<Attribute, Integer> getAttributes() { return new HashMap<>(attributes); }
    public void setAttribute(Attribute attribute, int value) { attributes.put(attribute, value); }
    public int getAttribute(Attribute attribute) { return attributes.getOrDefault(attribute, 0); }
    
    public List<Skill> getSkills() { return new ArrayList<>(skills); }
    public void addSkill(Skill skill) { skills.add(skill); }
    
    public List<Equipment> getEquipment() { return new ArrayList<>(equipment); }
    public void addEquipment(Equipment item) { equipment.add(item); }
    
    public Map<String, Object> getSpecialAbilities() { return new HashMap<>(specialAbilities); }
    public void addSpecialAbility(String name, Object ability) { specialAbilities.put(name, ability); }
    
    public int getHitPoints() { return hitPoints; }
    public void setHitPoints(int hitPoints) { this.hitPoints = hitPoints; }
    
    public int getManaPoints() { return manaPoints; }
    public void setManaPoints(int manaPoints) { this.manaPoints = manaPoints; }
    
    public String getBackground() { return background; }
    public void setBackground(String background) { this.background = background; }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== ").append(name).append(" the ").append(characterClass).append(" ===\n");
        sb.append("Level: ").append(level).append("\n");
        sb.append("HP: ").append(hitPoints).append(", MP: ").append(manaPoints).append("\n");
        sb.append("Background: ").append(background).append("\n\n");
        
        sb.append("Attributes:\n");
        for (Map.Entry<Attribute, Integer> entry : attributes.entrySet()) {
            sb.append("  ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }
        
        sb.append("\nSkills:\n");
        for (Skill skill : skills) {
            sb.append("  • ").append(skill).append("\n");
        }
        
        sb.append("\nEquipment:\n");
        for (Equipment item : equipment) {
            sb.append("  • ").append(item).append("\n");
        }
        
        if (!specialAbilities.isEmpty()) {
            sb.append("\nSpecial Abilities:\n");
            for (Map.Entry<String, Object> entry : specialAbilities.entrySet()) {
                sb.append("  • ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
            }
        }
        
        return sb.toString();
    }
}

// Abstract Character Creator Template
abstract class CharacterCreator {
    
    /**
     * Template method defining the character creation process
     */
    public final GameCharacter createCharacter(String name) {
        System.out.println("Starting character creation for: " + name);
        
        // Step 1: Initialize base character
        GameCharacter character = initializeCharacter(name);
        
        // Step 2: Set base attributes
        setBaseAttributes(character);
        
        // Step 3: Apply class-specific attribute modifiers
        applyClassModifiers(character);
        
        // Step 4: Calculate derived stats
        calculateDerivedStats(character);
        
        // Step 5: Assign starting skills
        assignStartingSkills(character);
        
        // Step 6: Equip starting equipment
        equipStartingGear(character);
        
        // Step 7: Add special abilities
        grantSpecialAbilities(character);
        
        // Step 8: Set character background
        if (shouldGenerateBackground()) {
            character.setBackground(generateBackground(character));
        }
        
        // Step 9: Apply final adjustments
        applyFinalAdjustments(character);
        
        // Step 10: Validate character
        validateCharacter(character);
        
        System.out.println("Character creation completed for " + character.getName());
        return character;
    }
    
    // Abstract methods that subclasses must implement
    protected abstract String getCharacterClass();
    protected abstract void applyClassModifiers(GameCharacter character);
    protected abstract void assignStartingSkills(GameCharacter character);
    protected abstract void equipStartingGear(GameCharacter character);
    protected abstract void grantSpecialAbilities(GameCharacter character);
    
    // Hook methods with default implementations
    protected GameCharacter initializeCharacter(String name) {
        return new GameCharacter(name, getCharacterClass());
    }
    
    protected void setBaseAttributes(GameCharacter character) {
        System.out.println("Setting base attributes...");
        // Standard base attributes for all characters
        character.setAttribute(Attribute.STRENGTH, 10);
        character.setAttribute(Attribute.INTELLIGENCE, 10);
        character.setAttribute(Attribute.DEXTERITY, 10);
        character.setAttribute(Attribute.CONSTITUTION, 10);
        character.setAttribute(Attribute.WISDOM, 10);
        character.setAttribute(Attribute.CHARISMA, 10);
    }
    
    protected void calculateDerivedStats(GameCharacter character) {
        System.out.println("Calculating derived stats...");
        
        // Calculate hit points based on constitution
        int baseHP = 20 + (character.getAttribute(Attribute.CONSTITUTION) - 10) * 2;
        character.setHitPoints(baseHP);
        
        // Calculate mana points based on intelligence
        int baseMP = 10 + (character.getAttribute(Attribute.INTELLIGENCE) - 10) * 3;
        character.setManaPoints(baseMP);
    }
    
    protected boolean shouldGenerateBackground() {
        return true; // Default: generate background
    }
    
    protected String generateBackground(GameCharacter character) {
        String[] backgrounds = {
            "Born in a small village, seeking adventure",
            "Former apprentice turned adventurer",
            "Noble seeking to prove their worth",
            "Traveler from distant lands",
            "Self-taught warrior of the wilderness"
        };
        return backgrounds[new Random().nextInt(backgrounds.length)];
    }
    
    protected void applyFinalAdjustments(GameCharacter character) {
        System.out.println("Applying final adjustments...");
        
        // Apply equipment bonuses to attributes
        for (Equipment item : character.getEquipment()) {
            for (Map.Entry<Attribute, Integer> bonus : item.getBonuses().entrySet()) {
                int currentValue = character.getAttribute(bonus.getKey());
                character.setAttribute(bonus.getKey(), currentValue + bonus.getValue());
            }
        }
        
        // Recalculate derived stats after equipment bonuses
        calculateDerivedStats(character);
    }
    
    protected void validateCharacter(GameCharacter character) {
        System.out.println("Validating character...");
        
        // Basic validation
        if (character.getName() == null || character.getName().trim().isEmpty()) {
            throw new IllegalStateException("Character must have a name");
        }
        
        if (character.getSkills().isEmpty()) {
            throw new IllegalStateException("Character must have at least one skill");
        }
        
        if (character.getHitPoints() <= 0) {
            throw new IllegalStateException("Character must have positive hit points");
        }
        
        System.out.println("Character validation passed");
    }
    
    // Utility methods
    protected void modifyAttribute(GameCharacter character, Attribute attribute, int modifier) {
        int currentValue = character.getAttribute(attribute);
        character.setAttribute(attribute, currentValue + modifier);
    }
}

// Concrete implementation for Warrior class
class WarriorCreator extends CharacterCreator {
    
    @Override
    protected String getCharacterClass() {
        return "Warrior";
    }
    
    @Override
    protected void applyClassModifiers(GameCharacter character) {
        System.out.println("Applying Warrior class modifiers...");
        
        // Warriors get bonuses to physical attributes
        modifyAttribute(character, Attribute.STRENGTH, 4);
        modifyAttribute(character, Attribute.CONSTITUTION, 3);
        modifyAttribute(character, Attribute.DEXTERITY, 1);
        
        // Penalties to mental attributes
        modifyAttribute(character, Attribute.INTELLIGENCE, -2);
        modifyAttribute(character, Attribute.WISDOM, -1);
    }
    
    @Override
    protected void assignStartingSkills(GameCharacter character) {
        System.out.println("Assigning Warrior starting skills...");
        
        character.addSkill(new Skill("Sword Combat", SkillType.COMBAT, 3, "Expertise with sword weapons"));
        character.addSkill(new Skill("Shield Defense", SkillType.COMBAT, 2, "Ability to use shields effectively"));
        character.addSkill(new Skill("Heavy Armor", SkillType.COMBAT, 2, "Proficiency with heavy armor"));
        character.addSkill(new Skill("Intimidation", SkillType.SOCIAL, 1, "Ability to intimidate opponents"));
        character.addSkill(new Skill("Weapon Maintenance", SkillType.CRAFTING, 1, "Basic weapon and armor care"));
    }
    
    @Override
    protected void equipStartingGear(GameCharacter character) {
        System.out.println("Equipping Warrior starting gear...");
        
        Equipment sword = new Equipment("Iron Sword", "Weapon")
            .addBonus(Attribute.STRENGTH, 2)
            .addSpecialEffect("Slash damage");
            
        Equipment shield = new Equipment("Wooden Shield", "Shield")
            .addBonus(Attribute.CONSTITUTION, 1)
            .addSpecialEffect("Block chance +10%");
            
        Equipment armor = new Equipment("Chain Mail", "Armor")
            .addBonus(Attribute.CONSTITUTION, 2)
            .addSpecialEffect("Physical damage reduction");
            
        Equipment boots = new Equipment("Leather Boots", "Footwear")
            .addBonus(Attribute.DEXTERITY, 1);
        
        character.addEquipment(sword);
        character.addEquipment(shield);
        character.addEquipment(armor);
        character.addEquipment(boots);
    }
    
    @Override
    protected void grantSpecialAbilities(GameCharacter character) {
        System.out.println("Granting Warrior special abilities...");
        
        character.addSpecialAbility("Battle Rage", "Increase damage by 50% for 3 turns");
        character.addSpecialAbility("Taunt", "Force enemy to attack you for 2 turns");
        character.addSpecialAbility("Shield Bash", "Stun enemy for 1 turn");
    }
    
    @Override
    protected void calculateDerivedStats(GameCharacter character) {
        super.calculateDerivedStats(character);
        
        // Warriors get extra hit points
        int extraHP = character.getAttribute(Attribute.STRENGTH) / 2;
        character.setHitPoints(character.getHitPoints() + extraHP);
    }
}

// Concrete implementation for Mage class
class MageCreator extends CharacterCreator {
    
    @Override
    protected String getCharacterClass() {
        return "Mage";
    }
    
    @Override
    protected void applyClassModifiers(GameCharacter character) {
        System.out.println("Applying Mage class modifiers...");
        
        // Mages get bonuses to mental attributes
        modifyAttribute(character, Attribute.INTELLIGENCE, 5);
        modifyAttribute(character, Attribute.WISDOM, 3);
        modifyAttribute(character, Attribute.CHARISMA, 1);
        
        // Penalties to physical attributes
        modifyAttribute(character, Attribute.STRENGTH, -3);
        modifyAttribute(character, Attribute.CONSTITUTION, -2);
    }
    
    @Override
    protected void assignStartingSkills(GameCharacter character) {
        System.out.println("Assigning Mage starting skills...");
        
        character.addSkill(new Skill("Elemental Magic", SkillType.MAGIC, 4, "Control over fire, ice, and lightning"));
        character.addSkill(new Skill("Arcane Knowledge", SkillType.MAGIC, 3, "Understanding of magical theory"));
        character.addSkill(new Skill("Spell Focus", SkillType.MAGIC, 2, "Improved spell accuracy and power"));
        character.addSkill(new Skill("Alchemy", SkillType.CRAFTING, 2, "Creation of magical potions"));
        character.addSkill(new Skill("Ancient Languages", SkillType.SOCIAL, 1, "Reading ancient magical texts"));
    }
    
    @Override
    protected void equipStartingGear(GameCharacter character) {
        System.out.println("Equipping Mage starting gear...");
        
        Equipment staff = new Equipment("Apprentice Staff", "Weapon")
            .addBonus(Attribute.INTELLIGENCE, 3)
            .addSpecialEffect("Spell power +20%");
            
        Equipment robes = new Equipment("Mage Robes", "Armor")
            .addBonus(Attribute.WISDOM, 2)
            .addSpecialEffect("Mana regeneration +1 per turn");
            
        Equipment hat = new Equipment("Pointed Hat", "Headwear")
            .addBonus(Attribute.INTELLIGENCE, 1)
            .addSpecialEffect("Spell critical chance +5%");
            
        Equipment spellbook = new Equipment("Basic Spellbook", "Accessory")
            .addBonus(Attribute.WISDOM, 1)
            .addSpecialEffect("Access to basic spells");
        
        character.addEquipment(staff);
        character.addEquipment(robes);
        character.addEquipment(hat);
        character.addEquipment(spellbook);
    }
    
    @Override
    protected void grantSpecialAbilities(GameCharacter character) {
        System.out.println("Granting Mage special abilities...");
        
        character.addSpecialAbility("Fireball", "Deal fire damage to single target");
        character.addSpecialAbility("Ice Shield", "Reduce incoming damage by 30% for 3 turns");
        character.addSpecialAbility("Lightning Bolt", "Fast spell that cannot be dodged");
        character.addSpecialAbility("Mana Surge", "Restore 50% of maximum mana");
    }
    
    @Override
    protected void calculateDerivedStats(GameCharacter character) {
        super.calculateDerivedStats(character);
        
        // Mages get extra mana points
        int extraMP = character.getAttribute(Attribute.INTELLIGENCE) / 2;
        character.setManaPoints(character.getManaPoints() + extraMP);
    }
    
    @Override
    protected String generateBackground(GameCharacter character) {
        String[] mageBackgrounds = {
            "Studied at the Arcane Academy",
            "Self-taught practitioner of forbidden magic",
            "Former court wizard seeking knowledge",
            "Apprentice to a legendary archmage",
            "Born with natural magical abilities"
        };
        return mageBackgrounds[new Random().nextInt(mageBackgrounds.length)];
    }
}

// Concrete implementation for Archer class
class ArcherCreator extends CharacterCreator {
    
    @Override
    protected String getCharacterClass() {
        return "Archer";
    }
    
    @Override
    protected void applyClassModifiers(GameCharacter character) {
        System.out.println("Applying Archer class modifiers...");
        
        // Archers get bonuses to dexterity and perception
        modifyAttribute(character, Attribute.DEXTERITY, 4);
        modifyAttribute(character, Attribute.WISDOM, 2);
        modifyAttribute(character, Attribute.CONSTITUTION, 1);
        
        // Minor penalties to other attributes
        modifyAttribute(character, Attribute.STRENGTH, -1);
        modifyAttribute(character, Attribute.CHARISMA, -1);
    }
    
    @Override
    protected void assignStartingSkills(GameCharacter character) {
        System.out.println("Assigning Archer starting skills...");
        
        character.addSkill(new Skill("Archery", SkillType.COMBAT, 4, "Expertise with bow and arrow"));
        character.addSkill(new Skill("Tracking", SkillType.STEALTH, 3, "Ability to track creatures and people"));
        character.addSkill(new Skill("Stealth", SkillType.STEALTH, 2, "Moving silently and hiding"));
        character.addSkill(new Skill("Survival", SkillType.CRAFTING, 2, "Wilderness survival skills"));
        character.addSkill(new Skill("Animal Handling", SkillType.SOCIAL, 1, "Ability to work with animals"));
    }
    
    @Override
    protected void equipStartingGear(GameCharacter character) {
        System.out.println("Equipping Archer starting gear...");
        
        Equipment bow = new Equipment("Hunting Bow", "Weapon")
            .addBonus(Attribute.DEXTERITY, 2)
            .addSpecialEffect("Ranged attacks");
            
        Equipment quiver = new Equipment("Arrow Quiver", "Accessory")
            .addBonus(Attribute.DEXTERITY, 1)
            .addSpecialEffect("Holds 30 arrows");
            
        Equipment leather = new Equipment("Leather Armor", "Armor")
            .addBonus(Attribute.DEXTERITY, 1)
            .addBonus(Attribute.CONSTITUTION, 1)
            .addSpecialEffect("Silent movement");
            
        Equipment cloak = new Equipment("Forest Cloak", "Cloak")
            .addBonus(Attribute.WISDOM, 1)
            .addSpecialEffect("Camouflage in forests");
        
        character.addEquipment(bow);
        character.addEquipment(quiver);
        character.addEquipment(leather);
        character.addEquipment(cloak);
    }
    
    @Override
    protected void grantSpecialAbilities(GameCharacter character) {
        System.out.println("Granting Archer special abilities...");
        
        character.addSpecialAbility("Aimed Shot", "Deal double damage with next ranged attack");
        character.addSpecialAbility("Multi-Shot", "Attack up to 3 targets with one action");
        character.addSpecialAbility("Eagle Eye", "Increase accuracy and range for 5 turns");
        character.addSpecialAbility("Hunter's Mark", "Mark target for +50% damage");
    }
    
    @Override
    protected String generateBackground(GameCharacter character) {
        String[] archerBackgrounds = {
            "Trained as a royal guard archer",
            "Grew up hunting in the deep forests",
            "Former military scout and messenger",
            "Learned archery from elven mentors",
            "Self-trained wilderness survivalist"
        };
        return archerBackgrounds[new Random().nextInt(archerBackgrounds.length)];
    }
}

// Advanced Character Creator with Customization Options
class CustomizableCharacterCreator extends CharacterCreator {
    private final String className;
    private final Map<Attribute, Integer> customModifiers;
    private final List<Skill> customSkills;
    private final List<Equipment> customEquipment;
    private final Map<String, Object> customAbilities;
    
    public CustomizableCharacterCreator(String className) {
        this.className = className;
        this.customModifiers = new HashMap<>();
        this.customSkills = new ArrayList<>();
        this.customEquipment = new ArrayList<>();
        this.customAbilities = new HashMap<>();
    }
    
    public CustomizableCharacterCreator addAttributeModifier(Attribute attribute, int modifier) {
        customModifiers.put(attribute, modifier);
        return this;
    }
    
    public CustomizableCharacterCreator addStartingSkill(Skill skill) {
        customSkills.add(skill);
        return this;
    }
    
    public CustomizableCharacterCreator addStartingEquipment(Equipment equipment) {
        customEquipment.add(equipment);
        return this;
    }
    
    public CustomizableCharacterCreator addSpecialAbility(String name, Object ability) {
        customAbilities.put(name, ability);
        return this;
    }
    
    @Override
    protected String getCharacterClass() {
        return className;
    }
    
    @Override
    protected void applyClassModifiers(GameCharacter character) {
        System.out.println("Applying custom class modifiers...");
        
        for (Map.Entry<Attribute, Integer> modifier : customModifiers.entrySet()) {
            modifyAttribute(character, modifier.getKey(), modifier.getValue());
        }
    }
    
    @Override
    protected void assignStartingSkills(GameCharacter character) {
        System.out.println("Assigning custom starting skills...");
        
        for (Skill skill : customSkills) {
            character.addSkill(skill);
        }
    }
    
    @Override
    protected void equipStartingGear(GameCharacter character) {
        System.out.println("Equipping custom starting gear...");
        
        for (Equipment equipment : customEquipment) {
            character.addEquipment(equipment);
        }
    }
    
    @Override
    protected void grantSpecialAbilities(GameCharacter character) {
        System.out.println("Granting custom special abilities...");
        
        for (Map.Entry<String, Object> ability : customAbilities.entrySet()) {
            character.addSpecialAbility(ability.getKey(), ability.getValue());
        }
    }
}

// Example usage and demonstration
public class GameCharacterExample {
    
    public static void main(String[] args) {
        System.out.println("Template Method Pattern - Game Character Creation Example");
        System.out.println("========================================================\n");
        
        // Example 1: Create a Warrior
        demonstrateWarriorCreation();
        
        System.out.println("\n" + "=".repeat(60) + "\n");
        
        // Example 2: Create a Mage
        demonstrateMageCreation();
        
        System.out.println("\n" + "=".repeat(60) + "\n");
        
        // Example 3: Create an Archer
        demonstrateArcherCreation();
        
        System.out.println("\n" + "=".repeat(60) + "\n");
        
        // Example 4: Create a Custom Character
        demonstrateCustomCharacterCreation();
        
        System.out.println("\n" + "=".repeat(60) + "\n");
        
        // Example 5: Batch Character Creation
        demonstrateBatchCreation();
    }
    
    private static void demonstrateWarriorCreation() {
        System.out.println("1. Creating a Warrior Character");
        System.out.println("==============================");
        
        CharacterCreator warriorCreator = new WarriorCreator();
        GameCharacter warrior = warriorCreator.createCharacter("Thorgar the Brave");
        
        System.out.println("\nCreated Character:");
        System.out.println(warrior);
    }
    
    private static void demonstrateMageCreation() {
        System.out.println("2. Creating a Mage Character");
        System.out.println("============================");
        
        CharacterCreator mageCreator = new MageCreator();
        GameCharacter mage = mageCreator.createCharacter("Eldara the Wise");
        
        System.out.println("\nCreated Character:");
        System.out.println(mage);
    }
    
    private static void demonstrateArcherCreation() {
        System.out.println("3. Creating an Archer Character");
        System.out.println("===============================");
        
        CharacterCreator archerCreator = new ArcherCreator();
        GameCharacter archer = archerCreator.createCharacter("Silvan Swiftarrow");
        
        System.out.println("\nCreated Character:");
        System.out.println(archer);
    }
    
    private static void demonstrateCustomCharacterCreation() {
        System.out.println("4. Creating a Custom Character (Paladin)");
        System.out.println("========================================");
        
        CustomizableCharacterCreator paladinCreator = new CustomizableCharacterCreator("Paladin")
            .addAttributeModifier(Attribute.STRENGTH, 2)
            .addAttributeModifier(Attribute.CONSTITUTION, 2)
            .addAttributeModifier(Attribute.WISDOM, 3)
            .addAttributeModifier(Attribute.CHARISMA, 2)
            .addAttributeModifier(Attribute.INTELLIGENCE, -1)
            .addStartingSkill(new Skill("Divine Magic", SkillType.MAGIC, 2, "Healing and protective magic"))
            .addStartingSkill(new Skill("Sword Combat", SkillType.COMBAT, 3, "Holy warrior combat skills"))
            .addStartingSkill(new Skill("Leadership", SkillType.SOCIAL, 2, "Inspiring and leading others"))
            .addStartingEquipment(new Equipment("Holy Sword", "Weapon")
                .addBonus(Attribute.STRENGTH, 2)
                .addSpecialEffect("Extra damage vs undead"))
            .addStartingEquipment(new Equipment("Plate Armor", "Armor")
                .addBonus(Attribute.CONSTITUTION, 3)
                .addSpecialEffect("Divine protection"))
            .addSpecialAbility("Healing Light", "Restore health to self or ally")
            .addSpecialAbility("Divine Protection", "Immunity to fear and charm effects")
            .addSpecialAbility("Smite Evil", "Deal extra damage to evil creatures");
        
        GameCharacter paladin = paladinCreator.createCharacter("Sir Gareth the Pure");
        
        System.out.println("\nCreated Character:");
        System.out.println(paladin);
    }
    
    private static void demonstrateBatchCreation() {
        System.out.println("5. Batch Character Creation");
        System.out.println("===========================");
        
        List<CharacterCreator> creators = Arrays.asList(
            new WarriorCreator(),
            new MageCreator(),
            new ArcherCreator()
        );
        
        List<String> names = Arrays.asList(
            "Grimjaw Ironbeard",
            "Mystic Moonwhisper", 
            "Robin Keeneye"
        );
        
        List<GameCharacter> party = new ArrayList<>();
        
        for (int i = 0; i < creators.size(); i++) {
            System.out.println("\nCreating party member " + (i + 1) + "...");
            GameCharacter character = creators.get(i).createCharacter(names.get(i));
            party.add(character);
        }
        
        System.out.println("\n=== Adventure Party Created ===");
        for (GameCharacter character : party) {
            System.out.println("• " + character.getName() + " the " + character.getCharacterClass() + 
                             " (Level " + character.getLevel() + ")");
        }
        
        // Calculate party statistics
        int totalHP = party.stream().mapToInt(GameCharacter::getHitPoints).sum();
        int totalMP = party.stream().mapToInt(GameCharacter::getManaPoints).sum();
        long totalSkills = party.stream().mapToLong(c -> c.getSkills().size()).sum();
        
        System.out.println("\nParty Statistics:");
        System.out.println("Total Hit Points: " + totalHP);
        System.out.println("Total Mana Points: " + totalMP);
        System.out.println("Total Skills: " + totalSkills);
        System.out.println("Party Size: " + party.size());
    }
}