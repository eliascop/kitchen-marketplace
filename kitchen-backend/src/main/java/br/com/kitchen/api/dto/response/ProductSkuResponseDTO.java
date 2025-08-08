package br.com.kitchen.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductSkuResponseDTO {
    private Long id;
    private String sku;
    private BigDecimal price;
    private StockResponseDTO stock;
    private List<ProductAttributeResponseDTO> attributes;
}