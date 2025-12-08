package br.com.kitchen.api.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockRequestDTO {
    private int totalQuantity;
    private int reservedQuantity;
    private int soldQuantity;
}