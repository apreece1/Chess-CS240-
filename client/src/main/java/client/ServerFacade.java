package client;

import com.google.gson.Gson;
import model.AuthData;
import java.util.List;
import model.GameData;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class ServerFacade {

    private final String baseUrl;
    private final Gson gson = new Gson();

    public ServerFacade(int port) {
        this.baseUrl = "http://localhost:" + port;
    }

    public AuthData register(String username, String password, String email) throws Exception {
        var body = Map.of(
                "username", username,
                "password", password,
                "email", email
        );
        return makeRequest("POST", "/user", body, AuthData.class, null);
    }

    public AuthData login(String username, String password) throws Exception {
        var body = Map.of(
                "username", username,
                "password", password
        );
        return makeRequest("POST", "/session", body, AuthData.class, null);
    }

    public void logout(String authToken) throws Exception {
        makeRequest("DELETE", "/session", null, Void.class, authToken);
    }

    public int createGame(String authToken, String gameName) throws Exception {
        var body = Map.of("gameName", gameName);
        Map<?,?> resp = makeRequest("POST", "/game", body, Map.class, authToken);
        return ((Number) resp.get("gameID")).intValue();
    }

    public void joinGame(String authToken, int gameID, String playerColor) throws Exception {
        Object body = Map.of(
                "gameID", gameID,
                "playerColor", playerColor
        );
        makeRequest("PUT", "/game", body, Void.class, authToken);
    }

    public void clear() throws Exception {
        makeRequest("DELETE", "/db", null, Void.class, null);
    }

    private <T> T makeRequest(String method, String path,
                              Object requestBody,
                              Class<T> responseType,
                              String authToken) throws Exception {

        var url = new URL(baseUrl + path);
        var connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(method);
        connection.setDoInput(true);
        connection.setRequestProperty("Accept", "application/json");
        connection.setRequestProperty("Content-Type", "application/json");

        if (authToken != null) {
            connection.setRequestProperty("authorization", authToken);
        }

        if (requestBody != null) {
            connection.setDoOutput(true);
            try (var out = new OutputStreamWriter(connection.getOutputStream())) {
                out.write(gson.toJson(requestBody));
            }
        }

        int status = connection.getResponseCode();

        InputStream stream = (status >= 200 && status < 300)
                ? connection.getInputStream()
                : connection.getErrorStream();


        String responseJson = "";
        if (stream != null) {
            try (var reader = new InputStreamReader(stream)) {
                var sb = new StringBuilder();
                char[] buf = new char[1024];
                int len;
                while ((len = reader.read(buf)) != -1) {
                    sb.append(buf, 0, len);
                }
                responseJson = sb.toString();
            }
        }

        if (status >= 200 && status < 300) {
            if (responseType == Void.class || responseJson.isBlank()) {
                return null;
            }
            return gson.fromJson(responseJson, responseType);
        } else {
            throw new Exception("HTTP " + status + ": " + responseJson);
        }
    }

    public List<GameData> listGames(String authToken) throws Exception {
        Map<?, ?> resp = makeRequest("GET", "/game", null, Map.class, authToken);
        var jsonElement = gson.toJsonTree(resp.get("games"));
        GameData[] games = gson.fromJson(jsonElement, GameData[].class);
        return List.of(games);
    }

}