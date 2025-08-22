# Builder Pattern Practice Problems

## Overview
These practice problems will help you master the Builder Pattern by implementing real-world scenarios. Start with the basic problems and progress to more complex challenges.

---

## Problem 1: Pizza Builder (Basic)
**Difficulty:** ⭐⭐☆☆☆

### Problem Statement
Create a pizza ordering system where customers can build custom pizzas with various toppings and configurations:
- **Size**: Small, Medium, Large, Extra Large
- **Crust**: Thin, Thick, Stuffed, Gluten-free
- **Base Sauce**: Tomato, White, BBQ, Pesto, No sauce
- **Cheese**: Mozzarella, Cheddar, Parmesan, Vegan, No cheese
- **Toppings**: Pepperoni, Mushrooms, Peppers, Onions, Olives, Sausage, Ham, Pineapple, etc.
- **Special Instructions**: Extra crispy, Well done, Light sauce, etc.

### Requirements
1. Create a `Pizza` class with all possible configurations
2. Implement a Builder with fluent interface
3. Include validation (e.g., vegan cheese with meat toppings warning)
4. Add preset methods for popular combinations (Margherita, Hawaiian, Supreme)
5. Calculate price based on size, crust, and toppings
6. Generate receipt with itemized costs

### Test Cases
```
Small Margherita Pizza:
- Size: Small, Crust: Thin, Sauce: Tomato, Cheese: Mozzarella, Toppings: Basil
- Price: $12.99

Large Supreme Pizza:
- Size: Large, Crust: Thick, Sauce: Tomato, Cheese: Mozzarella
- Toppings: Pepperoni, Sausage, Mushrooms, Peppers, Onions
- Price: $24.99
```

### Hints
- Consider dietary restrictions (vegetarian, vegan, gluten-free)
- Implement size-based pricing multipliers
- Add nutritional information calculation
- Include cooking time estimation

---

## Problem 2: Resume Builder (Intermediate)
**Difficulty:** ⭐⭐⭐☆☆

### Problem Statement
Design a resume/CV builder system that allows users to create professional resumes with different sections and formats:
- **Personal Info**: Name, contact details, profile photo, social links
- **Professional Summary**: Brief career overview
- **Work Experience**: Job title, company, dates, responsibilities, achievements
- **Education**: Degree, institution, graduation date, GPA, honors
- **Skills**: Technical skills, languages, proficiency levels
- **Projects**: Project name, description, technologies, links
- **Certifications**: Certification name, issuing organization, date, expiry
- **Awards**: Award name, organization, date, description
- **References**: Name, position, company, contact information

### Requirements
1. Create `Resume` class with all sections as optional
2. Implement nested builders for complex sections (WorkExperience, Education, etc.)
3. Support multiple resume formats (Chronological, Functional, Combination)
4. Add validation rules (e.g., graduation date before work start date)
5. Include different templates (Modern, Classic, Creative, ATS-friendly)
6. Generate resume in multiple formats (HTML, PDF, plain text)
7. Calculate resume completeness score

### Advanced Features
- Auto-suggest skills based on job titles
- Industry-specific templates
- Multiple language support
- Privacy settings for contact information
- Resume analytics (views, downloads)
- ATS (Applicant Tracking System) optimization

### Test Scenarios
```
Software Developer Resume:
- 3 work experiences, 2 education entries, 15 technical skills
- 4 projects with GitHub links
- 2 certifications (AWS, Google Cloud)
- ATS-friendly format
- Completeness score: 95%
```

---

## Problem 3: Investment Portfolio Builder (Intermediate)
**Difficulty:** ⭐⭐⭐☆☆

### Problem Statement
Create an investment portfolio construction system with different asset allocations, risk profiles, and investment strategies:
- **Investor Profile**: Age, risk tolerance, investment horizon, income, goals
- **Asset Classes**: Stocks, Bonds, ETFs, Mutual Funds, REITs, Commodities, Crypto
- **Geographic Distribution**: Domestic, International, Emerging markets
- **Sector Allocation**: Technology, Healthcare, Finance, Energy, etc.
- **Investment Style**: Growth, Value, Blend, Income-focused
- **Rebalancing Rules**: Frequency, thresholds, methods

