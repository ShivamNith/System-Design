package org.example.designPatterns.commandPattern;

import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.*;

/**
 * Smart Home Command Pattern Example
 * 
 * This example demonstrates the Command Pattern implementation for a smart home
 * automation system. It shows how commands can be used for device control,
 * scheduling, and automation scenarios.
 * 
 * Features:
 * - Various smart device commands (lights, thermostat, security, etc.)
 * - Scheduled command execution
 * - Macro commands for scenes
 * - Remote control simulation
 * - Command queuing and batch execution
 */
public class SmartHomeCommandExample {

    // Command interface
    interface Command {
        void execute();
        String getDescription();
        boolean isReversible();
        Command getUndoCommand();
    }

    // Smart Device interfaces and implementations
    interface SmartDevice {
        String getName();
        String getStatus();
    }

    static class SmartLight implements SmartDevice {
        private String name;
        private boolean isOn;
        private int brightness; // 0-100
        private String color;
        
        public SmartLight(String name) {
            this.name = name;
            this.isOn = false;
            this.brightness = 100;
            this.color = "white";
        }
        
        public void turnOn() {
            isOn = true;
            System.out.println(name + " light turned ON");
        }
        
        public void turnOff() {
            isOn = false;
            System.out.println(name + " light turned OFF");
        }
        
        public void setBrightness(int brightness) {
            this.brightness = Math.max(0, Math.min(100, brightness));
            System.out.println(name + " brightness set to " + this.brightness + "%");
        }
        
        public void setColor(String color) {
            this.color = color;
            System.out.println(name + " color set to " + color);
        }
        
        @Override
        public String getName() { return name; }
        
        @Override
        public String getStatus() {
            return name + ": " + (isOn ? "ON" : "OFF") + 
                   ", Brightness: " + brightness + "%, Color: " + color;
        }
        
        public boolean isOn() { return isOn; }
        public int getBrightness() { return brightness; }
        public String getColor() { return color; }
    }

    static class SmartThermostat implements SmartDevice {
        private String name;
        private double temperature;
        private String mode; // heating, cooling, auto, off
        
        public SmartThermostat(String name) {
            this.name = name;
            this.temperature = 22.0;
            this.mode = "auto";
        }
        
        public void setTemperature(double temp) {
            this.temperature = temp;
            System.out.println(name + " temperature set to " + temp + "°C");
        }
        
        public void setMode(String mode) {
            this.mode = mode;
            System.out.println(name + " mode set to " + mode);
        }
        
        @Override
        public String getName() { return name; }
        
        @Override
        public String getStatus() {
            return name + ": " + temperature + "°C, Mode: " + mode;
        }
        
        public double getTemperature() { return temperature; }
        public String getMode() { return mode; }
    }

    static class SmartSecuritySystem implements SmartDevice {
        private String name;
        private boolean isArmed;
        private String mode; // home, away, disarmed
        
        public SmartSecuritySystem(String name) {
            this.name = name;
            this.isArmed = false;
            this.mode = "disarmed";
        }
        
        public void arm(String mode) {
            this.isArmed = true;
            this.mode = mode;
            System.out.println(name + " ARMED in " + mode + " mode");
        }
        
        public void disarm() {
            this.isArmed = false;
            this.mode = "disarmed";
            System.out.println(name + " DISARMED");
        }
        
        @Override
        public String getName() { return name; }
        
        @Override
        public String getStatus() {
            return name + ": " + (isArmed ? "ARMED" : "DISARMED") + " (" + mode + ")";
        }
        
        public boolean isArmed() { return isArmed; }
        public String getMode() { return mode; }
    }

    // Concrete Commands
    static class LightOnCommand implements Command {
        private SmartLight light;
        
        public LightOnCommand(SmartLight light) {
            this.light = light;
        }
        
        @Override
        public void execute() {
            light.turnOn();
        }
        
        @Override
        public String getDescription() {
            return "Turn on " + light.getName();
        }
        
        @Override
        public boolean isReversible() {
            return true;
        }
        
        @Override
        public Command getUndoCommand() {
            return new LightOffCommand(light);
        }
    }

    static class LightOffCommand implements Command {
        private SmartLight light;
        
        public LightOffCommand(SmartLight light) {
            this.light = light;
        }
        
