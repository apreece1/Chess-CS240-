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

    private void configureDatabase() throws DataAccessException {
        String createTable = """
            CREATE TABLE IF NOT EXISTS user (
                username VARCHAR(255) NOT NULL,
                password VARCHAR(255) NOT NULL,
                email VARCHAR(255),
                PRIMARY KEY (username)
            );
        """;

        try (var conn = DatabaseManager.getConnection();
             var stmt = conn.prepareStatement(createTable)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Unable to create user table: " + e.getMessage());
        }
    }

    @Override
    public void createUser(UserData user) throws DataAccessException {
        String sql = "INSERT INTO user (username, password, email) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.username());
            stmt.setString(2, user.password());  // later: hash with BCrypt
            stmt.setString(3, user.email());

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new DataAccessException("Unable to insert user: " + e.getMessage());
        }
    }

    
}