### Requirements
1. Create `Portfolio` class with asset allocations
2. Implement risk assessment and portfolio optimization
3. Add preset strategies (Conservative, Moderate, Aggressive, Retirement)
4. Include diversification rules and constraints
5. Calculate expected returns, volatility, and Sharpe ratio
6. Support tax-advantaged accounts (401k, IRA, Roth IRA)
7. Generate portfolio performance reports

### Complex Features
- Monte Carlo simulations for portfolio projections
- Tax-loss harvesting strategies
- Dollar-cost averaging implementation
- ESG (Environmental, Social, Governance) screening
- Currency hedging options
- Automatic rebalancing algorithms

---

## Problem 4: Game Character Creator (Advanced)
**Difficulty:** ⭐⭐⭐⭐☆

### Problem Statement
Design a comprehensive character creation system for an RPG game with multiple races, classes, attributes, skills, and equipment:
- **Basic Info**: Name, race, class, gender, appearance
- **Attributes**: Strength, Dexterity, Intelligence, Wisdom, Constitution, Charisma
- **Skills**: Combat skills, magic schools, crafting abilities, social skills
- **Background**: Origin story, starting equipment, relationships
- **Appearance**: Height, weight, hair/eye color, distinctive features
- **Equipment**: Weapons, armor, accessories, consumables
- **Spells**: Known spells, spell slots, spell components

### Requirements
1. Create `Character` class with all attributes and complex relationships
2. Implement race and class-specific bonuses and restrictions
3. Add attribute point allocation with limits and dependencies
4. Include equipment compatibility checking (class restrictions, requirements)
5. Support multi-classing with experience point distribution
6. Generate character backstory using templates
7. Calculate combat statistics and derived attributes
8. Implement character advancement/leveling system

### Advanced Implementation
- Skill trees with prerequisites
- Equipment set bonuses
- Character relationship systems
- Alignment system affecting available actions
- Random character generation
- Character export/import functionality
- 3D model integration for appearance
- Voice and personality trait selection

### Game Mechanics Integration
```
Wizard Character:
- Race: Elf (+2 Intelligence, +1 Dexterity)
- Class: Wizard (Primary: Intelligence, HP: d6 per level)
- Starting spells: 6 cantrips, 2 first-level spells
- Equipment: Spellbook, component pouch, dagger, light crossbow
- Skills: Arcana, Investigation, History, Insight
```

---

## Problem 5: Smart Home Configuration Builder (Advanced)
**Difficulty:** ⭐⭐⭐⭐☆

### Problem Statement
Create a smart home automation system builder that allows users to configure devices, rooms, scenes, and automation rules:
- **Home Layout**: Rooms, zones, device locations
- **Devices**: Smart lights, thermostats, locks, cameras, sensors, speakers
- **Device Groups**: Logical groupings for control
- **Scenes**: Predefined device states (Movie night, Good morning, Away)
- **Automations**: Triggers, conditions, actions
- **Security Settings**: Access controls, notifications, alerts
- **Energy Management**: Usage monitoring, optimization rules

### Requirements
1. Create `SmartHome` class with hierarchical structure
2. Implement device compatibility and communication protocols
3. Add scene creation with device state validation
4. Include complex automation rules with multiple triggers/conditions
5. Support time-based, sensor-based, and location-based automations
6. Generate energy usage reports and optimization suggestions
7. Handle device failures and fallback configurations

### Complex Features
- Machine learning for usage pattern detection
- Voice command integration
- Geofencing for location-based automation
- Integration with weather services
- Load balancing for power management
- Security event correlation
- Mobile app configuration export

### System Architecture
```
Smart Home Configuration:
- 3 zones: Living area, bedrooms, outdoor
- 25 devices: 12 lights, 3 thermostats, 4 sensors, 6 others
- 8 scenes: Morning, Evening, Party, Vacation, Sleep, etc.
- 15 automation rules with complex triggers
- Energy optimization: 15% reduction target
```

---

