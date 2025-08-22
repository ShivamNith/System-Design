package org.example.designPatterns.singletonPattern;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Logger implementation using Singleton Pattern
 * 
 * This example demonstrates how to use the Singleton pattern to implement
 * a centralized logging system. It provides thread-safe logging with multiple
 * log levels, asynchronous processing, and configurable output destinations.
 * 
 * Features:
 * - Thread-safe singleton implementation
 * - Multiple log levels (TRACE, DEBUG, INFO, WARN, ERROR, FATAL)
 * - Asynchronous logging for better performance
 * - Multiple output destinations (console, file, custom)
 * - Log formatting and filtering
 * - Performance metrics
 * - Graceful shutdown
 */
public class LoggerExample {
    
    /**
     * Log levels enumeration
     */
    public enum LogLevel {
        TRACE(0, "TRACE"),
        DEBUG(1, "DEBUG"),
        INFO(2, "INFO"),
        WARN(3, "WARN"),
        ERROR(4, "ERROR"),
        FATAL(5, "FATAL");
        
        private final int priority;
        private final String name;
        
        LogLevel(int priority, String name) {
            this.priority = priority;
            this.name = name;
        }
        
        public int getPriority() { return priority; }
        public String getName() { return name; }
        
        public boolean isEnabled(LogLevel configuredLevel) {
            return this.priority >= configuredLevel.priority;
        }
    }
    
    /**
     * Log entry data structure
     */
    public static class LogEntry {
        private final LocalDateTime timestamp;
        private final LogLevel level;
        private final String logger;
        private final String message;
        private final Throwable throwable;
        private final Thread thread;
        
        public LogEntry(LogLevel level, String logger, String message, Throwable throwable) {
            this.timestamp = LocalDateTime.now();
            this.level = level;
            this.logger = logger;
            this.message = message;
            this.throwable = throwable;
            this.thread = Thread.currentThread();
        }
        
        // Getters
        public LocalDateTime getTimestamp() { return timestamp; }
        public LogLevel getLevel() { return level; }
        public String getLogger() { return logger; }
        public String getMessage() { return message; }
        public Throwable getThrowable() { return throwable; }
        public Thread getThread() { return thread; }
    }
    
    /**
     * Log output destination interface
     */
    public interface LogAppender {
        void append(LogEntry entry, String formattedMessage);
        void flush();
        void close();
    }
    
    /**
     * Console log appender
     */
    public static class ConsoleAppender implements LogAppender {
        @Override
        public void append(LogEntry entry, String formattedMessage) {
            if (entry.getLevel().getPriority() >= LogLevel.ERROR.getPriority()) {
                System.err.println(formattedMessage);
            } else {
                System.out.println(formattedMessage);
            }
        }
        
        @Override
        public void flush() {
            System.out.flush();
            System.err.flush();
        }
        
        @Override
        public void close() {
            // Console streams are not closed
        }
    }
    
    /**
     * File log appender
     */
    public static class FileAppender implements LogAppender {
        private final PrintWriter writer;
        private final String filename;
        
        public FileAppender(String filename) throws IOException {
            this.filename = filename;
            this.writer = new PrintWriter(new FileWriter(filename, true));
        }
        
        @Override
        public void append(LogEntry entry, String formattedMessage) {
            writer.println(formattedMessage);
        }
        
        @Override
        public void flush() {
            writer.flush();
        }
        
        @Override
        public void close() {
            writer.close();
        }
        
        public String getFilename() { return filename; }
    }
    
    /**
     * Logger - Singleton class for centralized logging
     */
    public static class Logger {
        
        // Default configuration
        private static final String DEFAULT_LOG_FORMAT = "{timestamp} [{level}] {logger} - {message}";
        private static final LogLevel DEFAULT_LOG_LEVEL = LogLevel.INFO;
        private static final int DEFAULT_QUEUE_SIZE = 10000;
        
        // Thread-safe singleton instance
        private static volatile Logger instance;
        
        // Logger configuration
        private volatile LogLevel currentLogLevel;
        private volatile String logFormat;
        
        // Asynchronous logging components
        private final BlockingQueue<LogEntry> logQueue;
        private final Thread logProcessorThread;
        private final AtomicBoolean isShutdown;
        
