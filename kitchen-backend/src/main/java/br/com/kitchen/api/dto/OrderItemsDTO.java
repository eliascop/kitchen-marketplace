package br.com.kitchen.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemsDTO {
    private Long id;
    private String sku;
    private BigDecimal price;
    private String productName;
    public int quantity;
    private BigDecimal itemValue;
    private ShippingDTO shipping;
}
