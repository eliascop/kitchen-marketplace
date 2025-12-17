package br.com.kitchen.indexation.dto;

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
    private StockDTO stock;
    private List<StockHistoryDTO> stockHistory = new ArrayList<>();
    private List<ProductAttributeDTO> attributes = new ArrayList<>();
}