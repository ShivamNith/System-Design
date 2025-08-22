# Mediator Pattern - Practice Problems

## Problem 1: Online Auction System
**Difficulty: Easy**

Design an online auction system using the Mediator Pattern where the auction house mediates between bidders.

**Requirements:**
- Create `AuctionHouse` mediator that manages multiple `Auction` sessions
- Implement `Bidder` colleagues that can place bids, receive notifications
- Support bid validation, automatic outbid notifications, and auction closing
- Handle multiple concurrent auctions and bidder registration
- Implement reserve price functionality and bid increment rules

**Test Cases:**
```java
AuctionHouse house = new AuctionHouse();
Bidder alice = new Bidder("Alice", house);
Bidder bob = new Bidder("Bob", house);
house.createAuction("Vintage Car", 10000, Duration.ofMinutes(5));
alice.placeBid("Vintage Car", 12000);
bob.placeBid("Vintage Car", 13000);
```

## Problem 2: Stock Trading Platform
**Difficulty: Easy-Medium**

Create a stock trading platform where the exchange mediates between traders, brokers, and market makers.

**Requirements:**
- Implement `StockExchange` mediator that matches buy/sell orders
- Create `Trader`, `Broker`, and `MarketMaker` colleagues with different capabilities
- Support order types: market orders, limit orders, stop-loss orders
- Handle order matching algorithms (price-time priority)
- Implement real-time price updates and trade notifications
- Add circuit breakers and trading halts

**Test Cases:**
```java
StockExchange exchange = new StockExchange();
Trader john = new Trader("John", exchange);
Broker broker = new Broker("XYZ Broker", exchange);
john.placeOrder(new BuyOrder("AAPL", 100, 150.00));
broker.placeOrder(new SellOrder("AAPL", 50, 149.50));
```

## Problem 3: Air Traffic Control System
**Difficulty: Medium**

Expand the air traffic control example to handle multiple airports and flight routing.

**Requirements:**
- Create `AirTrafficControlCenter` that coordinates multiple `ControlTower` mediators
- Implement flight routing between airports with weather considerations
- Support different aircraft types with specific requirements (cargo, passenger, military)
- Handle emergency situations, flight diversions, and priority handling
- Add weather integration and runway scheduling optimization
- Implement communication protocols between towers

**Test Cases:**
```java
AirTrafficControlCenter center = new AirTrafficControlCenter();
ControlTower jfk = new ControlTower("JFK", center);
ControlTower lax = new ControlTower("LAX", center);
Flight aa100 = new Flight("AA100", "JFK", "LAX", jfk);
aa100.requestFlightPlan();
```

## Problem 4: Distributed System Coordinator
**Difficulty: Medium-Hard**

Design a distributed system coordinator using the Mediator Pattern for microservices communication.

**Requirements:**
- Implement `ServiceMesh` mediator for inter-service communication
- Create various `Microservice` colleagues (API Gateway, Auth Service, Database Service)
- Support service discovery, load balancing, and circuit breaker patterns
- Handle request routing, retries, and timeout management
- Implement distributed logging and monitoring through mediator
- Add health checking and service degradation capabilities

**Test Cases:**
```java
ServiceMesh mesh = new ServiceMesh();
APIGateway gateway = new APIGateway("gateway", mesh);
AuthService auth = new AuthService("auth", mesh);
UserService users = new UserService("users", mesh);
gateway.routeRequest("/api/users", request);
```

## Problem 5: Game Engine Event System
**Difficulty: Medium-Hard**

Create a game engine event system where the game world mediates between game entities.

**Requirements:**
- Implement `GameWorld` mediator that manages entity interactions
- Create various `GameEntity` colleagues (Player, NPC, Item, Environment)
- Support event types: collision, interaction, state change, communication
- Handle spatial partitioning for efficient event distribution
- Implement event priorities and batching for performance
- Add scripting support for custom entity behaviors

**Test Cases:**
```java
GameWorld world = new GameWorld();
Player player = new Player("Hero", world);
NPC merchant = new NPC("Merchant", world);
Item sword = new Item("Magic Sword", world);
world.handleCollision(player, sword);
player.interact(merchant);
```

## Problem 6: Smart City Traffic Management
**Difficulty: Hard**

Design a smart city traffic management system using hierarchical mediators.

