package service;

import dataaccess.*;
import model.UserData;
import model.AuthData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTests {

    private UserService userService;

    @BeforeEach
    void setUp() {
        MemoryUserDAO userDAO = new MemoryUserDAO();
        AuthService authService = new AuthService(new MemoryAuthDAO());
        userService = new UserService(userDAO, authService);
    }

    @Test
    void testRegister() throws DataAccessException {
        UserData user = new UserData("user1", "pass", "a@b.com");
        AuthData auth = userService.register(user);
        assertNotNull(auth);
        assertEquals("user1", auth.username());
    }

    @Test
    void testRegisterAlreadyTaken() throws DataAccessException {
        UserData user = new UserData("user1", "pass", "a@b.com");

        // first registration succeeds
        userService.register(user);

        // second registration should throw
        assertThrows(DataAccessException.class, () -> userService.register(user));
    }

    @Test
    void testLoginUnauthorized() {
        UserData user = new UserData("user1", "pass", "a@b.com");
        try {
            userService.register(user);
        } catch (DataAccessException ignored) {}

        DataAccessException exception = assertThrows(DataAccessException.class, () -> {
            userService.login("user1", "wrongpass");
        });
        assertTrue(exception.getMessage().contains("unauthorized"));
    }

    @Test
    void testClear() {
        UserData user = new UserData("user1", "pass", "a@b.com");
        try {
            userService.register(user);
        } catch (DataAccessException ignored) {}

        try {
            userService.clear();
        } catch (DataAccessException ignored) {}

        DataAccessException exception = assertThrows(DataAccessException.class, () -> {
            userService.login("user1", "pass");
        });
        // It might say unauthorized or not found, depending on DAO implementation
        assertTrue(exception.getMessage().contains("unauthorized") || exception.getMessage().contains("not found"));
    }
}
