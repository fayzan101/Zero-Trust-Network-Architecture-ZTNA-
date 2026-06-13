package com.yourname.zerotrust.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class SecurityWebSocketHandler extends TextWebSocketHandler {

    @Autowired
    private SecurityEventBroadcaster broadcaster;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        broadcaster.register(session);
        try {
            session.sendMessage(new TextMessage(
                    "{\"type\":\"CONNECTED\",\"details\":\"ZTNA security dashboard connected\"}"));
        } catch (Exception ignored) {
            // client may disconnect immediately
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        broadcaster.unregister(session);
    }
}
