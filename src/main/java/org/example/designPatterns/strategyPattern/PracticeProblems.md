# Strategy Pattern Practice Problems

## Overview
These practice problems will help you master the Strategy Pattern by implementing real-world scenarios. Start with the basic problems and progress to more complex challenges.

---

## Problem 1: Text Formatter (Basic)
**Difficulty:** ⭐⭐☆☆☆

### Problem Statement
Create a text formatting system that can format text in different ways:
- **UPPERCASE**: Converts all text to uppercase
- **lowercase**: Converts all text to lowercase
- **Title Case**: Capitalizes first letter of each word
- **camelCase**: First word lowercase, subsequent words capitalized
- **snake_case**: Words separated by underscores

### Requirements
1. Create a `TextFormattingStrategy` interface
2. Implement at least 5 different formatting strategies
3. Create a `TextProcessor` class that uses the strategy
4. Allow runtime switching between strategies

### Test Cases
```
Input: "hello world from java"
- UPPERCASE: "HELLO WORLD FROM JAVA"
- lowercase: "hello world from java"
- Title Case: "Hello World From Java"
- camelCase: "helloWorldFromJava"
- snake_case: "hello_world_from_java"
```

### Hints
- Consider edge cases like multiple spaces, special characters
- Think about how to handle empty strings
- Make strategies reusable and stateless

---

## Problem 2: Discount Calculator (Intermediate)
**Difficulty:** ⭐⭐⭐☆☆

### Problem Statement
Design a discount calculation system for an e-commerce platform with different discount strategies:
- **Percentage Discount**: X% off the total
- **Fixed Amount Discount**: $X off the total
- **Buy X Get Y Free**: Buy certain quantity, get some free
- **Tiered Discount**: Different discounts based on total amount
- **Seasonal Discount**: Special discounts for holidays
- **Member Discount**: Special rates for premium members

### Requirements
1. Create `DiscountStrategy` interface with methods:
   - `calculateDiscount(Cart cart)`
   - `getDescription()`
   - `isApplicable(Cart cart)`
2. Implement complex discount logic including:
   - Minimum purchase requirements
   - Maximum discount limits
   - Product category restrictions
3. Allow combining multiple discounts
4. Create a `PriceCalculator` that applies strategies

### Additional Challenges
- Implement discount stacking rules
- Add expiry dates to discounts
- Create a discount priority system
- Handle conflicting discounts

### Test Scenarios
```
Cart with items worth $100:
- 20% discount = $20 off
- $15 fixed discount = $15 off
- Buy 2 Get 1 Free on specific items
- Tiered: 10% for $50+, 15% for $100+, 20% for $200+
```

---

## Problem 3: File Backup System (Intermediate)
**Difficulty:** ⭐⭐⭐☆☆

### Problem Statement
Create a file backup system with different backup strategies:
- **Full Backup**: Backs up all files
- **Incremental Backup**: Only backs up changed files since last backup
- **Differential Backup**: Backs up files changed since last full backup
- **Mirror Backup**: Creates exact copy, deletes files not in source
- **Compressed Backup**: Compresses files before backing up

### Requirements
1. Create `BackupStrategy` interface with methods:
   - `backup(List<File> files, String destination)`
   - `restore(String backupLocation, String destination)`
   - `getBackupSize(List<File> files)`
2. Implement file versioning
3. Add backup scheduling capabilities
4. Include progress reporting
5. Handle backup verification

### Features to Implement
- File filtering (by extension, size, date)
- Encryption option for sensitive backups
- Multi-destination backup
- Backup rotation policies
- Network backup support

---

## Problem 4: Game Character Attack System (Advanced)
**Difficulty:** ⭐⭐⭐⭐☆

### Problem Statement
Design an attack system for a role-playing game where characters can switch between different combat styles:
- **Melee Attack**: Close-range physical damage
- **Ranged Attack**: Long-range projectile damage
- **Magic Attack**: Elemental damage with mana cost
- **Stealth Attack**: High damage from hidden position
- **Area Attack**: Damage multiple enemies

### Requirements
1. Create `AttackStrategy` interface with:
   - `execute(Character attacker, List<Character> targets)`
   - `calculateDamage(Character attacker, Character target)`
   - `getCooldownTime()`
   - `getResourceCost()`
