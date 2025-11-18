package server;

import com.google.gson.Gson;
import io.javalin.http.Context;
import model.GameData;
import dataaccess.DataAccessException;
import service.GameService;
import java.sql.SQLException;
import java.util.Map;

public class GameHandler {

    private final GameService gameService;
    private final Gson gson = new Gson();

    public GameHandler(GameService gameService) {
        this.gameService = gameService;
    }

    private boolean isDatabaseFailure(DataAccessException e) {
        return e.getCause() instanceof SQLException
                || (e.getMessage() != null && e.getMessage().toLowerCase().contains("failed to get connection"));
    }

    private record ErrorMessage(String message) {}

    public void listGames(Context ctx) {
        try {
            String authToken = ctx.header("Authorization");
            if (authToken == null || authToken.isBlank()) {
                ctx.status(401).json(new ErrorMessage("Error: unauthorized"));
                return;
            }
            var games = gameService.listGames(authToken);
            ctx.status(200).json(Map.of("games", games));
        } catch (DataAccessException e) {

            String msg = e.getMessage();
            String lower = (msg == null) ? "" : msg.toLowerCase();



            if (isDatabaseFailure(e)) {
                ctx.status(500).json(new ErrorMessage("Error: internal server error"));
            }else if (lower.contains("unauthorized") || lower.contains("auth token")) {
                    ctx.status(401).json(new ErrorMessage("Error: unauthorized"));
                } else {
                    ctx.status(500).json(new ErrorMessage("Error: internal server error"));
                }
        } catch (Exception e) {
            ctx.status(500).json(new ErrorMessage("Error:" + e.getMessage()));
        }
    }


    public void createGame(Context ctx) {
        String authToken = ctx.header("Authorization");

        try {
            if (authToken == null || authToken.isBlank()) {
                ctx.status(401).json(new ErrorMessage("Error: unauthorized"));
                return;
            }

            GameData request = gson.fromJson(ctx.body(), GameData.class);

            int gameId = gameService.createGame(authToken, request.getGameName());
            ctx.status(200).json(Map.of("gameID", gameId));

        } catch (DataAccessException e) {
            String msg = e.getMessage();
            String lower = (msg == null) ? "" : msg.toLowerCase();

            if (lower.contains("unauthorized") || lower.contains("auth token")) {
                ctx.status(401).json(new ErrorMessage("Error: unauthorized"));
            } else if (isDatabaseFailure(e)) {
                ctx.status(500).json(new ErrorMessage("Error: internal server error"));
            } else {
                // logic errors -> 400
                ctx.status(400).json(new ErrorMessage("Error: " + msg));
            }
        } catch (Exception e) {
            ctx.status(500).json(new ErrorMessage("Error: " + e.getMessage()));
        }
    }

    public void joinGame(Context ctx) {
            String authToken = ctx.header("Authorization");
            if (authToken == null || authToken.isBlank()) {
                ctx.status(401).json(new ErrorMessage("Error: unauthorized"));
                return;
            }

            JoinGameRequest request;
            try {
                request = gson.fromJson(ctx.body(), JoinGameRequest.class);
            } catch (Exception e) {
                ctx.status(400).json(new ErrorMessage("Error: bad request"));
                return;
            }


            String playerColor = request.playerColor();
            if (playerColor == null || playerColor.isBlank()) {
                ctx.status(400).json(new ErrorMessage("Error: bad request"));
                return;
            }

            try{
                gameService.joinGame(authToken, request.gameID(), request.playerColor());
                ctx.status(200).json(Map.of());
            } catch (DataAccessException e) {
                String msg = e.getMessage();
                String lower = (msg == null) ? "" : msg.toLowerCase();

                if (lower.contains("unauthorized") || lower.contains("auth token")) {
                    ctx.status(401).json(new ErrorMessage("Error: unauthorized"));
                } else if ("Error: already taken".equals(msg)) {
                    ctx.status(403).json(new ErrorMessage(msg));
                } else if (isDatabaseFailure(e)) {
                    ctx.status(500).json(new ErrorMessage("Error: internal server error"));
                } else {
                    // remaining logic errors (bad gameID, etc.) can still be 400
                    ctx.status(400).json(new ErrorMessage("Error: " + msg));
                }
            }

    }

    private record JoinGameRequest(int gameID, String playerColor) {

    }


}
