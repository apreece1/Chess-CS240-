package service;

import dataaccess.*;
import model.AuthData;
import java.util.UUID;

public class AuthService {

    private final AuthDAO authDAO;

    public AuthService(AuthDAO authDAO) {
        this.authDAO = authDAO;
    }


}
