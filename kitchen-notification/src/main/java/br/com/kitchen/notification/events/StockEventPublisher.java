package br.com.kitchen.notification.events;

import br.com.kitchen.notification.application.dto.StockDTO;
import br.com.kitchen.notification.infrastructure.messaging.EventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StockEventPublisher {

    @Value("${stock.events.topic}")
    private String topicArn;

    private final EventPublisher eventPublisher;

    public void publishStockUpdated(StockDTO event) {
        eventPublisher.publish(topicArn, event);
    }
}