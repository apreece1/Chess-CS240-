package dataaccess;

import model.GameData;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Collection;

public class MemoryGameDAO  implements GameDAO{
    @Override
    public int createGame(GameData game) throws DataAccessException {
        return 0;
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