        // Output destinations
        private final Map<String, LogAppender> appenders;
        
        // Performance metrics
        private final AtomicLong totalLogCount;
        private final AtomicLong droppedLogCount;
        private final Map<LogLevel, AtomicLong> logLevelCounts;
        
        // Date formatter for timestamps
        private final DateTimeFormatter timestampFormatter;
        
        /**
         * Private constructor to prevent external instantiation
         */
        private Logger() {
            this.currentLogLevel = DEFAULT_LOG_LEVEL;
            this.logFormat = DEFAULT_LOG_FORMAT;
            this.logQueue = new LinkedBlockingQueue<>(DEFAULT_QUEUE_SIZE);
            this.isShutdown = new AtomicBoolean(false);
            this.appenders = new ConcurrentHashMap<>();
            this.totalLogCount = new AtomicLong(0);
            this.droppedLogCount = new AtomicLong(0);
            this.logLevelCounts = new ConcurrentHashMap<>();
            this.timestampFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
            
            // Initialize log level counters
            for (LogLevel level : LogLevel.values()) {
                logLevelCounts.put(level, new AtomicLong(0));
            }
            
            // Add default console appender
            addAppender("console", new ConsoleAppender());
            
            // Start log processor thread
            this.logProcessorThread = new Thread(this::processLogs, "LogProcessor");
            this.logProcessorThread.setDaemon(true);
            this.logProcessorThread.start();
            
            System.out.println("Logger initialized with level: " + currentLogLevel);
        }
        
        /**
         * Get the singleton instance of Logger (Double-checked locking)
         * 
         * @return the singleton instance
         */
        public static Logger getInstance() {
            if (instance == null) {
                synchronized (Logger.class) {
                    if (instance == null) {
                        instance = new Logger();
                    }
                }
            }
            return instance;
        }
        
        /**
         * Set the current log level
         * 
         * @param level the new log level
         */
        public void setLogLevel(LogLevel level) {
            this.currentLogLevel = level;
            info("Logger", "Log level changed to: " + level);
        }
        
        /**
         * Get the current log level
         * 
         * @return current log level
         */
        public LogLevel getLogLevel() {
            return currentLogLevel;
        }
        
        /**
         * Set the log message format
         * 
         * @param format the new log format
         */
        public void setLogFormat(String format) {
            this.logFormat = format;
            info("Logger", "Log format changed to: " + format);
        }
        
        /**
         * Add a log appender
         * 
         * @param name appender name
         * @param appender the appender instance
         */
        public void addAppender(String name, LogAppender appender) {
            appenders.put(name, appender);
            info("Logger", "Added appender: " + name);
        }
        
        /**
         * Remove a log appender
         * 
         * @param name appender name
         * @return the removed appender or null if not found
         */
        public LogAppender removeAppender(String name) {
            LogAppender removed = appenders.remove(name);
            if (removed != null) {
                info("Logger", "Removed appender: " + name);
            }
            return removed;
        }
        
        /**
         * Log a message at TRACE level
         */
        public void trace(String logger, String message) {
            log(LogLevel.TRACE, logger, message, null);
        }
        
        /**
         * Log a message at DEBUG level
         */
        public void debug(String logger, String message) {
            log(LogLevel.DEBUG, logger, message, null);
        }
        
        /**
         * Log a message at INFO level
         */
        public void info(String logger, String message) {
            log(LogLevel.INFO, logger, message, null);
        }
        
        /**
         * Log a message at WARN level
         */
        public void warn(String logger, String message) {
            log(LogLevel.WARN, logger, message, null);
        }
        
        /**
         * Log a message at WARN level with exception
         */
        public void warn(String logger, String message, Throwable throwable) {
            log(LogLevel.WARN, logger, message, throwable);
        }
        
        /**
         * Log a message at ERROR level
         */
        public void error(String logger, String message) {
            log(LogLevel.ERROR, logger, message, null);
        }
        
        /**
         * Log a message at ERROR level with exception
         */
        public void error(String logger, String message, Throwable throwable) {
            log(LogLevel.ERROR, logger, message, throwable);
        }
        
