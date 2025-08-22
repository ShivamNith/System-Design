package org.example.designPatterns.bridgePattern;

import java.util.*;

/**
 * Notification System Example using Bridge Pattern
 * 
 * This example demonstrates how the Bridge Pattern can be used to create
 * a notification system that works across different messaging platforms
 * while maintaining platform-specific implementations.
 * 
 * The abstraction represents notification services, while implementations handle
 * platform-specific message sending (Email, SMS, Slack, Push Notifications).
 */

// Implementation interface
interface MessageSender {
    void sendMessage(String recipient, String subject, String content);
    void sendBulkMessage(List<String> recipients, String subject, String content);
    boolean isConnected();
    void connect();
    void disconnect();
    String getServiceInfo();
    Map<String, Object> getDeliveryStatus(String messageId);
}

// Concrete Implementations
class EmailSender implements MessageSender {
    private boolean connected = false;
    private String smtpServer;
    private int port;
    private String username;
    private Map<String, Object> deliveryStatuses;
    
    public EmailSender(String smtpServer, int port, String username) {
        this.smtpServer = smtpServer;
        this.port = port;
        this.username = username;
        this.deliveryStatuses = new HashMap<>();
    }
    
    @Override
    public void connect() {
        System.out.println("Connecting to SMTP server: " + smtpServer + ":" + port);
        System.out.println("Authenticating user: " + username);
        System.out.println("Setting up TLS encryption...");
        connected = true;
        System.out.println("Connected to email server successfully!");
    }
    
    @Override
    public void disconnect() {
        if (connected) {
            System.out.println("Closing SMTP connection");
            connected = false;
        }
    }
    
    @Override
    public void sendMessage(String recipient, String subject, String content) {
        if (!connected) connect();
        
        String messageId = "email_" + System.currentTimeMillis() + "_" + Math.random();
        
        System.out.println("Sending email via SMTP:");
        System.out.println("  To: " + recipient);
        System.out.println("  Subject: " + subject);
        System.out.println("  Content Length: " + content.length() + " characters");
        System.out.println("  Format: HTML/Plain text with attachments support");
        System.out.println("  Message ID: " + messageId);
        
        // Simulate email delivery
        simulateDelivery(messageId, recipient, "email");
        
        System.out.println("Email sent successfully!");
    }
    
    @Override
    public void sendBulkMessage(List<String> recipients, String subject, String content) {
        if (!connected) connect();
        
        System.out.println("Sending bulk email to " + recipients.size() + " recipients");
        System.out.println("Using email batch processing for efficiency");
        
        for (int i = 0; i < recipients.size(); i++) {
            String recipient = recipients.get(i);
            sendMessage(recipient, subject, content);
            
            // Add small delay to avoid spam filters
            if (i < recipients.size() - 1) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        System.out.println("Bulk email campaign completed");
    }
    
    @Override
    public boolean isConnected() {
        return connected;
    }
    
    @Override
    public String getServiceInfo() {
        return "SMTP Email Service - " + smtpServer + ":" + port + " (User: " + username + ")";
    }
    
    @Override
    public Map<String, Object> getDeliveryStatus(String messageId) {
        return deliveryStatuses.getOrDefault(messageId, Map.of("status", "unknown"));
    }
    
    private void simulateDelivery(String messageId, String recipient, String channel) {
        Map<String, Object> status = new HashMap<>();
        status.put("messageId", messageId);
        status.put("recipient", recipient);
        status.put("channel", channel);
        status.put("status", "delivered");
        status.put("timestamp", new Date());
        status.put("deliveryTime", "2.3 seconds");
        deliveryStatuses.put(messageId, status);
    }
}

class SMSSender implements MessageSender {
    private boolean connected = false;
    private String serviceProvider;
    private String apiKey;
    private Map<String, Object> deliveryStatuses;
    
    public SMSSender(String serviceProvider, String apiKey) {
        this.serviceProvider = serviceProvider;
        this.apiKey = apiKey;
        this.deliveryStatuses = new HashMap<>();
    }
    
    @Override
    public void connect() {
        System.out.println("Connecting to SMS service: " + serviceProvider);
        System.out.println("Authenticating with API key: " + apiKey.substring(0, 4) + "****");
        System.out.println("Validating account balance and rate limits");
        connected = true;
        System.out.println("Connected to SMS service successfully!");
    }
    
