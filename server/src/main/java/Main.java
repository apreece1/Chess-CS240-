import server.Server;

import dataaccess.MemoryUserDAO;
import dataaccess.MemoryAuthDAO;
import service.UserService;
import service.AuthService;


public class Main {
    public static void main(String[] args) {
        var authDAO = new MemoryAuthDAO();
        var userDAO = new MemoryUserDAO();

        var authService = new AuthService(authDAO);
        var userService = new UserService(userDAO, authService);


        Server server = new Server(userService, authService);
        server.run(8080);

        System.out.println("♕ 240 Chess Server");
    }
}