        /**
         * Log a message at FATAL level
         */
        public void fatal(String logger, String message) {
            log(LogLevel.FATAL, logger, message, null);
        }
        
        /**
         * Log a message at FATAL level with exception
         */
        public void fatal(String logger, String message, Throwable throwable) {
            log(LogLevel.FATAL, logger, message, throwable);
        }
        
        /**
         * Generic log method
         * 
         * @param level log level
         * @param logger logger name
         * @param message log message
         * @param throwable optional exception
         */
        public void log(LogLevel level, String logger, String message, Throwable throwable) {
            // Check if log level is enabled
            if (!level.isEnabled(currentLogLevel)) {
                return;
            }
            
            // Check if logger is shutdown
            if (isShutdown.get()) {
                return;
            }
            
            // Create log entry
            LogEntry entry = new LogEntry(level, logger, message, throwable);
            
            // Try to add to queue (non-blocking)
            boolean added = logQueue.offer(entry);
            if (!added) {
                droppedLogCount.incrementAndGet();
                // Fallback: log directly to console for critical messages
                if (level.getPriority() >= LogLevel.ERROR.getPriority()) {
                    String formatted = formatLogEntry(entry);
                    System.err.println("[QUEUE_FULL] " + formatted);
                }
            }
        }
        
        /**
         * Process log entries asynchronously
         */
        private void processLogs() {
            while (!isShutdown.get() || !logQueue.isEmpty()) {
                try {
                    LogEntry entry = logQueue.poll();
                    if (entry != null) {
                        processLogEntry(entry);
                    } else {
                        // Brief pause to prevent busy waiting
                        Thread.sleep(10);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    // Log processor should never crash
                    System.err.println("Error in log processor: " + e.getMessage());
                }
            }
        }
        
        /**
         * Process a single log entry
         * 
         * @param entry the log entry to process
         */
        private void processLogEntry(LogEntry entry) {
            try {
                // Update metrics
                totalLogCount.incrementAndGet();
                logLevelCounts.get(entry.getLevel()).incrementAndGet();
                
                // Format the message
                String formattedMessage = formatLogEntry(entry);
                
                // Send to all appenders
                for (LogAppender appender : appenders.values()) {
                    try {
                        appender.append(entry, formattedMessage);
                    } catch (Exception e) {
                        // Don't let appender errors crash the logger
                        System.err.println("Error in log appender: " + e.getMessage());
                    }
                }
            } catch (Exception e) {
                System.err.println("Error processing log entry: " + e.getMessage());
            }
        }
        
        /**
         * Format a log entry according to the current format
         * 
         * @param entry the log entry
         * @return formatted log message
         */
        private String formatLogEntry(LogEntry entry) {
            String formatted = logFormat
                .replace("{timestamp}", timestampFormatter.format(entry.getTimestamp()))
                .replace("{level}", entry.getLevel().getName())
                .replace("{logger}", entry.getLogger())
                .replace("{message}", entry.getMessage())
                .replace("{thread}", entry.getThread().getName());
            
            // Add exception stack trace if present
            if (entry.getThrowable() != null) {
                StringWriter sw = new StringWriter();
                entry.getThrowable().printStackTrace(new PrintWriter(sw));
                formatted += "\n" + sw.toString();
            }
            
            return formatted;
        }
        
        /**
         * Flush all appenders
         */
        public void flush() {
            for (LogAppender appender : appenders.values()) {
                try {
                    appender.flush();
                } catch (Exception e) {
                    System.err.println("Error flushing appender: " + e.getMessage());
                }
            }
        }
        
        /**
         * Get logging statistics
         * 
         * @return logging statistics
         */
        public LoggingStats getStats() {
            Map<LogLevel, Long> levelCounts = new HashMap<>();
            for (Map.Entry<LogLevel, AtomicLong> entry : logLevelCounts.entrySet()) {
                levelCounts.put(entry.getKey(), entry.getValue().get());
            }
            
            return new LoggingStats(
                totalLogCount.get(),
                droppedLogCount.get(),
                logQueue.size(),
                levelCounts,
                appenders.size(),
                currentLogLevel
            );
        }
        
        /**
         * Shutdown the logger gracefully
         */
        public void shutdown() {
            if (isShutdown.compareAndSet(false, true)) {
                info("Logger", "Shutting down logger...");
                
                try {
                    // Wait for log processor to finish
                    logProcessorThread.join(5000); // Wait up to 5 seconds
                    
                    // Flush and close all appenders
                    for (Map.Entry<String, LogAppender> entry : appenders.entrySet()) {
                        try {
                            entry.getValue().flush();
                            entry.getValue().close();
                        } catch (Exception e) {
                            System.err.println("Error closing appender " + entry.getKey() + ": " + e.getMessage());
                        }
                    }
                    
                    System.out.println("Logger shutdown complete");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.err.println("Logger shutdown interrupted");
                }
            }
        }
        
        /**
         * Prevent cloning to maintain singleton property
         */
        @Override
        protected Object clone() throws CloneNotSupportedException {
            throw new CloneNotSupportedException("Singleton cannot be cloned");
        }
    }
    
