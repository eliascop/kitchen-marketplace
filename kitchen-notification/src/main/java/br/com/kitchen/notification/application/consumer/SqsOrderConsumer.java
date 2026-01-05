package br.com.kitchen.notification.application.consumer;

import br.com.kitchen.notification.application.dto.OrderDTO;
import br.com.kitchen.notification.application.dto.SnsNotificationDTO;
import br.com.kitchen.notification.domain.factory.NotificationFactory;
import br.com.kitchen.notification.domain.model.Notification;
import br.com.kitchen.notification.application.service.NotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class SqsOrderConsumer {

    private final ObjectMapper mapper;
    private final NotificationFactory notificationFactory;
    private final NotificationService notificationService;

    @SqsListener("order-customer-queue")
    public void listenOrderCustomerQueue(SnsNotificationDTO notification) {
        try {
            OrderDTO orderDTO = mapper.readValue(notification.getMessage(), OrderDTO.class);
            log.info("Your order was created: {}", orderDTO);

//            OrderWebSocket.notifyOrderUpdate(orderDTO); notificar o cliente que o pedido foi enviado

        } catch (Exception e) {
            log.error("An error has occurred on deserialize the message: {}", e.getMessage());
        }
    }

    @SqsListener("order-seller-queue")
    public void listenOrderSellerQueue(SnsNotificationDTO notification) {

        try {
            OrderDTO orderDTO =
                    mapper.readValue(notification.getMessage(), OrderDTO.class);

            List<Notification> notifications =
                    notificationFactory.fromOrderEvent(orderDTO);

            if (!notifications.isEmpty()) {
                notificationService.saveAll(notifications);
            }

            log.info("A new order was created: {}",orderDTO.toString());

        } catch (Exception e) {
            log.error("Failed to process order event. payload={}", e.getMessage());
        }
    }
}