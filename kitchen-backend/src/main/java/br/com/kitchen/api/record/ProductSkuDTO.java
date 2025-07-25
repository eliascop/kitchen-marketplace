package br.com.kitchen.api.record;

import java.math.BigDecimal;
import java.util.List;

public record ProductSkuDTO(
    String sku,
    BigDecimal price,
    Integer stock,
    List<ProductAttributeDTO> attributes
) {}