    /**
     * Logging statistics
     */
    public static class LoggingStats {
        private final long totalLogs;
        private final long droppedLogs;
        private final int queueSize;
        private final Map<LogLevel, Long> levelCounts;
        private final int appenderCount;
        private final LogLevel currentLevel;
        
        public LoggingStats(long totalLogs, long droppedLogs, int queueSize,
                           Map<LogLevel, Long> levelCounts, int appenderCount, LogLevel currentLevel) {
            this.totalLogs = totalLogs;
            this.droppedLogs = droppedLogs;
            this.queueSize = queueSize;
            this.levelCounts = new HashMap<>(levelCounts);
            this.appenderCount = appenderCount;
            this.currentLevel = currentLevel;
        }
        
        @Override
        public String toString() {
            return String.format(
                "LoggingStats{total=%d, dropped=%d, queued=%d, appenders=%d, level=%s}",
                totalLogs, droppedLogs, queueSize, appenderCount, currentLevel
            );
        }
        
        // Getters
        public long getTotalLogs() { return totalLogs; }
        public long getDroppedLogs() { return droppedLogs; }
        public int getQueueSize() { return queueSize; }
        public Map<LogLevel, Long> getLevelCounts() { return new HashMap<>(levelCounts); }
        public int getAppenderCount() { return appenderCount; }
        public LogLevel getCurrentLevel() { return currentLevel; }
    }
    
    /**
     * Application components demonstrating logger usage
     */
    public static class ApplicationComponents {
        
        /**
         * UserService component
         */
        public static class UserService {
            private static final String LOGGER_NAME = "UserService";
            private final Logger logger = Logger.getInstance();
            
            public void createUser(String username, String email) {
                logger.info(LOGGER_NAME, "Creating user: " + username);
                
                try {
                    // Simulate user creation
                    if (username == null || username.trim().isEmpty()) {
                        throw new IllegalArgumentException("Username cannot be empty");
                    }
                    
                    // Simulate processing time
                    Thread.sleep(100);
                    
                    logger.info(LOGGER_NAME, "User created successfully: " + username);
                    
                } catch (IllegalArgumentException e) {
                    logger.error(LOGGER_NAME, "Failed to create user: " + e.getMessage(), e);
                    throw e;
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    logger.warn(LOGGER_NAME, "User creation interrupted for: " + username);
                }
            }
            
            public void deleteUser(String username) {
                logger.warn(LOGGER_NAME, "Deleting user: " + username);
                
                try {
                    // Simulate deletion
                    Thread.sleep(50);
                    logger.info(LOGGER_NAME, "User deleted: " + username);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    logger.error(LOGGER_NAME, "User deletion interrupted", e);
                }
            }
        }
        
        /**
         * PaymentService component
         */
        public static class PaymentService {
            private static final String LOGGER_NAME = "PaymentService";
            private final Logger logger = Logger.getInstance();
            
