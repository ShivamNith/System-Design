package org.example.designPatterns.observerPattern;

import java.util.ArrayList;
import java.util.List;

/**
 * Weather Station Observer Pattern Example
 * 
 * This example demonstrates the Observer pattern with a weather monitoring system.
 * The weather station (subject) notifies multiple display devices (observers) 
 * when weather conditions change.
 * 
 * Key Components:
 * - WeatherData: Data model containing weather information
 * - WeatherSubject: Interface for weather data publishers
 * - WeatherObserver: Interface for weather data subscribers
 * - WeatherStation: Concrete subject that manages weather data
 * - Various Display classes: Concrete observers that show weather data
 */

// Weather data model
class WeatherData {
    private float temperature;
    private float humidity;
    private float pressure;
    private String condition;
    
    public WeatherData() {}
    
    public WeatherData(float temperature, float humidity, float pressure, String condition) {
        this.temperature = temperature;
        this.humidity = humidity;
        this.pressure = pressure;
        this.condition = condition;
    }
    
    // Getters and setters
    public float getTemperature() { return temperature; }
    public void setTemperature(float temperature) { this.temperature = temperature; }
    
    public float getHumidity() { return humidity; }
    public void setHumidity(float humidity) { this.humidity = humidity; }
    
    public float getPressure() { return pressure; }
    public void setPressure(float pressure) { this.pressure = pressure; }
    
    public String getCondition() { return condition; }
    public void setCondition(String condition) { this.condition = condition; }
    
    @Override
    public String toString() {
        return String.format("WeatherData{temp=%.1f°F, humidity=%.1f%%, pressure=%.1f inHg, condition='%s'}",
            temperature, humidity, pressure, condition);
    }
}

// Subject interface for weather station
interface WeatherSubject {
    void registerObserver(WeatherObserver observer);
    void removeObserver(WeatherObserver observer);
    void notifyObservers();
}

// Observer interface for weather displays
interface WeatherObserver {
    void update(WeatherData weatherData);
    String getDisplayName();
}

// Concrete weather station (Subject)
class WeatherStation implements WeatherSubject {
    private List<WeatherObserver> observers;
    private WeatherData currentWeatherData;
    
    public WeatherStation() {
        observers = new ArrayList<>();
        currentWeatherData = new WeatherData();
    }
    
    @Override
    public void registerObserver(WeatherObserver observer) {
        observers.add(observer);
        System.out.println("Observer registered: " + observer.getDisplayName());
    }
    
    @Override
    public void removeObserver(WeatherObserver observer) {
        int index = observers.indexOf(observer);
        if (index >= 0) {
            observers.remove(index);
            System.out.println("Observer removed: " + observer.getDisplayName());
        }
    }
    
    @Override
    public void notifyObservers() {
        System.out.println("\n--- Broadcasting weather update to " + observers.size() + " observers ---");
        for (WeatherObserver observer : observers) {
            try {
                observer.update(currentWeatherData);
            } catch (Exception e) {
                System.err.println("Error notifying observer " + observer.getDisplayName() + ": " + e.getMessage());
            }
        }
        System.out.println("--- End of broadcast ---\n");
    }
    
    public void setWeatherData(float temperature, float humidity, float pressure, String condition) {
        System.out.println("Weather Station: New data received - " + 
            String.format("%.1f°F, %.1f%% humidity, %.1f inHg, %s", 
            temperature, humidity, pressure, condition));
        
        currentWeatherData.setTemperature(temperature);
        currentWeatherData.setHumidity(humidity);
        currentWeatherData.setPressure(pressure);
        currentWeatherData.setCondition(condition);
        
        notifyObservers();
    }
    
    public WeatherData getCurrentWeatherData() {
        return currentWeatherData;
    }
    
    public int getObserverCount() {
        return observers.size();
    }
}

// Concrete observer - Current conditions display
class CurrentConditionsDisplay implements WeatherObserver {
    private WeatherData lastWeatherData;
    
    @Override
    public void update(WeatherData weatherData) {
        this.lastWeatherData = weatherData;
        display();
    }
    
    private void display() {
        System.out.println("Current Conditions Display:");
        System.out.println("  Temperature: " + lastWeatherData.getTemperature() + "°F");
        System.out.println("  Humidity: " + lastWeatherData.getHumidity() + "%");
        System.out.println("  Condition: " + lastWeatherData.getCondition());
    }
    
    @Override
    public String getDisplayName() {
        return "Current Conditions Display";
    }
}

// Concrete observer - Statistics display
class StatisticsDisplay implements WeatherObserver {
    private List<Float> temperatureHistory;
    private List<Float> humidityHistory;
    
