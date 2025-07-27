package br.com.kitchen.api.DTO;

import br.com.kitchen.api.dto.response.ProductSkuResponseDTO;

import java.math.BigDecimal;
import java.util.List;

public record ProductResponseDTO(
        Long id,
        String name,
        String description,
        String imageUrl,
        BigDecimal basePrice,
        String catalog,
        String category,
        List<ProductSkuResponseDTO> skus
) {}
