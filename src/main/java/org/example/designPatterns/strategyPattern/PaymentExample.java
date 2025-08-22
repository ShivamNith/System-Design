package org.example.designPatterns.strategyPattern;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

interface PaymentStrategy {
    boolean pay(double amount);
    String getPaymentType();
}

class CreditCardPayment implements PaymentStrategy {
    private String cardNumber;
    private String cvv;
    private String expiryDate;
    
    public CreditCardPayment(String cardNumber, String cvv, String expiryDate) {
        this.cardNumber = cardNumber;
        this.cvv = cvv;
        this.expiryDate = expiryDate;
    }
    
    @Override
    public boolean pay(double amount) {
        if (validateCard()) {
            System.out.println("Processing credit card payment...");
            System.out.println("Card Number: ****" + cardNumber.substring(cardNumber.length() - 4));
            System.out.println("Amount: $" + amount);
            System.out.println("Payment successful!");
            return true;
        }
        System.out.println("Credit card validation failed!");
        return false;
    }
    
    @Override
    public String getPaymentType() {
        return "Credit Card";
    }
    
    private boolean validateCard() {
        return cardNumber.length() == 16 && cvv.length() == 3;
    }
}

class PayPalPayment implements PaymentStrategy {
    private String email;
    private String password;
    
    public PayPalPayment(String email, String password) {
        this.email = email;
        this.password = password;
    }
    
    @Override
    public boolean pay(double amount) {
        if (authenticate()) {
            System.out.println("Processing PayPal payment...");
            System.out.println("Account: " + email);
            System.out.println("Amount: $" + amount);
            System.out.println("PayPal payment successful!");
            return true;
        }
        System.out.println("PayPal authentication failed!");
        return false;
    }
    
    @Override
    public String getPaymentType() {
        return "PayPal";
    }
    
    private boolean authenticate() {
        return email.contains("@") && password.length() >= 8;
    }
}

class CryptocurrencyPayment implements PaymentStrategy {
    private String walletAddress;
    private String privateKey;
    
    public CryptocurrencyPayment(String walletAddress, String privateKey) {
        this.walletAddress = walletAddress;
        this.privateKey = privateKey;
    }
    
    @Override
    public boolean pay(double amount) {
        if (validateWallet()) {
            System.out.println("Processing cryptocurrency payment...");
            System.out.println("Wallet: " + walletAddress.substring(0, 10) + "...");
            System.out.println("Amount: $" + amount + " (converted to crypto)");
            System.out.println("Broadcasting transaction to blockchain...");
            System.out.println("Transaction confirmed!");
            return true;
        }
        System.out.println("Wallet validation failed!");
        return false;
    }
    
    @Override
    public String getPaymentType() {
        return "Cryptocurrency";
    }
    
    private boolean validateWallet() {
        return walletAddress.length() > 20 && privateKey.length() > 10;
    }
}

class UPIPayment implements PaymentStrategy {
    private String upiId;
    private String pin;
    
    public UPIPayment(String upiId, String pin) {
        this.upiId = upiId;
        this.pin = pin;
    }
    
    @Override
    public boolean pay(double amount) {
        if (validateUPI()) {
            System.out.println("Processing UPI payment...");
            System.out.println("UPI ID: " + upiId);
            System.out.println("Amount: $" + amount);
            System.out.println("Verifying PIN...");
            System.out.println("UPI payment successful!");
            return true;
        }
        System.out.println("UPI validation failed!");
        return false;
    }
    
    @Override
    public String getPaymentType() {
        return "UPI";
    }
    
    private boolean validateUPI() {
        return upiId.contains("@") && pin.length() == 4;
    }
}

class Item {
    private String name;
    private double price;
    
    public Item(String name, double price) {
        this.name = name;
        this.price = price;
    }
    
    public String getName() { 
        return name; 
    }
    
    public double getPrice() { 
        return price; 
    }
    
    @Override
    public String toString() {
        return name + " - $" + price;
    }
}

