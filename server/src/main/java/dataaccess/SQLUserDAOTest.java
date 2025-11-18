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

    @Test
    void createUser_positive_insertsUser() throws DataAccessException {
        var user = new UserData("josh", "password", "josh@example.com");

        userDAO.createUser(user);
        var fromDb = userDAO.getUser("josh");

        assertNotNull(fromDb);
        assertEquals("josh", fromDb.username());
        assertEquals("password", fromDb.password());
        assertEquals("josh@example.com", fromDb.email());
    }

    @Test
    void createUser_negative_duplicateUsername_throws() throws DataAccessException {
        var user1 = new UserData("josh", "pw1", "one@example.com");
        var user2 = new UserData("josh", "pw2", "two@example.com");

        userDAO.createUser(user1);

        assertThrows(DataAccessException.class, () -> userDAO.createUser(user2),
                "Expected duplicate username to throw DataAccessException");
    }

    @Test
    void getUser_positive_existingUser() throws DataAccessException {
        var user = new UserData("josh", "password", "josh@example.com");
        userDAO.createUser(user);

        var fromDb = userDAO.getUser("josh");

        assertNotNull(fromDb);
        assertEquals("josh", fromDb.username());
    }

    @Test
    void getUser_negative_nonExistingUser_returnsNull() throws DataAccessException {
        var fromDb = userDAO.getUser("noSuchUser");
        assertNull(fromDb, "Expected null for non-existing user");
    }
}

