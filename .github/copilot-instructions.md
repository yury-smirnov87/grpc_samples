# AI Instructions for gRPC Samples Repository

This document provides guidance for AI assistants (like GitHub Copilot) working with this repository. Follow these instructions to maintain consistency, quality, and correctness.

## Repository Purpose & Context

- **Purpose**: Educational repository teaching Protobuf and gRPC fundamentals to developers new to these technologies
- **Technology Stack**: Java, Protocol Buffers v2, gRPC, Maven
- **Key Focus**: Making counterintuitive behaviors of Protobuf explicit and understandable

## Project Structure Understanding

```
src/main/resources/proto2/
├── Device.proto              # Core message definitions
└── DeviceService.proto       # gRPC service definition

src/main/java/example/proto/
├── ProtobufExample.java      # Educational example: message creation, modification, serialization
├── GrpcServer.java           # Server implementation (port 8080)
└── GrpcClientExample.java    # Client implementation with blocking and async stubs

target/
├── classes/                  # Compiled Java classes
└── generated-sources/        # Auto-generated Protobuf code
    ├── protobuf/grpc-java/   # Generated gRPC stubs
    └── java/                 # Generated Protobuf message classes
```

## Code Generation Flow

1. **Proto files** (`Device.proto`, `DeviceService.proto`) are the source of truth
2. **Maven compilation** generates Java classes in `target/generated-sources/`
3. **Main source code** (`ProtobufExample.java`, `GrpcServer.java`, etc.) uses the generated classes

**Important**: Never manually edit generated classes. Always modify the `.proto` files instead.

## Key Technical Patterns

### Protobuf Message Creation & Modification

```java
// Create: Use builder pattern
Device device = Device.newBuilder()
    .setId("123")
    .setName("Device Name")
    .build();

// Modify: Create new instance via toBuilder()
Device modified = device.toBuilder()
    .setName("New Name")
    .build();

// Check field existence: Use has* methods
if (device.hasName()) { /* field was set */ }
```

### gRPC Service Implementation

```java
// Server: Extend DeviceServiceGrpc.DeviceServiceImplBase
DeviceServiceGrpc.DeviceServiceImplBase service = new DeviceServiceGrpc.DeviceServiceImplBase() {
    @Override
    public void getDevices(GetDevicesRequest request, StreamObserver<DeviceServiceResponse> observer) {
        // Build response
        // Call observer.onNext(response)
        // Call observer.onCompleted()
    }
};
```

### gRPC Client Usage

```java
// Blocking stub (synchronous)
DeviceServiceGrpc.DeviceServiceBlockingStub stub = DeviceServiceGrpc.newBlockingStub(channel);
DeviceServiceResponse response = stub.getDevices(request);

// Future stub (asynchronous)
DeviceServiceGrpc.DeviceServiceFutureStub stub = DeviceServiceGrpc.newFutureStub(channel);
ListenableFuture<DeviceServiceResponse> future = stub.getDevices(request);
```

## Counterintuitive Behaviors to Remember

### 1. No Null Values in Protobuf
- Optional fields return default values, never null
- Strings return `""`, numbers return `0`, booleans return `false`
- **Solution**: Use `has*` methods to check if field was explicitly set

### 2. Immutability
- Protobuf messages cannot be modified after creation
- Always use `message.toBuilder()` to create modified copies
- Original message remains unchanged

### 3. Enum Zero Values Required
- Every enum must have a value of 0 as default
- Check `Device.proto` for the pattern: `DEVICE_TYPE_UNKNOWN = 0`

### 4. Nested Messages Default to Empty Instances
- Getting a nested message that wasn't set returns an empty instance, not null
- Call `has*` methods or use equality checks: `message.equals(Message.getDefaultInstance())`

### 5. Field Numbers Are Permanent
- Field numbers (not names) are encoded in the binary format
- Changing field numbers breaks compatibility
- Use `reserved` keyword to permanently reserve field numbers

## Before Making Changes

### Proto File Modifications
- **Never reuse or delete field numbers** without using `reserved`
- **Always increment field numbers** for new fields
- **Test backward compatibility** - ensure existing clients can still deserialize new messages
- **Follow naming conventions**: snake_case for field names, UPPER_CASE for enum values

