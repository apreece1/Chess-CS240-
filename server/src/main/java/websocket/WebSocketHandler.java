import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsMessageContext;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsErrorContext;
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

    public void onConnect(WsConnectContext ctx) {
        System.out.println("[WS CONNECT] " + ctx.sessionId());
    }


    public void onMessage(WsMessageContext ctx) {
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
            sendError(ctx, "Error: " + e.getMessage());
        }
    }


}