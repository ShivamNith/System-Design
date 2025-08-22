# SOLID Principles - Practice Problems

## Overview
This document contains 40 practice problems (8 per principle) of varying difficulty levels to help you master the SOLID Design Principles. Each problem builds upon core concepts and introduces real-world scenarios.

## Single Responsibility Principle (SRP) Problems

### Problem 1: Blog Management System (Beginner)
**Difficulty**: ⭐ Beginner

**Description**: 
You have a `BlogPost` class that handles multiple responsibilities:
```java
public class BlogPost {
    private String title;
    private String content;
    private Date publishDate;
    
    public void saveToDatabase() { }
    public void sendEmailToSubscribers() { }
    public String formatForDisplay() { }
    public boolean validateContent() { }
    public void generateSEOTags() { }
    public void moderateComments() { }
    public void calculateReadingTime() { }
}
```

**Task**: Refactor this class following SRP.

**Requirements**:
- Separate each responsibility into its own class
- Define clear interfaces for each responsibility
- Ensure each class has only one reason to change
- Maintain loose coupling between components

**Expected Classes**:
- `BlogPost` (domain model)
- `BlogRepository` (persistence)
- `NotificationService` (email)
- `BlogFormatter` (display)
- `ContentValidator` (validation)
- `SEOService` (SEO)
- `CommentModerator` (comments)
- `ReadingTimeCalculator` (metrics)

---

### Problem 2: Invoice Processing System (Beginner-Intermediate)
**Difficulty**: ⭐⭐ Beginner-Intermediate

**Description**:
Design an invoice processing system that handles invoice creation, calculation, persistence, and reporting.

**Current Monolithic Class**:
```java
public class Invoice {
    public void createInvoice() { }
    public void calculateTax() { }
    public void calculateDiscount() { }
    public void saveToDatabase() { }
    public void sendEmail() { }
    public void generatePDF() { }
    public void validateInvoice() { }
    public void applyLateFees() { }
}
```

**Requirements**:
- Separate financial calculations from business logic
- Isolate external dependencies (DB, email, PDF)
- Create testable components
- Support different tax strategies

**Deliverables**:
- Class diagram showing separated responsibilities
- Implementation of core classes
- Unit tests for each component

---

### Problem 3: User Authentication System (Intermediate)
**Difficulty**: ⭐⭐⭐ Intermediate

**Description**:
Refactor a user authentication system that currently violates SRP by handling authentication, authorization, session management, password encryption, and audit logging in a single class.

**Requirements**:
- Separate authentication from authorization
- Extract session management
- Isolate cryptographic operations
- Separate audit logging
- Support multiple authentication methods (password, OAuth, biometric)

**Challenges**:
- Maintain security while separating concerns
- Handle cross-cutting concerns like logging
- Support pluggable authentication providers

---

### Problem 4: E-commerce Product Catalog (Intermediate)
**Difficulty**: ⭐⭐⭐ Intermediate

**Description**:
Design a product catalog system where each component has a single responsibility.

**Responsibilities to Separate**:
- Product data management
- Inventory tracking
- Pricing calculations
- Search and filtering
- Image processing
- Review management
- Recommendation engine
- Import/Export functionality

**Requirements**:
- Each service should be independently deployable
- Support horizontal scaling
- Maintain data consistency
- Provide clear API boundaries

---

### Problem 5: Banking Transaction Processor (Intermediate-Advanced)
**Difficulty**: ⭐⭐⭐⭐ Intermediate-Advanced

**Description**:
Create a transaction processing system for a bank that properly separates concerns.

**Complex Requirements**:
- Transaction validation and execution
- Balance calculations and updates
- Fraud detection
- Regulatory compliance checking
- Transaction history and reporting
- Notification system
- Audit trail
- Currency conversion

**Constraints**:
- Must handle concurrent transactions
- Maintain ACID properties
- Support rollback mechanisms
- Comply with banking regulations

---

### Problem 6: Healthcare Patient Records (Advanced)
**Difficulty**: ⭐⭐⭐⭐ Advanced

**Description**:
Design a patient records management system following SRP while handling complex medical data.

**Responsibilities**:
- Patient demographic management
- Medical history tracking
- Prescription management
- Lab results processing
- Insurance claim processing
- Appointment scheduling
- HIPAA compliance
- Medical imaging storage
- Clinical decision support

**Challenges**:
- Maintain data privacy
- Support interoperability standards (HL7, FHIR)
- Handle large medical files
- Ensure audit compliance

---

