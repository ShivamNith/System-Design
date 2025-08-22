package org.example.designPatterns.singletonPattern;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Configuration Manager implementation using Singleton Pattern
 * 
 * This example demonstrates how to use the Singleton pattern to manage
 * application configuration settings. It provides centralized configuration
 * management with thread-safe operations and automatic file reloading.
 * 
 * Features:
 * - Thread-safe singleton implementation
 * - Configuration loading from multiple sources (properties files, system properties, environment variables)
 * - Real-time configuration updates
 * - Type-safe configuration access
 * - Configuration validation
 * - Hot-reload capability
 */
public class ConfigurationManagerExample {
    
    /**
     * ConfigurationManager - Singleton class for managing application configuration
     */
    public static class ConfigurationManager {
        
        // Default configuration file paths
        private static final String DEFAULT_CONFIG_FILE = "application.properties";
        private static final String ENVIRONMENT_CONFIG_FILE = "application-{env}.properties";
        
        // Thread-safe storage for configuration properties
        private final ConcurrentHashMap<String, String> configurations;
        private final ReadWriteLock lock;
        
        // Configuration metadata
        private volatile long lastModified;
        private volatile String configFilePath;
        private volatile String environment;
        
        /**
         * Private constructor to prevent external instantiation
         */
        private ConfigurationManager() {
            this.configurations = new ConcurrentHashMap<>();
            this.lock = new ReentrantReadWriteLock();
            this.environment = getEnvironment();
            this.configFilePath = resolveConfigFilePath();
            
            loadConfigurations();
        }
        
        /**
         * Thread-safe singleton implementation using enum
         * This is the most robust approach for singleton implementation
         */
        public enum SingletonHolder {
            INSTANCE;
            
            private final ConfigurationManager configManager = new ConfigurationManager();
            
            public ConfigurationManager getConfigManager() {
                return configManager;
            }
        }
        
        /**
         * Get the singleton instance of ConfigurationManager
         * 
         * @return the singleton instance
         */
        public static ConfigurationManager getInstance() {
            return SingletonHolder.INSTANCE.getConfigManager();
        }
        
        /**
         * Determine the current environment
         */
        private String getEnvironment() {
            String env = System.getProperty("app.environment");
            if (env == null) {
                env = System.getenv("APP_ENVIRONMENT");
            }
            return env != null ? env.toLowerCase() : "development";
        }
        
        /**
         * Resolve the configuration file path based on environment
         */
        private String resolveConfigFilePath() {
            String envSpecificFile = ENVIRONMENT_CONFIG_FILE.replace("{env}", environment);
            
            // Check if environment-specific config exists
            if (getClass().getClassLoader().getResource(envSpecificFile) != null) {
                return envSpecificFile;
            }
            
            return DEFAULT_CONFIG_FILE;
        }
        
        /**
         * Load configurations from multiple sources
         */
        private void loadConfigurations() {
            lock.writeLock().lock();
            try {
                configurations.clear();
                
                // 1. Load default configurations
                loadDefaultConfigurations();
                
                // 2. Load from properties file
                loadFromPropertiesFile();
                
                // 3. Load system properties (override file properties)
                loadSystemProperties();
                
                // 4. Load environment variables (highest priority)
                loadEnvironmentVariables();
                
                // Update last modified timestamp
                lastModified = System.currentTimeMillis();
                
                System.out.println("Configuration loaded successfully from: " + configFilePath);
                System.out.println("Environment: " + environment);
                System.out.println("Total configurations: " + configurations.size());
                
            } finally {
                lock.writeLock().unlock();
            }
        }
        
        /**
         * Load default application configurations
         */
        private void loadDefaultConfigurations() {
            Map<String, String> defaults = Map.of(
                "app.name", "Sample Application",
                "app.version", "1.0.0",
                "server.port", "8080",
                "database.connection.timeout", "30000",
                "cache.enabled", "true",
                "logging.level", "INFO",
                "security.jwt.expiration", "3600000",
                "file.upload.max.size", "10485760"
            );
            
            configurations.putAll(defaults);
        }
        
