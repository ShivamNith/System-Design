package org.example.designPatterns.chainOfResponsibilityPattern;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Expense Approval System Example demonstrating Chain of Responsibility Pattern
 * 
 * This example shows how expense requests flow through different approval
 * levels based on amount and employee hierarchy.
 */

// Employee class for context
class Employee {
    private int employeeId;
    private String name;
    private String department;
    private String role;
    private int managerId;
    
    public Employee(int employeeId, String name, String department, String role, int managerId) {
        this.employeeId = employeeId;
        this.name = name;
        this.department = department;
        this.role = role;
        this.managerId = managerId;
    }
    
    // Getters
    public int getEmployeeId() { return employeeId; }
    public String getName() { return name; }
    public String getDepartment() { return department; }
    public String getRole() { return role; }
    public int getManagerId() { return managerId; }
    
    @Override
    public String toString() {
        return String.format("%s (%s, %s)", name, role, department);
    }
}

// Request object
class ExpenseRequest {
    public enum Status {
        PENDING, APPROVED, REJECTED, REQUIRES_JUSTIFICATION
    }
    
    public enum Category {
        TRAVEL, MEALS, SUPPLIES, TRAINING, EQUIPMENT, OTHER
    }
    
    private int requestId;
    private Employee employee;
    private double amount;
    private Category category;
    private String description;
    private String businessJustification;
    private Status status;
    private LocalDateTime submittedAt;
    private List<String> approvalHistory;
    private String rejectionReason;
    
    public ExpenseRequest(int requestId, Employee employee, double amount, 
                         Category category, String description, String businessJustification) {
        this.requestId = requestId;
        this.employee = employee;
        this.amount = amount;
        this.category = category;
        this.description = description;
        this.businessJustification = businessJustification;
        this.status = Status.PENDING;
        this.submittedAt = LocalDateTime.now();
        this.approvalHistory = new ArrayList<>();
    }
    
    public void addApprovalStep(String step) {
        approvalHistory.add(String.format("[%s] %s", 
                          LocalDateTime.now().toString().substring(11, 19), step));
    }
    
    // Getters and setters
    public int getRequestId() { return requestId; }
    public Employee getEmployee() { return employee; }
    public double getAmount() { return amount; }
    public Category getCategory() { return category; }
    public String getDescription() { return description; }
    public String getBusinessJustification() { return businessJustification; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public List<String> getApprovalHistory() { return new ArrayList<>(approvalHistory); }
    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }
    
    @Override
    public String toString() {
        return String.format("Expense #%d: $%.2f (%s) by %s - %s", 
                           requestId, amount, category, employee.getName(), description);
    }
}

// Abstract Handler
abstract class ApprovalHandler {
    protected ApprovalHandler nextHandler;
    protected String approverTitle;
    protected double approvalLimit;
    
    public ApprovalHandler(String approverTitle, double approvalLimit) {
        this.approverTitle = approverTitle;
        this.approvalLimit = approvalLimit;
    }
    
    public ApprovalHandler setNext(ApprovalHandler nextHandler) {
        this.nextHandler = nextHandler;
        return nextHandler;
    }
    
    public final void processRequest(ExpenseRequest request) {
        System.out.println("\n--- " + approverTitle + " reviewing request ---");
        System.out.println("Request: " + request);
        System.out.println("Amount: $" + String.format("%.2f", request.getAmount()));
        System.out.println("Approval limit: $" + String.format("%.2f", approvalLimit));
        
        if (canApprove(request)) {
            if (shouldApprove(request)) {
                approveRequest(request);
            } else {
                rejectRequest(request);
            }
        } else if (nextHandler != null) {
            System.out.println(approverTitle + " cannot approve this amount. Escalating...");
            request.addApprovalStep("Escalated from " + approverTitle + " (exceeds $" + 
                                  String.format("%.2f", approvalLimit) + " limit)");
            nextHandler.processRequest(request);
        } else {
            System.out.println("No higher authority available for approval.");
            request.setStatus(ExpenseRequest.Status.REJECTED);
            request.setRejectionReason("Amount exceeds all approval limits");
            request.addApprovalStep("Rejected - exceeds maximum approval authority");
        }
    }
    
    protected boolean canApprove(ExpenseRequest request) {
        return request.getAmount() <= approvalLimit;
    }
    
