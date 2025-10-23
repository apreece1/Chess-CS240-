package service;

import dataaccess.*;
import model.AuthData;
import model.GameData;
import chess.ChessGame;
import java.util.Collection;

public class GameService {

    private final GameDAO gameDAO;
    private final AuthService authService;

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

        if (playerColor == null || playerColor.isBlank()) {
            return;
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

}
