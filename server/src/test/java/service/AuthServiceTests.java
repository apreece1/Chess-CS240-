package service;

import dataaccess.DataAccessException;
import model.AuthData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AuthServiceTests {
    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new StubAuthService();
    }

    static class StubAuthService extends AuthService {
        private final List<AuthData> auths = new ArrayList<>();

    }

    static class StubAuthService extends AuthService {
        private final List<AuthData> auths = new ArrayList<>();

        public StubAuthService() {
            super(null);
        }

        @Override
        public AuthData createAuth(String username) {
            AuthData auth = new AuthData(username, username);
            auths.add(auth);
            return auth;
        }

        @Override
        public AuthData verifyAuth(String authToken) throws DataAccessException {
            if (authToken == null || authToken.isBlank()) {
                throw new DataAccessException("Error: unauthorized");
            }

            
