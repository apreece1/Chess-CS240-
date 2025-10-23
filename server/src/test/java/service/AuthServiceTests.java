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
            return auths.stream()
                    .filter(a -> a.username().equals(authToken))
                    .findFirst()
                    .orElseThrow(() -> new DataAccessException("Error: unauthorized"));
        }

        @Override
        public void deleteAuth(String authToken) throws DataAccessException {
            AuthData toRemove = auths.stream()
                    .filter(a -> a.username().equals(authToken))
                    .findFirst()
                    .orElseThrow(() -> new DataAccessException("Auth token not found"));
            auths.remove(toRemove);
        }

        @Override
        public void clear() {
            auths.clear();
        }
    }

    @Test
    void testCreateAndVerifyAuth() throws DataAccessException {
        AuthData auth = authService.createAuth("user1");
        assertNotNull(auth);

        AuthData verified = authService.verifyAuth("user1");
        assertEquals("user1", verified.username());
    }

    @Test
    void testVerifyAuthUnauthorized() {
        DataAccessException ex = assertThrows(DataAccessException.class, () -> authService.verifyAuth(""));
        assertTrue(ex.getMessage().contains("unauthorized"));
    }

    @Test
    void testDeleteAuth() throws DataAccessException {
        authService.createAuth("user2");
        authService.deleteAuth("user2");

        DataAccessException ex = assertThrows(DataAccessException.class, () -> authService.verifyAuth("user2"));
        assertTrue(ex.getMessage().contains("unauthorized"));
    }
}