            public void processPayment(String orderId, double amount) {
                logger.info(LOGGER_NAME, String.format("Processing payment for order %s: $%.2f", orderId, amount));
                
                try {
                    // Simulate payment processing
                    if (amount <= 0) {
                        throw new IllegalArgumentException("Payment amount must be positive");
                    }
                    
                    if (amount > 10000) {
                        logger.warn(LOGGER_NAME, "Large payment detected: $" + amount);
                    }
                    
                    // Simulate processing time
                    Thread.sleep(200);
                    
                    logger.info(LOGGER_NAME, "Payment processed successfully for order: " + orderId);
                    
                } catch (IllegalArgumentException e) {
                    logger.error(LOGGER_NAME, "Payment processing failed: " + e.getMessage(), e);
                    throw e;
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    logger.fatal(LOGGER_NAME, "Payment processing critically interrupted", e);
                }
            }
        }
    }
    
    /**
     * Demonstration of the Logger Singleton
     */
    public static void main(String[] args) {
        System.out.println("=== Logger Singleton Demo ===\n");
        
        // Get singleton instance
        Logger logger = Logger.getInstance();
        
        // Verify singleton property
        Logger logger2 = Logger.getInstance();
        System.out.println("Singleton verification: " + (logger == logger2));
        System.out.println("Initial stats: " + logger.getStats() + "\n");
        
        try {
            // Add file appender
            logger.addAppender("file", new FileAppender("application.log"));
            System.out.println("File appender added\n");
            
            // Demonstrate different log levels
            System.out.println("--- Log Level Demonstration ---");
            logger.trace("Demo", "This is a trace message");
            logger.debug("Demo", "This is a debug message");
            logger.info("Demo", "This is an info message");
            logger.warn("Demo", "This is a warning message");
            logger.error("Demo", "This is an error message");
            logger.fatal("Demo", "This is a fatal message");
            System.out.println();
            
            // Change log level and test filtering
            System.out.println("--- Changing Log Level to WARN ---");
            logger.setLogLevel(LogLevel.WARN);
            logger.debug("Demo", "This debug message should not appear");
            logger.info("Demo", "This info message should not appear");
            logger.warn("Demo", "This warning message should appear");
            logger.error("Demo", "This error message should appear");
            System.out.println();
            
            // Reset log level
            logger.setLogLevel(LogLevel.INFO);
            
            // Demonstrate application components
            System.out.println("--- Application Components Demo ---");
            ApplicationComponents.UserService userService = new ApplicationComponents.UserService();
            ApplicationComponents.PaymentService paymentService = new ApplicationComponents.PaymentService();
            
            // Normal operations
            userService.createUser("john_doe", "john@example.com");
            paymentService.processPayment("ORDER-001", 99.99);
            
            // Error conditions
            try {
                userService.createUser("", "invalid@example.com");
            } catch (IllegalArgumentException e) {
                // Expected exception
            }
            
            try {
                paymentService.processPayment("ORDER-002", -50.0);
            } catch (IllegalArgumentException e) {
                // Expected exception
            }
            
            // Large payment warning
            paymentService.processPayment("ORDER-003", 15000.0);
            
            userService.deleteUser("john_doe");
            System.out.println();
            
            // Concurrent logging test
            System.out.println("--- Concurrent Logging Test ---");
            List<Thread> threads = new ArrayList<>();
            
            for (int i = 0; i < 5; i++) {
                final int threadId = i;
                Thread thread = new Thread(() -> {
                    for (int j = 0; j < 10; j++) {
                        logger.info("Thread-" + threadId, "Message " + j + " from thread " + threadId);
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }
                });
                threads.add(thread);
                thread.start();
            }
            
            // Wait for all threads to complete
            for (Thread thread : threads) {
                thread.join();
            }
            System.out.println();
            
            // Allow time for async processing
            Thread.sleep(1000);
            
            // Show final statistics
            System.out.println("--- Final Statistics ---");
            LoggingStats stats = logger.getStats();
            System.out.println(stats);
            System.out.println("Log level counts:");
            for (Map.Entry<LogLevel, Long> entry : stats.getLevelCounts().entrySet()) {
                System.out.println("  " + entry.getKey() + ": " + entry.getValue());
            }
            System.out.println();
            
        } catch (Exception e) {
            logger.error("Main", "Demo failed", e);
        } finally {
            // Shutdown logger
            logger.flush();
            logger.shutdown();
        }
        
        System.out.println("=== Demo Complete ===");
    }
}