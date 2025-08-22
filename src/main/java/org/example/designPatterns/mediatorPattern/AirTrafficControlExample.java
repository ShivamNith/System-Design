package org.example.designPatterns.mediatorPattern;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Mediator Pattern Example: Air Traffic Control System
 * 
 * This example demonstrates the Mediator Pattern with an air traffic control system
 * where the control tower mediates communication between aircraft to ensure safe operations.
 */

// Mediator interface for air traffic control
interface AirTrafficController {
    void requestLanding(Aircraft aircraft);
    void requestTakeoff(Aircraft aircraft);
    void requestAltitudeChange(Aircraft aircraft, int newAltitude);
    void registerAircraft(Aircraft aircraft);
    void unregisterAircraft(Aircraft aircraft);
    void emergencyBroadcast(Aircraft aircraft, String message);
}

// Concrete mediator - Control Tower
class ControlTower implements AirTrafficController {
    private List<Aircraft> aircraftList;
    private Queue<Aircraft> landingQueue;
    private Queue<Aircraft> takeoffQueue;
    private boolean runwayAvailable;
    private Map<Integer, Set<Aircraft>> altitudeLevels;
    
    public ControlTower() {
        this.aircraftList = new ArrayList<>();
        this.landingQueue = new LinkedList<>();
        this.takeoffQueue = new LinkedList<>();
        this.runwayAvailable = true;
        this.altitudeLevels = new HashMap<>();
    }
    
    @Override
    public void registerAircraft(Aircraft aircraft) {
        aircraftList.add(aircraft);
        int altitude = aircraft.getAltitude();
        altitudeLevels.computeIfAbsent(altitude, k -> new HashSet<>()).add(aircraft);
        broadcast(aircraft.getCallSign() + " has entered our airspace at altitude " + altitude + " feet", aircraft);
        System.out.println("[CONTROL TOWER] " + aircraft.getCallSign() + " registered in our airspace");
    }
    
    @Override
    public void unregisterAircraft(Aircraft aircraft) {
        aircraftList.remove(aircraft);
        altitudeLevels.get(aircraft.getAltitude()).remove(aircraft);
        broadcast(aircraft.getCallSign() + " has left our airspace", aircraft);
        System.out.println("[CONTROL TOWER] " + aircraft.getCallSign() + " unregistered from our airspace");
    }
    
    @Override
    public void requestLanding(Aircraft aircraft) {
        System.out.println("[CONTROL TOWER] Received landing request from " + aircraft.getCallSign());
        
        if (runwayAvailable && landingQueue.isEmpty()) {
            grantLanding(aircraft);
        } else {
            landingQueue.offer(aircraft);
            int position = landingQueue.size();
            aircraft.receive("[CONTROL TOWER] You are number " + position + " for landing. Please hold at altitude " + aircraft.getAltitude());
            System.out.println("[CONTROL TOWER] " + aircraft.getCallSign() + " added to landing queue, position " + position);
        }
    }
    
    @Override
    public void requestTakeoff(Aircraft aircraft) {
        System.out.println("[CONTROL TOWER] Received takeoff request from " + aircraft.getCallSign());
        
        if (runwayAvailable && takeoffQueue.isEmpty()) {
            grantTakeoff(aircraft);
        } else {
            takeoffQueue.offer(aircraft);
            int position = takeoffQueue.size();
            aircraft.receive("[CONTROL TOWER] You are number " + position + " for takeoff. Please hold at gate");
            System.out.println("[CONTROL TOWER] " + aircraft.getCallSign() + " added to takeoff queue, position " + position);
        }
    }
    
