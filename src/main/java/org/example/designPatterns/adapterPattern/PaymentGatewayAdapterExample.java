package org.example.designPatterns.adapterPattern;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Demonstration of Adapter Pattern with Payment Gateway Integration System
 * 
 * This example shows how to use the Adapter pattern to integrate
 * different payment gateways with incompatible APIs into a unified
 * payment processing system.
 */

// Target interface - standardized payment interface
interface PaymentGateway {
    PaymentResult processPayment(PaymentRequest request);
    RefundResult processRefund(String transactionId, double amount, String reason);
    PaymentStatus checkStatus(String transactionId);
    boolean isAvailable();
    GatewayInfo getGatewayInfo();
}

// Common payment classes
class PaymentRequest {
    private String orderId;
    private double amount;
    private String currency;
    private CustomerInfo customer;
    private PaymentMethod paymentMethod;
    private Map<String, String> metadata;
    
    public PaymentRequest(String orderId, double amount, String currency, 
                         CustomerInfo customer, PaymentMethod paymentMethod) {
        this.orderId = orderId;
        this.amount = amount;
        this.currency = currency;
        this.customer = customer;
        this.paymentMethod = paymentMethod;
        this.metadata = new HashMap<>();
    }
    
    // Getters and setters
    public String getOrderId() { return orderId; }
    public double getAmount() { return amount; }
    public String getCurrency() { return currency; }
    public CustomerInfo getCustomer() { return customer; }
    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public Map<String, String> getMetadata() { return metadata; }
    
    public void addMetadata(String key, String value) {
        metadata.put(key, value);
    }
}

class CustomerInfo {
    private String name;
    private String email;
    private String phone;
    private Address billingAddress;
    
    public CustomerInfo(String name, String email, String phone, Address billingAddress) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.billingAddress = billingAddress;
    }
    
    // Getters
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public Address getBillingAddress() { return billingAddress; }
}

class Address {
    private String street;
    private String city;
    private String state;
    private String country;
    private String zipCode;
    
    public Address(String street, String city, String state, String country, String zipCode) {
        this.street = street;
        this.city = city;
        this.state = state;
        this.country = country;
        this.zipCode = zipCode;
    }
    
    // Getters
    public String getStreet() { return street; }
    public String getCity() { return city; }
    public String getState() { return state; }
    public String getCountry() { return country; }
    public String getZipCode() { return zipCode; }
    
    @Override
    public String toString() {
        return street + ", " + city + ", " + state + " " + zipCode + ", " + country;
    }
}

class PaymentMethod {
    private PaymentType type;
    private String cardNumber;
    private String expiryMonth;
    private String expiryYear;
    private String cvv;
    private String cardholderName;
    
    public enum PaymentType {
        CREDIT_CARD, DEBIT_CARD, DIGITAL_WALLET, BANK_TRANSFER
    }
    
    public PaymentMethod(PaymentType type, String cardNumber, String expiryMonth, 
                        String expiryYear, String cvv, String cardholderName) {
        this.type = type;
        this.cardNumber = cardNumber;
        this.expiryMonth = expiryMonth;
        this.expiryYear = expiryYear;
        this.cvv = cvv;
        this.cardholderName = cardholderName;
    }
    
    // Getters
    public PaymentType getType() { return type; }
    public String getCardNumber() { return cardNumber; }
    public String getExpiryMonth() { return expiryMonth; }
    public String getExpiryYear() { return expiryYear; }
    public String getCvv() { return cvv; }
    public String getCardholderName() { return cardholderName; }
    
    public String getMaskedCardNumber() {
        if (cardNumber == null || cardNumber.length() < 4) return "****";
        return "**** **** **** " + cardNumber.substring(cardNumber.length() - 4);
    }
}

class PaymentResult {
    private boolean success;
    private String transactionId;
    private String gatewayTransactionId;
    private PaymentStatus status;
    private String message;
    private double processedAmount;
    private String currency;
    private LocalDateTime timestamp;
    private Map<String, Object> additionalData;
    
    public PaymentResult(boolean success, String transactionId, String gatewayTransactionId,
                        PaymentStatus status, String message, double processedAmount, String currency) {
        this.success = success;
        this.transactionId = transactionId;
        this.gatewayTransactionId = gatewayTransactionId;
        this.status = status;
        this.message = message;
        this.processedAmount = processedAmount;
        this.currency = currency;
        this.timestamp = LocalDateTime.now();
        this.additionalData = new HashMap<>();
    }
    
