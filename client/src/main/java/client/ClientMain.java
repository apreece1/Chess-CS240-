package client;

import ui.ChessClient;

public class ClientMain {
    public static void main(String[] args) {
        int port = 8081;  // must match your server port

        ServerFacade facade = new ServerFacade(port);
        ChessClient client = new ChessClient(facade);
        client.run();
    }
}
