package org.example.designPatterns.observerPattern;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Stock Market Observer Pattern Example
 * 
 * This example demonstrates the Observer pattern in a stock market system
 * where multiple investors monitor stock price changes and react accordingly.
 * The stock market (subject) notifies all interested investors (observers)
 * when stock prices change.
 * 
 * Key Components:
 * - Stock: Data model representing a stock with symbol and price
 * - StockMarket: Subject that manages stocks and price updates
 * - Investor: Observer interface for stock price notifications
 * - Various investor types: Individual, Institutional, Algorithm traders
 * 
 * Features:
 * - Real-time price notifications
 * - Different investor strategies
 * - Portfolio tracking
 * - Market statistics
 * - Trading alerts and automation
 */

// Stock data model
class Stock {
    private String symbol;
    private double price;
    private double previousPrice;
    private long volume;
    private LocalDateTime lastUpdated;
    
    public Stock(String symbol, double initialPrice) {
        this.symbol = symbol;
        this.price = initialPrice;
        this.previousPrice = initialPrice;
        this.volume = 0;
        this.lastUpdated = LocalDateTime.now();
    }
    
    // Getters and setters
    public String getSymbol() { return symbol; }
    
    public double getPrice() { return price; }
    public void setPrice(double price) {
        this.previousPrice = this.price;
        this.price = price;
        this.lastUpdated = LocalDateTime.now();
    }
    
    public double getPreviousPrice() { return previousPrice; }
    
    public long getVolume() { return volume; }
    public void setVolume(long volume) { this.volume = volume; }
    public void addVolume(long additionalVolume) { this.volume += additionalVolume; }
    
    public LocalDateTime getLastUpdated() { return lastUpdated; }
    
    public double getPriceChange() {
        return price - previousPrice;
    }
    
    public double getPriceChangePercentage() {
        if (previousPrice == 0) return 0;
        return ((price - previousPrice) / previousPrice) * 100;
    }
    
    @Override
    public String toString() {
        return String.format("%s: $%.2f (%.2f%%) Vol: %,d", 
            symbol, price, getPriceChangePercentage(), volume);
    }
}

// Observer interface for stock price changes
interface Investor {
    void update(Stock stock);
    String getInvestorName();
    String getInvestorType();
}

// Subject interface for stock market
interface StockMarketSubject {
    void addInvestor(String symbol, Investor investor);
    void removeInvestor(String symbol, Investor investor);
    void notifyInvestors(String symbol);
}

// Concrete stock market (Subject)
class StockMarket implements StockMarketSubject {
    private Map<String, Stock> stocks;
    private Map<String, List<Investor>> investors;
    private String marketName;
    
    public StockMarket(String marketName) {
        this.marketName = marketName;
        this.stocks = new HashMap<>();
        this.investors = new HashMap<>();
    }
    
    @Override
    public void addInvestor(String symbol, Investor investor) {
        investors.computeIfAbsent(symbol, k -> new CopyOnWriteArrayList<>()).add(investor);
        System.out.println("📈 " + investor.getInvestorName() + " (" + investor.getInvestorType() + 
            ") is now watching " + symbol);
    }
    
    @Override
    public void removeInvestor(String symbol, Investor investor) {
        List<Investor> symbolInvestors = investors.get(symbol);
        if (symbolInvestors != null) {
            symbolInvestors.remove(investor);
            System.out.println("📉 " + investor.getInvestorName() + " stopped watching " + symbol);
        }
    }
    
    @Override
    public void notifyInvestors(String symbol) {
        List<Investor> symbolInvestors = investors.get(symbol);
        if (symbolInvestors != null) {
            Stock stock = stocks.get(symbol);
            if (stock != null) {
                System.out.println("\n💹 Market Update: " + stock + " - Notifying " + 
                    symbolInvestors.size() + " investors");
                for (Investor investor : symbolInvestors) {
                    try {
                        investor.update(stock);
                    } catch (Exception e) {
                        System.err.println("Error notifying investor " + investor.getInvestorName() + 
                            ": " + e.getMessage());
                    }
                }
                System.out.println("--- End of notifications ---\n");
            }
        }
    }
    
