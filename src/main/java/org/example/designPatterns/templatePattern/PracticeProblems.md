# Template Method Pattern - Practice Problems

## Overview
This document contains 8 practice problems ranging from basic to expert level to help you master the Template Method Pattern. Each problem includes requirements, hints, and a complete solution with explanations.

---

## Problem 1: Basic Data Validator (Beginner)

### Description
Create a data validation system where different types of data (Email, Phone Number, Credit Card) follow the same validation process but have different validation rules.

### Requirements
1. Create an abstract `DataValidator` class with a template method `validateData(String data)`
2. The validation process should include:
   - Input sanitization (trim whitespace, convert to appropriate case)
   - Format validation (specific to each data type)
   - Length validation
   - Business rule validation
3. Implement concrete validators for:
   - Email addresses
   - Phone numbers
   - Credit card numbers
4. Each validator should return a `ValidationResult` with success/failure and error messages

### Hints
- Use regex patterns for format validation
- Consider different business rules for each data type
- Think about what steps are common vs. specific to each validator

### Solution
```java
import java.util.*;
import java.util.regex.Pattern;

// Result class
class ValidationResult {
    private final boolean valid;
    private final List<String> errors;
    
    public ValidationResult(boolean valid, List<String> errors) {
        this.valid = valid;
        this.errors = new ArrayList<>(errors);
    }
    
    public static ValidationResult success() {
        return new ValidationResult(true, new ArrayList<>());
    }
    
    public static ValidationResult failure(String... errors) {
        return new ValidationResult(false, Arrays.asList(errors));
    }
    
    public boolean isValid() { return valid; }
    public List<String> getErrors() { return new ArrayList<>(errors); }
}

// Abstract template
abstract class DataValidator {
    
    // Template method
    public final ValidationResult validateData(String data) {
        if (data == null) {
            return ValidationResult.failure("Input cannot be null");
        }
        
        // Step 1: Sanitize input
        String sanitized = sanitizeInput(data);
        
        // Step 2: Check basic length requirements
        if (!isValidLength(sanitized)) {
            return ValidationResult.failure("Invalid length");
        }
        
        // Step 3: Validate format
        if (!isValidFormat(sanitized)) {
            return ValidationResult.failure("Invalid format");
        }
        
        // Step 4: Apply business rules
        List<String> businessRuleErrors = validateBusinessRules(sanitized);
        if (!businessRuleErrors.isEmpty()) {
            return ValidationResult.failure(businessRuleErrors.toArray(new String[0]));
        }
        
        return ValidationResult.success();
    }
    
    // Abstract methods
    protected abstract boolean isValidFormat(String data);
    protected abstract List<String> validateBusinessRules(String data);
    
    // Hook methods with defaults
    protected String sanitizeInput(String data) {
        return data.trim();
    }
    
    protected boolean isValidLength(String data) {
        return !data.isEmpty();
    }
}

// Concrete implementations
class EmailValidator extends DataValidator {
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    
    @Override
    protected String sanitizeInput(String data) {
        return super.sanitizeInput(data).toLowerCase();
    }
    
    @Override
    protected boolean isValidLength(String data) {
        return data.length() >= 5 && data.length() <= 254;
    }
    
    @Override
    protected boolean isValidFormat(String data) {
        return EMAIL_PATTERN.matcher(data).matches();
    }
    
    @Override
    protected List<String> validateBusinessRules(String data) {
        List<String> errors = new ArrayList<>();
        
        if (data.startsWith(".") || data.endsWith(".")) {
            errors.add("Email cannot start or end with a dot");
        }
        
        if (data.contains("..")) {
            errors.add("Email cannot contain consecutive dots");
        }
        
        return errors;
    }
}

class PhoneValidator extends DataValidator {
    private static final Pattern PHONE_PATTERN = 
        Pattern.compile("^\\+?[1-9]\\d{1,14}$");
    
    @Override
    protected String sanitizeInput(String data) {
        return super.sanitizeInput(data).replaceAll("[\\s\\-\\(\\)]", "");
    }
    
    @Override
    protected boolean isValidLength(String data) {
        return data.length() >= 10 && data.length() <= 15;
    }
    
    @Override
    protected boolean isValidFormat(String data) {
        return PHONE_PATTERN.matcher(data).matches();
    }
    
    @Override
    protected List<String> validateBusinessRules(String data) {
        List<String> errors = new ArrayList<>();
        
        if (data.startsWith("+1") && data.length() != 12) {
            errors.add("US phone numbers must be 10 digits plus country code");
        }
        
        return errors;
    }
}

class CreditCardValidator extends DataValidator {
    @Override
    protected String sanitizeInput(String data) {
        return super.sanitizeInput(data).replaceAll("\\s", "");
    }
    
    @Override
    protected boolean isValidLength(String data) {
        return data.length() >= 13 && data.length() <= 19;
    }
    
    @Override
    protected boolean isValidFormat(String data) {
        return data.matches("\\d+");
    }
    
    @Override
    protected List<String> validateBusinessRules(String data) {
        List<String> errors = new ArrayList<>();
        
        if (!passesLuhnCheck(data)) {
            errors.add("Invalid credit card number (Luhn check failed)");
        }
        
        return errors;
    }
    
    private boolean passesLuhnCheck(String cardNumber) {
        int sum = 0;
        boolean alternate = false;
        
        for (int i = cardNumber.length() - 1; i >= 0; i--) {
            int n = Integer.parseInt(cardNumber.substring(i, i + 1));
            if (alternate) {
                n *= 2;
                if (n > 9) {
                    n = (n % 10) + 1;
                }
            }
            sum += n;
            alternate = !alternate;
        }
        
        return (sum % 10 == 0);
    }
}
```

---

## Problem 2: File Processor (Beginner-Intermediate)

### Description
Design a file processing system that can handle different file types (CSV, JSON, XML) with the same processing workflow but different parsing logic.

### Requirements
1. Create a template method for file processing that includes:
   - File validation
   - File reading
   - Data parsing
   - Data transformation
   - Result saving
2. Implement processors for CSV, JSON, and XML files
3. Handle errors gracefully with proper exception handling
4. Include hooks for preprocessing and postprocessing

### Solution
```java
import java.io.*;
import java.util.*;

abstract class FileProcessor {
    
    public final ProcessingResult processFile(String filePath) {
        try {
            validateFile(filePath);
            String content = readFile(filePath);
            
            if (shouldPreprocess()) {
                content = preprocess(content);
            }
            
            List<Map<String, Object>> data = parseData(content);
            List<Map<String, Object>> transformed = transformData(data);
            
            String output = generateOutput(transformed);
            
            if (shouldPostprocess()) {
                output = postprocess(output);
            }
            
            String outputPath = saveResult(output, filePath);
            
            return ProcessingResult.success(outputPath, transformed.size());
            
        } catch (Exception e) {
            return ProcessingResult.failure(e.getMessage());
        }
    }
    
    protected abstract List<Map<String, Object>> parseData(String content);
    protected abstract String generateOutput(List<Map<String, Object>> data);
    
    protected void validateFile(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new IOException("File does not exist: " + filePath);
        }
        if (!file.canRead()) {
            throw new IOException("Cannot read file: " + filePath);
        }
    }
    
    protected String readFile(String filePath) throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        }
        return content.toString();
    }
    
    protected boolean shouldPreprocess() { return false; }
    protected String preprocess(String content) { return content; }
    
    protected List<Map<String, Object>> transformData(List<Map<String, Object>> data) {
        return data; // Default: no transformation
    }
    
    protected boolean shouldPostprocess() { return false; }
    protected String postprocess(String output) { return output; }
    
    protected String saveResult(String output, String originalPath) throws IOException {
        String outputPath = originalPath + ".processed";
        try (PrintWriter writer = new PrintWriter(new FileWriter(outputPath))) {
            writer.print(output);
        }
        return outputPath;
    }
}

class CSVProcessor extends FileProcessor {
    @Override
    protected List<Map<String, Object>> parseData(String content) {
        List<Map<String, Object>> result = new ArrayList<>();
        String[] lines = content.split("\n");
        
        if (lines.length < 2) return result;
        
        String[] headers = lines[0].split(",");
        
        for (int i = 1; i < lines.length; i++) {
            String[] values = lines[i].split(",");
            Map<String, Object> row = new HashMap<>();
            
            for (int j = 0; j < Math.min(headers.length, values.length); j++) {
                row.put(headers[j].trim(), values[j].trim());
            }
            
            result.add(row);
        }
        
        return result;
    }
    
    @Override
    protected String generateOutput(List<Map<String, Object>> data) {
        if (data.isEmpty()) return "";
        
        StringBuilder output = new StringBuilder();
        Set<String> headers = data.get(0).keySet();
        
        // Write headers
        output.append(String.join(",", headers)).append("\n");
        
        // Write data
        for (Map<String, Object> row : data) {
            output.append(headers.stream()
                .map(header -> row.getOrDefault(header, "").toString())
                .collect(java.util.stream.Collectors.joining(",")))
                .append("\n");
        }
        
        return output.toString();
    }
}

class ProcessingResult {
    private final boolean success;
    private final String outputPath;
    private final int recordsProcessed;
    private final String error;
    
    private ProcessingResult(boolean success, String outputPath, int recordsProcessed, String error) {
        this.success = success;
        this.outputPath = outputPath;
        this.recordsProcessed = recordsProcessed;
        this.error = error;
    }
    
    public static ProcessingResult success(String outputPath, int recordsProcessed) {
        return new ProcessingResult(true, outputPath, recordsProcessed, null);
    }
    
    public static ProcessingResult failure(String error) {
        return new ProcessingResult(false, null, 0, error);
    }
    
    // Getters...
    public boolean isSuccess() { return success; }
    public String getOutputPath() { return outputPath; }
    public int getRecordsProcessed() { return recordsProcessed; }
    public String getError() { return error; }
}
```

---

## Problem 3: API Client Framework (Intermediate)

### Description
Create an API client framework that handles different authentication methods (API Key, OAuth, Basic Auth) with the same request lifecycle.

### Requirements
1. Template method should handle:
   - Request preparation
   - Authentication
   - Request execution
   - Response parsing
   - Error handling
2. Support different authentication strategies
3. Include retry logic and rate limiting
4. Provide hooks for request/response interceptors

