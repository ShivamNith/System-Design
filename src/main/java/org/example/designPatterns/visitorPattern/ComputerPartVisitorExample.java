package org.example.designPatterns.visitorPattern;

import java.util.ArrayList;
import java.util.List;

/**
 * Computer Part Visitor Example
 * 
 * This example demonstrates the Visitor Pattern applied to computer parts.
 * Different visitors can perform operations like displaying information,
 * calculating total cost, checking compatibility, generating reports, etc.
 * 
 * Key Features:
 * - Hierarchical structure of computer components
 * - Multiple operations without modifying component classes
 * - Composite pattern integration (Computer contains multiple parts)
 * - Real-world business logic for computer assembly
 */

// Visitor interface
interface ComputerPartVisitor {
    void visit(CPU cpu);
    void visit(GPU gpu);
    void visit(Memory memory);
    void visit(Storage storage);
    void visit(Motherboard motherboard);
    void visit(PowerSupply powerSupply);
    void visit(Computer computer);
}

// Element interface
interface ComputerPart {
    void accept(ComputerPartVisitor visitor);
    String getName();
    String getBrand();
    double getPrice();
}

// Abstract base class for common functionality
abstract class Component implements ComputerPart {
    protected String name;
    protected String brand;
    protected double price;
    protected String model;
    
    public Component(String name, String brand, double price, String model) {
        this.name = name;
        this.brand = brand;
        this.price = price;
        this.model = model;
    }
    
    @Override
    public String getName() { return name; }
    
    @Override
    public String getBrand() { return brand; }
    
    @Override
    public double getPrice() { return price; }
    
    public String getModel() { return model; }
}

// Concrete Components
class CPU extends Component {
    private int cores;
    private double clockSpeed; // in GHz
    private String socket;
    private int powerConsumption; // in Watts
    private String architecture;
    
    public CPU(String name, String brand, double price, String model,
               int cores, double clockSpeed, String socket, 
               int powerConsumption, String architecture) {
        super(name, brand, price, model);
        this.cores = cores;
        this.clockSpeed = clockSpeed;
        this.socket = socket;
        this.powerConsumption = powerConsumption;
        this.architecture = architecture;
    }
    
    @Override
    public void accept(ComputerPartVisitor visitor) {
        visitor.visit(this);
    }
    
    public int getCores() { return cores; }
    public double getClockSpeed() { return clockSpeed; }
    public String getSocket() { return socket; }
    public int getPowerConsumption() { return powerConsumption; }
    public String getArchitecture() { return architecture; }
    
    @Override
    public String toString() {
        return "CPU{" + brand + " " + name + ", " + cores + " cores, " + 
               clockSpeed + "GHz, " + socket + "}";
    }
}

class GPU extends Component {
    private int memorySize; // in GB
    private String memoryType;
    private int powerConsumption;
    private boolean rayTracingSupport;
    private String connectorType;
    
    public GPU(String name, String brand, double price, String model,
               int memorySize, String memoryType, int powerConsumption,
               boolean rayTracingSupport, String connectorType) {
        super(name, brand, price, model);
        this.memorySize = memorySize;
        this.memoryType = memoryType;
        this.powerConsumption = powerConsumption;
        this.rayTracingSupport = rayTracingSupport;
        this.connectorType = connectorType;
    }
    
    @Override
    public void accept(ComputerPartVisitor visitor) {
        visitor.visit(this);
    }
    
    public int getMemorySize() { return memorySize; }
    public String getMemoryType() { return memoryType; }
    public int getPowerConsumption() { return powerConsumption; }
    public boolean hasRayTracingSupport() { return rayTracingSupport; }
    public String getConnectorType() { return connectorType; }
    
    @Override
    public String toString() {
        return "GPU{" + brand + " " + name + ", " + memorySize + "GB " + 
               memoryType + ", " + powerConsumption + "W}";
    }
}

class Memory extends Component {
    private int capacity; // in GB
    private String type; // DDR4, DDR5, etc.
    private int speed; // in MHz
    private int modules; // number of memory sticks
    
    public Memory(String name, String brand, double price, String model,
                  int capacity, String type, int speed, int modules) {
        super(name, brand, price, model);
        this.capacity = capacity;
        this.type = type;
        this.speed = speed;
        this.modules = modules;
    }
    
