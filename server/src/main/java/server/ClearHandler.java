package server;

import dataaccess.DataAccessException;
import service.AuthService;
import service.GameService;
import service.UserService;
import io.javalin.http.Context;
import java.util.Map;


public class ClearHandler {

    private final UserService userService;
    private final AuthService authService;
    private final GameService gameService;

    public ClearHandler(UserService userService, AuthService authService, GameService gameService) {
        this.userService = userService;
        this.authService = authService;
        this.gameService = gameService;
    }

    public void clear(Context ctx) throws DataAccessException {

        String authToken = ctx.header("Authorization");
        if (authToken == null || authService.verifyAuth(authToken) == null) {
            throw new DataAccessException("Error: unauthorized");
        }

        userService.clear();
        authService.clear();
        gameService.clear();
        ctx.status(200).json(Map.of());
    }
}