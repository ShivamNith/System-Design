package org.example.designPatterns.chainOfResponsibilityPattern;

import java.util.*;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;

/**
 * HTTP Middleware Processing Example demonstrating Chain of Responsibility Pattern
 * 
 * This example shows how HTTP requests are processed through a chain of
 * middleware components for authentication, logging, validation, etc.
 */

// HTTP Request object
class HTTPRequest {
    private String method;
    private String path;
    private Map<String, String> headers;
    private Map<String, String> queryParams;
    private String body;
    private String clientIP;
    private LocalDateTime timestamp;
    private Map<String, Object> attributes;
    
    public HTTPRequest(String method, String path, String clientIP) {
        this.method = method;
        this.path = path;
        this.clientIP = clientIP;
        this.timestamp = LocalDateTime.now();
        this.headers = new HashMap<>();
        this.queryParams = new HashMap<>();
        this.attributes = new HashMap<>();
    }
    
    // Getters and setters
    public String getMethod() { return method; }
    public String getPath() { return path; }
    public Map<String, String> getHeaders() { return headers; }
    public String getHeader(String name) { return headers.get(name.toLowerCase()); }
    public void setHeader(String name, String value) { headers.put(name.toLowerCase(), value); }
    public Map<String, String> getQueryParams() { return queryParams; }
    public String getQueryParam(String name) { return queryParams.get(name); }
    public void setQueryParam(String name, String value) { queryParams.put(name, value); }
    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }
    public String getClientIP() { return clientIP; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public Object getAttribute(String name) { return attributes.get(name); }
    public void setAttribute(String name, Object value) { attributes.put(name, value); }
    
    @Override
    public String toString() {
        return String.format("%s %s from %s", method, path, clientIP);
    }
}

// HTTP Response object
class HTTPResponse {
    public enum Status {
        OK(200, "OK"),
        UNAUTHORIZED(401, "Unauthorized"),
        FORBIDDEN(403, "Forbidden"),
        NOT_FOUND(404, "Not Found"),
        RATE_LIMITED(429, "Too Many Requests"),
        INTERNAL_ERROR(500, "Internal Server Error");
        
        private final int code;
        private final String message;
        
        Status(int code, String message) {
            this.code = code;
            this.message = message;
        }
        
        public int getCode() { return code; }
        public String getMessage() { return message; }
    }
    
    private Status status;
    private Map<String, String> headers;
    private String body;
    private boolean completed;
    
    public HTTPResponse() {
        this.status = Status.OK;
        this.headers = new HashMap<>();
        this.completed = false;
    }
    
    // Getters and setters
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    public Map<String, String> getHeaders() { return headers; }
    public void setHeader(String name, String value) { headers.put(name, value); }
    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }
    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }
    
    @Override
    public String toString() {
        return String.format("Response: %d %s", status.getCode(), status.getMessage());
    }
}

// Abstract Middleware Handler
abstract class Middleware {
    protected Middleware next;
    protected String middlewareName;
    
    public Middleware(String middlewareName) {
        this.middlewareName = middlewareName;
    }
    
    public Middleware setNext(Middleware next) {
        this.next = next;
        return next;
    }
    
    public final HTTPResponse handle(HTTPRequest request) {
        System.out.println("→ " + middlewareName + " processing request: " + request);
        
        HTTPResponse response = new HTTPResponse();
        
        // Process the request
        boolean shouldContinue = processRequest(request, response);
        
        if (!shouldContinue || response.isCompleted()) {
            System.out.println("← " + middlewareName + " completed processing: " + response);
            return response;
        }
        
        // Continue to next middleware
        if (next != null) {
            response = next.handle(request);
        } else {
            // End of chain - no handler found
            response.setStatus(HTTPResponse.Status.NOT_FOUND);
            response.setBody("No handler found for " + request.getPath());
            response.setCompleted(true);
        }
        
        // Post-process the response
        postProcessResponse(request, response);
        
        return response;
    }
    
    protected abstract boolean processRequest(HTTPRequest request, HTTPResponse response);
    
    protected void postProcessResponse(HTTPRequest request, HTTPResponse response) {
        // Default implementation - can be overridden
    }
    
    public String getMiddlewareName() {
        return middlewareName;
    }
}

// Concrete Middleware - CORS Handler
class CORSMiddleware extends Middleware {
    private Set<String> allowedOrigins;
    private Set<String> allowedMethods;
    
