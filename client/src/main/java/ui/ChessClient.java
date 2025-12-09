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


