package server;

import io.javalin.*;
import org.eclipse.jetty.server.Authentication;
import service.UserService;
import service.AuthService;
import service.GameService;

public class Server {

    private final Javalin javalin;
    private final UserService userService;
    private final AuthService authService;
    private GameService gameService;

    public Server(UserService userService, AuthService authService, GameService gameService, UserService userService1, AuthService authService1) {
        this.userService = userService1;
        this.authService = authService1;
        this.gameService = gameService;
        this.javalin = Javalin.create(config -> config.staticFiles.add("web"));
        registerEndpoints();
    }

    public Server(){
        var authDAO = new dataaccess.MemoryAuthDAO();
        var userDAO = new dataaccess.MemoryUserDAO();
        var gameDAO = new dataaccess.MemoryGameDAO();
    }

    private void registerEndpoints(){

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