    @Override
    public void disconnect() {
        if (connected) {
            System.out.println("Disconnecting from SMS service");
            connected = false;
        }
    }
    
    @Override
    public void sendMessage(String recipient, String subject, String content) {
        if (!connected) connect();
        
        String messageId = "sms_" + System.currentTimeMillis() + "_" + Math.random();
        
        // SMS doesn't use subjects, combine subject and content
        String smsContent = (subject != null && !subject.isEmpty()) ? 
            subject + ": " + content : content;
        
        // Truncate if too long (SMS limit is typically 160 characters)
        if (smsContent.length() > 160) {
            smsContent = smsContent.substring(0, 157) + "...";
        }
        
        System.out.println("Sending SMS via " + serviceProvider + ":");
        System.out.println("  To: " + recipient);
        System.out.println("  Content: " + smsContent);
        System.out.println("  Length: " + smsContent.length() + "/160 characters");
        System.out.println("  Message ID: " + messageId);
        System.out.println("  Cost: $0.05");
        
        // Simulate SMS delivery
        simulateDelivery(messageId, recipient, "sms");
        
        System.out.println("SMS sent successfully!");
    }
    
    @Override
    public void sendBulkMessage(List<String> recipients, String subject, String content) {
        if (!connected) connect();
        
        System.out.println("Sending bulk SMS to " + recipients.size() + " recipients");
        System.out.println("Estimated cost: $" + (recipients.size() * 0.05));
        
        String smsContent = (subject != null && !subject.isEmpty()) ? 
            subject + ": " + content : content;
        if (smsContent.length() > 160) {
            smsContent = smsContent.substring(0, 157) + "...";
        }
        
        for (String recipient : recipients) {
            sendMessage(recipient, null, smsContent);
        }
        System.out.println("Bulk SMS campaign completed");
    }
    
    @Override
    public boolean isConnected() {
        return connected;
    }
    
    @Override
    public String getServiceInfo() {
        return "SMS Service - " + serviceProvider + " (Global coverage)";
    }
    
    @Override
    public Map<String, Object> getDeliveryStatus(String messageId) {
        return deliveryStatuses.getOrDefault(messageId, Map.of("status", "unknown"));
    }
    
    private void simulateDelivery(String messageId, String recipient, String channel) {
        Map<String, Object> status = new HashMap<>();
        status.put("messageId", messageId);
        status.put("recipient", recipient);
        status.put("channel", channel);
        status.put("status", "delivered");
        status.put("timestamp", new Date());
        status.put("deliveryTime", "1.8 seconds");
        status.put("cost", 0.05);
        deliveryStatuses.put(messageId, status);
    }
}

class SlackSender implements MessageSender {
    private boolean connected = false;
    private String workspaceUrl;
    private String botToken;
    private Map<String, Object> deliveryStatuses;
    
    public SlackSender(String workspaceUrl, String botToken) {
        this.workspaceUrl = workspaceUrl;
        this.botToken = botToken;
        this.deliveryStatuses = new HashMap<>();
    }
    
    @Override
    public void connect() {
        System.out.println("Connecting to Slack workspace: " + workspaceUrl);
        System.out.println("Authenticating with bot token");
        System.out.println("Verifying bot permissions and scopes");
        connected = true;
        System.out.println("Connected to Slack successfully!");
    }
    
    @Override
    public void disconnect() {
        if (connected) {
            System.out.println("Disconnecting from Slack");
            connected = false;
        }
    }
    
    @Override
    public void sendMessage(String recipient, String subject, String content) {
        if (!connected) connect();
        
        String messageId = "slack_" + System.currentTimeMillis() + "_" + Math.random();
        
        System.out.println("Sending Slack message:");
        System.out.println("  Channel/User: " + recipient);
        if (subject != null && !subject.isEmpty()) {
            System.out.println("  Subject: *" + subject + "*");
        }
        System.out.println("  Content: " + content);
        System.out.println("  Format: Markdown supported with rich formatting");
        System.out.println("  Message ID: " + messageId);
        System.out.println("  Features: Emoji reactions, threading, file attachments");
        
        // Simulate Slack delivery
        simulateDelivery(messageId, recipient, "slack");
        
        System.out.println("Slack message sent successfully!");
    }
    
