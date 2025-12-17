package br.com.kitchen.indexation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockDTO {
    private int id;
    private int totalQuantity;
    private int reservedQuantity;
    private int soldQuantity;
}