    @Override
    public void accept(ComputerPartVisitor visitor) {
        visitor.visit(this);
    }
    
    public int getCapacity() { return capacity; }
    public String getType() { return type; }
    public int getSpeed() { return speed; }
    public int getModules() { return modules; }
    
    @Override
    public String toString() {
        return "Memory{" + brand + " " + capacity + "GB " + type + 
               "-" + speed + ", " + modules + " modules}";
    }
}

class Storage extends Component {
    private int capacity; // in GB
    private String type; // SSD, HDD
    private String interfaceType; // SATA, NVMe, etc.
    private int readSpeed; // in MB/s
    private int writeSpeed; // in MB/s
    
    public Storage(String name, String brand, double price, String model,
                   int capacity, String type, String interfaceType,
                   int readSpeed, int writeSpeed) {
        super(name, brand, price, model);
        this.capacity = capacity;
        this.type = type;
        this.interfaceType = interfaceType;
        this.readSpeed = readSpeed;
        this.writeSpeed = writeSpeed;
    }
    
    @Override
    public void accept(ComputerPartVisitor visitor) {
        visitor.visit(this);
    }
    
    public int getCapacity() { return capacity; }
    public String getType() { return type; }
    public String getInterfaceType() { return interfaceType; }
    public int getReadSpeed() { return readSpeed; }
    public int getWriteSpeed() { return writeSpeed; }
    
    @Override
    public String toString() {
        return "Storage{" + brand + " " + capacity + "GB " + type + 
               " " + interfaceType + ", " + readSpeed + "/" + writeSpeed + " MB/s}";
    }
}

class Motherboard extends Component {
    private String chipset;
    private String cpuSocket;
    private int memorySlots;
    private String memoryType;
    private List<String> expansionSlots;
    
    public Motherboard(String name, String brand, double price, String model,
                      String chipset, String cpuSocket, int memorySlots,
                      String memoryType) {
        super(name, brand, price, model);
        this.chipset = chipset;
        this.cpuSocket = cpuSocket;
        this.memorySlots = memorySlots;
        this.memoryType = memoryType;
        this.expansionSlots = new ArrayList<>();
    }
    
    @Override
    public void accept(ComputerPartVisitor visitor) {
        visitor.visit(this);
    }
    
    public String getChipset() { return chipset; }
    public String getCpuSocket() { return cpuSocket; }
    public int getMemorySlots() { return memorySlots; }
    public String getMemoryType() { return memoryType; }
    public List<String> getExpansionSlots() { return expansionSlots; }
    
    public void addExpansionSlot(String slot) {
        expansionSlots.add(slot);
    }
    
    @Override
    public String toString() {
        return "Motherboard{" + brand + " " + name + ", " + chipset + 
               ", " + cpuSocket + ", " + memorySlots + " RAM slots}";
    }
}

class PowerSupply extends Component {
    private int wattage;
    private String efficiency; // 80+ Bronze, Gold, etc.
    private boolean modular;
    private List<String> connectors;
    
    public PowerSupply(String name, String brand, double price, String model,
                      int wattage, String efficiency, boolean modular) {
        super(name, brand, price, model);
        this.wattage = wattage;
        this.efficiency = efficiency;
        this.modular = modular;
        this.connectors = new ArrayList<>();
    }
    
    @Override
    public void accept(ComputerPartVisitor visitor) {
        visitor.visit(this);
    }
    
    public int getWattage() { return wattage; }
    public String getEfficiency() { return efficiency; }
    public boolean isModular() { return modular; }
    public List<String> getConnectors() { return connectors; }
    
    public void addConnector(String connector) {
        connectors.add(connector);
    }
    
    @Override
    public String toString() {
        return "PowerSupply{" + brand + " " + wattage + "W " + 
               efficiency + ", " + (modular ? "Modular" : "Non-modular") + "}";
    }
}

// Composite class
class Computer implements ComputerPart {
    private List<ComputerPart> parts;
    private String name;
    private String brand;
    
    public Computer(String name, String brand) {
        this.name = name;
        this.brand = brand;
        this.parts = new ArrayList<>();
    }
    
    public void addPart(ComputerPart part) {
        parts.add(part);
    }
    
    public void removePart(ComputerPart part) {
        parts.remove(part);
    }
    
    public List<ComputerPart> getParts() {
        return new ArrayList<>(parts);
    }
    
