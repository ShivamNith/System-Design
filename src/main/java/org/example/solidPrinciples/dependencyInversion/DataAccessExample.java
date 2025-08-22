package org.example.solidPrinciples.dependencyInversion;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Data Access Layer - Dependency Inversion Principle Example
 * 
 * This example demonstrates how to properly invert dependencies so that
 * high-level business logic doesn't depend on low-level data access details.
 * It shows the transformation from tightly coupled data access to a flexible,
 * testable architecture using abstractions.
 * 
 * Key Concepts:
 * - High-level modules should not depend on low-level modules
 * - Both should depend on abstractions
 * - Abstractions should not depend on details
 * - Details should depend on abstractions
 * 
 * Real-world scenario: E-commerce product catalog with multiple data sources
 */
public class DataAccessExample {
    
    /**
     * BEFORE: Violating DIP - High-level business logic directly depends on low-level data access
     */
    public static class ViolatingDIP {
        
        // High-level business logic class directly depending on concrete implementations
        public static class ProductService {
            // Direct dependencies on concrete data access implementations
            private MySQLProductDatabase mysqlDb;
            private RedisCache redisCache;
            private FileSystemStorage fileStorage;
            private ElasticsearchIndex searchIndex;
            
            public ProductService() {
                // Tight coupling - creating concrete instances directly
                try {
                    Connection conn = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/products", "user", "password");
                    this.mysqlDb = new MySQLProductDatabase(conn);
                } catch (SQLException e) {
                    throw new RuntimeException("Failed to connect to MySQL", e);
                }
                
                this.redisCache = new RedisCache("localhost", 6379);
                this.fileStorage = new FileSystemStorage("/var/data/products");
                this.searchIndex = new ElasticsearchIndex("http://localhost:9200");
            }
            
            public Product getProduct(String productId) {
                // Check cache first - tightly coupled to Redis
                Product cached = redisCache.get(productId);
                if (cached != null) {
                    return cached;
                }
                
                // Get from database - tightly coupled to MySQL
                Product product = mysqlDb.findById(productId);
                if (product != null) {
                    // Cache it - again coupled to Redis
                    redisCache.put(productId, product);
                    
                    // Load images - coupled to file system
                    List<String> images = fileStorage.getProductImages(productId);
                    product.setImages(images);
                }
                
                return product;
            }
            
            public List<Product> searchProducts(String query) {
                // Tightly coupled to Elasticsearch
                List<String> productIds = searchIndex.search(query);
                List<Product> products = new ArrayList<>();
                
                for (String id : productIds) {
                    // Each search result requires DB hit - inefficient
                    Product product = mysqlDb.findById(id);
                    if (product != null) {
                        products.add(product);
                    }
                }
                
                return products;
            }
            
            public void saveProduct(Product product) {
                // Save to database - coupled to MySQL
                mysqlDb.save(product);
                
                // Update cache - coupled to Redis
                redisCache.put(product.getId(), product);
                
                // Save images - coupled to file system
                if (product.getImages() != null) {
                    fileStorage.saveProductImages(product.getId(), product.getImages());
                }
                
                // Update search index - coupled to Elasticsearch
                searchIndex.index(product);
            }
            
            public void deleteProduct(String productId) {
                mysqlDb.delete(productId);
                redisCache.remove(productId);
                fileStorage.deleteProductImages(productId);
                searchIndex.remove(productId);
            }
            
            // Problems with this approach:
            // 1. Cannot unit test without actual database, cache, etc.
            // 2. Cannot switch to different implementations (PostgreSQL, Memcached, S3)
            // 3. Business logic is polluted with infrastructure concerns
            // 4. Changes to data access affect business logic
            // 5. Difficult to add new data sources
        }
        
        // Concrete implementations that the service depends on
        public static class MySQLProductDatabase {
            private Connection connection;
            
            public MySQLProductDatabase(Connection connection) {
                this.connection = connection;
            }
            
