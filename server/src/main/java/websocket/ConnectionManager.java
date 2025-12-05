package websocket;

import io.javalin.websocket.WsContext;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {

    private static class Connection {
        int gameId;
        String username;
        WsContext ctx;
    }

    private final Map<Integer, List<Connection>> connections = new ConcurrentHashMap<>();

    public void addConnection(int gameId, String username, WsContext ctx) {
        var list = connections.computeIfAbsent(gameId, k -> new ArrayList<>());
        Connection c = new Connection();
        c.gameId = gameId;
        c.username = username;
        c.


}
