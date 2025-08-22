package org.example.solidPrinciples.openClosed;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Payment Processing System - Open/Closed Principle Example
 * 
 * This example demonstrates how to design a payment processing system that is:
 * - OPEN for extension (can add new payment methods)
 * - CLOSED for modification (existing code doesn't change)
 * 
 * Key Concepts Demonstrated:
 * - Strategy pattern for payment methods
 * - Template method pattern for payment flow
 * - Plugin architecture for new payment providers
 * - Configuration-based payment method registration
 */
public class PaymentProcessingExample {
    
    /**
     * BEFORE: System violating OCP
     * Adding new payment methods requires modifying existing code
     */
    public static class ViolatingPaymentProcessor {
        
        public PaymentResult processPayment(String type, double amount, Map<String, String> details) {
            PaymentResult result = new PaymentResult();
            result.amount = amount;
            result.timestamp = LocalDateTime.now();
            
            // Every new payment method requires modifying this method
            if (type.equals("CREDIT_CARD")) {
                String cardNumber = details.get("cardNumber");
                String cvv = details.get("cvv");
                String expiry = details.get("expiry");
                
                // Validate credit card
                if (cardNumber == null || cardNumber.length() != 16) {
                    result.success = false;
                    result.message = "Invalid card number";
                    return result;
                }
                
                if (cvv == null || cvv.length() != 3) {
                    result.success = false;
                    result.message = "Invalid CVV";
                    return result;
                }
                
                // Process credit card payment
                System.out.println("Processing credit card payment...");
                System.out.println("Card: " + cardNumber.substring(0, 4) + "****");
                System.out.println("Amount: $" + amount);
                
                // Simulate processing
                result.success = true;
                result.transactionId = "CC_" + System.currentTimeMillis();
                result.message = "Credit card payment successful";
                
            } else if (type.equals("PAYPAL")) {
                String email = details.get("email");
                String password = details.get("password");
                
                // Validate PayPal credentials
                if (email == null || !email.contains("@")) {
                    result.success = false;
                    result.message = "Invalid PayPal email";
                    return result;
                }
                
                if (password == null || password.isEmpty()) {
                    result.success = false;
                    result.message = "PayPal password required";
                    return result;
                }
                
                // Process PayPal payment
                System.out.println("Processing PayPal payment...");
                System.out.println("Account: " + email);
                System.out.println("Amount: $" + amount);
                
                result.success = true;
                result.transactionId = "PP_" + System.currentTimeMillis();
                result.message = "PayPal payment successful";
                
            } else if (type.equals("BANK_TRANSFER")) {
                String accountNumber = details.get("accountNumber");
                String routingNumber = details.get("routingNumber");
                
                // Validate bank details
                if (accountNumber == null || accountNumber.length() < 8) {
                    result.success = false;
                    result.message = "Invalid account number";
                    return result;
                }
                
                if (routingNumber == null || routingNumber.length() != 9) {
                    result.success = false;
                    result.message = "Invalid routing number";
                    return result;
                }
                
                // Process bank transfer
                System.out.println("Processing bank transfer...");
                System.out.println("Account: ****" + accountNumber.substring(accountNumber.length() - 4));
                System.out.println("Amount: $" + amount);
                
                result.success = true;
                result.transactionId = "BT_" + System.currentTimeMillis();
                result.message = "Bank transfer initiated";
                
            } else if (type.equals("CRYPTOCURRENCY")) {
                // Added later - requires modifying this class
                String walletAddress = details.get("walletAddress");
                String currency = details.get("currency");
                
                if (walletAddress == null || walletAddress.length() < 26) {
                    result.success = false;
                    result.message = "Invalid wallet address";
                    return result;
                }
                
                System.out.println("Processing cryptocurrency payment...");
                System.out.println("Currency: " + currency);
                System.out.println("Wallet: " + walletAddress.substring(0, 6) + "...");
                System.out.println("Amount: $" + amount);
                
                result.success = true;
                result.transactionId = "CRYPTO_" + System.currentTimeMillis();
                result.message = "Cryptocurrency transaction broadcast";
                
            } else {
                result.success = false;
                result.message = "Unknown payment type: " + type;
            }
            
            // Log transaction (also violates OCP - different logging for each type)
            if (result.success) {
                if (type.equals("CREDIT_CARD")) {
                    System.out.println("LOG: Credit card transaction " + result.transactionId);
                } else if (type.equals("PAYPAL")) {
                    System.out.println("LOG: PayPal transaction " + result.transactionId);
                } else if (type.equals("BANK_TRANSFER")) {
                    System.out.println("LOG: Bank transfer " + result.transactionId);
                } else if (type.equals("CRYPTOCURRENCY")) {
                    System.out.println("LOG: Crypto transaction " + result.transactionId);
                }
            }
            
            return result;
        }
        
        // Calculate fees - also violates OCP
        public double calculateFee(String type, double amount) {
            if (type.equals("CREDIT_CARD")) {
                return amount * 0.029 + 0.30; // 2.9% + $0.30
            } else if (type.equals("PAYPAL")) {
                return amount * 0.034 + 0.49; // 3.4% + $0.49
            } else if (type.equals("BANK_TRANSFER")) {
                return 5.00; // Flat fee
            } else if (type.equals("CRYPTOCURRENCY")) {
                return amount * 0.015; // 1.5%
            }
            return 0;
        }
        
        static class PaymentResult {
            boolean success;
            String transactionId;
            String message;
            double amount;
            LocalDateTime timestamp;
        }
    }
    
    /**
     * AFTER: System following OCP
     * New payment methods can be added without modifying existing code
     */
    
    // Core abstractions
    public interface PaymentMethod {
        PaymentResult process(PaymentRequest request);
        PaymentValidation validate(PaymentRequest request);
        double calculateFee(double amount);
        String getMethodName();
        boolean isAvailable();
    }
    
    public static class PaymentRequest {
        private final double amount;
        private final String currency;
        private final Map<String, String> credentials;
        private final Map<String, Object> metadata;
        
        public PaymentRequest(double amount, String currency, Map<String, String> credentials) {
            this.amount = amount;
            this.currency = currency;
            this.credentials = credentials;
            this.metadata = new HashMap<>();
        }
        
        public double getAmount() { return amount; }
        public String getCurrency() { return currency; }
        public Map<String, String> getCredentials() { return credentials; }
        public Map<String, Object> getMetadata() { return metadata; }
        public void addMetadata(String key, Object value) { metadata.put(key, value); }
    }
    
    public static class PaymentResult {
        private final boolean success;
        private final String transactionId;
        private final String message;
        private final double amount;
        private final double fee;
        private final LocalDateTime timestamp;
        private final Map<String, Object> additionalInfo;
        
        private PaymentResult(Builder builder) {
            this.success = builder.success;
            this.transactionId = builder.transactionId;
            this.message = builder.message;
            this.amount = builder.amount;
            this.fee = builder.fee;
            this.timestamp = builder.timestamp;
            this.additionalInfo = builder.additionalInfo;
        }
        
        public boolean isSuccess() { return success; }
        public String getTransactionId() { return transactionId; }
        public String getMessage() { return message; }
        public double getAmount() { return amount; }
        public double getFee() { return fee; }
        public LocalDateTime getTimestamp() { return timestamp; }
        public Map<String, Object> getAdditionalInfo() { return additionalInfo; }
        
        public static class Builder {
            private boolean success;
            private String transactionId;
            private String message;
            private double amount;
            private double fee;
            private LocalDateTime timestamp = LocalDateTime.now();
            private Map<String, Object> additionalInfo = new HashMap<>();
            
            public Builder success(boolean success) {
                this.success = success;
                return this;
            }
            
            public Builder transactionId(String transactionId) {
                this.transactionId = transactionId;
                return this;
            }
            
            public Builder message(String message) {
                this.message = message;
                return this;
            }
            
            public Builder amount(double amount) {
                this.amount = amount;
                return this;
            }
            
            public Builder fee(double fee) {
                this.fee = fee;
                return this;
            }
            
            public Builder timestamp(LocalDateTime timestamp) {
                this.timestamp = timestamp;
                return this;
            }
            
            public Builder addInfo(String key, Object value) {
                this.additionalInfo.put(key, value);
                return this;
            }
            
            public PaymentResult build() {
                return new PaymentResult(this);
            }
        }
    }
    
    public static class PaymentValidation {
        private final boolean valid;
        private final List<String> errors;
        
        public PaymentValidation(boolean valid, List<String> errors) {
            this.valid = valid;
            this.errors = errors;
        }
        
        public boolean isValid() { return valid; }
        public List<String> getErrors() { return errors; }
    }
    
    // Abstract base class with template method pattern
    public abstract static class AbstractPaymentMethod implements PaymentMethod {
        protected final String methodName;
        protected boolean available = true;
        
        public AbstractPaymentMethod(String methodName) {
            this.methodName = methodName;
        }
        
        @Override
        public final PaymentResult process(PaymentRequest request) {
            // Template method pattern - defines the algorithm structure
            PaymentValidation validation = validate(request);
            if (!validation.isValid()) {
                return new PaymentResult.Builder()
                    .success(false)
                    .message("Validation failed: " + String.join(", ", validation.getErrors()))
                    .amount(request.getAmount())
                    .build();
            }
            
            double fee = calculateFee(request.getAmount());
            
            // Pre-processing hook
            beforeProcess(request);
            
            // Actual processing (implemented by subclasses)
            PaymentResult result = doProcess(request, fee);
            
            // Post-processing hook
            afterProcess(request, result);
            
            // Logging (consistent for all payment methods)
            logTransaction(result);
            
            return result;
        }
        
        protected abstract PaymentResult doProcess(PaymentRequest request, double fee);
        
        protected void beforeProcess(PaymentRequest request) {
            System.out.println("\n🔄 Initiating " + methodName + " payment for $" + 
                             String.format("%.2f", request.getAmount()));
        }
        
        protected void afterProcess(PaymentRequest request, PaymentResult result) {
            if (result.isSuccess()) {
                System.out.println("✅ " + methodName + " payment completed successfully");
            } else {
                System.out.println("❌ " + methodName + " payment failed: " + result.getMessage());
            }
        }
        
        protected void logTransaction(PaymentResult result) {
            System.out.println("📝 LOG: " + methodName + " transaction " + 
                             (result.isSuccess() ? "SUCCESS" : "FAILED") + 
                             " - ID: " + result.getTransactionId() +
                             " - Amount: $" + String.format("%.2f", result.getAmount()) +
                             " - Fee: $" + String.format("%.2f", result.getFee()));
        }
        
        @Override
        public String getMethodName() { return methodName; }
        
        @Override
        public boolean isAvailable() { return available; }
        
        public void setAvailable(boolean available) { this.available = available; }
    }
    
    // Concrete payment method implementations
    public static class CreditCardPayment extends AbstractPaymentMethod {
        private final String gateway;
        
        public CreditCardPayment(String gateway) {
            super("Credit Card");
            this.gateway = gateway;
        }
        
        @Override
        public PaymentValidation validate(PaymentRequest request) {
            List<String> errors = new ArrayList<>();
            Map<String, String> credentials = request.getCredentials();
            
            String cardNumber = credentials.get("cardNumber");
            if (cardNumber == null || !isValidCardNumber(cardNumber)) {
                errors.add("Invalid card number");
            }
            
            String cvv = credentials.get("cvv");
            if (cvv == null || cvv.length() < 3 || cvv.length() > 4) {
                errors.add("Invalid CVV");
            }
            
            String expiry = credentials.get("expiry");
            if (expiry == null || !expiry.matches("\\d{2}/\\d{2}")) {
                errors.add("Invalid expiry date (MM/YY)");
            }
            
            if (request.getAmount() <= 0) {
                errors.add("Amount must be positive");
            }
            
            return new PaymentValidation(errors.isEmpty(), errors);
        }
        
        @Override
        protected PaymentResult doProcess(PaymentRequest request, double fee) {
            String cardNumber = request.getCredentials().get("cardNumber");
            System.out.println("💳 Processing with gateway: " + gateway);
            System.out.println("💳 Card: " + maskCardNumber(cardNumber));
            System.out.println("💳 Amount: $" + String.format("%.2f", request.getAmount()));
            System.out.println("💳 Fee: $" + String.format("%.2f", fee));
            
            // Simulate processing
            String transactionId = "CC_" + gateway.toUpperCase() + "_" + System.currentTimeMillis();
            
            return new PaymentResult.Builder()
                .success(true)
                .transactionId(transactionId)
                .message("Credit card payment processed successfully")
                .amount(request.getAmount())
                .fee(fee)
                .addInfo("gateway", gateway)
                .addInfo("last4", cardNumber.substring(cardNumber.length() - 4))
                .build();
        }
        
        @Override
        public double calculateFee(double amount) {
            return amount * 0.029 + 0.30; // 2.9% + $0.30
        }
        
        private boolean isValidCardNumber(String cardNumber) {
            if (cardNumber == null) return false;
            String cleaned = cardNumber.replaceAll("\\s", "");
            return cleaned.matches("\\d{13,19}") && luhnCheck(cleaned);
        }
        
        private boolean luhnCheck(String cardNumber) {
            // Simplified Luhn algorithm
            int sum = 0;
            boolean alternate = false;
            for (int i = cardNumber.length() - 1; i >= 0; i--) {
                int digit = Character.getNumericValue(cardNumber.charAt(i));
                if (alternate) {
                    digit *= 2;
                    if (digit > 9) digit -= 9;
                }
                sum += digit;
                alternate = !alternate;
            }
            return sum % 10 == 0;
        }
        
        private String maskCardNumber(String cardNumber) {
            if (cardNumber.length() < 8) return "****";
            return cardNumber.substring(0, 4) + " **** **** " + 
                   cardNumber.substring(cardNumber.length() - 4);
        }
    }
    
    public static class PayPalPayment extends AbstractPaymentMethod {
        private final String apiEndpoint;
        
        public PayPalPayment(String apiEndpoint) {
            super("PayPal");
            this.apiEndpoint = apiEndpoint;
        }
        
        @Override
        public PaymentValidation validate(PaymentRequest request) {
            List<String> errors = new ArrayList<>();
            Map<String, String> credentials = request.getCredentials();
            
            String email = credentials.get("email");
            if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                errors.add("Invalid PayPal email");
            }
            
            String password = credentials.get("password");
            if (password == null || password.length() < 8) {
                errors.add("Invalid PayPal password");
            }
            
            return new PaymentValidation(errors.isEmpty(), errors);
        }
        
        @Override
        protected PaymentResult doProcess(PaymentRequest request, double fee) {
            String email = request.getCredentials().get("email");
            System.out.println("🅿️ Connecting to PayPal API: " + apiEndpoint);
            System.out.println("🅿️ Account: " + email);
            System.out.println("🅿️ Amount: $" + String.format("%.2f", request.getAmount()));
            System.out.println("🅿️ Fee: $" + String.format("%.2f", fee));
            
            String transactionId = "PP_" + System.currentTimeMillis();
            
            return new PaymentResult.Builder()
                .success(true)
                .transactionId(transactionId)
                .message("PayPal payment processed successfully")
                .amount(request.getAmount())
                .fee(fee)
                .addInfo("account", email)
                .addInfo("api", apiEndpoint)
                .build();
        }
        
        @Override
        public double calculateFee(double amount) {
            return amount * 0.034 + 0.49; // 3.4% + $0.49
        }
    }
    
    public static class BankTransferPayment extends AbstractPaymentMethod {
        private final String bankNetwork;
        
        public BankTransferPayment(String bankNetwork) {
            super("Bank Transfer");
            this.bankNetwork = bankNetwork;
        }
        
        @Override
        public PaymentValidation validate(PaymentRequest request) {
            List<String> errors = new ArrayList<>();
            Map<String, String> credentials = request.getCredentials();
            
            String accountNumber = credentials.get("accountNumber");
            if (accountNumber == null || accountNumber.length() < 8) {
                errors.add("Invalid account number");
            }
            
            String routingNumber = credentials.get("routingNumber");
            if (routingNumber == null || !routingNumber.matches("\\d{9}")) {
                errors.add("Invalid routing number (must be 9 digits)");
            }
            
            return new PaymentValidation(errors.isEmpty(), errors);
        }
        
        @Override
        protected PaymentResult doProcess(PaymentRequest request, double fee) {
            String accountNumber = request.getCredentials().get("accountNumber");
            String routingNumber = request.getCredentials().get("routingNumber");
            
            System.out.println("🏦 Initiating bank transfer via " + bankNetwork);
            System.out.println("🏦 Account: ****" + accountNumber.substring(accountNumber.length() - 4));
            System.out.println("🏦 Routing: " + routingNumber);
            System.out.println("🏦 Amount: $" + String.format("%.2f", request.getAmount()));
            System.out.println("🏦 Fee: $" + String.format("%.2f", fee));
            
            String transactionId = "BT_" + bankNetwork + "_" + System.currentTimeMillis();
            
            return new PaymentResult.Builder()
                .success(true)
                .transactionId(transactionId)
                .message("Bank transfer initiated successfully")
                .amount(request.getAmount())
                .fee(fee)
                .addInfo("network", bankNetwork)
                .addInfo("estimatedTime", "2-3 business days")
                .build();
        }
        
        @Override
        public double calculateFee(double amount) {
            return 5.00; // Flat fee
        }
    }
    
    // New payment methods can be added without modifying existing code
    public static class CryptocurrencyPayment extends AbstractPaymentMethod {
        private final String blockchain;
        private final double networkFee;
        
        public CryptocurrencyPayment(String blockchain, double networkFee) {
            super("Cryptocurrency");
            this.blockchain = blockchain;
            this.networkFee = networkFee;
        }
        
        @Override
        public PaymentValidation validate(PaymentRequest request) {
            List<String> errors = new ArrayList<>();
            Map<String, String> credentials = request.getCredentials();
            
            String walletAddress = credentials.get("walletAddress");
            if (walletAddress == null || !isValidWalletAddress(walletAddress)) {
                errors.add("Invalid wallet address for " + blockchain);
            }
            
            String privateKey = credentials.get("privateKey");
            if (privateKey == null || privateKey.length() < 32) {
                errors.add("Invalid private key");
            }
            
            return new PaymentValidation(errors.isEmpty(), errors);
        }
        
        @Override
        protected PaymentResult doProcess(PaymentRequest request, double fee) {
            String walletAddress = request.getCredentials().get("walletAddress");
            
            System.out.println("₿ Broadcasting transaction on " + blockchain);
            System.out.println("₿ To wallet: " + walletAddress.substring(0, 8) + "...");
            System.out.println("₿ Amount: $" + String.format("%.2f", request.getAmount()));
            System.out.println("₿ Network fee: $" + String.format("%.2f", networkFee));
            System.out.println("₿ Service fee: $" + String.format("%.2f", fee));
            
            String transactionId = blockchain.toUpperCase() + "_" + 
                                  UUID.randomUUID().toString().substring(0, 8);
            
            return new PaymentResult.Builder()
                .success(true)
                .transactionId(transactionId)
                .message("Transaction broadcast to " + blockchain + " network")
                .amount(request.getAmount())
                .fee(fee + networkFee)
                .addInfo("blockchain", blockchain)
                .addInfo("confirmations", "0/6")
                .addInfo("estimatedTime", "10-60 minutes")
                .build();
        }
        
        @Override
        public double calculateFee(double amount) {
            return amount * 0.015 + networkFee; // 1.5% + network fee
        }
        
        private boolean isValidWalletAddress(String address) {
            // Simplified validation based on blockchain type
            switch (blockchain.toLowerCase()) {
                case "bitcoin":
                    return address.matches("^[13][a-km-zA-HJ-NP-Z1-9]{25,34}$");
                case "ethereum":
                    return address.matches("^0x[a-fA-F0-9]{40}$");
                default:
                    return address.length() >= 26;
            }
        }
    }
    
    public static class ApplePayPayment extends AbstractPaymentMethod {
        private final String merchantId;
        
        public ApplePayPayment(String merchantId) {
            super("Apple Pay");
            this.merchantId = merchantId;
        }
        
        @Override
        public PaymentValidation validate(PaymentRequest request) {
            List<String> errors = new ArrayList<>();
            Map<String, String> credentials = request.getCredentials();
            
            String deviceId = credentials.get("deviceId");
            if (deviceId == null || deviceId.isEmpty()) {
                errors.add("Device ID required for Apple Pay");
            }
            
            String touchId = credentials.get("touchId");
            if (touchId == null || !touchId.equals("verified")) {
                errors.add("Touch ID or Face ID verification required");
            }
            
            return new PaymentValidation(errors.isEmpty(), errors);
        }
        
        @Override
        protected PaymentResult doProcess(PaymentRequest request, double fee) {
            String deviceId = request.getCredentials().get("deviceId");
            
            System.out.println("🍎 Processing Apple Pay transaction");
            System.out.println("🍎 Merchant: " + merchantId);
            System.out.println("🍎 Device: " + deviceId);
            System.out.println("🍎 Amount: $" + String.format("%.2f", request.getAmount()));
            System.out.println("🍎 Fee: $" + String.format("%.2f", fee));
            
            String transactionId = "AP_" + System.currentTimeMillis();
            
            return new PaymentResult.Builder()
                .success(true)
                .transactionId(transactionId)
                .message("Apple Pay transaction completed")
                .amount(request.getAmount())
                .fee(fee)
                .addInfo("merchant", merchantId)
                .addInfo("device", deviceId)
                .build();
        }
        
        @Override
        public double calculateFee(double amount) {
            return amount * 0.025; // 2.5%
        }
    }
    
    // Payment processor that works with any payment method
    public static class PaymentProcessor {
        private final Map<String, PaymentMethod> paymentMethods;
        private final List<PaymentInterceptor> interceptors;
        
        public PaymentProcessor() {
            this.paymentMethods = new HashMap<>();
            this.interceptors = new ArrayList<>();
        }
        
        // Register new payment methods without modifying processor code
        public void registerPaymentMethod(PaymentMethod method) {
            paymentMethods.put(method.getMethodName().toUpperCase(), method);
            System.out.println("✓ Registered payment method: " + method.getMethodName());
        }
        
        public void addInterceptor(PaymentInterceptor interceptor) {
            interceptors.add(interceptor);
        }
        
        public PaymentResult processPayment(String methodName, PaymentRequest request) {
            PaymentMethod method = paymentMethods.get(methodName.toUpperCase());
            
            if (method == null) {
                return new PaymentResult.Builder()
                    .success(false)
                    .message("Payment method not found: " + methodName)
                    .amount(request.getAmount())
                    .build();
            }
            
            if (!method.isAvailable()) {
                return new PaymentResult.Builder()
                    .success(false)
                    .message("Payment method currently unavailable: " + methodName)
                    .amount(request.getAmount())
                    .build();
            }
            
            // Apply interceptors (pre-processing)
            for (PaymentInterceptor interceptor : interceptors) {
                if (!interceptor.preProcess(request, method)) {
                    return new PaymentResult.Builder()
                        .success(false)
                        .message("Payment blocked by security check")
                        .amount(request.getAmount())
                        .build();
                }
            }
            
            // Process payment
            PaymentResult result = method.process(request);
            
            // Apply interceptors (post-processing)
            for (PaymentInterceptor interceptor : interceptors) {
                interceptor.postProcess(request, result, method);
            }
            
            return result;
        }
        
        public List<String> getAvailablePaymentMethods() {
            return paymentMethods.values().stream()
                .filter(PaymentMethod::isAvailable)
                .map(PaymentMethod::getMethodName)
                .collect(ArrayList::new, (list, name) -> list.add(name), ArrayList::addAll);
        }
        
        public double calculateTotalWithFee(String methodName, double amount) {
            PaymentMethod method = paymentMethods.get(methodName.toUpperCase());
            if (method == null) return amount;
            return amount + method.calculateFee(amount);
        }
    }
    
    // Interceptor interface for cross-cutting concerns
    public interface PaymentInterceptor {
        boolean preProcess(PaymentRequest request, PaymentMethod method);
        void postProcess(PaymentRequest request, PaymentResult result, PaymentMethod method);
    }
    
    // Example interceptors
    public static class FraudDetectionInterceptor implements PaymentInterceptor {
        private final double maxAmount;
        
        public FraudDetectionInterceptor(double maxAmount) {
            this.maxAmount = maxAmount;
        }
        
        @Override
        public boolean preProcess(PaymentRequest request, PaymentMethod method) {
            if (request.getAmount() > maxAmount) {
                System.out.println("⚠️ FRAUD CHECK: Amount exceeds maximum limit");
                return false;
            }
            System.out.println("✓ Fraud check passed");
            return true;
        }
        
        @Override
        public void postProcess(PaymentRequest request, PaymentResult result, PaymentMethod method) {
            if (result.isSuccess()) {
                System.out.println("📊 Recording transaction for fraud analysis");
            }
        }
    }
    
    public static class AuditInterceptor implements PaymentInterceptor {
        @Override
        public boolean preProcess(PaymentRequest request, PaymentMethod method) {
            System.out.println("📋 AUDIT: Payment request for $" + request.getAmount() + 
                             " via " + method.getMethodName());
            return true;
        }
        
        @Override
        public void postProcess(PaymentRequest request, PaymentResult result, PaymentMethod method) {
            System.out.println("📋 AUDIT: Payment " + (result.isSuccess() ? "succeeded" : "failed") +
                             " - Transaction: " + result.getTransactionId());
        }
    }
    
    /**
     * Demonstration of the Payment Processing System following OCP
     */
    public static void main(String[] args) {
        System.out.println("=== Payment Processing System - OCP Demo ===\n");
        
        // Create payment processor
        PaymentProcessor processor = new PaymentProcessor();
        
        // Register initial payment methods
        processor.registerPaymentMethod(new CreditCardPayment("Stripe"));
        processor.registerPaymentMethod(new PayPalPayment("api.paypal.com"));
        processor.registerPaymentMethod(new BankTransferPayment("ACH"));
        
        // Add interceptors
        processor.addInterceptor(new FraudDetectionInterceptor(10000));
        processor.addInterceptor(new AuditInterceptor());
        
        System.out.println("\n" + "=".repeat(50));
        System.out.println("Processing payments with initial methods:");
        System.out.println("=".repeat(50));
        
        // Process credit card payment
        Map<String, String> ccCredentials = new HashMap<>();
        ccCredentials.put("cardNumber", "4532123456789012");
        ccCredentials.put("cvv", "123");
        ccCredentials.put("expiry", "12/25");
        
        PaymentRequest ccRequest = new PaymentRequest(99.99, "USD", ccCredentials);
        PaymentResult ccResult = processor.processPayment("Credit Card", ccRequest);
        
        System.out.println("\n" + "-".repeat(50) + "\n");
        
        // Process PayPal payment
        Map<String, String> ppCredentials = new HashMap<>();
        ppCredentials.put("email", "user@example.com");
        ppCredentials.put("password", "securepass123");
        
        PaymentRequest ppRequest = new PaymentRequest(149.99, "USD", ppCredentials);
        PaymentResult ppResult = processor.processPayment("PayPal", ppRequest);
        
        System.out.println("\n" + "=".repeat(50));
        System.out.println("Adding new payment methods WITHOUT modifying existing code:");
        System.out.println("=".repeat(50) + "\n");
        
        // Add new payment methods without modifying processor or existing methods
        processor.registerPaymentMethod(new CryptocurrencyPayment("Bitcoin", 2.50));
        processor.registerPaymentMethod(new ApplePayPayment("MERCHANT123"));
        
        // Process cryptocurrency payment
        Map<String, String> cryptoCredentials = new HashMap<>();
        cryptoCredentials.put("walletAddress", "1A1zP1eP5QGefi2DMPTfTL5SLmv7DivfNa");
        cryptoCredentials.put("privateKey", "5Kb8kLf9zgWQnogidDA76MzPL6TsZZY36hWXMssSzNydYXYB9KF");
        
        PaymentRequest cryptoRequest = new PaymentRequest(500.00, "USD", cryptoCredentials);
        PaymentResult cryptoResult = processor.processPayment("Cryptocurrency", cryptoRequest);
        
        System.out.println("\n" + "-".repeat(50) + "\n");
        
        // Process Apple Pay payment
        Map<String, String> appleCredentials = new HashMap<>();
        appleCredentials.put("deviceId", "iPhone-XYZ123");
        appleCredentials.put("touchId", "verified");
        
        PaymentRequest appleRequest = new PaymentRequest(79.99, "USD", appleCredentials);
        PaymentResult appleResult = processor.processPayment("Apple Pay", appleRequest);
        
        System.out.println("\n" + "=".repeat(50));
        System.out.println("SUMMARY - Benefits of OCP in this design:");
        System.out.println("=".repeat(50));
        System.out.println("✅ Added Cryptocurrency payment without modifying existing code");
        System.out.println("✅ Added Apple Pay without touching other payment methods");
        System.out.println("✅ Each payment method can evolve independently");
        System.out.println("✅ Processor doesn't know about specific payment implementations");
        System.out.println("✅ Can add new interceptors for cross-cutting concerns");
        System.out.println("✅ Fee calculation is encapsulated in each payment method");
        System.out.println("✅ Validation logic is specific to each payment type");
        System.out.println("✅ New payment methods automatically get logging and fraud detection");
        
        System.out.println("\n📊 Available payment methods:");
        processor.getAvailablePaymentMethods().forEach(method -> 
            System.out.println("  • " + method));
        
        System.out.println("\n💰 Fee comparison for $100:");
        for (String method : processor.getAvailablePaymentMethods()) {
            double total = processor.calculateTotalWithFee(method, 100);
            System.out.println(String.format("  • %s: $%.2f", method, total));
        }
        
        System.out.println("\n=== Demo Complete ===");
    }
}