        @Override
        public void execute() {
            light.turnOff();
        }
        
        @Override
        public String getDescription() {
            return "Turn off " + light.getName();
        }
        
        @Override
        public boolean isReversible() {
            return true;
        }
        
        @Override
        public Command getUndoCommand() {
            return new LightOnCommand(light);
        }
    }

    static class SetBrightnessCommand implements Command {
        private SmartLight light;
        private int newBrightness;
        private int previousBrightness;
        
        public SetBrightnessCommand(SmartLight light, int brightness) {
            this.light = light;
            this.newBrightness = brightness;
            this.previousBrightness = light.getBrightness();
        }
        
        @Override
        public void execute() {
            light.setBrightness(newBrightness);
        }
        
        @Override
        public String getDescription() {
            return "Set " + light.getName() + " brightness to " + newBrightness + "%";
        }
        
        @Override
        public boolean isReversible() {
            return true;
        }
        
        @Override
        public Command getUndoCommand() {
            return new SetBrightnessCommand(light, previousBrightness);
        }
    }

    static class SetTemperatureCommand implements Command {
        private SmartThermostat thermostat;
        private double newTemperature;
        private double previousTemperature;
        
        public SetTemperatureCommand(SmartThermostat thermostat, double temperature) {
            this.thermostat = thermostat;
            this.newTemperature = temperature;
            this.previousTemperature = thermostat.getTemperature();
        }
        
        @Override
        public void execute() {
            thermostat.setTemperature(newTemperature);
        }
        
        @Override
        public String getDescription() {
            return "Set " + thermostat.getName() + " to " + newTemperature + "°C";
        }
        
        @Override
        public boolean isReversible() {
            return true;
        }
        
        @Override
        public Command getUndoCommand() {
            return new SetTemperatureCommand(thermostat, previousTemperature);
        }
    }

    static class ArmSecurityCommand implements Command {
        private SmartSecuritySystem security;
        private String mode;
        private boolean wasArmed;
        private String previousMode;
        
        public ArmSecurityCommand(SmartSecuritySystem security, String mode) {
            this.security = security;
            this.mode = mode;
            this.wasArmed = security.isArmed();
            this.previousMode = security.getMode();
        }
        
        @Override
        public void execute() {
            security.arm(mode);
        }
        
        @Override
        public String getDescription() {
            return "Arm " + security.getName() + " in " + mode + " mode";
        }
        
        @Override
        public boolean isReversible() {
            return true;
        }
        
        @Override
        public Command getUndoCommand() {
            if (wasArmed) {
                return new ArmSecurityCommand(security, previousMode);
            } else {
                return new DisarmSecurityCommand(security);
            }
        }
    }

    static class DisarmSecurityCommand implements Command {
        private SmartSecuritySystem security;
        private boolean wasArmed;
        private String previousMode;
        
        public DisarmSecurityCommand(SmartSecuritySystem security) {
            this.security = security;
            this.wasArmed = security.isArmed();
            this.previousMode = security.getMode();
        }
        
        @Override
        public void execute() {
            security.disarm();
        }
        
        @Override
        public String getDescription() {
            return "Disarm " + security.getName();
        }
        
        @Override
        public boolean isReversible() {
            return true;
        }
        
        @Override
        public Command getUndoCommand() {
            if (wasArmed) {
                return new ArmSecurityCommand(security, previousMode);
            } else {
                return this; // No-op if it was already disarmed
            }
        }
    }

    // Scene Command (Macro Command)
    static class SceneCommand implements Command {
        private String sceneName;
        private List<Command> commands;
        
        public SceneCommand(String sceneName) {
            this.sceneName = sceneName;
            this.commands = new ArrayList<>();
        }
        
        public void addCommand(Command command) {
            commands.add(command);
        }
        
        @Override
        public void execute() {
            System.out.println("Executing scene: " + sceneName);
            for (Command command : commands) {
                command.execute();
            }
        }
        
        @Override
        public String getDescription() {
            return "Scene: " + sceneName + " (" + commands.size() + " actions)";
        }
        
        @Override
        public boolean isReversible() {
            return commands.stream().allMatch(Command::isReversible);
        }
        
