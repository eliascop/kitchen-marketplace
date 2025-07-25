package br.com.kitchen.api.record;

import java.math.BigDecimal;
import java.util.List;

public record ProductRequestDTO(
    String name,
    String description,
    BigDecimal basePrice,
    String catalog,
    String category,
    List<ProductSkuDTO> skus
){}
