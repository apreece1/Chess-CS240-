package dataaccess;

import dataaccess.dataaccess.DatabaseManager;
import model.AuthData;

import java.sql.*;


public class MySqlAuthDAO {

    @Override
    public void createAuth(AuthData auth) throws DataAccessException {
        String sql = "INSERT INTO AuthToken (authToken, username) VALUES (?, ?)";
        try (var conn = DatabaseManager.getConnection();
             var stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, auth.authToken());
            stmt.setString(2, auth.username());
            stmt.executeUpdate();
        } catch (SQLException ex) {
            throw new DataAccessException("Failed to create auth token", ex);
        }
    }


}

