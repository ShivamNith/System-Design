package org.example.designPatterns.builderPattern;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Demonstration of Builder Pattern with Email Builder System
 * 
 * This example shows how to use the Builder pattern to construct
 * complex email messages with various formatting options and attachments.
 */

// Email attachment class
class EmailAttachment {
    private final String filename;
    private final String contentType;
    private final byte[] data;
    private final long size;
    
    public EmailAttachment(String filename, String contentType, byte[] data) {
        this.filename = filename;
        this.contentType = contentType;
        this.data = data != null ? data.clone() : new byte[0];
        this.size = this.data.length;
    }
    
    // Getters
    public String getFilename() { return filename; }
    public String getContentType() { return contentType; }
    public byte[] getData() { return data.clone(); }
    public long getSize() { return size; }
    
    @Override
    public String toString() {
        return String.format("%s (%s, %.2f KB)", filename, contentType, size / 1024.0);
    }
}

// Product Class - Email
class Email {
    private final String from;
    private final List<String> to;
    private final List<String> cc;
    private final List<String> bcc;
    private final String subject;
    private final String textBody;
    private final String htmlBody;
    private final List<EmailAttachment> attachments;
    private final Priority priority;
    private final boolean isReadReceiptRequested;
    private final boolean isDeliveryReceiptRequested;
    private final LocalDateTime scheduledTime;
    private final Map<String, String> customHeaders;
    private final String replyTo;
    private final String sender;
    private final EmailTemplate template;
    private final Map<String, String> templateVariables;
    
    public enum Priority {
        LOW, NORMAL, HIGH, URGENT
    }
    
    public enum EmailTemplate {
        PLAIN, NEWSLETTER, WELCOME, NOTIFICATION, INVOICE, MARKETING
    }
    
    private Email(Builder builder) {
        this.from = builder.from;
        this.to = new ArrayList<>(builder.to);
        this.cc = new ArrayList<>(builder.cc);
        this.bcc = new ArrayList<>(builder.bcc);
        this.subject = builder.subject;
        this.textBody = builder.textBody;
        this.htmlBody = builder.htmlBody;
        this.attachments = new ArrayList<>(builder.attachments);
        this.priority = builder.priority;
        this.isReadReceiptRequested = builder.isReadReceiptRequested;
        this.isDeliveryReceiptRequested = builder.isDeliveryReceiptRequested;
        this.scheduledTime = builder.scheduledTime;
        this.customHeaders = new HashMap<>(builder.customHeaders);
        this.replyTo = builder.replyTo;
        this.sender = builder.sender;
        this.template = builder.template;
        this.templateVariables = new HashMap<>(builder.templateVariables);
        
        validateEmail();
    }
    
    private void validateEmail() {
        if (from == null || from.trim().isEmpty()) {
            throw new IllegalStateException("From address is required");
        }
        if (to.isEmpty()) {
            throw new IllegalStateException("At least one recipient is required");
        }
        if (subject == null || subject.trim().isEmpty()) {
            throw new IllegalStateException("Subject is required");
        }
        if (textBody == null && htmlBody == null) {
            throw new IllegalStateException("Either text body or HTML body is required");
        }
        
        // Validate email addresses
        validateEmailAddresses();
        
        // Check attachment size limits
        long totalSize = attachments.stream().mapToLong(EmailAttachment::getSize).sum();
        if (totalSize > 25 * 1024 * 1024) { // 25MB limit
            throw new IllegalStateException("Total attachment size exceeds 25MB limit");
        }
    }
    