    @Override
    public void sendBulkMessage(List<String> recipients, String subject, String content) {
        if (!connected) connect();
        
        System.out.println("Sending Slack messages to " + recipients.size() + " channels/users");
        
        for (String recipient : recipients) {
            sendMessage(recipient, subject, content);
            // Small delay to respect rate limits
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        System.out.println("Bulk Slack messaging completed");
    }
    
    @Override
    public boolean isConnected() {
        return connected;
    }
    
    @Override
    public String getServiceInfo() {
        return "Slack Messaging - " + workspaceUrl + " (Bot integration)";
    }
    
    @Override
    public Map<String, Object> getDeliveryStatus(String messageId) {
        return deliveryStatuses.getOrDefault(messageId, Map.of("status", "unknown"));
    }
    
    private void simulateDelivery(String messageId, String recipient, String channel) {
        Map<String, Object> status = new HashMap<>();
        status.put("messageId", messageId);
        status.put("recipient", recipient);
        status.put("channel", channel);
        status.put("status", "delivered");
        status.put("timestamp", new Date());
        status.put("deliveryTime", "0.8 seconds");
        status.put("readReceipts", true);
        deliveryStatuses.put(messageId, status);
    }
}

class PushNotificationSender implements MessageSender {
    private boolean connected = false;
    private String platform; // FCM, APNS, etc.
    private String apiKey;
    private Map<String, Object> deliveryStatuses;
    
    public PushNotificationSender(String platform, String apiKey) {
        this.platform = platform;
        this.apiKey = apiKey;
        this.deliveryStatuses = new HashMap<>();
    }
    
    @Override
    public void connect() {
        System.out.println("Connecting to " + platform + " push notification service");
        System.out.println("Authenticating with service key");
        System.out.println("Validating app bundle ID and certificates");
        connected = true;
        System.out.println("Connected to push notification service successfully!");
    }
    
    @Override
    public void disconnect() {
        if (connected) {
            System.out.println("Disconnecting from push notification service");
            connected = false;
        }
    }
    
    @Override
    public void sendMessage(String recipient, String subject, String content) {
        if (!connected) connect();
        
        String messageId = "push_" + System.currentTimeMillis() + "_" + Math.random();
        
        System.out.println("Sending push notification via " + platform + ":");
        System.out.println("  Device Token: " + recipient.substring(0, 10) + "...");
        System.out.println("  Title: " + subject);
        System.out.println("  Body: " + content);
        System.out.println("  Badge Count: +1");
        System.out.println("  Sound: Default notification sound");
        System.out.println("  Message ID: " + messageId);
        
        // Simulate push notification delivery
        simulateDelivery(messageId, recipient, "push");
        
        System.out.println("Push notification sent successfully!");
    }
    
    @Override
    public void sendBulkMessage(List<String> recipients, String subject, String content) {
        if (!connected) connect();
        
        System.out.println("Sending bulk push notifications to " + recipients.size() + " devices");
        System.out.println("Using batch notification API for efficiency");
        
        // Group by batches for efficiency
        int batchSize = 1000;
        for (int i = 0; i < recipients.size(); i += batchSize) {
            int endIndex = Math.min(i + batchSize, recipients.size());
            List<String> batch = recipients.subList(i, endIndex);
            
            System.out.println("Processing batch " + (i/batchSize + 1) + ": " + batch.size() + " notifications");
            
            for (String recipient : batch) {
                sendMessage(recipient, subject, content);
            }
        }
        System.out.println("Bulk push notification campaign completed");
    }
    
    @Override
    public boolean isConnected() {
        return connected;
    }
    
    @Override
    public String getServiceInfo() {
        return "Push Notification Service - " + platform + " (Real-time delivery)";
    }
    
    @Override
    public Map<String, Object> getDeliveryStatus(String messageId) {
        return deliveryStatuses.getOrDefault(messageId, Map.of("status", "unknown"));
    }
    
    private void simulateDelivery(String messageId, String recipient, String channel) {
        Map<String, Object> status = new HashMap<>();
        status.put("messageId", messageId);
        status.put("recipient", recipient);
        status.put("channel", channel);
        status.put("status", "delivered");
        status.put("timestamp", new Date());
        status.put("deliveryTime", "0.3 seconds");
        status.put("deviceStatus", "active");
        deliveryStatuses.put(messageId, status);
    }
}

// Abstraction
abstract class NotificationService {
    protected MessageSender messageSender;
    protected List<String> messageTemplates;
    protected Map<String, String> configuration;
    
