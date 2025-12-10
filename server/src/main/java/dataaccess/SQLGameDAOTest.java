package dataaccess;

import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class SQLGameDAOTest {

    private GameDAO gameDAO;

    @BeforeEach
    void setUp() throws DataAccessException {
        gameDAO = new MySqlGameDAO();
        gameDAO.clear();
    }

    @Test
    void ClearPositiveClearsAllGames() throws DataAccessException {
        GameData game1 = new GameData(0, "Game 1", "white1", "black1", null);
        GameData game2 = new GameData(0, "Game 2", "white2", "black2", null);

        gameDAO.createGame(game1);
        gameDAO.createGame(game2);

        Collection<GameData> beforeClear = gameDAO.listGames();
        assertEquals(2, beforeClear.size());

        gameDAO.clear();

        Collection<GameData> afterClear = gameDAO.listGames();
        assertNotNull(afterClear);
        assertTrue(afterClear.isEmpty());
    }

    @Test
    void ClearNegativeOnEmptyTableNoException() {
        assertDoesNotThrow(() -> gameDAO.clear());
    }

    @Test
    void createGamePositivePersistsGame() throws DataAccessException {
        GameData game = new GameData(0, "My Game", "whiteUser", "blackUser", null);

        gameDAO.createGame(game);

        Collection<GameData> games = gameDAO.listGames();
        assertEquals(1, games.size());

        GameData fromDb = games.iterator().next();
        assertEquals("My Game", fromDb.getGameName());
        assertEquals("whiteUser", fromDb.getWhiteUsername());
        assertEquals("blackUser", fromDb.getBlackUsername());
    }

    @Test
    void CreateGameNegativeNullGameThrows() {
        assertThrows(NullPointerException.class, () -> gameDAO.createGame(null));
    }

    @Test
    void getGame_positive_existingGame() throws DataAccessException {
        GameData game = new GameData(0, "Game X", "whiteUser", "blackUser", null);
        gameDAO.createGame(game);

        Collection<GameData> games = gameDAO.listGames();
        GameData stored = games.iterator().next();
        int gameID = stored.getGameID();

        GameData fromDb = gameDAO.getGame(gameID);
        assertNotNull(fromDb);
        assertEquals("Game X", fromDb.getGameName());
        assertEquals("whiteUser", fromDb.getWhiteUsername());
        assertEquals("blackUser", fromDb.getBlackUsername());
    }

    @Test
    void GetGameNegativeNonExistingGameReturnsNull() throws DataAccessException {
        assertNull(gameDAO.getGame(999999));
    }

    @Test
    void ListGamesPositiveMultipleGamesReturned() throws DataAccessException {
        gameDAO.createGame(new GameData(0, "Game 1", "w1", "b1", null));
        gameDAO.createGame(new GameData(0, "Game 2", "w2", "b2", null));
        gameDAO.createGame(new GameData(0, "Game 3", "w3", "b3", null));

        Collection<GameData> games = gameDAO.listGames();
        assertEquals(3, games.size());
    }

    @Test
    void listGames_negative_emptyTable_returnsEmpty() throws DataAccessException {
        Collection<GameData> games = gameDAO.listGames();
        assertNotNull(games);
        assertTrue(games.isEmpty());
    }

    @Test
    void updateGame_positive_updatesExistingGame() throws DataAccessException {
        GameData original = new GameData(0, "Old Name", "whiteUser", "blackUser", null);
        gameDAO.createGame(original);

        Collection<GameData> games = gameDAO.listGames();
        GameData stored = games.iterator().next();
        int gameID = stored.getGameID();

        GameData updated = new GameData(gameID, "New Name", "newWhite", "newBlack", null);
        gameDAO.updateGame(updated);

        GameData fromDb = gameDAO.getGame(gameID);
        assertNotNull(fromDb);
        assertEquals("New Name", fromDb.getGameName());
        assertEquals("newWhite", fromDb.getWhiteUsername());
        assertEquals("newBlack", fromDb.getBlackUsername());
    }

    @Test
    void updateGame_negative_nonExistingGame_throws() {
        GameData fake = new GameData(999999, "Does not exist", "w", "b", null);
        assertThrows(DataAccessException.class, () -> gameDAO.updateGame(fake));
    }
}