    @Override
    public void requestAltitudeChange(Aircraft aircraft, int newAltitude) {
        System.out.println("[CONTROL TOWER] " + aircraft.getCallSign() + " requesting altitude change to " + newAltitude + " feet");
        
        // Check for conflicts at the new altitude
        Set<Aircraft> aircraftAtNewAltitude = altitudeLevels.get(newAltitude);
        if (aircraftAtNewAltitude != null && !aircraftAtNewAltitude.isEmpty()) {
            // Find alternative altitude
            int alternativeAltitude = findAlternativeAltitude(newAltitude);
            aircraft.receive("[CONTROL TOWER] Altitude " + newAltitude + " is occupied. Alternative altitude " + alternativeAltitude + " available");
        } else {
            // Grant altitude change
            altitudeLevels.get(aircraft.getAltitude()).remove(aircraft);
            aircraft.changeAltitude(newAltitude);
            altitudeLevels.computeIfAbsent(newAltitude, k -> new HashSet<>()).add(aircraft);
            aircraft.receive("[CONTROL TOWER] Cleared to altitude " + newAltitude + " feet");
            
            // Warn nearby aircraft
            warnNearbyAircraft(aircraft, newAltitude);
        }
    }
    
    @Override
    public void emergencyBroadcast(Aircraft aircraft, String message) {
        System.out.println("[CONTROL TOWER] EMERGENCY from " + aircraft.getCallSign() + ": " + message);
        String emergencyMessage = "EMERGENCY BROADCAST - " + aircraft.getCallSign() + ": " + message;
        broadcast(emergencyMessage, aircraft);
        
        // Clear runway if needed
        if (message.toLowerCase().contains("landing") || message.toLowerCase().contains("emergency landing")) {
            clearRunwayForEmergency(aircraft);
        }
    }
    
    private void grantLanding(Aircraft aircraft) {
        runwayAvailable = false;
        aircraft.receive("[CONTROL TOWER] Cleared for landing on runway 27. Wind 270 at 10 knots");
        System.out.println("[CONTROL TOWER] " + aircraft.getCallSign() + " cleared for landing");
        
        // Simulate landing time
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                runwayAvailable = true;
                aircraft.land();
                System.out.println("[CONTROL TOWER] " + aircraft.getCallSign() + " has landed safely. Runway clear");
                processNextInQueue();
            }
        }, 3000); // 3 seconds for landing
    }
    
    private void grantTakeoff(Aircraft aircraft) {
        runwayAvailable = false;
        aircraft.receive("[CONTROL TOWER] Cleared for takeoff on runway 27. Contact departure on 121.9");
        System.out.println("[CONTROL TOWER] " + aircraft.getCallSign() + " cleared for takeoff");
        
        // Simulate takeoff time
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                runwayAvailable = true;
                aircraft.takeoff();
                System.out.println("[CONTROL TOWER] " + aircraft.getCallSign() + " airborne. Runway clear");
                processNextInQueue();
            }
        }, 2000); // 2 seconds for takeoff
    }
    
    private void processNextInQueue() {
        if (!landingQueue.isEmpty()) {
            Aircraft nextLanding = landingQueue.poll();
            grantLanding(nextLanding);
        } else if (!takeoffQueue.isEmpty()) {
            Aircraft nextTakeoff = takeoffQueue.poll();
            grantTakeoff(nextTakeoff);
        }
    }
    
    private void clearRunwayForEmergency(Aircraft emergencyAircraft) {
        System.out.println("[CONTROL TOWER] Clearing runway for emergency aircraft " + emergencyAircraft.getCallSign());
        // Move all queued aircraft to hold
        List<Aircraft> allQueued = new ArrayList<>();
        allQueued.addAll(landingQueue);
        allQueued.addAll(takeoffQueue);
        
        for (Aircraft aircraft : allQueued) {
            aircraft.receive("[CONTROL TOWER] Emergency in progress. Please hold current position");
        }
        
        landingQueue.clear();
        takeoffQueue.clear();
        landingQueue.offer(emergencyAircraft);
    }
    
    private int findAlternativeAltitude(int requestedAltitude) {
        int[] alternatives = {requestedAltitude + 1000, requestedAltitude - 1000, 
                             requestedAltitude + 2000, requestedAltitude - 2000};
        
        for (int alt : alternatives) {
            if (alt > 0 && (altitudeLevels.get(alt) == null || altitudeLevels.get(alt).isEmpty())) {
                return alt;
            }
        }
        return requestedAltitude + 1000; // Default
    }
    
    private void warnNearbyAircraft(Aircraft changingAircraft, int newAltitude) {
        int[] nearbyAltitudes = {newAltitude + 1000, newAltitude - 1000};
        
        for (int altitude : nearbyAltitudes) {
            Set<Aircraft> nearbyAircraft = altitudeLevels.get(altitude);
            if (nearbyAircraft != null) {
                for (Aircraft aircraft : nearbyAircraft) {
                    aircraft.receive("[CONTROL TOWER] Traffic alert: " + changingAircraft.getCallSign() + 
                                   " changing to altitude " + newAltitude + " feet");
                }
            }
        }
    }
    
    private void broadcast(String message, Aircraft sender) {
        String timestamp = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        for (Aircraft aircraft : aircraftList) {
            if (aircraft != sender) {
                aircraft.receive("[" + timestamp + "] " + message);
            }
        }
    }
    
    public void printStatus() {
        System.out.println("\n=== CONTROL TOWER STATUS ===");
        System.out.println("Aircraft in airspace: " + aircraftList.size());
        System.out.println("Landing queue: " + landingQueue.size());
        System.out.println("Takeoff queue: " + takeoffQueue.size());
        System.out.println("Runway available: " + runwayAvailable);
        System.out.println("Altitude levels occupied: " + altitudeLevels.keySet());
        System.out.println("=============================\n");
    }
}