        @Override
        public Command getUndoCommand() {
            SceneCommand undoScene = new SceneCommand("Undo " + sceneName);
            // Add undo commands in reverse order
            for (int i = commands.size() - 1; i >= 0; i--) {
                Command command = commands.get(i);
                if (command.isReversible()) {
                    undoScene.addCommand(command.getUndoCommand());
                }
            }
            return undoScene;
        }
    }

    // Scheduled Command
    static class ScheduledCommand {
        private Command command;
        private LocalTime scheduleTime;
        private boolean executed;
        
        public ScheduledCommand(Command command, LocalTime scheduleTime) {
            this.command = command;
            this.scheduleTime = scheduleTime;
            this.executed = false;
        }
        
        public boolean shouldExecute(LocalTime currentTime) {
            return !executed && currentTime.isAfter(scheduleTime);
        }
        
        public void execute() {
            if (!executed) {
                command.execute();
                executed = true;
            }
        }
        
        public String getDescription() {
            return "Scheduled for " + scheduleTime + ": " + command.getDescription();
        }
    }

    // Smart Home Controller (Invoker)
    static class SmartHomeController {
        private Map<String, SmartDevice> devices;
        private List<Command> commandHistory;
        private Queue<Command> commandQueue;
        private List<ScheduledCommand> scheduledCommands;
        private Map<String, SceneCommand> scenes;
        
        public SmartHomeController() {
            this.devices = new HashMap<>();
            this.commandHistory = new ArrayList<>();
            this.commandQueue = new LinkedList<>();
            this.scheduledCommands = new ArrayList<>();
            this.scenes = new HashMap<>();
        }
        
        public void addDevice(SmartDevice device) {
            devices.put(device.getName(), device);
            System.out.println("Added device: " + device.getName());
        }
        
        public void executeCommand(Command command) {
            command.execute();
            commandHistory.add(command);
            System.out.println("Executed: " + command.getDescription());
        }
        
        public void queueCommand(Command command) {
            commandQueue.offer(command);
            System.out.println("Queued: " + command.getDescription());
        }
        
        public void executeQueuedCommands() {
            System.out.println("Executing queued commands...");
            while (!commandQueue.isEmpty()) {
                Command command = commandQueue.poll();
                executeCommand(command);
            }
        }
        
        public void scheduleCommand(Command command, LocalTime time) {
            ScheduledCommand scheduled = new ScheduledCommand(command, time);
            scheduledCommands.add(scheduled);
            System.out.println("Scheduled: " + scheduled.getDescription());
        }
        
        public void checkScheduledCommands() {
            LocalTime now = LocalTime.now();
            for (ScheduledCommand scheduled : scheduledCommands) {
                if (scheduled.shouldExecute(now)) {
                    System.out.println("Executing scheduled command:");
                    scheduled.execute();
                }
            }
        }
        
        public void createScene(String sceneName, List<Command> commands) {
            SceneCommand scene = new SceneCommand(sceneName);
            for (Command command : commands) {
                scene.addCommand(command);
            }
            scenes.put(sceneName, scene);
            System.out.println("Created scene: " + sceneName);
        }
        
        public void executeScene(String sceneName) {
            SceneCommand scene = scenes.get(sceneName);
            if (scene != null) {
                executeCommand(scene);
            } else {
                System.out.println("Scene not found: " + sceneName);
            }
        }
        
        public void showDeviceStatus() {
            System.out.println("\n=== Device Status ===");
            for (SmartDevice device : devices.values()) {
                System.out.println(device.getStatus());
            }
        }
        
        public void showCommandHistory() {
            System.out.println("\n=== Command History ===");
            for (int i = 0; i < commandHistory.size(); i++) {
                System.out.println((i + 1) + ". " + commandHistory.get(i).getDescription());
            }
        }
        
        public void showScheduledCommands() {
            System.out.println("\n=== Scheduled Commands ===");
            for (ScheduledCommand scheduled : scheduledCommands) {
                System.out.println(scheduled.getDescription());
            }
        }
        
        public SmartDevice getDevice(String name) {
            return devices.get(name);
        }
    }

    // Remote Control (Client)
    static class SmartHomeRemote {
        private SmartHomeController controller;
        private Map<String, Command> buttons;
        
        public SmartHomeRemote(SmartHomeController controller) {
            this.controller = controller;
            this.buttons = new HashMap<>();
        }
        
