package org.example.solidPrinciples.openClosed;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * Notification System - Open/Closed Principle Example
 * 
 * This example demonstrates how to build a flexible notification system that can:
 * - Support multiple notification channels (email, SMS, push, etc.)
 * - Add new channels without modifying existing code
 * - Apply different formatting and filtering strategies
 * - Handle priority and scheduling independently
 * 
 * Key Design Patterns Used:
 * - Strategy Pattern for notification channels
 * - Chain of Responsibility for filtering
 * - Decorator Pattern for message enhancement
 * - Factory Pattern for channel creation
 */
public class NotificationSystemExample {
    
    /**
     * BEFORE: Monolithic notification system violating OCP
     * Every new feature requires modifying this class
     */
    public static class ViolatingNotificationService {
        
        public void sendNotification(String type, String recipient, String message, 
                                    Map<String, String> config) {
            // Every new notification type requires modifying this method
            if (type.equals("EMAIL")) {
                sendEmail(recipient, message, config.get("subject"));
                
            } else if (type.equals("SMS")) {
                sendSMS(recipient, message, config.get("provider"));
                
            } else if (type.equals("PUSH")) {
                sendPushNotification(recipient, message, config.get("app"));
                
            } else if (type.equals("SLACK")) {
                // Added later - requires modification
                sendSlackMessage(recipient, message, config.get("channel"));
                
            } else if (type.equals("TEAMS")) {
                // Added even later - another modification
                sendTeamsMessage(recipient, message, config.get("webhook"));
                
            } else {
                System.out.println("Unknown notification type: " + type);
            }
        }
        
        private void sendEmail(String email, String message, String subject) {
            System.out.println("Sending email to: " + email);
            System.out.println("Subject: " + subject);
            System.out.println("Message: " + message);
            // Email sending logic
        }
        
        private void sendSMS(String phone, String message, String provider) {
            System.out.println("Sending SMS via " + provider + " to: " + phone);
            System.out.println("Message: " + message);
            // SMS sending logic
        }
        
        private void sendPushNotification(String deviceId, String message, String app) {
            System.out.println("Sending push notification to device: " + deviceId);
            System.out.println("App: " + app);
            System.out.println("Message: " + message);
            // Push notification logic
        }
        
        private void sendSlackMessage(String userId, String message, String channel) {
            System.out.println("Sending Slack message to: " + userId);
            System.out.println("Channel: " + channel);
            System.out.println("Message: " + message);
            // Slack integration logic
        }
        
        private void sendTeamsMessage(String userId, String message, String webhook) {
            System.out.println("Sending Teams message via webhook: " + webhook);
            System.out.println("To: " + userId);
            System.out.println("Message: " + message);
            // Teams integration logic
        }
        
        // Format message - also violates OCP
        public String formatMessage(String template, Map<String, String> variables, String type) {
            String formatted = template;
            
            // Different formatting for different notification types
            if (type.equals("EMAIL")) {
                // HTML formatting
                formatted = "<html><body>" + formatted + "</body></html>";
                for (Map.Entry<String, String> var : variables.entrySet()) {
                    formatted = formatted.replace("{{" + var.getKey() + "}}", 
                                                 "<b>" + var.getValue() + "</b>");
                }
                
            } else if (type.equals("SMS")) {
                // Plain text, shortened
                for (Map.Entry<String, String> var : variables.entrySet()) {
                    formatted = formatted.replace("{{" + var.getKey() + "}}", var.getValue());
                }
                if (formatted.length() > 160) {
                    formatted = formatted.substring(0, 157) + "...";
                }
                
            } else if (type.equals("PUSH")) {
                // Limited length for push
                for (Map.Entry<String, String> var : variables.entrySet()) {
                    formatted = formatted.replace("{{" + var.getKey() + "}}", var.getValue());
                }
                if (formatted.length() > 100) {
                    formatted = formatted.substring(0, 97) + "...";
                }
                
            } else if (type.equals("SLACK")) {
                // Markdown formatting
                formatted = "*" + formatted + "*";
                for (Map.Entry<String, String> var : variables.entrySet()) {
                    formatted = formatted.replace("{{" + var.getKey() + "}}", 
                                                 "_" + var.getValue() + "_");
                }
            }
            
            return formatted;
        }
    }
    