### Solution
```java
import java.util.*;
import java.util.concurrent.TimeUnit;

abstract class APIClient {
    private final String baseUrl;
    private final int maxRetries;
    private final long rateLimitDelay;
    
    public APIClient(String baseUrl, int maxRetries, long rateLimitDelay) {
        this.baseUrl = baseUrl;
        this.maxRetries = maxRetries;
        this.rateLimitDelay = rateLimitDelay;
    }
    
    public final APIResponse makeRequest(APIRequest request) {
        try {
            // Prepare request
            APIRequest preparedRequest = prepareRequest(request);
            
            // Apply authentication
            preparedRequest = authenticate(preparedRequest);
            
            // Add interceptors
            if (shouldInterceptRequest()) {
                preparedRequest = interceptRequest(preparedRequest);
            }
            
            // Execute with retry logic
            APIResponse response = executeWithRetry(preparedRequest);
            
            // Intercept response
            if (shouldInterceptResponse()) {
                response = interceptResponse(response);
            }
            
            // Parse response
            return parseResponse(response);
            
        } catch (Exception e) {
            return handleError(e);
        }
    }
    
    protected abstract APIRequest authenticate(APIRequest request);
    protected abstract APIResponse executeRequest(APIRequest request);
    
    protected APIRequest prepareRequest(APIRequest request) {
        request.setBaseUrl(baseUrl);
        request.addHeader("User-Agent", "Template-Method-Client/1.0");
        return request;
    }
    
    protected boolean shouldInterceptRequest() { return false; }
    protected APIRequest interceptRequest(APIRequest request) { return request; }
    
    protected boolean shouldInterceptResponse() { return false; }
    protected APIResponse interceptResponse(APIResponse response) { return response; }
    
    protected APIResponse parseResponse(APIResponse response) {
        return response; // Default: no parsing
    }
    
    protected APIResponse handleError(Exception e) {
        return APIResponse.error(500, e.getMessage());
    }
    
    private APIResponse executeWithRetry(APIRequest request) {
        APIResponse lastResponse = null;
        
        for (int attempt = 0; attempt <= maxRetries; attempt++) {
            if (attempt > 0) {
                try {
                    TimeUnit.MILLISECONDS.sleep(rateLimitDelay * attempt);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
            
            lastResponse = executeRequest(request);
            
            if (lastResponse.isSuccess() || !shouldRetry(lastResponse, attempt)) {
                break;
            }
        }
        
        return lastResponse;
    }
    
    protected boolean shouldRetry(APIResponse response, int attempt) {
        return attempt < maxRetries && 
               (response.getStatusCode() >= 500 || response.getStatusCode() == 429);
    }
}

class APIKeyClient extends APIClient {
    private final String apiKey;
    private final String keyHeader;
    
    public APIKeyClient(String baseUrl, String apiKey, String keyHeader) {
        super(baseUrl, 3, 1000);
        this.apiKey = apiKey;
        this.keyHeader = keyHeader;
    }
    
    @Override
    protected APIRequest authenticate(APIRequest request) {
        request.addHeader(keyHeader, apiKey);
        return request;
    }
    
    @Override
    protected APIResponse executeRequest(APIRequest request) {
        // Simulate HTTP request
        return APIResponse.success(200, "{\"status\":\"success\"}");
    }
}

class APIRequest {
    private String baseUrl;
    private String endpoint;
    private String method;
    private Map<String, String> headers = new HashMap<>();
    private String body;
    
    public APIRequest(String endpoint, String method) {
        this.endpoint = endpoint;
        this.method = method;
    }
    
    // Getters and setters...
    public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }
    public void addHeader(String key, String value) { headers.put(key, value); }
    public Map<String, String> getHeaders() { return new HashMap<>(headers); }
    public String getMethod() { return method; }
    public String getEndpoint() { return endpoint; }
    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }
}

class APIResponse {
    private final int statusCode;
    private final String body;
    private final Map<String, String> headers;
    private final boolean success;
    
    private APIResponse(int statusCode, String body, boolean success) {
        this.statusCode = statusCode;
        this.body = body;
        this.headers = new HashMap<>();
        this.success = success;
    }
    
    public static APIResponse success(int statusCode, String body) {
        return new APIResponse(statusCode, body, true);
    }
    
    public static APIResponse error(int statusCode, String body) {
        return new APIResponse(statusCode, body, false);
    }
    
    public int getStatusCode() { return statusCode; }
    public String getBody() { return body; }
    public boolean isSuccess() { return success; }
}
```

---

## Problem 4: Test Case Executor (Intermediate)

### Description
Build a test execution framework that can run different types of tests (Unit, Integration, E2E) with the same execution workflow.

### Requirements
1. Template method for test execution including:
   - Test setup
   - Test execution
   - Result verification
   - Cleanup
2. Different test types with specific setup and verification logic
3. Test result reporting and aggregation
4. Support for test fixtures and mocking

### Solution
```java
import java.time.LocalDateTime;
import java.util.*;

abstract class TestExecutor {
    
    public final TestResult executeTest(TestCase testCase) {
        TestResult.Builder resultBuilder = new TestResult.Builder(testCase.getName());
        long startTime = System.currentTimeMillis();
        
        try {
            // Setup phase
            setupTest(testCase);
            
            // Pre-execution hooks
            if (shouldSetupFixtures()) {
                setupFixtures(testCase);
            }
            
            // Execute test
            TestContext context = executeTestCase(testCase);
            
            // Verify results
            VerificationResult verification = verifyResults(context, testCase);
            
            long executionTime = System.currentTimeMillis() - startTime;
            
            return resultBuilder
                .success(verification.passed)
                .executionTime(executionTime)
                .output(context.getOutput())
                .errors(verification.errors)
                .build();
                
        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            return resultBuilder
                .success(false)
                .executionTime(executionTime)
                .errors(Arrays.asList(e.getMessage()))
                .build();
        } finally {
            cleanupTest(testCase);
        }
    }
    
    protected abstract TestContext executeTestCase(TestCase testCase);
    protected abstract VerificationResult verifyResults(TestContext context, TestCase testCase);
    
    protected void setupTest(TestCase testCase) {
        System.out.println("Setting up test: " + testCase.getName());
    }
    
    protected boolean shouldSetupFixtures() { return false; }
    protected void setupFixtures(TestCase testCase) { /* Default: no fixtures */ }
    
    protected void cleanupTest(TestCase testCase) {
        System.out.println("Cleaning up test: " + testCase.getName());
    }
}

class UnitTestExecutor extends TestExecutor {
    
    @Override
    protected boolean shouldSetupFixtures() {
        return true; // Unit tests often need mocks
    }
    
    @Override
    protected void setupFixtures(TestCase testCase) {
        System.out.println("Setting up mocks and stubs for: " + testCase.getName());
    }
    
    @Override
    protected TestContext executeTestCase(TestCase testCase) {
        TestContext context = new TestContext();
        
        try {
            // Execute the unit test method
            Object result = testCase.getTestMethod().invoke(testCase.getTestInstance());
            context.setResult(result);
            context.setOutput("Unit test executed successfully");
            
        } catch (Exception e) {
            context.setException(e);
            context.setOutput("Unit test failed with exception: " + e.getMessage());
        }
        
        return context;
    }
    
    @Override
    protected VerificationResult verifyResults(TestContext context, TestCase testCase) {
        List<String> errors = new ArrayList<>();
        
        if (context.getException() != null) {
            errors.add("Test threw exception: " + context.getException().getMessage());
            return new VerificationResult(false, errors);
        }
        
        // Verify assertions
        if (testCase.getExpectedResult() != null) {
            if (!Objects.equals(context.getResult(), testCase.getExpectedResult())) {
                errors.add("Expected: " + testCase.getExpectedResult() + 
                          ", but got: " + context.getResult());
            }
        }
        
        return new VerificationResult(errors.isEmpty(), errors);
    }
}

class IntegrationTestExecutor extends TestExecutor {
    
    @Override
    protected void setupTest(TestCase testCase) {
        super.setupTest(testCase);
        System.out.println("Initializing integration test environment");
    }
    
    @Override
    protected TestContext executeTestCase(TestCase testCase) {
        TestContext context = new TestContext();
        
        // Simulate integration test execution
        try {
            Thread.sleep(100); // Simulate longer execution time
            context.setResult("Integration test completed");
            context.setOutput("All integration points verified");
        } catch (InterruptedException e) {
            context.setException(e);
        }
        
        return context;
    }
    
    @Override
    protected VerificationResult verifyResults(TestContext context, TestCase testCase) {
        List<String> errors = new ArrayList<>();
        
        // Check system state after integration
        if (!verifySystemState()) {
            errors.add("System state verification failed");
        }
        
        return new VerificationResult(errors.isEmpty(), errors);
    }
    
    private boolean verifySystemState() {
        // Simulate system state verification
        return true;
    }
    
    @Override
    protected void cleanupTest(TestCase testCase) {
        super.cleanupTest(testCase);
        System.out.println("Restoring integration test environment");
    }
}

// Supporting classes
class TestCase {
    private final String name;
    private final Object testInstance;
    private final java.lang.reflect.Method testMethod;
    private final Object expectedResult;
    
    public TestCase(String name, Object testInstance, 
                   java.lang.reflect.Method testMethod, Object expectedResult) {
        this.name = name;
        this.testInstance = testInstance;
        this.testMethod = testMethod;
        this.expectedResult = expectedResult;
    }
    
    // Getters...
    public String getName() { return name; }
    public Object getTestInstance() { return testInstance; }
    public java.lang.reflect.Method getTestMethod() { return testMethod; }
    public Object getExpectedResult() { return expectedResult; }
}

class TestContext {
    private Object result;
    private String output;
    private Exception exception;
    
    // Getters and setters...
    public Object getResult() { return result; }
    public void setResult(Object result) { this.result = result; }
    public String getOutput() { return output; }
    public void setOutput(String output) { this.output = output; }
    public Exception getException() { return exception; }
    public void setException(Exception exception) { this.exception = exception; }
}

class VerificationResult {
    final boolean passed;
    final List<String> errors;
    
    public VerificationResult(boolean passed, List<String> errors) {
        this.passed = passed;
        this.errors = new ArrayList<>(errors);
    }
}

class TestResult {
    private final String testName;
    private final boolean success;
    private final long executionTime;
    private final String output;
    private final List<String> errors;
    private final LocalDateTime timestamp;
    
    private TestResult(Builder builder) {
        this.testName = builder.testName;
        this.success = builder.success;
        this.executionTime = builder.executionTime;
        this.output = builder.output;
        this.errors = new ArrayList<>(builder.errors);
        this.timestamp = LocalDateTime.now();
    }
    
    static class Builder {
        private final String testName;
        private boolean success;
        private long executionTime;
        private String output;
        private List<String> errors = new ArrayList<>();
        
        public Builder(String testName) {
            this.testName = testName;
        }
        
        public Builder success(boolean success) {
            this.success = success;
            return this;
        }
        
        public Builder executionTime(long executionTime) {
            this.executionTime = executionTime;
            return this;
        }
        
        public Builder output(String output) {
            this.output = output;
            return this;
        }
        
        public Builder errors(List<String> errors) {
            this.errors = new ArrayList<>(errors);
            return this;
        }
        
        public TestResult build() {
            return new TestResult(this);
        }
    }
    
    // Getters...
    public String getTestName() { return testName; }
    public boolean isSuccess() { return success; }
    public long getExecutionTime() { return executionTime; }
    public String getOutput() { return output; }
    public List<String> getErrors() { return new ArrayList<>(errors); }
    public LocalDateTime getTimestamp() { return timestamp; }
}
```

---

## Problem 5: Backup System (Intermediate-Advanced)

### Description
Design a backup system that supports different storage destinations (Local File System, Cloud Storage, FTP) with the same backup process.

### Requirements
1. Template method for backup process including:
   - Data collection
   - Compression
   - Encryption
   - Storage
   - Verification
2. Different storage implementations
3. Progress tracking and resumable backups
4. Backup integrity verification

