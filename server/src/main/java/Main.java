import dataaccess.GameDAO;
import server.Server;

import dataaccess.MemoryUserDAO;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import service.UserService;
import service.AuthService;
import service.GameService;


public class Main {
    public static void main(String[] args) {
        var authDAO = new MemoryAuthDAO();
        var userDAO = new MemoryUserDAO();
        var gameDAO = new MemoryGameDAO();

        var authService = new AuthService(authDAO);
        var userService = new UserService(userDAO, authService);
        var gameService = new GameService(gameDAO, authService);


        Server server = new Server(userService, authService, gameService);
        server.run(8080);

        System.out.println("♕ 240 Chess Server");
    }
}