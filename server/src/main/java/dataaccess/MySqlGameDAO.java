package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.GameData;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;

public class MySqlGameDAO implements GameDAO {

    private final Gson gson;

    public MySqlGameDAO() {
        this.gson = new GsonBuilder().create();
    }

    @Override
    public int createGame(GameData game) throws DataAccessException {
        String sql = "INSERT INTO game (gameName, whiteUsername, blackUsername, chessGame) VALUES (?, ?, ?, ?)";
        try (var conn = DatabaseManager.getConnection();
             var stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, game.getGameName());
            stmt.setString(2, game.getWhiteUsername());
            stmt.setString(3, game.getBlackUsername());
            stmt.setString(4, gson.toJson(game.getGame())); // serialize ChessGame to JSON
            stmt.executeUpdate();

            var rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            } else {
                throw new DataAccessException("Failed to retrieve generated gameID");
            }

        } catch (SQLException ex) {
            throw new DataAccessException("Failed to create game", ex);
        }

    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        String sql = "SELECT gameID, gameName, whiteUsername, blackUsername, chessGame FROM game WHERE gameID = ?";
        try (var conn = DatabaseManager.getConnection();
             var stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, gameID);
            var rs = stmt.executeQuery();

            if (!rs.next()) {
                return null;
            }

            ChessGame chessGame = gson.fromJson(rs.getString("chessGame"), ChessGame.class);

            return new GameData(
                    rs.getInt("gameID"),
                    rs.getString("gameName"),
                    rs.getString("whiteUsername"),
                    rs.getString("blackUsername"),
                    chessGame
            );

        } catch (SQLException ex) {
            throw new DataAccessException("Failed to get game", ex);
        }
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        String sql = "SELECT gameID, gameName, whiteUsername, blackUsername, chessGame FROM game";
        try (var conn = DatabaseManager.getConnection();
             var stmt = conn.prepareStatement(sql)) {

            var rs = stmt.executeQuery();
            Collection<GameData> games = new ArrayList<>();

            while (rs.next()) {
                ChessGame chessGame = gson.fromJson(rs.getString("chessGame"), ChessGame.class);
                GameData gameData = new GameData(
                        rs.getInt("gameID"),
                        rs.getString("gameName"),
                        rs.getString("whiteUsername"),
                        rs.getString("blackUsername"),
                        chessGame
                );
                games.add(gameData);
            }

            return games;

        } catch (SQLException ex) {
            throw new DataAccessException("Failed to list games", ex);
        }
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {
        String sql = "UPDATE game SET gameName = ?, whiteUsername = ?, blackUsername = ?, chessGame = ? WHERE gameID = ?";
        try (var conn = DatabaseManager.getConnection();
             var stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, game.getGameName());
            stmt.setString(2, game.getWhiteUsername());
            stmt.setString(3, game.getBlackUsername());
            stmt.setString(4, gson.toJson(game.getGame()));
            stmt.setInt(5, game.getGameID());

            int rows = stmt.executeUpdate();
            if (rows == 0) {
                throw new DataAccessException("Game not found");
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Failed to update game", ex);
        }
    }

    @Override
    public void clear() throws DataAccessException {
        String sql = "DELETE FROM game";
        try (var conn = DatabaseManager.getConnection();
             var stmt = conn.prepareStatement(sql)) {

            stmt.executeUpdate();

        } catch (SQLException ex) {
            throw new DataAccessException("Failed to clear games", ex);
        }
    }

}