import server.Server;

import dataaccess.MemoryUserDAO;
import dataaccess.MemoryAuthDAO;
import service.UserService;
import service.AuthService;


public class Main {
    public static void main(String[] args) {
        Server server = new Server();
        server.run(8080);

        System.out.println("♕ 240 Chess Server");
    }
}