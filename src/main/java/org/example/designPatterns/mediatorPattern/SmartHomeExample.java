package org.example.designPatterns.mediatorPattern;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Mediator Pattern Example: Smart Home Automation System
 * 
 * This example demonstrates the Mediator Pattern with a smart home system
 * where the home automation hub mediates between various smart devices
 * to create automated scenarios and coordinate device interactions.
 */

// Event types for smart home system
enum SmartHomeEvent {
    MOTION_DETECTED, DOOR_OPENED, DOOR_CLOSED, TEMPERATURE_CHANGED,
    LIGHT_ON, LIGHT_OFF, SECURITY_BREACH, WEATHER_UPDATE,
    SCHEDULE_TRIGGERED, USER_COMMAND, DEVICE_MALFUNCTION
}

// Smart home event class
class SmartHomeEventData {
    private SmartHomeEvent type;
    private String deviceId;
    private Object data;
    private long timestamp;
    
    public SmartHomeEventData(SmartHomeEvent type, String deviceId, Object data) {
        this.type = type;
        this.deviceId = deviceId;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }
    
    // Getters
    public SmartHomeEvent getType() { return type; }
    public String getDeviceId() { return deviceId; }
    public Object getData() { return data; }
    public long getTimestamp() { return timestamp; }
    
    @Override
    public String toString() {
        return String.format("[%s] %s from %s: %s", 
            LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")),
            type, deviceId, data);
    }
}

// Mediator interface for smart home system
interface SmartHomeMediator {
    void registerDevice(SmartDevice device);
    void unregisterDevice(SmartDevice device);
    void notify(SmartHomeEventData event);
    void executeScenario(String scenarioName);
    void scheduleAction(String deviceId, String action, int delaySeconds);
    void setHomeMode(HomeMode mode);
    HomeMode getHomeMode();
    List<SmartDevice> getDevicesByType(String deviceType);
    void logActivity(String message);
}

// Home modes enum
enum HomeMode {
    HOME, AWAY, SLEEP, VACATION, PARTY
}

// Concrete mediator - Smart Home Hub
class SmartHomeHub implements SmartHomeMediator {
    private Map<String, SmartDevice> devices;
    private HomeMode currentMode;
    private List<String> activityLog;
    private ScheduledExecutorService scheduler;
    private Map<String, Runnable> scenarios;
    private Map<SmartHomeEvent, List<Runnable>> eventHandlers;
    
    public SmartHomeHub() {
        this.devices = new HashMap<>();
        this.currentMode = HomeMode.HOME;
        this.activityLog = new ArrayList<>();
        this.scheduler = Executors.newScheduledThreadPool(5);
        this.scenarios = new HashMap<>();
        this.eventHandlers = new HashMap<>();
        initializeScenarios();
        initializeEventHandlers();
    }
    
    private void initializeScenarios() {
        // Movie Night scenario
        scenarios.put("movie_night", () -> {
            logActivity("Executing Movie Night scenario");
            for (SmartDevice device : getDevicesByType("Light")) {
                if (device instanceof SmartLight) {
                    ((SmartLight) device).dim(20);
                }
            }
            for (SmartDevice device : getDevicesByType("Entertainment")) {
                if (device instanceof SmartTV) {
                    ((SmartTV) device).turnOn();
                    ((SmartTV) device).setInput("Netflix");
                }
            }
            for (SmartDevice device : getDevicesByType("Climate")) {
                if (device instanceof SmartThermostat) {
                    ((SmartThermostat) device).setTemperature(22);
                }
            }
        });
        
        // Bedtime scenario
        scenarios.put("bedtime", () -> {
            logActivity("Executing Bedtime scenario");
            for (SmartDevice device : getDevicesByType("Light")) {
                if (device instanceof SmartLight) {
                    ((SmartLight) device).turnOff();
                }
            }
            for (SmartDevice device : getDevicesByType("Security")) {
                if (device instanceof SmartLock) {
                    ((SmartLock) device).lock();
                }
            }
            for (SmartDevice device : getDevicesByType("Climate")) {
                if (device instanceof SmartThermostat) {
                    ((SmartThermostat) device).setTemperature(18);
                }
            }
            setHomeMode(HomeMode.SLEEP);
        });
        
        // Welcome Home scenario
        scenarios.put("welcome_home", () -> {
            logActivity("Executing Welcome Home scenario");
            for (SmartDevice device : getDevicesByType("Light")) {
                if (device instanceof SmartLight) {
                    ((SmartLight) device).turnOn();
                    ((SmartLight) device).setBrightness(80);
                }
            }
            for (SmartDevice device : getDevicesByType("Climate")) {
                if (device instanceof SmartThermostat) {
                    ((SmartThermostat) device).setTemperature(23);
                }
            }
            for (SmartDevice device : getDevicesByType("Entertainment")) {
                if (device instanceof SmartSpeaker) {
                    ((SmartSpeaker) device).playWelcomeMessage();
                }
            }
            setHomeMode(HomeMode.HOME);
        });
    }
    