    private void validateEmailAddresses() {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        
        if (!from.matches(emailRegex)) {
            throw new IllegalStateException("Invalid from email address: " + from);
        }
        
        for (String email : to) {
            if (!email.matches(emailRegex)) {
                throw new IllegalStateException("Invalid to email address: " + email);
            }
        }
        
        for (String email : cc) {
            if (!email.matches(emailRegex)) {
                throw new IllegalStateException("Invalid cc email address: " + email);
            }
        }
        
        for (String email : bcc) {
            if (!email.matches(emailRegex)) {
                throw new IllegalStateException("Invalid bcc email address: " + email);
            }
        }
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static Builder from(String from) {
        return new Builder().from(from);
    }
    
    public String generateMessageId() {
        return "<" + UUID.randomUUID().toString() + "@" + from.split("@")[1] + ">";
    }
    
    public String getRenderedBody() {
        if (template != null && !templateVariables.isEmpty()) {
            return renderTemplate();
        }
        return htmlBody != null ? htmlBody : textBody;
    }
    
    private String renderTemplate() {
        String templateContent = getTemplateContent();
        
        for (Map.Entry<String, String> variable : templateVariables.entrySet()) {
            templateContent = templateContent.replace("{{" + variable.getKey() + "}}", variable.getValue());
        }
        
        return templateContent;
    }
    
    private String getTemplateContent() {
        switch (template) {
            case WELCOME:
                return """
                    <html><body>
                    <h1>Welcome {{name}}!</h1>
                    <p>Thank you for joining {{company}}. We're excited to have you on board.</p>
                    <p>Your account details:</p>
                    <ul>
                        <li>Email: {{email}}</li>
                        <li>Account ID: {{accountId}}</li>
                    </ul>
                    <p>Best regards,<br>{{company}} Team</p>
                    </body></html>
                    """;
            case NOTIFICATION:
                return """
                    <html><body>
                    <h2>{{title}}</h2>
                    <p>{{message}}</p>
                    <p><em>This notification was sent on {{timestamp}}</em></p>
                    </body></html>
                    """;
            case NEWSLETTER:
                return """
                    <html><body>
                    <h1>{{newsletterTitle}}</h1>
                    <p>Dear {{subscriberName}},</p>
                    <div>{{content}}</div>
                    <p>If you no longer wish to receive these emails, <a href="{{unsubscribeLink}}">click here to unsubscribe</a>.</p>
                    </body></html>
                    """;
            default:
                return htmlBody != null ? htmlBody : textBody;
        }
    }
    
    public static class Builder {
        private String from;
        private List<String> to = new ArrayList<>();
        private List<String> cc = new ArrayList<>();
        private List<String> bcc = new ArrayList<>();
        private String subject;
        private String textBody;
        private String htmlBody;
        private List<EmailAttachment> attachments = new ArrayList<>();
        private Priority priority = Priority.NORMAL;
        private boolean isReadReceiptRequested = false;
        private boolean isDeliveryReceiptRequested = false;
        private LocalDateTime scheduledTime;
        private Map<String, String> customHeaders = new HashMap<>();
        private String replyTo;
        private String sender;
        private EmailTemplate template;
        private Map<String, String> templateVariables = new HashMap<>();
        
        public Builder from(String from) {
            this.from = from;
            return this;
        }
        
        public Builder to(String... recipients) {
            this.to.addAll(Arrays.asList(recipients));
            return this;
        }
        
        public Builder to(List<String> recipients) {
            this.to.addAll(recipients);
            return this;
        }
        
        public Builder cc(String... recipients) {
            this.cc.addAll(Arrays.asList(recipients));
            return this;
        }
        
        public Builder cc(List<String> recipients) {
            this.cc.addAll(recipients);
            return this;
        }
        
        public Builder bcc(String... recipients) {
            this.bcc.addAll(Arrays.asList(recipients));
            return this;
        }
        
        public Builder bcc(List<String> recipients) {
            this.bcc.addAll(recipients);
            return this;
        }
        
        public Builder subject(String subject) {
            this.subject = subject;
            return this;
        }
        
        public Builder textBody(String textBody) {
            this.textBody = textBody;
            return this;
        }
        
        public Builder htmlBody(String htmlBody) {
            this.htmlBody = htmlBody;
            return this;
        }
        
        public Builder body(String body) {
            this.textBody = body;
            return this;
        }
        
        public Builder attach(String filename, String contentType, byte[] data) {
            this.attachments.add(new EmailAttachment(filename, contentType, data));
            return this;
        }
        
        public Builder attach(EmailAttachment attachment) {
            this.attachments.add(attachment);
            return this;
        }
        
        public Builder priority(Priority priority) {
            this.priority = priority;
            return this;
        }
        
        public Builder lowPriority() {
            this.priority = Priority.LOW;
            return this;
        }
        
        public Builder highPriority() {
            this.priority = Priority.HIGH;
            return this;
        }
        
        public Builder urgent() {
            this.priority = Priority.URGENT;
            return this;
        }
        
        public Builder requestReadReceipt() {
            this.isReadReceiptRequested = true;
            return this;
        }
        
        public Builder requestDeliveryReceipt() {
            this.isDeliveryReceiptRequested = true;
            return this;
        }
        
        public Builder scheduleFor(LocalDateTime scheduledTime) {
            this.scheduledTime = scheduledTime;
            return this;
        }
        
        public Builder scheduleForLater(int hours) {
            this.scheduledTime = LocalDateTime.now().plusHours(hours);
            return this;
        }
        
        public Builder addHeader(String name, String value) {
            this.customHeaders.put(name, value);
            return this;
        }
        
        public Builder replyTo(String replyTo) {
            this.replyTo = replyTo;
            return this;
        }
        
        public Builder sender(String sender) {
            this.sender = sender;
            return this;
        }
        
        public Builder template(EmailTemplate template) {
            this.template = template;
            return this;
        }
        
        public Builder templateVariable(String key, String value) {
            this.templateVariables.put(key, value);
            return this;
        }
        
        public Builder templateVariables(Map<String, String> variables) {
            this.templateVariables.putAll(variables);
            return this;
        }
        
        // Convenience methods for common email types
        public Builder welcomeEmail(String recipientName, String recipientEmail, String companyName) {
            return this.template(EmailTemplate.WELCOME)
                      .to(recipientEmail)
                      .subject("Welcome to " + companyName + "!")
                      .templateVariable("name", recipientName)
                      .templateVariable("email", recipientEmail)
                      .templateVariable("company", companyName)
                      .templateVariable("accountId", UUID.randomUUID().toString().substring(0, 8));
        }
        
        public Builder notificationEmail(String title, String message) {
            return this.template(EmailTemplate.NOTIFICATION)
                      .subject(title)
                      .templateVariable("title", title)
                      .templateVariable("message", message)
                      .templateVariable("timestamp", LocalDateTime.now().format(DateTimeFormatter.RFC_1123_DATE_TIME));
        }
        
        public Builder newsletterEmail(String title, String subscriberName, String content) {
            return this.template(EmailTemplate.NEWSLETTER)
                      .subject(title)
                      .templateVariable("newsletterTitle", title)
                      .templateVariable("subscriberName", subscriberName)
                      .templateVariable("content", content)
                      .templateVariable("unsubscribeLink", "https://example.com/unsubscribe");
        }
        
        public Builder marketingEmail(String recipientEmail) {
            return this.to(recipientEmail)
                      .template(EmailTemplate.MARKETING)
                      .addHeader("List-Unsubscribe", "<mailto:unsubscribe@example.com>")
                      .addHeader("X-Marketing-Campaign", "true");
        }
        
        public Builder transactionalEmail() {
            return this.priority(Priority.HIGH)
                      .addHeader("X-Email-Type", "transactional");
        }
        
        public Builder bulkEmail(List<String> recipients) {
            return this.bcc(recipients)
                      .addHeader("Precedence", "bulk")
                      .addHeader("X-Auto-Response-Suppress", "All");
        }
        
        // Preset configurations
        public Builder customerServiceDefaults() {
            return this.replyTo("support@example.com")
                      .addHeader("X-Department", "Customer Service")
                      .requestDeliveryReceipt();
        }
        
        public Builder securityAlertDefaults() {
            return this.urgent()
                      .addHeader("X-Email-Type", "security")
                      .addHeader("X-Priority", "1")
                      .requestReadReceipt();
        }
        
        public Email build() {
            return new Email(this);
        }
    }
    
    // Getters
    public String getFrom() { return from; }
    public List<String> getTo() { return new ArrayList<>(to); }
    public List<String> getCc() { return new ArrayList<>(cc); }
    public List<String> getBcc() { return new ArrayList<>(bcc); }
    public String getSubject() { return subject; }
    public String getTextBody() { return textBody; }
    public String getHtmlBody() { return htmlBody; }
    public List<EmailAttachment> getAttachments() { return new ArrayList<>(attachments); }
    public Priority getPriority() { return priority; }
    public boolean isReadReceiptRequested() { return isReadReceiptRequested; }
    public boolean isDeliveryReceiptRequested() { return isDeliveryReceiptRequested; }
    public LocalDateTime getScheduledTime() { return scheduledTime; }
    public Map<String, String> getCustomHeaders() { return new HashMap<>(customHeaders); }
    public String getReplyTo() { return replyTo; }
    public String getSender() { return sender; }
    public EmailTemplate getTemplate() { return template; }
    public Map<String, String> getTemplateVariables() { return new HashMap<>(templateVariables); }
    
    public boolean isScheduled() {
        return scheduledTime != null && scheduledTime.isAfter(LocalDateTime.now());
    }
    
    public long getTotalAttachmentSize() {
        return attachments.stream().mapToLong(EmailAttachment::getSize).sum();
    }
    
    public int getTotalRecipients() {
        return to.size() + cc.size() + bcc.size();
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Email Message:\n");
        sb.append("├── From: ").append(from).append("\n");
        sb.append("├── To: ").append(String.join(", ", to)).append("\n");
        
        if (!cc.isEmpty()) {
            sb.append("├── CC: ").append(String.join(", ", cc)).append("\n");
        }
        if (!bcc.isEmpty()) {
            sb.append("├── BCC: ").append(String.join(", ", bcc)).append("\n");
        }
        if (replyTo != null) {
            sb.append("├── Reply-To: ").append(replyTo).append("\n");
        }
        
        sb.append("├── Subject: ").append(subject).append("\n");
        sb.append("├── Priority: ").append(priority).append("\n");
        
        if (!attachments.isEmpty()) {
            sb.append("├── Attachments (").append(attachments.size()).append("):\n");
            for (int i = 0; i < attachments.size(); i++) {
                sb.append("│   ├── ").append(attachments.get(i)).append("\n");
            }
        }
        
        if (scheduledTime != null) {
            sb.append("├── Scheduled: ").append(scheduledTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)).append("\n");
        }
        
        if (template != null) {
            sb.append("├── Template: ").append(template).append("\n");
        }
        
        if (!customHeaders.isEmpty()) {
            sb.append("├── Custom Headers: ").append(customHeaders.size()).append("\n");
        }
        
        sb.append("├── Total Recipients: ").append(getTotalRecipients()).append("\n");
        sb.append("├── Total Attachment Size: ").append(String.format("%.2f KB", getTotalAttachmentSize() / 1024.0)).append("\n");
        sb.append("└── Read Receipt: ").append(isReadReceiptRequested ? "Requested" : "Not Requested");
        
        return sb.toString();
    }
}