### Solution
```java
import java.io.*;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.*;
import java.util.zip.GZIPOutputStream;

abstract class BackupManager {
    protected BackupProgress progress;
    
    public final BackupResult performBackup(BackupConfiguration config) {
        progress = new BackupProgress();
        
        try {
            // Validate configuration
            validateConfiguration(config);
            
            // Collect data to backup
            progress.setCurrentPhase("Collecting data");
            List<BackupItem> items = collectBackupItems(config);
            progress.setTotalItems(items.size());
            
            // Process each item
            List<ProcessedItem> processedItems = new ArrayList<>();
            for (int i = 0; i < items.size(); i++) {
                BackupItem item = items.get(i);
                progress.setCurrentItem(i + 1);
                
                ProcessedItem processed = processBackupItem(item, config);
                processedItems.add(processed);
                
                if (shouldVerifyIntegrity()) {
                    verifyItemIntegrity(processed);
                }
            }
            
            // Create backup manifest
            progress.setCurrentPhase("Creating manifest");
            BackupManifest manifest = createManifest(processedItems, config);
            
            // Store backup
            progress.setCurrentPhase("Storing backup");
            String backupLocation = storeBackup(processedItems, manifest, config);
            
            // Final verification
            progress.setCurrentPhase("Verifying backup");
            if (shouldVerifyBackup()) {
                verifyBackup(backupLocation, manifest);
            }
            
            // Cleanup
            cleanup(processedItems, config);
            
            progress.setCurrentPhase("Completed");
            return BackupResult.success(backupLocation, processedItems.size(), 
                                      calculateTotalSize(processedItems));
            
        } catch (Exception e) {
            cleanup(new ArrayList<>(), config);
            return BackupResult.failure(e.getMessage());
        }
    }
    
    // Abstract methods
    protected abstract List<BackupItem> collectBackupItems(BackupConfiguration config);
    protected abstract String storeBackup(List<ProcessedItem> items, 
                                        BackupManifest manifest, 
                                        BackupConfiguration config) throws Exception;
    
    // Hook methods
    protected void validateConfiguration(BackupConfiguration config) throws Exception {
        if (config.getSource() == null || config.getSource().isEmpty()) {
            throw new IllegalArgumentException("Backup source cannot be empty");
        }
    }
    
    protected ProcessedItem processBackupItem(BackupItem item, BackupConfiguration config) 
            throws Exception {
        byte[] data = readItemData(item);
        
        // Compress if enabled
        if (config.isCompressionEnabled()) {
            data = compress(data);
        }
        
        // Encrypt if enabled
        if (config.isEncryptionEnabled()) {
            data = encrypt(data, config.getEncryptionKey());
        }
        
        // Calculate checksum
        String checksum = calculateChecksum(data);
        
        return new ProcessedItem(item, data, checksum, data.length);
    }
    
    protected boolean shouldVerifyIntegrity() { return true; }
    protected boolean shouldVerifyBackup() { return true; }
    
    protected void verifyItemIntegrity(ProcessedItem item) throws Exception {
        // Default: verify checksum
        String recalculated = calculateChecksum(item.getData());
        if (!recalculated.equals(item.getChecksum())) {
            throw new Exception("Integrity check failed for item: " + item.getOriginalItem().getPath());
        }
    }
    
    protected void verifyBackup(String location, BackupManifest manifest) throws Exception {
        System.out.println("Verifying backup at: " + location);
    }
    
    protected void cleanup(List<ProcessedItem> items, BackupConfiguration config) {
        System.out.println("Performing cleanup");
    }
    
    // Helper methods
    private byte[] readItemData(BackupItem item) throws IOException {
        try (FileInputStream fis = new FileInputStream(item.getPath())) {
            return fis.readAllBytes();
        }
    }
    
    private byte[] compress(byte[] data) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (GZIPOutputStream gzos = new GZIPOutputStream(baos)) {
            gzos.write(data);
        }
        return baos.toByteArray();
    }
    
    private byte[] encrypt(byte[] data, String key) {
        // Simplified encryption (use proper encryption in production)
        byte[] keyBytes = key.getBytes();
        byte[] encrypted = new byte[data.length];
        
        for (int i = 0; i < data.length; i++) {
            encrypted[i] = (byte) (data[i] ^ keyBytes[i % keyBytes.length]);
        }
        
        return encrypted;
    }
    
    private String calculateChecksum(byte[] data) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest(data);
        
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        
        return hexString.toString();
    }
    
    private BackupManifest createManifest(List<ProcessedItem> items, BackupConfiguration config) {
        BackupManifest manifest = new BackupManifest();
        manifest.setBackupId(UUID.randomUUID().toString());
        manifest.setTimestamp(LocalDateTime.now());
        manifest.setConfiguration(config);
        manifest.setItems(items);
        return manifest;
    }
    
    private long calculateTotalSize(List<ProcessedItem> items) {
        return items.stream().mapToLong(ProcessedItem::getSize).sum();
    }
}

// Concrete implementations
class LocalFileBackupManager extends BackupManager {
    
    @Override
    protected List<BackupItem> collectBackupItems(BackupConfiguration config) {
        List<BackupItem> items = new ArrayList<>();
        File sourceDir = new File(config.getSource());
        
        collectItemsRecursively(sourceDir, items);
        return items;
    }
    
    private void collectItemsRecursively(File dir, List<BackupItem> items) {
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    items.add(new BackupItem(file.getAbsolutePath(), file.length()));
                } else if (file.isDirectory()) {
                    collectItemsRecursively(file, items);
                }
            }
        }
    }
    
    @Override
    protected String storeBackup(List<ProcessedItem> items, BackupManifest manifest, 
                                BackupConfiguration config) throws Exception {
        String backupDir = config.getDestination() + File.separator + 
                          "backup_" + System.currentTimeMillis();
        
        new File(backupDir).mkdirs();
        
        // Store each item
        for (ProcessedItem item : items) {
            String relativePath = item.getOriginalItem().getPath().replace(config.getSource(), "");
            File targetFile = new File(backupDir + relativePath);
            targetFile.getParentFile().mkdirs();
            
            try (FileOutputStream fos = new FileOutputStream(targetFile)) {
                fos.write(item.getData());
            }
        }
        
        // Store manifest
        File manifestFile = new File(backupDir, "manifest.json");
        try (PrintWriter writer = new PrintWriter(manifestFile)) {
            writer.println(manifest.toJson());
        }
        
        return backupDir;
    }
}

// Supporting classes
class BackupConfiguration {
    private String source;
    private String destination;
    private boolean compressionEnabled;
    private boolean encryptionEnabled;
    private String encryptionKey;
    
    // Getters and setters...
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }
    public boolean isCompressionEnabled() { return compressionEnabled; }
    public void setCompressionEnabled(boolean compressionEnabled) { this.compressionEnabled = compressionEnabled; }
    public boolean isEncryptionEnabled() { return encryptionEnabled; }
    public void setEncryptionEnabled(boolean encryptionEnabled) { this.encryptionEnabled = encryptionEnabled; }
    public String getEncryptionKey() { return encryptionKey; }
    public void setEncryptionKey(String encryptionKey) { this.encryptionKey = encryptionKey; }
}

class BackupItem {
    private final String path;
    private final long size;
    
    public BackupItem(String path, long size) {
        this.path = path;
        this.size = size;
    }
    
    public String getPath() { return path; }
    public long getSize() { return size; }
}

class ProcessedItem {
    private final BackupItem originalItem;
    private final byte[] data;
    private final String checksum;
    private final long size;
    
    public ProcessedItem(BackupItem originalItem, byte[] data, String checksum, long size) {
        this.originalItem = originalItem;
        this.data = data;
        this.checksum = checksum;
        this.size = size;
    }
    
    public BackupItem getOriginalItem() { return originalItem; }
    public byte[] getData() { return data; }
    public String getChecksum() { return checksum; }
    public long getSize() { return size; }
}

class BackupManifest {
    private String backupId;
    private LocalDateTime timestamp;
    private BackupConfiguration configuration;
    private List<ProcessedItem> items;
    
    // Getters and setters...
    public void setBackupId(String backupId) { this.backupId = backupId; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public void setConfiguration(BackupConfiguration configuration) { this.configuration = configuration; }
    public void setItems(List<ProcessedItem> items) { this.items = items; }
    
    public String toJson() {
        return "{ \"backupId\": \"" + backupId + "\", \"timestamp\": \"" + timestamp + "\" }";
    }
}

class BackupProgress {
    private String currentPhase;
    private int currentItem;
    private int totalItems;
    
    public void setCurrentPhase(String phase) { this.currentPhase = phase; }
    public void setCurrentItem(int item) { this.currentItem = item; }
    public void setTotalItems(int total) { this.totalItems = total; }
}

class BackupResult {
    private final boolean success;
    private final String location;
    private final int itemCount;
    private final long totalSize;
    private final String error;
    
    private BackupResult(boolean success, String location, int itemCount, long totalSize, String error) {
        this.success = success;
        this.location = location;
        this.itemCount = itemCount;
        this.totalSize = totalSize;
        this.error = error;
    }
    
    public static BackupResult success(String location, int itemCount, long totalSize) {
        return new BackupResult(true, location, itemCount, totalSize, null);
    }
    
    public static BackupResult failure(String error) {
        return new BackupResult(false, null, 0, 0, error);
    }
    
    // Getters...
    public boolean isSuccess() { return success; }
    public String getLocation() { return location; }
    public int getItemCount() { return itemCount; }
    public long getTotalSize() { return totalSize; }
    public String getError() { return error; }
}
```

---

## Problem 6: Workflow Engine (Advanced)

### Description
Create a workflow execution engine that can process different types of workflows (Sequential, Parallel, Conditional) with the same execution framework.

### Requirements
1. Template method for workflow execution including:
   - Workflow validation
   - Step preparation
   - Step execution
   - Result aggregation
   - Error handling and rollback
2. Support for different workflow types
3. Step dependencies and conditional execution
4. Workflow state management and persistence