    /**
     * AFTER: Flexible notification system following OCP
     */
    
    // Core abstractions
    public interface NotificationChannel {
        NotificationResult send(Notification notification);
        boolean isAvailable();
        String getChannelName();
        int getMaxMessageLength();
        NotificationPriority[] getSupportedPriorities();
    }
    
    public static class Notification {
        private final String recipient;
        private final String message;
        private final String subject;
        private final NotificationPriority priority;
        private final Map<String, Object> metadata;
        private final LocalDateTime timestamp;
        
        private Notification(Builder builder) {
            this.recipient = builder.recipient;
            this.message = builder.message;
            this.subject = builder.subject;
            this.priority = builder.priority;
            this.metadata = builder.metadata;
            this.timestamp = LocalDateTime.now();
        }
        
        public String getRecipient() { return recipient; }
        public String getMessage() { return message; }
        public String getSubject() { return subject; }
        public NotificationPriority getPriority() { return priority; }
        public Map<String, Object> getMetadata() { return metadata; }
        public LocalDateTime getTimestamp() { return timestamp; }
        
        public static class Builder {
            private String recipient;
            private String message;
            private String subject = "";
            private NotificationPriority priority = NotificationPriority.NORMAL;
            private Map<String, Object> metadata = new HashMap<>();
            
            public Builder recipient(String recipient) {
                this.recipient = recipient;
                return this;
            }
            
            public Builder message(String message) {
                this.message = message;
                return this;
            }
            
            public Builder subject(String subject) {
                this.subject = subject;
                return this;
            }
            
            public Builder priority(NotificationPriority priority) {
                this.priority = priority;
                return this;
            }
            
            public Builder addMetadata(String key, Object value) {
                this.metadata.put(key, value);
                return this;
            }
            
            public Notification build() {
                if (recipient == null || message == null) {
                    throw new IllegalStateException("Recipient and message are required");
                }
                return new Notification(this);
            }
        }
    }
    
    public enum NotificationPriority {
        LOW(3), NORMAL(2), HIGH(1), URGENT(0);
        
        private final int order;
        
        NotificationPriority(int order) {
            this.order = order;
        }
        
        public int getOrder() { return order; }
    }
    
    public static class NotificationResult {
        private final boolean success;
        private final String messageId;
        private final String channelName;
        private final String errorMessage;
        private final LocalDateTime sentAt;
        
        public NotificationResult(boolean success, String messageId, String channelName, 
                                 String errorMessage) {
            this.success = success;
            this.messageId = messageId;
            this.channelName = channelName;
            this.errorMessage = errorMessage;
            this.sentAt = LocalDateTime.now();
        }
        
        public boolean isSuccess() { return success; }
        public String getMessageId() { return messageId; }
        public String getChannelName() { return channelName; }
        public String getErrorMessage() { return errorMessage; }
        public LocalDateTime getSentAt() { return sentAt; }
    }
    
    // Message formatter interface
    public interface MessageFormatter {
        String format(String message, Map<String, Object> variables);
        String getName();
    }
    
    // Base notification channel with common functionality
    public abstract static class AbstractNotificationChannel implements NotificationChannel {
        protected final String channelName;
        protected final MessageFormatter formatter;
        protected boolean available = true;
        
        public AbstractNotificationChannel(String channelName, MessageFormatter formatter) {
            this.channelName = channelName;
            this.formatter = formatter;
        }
        
        @Override
        public final NotificationResult send(Notification notification) {
            if (!isAvailable()) {
                return new NotificationResult(false, null, channelName, 
                                            "Channel unavailable");
            }
            
            if (!isSupported(notification.getPriority())) {
                return new NotificationResult(false, null, channelName,
                                            "Priority not supported");
            }
            
            String formattedMessage = formatter.format(notification.getMessage(), 
                                                      notification.getMetadata());
            
            if (formattedMessage.length() > getMaxMessageLength()) {
                formattedMessage = truncateMessage(formattedMessage);
            }
            
            return doSend(notification, formattedMessage);
        }
        