    protected abstract boolean shouldApprove(ExpenseRequest request);
    
    protected void approveRequest(ExpenseRequest request) {
        request.setStatus(ExpenseRequest.Status.APPROVED);
        request.addApprovalStep("Approved by " + approverTitle);
        System.out.println("✓ Request APPROVED by " + approverTitle);
    }
    
    protected void rejectRequest(ExpenseRequest request) {
        request.setStatus(ExpenseRequest.Status.REJECTED);
        request.addApprovalStep("Rejected by " + approverTitle);
        System.out.println("✗ Request REJECTED by " + approverTitle);
    }
    
    public String getApproverInfo() {
        return String.format("%s (limit: $%.2f)", approverTitle, approvalLimit);
    }
}

// Concrete Handler - Team Lead
class TeamLeadHandler extends ApprovalHandler {
    
    public TeamLeadHandler() {
        super("Team Lead", 500.0);
    }
    
    @Override
    protected boolean shouldApprove(ExpenseRequest request) {
        // Team leads can approve routine expenses
        if (request.getCategory() == ExpenseRequest.Category.SUPPLIES ||
            request.getCategory() == ExpenseRequest.Category.MEALS) {
            return hasValidJustification(request);
        }
        
        // More scrutiny for other categories
        if (request.getAmount() > 200 && 
            (request.getBusinessJustification() == null || 
             request.getBusinessJustification().trim().length() < 20)) {
            request.setRejectionReason("Insufficient business justification for amount over $200");
            return false;
        }
        
        return hasValidJustification(request);
    }
    
    private boolean hasValidJustification(ExpenseRequest request) {
        return request.getBusinessJustification() != null && 
               !request.getBusinessJustification().trim().isEmpty();
    }
}

// Concrete Handler - Department Manager
class DepartmentManagerHandler extends ApprovalHandler {
    
    public DepartmentManagerHandler() {
        super("Department Manager", 2000.0);
    }
    
    @Override
    protected boolean shouldApprove(ExpenseRequest request) {
        // Department managers have broader approval authority
        if (request.getCategory() == ExpenseRequest.Category.EQUIPMENT ||
            request.getCategory() == ExpenseRequest.Category.TRAINING) {
            return evaluateBusinessNeed(request);
        }
        
        // Travel expenses require additional validation
        if (request.getCategory() == ExpenseRequest.Category.TRAVEL) {
            return validateTravelExpense(request);
        }
        
        return hasStrongJustification(request);
    }
    
    private boolean evaluateBusinessNeed(ExpenseRequest request) {
        // Simulate business need evaluation
        String justification = request.getBusinessJustification().toLowerCase();
        
        if (justification.contains("productivity") || 
            justification.contains("efficiency") ||
            justification.contains("required") ||
            justification.contains("critical")) {
            return true;
        }
        
        if (request.getAmount() > 1000 && justification.length() < 50) {
            request.setRejectionReason("High-value requests require detailed business justification");
            return false;
        }
        
        return true;
    }
    
    private boolean validateTravelExpense(ExpenseRequest request) {
        String description = request.getDescription().toLowerCase();
        
        if (description.contains("conference") || 
            description.contains("client meeting") ||
            description.contains("training")) {
            return hasStrongJustification(request);
        }
        
        request.setRejectionReason("Travel purpose not clearly business-related");
        return false;
    }
    
    private boolean hasStrongJustification(ExpenseRequest request) {
        return request.getBusinessJustification() != null && 
               request.getBusinessJustification().trim().length() >= 30;
    }
}

// Concrete Handler - Finance Director
class FinanceDirectorHandler extends ApprovalHandler {
    
    public FinanceDirectorHandler() {
        super("Finance Director", 10000.0);
    }
    
    @Override
    protected boolean shouldApprove(ExpenseRequest request) {
        // Finance directors scrutinize high-value expenses carefully
        if (request.getAmount() > 5000) {
            return evaluateHighValueExpense(request);
        }
        
        // Check budget impact for all requests
        return evaluateBudgetImpact(request);
    }
    
