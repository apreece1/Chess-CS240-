package dataaccess;


import model.UserData;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.mindrot.jbcrypt.BCrypt;

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

        String hashedPassword = BCrypt.hashpw(user.password(), BCrypt.gensalt());

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.username());
            stmt.setString(2, hashedPassword);  // later: hash with BCrypt
            stmt.setString(3, user.email());

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new DataAccessException("Unable to insert user: " + e.getMessage());
        }
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        String sql = "SELECT username, password, email FROM user WHERE username = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (!rs.next()) {
                throw new DataAccessException("User not found");
            }

            return new UserData(
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("email")
            );

        } catch (SQLException e) {
            throw new DataAccessException("Unable to read user: " + e.getMessage());
        }

    }

    @Override
    public void clear() throws DataAccessException {
        String sql = "DELETE FROM user";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new DataAccessException("Unable to clear user table: " + e.getMessage());
        }
    }

    public boolean verifyLogin(String username, String password) throws DataAccessException {
        try {
            UserData user = getUser(username); // fetch user from DB
            return BCrypt.checkpw(password, user.password()); // compare input to hash
        } catch (DataAccessException e) {
            // user not found or DB error
            return false;
        }
    }


}