class ShoppingCart {
    private List<Item> items;
    private PaymentStrategy paymentStrategy;
    
    public ShoppingCart() {
        this.items = new ArrayList<>();
    }
    
    public void addItem(Item item) {
        items.add(item);
        System.out.println("Added " + item.getName() + " to cart");
    }
    
    public void removeItem(Item item) {
        items.remove(item);
        System.out.println("Removed " + item.getName() + " from cart");
    }
    
    public void displayCart() {
        System.out.println("\n=== Shopping Cart ===");
        if (items.isEmpty()) {
            System.out.println("Cart is empty");
        } else {
            for (int i = 0; i < items.size(); i++) {
                System.out.println((i + 1) + ". " + items.get(i));
            }
            System.out.println("Total: $" + calculateTotal());
        }
    }
    
    public double calculateTotal() {
        return items.stream()
                .mapToDouble(Item::getPrice)
                .sum();
    }
    
    public void setPaymentStrategy(PaymentStrategy paymentStrategy) {
        this.paymentStrategy = paymentStrategy;
        System.out.println("Payment method set to: " + paymentStrategy.getPaymentType());
    }
    
    public boolean checkout() {
        if (items.isEmpty()) {
            System.out.println("Cart is empty. Add items before checkout.");
            return false;
        }
        
        if (paymentStrategy == null) {
            System.out.println("Please select a payment method!");
            return false;
        }
        
        double amount = calculateTotal();
        System.out.println("\n=== Checkout ===");
        System.out.println("Total amount: $" + amount);
        
        boolean paymentResult = paymentStrategy.pay(amount);
        
        if (paymentResult) {
            items.clear();
            System.out.println("Order completed successfully!");
        } else {
            System.out.println("Payment failed. Please try again.");
        }
        
        return paymentResult;
    }
}

public class PaymentExample {
    public static void main(String[] args) {
        ShoppingCart cart = new ShoppingCart();
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("=== E-Commerce Payment Strategy Example ===\n");
        
        cart.addItem(new Item("Laptop", 999.99));
        cart.addItem(new Item("Mouse", 29.99));
        cart.addItem(new Item("Keyboard", 79.99));
        
        cart.displayCart();
        
        System.out.println("\n=== Select Payment Method ===");
        System.out.println("1. Credit Card");
        System.out.println("2. PayPal");
        System.out.println("3. Cryptocurrency");
        System.out.println("4. UPI");
        System.out.print("Enter your choice (1-4): ");
        
        int choice = scanner.nextInt();
        scanner.nextLine();
        
        PaymentStrategy strategy = null;
        
        switch (choice) {
            case 1:
                System.out.print("Enter card number (16 digits): ");
                String cardNumber = scanner.nextLine();
                System.out.print("Enter CVV (3 digits): ");
                String cvv = scanner.nextLine();
                System.out.print("Enter expiry date (MM/YY): ");
                String expiry = scanner.nextLine();
                strategy = new CreditCardPayment(cardNumber, cvv, expiry);
                break;
                
            case 2:
                System.out.print("Enter PayPal email: ");
                String email = scanner.nextLine();
                System.out.print("Enter password: ");
                String password = scanner.nextLine();
                strategy = new PayPalPayment(email, password);
                break;
                
            case 3:
                System.out.print("Enter wallet address: ");
                String wallet = scanner.nextLine();
                System.out.print("Enter private key: ");
                String privateKey = scanner.nextLine();
                strategy = new CryptocurrencyPayment(wallet, privateKey);
                break;
                
            case 4:
                System.out.print("Enter UPI ID: ");
                String upiId = scanner.nextLine();
                System.out.print("Enter PIN (4 digits): ");
                String pin = scanner.nextLine();
                strategy = new UPIPayment(upiId, pin);
                break;
                
            default:
                System.out.println("Invalid choice!");
                scanner.close();
                return;
        }
        
        cart.setPaymentStrategy(strategy);
        cart.checkout();
        
        scanner.close();
    }
}