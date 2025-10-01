package br.com.kitchen.api.dto;

import br.com.kitchen.api.dto.response.StockResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductSkuDTO {
    private Long id;
    private String sku;
    private BigDecimal price;
    private ProductDTO product;
    private StockResponseDTO stock;
    private List<StockHistoryDTO> stockHistory = new ArrayList<>();
    private List<ProductAttributeDTO> attributes = new ArrayList<>();
}