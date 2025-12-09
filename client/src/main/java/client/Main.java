package client;

import ui.ChessClient;

public class Main {
    public static void main(String[] args) {
        int port = 8080;  // must match your server port

        ServerFacade facade = new ServerFacade(port);
        ChessClient client = new ChessClient(facade);
        client.run();
    }
}
