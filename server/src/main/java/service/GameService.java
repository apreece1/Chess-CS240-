package service;

import dataaccess.*;
import model.AuthData;
import model.GameData;
import chess.ChessGame;
import java.util.Collection;

public class GameService {

    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public GameService(GameDAO gameDAO, AuthDAO authDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
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

        GameData newGame = new GameData(0, null, null, gameName, new ChessGame())
        return gameDAO.createGame(newGame);

    }

    public void JoinGame(String authToken, int gameID, String playerColor) throws DataAccessException {
        var auth = verifyAuth(authToken);
        var game = gameDAO.getGame(gameID);

        if (game == null) {
            throw new DataAccessException("Error: bad request");
        }

        if (playerColor.equalsIgnoreCase("WHITE")) {
            if (game.whiteUsername() != null && !game.whiteUsername().equals(username)) {
                throw new DataAccessException("Error: already taken");
            }
            game = new GameData(game.gameID(), game.whiteUsername(), username, game.gameName(), game.game());
        } else if (playerColor.equalsIgnoreCase("BLACK")) {
            if (game.blackUsername() != null && !game.blackUsername().equals(username)){
                throw new DataAccessException("Error: already taken");
            }
            game = new GameData(game.gameID(), game.whiteUsername(), username, game.gameName(), game.game());
        } else {
            throw new DataAccessException("Error: bad request");
        }

        gameDAO.updateGame(game);

    }


}
