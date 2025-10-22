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

    public AuthData register(UserData user) throws DataAccessException {
        if (user.username() == null || user.username().isBlank() ||
            user.password() == null || user.password().isBlank()) {
            throw new DataAccessException("Error: bad request");
        }

        try{
            
        }
    }
}
