package br.com.kitchen.notification.application.service;

import br.com.kitchen.notification.application.dto.PayPalWebhookDTO;
import br.com.kitchen.notification.application.dto.PaymentNotificationDTO;
import br.com.kitchen.notification.application.mapper.PaymentMapper;
import br.com.kitchen.notification.events.EventsPublisherFacade;
import org.springframework.stereotype.Service;

@Service
public class WebhookService {
    private final EventsPublisherFacade eventsPublisher;

    public WebhookService(EventsPublisherFacade eventsPublisher) {
        this.eventsPublisher = eventsPublisher;
    }

    public void processWebhook(PayPalWebhookDTO webhookDTO) {
        if(webhookDTO.getResource() == null)
            throw new IllegalArgumentException("No resource founded in webhook--Paypal");

        if(webhookDTO.getResource().getCartId().isEmpty() || webhookDTO.getResource().getStatus().isEmpty())
            throw new IllegalArgumentException("Status or cartId cannot be empty");

        PaymentNotificationDTO paymentDTO = PaymentMapper.toDTO(webhookDTO);
        eventsPublisher.paymentConfirmed(paymentDTO);
    }
}
