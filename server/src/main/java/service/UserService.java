package service;

import dataaccess.*;
import model.UserData;
import model.AuthData;

public class UserService {

    private final UserDAO userDAO;
    private final AuthService authService;


    public UserService(UserDAO userDAO, AuthService authService) {
        this.userDAO = userDAO;
        this.authService = authService;
    }
}
