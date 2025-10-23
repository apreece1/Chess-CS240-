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

        try {
            userDAO.getUser(user.username());
            throw new DataAccessException("Error: already taken");
        } catch (DataAccessException ignored) {

        }

        userDAO.createUser(user);

        return authService.createAuth(user.username());

    }

    public AuthData login(String username, String password) throws DataAccessException {
        var user = userDAO.getUser(username);
        if (user == null || !user.password().equals(password)) {
            throw new DataAccessException("Error: unauthorized");
        }

        return authService.createAuth(username);
    }

    public void logout(String authToken) throws DataAccessException {
        authService.deleteAuth(authToken);
    }


    public void clear() throws DataAccessException{
        userDAO.clear();
        authService.clear();
    }

}
