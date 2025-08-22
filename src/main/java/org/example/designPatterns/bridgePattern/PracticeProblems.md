# Bridge Pattern Practice Problems

## Overview
These practice problems will help you master the Bridge Pattern by implementing real-world scenarios where you need to separate abstraction from implementation. Focus on creating flexible systems that can work across multiple platforms or technologies.

---

## Problem 1: Media Player System (Basic)
**Difficulty:** ⭐⭐☆☆☆

### Problem Statement
Create a media player system that can play different audio formats using different audio engines:
- **Audio Formats**: MP3, WAV, FLAC, AAC
- **Audio Engines**: Windows Media Foundation, Core Audio (macOS), ALSA (Linux), OpenAL

### Requirements
1. Create `AudioEngine` interface with methods:
   - `initialize()`
   - `playAudio(byte[] audioData, String format)`
   - `pauseAudio()`
   - `stopAudio()`
   - `setVolume(float volume)`
   - `getEngineInfo()`

2. Implement different audio engines with platform-specific features
3. Create `MediaPlayer` abstraction that uses audio engines
4. Support playlist functionality and format conversion
5. Allow runtime switching between audio engines

### Test Cases
```
Audio File: "song.mp3" (MP3 format, 3.5MB)
- Windows Engine: DirectSound output, 44.1kHz
- macOS Engine: Core Audio output, 48kHz  
- Linux Engine: ALSA output, 44.1kHz
- OpenAL Engine: 3D spatial audio support
```

### Hints
- Consider audio quality differences between engines
- Handle engine-specific initialization requirements
- Think about error handling for unsupported formats
- Implement proper resource cleanup

---

## Problem 2: Cloud Storage Manager (Intermediate)
**Difficulty:** ⭐⭐⭐☆☆

### Problem Statement
Design a cloud storage system that can work with different cloud providers:
- **Cloud Providers**: AWS S3, Google Cloud Storage, Azure Blob, Dropbox, OneDrive
- **Operations**: Upload, download, delete, list files, share links, sync

### Requirements
1. Create `CloudStorageProvider` interface:
   - `connect(String credentials)`
   - `uploadFile(String fileName, byte[] data, Map<String, String> metadata)`
   - `downloadFile(String fileName)`
   - `deleteFile(String fileName)`
   - `listFiles(String directory)`
   - `generateShareLink(String fileName, int expiryDays)`
   - `getStorageInfo()`

2. Implement provider-specific features:
   - AWS S3: Lifecycle policies, versioning
   - Google Cloud: Nearline/Coldline storage classes
   - Azure: Hot/Cool/Archive tiers
   - Dropbox: Team sharing features
   - OneDrive: Office integration

3. Create `CloudStorageManager` abstraction with:
   - File synchronization
   - Bandwidth optimization
   - Retry mechanisms
   - Progress tracking

### Advanced Features
- Multi-provider backup strategy
- Cost optimization across providers
- Automatic failover between providers
- File deduplication
- Encryption at rest and in transit

### Test Scenarios
```
Upload 100MB video file:
- AWS S3: Use multipart upload, S3 Standard storage
- Google Cloud: Use resumable upload, Standard storage
- Azure: Use block upload, Hot tier
- Dropbox: Use chunked upload with delta sync
```

---

## Problem 3: Graphics Rendering Engine (Intermediate)
**Difficulty:** ⭐⭐⭐☆☆

### Problem Statement
Create a graphics rendering system that works with different graphics APIs:
- **Graphics APIs**: OpenGL, DirectX, Vulkan, Metal, WebGL
- **Primitives**: Points, Lines, Triangles, Quads, Textures
- **Features**: Shaders, Lighting, Texturing, Animations

### Requirements
1. Create `GraphicsRenderer` interface:
   - `initialize(int width, int height)`
   - `createBuffer(float[] vertices, int[] indices)`
   - `loadTexture(String imagePath)`
   - `createShader(String vertexShader, String fragmentShader)`
   - `drawPrimitive(PrimitiveType type, int bufferID)`
   - `setProjectionMatrix(float[] matrix)`
   - `swapBuffers()`

2. Implement API-specific optimizations:
   - OpenGL: VAOs, VBOs, modern pipeline
   - DirectX: Command lists, resource barriers
   - Vulkan: Command buffers, synchronization
   - Metal: Command encoders, render passes
   - WebGL: Browser compatibility, extensions

