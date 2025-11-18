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
            throw new DataAccessException("bad request");
        }

        try {
            UserData existing = userDAO.getUser(user.username());
            if (existing != null) {
                throw new DataAccessException("already taken");
            }
        } catch (DataAccessException e) {
            if (!e.getMessage().toLowerCase().contains("not found")) {
                throw e;
            }
        }

        try {
            userDAO.createUser(user);
        } catch (DataAccessException e) {
            throw e;
        }

        return authService.createAuth(user.username());
    }

    public AuthData login(String username, String password) throws DataAccessException {
        UserData user;
        try {
            user = userDAO.getUser(username);
        } catch (DataAccessException e) {
            throw e;
        }

        if (user == null || !org.mindrot.jbcrypt.BCrypt.checkpw(password, user.password())) {
            throw new DataAccessException("unauthorized");
        }

        return authService.createAuth(username);
    }

    public void logout(String authToken) throws DataAccessException {
        authService.deleteAuth(authToken);
    }

    public void clear() throws DataAccessException {
        userDAO.clear();
        authService.clear();
    }

}