    @Override
    public void accept(ComputerPartVisitor visitor) {
        for (ComputerPart part : parts) {
            part.accept(visitor);
        }
        visitor.visit(this);
    }
    
    @Override
    public String getName() { return name; }
    
    @Override
    public String getBrand() { return brand; }
    
    @Override
    public double getPrice() {
        return parts.stream().mapToDouble(ComputerPart::getPrice).sum();
    }
    
    @Override
    public String toString() {
        return "Computer{" + brand + " " + name + ", " + parts.size() + " parts}";
    }
}

// Concrete Visitor - Display Information
class DisplayVisitor implements ComputerPartVisitor {
    private int indentLevel = 0;
    
    @Override
    public void visit(CPU cpu) {
        printIndented("CPU: " + cpu.getBrand() + " " + cpu.getName());
        printIndented("  Cores: " + cpu.getCores() + ", Clock: " + cpu.getClockSpeed() + "GHz");
        printIndented("  Socket: " + cpu.getSocket() + ", Power: " + cpu.getPowerConsumption() + "W");
        printIndented("  Architecture: " + cpu.getArchitecture());
        printIndented("  Price: $" + String.format("%.2f", cpu.getPrice()));
    }
    
    @Override
    public void visit(GPU gpu) {
        printIndented("GPU: " + gpu.getBrand() + " " + gpu.getName());
        printIndented("  Memory: " + gpu.getMemorySize() + "GB " + gpu.getMemoryType());
        printIndented("  Power: " + gpu.getPowerConsumption() + "W");
        printIndented("  Ray Tracing: " + (gpu.hasRayTracingSupport() ? "Yes" : "No"));
        printIndented("  Connector: " + gpu.getConnectorType());
        printIndented("  Price: $" + String.format("%.2f", gpu.getPrice()));
    }
    
    @Override
    public void visit(Memory memory) {
        printIndented("Memory: " + memory.getBrand() + " " + memory.getCapacity() + "GB");
        printIndented("  Type: " + memory.getType() + "-" + memory.getSpeed());
        printIndented("  Modules: " + memory.getModules());
        printIndented("  Price: $" + String.format("%.2f", memory.getPrice()));
    }
    
    @Override
    public void visit(Storage storage) {
        printIndented("Storage: " + storage.getBrand() + " " + storage.getCapacity() + "GB " + storage.getType());
        printIndented("  Interface: " + storage.getInterfaceType());
        printIndented("  Speed: " + storage.getReadSpeed() + "/" + storage.getWriteSpeed() + " MB/s");
        printIndented("  Price: $" + String.format("%.2f", storage.getPrice()));
    }
    
    @Override
    public void visit(Motherboard motherboard) {
        printIndented("Motherboard: " + motherboard.getBrand() + " " + motherboard.getName());
        printIndented("  Chipset: " + motherboard.getChipset());
        printIndented("  CPU Socket: " + motherboard.getCpuSocket());
        printIndented("  Memory: " + motherboard.getMemorySlots() + " slots, " + motherboard.getMemoryType());
        printIndented("  Price: $" + String.format("%.2f", motherboard.getPrice()));
    }
    
    @Override
    public void visit(PowerSupply powerSupply) {
        printIndented("Power Supply: " + powerSupply.getBrand() + " " + powerSupply.getWattage() + "W");
        printIndented("  Efficiency: " + powerSupply.getEfficiency());
        printIndented("  Modular: " + (powerSupply.isModular() ? "Yes" : "No"));
        printIndented("  Price: $" + String.format("%.2f", powerSupply.getPrice()));
    }
    
    @Override
    public void visit(Computer computer) {
        System.out.println("=== " + computer.getBrand() + " " + computer.getName() + " ===");
        System.out.println("Total Parts: " + computer.getParts().size());
        System.out.println("Total Price: $" + String.format("%.2f", computer.getPrice()));
    }
    
    private void printIndented(String text) {
        System.out.println("  ".repeat(indentLevel) + text);
    }
}

// Concrete Visitor - Price Calculator
class PriceCalculatorVisitor implements ComputerPartVisitor {
    private double totalPrice = 0;
    private double cpuPrice = 0;
    private double gpuPrice = 0;
    private double memoryPrice = 0;
    private double storagePrice = 0;
    private double motherboardPrice = 0;
    private double powerSupplyPrice = 0;
    
