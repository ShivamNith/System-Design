# Template Method Pattern

## Overview
The Template Method Pattern defines the skeleton of an algorithm in a base class, letting subclasses override specific steps of the algorithm without changing its structure. It's a behavioral design pattern that promotes code reuse and enforces a consistent algorithm structure across different implementations.

## Intent
- Define the skeleton of an algorithm in an operation, deferring some steps to subclasses
- Let subclasses redefine certain steps of an algorithm without changing the algorithm's structure
- Promote code reuse through inheritance while allowing customization of specific behaviors

## Problem Statement
You have several classes that perform similar operations with mostly identical steps, but differ in the implementation details of some steps. Instead of duplicating the entire algorithm in each class, you want to:
- Extract the common algorithm structure into a base class
- Allow subclasses to customize only the specific steps that vary
- Ensure the overall algorithm flow remains consistent

## Solution Structure

### UML Class Diagram
```
┌─────────────────────────┐
│    AbstractClass        │
├─────────────────────────┤
│ + templateMethod()      │ ← Defines algorithm skeleton
│ # primitiveOperation1() │ ← Abstract methods
│ # primitiveOperation2() │
│ # hook()               │ ← Optional hook methods
└─────────────────────────┘
            ▲
            │
    ┌───────┴───────┐
    │               │
┌─────────────────────────┐  ┌─────────────────────────┐
│   ConcreteClassA        │  │   ConcreteClassB        │
├─────────────────────────┤  ├─────────────────────────┤
│ # primitiveOperation1() │  │ # primitiveOperation1() │
│ # primitiveOperation2() │  │ # primitiveOperation2() │
│ # hook()               │  │ # hook()               │
└─────────────────────────┘  └─────────────────────────┘
```

### Key Components
1. **Abstract Class**: Defines the template method and declares primitive operations
2. **Template Method**: Defines the algorithm skeleton, calls primitive operations in specific order
3. **Primitive Operations**: Abstract methods that subclasses must implement
4. **Hook Methods**: Optional methods that provide default behavior but can be overridden
5. **Concrete Classes**: Implement the primitive operations to complete the algorithm

## Implementation

### Basic Template Method Structure
```java
public abstract class AbstractClass {
    // Template method defines the algorithm skeleton
    public final void templateMethod() {
        stepOne();
        stepTwo();
        if (hook()) {
            stepThree();
        }
        stepFour();
    }
    
    // Primitive operations - must be implemented by subclasses
    protected abstract void stepOne();
    protected abstract void stepTwo();
    protected abstract void stepFour();
    
    // Hook method - provides default behavior
    protected boolean hook() {
        return true;
    }
    
    // Optional: Common behavior that doesn't vary
    private void stepThree() {
        System.out.println("Common step three implementation");
    }
}
```

### Method Types in Template Method Pattern

#### 1. Template Methods
- Define the algorithm skeleton
- Should be final to prevent override
- Call primitive operations and hooks in specific order

#### 2. Primitive Operations (Abstract Methods)
- Must be implemented by subclasses
- Represent the varying parts of the algorithm
- Should be protected to allow subclass access

#### 3. Hook Methods
- Provide default behavior that can be optionally overridden
- Allow fine-tuning of the algorithm
- Often return boolean values for conditional execution

#### 4. Concrete Operations
- Implement common behavior that doesn't vary
- Called by the template method
- Should be private or protected

## Real-World Examples

### 1. Data Processing Pipeline
```java
public abstract class DataProcessor {
    // Template method
    public final ProcessResult processData() {
        Data rawData = extractData();
        Data cleanedData = cleanData(rawData);
        Data transformedData = transformData(cleanedData);
        ValidationResult validation = validateData(transformedData);
        
        if (validation.isValid()) {
            return saveData(transformedData);
        }
        
        return handleInvalidData(validation);
    }
    
    protected abstract Data extractData();
    protected abstract Data transformData(Data data);
    protected abstract ProcessResult saveData(Data data);
    
    // Default implementations (hooks)
    protected Data cleanData(Data data) {
        return data.removeNullValues();
    }
    
    protected ValidationResult validateData(Data data) {
        return new ValidationResult(true);
    }
    
    protected ProcessResult handleInvalidData(ValidationResult validation) {
        return ProcessResult.failure("Data validation failed");
    }
}
```

