package client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;

public class Main {
    public static void main(String[] args) throws IOException {
        PropertiesHolder.remoteNet = new InetSocketAddress("141.11.184.161", 9999);
        DatagramSocket socket = new DatagramSocket(5000);
        Socket remote = new Socket("141.11.184.161", 5000);
        System.out.println("remote connected");
        startClientListener(socket, remote);
        startRemoteListener(remote, socket);
    }

    public static void startRemoteListener(Socket src, DatagramSocket dst) throws IOException {
        src.connect(PropertiesHolder.remoteNet);
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