    public CORSMiddleware() {
        super("CORS Handler");
        this.allowedOrigins = new HashSet<>(Arrays.asList("https://example.com", "https://app.example.com"));
        this.allowedMethods = new HashSet<>(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    }
    
    @Override
    protected boolean processRequest(HTTPRequest request, HTTPResponse response) {
        String origin = request.getHeader("origin");
        
        // Handle preflight requests
        if ("OPTIONS".equals(request.getMethod())) {
            if (origin != null && allowedOrigins.contains(origin)) {
                response.setHeader("Access-Control-Allow-Origin", origin);
                response.setHeader("Access-Control-Allow-Methods", String.join(", ", allowedMethods));
                response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
                response.setHeader("Access-Control-Max-Age", "3600");
                response.setStatus(HTTPResponse.Status.OK);
                response.setCompleted(true);
                return false;
            } else {
                response.setStatus(HTTPResponse.Status.FORBIDDEN);
                response.setBody("CORS policy violation");
                response.setCompleted(true);
                return false;
            }
        }
        
        // Add CORS headers for actual requests
        if (origin != null && allowedOrigins.contains(origin)) {
            response.setHeader("Access-Control-Allow-Origin", origin);
        }
        
        return true;
    }
}

// Concrete Middleware - Rate Limiter
class RateLimitMiddleware extends Middleware {
    private Map<String, List<Long>> requestCounts;
    private int maxRequestsPerMinute;
    private long windowSizeMs;
    
    public RateLimitMiddleware(int maxRequestsPerMinute) {
        super("Rate Limiter");
        this.maxRequestsPerMinute = maxRequestsPerMinute;
        this.windowSizeMs = 60000; // 1 minute
        this.requestCounts = new ConcurrentHashMap<>();
    }
    
    @Override
    protected boolean processRequest(HTTPRequest request, HTTPResponse response) {
        String clientIP = request.getClientIP();
        long currentTime = System.currentTimeMillis();
        
        requestCounts.putIfAbsent(clientIP, new ArrayList<>());
        List<Long> requests = requestCounts.get(clientIP);
        
        // Remove old requests outside the window
        requests.removeIf(timestamp -> currentTime - timestamp > windowSizeMs);
        
        if (requests.size() >= maxRequestsPerMinute) {
            response.setStatus(HTTPResponse.Status.RATE_LIMITED);
            response.setHeader("X-RateLimit-Limit", String.valueOf(maxRequestsPerMinute));
            response.setHeader("X-RateLimit-Remaining", "0");
            response.setHeader("Retry-After", "60");
            response.setBody("Rate limit exceeded. Try again later.");
            response.setCompleted(true);
            return false;
        }
        
        // Add current request
        requests.add(currentTime);
        
        // Add rate limit headers
        response.setHeader("X-RateLimit-Limit", String.valueOf(maxRequestsPerMinute));
        response.setHeader("X-RateLimit-Remaining", String.valueOf(maxRequestsPerMinute - requests.size()));
        
        return true;
    }
}

// Concrete Middleware - Authentication
class AuthenticationMiddleware extends Middleware {
    private Map<String, String> validTokens;
    private Set<String> publicPaths;
    
    public AuthenticationMiddleware() {
        super("Authentication");
        this.validTokens = new HashMap<>();
        this.publicPaths = new HashSet<>(Arrays.asList("/health", "/", "/login", "/register"));
        
        // Initialize some valid tokens
        validTokens.put("token123", "user1");
        validTokens.put("token456", "user2");
        validTokens.put("admintoken", "admin");
    }
    
    @Override
    protected boolean processRequest(HTTPRequest request, HTTPResponse response) {
        // Skip authentication for public paths
        if (publicPaths.contains(request.getPath())) {
            return true;
        }
        
        String authHeader = request.getHeader("authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HTTPResponse.Status.UNAUTHORIZED);
            response.setHeader("WWW-Authenticate", "Bearer");
            response.setBody("Authentication required");
            response.setCompleted(true);
            return false;
        }
        
        String token = authHeader.substring(7); // Remove "Bearer " prefix
        String username = validTokens.get(token);
        
        if (username == null) {
            response.setStatus(HTTPResponse.Status.UNAUTHORIZED);
            response.setBody("Invalid token");
            response.setCompleted(true);
            return false;
        }
        
        // Add user info to request attributes
        request.setAttribute("username", username);
        request.setAttribute("isAdmin", "admin".equals(username));
        
        return true;
    }
}

// Concrete Middleware - Request Logger
class LoggingMiddleware extends Middleware {
    private List<String> requestLogs;
    
    public LoggingMiddleware() {
        super("Request Logger");
        this.requestLogs = new ArrayList<>();
    }
    
