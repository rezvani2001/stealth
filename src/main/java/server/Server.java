package server;


import client.PropertiesHolder;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class Server {
    public static void start() throws IOException {
        System.out.println("server mode");
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

                    byte b = (byte) stream.read();
                    if (b != 0) {
                        int len = stream.available();
                        bytes.put(b);

                        stream.read(bytes.array(), 1, len);

                        System.out.println("server received"+ len);
                        packet.setData(bytes.array(), 0, len);
                        packet.setSocketAddress(new InetSocketAddress("127.0.0.1", 9999));
                        dst.send(packet);
                    }
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
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
                    bytes.clear();
                    src.receive(packet);
                    System.out.println("received");
                    PropertiesHolder.port = packet.getPort();

                    stream.write(Arrays.copyOf(packet.getData(), packet.getLength()));
                    stream.flush();
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        });

        thread.start();
    }
}