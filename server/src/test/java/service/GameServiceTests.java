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

    