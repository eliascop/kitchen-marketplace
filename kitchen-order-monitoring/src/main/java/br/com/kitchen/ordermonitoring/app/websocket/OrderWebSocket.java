package br.com.kitchen.ordermonitoring.app.websocket;

import br.com.kitchen.ordermonitoring.app.dto.OrderDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
@ServerEndpoint("/ws/orders/v1/{orderId}")
@Slf4j
public class OrderWebSocket {

    private static final Map<String, Set<Session>> sessions = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("orderId") String orderId) {
        sessions.computeIfAbsent(orderId, k -> new CopyOnWriteArraySet<>()).add(session);
        log.info("Client connected on order: {}",orderId);
    }

    @OnMessage
    public void onMessage(String message, Session session) throws IOException {
        log.info("Received message from client: {}",message);
        session.getBasicRemote().sendText("Echo: " + message);
    }

    @OnClose
    public void onClose(Session session, @PathParam("orderId") String orderId) {
        Set<Session> orderSessions = sessions.get(orderId);
        if (orderSessions != null) {
            orderSessions.remove(session);
            if (orderSessions.isEmpty()) {
                sessions.remove(orderId);
            }
        }
        log.info("Client disconnected from order: {}", orderId);
    }

    public static void sendToOrder(String orderId, String message) {
        Set<Session> orderSessions = sessions.get(orderId);
        if (orderSessions != null) {
            for (Session session: orderSessions) {
                if (session.isOpen()) {
                    try {
                        session.getBasicRemote().sendText(message);
                    } catch (IOException e) {
                        log.error("An error occurred on send message: {}", e.getMessage());
                    }
                }
            }
        }
    }

    public static void notifyOrderUpdate(OrderDTO orderDTO) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            String message = mapper.writeValueAsString(orderDTO);
            sendToOrder(orderDTO.getId().toString(), message);
            AllOrdersWebSocket.notifyAll(message);
        } catch (JsonProcessingException e) {
            log.error("An error has occurred on sending the status: {}, error: {}", orderDTO.toString(), e.getMessage());
        }
    }
}
