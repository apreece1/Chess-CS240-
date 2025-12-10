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


}