package service;

import dataaccess.*;
import model.UserData;
import model.AuthData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTests {

    private UserService userService;
    
    @BeforeEach
    void setUp() {
        MemoryUserDAO userDAO = new MemoryUserDAO();
        AuthService authService = new AuthService(new MemoryAuthDAO());
        userService = new UserService(userDAO, authService);
    }
