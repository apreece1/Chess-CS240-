package websocket;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import io.javalin.websocket.WsContext;
import service.AuthService;
import service.GameService;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

public class WebSocketHandler {

    private final AuthService authService;
    private final GameService gameService;
    private final ConnectionManager connections;
    private final Gson gson = new Gson();

    public WebSocketHandler(AuthService authService,
                            GameService gameService,
                            ConnectionManager connections) {
        this.authService = authService;
        this.gameService = gameService;
        this.connections = connections;
    }

    public void onConnect(WsContext ctx) {
        System.out.println("[WS CONNECT] " + ctx.sessionId());
    }

    public void onMessage(WsContext ctx) {
        try {
            String rawJson = ctx.message();
            UserGameCommand command = gson.fromJson(rawJson, UserGameCommand.class);

            switch (command.getCommandType()) {
                case CONNECT -> handleConnect(ctx, command);
                case MAKE_MOVE -> handleMakeMove(ctx, command);
                case LEAVE -> handleLeave(ctx, command);
                case RESIGN -> handleResign(ctx, command);
            }

        } catch (Exception e) {
            e.printStackTrace();
            sendError(ctx, "Error: " + e.getMessage());
        }
    }

    public void onClose(WsContext ctx) {
        connections.removeConnection(ctx);
    }

    public void onError(WsContext ctx) {
        System.out.println("WebSocket error: " + ctx.error());
    }








}