package org.example.designPatterns.strategyPattern;

import java.util.*;

interface NavigationStrategy {
    Route calculateRoute(Location start, Location end);
    String getStrategyName();
    String getDescription();
}

class Location {
    private String name;
    private double latitude;
    private double longitude;
    
    public Location(String name, double latitude, double longitude) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }
    
    public String getName() { return name; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    
    public double distanceTo(Location other) {
        double lat1Rad = Math.toRadians(latitude);
        double lat2Rad = Math.toRadians(other.latitude);
        double deltaLat = Math.toRadians(other.latitude - latitude);
        double deltaLon = Math.toRadians(other.longitude - longitude);
        
        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
                   Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                   Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return 6371 * c;
    }
    
    @Override
    public String toString() {
        return name;
    }
}

class Route {
    private List<Location> waypoints;
    private double totalDistance;
    private double estimatedTime;
    private String routeType;
    private List<String> instructions;
    
    public Route(String routeType) {
        this.waypoints = new ArrayList<>();
        this.instructions = new ArrayList<>();
        this.routeType = routeType;
        this.totalDistance = 0;
        this.estimatedTime = 0;
    }
    
    public void addWaypoint(Location location) {
        if (!waypoints.isEmpty()) {
            Location lastPoint = waypoints.get(waypoints.size() - 1);
            totalDistance += lastPoint.distanceTo(location);
        }
        waypoints.add(location);
    }
    
    public void addInstruction(String instruction) {
        instructions.add(instruction);
    }
    
    public void setEstimatedTime(double time) {
        this.estimatedTime = time;
    }
    
    public double getTotalDistance() { return totalDistance; }
    public double getEstimatedTime() { return estimatedTime; }
    public String getRouteType() { return routeType; }
    public List<Location> getWaypoints() { return waypoints; }
    public List<String> getInstructions() { return instructions; }
    
    public void displayRoute() {
        System.out.println("\n=== " + routeType + " Route ===");
        System.out.println("Distance: " + String.format("%.2f", totalDistance) + " km");
        System.out.println("Estimated Time: " + String.format("%.0f", estimatedTime) + " minutes");
        System.out.println("\nRoute: ");
        for (int i = 0; i < waypoints.size(); i++) {
            System.out.print(waypoints.get(i).getName());
            if (i < waypoints.size() - 1) {
                System.out.print(" → ");
            }
        }
        System.out.println("\n\nTurn-by-turn directions:");
        for (int i = 0; i < instructions.size(); i++) {
            System.out.println((i + 1) + ". " + instructions.get(i));
        }
    }
}

class FastestRouteStrategy implements NavigationStrategy {
    @Override
    public Route calculateRoute(Location start, Location end) {
        Route route = new Route("Fastest");
        
        route.addWaypoint(start);
        route.addWaypoint(new Location("Highway Entrance", 
            (start.getLatitude() + end.getLatitude()) / 2 + 0.05,
            (start.getLongitude() + end.getLongitude()) / 2));
        route.addWaypoint(end);
        
        route.addInstruction("Start at " + start.getName());
        route.addInstruction("Take the highway entrance");
        route.addInstruction("Continue on highway for " + 
            String.format("%.1f", route.getTotalDistance() * 0.8) + " km");
        route.addInstruction("Exit highway");
        route.addInstruction("Arrive at " + end.getName());
        
        double avgSpeed = 90;
        route.setEstimatedTime((route.getTotalDistance() / avgSpeed) * 60);
        
        return route;
    }
    
    @Override
    public String getStrategyName() {
        return "Fastest Route";
    }
    
    @Override
    public String getDescription() {
        return "Prioritizes highways and main roads for minimum travel time";
    }
}

class ShortestRouteStrategy implements NavigationStrategy {
    @Override
    public Route calculateRoute(Location start, Location end) {
        Route route = new Route("Shortest");
        
        route.addWaypoint(start);
        route.addWaypoint(end);
        
        route.addInstruction("Start at " + start.getName());
        route.addInstruction("Head directly towards " + end.getName());
        route.addInstruction("Continue for " + 
            String.format("%.1f", route.getTotalDistance()) + " km");
        route.addInstruction("Arrive at " + end.getName());
        
        double avgSpeed = 50;
        route.setEstimatedTime((route.getTotalDistance() / avgSpeed) * 60);
        
        return route;
    }
    
