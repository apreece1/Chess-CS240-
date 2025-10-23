package server;

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

    public void clear(Context ctx) {
        String authToken = ctx.header("authorization")
        try {
            userService.clear();
            authService.clear();
            gameService.clear();
            ctx.status(200).json(Map.of());
        } catch (Exception e) {
            ctx.status(500).json(Map.of("message", "Error: " + (e.getMessage() != null ? e.getMessage() : "internal server error")));
        }
    }
}
