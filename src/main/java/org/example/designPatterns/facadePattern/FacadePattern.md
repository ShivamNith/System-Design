# Facade Design Pattern - Complete Guide

## Table of Contents
1. [Introduction](#introduction)
2. [Core Concepts](#core-concepts)
3. [When to Use Facade Pattern](#when-to-use-facade-pattern)
4. [Structure and Components](#structure-and-components)
5. [Implementation Steps](#implementation-steps)
6. [Detailed Examples](#detailed-examples)
7. [Real-World Applications](#real-world-applications)
8. [Advantages and Disadvantages](#advantages-and-disadvantages)
9. [Facade vs Other Patterns](#facade-vs-other-patterns)
10. [Best Practices](#best-practices)

## Introduction

The **Facade Pattern** is a structural design pattern that provides a simplified interface to a complex subsystem. It defines a higher-level interface that makes the subsystem easier to use by hiding the complexities of the subsystem from clients.

### Definition
> "Provide a unified interface to a set of interfaces in a subsystem. Facade defines a higher-level interface that makes the subsystem easier to use." - Gang of Four

### Problem It Solves
When working with complex subsystems, clients often face:
- Complex interfaces with many classes and methods to understand
- Tight coupling between client code and subsystem internals
- Difficult to use APIs that require knowledge of implementation details
- Code duplication when multiple clients need similar functionality
- Fragile code that breaks when subsystem internals change

## Core Concepts

### Key Principles
1. **Simplification**: Provide a simple interface to complex functionality
2. **Decoupling**: Reduce dependencies between clients and subsystems
3. **Abstraction**: Hide implementation details behind a clean interface
4. **Unification**: Combine multiple interfaces into a single, coherent interface
5. **Optional Access**: Clients can still access subsystem directly if needed

### Real-World Analogy
Think of a **home theater system**:
- **Complex subsystem**: You have a DVD player, amplifier, speakers, projector, screen, lights
- **Without facade**: You need to turn on each component individually, adjust settings, manage inputs/outputs
- **With facade**: A universal remote (facade) has one "Movie Mode" button that:
  - Turns on all components in the right order
  - Sets correct inputs and outputs
  - Adjusts volume and lighting
  - Starts the movie
- **Benefits**: Simple one-button operation vs. complex manual setup

## When to Use Facade Pattern

Use the Facade Pattern when:
1. **You want to provide a simple interface to a complex subsystem**
2. **You want to layer your subsystems** and need entry points to each layer
3. **You want to decouple clients from subsystem components**
4. **You have a complex system** that's difficult for clients to use
5. **You want to wrap a poorly designed collection of APIs** with a single well-designed API
6. **You need to reduce dependencies** between subsystems and clients

### Red Flags Indicating Need for Facade Pattern
- Client code directly instantiates many subsystem classes
- Complex initialization sequences required by clients
- Similar subsystem usage patterns repeated across multiple clients
- Clients need detailed knowledge of subsystem internals
- Frequent changes to subsystem break multiple clients
- Multiple APIs need to be called in specific sequences

## Structure and Components

### UML Diagram Structure
```
┌─────────────────────┐
│       Client        │
└─────────────────────┘
           │
           │ uses
           ▼
┌─────────────────────┐    ┌─────────────────────┐
│       Facade        │    │    Subsystem A      │
├─────────────────────┤◄───┤                     │
│ + operation1()      │    │ + complexOperation()│
│ + operation2()      │    │ + anotherMethod()   │
│ + operation3()      │    └─────────────────────┘
└─────────────────────┘    
           │               ┌─────────────────────┐
           │               │    Subsystem B      │
           └───────────────┤                     │
                           │ + complicatedTask() │
                           │ + helperMethod()    │
                           └─────────────────────┘
```

### Components

1. **Facade**: Provides simple methods that delegate to the appropriate subsystem objects
2. **Subsystem Classes**: Implement subsystem functionality and handle work assigned by the facade
3. **Client**: Uses the facade instead of calling subsystem objects directly

## Implementation Steps

1. **Identify the complex subsystem** that needs simplification
2. **Analyze client usage patterns** to understand common operations
3. **Design the facade interface** with simple, high-level methods
4. **Implement the facade** by delegating to appropriate subsystem components
5. **Update clients** to use the facade instead of subsystem directly
6. **Consider making subsystem classes package-private** to enforce facade usage

## Detailed Examples

### Example 1: Computer System Startup

```java
// Subsystem components
class CPU {
    public void freeze() {
        System.out.println("CPU: Freezing processor");
    }
    
    public void jump(long position) {
        System.out.println("CPU: Jumping to position " + position);
    }
    
    public void execute() {
        System.out.println("CPU: Executing instructions");
    }
    
    public void halt() {
        System.out.println("CPU: Halting processor");
    }
}

class Memory {
    private byte[] data;
    
    public Memory() {
        this.data = new byte[1024 * 1024]; // 1MB
    }
    
    public void load(long position, byte[] data) {
        System.out.println("Memory: Loading " + data.length + " bytes at position " + position);
        System.arraycopy(data, 0, this.data, (int)position, Math.min(data.length, this.data.length - (int)position));
    }
    
    public byte[] read(long position, int size) {
        System.out.println("Memory: Reading " + size + " bytes from position " + position);
        byte[] result = new byte[size];
        System.arraycopy(this.data, (int)position, result, 0, Math.min(size, this.data.length - (int)position));
        return result;
    }
    
    public void clear() {
        System.out.println("Memory: Clearing all memory");
        Arrays.fill(data, (byte)0);
    }
}

class HardDrive {
    private Map<String, byte[]> files;
    
    public HardDrive() {
        this.files = new HashMap<>();
        initializeBootFiles();
    }
    
    private void initializeBootFiles() {
        files.put("boot.img", "BOOT_LOADER_CODE".getBytes());
        files.put("kernel.sys", "KERNEL_SYSTEM_CODE".getBytes());
        files.put("drivers.sys", "DEVICE_DRIVERS_CODE".getBytes());
    }
    
    public byte[] read(String fileName, int size) {
        System.out.println("HardDrive: Reading " + size + " bytes from file " + fileName);
        byte[] file = files.get(fileName);
        if (file == null) {
            System.out.println("HardDrive: File " + fileName + " not found");
            return new byte[0];
        }
        return Arrays.copyOf(file, Math.min(size, file.length));
    }
    
    public void write(String fileName, byte[] data) {
        System.out.println("HardDrive: Writing " + data.length + " bytes to file " + fileName);
        files.put(fileName, Arrays.copyOf(data, data.length));
    }
    
    public void spinUp() {
        System.out.println("HardDrive: Spinning up disk");
    }
    
    public void spinDown() {
        System.out.println("HardDrive: Spinning down disk");
    }
}

class BIOS {
    public void performPowerOnSelfTest() {
        System.out.println("BIOS: Performing Power-On Self Test (POST)");
        System.out.println("BIOS: Checking CPU... OK");
        System.out.println("BIOS: Checking Memory... OK");
        System.out.println("BIOS: Checking Storage... OK");
    }
    
    public void initializeHardware() {
        System.out.println("BIOS: Initializing hardware components");
        System.out.println("BIOS: Setting up interrupt vectors");
        System.out.println("BIOS: Configuring system timers");
    }
    
    public void loadBootSector() {
        System.out.println("BIOS: Loading boot sector from primary drive");
    }
}

class OperatingSystem {
    public void loadKernel() {
        System.out.println("OS: Loading operating system kernel");
    }
    
    public void startSystemServices() {
        System.out.println("OS: Starting system services");
        System.out.println("OS: - Process manager started");
        System.out.println("OS: - Memory manager started");
        System.out.println("OS: - File system started");
        System.out.println("OS: - Network stack started");
    }
    
    public void loadDeviceDrivers() {
        System.out.println("OS: Loading device drivers");
    }
    
    public void showLoginScreen() {
        System.out.println("OS: System ready - displaying login screen");
    }
    
    public void shutdown() {
        System.out.println("OS: Shutting down system services");
        System.out.println("OS: Unmounting file systems");
        System.out.println("OS: System shutdown complete");
    }
}

// Facade
class ComputerFacade {
    private CPU cpu;
    private Memory memory;
    private HardDrive hardDrive;
    private BIOS bios;
    private OperatingSystem os;
    
    private static final long BOOT_ADDRESS = 0x7C00;
    private static final long KERNEL_ADDRESS = 0x100000;
    private static final int BOOT_SECTOR_SIZE = 512;
    
    public ComputerFacade() {
        this.cpu = new CPU();
        this.memory = new Memory();
        this.hardDrive = new HardDrive();
        this.bios = new BIOS();
        this.os = new OperatingSystem();
    }
    
    public void startComputer() {
        System.out.println("=== STARTING COMPUTER ===");
        System.out.println("Press power button...\n");
        
        // 1. BIOS Phase
        System.out.println("--- BIOS Phase ---");
        bios.performPowerOnSelfTest();
        bios.initializeHardware();
        cpu.freeze();
        
        // 2. Boot Loader Phase
        System.out.println("\n--- Boot Loader Phase ---");
        hardDrive.spinUp();
        byte[] bootSector = hardDrive.read("boot.img", BOOT_SECTOR_SIZE);
        memory.load(BOOT_ADDRESS, bootSector);
        cpu.jump(BOOT_ADDRESS);
        cpu.execute();
        
        // 3. Operating System Phase
        System.out.println("\n--- Operating System Loading Phase ---");
        byte[] kernel = hardDrive.read("kernel.sys", 64 * 1024); // 64KB kernel
        memory.load(KERNEL_ADDRESS, kernel);
        
        os.loadKernel();
        os.loadDeviceDrivers();
        os.startSystemServices();
        
        // 4. Ready State
        System.out.println("\n--- System Ready ---");
        os.showLoginScreen();
        System.out.println("Computer startup completed successfully!\n");
    }
    
    public void shutdownComputer() {
        System.out.println("=== SHUTTING DOWN COMPUTER ===");
        
        os.shutdown();
        
        System.out.println("Stopping CPU...");
        cpu.halt();
        
        System.out.println("Clearing memory...");
        memory.clear();
        
        System.out.println("Spinning down storage...");
        hardDrive.spinDown();
        
        System.out.println("Computer shutdown completed!\n");
    }
    
    public void restartComputer() {
        System.out.println("=== RESTARTING COMPUTER ===");
        shutdownComputer();
        
        System.out.println("Waiting 2 seconds...");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        startComputer();
    }
    
    public void enterSafeMode() {
        System.out.println("=== STARTING IN SAFE MODE ===");
        
        // Similar to normal startup but with minimal drivers
        bios.performPowerOnSelfTest();
        bios.initializeHardware();
        
        hardDrive.spinUp();
        byte[] bootSector = hardDrive.read("boot.img", BOOT_SECTOR_SIZE);
        memory.load(BOOT_ADDRESS, bootSector);
        
        System.out.println("Loading minimal kernel...");
        os.loadKernel();
        System.out.println("Loading essential drivers only...");
        os.startSystemServices();
        
        System.out.println("Safe mode startup completed!\n");
    }
    
    public void runDiagnostics() {
        System.out.println("=== RUNNING SYSTEM DIAGNOSTICS ===");
        
        System.out.println("Testing CPU...");
        cpu.freeze();
        cpu.jump(0);
        cpu.execute();
        cpu.halt();
        System.out.println("CPU test: PASSED");
        
        System.out.println("Testing Memory...");
        byte[] testData = "MEMORY_TEST".getBytes();
        memory.load(0, testData);
        byte[] readData = memory.read(0, testData.length);
        System.out.println("Memory test: " + (Arrays.equals(testData, readData) ? "PASSED" : "FAILED"));
        
        System.out.println("Testing Storage...");
        hardDrive.write("test.tmp", "DISK_TEST".getBytes());
        byte[] diskData = hardDrive.read("test.tmp", 9);
        System.out.println("Storage test: " + (Arrays.equals("DISK_TEST".getBytes(), diskData) ? "PASSED" : "FAILED"));
        
        System.out.println("Diagnostics completed!\n");
    }
}

// Client usage demonstration
class ComputerUser {
    public static void demonstrateComputerUsage() {
        ComputerFacade computer = new ComputerFacade();
        
        // Without facade, client would need to:
        // 1. Create and coordinate CPU, Memory, HardDrive, BIOS, OS instances
        // 2. Know the exact sequence of operations
        // 3. Handle all the complex interactions
        // 4. Manage state transitions and error conditions
        
        // With facade, client simply calls high-level operations:
        
        // Normal startup
        computer.startComputer();
        
        // Run some diagnostics
        computer.runDiagnostics();
        
        // Restart the system
        computer.restartComputer();
        
        // Boot in safe mode
        computer.enterSafeMode();
        
        // Shutdown
        computer.shutdownComputer();
    }
}
```

### Example 2: Home Theater System

```java
// Subsystem components
class DVDPlayer {
    private String movie;
    private boolean playing;
    
    public void on() {
        System.out.println("DVD Player: Powering on");
    }
    
    public void off() {
        System.out.println("DVD Player: Powering off");
        playing = false;
    }
    
    public void play(String movie) {
        this.movie = movie;
        this.playing = true;
        System.out.println("DVD Player: Playing '" + movie + "'");
    }
    
    public void pause() {
        if (playing) {
            System.out.println("DVD Player: Pausing '" + movie + "'");
        }
    }
    
    public void stop() {
        if (playing) {
            System.out.println("DVD Player: Stopping '" + movie + "'");
            playing = false;
        }
    }
    
    public void eject() {
        stop();
        System.out.println("DVD Player: Ejecting disc");
        movie = null;
    }
}

class Amplifier {
    private int volume;
    private DVDPlayer dvdPlayer;
    private boolean on;
    
    public void on() {
        on = true;
        System.out.println("Amplifier: Powering on");
    }
    
    public void off() {
        on = false;
        System.out.println("Amplifier: Powering off");
    }
    
    public void setDvd(DVDPlayer dvdPlayer) {
        this.dvdPlayer = dvdPlayer;
        System.out.println("Amplifier: Setting DVD player as input source");
    }
    
    public void setSurroundSound() {
        System.out.println("Amplifier: Setting surround sound mode (5.1)");
    }
    
    public void setStereoSound() {
        System.out.println("Amplifier: Setting stereo sound mode (2.0)");
    }
    
    public void setVolume(int volume) {
        this.volume = Math.max(0, Math.min(100, volume));
        System.out.println("Amplifier: Setting volume to " + this.volume);
    }
    
    public int getVolume() {
        return volume;
    }
}

class Tuner {
    private String station;
    
    public void on() {
        System.out.println("Tuner: Powering on");
    }
    
    public void off() {
        System.out.println("Tuner: Powering off");
    }
    
    public void setFrequency(String station) {
        this.station = station;
        System.out.println("Tuner: Setting frequency to " + station);
    }
    
    public void setAM() {
        System.out.println("Tuner: Switching to AM mode");
    }
    
    public void setFM() {
        System.out.println("Tuner: Switching to FM mode");
    }
}

class Projector {
    private boolean on;
    private String input;
    
    public void on() {
        on = true;
        System.out.println("Projector: Powering on");
    }
    
    public void off() {
        on = false;
        System.out.println("Projector: Powering off");
    }
    
    public void setInput(String input) {
        this.input = input;
        System.out.println("Projector: Setting input to " + input);
    }
    
    public void wideScreenMode() {
        System.out.println("Projector: Setting wide screen mode (16:9)");
    }
    
    public void standardMode() {
        System.out.println("Projector: Setting standard mode (4:3)");
    }
}

class TheaterLights {
    private int brightness; // 0-100
    
    public void on() {
        brightness = 100;
        System.out.println("Theater Lights: Turning on (brightness: " + brightness + "%)");
    }
    
    public void off() {
        brightness = 0;
        System.out.println("Theater Lights: Turning off");
    }
    
    public void dim(int brightness) {
        this.brightness = Math.max(0, Math.min(100, brightness));
        System.out.println("Theater Lights: Dimming to " + this.brightness + "%");
    }
    
    public int getBrightness() {
        return brightness;
    }
}

class Screen {
    public void up() {
        System.out.println("Screen: Raising screen");
    }
    
    public void down() {
        System.out.println("Screen: Lowering screen");
    }
}

class PopcornPopper {
    private boolean popping;
    
    public void on() {
        System.out.println("Popcorn Popper: Powering on");
    }
    
    public void off() {
        System.out.println("Popcorn Popper: Powering off");
        popping = false;
    }
    
    public void pop() {
        popping = true;
        System.out.println("Popcorn Popper: Popping corn!");
    }
    
    public boolean isPopping() {
        return popping;
    }
}

// Facade
class HomeTheaterFacade {
    private Amplifier amplifier;
    private Tuner tuner;
    private DVDPlayer dvdPlayer;
    private Projector projector;
    private TheaterLights lights;
    private Screen screen;
    private PopcornPopper popper;
    
    public HomeTheaterFacade(Amplifier amplifier, Tuner tuner, DVDPlayer dvdPlayer,
                           Projector projector, TheaterLights lights, Screen screen,
                           PopcornPopper popper) {
        this.amplifier = amplifier;
        this.tuner = tuner;
        this.dvdPlayer = dvdPlayer;
        this.projector = projector;
        this.lights = lights;
        this.screen = screen;
        this.popper = popper;
    }
    
    public void watchMovie(String movie) {
        System.out.println("=== STARTING MOVIE MODE ===");
        System.out.println("Get ready to watch a movie...\n");
        
        // Step 1: Prepare the environment
        System.out.println("--- Setting up environment ---");
        lights.dim(10); // Dim lights for movie experience
        screen.down(); // Lower the projection screen
        
        // Step 2: Setup audio/video equipment
        System.out.println("\n--- Setting up audio/video ---");
        projector.on();
        projector.wideScreenMode();
        projector.setInput("DVD");
        
        amplifier.on();
        amplifier.setDvd(dvdPlayer);
        amplifier.setSurroundSound();
        amplifier.setVolume(25); // Comfortable movie volume
        
        // Step 3: Start the movie
        System.out.println("\n--- Starting playback ---");
        dvdPlayer.on();
        dvdPlayer.play(movie);
        
        System.out.println("\n🎬 Movie '" + movie + "' is now playing! Enjoy!");
    }
    
    public void endMovie() {
        System.out.println("\n=== ENDING MOVIE MODE ===");
        
        System.out.println("--- Stopping playback ---");
        dvdPlayer.stop();
        dvdPlayer.eject();
        dvdPlayer.off();
        
        System.out.println("\n--- Shutting down equipment ---");
        amplifier.off();
        projector.off();
        
        System.out.println("\n--- Restoring environment ---");
        lights.on(); // Turn lights back on
        screen.up(); // Raise the screen
        
        System.out.println("🏠 Home theater system shut down. Hope you enjoyed the movie!");
    }
    
    public void listenToRadio(String station) {
        System.out.println("=== STARTING RADIO MODE ===");
        
        System.out.println("--- Setting up for radio ---");
        lights.on(); // Full lights for radio mode
        screen.up(); // Screen not needed for radio
        
        amplifier.on();
        amplifier.setStereoSound(); // Stereo is fine for radio
        amplifier.setVolume(15); // Lower volume for background music
        
        tuner.on();
        tuner.setFM();
        tuner.setFrequency(station);
        
        System.out.println("📻 Now playing radio station " + station);
    }
    
    public void stopRadio() {
        System.out.println("\n=== STOPPING RADIO MODE ===");
        
        tuner.off();
        amplifier.off();
        
        System.out.println("📻 Radio stopped.");
    }
    
    public void makePopcorn() {
        System.out.println("=== MAKING POPCORN ===");
        
        popper.on();
        popper.pop();
        
        System.out.println("🍿 Popcorn is ready! Perfect for movie time!");
    }
    
    public void partyMode() {
        System.out.println("=== STARTING PARTY MODE ===");
        
        // Bright lights for a party
        lights.on();
        screen.up(); // Screen up and out of the way
        
        // Loud music setup
        amplifier.on();
        amplifier.setStereoSound();
        amplifier.setVolume(50); // Party volume!
        
        tuner.on();
        tuner.setFM();
        tuner.setFrequency("101.5 FM"); // Popular music station
        
        System.out.println("🎉 Party mode activated! Let's dance!");
    }
    
    public void sleepMode() {
        System.out.println("=== ACTIVATING SLEEP MODE ===");
        
        // Turn everything off
        dvdPlayer.off();
        amplifier.off();
        tuner.off();
        projector.off();
        popper.off();
        
        // Very dim lights for night time
        lights.dim(2);
        screen.up();
        
        System.out.println("😴 Sleep mode activated. Good night!");
    }
    
    public void emergencyShutdown() {
        System.out.println("🚨 EMERGENCY SHUTDOWN - Turning off all equipment");
        
        dvdPlayer.off();
        amplifier.off();
        tuner.off();
        projector.off();
        popper.off();
        lights.off();
        
        System.out.println("⚡ All equipment safely shut down.");
    }
    
    public void getSystemStatus() {
        System.out.println("=== SYSTEM STATUS ===");
        System.out.println("🔊 Amplifier volume: " + amplifier.getVolume());
        System.out.println("💡 Light brightness: " + lights.getBrightness() + "%");
        System.out.println("🍿 Popcorn maker: " + (popper.isPopping() ? "Popping" : "Idle"));
        System.out.println("===================");
    }
}

// Client usage demonstration
class HomeTheaterUser {
    public static void demonstrateHomeTheater() {
        // Create all subsystem components
        Amplifier amplifier = new Amplifier();
        Tuner tuner = new Tuner();
        DVDPlayer dvdPlayer = new DVDPlayer();
        Projector projector = new Projector();
        TheaterLights lights = new TheaterLights();
        Screen screen = new Screen();
        PopcornPopper popper = new PopcornPopper();
        
        // Create facade
        HomeTheaterFacade homeTheater = new HomeTheaterFacade(
            amplifier, tuner, dvdPlayer, projector, lights, screen, popper);
        
        // Without facade, user would need to:
        // 1. Know how to operate each component
        // 2. Remember the correct sequence of operations
        // 3. Coordinate settings between components
        // 4. Handle error conditions and cleanup
        
        // With facade, user gets simple, high-level operations:
        
        // Make some popcorn first
        homeTheater.makePopcorn();
        
        // Watch a movie
        homeTheater.watchMovie("The Avengers");
        
        // Check system status during movie
        homeTheater.getSystemStatus();
        
        // End movie
        homeTheater.endMovie();
        
        // Switch to radio
        homeTheater.listenToRadio("105.7 FM");
        
        // Switch to party mode
        homeTheater.partyMode();
        
        // Finally, activate sleep mode
        homeTheater.sleepMode();
    }
}
```

### Example 3: Banking System

```java
// Subsystem components
class AccountManager {
    private Map<String, Double> accounts;
    private Map<String, List<String>> transactionHistory;
    
    public AccountManager() {
        this.accounts = new HashMap<>();
        this.transactionHistory = new HashMap<>();
        initializeAccounts();
    }
    
    private void initializeAccounts() {
        accounts.put("12345", 1000.0);
        accounts.put("67890", 2500.0);
        accounts.put("11111", 500.0);
        
        // Initialize transaction history
        transactionHistory.put("12345", new ArrayList<>());
        transactionHistory.put("67890", new ArrayList<>());
        transactionHistory.put("11111", new ArrayList<>());
    }
    
    public boolean accountExists(String accountNumber) {
        boolean exists = accounts.containsKey(accountNumber);
        System.out.println("AccountManager: Checking account " + accountNumber + " - " + 
                         (exists ? "EXISTS" : "NOT FOUND"));
        return exists;
    }
    
    public double getBalance(String accountNumber) {
        double balance = accounts.getOrDefault(accountNumber, 0.0);
        System.out.println("AccountManager: Account " + accountNumber + " balance: $" + 
                         String.format("%.2f", balance));
        return balance;
    }
    
    public boolean debit(String accountNumber, double amount) {
        if (!accountExists(accountNumber)) return false;
        
        double currentBalance = accounts.get(accountNumber);
        if (currentBalance >= amount) {
            accounts.put(accountNumber, currentBalance - amount);
            addTransactionHistory(accountNumber, "DEBIT: $" + String.format("%.2f", amount));
            System.out.println("AccountManager: Debited $" + String.format("%.2f", amount) + 
                             " from account " + accountNumber);
            return true;
        } else {
            System.out.println("AccountManager: Insufficient funds in account " + accountNumber);
            return false;
        }
    }
    
    public void credit(String accountNumber, double amount) {
        if (!accountExists(accountNumber)) return;
        
        double currentBalance = accounts.get(accountNumber);
        accounts.put(accountNumber, currentBalance + amount);
        addTransactionHistory(accountNumber, "CREDIT: $" + String.format("%.2f", amount));
        System.out.println("AccountManager: Credited $" + String.format("%.2f", amount) + 
                         " to account " + accountNumber);
    }
    
    private void addTransactionHistory(String accountNumber, String transaction) {
        transactionHistory.computeIfAbsent(accountNumber, k -> new ArrayList<>())
                          .add(new Date() + " - " + transaction);
    }
    
    public List<String> getTransactionHistory(String accountNumber) {
        return new ArrayList<>(transactionHistory.getOrDefault(accountNumber, new ArrayList<>()));
    }
}

class SecurityManager {
    private Map<String, String> accountPins;
    private Map<String, Integer> failedAttempts;
    private Set<String> lockedAccounts;
    private static final int MAX_ATTEMPTS = 3;
    
    public SecurityManager() {
        this.accountPins = new HashMap<>();
        this.failedAttempts = new HashMap<>();
        this.lockedAccounts = new HashSet<>();
        initializePins();
    }
    
    private void initializePins() {
        accountPins.put("12345", "1234");
        accountPins.put("67890", "5678");
        accountPins.put("11111", "0000");
    }
    
    public boolean validatePin(String accountNumber, String pin) {
        if (lockedAccounts.contains(accountNumber)) {
            System.out.println("SecurityManager: Account " + accountNumber + " is LOCKED");
            return false;
        }
        
        String correctPin = accountPins.get(accountNumber);
        if (correctPin != null && correctPin.equals(pin)) {
            System.out.println("SecurityManager: PIN validation SUCCESSFUL for account " + accountNumber);
            failedAttempts.put(accountNumber, 0); // Reset failed attempts
            return true;
        } else {
            int attempts = failedAttempts.getOrDefault(accountNumber, 0) + 1;
            failedAttempts.put(accountNumber, attempts);
            
            System.out.println("SecurityManager: PIN validation FAILED for account " + accountNumber + 
                             " (Attempt " + attempts + "/" + MAX_ATTEMPTS + ")");
            
            if (attempts >= MAX_ATTEMPTS) {
                lockedAccounts.add(accountNumber);
                System.out.println("SecurityManager: Account " + accountNumber + " has been LOCKED due to multiple failed attempts");
            }
            
            return false;
        }
    }
    
    public boolean isAccountLocked(String accountNumber) {
        return lockedAccounts.contains(accountNumber);
    }
    
    public void unlockAccount(String accountNumber, String adminKey) {
        if ("ADMIN_OVERRIDE_2023".equals(adminKey)) {
            lockedAccounts.remove(accountNumber);
            failedAttempts.put(accountNumber, 0);
            System.out.println("SecurityManager: Account " + accountNumber + " has been UNLOCKED by admin");
        } else {
            System.out.println("SecurityManager: Invalid admin key provided");
        }
    }
    
    public void logSecurityEvent(String accountNumber, String event) {
        System.out.println("SecurityManager: SECURITY LOG - Account " + accountNumber + ": " + event);
    }
}

class TransactionManager {
    private List<Transaction> transactionLog;
    private int transactionCounter;
    
    public TransactionManager() {
        this.transactionLog = new ArrayList<>();
        this.transactionCounter = 1000;
    }
    
    private static class Transaction {
        String id;
        String type;
        String fromAccount;
        String toAccount;
        double amount;
        Date timestamp;
        String status;
        
        Transaction(String id, String type, String fromAccount, String toAccount, 
                   double amount, String status) {
            this.id = id;
            this.type = type;
            this.fromAccount = fromAccount;
            this.toAccount = toAccount;
            this.amount = amount;
            this.timestamp = new Date();
            this.status = status;
        }
        
        @Override
        public String toString() {
            return String.format("[%s] %s: %s -> %s, $%.2f, Status: %s", 
                               id, type, fromAccount, toAccount, amount, status);
        }
    }
    
    public String createTransaction(String type, String fromAccount, String toAccount, double amount) {
        String transactionId = "TXN" + (++transactionCounter);
        Transaction transaction = new Transaction(transactionId, type, fromAccount, toAccount, amount, "PENDING");
        transactionLog.add(transaction);
        
        System.out.println("TransactionManager: Created transaction " + transactionId);
        return transactionId;
    }
    
    public void completeTransaction(String transactionId) {
        for (Transaction txn : transactionLog) {
            if (txn.id.equals(transactionId)) {
                txn.status = "COMPLETED";
                System.out.println("TransactionManager: Transaction " + transactionId + " completed");
                return;
            }
        }
    }
    
    public void failTransaction(String transactionId, String reason) {
        for (Transaction txn : transactionLog) {
            if (txn.id.equals(transactionId)) {
                txn.status = "FAILED - " + reason;
                System.out.println("TransactionManager: Transaction " + transactionId + " failed: " + reason);
                return;
            }
        }
    }
    
    public List<String> getTransactionHistory() {
        return transactionLog.stream()
                           .map(Transaction::toString)
                           .collect(java.util.stream.Collectors.toList());
    }
}

class NotificationService {
    public void sendSMS(String phoneNumber, String message) {
        System.out.println("NotificationService: SMS to " + phoneNumber + ": " + message);
    }
    
    public void sendEmail(String email, String subject, String message) {
        System.out.println("NotificationService: Email to " + email);
        System.out.println("  Subject: " + subject);
        System.out.println("  Message: " + message);
    }
    
    public void sendPushNotification(String deviceId, String message) {
        System.out.println("NotificationService: Push notification to device " + deviceId + ": " + message);
    }
}

class AuditLogger {
    private List<String> auditLog;
    
    public AuditLogger() {
        this.auditLog = new ArrayList<>();
    }
    
    public void log(String event) {
        String logEntry = new Date() + " - " + event;
        auditLog.add(logEntry);
        System.out.println("AuditLogger: " + logEntry);
    }
    
    public void logTransaction(String accountNumber, String operation, double amount) {
        log("Account " + accountNumber + " - " + operation + " $" + String.format("%.2f", amount));
    }
    
    public List<String> getAuditLog() {
        return new ArrayList<>(auditLog);
    }
}

// Facade
class BankingFacade {
    private AccountManager accountManager;
    private SecurityManager securityManager;
    private TransactionManager transactionManager;
    private NotificationService notificationService;
    private AuditLogger auditLogger;
    
    // Customer contact information
    private Map<String, CustomerInfo> customerInfo;
    
    private static class CustomerInfo {
        String phone;
        String email;
        String deviceId;
        
        CustomerInfo(String phone, String email, String deviceId) {
            this.phone = phone;
            this.email = email;
            this.deviceId = deviceId;
        }
    }
    
    public BankingFacade() {
        this.accountManager = new AccountManager();
        this.securityManager = new SecurityManager();
        this.transactionManager = new TransactionManager();
        this.notificationService = new NotificationService();
        this.auditLogger = new AuditLogger();
        
        initializeCustomerInfo();
    }
    
    private void initializeCustomerInfo() {
        customerInfo = new HashMap<>();
        customerInfo.put("12345", new CustomerInfo("555-0123", "john@email.com", "device123"));
        customerInfo.put("67890", new CustomerInfo("555-0456", "jane@email.com", "device456"));
        customerInfo.put("11111", new CustomerInfo("555-0789", "bob@email.com", "device789"));
    }
    
    public boolean login(String accountNumber, String pin) {
        System.out.println("=== ATM LOGIN ===");
        System.out.println("Account: " + accountNumber);
        
        auditLogger.log("Login attempt for account " + accountNumber);
        
        if (!accountManager.accountExists(accountNumber)) {
            System.out.println("❌ Account not found");
            return false;
        }
        
        if (securityManager.isAccountLocked(accountNumber)) {
            System.out.println("❌ Account is locked");
            return false;
        }
        
        boolean authenticated = securityManager.validatePin(accountNumber, pin);
        
        if (authenticated) {
            System.out.println("✅ Login successful");
            auditLogger.log("Successful login for account " + accountNumber);
            
            // Send login notification
            CustomerInfo info = customerInfo.get(accountNumber);
            if (info != null) {
                notificationService.sendPushNotification(info.deviceId, 
                    "You have successfully logged into your account");
            }
        } else {
            System.out.println("❌ Login failed");
            auditLogger.log("Failed login attempt for account " + accountNumber);
        }
        
        return authenticated;
    }
    
    public double checkBalance(String accountNumber) {
        System.out.println("\n=== BALANCE INQUIRY ===");
        
        double balance = accountManager.getBalance(accountNumber);
        auditLogger.logTransaction(accountNumber, "BALANCE_INQUIRY", 0);
        
        System.out.println("💰 Current balance: $" + String.format("%.2f", balance));
        return balance;
    }
    
    public boolean withdraw(String accountNumber, double amount) {
        System.out.println("\n=== WITHDRAWAL ===");
        System.out.println("Withdrawing $" + String.format("%.2f", amount));
        
        String transactionId = transactionManager.createTransaction("WITHDRAWAL", accountNumber, null, amount);
        
        boolean success = accountManager.debit(accountNumber, amount);
        
        if (success) {
            transactionManager.completeTransaction(transactionId);
            auditLogger.logTransaction(accountNumber, "WITHDRAWAL", amount);
            
            System.out.println("✅ Withdrawal successful");
            System.out.println("💰 New balance: $" + String.format("%.2f", accountManager.getBalance(accountNumber)));
            
            // Send notification
            CustomerInfo info = customerInfo.get(accountNumber);
            if (info != null) {
                notificationService.sendSMS(info.phone, 
                    "Withdrawal of $" + String.format("%.2f", amount) + " completed. " +
                    "Balance: $" + String.format("%.2f", accountManager.getBalance(accountNumber)));
            }
            
        } else {
            transactionManager.failTransaction(transactionId, "Insufficient funds");
            auditLogger.log("Failed withdrawal attempt for account " + accountNumber + " - Insufficient funds");
            System.out.println("❌ Withdrawal failed - Insufficient funds");
        }
        
        return success;
    }
    
    public void deposit(String accountNumber, double amount) {
        System.out.println("\n=== DEPOSIT ===");
        System.out.println("Depositing $" + String.format("%.2f", amount));
        
        String transactionId = transactionManager.createTransaction("DEPOSIT", null, accountNumber, amount);
        
        accountManager.credit(accountNumber, amount);
        transactionManager.completeTransaction(transactionId);
        auditLogger.logTransaction(accountNumber, "DEPOSIT", amount);
        
        System.out.println("✅ Deposit successful");
        System.out.println("💰 New balance: $" + String.format("%.2f", accountManager.getBalance(accountNumber)));
        
        // Send notification
        CustomerInfo info = customerInfo.get(accountNumber);
        if (info != null) {
            notificationService.sendEmail(info.email, "Deposit Confirmation",
                "Your deposit of $" + String.format("%.2f", amount) + " has been processed. " +
                "New balance: $" + String.format("%.2f", accountManager.getBalance(accountNumber)));
        }
    }
    
    public boolean transfer(String fromAccount, String toAccount, double amount) {
        System.out.println("\n=== MONEY TRANSFER ===");
        System.out.println("Transferring $" + String.format("%.2f", amount) + 
                         " from " + fromAccount + " to " + toAccount);
        
        if (!accountManager.accountExists(toAccount)) {
            System.out.println("❌ Destination account not found");
            return false;
        }
        
        String transactionId = transactionManager.createTransaction("TRANSFER", fromAccount, toAccount, amount);
        
        boolean success = accountManager.debit(fromAccount, amount);
        
        if (success) {
            accountManager.credit(toAccount, amount);
            transactionManager.completeTransaction(transactionId);
            
            auditLogger.log("Transfer completed: " + fromAccount + " -> " + toAccount + 
                           " Amount: $" + String.format("%.2f", amount));
            
            System.out.println("✅ Transfer successful");
            System.out.println("💰 Your new balance: $" + String.format("%.2f", accountManager.getBalance(fromAccount)));
            
            // Send notifications to both parties
            CustomerInfo fromInfo = customerInfo.get(fromAccount);
            CustomerInfo toInfo = customerInfo.get(toAccount);
            
            if (fromInfo != null) {
                notificationService.sendSMS(fromInfo.phone,
                    "Transfer of $" + String.format("%.2f", amount) + " to account " + toAccount + " completed.");
            }
            
            if (toInfo != null) {
                notificationService.sendPushNotification(toInfo.deviceId,
                    "You received $" + String.format("%.2f", amount) + " from account " + fromAccount);
            }
            
        } else {
            transactionManager.failTransaction(transactionId, "Insufficient funds");
            System.out.println("❌ Transfer failed - Insufficient funds");
        }
        
        return success;
    }
    
    public void printStatement(String accountNumber) {
        System.out.println("\n=== ACCOUNT STATEMENT ===");
        System.out.println("Account: " + accountNumber);
        System.out.println("Current Balance: $" + String.format("%.2f", accountManager.getBalance(accountNumber)));
        
        System.out.println("\nTransaction History:");
        List<String> transactions = accountManager.getTransactionHistory(accountNumber);
        
        if (transactions.isEmpty()) {
            System.out.println("No transactions found");
        } else {
            for (String transaction : transactions) {
                System.out.println("  " + transaction);
            }
        }
        
        auditLogger.log("Statement printed for account " + accountNumber);
        System.out.println("========================");
    }
    
    public void logout(String accountNumber) {
        System.out.println("\n=== LOGOUT ===");
        auditLogger.log("Logout for account " + accountNumber);
        System.out.println("Thank you for using our banking services!");
        System.out.println("Please remember to take your card.");
    }
    
    public void emergencyCardBlock(String accountNumber, String reason) {
        System.out.println("\n=== EMERGENCY CARD BLOCK ===");
        securityManager.logSecurityEvent(accountNumber, "EMERGENCY_BLOCK: " + reason);
        auditLogger.log("Emergency card block for account " + accountNumber + " - Reason: " + reason);
        
        // In a real system, this would disable the card
        System.out.println("🚨 Card blocked successfully for security reasons");
        
        CustomerInfo info = customerInfo.get(accountNumber);
        if (info != null) {
            notificationService.sendSMS(info.phone, "ALERT: Your card has been blocked for security reasons. Contact customer service.");
        }
    }
}

// Client usage demonstration
class BankCustomer {
    public static void demonstrateBankingOperations() {
        BankingFacade bank = new BankingFacade();
        
        // Without facade, customer would need to:
        // 1. Interact with AccountManager, SecurityManager, TransactionManager separately
        // 2. Handle security validation manually
        // 3. Coordinate notifications and audit logging
        // 4. Manage transaction states and error conditions
        
        // With facade, customer gets simple banking operations:
        
        String accountNumber = "12345";
        String pin = "1234";
        
        // Login
        if (bank.login(accountNumber, pin)) {
            // Check balance
            bank.checkBalance(accountNumber);
            
            // Make a deposit
            bank.deposit(accountNumber, 500.0);
            
            // Make a withdrawal
            bank.withdraw(accountNumber, 200.0);
            
            // Transfer money
            bank.transfer(accountNumber, "67890", 300.0);
            
            // Print statement
            bank.printStatement(accountNumber);
            
            // Logout
            bank.logout(accountNumber);
        }
        
        // Demonstrate failed login
        System.out.println("\n--- Demonstrating failed login ---");
        bank.login("12345", "9999"); // Wrong PIN
        bank.login("12345", "8888"); // Wrong PIN again
        bank.login("12345", "7777"); // This should lock the account
    }
}
```

## Real-World Applications

### 1. **Software Libraries and APIs**
- Database connection frameworks (JDBC, Hibernate)
- Web service clients (REST API wrappers)
- File I/O operations (BufferedReader, FileInputStream wrappers)

### 2. **Operating System Interfaces**
- System call wrappers
- Device driver interfaces
- Network protocol stacks

### 3. **Enterprise Applications**
- Service layer in multi-tier architectures
- Integration with legacy systems
- Microservices API gateways

### 4. **User Interface Systems**
- Form validation and submission
- Complex widget interactions
- Multi-step wizards and workflows

### 5. **Gaming Systems**
- Game engine interfaces
- Physics simulation wrappers
- Audio/graphics subsystem facades

## Advantages and Disadvantages

### Advantages
1. **Simplified Interface**: Reduces complexity for clients
2. **Loose Coupling**: Clients don't depend on subsystem internals
3. **Layered Architecture**: Supports clean separation of concerns
4. **Easy to Use**: Provides convenient methods for common tasks
5. **Flexibility**: Clients can still access subsystem directly if needed
6. **Maintainability**: Changes to subsystem don't affect clients using facade

### Disadvantages
1. **Additional Layer**: Introduces another layer of abstraction
2. **Potential Performance Overhead**: Extra method calls through facade
3. **Limited Functionality**: May not expose all subsystem capabilities
4. **God Object Risk**: Facade can become too large and complex
5. **Not Always Needed**: Simple subsystems may not benefit from facade

## Facade vs Other Patterns

### Facade vs Adapter Pattern
- **Facade**: Simplifies interface to existing subsystems
- **Adapter**: Changes interface to make incompatible interfaces work together
- Facade simplifies; Adapter converts

### Facade vs Mediator Pattern
- **Facade**: Unidirectional - clients use facade, facade uses subsystem
- **Mediator**: Bidirectional - colleagues communicate through mediator
- Facade simplifies access; Mediator manages communication

### Facade vs Abstract Factory Pattern
- **Facade**: Simplifies usage of existing objects
- **Abstract Factory**: Creates families of related objects
- Different purposes - simplification vs creation

## Best Practices

1. **Keep Facade Simple**: Don't add business logic to the facade
2. **Don't Hide Everything**: Allow direct access to subsystem when needed
3. **Single Responsibility**: Each facade should serve one specific purpose
4. **Avoid God Objects**: Don't put everything in one massive facade
5. **Document the Interface**: Clearly explain what the facade provides
6. **Handle Errors Gracefully**: Provide meaningful error messages to clients
7. **Consider Performance**: Monitor overhead introduced by facade layer
8. **Version Management**: Plan for evolution of both facade and subsystems
9. **Testing**: Test facade independently from subsystems using mocks
10. **Thread Safety**: Ensure facade is thread-safe if used in concurrent environments

## Common Pitfalls to Avoid

1. **Leaky Abstraction**: Don't expose subsystem details through facade interface
2. **Tight Coupling**: Facade shouldn't be tightly coupled to specific subsystem implementations
3. **Feature Creep**: Don't add unrelated functionality to the facade
4. **Poor Error Handling**: Don't just pass through subsystem exceptions without context
5. **Inadequate Testing**: Test facade behavior, not just subsystem delegation
6. **Over-Simplification**: Don't remove necessary flexibility for the sake of simplicity

## Conclusion

The Facade Pattern is an excellent tool for simplifying complex subsystems and providing clean, easy-to-use interfaces. It's particularly valuable when dealing with legacy systems, third-party libraries, or complex internal APIs.

Key takeaways:
- Use Facade to simplify complex subsystem interactions
- It provides a unified interface to a set of subsystem interfaces
- Great for reducing coupling between clients and subsystems
- Allows for both simplified access and direct access when needed
- Essential for creating clean, maintainable architectures

Remember: The Facade Pattern is about making complex things simple, not about hiding functionality. The goal is to provide a convenient interface while still allowing access to the full power of the subsystem when needed.