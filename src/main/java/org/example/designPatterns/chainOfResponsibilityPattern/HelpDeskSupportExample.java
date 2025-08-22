package org.example.designPatterns.chainOfResponsibilityPattern;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Help Desk Support System Example demonstrating Chain of Responsibility Pattern
 * 
 * This example shows how support tickets are routed through different
 * support levels based on priority and complexity.
 */

// Request object
class SupportTicket {
    public enum Priority {
        LOW, MEDIUM, HIGH, CRITICAL
    }
    
    public enum Category {
        GENERAL, TECHNICAL, BILLING, SECURITY
    }
    
    public enum Status {
        OPEN, IN_PROGRESS, RESOLVED, ESCALATED
    }
    
    private int ticketId;
    private String customerName;
    private String description;
    private Priority priority;
    private Category category;
    private Status status;
    private LocalDateTime createdAt;
    private List<String> resolutionLog;
    
    public SupportTicket(int ticketId, String customerName, String description, 
                        Priority priority, Category category) {
        this.ticketId = ticketId;
        this.customerName = customerName;
        this.description = description;
        this.priority = priority;
        this.category = category;
        this.status = Status.OPEN;
        this.createdAt = LocalDateTime.now();
        this.resolutionLog = new ArrayList<>();
    }
    
    public void addLogEntry(String entry) {
        resolutionLog.add(String.format("[%s] %s", 
                        LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")), entry));
    }
    
    // Getters and setters
    public int getTicketId() { return ticketId; }
    public String getCustomerName() { return customerName; }
    public String getDescription() { return description; }
    public Priority getPriority() { return priority; }
    public Category getCategory() { return category; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public List<String> getResolutionLog() { return new ArrayList<>(resolutionLog); }
    
    @Override
    public String toString() {
        return String.format("Ticket #%d: %s [%s/%s] - %s", 
                           ticketId, customerName, priority, category, description);
    }
}

// Abstract Handler
abstract class SupportHandler {
    protected SupportHandler nextHandler;
    protected String handlerName;
    protected List<SupportTicket.Priority> supportedPriorities;
    protected List<SupportTicket.Category> supportedCategories;
    
    public SupportHandler(String handlerName) {
        this.handlerName = handlerName;
        this.supportedPriorities = new ArrayList<>();
        this.supportedCategories = new ArrayList<>();
    }
    
    public SupportHandler setNext(SupportHandler nextHandler) {
        this.nextHandler = nextHandler;
        return nextHandler;
    }
    
    public final void handleTicket(SupportTicket ticket) {
        System.out.println("\n--- " + handlerName + " reviewing ticket ---");
        System.out.println("Ticket: " + ticket);
        
        if (canHandle(ticket)) {
            processTicket(ticket);
        } else if (nextHandler != null) {
            System.out.println(handlerName + " cannot handle this ticket. Escalating...");
            ticket.addLogEntry("Escalated from " + handlerName);
            nextHandler.handleTicket(ticket);
        } else {
            System.out.println("No handler available for ticket: " + ticket);
            ticket.setStatus(SupportTicket.Status.ESCALATED);
            ticket.addLogEntry("No suitable handler found - requires manual intervention");
        }
    }
    
    protected abstract boolean canHandle(SupportTicket ticket);
    protected abstract void processTicket(SupportTicket ticket);
    
    protected boolean isPrioritySupported(SupportTicket.Priority priority) {
        return supportedPriorities.isEmpty() || supportedPriorities.contains(priority);
    }
    
    protected boolean isCategorySupported(SupportTicket.Category category) {
        return supportedCategories.isEmpty() || supportedCategories.contains(category);
    }
    
    public String getHandlerInfo() {
        return String.format("%s - Priorities: %s, Categories: %s", 
                           handlerName, supportedPriorities, supportedCategories);
    }
}

// Concrete Handler - Level 1 Support (Junior)
class Level1SupportHandler extends SupportHandler {
    
    public Level1SupportHandler() {
        super("Level 1 Support (Junior)");
        supportedPriorities.addAll(Arrays.asList(
            SupportTicket.Priority.LOW, 
            SupportTicket.Priority.MEDIUM
        ));
        supportedCategories.addAll(Arrays.asList(
            SupportTicket.Category.GENERAL,
            SupportTicket.Category.BILLING
        ));
    }
    
    @Override
    protected boolean canHandle(SupportTicket ticket) {
        return isPrioritySupported(ticket.getPriority()) && 
               isCategorySupported(ticket.getCategory()) &&
               !isComplexIssue(ticket);
    }
    
    @Override
    protected void processTicket(SupportTicket ticket) {
        System.out.println(handlerName + " processing ticket...");
        ticket.setStatus(SupportTicket.Status.IN_PROGRESS);
        ticket.addLogEntry("Assigned to " + handlerName);
        
        // Simulate processing time
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Resolve common issues
        if (ticket.getCategory() == SupportTicket.Category.GENERAL) {
            ticket.addLogEntry("Provided standard FAQ response");
            ticket.setStatus(SupportTicket.Status.RESOLVED);
            System.out.println("✓ Ticket resolved by " + handlerName);
        } else if (ticket.getCategory() == SupportTicket.Category.BILLING) {
            ticket.addLogEntry("Verified account details and resolved billing query");
            ticket.setStatus(SupportTicket.Status.RESOLVED);
            System.out.println("✓ Ticket resolved by " + handlerName);
        }
    }
    
    private boolean isComplexIssue(SupportTicket ticket) {
        String description = ticket.getDescription().toLowerCase();
        return description.contains("bug") || description.contains("error") || 
               description.contains("crash") || description.contains("security");
    }
}

// Concrete Handler - Level 2 Support (Senior)
class Level2SupportHandler extends SupportHandler {
    
    public Level2SupportHandler() {
        super("Level 2 Support (Senior)");
        supportedPriorities.addAll(Arrays.asList(
            SupportTicket.Priority.MEDIUM, 
            SupportTicket.Priority.HIGH
        ));
        supportedCategories.addAll(Arrays.asList(
            SupportTicket.Category.TECHNICAL,
            SupportTicket.Category.GENERAL
        ));
    }
    
    @Override
    protected boolean canHandle(SupportTicket ticket) {
        return isPrioritySupported(ticket.getPriority()) && 
               isCategorySupported(ticket.getCategory());
    }
    
    @Override
    protected void processTicket(SupportTicket ticket) {
        System.out.println(handlerName + " analyzing technical issue...");
        ticket.setStatus(SupportTicket.Status.IN_PROGRESS);
        ticket.addLogEntry("Assigned to " + handlerName);
        
        // Simulate technical analysis
        try {
            Thread.sleep(800);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        String description = ticket.getDescription().toLowerCase();
        if (description.contains("password") || description.contains("login")) {
            ticket.addLogEntry("Reset user credentials and provided access instructions");
            ticket.setStatus(SupportTicket.Status.RESOLVED);
            System.out.println("✓ Ticket resolved by " + handlerName);
        } else if (description.contains("configuration")) {
            ticket.addLogEntry("Updated system configuration and tested functionality");
            ticket.setStatus(SupportTicket.Status.RESOLVED);
            System.out.println("✓ Ticket resolved by " + handlerName);
        } else {
            System.out.println(handlerName + " requires escalation for complex technical issue");
            if (nextHandler != null) {
                ticket.addLogEntry("Technical complexity requires specialist attention");
            }
        }
    }
}

// Concrete Handler - Security Specialist
class SecuritySpecialistHandler extends SupportHandler {
    
    public SecuritySpecialistHandler() {
        super("Security Specialist");
        supportedPriorities.addAll(Arrays.asList(
            SupportTicket.Priority.HIGH, 
            SupportTicket.Priority.CRITICAL
        ));
        supportedCategories.add(SupportTicket.Category.SECURITY);
    }
    
    @Override
    protected boolean canHandle(SupportTicket ticket) {
        return isPrioritySupported(ticket.getPriority()) && 
               isCategorySupported(ticket.getCategory());
    }
    
    @Override
    protected void processTicket(SupportTicket ticket) {
        System.out.println(handlerName + " conducting security analysis...");
        ticket.setStatus(SupportTicket.Status.IN_PROGRESS);
        ticket.addLogEntry("Assigned to " + handlerName);
        
        // Simulate security investigation
        try {
            Thread.sleep(1200);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        ticket.addLogEntry("Conducted security audit and implemented protective measures");
        ticket.addLogEntry("Verified system integrity and updated security protocols");
        ticket.setStatus(SupportTicket.Status.RESOLVED);
        System.out.println("✓ Security issue resolved by " + handlerName);
    }
}

// Concrete Handler - Engineering Team
class EngineeringTeamHandler extends SupportHandler {
    
    public EngineeringTeamHandler() {
        super("Engineering Team");
        supportedPriorities.add(SupportTicket.Priority.CRITICAL);
        supportedCategories.addAll(Arrays.asList(
            SupportTicket.Category.TECHNICAL,
            SupportTicket.Category.SECURITY
        ));
    }
    
    @Override
    protected boolean canHandle(SupportTicket ticket) {
        return isPrioritySupported(ticket.getPriority()) && 
               isCategorySupported(ticket.getCategory());
    }
    
    @Override
    protected void processTicket(SupportTicket ticket) {
        System.out.println(handlerName + " investigating critical system issue...");
        ticket.setStatus(SupportTicket.Status.IN_PROGRESS);
        ticket.addLogEntry("Escalated to " + handlerName);
        
        // Simulate engineering investigation
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        ticket.addLogEntry("Root cause analysis completed");
        ticket.addLogEntry("Developed and deployed fix");
        ticket.addLogEntry("Performed comprehensive testing");
        ticket.setStatus(SupportTicket.Status.RESOLVED);
        System.out.println("✓ Critical issue resolved by " + handlerName);
    }
}

// Support System Client
class SupportSystem {
    private SupportHandler handlerChain;
    private List<SupportTicket> tickets;
    private int nextTicketId;
    
    public SupportSystem() {
        this.tickets = new ArrayList<>();
        this.nextTicketId = 1000;
        setupHandlerChain();
    }
    
    private void setupHandlerChain() {
        // Create handlers
        Level1SupportHandler level1 = new Level1SupportHandler();
        Level2SupportHandler level2 = new Level2SupportHandler();
        SecuritySpecialistHandler security = new SecuritySpecialistHandler();
        EngineeringTeamHandler engineering = new EngineeringTeamHandler();
        
        // Build the chain
        handlerChain = level1;
        level1.setNext(level2)
              .setNext(security)
              .setNext(engineering);
        
        System.out.println("=== Support System Initialized ===");
        System.out.println("Handler Chain:");
        SupportHandler current = handlerChain;
        int level = 1;
        while (current != null) {
            System.out.println(level + ". " + current.getHandlerInfo());
            current = current.nextHandler;
            level++;
        }
    }
    
    public SupportTicket createTicket(String customerName, String description,
                                    SupportTicket.Priority priority, 
                                    SupportTicket.Category category) {
        SupportTicket ticket = new SupportTicket(nextTicketId++, customerName, 
                                                description, priority, category);
        tickets.add(ticket);
        ticket.addLogEntry("Ticket created");
        return ticket;
    }
    
    public void processTicket(SupportTicket ticket) {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("Processing: " + ticket);
        System.out.println("=".repeat(50));
        
        handlerChain.handleTicket(ticket);
        
        System.out.println("\n--- Final Status ---");
        System.out.println("Status: " + ticket.getStatus());
        System.out.println("Resolution Log:");
        for (String logEntry : ticket.getResolutionLog()) {
            System.out.println("  " + logEntry);
        }
    }
    
    public void printSystemStatistics() {
        System.out.println("\n=== Support System Statistics ===");
        Map<SupportTicket.Status, Integer> statusCounts = new HashMap<>();
        Map<SupportTicket.Priority, Integer> priorityCounts = new HashMap<>();
        
        for (SupportTicket ticket : tickets) {
            statusCounts.put(ticket.getStatus(), 
                           statusCounts.getOrDefault(ticket.getStatus(), 0) + 1);
            priorityCounts.put(ticket.getPriority(), 
                             priorityCounts.getOrDefault(ticket.getPriority(), 0) + 1);
        }
        
        System.out.println("Total tickets: " + tickets.size());
        System.out.println("Status breakdown: " + statusCounts);
        System.out.println("Priority breakdown: " + priorityCounts);
    }
}

// Client code
public class HelpDeskSupportExample {
    public static void main(String[] args) {
        System.out.println("=== Chain of Responsibility Pattern: Help Desk Support ===\n");
        
        SupportSystem supportSystem = new SupportSystem();
        
        // Create various support tickets
        List<SupportTicket> tickets = Arrays.asList(
            supportSystem.createTicket("John Doe", "How do I change my password?", 
                                     SupportTicket.Priority.LOW, SupportTicket.Category.GENERAL),
            
            supportSystem.createTicket("Jane Smith", "Billing discrepancy on my account", 
                                     SupportTicket.Priority.MEDIUM, SupportTicket.Category.BILLING),
            
            supportSystem.createTicket("Bob Wilson", "Application crashes when uploading files", 
                                     SupportTicket.Priority.HIGH, SupportTicket.Category.TECHNICAL),
            
            supportSystem.createTicket("Alice Brown", "Suspicious login attempts detected", 
                                     SupportTicket.Priority.CRITICAL, SupportTicket.Category.SECURITY),
            
            supportSystem.createTicket("Charlie Davis", "System-wide performance degradation", 
                                     SupportTicket.Priority.CRITICAL, SupportTicket.Category.TECHNICAL),
            
            supportSystem.createTicket("Eve Wilson", "Cannot access my account", 
                                     SupportTicket.Priority.MEDIUM, SupportTicket.Category.TECHNICAL)
        );
        
        // Process each ticket through the chain
        for (SupportTicket ticket : tickets) {
            supportSystem.processTicket(ticket);
            
            // Pause between tickets for readability
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        // Show system statistics
        supportSystem.printSystemStatistics();
        
        System.out.println("\n=== Chain of Responsibility Benefits Demonstrated ===");
        System.out.println("1. Automatic Routing - Tickets automatically routed to appropriate handler");
        System.out.println("2. Scalable Processing - Easy to add/remove handlers without changing client code");
        System.out.println("3. Specialized Handling - Each handler focuses on specific ticket types");
        System.out.println("4. Escalation Support - Unhandled tickets automatically escalated");
        System.out.println("5. Flexible Chain - Handler order and capabilities easily configurable");
        System.out.println("6. Decoupled System - Ticket creators don't need to know handler details");
    }
}