    @Override
    public void visit(CPU cpu) {
        cpuPrice += cpu.getPrice();
        totalPrice += cpu.getPrice();
    }
    
    @Override
    public void visit(GPU gpu) {
        gpuPrice += gpu.getPrice();
        totalPrice += gpu.getPrice();
    }
    
    @Override
    public void visit(Memory memory) {
        memoryPrice += memory.getPrice();
        totalPrice += memory.getPrice();
    }
    
    @Override
    public void visit(Storage storage) {
        storagePrice += storage.getPrice();
        totalPrice += storage.getPrice();
    }
    
    @Override
    public void visit(Motherboard motherboard) {
        motherboardPrice += motherboard.getPrice();
        totalPrice += motherboard.getPrice();
    }
    
    @Override
    public void visit(PowerSupply powerSupply) {
        powerSupplyPrice += powerSupply.getPrice();
        totalPrice += powerSupply.getPrice();
    }
    
    @Override
    public void visit(Computer computer) {
        // Computer price is calculated from its parts
    }
    
    public void printPriceBreakdown() {
        System.out.println("\n=== Price Breakdown ===");
        System.out.println("CPU: $" + String.format("%.2f", cpuPrice));
        System.out.println("GPU: $" + String.format("%.2f", gpuPrice));
        System.out.println("Memory: $" + String.format("%.2f", memoryPrice));
        System.out.println("Storage: $" + String.format("%.2f", storagePrice));
        System.out.println("Motherboard: $" + String.format("%.2f", motherboardPrice));
        System.out.println("Power Supply: $" + String.format("%.2f", powerSupplyPrice));
        System.out.println("-".repeat(25));
        System.out.println("Total: $" + String.format("%.2f", totalPrice));
        
        // Calculate percentages
        System.out.println("\n=== Cost Distribution ===");
        printPercentage("CPU", cpuPrice);
        printPercentage("GPU", gpuPrice);
        printPercentage("Memory", memoryPrice);
        printPercentage("Storage", storagePrice);
        printPercentage("Motherboard", motherboardPrice);
        printPercentage("Power Supply", powerSupplyPrice);
    }
    
    private void printPercentage(String component, double price) {
        double percentage = (price / totalPrice) * 100;
        System.out.println(component + ": " + String.format("%.1f", percentage) + "%");
    }
    
    public double getTotalPrice() { return totalPrice; }
}

// Concrete Visitor - Compatibility Checker
class CompatibilityCheckerVisitor implements ComputerPartVisitor {
    private List<String> issues = new ArrayList<>();
    private CPU cpu;
    private GPU gpu;
    private Memory memory;
    private Motherboard motherboard;
    private PowerSupply powerSupply;
    private int totalPowerConsumption = 0;
    
    @Override
    public void visit(CPU cpu) {
        this.cpu = cpu;
        totalPowerConsumption += cpu.getPowerConsumption();
    }
    
    @Override
    public void visit(GPU gpu) {
        this.gpu = gpu;
        totalPowerConsumption += gpu.getPowerConsumption();
    }
    
    @Override
    public void visit(Memory memory) {
        this.memory = memory;
        totalPowerConsumption += 10; // Estimated power consumption for memory
    }
    
    @Override
    public void visit(Storage storage) {
        totalPowerConsumption += 5; // Estimated power consumption for storage
    }
    
    @Override
    public void visit(Motherboard motherboard) {
        this.motherboard = motherboard;
        totalPowerConsumption += 20; // Estimated power consumption for motherboard
    }
    
    @Override
    public void visit(PowerSupply powerSupply) {
        this.powerSupply = powerSupply;
    }
    
    @Override
    public void visit(Computer computer) {
        checkCompatibility();
    }
    