    // Getters
    public boolean isSuccess() { return success; }
    public String getTransactionId() { return transactionId; }
    public String getGatewayTransactionId() { return gatewayTransactionId; }
    public PaymentStatus getStatus() { return status; }
    public String getMessage() { return message; }
    public double getProcessedAmount() { return processedAmount; }
    public String getCurrency() { return currency; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public Map<String, Object> getAdditionalData() { return additionalData; }
    
    public void addData(String key, Object value) {
        additionalData.put(key, value);
    }
    
    @Override
    public String toString() {
        return String.format("PaymentResult{success=%s, transactionId='%s', status=%s, amount=%.2f %s, message='%s'}", 
                           success, transactionId, status, processedAmount, currency, message);
    }
}

class RefundResult {
    private boolean success;
    private String refundId;
    private String originalTransactionId;
    private double refundedAmount;
    private String currency;
    private String message;
    private LocalDateTime timestamp;
    
    public RefundResult(boolean success, String refundId, String originalTransactionId,
                       double refundedAmount, String currency, String message) {
        this.success = success;
        this.refundId = refundId;
        this.originalTransactionId = originalTransactionId;
        this.refundedAmount = refundedAmount;
        this.currency = currency;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }
    
    // Getters
    public boolean isSuccess() { return success; }
    public String getRefundId() { return refundId; }
    public String getOriginalTransactionId() { return originalTransactionId; }
    public double getRefundedAmount() { return refundedAmount; }
    public String getCurrency() { return currency; }
    public String getMessage() { return message; }
    public LocalDateTime getTimestamp() { return timestamp; }
    
    @Override
    public String toString() {
        return String.format("RefundResult{success=%s, refundId='%s', amount=%.2f %s, message='%s'}", 
                           success, refundId, refundedAmount, currency, message);
    }
}

enum PaymentStatus {
    PENDING, AUTHORIZED, CAPTURED, COMPLETED, FAILED, CANCELLED, REFUNDED, EXPIRED
}

class GatewayInfo {
    private String name;
    private String version;
    private List<String> supportedCurrencies;
    private List<String> supportedCountries;
    private double transactionFee;
    private boolean supportsRefunds;
    
    public GatewayInfo(String name, String version, List<String> supportedCurrencies,
                      List<String> supportedCountries, double transactionFee, boolean supportsRefunds) {
        this.name = name;
        this.version = version;
        this.supportedCurrencies = new ArrayList<>(supportedCurrencies);
        this.supportedCountries = new ArrayList<>(supportedCountries);
        this.transactionFee = transactionFee;
        this.supportsRefunds = supportsRefunds;
    }
    
    // Getters
    public String getName() { return name; }
    public String getVersion() { return version; }
    public List<String> getSupportedCurrencies() { return new ArrayList<>(supportedCurrencies); }
    public List<String> getSupportedCountries() { return new ArrayList<>(supportedCountries); }
    public double getTransactionFee() { return transactionFee; }
    public boolean supportsRefunds() { return supportsRefunds; }
    
    @Override
    public String toString() {
        return String.format("%s v%s (Fee: %.2f%%, Refunds: %s)", 
                           name, version, transactionFee, supportsRefunds ? "Yes" : "No");
    }
}

// Adaptee 1 - PayPal API (different interface)
class PayPalAPI {
    private boolean isOnline = true;
    
    public PayPalPaymentResponse processPayment(PayPalPaymentRequest request) {
        System.out.println("PayPal: Processing payment for $" + request.getAmount());
        
        // Simulate processing
        boolean success = Math.random() > 0.05; // 95% success rate
        String paymentId = "PAY-" + System.currentTimeMillis();
        String status = success ? "APPROVED" : "FAILED";
        
        return new PayPalPaymentResponse(success, paymentId, status, 
                                       success ? "Payment approved" : "Payment failed");
    }
    
    public PayPalRefundResponse processRefund(String paymentId, double amount) {
        System.out.println("PayPal: Processing refund of $" + amount + " for payment " + paymentId);
        
        boolean success = Math.random() > 0.1; // 90% success rate
        String refundId = "REF-" + System.currentTimeMillis();
        
        return new PayPalRefundResponse(success, refundId, paymentId, 
                                      success ? "Refund processed" : "Refund failed");
    }
    
