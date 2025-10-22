package service;

import dataaccess.*;
import model.AuthData;

import java.util.UUID;

public class AuthService {

    private final AuthDAO authDAO;

    public AuthService(AuthDAO authDAO) {
        this.authDAO = authDAO;
    }

    public AuthData createAuth(String username) throws DataAccessException {
        String token = UUID.randomUUID().toString();
        AuthData auth = new AuthData(token, username);
        authDAO.createAuth(auth);
        return auth;
    }

    public AuthData getAuthDAO(String authToken) throws DataAccessException {
        return authDAO.getAuth(authToken);
    }

    public AuthData verifyAuth(String authToken) throws DataAccessException{
        var auth = authDAO.getAuth(authToken);
        if (auth == null) {
            throw new DataAccessException("Error: unauthorized");
        }
        return auth;
    }

    public void deleteAuth(String authToken) throws DataAccessException {
        authDAO.deleteAuth(authToken);
    }

    public void clear() throws DataAccessException {
        authDAO.clear();
    }

}
