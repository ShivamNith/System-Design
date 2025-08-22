package org.example.designPatterns.observerPattern;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Stock Trading Observer Pattern Example
 * 
 * This example demonstrates the Observer pattern in a stock trading system.
 * The stock exchange (subject) notifies various trading components (observers)
 * when stock prices change, enabling real-time trading decisions.
 * 
 * Key Components:
 * - Stock: Data model representing a stock with symbol, price, and volume
 * - StockExchange: Subject that manages multiple stocks and price updates
 * - Various Observer types: Traders, alerts, displays, and automated systems
 * 
 * Features:
 * - Real-time price notifications
 * - Price alerts and thresholds
 * - Trading strategy automation
 * - Portfolio tracking
 * - Thread-safe operations
 */

// Stock data model
class Stock {
    private String symbol;
    private double currentPrice;
    private double previousPrice;
    private long volume;
    private LocalDateTime lastUpdated;
    
    public Stock(String symbol, double initialPrice) {
        this.symbol = symbol;
        this.currentPrice = initialPrice;
        this.previousPrice = initialPrice;
        this.volume = 0;
        this.lastUpdated = LocalDateTime.now();
    }
    
    // Getters and setters
    public String getSymbol() { return symbol; }
    
    public double getCurrentPrice() { return currentPrice; }
    public void setCurrentPrice(double currentPrice) {
        this.previousPrice = this.currentPrice;
        this.currentPrice = currentPrice;
        this.lastUpdated = LocalDateTime.now();
    }
    
    public double getPreviousPrice() { return previousPrice; }
    
    public long getVolume() { return volume; }
    public void setVolume(long volume) { this.volume = volume; }
    public void addVolume(long additionalVolume) { this.volume += additionalVolume; }
    
    public LocalDateTime getLastUpdated() { return lastUpdated; }
    
    public double getPriceChange() {
        return currentPrice - previousPrice;
    }
    
    public double getPriceChangePercentage() {
        if (previousPrice == 0) return 0;
        return ((currentPrice - previousPrice) / previousPrice) * 100;
    }
    
    @Override
    public String toString() {
        return String.format("%s: $%.2f (%.2f%%) Vol: %,d", 
            symbol, currentPrice, getPriceChangePercentage(), volume);
    }
}

// Observer interface for stock price changes
interface StockObserver {
    void onPriceUpdate(Stock stock);
    String getObserverName();
    boolean isActive();
}

// Subject interface for stock exchange
interface StockSubject {
    void subscribe(String symbol, StockObserver observer);
    void unsubscribe(String symbol, StockObserver observer);
    void unsubscribeFromAll(StockObserver observer);
    void notifyObservers(String symbol);
}

// Concrete stock exchange (Subject)
class StockExchange implements StockSubject {
    private Map<String, Stock> stocks;
    // Using CopyOnWriteArrayList for thread safety
    private Map<String, List<StockObserver>> observers;
    private String exchangeName;
    
    public StockExchange(String exchangeName) {
        this.exchangeName = exchangeName;
        this.stocks = new HashMap<>();
        this.observers = new HashMap<>();
    }
    
    @Override
    public void subscribe(String symbol, StockObserver observer) {
        observers.computeIfAbsent(symbol, k -> new CopyOnWriteArrayList<>()).add(observer);
        System.out.println("📊 " + observer.getObserverName() + " subscribed to " + symbol);
    }
    
    @Override
    public void unsubscribe(String symbol, StockObserver observer) {
        List<StockObserver> symbolObservers = observers.get(symbol);
        if (symbolObservers != null) {
            symbolObservers.remove(observer);
            System.out.println("📊 " + observer.getObserverName() + " unsubscribed from " + symbol);
        }
    }
    
    @Override
    public void unsubscribeFromAll(StockObserver observer) {
        for (List<StockObserver> symbolObservers : observers.values()) {
            symbolObservers.remove(observer);
        }
        System.out.println("📊 " + observer.getObserverName() + " unsubscribed from all stocks");
    }
    
