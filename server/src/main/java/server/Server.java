package server;

import io.javalin.*;
import service.UserService;
import service.AuthService;
import service.GameService;
import com.google.gson.Gson;
import io.javalin.json.JsonMapper;
import java.lang.reflect.Type;


public class Server {

    private final Javalin javalin;
    private final UserService userService;
    private final AuthService authService;
    private final GameService gameService;

    public Server(UserService userService, AuthService authService, GameService gameService) {
        this.userService = userService;
        this.authService = authService;
        this.gameService = gameService;

        Gson gson = new GsonBuilder().create();
        JsonMapper gsonMapper = new JsonMapper() {
            @Override
            public String toJsonString(Object obj, Type type) {
                return gson.toJson(obj, type);
            }



        registerEndpoints();

    }

    public Server(){
        this.javalin = Javalin.create(config -> config.staticFiles.add("web"));

        var authDAO = new dataaccess.MemoryAuthDAO();
        var userDAO = new dataaccess.MemoryUserDAO();
        var gameDAO = new dataaccess.MemoryGameDAO();

        this.authService = new AuthService(authDAO);
        this.userService = new UserService(userDAO, authService);
        this.gameService = new GameService(gameDAO, authService);

        registerEndpoints();
    }

    private void registerEndpoints(){

        UserHandler userHandler = new UserHandler(userService, authService);
        GameHandler gameHandler = new GameHandler(gameService);
        ClearHandler clearHandler = new ClearHandler(userService, authService, gameService);

        // Register your endpoints and exception handlers here.
        javalin.post("/user", userHandler::register);
        javalin.post("/session", userHandler::login);
        javalin.delete("/session", userHandler::logout);

        javalin.get("/game", gameHandler::listGames);
        javalin.post("/game", gameHandler::createGame);
        javalin.put("/game/join", gameHandler::joinGame);

        javalin.delete("/db", clearHandler::clear);

    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
