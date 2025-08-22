# Facade Pattern Practice Problems

This document contains 8 practice problems ranging from basic to expert level to help you master the Facade Pattern. Each problem includes requirements, hints, and implementation guidelines.

## Problem 1: Library Management System (Basic)

### Description
Create a facade for a library management system that simplifies book borrowing and returning operations.

### Requirements
- **Subsystems**: BookCatalog, MembershipManager, InventoryManager, FineCalculator
- **Facade Operations**: 
  - `borrowBook(memberId, bookId)`
  - `returnBook(memberId, bookId)`
  - `renewBook(memberId, bookId)`
  - `getMemberStatus(memberId)`

### Hints
- BookCatalog: manages book information and availability
- MembershipManager: validates member status and borrowing limits
- InventoryManager: tracks book inventory and reservations
- FineCalculator: calculates overdue fines

### Expected Output
```
=== BORROWING BOOK ===
Member 12345 requesting book B001
Checking member status... ACTIVE
Checking book availability... AVAILABLE
Updating inventory... Book B001 reserved
Recording transaction... Success
Book borrowed successfully!
```

---

## Problem 2: Smart Home Automation (Basic-Intermediate)

### Description
Design a smart home facade that controls various IoT devices through simple voice commands.

### Requirements
- **Subsystems**: LightingSystem, SecuritySystem, ClimateControl, EntertainmentSystem, ApplianceManager
- **Facade Operations**:
  - `activateScenario(scenarioName)` (e.g., "Good Morning", "Movie Night", "Sleep Mode")
  - `emergencyShutdown()`
  - `setAwayMode()`
  - `setHomeMode()`

### Scenarios to Implement
1. **Good Morning**: Turn on lights, adjust temperature, start coffee maker, play news
2. **Movie Night**: Dim lights, close blinds, turn on TV/sound system
3. **Sleep Mode**: Turn off all devices, activate security, set night temperature
4. **Away Mode**: Full security activation, turn off non-essential devices

### Hints
- Each subsystem should have multiple components
- Consider device dependencies (e.g., security system affects lighting)
- Add error handling for device failures

---

## Problem 3: Online Shopping Platform (Intermediate)

### Description
Create a facade for an e-commerce checkout process that coordinates multiple services.

### Requirements
- **Subsystems**: ProductCatalog, InventoryService, PaymentProcessor, ShippingCalculator, TaxCalculator, OrderManager, NotificationService
- **Facade Operations**:
  - `addToCart(productId, quantity)`
  - `removeFromCart(productId)`
  - `calculateTotal()`
  - `processCheckout(paymentInfo, shippingAddress)`
  - `trackOrder(orderId)`

### Complex Logic to Handle
- Inventory validation and reservation
- Tax calculation based on shipping address
- Multiple payment methods (credit card, PayPal, gift cards)
- Shipping options and cost calculation
- Order confirmation and tracking

### Hints
- Implement cart session management
- Handle payment failures gracefully
- Support partial payments and split orders
- Include inventory rollback on payment failure

---

## Problem 4: Hospital Management System (Intermediate)

### Description
Build a facade for hospital operations managing patient care, staff scheduling, and resource allocation.

### Requirements
- **Subsystems**: PatientRegistry, DoctorScheduler, RoomManager, EquipmentTracker, BillingSystem, InsuranceValidator, PharmacySystem
- **Facade Operations**:
  - `admitPatient(patientInfo, emergencyLevel)`
  - `scheduleAppointment(patientId, doctorId, appointmentType)`
  - `dischargePatient(patientId)`
  - `prescribeMedication(patientId, medication, dosage)`
  - `processInsuranceClaim(patientId, treatmentCodes)`

### Complex Requirements
- Emergency vs. scheduled admissions
- Room allocation based on patient needs
- Doctor availability and specialization matching
- Insurance pre-authorization
- Medication interaction checking

### Hints
- Implement priority queues for emergency cases
- Add validation for doctor-patient compatibility
- Include insurance coverage verification
- Handle medication conflicts and allergies

---

## Problem 5: Cloud Infrastructure Management (Intermediate-Advanced)

### Description
Design a facade for cloud infrastructure management that simplifies complex deployment and scaling operations.

### Requirements
- **Subsystems**: ComputeManager, StorageManager, NetworkManager, DatabaseManager, SecurityManager, MonitoringService, LoadBalancer
- **Facade Operations**:
  - `deployApplication(appConfig, environment)`
  - `scaleApplication(appId, instances)`
  - `createEnvironment(environmentConfig)`
  - `setupDisasterRecovery(appId, backupStrategy)`
  - `migrateApplication(appId, targetEnvironment)`

### Advanced Features
- Multi-region deployment
- Auto-scaling based on metrics
- Blue-green deployments
- Infrastructure as Code integration
- Cost optimization recommendations

### Hints
- Implement environment templates (dev, staging, prod)
- Add health checks and rollback capabilities
- Support multiple cloud providers
- Include cost tracking and alerts

---

## Problem 6: Game Engine Management (Advanced)

### Description
Create a facade for a game engine that manages complex game systems and provides simple APIs for game developers.

