package mytest;

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
        userDAO = new userDAO();
        authService = new AuthService(new AuthDOA());
        userService = new UserService(userDAO, authService);
    }

    @Test
    void testClearDeleteUsersAndTokens() throws DataAccessException {

        userService.register(new UserData("testuser", "password", "test@mail,com"));


    }

}
