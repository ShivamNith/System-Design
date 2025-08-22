package org.example.designPatterns.builderPattern;

import java.util.ArrayList;
import java.util.List;

/**
 * Demonstration of Builder Pattern with Computer Configuration System
 * 
 * This example shows how to use the Builder pattern to construct complex objects
 * with many optional parameters in a readable and maintainable way.
 */

// Product Class - Computer
class Computer {
    // Required parameters
    private final String cpu;
    private final String ram;
    
    // Optional parameters
    private final String storage;
    private final String gpu;
    private final String motherboard;
    private final String powerSupply;
    private final String coolingSystem;
    private final boolean isWifiEnabled;
    private final boolean isBluetoothEnabled;
    private final int usbPorts;
    private final String operatingSystem;
    private final String warranty;
    private final List<String> accessories;
    
    private Computer(Builder builder) {
        // Required parameters validation
        if (builder.cpu == null || builder.cpu.trim().isEmpty()) {
            throw new IllegalStateException("CPU is required");
        }
        if (builder.ram == null || builder.ram.trim().isEmpty()) {
            throw new IllegalStateException("RAM is required");
        }
        
        // Set required parameters
        this.cpu = builder.cpu;
        this.ram = builder.ram;
        
        // Set optional parameters with defaults
        this.storage = builder.storage;
        this.gpu = builder.gpu;
        this.motherboard = builder.motherboard;
        this.powerSupply = builder.powerSupply;
        this.coolingSystem = builder.coolingSystem;
        this.isWifiEnabled = builder.isWifiEnabled;
        this.isBluetoothEnabled = builder.isBluetoothEnabled;
        this.usbPorts = builder.usbPorts;
        this.operatingSystem = builder.operatingSystem;
        this.warranty = builder.warranty;
        this.accessories = new ArrayList<>(builder.accessories);
        
        validateConfiguration();
    }
    