### Problem 7: Real-time Analytics Engine (Advanced)
**Difficulty**: ⭐⭐⭐⭐⭐ Advanced

**Description**:
Build a real-time analytics engine with properly separated components.

**Components to Design**:
- Data ingestion service
- Stream processing engine
- Aggregation service
- Storage manager
- Query engine
- Visualization service
- Alert manager
- Data retention service

**Performance Requirements**:
- Process 1M events/second
- Sub-second query response
- Support multiple data sources
- Handle late-arriving data

---

### Problem 8: Microservices Migration (Expert)
**Difficulty**: ⭐⭐⭐⭐⭐ Expert

**Description**:
Migrate a monolithic application to microservices using SRP as the guiding principle.

**Monolith Components**:
- User management
- Order processing
- Inventory management
- Payment processing
- Shipping coordination
- Customer support
- Analytics
- Reporting

**Requirements**:
- Define service boundaries using SRP
- Design inter-service communication
- Handle distributed transactions
- Implement service discovery
- Manage shared data
- Design for failure

**Deliverables**:
- Service decomposition strategy
- API contracts
- Data migration plan
- Deployment architecture

---

## Open/Closed Principle (OCP) Problems

### Problem 9: Discount System (Beginner)
**Difficulty**: ⭐ Beginner

**Description**:
Design a discount system that can accommodate new discount types without modifying existing code.

**Initial Discount Types**:
- Percentage discount
- Fixed amount discount
- Buy-one-get-one

**Requirements**:
- Add seasonal discounts without changing core logic
- Support compound discounts
- Add new discount types easily

**Test Scenarios**:
- Add loyalty program discount
- Add time-based flash sales
- Add quantity-based discounts

---

### Problem 10: Report Generator (Beginner-Intermediate)
**Difficulty**: ⭐⭐ Beginner-Intermediate

**Description**:
Create a report generation system that supports multiple output formats.

**Initial Formats**:
- PDF
- Excel
- CSV

**Requirements**:
- Add new formats (HTML, XML, JSON) without modifying existing code
- Support custom formatting per report type
- Handle different data sources

**Extension Points**:
- Report formatters
- Data processors
- Export handlers

---

### Problem 11: Notification System (Intermediate)
**Difficulty**: ⭐⭐⭐ Intermediate

**Description**:
Build a notification system that can send alerts through various channels.

**Requirements**:
- Support Email, SMS, Push notifications initially
- Add Slack, Teams, Discord without modification
- Support notification templates
- Handle delivery failures gracefully
- Implement retry mechanisms

**Advanced Features**:
- Priority-based routing
- User preference management
- Delivery tracking

---

### Problem 12: Payment Gateway Integration (Intermediate)
**Difficulty**: ⭐⭐⭐ Intermediate

**Description**:
Design a payment processing system that integrates with multiple payment gateways.

**Initial Gateways**:
- Stripe
- PayPal
- Square

**Requirements**:
- Add new gateways without modifying processor
- Support different payment methods per gateway
- Handle gateway-specific validations
- Implement fallback mechanisms

**Challenges**:
- Different API structures
- Various authentication methods
- Gateway-specific features

---

### Problem 13: Rule Engine (Intermediate-Advanced)
**Difficulty**: ⭐⭐⭐⭐ Intermediate-Advanced

**Description**:
Create a business rule engine that evaluates conditions and executes actions.

**Requirements**:
- Support adding new rule types dynamically
- Combine rules with AND/OR logic
- Support rule priorities
- Enable rule versioning
- Provide rule testing framework

**Rule Categories**:
- Validation rules
- Calculation rules
- Workflow rules
- Authorization rules

---

### Problem 14: Data Pipeline (Advanced)
**Difficulty**: ⭐⭐⭐⭐ Advanced

**Description**:
Design a data processing pipeline that can accommodate new transformation steps.

**Pipeline Stages**:
- Data extraction
- Validation
- Transformation
- Enrichment
- Loading

**Requirements**:
- Add new transformers without pipeline modification
- Support parallel processing
- Handle different data formats
- Implement error recovery

---

### Problem 15: Plugin Architecture (Advanced)
**Difficulty**: ⭐⭐⭐⭐⭐ Advanced

**Description**:
Build a plugin system for an IDE that follows OCP.

**Core Features**:
- Syntax highlighting
- Code completion
- Debugging

**Plugin Requirements**:
- Language support plugins
- Theme plugins
- Tool integration plugins
- Custom commands

**Challenges**:
- Plugin discovery and loading
- Version compatibility
- Plugin dependencies
- Security sandboxing

---

