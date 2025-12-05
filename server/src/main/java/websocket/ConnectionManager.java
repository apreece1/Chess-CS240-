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


    
}
