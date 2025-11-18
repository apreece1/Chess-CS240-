package server;

import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import io.javalin.*;
import service.UserService;
import service.AuthService;
import service.GameService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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

        this.javalin = createJavalin();
        registerEndpoints();
        registerExceptionHandlers();

    }

    public Server(){

        try {
            DatabaseManager.createDatabase();
            DatabaseManager.createTables();


            var authDAO = new dataaccess.MySqlAuthDAO();
            var userDAO = new dataaccess.MySqlUserDAO();
            var gameDAO = new dataaccess.MySqlGameDAO();

            this.authService = new AuthService(authDAO);
            this.userService = new UserService(userDAO, authService);
            this.gameService = new GameService(gameDAO, authService);

        } catch (DataAccessException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize DAOs", e);
        }

        this.javalin = createJavalin();
        registerEndpoints();
        registerExceptionHandlers();
    }

    private void registerEndpoints(){

        UserHandler userHandler = new UserHandler(userService, authService);
        GameHandler gameHandler = new GameHandler(gameService);
        ClearHandler clearHandler = new ClearHandler(userService, authService, gameService);


        javalin.post("/user", userHandler::register);
        javalin.post("/session", userHandler::login);
        javalin.delete("/session", userHandler::logout);

        javalin.get("/game", gameHandler::listGames);
        javalin.post("/game", gameHandler::createGame);
        javalin.put("/game", gameHandler::joinGame);

        javalin.delete("/db", clearHandler::clear);

    }

    private void registerExceptionHandlers() {
        javalin.exception(DataAccessException.class, (e, ctx) -> {
            e.printStackTrace();

            ctx.status(500);
            ctx.json("{\"error\":\"Internal Server Error\",\"message\":\"" + e.getMessage() + "\"}");
        });
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }

    private Javalin createJavalin() {
        Gson gson = new GsonBuilder().create();
        JsonMapper gsonMapper = new JsonMapper() {
            @Override
            public String toJsonString(Object obj, Type type) {
                return gson.toJson(obj, type);
            }

            @Override
            public <T> T fromJsonString(String json, Type targetType) {
                return gson.fromJson(json, targetType);
            }
        };

        return Javalin.create(config -> {
            config.staticFiles.add("web");
            config.jsonMapper(gsonMapper);
        });
    }

}
