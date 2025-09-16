package br.com.kitchen.api.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StockHistoryDTO {
    private Long id;
    private String sku;
    private Long sellerId;
    private Integer soldQuantity;
    private Integer reservedQuantity;
    private Integer totalQuantity;
    private String createdAt;
    private String eventType;
}