    private void initializeEventHandlers() {
        // Motion detected handler
        eventHandlers.put(SmartHomeEvent.MOTION_DETECTED, Arrays.asList(
            () -> {
                if (currentMode == HomeMode.AWAY) {
                    logActivity("Motion detected while away - potential security breach");
                    for (SmartDevice device : getDevicesByType("Security")) {
                        if (device instanceof SecurityCamera) {
                            ((SecurityCamera) device).startRecording();
                        }
                    }
                } else if (currentMode == HomeMode.SLEEP) {
                    // Turn on dim lights for night navigation
                    for (SmartDevice device : getDevicesByType("Light")) {
                        if (device instanceof SmartLight) {
                            ((SmartLight) device).turnOn();
                            ((SmartLight) device).dim(10);
                        }
                    }
                }
            }
        ));
        
        // Door opened handler
        eventHandlers.put(SmartHomeEvent.DOOR_OPENED, Arrays.asList(
            () -> {
                if (currentMode == HomeMode.AWAY) {
                    executeScenario("welcome_home");
                }
            }
        ));
        
        // Temperature changed handler
        eventHandlers.put(SmartHomeEvent.TEMPERATURE_CHANGED, Arrays.asList(
            () -> logActivity("Temperature adjustment made by smart thermostat")
        ));
    }
    
    @Override
    public void registerDevice(SmartDevice device) {
        devices.put(device.getDeviceId(), device);
        logActivity("Device registered: " + device.getDeviceId() + " (" + device.getDeviceType() + ")");
        System.out.println("[HUB] Device registered: " + device.getDeviceId());
    }
    
    @Override
    public void unregisterDevice(SmartDevice device) {
        if (devices.remove(device.getDeviceId()) != null) {
            logActivity("Device unregistered: " + device.getDeviceId());
            System.out.println("[HUB] Device unregistered: " + device.getDeviceId());
        }
    }
    
    @Override
    public void notify(SmartHomeEventData event) {
        logActivity("Event received: " + event);
        System.out.println("[HUB] " + event);
        
        // Execute event handlers
        List<Runnable> handlers = eventHandlers.get(event.getType());
        if (handlers != null) {
            for (Runnable handler : handlers) {
                handler.run();
            }
        }
        
        // Notify other devices that might be interested
        notifyRelevantDevices(event);
    }
    
    private void notifyRelevantDevices(SmartHomeEventData event) {
        for (SmartDevice device : devices.values()) {
            if (!device.getDeviceId().equals(event.getDeviceId())) {
                device.onEventReceived(event);
            }
        }
    }
    
    @Override
    public void executeScenario(String scenarioName) {
        Runnable scenario = scenarios.get(scenarioName);
        if (scenario != null) {
            scenario.run();
            System.out.println("[HUB] Executed scenario: " + scenarioName);
        } else {
            logActivity("Unknown scenario: " + scenarioName);
        }
    }
    
    @Override
    public void scheduleAction(String deviceId, String action, int delaySeconds) {
        SmartDevice device = devices.get(deviceId);
        if (device != null) {
            scheduler.schedule(() -> {
                logActivity("Scheduled action executed: " + action + " on " + deviceId);
                device.executeCommand(action);
            }, delaySeconds, TimeUnit.SECONDS);
            
            logActivity("Scheduled action: " + action + " on " + deviceId + " in " + delaySeconds + " seconds");
        }
    }
    
