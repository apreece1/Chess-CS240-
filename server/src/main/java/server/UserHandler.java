package server;


import com.google.gson.Gson;
import io.javalin.http.Context;
import service.UserService;
import service.AuthService;
import model.UserData;
import model.AuthData;
import model.LoginRequest;
import dataaccess.DataAccessException;

import java.util.Map;

public class UserHandler {

    private final UserService userService;
    private final AuthService authService;
    private final Gson gson = new Gson();


    public UserHandler(UserService userService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }

    private record ErrorMessage(String message) {}

    public void register(Context ctx) {
        try {
            UserData request = gson.fromJson(ctx.body(), UserData.class);
            AuthData result = userService.register(request);
            ctx.status(200).json(result);


        } catch (DataAccessException e) {
            ctx.status(400).json(new ErrorMessage(e.getMessage()));
        } catch (Exception e) {
            ctx.status(500).json(new ErrorMessage("Error: " + e.getMessage()));
        }
    }

    public void login(Context ctx) {
        try {
            LoginRequest request = gson.fromJson(ctx.body(), LoginRequest.class);

            if (request.username() == null || request.password() == null) {
                ctx.status(400).json(new ErrorMessage("Error: Missing username or password"));
                return;
            }

            AuthData result = userService.login(request.username(), request.password());
            ctx.status(200).json(result);

        } catch (com.google.gson.JsonSyntaxException e) {
           ctx.status(400).json(new ErrorMessage("Error: Bad request"));
        } catch (DataAccessException e) {
            ctx.status(401).json(new ErrorMessage("Error: " + e.getMessage()));
        } catch (Exception e) {
            ctx.status(500).json(new ErrorMessage("Error: " + e.getMessage()));
        }
    }

    public void logout(Context ctx) {
        try {
            String authToken = ctx.header("authorization");
            userService.logout(authToken);
            ctx.status(200).json(Map.of("message", "Logout successful"));

        } catch (DataAccessException e) {
            ctx.status(401).json(new ErrorMessage(e.getMessage()));
        } catch (Exception e) {
            ctx.status(500).json(new ErrorMessage("Error: " + e.getMessage()));
        }
    }


}