    public PayPalStatusResponse getPaymentStatus(String paymentId) {
        System.out.println("PayPal: Checking status for payment " + paymentId);
        return new PayPalStatusResponse(paymentId, "COMPLETED", "Payment completed successfully");
    }
    
    public boolean isServiceAvailable() { return isOnline; }
    public void setServiceAvailable(boolean available) { this.isOnline = available; }
}

// PayPal specific classes
class PayPalPaymentRequest {
    private String intent;
    private PayPalPayer payer;
    private double amount;
    private String currency;
    private String description;
    
    public PayPalPaymentRequest(String intent, PayPalPayer payer, double amount, String currency, String description) {
        this.intent = intent;
        this.payer = payer;
        this.amount = amount;
        this.currency = currency;
        this.description = description;
    }
    
    // Getters
    public String getIntent() { return intent; }
    public PayPalPayer getPayer() { return payer; }
    public double getAmount() { return amount; }
    public String getCurrency() { return currency; }
    public String getDescription() { return description; }
}

class PayPalPayer {
    private String paymentMethod;
    private String firstName;
    private String lastName;
    private String email;
    
    public PayPalPayer(String paymentMethod, String firstName, String lastName, String email) {
        this.paymentMethod = paymentMethod;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }
    
    // Getters
    public String getPaymentMethod() { return paymentMethod; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
}

class PayPalPaymentResponse {
    private boolean success;
    private String paymentId;
    private String status;
    private String message;
    
    public PayPalPaymentResponse(boolean success, String paymentId, String status, String message) {
        this.success = success;
        this.paymentId = paymentId;
        this.status = status;
        this.message = message;
    }
    
    // Getters
    public boolean isSuccess() { return success; }
    public String getPaymentId() { return paymentId; }
    public String getStatus() { return status; }
    public String getMessage() { return message; }
}

class PayPalRefundResponse {
    private boolean success;
    private String refundId;
    private String paymentId;
    private String message;
    
    public PayPalRefundResponse(boolean success, String refundId, String paymentId, String message) {
        this.success = success;
        this.refundId = refundId;
        this.paymentId = paymentId;
        this.message = message;
    }
    
    // Getters
    public boolean isSuccess() { return success; }
    public String getRefundId() { return refundId; }
    public String getPaymentId() { return paymentId; }
    public String getMessage() { return message; }
}

class PayPalStatusResponse {
    private String paymentId;
    private String status;
    private String message;
    
    public PayPalStatusResponse(String paymentId, String status, String message) {
        this.paymentId = paymentId;
        this.status = status;
        this.message = message;
    }
    
    // Getters
    public String getPaymentId() { return paymentId; }
    public String getStatus() { return status; }
    public String getMessage() { return message; }
}

// Adaptee 2 - Stripe API (different interface)
class StripeAPI {
    private boolean isReachable = true;
    
    public StripeCharge createCharge(StripeChargeRequest request) {
        System.out.println("Stripe: Creating charge for " + request.getAmount() + " " + request.getCurrency());
        
        boolean success = Math.random() > 0.02; // 98% success rate
        String chargeId = "ch_" + System.currentTimeMillis();
        String status = success ? "succeeded" : "failed";
        
        return new StripeCharge(chargeId, request.getAmount(), request.getCurrency(), status,
                              success ? "Charge succeeded" : "Your card was declined");
    }
    
    public StripeRefund createRefund(String chargeId, int amountCents, String reason) {
        System.out.println("Stripe: Creating refund for charge " + chargeId + " amount " + amountCents);
        
        boolean success = Math.random() > 0.05; // 95% success rate
        String refundId = "re_" + System.currentTimeMillis();
        
        return new StripeRefund(refundId, chargeId, amountCents, 
                               success ? "succeeded" : "failed", reason);
    }
    
    public StripeCharge retrieveCharge(String chargeId) {
        System.out.println("Stripe: Retrieving charge " + chargeId);
        return new StripeCharge(chargeId, 5000, "usd", "succeeded", "Retrieved charge");
    }
    