### 2. Authentication Process
```java
public abstract class AuthenticationProcess {
    public final AuthResult authenticate(String username, String password) {
        if (!validateInput(username, password)) {
            return AuthResult.failure("Invalid input");
        }
        
        User user = findUser(username);
        if (user == null) {
            return AuthResult.failure("User not found");
        }
        
        if (!verifyPassword(user, password)) {
            logFailedAttempt(username);
            return AuthResult.failure("Invalid credentials");
        }
        
        if (requiresTwoFactor(user)) {
            return handleTwoFactorAuth(user);
        }
        
        return createSession(user);
    }
    
    protected abstract User findUser(String username);
    protected abstract boolean verifyPassword(User user, String password);
    protected abstract AuthResult createSession(User user);
    
    // Hooks with default implementations
    protected boolean validateInput(String username, String password) {
        return username != null && password != null && 
               username.trim().length() > 0 && password.length() >= 8;
    }
    
    protected boolean requiresTwoFactor(User user) {
        return user.hasTwoFactorEnabled();
    }
    
    protected void logFailedAttempt(String username) {
        System.out.println("Failed login attempt for: " + username);
    }
    
    protected AuthResult handleTwoFactorAuth(User user) {
        return AuthResult.requiresTwoFactor(user.getId());
    }
}
```

## Advanced Concepts

### 1. Hook Methods with Parameters
```java
public abstract class FileProcessor {
    public final void processFile(String filename) {
        FileData data = readFile(filename);
        
        if (shouldPreprocess(data)) {
            data = preprocess(data);
        }
        
        ProcessResult result = processData(data);
        
        if (shouldSaveResult(result)) {
            saveResult(result, getOutputFilename(filename));
        }
        
        cleanup(data, result);
    }
    
    protected abstract ProcessResult processData(FileData data);
    
    // Hook methods with different signatures
    protected boolean shouldPreprocess(FileData data) {
        return false;
    }
    
    protected FileData preprocess(FileData data) {
        return data;
    }
    
    protected boolean shouldSaveResult(ProcessResult result) {
        return result.isSuccessful();
    }
    
    protected String getOutputFilename(String inputFilename) {
        return inputFilename + ".processed";
    }
    
    protected void cleanup(FileData data, ProcessResult result) {
        // Default: no cleanup needed
    }
}
```

### 2. Template Method with Strategy Pattern
```java
public abstract class ReportGenerator {
    private DataFormatter formatter;
    
    public ReportGenerator(DataFormatter formatter) {
        this.formatter = formatter;
    }
    
    public final Report generateReport(ReportRequest request) {
        Data rawData = collectData(request);
        Data processedData = processData(rawData);
        FormattedData formattedData = formatter.format(processedData);
        
        Report report = createReport(formattedData);
        
        if (shouldAddMetadata()) {
            report = addMetadata(report, request);
        }
        
        return report;
    }
    
    protected abstract Data collectData(ReportRequest request);
    protected abstract Data processData(Data rawData);
    protected abstract Report createReport(FormattedData data);
    
    protected boolean shouldAddMetadata() {
        return true;
    }
    
    protected Report addMetadata(Report report, ReportRequest request) {
        return report.withMetadata(
            "Generated", LocalDateTime.now(),
            "Requestor", request.getRequestor()
        );
    }
}
```

## Best Practices

### 1. Design Guidelines
- **Make template methods final**: Prevents subclasses from changing the algorithm structure
- **Use protected access**: Allows subclasses to implement abstract methods
- **Provide meaningful hook methods**: Give subclasses control over algorithm behavior
- **Keep primitive operations focused**: Each method should have a single responsibility