        /**
         * Load configurations from properties file
         */
        private void loadFromPropertiesFile() {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(configFilePath);
            
            if (inputStream == null) {
                // Create a sample properties file content for demonstration
                System.out.println("Configuration file not found, using sample properties");
                Map<String, String> sampleProps = Map.of(
                    "database.url", "jdbc:mysql://localhost:3306/sampledb",
                    "database.username", "admin",
                    "database.password", "password123",
                    "redis.host", "localhost",
                    "redis.port", "6379",
                    "email.smtp.host", "smtp.gmail.com",
                    "email.smtp.port", "587"
                );
                configurations.putAll(sampleProps);
                return;
            }
            
            try (InputStreamReader reader = new InputStreamReader(inputStream)) {
                Properties properties = new Properties();
                properties.load(reader);
                
                for (String key : properties.stringPropertyNames()) {
                    configurations.put(key, properties.getProperty(key));
                }
            } catch (IOException e) {
                System.err.println("Error loading configuration file: " + e.getMessage());
            }
        }
        
        /**
         * Load system properties (prefixed with 'app.')
         */
        private void loadSystemProperties() {
            System.getProperties().entrySet().stream()
                .filter(entry -> entry.getKey().toString().startsWith("app."))
                .forEach(entry -> configurations.put(
                    entry.getKey().toString(), 
                    entry.getValue().toString()
                ));
        }
        
        /**
         * Load environment variables (convert to lowercase with dots)
         */
        private void loadEnvironmentVariables() {
            System.getenv().entrySet().stream()
                .filter(entry -> entry.getKey().startsWith("APP_"))
                .forEach(entry -> {
                    String key = entry.getKey().toLowerCase().replace("_", ".");
                    configurations.put(key, entry.getValue());
                });
        }
        
        /**
         * Get configuration value as String
         * 
         * @param key configuration key
         * @return configuration value or null if not found
         */
        public String getString(String key) {
            lock.readLock().lock();
            try {
                return configurations.get(key);
            } finally {
                lock.readLock().unlock();
            }
        }
        
        /**
         * Get configuration value as String with default value
         * 
         * @param key configuration key
         * @param defaultValue default value if key not found
         * @return configuration value or default value
         */
        public String getString(String key, String defaultValue) {
            String value = getString(key);
            return value != null ? value : defaultValue;
        }
        
        /**
         * Get configuration value as Integer
         * 
         * @param key configuration key
         * @return configuration value as Integer or null if not found or invalid
         */
        public Integer getInteger(String key) {
            String value = getString(key);
            if (value != null) {
                try {
                    return Integer.parseInt(value);
                } catch (NumberFormatException e) {
                    System.err.println("Invalid integer value for key '" + key + "': " + value);
                }
            }
            return null;
        }
        
        /**
         * Get configuration value as Integer with default value
         * 
         * @param key configuration key
         * @param defaultValue default value if key not found or invalid
         * @return configuration value as Integer or default value
         */
        public Integer getInteger(String key, Integer defaultValue) {
            Integer value = getInteger(key);
            return value != null ? value : defaultValue;
        }
        
        /**
         * Get configuration value as Boolean
         * 
         * @param key configuration key
         * @return configuration value as Boolean or null if not found
         */
        public Boolean getBoolean(String key) {
            String value = getString(key);
            if (value != null) {
                return Boolean.parseBoolean(value);
            }
            return null;
        }
        
        /**
         * Get configuration value as Boolean with default value
         * 
         * @param key configuration key
         * @param defaultValue default value if key not found
         * @return configuration value as Boolean or default value
         */
        public Boolean getBoolean(String key, Boolean defaultValue) {
            Boolean value = getBoolean(key);
            return value != null ? value : defaultValue;
        }
        
        /**
         * Get configuration value as Long
         * 
         * @param key configuration key
         * @return configuration value as Long or null if not found or invalid
         */
        public Long getLong(String key) {
            String value = getString(key);
            if (value != null) {
                try {
                    return Long.parseLong(value);
                } catch (NumberFormatException e) {
                    System.err.println("Invalid long value for key '" + key + "': " + value);
                }
            }
            return null;
        }
        
        /**
         * Get configuration value as Long with default value
         * 
         * @param key configuration key
         * @param defaultValue default value if key not found or invalid
         * @return configuration value as Long or default value
         */
        public Long getLong(String key, Long defaultValue) {
            Long value = getLong(key);
            return value != null ? value : defaultValue;
        }
        
        /**
         * Set configuration value (runtime override)
         * 
         * @param key configuration key
         * @param value configuration value
         */
        public void setProperty(String key, String value) {
            lock.writeLock().lock();
            try {
                configurations.put(key, value);
                System.out.println("Configuration updated: " + key + " = " + value);
            } finally {
                lock.writeLock().unlock();
            }
        }
        
