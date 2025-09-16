package br.com.kitchen.ordermonitoring.app.websocket;

import jakarta.websocket.OnClose;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
@ServerEndpoint("/ws/orders/v1")
@Slf4j
public class AllOrdersWebSocket {

    private static final Set<Session> sessions = new CopyOnWriteArraySet<>();

    @OnOpen
    public void onOpen(Session session) {
        sessions.add(session);
        log.info("A client was connected on order global webSocket");
    }

    @OnClose
    public void onClose(Session session) {
        sessions.remove(session);
        log.info("A client was disconnected on global webSocket");
    }

    public static void notifyAll(String message) {
        for (Session session: sessions) {
            if (session.isOpen()) {
                try {
                    session.getBasicRemote().sendText(message);
                    log.info("A notification was sent. Messsage: {}", message);
                } catch (IOException e) {
                    log.error("An error occurred on send message to painel: {}", e.getMessage());
                }
            }
        }
    }
}
