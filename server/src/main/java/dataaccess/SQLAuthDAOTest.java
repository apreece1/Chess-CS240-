package dataaccess;

import model.AuthData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


public class SQLAuthDAOTest {

    private AuthDAO authDAO;

    @BeforeEach
    void setUp() throws DataAccessException {
        authDAO = new MySqlAuthDAO();
        authDAO.clear();
    }

    @Test
    void clear_positive_clearsAllAuths() throws DataAccessException {
        AuthData auth = authDAO.createAuth("josh");
        assertNotNull(authDAO.getAuth(auth.authToken()));

        authDAO.clear();

        assertNull(authDAO.getAuth(auth.authToken()));
    }

    @Test
    void clear_negative_onEmptyTable_noException() {
        assertDoesNotThrow(() -> authDAO.clear());
    }


    @Test
    void createAuth_positive_createsUniqueToken() throws DataAccessException {
        AuthData auth1 = authDAO.createAuth("josh");
        AuthData auth2 = authDAO.createAuth("josh");

        assertNotNull(auth1);
        assertNotNull(auth2);
        assertNotEquals(auth1.authToken(), auth2.authToken());
    }

    @Test
    void createAuth_negative_nullUsername_throws() {
        assertThrows(DataAccessException.class, () -> authDAO.createAuth(null));
    }

    

}