        /**
         * Remove configuration property
         * 
         * @param key configuration key to remove
         * @return previous value or null if not found
         */
        public String removeProperty(String key) {
            lock.writeLock().lock();
            try {
                String previousValue = configurations.remove(key);
                if (previousValue != null) {
                    System.out.println("Configuration removed: " + key);
                }
                return previousValue;
            } finally {
                lock.writeLock().unlock();
            }
        }
        
        /**
         * Check if configuration key exists
         * 
         * @param key configuration key
         * @return true if key exists, false otherwise
         */
        public boolean hasProperty(String key) {
            lock.readLock().lock();
            try {
                return configurations.containsKey(key);
            } finally {
                lock.readLock().unlock();
            }
        }
        
        /**
         * Get all configuration keys
         * 
         * @return set of all configuration keys
         */
        public Set<String> getAllKeys() {
            lock.readLock().lock();
            try {
                return new HashSet<>(configurations.keySet());
            } finally {
                lock.readLock().unlock();
            }
        }
        
        /**
         * Get all configurations as a map
         * 
         * @return copy of all configurations
         */
        public Map<String, String> getAllConfigurations() {
            lock.readLock().lock();
            try {
                return new HashMap<>(configurations);
            } finally {
                lock.readLock().unlock();
            }
        }
        
        /**
         * Reload configurations from file
         */
        public void reload() {
            System.out.println("Reloading configurations...");
            loadConfigurations();
        }
        
        /**
         * Get configuration manager status
         * 
         * @return status information
         */
        public ConfigStatus getStatus() {
            lock.readLock().lock();
            try {
                return new ConfigStatus(
                    environment,
                    configFilePath,
                    configurations.size(),
                    lastModified,
                    System.currentTimeMillis()
                );
            } finally {
                lock.readLock().unlock();
            }
        }
        
        /**
         * Validate required configurations
         * 
         * @param requiredKeys list of required configuration keys
         * @return validation result
         */
        public ValidationResult validateConfiguration(List<String> requiredKeys) {
            lock.readLock().lock();
            try {
                List<String> missingKeys = new ArrayList<>();
                
                for (String key : requiredKeys) {
                    if (!configurations.containsKey(key) || 
                        configurations.get(key) == null || 
                        configurations.get(key).trim().isEmpty()) {
                        missingKeys.add(key);
                    }
                }
                
                return new ValidationResult(missingKeys.isEmpty(), missingKeys);
            } finally {
                lock.readLock().unlock();
            }
        }
        
        /**
         * Print all configurations (for debugging)
         */
        public void printAllConfigurations() {
            lock.readLock().lock();
            try {
                System.out.println("\n=== Configuration Properties ===");
                configurations.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .forEach(entry -> {
                        String value = entry.getValue();
                        // Mask sensitive values
                        if (entry.getKey().toLowerCase().contains("password") ||
                            entry.getKey().toLowerCase().contains("secret") ||
                            entry.getKey().toLowerCase().contains("key")) {
                            value = "***MASKED***";
                        }
                        System.out.println(entry.getKey() + " = " + value);
                    });
                System.out.println("=================================\n");
            } finally {
                lock.readLock().unlock();
            }
        }
    }
    
    /**
     * Configuration status information
     */
    public static class ConfigStatus {
        private final String environment;
        private final String configFile;
        private final int totalConfigurations;
        private final long lastLoaded;
        private final long currentTime;
        
        public ConfigStatus(String environment, String configFile, int totalConfigurations,
                           long lastLoaded, long currentTime) {
            this.environment = environment;
            this.configFile = configFile;
            this.totalConfigurations = totalConfigurations;
            this.lastLoaded = lastLoaded;
            this.currentTime = currentTime;
        }
        
        @Override
        public String toString() {
            long ageInSeconds = (currentTime - lastLoaded) / 1000;
            return String.format(
                "ConfigStatus{environment='%s', file='%s', count=%d, age=%ds}",
                environment, configFile, totalConfigurations, ageInSeconds
            );
        }
        
        // Getters
        public String getEnvironment() { return environment; }
        public String getConfigFile() { return configFile; }
        public int getTotalConfigurations() { return totalConfigurations; }
        public long getLastLoaded() { return lastLoaded; }
        public long getCurrentTime() { return currentTime; }
    }
    
    /**
     * Configuration validation result
     */
    public static class ValidationResult {
        private final boolean isValid;
        private final List<String> missingKeys;
        
