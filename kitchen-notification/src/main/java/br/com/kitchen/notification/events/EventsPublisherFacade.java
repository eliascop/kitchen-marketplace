package br.com.kitchen.notification.events;

import br.com.kitchen.notification.application.dto.OrderDTO;
import br.com.kitchen.notification.application.dto.PaymentNotificationDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventsPublisherFacade {

    private final OrderEventPublisher orderPublisher;
    private final PaymentEventPublisher paymentPublisher;
    private final ProductEventPublisher productPublisher;
    private final StockEventPublisher stockPublisher;

    public void orderCreated(OrderDTO event) {
        orderPublisher.publishOrderCreated(event);
    }

    public void paymentConfirmed(PaymentNotificationDTO event) {
        paymentPublisher.publishPaymentConfirmed(event);
    }
}