    @Override
    public void notifyObservers(String symbol) {
        List<StockObserver> symbolObservers = observers.get(symbol);
        if (symbolObservers != null) {
            Stock stock = stocks.get(symbol);
            if (stock != null) {
                for (StockObserver observer : symbolObservers) {
                    if (observer.isActive()) {
                        try {
                            observer.onPriceUpdate(stock);
                        } catch (Exception e) {
                            System.err.println("Error notifying observer " + 
                                observer.getObserverName() + ": " + e.getMessage());
                        }
                    }
                }
            }
        }
    }
    
    public void addStock(String symbol, double initialPrice) {
        stocks.put(symbol, new Stock(symbol, initialPrice));
        System.out.println("📈 " + exchangeName + ": Added stock " + symbol + " at $" + initialPrice);
    }
    
    public void updateStockPrice(String symbol, double newPrice, long volume) {
        Stock stock = stocks.get(symbol);
        if (stock != null) {
            double oldPrice = stock.getCurrentPrice();
            stock.setCurrentPrice(newPrice);
            stock.addVolume(volume);
            
            System.out.println("💹 " + exchangeName + ": " + symbol + " updated from $" + 
                String.format("%.2f", oldPrice) + " to $" + String.format("%.2f", newPrice) + 
                " (Vol: +" + String.format("%,d", volume) + ")");
            
            notifyObservers(symbol);
        }
    }
    
    public Stock getStock(String symbol) {
        return stocks.get(symbol);
    }
    
    public int getObserverCount(String symbol) {
        List<StockObserver> symbolObservers = observers.get(symbol);
        return symbolObservers != null ? symbolObservers.size() : 0;
    }
    
    public String getExchangeName() {
        return exchangeName;
    }
}

// Concrete observer - Price alert system
class PriceAlert implements StockObserver {
    private String alertName;
    private double alertPrice;
    private boolean isAboveAlert; // true for "alert when above", false for "alert when below"
    private boolean isActive;
    private boolean hasTriggered;
    
    public PriceAlert(String alertName, double alertPrice, boolean isAboveAlert) {
        this.alertName = alertName;
        this.alertPrice = alertPrice;
        this.isAboveAlert = isAboveAlert;
        this.isActive = true;
        this.hasTriggered = false;
    }
    
    @Override
    public void onPriceUpdate(Stock stock) {
        if (hasTriggered) return; // One-time alert
        
        boolean shouldAlert = isAboveAlert ? 
            stock.getCurrentPrice() >= alertPrice : 
            stock.getCurrentPrice() <= alertPrice;
            
        if (shouldAlert) {
            hasTriggered = true;
            String direction = isAboveAlert ? "above" : "below";
            System.out.println("🚨 PRICE ALERT [" + alertName + "]: " + 
                stock.getSymbol() + " is " + direction + " $" + alertPrice + 
                " (Current: $" + String.format("%.2f", stock.getCurrentPrice()) + ")");
        }
    }
    
    @Override
    public String getObserverName() {
        return "PriceAlert[" + alertName + "]";
    }
    
    @Override
    public boolean isActive() {
        return isActive && !hasTriggered;
    }
    
    public void reset() {
        hasTriggered = false;
        System.out.println("🔄 Alert [" + alertName + "] reset");
    }
    
    public void setActive(boolean active) {
        this.isActive = active;
    }
}

// Concrete observer - Trading bot
class TradingBot implements StockObserver {
    private String botName;
    private double buyThreshold;
    private double sellThreshold;
    private int position; // positive = long, negative = short, 0 = no position
    private double averagePrice;
    private boolean isActive;
    
    public TradingBot(String botName, double buyThreshold, double sellThreshold) {
        this.botName = botName;
        this.buyThreshold = buyThreshold;
        this.sellThreshold = sellThreshold;
        this.position = 0;
        this.averagePrice = 0;
        this.isActive = true;
    }
    
    @Override
    public void onPriceUpdate(Stock stock) {
        double currentPrice = stock.getCurrentPrice();
        double priceChangePercent = stock.getPriceChangePercentage();
        
        // Buy signal: price dropped below buy threshold
        if (priceChangePercent <= buyThreshold && position <= 0) {
            buy(stock, 100); // Buy 100 shares
        }
        // Sell signal: price increased above sell threshold
        else if (priceChangePercent >= sellThreshold && position > 0) {
            sell(stock, position); // Sell all shares
        }
    }
    
