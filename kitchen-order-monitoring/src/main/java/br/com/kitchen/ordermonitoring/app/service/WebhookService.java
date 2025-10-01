package br.com.kitchen.ordermonitoring.app.service;

import br.com.kitchen.ordermonitoring.app.dto.PayPalWebhookDTO;
import br.com.kitchen.ordermonitoring.app.dto.PaymentNotificationDTO;
import br.com.kitchen.ordermonitoring.app.mapper.PaymentMapper;
import br.com.kitchen.ordermonitoring.app.producer.SnsProducer;
import org.springframework.stereotype.Service;

@Service
public class WebhookService {
    private final SnsProducer snsProducer;

    public WebhookService(SnsProducer snsProducer) {
        this.snsProducer = snsProducer;
    }

    public void processWebhook(PayPalWebhookDTO webhookDTO) {
        if(webhookDTO.getResource() == null)
            throw new IllegalArgumentException("No resource founded in webhook--Paypal");

        if(webhookDTO.getResource().getCartId().isEmpty() || webhookDTO.getResource().getStatus().isEmpty())
            throw new IllegalArgumentException("Status or cartId cannot be empty");

        PaymentNotificationDTO paymentDTO = PaymentMapper.toDTO(webhookDTO);
        snsProducer.publishToTopic(paymentDTO);
    }
}