        protected abstract NotificationResult doSend(Notification notification, 
                                                    String formattedMessage);
        
        protected String truncateMessage(String message) {
            int maxLength = getMaxMessageLength();
            if (message.length() <= maxLength) return message;
            return message.substring(0, maxLength - 3) + "...";
        }
        
        protected boolean isSupported(NotificationPriority priority) {
            return Arrays.asList(getSupportedPriorities()).contains(priority);
        }
        
        @Override
        public String getChannelName() { return channelName; }
        
        @Override
        public boolean isAvailable() { return available; }
        
        public void setAvailable(boolean available) { this.available = available; }
    }
    
    // Concrete notification channels
    public static class EmailChannel extends AbstractNotificationChannel {
        private final String smtpServer;
        private final int port;
        
        public EmailChannel(String smtpServer, int port, MessageFormatter formatter) {
            super("Email", formatter);
            this.smtpServer = smtpServer;
            this.port = port;
        }
        
        @Override
        protected NotificationResult doSend(Notification notification, String formattedMessage) {
            System.out.println("\n📧 EMAIL NOTIFICATION");
            System.out.println("Server: " + smtpServer + ":" + port);
            System.out.println("To: " + notification.getRecipient());
            System.out.println("Subject: " + notification.getSubject());
            System.out.println("Priority: " + notification.getPriority());
            System.out.println("Message:\n" + formattedMessage);
            
            String messageId = "EMAIL_" + System.currentTimeMillis();
            return new NotificationResult(true, messageId, channelName, null);
        }
        
        @Override
        public int getMaxMessageLength() {
            return 10000; // 10KB limit
        }
        
        @Override
        public NotificationPriority[] getSupportedPriorities() {
            return NotificationPriority.values(); // Supports all priorities
        }
    }
    
    public static class SMSChannel extends AbstractNotificationChannel {
        private final String provider;
        private final String apiKey;
        
        public SMSChannel(String provider, String apiKey, MessageFormatter formatter) {
            super("SMS", formatter);
            this.provider = provider;
            this.apiKey = apiKey;
        }
        
        @Override
        protected NotificationResult doSend(Notification notification, String formattedMessage) {
            System.out.println("\n📱 SMS NOTIFICATION");
            System.out.println("Provider: " + provider);
            System.out.println("To: " + notification.getRecipient());
            System.out.println("Priority: " + notification.getPriority());
            System.out.println("Message: " + formattedMessage);
            System.out.println("Characters: " + formattedMessage.length() + "/" + getMaxMessageLength());
            
            String messageId = "SMS_" + provider + "_" + System.currentTimeMillis();
            return new NotificationResult(true, messageId, channelName, null);
        }
        
        @Override
        public int getMaxMessageLength() {
            return 160; // Standard SMS length
        }
        
        @Override
        public NotificationPriority[] getSupportedPriorities() {
            // SMS only for high priority
            return new NotificationPriority[] { 
                NotificationPriority.HIGH, 
                NotificationPriority.URGENT 
            };
        }
    }
    
    public static class PushNotificationChannel extends AbstractNotificationChannel {
        private final String appId;
        private final String serverKey;
        
        public PushNotificationChannel(String appId, String serverKey, MessageFormatter formatter) {
            super("Push", formatter);
            this.appId = appId;
            this.serverKey = serverKey;
        }
        
        @Override
        protected NotificationResult doSend(Notification notification, String formattedMessage) {
            System.out.println("\n🔔 PUSH NOTIFICATION");
            System.out.println("App ID: " + appId);
            System.out.println("Device: " + notification.getRecipient());
            System.out.println("Priority: " + notification.getPriority());
            System.out.println("Title: " + notification.getSubject());
            System.out.println("Body: " + formattedMessage);
            
            String messageId = "PUSH_" + appId + "_" + System.currentTimeMillis();
            return new NotificationResult(true, messageId, channelName, null);
        }
        