            public Product findById(String id) {
                try {
                    PreparedStatement stmt = connection.prepareStatement(
                        "SELECT * FROM products WHERE id = ?");
                    stmt.setString(1, id);
                    ResultSet rs = stmt.executeQuery();
                    if (rs.next()) {
                        return new Product(
                            rs.getString("id"),
                            rs.getString("name"),
                            rs.getDouble("price")
                        );
                    }
                } catch (SQLException e) {
                    throw new RuntimeException("Database error", e);
                }
                return null;
            }
            
            public void save(Product product) {
                // MySQL specific save logic
                System.out.println("Saving to MySQL: " + product.getName());
            }
            
            public void delete(String id) {
                // MySQL specific delete logic
                System.out.println("Deleting from MySQL: " + id);
            }
        }
        
        public static class RedisCache {
            private String host;
            private int port;
            
            public RedisCache(String host, int port) {
                this.host = host;
                this.port = port;
                // Connect to Redis
            }
            
            public Product get(String key) {
                System.out.println("Getting from Redis: " + key);
                return null; // Simplified
            }
            
            public void put(String key, Product value) {
                System.out.println("Putting to Redis: " + key);
            }
            
            public void remove(String key) {
                System.out.println("Removing from Redis: " + key);
            }
        }
        
        public static class FileSystemStorage {
            private String basePath;
            
            public FileSystemStorage(String basePath) {
                this.basePath = basePath;
            }
            
            public List<String> getProductImages(String productId) {
                System.out.println("Loading images from filesystem: " + productId);
                return new ArrayList<>();
            }
            
            public void saveProductImages(String productId, List<String> images) {
                System.out.println("Saving images to filesystem: " + productId);
            }
            
            public void deleteProductImages(String productId) {
                System.out.println("Deleting images from filesystem: " + productId);
            }
        }
        
        public static class ElasticsearchIndex {
            private String url;
            
            public ElasticsearchIndex(String url) {
                this.url = url;
            }
            
            public List<String> search(String query) {
                System.out.println("Searching Elasticsearch: " + query);
                return new ArrayList<>();
            }
            
            public void index(Product product) {
                System.out.println("Indexing in Elasticsearch: " + product.getName());
            }
            
            public void remove(String productId) {
                System.out.println("Removing from Elasticsearch: " + productId);
            }
        }
    }
    
    /**
     * AFTER: Following DIP - Dependencies are inverted through abstractions
     */
    
    // Core domain model
    public static class Product {
        private String id;
        private String name;
        private String description;
        private double price;
        private String category;
        private int stockQuantity;
        private List<String> images;
        private Map<String, String> attributes;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        
        public Product(String id, String name, double price) {
            this.id = id;
            this.name = name;
            this.price = price;
            this.attributes = new HashMap<>();
            this.images = new ArrayList<>();
            this.createdAt = LocalDateTime.now();
            this.updatedAt = LocalDateTime.now();
        }
        
        // Getters and setters
        public String getId() { return id; }
        public String getName() { return name; }
        public void setName(String name) { 
            this.name = name;
            this.updatedAt = LocalDateTime.now();
        }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public double getPrice() { return price; }
        public void setPrice(double price) { 
            this.price = price;
            this.updatedAt = LocalDateTime.now();
        }
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        public int getStockQuantity() { return stockQuantity; }
        public void setStockQuantity(int quantity) { this.stockQuantity = quantity; }
        public List<String> getImages() { return images; }
        public void setImages(List<String> images) { this.images = images; }
        public Map<String, String> getAttributes() { return attributes; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public LocalDateTime getUpdatedAt() { return updatedAt; }
    }
    
    // Abstractions (interfaces) that both high-level and low-level modules depend on
    
    public interface ProductRepository {
        Product findById(String id);
        List<Product> findAll();
        List<Product> findByCategory(String category);
        void save(Product product);
        void update(Product product);
        void delete(String id);
        boolean exists(String id);
    }
    
    public interface CacheService {
        <T> Optional<T> get(String key, Class<T> type);
        void put(String key, Object value);
        void put(String key, Object value, int ttlSeconds);
        void evict(String key);
        void evictAll();
        boolean exists(String key);
    }
    