    @Override
    public String getStrategyName() {
        return "Shortest Route";
    }
    
    @Override
    public String getDescription() {
        return "Minimizes total distance traveled";
    }
}

class ScenicRouteStrategy implements NavigationStrategy {
    @Override
    public Route calculateRoute(Location start, Location end) {
        Route route = new Route("Scenic");
        
        route.addWaypoint(start);
        route.addWaypoint(new Location("Lakeside View", 
            start.getLatitude() + 0.02,
            start.getLongitude() + 0.03));
        route.addWaypoint(new Location("Mountain Pass", 
            (start.getLatitude() + end.getLatitude()) / 2,
            (start.getLongitude() + end.getLongitude()) / 2 - 0.05));
        route.addWaypoint(new Location("Historic Bridge", 
            end.getLatitude() - 0.01,
            end.getLongitude() - 0.02));
        route.addWaypoint(end);
        
        route.addInstruction("Start at " + start.getName());
        route.addInstruction("Drive along the lakeside road");
        route.addInstruction("Stop at Lakeside View for photos");
        route.addInstruction("Continue through Mountain Pass");
        route.addInstruction("Cross the Historic Bridge");
        route.addInstruction("Arrive at " + end.getName());
        
        double avgSpeed = 40;
        route.setEstimatedTime((route.getTotalDistance() / avgSpeed) * 60);
        
        return route;
    }
    
    @Override
    public String getStrategyName() {
        return "Scenic Route";
    }
    
    @Override
    public String getDescription() {
        return "Takes you through beautiful landscapes and points of interest";
    }
}

class EcoFriendlyRouteStrategy implements NavigationStrategy {
    @Override
    public Route calculateRoute(Location start, Location end) {
        Route route = new Route("Eco-Friendly");
        
        route.addWaypoint(start);
        route.addWaypoint(new Location("City Center", 
            (start.getLatitude() + end.getLatitude()) / 2,
            (start.getLongitude() + end.getLongitude()) / 2 + 0.02));
        route.addWaypoint(end);
        
        route.addInstruction("Start at " + start.getName());
        route.addInstruction("Drive at optimal speed (80 km/h)");
        route.addInstruction("Avoid heavy traffic areas");
        route.addInstruction("Use cruise control when possible");
        route.addInstruction("Arrive at " + end.getName());
        
        double avgSpeed = 70;
        route.setEstimatedTime((route.getTotalDistance() / avgSpeed) * 60);
        
        return route;
    }
    
    @Override
    public String getStrategyName() {
        return "Eco-Friendly Route";
    }
    
    @Override
    public String getDescription() {
        return "Optimizes for fuel efficiency and lower emissions";
    }
}

class TollFreeRouteStrategy implements NavigationStrategy {
    @Override
    public Route calculateRoute(Location start, Location end) {
        Route route = new Route("Toll-Free");
        
        route.addWaypoint(start);
        route.addWaypoint(new Location("Local Road Junction", 
            start.getLatitude() + 0.01,
            start.getLongitude() - 0.01));
        route.addWaypoint(new Location("State Route", 
            (start.getLatitude() + end.getLatitude()) / 2 - 0.03,
            (start.getLongitude() + end.getLongitude()) / 2));
        route.addWaypoint(end);
        
        route.addInstruction("Start at " + start.getName());
        route.addInstruction("Take local roads to avoid toll");
        route.addInstruction("Join State Route (no tolls)");
        route.addInstruction("Continue on alternate roads");
        route.addInstruction("Arrive at " + end.getName());
        
        double avgSpeed = 60;
        route.setEstimatedTime((route.getTotalDistance() / avgSpeed) * 60);
        
        return route;
    }
    
    @Override
    public String getStrategyName() {
        return "Toll-Free Route";
    }
    
    @Override
    public String getDescription() {
        return "Avoids all toll roads and bridges";
    }
}

class GPSNavigator {
    private NavigationStrategy navigationStrategy;
    private Location currentLocation;
    
    public GPSNavigator(Location startLocation) {
        this.currentLocation = startLocation;
    }
    
    public void setNavigationStrategy(NavigationStrategy strategy) {
        this.navigationStrategy = strategy;
        System.out.println("\nNavigation mode set to: " + strategy.getStrategyName());
        System.out.println("Description: " + strategy.getDescription());
    }
    
