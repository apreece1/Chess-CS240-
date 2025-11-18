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

}
