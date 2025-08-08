package br.com.kitchen.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockResponseDTO {
    private int totalQuantity;
    private int reservedQuantity;
    private int soldQuantity;
    private int availableQuantity;
}
