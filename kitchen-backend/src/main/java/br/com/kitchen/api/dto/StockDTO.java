package br.com.kitchen.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StockDTO {
    private Long id;
    private String sku;
    private Long sellerId;
    private int soldQuantity;
    private int totalQuantity;
    private int reservedQuantity;
}
