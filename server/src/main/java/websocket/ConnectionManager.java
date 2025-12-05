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
        c.ctx = ctx;
        list.add(c);

        ctx.attribute("gameId", gameId);
        ctx.attribute("username", username);
    }

    public void removeConnection(WsContext ctx) {
        Integer gameId = ctx.attribute("gameId");
        if (gameId == null) return;
        var list = connections.get(gameId);
        if (list == null) return;
        list.removeIf(c -> c.ctx.sessionId().equals(ctx.sessionId()));
    }

    public List<WsContext> getAllInGame(int gameId) {
        var list = connections.get(gameId);
        if (list == null) return List.of();
        return list.stream().map(c -> c.ctx).toList();
    }

    public List<WsContext> getOthersInGame(int gameId, WsContext exclude) {
        var list = connections.get(gameId);
        if (list == null) return List.of();
        return list.stream()
                .map(c -> c.ctx)
                .filter(ctx -> !ctx.sessionId().equals(exclude.sessionId()))
                .toList();
    }
}