    public NotificationService(MessageSender messageSender) {
        this.messageSender = messageSender;
        this.messageTemplates = new ArrayList<>();
        this.configuration = new HashMap<>();
        initializeConfiguration();
    }
    
    public void setMessageSender(MessageSender messageSender) {
        if (this.messageSender != null && this.messageSender.isConnected()) {
            this.messageSender.disconnect();
        }
        this.messageSender = messageSender;
        System.out.println("Switched to: " + messageSender.getServiceInfo());
    }
    
    protected abstract void initializeConfiguration();
    
    public abstract void sendWelcomeMessage(String recipient, String name);
    public abstract void sendPasswordReset(String recipient, String resetLink);
    public abstract void sendOrderConfirmation(String recipient, String orderNumber, double amount);
    public abstract void sendSystemAlert(String recipient, String alertType, String details);
    
    protected String formatTemplate(String template, Map<String, String> variables) {
        String formatted = template;
        for (Map.Entry<String, String> entry : variables.entrySet()) {
            formatted = formatted.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return formatted;
    }
    
    protected void logNotification(String type, String recipient) {
        System.out.println("AUDIT LOG: " + type + " notification sent to " + recipient + " at " + new Date());
    }
}

// Refined Abstractions
class CustomerNotificationService extends NotificationService {
    
    public CustomerNotificationService(MessageSender messageSender) {
        super(messageSender);
        initializeTemplates();
    }
    
    @Override
    protected void initializeConfiguration() {
        configuration.put("welcome_subject", "Welcome to Our Service!");
        configuration.put("reset_subject", "Password Reset Request");
        configuration.put("order_subject", "Order Confirmation");
        configuration.put("sender_name", "Customer Service Team");
    }
    
    private void initializeTemplates() {
        messageTemplates.add("Welcome {name}! We're excited to have you join our community. " +
                           "Your account has been created successfully and you can now start exploring our features.");
        messageTemplates.add("Hi {name}, you requested a password reset. " +
                           "Click the following link to reset your password: {resetLink}. " +
                           "This link expires in 1 hour for security reasons.");
        messageTemplates.add("Dear {name}, your order #{orderNumber} has been confirmed! " +
                           "Total amount: ${amount}. Thank you for your purchase. " +
                           "You'll receive tracking information once your order ships.");
        messageTemplates.add("System Alert: {alertType} - {details}");
    }
    
    @Override
    public void sendWelcomeMessage(String recipient, String name) {
        System.out.println("\n=== Sending Customer Welcome Message ===");
        
        Map<String, String> variables = new HashMap<>();
        variables.put("name", name);
        
        String content = formatTemplate(messageTemplates.get(0), variables);
        String subject = configuration.get("welcome_subject");
        
        messageSender.sendMessage(recipient, subject, content);
        logNotification("WELCOME", recipient);
        
        // Send additional onboarding resources
        String resourcesContent = "Here are some helpful resources to get you started:\n" +
            "1. User Guide: https://help.example.com/guide\n" +
            "2. Video Tutorials: https://help.example.com/videos\n" +
            "3. Community Forum: https://community.example.com\n" +
            "4. Contact Support: support@example.com";
        
        messageSender.sendMessage(recipient, "Getting Started Resources", resourcesContent);
        logNotification("ONBOARDING_RESOURCES", recipient);
    }
    
    @Override
    public void sendPasswordReset(String recipient, String resetLink) {
        System.out.println("\n=== Sending Password Reset ===");
        
        Map<String, String> variables = new HashMap<>();
        variables.put("name", recipient.split("@")[0]); // Extract name from email
        variables.put("resetLink", resetLink);
        
        String content = formatTemplate(messageTemplates.get(1), variables);
        String subject = configuration.get("reset_subject");
        
        messageSender.sendMessage(recipient, subject, content);
        logNotification("PASSWORD_RESET", recipient);
    }
    