### 2. Implementation Tips
```java
public abstract class BestPracticeTemplate {
    // 1. Final template method
    public final void templateMethod() {
        mandatoryStepOne();
        
        if (shouldExecuteOptionalStep()) {
            optionalStep();
        }
        
        mandatoryStepTwo();
        performCustomLogic();
        finalStep();
    }
    
    // 2. Abstract methods for varying behavior
    protected abstract void mandatoryStepOne();
    protected abstract void mandatoryStepTwo();
    protected abstract void performCustomLogic();
    
    // 3. Hook methods with sensible defaults
    protected boolean shouldExecuteOptionalStep() {
        return false; // Conservative default
    }
    
    protected void optionalStep() {
        // Empty default implementation
    }
    
    // 4. Invariant operations should be private or final
    private void finalStep() {
        System.out.println("Template execution completed");
    }
}
```

### 3. Error Handling
```java
public abstract class RobustTemplate {
    public final Result executeTemplate() {
        try {
            validatePreconditions();
            Result result = performMainOperation();
            return postProcess(result);
        } catch (Exception e) {
            return handleError(e);
        } finally {
            cleanup();
        }
    }
    
    protected abstract Result performMainOperation();
    
    protected void validatePreconditions() {
        // Default: no validation
    }
    
    protected Result postProcess(Result result) {
        return result;
    }
    
    protected Result handleError(Exception e) {
        return Result.failure(e.getMessage());
    }
    
    protected void cleanup() {
        // Default: no cleanup needed
    }
}
```

## When to Use Template Method Pattern

### Use When:
1. **Common algorithm structure**: Multiple classes share the same algorithm outline
2. **Controlled variation**: You want to allow customization of specific steps only
3. **Code duplication**: Similar code exists across multiple classes
4. **Framework design**: Building extensible frameworks or libraries
5. **Process standardization**: Need to ensure consistent process execution

### Don't Use When:
1. **Algorithm varies significantly**: The overall structure differs between implementations
2. **Simple inheritance**: Basic method overriding is sufficient
3. **Composition preferred**: When composition provides better flexibility
4. **Single responsibility**: When each class has completely different responsibilities

## Comparison with Other Patterns

### Template Method vs Strategy Pattern
| Aspect | Template Method | Strategy |
|--------|----------------|----------|
| **Structure** | Inheritance-based | Composition-based |
| **Algorithm** | Fixed skeleton, variable steps | Entire algorithm can vary |
| **Flexibility** | Less flexible, more structured | More flexible, less structured |
| **Runtime** | Cannot change behavior at runtime | Can change behavior at runtime |
| **Use Case** | Similar processes with variations | Different approaches to same problem |

### Template Method vs Factory Method
| Aspect | Template Method | Factory Method |
|--------|----------------|----------------|
| **Purpose** | Algorithm structure | Object creation |
| **Variation** | Steps in algorithm | Type of objects created |
| **Method Type** | Multiple abstract methods | Single abstract method |
| **Complexity** | Can be complex algorithm | Usually simple creation logic |

## Common Pitfalls and Solutions

### 1. Overly Complex Template Methods
**Problem**: Template method becomes too long and complex
```java
// Bad: Too complex
public final void complexTemplate() {
    stepA(); stepB(); stepC(); stepD(); stepE();
    stepF(); stepG(); stepH(); stepI(); stepJ();
    // ... many more steps
}
```

**Solution**: Break into smaller template methods
```java
// Good: Decomposed into smaller templates
public final void mainTemplate() {
    performInitialization();
    processData();
    generateOutput();
}

private void performInitialization() {
    stepA(); stepB(); stepC();
}

private void processData() {
    stepD(); stepE(); stepF();
}

private void generateOutput() {
    stepG(); stepH(); stepI();
}
```

### 2. Too Many Abstract Methods
**Problem**: Forcing subclasses to implement too many methods
```java
// Bad: Too many abstract methods
protected abstract void step1();
protected abstract void step2();
protected abstract void step3();
protected abstract void step4();
protected abstract void step5();
// ... many more
```