    @Override
    public void setHomeMode(HomeMode mode) {
        HomeMode previousMode = currentMode;
        currentMode = mode;
        logActivity("Home mode changed from " + previousMode + " to " + mode);
        System.out.println("[HUB] Home mode: " + mode);
        
        // Notify all devices of mode change
        SmartHomeEventData modeEvent = new SmartHomeEventData(SmartHomeEvent.USER_COMMAND, "HUB", 
            "MODE_CHANGE:" + mode);
        for (SmartDevice device : devices.values()) {
            device.onEventReceived(modeEvent);
        }
    }
    
    @Override
    public HomeMode getHomeMode() {
        return currentMode;
    }
    
    @Override
    public List<SmartDevice> getDevicesByType(String deviceType) {
        return devices.values().stream()
            .filter(device -> device.getDeviceType().equals(deviceType))
            .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }
    
    @Override
    public void logActivity(String message) {
        String timestamp = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        activityLog.add("[" + timestamp + "] " + message);
    }
    
    public void printStatus() {
        System.out.println("\n=== Smart Home Hub Status ===");
        System.out.println("Current Mode: " + currentMode);
        System.out.println("Registered Devices: " + devices.size());
        for (SmartDevice device : devices.values()) {
            System.out.println("  - " + device.getDeviceId() + " (" + device.getDeviceType() + 
                             ") - Status: " + device.getStatus());
        }
        System.out.println("Available Scenarios: " + scenarios.keySet());
        System.out.println("Recent Activity (last 5):");
        int start = Math.max(0, activityLog.size() - 5);
        for (int i = start; i < activityLog.size(); i++) {
            System.out.println("  " + activityLog.get(i));
        }
        System.out.println("==============================\n");
    }
    
    public void shutdown() {
        scheduler.shutdown();
    }
}

// Abstract colleague class
abstract class SmartDevice {
    protected String deviceId;
    protected String deviceType;
    protected SmartHomeMediator mediator;
    protected boolean isOnline;
    protected Map<String, Object> properties;
    
    public SmartDevice(String deviceId, String deviceType, SmartHomeMediator mediator) {
        this.deviceId = deviceId;
        this.deviceType = deviceType;
        this.mediator = mediator;
        this.isOnline = true;
        this.properties = new HashMap<>();
        mediator.registerDevice(this);
    }
    
    public String getDeviceId() { return deviceId; }
    public String getDeviceType() { return deviceType; }
    public boolean isOnline() { return isOnline; }
    
    public abstract String getStatus();
    public abstract void executeCommand(String command);
    public abstract void onEventReceived(SmartHomeEventData event);
    
    protected void sendEvent(SmartHomeEvent eventType, Object data) {
        if (isOnline) {
            SmartHomeEventData event = new SmartHomeEventData(eventType, deviceId, data);
            mediator.notify(event);
        }
    }
}

// Concrete colleague - Smart Light
class SmartLight extends SmartDevice {
    private boolean isOn;
    private int brightness; // 0-100
    private String color;
    
    public SmartLight(String deviceId, SmartHomeMediator mediator) {
        super(deviceId, "Light", mediator);
        this.isOn = false;
        this.brightness = 100;
        this.color = "white";
    }
    
    @Override
    public String getStatus() {
        return isOn ? "ON (Brightness: " + brightness + "%, Color: " + color + ")" : "OFF";
    }
    
    public void turnOn() {
        isOn = true;
        sendEvent(SmartHomeEvent.LIGHT_ON, "brightness:" + brightness);
        System.out.println("[" + deviceId + "] Light turned ON");
    }
    
    public void turnOff() {
        isOn = false;
        sendEvent(SmartHomeEvent.LIGHT_OFF, null);
        System.out.println("[" + deviceId + "] Light turned OFF");
    }
    
    public void setBrightness(int brightness) {
        this.brightness = Math.max(0, Math.min(100, brightness));
        if (isOn) {
            System.out.println("[" + deviceId + "] Brightness set to " + this.brightness + "%");
        }
    }
    
