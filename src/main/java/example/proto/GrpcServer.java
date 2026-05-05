package example.proto;

import com.example.proto2.device.Device;
import com.example.proto2.device.DeviceType;
import com.example.proto2.device.service.DeviceServiceGrpc;
import com.example.proto2.device.service.DeviceServiceResponse;
import com.example.proto2.device.service.GetDevicesRequest;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.io.IOException;
import java.util.UUID;

public class GrpcServer {

    public static void main(String[] args) throws IOException, InterruptedException {
        DeviceServiceGrpc.DeviceServiceImplBase service = new DeviceServiceGrpc.DeviceServiceImplBase() {
            @Override
            public void getDevices(GetDevicesRequest request, StreamObserver<DeviceServiceResponse> responseObserver) {
                DeviceServiceResponse response = DeviceServiceResponse.newBuilder()
                        .addDevices(Device.newBuilder()
                                .setId(UUID.randomUUID().toString())
                                .setVersion(1)
                                .setName("Mock device")
                                .setSerialNumber("SN987654321")
                                .setType(DeviceType.MGS)
                                .build())
                        .build();
                responseObserver.onNext(response);
                responseObserver.onCompleted();
            }
        };

        // Start the server on port 8080
        Server server = ServerBuilder.forPort(8080)
                .addService(service)
                .build();

        server.start();
        System.out.println("Server started on port " + server.getPort());

        // Keep the server running until the JVM is shut down
        server.awaitTermination();

        service.bindService();
    }
}
