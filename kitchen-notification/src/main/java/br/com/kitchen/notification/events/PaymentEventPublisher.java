package br.com.kitchen.notification.events;

import br.com.kitchen.notification.application.dto.PaymentNotificationDTO;
import br.com.kitchen.notification.infrastructure.messaging.EventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentEventPublisher {

    @Value("${payment.events.topic}")
    private String topicArn;

    private final EventPublisher eventPublisher;

    public void publishPaymentConfirmed(PaymentNotificationDTO event) {
        eventPublisher.publish(topicArn, event);
    }
}