    public boolean isApiReachable() { return isReachable; }
    public void setApiReachable(boolean reachable) { this.isReachable = reachable; }
}

// Stripe specific classes
class StripeChargeRequest {
    private int amount; // in cents
    private String currency;
    private String source; // token
    private String description;
    private Map<String, String> metadata;
    
    public StripeChargeRequest(int amount, String currency, String source, String description) {
        this.amount = amount;
        this.currency = currency;
        this.source = source;
        this.description = description;
        this.metadata = new HashMap<>();
    }
    
    // Getters
    public int getAmount() { return amount; }
    public String getCurrency() { return currency; }
    public String getSource() { return source; }
    public String getDescription() { return description; }
    public Map<String, String> getMetadata() { return metadata; }
}

class StripeCharge {
    private String id;
    private int amount;
    private String currency;
    private String status;
    private String failureMessage;
    
    public StripeCharge(String id, int amount, String currency, String status, String failureMessage) {
        this.id = id;
        this.amount = amount;
        this.currency = currency;
        this.status = status;
        this.failureMessage = failureMessage;
    }
    
    // Getters
    public String getId() { return id; }
    public int getAmount() { return amount; }
    public String getCurrency() { return currency; }
    public String getStatus() { return status; }
    public String getFailureMessage() { return failureMessage; }
}

class StripeRefund {
    private String id;
    private String chargeId;
    private int amount;
    private String status;
    private String reason;
    
    public StripeRefund(String id, String chargeId, int amount, String status, String reason) {
        this.id = id;
        this.chargeId = chargeId;
        this.amount = amount;
        this.status = status;
        this.reason = reason;
    }
    
    // Getters
    public String getId() { return id; }
    public String getChargeId() { return chargeId; }
    public int getAmount() { return amount; }
    public String getStatus() { return status; }
    public String getReason() { return reason; }
}

// Adapter for PayPal
class PayPalAdapter implements PaymentGateway {
    private PayPalAPI paypalAPI;
    private GatewayInfo gatewayInfo;
    
    public PayPalAdapter() {
        this.paypalAPI = new PayPalAPI();
        this.gatewayInfo = new GatewayInfo("PayPal", "v1.0", 
                                          Arrays.asList("USD", "EUR", "GBP", "CAD"),
                                          Arrays.asList("US", "CA", "GB", "DE", "FR"),
                                          2.9, true);
    }
    
    @Override
    public PaymentResult processPayment(PaymentRequest request) {
        // Convert to PayPal format
        String[] names = request.getCustomer().getName().split(" ", 2);
        String firstName = names[0];
        String lastName = names.length > 1 ? names[1] : "";
        
        PayPalPayer payer = new PayPalPayer("credit_card", firstName, lastName, 
                                          request.getCustomer().getEmail());
        
        PayPalPaymentRequest paypalRequest = new PayPalPaymentRequest("sale", payer, 
                                                                    request.getAmount(), 
                                                                    request.getCurrency(),
                                                                    "Order " + request.getOrderId());
        
        PayPalPaymentResponse response = paypalAPI.processPayment(paypalRequest);
        
        // Convert response
        PaymentStatus status = convertPayPalStatus(response.getStatus());
        PaymentResult result = new PaymentResult(
            response.isSuccess(),
            generateTransactionId(),
            response.getPaymentId(),
            status,
            response.getMessage(),
            request.getAmount(),
            request.getCurrency()
        );
        
        result.addData("gateway", "PayPal");
        result.addData("paypal_payment_id", response.getPaymentId());
        
        return result;
    }
    
    @Override
    public RefundResult processRefund(String transactionId, double amount, String reason) {
        PayPalRefundResponse response = paypalAPI.processRefund(transactionId, amount);
        
        return new RefundResult(
            response.isSuccess(),
            response.getRefundId(),
            transactionId,
            amount,
            "USD", // Assuming USD for simplicity
            response.getMessage()
        );
    }
    
    @Override
    public PaymentStatus checkStatus(String transactionId) {
        PayPalStatusResponse response = paypalAPI.getPaymentStatus(transactionId);
        return convertPayPalStatus(response.getStatus());
    }
    
    @Override
    public boolean isAvailable() {
        return paypalAPI.isServiceAvailable();
    }
    
    @Override
    public GatewayInfo getGatewayInfo() {
        return gatewayInfo;
    }
    
