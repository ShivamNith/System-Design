package org.example.designPatterns.templatePattern;

import java.io.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Template Method Pattern Example: Data Mining Application
 * 
 * This example demonstrates how different data sources (CSV, Database, API)
 * can follow the same data mining algorithm structure while implementing
 * specific data extraction and processing logic.
 */

// Data classes
class DataRecord {
    private final Map<String, Object> fields;
    private final LocalDateTime timestamp;
    
    public DataRecord(Map<String, Object> fields) {
        this.fields = new HashMap<>(fields);
        this.timestamp = LocalDateTime.now();
    }
    
    public Object getField(String name) {
        return fields.get(name);
    }
    
    public void setField(String name, Object value) {
        fields.put(name, value);
    }
    
    public Set<String> getFieldNames() {
        return fields.keySet();
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    @Override
    public String toString() {
        return "DataRecord{" + fields + ", timestamp=" + timestamp + "}";
    }
}

class DataSet {
    private final List<DataRecord> records;
    private final Map<String, Object> metadata;
    
    public DataSet() {
        this.records = new ArrayList<>();
        this.metadata = new HashMap<>();
    }
    
    public void addRecord(DataRecord record) {
        records.add(record);
    }
    
    public List<DataRecord> getRecords() {
        return new ArrayList<>(records);
    }
    
    public int size() {
        return records.size();
    }
    
    public void addMetadata(String key, Object value) {
        metadata.put(key, value);
    }
    
    public Object getMetadata(String key) {
        return metadata.get(key);
    }
    
    public boolean isEmpty() {
        return records.isEmpty();
    }
}

class MiningResult {
    private final DataSet processedData;
    private final Map<String, Object> insights;
    private final List<String> anomalies;
    private final boolean successful;
    
    public MiningResult(DataSet processedData, Map<String, Object> insights, 
                       List<String> anomalies, boolean successful) {
        this.processedData = processedData;
        this.insights = new HashMap<>(insights);
        this.anomalies = new ArrayList<>(anomalies);
        this.successful = successful;
    }
    
    public static MiningResult success(DataSet data, Map<String, Object> insights, List<String> anomalies) {
        return new MiningResult(data, insights, anomalies, true);
    }
    
    public static MiningResult failure(String error) {
        Map<String, Object> errorInsights = new HashMap<>();
        errorInsights.put("error", error);
        return new MiningResult(new DataSet(), errorInsights, new ArrayList<>(), false);
    }
    
    public DataSet getProcessedData() { return processedData; }
    public Map<String, Object> getInsights() { return insights; }
    public List<String> getAnomalies() { return anomalies; }
    public boolean isSuccessful() { return successful; }
}

// Abstract Data Miner Template
abstract class DataMiner {
    
    /**
     * Template method defining the data mining algorithm
     */
    public final MiningResult mineData() {
        System.out.println("Starting data mining process...");
        
        try {
            // Step 1: Extract data from source
            DataSet rawData = extractData();
            if (rawData.isEmpty()) {
                return MiningResult.failure("No data extracted from source");
            }
            
            // Step 2: Validate and clean data
            DataSet cleanedData = validateAndCleanData(rawData);
            
            // Step 3: Transform data if needed
            if (shouldTransformData(cleanedData)) {
                cleanedData = transformData(cleanedData);
            }
            
            // Step 4: Apply mining algorithms
            Map<String, Object> insights = applyMiningAlgorithms(cleanedData);
            
            // Step 5: Detect anomalies
            List<String> anomalies = detectAnomalies(cleanedData);
            
            // Step 6: Post-process results
            MiningResult result = postProcessResults(cleanedData, insights, anomalies);
            
            // Step 7: Save results if configured
            if (shouldSaveResults()) {
                saveResults(result);
            }
            
            System.out.println("Data mining completed successfully");
            return result;
            
        } catch (Exception e) {
            System.err.println("Data mining failed: " + e.getMessage());
            return MiningResult.failure(e.getMessage());
        }
    }
    
    // Abstract methods that subclasses must implement
    protected abstract DataSet extractData() throws Exception;
    protected abstract Map<String, Object> applyMiningAlgorithms(DataSet data);
    protected abstract void saveResults(MiningResult result) throws Exception;
    
