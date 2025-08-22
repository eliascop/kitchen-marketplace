package br.com.kitchen.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemsResponseDTO {
    private Long id;
    private ProductResponseDTO product;
    public int quantity;
    private BigDecimal itemValue;
}
