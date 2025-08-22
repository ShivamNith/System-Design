package org.example.designPatterns.facadePattern;

import java.util.*;

/**
 * Computer Startup Example - Facade Pattern Implementation
 * 
 * This example demonstrates the Facade Pattern by simplifying the complex
 * computer startup process. The facade hides the intricate details of
 * BIOS initialization, hardware checks, operating system loading, and
 * driver installation behind simple methods.
 */

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

// Facade - Simplifies the complex computer startup process
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
    
    /**
     * Simplified computer startup process
     * Hides the complexity of coordinating multiple subsystems
     */
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
    
    /**
     * Simplified computer shutdown process
     */
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
    
    /**
     * Restart computer - combines shutdown and startup
     */
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
    
    /**
     * Safe mode startup - minimal driver loading
     */
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
    
    /**
     * System diagnostics - tests all major components
     */
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

/**
 * Client code demonstration
 * Shows how the facade pattern simplifies computer operations
 */
public class ComputerStartupExample {
    public static void main(String[] args) {
        System.out.println("Computer Startup Facade Pattern Example");
        System.out.println("========================================\n");
        
        // Create computer facade
        ComputerFacade computer = new ComputerFacade();
        
        // Without facade, client would need to:
        // 1. Create and coordinate CPU, Memory, HardDrive, BIOS, OS instances
        // 2. Know the exact sequence of operations for startup
        // 3. Handle all the complex interactions between components
        // 4. Manage state transitions and error conditions
        
        // With facade, client simply calls high-level operations:
        
        // Normal startup
        computer.startComputer();
        
        // Run system diagnostics
        computer.runDiagnostics();
        
        // Restart the system
        computer.restartComputer();
        
        // Boot in safe mode
        computer.enterSafeMode();
        
        // Shutdown
        computer.shutdownComputer();
        
        System.out.println("Example completed!");
    }
}