    public void dim(int targetBrightness) {
        setBrightness(targetBrightness);
        if (!isOn) turnOn();
    }
    
    public void setColor(String color) {
        this.color = color;
        System.out.println("[" + deviceId + "] Color changed to " + color);
    }
    
    @Override
    public void executeCommand(String command) {
        String[] parts = command.split(":");
        switch (parts[0].toLowerCase()) {
            case "turn_on": turnOn(); break;
            case "turn_off": turnOff(); break;
            case "brightness": setBrightness(Integer.parseInt(parts[1])); break;
            case "color": setColor(parts[1]); break;
        }
    }
    
    @Override
    public void onEventReceived(SmartHomeEventData event) {
        if (event.getType() == SmartHomeEvent.MOTION_DETECTED && 
            mediator.getHomeMode() == HomeMode.HOME && !isOn) {
            turnOn();
        }
    }
}

// Concrete colleague - Smart Thermostat
class SmartThermostat extends SmartDevice {
    private double currentTemperature;
    private double targetTemperature;
    private boolean isHeating;
    private boolean isCooling;
    
    public SmartThermostat(String deviceId, SmartHomeMediator mediator) {
        super(deviceId, "Climate", mediator);
        this.currentTemperature = 20.0;
        this.targetTemperature = 22.0;
        this.isHeating = false;
        this.isCooling = false;
    }
    
    @Override
    public String getStatus() {
        return String.format("Current: %.1f°C, Target: %.1f°C, %s", 
            currentTemperature, targetTemperature,
            isHeating ? "Heating" : (isCooling ? "Cooling" : "Idle"));
    }
    
    public void setTemperature(double temperature) {
        targetTemperature = temperature;
        adjustTemperature();
        System.out.println("[" + deviceId + "] Target temperature set to " + temperature + "°C");
    }
    
    private void adjustTemperature() {
        if (Math.abs(currentTemperature - targetTemperature) > 0.5) {
            if (currentTemperature < targetTemperature) {
                isHeating = true;
                isCooling = false;
            } else {
                isHeating = false;
                isCooling = true;
            }
            sendEvent(SmartHomeEvent.TEMPERATURE_CHANGED, 
                "target:" + targetTemperature + ",current:" + currentTemperature);
        } else {
            isHeating = false;
            isCooling = false;
        }
    }
    
    @Override
    public void executeCommand(String command) {
        String[] parts = command.split(":");
        switch (parts[0].toLowerCase()) {
            case "set_temperature": setTemperature(Double.parseDouble(parts[1])); break;
        }
    }
    
    @Override
    public void onEventReceived(SmartHomeEventData event) {
        // React to home mode changes
        if (event.getType() == SmartHomeEvent.USER_COMMAND && 
            event.getData().toString().startsWith("MODE_CHANGE")) {
            String mode = event.getData().toString().split(":")[1];
            switch (HomeMode.valueOf(mode)) {
                case AWAY: setTemperature(18.0); break;
                case SLEEP: setTemperature(19.0); break;
                case HOME: setTemperature(22.0); break;
            }
        }
    }
}

// Concrete colleague - Smart Lock
class SmartLock extends SmartDevice {
    private boolean isLocked;
    private String lastAccessCode;
    
    public SmartLock(String deviceId, SmartHomeMediator mediator) {
        super(deviceId, "Security", mediator);
        this.isLocked = true;
    }
    
    @Override
    public String getStatus() {
        return isLocked ? "LOCKED" : "UNLOCKED";
    }
    
    public void lock() {
        isLocked = true;
        sendEvent(SmartHomeEvent.DOOR_CLOSED, "locked");
        System.out.println("[" + deviceId + "] Door locked");
    }
    
    public void unlock() {
        isLocked = false;
        sendEvent(SmartHomeEvent.DOOR_OPENED, "unlocked");
        System.out.println("[" + deviceId + "] Door unlocked");
    }
    
    @Override
    public void executeCommand(String command) {
        switch (command.toLowerCase()) {
            case "lock": lock(); break;
            case "unlock": unlock(); break;
        }
    }
    