2. Implement:
   - Damage calculation based on character stats
   - Critical hit chances
   - Elemental resistances
   - Combo attacks
   - Special effects (stun, poison, burn)
3. Create `Character` class with:
   - Health, mana, stamina
   - Attack power, defense, speed
   - Equipment bonuses
   - Skill levels

### Advanced Features
- Chain attacks between strategies
- Environmental factors affecting damage
- Counter-attack mechanisms
- Weakness/strength system
- Skill trees that modify strategies

### Game Mechanics
```
Warrior switching between:
- Sword slash (melee, high damage)
- Shield bash (melee, stuns enemy)
- Whirlwind (area, hits all nearby)

Mage switching between:
- Fireball (magic, burn damage)
- Ice shard (magic, slows enemy)
- Lightning storm (area magic)
```

---

## Problem 5: Investment Portfolio Strategy (Advanced)
**Difficulty:** ⭐⭐⭐⭐☆

### Problem Statement
Create an investment portfolio management system with different investment strategies:
- **Conservative**: 70% bonds, 20% stocks, 10% cash
- **Balanced**: 40% bonds, 50% stocks, 10% alternatives
- **Aggressive**: 10% bonds, 80% stocks, 10% alternatives
- **Income-focused**: High dividend stocks and bonds
- **Growth-focused**: Growth stocks and emerging markets
- **Index-based**: Follows market indices

### Requirements
1. Create `InvestmentStrategy` interface:
   - `allocatePortfolio(double amount, Market market)`
   - `rebalance(Portfolio portfolio, Market market)`
   - `calculateRisk(Portfolio portfolio)`
   - `projectReturns(Portfolio portfolio, int years)`
2. Implement:
   - Asset allocation algorithms
   - Risk assessment
   - Performance tracking
   - Tax optimization
   - Automatic rebalancing
3. Market conditions affect strategy performance
4. Historical data analysis

### Complex Features
- Dynamic strategy adjustment based on:
  - Market volatility
  - Investor age
  - Risk tolerance changes
  - Economic indicators
- Multi-strategy portfolios
- Dollar-cost averaging
- Stop-loss mechanisms

---

## Problem 6: Database Query Optimization (Expert)
**Difficulty:** ⭐⭐⭐⭐⭐

### Problem Statement
Design a database query execution system with different optimization strategies:
- **Index Scan**: Uses indexes for fast lookups
- **Full Table Scan**: Reads entire table
- **Hash Join**: For joining large tables
- **Nested Loop Join**: For small result sets
- **Sort Merge Join**: For pre-sorted data
- **Parallel Query**: Distributes work across threads

### Requirements
1. Create `QueryStrategy` interface:
   - `execute(Query query, Database db)`
   - `estimateCost(Query query, Statistics stats)`
   - `explainPlan()`
2. Implement:
   - Cost-based optimization
   - Statistics gathering
   - Query plan caching
   - Adaptive strategy selection
3. Create query analyzer that chooses optimal strategy
4. Performance monitoring and tuning

### Advanced Implementation
- Dynamic strategy switching during execution
- Machine learning for strategy prediction
- Query result caching
- Distributed query execution
- Real-time strategy adjustment

---

## Problem 7: AI Behavior Tree System (Expert)
**Difficulty:** ⭐⭐⭐⭐⭐

### Problem Statement
Create an AI behavior system for NPCs (Non-Player Characters) in a game using different behavior strategies that can be composed:
- **Patrol**: Move along predefined path
- **Chase**: Follow target aggressively
- **Flee**: Run away from danger
- **Search**: Look for specific objects/enemies
- **Guard**: Protect an area or object
- **Idle**: Default behavior with random actions

### Requirements
1. Create `BehaviorStrategy` interface:
   - `update(NPC npc, World world, float deltaTime)`
   - `canInterrupt()`
   - `getPriority()`
   - `getNextBehavior()`
2. Implement:
   - Behavior trees with composite strategies
   - Condition-based transitions
   - Memory system for NPCs
   - Emotional states affecting behavior
   - Group behaviors (formations, coordinated attacks)
3. Dynamic behavior switching based on:
   - Environmental stimuli
   - Health/resource levels
   - Player actions
   - Time of day
   - Alert levels

