package br.com.kitchen.lambda.document;

import br.com.kitchen.lambda.dto.OrderDTO;
import br.com.kitchen.lambda.utils.DateUtils;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

public record OrderIndexDocument(
        Long id,
        OffsetDateTime creation,
        String status,
        BigDecimal total,
        List<OrderItemsIndexDocument> items,
        PaymentIndexDocument payment

) {
    public static OrderIndexDocument from(OrderDTO order) {

        List<OrderItemsIndexDocument> itemsDocuments = order.getItems().stream()
                .map(itemsDTO -> new OrderItemsIndexDocument(
                        itemsDTO.getId(),
                        itemsDTO.getSku(),
                        itemsDTO.getPrice(),
                        itemsDTO.getProductName(),
                        itemsDTO.getQuantity(),
                        itemsDTO.getItemValue(),
                        ShippingIndexDocument.from(itemsDTO.getShipping())
                ))
                .toList();

        return new OrderIndexDocument(
                order.getId(),
                DateUtils.toOffset(order.getCreation()),
                order.getStatus(),
                order.getTotal(),
                itemsDocuments,
                PaymentIndexDocument.from(order.getPayment())
        );

    }
}
