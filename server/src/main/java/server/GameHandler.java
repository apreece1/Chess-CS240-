package server;

import com.google.gson.Gson;
import io.javalin.http.Context;
import service.AuthService;
import model.GameData;
import dataaccess.DataAccessException;
import service.GameService;

public class GameHandler {

    private final GameService gameService;
    private final AuthService authService;
    private final Gson gson = new Gson();

    public GameHandler(GameService gameService, AuthService authService) {
        this.gameService = gameService;
        this.authService = authService;
    }


}