**Solution**: Provide default implementations where possible
```java
// Good: Mix of abstract and hook methods
protected abstract void essentialStep1();
protected abstract void essentialStep2();

protected void optionalStep3() {
    // Default implementation
}

protected boolean shouldExecuteStep4() {
    return false; // Hook method
}
```

### 3. Inappropriate Use of Final
**Problem**: Making all methods final, preventing necessary overrides
```java
// Bad: Hook method is final
protected final boolean shouldProcess() {
    return true;
}
```

**Solution**: Only make template methods final
```java
// Good: Template method final, hooks overridable
public final void templateMethod() { /* ... */ }
protected boolean shouldProcess() { return true; }
```

## Testing Template Method Pattern

### 1. Testing Template Methods
```java
@Test
public void testTemplateMethodExecution() {
    TestableTemplate template = new TestableTemplate();
    Result result = template.executeTemplate();
    
    // Verify the algorithm executed correctly
    assertTrue(result.isSuccess());
    assertEquals(expectedSteps, template.getExecutedSteps());
}

// Test double for verification
class TestableTemplate extends AbstractTemplate {
    private List<String> executedSteps = new ArrayList<>();
    
    @Override
    protected void stepOne() {
        executedSteps.add("stepOne");
    }
    
    @Override
    protected void stepTwo() {
        executedSteps.add("stepTwo");
    }
    
    public List<String> getExecutedSteps() {
        return executedSteps;
    }
}
```

### 2. Testing Hook Methods
```java
@Test
public void testHookMethodBehavior() {
    // Test with hook returning true
    CustomTemplate templateWithHook = new CustomTemplate() {
        @Override
        protected boolean shouldExecuteOptionalStep() {
            return true;
        }
    };
    
    Result result1 = templateWithHook.executeTemplate();
    assertTrue(result1.hasOptionalStepExecuted());
    
    // Test with hook returning false
    CustomTemplate templateWithoutHook = new CustomTemplate() {
        @Override
        protected boolean shouldExecuteOptionalStep() {
            return false;
        }
    };
    
    Result result2 = templateWithoutHook.executeTemplate();
    assertFalse(result2.hasOptionalStepExecuted());
}
```

## Performance Considerations

### 1. Method Call Overhead
- Template methods involve multiple method calls
- Virtual method dispatch can add overhead
- Consider inlining for performance-critical sections

### 2. Memory Usage
- Inheritance hierarchy can increase memory footprint
- Be mindful of object creation in template methods
- Use object pooling for frequently created objects

### 3. Optimization Techniques
```java
public abstract class OptimizedTemplate {
    // Cache expensive computations
    private volatile ExpensiveResult cachedResult;
    
    public final void templateMethod() {
        if (shouldUseCache() && cachedResult != null) {
            processWithCache(cachedResult);
        } else {
            ExpensiveResult result = performExpensiveOperation();
            if (shouldCache(result)) {
                cachedResult = result;
            }
            processResult(result);
        }
    }
    
    protected boolean shouldUseCache() { return true; }
    protected boolean shouldCache(ExpensiveResult result) { return true; }
    protected abstract ExpensiveResult performExpensiveOperation();
    protected abstract void processResult(ExpensiveResult result);
    protected abstract void processWithCache(ExpensiveResult result);
}
```

## Conclusion

The Template Method Pattern is a powerful tool for:
- **Code Reuse**: Extracting common algorithm structure
- **Consistency**: Ensuring uniform process execution
- **Extensibility**: Allowing controlled customization
- **Maintainability**: Centralizing algorithm logic

It's particularly valuable in framework design, data processing pipelines, and any scenario where you have a well-defined process with variable steps. The key is to identify the invariant parts of your algorithm and extract them into a template method, while allowing subclasses to customize only the parts that truly need to vary.

Remember to balance flexibility with structure—provide enough hooks for customization without making the template too complex or the subclass interface too burdensome.