    public void addStock(String symbol, double initialPrice) {
        stocks.put(symbol, new Stock(symbol, initialPrice));
        System.out.println("🏢 " + marketName + ": Listed " + symbol + " at $" + initialPrice);
    }
    
    public void updateStockPrice(String symbol, double newPrice, long volume) {
        Stock stock = stocks.get(symbol);
        if (stock != null) {
            stock.setPrice(newPrice);
            stock.addVolume(volume);
            notifyInvestors(symbol);
        }
    }
    
    public Stock getStock(String symbol) {
        return stocks.get(symbol);
    }
    
    public int getInvestorCount(String symbol) {
        List<Investor> symbolInvestors = investors.get(symbol);
        return symbolInvestors != null ? symbolInvestors.size() : 0;
    }
    
    public String getMarketName() {
        return marketName;
    }
    
    public void printMarketSummary() {
        System.out.println("\n=== " + marketName + " Market Summary ===");
        for (Stock stock : stocks.values()) {
            System.out.println("  " + stock + " (Watchers: " + getInvestorCount(stock.getSymbol()) + ")");
        }
        System.out.println("=====================================\n");
    }
}

// Concrete observer - Individual investor
class IndividualInvestor implements Investor {
    private String name;
    private double cashBalance;
    private Map<String, Integer> portfolio; // symbol -> shares owned
    private Map<String, Double> alertThresholds; // symbol -> price threshold
    
    public IndividualInvestor(String name, double initialCash) {
        this.name = name;
        this.cashBalance = initialCash;
        this.portfolio = new HashMap<>();
        this.alertThresholds = new HashMap<>();
    }
    
    @Override
    public void update(Stock stock) {
        System.out.println("👤 [" + name + "] Received update: " + stock);
        
        // Check if we own this stock
        if (portfolio.containsKey(stock.getSymbol())) {
            int shares = portfolio.get(stock.getSymbol());
            double currentValue = shares * stock.getPrice();
            System.out.println("   💼 Portfolio impact: " + shares + " shares worth $" + 
                String.format("%.2f", currentValue));
        }
        
        // Check price alerts
        if (alertThresholds.containsKey(stock.getSymbol())) {
            double threshold = alertThresholds.get(stock.getSymbol());
            if (stock.getPrice() >= threshold) {
                System.out.println("   🚨 ALERT: " + stock.getSymbol() + " hit target price $" + threshold);
            }
        }
        
        // Simple investment strategy
        makeInvestmentDecision(stock);
    }
    
    private void makeInvestmentDecision(Stock stock) {
        double priceChange = stock.getPriceChangePercentage();
        
        // Buy on significant dips if we have cash
        if (priceChange < -5.0 && cashBalance >= stock.getPrice() * 10) {
            buyStock(stock, 10);
        }
        // Sell on significant gains if we own the stock
        else if (priceChange > 8.0 && portfolio.containsKey(stock.getSymbol())) {
            int shares = portfolio.get(stock.getSymbol());
            sellStock(stock, Math.min(shares, 5));
        }
    }
    
    private void buyStock(Stock stock, int shares) {
        double cost = shares * stock.getPrice();
        if (cashBalance >= cost) {
            cashBalance -= cost;
            portfolio.merge(stock.getSymbol(), shares, Integer::sum);
            System.out.println("   ✅ BOUGHT: " + shares + " shares of " + stock.getSymbol() + 
                " at $" + String.format("%.2f", stock.getPrice()) + 
                " (Cash left: $" + String.format("%.2f", cashBalance) + ")");
        }
    }
    
