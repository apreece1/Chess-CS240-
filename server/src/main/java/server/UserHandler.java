package server;


import com.google.gson.Gson;
import service.UserService;

public class UserHandler {

    private final UserService userService;
    private final AuthService authService;
    private final Gson gson = new Gson();


    public UserHandler(UserService userService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }
}
