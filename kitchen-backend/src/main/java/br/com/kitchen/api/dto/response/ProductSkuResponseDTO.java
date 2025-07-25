package br.com.kitchen.api.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record ProductSkuResponseDTO(
        Long id,
        String sku,
        BigDecimal price,
        StockResponseDTO stock,
        List<ProductAttributeResponseDTO> attributes
) {}