        @Override
        public int getMaxMessageLength() {
            return 250; // Push notification limit
        }
        
        @Override
        public NotificationPriority[] getSupportedPriorities() {
            return NotificationPriority.values();
        }
    }
    
    // New channels can be added without modifying existing code
    public static class SlackChannel extends AbstractNotificationChannel {
        private final String workspace;
        private final String token;
        
        public SlackChannel(String workspace, String token, MessageFormatter formatter) {
            super("Slack", formatter);
            this.workspace = workspace;
            this.token = token;
        }
        
        @Override
        protected NotificationResult doSend(Notification notification, String formattedMessage) {
            System.out.println("\n💬 SLACK NOTIFICATION");
            System.out.println("Workspace: " + workspace);
            System.out.println("Channel/User: " + notification.getRecipient());
            System.out.println("Priority: " + notification.getPriority());
            
            // Add priority emoji
            String emoji = getPriorityEmoji(notification.getPriority());
            String slackMessage = emoji + " " + formattedMessage;
            
            System.out.println("Message: " + slackMessage);
            
            String messageId = "SLACK_" + System.currentTimeMillis();
            return new NotificationResult(true, messageId, channelName, null);
        }
        
        private String getPriorityEmoji(NotificationPriority priority) {
            switch (priority) {
                case URGENT: return "🚨";
                case HIGH: return "⚠️";
                case NORMAL: return "ℹ️";
                case LOW: return "💡";
                default: return "";
            }
        }
        
        @Override
        public int getMaxMessageLength() {
            return 4000; // Slack message limit
        }
        
        @Override
        public NotificationPriority[] getSupportedPriorities() {
            return NotificationPriority.values();
        }
    }
    
    public static class TeamsChannel extends AbstractNotificationChannel {
        private final String webhookUrl;
        
        public TeamsChannel(String webhookUrl, MessageFormatter formatter) {
            super("Teams", formatter);
            this.webhookUrl = webhookUrl;
        }
        
        @Override
        protected NotificationResult doSend(Notification notification, String formattedMessage) {
            System.out.println("\n👥 TEAMS NOTIFICATION");
            System.out.println("Webhook: " + webhookUrl);
            System.out.println("To: " + notification.getRecipient());
            System.out.println("Title: " + notification.getSubject());
            System.out.println("Priority: " + notification.getPriority());
            System.out.println("Message: " + formattedMessage);
            
            String messageId = "TEAMS_" + System.currentTimeMillis();
            return new NotificationResult(true, messageId, channelName, null);
        }
        
        @Override
        public int getMaxMessageLength() {
            return 5000;
        }
        
        @Override
        public NotificationPriority[] getSupportedPriorities() {
            return NotificationPriority.values();
        }
    }
    
    // Message formatters
    public static class PlainTextFormatter implements MessageFormatter {
        @Override
        public String format(String message, Map<String, Object> variables) {
            String formatted = message;
            for (Map.Entry<String, Object> entry : variables.entrySet()) {
                formatted = formatted.replace("{{" + entry.getKey() + "}}", 
                                             String.valueOf(entry.getValue()));
            }
            return formatted;
        }
        
        @Override
        public String getName() { return "PlainText"; }
    }
    
    public static class HTMLFormatter implements MessageFormatter {
        @Override
        public String format(String message, Map<String, Object> variables) {
            String formatted = "<html><body>" + message + "</body></html>";
            for (Map.Entry<String, Object> entry : variables.entrySet()) {
                formatted = formatted.replace("{{" + entry.getKey() + "}}", 
                                             "<b>" + entry.getValue() + "</b>");
            }
            return formatted;
        }
        
        @Override
        public String getName() { return "HTML"; }
    }
    