    public StatisticsDisplay() {
        temperatureHistory = new ArrayList<>();
        humidityHistory = new ArrayList<>();
    }
    
    @Override
    public void update(WeatherData weatherData) {
        temperatureHistory.add(weatherData.getTemperature());
        humidityHistory.add(weatherData.getHumidity());
        display();
    }
    
    private void display() {
        if (temperatureHistory.isEmpty()) return;
        
        float avgTemp = (float) temperatureHistory.stream().mapToDouble(Float::doubleValue).average().orElse(0.0);
        float maxTemp = temperatureHistory.stream().max(Float::compareTo).orElse(0.0f);
        float minTemp = temperatureHistory.stream().min(Float::compareTo).orElse(0.0f);
        
        float avgHumidity = (float) humidityHistory.stream().mapToDouble(Float::doubleValue).average().orElse(0.0);
        
        System.out.println("Statistics Display (" + temperatureHistory.size() + " readings):");
        System.out.printf("  Temperature - Avg: %.1f°F, Max: %.1f°F, Min: %.1f°F%n", avgTemp, maxTemp, minTemp);
        System.out.printf("  Humidity - Avg: %.1f%%%n", avgHumidity);
    }
    
    @Override
    public String getDisplayName() {
        return "Statistics Display";
    }
    
    public void reset() {
        temperatureHistory.clear();
        humidityHistory.clear();
        System.out.println("Statistics Display: History cleared");
    }
}

// Concrete observer - Forecast display
class ForecastDisplay implements WeatherObserver {
    private WeatherData currentWeatherData;
    private WeatherData previousWeatherData;
    
    @Override
    public void update(WeatherData weatherData) {
        this.previousWeatherData = this.currentWeatherData;
        this.currentWeatherData = weatherData;
        display();
    }
    
    private void display() {
        System.out.println("Forecast Display:");
        
        if (previousWeatherData == null) {
            System.out.println("  Forecast: Insufficient data for prediction");
            return;
        }
        
        float pressureChange = currentWeatherData.getPressure() - previousWeatherData.getPressure();
        String forecast;
        
        if (pressureChange > 0.1f) {
            forecast = "Improving weather on the way!";
        } else if (pressureChange < -0.1f) {
            forecast = "Watch out for cooler, rainy weather";
        } else {
            forecast = "More of the same";
        }
        
        System.out.println("  Pressure change: " + String.format("%.2f", pressureChange) + " inHg");
        System.out.println("  Forecast: " + forecast);
    }
    
    @Override
    public String getDisplayName() {
        return "Forecast Display";
    }
}

// Concrete observer - Heat index display
class HeatIndexDisplay implements WeatherObserver {
    private float heatIndex;
    
    @Override
    public void update(WeatherData weatherData) {
        this.heatIndex = calculateHeatIndex(weatherData.getTemperature(), weatherData.getHumidity());
        display();
    }
    
    private float calculateHeatIndex(float temperature, float humidity) {
        // Simplified heat index calculation
        if (temperature < 80.0f) {
            return temperature; // Heat index not applicable below 80°F
        }
        
        float t = temperature;
        float h = humidity;
        
        // Simplified formula (actual formula is more complex)
        return (float) (t + 0.5 * (t - 80) * (h / 100.0));
    }
    
    private void display() {
        System.out.println("Heat Index Display:");
        System.out.printf("  Heat Index: %.1f°F", heatIndex);
        
        if (heatIndex >= 105) {
            System.out.println(" (DANGER - Heat exhaustion likely)");
        } else if (heatIndex >= 90) {
            System.out.println(" (CAUTION - Fatigue possible)");
        } else {
            System.out.println(" (SAFE)");
        }
    }
    
    @Override
    public String getDisplayName() {
        return "Heat Index Display";
    }
}

// Mobile app observer with notification preferences
class MobileAppDisplay implements WeatherObserver {
    private String userId;
    private boolean notificationsEnabled;
    private float temperatureThreshold;
    
    public MobileAppDisplay(String userId, float temperatureThreshold) {
        this.userId = userId;
        this.temperatureThreshold = temperatureThreshold;
        this.notificationsEnabled = true;
    }
    
    @Override
    public void update(WeatherData weatherData) {
        if (notificationsEnabled) {
            display(weatherData);
            checkAlerts(weatherData);
        }
    }
    
    private void display(WeatherData weatherData) {
        System.out.println("Mobile App (" + userId + "):");
        System.out.println("  📱 Weather Update: " + weatherData.getTemperature() + "°F, " + 
            weatherData.getCondition());
    }
    
