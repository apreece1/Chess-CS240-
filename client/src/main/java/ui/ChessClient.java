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


}
