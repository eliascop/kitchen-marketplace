package br.com.kitchen.ordermonitoring.app.consumer;

import br.com.kitchen.ordermonitoring.app.dto.OrderDTO;
import br.com.kitchen.ordermonitoring.app.dto.SnsNotification;
import br.com.kitchen.ordermonitoring.app.dto.StockDTO;
import br.com.kitchen.ordermonitoring.app.websocket.OrderWebSocket;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SqsOrderConsumer {

    private final ObjectMapper mapper;

    @SqsListener("order-queue")
    public void listenOrderQueue(String message) {
        try {
            SnsNotification notification = mapper.readValue(message, SnsNotification.class);
            OrderDTO orderDTO = mapper.readValue(notification.getMessage(), OrderDTO.class);
            log.info("Order received: {}", orderDTO);

            OrderWebSocket.notifyOrderUpdate(orderDTO);

        } catch (Exception e) {
            log.error("An error has occurred on deserialize the message: {}", e.getMessage());
        }
    }

    @SqsListener("stock-queue")
    public void listenStockQueue(String message) {
        try {
            SnsNotification notification = mapper.readValue(message, SnsNotification.class);
            StockDTO stockDTO = mapper.readValue(notification.getMessage(), StockDTO.class);
            log.info("Stock received: {}", stockDTO);

        } catch (Exception e) {
            log.error("An error has occurred on deserialize: {}", e.getMessage());
        }
    }

}
