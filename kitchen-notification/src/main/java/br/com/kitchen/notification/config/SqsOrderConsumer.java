package br.com.kitchen.notification.config;

import br.com.kitchen.notification.dto.OrderDTO;
import br.com.kitchen.notification.dto.SnsNotificationDTO;
import br.com.kitchen.notification.factory.NotificationFactory;
import br.com.kitchen.notification.model.Notification;
import br.com.kitchen.notification.service.NotificationService;
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

    @SqsListener("order-notification-queue")
    public void consume(SnsNotificationDTO notification) {

        try {

            OrderDTO orderDTO =
                    mapper.readValue(notification.getMessage(), OrderDTO.class);

            List<Notification> notifications =
                    notificationFactory.fromOrderEvent(orderDTO);

            if (!notifications.isEmpty()) {
                notificationService.saveAll(notifications);
            }

            log.info(
                    "Processed order event. orderId={}, status={}, notifications={}",
                    orderDTO.getId(),
                    orderDTO.getStatus(),
                    notifications.size()
            );

        } catch (Exception e) {
            log.error("Failed to process order event. payload={}", notification.getMessage(), e);
        }
    }
}