    private void validateConfiguration() {
        if (usbPorts < 0) {
            throw new IllegalStateException("USB ports cannot be negative");
        }
        
        // Check GPU and gaming requirements
        if (gpu != null && gpu.contains("RTX") && !powerSupply.contains("850W") && !powerSupply.contains("1000W")) {
            System.out.println("Warning: High-end GPU may need more powerful PSU");
        }
        
        // Check cooling requirements
        if (cpu.contains("i9") && coolingSystem.equals("Stock Cooler")) {
            System.out.println("Warning: High-end CPU may need better cooling");
        }
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    // Static nested Builder class
    public static class Builder {
        // Required parameters
        private String cpu;
        private String ram;
        
        // Optional parameters with default values
        private String storage = "256GB SSD";
        private String gpu = "Integrated Graphics";
        private String motherboard = "Standard ATX";
        private String powerSupply = "500W 80+ Bronze";
        private String coolingSystem = "Stock Cooler";
        private boolean isWifiEnabled = false;
        private boolean isBluetoothEnabled = false;
        private int usbPorts = 4;
        private String operatingSystem = "No OS";
        private String warranty = "1 Year Standard";
        private List<String> accessories = new ArrayList<>();
        
        public Builder() {}
        
        // Required parameter methods
        public Builder cpu(String cpu) {
            this.cpu = cpu;
            return this;
        }
        
        public Builder ram(String ram) {
            this.ram = ram;
            return this;
        }
        
        // Optional parameter methods
        public Builder storage(String storage) {
            this.storage = storage;
            return this;
        }
        
        public Builder gpu(String gpu) {
            this.gpu = gpu;
            return this;
        }
        
        public Builder motherboard(String motherboard) {
            this.motherboard = motherboard;
            return this;
        }
        
        public Builder powerSupply(String powerSupply) {
            this.powerSupply = powerSupply;
            return this;
        }
        
        public Builder coolingSystem(String coolingSystem) {
            this.coolingSystem = coolingSystem;
            return this;
        }
        
        public Builder enableWifi() {
            this.isWifiEnabled = true;
            return this;
        }
        
        public Builder disableWifi() {
            this.isWifiEnabled = false;
            return this;
        }
        
        public Builder enableBluetooth() {
            this.isBluetoothEnabled = true;
            return this;
        }
        
        public Builder disableBluetooth() {
            this.isBluetoothEnabled = false;
            return this;
        }
        
        public Builder usbPorts(int count) {
            this.usbPorts = count;
            return this;
        }
        
        public Builder operatingSystem(String os) {
            this.operatingSystem = os;
            return this;
        }
        
        public Builder warranty(String warranty) {
            this.warranty = warranty;
            return this;
        }
        
        public Builder addAccessory(String accessory) {
            this.accessories.add(accessory);
            return this;
        }
        
        // Preset configurations for common use cases
        public Builder gamingPreset() {
            return this.gpu("NVIDIA GeForce RTX 4080")
                      .storage("1TB NVMe SSD")
                      .powerSupply("850W 80+ Gold")
                      .coolingSystem("Liquid Cooling AIO")
                      .motherboard("Gaming Z790 ATX")
                      .usbPorts(8)
                      .enableWifi()
                      .enableBluetooth()
                      .addAccessory("RGB Lighting Kit")
                      .addAccessory("Gaming Keyboard")
                      .addAccessory("Gaming Mouse");
        }
        
        public Builder officePreset() {
            return this.gpu("Integrated Graphics")
                      .storage("512GB SSD")
                      .powerSupply("450W 80+ Bronze")
                      .coolingSystem("Stock Cooler")
                      .motherboard("Business B760 mATX")
                      .usbPorts(6)
                      .enableWifi()
                      .operatingSystem("Windows 11 Pro")
                      .addAccessory("Office Keyboard")
                      .addAccessory("Office Mouse");
        }
        
        public Builder workstationPreset() {
            return this.gpu("NVIDIA RTX A4000")
                      .storage("2TB NVMe SSD + 4TB HDD")
                      .powerSupply("1000W 80+ Platinum")
                      .coolingSystem("Professional Liquid Cooling")
                      .motherboard("Workstation W680 ATX")
                      .usbPorts(12)
                      .enableWifi()
                      .enableBluetooth()
                      .operatingSystem("Windows 11 Pro for Workstations")
                      .warranty("5 Years Pro Support")
                      .addAccessory("Professional Monitor")
                      .addAccessory("Mechanical Keyboard")
                      .addAccessory("Precision Mouse");
        }
        
        public Builder budgetPreset() {
            return this.gpu("Integrated Graphics")
                      .storage("256GB SSD")
                      .powerSupply("400W 80+ White")
                      .coolingSystem("Stock Cooler")
                      .motherboard("Basic mATX")
                      .usbPorts(4)
                      .operatingSystem("Linux Ubuntu")
                      .warranty("1 Year Basic");
        }
        
        public Computer build() {
            return new Computer(this);
        }
    }
    
    // Getters
    public String getCpu() { return cpu; }
    public String getRam() { return ram; }
    public String getStorage() { return storage; }
    public String getGpu() { return gpu; }
    public String getMotherboard() { return motherboard; }
    public String getPowerSupply() { return powerSupply; }
    public String getCoolingSystem() { return coolingSystem; }
    public boolean isWifiEnabled() { return isWifiEnabled; }
    public boolean isBluetoothEnabled() { return isBluetoothEnabled; }
    public int getUsbPorts() { return usbPorts; }
    public String getOperatingSystem() { return operatingSystem; }
    public String getWarranty() { return warranty; }
    public List<String> getAccessories() { return new ArrayList<>(accessories); }
    
    public double calculatePrice() {
        double basePrice = 500.0; // Base system price
        
        // Add costs based on components
        if (cpu.contains("i9")) basePrice += 400;
        else if (cpu.contains("i7")) basePrice += 250;
        else if (cpu.contains("i5")) basePrice += 150;
        
        if (ram.contains("32GB")) basePrice += 200;
        else if (ram.contains("16GB")) basePrice += 100;
        
        if (gpu.contains("RTX 4080")) basePrice += 1000;
        else if (gpu.contains("RTX A4000")) basePrice += 800;
        else if (gpu.contains("GTX")) basePrice += 300;
        
        if (storage.contains("2TB")) basePrice += 300;
        else if (storage.contains("1TB")) basePrice += 150;
        
        basePrice += accessories.size() * 50;
        
        return basePrice;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Computer Configuration:\n");
        sb.append("├── CPU: ").append(cpu).append("\n");
        sb.append("├── RAM: ").append(ram).append("\n");
        sb.append("├── Storage: ").append(storage).append("\n");
        sb.append("├── GPU: ").append(gpu).append("\n");
        sb.append("├── Motherboard: ").append(motherboard).append("\n");
        sb.append("├── Power Supply: ").append(powerSupply).append("\n");
        sb.append("├── Cooling: ").append(coolingSystem).append("\n");
        sb.append("├── WiFi: ").append(isWifiEnabled ? "Enabled" : "Disabled").append("\n");
        sb.append("├── Bluetooth: ").append(isBluetoothEnabled ? "Enabled" : "Disabled").append("\n");
        sb.append("├── USB Ports: ").append(usbPorts).append("\n");
        sb.append("├── OS: ").append(operatingSystem).append("\n");
        sb.append("├── Warranty: ").append(warranty).append("\n");
        if (!accessories.isEmpty()) {
            sb.append("├── Accessories:\n");
            for (int i = 0; i < accessories.size(); i++) {
                sb.append("│   ├── ").append(accessories.get(i)).append("\n");
            }
        }
        sb.append("└── Total Price: $").append(String.format("%.2f", calculatePrice()));
        
        return sb.toString();
    }
}

// Director class for orchestrating complex builds
class ComputerDirector {
    