    private void buy(Stock stock, int shares) {
        if (position == 0) {
            averagePrice = stock.getCurrentPrice();
        } else {
            // Average down
            averagePrice = ((averagePrice * position) + (stock.getCurrentPrice() * shares)) / (position + shares);
        }
        position += shares;
        
        System.out.println("🤖 " + botName + " BUY: " + shares + " shares of " + 
            stock.getSymbol() + " at $" + String.format("%.2f", stock.getCurrentPrice()) + 
            " (Position: " + position + ", Avg: $" + String.format("%.2f", averagePrice) + ")");
    }
    
    private void sell(Stock stock, int shares) {
        position -= shares;
        double profit = (stock.getCurrentPrice() - averagePrice) * shares;
        
        System.out.println("🤖 " + botName + " SELL: " + shares + " shares of " + 
            stock.getSymbol() + " at $" + String.format("%.2f", stock.getCurrentPrice()) + 
            " (P&L: $" + String.format("%.2f", profit) + ", Remaining: " + position + ")");
            
        if (position == 0) {
            averagePrice = 0;
        }
    }
    
    @Override
    public String getObserverName() {
        return "TradingBot[" + botName + "]";
    }
    
    @Override
    public boolean isActive() {
        return isActive;
    }
    
    public void setActive(boolean active) {
        this.isActive = active;
        System.out.println("🤖 " + botName + " is now " + (active ? "active" : "inactive"));
    }
    
    public int getPosition() {
        return position;
    }
    
    public double getAveragePrice() {
        return averagePrice;
    }
}

// Concrete observer - Portfolio tracker
class PortfolioTracker implements StockObserver {
    private String portfolioName;
    private Map<String, Integer> holdings; // symbol -> shares
    private Map<String, Double> costBasis; // symbol -> average cost per share
    private boolean isActive;
    
    public PortfolioTracker(String portfolioName) {
        this.portfolioName = portfolioName;
        this.holdings = new HashMap<>();
        this.costBasis = new HashMap<>();
        this.isActive = true;
    }
    
    @Override
    public void onPriceUpdate(Stock stock) {
        String symbol = stock.getSymbol();
        if (holdings.containsKey(symbol)) {
            updatePortfolioValue(stock);
        }
    }
    
    private void updatePortfolioValue(Stock stock) {
        String symbol = stock.getSymbol();
        int shares = holdings.get(symbol);
        double avgCost = costBasis.get(symbol);
        double currentValue = shares * stock.getCurrentPrice();
        double totalCost = shares * avgCost;
        double unrealizedPnL = currentValue - totalCost;
        
        System.out.println("📋 Portfolio [" + portfolioName + "] " + symbol + ": " +
            shares + " shares @ $" + String.format("%.2f", stock.getCurrentPrice()) + 
            " (Cost: $" + String.format("%.2f", avgCost) + 
            ", P&L: $" + String.format("%.2f", unrealizedPnL) + ")");
    }
    
    public void addHolding(String symbol, int shares, double costPerShare) {
        if (holdings.containsKey(symbol)) {
            // Average cost basis
            int existingShares = holdings.get(symbol);
            double existingCost = costBasis.get(symbol);
            double newAvgCost = ((existingShares * existingCost) + (shares * costPerShare)) / 
                               (existingShares + shares);
            holdings.put(symbol, existingShares + shares);
            costBasis.put(symbol, newAvgCost);
        } else {
            holdings.put(symbol, shares);
            costBasis.put(symbol, costPerShare);
        }
        
        System.out.println("📋 Portfolio [" + portfolioName + "]: Added " + shares + 
            " shares of " + symbol + " at $" + String.format("%.2f", costPerShare));
    }
    
    @Override
    public String getObserverName() {
        return "Portfolio[" + portfolioName + "]";
    }
    
    @Override
    public boolean isActive() {
        return isActive;
    }
    
    public void setActive(boolean active) {
        this.isActive = active;
    }
    
    public Map<String, Integer> getHoldings() {
        return new HashMap<>(holdings);
    }
}

// Concrete observer - Real-time display
class RealTimeDisplay implements StockObserver {
    private String displayName;
    private List<String> watchedSymbols;
    private boolean isActive;
    
    public RealTimeDisplay(String displayName) {
        this.displayName = displayName;
        this.watchedSymbols = new ArrayList<>();
        this.isActive = true;
    }
    