### Problem 16: API Gateway (Expert)
**Difficulty**: ⭐⭐⭐⭐⭐ Expert

**Description**:
Implement an API gateway that can be extended with new features.

**Extensible Features**:
- Authentication strategies
- Rate limiting algorithms
- Request/response transformers
- Caching strategies
- Routing rules
- Load balancing algorithms

**Requirements**:
- Zero-downtime feature addition
- Performance optimization
- Multi-tenancy support
- Monitoring and metrics

---

## Liskov Substitution Principle (LSP) Problems

### Problem 17: Vehicle Hierarchy (Beginner)
**Difficulty**: ⭐ Beginner

**Description**:
Design a vehicle hierarchy that properly follows LSP.

**Challenge**:
```java
class Vehicle {
    void startEngine() { }
    void accelerate() { }
}
class ElectricCar extends Vehicle {
    void startEngine() { 
        // No engine to start! 
    }
}
```

**Requirements**:
- Handle vehicles with and without engines
- Support different propulsion methods
- Maintain substitutability

---

### Problem 18: Employee Types (Beginner-Intermediate)
**Difficulty**: ⭐⭐ Beginner-Intermediate

**Description**:
Create an employee hierarchy for different employment types.

**Employee Types**:
- Full-time employees
- Part-time employees
- Contractors
- Interns
- Volunteers

**Challenges**:
- Different compensation structures
- Various benefit eligibilities
- Different working hour constraints

---

### Problem 19: File System Operations (Intermediate)
**Difficulty**: ⭐⭐⭐ Intermediate

**Description**:
Design file system components that respect LSP.

**Components**:
- Files
- Directories
- Symbolic links
- Special files (devices, pipes)

**Requirements**:
- All items should be listable
- Support appropriate operations per type
- Handle permissions correctly
- Maintain filesystem integrity

---

### Problem 20: Account Types (Intermediate)
**Difficulty**: ⭐⭐⭐ Intermediate

**Description**:
Design a banking account hierarchy following LSP.

**Account Types**:
- Savings Account
- Checking Account
- Fixed Deposit
- Credit Card Account
- Loan Account

**Challenges**:
- Different withdrawal rules
- Various interest calculations
- Different transaction limits
- Overdraft handling

---

### Problem 21: Data Storage Abstraction (Intermediate-Advanced)
**Difficulty**: ⭐⭐⭐⭐ Intermediate-Advanced

**Description**:
Create a data storage abstraction that works with different storage backends.

**Storage Types**:
- File system
- Database (SQL)
- NoSQL stores
- In-memory cache
- Cloud storage

**Requirements**:
- Consistent API across all storage types
- Handle storage-specific limitations
- Support transactions where applicable
- Maintain data consistency

---

### Problem 22: UI Component Library (Advanced)
**Difficulty**: ⭐⭐⭐⭐ Advanced

**Description**:
Design a UI component hierarchy that respects LSP.

**Components**:
- Buttons
- Input fields
- Dropdowns
- Modals
- Tables
- Charts

**Challenges**:
- Different event handling
- Various validation requirements
- Accessibility compliance
- Responsive behavior

---

### Problem 23: Stream Processing (Advanced)
**Difficulty**: ⭐⭐⭐⭐⭐ Advanced

**Description**:
Implement stream processing components following LSP.

**Stream Types**:
- Input streams
- Output streams
- Bidirectional streams
- Buffered streams
- Compressed streams

**Requirements**:
- Maintain stream contracts
- Handle different data rates
- Support chaining
- Ensure proper resource cleanup

---

### Problem 24: Distributed System Nodes (Expert)
**Difficulty**: ⭐⭐⭐⭐⭐ Expert

**Description**:
Design a distributed system with different node types that follow LSP.

**Node Types**:
- Master nodes
- Worker nodes
- Storage nodes
- Coordinator nodes
- Gateway nodes

**Requirements**:
- Nodes must be interchangeable where appropriate
- Handle node failures gracefully
- Support dynamic role assignment
- Maintain cluster consistency

---

## Interface Segregation Principle (ISP) Problems

### Problem 25: Smart Home Devices (Beginner)
**Difficulty**: ⭐ Beginner

**Description**:
Design interfaces for smart home devices avoiding interface pollution.

**Devices**:
- Smart lights
- Thermostats
- Security cameras
- Smart locks
- Smart speakers

**Requirements**:
- Avoid forcing devices to implement unnecessary methods
- Support device-specific features
- Enable device composition

---

### Problem 26: Media Player (Beginner-Intermediate)
**Difficulty**: ⭐⭐ Beginner-Intermediate