## Problem 6: API Request Builder (Advanced)
**Difficulty:** ⭐⭐⭐⭐☆

### Problem Statement
Design a comprehensive HTTP API request builder for testing and integration purposes:
- **Request Configuration**: Method, URL, headers, body, parameters
- **Authentication**: Bearer token, Basic auth, API key, OAuth, custom
- **Request Body**: JSON, XML, form data, multipart, raw, GraphQL
- **Response Handling**: Expected status codes, response validation, error handling
- **Testing Features**: Assertions, test data generation, response extraction
- **Performance**: Timeout settings, retry logic, rate limiting
- **Documentation**: Auto-generated documentation, examples

### Requirements
1. Create flexible `APIRequest` class supporting all HTTP methods
2. Implement multiple authentication mechanisms
3. Add request/response validation and transformation
4. Include test assertion framework
5. Support request chaining and dependency injection
6. Generate comprehensive test reports
7. Handle different content types and serialization

### Advanced Features
- Mock server integration
- Load testing capabilities
- Response caching mechanisms
- Request/response middleware
- Environment-specific configurations
- CI/CD pipeline integration
- Performance metrics collection

---

## Problem 7: Database Schema Builder (Expert)
**Difficulty:** ⭐⭐⭐⭐⭐

### Problem Statement
Create a database schema design system that can generate DDL (Data Definition Language) for multiple database platforms:
- **Tables**: Columns, data types, constraints, indexes
- **Relationships**: Foreign keys, one-to-many, many-to-many
- **Constraints**: Primary keys, unique constraints, check constraints
- **Indexes**: Single column, composite, partial, unique
- **Views**: Complex queries, materialized views
- **Stored Procedures**: Parameters, return types, logic
- **Triggers**: Event types, timing, conditions

### Requirements
1. Create `DatabaseSchema` class with all database objects
2. Implement cross-platform SQL generation (MySQL, PostgreSQL, SQLServer, Oracle)
3. Add schema validation and constraint checking
4. Include relationship integrity validation
5. Support schema versioning and migration generation
6. Generate optimization recommendations
7. Export schema documentation

### Expert-Level Features
- Automatic relationship inference
- Data type optimization recommendations
- Performance impact analysis
- Schema comparison and diff generation
- Reverse engineering from existing databases
- Data modeling best practices validation
- Distributed database partitioning strategies

---

## Problem 8: Cloud Infrastructure Builder (Expert)
**Difficulty:** ⭐⭐⭐⭐⭐

### Problem Statement
Design a cloud infrastructure configuration builder that generates Infrastructure as Code (IaC) for multiple cloud providers:
- **Compute Resources**: Virtual machines, containers, serverless functions
- **Storage**: Block storage, object storage, databases, data warehouses
- **Networking**: VPCs, subnets, load balancers, CDNs, firewalls
- **Security**: IAM roles, policies, encryption, certificates
- **Monitoring**: Logging, metrics, alerts, dashboards
- **Deployment**: CI/CD pipelines, blue-green deployments, auto-scaling

### Requirements
1. Create `CloudInfrastructure` class with modular components
2. Implement multi-cloud support (AWS, Azure, Google Cloud)
3. Add cost estimation and optimization
4. Include security best practices validation
5. Support infrastructure testing and validation
6. Generate Terraform, CloudFormation, or ARM templates
7. Provide disaster recovery and backup strategies

### Enterprise Features
- Compliance frameworks integration (SOC2, HIPAA, PCI)
- Multi-region deployment strategies
- Resource tagging and governance policies
- Cost allocation and chargeback models
- Infrastructure drift detection
- Automated security scanning
- Performance optimization recommendations

### Infrastructure Example
```
Web Application Infrastructure:
- Load balancer → Web servers (auto-scaling group)
- Application servers → Database cluster (read replicas)
- Redis cache → File storage (S3/Blob)
- Monitoring stack → Log aggregation
- Backup strategy → Disaster recovery setup
```

---

## Solution Structure Template

For each problem, structure your solution as follows:

```java
// Product Class
public class Product {
    private final Type field1;
    private final Type field2;
    // ... more fields
    
    private Product(Builder builder) {
        this.field1 = builder.field1;
        this.field2 = builder.field2;
        // ... set all fields
        
        validate(); // Validate the built object
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    private void validate() {
        // Validation logic
    }
    
    public static class Builder {
        private Type field1;
        private Type field2;
        // ... builder fields
        
        public Builder field1(Type value) {
            this.field1 = value;
            return this;
        }
        
        public Builder field2(Type value) {
            this.field2 = value;
            return this;
        }
        
        // Convenience methods
        public Builder presetConfiguration() {
            // Set multiple fields at once
            return this;
        }
        
        public Product build() {
            return new Product(this);
        }
    }
    
    // Getters, toString, equals, hashCode
}

// Director (optional)
public class ProductDirector {
    public Product createCommonConfiguration() {
        return Product.builder()
                // ... configuration
                .build();
    }
}

// Demonstration class
public class BuilderExample {
    public static void main(String[] args) {
        // Demonstrate various builder usages
    }
}
```

---

## Evaluation Criteria

Your solutions will be evaluated based on:

1. **Correct Implementation** (35%)
   - Proper use of Builder Pattern
   - Immutable product objects
   - Fluent interface implementation
   - Working validation logic

2. **Code Quality** (25%)
   - Clean, readable code
   - Proper naming conventions
   - SOLID principles adherence
   - Appropriate error handling

3. **Complexity Handling** (20%)
   - Nested builders for complex objects
   - Optional vs required parameters
   - Default value management
   - Complex validation rules

4. **Flexibility & Extensibility** (10%)
   - Easy to add new fields/options
   - Support for presets and templates
   - Multiple output formats
   - Customization options

5. **Testing & Documentation** (10%)
   - Unit tests for builders
   - Edge case handling
   - Clear documentation
   - Usage examples

---

## Tips for Success

1. **Start with Simple Builders**: Begin with basic required/optional parameters
2. **Add Validation**: Validate both individual fields and object state
3. **Use Fluent Interface**: Return builder instance from setter methods
4. **Implement Presets**: Provide common configuration methods
5. **Handle Defaults**: Set sensible defaults for optional parameters
6. **Consider Immutability**: Make built objects immutable
7. **Think About Relationships**: Handle complex object relationships properly
8. **Plan for Extension**: Design builders to be easily extensible
9. **Test Thoroughly**: Test normal usage, edge cases, and error conditions
10. **Document Well**: Provide clear examples and usage documentation

---

## Advanced Challenges

After completing the main problems, try these additional challenges:

1. **Generic Builder Framework**: Create a framework for generating builders
2. **Builder Inheritance**: Implement builder inheritance hierarchies
3. **Conditional Building**: Implement conditional field setting based on other fields
4. **Builder Validation Framework**: Create reusable validation system
5. **Template-Based Building**: Use templates to generate common configurations
6. **Performance Optimization**: Optimize builders for high-frequency usage
7. **Serialization Support**: Add JSON/XML serialization to built objects
8. **Builder Analytics**: Track how builders are used and optimize accordingly

---

## Common Pitfalls to Avoid

1. **Mutable Products**: Don't make the built object mutable after creation
2. **Missing Validation**: Always validate the final object state
3. **Breaking Fluent Interface**: Don't forget to return `this` from builder methods
4. **Complex Constructors**: Use builders instead of constructors with many parameters
5. **Shared Builder State**: Don't reuse builder instances unless intended
6. **Inconsistent Defaults**: Ensure default values are consistent and documented
7. **Memory Leaks**: Don't hold unnecessary references in builders
8. **Thread Safety**: Consider thread safety if builders will be shared

---

## Resources for Learning

- Effective Java by Joshua Bloch (Item 2: Consider a builder when faced with many constructor parameters)
- Design Patterns: Elements of Reusable Object-Oriented Software (Gang of Four)
- Head First Design Patterns
- Refactoring Guru Builder Pattern
- Building Maintainable Software (O'Reilly)

Good luck with your practice! The Builder pattern is particularly useful for creating complex objects with many optional parameters. Focus on creating readable, maintainable code that follows the single responsibility principle.