    @Override
    public void onPriceUpdate(Stock stock) {
        watchedSymbols.add(stock.getSymbol());
        displayStockInfo(stock);
    }
    
    private void displayStockInfo(Stock stock) {
        String timestamp = stock.getLastUpdated().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        String priceChangeIcon = stock.getPriceChange() >= 0 ? "📈" : "📉";
        
        System.out.println("💻 Display [" + displayName + "] " + timestamp + " " + 
            priceChangeIcon + " " + stock.toString());
    }
    
    @Override
    public String getObserverName() {
        return "Display[" + displayName + "]";
    }
    
    @Override
    public boolean isActive() {
        return isActive;
    }
    
    public void setActive(boolean active) {
        this.isActive = active;
        System.out.println("💻 Display [" + displayName + "] is now " + (active ? "active" : "inactive"));
    }
    
    public List<String> getWatchedSymbols() {
        return new ArrayList<>(watchedSymbols);
    }
}

// Market data simulator
class MarketDataSimulator {
    private StockExchange exchange;
    
    public MarketDataSimulator(StockExchange exchange) {
        this.exchange = exchange;
    }
    
    public void simulateMarketActivity() {
        System.out.println("\n=== Simulating Market Activity ===");
        
        // Morning volatility
        exchange.updateStockPrice("AAPL", 148.50, 15000);
        exchange.updateStockPrice("GOOGL", 2745.25, 8000);
        exchange.updateStockPrice("TSLA", 898.75, 25000);
        
        // Midday stability
        exchange.updateStockPrice("AAPL", 149.25, 12000);
        exchange.updateStockPrice("GOOGL", 2750.80, 6000);
        
        // Afternoon surge
        exchange.updateStockPrice("TSLA", 925.50, 30000);
        exchange.updateStockPrice("AAPL", 152.75, 20000);
        
        // End of day selloff
        exchange.updateStockPrice("GOOGL", 2735.60, 15000);
        exchange.updateStockPrice("TSLA", 912.25, 18000);
        exchange.updateStockPrice("AAPL", 150.80, 16000);
    }
}

// Demonstration class
public class StockTradingExample {
    public static void main(String[] args) {
        System.out.println("=== Stock Trading Observer Pattern Example ===\n");
        
        // Create stock exchange (subject)
        StockExchange nasdaq = new StockExchange("NASDAQ");
        
        // Add initial stocks
        nasdaq.addStock("AAPL", 150.00);
        nasdaq.addStock("GOOGL", 2750.00);
        nasdaq.addStock("TSLA", 900.00);
        
        // Create observers
        PriceAlert appleHighAlert = new PriceAlert("Apple High", 155.00, true);
        PriceAlert teslaLowAlert = new PriceAlert("Tesla Low", 850.00, false);
        
        TradingBot momentumBot = new TradingBot("MomentumBot", -2.0, 3.0); // Buy on 2% drop, sell on 3% gain
        TradingBot contrarian = new TradingBot("Contrarian", -5.0, 2.0);   // Buy on 5% drop, sell on 2% gain
        
        PortfolioTracker personalPortfolio = new PortfolioTracker("Personal");
        PortfolioTracker retirementPortfolio = new PortfolioTracker("401k");
        
        RealTimeDisplay tradingFloor = new RealTimeDisplay("Trading Floor");
        RealTimeDisplay mobileApp = new RealTimeDisplay("Mobile App");
        
        // Set up subscriptions
        System.out.println("\n--- Setting up Subscriptions ---");
        
        // Alerts
        nasdaq.subscribe("AAPL", appleHighAlert);
        nasdaq.subscribe("TSLA", teslaLowAlert);
        
        // Trading bots
        nasdaq.subscribe("AAPL", momentumBot);
        nasdaq.subscribe("GOOGL", momentumBot);
        nasdaq.subscribe("TSLA", contrarian);
        
        // Portfolio trackers
        nasdaq.subscribe("AAPL", personalPortfolio);
        nasdaq.subscribe("GOOGL", personalPortfolio);
        nasdaq.subscribe("TSLA", retirementPortfolio);
        
        // Displays
        nasdaq.subscribe("AAPL", tradingFloor);
        nasdaq.subscribe("GOOGL", tradingFloor);
        nasdaq.subscribe("TSLA", tradingFloor);
        nasdaq.subscribe("AAPL", mobileApp);
        
        // Set up portfolios
        System.out.println("\n--- Setting up Portfolios ---");
        personalPortfolio.addHolding("AAPL", 100, 145.00);
        personalPortfolio.addHolding("GOOGL", 10, 2700.00);
        retirementPortfolio.addHolding("TSLA", 50, 850.00);
        
        // Simulate market data
        MarketDataSimulator simulator = new MarketDataSimulator(nasdaq);
        simulator.simulateMarketActivity();
        
        // Demonstrate dynamic observer management
        System.out.println("\n=== Dynamic Observer Management ===");
        System.out.println("Deactivating momentum bot...");
        momentumBot.setActive(false);
        
        System.out.println("Unsubscribing mobile app from all stocks...");
        nasdaq.unsubscribeFromAll(mobileApp);
        
        System.out.println("More market activity...");
        nasdaq.updateStockPrice("AAPL", 158.50, 25000); // Should trigger Apple alert
        nasdaq.updateStockPrice("TSLA", 840.00, 20000);  // Should trigger Tesla alert
        
        // Reset and reactivate
        System.out.println("\n=== Reset and Reactivation ===");
        appleHighAlert.reset();
        appleHighAlert.setActive(true);
        momentumBot.setActive(true);
        
        // Subscribe mobile app to Tesla only
        nasdaq.subscribe("TSLA", mobileApp);
        
        // Final market update
        nasdaq.updateStockPrice("TSLA", 950.00, 30000);
        
        // Print final status
        printFinalStatus(nasdaq, momentumBot, contrarian, personalPortfolio, retirementPortfolio);
        
        demonstratePatternBenefits();
    }
    