    public interface SearchService {
        List<String> search(String query);
        List<String> searchWithFilters(String query, Map<String, Object> filters);
        void index(Product product);
        void reindex(List<Product> products);
        void remove(String productId);
        void clearIndex();
    }
    
    public interface StorageService {
        String store(String key, byte[] data);
        byte[] retrieve(String key);
        void delete(String key);
        boolean exists(String key);
        List<String> listKeys(String prefix);
    }
    
    // Unit of Work pattern for transaction management
    public interface UnitOfWork {
        void begin();
        void commit();
        void rollback();
        boolean isActive();
    }
    
    // High-level business logic that depends on abstractions
    public static class ProductService {
        private final ProductRepository repository;
        private final CacheService cache;
        private final SearchService searchService;
        private final StorageService storage;
        private final UnitOfWork unitOfWork;
        
        // Dependencies are injected, not created
        public ProductService(ProductRepository repository,
                            CacheService cache,
                            SearchService searchService,
                            StorageService storage,
                            UnitOfWork unitOfWork) {
            this.repository = repository;
            this.cache = cache;
            this.searchService = searchService;
            this.storage = storage;
            this.unitOfWork = unitOfWork;
        }
        
        public Product getProduct(String productId) {
            // Check cache first
            Optional<Product> cached = cache.get("product:" + productId, Product.class);
            if (cached.isPresent()) {
                System.out.println("Cache hit for product: " + productId);
                return cached.get();
            }
            
            // Get from repository
            Product product = repository.findById(productId);
            if (product != null) {
                // Cache it
                cache.put("product:" + productId, product, 3600); // 1 hour TTL
                
                // Load images from storage
                List<String> imageUrls = loadProductImages(productId);
                product.setImages(imageUrls);
            }
            
            return product;
        }
        
        public List<Product> searchProducts(String query, Map<String, Object> filters) {
            // Use search service
            List<String> productIds = searchService.searchWithFilters(query, filters);
            
            // Batch load from cache/repository
            List<Product> products = new ArrayList<>();
            for (String id : productIds) {
                Product product = getProduct(id); // Uses cache
                if (product != null) {
                    products.add(product);
                }
            }
            
            return products;
        }
        
        public void saveProduct(Product product) {
            unitOfWork.begin();
            try {
                // Save to repository
                if (repository.exists(product.getId())) {
                    repository.update(product);
                } else {
                    repository.save(product);
                }
                
                // Update cache
                cache.put("product:" + product.getId(), product);
                
                // Store images
                if (product.getImages() != null && !product.getImages().isEmpty()) {
                    storeProductImages(product.getId(), product.getImages());
                }
                
                // Update search index
                searchService.index(product);
                
                unitOfWork.commit();
                System.out.println("Product saved successfully: " + product.getName());
                
            } catch (Exception e) {
                unitOfWork.rollback();
                throw new RuntimeException("Failed to save product", e);
            }
        }
        
        public void deleteProduct(String productId) {
            unitOfWork.begin();
            try {
                repository.delete(productId);
                cache.evict("product:" + productId);
                deleteProductImages(productId);
                searchService.remove(productId);
                
                unitOfWork.commit();
                System.out.println("Product deleted: " + productId);
                
            } catch (Exception e) {
                unitOfWork.rollback();
                throw new RuntimeException("Failed to delete product", e);
            }
        }
        
        public void updateStock(String productId, int quantity) {
            Product product = getProduct(productId);
            if (product != null) {
                product.setStockQuantity(quantity);
                saveProduct(product);
            }
        }
        
        private List<String> loadProductImages(String productId) {
            List<String> imageKeys = storage.listKeys("images/" + productId + "/");
            return imageKeys.stream()
                .map(key -> "/api/images/" + key)
                .collect(Collectors.toList());
        }
        