    private PaymentStatus convertPayPalStatus(String paypalStatus) {
        switch (paypalStatus.toUpperCase()) {
            case "APPROVED":
            case "COMPLETED": return PaymentStatus.COMPLETED;
            case "PENDING": return PaymentStatus.PENDING;
            case "FAILED": return PaymentStatus.FAILED;
            case "CANCELLED": return PaymentStatus.CANCELLED;
            default: return PaymentStatus.FAILED;
        }
    }
    
    private String generateTransactionId() {
        return "TXN-" + System.currentTimeMillis();
    }
}

// Adapter for Stripe
class StripeAdapter implements PaymentGateway {
    private StripeAPI stripeAPI;
    private GatewayInfo gatewayInfo;
    
    public StripeAdapter() {
        this.stripeAPI = new StripeAPI();
        this.gatewayInfo = new GatewayInfo("Stripe", "v1.0",
                                          Arrays.asList("USD", "EUR", "GBP", "CAD", "AUD", "JPY"),
                                          Arrays.asList("US", "CA", "GB", "DE", "FR", "AU", "JP"),
                                          2.9, true);
    }
    
    @Override
    public PaymentResult processPayment(PaymentRequest request) {
        // Convert amount to cents
        int amountCents = (int) Math.round(request.getAmount() * 100);
        
        // Tokenize card (in real implementation, this would be done securely)
        String cardToken = tokenizeCard(request.getPaymentMethod());
        
        StripeChargeRequest chargeRequest = new StripeChargeRequest(
            amountCents,
            request.getCurrency().toLowerCase(),
            cardToken,
            "Payment for order " + request.getOrderId()
        );
        
        StripeCharge charge = stripeAPI.createCharge(chargeRequest);
        
        // Convert response
        PaymentStatus status = convertStripeStatus(charge.getStatus());
        PaymentResult result = new PaymentResult(
            "succeeded".equals(charge.getStatus()),
            generateTransactionId(),
            charge.getId(),
            status,
            charge.getFailureMessage() != null ? charge.getFailureMessage() : "Payment processed",
            request.getAmount(),
            request.getCurrency()
        );
        
        result.addData("gateway", "Stripe");
        result.addData("stripe_charge_id", charge.getId());
        
        return result;
    }
    
    @Override
    public RefundResult processRefund(String transactionId, double amount, String reason) {
        int amountCents = (int) Math.round(amount * 100);
        StripeRefund refund = stripeAPI.createRefund(transactionId, amountCents, reason);
        
        return new RefundResult(
            "succeeded".equals(refund.getStatus()),
            refund.getId(),
            transactionId,
            amount,
            "USD", // Assuming USD for simplicity
            "succeeded".equals(refund.getStatus()) ? "Refund processed" : "Refund failed"
        );
    }
    
    @Override
    public PaymentStatus checkStatus(String transactionId) {
        StripeCharge charge = stripeAPI.retrieveCharge(transactionId);
        return convertStripeStatus(charge.getStatus());
    }
    
    @Override
    public boolean isAvailable() {
        return stripeAPI.isApiReachable();
    }
    
    @Override
    public GatewayInfo getGatewayInfo() {
        return gatewayInfo;
    }
    
    private String tokenizeCard(PaymentMethod paymentMethod) {
        // In real implementation, this would securely tokenize the card
        return "tok_" + paymentMethod.getCardNumber().hashCode();
    }
    
    private PaymentStatus convertStripeStatus(String stripeStatus) {
        switch (stripeStatus.toLowerCase()) {
            case "succeeded": return PaymentStatus.COMPLETED;
            case "pending": return PaymentStatus.PENDING;
            case "failed": return PaymentStatus.FAILED;
            default: return PaymentStatus.FAILED;
        }
    }
    
    private String generateTransactionId() {
        return "TXN-" + System.currentTimeMillis();
    }
}

// Payment service using the unified interface
class PaymentService {
    private Map<String, PaymentGateway> gateways;
    private PaymentGateway primaryGateway;
    private PaymentGateway fallbackGateway;
    
    public PaymentService() {
        this.gateways = new HashMap<>();
        initializeGateways();
    }
    
