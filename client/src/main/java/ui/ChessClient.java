package ui;

import chess.ChessMove;
import chess.ChessPosition;
import client.ServerFacade;
import client.WebSocketFacade;
import client.GameplayObserver;
import model.AuthData;
import model.GameData;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ChessClient implements GameplayObserver {
    private final ServerFacade facade;
    private final int port;
    private WebSocketFacade ws;

    private AuthData currentUser;
    private List<GameData> lastGames = new ArrayList<>();
    private GameData currentGame;
    private final Scanner scanner = new Scanner(System.in);

    @Override
    public void onLoadGame(GameData game) {
        System.out.println("[CLIENT] LOAD_GAME received for gameID=" + game.getGameID());
        this.currentGame = game;
        redrawBoard();
    }

    @Override
    public void onNotification(String message) {
        System.out.println("[Notification] " + message);
    }

    @Override
    public void onError(String errorMessage) {
        System.out.println(errorMessage);
    }

    private enum State { PRELOGIN, POSTLOGIN }
    private State state = State.PRELOGIN;

    public ChessClient(ServerFacade facade) {
        this.facade = facade;
        this.port = facade.getPort();
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

    private void printPostloginHelp() {
        System.out.println("""
                Commands:
                  help    - Show this help
                  logout  - Logout
                  create  - Create a new game
                  list    - List existing games
                  play    - Join a game as a player
                  observe - Observe a game
                  quit    - Exit program
                """);
    }

    private void logout() throws Exception {
        if (currentUser != null) {
            facade.logout(currentUser.authToken());
        }
        if (ws != null) {
            ws.close();
            ws = null;
        }
        currentUser = null;
        currentGame = null;
        lastGames.clear();
        state = State.PRELOGIN;
        System.out.println("Logged out.");
    }

    private void createGame() throws Exception {
        System.out.print("Game name: ");
        String name = scanner.nextLine().trim();
        if (name.isBlank()) {
            System.out.println("Game name cannot be empty.");
            return;
        }
        int gameID = facade.createGame(currentUser.authToken(), name);
        System.out.println("Created game '" + name + "' (internal id " + gameID + ").");
    }

    private void listGames() throws Exception {
        lastGames = facade.listGames(currentUser.authToken());
        if (lastGames.isEmpty()) {
            System.out.println("No games found.");
            return;
        }
        System.out.print("Color (WHITE/BLACK): ");
        String color = scanner.nextLine().trim().toUpperCase();
        if (!color.equals("WHITE") && !color.equals("BLACK")) {
            System.out.println("Invalid color.");
            return;
        }

        facade.joinGame(currentUser.authToken(), game.getGameID(), color);
        System.out.println("Joined game '" + game.getGameName() + "' as " + color + ".");

        startGameplay(game);
    }

    private void observeGame() throws Exception {
        if (lastGames.isEmpty()) {
            System.out.println("No games listed. Use 'list' first.");
            return;
        }
        System.out.print("Game number: ");
        int index = parseIntSafe(scanner.nextLine());
        if (index < 1 || index > lastGames.size()) {
            System.out.println("Invalid game number.");
            return;
        }
        GameData game = lastGames.get(index - 1);

        System.out.println("Observing game '" + game.getGameName() + "'.");
        startGameplay(game);
    }

    private void startGameplay(GameData game) throws Exception {
        currentGame = game;
        if (ws != null) {
            ws.close();
        }
        ws = new WebSocketFacade(port, this);
        ws.connect(currentUser.authToken(), game.getGameID());
        gameplayLoop(game.getGameID());
    }

    private void gameplayLoop(int gameID) throws Exception {
        boolean inGame = true;
        while (inGame) {
            System.out.print("\n[Game] Enter command (help, redraw, move, highlight, resign, leave): ");
            String cmd = scanner.nextLine().trim().toLowerCase();

            switch (cmd) {
                case "help" -> printGameHelp();
                case "redraw" -> redrawBoard();
                case "move" -> handleMove(gameID);
                case "highlight" -> handleHighlight();
                case "resign" -> handleResign(gameID);
                case "leave" -> {
                    handleLeave(gameID);
                    inGame = false;
                }
                default -> System.out.println("Unknown command. Type 'help'.");
            }
        }
    }