// Abstract colleague class
abstract class Aircraft {
    protected String callSign;
    protected String aircraftType;
    protected int altitude;
    protected AirTrafficController controller;
    protected String status;
    
    public Aircraft(String callSign, String aircraftType, AirTrafficController controller) {
        this.callSign = callSign;
        this.aircraftType = aircraftType;
        this.controller = controller;
        this.altitude = 0;
        this.status = "On Ground";
        controller.registerAircraft(this);
    }
    
    public String getCallSign() { return callSign; }
    public int getAltitude() { return altitude; }
    public String getStatus() { return status; }
    
    public void changeAltitude(int newAltitude) {
        this.altitude = newAltitude;
    }
    
    public void requestLanding() {
        controller.requestLanding(this);
    }
    
    public void requestTakeoff() {
        controller.requestTakeoff(this);
    }
    
    public void requestAltitudeChange(int newAltitude) {
        controller.requestAltitudeChange(this, newAltitude);
    }
    
    public void declareEmergency(String emergency) {
        controller.emergencyBroadcast(this, emergency);
    }
    
    public abstract void receive(String message);
    
    public void land() {
        this.status = "Landed";
        this.altitude = 0;
        System.out.println("[" + callSign + "] Landed safely");
    }
    
    public void takeoff() {
        this.status = "Airborne";
        this.altitude = 1000;
        System.out.println("[" + callSign + "] Taking off");
    }
}

// Concrete colleague - Commercial Aircraft
class CommercialAircraft extends Aircraft {
    private int passengers;
    
    public CommercialAircraft(String callSign, int passengers, AirTrafficController controller) {
        super(callSign, "Commercial", controller);
        this.passengers = passengers;
    }
    
    @Override
    public void receive(String message) {
        System.out.println("[" + callSign + " - Commercial with " + passengers + " passengers] Received: " + message);
    }
}

// Concrete colleague - Cargo Aircraft
class CargoAircraft extends Aircraft {
    private double cargoWeight;
    
    public CargoAircraft(String callSign, double cargoWeight, AirTrafficController controller) {
        super(callSign, "Cargo", controller);
        this.cargoWeight = cargoWeight;
    }
    
    @Override
    public void receive(String message) {
        System.out.println("[" + callSign + " - Cargo " + cargoWeight + " tons] Received: " + message);
    }
}

// Concrete colleague - Private Aircraft
class PrivateAircraft extends Aircraft {
    private String owner;
    
    public PrivateAircraft(String callSign, String owner, AirTrafficController controller) {
        super(callSign, "Private", controller);
        this.owner = owner;
    }
    
    @Override
    public void receive(String message) {
        System.out.println("[" + callSign + " - Private (" + owner + ")] Received: " + message);
    }
}

// Emergency aircraft type
class EmergencyAircraft extends Aircraft {
    private String emergencyType;
    
