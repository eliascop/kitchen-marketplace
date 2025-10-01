package br.com.kitchen.api.util;

import br.com.kitchen.api.enumerations.OrderStatus;
import br.com.kitchen.api.model.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class OrderTestBuilder {

    public static Order buildValidOrder(User user, Seller seller) {
        Product product = ProductTestBuilder.buildDefaultProduct();

        Order order = new Order();
        order.setUser(user);
        order.setCreation(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING_PROCESSING);
        order.setTotal(BigDecimal.valueOf(100));

        OrderItems item = new OrderItems();
       // OrderItems item = new OrderItems(order, product, 2, seller);
        order.getOrderItems().add(item);

        return order;
    }
}