**Description**:
Create a media player system with properly segregated interfaces.

**Media Types**:
- Audio files
- Video files
- Streaming content
- Podcasts
- Live broadcasts

**Features to Segregate**:
- Playback control
- Playlist management
- Subtitle support
- Quality adjustment
- Download capability

---

### Problem 27: Restaurant Management (Intermediate)
**Difficulty**: ⭐⭐⭐ Intermediate

**Description**:
Design a restaurant management system with role-specific interfaces.

**Roles**:
- Waiter
- Chef
- Manager
- Cashier
- Delivery driver

**Requirements**:
- Each role sees only relevant operations
- Support multi-role employees
- Maintain security boundaries

---

### Problem 28: CRM System (Intermediate)
**Difficulty**: ⭐⭐⭐ Intermediate

**Description**:
Build a CRM with segregated interfaces for different modules.

**Modules**:
- Contact management
- Sales pipeline
- Marketing automation
- Customer support
- Analytics

**Challenges**:
- Avoid monolithic interfaces
- Support module independence
- Enable selective feature access

---

### Problem 29: Game Engine Components (Intermediate-Advanced)
**Difficulty**: ⭐⭐⭐⭐ Intermediate-Advanced

**Description**:
Design game engine components with focused interfaces.

**Components**:
- Renderable objects
- Physics bodies
- Audio sources
- AI agents
- Network entities

**Requirements**:
- Support component composition
- Avoid unnecessary dependencies
- Enable performance optimization
- Support different game genres

---

### Problem 30: IoT Device Management (Advanced)
**Difficulty**: ⭐⭐⭐⭐ Advanced

**Description**:
Create an IoT platform with properly segregated device interfaces.

**Device Categories**:
- Sensors
- Actuators
- Gateways
- Edge processors
- Controllers

**Capabilities to Segregate**:
- Data collection
- Command execution
- Configuration management
- Firmware updates
- Diagnostics

---

### Problem 31: Middleware Framework (Advanced)
**Difficulty**: ⭐⭐⭐⭐⭐ Advanced

**Description**:
Build a middleware framework with segregated interfaces.

**Middleware Types**:
- Authentication
- Logging
- Caching
- Rate limiting
- Compression
- CORS handling

**Requirements**:
- Support middleware chaining
- Enable selective application
- Maintain performance
- Support async operations

---

### Problem 32: Multi-Protocol Server (Expert)
**Difficulty**: ⭐⭐⭐⭐⭐ Expert

**Description**:
Implement a server supporting multiple protocols with segregated interfaces.

**Protocols**:
- HTTP/HTTPS
- WebSocket
- gRPC
- GraphQL
- MQTT

**Challenges**:
- Protocol-specific features
- Connection management
- Security requirements
- Performance optimization

---

## Dependency Inversion Principle (DIP) Problems

### Problem 33: Logging Framework (Beginner)
**Difficulty**: ⭐ Beginner

**Description**:
Create a logging framework that doesn't depend on specific implementations.

**Requirements**:
- Support file, console, database logging
- Allow log level configuration
- Support custom formatters
- Enable runtime logger switching

---

### Problem 34: Cache System (Beginner-Intermediate)
**Difficulty**: ⭐⭐ Beginner-Intermediate

**Description**:
Design a caching system following DIP.

**Cache Implementations**:
- In-memory cache
- Redis cache
- File-based cache
- Distributed cache

**Requirements**:
- High-level modules shouldn't depend on cache specifics
- Support cache strategies (LRU, LFU, TTL)
- Handle cache misses gracefully

---

### Problem 35: Email Service (Intermediate)
**Difficulty**: ⭐⭐⭐ Intermediate

**Description**:
Build an email service that follows DIP.

**Email Providers**:
- SMTP
- SendGrid
- AWS SES
- Mailgun

**Requirements**:
- Business logic independent of email provider
- Support provider switching
- Handle delivery failures
- Support templates

---

### Problem 36: Data Access Layer (Intermediate)
**Difficulty**: ⭐⭐⭐ Intermediate

**Description**:
Create a data access layer using DIP.

**Data Sources**:
- SQL databases
- NoSQL stores
- REST APIs
- GraphQL endpoints
- File systems

**Requirements**:
- Repository pattern implementation
- Unit of work pattern
- Support transactions
- Enable testing with mocks

---

### Problem 37: Authentication System (Intermediate-Advanced)
**Difficulty**: ⭐⭐⭐⭐ Intermediate-Advanced

**Description**:
Design an authentication system following DIP.

