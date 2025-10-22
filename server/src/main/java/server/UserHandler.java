package server;


import com.google.gson.Gson;
import io.javalin.http.Context;
import service.UserService;
import service.AuthService;
import model.UserData;
import model.AuthData;
import dataaccess.DataAccessException;

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
            UserData request = gson.fromJson(ctx.body(), UserData.class);
            AuthData result = userService.login(request);
            ctx.status(200).json(result);

        } catch (DataAccessException e) {
            ctx.status(401).json(new ErrorMessage(e.getMessage()));
        } catch (Exception e) {
            ctx.status(500).json(new ErrorMessage("Error: " + e.getMessage()));
        }
    }

    public void logout(Context ctx) {
        try {
            String authToken = ctx.header("authorization");
            userService.logout(authToken);
            ctx.status()
        }
    }


}
