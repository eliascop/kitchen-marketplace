package br.com.kitchen.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockDTO {
    private Long id;
    private String sku;
    private Long sellerId;
    private int soldQuantity;
    private int totalQuantity;
    private int reservedQuantity;
}
