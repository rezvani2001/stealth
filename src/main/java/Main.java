import client.Client;
import server.Server;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        if (args.length > 0 && args[0].equals("server")) {
            Server.start();
        } else
            Client.start();
    }
}