// Email Director for common patterns
class EmailDirector {
    
    public Email createWelcomeEmail(String from, String to, String name, String company) {
        return Email.builder()
                .from(from)
                .welcomeEmail(name, to, company)
                .build();
    }
    
    public Email createPasswordResetEmail(String from, String to, String resetLink) {
        return Email.builder()
                .from(from)
                .to(to)
                .subject("Password Reset Request")
                .htmlBody("<html><body><h2>Password Reset</h2><p>Click <a href='" + resetLink + "'>here</a> to reset your password.</p></body></html>")
                .securityAlertDefaults()
                .build();
    }
    
    public Email createInvoiceEmail(String from, String to, String invoiceNumber, EmailAttachment invoice) {
        return Email.builder()
                .from(from)
                .to(to)
                .subject("Invoice " + invoiceNumber)
                .htmlBody("<html><body><h2>Invoice " + invoiceNumber + "</h2><p>Please find your invoice attached.</p></body></html>")
                .attach(invoice)
                .transactionalEmail()
                .build();
    }
    
    public Email createNewsletterEmail(String from, List<String> subscribers, String title, String content) {
        return Email.builder()
                .from(from)
                .bulkEmail(subscribers)
                .newsletterEmail(title, "Valued Subscriber", content)
                .build();
    }
}