    @Override
    protected boolean processRequest(HTTPRequest request, HTTPResponse response) {
        String logEntry = String.format("[%s] %s %s from %s", 
                                      request.getTimestamp().toString().substring(11, 19),
                                      request.getMethod(), 
                                      request.getPath(),
                                      request.getClientIP());
        
        if (request.getAttribute("username") != null) {
            logEntry += " (user: " + request.getAttribute("username") + ")";
        }
        
        requestLogs.add(logEntry);
        System.out.println("  [LOG] " + logEntry);
        
        return true;
    }
    
    @Override
    protected void postProcessResponse(HTTPRequest request, HTTPResponse response) {
        String responseLog = String.format("[%s] Response: %d %s for %s %s",
                                         LocalDateTime.now().toString().substring(11, 19),
                                         response.getStatus().getCode(),
                                         response.getStatus().getMessage(),
                                         request.getMethod(),
                                         request.getPath());
        
        requestLogs.add(responseLog);
        System.out.println("  [LOG] " + responseLog);
    }
    
    public List<String> getRequestLogs() {
        return new ArrayList<>(requestLogs);
    }
}

// Concrete Middleware - Request Validator
class ValidationMiddleware extends Middleware {
    
    public ValidationMiddleware() {
        super("Request Validator");
    }
    
    @Override
    protected boolean processRequest(HTTPRequest request, HTTPResponse response) {
        // Validate request method
        if (!isValidMethod(request.getMethod())) {
            response.setStatus(HTTPResponse.Status.NOT_FOUND);
            response.setBody("Method not allowed");
            response.setCompleted(true);
            return false;
        }
        
        // Validate path
        if (!isValidPath(request.getPath())) {
            response.setStatus(HTTPResponse.Status.NOT_FOUND);
            response.setBody("Invalid path");
            response.setCompleted(true);
            return false;
        }
        
        // Validate content type for POST/PUT requests
        if (("POST".equals(request.getMethod()) || "PUT".equals(request.getMethod())) &&
            request.getBody() != null && !request.getBody().isEmpty()) {
            
            String contentType = request.getHeader("content-type");
            if (contentType == null || !contentType.contains("application/json")) {
                response.setStatus(HTTPResponse.Status.NOT_FOUND);
                response.setBody("Content-Type must be application/json");
                response.setCompleted(true);
                return false;
            }
        }
        
        return true;
    }
    
    private boolean isValidMethod(String method) {
        return Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS").contains(method);
    }
    
    private boolean isValidPath(String path) {
        return path != null && path.startsWith("/") && !path.contains("../");
    }
}

// Concrete Middleware - Application Handler (end of chain)
class ApplicationHandler extends Middleware {
    private Map<String, String> routes;
    
    public ApplicationHandler() {
        super("Application Handler");
        this.routes = new HashMap<>();
        initializeRoutes();
    }
    
    private void initializeRoutes() {
        routes.put("GET /", "Welcome to the API");
        routes.put("GET /health", "Service is healthy");
        routes.put("GET /users", "List of users");
        routes.put("POST /users", "User created successfully");
        routes.put("GET /admin", "Admin dashboard");
        routes.put("POST /data", "Data processed successfully");
    }
    
    @Override
    protected boolean processRequest(HTTPRequest request, HTTPResponse response) {
        String routeKey = request.getMethod() + " " + request.getPath();
        
        // Check if route exists
        if (!routes.containsKey(routeKey)) {
            response.setStatus(HTTPResponse.Status.NOT_FOUND);
            response.setBody("Route not found: " + request.getPath());
            response.setCompleted(true);
            return false;
        }
        
        // Check admin access
        if (request.getPath().startsWith("/admin")) {
            Boolean isAdmin = (Boolean) request.getAttribute("isAdmin");
            if (isAdmin == null || !isAdmin) {
                response.setStatus(HTTPResponse.Status.FORBIDDEN);
                response.setBody("Admin access required");
                response.setCompleted(true);
                return false;
            }
        }
        
        // Process the request
        response.setStatus(HTTPResponse.Status.OK);
        response.setBody(routes.get(routeKey));
        response.setHeader("Content-Type", "application/json");
        response.setCompleted(true);
        
        return false; // End of processing
    }
}

// Web Server class that manages the middleware chain
class WebServer {
    private Middleware middlewareChain;
    private LoggingMiddleware logger;
    
    public WebServer() {
        setupMiddlewareChain();
    }
    
