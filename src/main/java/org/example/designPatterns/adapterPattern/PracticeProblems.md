# Adapter Pattern Practice Problems

## Overview
These practice problems will help you master the Adapter Pattern by implementing real-world integration scenarios. Start with the basic problems and progress to more complex challenges.

---

## Problem 1: File Format Converter (Basic)
**Difficulty:** ⭐⭐☆☆☆

### Problem Statement
Create a file format conversion system that can read different file formats and convert them to a common internal format:
- **CSV Reader**: Reads comma-separated values
- **JSON Reader**: Reads JSON files
- **XML Reader**: Reads XML files
- **Fixed-Width Reader**: Reads fixed-width text files
- **Excel Reader**: Reads Excel spreadsheets

### Requirements
1. Create a `DataReader` interface with methods:
   - `readData(String filePath)`
   - `getHeaders()`
   - `getRowCount()`
2. Create adapters for each file format
3. Return data in a common `DataTable` format
4. Handle different data types (string, number, date, boolean)
5. Provide error handling for malformed files
6. Support configurable options (delimiter, encoding, sheet selection)

### Test Cases
```
CSV File: "name,age,email\nJohn,25,john@example.com"
JSON File: [{"name":"John","age":25,"email":"john@example.com"}]
XML File: <records><record><name>John</name><age>25</age></record></records>

All should produce same DataTable structure:
Headers: [name, age, email]
Rows: [{name: "John", age: 25, email: "john@example.com"}]
```

### Hints
- Consider using existing libraries for parsing (simulate with simple string parsing)
- Think about how to handle different data types consistently
- Design for extensibility to add new formats easily

---

## Problem 2: Social Media Integration (Intermediate)
**Difficulty:** ⭐⭐⭐☆☆

### Problem Statement
Create a social media management system that can post to different platforms with incompatible APIs:
- **Twitter API**: Character limit, hashtags, mentions
- **Facebook API**: Rich media, link previews, targeting
- **Instagram API**: Image focus, story vs feed posts
- **LinkedIn API**: Professional content, company pages
- **TikTok API**: Video content, effects, music

### Requirements
1. Create a `SocialMediaPoster` interface:
   - `postContent(SocialMediaPost post)`
   - `schedulePost(SocialMediaPost post, DateTime scheduledTime)`
   - `deletePost(String postId)`
   - `getPostAnalytics(String postId)`
2. Handle platform-specific constraints:
   - Character limits and content formatting
   - Media type restrictions
   - Hashtag and mention formats
3. Implement cross-platform posting with automatic adaptation
4. Add analytics aggregation across platforms
5. Support platform-specific features through metadata

### Advanced Features
- Auto-resize images for different platform requirements
- Convert hashtags between platform formats
- Content optimization suggestions for each platform
- Bulk posting with platform-specific scheduling
- Engagement tracking and unified reporting

### Test Scenarios
```
Post Content: "Check out our new product! #innovation #tech @partner"
- Twitter: Auto-truncate, convert mentions
- Instagram: Focus on image, convert hashtags
- LinkedIn: Professional tone adaptation
- Facebook: Add link preview, audience targeting
```

---

## Problem 3: Legacy System Integration (Intermediate)
**Difficulty:** ⭐⭐⭐☆☆

### Problem Statement
Integrate multiple legacy systems with different interfaces into a modern application:
- **COBOL Mainframe**: Fixed-length records, EBCDIC encoding
- **Old Database**: Stored procedures, proprietary SQL dialect
- **FTP Server**: Batch file processing, specific naming conventions
- **SOAP Web Service**: XML messaging, complex schemas
- **Legacy REST API**: Outdated authentication, inconsistent responses

### Requirements
1. Create a `LegacySystemAdapter` interface:
   - `retrieveData(QueryRequest request)`
   - `updateData(UpdateRequest request)`
   - `executeTransaction(TransactionRequest request)`
2. Handle different data formats and encodings
3. Implement authentication adapters for each system
4. Provide data transformation and validation
5. Add connection pooling and retry mechanisms
6. Create unified error handling and logging

### Complex Features
- Transaction coordination across multiple systems
- Data synchronization and conflict resolution
- Performance monitoring and optimization
- Fallback mechanisms for system failures
- Audit trail for all operations
- Configuration management for different environments