    private void checkCompatibility() {
        // Check CPU and Motherboard socket compatibility
        if (cpu != null && motherboard != null) {
            if (!cpu.getSocket().equals(motherboard.getCpuSocket())) {
                issues.add("CPU socket (" + cpu.getSocket() + 
                          ") is not compatible with motherboard socket (" + 
                          motherboard.getCpuSocket() + ")");
            }
        }
        
        // Check Memory and Motherboard compatibility
        if (memory != null && motherboard != null) {
            if (!memory.getType().equals(motherboard.getMemoryType())) {
                issues.add("Memory type (" + memory.getType() + 
                          ") is not compatible with motherboard (" + 
                          motherboard.getMemoryType() + ")");
            }
            
            if (memory.getModules() > motherboard.getMemorySlots()) {
                issues.add("Memory modules (" + memory.getModules() + 
                          ") exceed motherboard slots (" + 
                          motherboard.getMemorySlots() + ")");
            }
        }
        
        // Check Power Supply adequacy
        if (powerSupply != null) {
            int recommendedWattage = (int) (totalPowerConsumption * 1.2); // 20% headroom
            if (powerSupply.getWattage() < recommendedWattage) {
                issues.add("Power supply (" + powerSupply.getWattage() + 
                          "W) may be insufficient. Recommended: " + 
                          recommendedWattage + "W");
            }
        }
        
        // Check for missing essential components
        if (cpu == null) issues.add("Missing CPU");
        if (motherboard == null) issues.add("Missing Motherboard");
        if (memory == null) issues.add("Missing Memory");
        if (powerSupply == null) issues.add("Missing Power Supply");
    }
    
    public void printCompatibilityReport() {
        System.out.println("\n=== Compatibility Report ===");
        System.out.println("Total Power Consumption: " + totalPowerConsumption + "W");
        
        if (issues.isEmpty()) {
            System.out.println("✓ All components are compatible!");
        } else {
            System.out.println("⚠ Found " + issues.size() + " compatibility issues:");
            for (String issue : issues) {
                System.out.println("- " + issue);
            }
        }
    }
    
    public boolean isCompatible() {
        return issues.isEmpty();
    }
}

// Concrete Visitor - Performance Estimator
class PerformanceEstimatorVisitor implements ComputerPartVisitor {
    private int cpuScore = 0;
    private int gpuScore = 0;
    private int memoryScore = 0;
    private int storageScore = 0;
    private int overallScore = 0;
    
    @Override
    public void visit(CPU cpu) {
        // Simple scoring based on cores and clock speed
        cpuScore = (int) (cpu.getCores() * cpu.getClockSpeed() * 100);
        if ("Intel".equals(cpu.getBrand())) cpuScore += 50;
        if ("AMD".equals(cpu.getBrand())) cpuScore += 60;
    }
    
    @Override
    public void visit(GPU gpu) {
        // Simple scoring based on memory and features
        gpuScore = gpu.getMemorySize() * 100;
        if (gpu.hasRayTracingSupport()) gpuScore += 200;
        if ("NVIDIA".equals(gpu.getBrand())) gpuScore += 100;
        if ("AMD".equals(gpu.getBrand())) gpuScore += 80;
    }
    
    @Override
    public void visit(Memory memory) {
        // Scoring based on capacity and speed
        memoryScore = memory.getCapacity() * 10 + memory.getSpeed() / 10;
    }
    
    @Override
    public void visit(Storage storage) {
        // Scoring based on type and speed
        if ("SSD".equals(storage.getType())) {
            storageScore = storage.getReadSpeed() / 10;
        } else {
            storageScore = storage.getReadSpeed() / 20; // HDD penalty
        }
    }
    
    @Override
    public void visit(Motherboard motherboard) {
        // Motherboard doesn't directly affect performance score
    }
    
    @Override
    public void visit(PowerSupply powerSupply) {
        // Power supply doesn't directly affect performance score
    }
    
    @Override
    public void visit(Computer computer) {
        calculateOverallScore();
    }
    
    private void calculateOverallScore() {
        overallScore = (int) (cpuScore * 0.3 + gpuScore * 0.4 + 
                             memoryScore * 0.2 + storageScore * 0.1);
    }
    
    public void printPerformanceReport() {
        System.out.println("\n=== Performance Estimate ===");
        System.out.println("CPU Score: " + cpuScore);
        System.out.println("GPU Score: " + gpuScore);
        System.out.println("Memory Score: " + memoryScore);
        System.out.println("Storage Score: " + storageScore);
        System.out.println("-".repeat(25));
        System.out.println("Overall Score: " + overallScore);
        
        // Performance categories
        if (overallScore > 2000) {
            System.out.println("Performance Level: High-End Gaming/Workstation");
        } else if (overallScore > 1000) {
            System.out.println("Performance Level: Mid-Range Gaming");
        } else if (overallScore > 500) {
            System.out.println("Performance Level: Budget Gaming/Productivity");
        } else {
            System.out.println("Performance Level: Basic Office Use");
        }
    }
    
