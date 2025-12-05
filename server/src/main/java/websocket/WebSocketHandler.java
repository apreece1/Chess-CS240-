package websocket;

import com.google.gson.Gson;
import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import websocket.commands.UserGameCommand;


@ServerEndpoint("/ws")
public class WebSocketHandler {

    private static final Gson gson = new Gson();

    @OnOpen
    public void onOpen(Session session) {
        System.out.println("[WS OPEN] " + session.getId());
    }

    @OnMessage
    public void onMessage(Session session, String message) {
        System.out.println("[WS IN] " + message);

}