### Complex Features
- Hierarchical behavior composition
- Learning from player patterns
- Emergent behaviors from simple rules
- Performance optimization for hundreds of NPCs
- Debugging and visualization tools

---

## Problem 8: Multi-Cloud Deployment Strategy (Expert)
**Difficulty:** ⭐⭐⭐⭐⭐

### Problem Statement
Design a multi-cloud deployment system that can deploy applications using different strategies across cloud providers:
- **Blue-Green Deployment**: Two identical environments
- **Canary Deployment**: Gradual rollout
- **Rolling Deployment**: Update instances incrementally
- **A/B Testing Deployment**: Split traffic for testing
- **Disaster Recovery Deployment**: Multi-region failover
- **Cost-Optimized Deployment**: Cheapest resources

### Requirements
1. Create `DeploymentStrategy` interface:
   - `deploy(Application app, CloudConfig config)`
   - `rollback(Deployment deployment)`
   - `healthCheck(Deployment deployment)`
   - `scaleResources(int instances)`
2. Support multiple cloud providers:
   - AWS, Azure, Google Cloud, Private Cloud
3. Implement:
   - Load balancing
   - Auto-scaling rules
   - Health monitoring
   - Cost tracking
   - Compliance checking
4. Handle:
   - Network latency
   - Data sovereignty
   - Service dependencies
   - Configuration management

### Advanced Requirements
- Multi-region coordination
- Zero-downtime deployments
- Automatic failover
- Performance-based routing
- Predictive scaling
- Cost optimization algorithms

---

## Solution Structure Template

For each problem, structure your solution as follows:

```java
// Strategy Interface
public interface XxxStrategy {
    // Define contract methods
}

// Concrete Strategies
public class ConcreteStrategyA implements XxxStrategy {
    // Implementation
}

public class ConcreteStrategyB implements XxxStrategy {
    // Implementation
}

// Context Class
public class Context {
    private XxxStrategy strategy;
    
    public void setStrategy(XxxStrategy strategy) {
        this.strategy = strategy;
    }
    
    public void executeStrategy() {
        // Delegate to strategy
    }
}

// Client Code
public class Client {
    public static void main(String[] args) {
        // Demonstrate strategy usage
        // Show switching strategies
        // Test different scenarios
    }
}
```

---

## Evaluation Criteria

Your solutions will be evaluated based on:

1. **Correct Implementation** (40%)
   - Proper use of Strategy Pattern
   - All requirements met
   - Working code

2. **Code Quality** (25%)
   - Clean, readable code
   - Proper naming conventions
   - SOLID principles adherence

3. **Extensibility** (20%)
   - Easy to add new strategies
   - Loosely coupled design
   - Reusable components

4. **Error Handling** (10%)
   - Graceful error handling
   - Input validation
   - Edge case handling

5. **Testing** (5%)
   - Unit tests for strategies
   - Integration tests
   - Test coverage

---

## Tips for Success

1. **Start Simple**: Begin with basic implementation, then add complexity
2. **Think Interface First**: Design the strategy interface carefully
3. **Keep Strategies Stateless**: Avoid maintaining state in strategy objects
4. **Use Dependency Injection**: Inject strategies rather than creating them
5. **Consider Performance**: Some strategies may be more efficient than others
6. **Document Your Code**: Explain strategy selection criteria
7. **Test Thoroughly**: Each strategy should be independently testable
8. **Handle Null Cases**: Always check if strategy is set before using

---

## Bonus Challenges

After completing the main problems, try these additional challenges:

1. **Strategy Factory**: Create a factory that selects strategies based on context
2. **Strategy Chain**: Implement chain of responsibility with strategies
3. **Composite Strategies**: Combine multiple strategies into one
4. **Strategy Cache**: Cache strategy results for performance
5. **Async Strategies**: Implement asynchronous strategy execution
6. **Strategy Analytics**: Track strategy usage and performance metrics
7. **Strategy Versioning**: Support multiple versions of strategies
8. **Strategy Configuration**: Load strategies from configuration files

---

## Resources for Learning

- Design Patterns: Elements of Reusable Object-Oriented Software (Gang of Four)
- Head First Design Patterns
- Refactoring Guru Strategy Pattern
- Source Making Strategy Pattern

Good luck with your practice! Remember, mastering design patterns takes time and practice. Start with simpler problems and gradually move to more complex ones.