---

## Problem 4: Cloud Service Integration (Advanced)
**Difficulty:** ⭐⭐⭐⭐☆

### Problem Statement
Create a unified cloud storage service that works with multiple cloud providers:
- **AWS S3**: Buckets, regions, IAM policies, lifecycle management
- **Google Cloud Storage**: Projects, buckets, ACLs, nearline/coldline
- **Azure Blob Storage**: Containers, access tiers, SAS tokens
- **Dropbox API**: File operations, sharing, team management
- **OneDrive API**: Personal/business accounts, permissions

### Requirements
1. Create a `CloudStorageService` interface:
   - `uploadFile(String path, InputStream data, StorageOptions options)`
   - `downloadFile(String path, OutputStream destination)`
   - `deleteFile(String path)`
   - `listFiles(String directory, ListOptions options)`
   - `getFileMetadata(String path)`
   - `shareFile(String path, ShareOptions options)`
2. Handle different authentication mechanisms
3. Implement consistent metadata handling
4. Add progress tracking for large file operations
5. Support resumable uploads/downloads
6. Implement cost optimization across providers

### Enterprise Features
- Multi-region replication strategies
- Automatic provider selection based on cost/performance
- Data encryption and compliance requirements
- Bandwidth optimization and CDN integration
- Backup and disaster recovery coordination
- Usage analytics and cost reporting

### Architecture Considerations
```
Upload Strategies:
- AWS: Multipart upload for large files
- Google: Resumable uploads
- Azure: Block blob uploads
- Dropbox: Session-based uploads

Adapt all to common UploadStrategy interface
```

---

## Problem 5: Message Queue Integration (Advanced)
**Difficulty:** ⭐⭐⭐⭐☆

### Problem Statement
Build a unified messaging system that works with different message queue technologies:
- **Apache Kafka**: Topics, partitions, consumer groups
- **RabbitMQ**: Exchanges, queues, routing keys
- **Amazon SQS**: FIFO queues, dead letter queues, visibility timeout
- **Redis Pub/Sub**: Channels, patterns, persistence
- **Apache ActiveMQ**: JMS standard, topics, queues

### Requirements
1. Create a `MessageBroker` interface:
   - `publishMessage(String topic, Message message)`
   - `subscribeToTopic(String topic, MessageHandler handler)`
   - `createTopic(String topic, TopicConfiguration config)`
   - `getTopicStatistics(String topic)`
2. Handle different message formats and serialization
3. Implement reliable delivery guarantees
4. Support batch operations for performance
5. Add monitoring and alerting capabilities
6. Implement message routing and filtering

### Advanced Implementation
- Message transformation between formats
- Dead letter queue handling
- Exactly-once delivery semantics
- Dynamic scaling based on queue depth
- Cross-broker message routing
- Schema registry integration
- Performance optimization for high throughput

### Messaging Patterns
```
Pattern Adaptations:
- Publish/Subscribe → All brokers
- Request/Reply → Temporary queues/correlationId
- Message Routing → Exchange types/topic filters
- Load Balancing → Consumer groups/competing consumers
```

---

## Problem 6: Authentication Provider Integration (Expert)
**Difficulty:** ⭐⭐⭐⭐⭐

### Problem Statement
Create a unified authentication system that integrates multiple identity providers:
- **OAuth 2.0 Providers**: Google, Facebook, GitHub, Microsoft
- **SAML Providers**: Enterprise identity systems
- **LDAP/Active Directory**: Corporate directory services
- **JWT Token Services**: Custom token issuers
- **Multi-Factor Authentication**: TOTP, SMS, biometric

### Requirements
1. Create an `AuthenticationProvider` interface:
   - `authenticate(AuthenticationRequest request)`
   - `validateToken(String token)`
   - `refreshToken(String refreshToken)`
   - `getUserInfo(String token)`
   - `logout(String token)`
2. Handle different authentication flows
3. Implement token normalization and validation
4. Support role-based access control (RBAC)
5. Add session management across providers
6. Implement security features (rate limiting, fraud detection)

### Enterprise Security Features
- Single Sign-On (SSO) coordination
- Federated identity management
- Compliance with security standards (GDPR, SOX, HIPAA)
- Advanced threat protection
- Audit logging and compliance reporting
- Zero-trust security model implementation
- Passwordless authentication support

