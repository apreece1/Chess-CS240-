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

    @BeforeEach
    public void clearDatabase() throws Exception {
        facade.clear();
    }

    @Test
    public void getPortPositive() {
        int port = 1234;
        ServerFacade f = new ServerFacade(port);
        assertEquals(port, f.getPort());
    }

    @Test
    public void getPortNegative() {
        int port = 1234;
        ServerFacade f = new ServerFacade(port);
        assertNotEquals(9999, f.getPort());
    }

    @Test
    public void registerPositive() throws Exception {
        AuthData auth = facade.register("user1", "password", "user1@example.com");
        assertNotNull(auth);
        assertEquals("user1", auth.username());
        assertNotNull(auth.authToken());
    }

    @Test
    public void registerNegativeDuplicateUsername() throws Exception {
        facade.register("user1", "password", "user1@example.com");
        Exception ex = assertThrows(Exception.class, () ->
                facade.register("user1", "password2", "other@example.com"));
        assertTrue(ex.getMessage().contains("HTTP"));
    }

    @Test
    public void loginPositive() throws Exception {
        facade.register("user2", "password", "user2@example.com");
        AuthData auth = facade.login("user2", "password");
        assertNotNull(auth);
        assertEquals("user2", auth.username());
    }

    @Test
    public void loginNegativeBadPassword() throws Exception {
        facade.register("user3", "password", "user3@example.com");
        Exception ex = assertThrows(Exception.class, () ->
                facade.login("user3", "wrongpassword"));
        assertTrue(ex.getMessage().contains("HTTP"));
    }

    @Test
    public void logoutPositive() throws Exception {
        AuthData auth = facade.register("user4", "password", "user4@example.com");
        assertDoesNotThrow(() -> facade.logout(auth.authToken()));
    }

    @Test
    public void logoutNegativeInvalidToken() {
        Exception ex = assertThrows(Exception.class, () ->
                facade.logout("bad-token"));
        assertTrue(ex.getMessage().contains("HTTP"));
    }

    @Test
    public void createGamePositive() throws Exception {
        AuthData auth = facade.register("user5", "password", "user5@example.com");
        int gameID = facade.createGame(auth.authToken(), "My Game");
        assertTrue(gameID > 0);
    }

    @Test
    public void createGameNegativeInvalidToken() {
        Exception ex = assertThrows(Exception.class, () ->
                facade.createGame("bad-token", "Game"));
        assertTrue(ex.getMessage().contains("HTTP"));
    }

    @Test
    public void joinGamePositive() throws Exception {
        AuthData auth = facade.register("user6", "password", "user6@example.com");
        int gameID = facade.createGame(auth.authToken(), "Joinable");
        assertDoesNotThrow(() ->
                facade.joinGame(auth.authToken(), gameID, "WHITE"));
    }

    @Test
    public void joinGameNegativeBadGameId() throws Exception {
        AuthData auth = facade.register("user7", "password", "user7@example.com");
        Exception ex = assertThrows(Exception.class, () ->
                facade.joinGame(auth.authToken(), 999999, "WHITE"));
        assertTrue(ex.getMessage().contains("HTTP"));
    }

    @Test
    public void clearPositive() throws Exception {
        AuthData auth = facade.register("user8", "password", "user8@example.com");
        facade.createGame(auth.authToken(), "Game");
        facade.clear();

        Exception ex = assertThrows(Exception.class, () ->
                facade.listGames(auth.authToken()));
        assertTrue(ex.getMessage().contains("HTTP"));
    }
    @Test
    public void clearNegativeOldTokenStillInvalid() throws Exception {
        AuthData auth = facade.register("user9", "password", "user9@example.com");
        facade.clear();

        Exception ex = assertThrows(Exception.class, () ->
                facade.logout(auth.authToken()));
        assertTrue(ex.getMessage().contains("HTTP"));
    }

    @Test
    public void listGamesPositive() throws Exception {
        AuthData auth = facade.register("user10", "password", "user10@example.com");
        int gameID = facade.createGame(auth.authToken(), "List Game");

        List<GameData> games = facade.listGames(auth.authToken());
        assertFalse(games.isEmpty());
        assertTrue(games.stream().anyMatch(g -> g.getGameID() == gameID));
    }

    @Test
    public void listGamesNegativeInvalidToken() {
        Exception ex = assertThrows(Exception.class, () ->
                facade.listGames("invalid-token"));
        assertTrue(ex.getMessage().contains("HTTP"));
    }
}
