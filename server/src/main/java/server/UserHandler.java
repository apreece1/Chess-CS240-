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
            throw new RuntimeException(e);
        }
    }
}
