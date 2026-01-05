package br.com.kitchen.notification.domain.factory;

import br.com.kitchen.notification.domain.builder.NotificationBuilder;
import br.com.kitchen.notification.application.dto.OrderDTO;
import br.com.kitchen.notification.application.dto.OrderItemsDTO;
import br.com.kitchen.notification.domain.enums.NotificationType;
import br.com.kitchen.notification.domain.model.Notification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class NotificationFactory {

    public List<Notification> fromOrderEvent(OrderDTO order) {

        List<Notification> notifications = new ArrayList<>();

        switch (order.getStatus()) {

            case "CREATED" -> {

                Map<Long, List<OrderItemsDTO>> itemsBySellerOrderId =
                        order.getItems().stream()
                                .collect(Collectors.groupingBy(OrderItemsDTO::getId));

                for (Map.Entry<Long, List<OrderItemsDTO>> entry : itemsBySellerOrderId.entrySet()) {

                    Long sellerOrderId = entry.getKey();
                    OrderItemsDTO firstItem = entry.getValue().get(0);
                    Long sellerId = firstItem.getShipping()
                            .getSeller()
                            .getId();

                    notifications.add(
                            NotificationBuilder.forSeller(
                                    sellerId,
                                    NotificationType.ORDER_CONFIRMED,
                                    "Novo pedido recebido",
                                    "Você recebeu o pedido #" + sellerOrderId +
                                            " (número do pedido do cliente #" + order.getId() + ")",
                                    sellerOrderId,
                                    order.getId()
                            )
                    );
                }
            }

            case "SHIPPED" -> {
                notifications.add(
                        NotificationBuilder.forCustomer(
                                order.getCustomerId(),
                                NotificationType.ORDER_SHIPPED,
                                "Pedido enviado",
                                "Seu pedido #" + order.getId() + " foi enviado",
                                order.getId()
                        )
                );
            }

            default -> log.debug(
                    "No notification configured for order status {}", order.getStatus()
            );

        }

        return notifications;
    }
}