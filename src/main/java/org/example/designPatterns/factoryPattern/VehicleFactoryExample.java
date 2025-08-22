package org.example.designPatterns.factoryPattern;

import java.util.*;

interface Vehicle {
    void manufacture();
    void testDrive();
    String getSpecifications();
    double getPrice();
    String getVehicleType();
}

class Car implements Vehicle {
    private String model;
    private String engine;
    private int year;
    
    public Car(String model, int year) {
        this.model = model;
        this.year = year;
        this.engine = "V6 Engine";
    }
    
    @Override
    public void manufacture() {
        System.out.println("Manufacturing car: " + model);
        System.out.println("Year: " + year);
        System.out.println("Installing " + engine);
        System.out.println("Adding 4 wheels");
        System.out.println("Setting up car interior");
        System.out.println("Car manufacturing complete!");
    }
    
    @Override
    public void testDrive() {
        System.out.println("Test driving " + model + " on highway");
        System.out.println("Testing acceleration: 0-60 mph in 7 seconds");
        System.out.println("Testing braking system");
        System.out.println("Testing handling and comfort");
    }
    
    @Override
    public String getSpecifications() {
        return String.format("Car - Model: %s, Year: %d, Engine: %s, Seats: 5, Drive: FWD", 
            model, year, engine);
    }
    
    @Override
    public double getPrice() {
        return 25000.00;
    }
    
    @Override
    public String getVehicleType() {
        return "Car";
    }
}

class Motorcycle implements Vehicle {
    private String model;
    private int engineCC;
    private String type;
    
    public Motorcycle(String model, int engineCC) {
        this.model = model;
        this.engineCC = engineCC;
        this.type = engineCC > 500 ? "Sport" : "Standard";
    }
    
    @Override
    public void manufacture() {
        System.out.println("Manufacturing motorcycle: " + model);
        System.out.println("Type: " + type);
        System.out.println("Installing " + engineCC + "cc engine");
        System.out.println("Adding 2 wheels");
        System.out.println("Setting up handlebars and controls");
        System.out.println("Motorcycle manufacturing complete!");
    }
    
    @Override
    public void testDrive() {
        System.out.println("Test driving " + model + " on test track");
        System.out.println("Testing acceleration and top speed");
        System.out.println("Testing cornering ability");
        System.out.println("Testing braking distance");
    }
    
    @Override
    public String getSpecifications() {
        return String.format("Motorcycle - Model: %s, Type: %s, Engine: %dcc, Max Speed: %d mph", 
            model, type, engineCC, engineCC > 500 ? 150 : 100);
    }
    
    @Override
    public double getPrice() {
        return 12000.00 + (engineCC - 500) * 10;
    }
    
    @Override
    public String getVehicleType() {
        return "Motorcycle";
    }
}

class Truck implements Vehicle {
    private String model;
    private int loadCapacity;
    private int wheels;
    
    public Truck(String model, int loadCapacity) {
        this.model = model;
        this.loadCapacity = loadCapacity;
        this.wheels = loadCapacity > 5000 ? 8 : 6;
    }
    
    @Override
    public void manufacture() {
        System.out.println("Manufacturing truck: " + model);
        System.out.println("Load capacity: " + loadCapacity + " kg");
        System.out.println("Installing heavy-duty diesel engine");
        System.out.println("Adding " + wheels + " wheels");
        System.out.println("Setting up cargo area");
        System.out.println("Installing hydraulic systems");
        System.out.println("Truck manufacturing complete!");
    }
    
    @Override
    public void testDrive() {
        System.out.println("Test driving " + model + " with load simulation");
        System.out.println("Testing with " + (loadCapacity * 0.8) + " kg load");
        System.out.println("Testing uphill performance");
        System.out.println("Testing brake performance with load");
    }
    
    @Override
    public String getSpecifications() {
        return String.format("Truck - Model: %s, Load Capacity: %d kg, Wheels: %d, Type: %s", 
            model, loadCapacity, wheels, loadCapacity > 5000 ? "Heavy Duty" : "Medium Duty");
    }
    
    @Override
    public double getPrice() {
        return 45000.00 + (loadCapacity - 3000) * 5;
    }
    
    @Override
    public String getVehicleType() {
        return "Truck";
    }
}

class ElectricCar implements Vehicle {
    private String model;
    private int batteryCapacity;
    private int range;
    
    public ElectricCar(String model, int batteryCapacity) {
        this.model = model;
        this.batteryCapacity = batteryCapacity;
        this.range = batteryCapacity * 5; // Simplified calculation
    }
    
    @Override
    public void manufacture() {
        System.out.println("Manufacturing electric car: " + model);
        System.out.println("Installing " + batteryCapacity + " kWh battery pack");
        System.out.println("Installing electric motors");
        System.out.println("Setting up regenerative braking system");
        System.out.println("Installing charging port");
        System.out.println("Electric car manufacturing complete!");
    }
    
    @Override
    public void testDrive() {
        System.out.println("Test driving " + model + " in EV mode");
        System.out.println("Testing silent operation");
        System.out.println("Testing instant torque delivery");
        System.out.println("Testing range: " + range + " miles");
        System.out.println("Testing fast charging capability");
    }
    
