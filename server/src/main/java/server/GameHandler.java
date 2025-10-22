package server;

import com.google.gson.Gson;
import io.javalin.http.Context;
import service.AuthService;
import model.GameData;
import dataaccess.DataAccessException;
import service.GameService;

public class GameHandler {

    private final GameService gameService;
    private final AuthService authService;
    private final Gson gson = new Gson();

    public GameHandler(GameService gameService, AuthService authService) {
        this.gameService = gameService;
        this.authService = authService;
    }

    private record ErrorMessage(String message) {}

    public void listGames(Context ctx) {
        try {
            String authToken = ctx.header("authorization");
            var games = gameService.listGames(authToken);
            ctx.status(200).json(games);
        } catch (DataAccessException e) {
            ctx.status(401).json(new ErrorMessage(e.getMessage()));
        } catch (Exception e) {
            ctx.status(500).json(new ErrorMessage("Error:" +e.getMessage()));
        }
    }

    public void createGame(Context ctx) {
        try {
            String authToken = ctx.header("authorization");
            GameData request = gson.fromJson(ctx.body(), GameData.class);
            int gameId = gameService.createGame(authToken, request.gameName());
            ctx.status(200).json("{\"gameID\":" + gameId + "}");
        } catch (DataAccessException e) {
            ctx.status(400).json(new ErrorMessage(e.getMessage()));
        } catch (Exception e) {
            ctx.status(500).json(new ErrorMessage("Error:" +e.getMessage()));
        }
    }

    public void joinGame(Context ctx) {
        try {
            String authToken = ctx.header("authorization");
            JoinGameRequest request = gson.fromJson(ctx.body(), JoinGameRequest.class);
            gameService.JoinGame(authToken, request.gameID(), request.playerColor());
            ctx.status(200).json("{}");
        } catch (DataAccessException e) {
            ctx.status(400).json(new ErrorMessage(e.getMessage()));
        } catch (Exception e) {
            ctx.status(500).json(new ErrorMessage("Error:" + e.getMessage()));
        }
    }

    private record JoinGameRequest(int gameID, String playerColor) {

    }


}
