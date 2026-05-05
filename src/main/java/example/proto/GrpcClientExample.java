package example.proto;

import com.example.proto2.device.service.DeviceServiceGrpc;
import com.example.proto2.device.service.DeviceServiceResponse;
import com.example.proto2.device.service.GetDevicesRequest;
import com.google.common.util.concurrent.ListenableFuture;
import io.grpc.ManagedChannel;
import io.grpc.StatusRuntimeException;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;


import java.util.UUID;
import java.util.concurrent.Executors;

public class GrpcClientExample {

    public static void main(String[] args) {
        ManagedChannel channel = NettyChannelBuilder.forAddress("localhost", 8080)
                .usePlaintext()
                .build();

        blockingStub(channel);
//        futureStub(channel);
    }

    private static void blockingStub(ManagedChannel channel) {
        DeviceServiceGrpc.DeviceServiceBlockingStub stub = DeviceServiceGrpc.newBlockingStub(channel);
        try {
            GetDevicesRequest request = GetDevicesRequest.newBuilder().addDeviceIds(UUID.randomUUID().toString()).build();

            DeviceServiceResponse deviceResponse = stub.getDevices(request);

            if (deviceResponse.getDevicesCount() != 0) {
                deviceResponse.getDevicesList().forEach(System.out::println);
            }

        } catch (StatusRuntimeException e) {
            switch (e.getStatus().getCode()) {
                case NOT_FOUND -> System.out.println("Device not found");
                case INTERNAL -> System.out.println("Internal server error");

                default -> System.out.println("Unknown error: " + e.getStatus().getCode());
            }
        }
    }

    private static void futureStub(ManagedChannel channel) {
        DeviceServiceGrpc.DeviceServiceFutureStub stub = DeviceServiceGrpc.newFutureStub(channel);

        GetDevicesRequest request = GetDevicesRequest.newBuilder().addDeviceIds(UUID.randomUUID().toString()).build();

        ListenableFuture<DeviceServiceResponse> futureResponse = stub.getDevices(request);

        futureResponse.addListener(() -> {
            try {
                DeviceServiceResponse response = futureResponse.get();
                if (response.getDevicesCount() != 0) {
                    response.getDevicesList().forEach(System.out::println);
                }
            } catch (Exception e) {
                System.out.println("Error retrieving response: " + e.getMessage());
            }
        }, Executors.newSingleThreadExecutor());

        try {
            Thread.sleep(2000); // Wait for the async response to be printed
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