**Auth Methods**:
- Username/password
- OAuth 2.0
- SAML
- JWT
- Biometric

**Requirements**:
- Business logic independent of auth method
- Support multiple providers
- Enable provider chaining
- Maintain security

---

### Problem 38: Message Queue Integration (Advanced)
**Difficulty**: ⭐⭐⭐⭐ Advanced

**Description**:
Implement message queue abstraction following DIP.

**Queue Systems**:
- RabbitMQ
- Kafka
- AWS SQS
- Azure Service Bus
- Redis Pub/Sub

**Requirements**:
- Abstract message publishing/consuming
- Support different message patterns
- Handle connection failures
- Enable message transformation

---

### Problem 39: Monitoring System (Advanced)
**Difficulty**: ⭐⭐⭐⭐⭐ Advanced

**Description**:
Build a monitoring system using DIP.

**Monitoring Targets**:
- Application metrics
- System resources
- Network traffic
- Database performance
- External services

**Requirements**:
- Decouple collection from storage
- Support multiple metric types
- Enable real-time alerting
- Support various visualization tools

---

### Problem 40: Service Mesh Implementation (Expert)
**Difficulty**: ⭐⭐⭐⭐⭐ Expert

**Description**:
Design a service mesh that follows DIP principles.

**Components**:
- Service discovery
- Load balancing
- Circuit breaking
- Request routing
- Security policies

**Requirements**:
- Services independent of mesh implementation
- Support multiple protocols
- Enable policy injection
- Maintain observability

---

## Evaluation Criteria for All Problems

### Code Quality ✅
- Proper application of SOLID principle
- Clean, readable code
- Appropriate naming conventions
- Comprehensive documentation

### Design Quality 🏗️
- Clear separation of concerns
- Proper abstraction levels
- Minimal coupling
- High cohesion

### Testing 🧪
- Unit test coverage
- Integration tests where applicable
- Test demonstrates principle benefits
- Mock usage for dependencies

### Extensibility 🔧
- Easy to add new features
- Minimal code changes for extensions
- Clear extension points
- Backward compatibility

### Performance 🚀
- Efficient implementations
- Appropriate data structures
- Optimization where necessary
- Scalability considerations

## Study Tips

### For Beginners:
1. Start with simple examples
2. Focus on understanding why the principle matters
3. Practice identifying violations in existing code
4. Write tests to verify your implementations

### For Intermediate:
1. Combine multiple principles in solutions
2. Consider real-world constraints
3. Practice refactoring legacy code
4. Study open-source implementations

### For Advanced:
1. Focus on system-level design
2. Consider distributed system challenges
3. Optimize for performance while maintaining principles
4. Mentor others on SOLID principles

## Common Pitfalls to Avoid

### SRP Pitfalls:
- Creating too many tiny classes
- Confusing SRP with "do one thing"
- Ignoring cohesion in pursuit of separation

### OCP Pitfalls:
- Over-engineering for unlikely changes
- Creating unnecessary abstractions
- Violating YAGNI (You Aren't Gonna Need It)

### LSP Pitfalls:
- Forcing inheritance where composition is better
- Ignoring behavioral compatibility
- Weakening postconditions in derived classes

### ISP Pitfalls:
- Creating too many small interfaces
- Losing cohesion in interface design
- Ignoring interface evolution needs

### DIP Pitfalls:
- Creating abstractions for everything
- Ignoring framework constraints
- Over-complicating simple scenarios

## Additional Challenges

### Challenge 1: SOLID Analyzer
Build a tool that analyzes code for SOLID violations.

### Challenge 2: Refactoring Kata
Take a legacy codebase and refactor it to follow SOLID principles.

### Challenge 3: Design Review
Review and critique designs for SOLID compliance.

### Challenge 4: Framework Design
Design a small framework that exemplifies all SOLID principles.

### Challenge 5: Teaching Materials
Create examples and exercises to teach SOLID to others.

## Resources for Further Learning

1. **Books**:
   - "Clean Code" by Robert C. Martin
   - "Design Patterns" by Gang of Four
   - "Refactoring" by Martin Fowler

2. **Online Courses**:
   - SOLID principles courses on Pluralsight
   - Design patterns courses on Udemy
   - Architecture courses on Coursera

3. **Practice Platforms**:
   - Exercism.io
   - CodeKata
   - Refactoring.guru

4. **Open Source Projects**:
   - Study Spring Framework (Java)
   - Analyze .NET Core (C#)
   - Review Express.js (JavaScript)

Remember: SOLID principles are guidelines, not rules. Apply them pragmatically based on your specific context and requirements.