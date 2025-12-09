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
        int i = 1;
        for (GameData g : lastGames) {
            String white = g.getWhiteUsername() == null ? "-" : g.getWhiteUsername();
            String black = g.getBlackUsername() == null ? "-" : g.getBlackUsername();
            System.out.printf("%d. %s (white: %s, black: %s)%n", i++, g.getGameName(), white, black);
        }
    }

    private void playGame() throws Exception {
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

    private void printGameHelp() {
        System.out.println("""
                Gameplay commands:
                  help      - Show this help
                  redraw    - Redraw the chess board
                  move      - Make a move
                  highlight - Highlight legal moves for a piece
                  resign    - Resign the game
                  leave     - Leave the game and return to post-login menu
                """);
    }



    private void redrawBoard() {
        if (currentGame == null) {
            System.out.println("No game loaded.");
            return;
        }
        boolean whiteOnBottom = true;
        if (currentUser != null &&
                currentGame.getBlackUsername() != null &&
                currentUser.username().equals(currentGame.getBlackUsername())) {
            whiteOnBottom = false;
        }
        BoardPrinter.printGame(currentGame.getGame(), whiteOnBottom);
    }


    private void handleMove(int gameID) throws Exception {
        if (currentGame == null) {
            System.out.println("No game loaded.");
            return;
        }
        System.out.print("From (e.g., e2): ");
        String fromStr = scanner.nextLine().trim();
        System.out.print("To (e.g., e4): ");
        String toStr = scanner.nextLine().trim();

        ChessPosition from = parsePosition(fromStr);
        ChessPosition to = parsePosition(toStr);
        if (from == null || to == null) {
            System.out.println("Invalid coordinates.");
            return;
        }

        ChessMove move = new ChessMove(from, to, null);
        ws.makeMove(currentUser.authToken(), gameID, move);
    }

    private void handleHighlight() {
        if (currentGame == null) {
            System.out.println("No game loaded.");
            return;
        }
        System.out.print("Square to highlight (e.g., e2): ");
        String posStr = scanner.nextLine().trim();
        ChessPosition pos = parsePosition(posStr);
        if (pos == null) {
            System.out.println("Invalid coordinates.");
            return;
        }
        System.out.println("Highlighting not yet implemented with BoardPrinter and currentGame.getGame().");
    }

    private void handleResign(int gameID) throws Exception {
        System.out.print("Are you sure you want to resign? (y/n): ");
        String ans = scanner.nextLine().trim().toLowerCase();
        if (ans.equals("y") || ans.equals("yes")) {
            ws.resign(currentUser.authToken(), gameID);
        } else {
            System.out.println("Resign cancelled.");
        }
    }

    private void handleLeave(int gameID) throws Exception {
        ws.leave(currentUser.authToken(), gameID);
        ws.close();
        ws = null;
        currentGame = null;
        System.out.println("Left game.");
    }

    private ChessPosition parsePosition(String algebraic) {
        if (algebraic == null || algebraic.length() != 2) {
            return null;
        }
        char file = Character.toLowerCase(algebraic.charAt(0));
        char rank = algebraic.charAt(1);
        if (file < 'a' || file > 'h' || rank < '1' || rank > '8') {
            return null;
        }
        int col = file - 'a' + 1;
        int row = rank - '0';
        return new ChessPosition(row, col);
    }

    private int parseIntSafe(String s) {
        try {
            return Integer.parseInt(s.trim());
        } catch (NumberFormatException ex) {
            return -1;
        }
    }

    public static void printGameWithHighlights(ChessGame game,
                                               boolean whitePerspective,
                                               ChessPosition from,
                                               Collection<ChessMove> moves) {
        System.out.println(EscapeSequences.ERASE_SCREEN);
        printBoardWithHighlights(game, whitePerspective, from, moves);
    }






}


