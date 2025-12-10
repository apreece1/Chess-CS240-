package dataaccess;

import model.AuthData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class SQLAuthDAOTest {

    private AuthDAO authDAO;

    @BeforeEach
    void setUp() throws DataAccessException {
        authDAO = new MySqlAuthDAO();
        authDAO.clear();
    }

    @Test
    void clearPositiveClearsAllAuths() throws DataAccessException {
        String token = UUID.randomUUID().toString();
        AuthData auth = new AuthData(token, "josh");

        authDAO.createAuth(auth);
        assertNotNull(authDAO.getAuth(token));

        authDAO.clear();
        assertNull(authDAO.getAuth(token));
    }

    @Test
    void clearNegativeOnEmptyTableNoException() {
        assertDoesNotThrow(() -> authDAO.clear());
    }

    @Test
    void createAuthPositiveCreatesUniqueToken() throws DataAccessException {
        String token1 = UUID.randomUUID().toString();
        String token2 = UUID.randomUUID().toString();

        AuthData auth1 = new AuthData(token1, "josh");
        AuthData auth2 = new AuthData(token2, "josh");

        authDAO.createAuth(auth1);
        authDAO.createAuth(auth2);

        assertNotNull(authDAO.getAuth(token1));
        assertNotNull(authDAO.getAuth(token2));
        assertNotEquals(token1, token2);
    }

    @Test
    void createAuthNegativeNullUsernameThrows() {
        String token = UUID.randomUUID().toString();
        AuthData bad = new AuthData(token, null);

        assertThrows(DataAccessException.class, () -> authDAO.createAuth(bad));
    }

    @Test
    void getAuthPositiveExistingToken() throws DataAccessException {
        String token = UUID.randomUUID().toString();
        AuthData auth = new AuthData(token, "josh");

        authDAO.createAuth(auth);

        AuthData fromDb = authDAO.getAuth(token);
        assertNotNull(fromDb);
        assertEquals("josh", fromDb.username());
    }

    @Test
    void getAuthNegativeUnknownTokenReturnsNull() throws DataAccessException {
        assertNull(authDAO.getAuth("fake-token"));
    }

    @Test
    void deleteAuthPositiveExistingToken() throws DataAccessException {
        String token = UUID.randomUUID().toString();
        AuthData auth = new AuthData(token, "josh");

        authDAO.createAuth(auth);

        authDAO.deleteAuth(token);
        assertNull(authDAO.getAuth(token));
    }

    @Test
    void deleteAuthNegativeNonExistingTokenThrows() {
        assertThrows(DataAccessException.class, () -> authDAO.deleteAuth("fake-token"));
    }
}
