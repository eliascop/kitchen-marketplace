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

    private String catalog;
    private String category;

    private LocalDateTime createdAt;
    private LocalDateTime activatedAt;
    private Boolean active;

    private List<ProductSkuSearchDocumentDTO> skus;
}
