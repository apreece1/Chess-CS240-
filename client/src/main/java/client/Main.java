package client;

import server.Server;     // ← THIS must match your project
import ui.ChessClient;

public class Main {
    public static void main(String[] args) {
        Server server = new Server();
        int port = server.run(8080);
        System.out.println("Server running on port " + port);

        ServerFacade facade = new ServerFacade(port);
        ChessClient client = new ChessClient(facade);
        client.run();

        server.stop();
    }
}
