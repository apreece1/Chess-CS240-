import dataaccess.*;
import dataaccess.DatabaseManager;
import server.Server;

import dataaccess.MySqlAuthDAO;
import dataaccess.MySqlUserDAO;
import dataaccess.MySqlGameDAO;
import service.UserService;
import service.AuthService;
import service.GameService;


public class Main {
    public static void main(String[] args) {
        try {
            DatabaseManager.createDatabase();
            DatabaseManager.createTables();
            System.out.println("Database and tables initialized successfully.");
        } catch (DataAccessException e) {
            System.err.println("Fatal Error: Could not initialize database. Exiting.");
            e.printStackTrace();
            return;
        }

        MySqlAuthDAO authDAO;
        MySqlUserDAO userDAO;
        MySqlGameDAO gameDAO;

        try {
            authDAO = new MySqlAuthDAO();
            userDAO = new MySqlUserDAO();
            gameDAO = new MySqlGameDAO();
        } catch (DataAccessException e) {
            System.err.println("Fatal Error: Could not initialize DAO classes. Exiting.");
            e.printStackTrace();
            return;
        }

        var authService = new AuthService(authDAO);
        var userService = new UserService(userDAO, authService);
        var gameService = new GameService(gameDAO, authService);


        Server server = new Server(userService, authService, gameService);
        server.run(8081);

        System.out.println("♕ 240 Chess Server");
    }
}