        public void setButton(String buttonName, Command command) {
            buttons.put(buttonName, command);
            System.out.println("Button '" + buttonName + "' configured: " + command.getDescription());
        }
        
        public void pressButton(String buttonName) {
            Command command = buttons.get(buttonName);
            if (command != null) {
                System.out.println("Pressing button: " + buttonName);
                controller.executeCommand(command);
            } else {
                System.out.println("Button not configured: " + buttonName);
            }
        }
        
        public void showButtons() {
            System.out.println("\n=== Remote Control Buttons ===");
            for (Map.Entry<String, Command> entry : buttons.entrySet()) {
                System.out.println(entry.getKey() + ": " + entry.getValue().getDescription());
            }
        }
    }

    // Demonstration
    public static void main(String[] args) {
        System.out.println("=== Smart Home Command Pattern Example ===\n");
        
        // Setup smart home system
        SmartHomeController controller = new SmartHomeController();
        
        // Add devices
        SmartLight livingRoomLight = new SmartLight("Living Room");
        SmartLight kitchenLight = new SmartLight("Kitchen");
        SmartThermostat thermostat = new SmartThermostat("Main Thermostat");
        SmartSecuritySystem security = new SmartSecuritySystem("Home Security");
        
        controller.addDevice(livingRoomLight);
        controller.addDevice(kitchenLight);
        controller.addDevice(thermostat);
        controller.addDevice(security);
        
        // Setup remote control
        SmartHomeRemote remote = new SmartHomeRemote(controller);
        
        // Configure remote buttons
        remote.setButton("1", new LightOnCommand(livingRoomLight));
        remote.setButton("2", new LightOffCommand(livingRoomLight));
        remote.setButton("3", new SetBrightnessCommand(livingRoomLight, 50));
        remote.setButton("4", new SetTemperatureCommand(thermostat, 24.0));
        remote.setButton("5", new ArmSecurityCommand(security, "away"));
        
        System.out.println("\n1. Basic Remote Control Usage:");
        remote.showButtons();
        
        remote.pressButton("1"); // Turn on living room light
        remote.pressButton("3"); // Set brightness to 50%
        remote.pressButton("4"); // Set temperature to 24°C
        
        controller.showDeviceStatus();
        
        // Create and execute scenes
        System.out.println("\n2. Scene Commands:");
        List<Command> goodNightCommands = Arrays.asList(
            new LightOffCommand(livingRoomLight),
            new LightOffCommand(kitchenLight),
            new SetTemperatureCommand(thermostat, 20.0),
            new ArmSecurityCommand(security, "home")
        );
        
        controller.createScene("Good Night", goodNightCommands);
        controller.executeScene("Good Night");
        
        controller.showDeviceStatus();
        
        // Command queuing
        System.out.println("\n3. Command Queuing:");
        controller.queueCommand(new LightOnCommand(kitchenLight));
        controller.queueCommand(new SetBrightnessCommand(kitchenLight, 75));
        controller.queueCommand(new SetTemperatureCommand(thermostat, 22.0));
        
        controller.executeQueuedCommands();
        controller.showDeviceStatus();
        
        // Morning scene
        System.out.println("\n4. Morning Scene:");
        List<Command> morningCommands = Arrays.asList(
            new DisarmSecurityCommand(security),
            new LightOnCommand(livingRoomLight),
            new LightOnCommand(kitchenLight),
            new SetBrightnessCommand(livingRoomLight, 80),
            new SetBrightnessCommand(kitchenLight, 90),
            new SetTemperatureCommand(thermostat, 23.0)
        );
        
        controller.createScene("Good Morning", morningCommands);
        controller.executeScene("Good Morning");
        
        controller.showDeviceStatus();
        
        // Show command history
        System.out.println("\n5. Command History:");
        controller.showCommandHistory();
        
        // Demonstrate undo functionality
        System.out.println("\n6. Undo Demonstration:");
        Command lastCommand = controller.commandHistory.get(controller.commandHistory.size() - 1);
        if (lastCommand.isReversible()) {
            System.out.println("Undoing last command: " + lastCommand.getDescription());
            Command undoCommand = lastCommand.getUndoCommand();
            controller.executeCommand(undoCommand);
        }
        
        controller.showDeviceStatus();
        
        System.out.println("\nDemonstration completed!");
    }
}