    private void initializeGateways() {
        PaymentGateway paypal = new PayPalAdapter();
        PaymentGateway stripe = new StripeAdapter();
        
        gateways.put("paypal", paypal);
        gateways.put("stripe", stripe);
        
        // Set default gateways
        this.primaryGateway = stripe;
        this.fallbackGateway = paypal;
    }
    
    public PaymentResult processPayment(PaymentRequest request) {
        return processPaymentWithFallback(request, primaryGateway, fallbackGateway);
    }
    
    public PaymentResult processPaymentWithGateway(PaymentRequest request, String gatewayName) {
        PaymentGateway gateway = gateways.get(gatewayName.toLowerCase());
        if (gateway == null) {
            throw new IllegalArgumentException("Unknown gateway: " + gatewayName);
        }
        
        if (!gateway.isAvailable()) {
            return new PaymentResult(false, null, null, PaymentStatus.FAILED,
                                   "Gateway " + gatewayName + " is not available",
                                   0, request.getCurrency());
        }
        
        return gateway.processPayment(request);
    }
    
    private PaymentResult processPaymentWithFallback(PaymentRequest request, 
                                                   PaymentGateway primary, 
                                                   PaymentGateway fallback) {
        // Try primary gateway
        if (primary.isAvailable()) {
            try {
                PaymentResult result = primary.processPayment(request);
                if (result.isSuccess()) {
                    return result;
                } else {
                    System.out.println("Primary gateway failed: " + result.getMessage());
                }
            } catch (Exception e) {
                System.out.println("Primary gateway error: " + e.getMessage());
            }
        } else {
            System.out.println("Primary gateway not available");
        }
        
        // Try fallback gateway
        if (fallback.isAvailable()) {
            System.out.println("Using fallback gateway: " + fallback.getGatewayInfo().getName());
            try {
                return fallback.processPayment(request);
            } catch (Exception e) {
                System.out.println("Fallback gateway error: " + e.getMessage());
            }
        }
        
        return new PaymentResult(false, null, null, PaymentStatus.FAILED,
                               "All payment gateways are unavailable", 0, request.getCurrency());
    }
    
    public RefundResult processRefund(String transactionId, double amount, String reason, String gatewayName) {
        PaymentGateway gateway = gateways.get(gatewayName.toLowerCase());
        if (gateway == null || !gateway.isAvailable()) {
            return new RefundResult(false, null, transactionId, amount, "USD",
                                  "Gateway not available for refund");
        }
        
        return gateway.processRefund(transactionId, amount, reason);
    }
    
    public void printGatewayInfo() {
        System.out.println("\n=== Available Payment Gateways ===");
        for (Map.Entry<String, PaymentGateway> entry : gateways.entrySet()) {
            String name = entry.getKey();
            PaymentGateway gateway = entry.getValue();
            GatewayInfo info = gateway.getGatewayInfo();
            System.out.println(name.toUpperCase() + ": " + info + 
                             " (Available: " + gateway.isAvailable() + ")");
        }
    }
    
    public void setPrimaryGateway(String gatewayName) {
        PaymentGateway gateway = gateways.get(gatewayName.toLowerCase());
        if (gateway != null) {
            this.primaryGateway = gateway;
            System.out.println("Primary gateway set to: " + gateway.getGatewayInfo().getName());
        }
    }
}

// Demonstration class
public class PaymentGatewayAdapterExample {
    