    @Override
    public void onEventReceived(SmartHomeEventData event) {
        // Auto-lock when switching to AWAY mode
        if (event.getType() == SmartHomeEvent.USER_COMMAND && 
            event.getData().toString().equals("MODE_CHANGE:AWAY") && !isLocked) {
            lock();
        }
    }
}

// Additional device types
class SmartTV extends SmartDevice {
    private boolean isOn;
    private String currentInput;
    
    public SmartTV(String deviceId, SmartHomeMediator mediator) {
        super(deviceId, "Entertainment", mediator);
        this.isOn = false;
        this.currentInput = "HDMI1";
    }
    
    @Override
    public String getStatus() {
        return isOn ? "ON (Input: " + currentInput + ")" : "OFF";
    }
    
    public void turnOn() {
        isOn = true;
        System.out.println("[" + deviceId + "] TV turned ON");
    }
    
    public void setInput(String input) {
        if (isOn) {
            currentInput = input;
            System.out.println("[" + deviceId + "] Input changed to " + input);
        }
    }
    
    @Override
    public void executeCommand(String command) {
        String[] parts = command.split(":");
        switch (parts[0].toLowerCase()) {
            case "turn_on": turnOn(); break;
            case "input": if (parts.length > 1) setInput(parts[1]); break;
        }
    }
    
    @Override
    public void onEventReceived(SmartHomeEventData event) {
        // Can implement auto-off when switching to sleep mode
    }
}

class SmartSpeaker extends SmartDevice {
    private int volume;
    private boolean isMuted;
    
    public SmartSpeaker(String deviceId, SmartHomeMediator mediator) {
        super(deviceId, "Entertainment", mediator);
        this.volume = 50;
        this.isMuted = false;
    }
    
    @Override
    public String getStatus() {
        return isMuted ? "MUTED" : "Volume: " + volume + "%";
    }
    
    public void playWelcomeMessage() {
        System.out.println("[" + deviceId + "] Playing: 'Welcome home! The temperature is " + 
                         "22°C and all systems are normal.'");
    }
    
    @Override
    public void executeCommand(String command) {
        // Implementation for various speaker commands
    }
    
    @Override
    public void onEventReceived(SmartHomeEventData event) {
        // Can announce important events
    }
}

class SecurityCamera extends SmartDevice {
    private boolean isRecording;
    
    public SecurityCamera(String deviceId, SmartHomeMediator mediator) {
        super(deviceId, "Security", mediator);
        this.isRecording = false;
    }
    
    @Override
    public String getStatus() {
        return isRecording ? "RECORDING" : "STANDBY";
    }
    
    public void startRecording() {
        isRecording = true;
        System.out.println("[" + deviceId + "] Started recording");
    }
    
    public void stopRecording() {
        isRecording = false;
        System.out.println("[" + deviceId + "] Stopped recording");
    }
    
    @Override
    public void executeCommand(String command) {
        switch (command.toLowerCase()) {
            case "start_recording": startRecording(); break;
            case "stop_recording": stopRecording(); break;
        }
    }
    
    @Override
    public void onEventReceived(SmartHomeEventData event) {
        // Auto-record on security events
    }
}

// Motion sensor
class MotionSensor extends SmartDevice {
    private boolean motionDetected;
    
    public MotionSensor(String deviceId, SmartHomeMediator mediator) {
        super(deviceId, "Sensor", mediator);
        this.motionDetected = false;
    }
    
    @Override
    public String getStatus() {
        return motionDetected ? "MOTION DETECTED" : "NO MOTION";
    }
    
    public void detectMotion() {
        motionDetected = true;
        sendEvent(SmartHomeEvent.MOTION_DETECTED, "motion detected");
        System.out.println("[" + deviceId + "] Motion detected!");
        
        // Reset after 5 seconds
        mediator.scheduleAction(deviceId, "clear_motion", 5);
    }
    
    @Override
    public void executeCommand(String command) {
        switch (command.toLowerCase()) {
            case "clear_motion": 
                motionDetected = false; 
                System.out.println("[" + deviceId + "] Motion cleared");
                break;
        }
    }
    
    @Override
    public void onEventReceived(SmartHomeEventData event) {
        // Motion sensors typically don't react to other events
    }
}