    public static class MarkdownFormatter implements MessageFormatter {
        @Override
        public String format(String message, Map<String, Object> variables) {
            String formatted = message;
            for (Map.Entry<String, Object> entry : variables.entrySet()) {
                formatted = formatted.replace("{{" + entry.getKey() + "}}", 
                                             "**" + entry.getValue() + "**");
            }
            return formatted;
        }
        
        @Override
        public String getName() { return "Markdown"; }
    }
    
    // Notification filter interface for extensible filtering
    public interface NotificationFilter {
        boolean shouldSend(Notification notification);
        String getFilterName();
    }
    
    public static class PriorityFilter implements NotificationFilter {
        private final NotificationPriority minPriority;
        
        public PriorityFilter(NotificationPriority minPriority) {
            this.minPriority = minPriority;
        }
        
        @Override
        public boolean shouldSend(Notification notification) {
            return notification.getPriority().getOrder() <= minPriority.getOrder();
        }
        
        @Override
        public String getFilterName() { 
            return "PriorityFilter(min=" + minPriority + ")"; 
        }
    }
    
    public static class TimeWindowFilter implements NotificationFilter {
        private final int startHour;
        private final int endHour;
        
        public TimeWindowFilter(int startHour, int endHour) {
            this.startHour = startHour;
            this.endHour = endHour;
        }
        
        @Override
        public boolean shouldSend(Notification notification) {
            int currentHour = LocalDateTime.now().getHour();
            return currentHour >= startHour && currentHour < endHour;
        }
        
        @Override
        public String getFilterName() { 
            return "TimeWindow(" + startHour + "-" + endHour + ")"; 
        }
    }
    
    // Main notification service
    public static class NotificationService {
        private final Map<String, NotificationChannel> channels;
        private final List<NotificationFilter> filters;
        private final ExecutorService executor;
        private final List<NotificationResult> history;
        
        public NotificationService() {
            this.channels = new ConcurrentHashMap<>();
            this.filters = new CopyOnWriteArrayList<>();
            this.executor = Executors.newFixedThreadPool(5);
            this.history = new CopyOnWriteArrayList<>();
        }
        
        public void registerChannel(NotificationChannel channel) {
            channels.put(channel.getChannelName().toUpperCase(), channel);
            System.out.println("✓ Registered channel: " + channel.getChannelName());
        }
        
        public void addFilter(NotificationFilter filter) {
            filters.add(filter);
            System.out.println("✓ Added filter: " + filter.getFilterName());
        }
        
        public NotificationResult send(String channelName, Notification notification) {
            // Apply filters
            for (NotificationFilter filter : filters) {
                if (!filter.shouldSend(notification)) {
                    String message = "Blocked by filter: " + filter.getFilterName();
                    return new NotificationResult(false, null, channelName, message);
                }
            }
            
            NotificationChannel channel = channels.get(channelName.toUpperCase());
            if (channel == null) {
                return new NotificationResult(false, null, channelName, 
                                            "Channel not found");
            }
            
            NotificationResult result = channel.send(notification);
            history.add(result);
            return result;
        }
        
        public CompletableFuture<NotificationResult> sendAsync(String channelName, 
                                                               Notification notification) {
            return CompletableFuture.supplyAsync(() -> send(channelName, notification), executor);
        }
        
        public List<NotificationResult> broadcast(Notification notification) {
            List<CompletableFuture<NotificationResult>> futures = new ArrayList<>();
            
            for (NotificationChannel channel : channels.values()) {
                if (channel.isAvailable()) {
                    futures.add(sendAsync(channel.getChannelName(), notification));
                }
            }
            
            return futures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());
        }
        
        public List<String> getAvailableChannels() {
            return channels.values().stream()
                .filter(NotificationChannel::isAvailable)
                .map(NotificationChannel::getChannelName)
                .collect(Collectors.toList());
        }
        
        public void shutdown() {
            executor.shutdown();
        }
        