    @Override
    public String getSpecifications() {
        return String.format("Electric Car - Model: %s, Battery: %d kWh, Range: %d miles, Charging: Fast DC", 
            model, batteryCapacity, range);
    }
    
    @Override
    public double getPrice() {
        return 35000.00 + batteryCapacity * 200;
    }
    
    @Override
    public String getVehicleType() {
        return "Electric Car";
    }
}

// Simple Factory Implementation
class VehicleFactory {
    public enum VehicleType {
        CAR, MOTORCYCLE, TRUCK, ELECTRIC_CAR
    }
    
    public static Vehicle createVehicle(VehicleType type, Map<String, Object> params) {
        switch (type) {
            case CAR:
                return new Car(
                    (String) params.getOrDefault("model", "Sedan"),
                    (Integer) params.getOrDefault("year", 2024)
                );
            case MOTORCYCLE:
                return new Motorcycle(
                    (String) params.getOrDefault("model", "Sport"),
                    (Integer) params.getOrDefault("engineCC", 600)
                );
            case TRUCK:
                return new Truck(
                    (String) params.getOrDefault("model", "Hauler"),
                    (Integer) params.getOrDefault("loadCapacity", 5000)
                );
            case ELECTRIC_CAR:
                return new ElectricCar(
                    (String) params.getOrDefault("model", "Model E"),
                    (Integer) params.getOrDefault("batteryCapacity", 75)
                );
            default:
                throw new IllegalArgumentException("Unknown vehicle type: " + type);
        }
    }
    
    public static Vehicle createVehicleFromConfig(String config) {
        String[] parts = config.split(",");
        Map<String, Object> params = new HashMap<>();
        
        VehicleType type = VehicleType.valueOf(parts[0].toUpperCase());
        
        for (int i = 1; i < parts.length; i++) {
            String[] keyValue = parts[i].split("=");
            String key = keyValue[0].trim();
            String value = keyValue[1].trim();
            
            // Try to parse as integer, otherwise keep as string
            try {
                params.put(key, Integer.parseInt(value));
            } catch (NumberFormatException e) {
                params.put(key, value);
            }
        }
        
        return createVehicle(type, params);
    }
}

// Factory Method Pattern Implementation
abstract class VehicleManufacturer {
    protected String manufacturerName;
    
    public VehicleManufacturer(String manufacturerName) {
        this.manufacturerName = manufacturerName;
    }
    
    // Factory Method
    public abstract Vehicle createVehicle(String model);
    
    // Template method using factory method
    public void deliverVehicle(String model, String customerName) {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("VEHICLE ORDER PROCESSING");
        System.out.println("Manufacturer: " + manufacturerName);
        System.out.println("Customer: " + customerName);
        System.out.println("=".repeat(50));
        
        Vehicle vehicle = createVehicle(model);
        
        System.out.println("\n1. MANUFACTURING PHASE:");
        System.out.println("-".repeat(30));
        vehicle.manufacture();
        
        System.out.println("\n2. QUALITY TESTING PHASE:");
        System.out.println("-".repeat(30));
        vehicle.testDrive();
        
        System.out.println("\n3. VEHICLE SPECIFICATIONS:");
        System.out.println("-".repeat(30));
        System.out.println(vehicle.getSpecifications());
        
        System.out.println("\n4. PRICING:");
        System.out.println("-".repeat(30));
        System.out.println("Final Price: $" + String.format("%.2f", vehicle.getPrice()));
        
        System.out.println("\n" + "=".repeat(50));
        System.out.println("VEHICLE DELIVERED SUCCESSFULLY!");
        System.out.println("=".repeat(50) + "\n");
    }
}

class CarManufacturer extends VehicleManufacturer {
    public CarManufacturer() {
        super("Premium Auto Works");
    }
    
    @Override
    public Vehicle createVehicle(String model) {
        return new Car(model, Calendar.getInstance().get(Calendar.YEAR));
    }
}

class MotorcycleManufacturer extends VehicleManufacturer {
    private int defaultCC;
    
    public MotorcycleManufacturer(int defaultCC) {
        super("Speed Bikes Inc.");
        this.defaultCC = defaultCC;
    }
    
    @Override
    public Vehicle createVehicle(String model) {
        return new Motorcycle(model, defaultCC);
    }
}

class TruckManufacturer extends VehicleManufacturer {
    public TruckManufacturer() {
        super("Heavy Industries Corp.");
    }
    
    @Override
    public Vehicle createVehicle(String model) {
        // Default load capacity based on model
        int capacity = model.toLowerCase().contains("heavy") ? 10000 : 5000;
        return new Truck(model, capacity);
    }
}

class ElectricVehicleManufacturer extends VehicleManufacturer {
    public ElectricVehicleManufacturer() {
        super("Future Motors");
    }
    
    @Override
    public Vehicle createVehicle(String model) {
        // Default battery capacity based on model
        int battery = model.toLowerCase().contains("long range") ? 100 : 75;
        return new ElectricCar(model, battery);
    }
}

// Vehicle Dealership using factories
class VehicleDealership {
    private Map<String, VehicleManufacturer> manufacturers;
    private List<Vehicle> inventory;
    