### Requirements
- **Subsystems**: RenderingEngine, PhysicsEngine, AudioManager, InputManager, SceneManager, AssetLoader, NetworkManager, AISystem
- **Facade Operations**:
  - `initializeGame(gameConfig)`
  - `loadLevel(levelId)`
  - `spawnEntity(entityType, position, properties)`
  - `handlePlayerAction(action, parameters)`
  - `saveGameState()`
  - `connectMultiplayer(serverAddress)`

### Complex Game Features
- Multi-threaded rendering and physics
- Asset streaming and memory management
- Real-time multiplayer synchronization
- AI behavior coordination
- Dynamic lighting and shadows

### Hints
- Implement component-entity-system architecture
- Add performance profiling and optimization
- Support hot-reloading of assets
- Include save/load state management
- Handle network latency and prediction

---

## Problem 7: Trading System Integration (Advanced)

### Description
Build a comprehensive trading system facade that integrates with multiple financial markets and provides unified trading operations.

### Requirements
- **Subsystems**: MarketDataProvider, OrderManager, RiskManager, PortfolioTracker, ComplianceChecker, SettlementManager, ReportingEngine, AlertSystem
- **Facade Operations**:
  - `placeOrder(symbol, orderType, quantity, price, strategy)`
  - `executeStrategy(strategyId, parameters)`
  - `managePortfolio(accountId, instructions)`
  - `generateRiskReport(portfolioId, timeframe)`
  - `processEndOfDay(tradingDate)`

### Financial Complexity
- Multiple asset classes (stocks, bonds, derivatives, crypto)
- Real-time market data processing
- Risk limit enforcement
- Regulatory compliance checking
- Settlement and clearing integration

### Hints
- Implement circuit breakers and risk controls
- Add market hours validation
- Support different order types (market, limit, stop)
- Include regulatory reporting
- Handle market volatility and halts

---

## Problem 8: Autonomous Vehicle Control System (Expert)

### Description
Design the ultimate facade for an autonomous vehicle that coordinates numerous complex subsystems for safe navigation.

### Requirements
- **Subsystems**: SensorFusion, PathPlanning, VehicleControl, TrafficAnalysis, WeatherAssessment, CommunicationHub, SafetyMonitor, DiagnosticSystem, MapManager, PassengerInterface
- **Facade Operations**:
  - `startAutonomousMode(destination, preferences)`
  - `handleEmergencyScenario(scenarioType)`
  - `switchToManualMode()`
  - `updateRoute(newDestination, avoidances)`
  - `communicateWithTrafficInfrastructure()`
  - `performSystemDiagnostics()`

### Extreme Complexity
- Real-time sensor data fusion (LIDAR, cameras, radar, GPS)
- Machine learning model integration
- V2V (Vehicle-to-Vehicle) and V2I (Vehicle-to-Infrastructure) communication
- Fail-safe mechanisms and redundancy
- Ethical decision making in unavoidable accidents
- Weather and road condition adaptation

### Advanced Requirements
- Support for multiple driving modes (city, highway, parking)
- Integration with traffic management systems
- Passenger comfort optimization
- Predictive maintenance
- Cybersecurity threat detection
- Over-the-air update management

### Hints
- Implement multi-layered safety systems
- Add comprehensive logging for accident investigation
- Support graceful degradation when subsystems fail
- Include machine learning model versioning
- Handle edge cases and unexpected scenarios
- Ensure real-time performance guarantees

---

## Implementation Guidelines

### For All Problems:

1. **Start with Subsystem Design**
   - Define clear interfaces for each subsystem
   - Implement basic functionality first
   - Add complexity gradually

2. **Facade Design Principles**
   - Keep facade methods simple and intuitive
   - Hide subsystem complexity from clients
   - Provide meaningful error messages
   - Include proper exception handling

3. **Testing Strategy**
   - Unit test each subsystem independently
   - Integration test the facade operations
   - Test error conditions and edge cases
   - Performance test for advanced problems

4. **Code Organization**
   ```
   facadePattern/
   ├── problem1/
   │   ├── subsystems/
   │   ├── facade/
   │   └── client/
   ├── problem2/
   │   └── ...
   ```

5. **Documentation Requirements**
   - Document all facade methods
   - Explain complex business logic
   - Provide usage examples
   - Include system architecture diagrams

## Evaluation Criteria

### Basic Problems (1-2)
- Correct implementation of facade pattern
- Proper subsystem encapsulation
- Clear client interface

### Intermediate Problems (3-5)
- Complex workflow coordination
- Error handling and recovery
- Performance considerations
- Extensible design

### Advanced Problems (6-7)
- Sophisticated state management
- Concurrency handling
- Integration complexity
- Scalability planning

### Expert Problem (8)
- Real-time constraints
- Safety-critical considerations
- Machine learning integration
- Distributed system coordination

## Bonus Challenges

1. **Add Monitoring**: Implement comprehensive logging and metrics collection
2. **Create Configuration**: Make facades configurable through external files
3. **Build CLI Interface**: Create command-line tools for facade operations
4. **Add Web API**: Expose facade operations through REST APIs
5. **Implement Caching**: Add intelligent caching for improved performance
6. **Create Simulators**: Build realistic simulators for testing complex scenarios

Remember: The Facade Pattern is about simplification, not feature reduction. Your facades should make complex operations easy while still providing access to advanced functionality when needed.