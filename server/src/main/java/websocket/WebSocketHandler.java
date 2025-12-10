package websocket;

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
        ctx.enableAutomaticPings();
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

    public void onClose(WsCloseContext ctx) {
        connections.removeConnection(ctx);
        System.out.println("[WS CLOSE] " + ctx.sessionId());
    }

    public void onError(WsErrorContext ctx) {
        System.out.println("[WS ERROR] " + ctx.sessionId());
    }

    private void handleConnect(WsContext ctx, UserGameCommand cmd) {
        try {
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

        } catch (DataAccessException e) {
            sendError(ctx, e.getMessage());
        }
    }

    private void handleMakeMove(WsContext ctx, UserGameCommand cmd) {
        try {
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

            try {
                gameService.makeMove(cmd.getGameID(), username, move);
            } catch (DataAccessException e) {
                sendError(ctx, e.getMessage());
                return;
            }

            gameData = gameService.getGame(cmd.getGameID());
            ChessGame game = gameData.getGame();

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

            boolean whiteInCheck = game.isInCheck(ChessGame.TeamColor.WHITE);
            boolean blackInCheck = game.isInCheck(ChessGame.TeamColor.BLACK);
            boolean whiteCheckmated = game.isInCheckmate(ChessGame.TeamColor.WHITE);
            boolean blackCheckmated = game.isInCheckmate(ChessGame.TeamColor.BLACK);
            boolean whiteStalemate = game.isInStalemate(ChessGame.TeamColor.WHITE);
            boolean blackStalemate = game.isInStalemate(ChessGame.TeamColor.BLACK);

            String statusText = null;


            if (whiteCheckmated) {
                statusText = "Checkmate! Black wins.";
            } else if (blackCheckmated) {
                statusText = "Checkmate! White wins.";
            }

            else if (whiteStalemate || blackStalemate) {
                statusText = "Stalemate! The game is a draw.";
            }

            else if (whiteInCheck) {
                statusText = "White is in check.";
            } else if (blackInCheck) {
                statusText = "Black is in check.";
            }

            if (statusText != null) {
                ServerMessage status = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
                status.setMessage(statusText);
                for (var c : connections.getAllInGame(cmd.getGameID())) {
                    send(c, status);
                }
            }


        } catch (DataAccessException e) {
            sendError(ctx, e.getMessage());
        }
    }

    private void handleLeave(WsContext ctx, UserGameCommand cmd) {
        try {
            var auth = authService.getAuth(cmd.getAuthToken());
            if (auth == null) {
                sendError(ctx, "Error: invalid auth token");
                return;
            }

            String username = auth.username();
            int gameID = cmd.getGameID();

            gameService.removePlayerFromGame(gameID, username);

            connections.removeConnection(ctx);

            ServerMessage note = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
            note.setMessage(username + " left the game");

            for (var ctx2 : connections.getAllInGame(gameID)) {
                send(ctx2, note);
            }

        } catch (DataAccessException e) {
            sendError(ctx, e.getMessage());
        }
    }


    private void handleResign(WsContext ctx, UserGameCommand cmd) {
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

        } catch (DataAccessException e) {
            sendError(ctx, e.getMessage());
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