// Demonstration class
public class EmailBuilderExample {
    
    public static void main(String[] args) {
        System.out.println("=== Builder Pattern Demonstration - Email Builder System ===\n");
        
        // Example 1: Simple email
        System.out.println("1. Simple Email:");
        Email simpleEmail = Email.builder()
                .from("sender@example.com")
                .to("recipient@example.com")
                .subject("Hello World")
                .body("This is a simple text email.")
                .build();
        
        System.out.println(simpleEmail);
        System.out.println();
        
        // Example 2: Complex email with all features
        System.out.println("2. Complex Email with Multiple Recipients and Attachments:");
        
        // Create sample attachments
        EmailAttachment document = new EmailAttachment("document.pdf", "application/pdf", "PDF content".getBytes());
        EmailAttachment image = new EmailAttachment("image.jpg", "image/jpeg", "JPEG data".getBytes());
        
        Email complexEmail = Email.builder()
                .from("marketing@company.com")
                .to("customer1@example.com", "customer2@example.com")
                .cc("manager@company.com")
                .bcc("analytics@company.com")
                .subject("Monthly Newsletter - Important Updates")
                .htmlBody("<html><body><h1>Newsletter</h1><p>Important updates this month...</p></body></html>")
                .attach(document)
                .attach(image)
                .highPriority()
                .requestReadReceipt()
                .replyTo("noreply@company.com")
                .addHeader("X-Campaign-ID", "NEWSLETTER-2024-01")
                .scheduleForLater(2)
                .build();
        
        System.out.println(complexEmail);
        System.out.println();
        
        // Example 3: Welcome email using template
        System.out.println("3. Welcome Email Using Template:");
        Email welcomeEmail = Email.builder()
                .from("welcome@company.com")
                .welcomeEmail("John Doe", "john.doe@example.com", "TechCorp")
                .build();
        
        System.out.println(welcomeEmail);
        System.out.println("Rendered HTML Body:");
        System.out.println(welcomeEmail.getRenderedBody());
        System.out.println();
        
        // Example 4: Notification email
        System.out.println("4. Notification Email:");
        Email notificationEmail = Email.builder()
                .from("notifications@app.com")
                .to("user@example.com")
                .notificationEmail("System Maintenance Scheduled", 
                                 "Our system will undergo maintenance on Sunday from 2 AM to 4 AM EST.")
                .build();
        
        System.out.println(notificationEmail);
        System.out.println();
        
        // Example 5: Bulk marketing email
        System.out.println("5. Bulk Marketing Email:");
        List<String> subscribers = Arrays.asList(
            "subscriber1@example.com",
            "subscriber2@example.com", 
            "subscriber3@example.com"
        );
        
        Email bulkEmail = Email.builder()
                .from("marketing@store.com")
                .bulkEmail(subscribers)
                .subject("Special Offer - 50% Off!")
                .htmlBody("<html><body><h1>Limited Time Offer!</h1><p>Get 50% off all items this weekend!</p></body></html>")
                .marketingEmail("marketing@store.com")
                .build();
        
        System.out.println(bulkEmail);
        System.out.println();
        
        // Example 6: Security alert email
        System.out.println("6. Security Alert Email:");
        Email securityEmail = Email.builder()
                .from("security@bank.com")
                .to("customer@example.com")
                .subject("URGENT: Suspicious Activity Detected")
                .htmlBody("<html><body><h2 style='color:red;'>Security Alert</h2><p>We detected unusual activity on your account.</p></body></html>")
                .securityAlertDefaults()
                .build();
        
        System.out.println(securityEmail);
        System.out.println();
        
        // Example 7: Using EmailDirector
        System.out.println("7. Using EmailDirector for Common Patterns:");
        EmailDirector director = new EmailDirector();
        
        Email directorWelcome = director.createWelcomeEmail(
            "onboarding@company.com", 
            "newuser@example.com", 
            "Jane Smith", 
            "Amazing Corp"
        );
        System.out.println("Director Welcome Email:");
        System.out.println(directorWelcome);
        System.out.println();
        
        Email passwordReset = director.createPasswordResetEmail(
            "security@company.com",
            "user@example.com", 
            "https://company.com/reset?token=abc123"
        );
        System.out.println("Password Reset Email:");
        System.out.println(passwordReset);
        System.out.println();
        
        // Example 8: Newsletter with template
        System.out.println("8. Newsletter with Template:");
        Email newsletter = Email.builder()
                .from("newsletter@magazine.com")
                .newsletterEmail("Tech Weekly - Issue #42", "Tech Enthusiast", 
                               "<h3>This Week's Highlights</h3><ul><li>AI Breakthrough</li><li>New Framework Released</li></ul>")
                .to("subscriber@example.com")
                .build();
        
        System.out.println(newsletter);
        System.out.println("Rendered Newsletter Body:");
        System.out.println(newsletter.getRenderedBody());
        System.out.println();
        
        // Example 9: Error handling
        System.out.println("9. Error Handling Demonstration:");
        try {
            Email invalidEmail = Email.builder()
                    .from("invalid-email")
                    .to("recipient@example.com")
                    .subject("Test")
                    .body("Test body")
                    .build();
        } catch (IllegalStateException e) {
            System.out.println("Caught expected error: " + e.getMessage());
        }
        
        try {
            Email noRecipientEmail = Email.builder()
                    .from("sender@example.com")
                    .subject("Test")
                    .body("Test body")
                    .build();
        } catch (IllegalStateException e) {
            System.out.println("Caught expected error: " + e.getMessage());
        }
        System.out.println();
        
        // Example 10: Scheduled email
        System.out.println("10. Scheduled Email:");
        Email scheduledEmail = Email.builder()
                .from("scheduler@example.com")
                .to("recipient@example.com")
                .subject("Scheduled Message")
                .body("This message was scheduled to be sent later.")
                .scheduleFor(LocalDateTime.now().plusHours(24))
                .build();
        
        System.out.println(scheduledEmail);
        System.out.println("Is scheduled: " + scheduledEmail.isScheduled());
        System.out.println();
        
        System.out.println("=== Demonstration Complete ===");
    }
}