        private void storeProductImages(String productId, List<String> images) {
            for (int i = 0; i < images.size(); i++) {
                String key = "images/" + productId + "/image_" + i;
                // In real implementation, would store actual image data
                storage.store(key, images.get(i).getBytes());
            }
        }
        
        private void deleteProductImages(String productId) {
            List<String> imageKeys = storage.listKeys("images/" + productId + "/");
            for (String key : imageKeys) {
                storage.delete(key);
            }
        }
    }
    
    // Concrete implementations of the abstractions
    
    // MySQL implementation of ProductRepository
    public static class MySQLProductRepository implements ProductRepository {
        private final Connection connection;
        
        public MySQLProductRepository(Connection connection) {
            this.connection = connection;
        }
        
        @Override
        public Product findById(String id) {
            try {
                PreparedStatement stmt = connection.prepareStatement(
                    "SELECT * FROM products WHERE id = ?");
                stmt.setString(1, id);
                ResultSet rs = stmt.executeQuery();
                
                if (rs.next()) {
                    Product product = new Product(
                        rs.getString("id"),
                        rs.getString("name"),
                        rs.getDouble("price")
                    );
                    product.setDescription(rs.getString("description"));
                    product.setCategory(rs.getString("category"));
                    product.setStockQuantity(rs.getInt("stock_quantity"));
                    return product;
                }
            } catch (SQLException e) {
                throw new RuntimeException("Database error", e);
            }
            return null;
        }
        
        @Override
        public List<Product> findAll() {
            List<Product> products = new ArrayList<>();
            try {
                Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM products");
                
                while (rs.next()) {
                    Product product = new Product(
                        rs.getString("id"),
                        rs.getString("name"),
                        rs.getDouble("price")
                    );
                    products.add(product);
                }
            } catch (SQLException e) {
                throw new RuntimeException("Database error", e);
            }
            return products;
        }
        
        @Override
        public List<Product> findByCategory(String category) {
            List<Product> products = new ArrayList<>();
            try {
                PreparedStatement stmt = connection.prepareStatement(
                    "SELECT * FROM products WHERE category = ?");
                stmt.setString(1, category);
                ResultSet rs = stmt.executeQuery();
                
                while (rs.next()) {
                    Product product = new Product(
                        rs.getString("id"),
                        rs.getString("name"),
                        rs.getDouble("price")
                    );
                    product.setCategory(category);
                    products.add(product);
                }
            } catch (SQLException e) {
                throw new RuntimeException("Database error", e);
            }
            return products;
        }
        
        @Override
        public void save(Product product) {
            try {
                PreparedStatement stmt = connection.prepareStatement(
                    "INSERT INTO products (id, name, description, price, category, stock_quantity) " +
                    "VALUES (?, ?, ?, ?, ?, ?)");
                stmt.setString(1, product.getId());
                stmt.setString(2, product.getName());
                stmt.setString(3, product.getDescription());
                stmt.setDouble(4, product.getPrice());
                stmt.setString(5, product.getCategory());
                stmt.setInt(6, product.getStockQuantity());
                stmt.executeUpdate();
                
                System.out.println("MySQL: Saved product " + product.getName());
            } catch (SQLException e) {
                throw new RuntimeException("Failed to save product", e);
            }
        }
        
        @Override
        public void update(Product product) {
            try {
                PreparedStatement stmt = connection.prepareStatement(
                    "UPDATE products SET name=?, description=?, price=?, category=?, stock_quantity=? " +
                    "WHERE id=?");
                stmt.setString(1, product.getName());
                stmt.setString(2, product.getDescription());
                stmt.setDouble(3, product.getPrice());
                stmt.setString(4, product.getCategory());
                stmt.setInt(5, product.getStockQuantity());
                stmt.setString(6, product.getId());
                stmt.executeUpdate();
                
                System.out.println("MySQL: Updated product " + product.getName());
            } catch (SQLException e) {
                throw new RuntimeException("Failed to update product", e);
            }
        }
        