    // Hook methods with default implementations
    protected DataSet validateAndCleanData(DataSet rawData) {
        System.out.println("Applying default data validation and cleaning...");
        DataSet cleanedData = new DataSet();
        
        for (DataRecord record : rawData.getRecords()) {
            if (isValidRecord(record)) {
                DataRecord cleanedRecord = cleanRecord(record);
                cleanedData.addRecord(cleanedRecord);
            }
        }
        
        System.out.println("Cleaned " + cleanedData.size() + " records from " + rawData.size() + " raw records");
        return cleanedData;
    }
    
    protected boolean shouldTransformData(DataSet data) {
        return false; // Default: no transformation needed
    }
    
    protected DataSet transformData(DataSet data) {
        System.out.println("Applying default data transformation...");
        return data; // Default: no transformation
    }
    
    protected List<String> detectAnomalies(DataSet data) {
        System.out.println("Applying default anomaly detection...");
        List<String> anomalies = new ArrayList<>();
        
        // Simple anomaly detection: records with null values
        for (int i = 0; i < data.getRecords().size(); i++) {
            DataRecord record = data.getRecords().get(i);
            for (String fieldName : record.getFieldNames()) {
                if (record.getField(fieldName) == null) {
                    anomalies.add("Record " + i + " has null value in field: " + fieldName);
                }
            }
        }
        
        return anomalies;
    }
    
    protected MiningResult postProcessResults(DataSet data, Map<String, Object> insights, List<String> anomalies) {
        // Add metadata
        data.addMetadata("processing_time", LocalDateTime.now());
        data.addMetadata("record_count", data.size());
        data.addMetadata("anomaly_count", anomalies.size());
        
        return MiningResult.success(data, insights, anomalies);
    }
    
    protected boolean shouldSaveResults() {
        return true; // Default: save results
    }
    
    // Helper methods
    private boolean isValidRecord(DataRecord record) {
        // Basic validation: record should have at least one non-null field
        return record.getFieldNames().stream()
                .anyMatch(field -> record.getField(field) != null);
    }
    
    private DataRecord cleanRecord(DataRecord record) {
        Map<String, Object> cleanedFields = new HashMap<>();
        
        for (String fieldName : record.getFieldNames()) {
            Object value = record.getField(fieldName);
            if (value instanceof String) {
                cleanedFields.put(fieldName, ((String) value).trim());
            } else {
                cleanedFields.put(fieldName, value);
            }
        }
        
        return new DataRecord(cleanedFields);
    }
}

// Concrete implementation for CSV data source
class CSVDataMiner extends DataMiner {
    private final String csvFilePath;
    private final String delimiter;
    
    public CSVDataMiner(String csvFilePath, String delimiter) {
        this.csvFilePath = csvFilePath;
        this.delimiter = delimiter;
    }
    
    @Override
    protected DataSet extractData() throws Exception {
        System.out.println("Extracting data from CSV file: " + csvFilePath);
        DataSet dataSet = new DataSet();
        
        // Simulate CSV reading (in real implementation, use a CSV library)
        List<String> lines = simulateCSVReading();
        
        if (lines.isEmpty()) {
            return dataSet;
        }
        
        // First line as headers
        String[] headers = lines.get(0).split(delimiter);
        
        // Process data lines
        for (int i = 1; i < lines.size(); i++) {
            String[] values = lines.get(i).split(delimiter);
            Map<String, Object> fields = new HashMap<>();
            
            for (int j = 0; j < Math.min(headers.length, values.length); j++) {
                fields.put(headers[j].trim(), parseValue(values[j].trim()));
            }
            
            dataSet.addRecord(new DataRecord(fields));
        }
        
        dataSet.addMetadata("source", "CSV");
        dataSet.addMetadata("file_path", csvFilePath);
        return dataSet;
    }
    
    @Override
    protected boolean shouldTransformData(DataSet data) {
        // CSV data often needs transformation for numeric analysis
        return true;
    }
    