    private void sellStock(Stock stock, int shares) {
        if (portfolio.containsKey(stock.getSymbol()) && portfolio.get(stock.getSymbol()) >= shares) {
            double proceeds = shares * stock.getPrice();
            cashBalance += proceeds;
            portfolio.compute(stock.getSymbol(), (k, v) -> v - shares);
            if (portfolio.get(stock.getSymbol()) == 0) {
                portfolio.remove(stock.getSymbol());
            }
            System.out.println("   ✅ SOLD: " + shares + " shares of " + stock.getSymbol() + 
                " at $" + String.format("%.2f", stock.getPrice()) + 
                " (Cash now: $" + String.format("%.2f", cashBalance) + ")");
        }
    }
    
    public void setAlert(String symbol, double priceThreshold) {
        alertThresholds.put(symbol, priceThreshold);
        System.out.println("🔔 " + name + " set alert for " + symbol + " at $" + priceThreshold);
    }
    
    @Override
    public String getInvestorName() {
        return name;
    }
    
    @Override
    public String getInvestorType() {
        return "Individual";
    }
    
    public double getCashBalance() {
        return cashBalance;
    }
    
    public Map<String, Integer> getPortfolio() {
        return new HashMap<>(portfolio);
    }
}

// Concrete observer - Institutional investor
class InstitutionalInvestor implements Investor {
    private String institutionName;
    private double totalAssets;
    private Map<String, Double> targetAllocations; // symbol -> target percentage
    private Map<String, Integer> currentHoldings; // symbol -> shares
    
    public InstitutionalInvestor(String institutionName, double totalAssets) {
        this.institutionName = institutionName;
        this.totalAssets = totalAssets;
        this.targetAllocations = new HashMap<>();
        this.currentHoldings = new HashMap<>();
    }
    
    @Override
    public void update(Stock stock) {
        System.out.println("🏛️ [" + institutionName + "] Analyzing: " + stock);
        
        // Calculate current allocation
        String symbol = stock.getSymbol();
        if (targetAllocations.containsKey(symbol)) {
            double targetAllocation = targetAllocations.get(symbol);
            int currentShares = currentHoldings.getOrDefault(symbol, 0);
            double currentValue = currentShares * stock.getPrice();
            double currentAllocation = (currentValue / totalAssets) * 100;
            
            System.out.println("   📊 Current allocation: " + String.format("%.2f", currentAllocation) + 
                "%, Target: " + String.format("%.2f", targetAllocation) + "%");
            
            // Rebalancing logic
            if (Math.abs(currentAllocation - targetAllocation) > 2.0) {
                rebalancePosition(stock, targetAllocation, currentAllocation);
            }
        }
    }
    
    private void rebalancePosition(Stock stock, double targetAllocation, double currentAllocation) {
        if (currentAllocation < targetAllocation - 2.0) {
            // Need to buy more
            double targetValue = (targetAllocation / 100) * totalAssets;
            int currentShares = currentHoldings.getOrDefault(stock.getSymbol(), 0);
            int targetShares = (int) (targetValue / stock.getPrice());
            int sharesToBuy = targetShares - currentShares;
            
            if (sharesToBuy > 0) {
                currentHoldings.put(stock.getSymbol(), targetShares);
                System.out.println("   📈 REBALANCE BUY: " + sharesToBuy + " shares of " + 
                    stock.getSymbol() + " (Total: " + targetShares + ")");
            }
        } else if (currentAllocation > targetAllocation + 2.0) {
            // Need to sell some
            double targetValue = (targetAllocation / 100) * totalAssets;
            int targetShares = (int) (targetValue / stock.getPrice());
            int currentShares = currentHoldings.get(stock.getSymbol());
            int sharesToSell = currentShares - targetShares;
            
            if (sharesToSell > 0) {
                currentHoldings.put(stock.getSymbol(), targetShares);
                System.out.println("   📉 REBALANCE SELL: " + sharesToSell + " shares of " + 
                    stock.getSymbol() + " (Remaining: " + targetShares + ")");
            }
        }
    }
    