        public List<NotificationResult> getHistory() {
            return new ArrayList<>(history);
        }
    }
    
    /**
     * Demonstration of the Notification System following OCP
     */
    public static void main(String[] args) {
        System.out.println("=== Notification System - OCP Demo ===\n");
        
        // Create notification service
        NotificationService service = new NotificationService();
        
        // Register initial channels with different formatters
        service.registerChannel(new EmailChannel("smtp.gmail.com", 587, new HTMLFormatter()));
        service.registerChannel(new SMSChannel("Twilio", "API_KEY_123", new PlainTextFormatter()));
        service.registerChannel(new PushNotificationChannel("APP_001", "SERVER_KEY", new PlainTextFormatter()));
        
        // Add filters
        service.addFilter(new PriorityFilter(NotificationPriority.LOW));
        service.addFilter(new TimeWindowFilter(8, 22)); // Only send between 8 AM and 10 PM
        
        System.out.println("\n" + "=".repeat(50));
        System.out.println("Sending notifications with initial channels:");
        System.out.println("=".repeat(50));
        
        // Create notifications
        Notification urgent = new Notification.Builder()
            .recipient("user@example.com")
            .subject("System Alert")
            .message("Critical error in module {{module}}")
            .priority(NotificationPriority.URGENT)
            .addMetadata("module", "PaymentService")
            .build();
        
        Notification normal = new Notification.Builder()
            .recipient("+1234567890")
            .message("Your order {{orderId}} has been shipped")
            .priority(NotificationPriority.NORMAL)
            .addMetadata("orderId", "ORD-12345")
            .build();
        
        // Send via different channels
        service.send("Email", urgent);
        service.send("SMS", urgent);
        service.send("Push", normal);
        
        System.out.println("\n" + "=".repeat(50));
        System.out.println("Adding NEW channels WITHOUT modifying existing code:");
        System.out.println("=".repeat(50) + "\n");
        
        // Add new channels without modifying existing code
        service.registerChannel(new SlackChannel("workspace-123", "xoxb-token", new MarkdownFormatter()));
        service.registerChannel(new TeamsChannel("https://webhook.teams.com/123", new MarkdownFormatter()));
        
        // Send via new channels
        Notification slackNotif = new Notification.Builder()
            .recipient("#general")
            .message("Deployment {{version}} completed successfully")
            .priority(NotificationPriority.HIGH)
            .addMetadata("version", "v2.3.1")
            .build();
        
        service.send("Slack", slackNotif);
        service.send("Teams", slackNotif);
        
        System.out.println("\n" + "=".repeat(50));
        System.out.println("Broadcasting to all available channels:");
        System.out.println("=".repeat(50));
        
        Notification broadcast = new Notification.Builder()
            .recipient("all-users")
            .subject("Maintenance Notice")
            .message("System maintenance scheduled for {{date}} at {{time}}")
            .priority(NotificationPriority.HIGH)
            .addMetadata("date", "Dec 25, 2024")
            .addMetadata("time", "2:00 AM UTC")
            .build();
        
        List<NotificationResult> results = service.broadcast(broadcast);
        
        System.out.println("\n📊 Broadcast Results:");
        for (NotificationResult result : results) {
            System.out.println(String.format("  • %s: %s (ID: %s)", 
                result.getChannelName(),
                result.isSuccess() ? "✓ Success" : "✗ Failed",
                result.getMessageId()));
        }
        
        System.out.println("\n" + "=".repeat(50));
        System.out.println("BENEFITS of OCP in this design:");
        System.out.println("=".repeat(50));
        System.out.println("✅ Added Slack and Teams channels without modifying existing code");
        System.out.println("✅ Each channel has its own formatting and validation logic");
        System.out.println("✅ Filters can be added/removed dynamically");
        System.out.println("✅ Message formatters are independent and reusable");
        System.out.println("✅ Priority handling is channel-specific");
        System.out.println("✅ New channels automatically work with existing filters");
        System.out.println("✅ Service doesn't know about specific channel implementations");
        
        System.out.println("\n📱 Available channels: " + service.getAvailableChannels());
        
        service.shutdown();
        System.out.println("\n=== Demo Complete ===");
    }
}