    @Override
    protected DataSet transformData(DataSet data) {
        System.out.println("Transforming CSV data for analysis...");
        DataSet transformedData = new DataSet();
        
        for (DataRecord record : data.getRecords()) {
            Map<String, Object> transformedFields = new HashMap<>();
            
            for (String fieldName : record.getFieldNames()) {
                Object value = record.getField(fieldName);
                
                // Try to convert string numbers to actual numbers
                if (value instanceof String) {
                    String strValue = (String) value;
                    try {
                        if (strValue.contains(".")) {
                            transformedFields.put(fieldName, Double.parseDouble(strValue));
                        } else {
                            transformedFields.put(fieldName, Integer.parseInt(strValue));
                        }
                    } catch (NumberFormatException e) {
                        transformedFields.put(fieldName, value);
                    }
                } else {
                    transformedFields.put(fieldName, value);
                }
            }
            
            transformedData.addRecord(new DataRecord(transformedFields));
        }
        
        return transformedData;
    }
    
    @Override
    protected Map<String, Object> applyMiningAlgorithms(DataSet data) {
        System.out.println("Applying CSV-specific mining algorithms...");
        Map<String, Object> insights = new HashMap<>();
        
        // Calculate basic statistics
        Map<String, Double> numericAverages = new HashMap<>();
        Map<String, Integer> fieldCounts = new HashMap<>();
        
        for (DataRecord record : data.getRecords()) {
            for (String fieldName : record.getFieldNames()) {
                Object value = record.getField(fieldName);
                
                fieldCounts.put(fieldName, fieldCounts.getOrDefault(fieldName, 0) + 1);
                
                if (value instanceof Number) {
                    double numValue = ((Number) value).doubleValue();
                    numericAverages.put(fieldName, 
                        numericAverages.getOrDefault(fieldName, 0.0) + numValue);
                }
            }
        }
        
        // Calculate averages
        for (String field : numericAverages.keySet()) {
            int count = fieldCounts.get(field);
            if (count > 0) {
                numericAverages.put(field, numericAverages.get(field) / count);
            }
        }
        
        insights.put("numeric_averages", numericAverages);
        insights.put("total_records", data.size());
        insights.put("field_counts", fieldCounts);
        
        return insights;
    }
    
    @Override
    protected void saveResults(MiningResult result) throws Exception {
        System.out.println("Saving CSV mining results to file...");
        String outputPath = csvFilePath.replace(".csv", "_results.txt");
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(outputPath))) {
            writer.println("CSV Data Mining Results");
            writer.println("======================");
            writer.println("Source: " + csvFilePath);
            writer.println("Records processed: " + result.getProcessedData().size());
            writer.println("Anomalies found: " + result.getAnomalies().size());
            writer.println();
            
            writer.println("Insights:");
            for (Map.Entry<String, Object> entry : result.getInsights().entrySet()) {
                writer.println("  " + entry.getKey() + ": " + entry.getValue());
            }
            
            if (!result.getAnomalies().isEmpty()) {
                writer.println("\nAnomalies:");
                for (String anomaly : result.getAnomalies()) {
                    writer.println("  - " + anomaly);
                }
            }
        }
        
        System.out.println("Results saved to: " + outputPath);
    }
    
    // Helper methods
    private List<String> simulateCSVReading() {
        // Simulate reading a CSV file
        return Arrays.asList(
            "name,age,salary,department",
            "John Doe,30,50000,Engineering",
            "Jane Smith,25,55000,Marketing",
            "Bob Johnson,35,60000,Engineering",
            "Alice Brown,28,52000,Sales",
            "Charlie Wilson,32,58000,Marketing"
        );
    }
    
    private Object parseValue(String value) {
        if (value.isEmpty() || "null".equalsIgnoreCase(value)) {
            return null;
        }
        return value;
    }
}

// Concrete implementation for Database data source
class DatabaseDataMiner extends DataMiner {
    private final String connectionString;
    private final String query;
    
    public DatabaseDataMiner(String connectionString, String query) {
        this.connectionString = connectionString;
        this.query = query;
    }
    
