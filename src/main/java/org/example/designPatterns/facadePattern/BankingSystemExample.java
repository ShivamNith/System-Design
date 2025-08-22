package org.example.designPatterns.facadePattern;

import java.util.*;

/**
 * Banking System Example - Facade Pattern Implementation
 * 
 * This example demonstrates the Facade Pattern by providing a simplified
 * interface for complex banking operations involving account management,
 * security validation, transaction processing, notifications, and audit logging.
 */

// Subsystem components
class AccountManager {
    private Map<String, Double> accounts;
    private Map<String, List<String>> transactionHistory;
    
    public AccountManager() {
        this.accounts = new HashMap<>();
        this.transactionHistory = new HashMap<>();
        initializeAccounts();
    }
    
    private void initializeAccounts() {
        accounts.put("12345", 1000.0);
        accounts.put("67890", 2500.0);
        accounts.put("11111", 500.0);
        
        // Initialize transaction history
        transactionHistory.put("12345", new ArrayList<>());
        transactionHistory.put("67890", new ArrayList<>());
        transactionHistory.put("11111", new ArrayList<>());
    }
    
    public boolean accountExists(String accountNumber) {
        boolean exists = accounts.containsKey(accountNumber);
        System.out.println("AccountManager: Checking account " + accountNumber + " - " + 
                         (exists ? "EXISTS" : "NOT FOUND"));
        return exists;
    }
    
    public double getBalance(String accountNumber) {
        double balance = accounts.getOrDefault(accountNumber, 0.0);
        System.out.println("AccountManager: Account " + accountNumber + " balance: $" + 
                         String.format("%.2f", balance));
        return balance;
    }
    
    public boolean debit(String accountNumber, double amount) {
        if (!accountExists(accountNumber)) return false;
        
        double currentBalance = accounts.get(accountNumber);
        if (currentBalance >= amount) {
            accounts.put(accountNumber, currentBalance - amount);
            addTransactionHistory(accountNumber, "DEBIT: $" + String.format("%.2f", amount));
            System.out.println("AccountManager: Debited $" + String.format("%.2f", amount) + 
                             " from account " + accountNumber);
            return true;
        } else {
            System.out.println("AccountManager: Insufficient funds in account " + accountNumber);
            return false;
        }
    }
    
    public void credit(String accountNumber, double amount) {
        if (!accountExists(accountNumber)) return;
        
        double currentBalance = accounts.get(accountNumber);
        accounts.put(accountNumber, currentBalance + amount);
        addTransactionHistory(accountNumber, "CREDIT: $" + String.format("%.2f", amount));
        System.out.println("AccountManager: Credited $" + String.format("%.2f", amount) + 
                         " to account " + accountNumber);
    }
    
    private void addTransactionHistory(String accountNumber, String transaction) {
        transactionHistory.computeIfAbsent(accountNumber, k -> new ArrayList<>())
                          .add(new Date() + " - " + transaction);
    }
    
    public List<String> getTransactionHistory(String accountNumber) {
        return new ArrayList<>(transactionHistory.getOrDefault(accountNumber, new ArrayList<>()));
    }
}

class SecurityManager {
    private Map<String, String> accountPins;
    private Map<String, Integer> failedAttempts;
    private Set<String> lockedAccounts;
    private static final int MAX_ATTEMPTS = 3;
    
    public SecurityManager() {
        this.accountPins = new HashMap<>();
        this.failedAttempts = new HashMap<>();
        this.lockedAccounts = new HashSet<>();
        initializePins();
    }
    
    private void initializePins() {
        accountPins.put("12345", "1234");
        accountPins.put("67890", "5678");
        accountPins.put("11111", "0000");
    }
    
    public boolean validatePin(String accountNumber, String pin) {
        if (lockedAccounts.contains(accountNumber)) {
            System.out.println("SecurityManager: Account " + accountNumber + " is LOCKED");
            return false;
        }
        
        String correctPin = accountPins.get(accountNumber);
        if (correctPin != null && correctPin.equals(pin)) {
            System.out.println("SecurityManager: PIN validation SUCCESSFUL for account " + accountNumber);
            failedAttempts.put(accountNumber, 0); // Reset failed attempts
            return true;
        } else {
            int attempts = failedAttempts.getOrDefault(accountNumber, 0) + 1;
            failedAttempts.put(accountNumber, attempts);
            
            System.out.println("SecurityManager: PIN validation FAILED for account " + accountNumber + 
                             " (Attempt " + attempts + "/" + MAX_ATTEMPTS + ")");
            
            if (attempts >= MAX_ATTEMPTS) {
                lockedAccounts.add(accountNumber);
                System.out.println("SecurityManager: Account " + accountNumber + " has been LOCKED due to multiple failed attempts");
            }
            
            return false;
        }
    }
    
