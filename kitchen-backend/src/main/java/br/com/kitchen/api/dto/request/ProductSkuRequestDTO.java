package br.com.kitchen.api.dto.request;

import br.com.kitchen.api.dto.response.StockResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ProductSkuRequestDTO {
    private Long id;
    private String sku;
    private BigDecimal price;
    private StockResponseDTO stock;
}