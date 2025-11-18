package ui;

import client.ServerFacade;
import model.AuthData;
import model.GameData;

import java.util.*;

public class ChessClient {

    private final ServerFacade facade;
    private AuthData currentUser;
    private List<GameData> lastGames = new ArrayList<>();
    private final Scanner scanner = new Scanner(System.in);

    private enum State { PRELOGIN, POSTLOGIN }
    private State state = State.PRELOGIN;

    public ChessClient(ServerFacade facade) {
        this.facade = facade;
    }

    public void run() {
        System.out.println("♕ Welcome to CS 240 Chess!");
        boolean running = true;

        while (running) {
            try {
                running = (state == State.PRELOGIN) ? preloginLoop() : postloginLoop();
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }

        System.out.println("Goodbye!");
    }

    private boolean preloginLoop() throws Exception {
        System.out.print("\n[Prelogin] Enter command (help, login, register, quit): ");
        String cmd = scanner.nextLine().trim().toLowerCase();

        switch (cmd) {
            case "help" -> printPreloginHelp();
            case "login" -> login();
            case "register" -> register();
            case "quit" -> { return false; }
            default -> System.out.println("Unknown command. Type 'help'.");
        }
        return true;
    }

    private void printPreloginHelp() {
        System.out.println("""
                Commands:
                  help     - Show this help
                  login    - Login to account
                  register - Create a new account
                  quit     - Exit program
                """);
    }

    private void login() throws Exception {
        System.out.print("Username: ");
        String u = scanner.nextLine().trim();
        System.out.print("Password: ");
        String p = scanner.nextLine().trim();

        currentUser = facade.login(u, p);
        System.out.println("Logged in as " + u);
        state = State.POSTLOGIN;
    }


    private void register() throws Exception {
        System.out.print("Username: ");
        String u = scanner.nextLine().trim();
        System.out.print("Password: ");
        String p = scanner.nextLine().trim();
        System.out.print("Email: ");
        String e = scanner.nextLine().trim();

        currentUser = facade.register(u, p, e);
        System.out.println("Registered and logged in as " + u);
        state = State.POSTLOGIN;
    }

    private boolean postloginLoop() throws Exception {
        System.out.print("\n[Postlogin] Enter command (help, logout, create, list, play, observe, quit): ");
        String cmd = scanner.nextLine().trim().toLowerCase();

        switch (cmd) {
            case "help" -> printPostloginHelp();
            case "logout" -> logout();
            case "create" -> createGame();
            case "list" -> listGames();
            case "play" -> playGame();
            case "observe" -> observeGame();
            case "quit" -> { return false; }
            default -> System.out.println("Unknown command. Type 'help'.");
        }
        return true;
    }


}
