package ui;

import client.ServerFacade;
import model.AuthData;
import model.GameData;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

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


    






}
