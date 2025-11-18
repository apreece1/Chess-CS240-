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


}
