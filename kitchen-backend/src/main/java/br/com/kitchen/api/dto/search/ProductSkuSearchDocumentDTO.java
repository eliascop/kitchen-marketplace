package br.com.kitchen.api.dto.search;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductSkuSearchDocumentDTO {

    private Long id;
    private String sku;
    private BigDecimal price;

    private Integer stockQuantity;

    private List<SkuAttributeSearchDocumentDTO> attributes;
}