    public int getOverallScore() { return overallScore; }
}

// Concrete Visitor - Assembly Guide Generator
class AssemblyGuideVisitor implements ComputerPartVisitor {
    private List<String> assemblySteps = new ArrayList<>();
    private List<String> requiredTools = new ArrayList<>();
    private List<String> warnings = new ArrayList<>();
    
    @Override
    public void visit(CPU cpu) {
        assemblySteps.add("Install CPU into motherboard socket " + cpu.getSocket());
        assemblySteps.add("Apply thermal paste and install CPU cooler");
        requiredTools.add("Thermal paste");
        
        if (cpu.getPowerConsumption() > 150) {
            warnings.add("High-power CPU - ensure adequate cooling");
        }
    }
    
    @Override
    public void visit(GPU gpu) {
        assemblySteps.add("Install GPU into PCIe x16 slot");
        assemblySteps.add("Connect GPU power cables (" + gpu.getConnectorType() + ")");
        
        if (gpu.isFragile()) {
            warnings.add("Handle GPU carefully - fragile components");
        }
        
        if (gpu.getPowerConsumption() > 250) {
            warnings.add("High-power GPU - ensure adequate PSU and cooling");
        }
    }
    
    @Override
    public void visit(Memory memory) {
        assemblySteps.add("Install " + memory.getModules() + " memory modules in slots");
        assemblySteps.add("Ensure memory is properly seated and locked");
        
        if (memory.getModules() > 2) {
            warnings.add("Multiple memory modules - check motherboard slot configuration");
        }
    }
    
    @Override
    public void visit(Storage storage) {
        if ("NVMe".equals(storage.getInterfaceType())) {
            assemblySteps.add("Install NVMe SSD directly onto motherboard M.2 slot");
        } else {
            assemblySteps.add("Mount " + storage.getType() + " in drive bay");
            assemblySteps.add("Connect SATA data and power cables");
            requiredTools.add("SATA cables");
        }
    }
    
    @Override
    public void visit(Motherboard motherboard) {
        assemblySteps.add("Install I/O shield in case");
        assemblySteps.add("Mount motherboard in case with standoffs");
        assemblySteps.add("Connect front panel connectors");
        requiredTools.add("Screwdriver set");
        requiredTools.add("Anti-static wrist strap (recommended)");
    }
    
    @Override
    public void visit(PowerSupply powerSupply) {
        assemblySteps.add("Install power supply in case");
        assemblySteps.add("Connect 24-pin motherboard power");
        assemblySteps.add("Connect CPU power (8-pin)");
        
        if (powerSupply.isModular()) {
            assemblySteps.add("Connect required modular cables");
            warnings.add("Modular PSU - only connect needed cables");
        }
    }
    
    @Override
    public void visit(Computer computer) {
        // Final assembly steps
        assemblySteps.add("Connect all remaining power and data cables");
        assemblySteps.add("Perform cable management");
        assemblySteps.add("Close case and connect peripherals");
        assemblySteps.add("Power on and enter BIOS");
        assemblySteps.add("Install operating system");
    }
    
    public void printAssemblyGuide() {
        System.out.println("\n=== Assembly Guide ===");
        
        System.out.println("\nRequired Tools:");
        for (String tool : requiredTools) {
            System.out.println("- " + tool);
        }
        
        System.out.println("\nAssembly Steps:");
        for (int i = 0; i < assemblySteps.size(); i++) {
            System.out.println((i + 1) + ". " + assemblySteps.get(i));
        }
        
        if (!warnings.isEmpty()) {
            System.out.println("\n⚠ Important Warnings:");
            for (String warning : warnings) {
                System.out.println("- " + warning);
            }
        }
    }
}

