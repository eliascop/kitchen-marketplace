package br.com.kitchen.notification.events;

import br.com.kitchen.notification.application.dto.ProductDTO;
import br.com.kitchen.notification.infrastructure.messaging.EventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ProductEventPublisher {

    @Value("${product.events.topic}")
    private String topicArn;

    private final EventPublisher eventPublisher;

    public void publishPaymentConfirmed(ProductDTO event) {
        eventPublisher.publish(topicArn, event);
    }
}