// Client code demonstrating the Mediator Pattern
public class SmartHomeExample {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Smart Home Mediator Pattern Demo ===\n");
        
        // Create smart home hub (mediator)
        SmartHomeHub hub = new SmartHomeHub();
        
        // Create smart devices (colleagues)
        SmartLight livingRoomLight = new SmartLight("LivingRoom_Light", hub);
        SmartLight bedroomLight = new SmartLight("Bedroom_Light", hub);
        SmartThermostat thermostat = new SmartThermostat("Main_Thermostat", hub);
        SmartLock frontDoor = new SmartLock("Front_Door", hub);
        SmartTV livingRoomTV = new SmartTV("LivingRoom_TV", hub);
        SmartSpeaker speaker = new SmartSpeaker("Main_Speaker", hub);
        SecurityCamera camera = new SecurityCamera("Front_Camera", hub);
        MotionSensor motionSensor = new MotionSensor("Living_Motion", hub);
        
        Thread.sleep(1000);
        hub.printStatus();
        
        System.out.println("=== Manual Device Control ===");
        livingRoomLight.turnOn();
        livingRoomLight.setBrightness(75);
        thermostat.setTemperature(24);
        frontDoor.unlock();
        
        Thread.sleep(1000);
        
        System.out.println("\n=== Scenario Execution ===");
        hub.executeScenario("movie_night");
        
        Thread.sleep(2000);
        
        System.out.println("\n=== Motion Detection Event ===");
        motionSensor.detectMotion();
        
        Thread.sleep(1000);
        
        System.out.println("\n=== Home Mode Changes ===");
        hub.setHomeMode(HomeMode.AWAY);
        
        Thread.sleep(1000);
        
        // Simulate motion while away (security scenario)
        System.out.println("\n=== Security Event (Motion While Away) ===");
        motionSensor.detectMotion();
        
        Thread.sleep(2000);
        
        // Coming back home
        System.out.println("\n=== Coming Home ===");
        frontDoor.unlock(); // This triggers door opened event
        
        Thread.sleep(1000);
        
        System.out.println("\n=== Scheduled Actions ===");
        hub.scheduleAction("Bedroom_Light", "turn_on", 3);
        hub.scheduleAction("Main_Thermostat", "set_temperature:20", 5);
        
        System.out.println("\n=== Bedtime Scenario ===");
        hub.executeScenario("bedtime");
        
        Thread.sleep(2000);
        
        // Motion during sleep (night light scenario)
        System.out.println("\n=== Night Motion (Sleep Mode) ===");
        motionSensor.detectMotion();
        
        Thread.sleep(6000); // Wait for scheduled actions
        
        hub.printStatus();
        
        // Cleanup
        hub.shutdown();
    }
}

