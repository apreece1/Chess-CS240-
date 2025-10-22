package server;

import io.javalin.*;
import org.eclipse.jetty.server.Authentication;
import service.UserService;
import service.AuthService;
import service.GameService;

public class Server {

    private final Javalin javalin;

    public Server(UserService userService, AuthService authService, GameService gameService) {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        UserHandler userHandler = new UserHandler(userService, authService);
        GameHandler gameHandler = new GameHandler(gameService);

        // Register your endpoints and exception handlers here.
        javalin.post("/user", userHandler::register);
        javalin.post("/session", userHandler::login);
        javalin.delete("/session", userHandler::logout);

        javalin.get("/game", gameHandler::listGames);
        javalin.post("/game", gameHandler:: createGame);
        javalin.put("/game:id?/join", gameHandler::joinGame);

    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
