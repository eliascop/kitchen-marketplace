package br.com.kitchen.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductDTO {
    private Long id;
    private String name;
    private String description;
    private String imageUrl;
    private BigDecimal basePrice;
    private CatalogDTO catalog;
    private CategoryDTO category;
    private String productStatus;
    private SellerDTO seller;
    private LocalDateTime createdAt;
    private LocalDateTime activatedAt;
    private List<ProductSkuDTO> skus;
}