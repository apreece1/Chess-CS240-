package mytest;

import dataaccess.AuthDAO;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.UserService;
import service.AuthService;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTests {

    private UserService userService;
    private AuthService authService;
    private UserDAO userDAO;

    @BeforeEach
    void setup() {
        userDAO = new UserDAO();
        authDAO = new AuthDAO();
        authService = new AuthService(new AuthDAO());
        userService = new UserService(userDAO, authService);
    }

    @Test
    void testClearDeleteUsersAndTokens() throws DataAccessException {

        userService.register(new UserData("testuser", "password", "test@mail,com"));

        assertNotNull(userService.login("testuser", "password"));

        userService.clear();

        DataAccessException e = assertThrows(DataAccessException.class, () ->
                userService.login("testuser", "password")
        );

        assertTrue(e.getMessage().contains("Error"));
    }

}
