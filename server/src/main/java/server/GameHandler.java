package server;

import com.google.gson.Gson;
import io.javalin.http.Context;
import service.AuthService;
import model.GameData;
import dataaccess.DataAccessException;
import service.GameService;

import java.util.Map;

public class GameHandler {

    private final GameService gameService;
    private final Gson gson = new Gson();

    public GameHandler(GameService gameService) {
        this.gameService = gameService;
    }

    private record ErrorMessage(String message) {}

    public void listGames(Context ctx) {
        try {
            String authToken = ctx.header("authorization");
            var games = gameService.listGames(authToken);
            ctx.status(200).json(Map.of("games", games));
        } catch (DataAccessException e) {
            ctx.status(401).json(new ErrorMessage(e.getMessage()));
        } catch (Exception e) {
            ctx.status(500).json(new ErrorMessage("Error:" +e.getMessage()));
        }
    }

    public void createGame(Context ctx) {
        String authToken = ctx.header("authorization");

        try {
            if (authToken == null || authToken.isBlank()) {
                throw new DataAccessException("Auth token not found");
            }

            GameData request = gson.fromJson(ctx.body(), GameData.class);

            int gameId = gameService.createGame(authToken, request.getGameName());
            ctx.status(200).json(Map.of("gameID", gameId));

        } catch (DataAccessException e) {
            if(e.getMessage().contains("Auth token")){
                ctx.status(401).json(new ErrorMessage("Error: Auth token not found"));
            } else {
                ctx.status(400).json(new ErrorMessage("Error: " + e.getMessage()));
            }
        } catch (Exception e) {
            ctx.status(500).json(new ErrorMessage("Error:" +e.getMessage()));
        }
    }

    public void joinGame(Context ctx) {
            String authToken = ctx.header("authorization");
            if (authToken == null || authToken.isBlank()) {
                ctx.status(401).json(new ErrorMessage("Error: Auth token not found"));
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
                if (e.getMessage().contains("Auth token")) {
                    ctx.status(401).json(new ErrorMessage("Error: Auth token not found"));
                } else if ("Error: already taken".equals(e.getMessage())) {
                    ctx.status(403).json(new ErrorMessage(e.getMessage()));
                } else {
                    ctx.status(400).json(new ErrorMessage("Error: " + e.getMessage()));
                }
            } catch (Exception e) {
                ctx.status(500).json(new ErrorMessage("Error:" + e.getMessage()));
            }
    }

    private record JoinGameRequest(int gameID, String playerColor) {

    }


}