    public void setTargetAllocation(String symbol, double percentage) {
        targetAllocations.put(symbol, percentage);
        System.out.println("🎯 " + institutionName + " set target allocation for " + 
            symbol + ": " + percentage + "%");
    }
    
    @Override
    public String getInvestorName() {
        return institutionName;
    }
    
    @Override
    public String getInvestorType() {
        return "Institutional";
    }
    
    public Map<String, Integer> getCurrentHoldings() {
        return new HashMap<>(currentHoldings);
    }
}

// Concrete observer - Algorithmic trader
class AlgorithmicTrader implements Investor {
    private String algorithmName;
    private double buyThreshold;
    private double sellThreshold;
    private int maxPosition;
    private Map<String, Integer> positions; // symbol -> current position
    private Map<String, Double> averagePrices; // symbol -> average buy price
    
    public AlgorithmicTrader(String algorithmName, double buyThreshold, double sellThreshold, int maxPosition) {
        this.algorithmName = algorithmName;
        this.buyThreshold = buyThreshold;
        this.sellThreshold = sellThreshold;
        this.maxPosition = maxPosition;
        this.positions = new HashMap<>();
        this.averagePrices = new HashMap<>();
    }
    
    @Override
    public void update(Stock stock) {
        System.out.println("🤖 [" + algorithmName + "] Processing: " + stock);
        
        double priceChangePercent = stock.getPriceChangePercentage();
        String symbol = stock.getSymbol();
        int currentPosition = positions.getOrDefault(symbol, 0);
        
        // Buy signal
        if (priceChangePercent <= buyThreshold && currentPosition < maxPosition) {
            int sharesToBuy = Math.min(100, maxPosition - currentPosition);
            executeOrder(stock, sharesToBuy, "BUY");
        }
        // Sell signal
        else if (priceChangePercent >= sellThreshold && currentPosition > 0) {
            int sharesToSell = Math.min(100, currentPosition);
            executeOrder(stock, sharesToSell, "SELL");
        }
        
        // Risk management
        checkStopLoss(stock);
    }
    
    private void executeOrder(Stock stock, int shares, String action) {
        String symbol = stock.getSymbol();
        
        if ("BUY".equals(action)) {
            int currentPosition = positions.getOrDefault(symbol, 0);
            double currentAvg = averagePrices.getOrDefault(symbol, 0.0);
            
            // Calculate new average price
            double newAvg = currentPosition == 0 ? stock.getPrice() :
                ((currentAvg * currentPosition) + (stock.getPrice() * shares)) / (currentPosition + shares);
            
            positions.put(symbol, currentPosition + shares);
            averagePrices.put(symbol, newAvg);
            
            System.out.println("   🟢 ALGO BUY: " + shares + " shares at $" + 
                String.format("%.2f", stock.getPrice()) + " (Position: " + 
                positions.get(symbol) + ", Avg: $" + String.format("%.2f", newAvg) + ")");
            
        } else if ("SELL".equals(action)) {
            int currentPosition = positions.get(symbol);
            double avgPrice = averagePrices.get(symbol);
            double profit = (stock.getPrice() - avgPrice) * shares;
            
            positions.put(symbol, currentPosition - shares);
            if (positions.get(symbol) == 0) {
                positions.remove(symbol);
                averagePrices.remove(symbol);
            }
            
            System.out.println("   🔴 ALGO SELL: " + shares + " shares at $" + 
                String.format("%.2f", stock.getPrice()) + " (P&L: $" + 
                String.format("%.2f", profit) + ", Remaining: " + 
                positions.getOrDefault(symbol, 0) + ")");
        }
    }
    
    private void checkStopLoss(Stock stock) {
        String symbol = stock.getSymbol();
        if (positions.containsKey(symbol)) {
            double avgPrice = averagePrices.get(symbol);
            double currentPrice = stock.getPrice();
            double lossPercent = ((currentPrice - avgPrice) / avgPrice) * 100;
            
            if (lossPercent < -10.0) { // 10% stop loss
                int position = positions.get(symbol);
                executeOrder(stock, position, "SELL");
                System.out.println("   🛑 STOP LOSS triggered for " + symbol);
            }
        }
    }
    
