package server;

import io.javalin.*;
import org.eclipse.jetty.server.Authentication;
import service.UserService;
import service.AuthService;

public class Server {

    private final Javalin javalin;

    public Server(UserService userService, AuthService authService) {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        UserHandler userHandler = new UserHandler(userService, authService);

        // Register your endpoints and exception handlers here.
        javalin.post("/user", userHandler::register);
        javalin.post("/session", userHandler::login);
        javalin.delete("/session", userHandler::logout);

    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
