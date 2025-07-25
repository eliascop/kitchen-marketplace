package br.com.kitchen.api.util;

import br.com.kitchen.api.model.*;

import java.math.BigDecimal;
import java.util.Date;

public class OrderTestBuilder {

    public static Order buildValidOrder(User user) {
        Product product = ProductTestBuilder.buildDefaultProduct();

        Order order = new Order();
        order.setUser(user);
        order.setCreation(new Date());
        order.setStatus("PENDING");
        order.setTotal(BigDecimal.valueOf(100));

        OrderItems item = new OrderItems(null, order, product, 1, order.getTotal());
        order.getOrderItems().add(item);

        return order;
    }
}