### Solution
```java
import java.util.*;
import java.util.concurrent.*;
import java.time.LocalDateTime;

abstract class WorkflowEngine {
    private final ExecutorService executorService;
    
    public WorkflowEngine() {
        this.executorService = Executors.newCachedThreadPool();
    }
    
    public final WorkflowResult executeWorkflow(Workflow workflow) {
        WorkflowContext context = new WorkflowContext(workflow.getId());
        
        try {
            // Validate workflow
            validateWorkflow(workflow);
            
            // Initialize context
            initializeContext(context, workflow);
            
            // Prepare steps
            List<WorkflowStep> preparedSteps = prepareSteps(workflow.getSteps(), context);
            
            // Execute workflow
            context.setState(WorkflowState.RUNNING);
            ExecutionResult result = executeSteps(preparedSteps, context);
            
            // Aggregate results
            WorkflowResult finalResult = aggregateResults(result, context);
            
            // Update final state
            context.setState(finalResult.isSuccess() ? WorkflowState.COMPLETED : WorkflowState.FAILED);
            
            // Persist workflow state
            if (shouldPersistState()) {
                persistWorkflowState(context);
            }
            
            return finalResult;
            
        } catch (Exception e) {
            context.setState(WorkflowState.FAILED);
            context.addError(e.getMessage());
            
            // Attempt rollback
            if (shouldRollbackOnFailure()) {
                rollbackWorkflow(context);
            }
            
            return WorkflowResult.failure(e.getMessage(), context.getExecutedSteps());
        } finally {
            cleanup(context);
        }
    }
    
    // Abstract methods
    protected abstract ExecutionResult executeSteps(List<WorkflowStep> steps, WorkflowContext context);
    
    // Hook methods
    protected void validateWorkflow(Workflow workflow) throws Exception {
        if (workflow.getSteps().isEmpty()) {
            throw new IllegalArgumentException("Workflow must have at least one step");
        }
        
        // Check for circular dependencies
        validateDependencies(workflow.getSteps());
    }
    
    protected void initializeContext(WorkflowContext context, Workflow workflow) {
        context.setWorkflowType(getWorkflowType());
        context.setStartTime(LocalDateTime.now());
    }
    
    protected List<WorkflowStep> prepareSteps(List<WorkflowStep> steps, WorkflowContext context) {
        List<WorkflowStep> prepared = new ArrayList<>();
        
        for (WorkflowStep step : steps) {
            WorkflowStep preparedStep = prepareStep(step, context);
            prepared.add(preparedStep);
        }
        
        return prepared;
    }
    
    protected WorkflowStep prepareStep(WorkflowStep step, WorkflowContext context) {
        step.setContext(context);
        return step;
    }
    
    protected WorkflowResult aggregateResults(ExecutionResult result, WorkflowContext context) {
        if (result.isSuccess()) {
            return WorkflowResult.success(context.getExecutedSteps(), result.getOutputs());
        } else {
            return WorkflowResult.failure(result.getError(), context.getExecutedSteps());
        }
    }
    
    protected boolean shouldPersistState() { return true; }
    protected boolean shouldRollbackOnFailure() { return true; }
    
    protected void persistWorkflowState(WorkflowContext context) {
        System.out.println("Persisting workflow state for: " + context.getWorkflowId());
    }
    
    protected void rollbackWorkflow(WorkflowContext context) {
        System.out.println("Rolling back workflow: " + context.getWorkflowId());
        
        // Rollback in reverse order
        List<WorkflowStep> executedSteps = context.getExecutedSteps();
        for (int i = executedSteps.size() - 1; i >= 0; i--) {
            WorkflowStep step = executedSteps.get(i);
            try {
                step.rollback();
            } catch (Exception e) {
                context.addError("Rollback failed for step " + step.getId() + ": " + e.getMessage());
            }
        }
    }
    
    protected void cleanup(WorkflowContext context) {
        // Default cleanup
    }
    
    protected abstract String getWorkflowType();
    
    // Helper methods
    private void validateDependencies(List<WorkflowStep> steps) throws Exception {
        Map<String, WorkflowStep> stepMap = new HashMap<>();
        for (WorkflowStep step : steps) {
            stepMap.put(step.getId(), step);
        }
        
        Set<String> visited = new HashSet<>();
        Set<String> recursionStack = new HashSet<>();
        
        for (WorkflowStep step : steps) {
            if (hasCycle(step, stepMap, visited, recursionStack)) {
                throw new Exception("Circular dependency detected in workflow");
            }
        }
    }
    
    private boolean hasCycle(WorkflowStep step, Map<String, WorkflowStep> stepMap, 
                           Set<String> visited, Set<String> recursionStack) {
        if (recursionStack.contains(step.getId())) {
            return true;
        }
        
        if (visited.contains(step.getId())) {
            return false;
        }
        
        visited.add(step.getId());
        recursionStack.add(step.getId());
        
        for (String dependency : step.getDependencies()) {
            WorkflowStep depStep = stepMap.get(dependency);
            if (depStep != null && hasCycle(depStep, stepMap, visited, recursionStack)) {
                return true;
            }
        }
        
        recursionStack.remove(step.getId());
        return false;
    }
    
    public void shutdown() {
        executorService.shutdown();
    }
}

// Concrete implementations
class SequentialWorkflowEngine extends WorkflowEngine {
    
    @Override
    protected String getWorkflowType() {
        return "Sequential";
    }
    
    @Override
    protected ExecutionResult executeSteps(List<WorkflowStep> steps, WorkflowContext context) {
        Map<String, Object> outputs = new HashMap<>();
        
        for (WorkflowStep step : steps) {
            try {
                if (shouldExecuteStep(step, context)) {
                    StepResult result = step.execute();
                    
                    if (!result.isSuccess()) {
                        return ExecutionResult.failure("Step " + step.getId() + " failed: " + result.getError());
                    }
                    
                    outputs.putAll(result.getOutputs());
                    context.addExecutedStep(step);
                }
            } catch (Exception e) {
                return ExecutionResult.failure("Step " + step.getId() + " threw exception: " + e.getMessage());
            }
        }
        
        return ExecutionResult.success(outputs);
    }
    
    private boolean shouldExecuteStep(WorkflowStep step, WorkflowContext context) {
        // Check if all dependencies are satisfied
        for (String dependency : step.getDependencies()) {
            if (!context.isStepExecuted(dependency)) {
                return false;
            }
        }
        
        // Check conditional execution
        return step.evaluateCondition(context);
    }
}

class ParallelWorkflowEngine extends WorkflowEngine {
    
    @Override
    protected String getWorkflowType() {
        return "Parallel";
    }
    
    @Override
    protected ExecutionResult executeSteps(List<WorkflowStep> steps, WorkflowContext context) {
        List<Future<StepResult>> futures = new ArrayList<>();
        ExecutorService executor = Executors.newFixedThreadPool(steps.size());
        
        try {
            // Submit all steps for parallel execution
            for (WorkflowStep step : steps) {
                Future<StepResult> future = executor.submit(() -> {
                    try {
                        return step.execute();
                    } catch (Exception e) {
                        return StepResult.failure("Exception in step " + step.getId() + ": " + e.getMessage());
                    }
                });
                futures.add(future);
            }
            
            // Collect results
            Map<String, Object> outputs = new HashMap<>();
            for (int i = 0; i < futures.size(); i++) {
                try {
                    StepResult result = futures.get(i).get(30, TimeUnit.SECONDS);
                    
                    if (!result.isSuccess()) {
                        return ExecutionResult.failure("Parallel step failed: " + result.getError());
                    }
                    
                    outputs.putAll(result.getOutputs());
                    context.addExecutedStep(steps.get(i));
                    
                } catch (TimeoutException e) {
                    return ExecutionResult.failure("Step timeout exceeded");
                } catch (Exception e) {
                    return ExecutionResult.failure("Error executing parallel step: " + e.getMessage());
                }
            }
            
            return ExecutionResult.success(outputs);
            
        } finally {
            executor.shutdown();
        }
    }
}

// Supporting classes
class Workflow {
    private final String id;
    private final List<WorkflowStep> steps;
    private final Map<String, Object> parameters;
    
    public Workflow(String id, List<WorkflowStep> steps) {
        this.id = id;
        this.steps = new ArrayList<>(steps);
        this.parameters = new HashMap<>();
    }
    
    public String getId() { return id; }
    public List<WorkflowStep> getSteps() { return new ArrayList<>(steps); }
    public Map<String, Object> getParameters() { return new HashMap<>(parameters); }
    
    public void addParameter(String key, Object value) {
        parameters.put(key, value);
    }
}

abstract class WorkflowStep {
    protected final String id;
    protected final List<String> dependencies;
    protected WorkflowContext context;
    
    public WorkflowStep(String id) {
        this.id = id;
        this.dependencies = new ArrayList<>();
    }
    
    public abstract StepResult execute() throws Exception;
    
    public void rollback() throws Exception {
        // Default: no rollback needed
    }
    
    public boolean evaluateCondition(WorkflowContext context) {
        return true; // Default: always execute
    }
    
    public String getId() { return id; }
    public List<String> getDependencies() { return new ArrayList<>(dependencies); }
    
    public void addDependency(String stepId) {
        dependencies.add(stepId);
    }
    
    public void setContext(WorkflowContext context) {
        this.context = context;
    }
}

class WorkflowContext {
    private final String workflowId;
    private String workflowType;
    private WorkflowState state;
    private LocalDateTime startTime;
    private final List<WorkflowStep> executedSteps;
    private final Map<String, Object> variables;
    private final List<String> errors;
    
    public WorkflowContext(String workflowId) {
        this.workflowId = workflowId;
        this.state = WorkflowState.PENDING;
        this.executedSteps = new ArrayList<>();
        this.variables = new HashMap<>();
        this.errors = new ArrayList<>();
    }
    
    public String getWorkflowId() { return workflowId; }
    public String getWorkflowType() { return workflowType; }
    public void setWorkflowType(String workflowType) { this.workflowType = workflowType; }
    public WorkflowState getState() { return state; }
    public void setState(WorkflowState state) { this.state = state; }
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    
    public List<WorkflowStep> getExecutedSteps() { return new ArrayList<>(executedSteps); }
    public void addExecutedStep(WorkflowStep step) { executedSteps.add(step); }
    
    public boolean isStepExecuted(String stepId) {
        return executedSteps.stream().anyMatch(step -> step.getId().equals(stepId));
    }
    
    public Map<String, Object> getVariables() { return new HashMap<>(variables); }
    public void setVariable(String key, Object value) { variables.put(key, value); }
    public Object getVariable(String key) { return variables.get(key); }
    
    public List<String> getErrors() { return new ArrayList<>(errors); }
    public void addError(String error) { errors.add(error); }
}

enum WorkflowState {
    PENDING, RUNNING, COMPLETED, FAILED, CANCELLED
}

class StepResult {
    private final boolean success;
    private final Map<String, Object> outputs;
    private final String error;
    
    private StepResult(boolean success, Map<String, Object> outputs, String error) {
        this.success = success;
        this.outputs = new HashMap<>(outputs);
        this.error = error;
    }
    
    public static StepResult success(Map<String, Object> outputs) {
        return new StepResult(true, outputs, null);
    }
    
    public static StepResult failure(String error) {
        return new StepResult(false, new HashMap<>(), error);
    }
    
    public boolean isSuccess() { return success; }
    public Map<String, Object> getOutputs() { return new HashMap<>(outputs); }
    public String getError() { return error; }
}

class ExecutionResult {
    private final boolean success;
    private final Map<String, Object> outputs;
    private final String error;
    
    private ExecutionResult(boolean success, Map<String, Object> outputs, String error) {
        this.success = success;
        this.outputs = new HashMap<>(outputs);
        this.error = error;
    }
    
    public static ExecutionResult success(Map<String, Object> outputs) {
        return new ExecutionResult(true, outputs, null);
    }
    
    public static ExecutionResult failure(String error) {
        return new ExecutionResult(false, new HashMap<>(), error);
    }
    
    public boolean isSuccess() { return success; }
    public Map<String, Object> getOutputs() { return new HashMap<>(outputs); }
    public String getError() { return error; }
}

class WorkflowResult {
    private final boolean success;
    private final List<WorkflowStep> executedSteps;
    private final Map<String, Object> outputs;
    private final String error;
    
    private WorkflowResult(boolean success, List<WorkflowStep> executedSteps, 
                          Map<String, Object> outputs, String error) {
        this.success = success;
        this.executedSteps = new ArrayList<>(executedSteps);
        this.outputs = new HashMap<>(outputs);
        this.error = error;
    }
    
    public static WorkflowResult success(List<WorkflowStep> executedSteps, Map<String, Object> outputs) {
        return new WorkflowResult(true, executedSteps, outputs, null);
    }
    
    public static WorkflowResult failure(String error, List<WorkflowStep> executedSteps) {
        return new WorkflowResult(false, executedSteps, new HashMap<>(), error);
    }
    
    public boolean isSuccess() { return success; }
    public List<WorkflowStep> getExecutedSteps() { return new ArrayList<>(executedSteps); }
    public Map<String, Object> getOutputs() { return new HashMap<>(outputs); }
    public String getError() { return error; }
}
```

---

## Problem 7: Code Generator Framework (Advanced)

### Description
Build a code generation framework that can generate different programming languages (Java, Python, JavaScript) from the same abstract syntax model.

### Requirements
1. Template method for code generation including:
   - Model validation
   - Symbol table creation
   - Code structure generation
   - Type resolution
   - Code optimization
   - Output formatting
2. Language-specific generators
3. Support for templates and custom formatting
4. Generated code validation