    public boolean isAccountLocked(String accountNumber) {
        return lockedAccounts.contains(accountNumber);
    }
    
    public void unlockAccount(String accountNumber, String adminKey) {
        if ("ADMIN_OVERRIDE_2023".equals(adminKey)) {
            lockedAccounts.remove(accountNumber);
            failedAttempts.put(accountNumber, 0);
            System.out.println("SecurityManager: Account " + accountNumber + " has been UNLOCKED by admin");
        } else {
            System.out.println("SecurityManager: Invalid admin key provided");
        }
    }
    
    public void logSecurityEvent(String accountNumber, String event) {
        System.out.println("SecurityManager: SECURITY LOG - Account " + accountNumber + ": " + event);
    }
}

class TransactionManager {
    private List<Transaction> transactionLog;
    private int transactionCounter;
    
    public TransactionManager() {
        this.transactionLog = new ArrayList<>();
        this.transactionCounter = 1000;
    }
    
    private static class Transaction {
        String id;
        String type;
        String fromAccount;
        String toAccount;
        double amount;
        Date timestamp;
        String status;
        
        Transaction(String id, String type, String fromAccount, String toAccount, 
                   double amount, String status) {
            this.id = id;
            this.type = type;
            this.fromAccount = fromAccount;
            this.toAccount = toAccount;
            this.amount = amount;
            this.timestamp = new Date();
            this.status = status;
        }
        
        @Override
        public String toString() {
            return String.format("[%s] %s: %s -> %s, $%.2f, Status: %s", 
                               id, type, fromAccount, toAccount, amount, status);
        }
    }
    
    public String createTransaction(String type, String fromAccount, String toAccount, double amount) {
        String transactionId = "TXN" + (++transactionCounter);
        Transaction transaction = new Transaction(transactionId, type, fromAccount, toAccount, amount, "PENDING");
        transactionLog.add(transaction);
        
        System.out.println("TransactionManager: Created transaction " + transactionId);
        return transactionId;
    }
    
    public void completeTransaction(String transactionId) {
        for (Transaction txn : transactionLog) {
            if (txn.id.equals(transactionId)) {
                txn.status = "COMPLETED";
                System.out.println("TransactionManager: Transaction " + transactionId + " completed");
                return;
            }
        }
    }
    
    public void failTransaction(String transactionId, String reason) {
        for (Transaction txn : transactionLog) {
            if (txn.id.equals(transactionId)) {
                txn.status = "FAILED - " + reason;
                System.out.println("TransactionManager: Transaction " + transactionId + " failed: " + reason);
                return;
            }
        }
    }
    
    public List<String> getTransactionHistory() {
        return transactionLog.stream()
                           .map(Transaction::toString)
                           .collect(Collectors.toList());
    }
}

class NotificationService {
    public void sendSMS(String phoneNumber, String message) {
        System.out.println("NotificationService: SMS to " + phoneNumber + ": " + message);
    }
    
    public void sendEmail(String email, String subject, String message) {
        System.out.println("NotificationService: Email to " + email);
        System.out.println("  Subject: " + subject);
        System.out.println("  Message: " + message);
    }
    
    public void sendPushNotification(String deviceId, String message) {
        System.out.println("NotificationService: Push notification to device " + deviceId + ": " + message);
    }
}

class AuditLogger {
    private List<String> auditLog;
    
    public AuditLogger() {
        this.auditLog = new ArrayList<>();
    }
    
    public void log(String event) {
        String logEntry = new Date() + " - " + event;
        auditLog.add(logEntry);
        System.out.println("AuditLogger: " + logEntry);
    }
    
    public void logTransaction(String accountNumber, String operation, double amount) {
        log("Account " + accountNumber + " - " + operation + " $" + String.format("%.2f", amount));
    }
    
    public List<String> getAuditLog() {
        return new ArrayList<>(auditLog);
    }
}

// Facade - Simplifies banking operations
class BankingFacade {
    private AccountManager accountManager;
    private SecurityManager securityManager;
    private TransactionManager transactionManager;
    private NotificationService notificationService;
    private AuditLogger auditLogger;
    
    // Customer contact information
    private Map<String, CustomerInfo> customerInfo;
    
    private static class CustomerInfo {
        String phone;
        String email;
        String deviceId;
        
        CustomerInfo(String phone, String email, String deviceId) {
            this.phone = phone;
            this.email = email;
            this.deviceId = deviceId;
        }
    }
    
    public BankingFacade() {
        this.accountManager = new AccountManager();
        this.securityManager = new SecurityManager();
        this.transactionManager = new TransactionManager();
        this.notificationService = new NotificationService();
        this.auditLogger = new AuditLogger();
        
        initializeCustomerInfo();
    }
    
