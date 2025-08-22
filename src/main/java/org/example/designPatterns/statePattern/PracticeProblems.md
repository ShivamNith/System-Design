# State Pattern Practice Problems

## Table of Contents
1. [Beginner Problems](#beginner-problems)
2. [Intermediate Problems](#intermediate-problems)
3. [Advanced Problems](#advanced-problems)
4. [Expert Problems](#expert-problems)
5. [Solutions](#solutions)

---

## Beginner Problems

### Problem 1: Simple Light Switch
**Difficulty**: ⭐ Basic

**Description**: 
Implement a light switch that can be in two states: ON and OFF. The switch should:
- Turn on when pressed in OFF state
- Turn off when pressed in ON state
- Display current state
- Track how many times it's been switched

**Requirements**:
- Create `LightState` interface with `press()` method
- Implement `OnState` and `OffState` classes  
- Create `LightSwitch` context class
- Include a switch counter

**Expected Output**:
```
Light is OFF
*press* Light is ON (switched 1 times)
*press* Light is OFF (switched 2 times)
```

---

### Problem 2: Basic Media Player
**Difficulty**: ⭐ Basic

**Description**: 
Create a simple media player with three states: STOPPED, PLAYING, and PAUSED.

**Requirements**:
- `play()` method: STOPPED→PLAYING, PAUSED→PLAYING
- `pause()` method: PLAYING→PAUSED
- `stop()` method: Any state→STOPPED
- Invalid operations should display appropriate messages
- Track current song and position

**State Transitions**:
```
STOPPED ──play──> PLAYING
PLAYING ──pause──> PAUSED
PLAYING ──stop──> STOPPED
PAUSED ──play──> PLAYING
PAUSED ──stop──> STOPPED
```

---

## Intermediate Problems

### Problem 3: ATM Machine
**Difficulty**: ⭐⭐ Intermediate

**Description**: 
Design an ATM machine with the following states:
- IDLE: Waiting for card insertion
- CARD_INSERTED: Card inserted, waiting for PIN
- AUTHENTICATED: PIN verified, ready for transactions
- TRANSACTION: Processing a transaction
- MAINTENANCE: Machine under maintenance

**Requirements**:
- Implement card insertion, PIN verification, and transactions
- Handle invalid PINs (max 3 attempts)
- Support balance inquiry, withdrawal, and deposit
- Include maintenance mode
- Validate account balance for withdrawals

**Business Rules**:
- Lock card after 3 failed PIN attempts
- Minimum withdrawal: $20
- Maximum withdrawal per transaction: $500
- ATM cash limit: $10,000

---

### Problem 4: Elevator Controller
**Difficulty**: ⭐⭐ Intermediate

**Description**: 
Create an elevator system with states:
- IDLE: Elevator at rest
- MOVING_UP: Going up
- MOVING_DOWN: Going down
- DOOR_OPEN: Doors are open
- MAINTENANCE: Out of service

**Requirements**:
- Handle floor requests from inside and outside
- Implement door opening/closing logic
- Include emergency stop functionality
- Track current floor and direction
- Queue system for multiple requests

**Constraints**:
- Building has 10 floors (0-9)
- Doors stay open for 5 seconds
- Emergency stop works from any state
- Maintenance mode accessible only by technicians

---

## Advanced Problems

### Problem 5: Game Character State Machine
**Difficulty**: ⭐⭐⭐ Advanced

**Description**: 
Design a game character with complex state management:

**States**: IDLE, WALKING, RUNNING, JUMPING, ATTACKING, DEFENDING, STUNNED, DEAD

**Requirements**:
- Implement health and stamina systems
- Some actions consume stamina
- Character can be stunned when hit while not defending
- Implement combo attacks (3-hit combo)
- Include invincibility frames after being hit
- Character dies when health reaches 0

**Complex Rules**:
- Can't attack or defend while stunned
- Running consumes stamina over time
- Jumping while running gives longer jump
- Defending reduces damage by 50%
- Combo attacks deal increasing damage

---

### Problem 6: Order Processing Workflow
**Difficulty**: ⭐⭐⭐ Advanced

**Description**: 
Create an e-commerce order processing system with states:
- PENDING: Order placed, awaiting payment
- PAID: Payment confirmed
- PROCESSING: Being prepared for shipping
- SHIPPED: On the way to customer
- DELIVERED: Successfully delivered
- CANCELLED: Order cancelled
- RETURNED: Order returned by customer

**Requirements**:
- Implement payment processing with retries
- Handle inventory checks before processing
- Support partial shipping for large orders
- Include refund processing for cancellations/returns
- Implement notification system for state changes
- Track timestamps for each state transition

**Business Logic**:
- Orders auto-cancel after 24 hours without payment
- Can only cancel before shipping
- Returns accepted within 30 days of delivery
- Inventory reserved when payment confirmed

---

## Expert Problems

### Problem 7: Traffic Light System
**Difficulty**: ⭐⭐⭐⭐ Expert

**Description**: 
Design a smart traffic light intersection controller with:
- Four-way intersection with traffic sensors
- Emergency vehicle priority
- Pedestrian crossing requests
- Night mode (flashing yellow)
- Maintenance mode

**States**: 
- NORTH_SOUTH_GREEN, NORTH_SOUTH_YELLOW, NORTH_SOUTH_RED
- EAST_WEST_GREEN, EAST_WEST_YELLOW, EAST_WEST_RED
- ALL_RED (safety state), PEDESTRIAN_CROSSING
- EMERGENCY_OVERRIDE, NIGHT_MODE, MAINTENANCE

**Requirements**:
- Implement traffic sensors that detect vehicle presence
- Emergency vehicles can trigger immediate state changes
- Pedestrian buttons request crossing time
- Adaptive timing based on traffic volume
- Safety delays between conflicting green lights
- Weather mode (longer cycles in rain/snow)

**Complex Features**:
- Machine learning integration for traffic pattern optimization
- Integration with city traffic management system
- Fault detection and automatic maintenance requests
- Historical data collection and analysis
- Multi-intersection coordination

---

### Problem 8: Distributed System Node State Manager
**Difficulty**: ⭐⭐⭐⭐ Expert

**Description**: 
Create a distributed system node that manages its state in a cluster:

**States**: STARTING, HEALTHY, DEGRADED, FAILING, FAILED, RECOVERING, LEAVING, MAINTENANCE

**Requirements**:
- Health check system with multiple metrics
- Automatic failover and recovery
- Load shedding in degraded state
- Graceful shutdown process
- Integration with service discovery
- State synchronization across nodes

**Advanced Features**:
- Implement circuit breaker pattern within states
- Support for rolling updates
- Chaos engineering compatibility
- Performance metrics collection
- Automatic scaling triggers
- State persistence and recovery

**Monitoring Integration**:
- Prometheus metrics export
- Distributed tracing support
- Log correlation across state changes
- Alert generation for critical state transitions

---

## Solutions

### Solution 1: Simple Light Switch

```java
// State interface
interface LightState {
    void press(LightSwitch lightSwitch);
    String getStateName();
}

// Context class
class LightSwitch {
    private LightState currentState;
    private int switchCount;
    
    public LightSwitch() {
        currentState = new OffState();
        switchCount = 0;
    }
    
    public void press() {
        currentState.press(this);
    }
    
    public void setState(LightState state) {
        currentState = state;
        switchCount++;
    }
    
    public void displayStatus() {
        System.out.println("Light is " + currentState.getStateName() + 
                         " (switched " + switchCount + " times)");
    }
}

// Concrete states
class OnState implements LightState {
    public void press(LightSwitch lightSwitch) {
        lightSwitch.setState(new OffState());
        System.out.println("*press* Light switched off");
        lightSwitch.displayStatus();
    }
    
    public String getStateName() { return "ON"; }
}

class OffState implements LightState {
    public void press(LightSwitch lightSwitch) {
        lightSwitch.setState(new OnState());
        System.out.println("*press* Light switched on");
        lightSwitch.displayStatus();
    }
    
    public String getStateName() { return "OFF"; }
}

// Usage
public class LightSwitchDemo {
    public static void main(String[] args) {
        LightSwitch light = new LightSwitch();
        light.displayStatus(); // Light is OFF (switched 0 times)
        
        light.press(); // *press* Light switched on, Light is ON (switched 1 times)
        light.press(); // *press* Light switched off, Light is OFF (switched 2 times)
        light.press(); // *press* Light switched on, Light is ON (switched 3 times)
    }
}
```

### Solution 2: Basic Media Player

```java
// State interface
interface PlayerState {
    void play(MediaPlayer player);
    void pause(MediaPlayer player);
    void stop(MediaPlayer player);
    String getStateName();
}

// Context class
class MediaPlayer {
    private PlayerState currentState;
    private String currentSong;
    private int position;
    
    public MediaPlayer() {
        currentState = new StoppedState();
        currentSong = "";
        position = 0;
    }
    
    public void setState(PlayerState state) {
        currentState = state;
        displayStatus();
    }
    
    public void play() { currentState.play(this); }
    public void pause() { currentState.pause(this); }
    public void stop() { currentState.stop(this); }
    
    public void loadSong(String song) {
        currentSong = song;
        position = 0;
    }
    
    public void setPosition(int pos) { position = pos; }
    public int getPosition() { return position; }
    public String getCurrentSong() { return currentSong; }
    
    public void displayStatus() {
        System.out.println("Player: " + currentState.getStateName() + 
                         (currentSong.isEmpty() ? "" : " - " + currentSong + 
                          " [" + position + "s]"));
    }
}

// Concrete states
class StoppedState implements PlayerState {
    public void play(MediaPlayer player) {
        player.setState(new PlayingState());
        System.out.println("Starting playback...");
    }
    
    public void pause(MediaPlayer player) {
        System.out.println("Can't pause - player is stopped");
    }
    
    public void stop(MediaPlayer player) {
        System.out.println("Already stopped");
    }
    
    public String getStateName() { return "STOPPED"; }
}

class PlayingState implements PlayerState {
    public void play(MediaPlayer player) {
        System.out.println("Already playing");
    }
    
    public void pause(MediaPlayer player) {
        player.setState(new PausedState());
        System.out.println("Playback paused");
    }
    
    public void stop(MediaPlayer player) {
        player.setPosition(0);
        player.setState(new StoppedState());
        System.out.println("Playback stopped");
    }
    
    public String getStateName() { return "PLAYING"; }
}

class PausedState implements PlayerState {
    public void play(MediaPlayer player) {
        player.setState(new PlayingState());
        System.out.println("Resuming playback...");
    }
    
    public void pause(MediaPlayer player) {
        System.out.println("Already paused");
    }
    
    public void stop(MediaPlayer player) {
        player.setPosition(0);
        player.setState(new StoppedState());
        System.out.println("Playback stopped");
    }
    
    public String getStateName() { return "PAUSED"; }
}

// Usage
public class MediaPlayerDemo {
    public static void main(String[] args) {
        MediaPlayer player = new MediaPlayer();
        player.loadSong("Song.mp3");
        
        player.displayStatus(); // Player: STOPPED - Song.mp3 [0s]
        player.play();          // Starting playback... Player: PLAYING - Song.mp3 [0s]
        player.pause();         // Playback paused Player: PAUSED - Song.mp3 [0s]
        player.play();          // Resuming playback... Player: PLAYING - Song.mp3 [0s]
        player.stop();          // Playback stopped Player: STOPPED - Song.mp3 [0s]
    }
}
```

### Solution 3: ATM Machine (Partial Implementation)

```java
// State interface
interface ATMState {
    void insertCard(ATM atm, String cardNumber);
    void enterPIN(ATM atm, String pin);
    void selectTransaction(ATM atm, TransactionType type, double amount);
    void ejectCard(ATM atm);
    void enterMaintenance(ATM atm);
    String getStateName();
}

enum TransactionType {
    BALANCE_INQUIRY, WITHDRAWAL, DEPOSIT
}

// Context class
class ATM {
    private ATMState currentState;
    private String currentCard;
    private int pinAttempts;
    private double accountBalance;
    private double atmCashAmount;
    
    public ATM() {
        currentState = new IdleState();
        pinAttempts = 0;
        accountBalance = 1000.0; // Demo balance
        atmCashAmount = 10000.0;
    }
    
    public void setState(ATMState state) {
        System.out.println("ATM State: " + currentState.getStateName() + 
                         " -> " + state.getStateName());
        currentState = state;
    }
    
    // Public interface methods
    public void insertCard(String cardNumber) { 
        currentState.insertCard(this, cardNumber); 
    }
    public void enterPIN(String pin) { 
        currentState.enterPIN(this, pin); 
    }
    public void selectTransaction(TransactionType type, double amount) { 
        currentState.selectTransaction(this, type, amount); 
    }
    public void ejectCard() { 
        currentState.ejectCard(this); 
    }
    public void enterMaintenance() { 
        currentState.enterMaintenance(this); 
    }
    
    // Internal methods
    public boolean validatePIN(String pin) {
        return "1234".equals(pin); // Demo PIN
    }
    
    public void incrementPinAttempts() { pinAttempts++; }
    public void resetPinAttempts() { pinAttempts = 0; }
    public int getPinAttempts() { return pinAttempts; }
    
    public void setCurrentCard(String card) { currentCard = card; }
    public String getCurrentCard() { return currentCard; }
    
    public double getAccountBalance() { return accountBalance; }
    public void updateAccountBalance(double amount) { accountBalance += amount; }
    
    public double getATMCash() { return atmCashAmount; }
    public void updateATMCash(double amount) { atmCashAmount += amount; }
}

// Concrete states
class IdleState implements ATMState {
    public void insertCard(ATM atm, String cardNumber) {
        atm.setCurrentCard(cardNumber);
        atm.setState(new CardInsertedState());
        System.out.println("Card inserted: " + cardNumber);
        System.out.println("Please enter your PIN");
    }
    
    public void enterPIN(ATM atm, String pin) {
        System.out.println("Please insert your card first");
    }
    
    public void selectTransaction(ATM atm, TransactionType type, double amount) {
        System.out.println("Please insert your card first");
    }
    
    public void ejectCard(ATM atm) {
        System.out.println("No card inserted");
    }
    
    public void enterMaintenance(ATM atm) {
        atm.setState(new MaintenanceState());
        System.out.println("Entering maintenance mode");
    }
    
    public String getStateName() { return "IDLE"; }
}

class CardInsertedState implements ATMState {
    public void insertCard(ATM atm, String cardNumber) {
        System.out.println("Card already inserted");
    }
    
    public void enterPIN(ATM atm, String pin) {
        if (atm.validatePIN(pin)) {
            atm.resetPinAttempts();
            atm.setState(new AuthenticatedState());
            System.out.println("PIN accepted. Please select transaction");
        } else {
            atm.incrementPinAttempts();
            System.out.println("Invalid PIN. Attempts: " + atm.getPinAttempts());
            
            if (atm.getPinAttempts() >= 3) {
                System.out.println("Card locked. Please contact your bank");
                atm.setState(new IdleState());
                atm.setCurrentCard(null);
            }
        }
    }
    
    public void selectTransaction(ATM atm, TransactionType type, double amount) {
        System.out.println("Please enter your PIN first");
    }
    
    public void ejectCard(ATM atm) {
        atm.setState(new IdleState());
        atm.setCurrentCard(null);
        System.out.println("Card ejected");
    }
    
    public void enterMaintenance(ATM atm) {
        System.out.println("Cannot enter maintenance while card is inserted");
    }
    
    public String getStateName() { return "CARD_INSERTED"; }
}

class AuthenticatedState implements ATMState {
    public void insertCard(ATM atm, String cardNumber) {
        System.out.println("Card already inserted");
    }
    
    public void enterPIN(ATM atm, String pin) {
        System.out.println("Already authenticated");
    }
    
    public void selectTransaction(ATM atm, TransactionType type, double amount) {
        atm.setState(new TransactionState());
        
        switch (type) {
            case BALANCE_INQUIRY:
                System.out.println("Account balance: $" + atm.getAccountBalance());
                atm.setState(new AuthenticatedState());
                break;
                
            case WITHDRAWAL:
                if (amount < 20) {
                    System.out.println("Minimum withdrawal is $20");
                } else if (amount > 500) {
                    System.out.println("Maximum withdrawal per transaction is $500");
                } else if (amount > atm.getAccountBalance()) {
                    System.out.println("Insufficient funds");
                } else if (amount > atm.getATMCash()) {
                    System.out.println("ATM has insufficient cash");
                } else {
                    atm.updateAccountBalance(-amount);
                    atm.updateATMCash(-amount);
                    System.out.println("Please take your cash: $" + amount);
                    System.out.println("New balance: $" + atm.getAccountBalance());
                }
                atm.setState(new AuthenticatedState());
                break;
                
            case DEPOSIT:
                atm.updateAccountBalance(amount);
                System.out.println("Deposit successful: $" + amount);
                System.out.println("New balance: $" + atm.getAccountBalance());
                atm.setState(new AuthenticatedState());
                break;
        }
    }
    
    public void ejectCard(ATM atm) {
        atm.setState(new IdleState());
        atm.setCurrentCard(null);
        atm.resetPinAttempts();
        System.out.println("Thank you. Card ejected");
    }
    
    public void enterMaintenance(ATM atm) {
        System.out.println("Cannot enter maintenance during transaction");
    }
    
    public String getStateName() { return "AUTHENTICATED"; }
}

class TransactionState implements ATMState {
    public void insertCard(ATM atm, String cardNumber) {
        System.out.println("Transaction in progress");
    }
    
    public void enterPIN(ATM atm, String pin) {
        System.out.println("Transaction in progress");
    }
    
    public void selectTransaction(ATM atm, TransactionType type, double amount) {
        System.out.println("Transaction in progress");
    }
    
    public void ejectCard(ATM atm) {
        System.out.println("Cannot eject card during transaction");
    }
    
    public void enterMaintenance(ATM atm) {
        System.out.println("Cannot enter maintenance during transaction");
    }
    
    public String getStateName() { return "TRANSACTION"; }
}

class MaintenanceState implements ATMState {
    public void insertCard(ATM atm, String cardNumber) {
        System.out.println("ATM is under maintenance");
    }
    
    public void enterPIN(ATM atm, String pin) {
        System.out.println("ATM is under maintenance");
    }
    
    public void selectTransaction(ATM atm, TransactionType type, double amount) {
        System.out.println("ATM is under maintenance");
    }
    
    public void ejectCard(ATM atm) {
        System.out.println("ATM is under maintenance");
    }
    
    public void enterMaintenance(ATM atm) {
        System.out.println("Exiting maintenance mode");
        atm.setState(new IdleState());
    }
    
    public String getStateName() { return "MAINTENANCE"; }
}

// Usage
public class ATMDemo {
    public static void main(String[] args) {
        ATM atm = new ATM();
        
        // Normal transaction
        atm.insertCard("1234-5678-9012-3456");
        atm.enterPIN("1234");
        atm.selectTransaction(TransactionType.BALANCE_INQUIRY, 0);
        atm.selectTransaction(TransactionType.WITHDRAWAL, 100);
        atm.ejectCard();
        
        // Invalid PIN scenario
        atm.insertCard("1234-5678-9012-3456");
        atm.enterPIN("0000");
        atm.enterPIN("1111");
        atm.enterPIN("2222"); // Card locked
    }
}
```

### Solution Hints for Other Problems

**Problem 4 (Elevator Controller)**:
- Use a request queue to manage floor requests
- Implement a direction optimization algorithm
- Add timer-based door closing
- Include emergency override functionality

**Problem 5 (Game Character)**:
- Use a health/stamina monitoring system
- Implement state-based damage calculations
- Add animation state integration
- Include special move combinations

**Problem 6 (Order Processing)**:
- Implement timeout mechanisms for payments
- Add inventory reservation system
- Include notification dispatching
- Create audit trail for all state changes

**Problem 7 (Traffic Light)**:
- Use sensor-based adaptive timing
- Implement emergency vehicle detection
- Add pedestrian crossing integration
- Include weather condition adjustments

**Problem 8 (Distributed System Node)**:
- Implement health check aggregation
- Add circuit breaker integration
- Include state synchronization protocols
- Add performance metric collection

## Key Learning Points

1. **State Encapsulation**: Each state handles its specific behavior independently
2. **Transition Logic**: States manage when and how to transition to other states
3. **Context Management**: The context maintains state and provides services to states
4. **Error Handling**: Invalid operations are handled gracefully within each state
5. **Extensibility**: New states can be added without modifying existing code
6. **Real-world Complexity**: Production systems require careful consideration of edge cases, timeouts, and error conditions

## Best Practices Applied

1. **Single Responsibility**: Each state class handles one specific state behavior
2. **Open/Closed Principle**: Easy to add new states without modifying existing ones
3. **Dependency Inversion**: States depend on abstractions, not concrete implementations
4. **Error Handling**: Comprehensive error handling for invalid state transitions
5. **Logging and Monitoring**: State transitions are logged for debugging and audit
6. **Resource Management**: Proper cleanup and resource management in state transitions