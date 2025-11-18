package dataaccess;

import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


public class SQLUserDAOTest {

    private UserDAO userDAO;

    @BeforeEach
    void setUp() throws DataAccessException {
        // however you construct it in your code
        userDAO = new MySqlUserDAO();
        userDAO.clear();
    }

    @Test
    void clear_positive_clearsAllUsers() throws DataAccessException {
        var user = new UserData("josh", "password", "josh@example.com");
        userDAO.createUser(user);

        assertNotNull(userDAO.getUser("josh"));

        userDAO.clear();

        var fromDb = userDAO.getUser("josh");
        assertNull(fromDb, "Expected user to be removed after clear()");
    }

    @Test
    void clear_negative_onEmptyTable_noException() {
        assertDoesNotThrow(() -> userDAO.clear());
    }


}