    private boolean evaluateHighValueExpense(ExpenseRequest request) {
        String justification = request.getBusinessJustification();
        
        if (justification == null || justification.trim().length() < 100) {
            request.setRejectionReason("High-value expenses require comprehensive business justification (min 100 characters)");
            return false;
        }
        
        // Check for strategic alignment
        String justificationLower = justification.toLowerCase();
        if (justificationLower.contains("strategic") ||
            justificationLower.contains("competitive") ||
            justificationLower.contains("compliance") ||
            justificationLower.contains("revenue")) {
            return true;
        }
        
        request.setRejectionReason("High-value expense lacks strategic business alignment");
        return false;
    }
    
    private boolean evaluateBudgetImpact(ExpenseRequest request) {
        // Simulate budget check (in real system, would check actual budget)
        if (request.getCategory() == ExpenseRequest.Category.EQUIPMENT && 
            request.getAmount() > 3000) {
            // Equipment purchases require budget pre-approval
            String justification = request.getBusinessJustification().toLowerCase();
            if (!justification.contains("budgeted") && !justification.contains("approved")) {
                request.setRejectionReason("Equipment purchases over $3000 require budget pre-approval");
                return false;
            }
        }
        
        return true;
    }
}

// Concrete Handler - CEO
class CEOHandler extends ApprovalHandler {
    
    public CEOHandler() {
        super("CEO", 50000.0);
    }
    
    @Override
    protected boolean shouldApprove(ExpenseRequest request) {
        // CEO reviews only the highest value expenses
        System.out.println("CEO conducting executive review...");
        
        if (request.getAmount() > 25000) {
            return conductExecutiveReview(request);
        }
        
        // For lower amounts, CEO typically approves if it reached this level
        return hasExecutiveJustification(request);
    }
    
    private boolean conductExecutiveReview(ExpenseRequest request) {
        String justification = request.getBusinessJustification().toLowerCase();
        
        // Executive-level criteria
        if (justification.contains("acquisition") ||
            justification.contains("merger") ||
            justification.contains("expansion") ||
            justification.contains("partnership")) {
            return true;
        }
        
        request.setRejectionReason("Executive-level expenses must align with strategic corporate initiatives");
        return false;
    }
    
    private boolean hasExecutiveJustification(ExpenseRequest request) {
        // If it reached CEO level, typically has been well-vetted
        return request.getBusinessJustification() != null && 
               request.getBusinessJustification().trim().length() >= 50;
    }
}

// Expense System Client
class ExpenseApprovalSystem {
    private ApprovalHandler approvalChain;
    private List<ExpenseRequest> requests;
    private int nextRequestId;
    
    public ExpenseApprovalSystem() {
        this.requests = new ArrayList<>();
        this.nextRequestId = 1001;
        setupApprovalChain();
    }
    
    private void setupApprovalChain() {
        // Create approval handlers
        TeamLeadHandler teamLead = new TeamLeadHandler();
        DepartmentManagerHandler manager = new DepartmentManagerHandler();
        FinanceDirectorHandler financeDirector = new FinanceDirectorHandler();
        CEOHandler ceo = new CEOHandler();
        
        // Build the chain
        approvalChain = teamLead;
        teamLead.setNext(manager)
                .setNext(financeDirector)
                .setNext(ceo);
        
        System.out.println("=== Expense Approval System Initialized ===");
        System.out.println("Approval Chain:");
        ApprovalHandler current = approvalChain;
        int level = 1;
        while (current != null) {
            System.out.println(level + ". " + current.getApproverInfo());
            current = current.nextHandler;
            level++;
        }
    }
    
    public ExpenseRequest createExpenseRequest(Employee employee, double amount,
                                             ExpenseRequest.Category category,
                                             String description, String justification) {
        ExpenseRequest request = new ExpenseRequest(nextRequestId++, employee, amount,
                                                  category, description, justification);
        requests.add(request);
        request.addApprovalStep("Request submitted by " + employee.getName());
        return request;
    }
    
    public void processExpenseRequest(ExpenseRequest request) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("Processing: " + request);
        System.out.println("Business Justification: " + request.getBusinessJustification());
        System.out.println("=".repeat(60));
        
        approvalChain.processRequest(request);
        
        System.out.println("\n--- Final Decision ---");
        System.out.println("Status: " + request.getStatus());
        if (request.getRejectionReason() != null) {
            System.out.println("Rejection Reason: " + request.getRejectionReason());
        }
        