    public EmergencyAircraft(String callSign, String emergencyType, AirTrafficController controller) {
        super(callSign, "Emergency", controller);
        this.emergencyType = emergencyType;
    }
    
    @Override
    public void receive(String message) {
        System.out.println("[" + callSign + " - EMERGENCY " + emergencyType + "] Received: " + message);
    }
}

// Client code demonstrating the Mediator Pattern
public class AirTrafficControlExample {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Air Traffic Control Mediator Pattern Demo ===\n");
        
        // Create control tower (mediator)
        ControlTower controlTower = new ControlTower();
        
        // Create aircraft (colleagues)
        CommercialAircraft flight123 = new CommercialAircraft("AA123", 180, controlTower);
        CargoAircraft cargo456 = new CargoAircraft("FX456", 50.5, controlTower);
        PrivateAircraft private789 = new PrivateAircraft("N789XY", "John Smith", controlTower);
        
        Thread.sleep(1000);
        
        // Aircraft operations
        System.out.println("\n=== Aircraft Operations ===");
        
        // Set initial altitudes
        flight123.changeAltitude(35000);
        cargo456.changeAltitude(30000);
        private789.changeAltitude(5000);
        
        Thread.sleep(1000);
        controlTower.printStatus();
        
        // Request altitude changes
        System.out.println("=== Altitude Change Requests ===");
        flight123.requestAltitudeChange(30000); // Conflict with cargo
        cargo456.requestAltitudeChange(25000);  // Available
        
        Thread.sleep(1000);
        
        // Landing requests
        System.out.println("\n=== Landing Requests ===");
        flight123.requestLanding();
        cargo456.requestLanding();
        private789.requestLanding();
        
        Thread.sleep(5000); // Wait for landing operations
        
        // Create more aircraft for takeoff
        CommercialAircraft flight999 = new CommercialAircraft("UA999", 200, controlTower);
        PrivateAircraft privateLMN = new PrivateAircraft("N123LM", "Jane Doe", controlTower);
        
        System.out.println("\n=== Takeoff Requests ===");
        flight999.requestTakeoff();
        privateLMN.requestTakeoff();
        
        Thread.sleep(3000);
        
        // Emergency scenario
        System.out.println("\n=== Emergency Scenario ===");
        EmergencyAircraft emergency = new EmergencyAircraft("EMR001", "Engine Failure", controlTower);
        emergency.changeAltitude(15000);
        
        Thread.sleep(1000);
        
        emergency.declareEmergency("Requesting immediate landing - single engine failure");
        
        Thread.sleep(2000);
        
        // More aircraft trying to land during emergency
        CommercialAircraft flight555 = new CommercialAircraft("DL555", 150, controlTower);
        flight555.changeAltitude(20000);
        flight555.requestLanding();
        
        Thread.sleep(5000);
        
        controlTower.printStatus();
        
        // Altitude conflict scenario
        System.out.println("\n=== Complex Altitude Management ===");
        flight555.requestAltitudeChange(15000); // Conflict with emergency aircraft
        
        Thread.sleep(2000);
        
        System.out.println("\n=== Final Status ===");
        controlTower.printStatus();
    }
}