    @Override
    public void sendOrderConfirmation(String recipient, String orderNumber, double amount) {
        System.out.println("\n=== Sending Order Confirmation ===");
        
        Map<String, String> variables = new HashMap<>();
        variables.put("name", recipient.split("@")[0]);
        variables.put("orderNumber", orderNumber);
        variables.put("amount", String.format("%.2f", amount));
        
        String content = formatTemplate(messageTemplates.get(2), variables);
        String subject = configuration.get("order_subject") + " - #" + orderNumber;
        
        messageSender.sendMessage(recipient, subject, content);
        logNotification("ORDER_CONFIRMATION", recipient);
    }
    
    @Override
    public void sendSystemAlert(String recipient, String alertType, String details) {
        System.out.println("\n=== Sending System Alert to Customer ===");
        
        Map<String, String> variables = new HashMap<>();
        variables.put("alertType", alertType);
        variables.put("details", details);
        
        String content = formatTemplate(messageTemplates.get(3), variables);
        
        messageSender.sendMessage(recipient, "System Alert: " + alertType, content);
        logNotification("SYSTEM_ALERT", recipient);
    }
    
    public void sendPromotionalMessage(List<String> customers, String promoCode, int discountPercent) {
        System.out.println("\n=== Sending Promotional Campaign ===");
        
        String content = "🎉 Special Offer! Use code " + promoCode + " for " + discountPercent + "% off your next order!\n" +
                        "Valid until the end of this month. Don't miss out on this exclusive deal!\n\n" +
                        "Shop now: https://shop.example.com\n" +
                        "Terms and conditions apply.";
        
        String subject = "Exclusive " + discountPercent + "% Discount - Limited Time!";
        
        messageSender.sendBulkMessage(customers, subject, content);
        
        for (String customer : customers) {
            logNotification("PROMOTIONAL", customer);
        }
    }
}

class AdminNotificationService extends NotificationService {
    private List<String> adminContacts;
    private String alertLevel = "INFO";
    
    public AdminNotificationService(MessageSender messageSender) {
        super(messageSender);
        this.adminContacts = new ArrayList<>();
        initializeTemplates();
    }
    
    @Override
    protected void initializeConfiguration() {
        configuration.put("alert_subject", "System Alert");
        configuration.put("monitoring_subject", "System Monitoring");
        configuration.put("security_subject", "Security Alert");
        configuration.put("sender_name", "System Administrator");
    }
    
    private void initializeTemplates() {
        messageTemplates.add("🚨 CRITICAL ALERT: {alertType}\n\nDetails: {details}\nTime: {timestamp}\nHost: {hostname}");
        messageTemplates.add("📊 System Report: New user registration\nUser: {userName}\nEmail: {email}\nTime: {timestamp}");
        messageTemplates.add("🔒 SECURITY ALERT: {alertType}\nIP Address: {ipAddress}\nDetails: {details}\nTime: {timestamp}");
        messageTemplates.add("📈 Performance Alert: {metricName} threshold exceeded\nCurrent Value: {currentValue}\nThreshold: {threshold}");
    }
    
    public void addAdminContact(String contact) {
        adminContacts.add(contact);
        System.out.println("Added admin contact: " + contact);
    }
    
    public void setAlertLevel(String level) {
        this.alertLevel = level;
        System.out.println("Alert level set to: " + level);
    }
    
    @Override
    public void sendWelcomeMessage(String recipient, String name) {
        // Admin service sends welcome notifications to other admins about new admin users
        System.out.println("\n=== Notifying Admins of New Administrator ===");
        
        String content = "New administrator account created:\n" +
                        "Name: " + name + "\n" +
                        "Email: " + recipient + "\n" +
                        "Time: " + new Date() + "\n" +
                        "Please ensure proper access controls are configured.";
        
        messageSender.sendBulkMessage(adminContacts, "New Administrator Account", content);
        logNotification("ADMIN_WELCOME", recipient);
    }
    
    @Override
    public void sendPasswordReset(String recipient, String resetLink) {
        System.out.println("\n=== Admin Password Reset Notification ===");
        
        String content = "Administrator password reset requested for: " + recipient + "\n" +
                        "Reset link: " + resetLink + "\n" +
                        "Time: " + new Date() + "\n" +
                        "If this was not requested by you, please contact security immediately.";
        
        messageSender.sendMessage(recipient, "Admin Password Reset", content);
        
        // Also notify other admins
        String alertContent = "Password reset requested for admin: " + recipient;
        messageSender.sendBulkMessage(adminContacts, "Admin Password Reset Alert", alertContent);
        
        logNotification("ADMIN_PASSWORD_RESET", recipient);
    }
    