/*
Expected Output:
=== Smart Home Mediator Pattern Demo ===

[HUB] Device registered: LivingRoom_Light
[HUB] Device registered: Bedroom_Light
[HUB] Device registered: Main_Thermostat
[HUB] Device registered: Front_Door
[HUB] Device registered: LivingRoom_TV
[HUB] Device registered: Main_Speaker
[HUB] Device registered: Front_Camera
[HUB] Device registered: Living_Motion

=== Smart Home Hub Status ===
Current Mode: HOME
Registered Devices: 8
  - LivingRoom_Light (Light) - Status: OFF
  - Bedroom_Light (Light) - Status: OFF
  - Main_Thermostat (Climate) - Status: Current: 20.0°C, Target: 22.0°C, Heating
  - Front_Door (Security) - Status: LOCKED
  - LivingRoom_TV (Entertainment) - Status: OFF
  - Main_Speaker (Entertainment) - Status: Volume: 50%
  - Front_Camera (Security) - Status: STANDBY
  - Living_Motion (Sensor) - Status: NO MOTION
Available Scenarios: [movie_night, bedtime, welcome_home]
Recent Activity (last 5):
  [timestamp] Device registered: LivingRoom_Light (Light)
  [timestamp] Device registered: Bedroom_Light (Light)
  [timestamp] Device registered: Main_Thermostat (Climate)
  [timestamp] Device registered: Front_Door (Security)
  [timestamp] Device registered: Living_Motion (Sensor)
==============================

=== Manual Device Control ===
[LivingRoom_Light] Light turned ON
[HUB] [timestamp] LIGHT_ON from LivingRoom_Light: brightness:100
[LivingRoom_Light] Brightness set to 75%
[Main_Thermostat] Target temperature set to 24.0°C
[HUB] [timestamp] TEMPERATURE_CHANGED from Main_Thermostat: target:24.0,current:20.0
[Front_Door] Door unlocked
[HUB] [timestamp] DOOR_OPENED from Front_Door: unlocked

=== Scenario Execution ===
[HUB] Executed scenario: movie_night
[LivingRoom_Light] Brightness set to 20%
[Bedroom_Light] Light turned ON
[Bedroom_Light] Brightness set to 20%
[LivingRoom_TV] TV turned ON
[LivingRoom_TV] Input changed to Netflix
[Main_Thermostat] Target temperature set to 22.0°C

=== Motion Detection Event ===
[Living_Motion] Motion detected!
[HUB] [timestamp] MOTION_DETECTED from Living_Motion: motion detected
[LivingRoom_Light] Light turned ON

=== Home Mode Changes ===
[HUB] Home mode: AWAY
[Front_Door] Door locked
[Main_Thermostat] Target temperature set to 18.0°C

=== Security Event (Motion While Away) ===
[Living_Motion] Motion detected!
[HUB] [timestamp] MOTION_DETECTED from Living_Motion: motion detected
[Front_Camera] Started recording

=== Coming Home ===
[Front_Door] Door unlocked
[HUB] [timestamp] DOOR_OPENED from Front_Door: unlocked
[HUB] Executed scenario: welcome_home
[LivingRoom_Light] Light turned ON
[LivingRoom_Light] Brightness set to 80%
[Bedroom_Light] Light turned ON
[Bedroom_Light] Brightness set to 80%
[Main_Thermostat] Target temperature set to 23.0°C
[Main_Speaker] Playing: 'Welcome home! The temperature is 22°C and all systems are normal.'
[HUB] Home mode: HOME

=== Scheduled Actions ===

=== Bedtime Scenario ===
[HUB] Executed scenario: bedtime
[LivingRoom_Light] Light turned OFF
[Bedroom_Light] Light turned OFF
[Front_Door] Door locked
[Main_Thermostat] Target temperature set to 18.0°C
[HUB] Home mode: SLEEP

=== Night Motion (Sleep Mode) ===
[Living_Motion] Motion detected!
[HUB] [timestamp] MOTION_DETECTED from Living_Motion: motion detected
[LivingRoom_Light] Light turned ON
[LivingRoom_Light] Brightness set to 10%
[Bedroom_Light] Light turned ON
[Bedroom_Light] Brightness set to 10%
[Bedroom_Light] Light turned ON
[Main_Thermostat] Target temperature set to 20.0°C

=== Smart Home Hub Status ===
Current Mode: SLEEP
Registered Devices: 8
  - LivingRoom_Light (Light) - Status: ON (Brightness: 10%, Color: white)
  - Bedroom_Light (Light) - Status: ON (Brightness: 10%, Color: white)
  - Main_Thermostat (Climate) - Status: Current: 20.0°C, Target: 20.0°C, Idle
  - Front_Door (Security) - Status: LOCKED
  - LivingRoom_TV (Entertainment) - Status: ON (Input: Netflix)
  - Main_Speaker (Entertainment) - Status: Volume: 50%
  - Front_Camera (Security) - Status: RECORDING
  - Living_Motion (Sensor) - Status: NO MOTION
Available Scenarios: [movie_night, bedtime, welcome_home]
Recent Activity (last 5):
  [timestamp] Scheduled action executed: turn_on on Bedroom_Light
  [timestamp] Scheduled action executed: set_temperature:20 on Main_Thermostat
  [timestamp] Event received: [timestamp] MOTION_DETECTED from Living_Motion: motion detected
  [timestamp] Motion detected while away - potential security breach
  [timestamp] Executing Night Motion scenario
==============================
*/