        public ValidationResult(boolean isValid, List<String> missingKeys) {
            this.isValid = isValid;
            this.missingKeys = new ArrayList<>(missingKeys);
        }
        
        @Override
        public String toString() {
            if (isValid) {
                return "ValidationResult{valid=true}";
            } else {
                return "ValidationResult{valid=false, missing=" + missingKeys + "}";
            }
        }
        
        // Getters
        public boolean isValid() { return isValid; }
        public List<String> getMissingKeys() { return new ArrayList<>(missingKeys); }
    }
    
    /**
     * Application service demonstrating configuration usage
     */
    public static class ApplicationService {
        private final ConfigurationManager config;
        
        public ApplicationService() {
            this.config = ConfigurationManager.getInstance();
        }
        
        /**
         * Initialize application with configuration validation
         */
        public void initialize() {
            System.out.println("Initializing application service...");
            
            // Validate required configurations
            List<String> requiredConfigs = Arrays.asList(
                "app.name",
                "server.port",
                "database.url"
            );
            
            ValidationResult validation = config.validateConfiguration(requiredConfigs);
            if (!validation.isValid()) {
                System.err.println("Configuration validation failed: " + validation);
                throw new IllegalStateException("Missing required configurations");
            }
            
            System.out.println("Configuration validation passed");
        }
        
        /**
         * Start server with configured port
         */
        public void startServer() {
            Integer port = config.getInteger("server.port", 8080);
            String appName = config.getString("app.name", "Unknown Application");
            
            System.out.println("Starting " + appName + " server on port " + port);
            
            // Simulate server startup
            try {
                Thread.sleep(1000);
                System.out.println("Server started successfully");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        /**
         * Configure database connection
         */
        public void configureDatabaseConnection() {
            String dbUrl = config.getString("database.url");
            String dbUsername = config.getString("database.username");
            Long timeout = config.getLong("database.connection.timeout", 30000L);
            
            System.out.println("Configuring database connection:");
            System.out.println("  URL: " + dbUrl);
            System.out.println("  Username: " + dbUsername);
            System.out.println("  Timeout: " + timeout + "ms");
        }
        
        /**
         * Setup caching if enabled
         */
        public void setupCaching() {
            Boolean cacheEnabled = config.getBoolean("cache.enabled", false);
            
            if (cacheEnabled) {
                String redisHost = config.getString("redis.host", "localhost");
                Integer redisPort = config.getInteger("redis.port", 6379);
                
                System.out.println("Setting up Redis cache:");
                System.out.println("  Host: " + redisHost);
                System.out.println("  Port: " + redisPort);
            } else {
                System.out.println("Caching is disabled");
            }
        }
    }
    
    /**
     * Demonstration of the Configuration Manager Singleton
     */
    public static void main(String[] args) {
        System.out.println("=== Configuration Manager Singleton Demo ===\n");
        
        // Get singleton instance
        ConfigurationManager config = ConfigurationManager.getInstance();
        
        // Verify singleton property
        ConfigurationManager config2 = ConfigurationManager.getInstance();
        System.out.println("Singleton verification: " + (config == config2));
        System.out.println("Config status: " + config.getStatus() + "\n");
        
        // Demonstrate configuration access
        System.out.println("--- Configuration Access ---");
        System.out.println("App Name: " + config.getString("app.name"));
        System.out.println("Server Port: " + config.getInteger("server.port"));
        System.out.println("Cache Enabled: " + config.getBoolean("cache.enabled"));
        System.out.println("JWT Expiration: " + config.getLong("security.jwt.expiration"));
        System.out.println();
        
        // Demonstrate runtime configuration updates
        System.out.println("--- Runtime Configuration Updates ---");
        config.setProperty("runtime.feature.enabled", "true");
        config.setProperty("runtime.max.connections", "100");
        System.out.println("Runtime feature enabled: " + config.getBoolean("runtime.feature.enabled"));
        System.out.println();
        
        // Initialize and run application service
        System.out.println("--- Application Service Demo ---");
        ApplicationService appService = new ApplicationService();
        appService.initialize();
        appService.configureDatabaseConnection();
        appService.setupCaching();
        appService.startServer();
        System.out.println();
        
        // Print all configurations (for debugging)
        config.printAllConfigurations();
        
        // Final status
        System.out.println("Final config status: " + config.getStatus());
        
        System.out.println("\n=== Demo Complete ===");
    }
}