    @Override
    public void sendOrderConfirmation(String recipient, String orderNumber, double amount) {
        // Admin gets order monitoring notifications
        System.out.println("\n=== Sending Order Monitoring Alert ===");
        
        String content = "High-value order alert:\n" +
                        "Order: #" + orderNumber + "\n" +
                        "Amount: $" + String.format("%.2f", amount) + "\n" +
                        "Customer: " + recipient + "\n" +
                        "Time: " + new Date() + "\n" +
                        "Please review for fraud detection.";
        
        messageSender.sendBulkMessage(adminContacts, "High-Value Order Alert", content);
        logNotification("ORDER_MONITORING", recipient);
    }
    
    @Override
    public void sendSystemAlert(String recipient, String alertType, String details) {
        System.out.println("\n=== Sending System Alert ===");
        
        Map<String, String> variables = new HashMap<>();
        variables.put("alertType", alertType);
        variables.put("details", details);
        variables.put("timestamp", new Date().toString());
        variables.put("hostname", "server-01.example.com");
        
        String content = formatTemplate(messageTemplates.get(0), variables);
        String subject = "[" + alertLevel + "] " + configuration.get("alert_subject") + ": " + alertType;
        
        // Send to specific recipient if provided, otherwise to all admins
        if (recipient != null) {
            messageSender.sendMessage(recipient, subject, content);
        } else {
            messageSender.sendBulkMessage(adminContacts, subject, content);
        }
        
        logNotification("SYSTEM_ALERT_" + alertLevel, recipient != null ? recipient : "ALL_ADMINS");
    }
    
    public void sendUserRegistrationNotification(String userName, String email) {
        System.out.println("\n=== Sending User Registration Notification ===");
        
        Map<String, String> variables = new HashMap<>();
        variables.put("userName", userName);
        variables.put("email", email);
        variables.put("timestamp", new Date().toString());
        
        String content = formatTemplate(messageTemplates.get(1), variables);
        
        messageSender.sendBulkMessage(adminContacts, "New User Registration", content);
        logNotification("USER_REGISTRATION", email);
    }
    
    public void sendSecurityAlert(String alertType, String ipAddress, String details) {
        System.out.println("\n=== Sending Security Alert ===");
        
        Map<String, String> variables = new HashMap<>();
        variables.put("alertType", alertType);
        variables.put("ipAddress", ipAddress);
        variables.put("details", details);
        variables.put("timestamp", new Date().toString());
        
        String content = formatTemplate(messageTemplates.get(2), variables);
        String subject = "🔒 SECURITY ALERT: " + alertType;
        
        messageSender.sendBulkMessage(adminContacts, subject, content);
        logNotification("SECURITY_ALERT", ipAddress);
    }
    
