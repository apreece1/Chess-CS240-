package server;


import com.google.gson.Gson;
import io.javalin.http.Context;
import service.UserService;
import service.AuthService;
import model.UserData;
import model.AuthData;
import model.LoginRequest;
import dataaccess.DataAccessException;

import java.sql.SQLException;
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

    private boolean isDatabaseFailure(DataAccessException e) {
        return e.getCause() instanceof SQLException
                || (e.getMessage() != null
                && e.getMessage().toLowerCase().contains("failed to get connection"));
    }

    private void handleAuthDataException(Context ctx, DataAccessException e) {
        String msg = e.getMessage() == null ? "" : e.getMessage().toLowerCase();
        if (msg.contains("unauthorized")) {
            ctx.status(401).json(new ErrorMessage("Error: unauthorized"));
        } else if (isDatabaseFailure(e)) {
            ctx.status(500).json(new ErrorMessage("Internal Server Error"));
        } else {
            ctx.status(500).json(new ErrorMessage("Internal Server Error"));
        }
    }

    public void register(Context ctx) {
        try {
            UserData request = gson.fromJson(ctx.body(), UserData.class);

            if (request.username() == null || request.password() == null) {
                ctx.status(400).json(new ErrorMessage("Error: Missing username or password"));
                return;
            }

            AuthData result = userService.register(request);
            ctx.status(200).json(result);

        } catch (com.google.gson.JsonSyntaxException e) {
            ctx.status(400).json(new ErrorMessage("Error: Bad request"));
        } catch (DataAccessException e) {
            String msg = e.getMessage();
            if (msg != null && msg.toLowerCase().contains("already taken")) {
                ctx.status(403).json(new ErrorMessage("Error: already taken"));
            } else if (isDatabaseFailure(e)) {
                ctx.status(500).json(new ErrorMessage("Internal Server Error"));
            } else {
                ctx.status(500).json(new ErrorMessage("Internal Server Error"));
            }
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
            handleAuthDataException(ctx, e);
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
            String msg = e.getMessage() == null ? "" : e.getMessage().toLowerCase();

            if (msg.contains("unauthorized")) {
                // e.g. second logout, or bogus token
                ctx.status(401).json(new ErrorMessage("Error: unauthorized"));
            } else if (isDatabaseFailure(e)) {
                ctx.status(500).json(new ErrorMessage("Internal Server Error"));
            } else {
                ctx.status(500).json(new ErrorMessage("Internal Server Error"));
            }

        } catch (Exception e) {
            ctx.status(500).json(new ErrorMessage("Error: " + e.getMessage()));
        }
    }

}
