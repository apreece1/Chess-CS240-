package dataaccess;

import model.AuthData;

import java.sql.*;


public class MySqlAuthDAO implements AuthDAO {

    @Override
    public void createAuth(AuthData auth) throws DataAccessException {
        String sql = "INSERT INTO authToken (authToken, username) VALUES (?, ?)";
        try (var conn = DatabaseManager.getConnection();
             var stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, auth.authToken());
            stmt.setString(2, auth.username());
            stmt.executeUpdate();

        } catch (SQLException ex) {
            throw new DataAccessException("Failed to create auth token", ex);
        }
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        String sql = "SELECT authToken, username FROM authToken WHERE authToken = ?";
        try (var conn = DatabaseManager.getConnection();

             var stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, authToken);
            var rs = stmt.executeQuery();

            if (!rs.next()) {
                return null;
            }

            return new AuthData(
                    rs.getString("authToken"),
                    rs.getString("username"));

        } catch (SQLException ex) {
            throw new DataAccessException("Failed to get auth token", ex);
        }
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        String sql = "DELETE FROM authToken WHERE authToken = ?";
        try (var conn = DatabaseManager.getConnection();
             var stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, authToken);
            int rows = stmt.executeUpdate();

            if (rows == 0) {
                throw new DataAccessException("unauthorized");
            }


        } catch (SQLException ex) {
            throw new DataAccessException("Failed to delete auth token", ex);
        }
    }


    @Override
    public void clear() throws DataAccessException {
        String sql = "DELETE FROM authToken";
        try (var conn = DatabaseManager.getConnection();
             var stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();

        } catch (SQLException ex) {
            throw new DataAccessException("Failed to clear auth tokens", ex);
        }
    }
}

