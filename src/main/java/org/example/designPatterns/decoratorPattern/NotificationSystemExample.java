package org.example.designPatterns.decoratorPattern;

import java.util.*;
import java.util.concurrent.*;
import java.text.SimpleDateFormat;
import java.util.stream.Collectors;

/**
 * Notification System Decorator Pattern Example
 * 
 * This example demonstrates the Decorator Pattern using a notification system.
 * Base notifications (Email, SMS, Push) are core components, and various
 * enhancements (encryption, compression, retry logic, logging) are decorators
 * that can be combined flexibly.
 * 
 * Features:
 * - Multiple notification channels
 * - Various decorators for reliability and security
 * - Flexible composition of features
 * - Real-time statistics and monitoring
 * - Template system integration
 */

// Component interface
interface Notification {
    void send(String message);
    String getDeliveryMethod();
    boolean isDelivered();
    Date getTimestamp();
    String getRecipient();
    void setRecipient(String recipient);
    double getCost();
    int getPriority(); // 1 = low, 5 = high
    Map<String, Object> getMetadata();
}

// Base notification implementation
abstract class BaseNotification implements Notification {
    protected String recipient;
    protected Date timestamp;
    protected boolean delivered;
    protected Map<String, Object> metadata;
    protected int priority;
    
    public BaseNotification(String recipient) {
        this.recipient = recipient;
        this.timestamp = new Date();
        this.delivered = false;
        this.metadata = new HashMap<>();
        this.priority = 3; // Medium priority by default
    }
    
    @Override
    public String getRecipient() {
        return recipient;
    }
    
    @Override
    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }
    
    @Override
    public Date getTimestamp() {
        return timestamp;
    }
    
    @Override
    public boolean isDelivered() {
        return delivered;
    }
    
    @Override
    public int getPriority() {
        return priority;
    }
    
    public void setPriority(int priority) {
        this.priority = Math.max(1, Math.min(5, priority));
    }
    
    @Override
    public Map<String, Object> getMetadata() {
        return new HashMap<>(metadata);
    }
    
    public void addMetadata(String key, Object value) {
        metadata.put(key, value);
    }
    
    protected void markAsDelivered() {
        this.delivered = true;
        this.timestamp = new Date();
    }
}

// Concrete components
class EmailNotification extends BaseNotification {
    private String subject;
    private String emailAddress;
    private String htmlContent;
    private List<String> attachments;
    
    public EmailNotification(String emailAddress) {
        super(emailAddress);
        this.emailAddress = emailAddress;
        this.subject = "Notification";
        this.htmlContent = null;
        this.attachments = new ArrayList<>();
        addMetadata("email_address", emailAddress);
        addMetadata("content_type", "text");
    }
    
    public EmailNotification(String emailAddress, String subject) {
        this(emailAddress);
        this.subject = subject;
    }
    
