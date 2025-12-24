package br.com.kitchen.lambda.document;

import java.math.BigDecimal;

public record OrderItemsIndexDocument(
        Long id,
        String sku,
        BigDecimal price,
        String productName,
        int quantity,
        BigDecimal itemValue,
        ShippingIndexDocument shipping
) {
}