3. Create graphics abstractions:
   - `Shape` (Circle, Rectangle, Polygon)
   - `Sprite` (2D textured quad)
   - `Model` (3D mesh with materials)
   - `Scene` (collection of drawable objects)

### Complex Features
- Multi-pass rendering
- Post-processing effects
- Particle systems
- Skeletal animation
- Level-of-detail (LOD) system

---

## Problem 4: Payment Gateway System (Advanced)
**Difficulty:** ⭐⭐⭐⭐☆

### Problem Statement
Design a payment processing system that integrates with multiple payment providers:
- **Providers**: Stripe, PayPal, Square, Razorpay, Adyen, Braintree
- **Payment Methods**: Credit cards, Digital wallets, Bank transfers, Cryptocurrency
- **Operations**: Process payments, Refunds, Subscriptions, Webhooks

### Requirements
1. Create `PaymentProvider` interface:
   - `authenticateClient(String apiKey, String secretKey)`
   - `processPayment(PaymentRequest request)`
   - `processRefund(String transactionId, double amount)`
   - `createSubscription(SubscriptionRequest request)`
   - `handleWebhook(String payload, String signature)`
   - `getTransactionStatus(String transactionId)`
   - `getProviderCapabilities()`

2. Implement provider-specific features:
   - Stripe: Strong Customer Authentication (SCA), Connect platform
   - PayPal: Express Checkout, recurring payments
   - Square: In-person payments, inventory management
   - Razorpay: UPI, net banking (India-specific)
   - Cryptocurrency: Blockchain confirmations, wallet integration

3. Create payment abstractions:
   - `PaymentProcessor` (main abstraction)
   - `FraudDetection` (risk scoring, velocity checks)
   - `PaymentReconciliation` (settlement tracking)
   - `ComplianceManager` (PCI DSS, regional regulations)

### Advanced Requirements
- Multi-currency support with real-time conversion
- Smart routing (choose best provider based on cost/success rate)
- Cascading fallback (try multiple providers)
- Regulatory compliance (GDPR, PCI DSS, PSD2)
- Real-time analytics and reporting
- Dispute management
- Tokenization for stored payment methods

### Integration Challenges
- Handle different error response formats
- Manage varying webhook formats
- Deal with different authentication methods
- Support various testing/sandbox environments
- Handle rate limiting and retries

---

## Problem 5: Database Query Engine (Advanced)
**Difficulty:** ⭐⭐⭐⭐☆

### Problem Statement
Create a unified query engine that can work with different database types:
- **Databases**: MySQL, PostgreSQL, MongoDB, Cassandra, Redis, Elasticsearch
- **Query Types**: CRUD operations, Aggregations, Full-text search, Graph queries
- **Features**: Connection pooling, Query optimization, Caching, Transactions

### Requirements
1. Create `DatabaseConnector` interface:
   - `connect(ConnectionConfig config)`
   - `executeQuery(QueryObject query)`
   - `executeTransaction(List<QueryObject> queries)`
   - `createIndex(String table, List<String> columns)`
   - `getQueryPlan(QueryObject query)`
   - `getPerformanceMetrics()`

2. Implement database-specific optimizations:
   - SQL databases: Query plan analysis, index recommendations
   - MongoDB: Aggregation pipeline optimization
   - Cassandra: Partition key optimization
   - Redis: Pipeline batching, cluster support
   - Elasticsearch: Query DSL, faceted search

3. Create query abstractions:
   - `QueryBuilder` (fluent API for building queries)
   - `ResultMapper` (convert results to objects)
   - `CacheManager` (intelligent query result caching)
   - `MigrationManager` (schema evolution)

### Expert Features
- Query federation across multiple databases
- Automatic query optimization
- Intelligent caching strategies
- Real-time replication monitoring
- Automatic failover and load balancing
- Query performance analysis
- Schema migration tools

---

## Problem 6: Video Streaming Platform (Expert)
**Difficulty:** ⭐⭐⭐⭐⭐

### Problem Statement
Build a video streaming platform that supports multiple streaming protocols and CDNs:
- **Protocols**: HLS, DASH, RTMP, WebRTC, SRT
- **CDNs**: CloudFlare, AWS CloudFront, Azure CDN, Google Cloud CDN
- **Features**: Adaptive bitrate, Live streaming, DVR, Analytics

