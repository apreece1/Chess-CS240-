package client;

import model.AuthData;
import model.GameData;
import org.junit.jupiter.api.*;

import server.Server;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MyServerFacadeTests {
    private static Server server;
    private static ServerFacade facade;

    @BeforeAll
    public static void startServer() {
        server = new Server();
        int port = server.run(0);
        facade = new ServerFacade(port);
    }

    @AfterAll
    public static void stopServer() {
        if (server != null) {
            server.stop();
        }
    }
}