    public Computer buildBudgetOfficeComputer() {
        return Computer.builder()
                .cpu("Intel Core i3-13100")
                .ram("8GB DDR4-3200")
                .officePreset()
                .warranty("2 Years Basic")
                .build();
    }
    
    public Computer buildHighEndGamingComputer() {
        return Computer.builder()
                .cpu("Intel Core i9-13900K")
                .ram("32GB DDR5-5600")
                .gamingPreset()
                .operatingSystem("Windows 11 Home")
                .warranty("3 Years Premium Gaming")
                .addAccessory("VR Headset")
                .addAccessory("Racing Wheel")
                .build();
    }
    
    public Computer buildWorkstationComputer() {
        return Computer.builder()
                .cpu("Intel Xeon W-2295")
                .ram("64GB ECC DDR4")
                .workstationPreset()
                .build();
    }
    
    public Computer buildCustomComputer(String cpuType, String ramType, String useCase) {
        Computer.Builder builder = Computer.builder()
                .cpu(cpuType)
                .ram(ramType);
        
        switch (useCase.toLowerCase()) {
            case "gaming":
                return builder.gamingPreset().build();
            case "office":
                return builder.officePreset().build();
            case "workstation":
                return builder.workstationPreset().build();
            case "budget":
                return builder.budgetPreset().build();
            default:
                return builder.build();
        }
    }
}

// Demonstration class
public class ComputerBuilderExample {
    
