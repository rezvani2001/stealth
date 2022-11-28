package server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;
import java.nio.ByteBuffer;

public class Server {
    public static void start() throws IOException {
        PropertiesHolder.remoteNet = new InetSocketAddress("127.0.0.1", 9999);
        ServerSocket remote = new ServerSocket(5000);

        while (true) {
            Socket client = remote.accept();
            System.out.println("remote connected");

            DatagramSocket socket = new DatagramSocket();

            startClientListener(socket, client);
            startRemoteListener(client, socket);
        }

    }



    public static void startRemoteListener(Socket src, DatagramSocket dst) throws IOException {
        InputStream stream = src.getInputStream();

        Thread thread = new Thread(() -> {
            try {
                ByteBuffer bytes = ByteBuffer.allocate(40960);
                DatagramPacket packet = new DatagramPacket(bytes.array(), bytes.capacity());

                while (true) {
                    bytes.clear();

                    stream.read(bytes.array(), 0, stream.available());
                    packet.setData(bytes.array());
                    packet.setSocketAddress(new InetSocketAddress("127.0.0.1", PropertiesHolder.port));
                    dst.send(packet);
                }
            } catch (IOException ignored) {

            }
        });

        thread.start();
    }

    public static void startClientListener(DatagramSocket src, Socket dst) throws IOException {
        OutputStream stream = dst.getOutputStream();

        Thread thread = new Thread(() -> {
            try {
                ByteBuffer bytes = ByteBuffer.allocate(40960);
                DatagramPacket packet = new DatagramPacket(bytes.array(), bytes.capacity());

                while (true) {
                    src.receive(packet);
                    PropertiesHolder.port = packet.getPort();

                    stream.write(packet.getData());
                }
            } catch (IOException ignored) {

            }
        });

        thread.start();
    }
}


class PropertiesHolder {
    public static int port;
    public static InetSocketAddress remoteNet;
}