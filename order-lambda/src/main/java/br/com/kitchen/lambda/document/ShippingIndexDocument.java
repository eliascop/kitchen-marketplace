package br.com.kitchen.lambda.document;

import br.com.kitchen.lambda.dto.ShippingDTO;

import java.math.BigDecimal;

public record ShippingIndexDocument(
        Long id,
        String carrier,
        String method,
        BigDecimal cost,
        Long estimatedDays,
        SellerIndexDocument seller
) {
    public static ShippingIndexDocument from(ShippingDTO shippingDTO){
        return new ShippingIndexDocument(
                shippingDTO.getId(),
                shippingDTO.getCarrier(),
                shippingDTO.getMethod(),
                shippingDTO.getCost(),
                shippingDTO.getEstimatedDays(),
                SellerIndexDocument.from(shippingDTO.getSeller())
        );
    }
}