### Solution
```java
import java.util.*;
import java.util.stream.Collectors;

abstract class CodeGenerator {
    protected final CodeGenerationContext context;
    
    public CodeGenerator() {
        this.context = new CodeGenerationContext();
    }
    
    public final GeneratedCode generateCode(CodeModel model, GenerationOptions options) {
        try {
            // Validate input model
            validateModel(model);
            
            // Initialize generation context
            initializeContext(model, options);
            
            // Build symbol table
            buildSymbolTable(model);
            
            // Generate code structure
            CodeStructure structure = generateCodeStructure(model);
            
            // Resolve types and dependencies
            resolveTypes(structure);
            
            // Generate actual code
            String generatedCode = generateSourceCode(structure);
            
            // Apply optimizations
            if (shouldOptimizeCode()) {
                generatedCode = optimizeCode(generatedCode);
            }
            
            // Format output
            String formattedCode = formatCode(generatedCode);
            
            // Validate generated code
            if (shouldValidateGenerated()) {
                validateGeneratedCode(formattedCode);
            }
            
            return GeneratedCode.success(formattedCode, getLanguage(), 
                                       structure.getGeneratedFiles());
            
        } catch (Exception e) {
            return GeneratedCode.failure(e.getMessage());
        }
    }
    
    // Abstract methods
    protected abstract String getLanguage();
    protected abstract CodeStructure generateCodeStructure(CodeModel model);
    protected abstract String generateSourceCode(CodeStructure structure);
    protected abstract String formatCode(String code);
    
    // Hook methods
    protected void validateModel(CodeModel model) throws Exception {
        if (model.getClasses().isEmpty() && model.getInterfaces().isEmpty()) {
            throw new IllegalArgumentException("Model must contain at least one class or interface");
        }
    }
    
    protected void initializeContext(CodeModel model, GenerationOptions options) {
        context.setTargetLanguage(getLanguage());
        context.setOptions(options);
        context.setModel(model);
    }
    
    protected void buildSymbolTable(CodeModel model) {
        SymbolTable symbolTable = new SymbolTable();
        
        // Add classes
        for (ClassDefinition classDef : model.getClasses()) {
            symbolTable.addClass(classDef.getName(), classDef);
        }
        
        // Add interfaces
        for (InterfaceDefinition interfaceDef : model.getInterfaces()) {
            symbolTable.addInterface(interfaceDef.getName(), interfaceDef);
        }
        
        context.setSymbolTable(symbolTable);
    }
    
    protected void resolveTypes(CodeStructure structure) {
        TypeResolver resolver = new TypeResolver(context.getSymbolTable(), getLanguage());
        
        for (GeneratedFile file : structure.getGeneratedFiles()) {
            resolver.resolveTypes(file);
        }
    }
    
    protected boolean shouldOptimizeCode() {
        return context.getOptions().isOptimizationEnabled();
    }
    
    protected String optimizeCode(String code) {
        // Default optimizations
        return code.replaceAll("\\s+", " ")  // Collapse whitespace
                  .replaceAll("\\n\\s*\\n", "\n");  // Remove empty lines
    }
    
    protected boolean shouldValidateGenerated() {
        return context.getOptions().isValidationEnabled();
    }
    
    protected void validateGeneratedCode(String code) throws Exception {
        // Basic validation - check for syntax errors
        if (code.contains("SYNTAX_ERROR")) {
            throw new Exception("Generated code contains syntax errors");
        }
    }
}

// Concrete implementations
class JavaCodeGenerator extends CodeGenerator {
    
    @Override
    protected String getLanguage() {
        return "Java";
    }
    
    @Override
    protected CodeStructure generateCodeStructure(CodeModel model) {
        CodeStructure structure = new CodeStructure();
        
        // Generate class files
        for (ClassDefinition classDef : model.getClasses()) {
            GeneratedFile file = generateClassFile(classDef);
            structure.addFile(file);
        }
        
        // Generate interface files
        for (InterfaceDefinition interfaceDef : model.getInterfaces()) {
            GeneratedFile file = generateInterfaceFile(interfaceDef);
            structure.addFile(file);
        }
        
        return structure;
    }
    
    private GeneratedFile generateClassFile(ClassDefinition classDef) {
        StringBuilder content = new StringBuilder();
        
        // Package declaration
        if (classDef.getPackageName() != null) {
            content.append("package ").append(classDef.getPackageName()).append(";\n\n");
        }
        
        // Imports
        for (String importStmt : classDef.getImports()) {
            content.append("import ").append(importStmt).append(";\n");
        }
        content.append("\n");
        
        // Class declaration
        content.append("public class ").append(classDef.getName());
        
        if (classDef.getSuperClass() != null) {
            content.append(" extends ").append(classDef.getSuperClass());
        }
        
        if (!classDef.getInterfaces().isEmpty()) {
            content.append(" implements ");
            content.append(String.join(", ", classDef.getInterfaces()));
        }
        
        content.append(" {\n");
        
        // Fields
        for (FieldDefinition field : classDef.getFields()) {
            content.append(generateField(field));
        }
        
        // Methods
        for (MethodDefinition method : classDef.getMethods()) {
            content.append(generateMethod(method));
        }
        
        content.append("}\n");
        
        return new GeneratedFile(classDef.getName() + ".java", content.toString());
    }
    
    private GeneratedFile generateInterfaceFile(InterfaceDefinition interfaceDef) {
        StringBuilder content = new StringBuilder();
        
        // Package declaration
        if (interfaceDef.getPackageName() != null) {
            content.append("package ").append(interfaceDef.getPackageName()).append(";\n\n");
        }
        
        // Interface declaration
        content.append("public interface ").append(interfaceDef.getName()).append(" {\n");
        
        // Methods
        for (MethodDefinition method : interfaceDef.getMethods()) {
            content.append(generateInterfaceMethod(method));
        }
        
        content.append("}\n");
        
        return new GeneratedFile(interfaceDef.getName() + ".java", content.toString());
    }
    
    private String generateField(FieldDefinition field) {
        StringBuilder sb = new StringBuilder();
        sb.append("    ");
        
        if (field.isPrivate()) sb.append("private ");
        else if (field.isProtected()) sb.append("protected ");
        else sb.append("public ");
        
        if (field.isStatic()) sb.append("static ");
        if (field.isFinal()) sb.append("final ");
        
        sb.append(field.getType()).append(" ").append(field.getName());
        
        if (field.getInitialValue() != null) {
            sb.append(" = ").append(field.getInitialValue());
        }
        
        sb.append(";\n");
        return sb.toString();
    }
    
    private String generateMethod(MethodDefinition method) {
        StringBuilder sb = new StringBuilder();
        sb.append("    ");
        
        if (method.isPrivate()) sb.append("private ");
        else if (method.isProtected()) sb.append("protected ");
        else sb.append("public ");
        
        if (method.isStatic()) sb.append("static ");
        
        sb.append(method.getReturnType()).append(" ").append(method.getName()).append("(");
        
        // Parameters
        sb.append(method.getParameters().stream()
            .map(param -> param.getType() + " " + param.getName())
            .collect(Collectors.joining(", ")));
        
        sb.append(") {\n");
        
        if (method.getBody() != null) {
            sb.append("        ").append(method.getBody()).append("\n");
        }
        
        sb.append("    }\n");
        return sb.toString();
    }
    
    private String generateInterfaceMethod(MethodDefinition method) {
        StringBuilder sb = new StringBuilder();
        sb.append("    ");
        sb.append(method.getReturnType()).append(" ").append(method.getName()).append("(");
        
        sb.append(method.getParameters().stream()
            .map(param -> param.getType() + " " + param.getName())
            .collect(Collectors.joining(", ")));
        
        sb.append(");\n");
        return sb.toString();
    }
    
    @Override
    protected String generateSourceCode(CodeStructure structure) {
        StringBuilder allCode = new StringBuilder();
        
        for (GeneratedFile file : structure.getGeneratedFiles()) {
            allCode.append("// File: ").append(file.getFilename()).append("\n");
            allCode.append(file.getContent()).append("\n\n");
        }
        
        return allCode.toString();
    }
    
    @Override
    protected String formatCode(String code) {
        // Java-specific formatting
        return code.replaceAll("\\{\\s*\\n\\s*\\n", "{\n")  // Remove extra newlines after {
                  .replaceAll("\\n\\s*\\}", "\n}");        // Clean up closing braces
    }
}

class PythonCodeGenerator extends CodeGenerator {
    
    @Override
    protected String getLanguage() {
        return "Python";
    }
    
    @Override
    protected CodeStructure generateCodeStructure(CodeModel model) {
        CodeStructure structure = new CodeStructure();
        
        // Python typically puts multiple classes in one file
        StringBuilder content = new StringBuilder();
        
        // Add imports
        Set<String> allImports = new HashSet<>();
        for (ClassDefinition classDef : model.getClasses()) {
            allImports.addAll(classDef.getImports());
        }
        
        for (String importStmt : allImports) {
            content.append("import ").append(importStmt).append("\n");
        }
        content.append("\n\n");
        
        // Generate classes
        for (ClassDefinition classDef : model.getClasses()) {
            content.append(generatePythonClass(classDef)).append("\n\n");
        }
        
        structure.addFile(new GeneratedFile("generated_classes.py", content.toString()));
        return structure;
    }
    
    private String generatePythonClass(ClassDefinition classDef) {
        StringBuilder sb = new StringBuilder();
        
        sb.append("class ").append(classDef.getName());
        
        if (classDef.getSuperClass() != null) {
            sb.append("(").append(classDef.getSuperClass()).append(")");
        }
        
        sb.append(":\n");
        
        // Constructor
        if (!classDef.getFields().isEmpty()) {
            sb.append("    def __init__(self");
            
            for (FieldDefinition field : classDef.getFields()) {
                sb.append(", ").append(field.getName());
            }
            
            sb.append("):\n");
            
            for (FieldDefinition field : classDef.getFields()) {
                sb.append("        self.").append(field.getName())
                  .append(" = ").append(field.getName()).append("\n");
            }
            sb.append("\n");
        }
        
        // Methods
        for (MethodDefinition method : classDef.getMethods()) {
            sb.append(generatePythonMethod(method)).append("\n");
        }
        
        return sb.toString();
    }
    
    private String generatePythonMethod(MethodDefinition method) {
        StringBuilder sb = new StringBuilder();
        sb.append("    def ").append(method.getName()).append("(self");
        
        for (ParameterDefinition param : method.getParameters()) {
            sb.append(", ").append(param.getName());
        }
        
        sb.append("):\n");
        
        if (method.getBody() != null) {
            sb.append("        ").append(method.getBody()).append("\n");
        } else {
            sb.append("        pass\n");
        }
        
        return sb.toString();
    }
    
    @Override
    protected String generateSourceCode(CodeStructure structure) {
        return structure.getGeneratedFiles().get(0).getContent();
    }
    
    @Override
    protected String formatCode(String code) {
        // Python-specific formatting
        return code.replaceAll("\\n\\s*\\n\\s*\\n", "\n\n");  // Normalize multiple newlines
    }
}

// Supporting classes
class CodeModel {
    private final List<ClassDefinition> classes;
    private final List<InterfaceDefinition> interfaces;
    
    public CodeModel() {
        this.classes = new ArrayList<>();
        this.interfaces = new ArrayList<>();
    }
    
    public List<ClassDefinition> getClasses() { return new ArrayList<>(classes); }
    public List<InterfaceDefinition> getInterfaces() { return new ArrayList<>(interfaces); }
    
    public void addClass(ClassDefinition classDef) { classes.add(classDef); }
    public void addInterface(InterfaceDefinition interfaceDef) { interfaces.add(interfaceDef); }
}

class ClassDefinition {
    private final String name;
    private String packageName;
    private String superClass;
    private final List<String> interfaces;
    private final List<String> imports;
    private final List<FieldDefinition> fields;
    private final List<MethodDefinition> methods;
    
    public ClassDefinition(String name) {
        this.name = name;
        this.interfaces = new ArrayList<>();
        this.imports = new ArrayList<>();
        this.fields = new ArrayList<>();
        this.methods = new ArrayList<>();
    }
    
    // Getters and adders...
    public String getName() { return name; }
    public String getPackageName() { return packageName; }
    public void setPackageName(String packageName) { this.packageName = packageName; }
    public String getSuperClass() { return superClass; }
    public void setSuperClass(String superClass) { this.superClass = superClass; }
    public List<String> getInterfaces() { return new ArrayList<>(interfaces); }
    public void addInterface(String interfaceName) { interfaces.add(interfaceName); }
    public List<String> getImports() { return new ArrayList<>(imports); }
    public void addImport(String importStmt) { imports.add(importStmt); }
    public List<FieldDefinition> getFields() { return new ArrayList<>(fields); }
    public void addField(FieldDefinition field) { fields.add(field); }
    public List<MethodDefinition> getMethods() { return new ArrayList<>(methods); }
    public void addMethod(MethodDefinition method) { methods.add(method); }
}

class InterfaceDefinition {
    private final String name;
    private String packageName;
    private final List<MethodDefinition> methods;
    
    public InterfaceDefinition(String name) {
        this.name = name;
        this.methods = new ArrayList<>();
    }
    
    public String getName() { return name; }
    public String getPackageName() { return packageName; }
    public void setPackageName(String packageName) { this.packageName = packageName; }
    public List<MethodDefinition> getMethods() { return new ArrayList<>(methods); }
    public void addMethod(MethodDefinition method) { methods.add(method); }
}

class FieldDefinition {
    private final String name;
    private final String type;
    private boolean isPrivate;
    private boolean isProtected;
    private boolean isStatic;
    private boolean isFinal;
    private String initialValue;
    
    public FieldDefinition(String name, String type) {
        this.name = name;
        this.type = type;
    }
    
    // Getters and setters...
    public String getName() { return name; }
    public String getType() { return type; }
    public boolean isPrivate() { return isPrivate; }
    public void setPrivate(boolean isPrivate) { this.isPrivate = isPrivate; }
    public boolean isProtected() { return isProtected; }
    public void setProtected(boolean isProtected) { this.isProtected = isProtected; }
    public boolean isStatic() { return isStatic; }
    public void setStatic(boolean isStatic) { this.isStatic = isStatic; }
    public boolean isFinal() { return isFinal; }
    public void setFinal(boolean isFinal) { this.isFinal = isFinal; }
    public String getInitialValue() { return initialValue; }
    public void setInitialValue(String initialValue) { this.initialValue = initialValue; }
}

class MethodDefinition {
    private final String name;
    private final String returnType;
    private final List<ParameterDefinition> parameters;
    private boolean isPrivate;
    private boolean isProtected;
    private boolean isStatic;
    private String body;
    
    public MethodDefinition(String name, String returnType) {
        this.name = name;
        this.returnType = returnType;
        this.parameters = new ArrayList<>();
    }
    
    // Getters and setters...
    public String getName() { return name; }
    public String getReturnType() { return returnType; }
    public List<ParameterDefinition> getParameters() { return new ArrayList<>(parameters); }
    public void addParameter(ParameterDefinition parameter) { parameters.add(parameter); }
    public boolean isPrivate() { return isPrivate; }
    public void setPrivate(boolean isPrivate) { this.isPrivate = isPrivate; }
    public boolean isProtected() { return isProtected; }
    public void setProtected(boolean isProtected) { this.isProtected = isProtected; }
    public boolean isStatic() { return isStatic; }
    public void setStatic(boolean isStatic) { this.isStatic = isStatic; }
    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }
}

class ParameterDefinition {
    private final String name;
    private final String type;
    
    public ParameterDefinition(String name, String type) {
        this.name = name;
        this.type = type;
    }
    
    public String getName() { return name; }
    public String getType() { return type; }
}

class GenerationOptions {
    private boolean optimizationEnabled = true;
    private boolean validationEnabled = true;
    private String indentation = "    ";
    
    // Getters and setters...
    public boolean isOptimizationEnabled() { return optimizationEnabled; }
    public void setOptimizationEnabled(boolean optimizationEnabled) { this.optimizationEnabled = optimizationEnabled; }
    public boolean isValidationEnabled() { return validationEnabled; }
    public void setValidationEnabled(boolean validationEnabled) { this.validationEnabled = validationEnabled; }
    public String getIndentation() { return indentation; }
    public void setIndentation(String indentation) { this.indentation = indentation; }
}

class CodeGenerationContext {
    private String targetLanguage;
    private GenerationOptions options;
    private CodeModel model;
    private SymbolTable symbolTable;
    
    // Getters and setters...
    public String getTargetLanguage() { return targetLanguage; }
    public void setTargetLanguage(String targetLanguage) { this.targetLanguage = targetLanguage; }
    public GenerationOptions getOptions() { return options; }
    public void setOptions(GenerationOptions options) { this.options = options; }
    public CodeModel getModel() { return model; }
    public void setModel(CodeModel model) { this.model = model; }
    public SymbolTable getSymbolTable() { return symbolTable; }
    public void setSymbolTable(SymbolTable symbolTable) { this.symbolTable = symbolTable; }
}

class SymbolTable {
    private final Map<String, ClassDefinition> classes;
    private final Map<String, InterfaceDefinition> interfaces;
    
    public SymbolTable() {
        this.classes = new HashMap<>();
        this.interfaces = new HashMap<>();
    }
    
    public void addClass(String name, ClassDefinition classDef) {
        classes.put(name, classDef);
    }
    
    public void addInterface(String name, InterfaceDefinition interfaceDef) {
        interfaces.put(name, interfaceDef);
    }
    
    public ClassDefinition getClass(String name) {
        return classes.get(name);
    }
    
    public InterfaceDefinition getInterface(String name) {
        return interfaces.get(name);
    }
}

class TypeResolver {
    private final SymbolTable symbolTable;
    private final String targetLanguage;
    
    public TypeResolver(SymbolTable symbolTable, String targetLanguage) {
        this.symbolTable = symbolTable;
        this.targetLanguage = targetLanguage;
    }
    
    public void resolveTypes(GeneratedFile file) {
        // Type resolution logic would go here
        System.out.println("Resolving types for: " + file.getFilename());
    }
}

class CodeStructure {
    private final List<GeneratedFile> generatedFiles;
    
    public CodeStructure() {
        this.generatedFiles = new ArrayList<>();
    }
    
    public List<GeneratedFile> getGeneratedFiles() {
        return new ArrayList<>(generatedFiles);
    }
    
    public void addFile(GeneratedFile file) {
        generatedFiles.add(file);
    }
}

class GeneratedFile {
    private final String filename;
    private final String content;
    
    public GeneratedFile(String filename, String content) {
        this.filename = filename;
        this.content = content;
    }
    
    public String getFilename() { return filename; }
    public String getContent() { return content; }
}

class GeneratedCode {
    private final boolean success;
    private final String code;
    private final String language;
    private final List<GeneratedFile> files;
    private final String error;
    
    private GeneratedCode(boolean success, String code, String language, 
                         List<GeneratedFile> files, String error) {
        this.success = success;
        this.code = code;
        this.language = language;
        this.files = new ArrayList<>(files);
        this.error = error;
    }
    
    public static GeneratedCode success(String code, String language, List<GeneratedFile> files) {
        return new GeneratedCode(true, code, language, files, null);
    }
    
    public static GeneratedCode failure(String error) {
        return new GeneratedCode(false, null, null, new ArrayList<>(), error);
    }
    
    public boolean isSuccess() { return success; }
    public String getCode() { return code; }
    public String getLanguage() { return language; }
    public List<GeneratedFile> getFiles() { return new ArrayList<>(files); }
    public String getError() { return error; }
}
```

