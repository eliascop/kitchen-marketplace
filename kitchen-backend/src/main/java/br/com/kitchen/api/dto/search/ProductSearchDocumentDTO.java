package br.com.kitchen.api.dto.search;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductSearchDocumentDTO {

    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private String imageUrl;

    private Long sellerId;
    private String sellerName;

    private Long catalogId;
    private String catalogName;

    private Long categoryId;
    private String categoryName;

    private LocalDateTime createdAt;
    private LocalDateTime activatedAt;
    private String productStatus;

    private List<ProductSkuSearchDocumentDTO> skus;
}