    public void sendPerformanceAlert(String metricName, double currentValue, double threshold) {
        System.out.println("\n=== Sending Performance Alert ===");
        
        Map<String, String> variables = new HashMap<>();
        variables.put("metricName", metricName);
        variables.put("currentValue", String.valueOf(currentValue));
        variables.put("threshold", String.valueOf(threshold));
        
        String content = formatTemplate(messageTemplates.get(3), variables);
        String subject = "📈 Performance Alert: " + metricName;
        
        messageSender.sendBulkMessage(adminContacts, subject, content);
        logNotification("PERFORMANCE_ALERT", metricName);
    }
}

// Demo Application
public class NotificationSystemExample {
    public static void main(String[] args) {
        System.out.println("=== Notification System Bridge Pattern Demo ===\n");
        
        // Create different message senders
        MessageSender emailSender = new EmailSender("smtp.gmail.com", 587, "notifications@example.com");
        MessageSender smsSender = new SMSSender("Twilio", "sk_test_1234567890abcdef");
        MessageSender slackSender = new SlackSender("example-workspace.slack.com", "xoxb-1234567890");
        MessageSender pushSender = new PushNotificationSender("FCM", "AAAAkey123");
        
        // Create notification services
        CustomerNotificationService customerService = new CustomerNotificationService(emailSender);
        AdminNotificationService adminService = new AdminNotificationService(slackSender);
        
        // Configure admin contacts
        adminService.addAdminContact("#alerts");
        adminService.addAdminContact("#devops");
        adminService.addAdminContact("admin@example.com");
        
        // Demo 1: Customer notifications via Email
        System.out.println("1. Customer Service via Email:");
        customerService.sendWelcomeMessage("john.doe@example.com", "John Doe");
        customerService.sendOrderConfirmation("john.doe@example.com", "ORD-12345", 149.99);
        
        // Demo 2: Switch customer service to SMS
        System.out.println("\n" + "=".repeat(60));
        System.out.println("2. Switching Customer Service to SMS:");
        customerService.setMessageSender(smsSender);
        customerService.sendPasswordReset("+1234567890", "https://example.com/reset/xyz123");
        
        // Demo 3: Admin notifications via Slack
        System.out.println("\n" + "=".repeat(60));
        System.out.println("3. Admin Notifications via Slack:");
        adminService.sendSystemAlert(null, "High CPU Usage", "CPU usage at 95% for 5 minutes");
        adminService.sendSecurityAlert("Failed Login Attempts", "192.168.1.100", "5 failed attempts in 2 minutes");
        adminService.sendUserRegistrationNotification("Jane Smith", "jane.smith@example.com");
        
        // Demo 4: Switch admin service to Email for critical alerts
        System.out.println("\n" + "=".repeat(60));
        System.out.println("4. Switching Admin Service to Email for Critical Alerts:");
        adminService.setMessageSender(emailSender);
        adminService.setAlertLevel("CRITICAL");
        adminService.sendSystemAlert(null, "Database Connection Lost", "Primary database unreachable for 2 minutes");
        
        // Demo 5: Promotional campaign via Push Notifications
        System.out.println("\n" + "=".repeat(60));
        System.out.println("5. Promotional Campaign via Push Notifications:");
        customerService.setMessageSender(pushSender);
        
        List<String> customers = Arrays.asList(
            "device_token_1234567890abcdef",
            "device_token_2345678901bcdefg", 
            "device_token_3456789012cdefgh"
        );
        
        customerService.sendPromotionalMessage(customers, "SAVE20", 20);
        
        // Demo 6: Multi-channel notification strategy
        System.out.println("\n" + "=".repeat(60));
        System.out.println("6. Multi-Channel Notification Strategy:");
        demonstrateMultiChannelStrategy();
        
        // Demo 7: Delivery status tracking
        System.out.println("\n" + "=".repeat(60));
        System.out.println("7. Delivery Status Tracking:");
        demonstrateDeliveryTracking(emailSender, smsSender);
        
        // Cleanup
        emailSender.disconnect();
        smsSender.disconnect();
        slackSender.disconnect();
        pushSender.disconnect();
    }
    
    private static void demonstrateMultiChannelStrategy() {
        MessageSender[] channels = {
            new EmailSender("smtp.example.com", 587, "noreply@example.com"),
            new SMSSender("Twilio", "api_key_123"),
            new SlackSender("company.slack.com", "bot_token_xyz"),
            new PushNotificationSender("FCM", "fcm_key_abc")
        };
        
        CustomerNotificationService service = new CustomerNotificationService(channels[0]);
        
        String[] recipients = {"user@example.com", "+1234567890", "#general", "push_token_123"};
        String[] messageTypes = {"Welcome Email", "Urgent SMS", "Team Update", "Mobile Alert"};
        
        for (int i = 0; i < channels.length; i++) {
            System.out.println("\nSending " + messageTypes[i] + ":");
            service.setMessageSender(channels[i]);
            service.sendWelcomeMessage(recipients[i], "Multi-Channel User");
        }
    }
    
    private static void demonstrateDeliveryTracking(MessageSender... senders) {
        for (MessageSender sender : senders) {
            sender.connect();
            sender.sendMessage("tracking@example.com", "Test Message", "Testing delivery tracking");
            
            // Simulate checking delivery status
            System.out.println("Checking delivery status for " + sender.getServiceInfo() + ":");
            // In a real implementation, you would store and track message IDs
            System.out.println("  Status: Messages delivered successfully");
            System.out.println("  Delivery rate: 98.5%");
            System.out.println("  Average delivery time: 1.2 seconds");
            
            sender.disconnect();
        }
    }
}