**Requirements:**
- Create `CityTrafficController` that coordinates multiple `IntersectionController` mediators
- Implement various traffic participants: cars, buses, pedestrians, emergency vehicles
- Support adaptive traffic light timing based on real-time conditions
- Handle emergency vehicle priority and route optimization
- Implement traffic flow prediction and congestion management
- Add integration with public transportation and parking systems

**Test Cases:**
```java
CityTrafficController city = new CityTrafficController();
IntersectionController intersection1 = new IntersectionController("Main_5th", city);
Vehicle ambulance = new EmergencyVehicle("AMB001", city);
ambulance.requestPriorityRoute("Hospital", "Accident_Site");
```

## Problem 7: Enterprise Workflow Engine
**Difficulty: Hard**

Implement an enterprise workflow engine where the workflow manager mediates between process steps.

**Requirements:**
- Create `WorkflowEngine` mediator that orchestrates business processes
- Implement various `ProcessStep` colleagues (User Task, Service Task, Decision Gateway)
- Support parallel execution, conditional branching, and loop handling
- Handle process versioning, rollbacks, and error recovery
- Implement integration with external systems (databases, APIs, message queues)
- Add process monitoring, metrics collection, and SLA management

**Test Cases:**
```java
WorkflowEngine engine = new WorkflowEngine();
ProcessDefinition approval = new ProcessDefinition("document_approval");
UserTask review = new UserTask("review_document", engine);
ServiceTask notify = new ServiceTask("send_notification", engine);
engine.startProcess("document_approval", processData);
```

## Problem 8: Multiplayer Game Server
**Difficulty: Hard**

Design a multiplayer game server architecture using the Mediator Pattern for player coordination.

**Requirements:**
- Implement `GameServer` mediator managing multiple `GameRoom` sub-mediators
- Create various `Player` colleagues with different roles and capabilities
- Support real-time game state synchronization and conflict resolution
- Handle player matchmaking, team formation, and skill-based balancing
- Implement anti-cheat mechanisms and fair play enforcement
- Add spectator mode, replay system, and tournament management

**Test Cases:**
```java
GameServer server = new GameServer();
GameRoom room = server.createRoom("Battle Arena", GameMode.TEAM_DEATH_MATCH);
Player player1 = new Player("Alice", PlayerClass.WARRIOR, server);
Player player2 = new Player("Bob", PlayerClass.MAGE, server);
room.addPlayer(player1);
player1.useAbility("Fire Blast", target);
```

## Advanced Challenges

### Performance Optimization Challenges:
1. **Efficient Event Routing**: Design mediators that can handle millions of events per second
2. **Memory Management**: Implement mediators that efficiently manage memory for large numbers of colleagues
3. **Concurrent Mediation**: Create thread-safe mediators that handle concurrent colleague interactions

### Pattern Combination Challenges:
1. **Mediator + Observer**: Combine with Observer pattern for event broadcasting
2. **Mediator + Command**: Use Command pattern for undo/redo of mediated actions
3. **Mediator + State**: Different mediation strategies based on system state

### Real-World Integration:
1. **Message Queue Integration**: Implement mediators using actual message queues (RabbitMQ, Kafka)
2. **Database-Backed Mediation**: Store mediation state and history in databases
3. **Cloud-Native Mediators**: Design mediators that work in containerized, cloud environments

## Testing Guidelines

For each problem, ensure your solution includes:

1. **Unit Tests**: Test individual colleague behaviors and mediator functionality
2. **Integration Tests**: Test complex interaction scenarios
3. **Performance Tests**: Verify system performance under load
4. **Concurrency Tests**: Test thread safety and concurrent access
5. **Failure Tests**: Test system behavior during failures and recovery

## Evaluation Criteria

Your solutions will be evaluated based on:

1. **Decoupling**: How well colleagues are decoupled from each other
2. **Extensibility**: Ease of adding new colleague types
3. **Performance**: Efficient handling of mediated communications
4. **Scalability**: Ability to handle increasing numbers of colleagues
5. **Maintainability**: Clean, understandable mediation logic
6. **Error Handling**: Robust handling of edge cases and failures

## Design Patterns Integration

Consider how the Mediator Pattern integrates with:

- **Observer Pattern**: For broadcasting events to multiple observers
- **Command Pattern**: For encapsulating requests between colleagues
- **Strategy Pattern**: For different mediation strategies
- **Facade Pattern**: For simplifying complex subsystem interactions
- **Proxy Pattern**: For controlling access to colleagues through the mediator