    private void checkAlerts(WeatherData weatherData) {
        if (weatherData.getTemperature() > temperatureThreshold) {
            System.out.println("  🚨 ALERT: Temperature (" + weatherData.getTemperature() + 
                "°F) exceeds your threshold (" + temperatureThreshold + "°F)");
        }
        
        if (weatherData.getHumidity() > 85) {
            System.out.println("  💧 ALERT: High humidity (" + weatherData.getHumidity() + "%)");
        }
    }
    
    public void setNotificationsEnabled(boolean enabled) {
        this.notificationsEnabled = enabled;
        System.out.println("Mobile App (" + userId + "): Notifications " + 
            (enabled ? "enabled" : "disabled"));
    }
    
    public void setTemperatureThreshold(float threshold) {
        this.temperatureThreshold = threshold;
        System.out.println("Mobile App (" + userId + "): Temperature threshold set to " + 
            threshold + "°F");
    }
    
    @Override
    public String getDisplayName() {
        return "Mobile App (" + userId + ")";
    }
}

// Demonstration class
public class WeatherStationExample {
    public static void main(String[] args) {
        System.out.println("=== Weather Station Observer Pattern Example ===\n");
        
        // Create the weather station (subject)
        WeatherStation weatherStation = new WeatherStation();
        
        // Create various weather displays (observers)
        CurrentConditionsDisplay currentDisplay = new CurrentConditionsDisplay();
        StatisticsDisplay statisticsDisplay = new StatisticsDisplay();
        ForecastDisplay forecastDisplay = new ForecastDisplay();
        HeatIndexDisplay heatIndexDisplay = new HeatIndexDisplay();
        
        // Create mobile app observers with different preferences
        MobileAppDisplay johnApp = new MobileAppDisplay("John", 85.0f);
        MobileAppDisplay sarahApp = new MobileAppDisplay("Sarah", 75.0f);
        
        // Register observers with the weather station
        System.out.println("--- Registering Observers ---");
        weatherStation.registerObserver(currentDisplay);
        weatherStation.registerObserver(statisticsDisplay);
        weatherStation.registerObserver(forecastDisplay);
        weatherStation.registerObserver(heatIndexDisplay);
        weatherStation.registerObserver(johnApp);
        weatherStation.registerObserver(sarahApp);
        
        System.out.println("Total observers: " + weatherStation.getObserverCount() + "\n");
        
        // Simulate weather data updates
        System.out.println("=== Weather Data Updates ===");
        
        weatherStation.setWeatherData(82.0f, 70.0f, 29.92f, "Partly Cloudy");
        
        weatherStation.setWeatherData(78.0f, 65.0f, 30.15f, "Sunny");
        
        weatherStation.setWeatherData(88.0f, 85.0f, 29.85f, "Humid");
        
        // Demonstrate dynamic observer management
        System.out.println("=== Dynamic Observer Management ===");
        System.out.println("Sarah disabling notifications...");
        sarahApp.setNotificationsEnabled(false);
        
        System.out.println("Removing Heat Index Display...");
        weatherStation.removeObserver(heatIndexDisplay);
        
        weatherStation.setWeatherData(92.0f, 80.0f, 29.70f, "Hot and Humid");
        
        // Demonstrate observer preferences
        System.out.println("=== Observer Preference Changes ===");
        johnApp.setTemperatureThreshold(90.0f);
        sarahApp.setNotificationsEnabled(true);
        sarahApp.setTemperatureThreshold(85.0f);
        
        weatherStation.setWeatherData(95.0f, 75.0f, 29.60f, "Very Hot");
        
        // Reset statistics
        System.out.println("=== Statistics Reset ===");
        statisticsDisplay.reset();
        
        weatherStation.setWeatherData(72.0f, 60.0f, 30.20f, "Clear");
        
        System.out.println("\n=== Final Observer Count: " + weatherStation.getObserverCount() + " ===");
        
        demonstratePatternBenefits();
    }
    
    private static void demonstratePatternBenefits() {
        System.out.println("\n=== Observer Pattern Benefits Demonstrated ===");
        System.out.println("✓ Loose Coupling: Weather station doesn't know specific observer implementations");
        System.out.println("✓ Dynamic Subscription: Observers can be added/removed at runtime");
        System.out.println("✓ Broadcast Communication: One weather update notifies all interested parties");
        System.out.println("✓ Open/Closed Principle: New display types can be added without modifying existing code");
        System.out.println("✓ Single Responsibility: Each observer handles its own display logic");
        System.out.println("✓ Event-Driven Architecture: Updates are pushed automatically when data changes");
    }
}