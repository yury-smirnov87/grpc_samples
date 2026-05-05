package example.proto;

import com.example.proto2.device.Device;
import com.example.proto2.device.DeviceMetadata;
import com.example.proto2.device.DeviceType;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.google.protobuf.util.JsonFormat;

import java.util.UUID;

/**
 * Hello world!
 */
public class ProtobufExample {

    public static void main(String[] args) {
        Device device = initProtoMessage();
        System.out.println("============");

        changeProtoMessage(device);
        System.out.println("============");

        convertProtoMessagetoJson(device);
        System.out.println("============");

        checkDefaults();
    }

    /*
       Showcases how to instantiate and initialize a protobuf object from scratch.
     */
    private static Device initProtoMessage() {
        // Use a new builder object to initialize fields and crate a Device.
        Device device = Device.newBuilder()
                .setId(UUID.randomUUID().toString())
                .setVersion(1)
                .setCreatedAt(System.currentTimeMillis())
                .setUpdatedAt(System.currentTimeMillis())
                .setDeletedAt(System.currentTimeMillis())
                .setName("Sample device")
                .setSerialNumber("SN123456789")
                .setType(DeviceType.FT1)
                .setEnabled(true)
                .setDeviceMetadata(DeviceMetadata.newBuilder()
                        .setHwType("Dashcam")
                        .setHwModel("VT_230")
                        .build())
                .build();

        // The created device is immutable.
        System.out.println(device);

        return device;
    }

    /*
        Showcases how to approach changing an existing protobuf object.
     */
    private static Device changeProtoMessage(Device device) {
        Device changedDevice = device.toBuilder()
                // set a new name directly on the property
                .setName("Changed name")
                // call method clear to erase values. Cannot set a null
                .clearDeviceMetadata()
                .clearDeletedAt()
                .build();

        // the result is a new object, the original one remains as is.
        System.out.println(changedDevice);

        return changedDevice;
    }

    // Showcases how a protobuf object can be converted to JSON
    private static void convertProtoMessagetoJson(Message message) {
        try {
            String json = JsonFormat.printer().print(message);
            System.out.println(json);
        } catch (InvalidProtocolBufferException e) {
            throw new RuntimeException(e);
        }
    }

    private static void checkDefaults() {
        // A completely not initialized device
        Device device = Device.newBuilder().build();

        //Check if an empty device is equal to a default instance
        checkDefaultInstance(device);

        // Check not set String property returns as empty
        checkDefaultStringProperty(device);

        // Check not set long property returns as 0
        checkDefaultLongProperty(device);

        // Check boolean property returns as false
        checkDefaultBoolean(device);

        // Check enum property returns as 0th element (DEVICE_TYPE_UNKNOWN)
        checkDefaultEnumProperty(device);

        // Check nested message returns as a default instance
        checkDefaultNestedMessage(device);
    }

    private static void checkDefaultInstance(Device device) {
        if (device.equals(Device.getDefaultInstance())) {
            System.out.println("Device is equal to default instance");
        }
        System.out.println("============");
    }

    private static void checkDefaultStringProperty(Device device) {
        Device changed = device.toBuilder().build();
        if (changed.hasName()) {
            System.out.println("Name: set");
        } else {
            System.out.println("Name: not set");
        }

        System.out.println("Name: |" + changed.getName() + "|");
        System.out.println("============");
    }

    private static void checkDefaultLongProperty(Device device) {
        if (device.hasCreatedAt()) {
            System.out.println("Created at: set");
        } else {
            System.out.println("Created at: not set");
        }
        System.out.println(device.getCreatedAt());
        System.out.println("============");
    }

    private static void checkDefaultBoolean(Device device) {
        if (device.hasEnabled()) {
            System.out.println("Enabled: set");
        } else {
            System.out.println("Enabled: not set");
        }
        System.out.println(device.getEnabled());
        System.out.println("============");
    }

    private static void checkDefaultEnumProperty(Device device) {
        if (device.hasType()) {
            System.out.println("Type: set");
        } else {
            System.out.println("Type: not set");
        }
        System.out.println(device.getType());
        System.out.println("============");
    }

    private static void checkDefaultNestedMessage(Device device) {
        if (device.hasDeviceMetadata()) {
            System.out.println("DeviceMetadata: set");
        } else {
            System.out.println("DeviceMetadata: not set");
        }
        System.out.println("DeviceMetadata default instance: " + device.getDeviceMetadata()
                .equals(DeviceMetadata.getDefaultInstance()));
        System.out.println("============");
    }
}