/*
Expected Output:
=== Air Traffic Control Mediator Pattern Demo ===

[CONTROL TOWER] AA123 registered in our airspace
[AA123 - Commercial with 180 passengers] Received: [timestamp] AA123 has entered our airspace at altitude 0 feet
[CONTROL TOWER] FX456 registered in our airspace
[FX456 - Cargo 50.5 tons] Received: [timestamp] FX456 has entered our airspace at altitude 0 feet
[AA123 - Commercial with 180 passengers] Received: [timestamp] FX456 has entered our airspace at altitude 0 feet
[CONTROL TOWER] N789XY registered in our airspace
[N789XY - Private (John Smith)] Received: [timestamp] N789XY has entered our airspace at altitude 0 feet
[AA123 - Commercial with 180 passengers] Received: [timestamp] N789XY has entered our airspace at altitude 0 feet
[FX456 - Cargo 50.5 tons] Received: [timestamp] N789XY has entered our airspace at altitude 0 feet

=== Aircraft Operations ===

=== CONTROL TOWER STATUS ===
Aircraft in airspace: 3
Landing queue: 0
Takeoff queue: 0
Runway available: true
Altitude levels occupied: [0, 35000, 30000, 5000]
=============================

=== Altitude Change Requests ===
[CONTROL TOWER] AA123 requesting altitude change to 30000 feet
[AA123 - Commercial with 180 passengers] Received: [CONTROL TOWER] Altitude 30000 is occupied. Alternative altitude 31000 available
[CONTROL TOWER] FX456 requesting altitude change to 25000 feet
[FX456 - Cargo 50.5 tons] Received: [CONTROL TOWER] Cleared to altitude 25000 feet

=== Landing Requests ===
[CONTROL TOWER] Received landing request from AA123
[CONTROL TOWER] AA123 cleared for landing
[AA123 - Commercial with 180 passengers] Received: [CONTROL TOWER] Cleared for landing on runway 27. Wind 270 at 10 knots
[CONTROL TOWER] Received landing request from FX456
[FX456 - Cargo 50.5 tons] Received: [CONTROL TOWER] You are number 1 for landing. Please hold at altitude 25000
[CONTROL TOWER] FX456 added to landing queue, position 1
[CONTROL TOWER] Received landing request from N789XY
[N789XY - Private (John Smith)] Received: [CONTROL TOWER] You are number 2 for landing. Please hold at altitude 5000
[CONTROL TOWER] N789XY added to landing queue, position 2
[AA123] Landed safely
[CONTROL TOWER] AA123 has landed safely. Runway clear
[CONTROL TOWER] FX456 cleared for landing
[FX456 - Cargo 50.5 tons] Received: [CONTROL TOWER] Cleared for landing on runway 27. Wind 270 at 10 knots
[FX456] Landed safely
[CONTROL TOWER] FX456 has landed safely. Runway clear
[CONTROL TOWER] N789XY cleared for landing
[N789XY - Private (John Smith)] Received: [CONTROL TOWER] Cleared for landing on runway 27. Wind 270 at 10 knots

=== Takeoff Requests ===
[CONTROL TOWER] UA999 registered in our airspace
[CONTROL TOWER] N123LM registered in our airspace
[CONTROL TOWER] Received takeoff request from UA999
[CONTROL TOWER] UA999 cleared for takeoff
[UA999 - Commercial with 200 passengers] Received: [CONTROL TOWER] Cleared for takeoff on runway 27. Contact departure on 121.9
[CONTROL TOWER] Received takeoff request from N123LM
[N123LM - Private (Jane Doe)] Received: [CONTROL TOWER] You are number 1 for takeoff. Please hold at gate
[N123LM] Received takeoff queue, position 1
[UA999] Taking off
[CONTROL TOWER] UA999 airborne. Runway clear

=== Emergency Scenario ===
[CONTROL TOWER] EMR001 registered in our airspace
[CONTROL TOWER] EMERGENCY from EMR001: Requesting immediate landing - single engine failure
[EMR001 - EMERGENCY Engine Failure] Received: EMERGENCY BROADCAST - EMR001: Requesting immediate landing - single engine failure
[CONTROL TOWER] Clearing runway for emergency aircraft EMR001
[CONTROL TOWER] EMR001 cleared for landing
[EMR001 - EMERGENCY Engine Failure] Received: [CONTROL TOWER] Cleared for landing on runway 27. Wind 270 at 10 knots
[EMR001] Landed safely
[CONTROL TOWER] EMR001 has landed safely. Runway clear

=== Complex Altitude Management ===
[CONTROL TOWER] DL555 requesting altitude change to 15000 feet
[DL555 - Commercial with 150 passengers] Received: [CONTROL TOWER] Altitude 15000 is occupied. Alternative altitude 16000 available

=== Final Status ===
Aircraft in airspace: 6
Landing queue: 0
Takeoff queue: 1
Runway available: true
Altitude levels occupied: [0, 1000, 20000, 16000, 15000]
=============================
*/