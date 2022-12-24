package client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Scanner;

public class Client {
    public static void start() throws IOException {
        System.out.println("client mode");

        DatagramSocket socket = new DatagramSocket(9999);
        Socket remote = new Socket("127.0.0.1", 5000);

        System.out.println("remote connected");

        startClientListener(socket, remote);
        startRemoteListener(remote, socket);
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
                    int len = stream.available();
                    bytes.put(b);

                    stream.read(bytes.array(), 1, len);

                    System.out.println("client received");
                    System.out.println("send to: " + PropertiesHolder.port);

                    packet.setData(bytes.array(), 0, len);
                    packet.setSocketAddress(new InetSocketAddress("127.0.0.1", PropertiesHolder.port));
                    dst.send(packet);
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
//                Scanner scanner = new Scanner(System.in);
                while (true) {
                    bytes.clear();


                    src.receive(packet);
                    System.out.println("received" + packet.getLength());
                    PropertiesHolder.port = packet.getPort();

                    stream.write(Arrays.copyOf(packet.getData(), packet.getLength()));
                    stream.flush();
//                    stream.write(scanner.nextLine().getBytes());
//                    stream.flush();
                }

            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        });

        thread.start();
    }
}