    public static void main(String[] args) {
        System.out.println("=== Adapter Pattern Demonstration - Payment Gateway Integration ===\n");
        
        // Example 1: Basic payment processing
        System.out.println("1. Basic Payment Processing:");
        
        // Create customer and payment method
        Address address = new Address("123 Main St", "New York", "NY", "USA", "10001");
        CustomerInfo customer = new CustomerInfo("John Doe", "john@example.com", "+1-555-0123", address);
        PaymentMethod paymentMethod = new PaymentMethod(
            PaymentMethod.PaymentType.CREDIT_CARD,
            "4111111111111111", "12", "2025", "123", "John Doe"
        );
        
        PaymentRequest request = new PaymentRequest("ORDER-001", 99.99, "USD", customer, paymentMethod);
        request.addMetadata("product", "Premium Subscription");
        
        PaymentService paymentService = new PaymentService();
        paymentService.printGatewayInfo();
        
        PaymentResult result = paymentService.processPayment(request);
        System.out.println("Payment Result: " + result);
        System.out.println();
        
        // Example 2: Gateway-specific processing
        System.out.println("2. Gateway-Specific Processing:");
        
        PaymentRequest request2 = new PaymentRequest("ORDER-002", 149.99, "USD", customer, paymentMethod);
        
        System.out.println("Processing with PayPal:");
        PaymentResult paypalResult = paymentService.processPaymentWithGateway(request2, "paypal");
        System.out.println("PayPal Result: " + paypalResult);
        
        System.out.println("\nProcessing with Stripe:");
        PaymentResult stripeResult = paymentService.processPaymentWithGateway(request2, "stripe");
        System.out.println("Stripe Result: " + stripeResult);
        System.out.println();
        
        // Example 3: Refund processing
        System.out.println("3. Refund Processing:");
        if (result.isSuccess()) {
            RefundResult refund = paymentService.processRefund(
                result.getGatewayTransactionId(), 
                50.00, 
                "Customer requested partial refund", 
                "stripe"
            );
            System.out.println("Refund Result: " + refund);
        }
        System.out.println();
        
        // Example 4: Fallback mechanism
        System.out.println("4. Gateway Fallback Mechanism:");
        
        // Simulate primary gateway failure
        PaymentGateway stripeGateway = new StripeAdapter();
        if (stripeGateway instanceof StripeAdapter) {
            // In real implementation, we'd have a method to control this
            System.out.println("Simulating Stripe gateway failure...");
        }
        
        PaymentRequest request3 = new PaymentRequest("ORDER-003", 299.99, "USD", customer, paymentMethod);
        PaymentResult fallbackResult = paymentService.processPayment(request3);
        System.out.println("Fallback Result: " + fallbackResult);
        System.out.println();
        
        // Example 5: Different customer and payment scenarios
        System.out.println("5. Different Payment Scenarios:");
        
        // International customer
        Address intlAddress = new Address("10 Downing St", "London", "England", "UK", "SW1A 2AA");
        CustomerInfo intlCustomer = new CustomerInfo("Jane Smith", "jane@example.co.uk", "+44-20-1234-5678", intlAddress);
        PaymentMethod intlPayment = new PaymentMethod(
            PaymentMethod.PaymentType.DEBIT_CARD,
            "5555555555554444", "06", "2026", "456", "Jane Smith"
        );
        
        PaymentRequest intlRequest = new PaymentRequest("ORDER-INTL-001", 75.50, "GBP", intlCustomer, intlPayment);
        PaymentResult intlResult = paymentService.processPayment(intlRequest);
        System.out.println("International Payment: " + intlResult);
        
        // High-value transaction
        PaymentRequest highValueRequest = new PaymentRequest("ORDER-HV-001", 5000.00, "USD", customer, paymentMethod);
        PaymentResult hvResult = paymentService.processPayment(highValueRequest);
        System.out.println("High-Value Payment: " + hvResult);
        System.out.println();
        
        // Example 6: Error handling
        System.out.println("6. Error Handling:");
        try {
            PaymentResult unknownGateway = paymentService.processPaymentWithGateway(request, "unknown");
        } catch (IllegalArgumentException e) {
            System.out.println("Caught expected error: " + e.getMessage());
        }
        
        // Test with invalid card
        PaymentMethod invalidCard = new PaymentMethod(
            PaymentMethod.PaymentType.CREDIT_CARD,
            "0000000000000000", "01", "2020", "000", "Invalid Card"
        );
        PaymentRequest invalidRequest = new PaymentRequest("ORDER-INVALID", 50.00, "USD", customer, invalidCard);
        PaymentResult invalidResult = paymentService.processPayment(invalidRequest);
        System.out.println("Invalid Card Result: " + invalidResult);
        System.out.println();
        
        // Example 7: Gateway switching
        System.out.println("7. Gateway Management:");
        System.out.println("Switching primary gateway to PayPal...");
        paymentService.setPrimaryGateway("paypal");
        
        PaymentRequest switchTest = new PaymentRequest("ORDER-SWITCH", 25.99, "USD", customer, paymentMethod);
        PaymentResult switchResult = paymentService.processPayment(switchTest);
        System.out.println("Switched Gateway Result: " + switchResult);
        System.out.println();
        
        System.out.println("=== Demonstration Complete ===");
    }
}