    @Override
    public String getInvestorName() {
        return algorithmName;
    }
    
    @Override
    public String getInvestorType() {
        return "Algorithm";
    }
    
    public Map<String, Integer> getPositions() {
        return new HashMap<>(positions);
    }
}

// Concrete observer - Market analyst
class MarketAnalyst implements Investor {
    private String analystName;
    private Map<String, List<Double>> priceHistory; // symbol -> price history
    private int maxHistorySize;
    
    public MarketAnalyst(String analystName, int maxHistorySize) {
        this.analystName = analystName;
        this.maxHistorySize = maxHistorySize;
        this.priceHistory = new HashMap<>();
    }
    
    @Override
    public void update(Stock stock) {
        String symbol = stock.getSymbol();
        
        // Update price history
        priceHistory.computeIfAbsent(symbol, k -> new ArrayList<>()).add(stock.getPrice());
        List<Double> history = priceHistory.get(symbol);
        
        // Keep only recent history
        if (history.size() > maxHistorySize) {
            history.remove(0);
        }
        
        if (history.size() >= 3) {
            analyzeStock(stock, history);
        }
    }
    
    private void analyzeStock(Stock stock, List<Double> history) {
        String symbol = stock.getSymbol();
        
        // Calculate moving average
        double sum = history.stream().mapToDouble(Double::doubleValue).sum();
        double movingAverage = sum / history.size();
        
        // Determine trend
        String trend = "SIDEWAYS";
        if (stock.getPrice() > movingAverage * 1.02) {
            trend = "UPTREND";
        } else if (stock.getPrice() < movingAverage * 0.98) {
            trend = "DOWNTREND";
        }
        
        // Calculate volatility (simplified)
        double variance = history.stream()
            .mapToDouble(price -> Math.pow(price - movingAverage, 2))
            .average().orElse(0.0);
        double volatility = Math.sqrt(variance);
        
        System.out.println("📊 [" + analystName + "] Analysis for " + symbol + ":");
        System.out.println("   Moving Average: $" + String.format("%.2f", movingAverage));
        System.out.println("   Trend: " + trend);
        System.out.println("   Volatility: $" + String.format("%.2f", volatility));
        
        // Generate recommendations
        generateRecommendation(stock, trend, movingAverage);
    }
    
    private void generateRecommendation(Stock stock, String trend, double movingAverage) {
        String recommendation = "HOLD";
        
        if ("UPTREND".equals(trend) && stock.getPriceChangePercentage() > 0) {
            recommendation = "BUY";
        } else if ("DOWNTREND".equals(trend) && stock.getPriceChangePercentage() < -3) {
            recommendation = "SELL";
        } else if (stock.getPrice() < movingAverage * 0.95) {
            recommendation = "BUY (Oversold)";
        }
        
        System.out.println("   💡 Recommendation: " + recommendation);
    }
    
    @Override
    public String getInvestorName() {
        return analystName;
    }
    
    @Override
    public String getInvestorType() {
        return "Analyst";
    }
    
    public Map<String, List<Double>> getPriceHistory() {
        return new HashMap<>(priceHistory);
    }
}

// Market simulator for generating price movements
class MarketSimulator {
    private StockMarket market;
    
    public MarketSimulator(StockMarket market) {
        this.market = market;
    }
    
