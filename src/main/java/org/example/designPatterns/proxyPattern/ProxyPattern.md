# Proxy Pattern

## Overview
The Proxy Pattern is a structural design pattern that provides a placeholder or surrogate for another object to control access to it. The proxy acts as an intermediary between the client and the real object, allowing for additional functionality such as lazy loading, access control, caching, or logging.

## Problem Statement
Sometimes you need to control access to an object, add functionality before/after method calls, or defer expensive object creation. Direct access to the object might not be suitable due to:
- Resource-intensive object creation
- Remote object access
- Security restrictions
- Caching requirements
- Logging and monitoring needs

## Solution
The Proxy pattern solves this by:
1. **Proxy Interface**: Common interface for both proxy and real object
2. **Real Subject**: The actual object that performs the work
3. **Proxy**: Controls access to the real subject and adds additional behavior
4. **Transparent Access**: Client interacts with proxy as if it were the real object

## Structure

### Components
1. **Subject Interface**: Defines the common interface for RealSubject and Proxy
2. **RealSubject**: The real object that the proxy represents
3. **Proxy**: Controls access to RealSubject and may add additional behavior
4. **Client**: Uses the Subject interface to work with both proxy and real objects

## Types of Proxies

### 1. Virtual Proxy
- **Purpose**: Lazy initialization of expensive objects
- **Use Case**: Loading large images, database connections
- **Benefit**: Improves performance by deferring creation

### 2. Protection Proxy
- **Purpose**: Access control and security
- **Use Case**: User permissions, authentication
- **Benefit**: Centralized security management

### 3. Remote Proxy
- **Purpose**: Represents objects in different address spaces
- **Use Case**: Network services, distributed systems
- **Benefit**: Transparent remote access

### 4. Caching Proxy
- **Purpose**: Stores results of expensive operations
- **Use Case**: Database queries, web requests
- **Benefit**: Improved performance through caching

## Implementation Details

### Key Characteristics
- **Transparent Interface**: Same interface as the real object
- **Lazy Loading**: Create real object only when needed
- **Access Control**: Validate permissions before delegation
- **Additional Behavior**: Logging, caching, validation

### Benefits
1. **Controlled Access**: Manages how and when the real object is accessed
2. **Lazy Initialization**: Defers expensive object creation
3. **Additional Functionality**: Adds cross-cutting concerns
4. **Transparency**: Client code remains unchanged
5. **Security**: Centralizes access control logic

### Drawbacks
1. **Complexity**: Adds an extra layer of indirection
2. **Performance**: May introduce overhead for simple operations
3. **Code Duplication**: Proxy may duplicate interface methods
4. **Maintenance**: Additional class to maintain

## When to Use
- Need to control access to an object
- Want to add functionality without changing the object
- Object creation is expensive and should be deferred
- Need to implement cross-cutting concerns (logging, security, caching)
- Working with remote objects or services

## Real-world Applications
1. **Web Proxies**: Caching and filtering web content
2. **ORM Frameworks**: Lazy loading of database entities
3. **Security Systems**: Authentication and authorization
4. **Image Loading**: Progressive image loading in applications
5. **Network Services**: Service discovery and load balancing

## Implementation Examples
1. **Virtual Proxy for Image Loading**: Defer expensive image loading
2. **Protection Proxy for Document Access**: Control document access based on permissions
3. **Caching Proxy for Database**: Cache expensive database queries

## Best Practices
1. Keep proxy interface identical to the real object
2. Implement lazy initialization properly
3. Handle exceptions from the real object
4. Consider thread safety in multi-threaded environments
5. Use composition over inheritance
6. Document proxy behavior clearly