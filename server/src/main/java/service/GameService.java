package service;

import dataaccess.*;
import model.AuthData;
import model.GameData;
import chess.ChessGame;
import java.util.Collection;
import chess.ChessMove;

public class GameService {

    private final GameDAO gameDAO;
    private final AuthService authService;

    private final java.util.Set<Integer> completedGames =
            java.util.concurrent.ConcurrentHashMap.newKeySet();

    public GameService(GameDAO gameDAO, AuthService authService) {
        this.gameDAO = gameDAO;
        this.authService = authService;
    }

    public Collection<GameData> listGames(String authToken) throws DataAccessException {
        verifyAuth(authToken);
        return gameDAO.listGames();
    }

    public int createGame(String authToken, String gameName) throws DataAccessException {
        verifyAuth(authToken);

        if (gameName == null || gameName.isBlank()) {
            throw new DataAccessException("Error: bad request");
        }

        GameData newGame = new GameData(0, gameName, null, null, new ChessGame());
        return gameDAO.createGame(newGame);
    }


    public void joinGame(String authToken, int gameID, String playerColor) throws DataAccessException {
        var auth = verifyAuth(authToken);
        String username = auth.username();
        var game = gameDAO.getGame(gameID);

        if (game == null) {
            throw new DataAccessException("Error: bad request");

        }


        if (playerColor.equalsIgnoreCase("WHITE")) {
            if (game.getWhiteUsername() != null && !game.getWhiteUsername().equals(username)) {
                throw new DataAccessException("Error: already taken");
            }
            game.setWhiteUsername(username);
        } else if (playerColor.equalsIgnoreCase("BLACK")) {
            if (game.getBlackUsername() != null && !game.getBlackUsername().equals(username)){
                throw new DataAccessException("Error: already taken");
            }
            game.setBlackUsername(username);
        } else {
            throw new DataAccessException("Error: bad request");
        }

        gameDAO.updateGame(game);

    }


    public void makeMove(int gameID, String username, ChessMove move) throws DataAccessException {

        GameData gameData = gameDAO.getGame(gameID);
        if (gameData == null) {
            throw new DataAccessException("Error: bad request");
        }

        // Game already over?
        if (completedGames.contains(gameID)) {
            throw new DataAccessException("Error: game already over");
        }

        ChessGame game = gameData.getGame();

        String white = gameData.getWhiteUsername();
        String black = gameData.getBlackUsername();

        // Observer cannot move
        ChessGame.TeamColor playerColor;
        if (username.equals(white)) {
            playerColor = ChessGame.TeamColor.WHITE;
        } else if (username.equals(black)) {
            playerColor = ChessGame.TeamColor.BLACK;
        } else {
            throw new DataAccessException("Error: observer cannot move");
        }

        // Wrong turn
        if (game.getTeamTurn() != playerColor) {
            throw new DataAccessException("Error: not your turn");
        }

        // Invalid move
        try {
            game.makeMove(move);
        } catch (Exception ex) {
            throw new DataAccessException("Error: invalid move");
        }

        // Save updated game
        gameData.setGame(game);
        gameDAO.updateGame(gameData);

        // If game ended, mark it complete
        if (game.isInCheckmate(ChessGame.TeamColor.WHITE) ||
                game.isInCheckmate(ChessGame.TeamColor.BLACK) ||
                game.isInStalemate(ChessGame.TeamColor.WHITE) ||
                game.isInStalemate(ChessGame.TeamColor.BLACK)) {

            completedGames.add(gameID);
        }
    }



    public void clear() throws DataAccessException {
        gameDAO.clear();
    }

    private AuthData verifyAuth(String authToken) throws DataAccessException {
        var auth = authService.verifyAuth(authToken);
        if (auth == null) {
            throw new DataAccessException("Error: unauthorized");
        }
        return auth;
    }

    public GameData getGame(int gameID) throws DataAccessException {
        return gameDAO.getGame(gameID);
    }

}