    public void simulateMarketDay() {
        System.out.println("\n🌅 === MARKET OPENING ===");
        
        // Morning volatility
        market.updateStockPrice("AAPL", 152.25, 12000);
        market.updateStockPrice("GOOGL", 2725.50, 8000);
        market.updateStockPrice("TSLA", 885.75, 20000);
        market.updateStockPrice("MSFT", 338.90, 15000);
        
        // Midday movements
        market.updateStockPrice("AAPL", 148.75, 18000); // Apple drops
        market.updateStockPrice("GOOGL", 2750.25, 10000); // Google rises
        market.updateStockPrice("MSFT", 342.15, 12000); // Microsoft up
        
        // Afternoon surge
        market.updateStockPrice("TSLA", 925.50, 35000); // Tesla jumps
        market.updateStockPrice("AAPL", 155.80, 25000); // Apple recovers strong
        
        // End of day profit-taking
        market.updateStockPrice("GOOGL", 2738.90, 14000);
        market.updateStockPrice("TSLA", 915.25, 22000);
        market.updateStockPrice("MSFT", 340.50, 16000);
        market.updateStockPrice("AAPL", 153.45, 20000);
        
        System.out.println("🌇 === MARKET CLOSING ===\n");
    }
}

// Demonstration class
public class StockMarketExample {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Stock Market Observer Pattern Example ===\n");
        
        // Create the stock market (subject)
        StockMarket nasdaq = new StockMarket("NASDAQ");
        
        // Add stocks to the market
        nasdaq.addStock("AAPL", 150.00);
        nasdaq.addStock("GOOGL", 2700.00);
        nasdaq.addStock("TSLA", 900.00);
        nasdaq.addStock("MSFT", 335.00);
        
        // Create different types of investors (observers)
        IndividualInvestor john = new IndividualInvestor("John Smith", 10000.0);
        IndividualInvestor sarah = new IndividualInvestor("Sarah Johnson", 15000.0);
        
        InstitutionalInvestor vanguard = new InstitutionalInvestor("Vanguard Fund", 1000000.0);
        InstitutionalInvestor blackrock = new InstitutionalInvestor("BlackRock", 2000000.0);
        
        AlgorithmicTrader momentumBot = new AlgorithmicTrader("MomentumBot", -3.0, 5.0, 500);
        AlgorithmicTrader arbitrageBot = new AlgorithmicTrader("ArbitrageBot", -1.5, 2.0, 1000);
        
        MarketAnalyst goldmanAnalyst = new MarketAnalyst("Goldman Sachs Research", 10);
        
        // Set up subscriptions
        System.out.println("\n--- Setting up Investor Subscriptions ---");
        
        // Individual investors
        nasdaq.addInvestor("AAPL", john);
        nasdaq.addInvestor("TSLA", john);
        nasdaq.addInvestor("GOOGL", sarah);
        nasdaq.addInvestor("MSFT", sarah);
        
        // Set price alerts
        john.setAlert("AAPL", 155.0);
        sarah.setAlert("GOOGL", 2750.0);
        
        // Institutional investors with target allocations
        vanguard.setTargetAllocation("AAPL", 25.0);
        vanguard.setTargetAllocation("GOOGL", 20.0);
        vanguard.setTargetAllocation("MSFT", 30.0);
        
        blackrock.setTargetAllocation("AAPL", 20.0);
        blackrock.setTargetAllocation("TSLA", 15.0);
        blackrock.setTargetAllocation("GOOGL", 25.0);
        
        nasdaq.addInvestor("AAPL", vanguard);
        nasdaq.addInvestor("GOOGL", vanguard);
        nasdaq.addInvestor("MSFT", vanguard);
        nasdaq.addInvestor("AAPL", blackrock);
        nasdaq.addInvestor("TSLA", blackrock);
        nasdaq.addInvestor("GOOGL", blackrock);
        
        // Algorithmic traders
        nasdaq.addInvestor("AAPL", momentumBot);
        nasdaq.addInvestor("GOOGL", momentumBot);
        nasdaq.addInvestor("TSLA", arbitrageBot);
        nasdaq.addInvestor("MSFT", arbitrageBot);
        
        // Market analyst watching all stocks
        nasdaq.addInvestor("AAPL", goldmanAnalyst);
        nasdaq.addInvestor("GOOGL", goldmanAnalyst);
        nasdaq.addInvestor("TSLA", goldmanAnalyst);
        nasdaq.addInvestor("MSFT", goldmanAnalyst);
        
