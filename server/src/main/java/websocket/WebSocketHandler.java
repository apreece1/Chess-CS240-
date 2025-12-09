package websocket;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
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
        System.out.println("[WS ERROR] " + ctx.sessionId());
    }


    private void handleConnect(WsContext ctx, UserGameCommand cmd) {
        var auth = authService.getAuth(cmd.getAuthToken());
        if (auth == null) {
            sendError(ctx, "Error: invalid auth token");
            return;
        }

        var gameData = gameService.getGame(cmd.getGameID());
        if (gameData == null) {
            sendError(ctx, "Error: game not found");
            return;
        }

        String username = auth.username();

        connections.addConnection(cmd.getGameID(), username, ctx);

        ServerMessage load = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
        load.setGame(gameData);
        send(ctx, load);

        ServerMessage note = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        note.setMessage(username + " connected");

        for (var other : connections.getOthersInGame(cmd.getGameID(), ctx)) {
            send(other, note);
        }
    }

    private void handleMakeMove(WsContext ctx, UserGameCommand cmd) {
        var auth = authService.getAuth(cmd.getAuthToken());
        if (auth == null) {
            sendError(ctx, "Error: invalid auth token");
            return;
        }

        var gameData = gameService.getGame(cmd.getGameID());
        if (gameData == null) {
            sendError(ctx, "Error: game not found");
            return;
        }


        ChessMove move = cmd.getMove();
        String username = auth.username();

        gameService.makeMove(cmd.getGameID(), username, move);

        gameData = gameService.getGame(cmd.getGameID());


        ServerMessage load = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
        load.setGame(gameData);

        for (var ctx2 : connections.getAllInGame(cmd.getGameID())) {
            send(ctx2, load);
        }

        ServerMessage note = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        note.setMessage(username + " made a move");

        for (var ctx2 : connections.getOthersInGame(cmd.getGameID(), ctx)) {
            send(ctx2, note);
        }
    }

    private void handleLeave(WsContext ctx, UserGameCommand cmd) {
        var auth = authService.getAuth(cmd.getAuthToken());
        if (auth == null) return;

        String username = auth.username();
        connections.removeConnection(ctx);

        ServerMessage note = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        note.setMessage(username + " left the game");

        for (var ctx2 : connections.getAllInGame(cmd.getGameID())) {
            send(ctx2, note);
        }
    }

    private void handleResign(io.javalin.websocket.WsContext ctx, UserGameCommand cmd) {
        try {
            var auth = authService.getAuth(cmd.getAuthToken());
            if (auth == null) {
                sendError(ctx, "Error: invalid auth token");
                return;
            }

            int gameID = cmd.getGameID();
            String username = auth.username();

            try {
                gameService.resign(gameID, username);
            } catch (DataAccessException e) {
                sendError(ctx, e.getMessage());
                return;
            }

            ServerMessage note = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
            note.setMessage(username + " resigned");

            for (var other : connections.getAllInGame(gameID)) {
                send(other, note);
            }

        } catch (Exception e) {
            sendError(ctx, "Error: " + e.getMessage());
        }
    }


    private void send(WsContext ctx, ServerMessage msg) {
        ctx.send(gson.toJson(msg));
    }

    private void sendError(WsContext ctx, String text) {
        ServerMessage err = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
        err.setErrorMessage(text);
        send(ctx, err);
    }
}