    private void initializeCustomerInfo() {
        customerInfo = new HashMap<>();
        customerInfo.put("12345", new CustomerInfo("555-0123", "john@email.com", "device123"));
        customerInfo.put("67890", new CustomerInfo("555-0456", "jane@email.com", "device456"));
        customerInfo.put("11111", new CustomerInfo("555-0789", "bob@email.com", "device789"));
    }
    
    /**
     * Simplified login process with security validation and notifications
     */
    public boolean login(String accountNumber, String pin) {
        System.out.println("=== ATM LOGIN ===");
        System.out.println("Account: " + accountNumber);
        
        auditLogger.log("Login attempt for account " + accountNumber);
        
        if (!accountManager.accountExists(accountNumber)) {
            System.out.println("Account not found");
            return false;
        }
        
        if (securityManager.isAccountLocked(accountNumber)) {
            System.out.println("Account is locked");
            return false;
        }
        
        boolean authenticated = securityManager.validatePin(accountNumber, pin);
        
        if (authenticated) {
            System.out.println("Login successful");
            auditLogger.log("Successful login for account " + accountNumber);
            
            // Send login notification
            CustomerInfo info = customerInfo.get(accountNumber);
            if (info != null) {
                notificationService.sendPushNotification(info.deviceId, 
                    "You have successfully logged into your account");
            }
        } else {
            System.out.println("Login failed");
            auditLogger.log("Failed login attempt for account " + accountNumber);
        }
        
        return authenticated;
    }
    
    /**
     * Simple balance inquiry with audit logging
     */
    public double checkBalance(String accountNumber) {
        System.out.println("\n=== BALANCE INQUIRY ===");
        
        double balance = accountManager.getBalance(accountNumber);
        auditLogger.logTransaction(accountNumber, "BALANCE_INQUIRY", 0);
        
        System.out.println("Current balance: $" + String.format("%.2f", balance));
        return balance;
    }
    
    /**
     * Simplified withdrawal with notifications and transaction tracking
     */
    public boolean withdraw(String accountNumber, double amount) {
        System.out.println("\n=== WITHDRAWAL ===");
        System.out.println("Withdrawing $" + String.format("%.2f", amount));
        
        String transactionId = transactionManager.createTransaction("WITHDRAWAL", accountNumber, null, amount);
        
        boolean success = accountManager.debit(accountNumber, amount);
        
        if (success) {
            transactionManager.completeTransaction(transactionId);
            auditLogger.logTransaction(accountNumber, "WITHDRAWAL", amount);
            
            System.out.println("Withdrawal successful");
            System.out.println("New balance: $" + String.format("%.2f", accountManager.getBalance(accountNumber)));
            
            // Send notification
            CustomerInfo info = customerInfo.get(accountNumber);
            if (info != null) {
                notificationService.sendSMS(info.phone, 
                    "Withdrawal of $" + String.format("%.2f", amount) + " completed. " +
                    "Balance: $" + String.format("%.2f", accountManager.getBalance(accountNumber)));
            }
            
        } else {
            transactionManager.failTransaction(transactionId, "Insufficient funds");
            auditLogger.log("Failed withdrawal attempt for account " + accountNumber + " - Insufficient funds");
            System.out.println("Withdrawal failed - Insufficient funds");
        }
        
        return success;
    }
    
    /**
     * Simplified deposit with notifications and transaction tracking
     */
    public void deposit(String accountNumber, double amount) {
        System.out.println("\n=== DEPOSIT ===");
        System.out.println("Depositing $" + String.format("%.2f", amount));
        
        String transactionId = transactionManager.createTransaction("DEPOSIT", null, accountNumber, amount);
        
        accountManager.credit(accountNumber, amount);
        transactionManager.completeTransaction(transactionId);
        auditLogger.logTransaction(accountNumber, "DEPOSIT", amount);
        
        System.out.println("Deposit successful");
        System.out.println("New balance: $" + String.format("%.2f", accountManager.getBalance(accountNumber)));
        
        // Send notification
        CustomerInfo info = customerInfo.get(accountNumber);
        if (info != null) {
            notificationService.sendEmail(info.email, "Deposit Confirmation",
                "Your deposit of $" + String.format("%.2f", amount) + " has been processed. " +
                "New balance: $" + String.format("%.2f", accountManager.getBalance(accountNumber)));
        }
    }
    