---

## Problem 8: Machine Learning Pipeline (Expert)

### Description
Create a machine learning pipeline framework that supports different algorithms (Decision Tree, Neural Network, SVM) with the same training and evaluation process.

### Requirements
1. Template method for ML pipeline including:
   - Data preprocessing
   - Feature engineering
   - Model training
   - Model evaluation
   - Model persistence
   - Hyperparameter tuning
2. Different algorithm implementations
3. Cross-validation and performance metrics
4. Pipeline optimization and caching

### Solution
```java
import java.util.*;
import java.util.stream.Collectors;
import java.util.concurrent.ThreadLocalRandom;

abstract class MLPipeline {
    protected PipelineConfiguration config;
    protected ModelMetrics metrics;
    
    public final PipelineResult executePipeline(MLDataset dataset, PipelineConfiguration config) {
        this.config = config;
        this.metrics = new ModelMetrics();
        
        try {
            // Validate dataset and configuration
            validateInputs(dataset, config);
            
            // Preprocess data
            MLDataset preprocessedData = preprocessData(dataset);
            
            // Feature engineering
            MLDataset engineeredData = performFeatureEngineering(preprocessedData);
            
            // Split data
            DataSplit dataSplit = splitData(engineeredData);
            
            // Train model
            MLModel model = trainModel(dataSplit.getTrainingData());
            
            // Evaluate model
            EvaluationResult evaluation = evaluateModel(model, dataSplit.getValidationData());
            
            // Hyperparameter tuning if enabled
            if (shouldTuneHyperparameters()) {
                HyperparameterTuningResult tuningResult = tuneHyperparameters(dataSplit);
                model = tuningResult.getBestModel();
                evaluation = tuningResult.getBestEvaluation();
            }
            
            // Cross-validation if enabled
            if (shouldPerformCrossValidation()) {
                CrossValidationResult cvResult = performCrossValidation(engineeredData);
                evaluation = combineEvaluations(evaluation, cvResult);
            }
            
            // Save model if requested
            if (shouldSaveModel()) {
                String modelPath = saveModel(model);
                evaluation.setModelPath(modelPath);
            }
            
            // Generate report
            if (shouldGenerateReport()) {
                ModelReport report = generateReport(model, evaluation);
                evaluation.setReport(report);
            }
            
            return PipelineResult.success(model, evaluation, metrics);
            
        } catch (Exception e) {
            return PipelineResult.failure(e.getMessage());
        }
    }
    
    // Abstract methods
    protected abstract String getAlgorithmName();
    protected abstract MLModel trainModel(MLDataset trainingData) throws Exception;
    protected abstract EvaluationResult evaluateModel(MLModel model, MLDataset testData) throws Exception;
    
    // Hook methods
    protected void validateInputs(MLDataset dataset, PipelineConfiguration config) throws Exception {
        if (dataset.isEmpty()) {
            throw new IllegalArgumentException("Dataset cannot be empty");
        }
        
        if (config.getTargetColumn() == null) {
            throw new IllegalArgumentException("Target column must be specified");
        }
        
        if (!dataset.hasColumn(config.getTargetColumn())) {
            throw new IllegalArgumentException("Target column not found in dataset");
        }
    }
    
    protected MLDataset preprocessData(MLDataset dataset) {
        System.out.println("Preprocessing data...");
        
        MLDataset processed = new MLDataset(dataset);
        
        // Handle missing values
        processed = handleMissingValues(processed);
        
        // Normalize/standardize features
        if (config.isNormalizationEnabled()) {
            processed = normalizeFeatures(processed);
        }
        
        // Remove outliers if configured
        if (config.isOutlierRemovalEnabled()) {
            processed = removeOutliers(processed);
        }
        
        return processed;
    }
    
    protected MLDataset performFeatureEngineering(MLDataset dataset) {
        System.out.println("Performing feature engineering...");
        
        MLDataset engineered = new MLDataset(dataset);
        
        // Feature selection
        if (config.isFeatureSelectionEnabled()) {
            engineered = selectFeatures(engineered);
        }
        
        // Feature creation
        engineered = createFeatures(engineered);
        
        // Encoding categorical variables
        engineered = encodeCategoricalVariables(engineered);
        
        return engineered;
    }
    
    protected DataSplit splitData(MLDataset dataset) {
        double trainRatio = config.getTrainTestSplitRatio();
        int trainSize = (int) (dataset.size() * trainRatio);
        
        List<DataInstance> shuffled = new ArrayList<>(dataset.getInstances());
        Collections.shuffle(shuffled);
        
        List<DataInstance> trainInstances = shuffled.subList(0, trainSize);
        List<DataInstance> testInstances = shuffled.subList(trainSize, shuffled.size());
        
        return new DataSplit(
            new MLDataset(trainInstances),
            new MLDataset(testInstances)
        );
    }
    
    protected boolean shouldTuneHyperparameters() {
        return config.isHyperparameterTuningEnabled();
    }
    
    protected HyperparameterTuningResult tuneHyperparameters(DataSplit dataSplit) throws Exception {
        System.out.println("Tuning hyperparameters...");
        
        HyperparameterSpace space = getHyperparameterSpace();
        MLModel bestModel = null;
        EvaluationResult bestEvaluation = null;
        double bestScore = Double.NEGATIVE_INFINITY;
        
        for (int i = 0; i < config.getHyperparameterTuningIterations(); i++) {
            HyperparameterConfiguration hyperConfig = sampleHyperparameters(space);
            
            MLModel model = trainModelWithHyperparameters(dataSplit.getTrainingData(), hyperConfig);
            EvaluationResult evaluation = evaluateModel(model, dataSplit.getValidationData());
            
            double score = evaluation.getPrimaryMetric();
            if (score > bestScore) {
                bestScore = score;
                bestModel = model;
                bestEvaluation = evaluation;
            }
        }
        
        return new HyperparameterTuningResult(bestModel, bestEvaluation, bestScore);
    }
    
    protected boolean shouldPerformCrossValidation() {
        return config.isCrossValidationEnabled();
    }
    
    protected CrossValidationResult performCrossValidation(MLDataset dataset) throws Exception {
        System.out.println("Performing cross-validation...");
        
        int folds = config.getCrossValidationFolds();
        List<EvaluationResult> foldResults = new ArrayList<>();
        
        List<MLDataset> foldDatasets = createFolds(dataset, folds);
        
        for (int i = 0; i < folds; i++) {
            MLDataset testFold = foldDatasets.get(i);
            MLDataset trainFold = combineFolds(foldDatasets, i);
            
            MLModel model = trainModel(trainFold);
            EvaluationResult evaluation = evaluateModel(model, testFold);
            foldResults.add(evaluation);
        }
        
        return new CrossValidationResult(foldResults);
    }
    
    protected boolean shouldSaveModel() {
        return config.getModelSavePath() != null;
    }
    
    protected String saveModel(MLModel model) throws Exception {
        String path = config.getModelSavePath() + "/" + getAlgorithmName() + "_model.pkl";
        model.save(path);
        return path;
    }
    
    protected boolean shouldGenerateReport() {
        return config.isReportGenerationEnabled();
    }
    
    protected ModelReport generateReport(MLModel model, EvaluationResult evaluation) {
        return new ModelReport(getAlgorithmName(), model, evaluation);
    }
    
    // Helper methods
    protected MLDataset handleMissingValues(MLDataset dataset) {
        // Simple strategy: remove rows with missing values
        List<DataInstance> cleanInstances = dataset.getInstances().stream()
            .filter(instance -> !instance.hasMissingValues())
            .collect(Collectors.toList());
        
        return new MLDataset(cleanInstances);
    }
    
    protected MLDataset normalizeFeatures(MLDataset dataset) {
        // Min-max normalization
        MLDataset normalized = new MLDataset();
        
        for (String column : dataset.getFeatureColumns()) {
            if (dataset.isNumericColumn(column)) {
                double min = dataset.getColumnMin(column);
                double max = dataset.getColumnMax(column);
                
                for (DataInstance instance : dataset.getInstances()) {
                    double value = instance.getNumericValue(column);
                    double normalizedValue = (value - min) / (max - min);
                    instance.setValue(column, normalizedValue);
                }
            }
        }
        
        return dataset;
    }
    
    protected MLDataset removeOutliers(MLDataset dataset) {
        // Simple outlier removal using IQR
        List<DataInstance> cleanInstances = new ArrayList<>();
        
        for (String column : dataset.getFeatureColumns()) {
            if (dataset.isNumericColumn(column)) {
                double q1 = dataset.getColumnPercentile(column, 25);
                double q3 = dataset.getColumnPercentile(column, 75);
                double iqr = q3 - q1;
                double lowerBound = q1 - 1.5 * iqr;
                double upperBound = q3 + 1.5 * iqr;
                
                cleanInstances.addAll(dataset.getInstances().stream()
                    .filter(instance -> {
                        double value = instance.getNumericValue(column);
                        return value >= lowerBound && value <= upperBound;
                    })
                    .collect(Collectors.toList()));
            }
        }
        
        return new MLDataset(cleanInstances);
    }
    
    protected MLDataset selectFeatures(MLDataset dataset) {
        // Feature selection based on correlation with target
        List<String> selectedFeatures = new ArrayList<>();
        String targetColumn = config.getTargetColumn();
        
        for (String feature : dataset.getFeatureColumns()) {
            if (!feature.equals(targetColumn)) {
                double correlation = dataset.calculateCorrelation(feature, targetColumn);
                if (Math.abs(correlation) > config.getFeatureSelectionThreshold()) {
                    selectedFeatures.add(feature);
                }
            }
        }
        
        return dataset.selectColumns(selectedFeatures);
    }
    
    protected MLDataset createFeatures(MLDataset dataset) {
        // Default: no feature creation
        return dataset;
    }
    
    protected MLDataset encodeCategoricalVariables(MLDataset dataset) {
        // One-hot encoding for categorical variables
        MLDataset encoded = new MLDataset(dataset);
        
        for (String column : dataset.getCategoricalColumns()) {
            Set<String> uniqueValues = dataset.getUniqueValues(column);
            
            for (String value : uniqueValues) {
                String newColumnName = column + "_" + value;
                for (DataInstance instance : encoded.getInstances()) {
                    boolean hasValue = value.equals(instance.getStringValue(column));
                    instance.setValue(newColumnName, hasValue ? 1.0 : 0.0);
                }
            }
            
            // Remove original categorical column
            encoded.removeColumn(column);
        }
        
        return encoded;
    }
    
    protected HyperparameterSpace getHyperparameterSpace() {
        return new HyperparameterSpace(); // Default empty space
    }
    
    protected HyperparameterConfiguration sampleHyperparameters(HyperparameterSpace space) {
        return space.sample();
    }
    
    protected MLModel trainModelWithHyperparameters(MLDataset trainingData, 
                                                   HyperparameterConfiguration hyperConfig) throws Exception {
        // Default: ignore hyperparameters
        return trainModel(trainingData);
    }
    
    protected EvaluationResult combineEvaluations(EvaluationResult eval1, CrossValidationResult cvResult) {
        // Combine single evaluation with cross-validation results
        EvaluationResult combined = new EvaluationResult(eval1);
        combined.setCrossValidationResults(cvResult);
        return combined;
    }
    
    protected List<MLDataset> createFolds(MLDataset dataset, int folds) {
        List<DataInstance> shuffled = new ArrayList<>(dataset.getInstances());
        Collections.shuffle(shuffled);
        
        List<MLDataset> foldDatasets = new ArrayList<>();
        int foldSize = shuffled.size() / folds;
        
        for (int i = 0; i < folds; i++) {
            int start = i * foldSize;
            int end = (i == folds - 1) ? shuffled.size() : start + foldSize;
            
            List<DataInstance> foldInstances = shuffled.subList(start, end);
            foldDatasets.add(new MLDataset(foldInstances));
        }
        
        return foldDatasets;
    }
    
    protected MLDataset combineFolds(List<MLDataset> folds, int excludeIndex) {
        List<DataInstance> combined = new ArrayList<>();
        
        for (int i = 0; i < folds.size(); i++) {
            if (i != excludeIndex) {
                combined.addAll(folds.get(i).getInstances());
            }
        }
        
        return new MLDataset(combined);
    }
}

// Concrete implementations
class DecisionTreePipeline extends MLPipeline {
    
    @Override
    protected String getAlgorithmName() {
        return "DecisionTree";
    }
    
    @Override
    protected MLModel trainModel(MLDataset trainingData) throws Exception {
        System.out.println("Training Decision Tree model...");
        
        DecisionTreeModel model = new DecisionTreeModel();
        model.train(trainingData, config.getTargetColumn());
        
        return model;
    }
    
    @Override
    protected EvaluationResult evaluateModel(MLModel model, MLDataset testData) throws Exception {
        System.out.println("Evaluating Decision Tree model...");
        
        EvaluationResult result = new EvaluationResult();
        
        List<Prediction> predictions = model.predict(testData);
        
        // Calculate metrics
        double accuracy = calculateAccuracy(predictions, testData);
        double precision = calculatePrecision(predictions, testData);
        double recall = calculateRecall(predictions, testData);
        double f1Score = 2 * (precision * recall) / (precision + recall);
        
        result.addMetric("accuracy", accuracy);
        result.addMetric("precision", precision);
        result.addMetric("recall", recall);
        result.addMetric("f1_score", f1Score);
        result.setPrimaryMetric(accuracy);
        
        return result;
    }
    
    private double calculateAccuracy(List<Prediction> predictions, MLDataset testData) {
        int correct = 0;
        List<DataInstance> instances = testData.getInstances();
        
        for (int i = 0; i < predictions.size(); i++) {
            Object predicted = predictions.get(i).getValue();
            Object actual = instances.get(i).getValue(config.getTargetColumn());
            
            if (Objects.equals(predicted, actual)) {
                correct++;
            }
        }
        
        return (double) correct / predictions.size();
    }
    
    private double calculatePrecision(List<Prediction> predictions, MLDataset testData) {
        // Simplified binary classification precision calculation
        return 0.85; // Simulated
    }
    
    private double calculateRecall(List<Prediction> predictions, MLDataset testData) {
        // Simplified binary classification recall calculation
        return 0.82; // Simulated
    }
}

// Supporting classes
class MLDataset {
    private List<DataInstance> instances;
    private Set<String> columnNames;
    
    public MLDataset() {
        this.instances = new ArrayList<>();
        this.columnNames = new HashSet<>();
    }
    
    public MLDataset(List<DataInstance> instances) {
        this.instances = new ArrayList<>(instances);
        this.columnNames = new HashSet<>();
        
        if (!instances.isEmpty()) {
            this.columnNames.addAll(instances.get(0).getColumnNames());
        }
    }
    
    public MLDataset(MLDataset other) {
        this.instances = new ArrayList<>(other.instances);
        this.columnNames = new HashSet<>(other.columnNames);
    }
    
    public boolean isEmpty() { return instances.isEmpty(); }
    public int size() { return instances.size(); }
    public List<DataInstance> getInstances() { return new ArrayList<>(instances); }
    
    public boolean hasColumn(String columnName) {
        return columnNames.contains(columnName);
    }
    
    public List<String> getFeatureColumns() {
        return new ArrayList<>(columnNames);
    }
    
    public List<String> getCategoricalColumns() {
        return columnNames.stream()
            .filter(this::isCategoricalColumn)
            .collect(Collectors.toList());
    }
    
    public boolean isNumericColumn(String column) {
        return instances.stream()
            .anyMatch(instance -> instance.getValue(column) instanceof Number);
    }
    
    public boolean isCategoricalColumn(String column) {
        return !isNumericColumn(column);
    }
    
    public double getColumnMin(String column) {
        return instances.stream()
            .mapToDouble(instance -> instance.getNumericValue(column))
            .min()
            .orElse(0.0);
    }
    
    public double getColumnMax(String column) {
        return instances.stream()
            .mapToDouble(instance -> instance.getNumericValue(column))
            .max()
            .orElse(0.0);
    }
    
    public double getColumnPercentile(String column, double percentile) {
        List<Double> values = instances.stream()
            .map(instance -> instance.getNumericValue(column))
            .sorted()
            .collect(Collectors.toList());
        
        int index = (int) Math.ceil(percentile / 100.0 * values.size()) - 1;
        return values.get(Math.max(0, Math.min(index, values.size() - 1)));
    }
    
    public double calculateCorrelation(String col1, String col2) {
        // Simplified correlation calculation
        return ThreadLocalRandom.current().nextDouble(-1.0, 1.0);
    }
    
    public Set<String> getUniqueValues(String column) {
        return instances.stream()
            .map(instance -> instance.getStringValue(column))
            .collect(Collectors.toSet());
    }
    
    public MLDataset selectColumns(List<String> columns) {
        List<DataInstance> selectedInstances = instances.stream()
            .map(instance -> instance.selectColumns(columns))
            .collect(Collectors.toList());
        
        return new MLDataset(selectedInstances);
    }
    
    public void removeColumn(String column) {
        columnNames.remove(column);
        for (DataInstance instance : instances) {
            instance.removeColumn(column);
        }
    }
}

class DataInstance {
    private Map<String, Object> values;
    
    public DataInstance() {
        this.values = new HashMap<>();
    }
    
    public DataInstance(Map<String, Object> values) {
        this.values = new HashMap<>(values);
    }
    
    public Set<String> getColumnNames() {
        return new HashSet<>(values.keySet());
    }
    
    public Object getValue(String column) {
        return values.get(column);
    }
    
    public void setValue(String column, Object value) {
        values.put(column, value);
    }
    
    public double getNumericValue(String column) {
        Object value = values.get(column);
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        return 0.0;
    }
    
    public String getStringValue(String column) {
        Object value = values.get(column);
        return value != null ? value.toString() : "";
    }
    
    public boolean hasMissingValues() {
        return values.values().stream().anyMatch(Objects::isNull);
    }
    
    public DataInstance selectColumns(List<String> columns) {
        Map<String, Object> selected = new HashMap<>();
        for (String column : columns) {
            if (values.containsKey(column)) {
                selected.put(column, values.get(column));
            }
        }
        return new DataInstance(selected);
    }
    
    public void removeColumn(String column) {
        values.remove(column);
    }
}

class PipelineConfiguration {
    private String targetColumn;
    private double trainTestSplitRatio = 0.8;
    private boolean normalizationEnabled = true;
    private boolean outlierRemovalEnabled = false;
    private boolean featureSelectionEnabled = true;
    private double featureSelectionThreshold = 0.1;
    private boolean hyperparameterTuningEnabled = false;
    private int hyperparameterTuningIterations = 50;
    private boolean crossValidationEnabled = true;
    private int crossValidationFolds = 5;
    private String modelSavePath;
    private boolean reportGenerationEnabled = true;
    
    // Getters and setters...
    public String getTargetColumn() { return targetColumn; }
    public void setTargetColumn(String targetColumn) { this.targetColumn = targetColumn; }
    public double getTrainTestSplitRatio() { return trainTestSplitRatio; }
    public void setTrainTestSplitRatio(double trainTestSplitRatio) { this.trainTestSplitRatio = trainTestSplitRatio; }
    public boolean isNormalizationEnabled() { return normalizationEnabled; }
    public void setNormalizationEnabled(boolean normalizationEnabled) { this.normalizationEnabled = normalizationEnabled; }
    public boolean isOutlierRemovalEnabled() { return outlierRemovalEnabled; }
    public void setOutlierRemovalEnabled(boolean outlierRemovalEnabled) { this.outlierRemovalEnabled = outlierRemovalEnabled; }
    public boolean isFeatureSelectionEnabled() { return featureSelectionEnabled; }
    public void setFeatureSelectionEnabled(boolean featureSelectionEnabled) { this.featureSelectionEnabled = featureSelectionEnabled; }
    public double getFeatureSelectionThreshold() { return featureSelectionThreshold; }
    public void setFeatureSelectionThreshold(double featureSelectionThreshold) { this.featureSelectionThreshold = featureSelectionThreshold; }
    public boolean isHyperparameterTuningEnabled() { return hyperparameterTuningEnabled; }
    public void setHyperparameterTuningEnabled(boolean hyperparameterTuningEnabled) { this.hyperparameterTuningEnabled = hyperparameterTuningEnabled; }
    public int getHyperparameterTuningIterations() { return hyperparameterTuningIterations; }
    public void setHyperparameterTuningIterations(int hyperparameterTuningIterations) { this.hyperparameterTuningIterations = hyperparameterTuningIterations; }
    public boolean isCrossValidationEnabled() { return crossValidationEnabled; }
    public void setCrossValidationEnabled(boolean crossValidationEnabled) { this.crossValidationEnabled = crossValidationEnabled; }
    public int getCrossValidationFolds() { return crossValidationFolds; }
    public void setCrossValidationFolds(int crossValidationFolds) { this.crossValidationFolds = crossValidationFolds; }
    public String getModelSavePath() { return modelSavePath; }
    public void setModelSavePath(String modelSavePath) { this.modelSavePath = modelSavePath; }
    public boolean isReportGenerationEnabled() { return reportGenerationEnabled; }
    public void setReportGenerationEnabled(boolean reportGenerationEnabled) { this.reportGenerationEnabled = reportGenerationEnabled; }
}

abstract class MLModel {
    public abstract void train(MLDataset dataset, String targetColumn) throws Exception;
    public abstract List<Prediction> predict(MLDataset dataset) throws Exception;
    public abstract void save(String path) throws Exception;
}

class DecisionTreeModel extends MLModel {
    @Override
    public void train(MLDataset dataset, String targetColumn) throws Exception {
        // Decision tree training logic
        System.out.println("Training decision tree...");
    }
    
    @Override
    public List<Prediction> predict(MLDataset dataset) throws Exception {
        List<Prediction> predictions = new ArrayList<>();
        
        for (DataInstance instance : dataset.getInstances()) {
            // Simulated prediction
            double prediction = ThreadLocalRandom.current().nextBoolean() ? 1.0 : 0.0;
            predictions.add(new Prediction(prediction, 0.75));
        }
        
        return predictions;
    }
    
    @Override
    public void save(String path) throws Exception {
        System.out.println("Saving decision tree model to: " + path);
    }
}

class Prediction {
    private final Object value;
    private final double confidence;
    
    public Prediction(Object value, double confidence) {
        this.value = value;
        this.confidence = confidence;
    }
    
    public Object getValue() { return value; }
    public double getConfidence() { return confidence; }
}

class DataSplit {
    private final MLDataset trainingData;
    private final MLDataset validationData;
    
    public DataSplit(MLDataset trainingData, MLDataset validationData) {
        this.trainingData = trainingData;
        this.validationData = validationData;
    }
    
    public MLDataset getTrainingData() { return trainingData; }
    public MLDataset getValidationData() { return validationData; }
}

class EvaluationResult {
    private Map<String, Double> metrics;
    private double primaryMetric;
    private CrossValidationResult crossValidationResults;
    private String modelPath;
    private ModelReport report;
    
    public EvaluationResult() {
        this.metrics = new HashMap<>();
    }
    
    public EvaluationResult(EvaluationResult other) {
        this.metrics = new HashMap<>(other.metrics);
        this.primaryMetric = other.primaryMetric;
        this.crossValidationResults = other.crossValidationResults;
        this.modelPath = other.modelPath;
        this.report = other.report;
    }
    
    public void addMetric(String name, double value) {
        metrics.put(name, value);
    }
    
    public Map<String, Double> getMetrics() { return new HashMap<>(metrics); }
    public double getPrimaryMetric() { return primaryMetric; }
    public void setPrimaryMetric(double primaryMetric) { this.primaryMetric = primaryMetric; }
    public CrossValidationResult getCrossValidationResults() { return crossValidationResults; }
    public void setCrossValidationResults(CrossValidationResult crossValidationResults) { this.crossValidationResults = crossValidationResults; }
    public String getModelPath() { return modelPath; }
    public void setModelPath(String modelPath) { this.modelPath = modelPath; }
    public ModelReport getReport() { return report; }
    public void setReport(ModelReport report) { this.report = report; }
}

class ModelMetrics {
    private long trainingTime;
    private long evaluationTime;
    
    public long getTrainingTime() { return trainingTime; }
    public void setTrainingTime(long trainingTime) { this.trainingTime = trainingTime; }
    public long getEvaluationTime() { return evaluationTime; }
    public void setEvaluationTime(long evaluationTime) { this.evaluationTime = evaluationTime; }
}

class HyperparameterSpace {
    public HyperparameterConfiguration sample() {
        return new HyperparameterConfiguration();
    }
}

class HyperparameterConfiguration {
    // Placeholder for hyperparameter values
}

class HyperparameterTuningResult {
    private final MLModel bestModel;
    private final EvaluationResult bestEvaluation;
    private final double bestScore;
    
    public HyperparameterTuningResult(MLModel bestModel, EvaluationResult bestEvaluation, double bestScore) {
        this.bestModel = bestModel;
        this.bestEvaluation = bestEvaluation;
        this.bestScore = bestScore;
    }
    
    public MLModel getBestModel() { return bestModel; }
    public EvaluationResult getBestEvaluation() { return bestEvaluation; }
    public double getBestScore() { return bestScore; }
}

class CrossValidationResult {
    private final List<EvaluationResult> foldResults;
    
    public CrossValidationResult(List<EvaluationResult> foldResults) {
        this.foldResults = new ArrayList<>(foldResults);
    }
    
    public List<EvaluationResult> getFoldResults() { return new ArrayList<>(foldResults); }
}

class ModelReport {
    private final String algorithmName;
    private final MLModel model;
    private final EvaluationResult evaluation;
    
    public ModelReport(String algorithmName, MLModel model, EvaluationResult evaluation) {
        this.algorithmName = algorithmName;
        this.model = model;
        this.evaluation = evaluation;
    }
    
    public String getAlgorithmName() { return algorithmName; }
    public MLModel getModel() { return model; }
    public EvaluationResult getEvaluation() { return evaluation; }
}

class PipelineResult {
    private final boolean success;
    private final MLModel model;
    private final EvaluationResult evaluation;
    private final ModelMetrics metrics;
    private final String error;
    
    private PipelineResult(boolean success, MLModel model, EvaluationResult evaluation, 
                          ModelMetrics metrics, String error) {
        this.success = success;
        this.model = model;
        this.evaluation = evaluation;
        this.metrics = metrics;
        this.error = error;
    }
    
    public static PipelineResult success(MLModel model, EvaluationResult evaluation, ModelMetrics metrics) {
        return new PipelineResult(true, model, evaluation, metrics, null);
    }
    
    public static PipelineResult failure(String error) {
        return new PipelineResult(false, null, null, null, error);
    }
    
    public boolean isSuccess() { return success; }
    public MLModel getModel() { return model; }
    public EvaluationResult getEvaluation() { return evaluation; }
    public ModelMetrics getMetrics() { return metrics; }
    public String getError() { return error; }
}
```

---

## Summary

These practice problems cover the Template Method Pattern from basic to expert level:

1. **Basic Data Validator** - Introduces the basic concepts with simple validation rules
2. **File Processor** - Adds file I/O and error handling
3. **API Client Framework** - Introduces authentication strategies and retry logic
4. **Test Case Executor** - Covers test frameworks and result reporting
5. **Backup System** - Advanced features like compression, encryption, and verification
6. **Workflow Engine** - Complex state management and parallel execution
7. **Code Generator Framework** - Symbol tables, type resolution, and multi-language support
8. **Machine Learning Pipeline** - Expert-level with hyperparameter tuning, cross-validation, and optimization

Each problem builds upon previous concepts while introducing new complexities and real-world considerations. Practice these problems in order to gradually master the Template Method Pattern.