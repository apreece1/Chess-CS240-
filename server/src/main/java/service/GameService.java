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

    
}
