package client;

import org.junit.jupiter.api.*;
import server.Server;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade(port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    public void clearDB() throws Exception {
        facade.clear();
    }


    @Test
    void registerPositive() throws Exception {
        var auth = facade.register("player1", "password", "p1@email.com");
        assertNotNull(auth);
        assertNotNull(auth.authToken());
        assertTrue(auth.authToken().length() > 5);
    }


    @Test
    void loginPositive() throws Exception {
        facade.register("player1", "password", "p1@email.com");
        var auth = facade.login("player1", "password");
        assertNotNull(auth);
        assertNotNull(auth.authToken());
    }

    @Test
    void loginNegativeWrongPassword() throws Exception {
        facade.register("player1", "password", "p1@email.com");
        assertThrows(Exception.class, () ->
                facade.login("player1", "wrong"));
    }

    @Test
    void logoutPositive() throws Exception {
        var auth = facade.register("player1", "password", "p1@email.com");
        assertDoesNotThrow(() -> facade.logout(auth.authToken()));
    }

    @Test
    void logoutNegativeBadToken() {
        assertThrows(Exception.class, () -> facade.logout("bad-token"));
    }

    @Test
    void createGamePositive() throws Exception {
        var auth = facade.register("player1", "password", "p1@email.com");
        int gameId = facade.createGame(auth.authToken(), "TestGame");
        assertTrue(gameId > 0);
    }





}
