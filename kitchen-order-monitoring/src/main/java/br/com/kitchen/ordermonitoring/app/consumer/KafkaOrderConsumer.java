package br.com.kitchen.ordermonitoring.app.consumer;

import br.com.kitchen.ordermonitoring.app.dto.OrderDTO;
import br.com.kitchen.ordermonitoring.app.websocket.OrderWebSocket;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaOrderConsumer extends KafkaGenericConsumer<OrderDTO> {

    public KafkaOrderConsumer() {
        super(OrderDTO.class, OrderWebSocket::notifyOrderUpdate);
    }

    @KafkaListener(topics = "order-status-updates", groupId = "order-monitoring-consumer-order-update")
    public void listenOrderUpdates(String message) {consume(message);}

    @Override
    protected boolean isValid(OrderDTO orderDTO) {
        return orderDTO.getId() != null && orderDTO.getStatus() != null && !orderDTO.getStatus().isEmpty();
    }
}
