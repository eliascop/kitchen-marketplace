package br.com.kitchen.notification.events;

import br.com.kitchen.notification.application.dto.OrderDTO;
import br.com.kitchen.notification.infrastructure.messaging.EventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderEventPublisher {

    @Value("${order.events.topic}")
    private String topicArn;

    private final EventPublisher eventPublisher;

    public void publishOrderCreated(OrderDTO event) {
        eventPublisher.publish(topicArn, event);
    }
}
