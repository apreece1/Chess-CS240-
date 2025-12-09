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

    