    @Override
    protected DataSet extractData() throws Exception {
        System.out.println("Extracting data from database...");
        DataSet dataSet = new DataSet();
        
        // Simulate database connection and query execution
        List<Map<String, Object>> rows = simulateDatabaseQuery();
        
        for (Map<String, Object> row : rows) {
            dataSet.addRecord(new DataRecord(row));
        }
        
        dataSet.addMetadata("source", "Database");
        dataSet.addMetadata("connection", connectionString);
        dataSet.addMetadata("query", query);
        
        return dataSet;
    }
    
    @Override
    protected DataSet validateAndCleanData(DataSet rawData) {
        System.out.println("Applying database-specific validation and cleaning...");
        DataSet cleanedData = super.validateAndCleanData(rawData);
        
        // Additional database-specific cleaning
        for (DataRecord record : cleanedData.getRecords()) {
            // Handle database-specific data types
            for (String fieldName : record.getFieldNames()) {
                Object value = record.getField(fieldName);
                if (value instanceof Timestamp) {
                    record.setField(fieldName, ((Timestamp) value).toLocalDateTime());
                }
            }
        }
        
        return cleanedData;
    }
    
    @Override
    protected List<String> detectAnomalies(DataSet data) {
        System.out.println("Applying database-specific anomaly detection...");
        List<String> anomalies = super.detectAnomalies(data);
        
        // Additional database-specific anomaly detection
        for (int i = 0; i < data.getRecords().size(); i++) {
            DataRecord record = data.getRecords().get(i);
            
            // Check for referential integrity issues
            Object id = record.getField("id");
            if (id != null && (Integer) id < 0) {
                anomalies.add("Record " + i + " has invalid ID: " + id);
            }
        }
        
        return anomalies;
    }
    
    @Override
    protected Map<String, Object> applyMiningAlgorithms(DataSet data) {
        System.out.println("Applying database-specific mining algorithms...");
        Map<String, Object> insights = new HashMap<>();
        
        // Group data by categories
        Map<String, List<DataRecord>> groupedData = new HashMap<>();
        for (DataRecord record : data.getRecords()) {
            String category = (String) record.getField("category");
            if (category != null) {
                groupedData.computeIfAbsent(category, k -> new ArrayList<>()).add(record);
            }
        }
        
        // Calculate category statistics
        Map<String, Integer> categoryCounts = new HashMap<>();
        for (Map.Entry<String, List<DataRecord>> entry : groupedData.entrySet()) {
            categoryCounts.put(entry.getKey(), entry.getValue().size());
        }
        
        insights.put("category_distribution", categoryCounts);
        insights.put("total_categories", groupedData.size());
        insights.put("largest_category", 
            categoryCounts.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("N/A"));
        
        return insights;
    }
    
    @Override
    protected void saveResults(MiningResult result) throws Exception {
        System.out.println("Saving database mining results...");
        
        // In a real implementation, this would save back to database
        // For simulation, we'll just print the results
        System.out.println("Database Mining Results:");
        System.out.println("========================");
        System.out.println("Records processed: " + result.getProcessedData().size());
        System.out.println("Insights: " + result.getInsights());
        System.out.println("Anomalies: " + result.getAnomalies().size());
    }
    
    // Simulate database query execution
    private List<Map<String, Object>> simulateDatabaseQuery() {
        List<Map<String, Object>> rows = new ArrayList<>();
        
        for (int i = 1; i <= 10; i++) {
            Map<String, Object> row = new HashMap<>();
            row.put("id", i);
            row.put("product_name", "Product " + i);
            row.put("category", i % 3 == 0 ? "Electronics" : i % 2 == 0 ? "Clothing" : "Books");
            row.put("price", 10.0 + (i * 5.5));
            row.put("stock_quantity", 100 - (i * 3));
            rows.add(row);
        }
        
        return rows;
    }
}

// Concrete implementation for API data source
class APIDataMiner extends DataMiner {
    private final String apiEndpoint;
    private final Map<String, String> headers;
    private final int maxRetries;
    
    public APIDataMiner(String apiEndpoint, Map<String, String> headers, int maxRetries) {
        this.apiEndpoint = apiEndpoint;
        this.headers = new HashMap<>(headers);
        this.maxRetries = maxRetries;
    }
    