        nasdaq.printMarketSummary();
        
        // Simulate market activity
        MarketSimulator simulator = new MarketSimulator(nasdaq);
        simulator.simulateMarketDay();
        
        // Add some delays for better readability
        Thread.sleep(1000);
        
        // Demonstrate dynamic investor management
        System.out.println("=== Dynamic Investor Management ===");
        
        // Add a new day trader
        IndividualInvestor dayTrader = new IndividualInvestor("Day Trader Mike", 5000.0);
        nasdaq.addInvestor("TSLA", dayTrader);
        dayTrader.setAlert("TSLA", 920.0);
        
        // Remove an algorithmic trader
        nasdaq.removeInvestor("TSLA", arbitrageBot);
        
        // Generate some final market activity
        System.out.println("\n--- After Hours Trading ---");
        nasdaq.updateStockPrice("TSLA", 935.75, 8000); // Should trigger day trader's alert
        nasdaq.updateStockPrice("AAPL", 157.25, 5000);  // Should trigger John's alert
        
        // Print final statistics
        printFinalStatistics(nasdaq, john, sarah, vanguard, momentumBot, goldmanAnalyst);
        
        demonstratePatternBenefits();
    }
    
    private static void printFinalStatistics(StockMarket market, IndividualInvestor john, 
                                           IndividualInvestor sarah, InstitutionalInvestor vanguard,
                                           AlgorithmicTrader bot, MarketAnalyst analyst) {
        System.out.println("\n=== Final Investment Statistics ===");
        
        // Market summary
        market.printMarketSummary();
        
        // Individual investor portfolios
        System.out.println("Individual Investor Portfolios:");
        System.out.println("  " + john.getInvestorName() + ":");
        System.out.println("    Cash: $" + String.format("%.2f", john.getCashBalance()));
        System.out.println("    Portfolio: " + john.getPortfolio());
        
        System.out.println("  " + sarah.getInvestorName() + ":");
        System.out.println("    Cash: $" + String.format("%.2f", sarah.getCashBalance()));
        System.out.println("    Portfolio: " + sarah.getPortfolio());
        
        // Institutional holdings
        System.out.println("\nInstitutional Holdings:");
        System.out.println("  " + vanguard.getInvestorName() + ": " + vanguard.getCurrentHoldings());
        
        // Algorithmic trader positions
        System.out.println("\nAlgorithmic Trader Positions:");
        System.out.println("  " + bot.getInvestorName() + ": " + bot.getPositions());
        
        // Analyst price history sample
        System.out.println("\nMarket Analysis Data Points Collected:");
        Map<String, List<Double>> history = analyst.getPriceHistory();
        for (Map.Entry<String, List<Double>> entry : history.entrySet()) {
            System.out.println("  " + entry.getKey() + ": " + entry.getValue().size() + " data points");
        }
    }
    
    private static void demonstratePatternBenefits() {
        System.out.println("\n=== Observer Pattern Benefits in Stock Market ===");
        System.out.println("✓ Real-time Updates: All investors receive immediate price notifications");
        System.out.println("✓ Loose Coupling: Market doesn't know about specific investor strategies");
        System.out.println("✓ Scalability: Can add unlimited investors without changing market code");
        System.out.println("✓ Flexibility: Different investor types react differently to same price changes");
        System.out.println("✓ Dynamic Subscription: Investors can start/stop watching stocks anytime");
        System.out.println("✓ Parallel Processing: Multiple strategies can execute simultaneously");
        System.out.println("✓ Event-driven Architecture: Perfect for reactive trading systems");
        System.out.println("✓ Separation of Concerns: Market logic separate from investment strategies");
        System.out.println("✓ Easy Testing: Individual investor strategies can be tested in isolation");
        System.out.println("✓ Extensibility: New investor types can be added without modification");
    }
}