        System.out.println("Approval History:");
        for (String step : request.getApprovalHistory()) {
            System.out.println("  " + step);
        }
    }
    
    public void printSystemStatistics() {
        System.out.println("\n=== Expense System Statistics ===");
        Map<ExpenseRequest.Status, Integer> statusCounts = new HashMap<>();
        Map<ExpenseRequest.Category, Double> categoryTotals = new HashMap<>();
        double totalApproved = 0;
        
        for (ExpenseRequest request : requests) {
            statusCounts.put(request.getStatus(), 
                           statusCounts.getOrDefault(request.getStatus(), 0) + 1);
            
            if (request.getStatus() == ExpenseRequest.Status.APPROVED) {
                totalApproved += request.getAmount();
                categoryTotals.put(request.getCategory(),
                                 categoryTotals.getOrDefault(request.getCategory(), 0.0) + request.getAmount());
            }
        }
        
        System.out.println("Total requests: " + requests.size());
        System.out.println("Status breakdown: " + statusCounts);
        System.out.println("Total approved amount: $" + String.format("%.2f", totalApproved));
        System.out.println("Approved by category: " + categoryTotals);
    }
}

// Client code
public class ExpenseApprovalExample {
    public static void main(String[] args) {
        System.out.println("=== Chain of Responsibility Pattern: Expense Approval ===\n");
        
        ExpenseApprovalSystem expenseSystem = new ExpenseApprovalSystem();
        
        // Create employees
        Employee developer = new Employee(101, "Alice Johnson", "Engineering", "Senior Developer", 201);
        Employee analyst = new Employee(102, "Bob Smith", "Marketing", "Business Analyst", 202);
        Employee manager = new Employee(201, "Carol Brown", "Engineering", "Engineering Manager", 301);
        
        // Create various expense requests
        List<ExpenseRequest> requests = Arrays.asList(
            expenseSystem.createExpenseRequest(developer, 150.0, 
                ExpenseRequest.Category.SUPPLIES, "Development books and software licenses", 
                "Books and tools to improve coding skills and productivity"),
            
            expenseSystem.createExpenseRequest(analyst, 450.0, 
                ExpenseRequest.Category.TRAINING, "Digital Marketing Certification Course", 
                "Required certification for upcoming campaign management responsibilities"),
            
            expenseSystem.createExpenseRequest(developer, 1200.0, 
                ExpenseRequest.Category.EQUIPMENT, "High-resolution monitor for development", 
                "Dual monitor setup needed for productivity improvement and code review efficiency"),
            
            expenseSystem.createExpenseRequest(manager, 3500.0, 
                ExpenseRequest.Category.TRAVEL, "Tech conference attendance", 
                "Strategic conference to learn about new technologies and network with industry leaders for competitive advantage"),
            
            expenseSystem.createExpenseRequest(analyst, 7500.0, 
                ExpenseRequest.Category.EQUIPMENT, "Marketing automation software", 
                "Budgeted software purchase to automate email campaigns and improve lead conversion rates by 25%"),
            
            expenseSystem.createExpenseRequest(manager, 25000.0, 
                ExpenseRequest.Category.OTHER, "Team building retreat", 
                "Strategic team building event to improve collaboration and reduce turnover in critical engineering team"),
            
            expenseSystem.createExpenseRequest(developer, 75.0, 
                ExpenseRequest.Category.MEALS, "Client lunch meeting", 
                "Lunch with potential client to discuss project requirements")
        );
        
        // Process each expense request through the chain
        for (ExpenseRequest request : requests) {
            expenseSystem.processExpenseRequest(request);
            
            // Pause between requests for readability
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        // Show system statistics
        expenseSystem.printSystemStatistics();
        
        System.out.println("\n=== Chain of Responsibility Benefits Demonstrated ===");
        System.out.println("1. Hierarchical Approval - Expenses automatically routed to appropriate approval level");
        System.out.println("2. Flexible Authorization - Different approval limits for different roles");
        System.out.println("3. Consistent Policy - Same approval criteria applied across all requests");
        System.out.println("4. Audit Trail - Complete history of approval decisions and rationale");
        System.out.println("5. Scalable Process - Easy to add new approval levels or modify limits");
        System.out.println("6. Automated Escalation - High-value requests automatically escalated");
    }
}