    @Override
    protected DataSet extractData() throws Exception {
        System.out.println("Extracting data from API: " + apiEndpoint);
        DataSet dataSet = new DataSet();
        
        // Simulate API call with retry logic
        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                List<Map<String, Object>> apiData = simulateAPICall();
                
                for (Map<String, Object> item : apiData) {
                    dataSet.addRecord(new DataRecord(item));
                }
                
                dataSet.addMetadata("source", "API");
                dataSet.addMetadata("endpoint", apiEndpoint);
                dataSet.addMetadata("attempts", attempt);
                
                break; // Success, exit retry loop
                
            } catch (Exception e) {
                if (attempt == maxRetries) {
                    throw new Exception("API call failed after " + maxRetries + " attempts", e);
                }
                System.out.println("API call attempt " + attempt + " failed, retrying...");
                Thread.sleep(1000 * attempt); // Exponential backoff
            }
        }
        
        return dataSet;
    }
    
    @Override
    protected boolean shouldTransformData(DataSet data) {
        // API data often needs transformation for consistency
        return true;
    }
    
    @Override
    protected DataSet transformData(DataSet data) {
        System.out.println("Transforming API data...");
        DataSet transformedData = new DataSet();
        
        for (DataRecord record : data.getRecords()) {
            Map<String, Object> transformedFields = new HashMap<>();
            
            for (String fieldName : record.getFieldNames()) {
                Object value = record.getField(fieldName);
                
                // Flatten nested objects
                if (value instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> nestedMap = (Map<String, Object>) value;
                    for (Map.Entry<String, Object> entry : nestedMap.entrySet()) {
                        transformedFields.put(fieldName + "_" + entry.getKey(), entry.getValue());
                    }
                } else {
                    transformedFields.put(fieldName, value);
                }
            }
            
            transformedData.addRecord(new DataRecord(transformedFields));
        }
        
        return transformedData;
    }
    
    @Override
    protected List<String> detectAnomalies(DataSet data) {
        System.out.println("Applying API-specific anomaly detection...");
        List<String> anomalies = super.detectAnomalies(data);
        
        // API-specific anomaly detection
        for (int i = 0; i < data.getRecords().size(); i++) {
            DataRecord record = data.getRecords().get(i);
            
            // Check for missing required API fields
            if (record.getField("api_id") == null) {
                anomalies.add("Record " + i + " missing required API ID");
            }
            
            // Check for rate limiting indicators
            Object status = record.getField("status");
            if ("rate_limited".equals(status)) {
                anomalies.add("Record " + i + " indicates rate limiting");
            }
        }
        
        return anomalies;
    }
    
    @Override
    protected Map<String, Object> applyMiningAlgorithms(DataSet data) {
        System.out.println("Applying API-specific mining algorithms...");
        Map<String, Object> insights = new HashMap<>();
        
        // Analyze API response patterns
        Map<String, Integer> statusCounts = new HashMap<>();
        List<Integer> responseTimes = new ArrayList<>();
        
        for (DataRecord record : data.getRecords()) {
            String status = (String) record.getField("status");
            if (status != null) {
                statusCounts.put(status, statusCounts.getOrDefault(status, 0) + 1);
            }
            
            Object responseTime = record.getField("response_time");
            if (responseTime instanceof Number) {
                responseTimes.add(((Number) responseTime).intValue());
            }
        }
        
        // Calculate average response time
        double avgResponseTime = responseTimes.stream()
            .mapToInt(Integer::intValue)
            .average()
            .orElse(0.0);
        
        insights.put("status_distribution", statusCounts);
        insights.put("average_response_time", avgResponseTime);
        insights.put("total_api_calls", data.size());
        
        return insights;
    }
    
    @Override
    protected void saveResults(MiningResult result) throws Exception {
        System.out.println("Saving API mining results to JSON format...");
        
        // Simulate saving to JSON file
        String jsonOutput = convertToJSON(result);
        String outputPath = "api_mining_results.json";
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(outputPath))) {
            writer.println(jsonOutput);
        }
        
        System.out.println("API results saved to: " + outputPath);
    }
    
    // Simulate API call
    private List<Map<String, Object>> simulateAPICall() {
        List<Map<String, Object>> apiData = new ArrayList<>();
        
        for (int i = 1; i <= 8; i++) {
            Map<String, Object> item = new HashMap<>();
            item.put("api_id", "api_" + i);
            item.put("user_id", 1000 + i);
            item.put("action", i % 2 == 0 ? "login" : "logout");
            item.put("timestamp", LocalDateTime.now().minusHours(i));
            item.put("status", i == 7 ? "rate_limited" : "success");
            item.put("response_time", 100 + (i * 25));
            
            // Add nested object
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("ip", "192.168.1." + i);
            metadata.put("user_agent", "Browser " + i);
            item.put("metadata", metadata);
            
            apiData.add(item);
        }
        
        return apiData;
    }
    
    private String convertToJSON(MiningResult result) {
        // Simple JSON conversion (in real implementation, use a JSON library)
        StringBuilder json = new StringBuilder();
        json.append("{\n");
        json.append("  \"success\": ").append(result.isSuccessful()).append(",\n");
        json.append("  \"recordCount\": ").append(result.getProcessedData().size()).append(",\n");
        json.append("  \"anomalyCount\": ").append(result.getAnomalies().size()).append(",\n");
        json.append("  \"insights\": ").append(result.getInsights()).append("\n");
        json.append("}");
        return json.toString();
    }
}

