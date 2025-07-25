package br.com.kitchen.ordermonitoring.app.websocket;

import br.com.kitchen.ordermonitoring.app.dto.OrderDTO;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
@ServerEndpoint("/ws/orders/v1")
public class AllOrdersWebSocket {

    private static final Set<Session> sessions = new CopyOnWriteArraySet<>();

    @OnOpen
    public void onOpen(Session session) {
        sessions.add(session);
        System.out.println("Cliente conectado ao WebSocket global de pedidos");
    }

    @OnClose
    public void onClose(Session session) {
        sessions.remove(session);
        System.out.println("Cliente desconectado do WebSocket global");
    }

    public static void notifyAll(String message) {
        for (Session session : sessions) {
            if (session.isOpen()) {
                try {
                    session.getBasicRemote().sendText(message);
                } catch (IOException e) {
                    System.err.println("Erro ao enviar para o painel: " + e.getMessage());
                }
            }
        }
    }
}
