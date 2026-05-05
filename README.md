# gRPC & Protobuf Samples

Welcome! This repository contains practical examples of **Protobuf** and **gRPC** in Java. This guide is designed for developers encountering these technologies for the first time.

## What is Protocol Buffers (Protobuf)?

**Protocol Buffers** is a method of serializing structured data developed by Google. Think of it as a more efficient and language-agnostic alternative to JSON or XML.

### Key Concepts:

- **Schema Definition**: You define your data structure in `.proto` files using a simple language
- **Code Generation**: From these definitions, tools generate classes in your language (Java, Python, Go, etc.)
- **Efficient Serialization**: Data is serialized into a compact binary format, making it faster and smaller than JSON/XML
- **Version Compatibility**: You can evolve your schema over time while maintaining backward compatibility

### Example from this repo:

```protobuf
message Device {
  optional string id = 1;
  optional int64 version = 2;
  optional string name = 12;
  optional bool enabled = 13;
  optional DeviceMetadata device_metadata = 14;
}
```

## What is gRPC?

**gRPC** (gRPC Remote Procedure Call) is a high-performance framework for building distributed systems. It uses HTTP/2 for transport and Protobuf for serialization.

### Key Concepts:

- **Services Definition**: Define RPCs in `.proto` files
- **Multiple Communication Patterns**: 
  - Unary: Client sends one request, server sends one response
  - Server Streaming: Client sends request, server streams multiple responses
  - Client Streaming: Client streams requests, server sends one response
  - Bidirectional Streaming: Both sides stream messages
- **Automatic Client/Server Generation**: gRPC generates stubs for making calls and base classes for services

### Example from this repo:

```protobuf
service DeviceService {
  rpc getDevices(GetDevicesRequest) returns (DeviceServiceResponse);
}
```

This generates:
- **Client stubs** to call the service (blocking, async, etc.)
- **Server base class** to implement the service

## Counterintuitive Behaviors (⚠️ Important!)

When working with Protobuf, you'll encounter some behaviors that differ from typical Java objects:

### 1. **Optional Fields Don't Return `null`**

In Protobuf, optional fields never return `null`. Instead, they return **default values**:

```java
Device device = Device.newBuilder().build(); // Create empty device

device.getName();        // Returns "" (empty string), NOT null
device.getVersion();     // Returns 0L (zero), NOT null
device.getEnabled();     // Returns false, NOT null
```

**Why?** Protobuf can't represent null values in binary format. Default values are baked into the serialization.

**Workaround**: Use the `has*` methods to check if a field was explicitly set:

```java
if (device.hasName()) {
    System.out.println("Name was explicitly set: " + device.getName());
} else {
    System.out.println("Name was not set, getting default");
}
```

### 2. **Protobuf Objects are Immutable**

Once created, you cannot modify a Protobuf message:

```java
Device device = Device.newBuilder().setName("Original").build();
device.setName("Modified"); // ❌ Compilation Error!
```

**To modify**: Use the builder pattern and create a new object:

```java
Device modified = device.toBuilder()
    .setName("Changed name")
    .clearDeviceMetadata()
    .build();

// Original device is unchanged
System.out.println(device.getName());        // "Original"
System.out.println(modified.getName());      // "Changed name"
```

### 3. **Enums Must Have a Zero Value**

In Protobuf, every enum must have a value of 0 (the default):

```protobuf
enum DeviceType {
  DEVICE_TYPE_UNKNOWN = 0;  // ✓ Must exist for default values
  DASHLINK = 1;
  FT1 = 2;
}
```

When you create an empty message, the enum defaults to the 0th element:

```java
Device device = Device.newBuilder().build();
device.getType(); // Returns DEVICE_TYPE_UNKNOWN (value 0)
```

### 4. **Nested Messages Default to Empty Instances (Not Null)**

```java
Device device = Device.newBuilder().build();

DeviceMetadata metadata = device.getDeviceMetadata();
// metadata is NOT null, it's a default DeviceMetadata instance

metadata.equals(DeviceMetadata.getDefaultInstance()); // true
```

### 5. **Field Numbers Matter More Than Names**

When you add/remove fields, use field numbers strategically:

```protobuf
message Device {
  optional string id = 1;
  optional int64 version = 2;
  // ... other fields ...
  
  reserved 6 to 9;  // These numbers are reserved and can't be reused
}
```

The **field number** is what's encoded in the binary format. If you change field names, clients can still decode old data. But woe if you reuse or remove field numbers carelessly!

## Project Structure

```
src/main/resources/proto2/
├── Device.proto              # Message definitions
└── DeviceService.proto       # Service definitions

src/main/java/example/proto/
├── ProtobufExample.java      # Demonstrates: creating, modifying, serializing Protobuf messages
├── GrpcServer.java           # Implements the DeviceService (server-side)
└── GrpcClientExample.java    # Calls the DeviceService (client-side)
```

## Running the Examples

### Start the server:
```bash
mvn compile exec:java -Dexec.mainClass="example.proto.GrpcServer"
```

### In another terminal, run the client:
```bash
mvn compile exec:java -Dexec.mainClass="example.proto.GrpcClientExample"
```

### Explore Protobuf behaviors:
```bash
mvn compile exec:java -Dexec.mainClass="example.proto.ProtobufExample"
```

## Key Takeaways

1. **Protobuf ≠ JSON**: No null values, immutable objects, efficient binary format
2. **gRPC ≠ REST**: HTTP/2, streaming support, strongly typed, auto-generated stubs
3. **Use `has*` methods**: Check if fields were explicitly set
4. **Embrace the builder pattern**: Create and modify messages using builders
5. **Design field numbers carefully**: They're permanent in your schema
6. **Read the generated code**: Understanding what gRPC generates helps you use it effectively

## Further Reading

- [Protocol Buffers Documentation](https://developers.google.com/protocol-buffers)
- [gRPC Java Guide](https://grpc.io/docs/languages/java/)
- [gRPC Concepts](https://grpc.io/docs/what-is-grpc/core-concepts/)

---

**Happy protobuffering!** 🚀
