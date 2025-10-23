package service;

import dataaccess.MemoryGameDAO;
import dataaccess.DataAccessException;
import model.GameData;
import model.AuthData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class GameServiceTests {

    private GameService gameService;

    @BeforeEach
    void setUp() {
        MemoryGameDAO gameDAO = new MemoryGameDAO();
        StubAuthService authService = new StubAuthService();
        gameService = new GameService(gameDAO, authService);
    }

    static class StubAuthService extends AuthService {
        public StubAuthService() {
            super(null);
        }

        @Override
        public AuthData verifyAuth(String authToken) throws DataAccessException {
            if (authToken == null || authToken.isBlank()) {
                throw new DataAccessException("Error: unauthorized");
            }
            return new AuthData(authToken, "user_" + authToken);
        }
    }

    @Test
    void testCreateGame() throws DataAccessException {
        String token = "valid-token";
        int gameID = gameService.createGame(token, "My Game");

        assertTrue(gameID > 0);
        Collection<GameData> games = gameService.listGames(token);
        assertEquals(1, games.size());
    }

    @Test
    void testJoinGame() throws DataAccessException {
        String token = "valid-token";
        int gameID = gameService.createGame(token, "My Game");

        gameService.joinGame(token, gameID, "WHITE");

        var game = gameService.listGames(token).stream()
                .filter(g -> g.getGameID() == gameID)
                .findFirst()
                .orElseThrow();

        assertEquals("user_valid-token", game.getWhiteUsername());
    }

    @Test
    void testJoinGameAlreadyTaken() throws DataAccessException {
        String token1 = "token1";
        String token2 = "token2";

        int gameID = gameService.createGame(token1, "My Game");

        gameService.joinGame(token1, gameID, "WHITE");

        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            gameService.joinGame(token2, gameID, "WHITE");
        });
        assertTrue(ex.getMessage().contains("already taken"));
    }

    @Test
    void testCreateGameBadRequest() {
        String token = "valid-token";
        DataAccessException ex1 = assertThrows(DataAccessException.class, () -> {
            gameService.createGame(token, null);
        });
        assertTrue(ex1.getMessage().contains("bad request"));

        DataAccessException ex2 = assertThrows(DataAccessException.class, () -> {
            gameService.createGame(token, "  ");
        });
        assertTrue(ex2.getMessage().contains("bad request"));
    }

    @Test
    void testClear() throws DataAccessException {
        String token = "valid-token";
        int gameID = gameService.createGame(token, "My Game");

        Collection<GameData> gamesBefore = gameService.listGames(token);
        assertEquals(1, gamesBefore.size());

        gameService.clear();

        Collection<GameData> gamesAfter = gameService.listGames(token);
        assertEquals(0, gamesAfter.size());
    }
}