        @Override
        public void delete(String id) {
            try {
                PreparedStatement stmt = connection.prepareStatement(
                    "DELETE FROM products WHERE id = ?");
                stmt.setString(1, id);
                stmt.executeUpdate();
                
                System.out.println("MySQL: Deleted product " + id);
            } catch (SQLException e) {
                throw new RuntimeException("Failed to delete product", e);
            }
        }
        
        @Override
        public boolean exists(String id) {
            try {
                PreparedStatement stmt = connection.prepareStatement(
                    "SELECT 1 FROM products WHERE id = ?");
                stmt.setString(1, id);
                ResultSet rs = stmt.executeQuery();
                return rs.next();
            } catch (SQLException e) {
                throw new RuntimeException("Database error", e);
            }
        }
    }
    
    // In-memory implementation for testing
    public static class InMemoryProductRepository implements ProductRepository {
        private final Map<String, Product> products = new ConcurrentHashMap<>();
        
        @Override
        public Product findById(String id) {
            return products.get(id);
        }
        
        @Override
        public List<Product> findAll() {
            return new ArrayList<>(products.values());
        }
        
        @Override
        public List<Product> findByCategory(String category) {
            return products.values().stream()
                .filter(p -> category.equals(p.getCategory()))
                .collect(Collectors.toList());
        }
        
        @Override
        public void save(Product product) {
            products.put(product.getId(), product);
            System.out.println("InMemory: Saved product " + product.getName());
        }
        
        @Override
        public void update(Product product) {
            products.put(product.getId(), product);
            System.out.println("InMemory: Updated product " + product.getName());
        }
        
        @Override
        public void delete(String id) {
            products.remove(id);
            System.out.println("InMemory: Deleted product " + id);
        }
        
        @Override
        public boolean exists(String id) {
            return products.containsKey(id);
        }
    }
    
    // Redis implementation of CacheService
    public static class RedisCacheService implements CacheService {
        private final String host;
        private final int port;
        // In real implementation, would use Jedis or Lettuce client
        private final Map<String, Object> cache = new ConcurrentHashMap<>();
        
        public RedisCacheService(String host, int port) {
            this.host = host;
            this.port = port;
            System.out.println("Connected to Redis at " + host + ":" + port);
        }
        
        @Override
        @SuppressWarnings("unchecked")
        public <T> Optional<T> get(String key, Class<T> type) {
            Object value = cache.get(key);
            if (value != null && type.isInstance(value)) {
                System.out.println("Redis: Cache hit for " + key);
                return Optional.of((T) value);
            }
            System.out.println("Redis: Cache miss for " + key);
            return Optional.empty();
        }
        
        @Override
        public void put(String key, Object value) {
            cache.put(key, value);
            System.out.println("Redis: Cached " + key);
        }
        
        @Override
        public void put(String key, Object value, int ttlSeconds) {
            // In real implementation, would set TTL
            cache.put(key, value);
            System.out.println("Redis: Cached " + key + " with TTL " + ttlSeconds + "s");
        }
        
        @Override
        public void evict(String key) {
            cache.remove(key);
            System.out.println("Redis: Evicted " + key);
        }
        
        @Override
        public void evictAll() {
            cache.clear();
            System.out.println("Redis: Cleared all cache");
        }
        
        @Override
        public boolean exists(String key) {
            return cache.containsKey(key);
        }
    }
    
    // In-memory cache for testing
    public static class InMemoryCacheService implements CacheService {
        private final Map<String, Object> cache = new ConcurrentHashMap<>();
        
        @Override
        @SuppressWarnings("unchecked")
        public <T> Optional<T> get(String key, Class<T> type) {
            Object value = cache.get(key);
            if (value != null && type.isInstance(value)) {
                return Optional.of((T) value);
            }
            return Optional.empty();
        }
        
        @Override
        public void put(String key, Object value) {
            cache.put(key, value);
        }
        
        @Override
        public void put(String key, Object value, int ttlSeconds) {
            cache.put(key, value);
        }
        
        @Override
        public void evict(String key) {
            cache.remove(key);
        }
        
        @Override
        public void evictAll() {
            cache.clear();
        }
        
