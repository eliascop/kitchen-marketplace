package br.com.kitchen.ordermonitoring.app.consumer;

import br.com.kitchen.ordermonitoring.app.dto.OrderDTO;
import br.com.kitchen.ordermonitoring.app.dto.SnsNotification;
import br.com.kitchen.ordermonitoring.app.websocket.OrderWebSocket;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SqsOrderConsumer {

    private final ObjectMapper mapper;

    @SqsListener("order-queue")
    public void listenNewOrder(String message) {
        try {
            SnsNotification notification = mapper.readValue(message, SnsNotification.class);
            OrderDTO orderDTO = mapper.readValue(notification.getMessage(), OrderDTO.class);
            System.out.println("Order received: " + orderDTO);

            OrderWebSocket.notifyOrderUpdate(orderDTO);

        } catch (Exception e) {
            System.err.println("Error on deserializer: " + e.getMessage());
        }
    }

}
