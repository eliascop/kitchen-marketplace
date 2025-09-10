package br.com.kitchen.ordermonitoring.app.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StockDTO {
    private Long id;
    private String sku;
    private String stockAction;
    private Long sellerId;
    private int soldQuantity;
}
