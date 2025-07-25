package br.com.kitchen.api.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record ProductResponseDTO(
        Long id,
        String name,
        String description,
        BigDecimal price,
        String catalogName,
        String categoryName,
        boolean active,
        List<ProductSkuResponseDTO> skus
) {}