    /**
     * Simplified money transfer with validation and notifications
     */
    public boolean transfer(String fromAccount, String toAccount, double amount) {
        System.out.println("\n=== MONEY TRANSFER ===");
        System.out.println("Transferring $" + String.format("%.2f", amount) + 
                         " from " + fromAccount + " to " + toAccount);
        
        if (!accountManager.accountExists(toAccount)) {
            System.out.println("Destination account not found");
            return false;
        }
        
        String transactionId = transactionManager.createTransaction("TRANSFER", fromAccount, toAccount, amount);
        
        boolean success = accountManager.debit(fromAccount, amount);
        
        if (success) {
            accountManager.credit(toAccount, amount);
            transactionManager.completeTransaction(transactionId);
            
            auditLogger.log("Transfer completed: " + fromAccount + " -> " + toAccount + 
                           " Amount: $" + String.format("%.2f", amount));
            
            System.out.println("Transfer successful");
            System.out.println("Your new balance: $" + String.format("%.2f", accountManager.getBalance(fromAccount)));
            
            // Send notifications to both parties
            CustomerInfo fromInfo = customerInfo.get(fromAccount);
            CustomerInfo toInfo = customerInfo.get(toAccount);
            
            if (fromInfo != null) {
                notificationService.sendSMS(fromInfo.phone,
                    "Transfer of $" + String.format("%.2f", amount) + " to account " + toAccount + " completed.");
            }
            
            if (toInfo != null) {
                notificationService.sendPushNotification(toInfo.deviceId,
                    "You received $" + String.format("%.2f", amount) + " from account " + fromAccount);
            }
            
        } else {
            transactionManager.failTransaction(transactionId, "Insufficient funds");
            System.out.println("Transfer failed - Insufficient funds");
        }
        
        return success;
    }
    
    /**
     * Print comprehensive account statement
     */
    public void printStatement(String accountNumber) {
        System.out.println("\n=== ACCOUNT STATEMENT ===");
        System.out.println("Account: " + accountNumber);
        System.out.println("Current Balance: $" + String.format("%.2f", accountManager.getBalance(accountNumber)));
        
        System.out.println("\nTransaction History:");
        List<String> transactions = accountManager.getTransactionHistory(accountNumber);
        
        if (transactions.isEmpty()) {
            System.out.println("No transactions found");
        } else {
            for (String transaction : transactions) {
                System.out.println("  " + transaction);
            }
        }
        
        auditLogger.log("Statement printed for account " + accountNumber);
        System.out.println("========================");
    }
    
    /**
     * Logout with audit logging
     */
    public void logout(String accountNumber) {
        System.out.println("\n=== LOGOUT ===");
        auditLogger.log("Logout for account " + accountNumber);
        System.out.println("Thank you for using our banking services!");
        System.out.println("Please remember to take your card.");
    }
    
    /**
     * Emergency card blocking
     */
    public void emergencyCardBlock(String accountNumber, String reason) {
        System.out.println("\n=== EMERGENCY CARD BLOCK ===");
        securityManager.logSecurityEvent(accountNumber, "EMERGENCY_BLOCK: " + reason);
        auditLogger.log("Emergency card block for account " + accountNumber + " - Reason: " + reason);
        
        System.out.println("Card blocked successfully for security reasons");
        
        CustomerInfo info = customerInfo.get(accountNumber);
        if (info != null) {
            notificationService.sendSMS(info.phone, "ALERT: Your card has been blocked for security reasons. Contact customer service.");
        }
    }
}

/**
 * Client code demonstration
 * Shows how the facade pattern simplifies banking operations
 */
public class BankingSystemExample {
    public static void main(String[] args) {
        System.out.println("Banking System Facade Pattern Example");
        System.out.println("====================================\n");
        
        BankingFacade bank = new BankingFacade();
        
        // Without facade, customer would need to:
        // 1. Interact with AccountManager, SecurityManager, TransactionManager separately
        // 2. Handle security validation manually
        // 3. Coordinate notifications and audit logging
        // 4. Manage transaction states and error conditions
        
        // With facade, customer gets simple banking operations:
        
        String accountNumber = "12345";
        String pin = "1234";
        
        // Login process
        if (bank.login(accountNumber, pin)) {
            // Check balance
            bank.checkBalance(accountNumber);
            
            System.out.println("\n" + "=".repeat(50) + "\n");
            
            // Make a deposit
            bank.deposit(accountNumber, 500.0);
            
            System.out.println("\n" + "=".repeat(50) + "\n");
            
            // Make a withdrawal
            bank.withdraw(accountNumber, 200.0);
            
            System.out.println("\n" + "=".repeat(50) + "\n");
            
            // Transfer money
            bank.transfer(accountNumber, "67890", 300.0);
            
            System.out.println("\n" + "=".repeat(50) + "\n");
            
            // Print statement
            bank.printStatement(accountNumber);
            
            System.out.println("\n" + "=".repeat(50) + "\n");
            
            // Logout
            bank.logout(accountNumber);
        }
        
        System.out.println("\n" + "=".repeat(60) + "\n");
        
        // Demonstrate failed login attempts
        System.out.println("--- Demonstrating Security Features ---");
        bank.login("12345", "9999"); // Wrong PIN
        bank.login("12345", "8888"); // Wrong PIN again
        bank.login("12345", "7777"); // This should lock the account
        
        System.out.println("\nExample completed!");
    }
}