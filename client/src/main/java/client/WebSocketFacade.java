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

    public void connect(String authToken, int gameID) throws Exception {
        if (session != null && session.isOpen()) {
            return;
        }

        ClientManager client = ClientManager.createClient();

        session = client.connectToServer(new Endpoint() {
            @Override
            public void onOpen(Session session, EndpointConfig config) {
                session.addMessageHandler((MessageHandler.Whole<String>) WebSocketFacade.this::handleMessage);
            }, ClientEndpointConfig.Builder.create().build(), URI.create(wsUrl));

            UserGameCommand cmd = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID);
            sendCommand(cmd);
        }

        public void makeMove(String authToken, int gameID, ChessMove move) throws IOException {
            UserGameCommand cmd = new UserGameCommand(UserGameCommand.CommandType.MAKE_MOVE, authToken, gameID);
            cmd.setMove(move);
            sendCommand(cmd);
        }


}