        @Override
        public boolean exists(String key) {
            return cache.containsKey(key);
        }
    }
    
    // Elasticsearch implementation of SearchService
    public static class ElasticsearchService implements SearchService {
        private final String url;
        private final Map<String, Product> index = new ConcurrentHashMap<>();
        
        public ElasticsearchService(String url) {
            this.url = url;
            System.out.println("Connected to Elasticsearch at " + url);
        }
        
        @Override
        public List<String> search(String query) {
            System.out.println("Elasticsearch: Searching for '" + query + "'");
            return index.values().stream()
                .filter(p -> p.getName().toLowerCase().contains(query.toLowerCase()) ||
                           (p.getDescription() != null && 
                            p.getDescription().toLowerCase().contains(query.toLowerCase())))
                .map(Product::getId)
                .collect(Collectors.toList());
        }
        
        @Override
        public List<String> searchWithFilters(String query, Map<String, Object> filters) {
            System.out.println("Elasticsearch: Searching with filters");
            return search(query); // Simplified
        }
        
        @Override
        public void index(Product product) {
            index.put(product.getId(), product);
            System.out.println("Elasticsearch: Indexed " + product.getName());
        }
        
        @Override
        public void reindex(List<Product> products) {
            for (Product product : products) {
                index(product);
            }
        }
        
        @Override
        public void remove(String productId) {
            index.remove(productId);
            System.out.println("Elasticsearch: Removed " + productId);
        }
        
        @Override
        public void clearIndex() {
            index.clear();
            System.out.println("Elasticsearch: Cleared index");
        }
    }
    
    // S3 implementation of StorageService
    public static class S3StorageService implements StorageService {
        private final String bucketName;
        private final Map<String, byte[]> storage = new ConcurrentHashMap<>();
        
        public S3StorageService(String bucketName) {
            this.bucketName = bucketName;
            System.out.println("Connected to S3 bucket: " + bucketName);
        }
        
        @Override
        public String store(String key, byte[] data) {
            storage.put(key, data);
            System.out.println("S3: Stored " + key + " to bucket " + bucketName);
            return "s3://" + bucketName + "/" + key;
        }
        
        @Override
        public byte[] retrieve(String key) {
            System.out.println("S3: Retrieved " + key + " from bucket " + bucketName);
            return storage.get(key);
        }
        
        @Override
        public void delete(String key) {
            storage.remove(key);
            System.out.println("S3: Deleted " + key + " from bucket " + bucketName);
        }
        
        @Override
        public boolean exists(String key) {
            return storage.containsKey(key);
        }
        
        @Override
        public List<String> listKeys(String prefix) {
            return storage.keySet().stream()
                .filter(key -> key.startsWith(prefix))
                .collect(Collectors.toList());
        }
    }
    
    // Simple Unit of Work implementation
    public static class SimpleUnitOfWork implements UnitOfWork {
        private boolean active = false;
        
        @Override
        public void begin() {
            active = true;
            System.out.println("Transaction: BEGIN");
        }
        
        @Override
        public void commit() {
            if (active) {
                System.out.println("Transaction: COMMIT");
                active = false;
            }
        }
        
        @Override
        public void rollback() {
            if (active) {
                System.out.println("Transaction: ROLLBACK");
                active = false;
            }
        }
        
        @Override
        public boolean isActive() {
            return active;
        }
    }
    
    // Dependency injection container
    public static class DIContainer {
        
        public static ProductService createProductService(String profile) {
            switch (profile) {
                case "production":
                    return createProductionService();
                case "development":
                    return createDevelopmentService();
                case "testing":
                    return createTestingService();
                default:
                    throw new IllegalArgumentException("Unknown profile: " + profile);
            }
        }
        