### Security Considerations
```
Token Standards:
- JWT: Self-contained, stateless
- SAML: XML-based, enterprise
- OAuth: Authorization delegation
- OpenID Connect: Identity layer

Unified UserPrincipal format with role mapping
```

---

## Problem 7: IoT Device Integration (Expert)
**Difficulty:** ⭐⭐⭐⭐⭐

### Problem Statement
Build a unified IoT platform that integrates devices using different protocols and data formats:
- **MQTT Devices**: Lightweight messaging, topics, QoS levels
- **HTTP REST Devices**: RESTful APIs, polling vs webhooks
- **CoAP Devices**: Constrained devices, UDP-based
- **Bluetooth LE**: Personal area networks, characteristics
- **LoRaWAN**: Long-range, low-power devices
- **Industrial Protocols**: Modbus, OPC-UA, BACnet

### Requirements
1. Create a `DeviceAdapter` interface:
   - `connectToDevice(DeviceConfiguration config)`
   - `sendCommand(String deviceId, Command command)`
   - `readSensorData(String deviceId, List<String> sensors)`
   - `subscribeToEvents(String deviceId, EventHandler handler)`
   - `getDeviceStatus(String deviceId)`
2. Handle different communication patterns and protocols
3. Implement device discovery and auto-configuration
4. Support real-time data streaming and batch processing
5. Add device management capabilities
6. Implement edge computing and local processing

### Industrial IoT Features
- Protocol conversion and message routing
- Device firmware update management
- Predictive maintenance and anomaly detection
- Scalable data ingestion (millions of devices)
- Edge-to-cloud data synchronization
- Industrial security and compliance
- Digital twin integration
- Time-series data optimization

### Protocol Adaptations
```
Data Flow Patterns:
- MQTT: Publish/Subscribe with retained messages
- HTTP: Request/Response with polling
- CoAP: Observe pattern for notifications
- BLE: Characteristic notifications
- LoRaWAN: Uplink/Downlink with duty cycles

Unified Device abstraction with capability discovery
```

---

## Problem 8: Financial System Integration (Expert)
**Difficulty:** ⭐⭐⭐⭐⭐

### Problem Statement
Create a comprehensive financial integration platform that connects with various financial systems and protocols:
- **Banking APIs**: Account information, transactions, payments
- **Stock Market Feeds**: Real-time quotes, historical data
- **Cryptocurrency Exchanges**: Trading, wallets, DeFi protocols
- **Payment Processors**: Credit cards, ACH, wire transfers
- **Regulatory Systems**: KYC, AML, tax reporting

### Requirements
1. Create a `FinancialServiceAdapter` interface:
   - `getAccountBalance(String accountId)`
   - `executeTransaction(TransactionRequest request)`
   - `getMarketData(String symbol, DataRequest request)`
   - `validateCompliance(ComplianceCheck check)`
   - `generateReport(ReportRequest request)`
2. Handle different financial data formats and standards
3. Implement real-time data processing with low latency
4. Support complex financial calculations and risk assessment
5. Add comprehensive audit trails and compliance reporting
6. Implement high-availability and disaster recovery

### Financial Compliance Features
- PCI DSS compliance for payment data
- SOX compliance for financial reporting
- GDPR compliance for customer data
- Real-time fraud detection and prevention
- Market data entitlement management
- Regulatory change management
- Cross-border transaction handling
- Anti-money laundering (AML) integration

### Financial Protocols
```
Standards Integration:
- ISO 20022: Financial messaging standard
- FIX Protocol: Trading communications
- SWIFT: International transfers
- Open Banking APIs: PSD2 compliance
- FpML: Financial derivatives

Risk Management:
- Real-time position monitoring
- Automated compliance checks
- Market risk calculations
- Counterparty risk assessment
```

---

## Solution Structure Template

For each problem, structure your solution as follows:

```java
// Target Interface
public interface UnifiedInterface {
    CommonResult performOperation(CommonRequest request);
    boolean isAvailable();
    SystemInfo getSystemInfo();
}

// Common Data Classes
public class CommonRequest {
    // Standardized request format
}

public class CommonResult {
    // Standardized response format
}

// Adaptee Interface (Third-party system)
public class ThirdPartySystem {
    // System-specific methods with different signatures
}

// Adapter Implementation
public class ThirdPartyAdapter implements UnifiedInterface {
    private ThirdPartySystem thirdPartySystem;
    
    public ThirdPartyAdapter(ThirdPartySystem system) {
        this.thirdPartySystem = system;
    }
    
    @Override
    public CommonResult performOperation(CommonRequest request) {
        // Convert request format
        ThirdPartyRequest adaptedRequest = convertRequest(request);
        
        // Call third-party system
        ThirdPartyResponse response = thirdPartySystem.specificMethod(adaptedRequest);
        
        // Convert response format
        return convertResponse(response);
    }
    
    private ThirdPartyRequest convertRequest(CommonRequest request) {
        // Conversion logic
    }
    
    private CommonResult convertResponse(ThirdPartyResponse response) {
        // Conversion logic
    }
}

// Factory for creating adapters
public class AdapterFactory {
    public static UnifiedInterface createAdapter(String systemType, Configuration config) {
        // Factory logic
    }
}

// Service using adapters
public class IntegrationService {
    private List<UnifiedInterface> adapters;
    
    public void performOperationOnAllSystems(CommonRequest request) {
        // Unified operation across all adapted systems
    }
}
```

---

## Evaluation Criteria

Your solutions will be evaluated based on:

1. **Correct Implementation** (30%)
   - Proper use of Adapter Pattern
   - Correct interface adaptation
   - Working integration code

2. **Code Quality** (25%)
   - Clean, readable code
   - Proper error handling
   - Consistent naming conventions

3. **Integration Complexity** (20%)
   - Handling of different data formats
   - Protocol differences management
   - Authentication/security considerations

4. **Extensibility** (15%)
   - Easy to add new systems
   - Configurable adapters
   - Plugin architecture support

5. **Real-world Considerations** (10%)
   - Performance optimization
   - Error recovery mechanisms
   - Monitoring and logging
   - Documentation quality

---

## Tips for Success

1. **Understand the Systems**: Research the actual APIs and protocols you're adapting
2. **Design Common Interface First**: Define a clean, consistent target interface
3. **Handle Data Conversion**: Pay attention to data type mapping and format conversion
4. **Consider Performance**: Some adaptations may be expensive - optimize accordingly
5. **Plan for Failure**: Third-party systems fail - design resilient adapters
6. **Configuration Management**: Make adapters configurable for different environments
7. **Security First**: Ensure secure handling of credentials and sensitive data
8. **Document Everything**: Clear documentation of mappings and limitations
9. **Test Thoroughly**: Test with real data and edge cases
10. **Monitor in Production**: Add logging and metrics for troubleshooting

---

## Advanced Challenges

After completing the main problems, try these additional challenges:

1. **Dynamic Adapter Loading**: Load adapters at runtime from configuration
2. **Adapter Composition**: Combine multiple adapters for complex workflows
3. **Performance Benchmarking**: Compare adapter performance and optimize
4. **Circuit Breaker Pattern**: Add resilience to adapter calls
5. **Adapter Versioning**: Support multiple versions of the same system
6. **Auto-Discovery**: Automatically discover and configure systems
7. **Adapter Testing Framework**: Create tools for testing adapters
8. **Monitoring Dashboard**: Build real-time monitoring for all integrations

---

## Real-world Integration Patterns

1. **Enterprise Service Bus (ESB)**: Message-oriented middleware
2. **API Gateway**: Centralized API management and adaptation
3. **Extract-Transform-Load (ETL)**: Data integration pipelines
4. **Microservices Integration**: Service mesh and API composition
5. **Legacy Modernization**: Gradual system replacement strategies
6. **Cloud Migration**: Hybrid cloud integration patterns
7. **B2B Integration**: Partner system integration
8. **Mobile Backend**: Multi-platform mobile API adaptation

---

## Resources for Learning

- Enterprise Integration Patterns by Gregor Hohpe and Bobby Woolf
- Building Microservices by Sam Newman  
- Patterns of Enterprise Application Architecture by Martin Fowler
- API Design Patterns by JJ Geewax
- Integration-focused architecture documentation from major cloud providers

Good luck with your practice! The Adapter pattern is crucial for real-world system integration. Focus on creating robust, maintainable adapters that handle the complexities of production systems.