### Java Code Modifications
- **Keep examples educational**: Comments explaining "why" are valued
- **Highlight counterintuitive behaviors**: Use the patterns shown in `ProtobufExample.java`
- **Error handling**: Use appropriate gRPC error codes (`NOT_FOUND`, `INTERNAL`, etc.)
- **Documentation**: Explain generated vs. manual code clearly

## Testing & Validation

### Build & Compile
```bash
mvn clean compile
```

### Generate Code
Code generation happens automatically during `mvn compile`. Generated code appears in:
- `target/generated-sources/protobuf/java/com/example/proto2/device/`
- `target/generated-sources/protobuf/grpc-java/com/example/proto2/device/service/`

### Run Examples
```bash
# Terminal 1: Start server
mvn compile exec:java -Dexec.mainClass="example.proto.GrpcServer"

# Terminal 2: Run client
mvn compile exec:java -Dexec.mainClass="example.proto.GrpcClientExample"

# Single terminal: Run Protobuf exploration
mvn compile exec:java -Dexec.mainClass="example.proto.ProtobufExample"
```

## Import Statements to Know

### Protobuf/gRPC Generated Code
```java
import com.example.proto2.device.*;              // Generated message classes
import com.example.proto2.device.service.*;      // Generated service stubs
import com.google.protobuf.*;                    // Protobuf utilities
```

### Common Dependencies
```java
import io.grpc.*;                                 // gRPC core (ServerBuilder, ManagedChannel, etc.)
import io.grpc.netty.shaded.io.grpc.netty.*;    // NettyChannelBuilder for client connections
import com.google.common.util.concurrent.*;      // ListenableFuture for async operations
```

## Documentation Standards

- **Method comments**: Explain what the method demonstrates about Protobuf/gRPC behavior
- **Code comments**: Highlight counterintuitive aspects (no null returns, immutability, etc.)
- **README updates**: Keep aligned with actual code examples
- **Examples**: Show both correct usage and common mistakes (with explanations)

## Common Mistakes to Avoid

1. ❌ **Directly setting fields on a Protobuf message**
   - ✅ Always use `message.toBuilder().setField(...).build()`

2. ❌ **Checking `if (message.getField() == null)`**
   - ✅ Use `if (message.hasField())` for optional fields

3. ❌ **Reusing field numbers in proto definitions**
   - ✅ Mark old ones as `reserved`, use new numbers for new fields

4. ❌ **Manually editing generated code**
   - ✅ Modify `.proto` files and regenerate

5. ❌ **Forgetting zero value for enums**
   - ✅ Always include `FIELD_TYPE_UNKNOWN = 0` in enum definitions

6. ❌ **Not handling gRPC StatusRuntimeException in client code**
   - ✅ Wrap calls in try-catch and handle specific error codes

## When Adding New Examples

1. **Create new `.proto` definitions** if exploring new concepts
2. **Generate code** with `mvn compile`
3. **Write educational Java code** that highlights learning points
4. **Add to README** - update "Running the Examples" section
5. **Document counterintuitive behaviors** - this is the repo's main value
6. **Test thoroughly** - both code generation and runtime execution

## Maintaining Educational Value

- This repo is for **learning**, not production use
- Prioritize **clarity and explanation** over code minimalism
- **Show mistakes and solutions** - compare right vs. wrong approaches
- **Explain the why**, not just the how
- **Link to official docs** when explaining concepts

## File Modification Guidelines

### Proto Files (`Device.proto`, `DeviceService.proto`)
- Comment new fields with their purpose
- Use `reserved` for removed fields
- Maintain documentation comments above messages and fields

### Java Example Files
- Add educational comments explaining Protobuf/gRPC behavior
- Use descriptive variable names
- Structure code to be readable and teachable
- Include error handling examples

### README.md
- Keep "Counterintuitive Behaviors" section current
- Update examples to match actual code
- Add new sections for new learning concepts
- Maintain beginner-friendly language

## Dependencies & Versions

Current stack (from pom.xml context):
- **gRPC**: Typically 1.50+
- **Protobuf**: 3.x (even though using proto2 syntax)
- **Java**: 11+
- **Maven**: 3.6+

When suggesting dependency updates:
- Maintain backward compatibility with examples
- Test all examples after updates
- Document breaking changes

---

**Last Updated**: May 5, 2026
**Target Audience**: AI Assistants helping developers learn Protobuf/gRPC fundamentals