// Example usage and demonstration
public class DataMiningExample {
    
    public static void main(String[] args) {
        System.out.println("Template Method Pattern - Data Mining Example");
        System.out.println("=============================================\n");
        
        // Example 1: CSV Data Mining
        demonstrateCSVMining();
        
        System.out.println("\n" + "=".repeat(50) + "\n");
        
        // Example 2: Database Data Mining
        demonstrateDatabaseMining();
        
        System.out.println("\n" + "=".repeat(50) + "\n");
        
        // Example 3: API Data Mining
        demonstrateAPIMining();
    }
    
    private static void demonstrateCSVMining() {
        System.out.println("1. CSV Data Mining Example");
        System.out.println("--------------------------");
        
        DataMiner csvMiner = new CSVDataMiner("employee_data.csv", ",");
        MiningResult result = csvMiner.mineData();
        
        printResults("CSV", result);
    }
    
    private static void demonstrateDatabaseMining() {
        System.out.println("2. Database Data Mining Example");
        System.out.println("-------------------------------");
        
        DataMiner dbMiner = new DatabaseDataMiner(
            "jdbc:postgresql://localhost/mydb", 
            "SELECT * FROM products"
        );
        MiningResult result = dbMiner.mineData();
        
        printResults("Database", result);
    }
    
    private static void demonstrateAPIMining() {
        System.out.println("3. API Data Mining Example");
        System.out.println("--------------------------");
        
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer token123");
        headers.put("Content-Type", "application/json");
        
        DataMiner apiMiner = new APIDataMiner(
            "https://api.example.com/user-activity", 
            headers, 
            3
        );
        MiningResult result = apiMiner.mineData();
        
        printResults("API", result);
    }
    
    private static void printResults(String source, MiningResult result) {
        System.out.println("\n" + source + " Mining Results:");
        System.out.println("Success: " + result.isSuccessful());
        
        if (result.isSuccessful()) {
            System.out.println("Records processed: " + result.getProcessedData().size());
            System.out.println("Insights found: " + result.getInsights().size());
            System.out.println("Anomalies detected: " + result.getAnomalies().size());
            
            System.out.println("\nKey Insights:");
            result.getInsights().entrySet().stream()
                .limit(3)
                .forEach(entry -> 
                    System.out.println("  • " + entry.getKey() + ": " + entry.getValue()));
                    
            if (!result.getAnomalies().isEmpty()) {
                System.out.println("\nSample Anomalies:");
                result.getAnomalies().stream()
                    .limit(3)
                    .forEach(anomaly -> System.out.println("  ⚠ " + anomaly));
            }
        } else {
            System.out.println("Error: " + result.getInsights().get("error"));
        }
    }
}