    private void setupMiddlewareChain() {
        // Create middleware components
        CORSMiddleware cors = new CORSMiddleware();
        RateLimitMiddleware rateLimit = new RateLimitMiddleware(10); // 10 requests per minute
        AuthenticationMiddleware auth = new AuthenticationMiddleware();
        logger = new LoggingMiddleware();
        ValidationMiddleware validator = new ValidationMiddleware();
        ApplicationHandler appHandler = new ApplicationHandler();
        
        // Build the middleware chain
        middlewareChain = cors;
        cors.setNext(rateLimit)
            .setNext(auth)
            .setNext(logger)
            .setNext(validator)
            .setNext(appHandler);
        
        System.out.println("=== Web Server Initialized ===");
        System.out.println("Middleware Chain:");
        Middleware current = middlewareChain;
        int order = 1;
        while (current != null) {
            System.out.println(order + ". " + current.getMiddlewareName());
            current = current.next;
            order++;
        }
    }
    
    public HTTPResponse processRequest(HTTPRequest request) {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("Incoming Request: " + request);
        System.out.println("=".repeat(50));
        
        HTTPResponse response = middlewareChain.handle(request);
        
        System.out.println("\nFinal Response: " + response);
        if (response.getBody() != null) {
            System.out.println("Response Body: " + response.getBody());
        }
        
        return response;
    }
    
    public void printRequestLogs() {
        System.out.println("\n=== Request Logs ===");
        for (String log : logger.getRequestLogs()) {
            System.out.println(log);
        }
    }
}

// Client code
public class HTTPMiddlewareExample {
    public static void main(String[] args) {
        System.out.println("=== Chain of Responsibility Pattern: HTTP Middleware ===\n");
        
        WebServer server = new WebServer();
        
        // Create various HTTP requests
        List<HTTPRequest> requests = Arrays.asList(
            createRequest("GET", "/", "192.168.1.100", null),
            createRequest("GET", "/health", "192.168.1.101", null),
            createRequest("GET", "/users", "192.168.1.102", "Bearer token123"),
            createRequest("POST", "/users", "192.168.1.103", "Bearer token456", "application/json", "{\"name\":\"John\"}"),
            createRequest("GET", "/admin", "192.168.1.104", "Bearer admintoken"),
            createRequest("GET", "/admin", "192.168.1.105", "Bearer token123"), // Should fail - not admin
            createRequest("POST", "/data", "192.168.1.106", null), // Should fail - no auth
            createRequest("OPTIONS", "/users", "192.168.1.107", null, "https://example.com"),
            createRequest("INVALID", "/test", "192.168.1.108", null), // Should fail - invalid method
            createRequest("GET", "/nonexistent", "192.168.1.109", "Bearer token123") // Should fail - route not found
        );
        
        // Process each request
        for (HTTPRequest request : requests) {
            server.processRequest(request);
            
            // Small delay for readability
            try {
                Thread.sleep(800);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        // Test rate limiting by sending multiple requests from same IP
        System.out.println("\n=== Testing Rate Limiting ===");
        for (int i = 0; i < 12; i++) {
            HTTPRequest request = createRequest("GET", "/health", "192.168.1.200", null);
            server.processRequest(request);
        }
        
        // Show request logs
        server.printRequestLogs();
        
        System.out.println("\n=== Chain of Responsibility Benefits Demonstrated ===");
        System.out.println("1. Modular Processing - Each middleware handles specific concerns");
        System.out.println("2. Flexible Pipeline - Easy to add, remove, or reorder middleware");
        System.out.println("3. Cross-cutting Concerns - CORS, authentication, logging handled transparently");
        System.out.println("4. Early Termination - Invalid requests stopped early in the chain");
        System.out.println("5. Request Enhancement - Each middleware can add information for downstream processors");
        System.out.println("6. Separation of Concerns - Each middleware has single responsibility");
    }
    
    private static HTTPRequest createRequest(String method, String path, String clientIP, String authToken) {
        return createRequest(method, path, clientIP, authToken, null, null);
    }
    
    private static HTTPRequest createRequest(String method, String path, String clientIP, 
                                           String authToken, String contentType, String body) {
        HTTPRequest request = new HTTPRequest(method, path, clientIP);
        
        if (authToken != null) {
            request.setHeader("Authorization", authToken);
        }
        
        if (contentType != null) {
            request.setHeader("Content-Type", contentType);
        }
        
        if (body != null) {
            request.setBody(body);
        }
        
        return request;
    }
    
    private static HTTPRequest createRequest(String method, String path, String clientIP, 
                                           String authToken, String origin) {
        HTTPRequest request = new HTTPRequest(method, path, clientIP);
        
        if (authToken != null) {
            request.setHeader("Authorization", authToken);
        }
        
        if (origin != null) {
            request.setHeader("Origin", origin);
        }
        
        return request;
    }
}