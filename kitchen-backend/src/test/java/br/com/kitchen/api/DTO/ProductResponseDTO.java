package br.com.kitchen.api.DTO;

import br.com.kitchen.api.dto.response.ProductSkuResponseDTO;

import java.math.BigDecimal;
import java.util.List;

public record ProductResponseDTO(
        Long id,
        String name,
        String description,
        BigDecimal basePrice,
        String catalogName,
        String categoryName,
        List<ProductSkuResponseDTO> skus
) {}
