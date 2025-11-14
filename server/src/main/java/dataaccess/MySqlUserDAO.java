package dataaccess;


import model.UserData;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MySqlUserDAO implements UserDAO{

    public MySqlUserDAO() throws DataAccessException {
        configureDatabase();
    }

    

}