    public VehicleDealership() {
        manufacturers = new HashMap<>();
        inventory = new ArrayList<>();
        
        // Register manufacturers
        manufacturers.put("car", new CarManufacturer());
        manufacturers.put("motorcycle", new MotorcycleManufacturer(600));
        manufacturers.put("truck", new TruckManufacturer());
        manufacturers.put("electric", new ElectricVehicleManufacturer());
    }
    
    public void orderVehicle(String type, String model, String customer) {
        VehicleManufacturer manufacturer = manufacturers.get(type.toLowerCase());
        if (manufacturer == null) {
            System.out.println("Error: Unknown vehicle type - " + type);
            return;
        }
        
        manufacturer.deliverVehicle(model, customer);
    }
    
    public void addToInventory(String type, String model) {
        VehicleManufacturer manufacturer = manufacturers.get(type.toLowerCase());
        if (manufacturer != null) {
            Vehicle vehicle = manufacturer.createVehicle(model);
            inventory.add(vehicle);
            System.out.println("Added to inventory: " + vehicle.getSpecifications());
        }
    }
    
    public void showInventory() {
        System.out.println("\n=== CURRENT INVENTORY ===");
        if (inventory.isEmpty()) {
            System.out.println("No vehicles in inventory");
        } else {
            for (int i = 0; i < inventory.size(); i++) {
                Vehicle v = inventory.get(i);
                System.out.println((i + 1) + ". " + v.getSpecifications() + 
                    " - $" + String.format("%.2f", v.getPrice()));
            }
        }
        System.out.println();
    }
}

public class VehicleFactoryExample {
    public static void main(String[] args) {
        System.out.println("=== VEHICLE FACTORY PATTERN EXAMPLE ===\n");
        
        // 1. Simple Factory Example
        System.out.println("1. SIMPLE FACTORY PATTERN:");
        System.out.println("-".repeat(40));
        
        Map<String, Object> carParams = new HashMap<>();
        carParams.put("model", "Sedan Deluxe");
        carParams.put("year", 2024);
        
        Vehicle car = VehicleFactory.createVehicle(VehicleFactory.VehicleType.CAR, carParams);
        System.out.println("Created: " + car.getSpecifications());
        
        Map<String, Object> bikeParams = new HashMap<>();
        bikeParams.put("model", "Ninja");
        bikeParams.put("engineCC", 1000);
        
        Vehicle bike = VehicleFactory.createVehicle(VehicleFactory.VehicleType.MOTORCYCLE, bikeParams);
        System.out.println("Created: " + bike.getSpecifications());
        
        // Create from config string
        Vehicle truck = VehicleFactory.createVehicleFromConfig("truck,model=BigHauler,loadCapacity=8000");
        System.out.println("Created from config: " + truck.getSpecifications());
        
        // 2. Factory Method Pattern Example
        System.out.println("\n2. FACTORY METHOD PATTERN:");
        System.out.println("-".repeat(40));
        
        CarManufacturer carFactory = new CarManufacturer();
        carFactory.deliverVehicle("Luxury Sedan", "John Smith");
        
        MotorcycleManufacturer bikeFactory = new MotorcycleManufacturer(750);
        bikeFactory.deliverVehicle("Cruiser", "Jane Doe");
        
        // 3. Dealership Example (combining patterns)
        System.out.println("\n3. DEALERSHIP SYSTEM:");
        System.out.println("-".repeat(40));
        
        VehicleDealership dealership = new VehicleDealership();
        
        // Add vehicles to inventory
        dealership.addToInventory("car", "Compact");
        dealership.addToInventory("electric", "Long Range Model");
        dealership.addToInventory("truck", "Heavy Duty");
        
        dealership.showInventory();
        
        // Customer orders
        dealership.orderVehicle("electric", "Standard Range", "Alice Johnson");
        
        // 4. Interactive Demo
        System.out.println("\n4. INTERACTIVE VEHICLE CREATION:");
        System.out.println("-".repeat(40));
        
        Scanner scanner = new Scanner(System.in);
        System.out.println("Available vehicle types:");
        System.out.println("1. Car");
        System.out.println("2. Motorcycle");
        System.out.println("3. Truck");
        System.out.println("4. Electric Car");
        System.out.print("Select vehicle type (1-4): ");
        
        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        
        System.out.print("Enter model name: ");
        String modelName = scanner.nextLine();
        
        VehicleManufacturer selectedManufacturer = null;
        
        switch (choice) {
            case 1:
                selectedManufacturer = new CarManufacturer();
                break;
            case 2:
                selectedManufacturer = new MotorcycleManufacturer(600);
                break;
            case 3:
                selectedManufacturer = new TruckManufacturer();
                break;
            case 4:
                selectedManufacturer = new ElectricVehicleManufacturer();
                break;
            default:
                System.out.println("Invalid choice!");
                scanner.close();
                return;
        }
        
        System.out.print("Enter customer name: ");
        String customerName = scanner.nextLine();
        
        selectedManufacturer.deliverVehicle(modelName, customerName);
        
        scanner.close();
    }
}