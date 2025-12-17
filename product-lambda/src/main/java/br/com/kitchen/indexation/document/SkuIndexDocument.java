package br.com.kitchen.indexation.document;

import java.math.BigDecimal;
import java.util.List;

public record SkuIndexDocument(
        Long id,
        String skuCode,
        BigDecimal price,
        Integer stock,
        List<AttributeIndexDocument> attributes
) {}