    private static void printFinalStatus(StockExchange exchange, TradingBot bot1, TradingBot bot2,
                                       PortfolioTracker portfolio1, PortfolioTracker portfolio2) {
        System.out.println("\n=== Final Status ===");
        System.out.println("Exchange: " + exchange.getExchangeName());
        
        // Stock prices
        System.out.println("\nFinal Stock Prices:");
        System.out.println("  " + exchange.getStock("AAPL"));
        System.out.println("  " + exchange.getStock("GOOGL"));
        System.out.println("  " + exchange.getStock("TSLA"));
        
        // Bot positions
        System.out.println("\nBot Positions:");
        System.out.println("  " + bot1.getObserverName() + " - Position: " + bot1.getPosition() + 
            " shares @ $" + String.format("%.2f", bot1.getAveragePrice()));
        System.out.println("  " + bot2.getObserverName() + " - Position: " + bot2.getPosition() + 
            " shares @ $" + String.format("%.2f", bot2.getAveragePrice()));
        
        // Portfolio holdings
        System.out.println("\nPortfolio Holdings:");
        System.out.println("  " + portfolio1.getObserverName() + ": " + portfolio1.getHoldings());
        System.out.println("  " + portfolio2.getObserverName() + ": " + portfolio2.getHoldings());
        
        // Observer counts
        System.out.println("\nObserver Counts:");
        System.out.println("  AAPL: " + exchange.getObserverCount("AAPL") + " observers");
        System.out.println("  GOOGL: " + exchange.getObserverCount("GOOGL") + " observers");
        System.out.println("  TSLA: " + exchange.getObserverCount("TSLA") + " observers");
    }
    
    private static void demonstratePatternBenefits() {
        System.out.println("\n=== Observer Pattern Benefits in Stock Trading ===");
        System.out.println("✓ Real-time Notifications: Instant price updates to all interested parties");
        System.out.println("✓ Loose Coupling: Exchange doesn't know about specific observer implementations");
        System.out.println("✓ Scalability: Can add unlimited observers without modifying core logic");
        System.out.println("✓ Flexibility: Different observers can react differently to same price changes");
        System.out.println("✓ Event-driven Architecture: Perfect for reactive trading systems");
        System.out.println("✓ Multi-subscriber Support: Multiple bots, alerts, and displays per stock");
        System.out.println("✓ Dynamic Management: Observers can be added/removed during trading hours");
        System.out.println("✓ Thread Safety: Uses thread-safe collections for concurrent access");
    }
}