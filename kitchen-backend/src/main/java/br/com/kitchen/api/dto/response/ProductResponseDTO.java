package br.com.kitchen.api.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record ProductResponseDTO(
        Long id,
        String name,
        String description,
        String imageUrl,
        BigDecimal price,
        String catalog,
        String category,
        boolean active,
        List<ProductSkuResponseDTO> skus
) {}
