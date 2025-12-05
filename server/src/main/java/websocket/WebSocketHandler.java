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

}