    @Override
    public void send(String message) {
        System.out.println("📧 Sending email to: " + emailAddress);
        System.out.println("📋 Subject: " + subject);
        
        if (htmlContent != null) {
            System.out.println("🎨 HTML content detected");
            addMetadata("content_type", "html");
        }
        
        if (!attachments.isEmpty()) {
            System.out.println("📎 Attachments: " + attachments.size());
            addMetadata("attachments", attachments.size());
        }
        
        System.out.println("💬 Message: " + message);
        System.out.println("🕐 Timestamp: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(timestamp));
        
        // Simulate email sending delay
        try {
            Thread.sleep(100 + (int)(Math.random() * 200));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        markAsDelivered();
        System.out.println("✅ Email delivered successfully!");
    }
    
    @Override
    public String getDeliveryMethod() {
        return "Email";
    }
    
    @Override
    public double getCost() {
        double baseCost = 0.01;
        double attachmentCost = attachments.size() * 0.005;
        return baseCost + attachmentCost;
    }
    
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    
    public void setHtmlContent(String htmlContent) {
        this.htmlContent = htmlContent;
    }
    
    public void addAttachment(String attachment) {
        attachments.add(attachment);
    }
    
    public List<String> getAttachments() {
        return new ArrayList<>(attachments);
    }
}

class SMSNotification extends BaseNotification {
    private String phoneNumber;
    private boolean international;
    private boolean longMessage;
    
    public SMSNotification(String phoneNumber) {
        super(phoneNumber);
        this.phoneNumber = phoneNumber;
        this.international = phoneNumber.startsWith("+") && !phoneNumber.startsWith("+1");
        addMetadata("phone_number", phoneNumber);
        addMetadata("international", international);
    }
    
    @Override
    public void send(String message) {
        System.out.println("📱 Sending SMS to: " + formatPhoneNumber(phoneNumber));
        
        if (international) {
            System.out.println("🌍 International SMS");
        }
        
        longMessage = message.length() > 160;
        if (longMessage) {
            int segments = (int) Math.ceil(message.length() / 160.0);
            System.out.println("📄 Long message - " + segments + " segments");
            addMetadata("segments", segments);
        }
        
        System.out.println("💬 Message: " + message);
        System.out.println("📏 Length: " + message.length() + " characters");
        
        // Simulate SMS sending
        try {
            Thread.sleep(50 + (int)(Math.random() * 100));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        markAsDelivered();
        System.out.println("✅ SMS delivered successfully!");
    }
    
    @Override
    public String getDeliveryMethod() {
        return international ? "International SMS" : "SMS";
    }
    
    @Override
    public double getCost() {
        double baseCost = international ? 0.25 : 0.05;
        if (longMessage) {
            int segments = (Integer) metadata.getOrDefault("segments", 1);
            baseCost *= segments;
        }
        return baseCost;
    }
    
    private String formatPhoneNumber(String phone) {
        if (phone.length() >= 4) {
            return phone.substring(0, phone.length() - 4) + "****";
        }
        return phone;
    }
    
    public boolean isInternational() { return international; }
    public boolean isLongMessage() { return longMessage; }
}

class PushNotification extends BaseNotification {
    private String deviceId;
    private String appId;
    private String platform;
    private Map<String, Object> payload;
    
    public PushNotification(String deviceId, String appId, String platform) {
        super(deviceId);
        this.deviceId = deviceId;
        this.appId = appId;
        this.platform = platform;
        this.payload = new HashMap<>();
        addMetadata("device_id", deviceId);
        addMetadata("app_id", appId);
        addMetadata("platform", platform);
    }
    
    @Override
    public void send(String message) {
        System.out.println("🔔 Sending push notification");
        System.out.println("📱 Platform: " + platform);
        System.out.println("📦 App ID: " + appId);
        System.out.println("🔧 Device: " + deviceId.substring(0, Math.min(8, deviceId.length())) + "...");
        System.out.println("💬 Message: " + message);
        
        if (!payload.isEmpty()) {
            System.out.println("📊 Custom payload: " + payload.size() + " items");
            addMetadata("payload_size", payload.size());
        }
        
        // Simulate push notification
        try {
            Thread.sleep(25 + (int)(Math.random() * 75));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        markAsDelivered();
        System.out.println("✅ Push notification delivered!");
    }
    
    @Override
    public String getDeliveryMethod() {
        return platform + " Push Notification";
    }
    
    @Override
    public double getCost() {
        return 0.001; // Very cheap for push notifications
    }
    
    public String getPlatform() { return platform; }
    public String getAppId() { return appId; }
    
    public void addPayload(String key, Object value) {
        payload.put(key, value);
    }
    
    public Map<String, Object> getPayload() {
        return new HashMap<>(payload);
    }
}

class SlackNotification extends BaseNotification {
    private String channelId;
    private String botToken;
    private boolean threadReply;
    private String parentMessageId;
    
    public SlackNotification(String channelId, String botToken) {
        super(channelId);
        this.channelId = channelId;
        this.botToken = botToken;
        this.threadReply = false;
        addMetadata("channel_id", channelId);
    }
    
    @Override
    public void send(String message) {
        System.out.println("💬 Sending Slack message");
        System.out.println("📢 Channel: " + channelId);
        
        if (threadReply && parentMessageId != null) {
            System.out.println("🧵 Thread reply to: " + parentMessageId.substring(0, 8) + "...");
            addMetadata("thread_reply", true);
        }
        
        System.out.println("💬 Message: " + message);
        
        // Simulate Slack API call
        try {
            Thread.sleep(150 + (int)(Math.random() * 100));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        markAsDelivered();
        System.out.println("✅ Slack message delivered!");
    }
    
    @Override
    public String getDeliveryMethod() {
        return "Slack";
    }
    
    @Override
    public double getCost() {
        return 0.0; // Free for most Slack usage
    }
    
    public void setAsThreadReply(String parentMessageId) {
        this.threadReply = true;
        this.parentMessageId = parentMessageId;
    }
}

// Abstract decorator
abstract class NotificationDecorator implements Notification {
    protected Notification notification;
    
    public NotificationDecorator(Notification notification) {
        this.notification = notification;
    }
    
    @Override
    public void send(String message) {
        notification.send(message);
    }
    
    @Override
    public String getDeliveryMethod() {
        return notification.getDeliveryMethod();
    }
    
    @Override
    public boolean isDelivered() {
        return notification.isDelivered();
    }
    
    @Override
    public Date getTimestamp() {
        return notification.getTimestamp();
    }
    
    @Override
    public String getRecipient() {
        return notification.getRecipient();
    }
    
    @Override
    public void setRecipient(String recipient) {
        notification.setRecipient(recipient);
    }
    
    @Override
    public double getCost() {
        return notification.getCost();
    }
    
    @Override
    public int getPriority() {
        return notification.getPriority();
    }
    
    @Override
    public Map<String, Object> getMetadata() {
        return notification.getMetadata();
    }
}

// Concrete decorators
class EncryptionDecorator extends NotificationDecorator {
    private EncryptionType encryptionType;
    private String encryptionKey;
    
    public enum EncryptionType {
        AES256("AES-256", 0.02, "🔐", "Advanced Encryption Standard"),
        RSA("RSA-2048", 0.05, "🔒", "RSA Public Key Encryption"),
        PGP("PGP", 0.03, "🛡️", "Pretty Good Privacy");
        
        private final String name;
        private final double additionalCost;
        private final String icon;
        private final String description;
        
        EncryptionType(String name, double cost, String icon, String description) {
            this.name = name;
            this.additionalCost = cost;
            this.icon = icon;
            this.description = description;
        }
        
        public String getName() { return name; }
        public double getAdditionalCost() { return additionalCost; }
        public String getIcon() { return icon; }
        public String getDescription() { return description; }
    }
    
    public EncryptionDecorator(Notification notification, EncryptionType encryptionType) {
        super(notification);
        this.encryptionType = encryptionType;
        this.encryptionKey = generateEncryptionKey();
    }
    
    public EncryptionDecorator(Notification notification) {
        this(notification, EncryptionType.AES256);
    }
    
    @Override
    public void send(String message) {
        System.out.println(encryptionType.getIcon() + " Encrypting message with " + encryptionType.getName());
        System.out.println("🔑 Using key: " + encryptionKey.substring(0, 8) + "...");
        
        String encryptedMessage = encrypt(message);
        int originalLength = message.length();
        int encryptedLength = encryptedMessage.length();
        
        System.out.println("📏 Original length: " + originalLength + " chars");
        System.out.println("📏 Encrypted length: " + encryptedLength + " chars");
        System.out.println("🔒 Encryption overhead: " + (encryptedLength - originalLength) + " chars");
        
        Map<String, Object> metadata = getMetadata();
        metadata.put("encrypted", true);
        metadata.put("encryption_type", encryptionType.getName());
        metadata.put("encryption_overhead", encryptedLength - originalLength);
        
        super.send(encryptedMessage);
    }
    
    @Override
    public String getDeliveryMethod() {
        return notification.getDeliveryMethod() + " with " + encryptionType.getName() + " Encryption";
    }
    
    @Override
    public double getCost() {
        return notification.getCost() + encryptionType.getAdditionalCost();
    }
    
    private String encrypt(String message) {
        // Simulate encryption (in reality, would use proper encryption libraries)
        StringBuilder encrypted = new StringBuilder();
        for (char c : message.toCharArray()) {
            encrypted.append((char) (c + 1)); // Simple Caesar cipher for demo
        }
        return "ENC:" + encryptionType.name() + ":" + 
               Base64.getEncoder().encodeToString(encrypted.toString().getBytes());
    }
    
    private String generateEncryptionKey() {
        return "KEY-" + encryptionType.name() + "-" + System.currentTimeMillis() + 
               "-" + (int)(Math.random() * 10000);
    }
    
    public EncryptionType getEncryptionType() { return encryptionType; }
    public String getEncryptionKey() { return encryptionKey; }
}

class CompressionDecorator extends NotificationDecorator {
    private CompressionType compressionType;
    private int threshold;
    
    public enum CompressionType {
        GZIP("GZIP", 0.005, 0.7, "GNU Zip compression"),
        LZ4("LZ4", 0.003, 0.8, "Fast compression algorithm"),
        BROTLI("Brotli", 0.008, 0.6, "Google's compression algorithm"),
        ZSTD("Zstandard", 0.006, 0.65, "Facebook's compression");
        
        private final String name;
        private final double additionalCost;
        private final double compressionRatio;
        private final String description;
        
        CompressionType(String name, double cost, double ratio, String description) {
            this.name = name;
            this.additionalCost = cost;
            this.compressionRatio = ratio;
            this.description = description;
        }
        
        public String getName() { return name; }
        public double getAdditionalCost() { return additionalCost; }
        public double getCompressionRatio() { return compressionRatio; }
        public String getDescription() { return description; }
    }
    
    public CompressionDecorator(Notification notification, CompressionType compressionType, int threshold) {
        super(notification);
        this.compressionType = compressionType;
        this.threshold = threshold;
    }
    
    public CompressionDecorator(Notification notification) {
        this(notification, CompressionType.GZIP, 100);
    }
    
    @Override
    public void send(String message) {
        if (message.length() >= threshold) {
            System.out.println("🗜️ Compressing message with " + compressionType.getName());
            System.out.println("📝 Algorithm: " + compressionType.getDescription());
            
            String compressedMessage = compress(message);
            int originalSize = message.length();
            int compressedSize = compressedMessage.length();
            double savings = ((double)(originalSize - compressedSize) / originalSize) * 100;
            
            System.out.println("📏 Original size: " + originalSize + " chars");
            System.out.println("📏 Compressed size: " + compressedSize + " chars");
            System.out.println("💰 Compression savings: " + String.format("%.1f%%", savings));
            
            Map<String, Object> metadata = getMetadata();
            metadata.put("compressed", true);
            metadata.put("compression_type", compressionType.getName());
            metadata.put("compression_ratio", savings);
            metadata.put("original_size", originalSize);
            metadata.put("compressed_size", compressedSize);
            
            super.send(compressedMessage);
        } else {
            System.out.println("📝 Message too short for compression (< " + threshold + " chars)");
            
            Map<String, Object> metadata = getMetadata();
            metadata.put("compressed", false);
            metadata.put("compression_skipped_reason", "Below threshold");
            
            super.send(message);
        }
    }
    
    @Override
    public String getDeliveryMethod() {
        return notification.getDeliveryMethod() + " with " + compressionType.getName() + " Compression";
    }
    
    @Override
    public double getCost() {
        return notification.getCost() + compressionType.getAdditionalCost();
    }
    
    private String compress(String message) {
        // Simulate compression
        int compressedLength = (int)(message.length() * compressionType.getCompressionRatio());
        return "COMP:" + compressionType.name() + ":" + 
               message.substring(0, Math.min(compressedLength, message.length())) + "...";
    }
    
    public CompressionType getCompressionType() { return compressionType; }
    public int getThreshold() { return threshold; }
}

class RetryDecorator extends NotificationDecorator {
    private int maxRetries;
    private int retryDelay;
    private int currentAttempt;
    private double failureRate;
    
    public RetryDecorator(Notification notification, int maxRetries, int retryDelay) {
        super(notification);
        this.maxRetries = Math.max(1, maxRetries);
        this.retryDelay = Math.max(100, retryDelay);
        this.currentAttempt = 0;
        this.failureRate = 0.15; // 15% simulated failure rate
    }
    
    public RetryDecorator(Notification notification) {
        this(notification, 3, 1000);
    }
    
    @Override
    public void send(String message) {
        currentAttempt = 0;
        boolean success = false;
        Exception lastException = null;
        
        while (!success && currentAttempt < maxRetries) {
            currentAttempt++;
            
            if (currentAttempt > 1) {
                int backoffDelay = retryDelay * (int)Math.pow(2, currentAttempt - 1); // Exponential backoff
                System.out.println("🔄 Retry attempt " + currentAttempt + "/" + maxRetries + 
                                 " (delay: " + backoffDelay + "ms)");
                
                try {
                    Thread.sleep(backoffDelay);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
            
            try {
                // Simulate potential failure
                if (Math.random() < failureRate && currentAttempt < maxRetries) {
                    throw new RuntimeException("Simulated delivery failure (network timeout)");
                }
                
                super.send(message);
                success = true;
                
                if (currentAttempt > 1) {
                    System.out.println("✅ Delivery successful after " + currentAttempt + " attempts");
                }
                
                Map<String, Object> metadata = getMetadata();
                metadata.put("retry_attempts", currentAttempt);
                metadata.put("retry_successful", true);
                
            } catch (Exception e) {
                lastException = e;
                System.out.println("❌ Attempt " + currentAttempt + " failed: " + e.getMessage());
                
                if (currentAttempt >= maxRetries) {
                    System.out.println("🚫 Max retries exceeded. Delivery failed permanently.");
                    
                    Map<String, Object> metadata = getMetadata();
                    metadata.put("retry_attempts", currentAttempt);
                    metadata.put("retry_successful", false);
                    metadata.put("final_error", e.getMessage());
                    
                    throw new RuntimeException("Failed to deliver after " + maxRetries + " attempts", e);
                }
            }
        }
    }
    
    @Override
    public String getDeliveryMethod() {
        return notification.getDeliveryMethod() + " with Retry (max " + maxRetries + ")";
    }
    
    @Override
    public double getCost() {
        // Cost increases with potential retries
        return notification.getCost() * (1 + (maxRetries - 1) * 0.1);
    }
    
    public int getMaxRetries() { return maxRetries; }
    public int getCurrentAttempt() { return currentAttempt; }
    
    public void setFailureRate(double failureRate) {
        this.failureRate = Math.max(0.0, Math.min(1.0, failureRate));
    }
}

class LoggingDecorator extends NotificationDecorator {
    private LogLevel logLevel;
    private String loggerName;
    private boolean includeMetadata;
    
    public enum LogLevel {
        DEBUG("DEBUG", "🐛"),
        INFO("INFO", "ℹ️"),
        WARN("WARN", "⚠️"),
        ERROR("ERROR", "❌");
        
        private final String name;
        private final String icon;
        
        LogLevel(String name, String icon) {
            this.name = name;
            this.icon = icon;
        }
        
        public String getName() { return name; }
        public String getIcon() { return icon; }
    }
    
    public LoggingDecorator(Notification notification, LogLevel logLevel, String loggerName) {
        super(notification);
        this.logLevel = logLevel;
        this.loggerName = loggerName;
        this.includeMetadata = true;
    }
    
    public LoggingDecorator(Notification notification) {
        this(notification, LogLevel.INFO, "NotificationService");
    }
    
    @Override
    public void send(String message) {
        logBefore(message);
        
        long startTime = System.currentTimeMillis();
        
        try {
            super.send(message);
            long duration = System.currentTimeMillis() - startTime;
            logSuccess(message, duration);
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logError(message, e, duration);
            throw e;
        }
    }
    
    private void logBefore(String message) {
        log(LogLevel.DEBUG, "Attempting to send notification");
        log(LogLevel.DEBUG, "Recipient: " + getRecipient());
        log(LogLevel.DEBUG, "Delivery method: " + notification.getDeliveryMethod());
        log(LogLevel.DEBUG, "Message length: " + message.length() + " characters");
        log(LogLevel.DEBUG, "Priority: " + getPriority());
        log(LogLevel.DEBUG, "Estimated cost: $" + String.format("%.4f", getCost()));
        
        if (includeMetadata && !getMetadata().isEmpty()) {
            log(LogLevel.DEBUG, "Metadata: " + getMetadata());
        }
    }
    
    private void logSuccess(String message, long duration) {
        log(LogLevel.INFO, "✅ Notification delivered successfully");
        log(LogLevel.INFO, "Delivery method: " + getDeliveryMethod());
        log(LogLevel.INFO, "Final cost: $" + String.format("%.4f", getCost()));
        log(LogLevel.INFO, "Processing time: " + duration + "ms");
        log(LogLevel.INFO, "Timestamp: " + getTimestamp());
    }
    
    private void logError(String message, Exception e, long duration) {
        log(LogLevel.ERROR, "❌ Failed to deliver notification: " + e.getMessage());
        log(LogLevel.ERROR, "Recipient: " + getRecipient());
        log(LogLevel.ERROR, "Delivery method: " + getDeliveryMethod());
        log(LogLevel.ERROR, "Processing time: " + duration + "ms");
        log(LogLevel.WARN, "Stack trace: " + e.getClass().getSimpleName());
    }
    
    private void log(LogLevel level, String message) {
        if (level.ordinal() >= this.logLevel.ordinal()) {
            String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());
            System.out.println(String.format("[%s] %s %s - %s: %s", 
                timestamp, level.getIcon(), level.getName(), loggerName, message));
        }
    }
    
    @Override
    public String getDeliveryMethod() {
        return notification.getDeliveryMethod() + " with Logging";
    }
    
    @Override
    public double getCost() {
        return notification.getCost() + 0.001; // Small cost for logging
    }
    
    public void setIncludeMetadata(boolean includeMetadata) {
        this.includeMetadata = includeMetadata;
    }
    
    public LogLevel getLogLevel() { return logLevel; }
    public void setLogLevel(LogLevel logLevel) { this.logLevel = logLevel; }
}

class RateLimitingDecorator extends NotificationDecorator {
    private static final Map<String, Queue<Long>> recipientTimestamps = new ConcurrentHashMap<>();
    private int maxRequestsPerMinute;
    private long timeWindowMs;
    
    public RateLimitingDecorator(Notification notification, int maxRequestsPerMinute) {
        super(notification);
        this.maxRequestsPerMinute = maxRequestsPerMinute;
        this.timeWindowMs = 60 * 1000; // 1 minute
    }
    
    public RateLimitingDecorator(Notification notification) {
        this(notification, 10); // Default: 10 notifications per minute
    }
    
    @Override
    public void send(String message) {
        String recipient = getRecipient();
        
        if (isRateLimited(recipient)) {
            String errorMsg = "Rate limit exceeded for recipient: " + recipient + 
                            ". Max " + maxRequestsPerMinute + " notifications per minute.";
            System.out.println("⏱️ " + errorMsg);
            
            Map<String, Object> metadata = getMetadata();
            metadata.put("rate_limited", true);
            metadata.put("rate_limit_reason", "Exceeded " + maxRequestsPerMinute + " per minute");
            
            throw new RuntimeException(errorMsg);
        }
        
        recordRequest(recipient);
        System.out.println("⏱️ Rate limit check passed for: " + recipient + 
                         " (" + getCurrentRequestCount(recipient) + "/" + maxRequestsPerMinute + ")");
        
        Map<String, Object> metadata = getMetadata();
        metadata.put("rate_limited", false);
        metadata.put("current_rate", getCurrentRequestCount(recipient) + "/" + maxRequestsPerMinute);
        
        super.send(message);
    }
    
    private boolean isRateLimited(String recipient) {
        Queue<Long> timestamps = recipientTimestamps.computeIfAbsent(recipient, k -> new LinkedList<>());
        long currentTime = System.currentTimeMillis();
        
        // Clean old timestamps
        while (!timestamps.isEmpty() && (currentTime - timestamps.peek()) > timeWindowMs) {
            timestamps.poll();
        }
        
        return timestamps.size() >= maxRequestsPerMinute;
    }
    
    private void recordRequest(String recipient) {
        Queue<Long> timestamps = recipientTimestamps.computeIfAbsent(recipient, k -> new LinkedList<>());
        timestamps.offer(System.currentTimeMillis());
    }
    
    private int getCurrentRequestCount(String recipient) {
        Queue<Long> timestamps = recipientTimestamps.get(recipient);
        return timestamps != null ? timestamps.size() : 0;
    }
    
    @Override
    public String getDeliveryMethod() {
        return notification.getDeliveryMethod() + " with Rate Limiting (" + maxRequestsPerMinute + "/min)";
    }
    
    @Override
    public double getCost() {
        return notification.getCost() + 0.002; // Small cost for rate limiting
    }
    
    public int getMaxRequestsPerMinute() { return maxRequestsPerMinute; }
    
    public static void clearRateLimitHistory() {
        recipientTimestamps.clear();
        System.out.println("🧹 Rate limit history cleared");
    }
}

// Notification service
class NotificationService {
    private String serviceName;
    private Map<String, NotificationTemplate> templates;
    private List<Notification> sentNotifications;
    
    public static class NotificationTemplate {
        private String name;
        private String template;
        private Map<String, String> defaultValues;
        
        public NotificationTemplate(String name, String template) {
            this.name = name;
            this.template = template;
            this.defaultValues = new HashMap<>();
        }
        
        public String getName() { return name; }
        public String getTemplate() { return template; }
        
        public void setDefaultValue(String key, String value) {
            defaultValues.put(key, value);
        }
        
        public String render(Map<String, String> values) {
            String result = template;
            
            // Apply default values first
            for (Map.Entry<String, String> entry : defaultValues.entrySet()) {
                result = result.replace("{{" + entry.getKey() + "}}", entry.getValue());
            }
            
            // Apply provided values
            for (Map.Entry<String, String> entry : values.entrySet()) {
                result = result.replace("{{" + entry.getKey() + "}}", entry.getValue());
            }
            
            return result;
        }
    }
    
    public NotificationService(String serviceName) {
        this.serviceName = serviceName;
        this.templates = new HashMap<>();
        this.sentNotifications = new ArrayList<>();
        initializeDefaultTemplates();
    }
    
    private void initializeDefaultTemplates() {
        NotificationTemplate welcome = new NotificationTemplate("welcome",
            "Welcome {{name}}! Thank you for joining {{service}}. Your account is now active.");
        welcome.setDefaultValue("service", serviceName);
        templates.put("welcome", welcome);
        
        NotificationTemplate alert = new NotificationTemplate("alert",
            "🚨 ALERT: {{alertType}} detected at {{timestamp}}. Message: {{message}}");
        templates.put("alert", alert);
        
        NotificationTemplate reminder = new NotificationTemplate("reminder",
            "⏰ Reminder: {{event}} is scheduled for {{date}} at {{time}}. Location: {{location}}");
        templates.put("reminder", reminder);
    }
    
    public void sendNotification(Notification notification, String message) {
        try {
            System.out.println("\n" + "=".repeat(70));
            System.out.println("📬 " + serviceName + " - Sending Notification");
            System.out.println("=".repeat(70));
            
            long startTime = System.currentTimeMillis();
            notification.send(message);
            long duration = System.currentTimeMillis() - startTime;
            
            sentNotifications.add(notification);
            
            System.out.println("📊 Total processing time: " + duration + "ms");
            System.out.println("=".repeat(70));
            
        } catch (Exception e) {
            System.out.println("🚫 Notification delivery failed: " + e.getMessage());
            System.out.println("=".repeat(70));
            throw e;
        }
    }
    
    public void sendTemplateNotification(Notification notification, String templateName, 
                                       Map<String, String> values) {
        NotificationTemplate template = templates.get(templateName);
        if (template == null) {
            throw new IllegalArgumentException("Template not found: " + templateName);
        }
        
        String message = template.render(values);
        sendNotification(notification, message);
    }
    
    public void addTemplate(NotificationTemplate template) {
        templates.put(template.getName(), template);
        System.out.println("📝 Added template: " + template.getName());
    }
    
    public Set<String> getAvailableTemplates() {
        return new HashSet<>(templates.keySet());
    }
    
    public void showStatistics() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("📊 " + serviceName + " - Notification Statistics");
        System.out.println("=".repeat(60));
        
        int totalNotifications = sentNotifications.size();
        long deliveredCount = sentNotifications.stream()
                .mapToLong(n -> n.isDelivered() ? 1 : 0)
                .sum();
        
        double totalCost = sentNotifications.stream()
                .mapToDouble(Notification::getCost)
                .sum();
        
        double averageCost = totalNotifications > 0 ? totalCost / totalNotifications : 0;
        
        Map<String, Long> methodCounts = sentNotifications.stream()
                .collect(Collectors.groupingBy(
                    n -> n.getDeliveryMethod().split(" ")[0], // Get base method
                    Collectors.counting()
                ));
        
        Map<Integer, Long> priorityCounts = sentNotifications.stream()
                .collect(Collectors.groupingBy(
                    Notification::getPriority,
                    Collectors.counting()
                ));
        
        System.out.println("📈 Total notifications: " + totalNotifications);
        System.out.println("✅ Delivered: " + deliveredCount + " (" + 
                         String.format("%.1f%%", totalNotifications > 0 ? (double)deliveredCount / totalNotifications * 100 : 0) + ")");
        System.out.println("💰 Total cost: $" + String.format("%.4f", totalCost));
        System.out.println("💵 Average cost: $" + String.format("%.4f", averageCost));
        
        System.out.println("\n📊 Delivery methods:");
        for (Map.Entry<String, Long> entry : methodCounts.entrySet()) {
            System.out.println("  " + entry.getKey() + ": " + entry.getValue());
        }
        
        System.out.println("\n🎯 Priority distribution:");
        for (Map.Entry<Integer, Long> entry : priorityCounts.entrySet()) {
            String priorityName = getPriorityName(entry.getKey());
            System.out.println("  " + priorityName + " (" + entry.getKey() + "): " + entry.getValue());
        }
        
        System.out.println("=".repeat(60));
    }
    
    private String getPriorityName(int priority) {
        switch (priority) {
            case 1: return "Very Low";
            case 2: return "Low";
            case 3: return "Medium";
            case 4: return "High";
            case 5: return "Critical";
            default: return "Unknown";
        }
    }
    
    public List<Notification> getSentNotifications() {
        return new ArrayList<>(sentNotifications);
    }
    
    public void clearHistory() {
        sentNotifications.clear();
        System.out.println("🧹 Notification history cleared");
    }
}

// Demo class
public class NotificationSystemExample {
    public static void main(String[] args) {
        System.out.println("=== 📬 Decorator Pattern: Notification System Example ===\n");
        
        NotificationService service = new NotificationService("AlertFlow Notifications");
        
        // Example 1: Simple email notification
        System.out.println("--- 📧 Example 1: Basic Email ---");
        Notification email1 = new EmailNotification("user@example.com", "Welcome!");
        service.sendNotification(email1, "Welcome to our service! We're excited to have you aboard.");
        
        // Example 2: Enhanced email with encryption
        System.out.println("\n--- 🔐 Example 2: Encrypted Email ---");
        Notification email2 = new EmailNotification("secure@company.com", "Confidential Data");
        email2 = new EncryptionDecorator(email2, EncryptionDecorator.EncryptionType.AES256);
        email2 = new LoggingDecorator(email2);
        
        service.sendNotification(email2, "This is sensitive company information that needs to be encrypted.");
        
        // Example 3: SMS with retry logic
        System.out.println("\n--- 🔄 Example 3: SMS with Retry ---");
        Notification sms1 = new SMSNotification("+1-555-0123");
        sms1 = new RetryDecorator(sms1, 4, 500);
        sms1 = new LoggingDecorator(sms1, LoggingDecorator.LogLevel.INFO, "SMS-Service");
        
        try {
            service.sendNotification(sms1, "Your verification code is: 123456");
        } catch (Exception e) {
            System.out.println("SMS delivery ultimately failed: " + e.getMessage());
        }
        
        // Example 4: International SMS with compression
        System.out.println("\n--- 🌍 Example 4: International SMS with Compression ---");
        Notification sms2 = new SMSNotification("+44-20-7946-0958");
        sms2 = new CompressionDecorator(sms2, CompressionDecorator.CompressionType.LZ4, 50);
        sms2 = new LoggingDecorator(sms2);
        
        String longMessage = "This is a very long message that will benefit from compression. " +
                           "It contains a lot of text that can be compressed to save on SMS costs. " +
                           "International SMS can be expensive, so compression helps reduce the cost.";
        
        service.sendNotification(sms2, longMessage);
        
        // Example 5: Push notification with rate limiting
        System.out.println("\n--- ⏱️ Example 5: Push Notification with Rate Limiting ---");
        Notification push1 = new PushNotification("device123456", "com.app.mobile", "iOS");
        push1 = new RateLimitingDecorator(push1, 2); // Only 2 per minute
        push1 = new LoggingDecorator(push1);
        
        ((PushNotification) ((LoggingDecorator) ((RateLimitingDecorator) push1).notification).notification)
            .addPayload("action", "open_screen");
        ((PushNotification) ((LoggingDecorator) ((RateLimitingDecorator) push1).notification).notification)
            .addPayload("screen", "dashboard");
        
        service.sendNotification(push1, "You have a new message!");
        
        // Try to send another immediately (should be rate limited)
        try {
            service.sendNotification(push1, "Another notification!");
        } catch (Exception e) {
            System.out.println("Expected rate limit exception: " + e.getMessage());
        }
        
        // Example 6: Slack notification with multiple decorators
        System.out.println("\n--- 💬 Example 6: Enhanced Slack Notification ---");
        Notification slack1 = new SlackNotification("#alerts", "bot-token-123");
        slack1 = new CompressionDecorator(slack1, CompressionDecorator.CompressionType.GZIP, 80);
        slack1 = new EncryptionDecorator(slack1, EncryptionDecorator.EncryptionType.PGP);
        slack1 = new RetryDecorator(slack1, 3, 1000);
        slack1 = new LoggingDecorator(slack1, LoggingDecorator.LogLevel.DEBUG, "Slack-Bot");
        
        service.sendNotification(slack1, "🚨 Production Alert: Database connection timeout detected on server-01. " +
                                        "Response time increased by 300%. Investigation started.");
        
        // Example 7: Template-based notifications
        System.out.println("\n--- 📝 Example 7: Template-Based Notifications ---");
        
        Map<String, String> welcomeData = new HashMap<>();
        welcomeData.put("name", "John Doe");
        
        Notification welcomeEmail = new EmailNotification("john.doe@email.com", "Welcome to AlertFlow!");
        welcomeEmail = new LoggingDecorator(welcomeEmail);
        
        service.sendTemplateNotification(welcomeEmail, "welcome", welcomeData);
        
        // Example 8: High-priority alert with all decorators
        System.out.println("\n--- 🚨 Example 8: Critical Alert with Full Enhancement ---");
        
        Map<String, String> alertData = new HashMap<>();
        alertData.put("alertType", "SECURITY BREACH");
        alertData.put("timestamp", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        alertData.put("message", "Unauthorized access attempt detected from IP 192.168.1.100");
        
        Notification criticalAlert = new EmailNotification("security@company.com", "CRITICAL SECURITY ALERT");
        ((BaseNotification) criticalAlert).setPriority(5); // Critical priority
        
        criticalAlert = new EncryptionDecorator(criticalAlert, EncryptionDecorator.EncryptionType.RSA);
        criticalAlert = new CompressionDecorator(criticalAlert, CompressionDecorator.CompressionType.BROTLI, 50);
        criticalAlert = new RetryDecorator(criticalAlert, 5, 2000);
        criticalAlert = new LoggingDecorator(criticalAlert, LoggingDecorator.LogLevel.DEBUG, "SecurityAlert");
        
        service.sendTemplateNotification(criticalAlert, "alert", alertData);
        
        // Show comprehensive statistics
        service.showStatistics();
        
        // Demonstrate decorator flexibility
        System.out.println("\n🔧 Demonstrating Decorator Pattern Flexibility:");
        System.out.println("=".repeat(70));
        
        // Base notification
        Notification flexDemo = new EmailNotification("demo@example.com");
        System.out.println("Base: " + flexDemo.getDeliveryMethod() + " - $" + 
                         String.format("%.4f", flexDemo.getCost()));
        
        // Add decorators incrementally
        flexDemo = new LoggingDecorator(flexDemo);
        System.out.println("+ Logging: " + flexDemo.getDeliveryMethod() + " - $" + 
                         String.format("%.4f", flexDemo.getCost()));
        
        flexDemo = new EncryptionDecorator(flexDemo);
        System.out.println("+ Encryption: " + flexDemo.getDeliveryMethod() + " - $" + 
                         String.format("%.4f", flexDemo.getCost()));
        
        flexDemo = new CompressionDecorator(flexDemo);
        System.out.println("+ Compression: " + flexDemo.getDeliveryMethod() + " - $" + 
                         String.format("%.4f", flexDemo.getCost()));
        
        flexDemo = new RetryDecorator(flexDemo);
        System.out.println("+ Retry: " + flexDemo.getDeliveryMethod() + " - $" + 
                         String.format("%.4f", flexDemo.getCost()));
        
        flexDemo = new RateLimitingDecorator(flexDemo);
        System.out.println("+ Rate Limiting: " + flexDemo.getDeliveryMethod() + " - $" + 
                         String.format("%.4f", flexDemo.getCost()));
        
        // Clean up
        RateLimitingDecorator.clearRateLimitHistory();
        
        System.out.println("\n=== ✅ Demo Complete ===");
        
        // Key benefits demonstrated
        System.out.println("\n🎯 Key Benefits Demonstrated:");
        System.out.println("✅ Dynamic composition - decorators added at runtime");
        System.out.println("✅ Flexible feature combinations");
        System.out.println("✅ Each decorator adds specific functionality");
        System.out.println("✅ Transparent interface - all work the same way");
        System.out.println("✅ Easy to add new decorators without changing existing code");
        System.out.println("✅ Cost and metadata tracking through the chain");
        System.out.println("✅ Error handling and retry logic");
        System.out.println("✅ Security and reliability enhancements");
    }
}