### Requirements
1. Create `StreamingProvider` interface:
   - `initializeStream(StreamConfig config)`
   - `publishStream(String streamKey, VideoData data)`
   - `subscribeToStream(String streamId, QualityLevel quality)`
   - `generatePlaylistURL(String streamId, Protocol protocol)`
   - `getViewerAnalytics(String streamId)`
   - `handleViewerConnection(ViewerSession session)`

2. Implement protocol-specific features:
   - HLS: Segment generation, master playlist
   - DASH: MPD generation, dynamic adaptation
   - RTMP: Real-time publishing, low latency
   - WebRTC: Peer-to-peer, ultra-low latency
   - SRT: Reliable transport, error recovery

3. Create streaming abstractions:
   - `VideoEncoder` (H.264, H.265, VP9, AV1)
   - `AdaptiveBitrateManager` (quality switching logic)
   - `CDNManager` (content distribution, caching)
   - `AnalyticsCollector` (viewer metrics, performance data)

### Complex Requirements
- Multi-bitrate encoding and packaging
- Geographic content distribution
- Real-time viewer analytics
- Content protection (DRM)
- Live transcoding and processing
- Edge server management
- Bandwidth optimization
- Quality of Service monitoring

---

## Problem 7: Machine Learning Model Serving (Expert)
**Difficulty:** ⭐⭐⭐⭐⭐

### Problem Statement
Design a model serving platform that can deploy ML models across different inference engines:
- **Engines**: TensorFlow Serving, TorchServe, ONNX Runtime, TensorRT, OpenVINO
- **Model Types**: Neural networks, Tree-based models, Computer vision, NLP
- **Deployment**: Batch inference, Real-time inference, Edge deployment

### Requirements
1. Create `InferenceEngine` interface:
   - `loadModel(ModelArtifact model, ModelConfig config)`
   - `predict(InputData input)`
   - `batchPredict(List<InputData> inputs)`
   - `getModelMetadata()`
   - `optimizeModel(OptimizationConfig config)`
   - `getInferenceMetrics()`

2. Implement engine-specific optimizations:
   - TensorFlow: Graph optimization, quantization
   - PyTorch: TorchScript compilation, mobile optimization
   - ONNX: Cross-platform compatibility, hardware acceleration
   - TensorRT: GPU optimization, reduced precision
   - OpenVINO: Intel hardware optimization

3. Create serving abstractions:
   - `ModelRegistry` (version management, A/B testing)
   - `AutoScaler` (dynamic resource allocation)
   - `ModelMonitor` (drift detection, performance tracking)
   - `PipelineManager` (multi-model workflows)

### Advanced Features
- Model versioning and rollback
- A/B testing and canary deployments
- Automatic scaling based on load
- Model performance monitoring
- Feature store integration
- Explainability and interpretability
- Edge deployment optimization
- Multi-tenant isolation

---

## Problem 8: IoT Device Management Platform (Expert)
**Difficulty:** ⭐⭐⭐⭐⭐

### Problem Statement
Create an IoT platform that manages devices across different communication protocols:
- **Protocols**: MQTT, CoAP, LoRaWAN, Zigbee, WiFi, Bluetooth, NB-IoT
- **Device Types**: Sensors, Actuators, Gateways, Edge computers
- **Features**: Device provisioning, Firmware updates, Data collection, Remote control

### Requirements
1. Create `IoTProtocolHandler` interface:
   - `connectDevice(DeviceCredentials credentials)`
   - `sendCommand(String deviceId, Command command)`
   - `receiveData(String deviceId)`
   - `updateFirmware(String deviceId, FirmwarePackage firmware)`
   - `getDeviceStatus(String deviceId)`
   - `handleDeviceEvents(EventHandler handler)`

2. Implement protocol-specific features:
   - MQTT: QoS levels, retained messages, last will
   - CoAP: Observe pattern, blockwise transfer
   - LoRaWAN: OTAA/ABP activation, spreading factors
   - Zigbee: Mesh networking, cluster commands
   - NB-IoT: Power saving modes, extended coverage

3. Create IoT abstractions:
   - `DeviceManager` (lifecycle management)
   - `DataProcessor` (real-time stream processing)
   - `AlertManager` (threshold monitoring, notifications)
   - `SecurityManager` (authentication, encryption)

### Complex Requirements
- Device fleet management at scale
- Real-time data processing and analytics
- Edge computing and local processing
- Device security and certificate management
- Over-the-air firmware updates
- Network topology management
- Power consumption optimization
- Interoperability across protocols

---

## Solution Structure Template

For each problem, structure your solution as follows:

```java
// Implementation interface
public interface XxxImplementation {
    // Define primitive operations
}

// Concrete Implementations
public class ConcreteImplementationA implements XxxImplementation {
    // Platform/provider-specific implementation
}

public class ConcreteImplementationB implements XxxImplementation {
    // Different platform/provider-specific implementation
}

// Abstraction
public abstract class AbstractionBase {
    protected XxxImplementation implementation;
    
    public AbstractionBase(XxxImplementation implementation) {
        this.implementation = implementation;
    }
    
    public void setImplementation(XxxImplementation implementation) {
        this.implementation = implementation;
    }
    
    // Abstract methods for refined abstractions
    public abstract void operation1();
    public abstract void operation2();
}

// Refined Abstractions
public class RefinedAbstractionA extends AbstractionBase {
    public RefinedAbstractionA(XxxImplementation implementation) {
        super(implementation);
    }
    
    @Override
    public void operation1() {
        // Use implementation to perform operations
        implementation.primitiveOperation1();
        // Add abstraction-specific logic
    }
}

// Client Code
public class BridgePatternDemo {
    public static void main(String[] args) {
        // Create implementations
        XxxImplementation impl1 = new ConcreteImplementationA();
        XxxImplementation impl2 = new ConcreteImplementationB();
        
        // Create abstraction with implementation
        AbstractionBase abstraction = new RefinedAbstractionA(impl1);
        
        // Use the abstraction
        abstraction.operation1();
        
        // Switch implementation at runtime
        abstraction.setImplementation(impl2);
        abstraction.operation1();
    }
}
```

---

## Evaluation Criteria

Your solutions will be evaluated based on:

1. **Correct Bridge Pattern Implementation** (30%)
   - Proper separation of abstraction and implementation
   - Correct use of composition over inheritance
   - Interface design that allows independent variation

2. **Code Quality and Design** (25%)
   - Clean, readable, and maintainable code
   - Proper use of SOLID principles
   - Appropriate error handling and edge cases

3. **Functionality and Features** (20%)
   - All requirements implemented correctly
   - Working demonstrations of key features
   - Proper handling of different scenarios

4. **Extensibility and Flexibility** (15%)
   - Easy to add new implementations
   - Easy to add new abstractions
   - Runtime switching between implementations

5. **Real-world Applicability** (10%)
   - Solutions that could work in production
   - Consideration of performance and scalability
   - Appropriate use of design patterns

---

## Tips for Success

1. **Understand the Domain**: Research the specific technologies/platforms you're working with
2. **Start with Interfaces**: Design the implementation interface before concrete classes
3. **Think About Variations**: Consider how both abstractions and implementations might evolve
4. **Handle Differences**: Account for platform/provider-specific capabilities and limitations
5. **Test Thoroughly**: Ensure your bridge works correctly with all combinations
6. **Consider Performance**: Some implementations may be faster/slower than others
7. **Document Decisions**: Explain why you chose specific interface designs
8. **Plan for Failure**: Handle cases where implementations are unavailable or fail

---

## Bonus Challenges

After completing the main problems, try these additional challenges:

1. **Dynamic Implementation Discovery**: Load implementations at runtime using reflection or plugins
2. **Implementation Chaining**: Chain multiple implementations together (e.g., caching + storage)
3. **Performance Benchmarking**: Compare performance across different implementations
4. **Configuration Management**: External configuration for implementation selection
5. **Monitoring and Metrics**: Add comprehensive monitoring to your bridge implementations
6. **Testing Framework**: Create automated tests for all abstraction-implementation combinations
7. **Documentation Generator**: Generate API documentation from your bridge interfaces
8. **Migration Tools**: Create tools to migrate between different implementations

---

## Common Pitfalls to Avoid

1. **Leaky Abstractions**: Don't let implementation details leak through the abstraction
2. **Over-Engineering**: Don't use Bridge pattern for simple scenarios
3. **Inconsistent Interfaces**: Ensure all implementations truly follow the same contract
4. **Ignoring Capabilities**: Account for different capabilities across implementations
5. **Poor Error Handling**: Handle implementation-specific errors gracefully
6. **Tight Coupling**: Avoid dependencies between abstraction and specific implementations
7. **Performance Ignorance**: Consider the performance implications of your design
8. **Testing Gaps**: Test all combinations of abstractions and implementations

Good luck with your Bridge Pattern implementation! Remember that the key is to create a design where abstractions and implementations can vary independently while maintaining a consistent interface for clients.