        private static ProductService createProductionService() {
            try {
                // Production dependencies
                Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://prod-db:3306/products", "user", "password");
                ProductRepository repository = new MySQLProductRepository(conn);
                CacheService cache = new RedisCacheService("prod-redis", 6379);
                SearchService search = new ElasticsearchService("http://prod-es:9200");
                StorageService storage = new S3StorageService("prod-images");
                UnitOfWork unitOfWork = new SimpleUnitOfWork();
                
                return new ProductService(repository, cache, search, storage, unitOfWork);
            } catch (SQLException e) {
                throw new RuntimeException("Failed to create production service", e);
            }
        }
        
        private static ProductService createDevelopmentService() {
            // Development dependencies - mix of real and in-memory
            ProductRepository repository = new InMemoryProductRepository();
            CacheService cache = new InMemoryCacheService();
            SearchService search = new ElasticsearchService("http://localhost:9200");
            StorageService storage = new S3StorageService("dev-images");
            UnitOfWork unitOfWork = new SimpleUnitOfWork();
            
            return new ProductService(repository, cache, search, storage, unitOfWork);
        }
        
        private static ProductService createTestingService() {
            // Testing dependencies - all in-memory
            ProductRepository repository = new InMemoryProductRepository();
            CacheService cache = new InMemoryCacheService();
            SearchService search = new ElasticsearchService("http://test:9200");
            StorageService storage = new S3StorageService("test-images");
            UnitOfWork unitOfWork = new SimpleUnitOfWork();
            
            return new ProductService(repository, cache, search, storage, unitOfWork);
        }
    }
    
    /**
     * Demonstration of Dependency Inversion Principle
     */
    public static void main(String[] args) {
        System.out.println("=== Data Access Layer - DIP Demo ===\n");
        
        // Create service with different configurations
        System.out.println("1. DEVELOPMENT ENVIRONMENT");
        System.out.println("─".repeat(50));
        ProductService devService = DIContainer.createProductService("development");
        
        // Create and save products
        Product laptop = new Product("1", "Gaming Laptop", 1499.99);
        laptop.setDescription("High-performance gaming laptop");
        laptop.setCategory("Electronics");
        laptop.setStockQuantity(10);
        
        Product phone = new Product("2", "Smartphone", 899.99);
        phone.setDescription("Latest flagship smartphone");
        phone.setCategory("Electronics");
        phone.setStockQuantity(25);
        
        devService.saveProduct(laptop);
        devService.saveProduct(phone);
        
        System.out.println("\n2. RETRIEVING PRODUCTS");
        System.out.println("─".repeat(50));
        Product retrieved = devService.getProduct("1");
        if (retrieved != null) {
            System.out.println("Retrieved: " + retrieved.getName() + " - $" + retrieved.getPrice());
        }
        
        System.out.println("\n3. SEARCHING PRODUCTS");
        System.out.println("─".repeat(50));
        List<Product> searchResults = devService.searchProducts("gaming", new HashMap<>());
        System.out.println("Found " + searchResults.size() + " products matching 'gaming'");
        
        System.out.println("\n4. UPDATING STOCK");
        System.out.println("─".repeat(50));
        devService.updateStock("1", 5);
        
        System.out.println("\n5. TESTING ENVIRONMENT");
        System.out.println("─".repeat(50));
        ProductService testService = DIContainer.createProductService("testing");
        
        Product testProduct = new Product("test1", "Test Product", 99.99);
        testService.saveProduct(testProduct);
        
        System.out.println("\n6. BENEFITS OF DIP IN THIS DESIGN");
        System.out.println("─".repeat(50));
        System.out.println("✅ Business logic doesn't depend on specific data sources");
        System.out.println("✅ Easy to switch between MySQL, PostgreSQL, MongoDB, etc.");
        System.out.println("✅ Can use in-memory implementations for testing");
        System.out.println("✅ Cache implementation can be changed without affecting business logic");
        System.out.println("✅ Storage can be switched from S3 to Azure Blob or local filesystem");
        System.out.println("✅ Search can be switched from Elasticsearch to Solr or Algolia");
        System.out.println("✅ All dependencies are injected, making testing easy");
        System.out.println("✅ High-level and low-level modules both depend on abstractions");
        
        System.out.println("\n=== Demo Complete ===");
    }
}