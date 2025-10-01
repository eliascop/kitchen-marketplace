package br.com.kitchen.api.consumer;

import br.com.kitchen.api.dto.PaymentNotificationDTO;
import br.com.kitchen.api.dto.SnsNotificationDTO;
import br.com.kitchen.api.service.PaymentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SqsPaymentConsumer {

    private final ObjectMapper mapper;
    private final PaymentService paymentService;

    @SqsListener("payment-queue")
    public void listenPaymentQueue(String message) {
        try {
            SnsNotificationDTO notification = mapper.readValue(message, SnsNotificationDTO.class);
            PaymentNotificationDTO paymentNotification = mapper.readValue(notification.getMessage(), PaymentNotificationDTO.class);
            paymentService.processPayment(paymentNotification);
        } catch (Exception e) {
            log.error("An error has occurred on deserialize the message: {}", e.getMessage());
        }
    }

}