// Demo class
public class ComputerPartVisitorExample {
    public static void main(String[] args) {
        System.out.println("=== Computer Part Visitor Pattern Example ===\n");
        
        // Create a sample computer
        Computer gamingPC = createSampleComputer();
        
        System.out.println("1. Displaying computer details:");
        DisplayVisitor displayVisitor = new DisplayVisitor();
        gamingPC.accept(displayVisitor);
        
        System.out.println("\n" + "=".repeat(50));
        
        System.out.println("2. Calculating total cost:");
        PriceCalculatorVisitor priceCalculator = new PriceCalculatorVisitor();
        gamingPC.accept(priceCalculator);
        priceCalculator.printPriceBreakdown();
        
        System.out.println("\n" + "=".repeat(50));
        
        System.out.println("3. Checking compatibility:");
        CompatibilityCheckerVisitor compatibilityChecker = new CompatibilityCheckerVisitor();
        gamingPC.accept(compatibilityChecker);
        compatibilityChecker.printCompatibilityReport();
        
        System.out.println("\n" + "=".repeat(50));
        
        System.out.println("4. Estimating performance:");
        PerformanceEstimatorVisitor performanceEstimator = new PerformanceEstimatorVisitor();
        gamingPC.accept(performanceEstimator);
        performanceEstimator.printPerformanceReport();
        
        System.out.println("\n" + "=".repeat(50));
        
        System.out.println("5. Assembly guide:");
        AssemblyGuideVisitor assemblyGuide = new AssemblyGuideVisitor();
        gamingPC.accept(assemblyGuide);
        assemblyGuide.printAssemblyGuide();
    }
    
    private static Computer createSampleComputer() {
        Computer gamingPC = new Computer("Gaming PC", "Custom Build");
        
        // Create CPU
        CPU cpu = new CPU("Core i7-13700K", "Intel", 419.99, "i7-13700K",
                         16, 3.4, "LGA1700", 125, "Raptor Lake");
        
        // Create GPU
        GPU gpu = new GPU("GeForce RTX 4070", "NVIDIA", 599.99, "RTX 4070",
                         12, "GDDR6X", 200, true, "PCIe 4.0");
        
        // Create Memory
        Memory memory = new Memory("Vengeance LPX", "Corsair", 129.99, "CMK32GX4M2E3200C16",
                                  32, "DDR4", 3200, 2);
        
        // Create Storage
        Storage nvmeSSD = new Storage("980 PRO", "Samsung", 149.99, "MZ-V8P1T0B/AM",
                                     1000, "SSD", "NVMe", 7000, 5000);
        Storage hdd = new Storage("Blue", "WD", 54.99, "WD10EZEX",
                                 1000, "HDD", "SATA", 150, 150);
        
        // Create Motherboard
        Motherboard motherboard = new Motherboard("Z790 AORUS ELITE", "Gigabyte", 
                                                  229.99, "Z790 AORUS ELITE",
                                                  "Z790", "LGA1700", 4, "DDR4");
        motherboard.addExpansionSlot("PCIe 5.0 x16");
        motherboard.addExpansionSlot("PCIe 4.0 x16");
        motherboard.addExpansionSlot("PCIe 3.0 x1");
        
        // Create Power Supply
        PowerSupply psu = new PowerSupply("RM750x", "Corsair", 139.99, "CP-9020199-NA",
                                         750, "80+ Gold", true);
        psu.addConnector("24-pin ATX");
        psu.addConnector("8-pin EPS");
        psu.addConnector("PCIe 8-pin");
        
        // Add all parts to computer
        gamingPC.addPart(cpu);
        gamingPC.addPart(gpu);
        gamingPC.addPart(memory);
        gamingPC.addPart(nvmeSSD);
        gamingPC.addPart(hdd);
        gamingPC.addPart(motherboard);
        gamingPC.addPart(psu);
        
        return gamingPC;
    }
}

/*
Key Benefits Demonstrated:

1. **Multiple Operations**: Display, pricing, compatibility, performance, assembly guide
2. **Clean Separation**: Computer part classes focus on data, visitors handle operations
3. **Easy Extension**: New operations (cooling analysis, upgrade recommendations) can be added
4. **Complex Logic**: Each visitor implements sophisticated domain-specific logic
5. **Composite Integration**: Works seamlessly with the composite Computer class

Operations Implemented:
1. Display visitor for detailed information presentation
2. Price calculator with breakdown and cost distribution
3. Compatibility checker for hardware compatibility issues
4. Performance estimator with scoring system
5. Assembly guide generator with tools and warnings

This example shows how the Visitor pattern excels when:
- You have a stable object structure (computer parts don't change often)
- Multiple complex operations need to be performed
- Business logic is domain-specific and changes frequently
- You want to keep data classes clean and focused
*/