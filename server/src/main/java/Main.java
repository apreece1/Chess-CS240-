import dataaccess.*;
import dataaccess.dataaccess.DatabaseManager;
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

        var authDAO = new MySqlAuthDAO();
        var userDAO = new MySqlUserDAO();
        var gameDAO = new MeySqlGameDAO();

        var authService = new AuthService(authDAO);
        var userService = new UserService(userDAO, authService);
        var gameService = new GameService(gameDAO, authService);


        Server server = new Server(userService, authService, gameService);
        server.run(8080);

        System.out.println("♕ 240 Chess Server");
    }
}