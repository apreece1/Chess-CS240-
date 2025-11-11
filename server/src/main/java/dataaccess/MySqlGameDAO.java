package dataaccess;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dataaccess.dataaccess.DatabaseManager;
import model.GameData;
import model.ChessGame;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;

public class MySqlGameDAO implements GameDAO{

    private final Gson gson;

    public MySqlGameDAO() {
        this.gson = new GsonBuilder().create();
    }

    @Override
    public int createGame(GameData game) throws DataAccessException {
        String sql = "INSERT INTO Game (gameName, whiteUsername, blackUsername, chessGame) VALUES (?, ?, ?, ?)";
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
        String sql = "SELECT gameID, gameName, whiteUsername, blackUsername, chessGame FROM Game WHERE gameID = ?";
        try (var conn = DatabaseManager.getConnection();
             var stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, gameID);
            var rs = stmt.executeQuery();

            if (!rs.next()) {
                throw new DataAccessException("Game not found");
            }

            ChessGame chessGame = gson.fromJson(rs.getString("chessGame"), ChessGame.class);

            return new GameData(
                    rs.getInt("gameID"),
                    rs.getString("gameName"),
                    rs.getString("whiteUsername"),
                    rs.getString("blackUsername"),
                    chessGame
            );



        }
