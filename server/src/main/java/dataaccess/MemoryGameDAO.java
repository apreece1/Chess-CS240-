package dataaccess;

import model.GameData;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Collection;

public class MemoryGameDAO  implements GameDAO{

    private final Map<Integer, GameData> games = new HashMap<>();
    private int nextGameID = 1;

    @Override
    public int createGame(GameData game) throws DataAccessException {
        int gameID = nextGameID++;
        var newGame = new GameData(
                gameID,
                game.whiteUsername(),
                game.blackUsername(),
                game.gameName(),
                game.game()
        );
        games.put(gameID, newGame);
        return gameID;
    }


    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        var game = games.get(gameID);
        if (game == null) {
            throw new DataAccessException("Game not found");
        }
        return game;
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        return games.values();
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {
        if (!games.containsKey(game.gameID())){
            throw new DataAccessException("Game not found")
        }

    }

    @Override
    public void clear() throws DataAccessException {

    }
}
