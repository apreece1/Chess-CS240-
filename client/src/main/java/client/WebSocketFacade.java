package client;

import chess.ChessMove;
import com.google.gson.Gson;
import jakarta.websocket.ClientEndpointConfig;
import jakarta.websocket.Endpoint;
import jakarta.websocket.EndpointConfig;
import jakarta.websocket.MessageHandler;
import jakarta.websocket.Session;
import model.GameData;
import org.glassfish.tyrus.client.ClientManager;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.net.URI;

public class WebSocketFacade {

    private final String wsUrl;
    private final GameplayObserver observer;
    private final Gson gson = new Gson();
    private Session session;

    public WebSocketFacade(int port, GameplayObserver observer) {
        this.wsUrl = "ws://localhost:" + port + "/ws";
        this.observer = observer;
    }


}