    public Route navigate(Location destination) {
        if (navigationStrategy == null) {
            throw new IllegalStateException("Please select a navigation strategy!");
        }
        
        System.out.println("\nCalculating route from " + currentLocation.getName() + 
                         " to " + destination.getName() + "...");
        
        Route route = navigationStrategy.calculateRoute(currentLocation, destination);
        route.displayRoute();
        
        return route;
    }
    
    public void compareRoutes(Location destination) {
        NavigationStrategy[] strategies = {
            new FastestRouteStrategy(),
            new ShortestRouteStrategy(),
            new ScenicRouteStrategy(),
            new EcoFriendlyRouteStrategy(),
            new TollFreeRouteStrategy()
        };
        
        System.out.println("\n=== Route Comparison ===");
        System.out.println("From: " + currentLocation.getName());
        System.out.println("To: " + destination.getName());
        System.out.println("\nAvailable routes:");
        
        List<Route> routes = new ArrayList<>();
        
        for (NavigationStrategy strategy : strategies) {
            Route route = strategy.calculateRoute(currentLocation, destination);
            routes.add(route);
            
            System.out.println("\n" + strategy.getStrategyName() + ":");
            System.out.println("  Distance: " + String.format("%.2f", route.getTotalDistance()) + " km");
            System.out.println("  Time: " + String.format("%.0f", route.getEstimatedTime()) + " minutes");
            System.out.println("  Waypoints: " + route.getWaypoints().size());
        }
        
        Route fastest = routes.stream()
            .min(Comparator.comparing(Route::getEstimatedTime))
            .orElse(null);
        Route shortest = routes.stream()
            .min(Comparator.comparing(Route::getTotalDistance))
            .orElse(null);
        
        System.out.println("\n=== Recommendations ===");
        if (fastest != null) {
            System.out.println("Fastest: " + fastest.getRouteType() + 
                             " (" + String.format("%.0f", fastest.getEstimatedTime()) + " min)");
        }
        if (shortest != null) {
            System.out.println("Shortest: " + shortest.getRouteType() + 
                             " (" + String.format("%.2f", shortest.getTotalDistance()) + " km)");
        }
    }
    
    public void setCurrentLocation(Location location) {
        this.currentLocation = location;
    }
}

public class NavigationExample {
    public static void main(String[] args) {
        System.out.println("=== GPS Navigation Strategy Pattern Example ===");
        
        Location home = new Location("Home", 37.7749, -122.4194);
        Location office = new Location("Office", 37.7849, -122.4094);
        Location airport = new Location("Airport", 37.6213, -122.3790);
        
        GPSNavigator gps = new GPSNavigator(home);
        
        System.out.println("\n1. Testing different navigation strategies:");
        
        gps.setNavigationStrategy(new FastestRouteStrategy());
        gps.navigate(office);
        
        gps.setNavigationStrategy(new ScenicRouteStrategy());
        gps.navigate(office);
        
        gps.setNavigationStrategy(new EcoFriendlyRouteStrategy());
        gps.navigate(office);
        
        System.out.println("\n2. Comparing all routes for a longer trip:");
        gps.compareRoutes(airport);
        
        System.out.println("\n3. Interactive navigation:");
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("\nSelect navigation mode:");
        System.out.println("1. Fastest Route");
        System.out.println("2. Shortest Route");
        System.out.println("3. Scenic Route");
        System.out.println("4. Eco-Friendly Route");
        System.out.println("5. Toll-Free Route");
        System.out.print("Enter choice (1-5): ");
        
        int choice = scanner.nextInt();
        
        NavigationStrategy selectedStrategy = null;
        switch (choice) {
            case 1: selectedStrategy = new FastestRouteStrategy(); break;
            case 2: selectedStrategy = new ShortestRouteStrategy(); break;
            case 3: selectedStrategy = new ScenicRouteStrategy(); break;
            case 4: selectedStrategy = new EcoFriendlyRouteStrategy(); break;
            case 5: selectedStrategy = new TollFreeRouteStrategy(); break;
            default: 
                System.out.println("Invalid choice!");
                scanner.close();
                return;
        }
        
        gps.setNavigationStrategy(selectedStrategy);
        gps.navigate(airport);
        
        scanner.close();
    }
}