    public static void main(String[] args) {
        System.out.println("=== Builder Pattern Demonstration - Computer Configuration System ===\n");
        
        // Example 1: Basic computer with minimal configuration
        System.out.println("1. Basic Computer (Required parameters only):");
        Computer basicComputer = Computer.builder()
                .cpu("Intel Core i5-13400")
                .ram("16GB DDR4-3200")
                .build();
        System.out.println(basicComputer);
        System.out.println();
        
        // Example 2: Gaming computer using preset
        System.out.println("2. Gaming Computer (Using gaming preset):");
        Computer gamingComputer = Computer.builder()
                .cpu("Intel Core i7-13700K")
                .ram("32GB DDR5-5600")
                .gamingPreset()
                .operatingSystem("Windows 11 Gaming Edition")
                .warranty("3 Years Gaming Pro")
                .build();
        System.out.println(gamingComputer);
        System.out.println();
        
        // Example 3: Office computer using preset
        System.out.println("3. Office Computer (Using office preset):");
        Computer officeComputer = Computer.builder()
                .cpu("Intel Core i5-13400")
                .ram("16GB DDR4-3200")
                .officePreset()
                .warranty("2 Years Business")
                .build();
        System.out.println(officeComputer);
        System.out.println();
        
        // Example 4: Custom configuration with all options
        System.out.println("4. Custom Workstation (All options specified):");
        Computer customWorkstation = Computer.builder()
                .cpu("Intel Xeon W-3275")
                .ram("128GB ECC DDR4")
                .gpu("NVIDIA RTX A6000")
                .storage("4TB NVMe SSD RAID")
                .motherboard("Professional W680 EATX")
                .powerSupply("1200W 80+ Titanium")
                .coolingSystem("Custom Loop Liquid Cooling")
                .usbPorts(16)
                .enableWifi()
                .enableBluetooth()
                .operatingSystem("Windows 11 Pro for Workstations")
                .warranty("5 Years Premium Support")
                .addAccessory("Dual 4K Monitors")
                .addAccessory("Professional Keyboard")
                .addAccessory("Graphics Tablet")
                .addAccessory("Studio Speakers")
                .build();
        System.out.println(customWorkstation);
        System.out.println();
        
        // Example 5: Using Director for common configurations
        System.out.println("5. Using Director Pattern:");
        ComputerDirector director = new ComputerDirector();
        
        System.out.println("Budget Office Computer:");
        Computer budgetOffice = director.buildBudgetOfficeComputer();
        System.out.println(budgetOffice);
        System.out.println();
        
        System.out.println("High-End Gaming Computer:");
        Computer highEndGaming = director.buildHighEndGamingComputer();
        System.out.println(highEndGaming);
        System.out.println();
        
        // Example 6: Error handling - missing required parameters
        System.out.println("6. Error Handling Demonstration:");
        try {
            Computer invalidComputer = Computer.builder()
                    .ram("16GB DDR4-3200")
                    // Missing CPU - should throw exception
                    .build();
        } catch (IllegalStateException e) {
            System.out.println("Caught expected error: " + e.getMessage());
        }
        
        try {
            Computer anotherInvalidComputer = Computer.builder()
                    .cpu("Intel Core i5-13400")
                    // Missing RAM - should throw exception
                    .build();
        } catch (IllegalStateException e) {
            System.out.println("Caught expected error: " + e.getMessage());
        }
        System.out.println();
        
        // Example 7: Price comparison
        System.out.println("7. Price Comparison:");
        Computer budget = Computer.builder()
                .cpu("Intel Core i3-13100")
                .ram("8GB DDR4-3200")
                .budgetPreset()
                .build();
        
        Computer midRange = Computer.builder()
                .cpu("Intel Core i5-13400")
                .ram("16GB DDR4-3200")
                .officePreset()
                .build();
        
        Computer highEnd = Computer.builder()
                .cpu("Intel Core i9-13900K")
                .ram("32GB DDR5-5600")
                .gamingPreset()
                .build();
        
        System.out.printf("Budget Computer: $%.2f%n", budget.calculatePrice());
        System.out.printf("Mid-Range Computer: $%.2f%n", midRange.calculatePrice());
        System.out.printf("High-End Computer: $%.2f%n", highEnd.calculatePrice());
        System.out.println();
        
        // Example 8: Method chaining flexibility
        System.out.println("8. Method Chaining Flexibility:");
        Computer flexibleBuild = Computer.builder()
                .cpu("AMD Ryzen 7 7700X")
                .ram("32GB DDR5-5600")
                .enableWifi()
                .enableBluetooth()
                .usbPorts(8)
                .addAccessory("Webcam")
                .addAccessory("Microphone")
                .operatingSystem("Windows 11 Pro")
                .warranty("3 Years")
                .build();
        
        System.out.println(flexibleBuild);
        System.out.println();
        
        System.out.println("=== Demonstration Complete ===");
    }
}