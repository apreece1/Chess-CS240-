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
        return null;
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        return List.of();
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {

    }

    @Override
    public void clear() throws DataAccessException {

    }
}
