package br.com.kitchen.api.dto.search;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
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
    private BigDecimal basePrice;
    private String imageUrl;

    private Long sellerId;
    private String sellerName;

    private Long catalogId;
    private String catalogName;

    private Long categoryId;
    private String categoryName;

    private OffsetDateTime createdAt;
    private OffsetDateTime activatedAt;
    private String productStatus;

    private List<ProductSkuSearchDocumentDTO> skus;
}
