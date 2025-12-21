package br.com.kitchen.indexation.document;

import br.com.kitchen.indexation.dto.CategoryDTO;
import br.com.kitchen.indexation.dto.ProductDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;

public record ProductIndexDocument(
        Long id,
        String name,
        String description,
        Long categoryId,
        String categoryName,
        BigDecimal basePrice,
        List<SkuIndexDocument> skus,
        OffsetDateTime createdAt,
        OffsetDateTime activatedAt

) {
    public static ProductIndexDocument from(ProductDTO product, CategoryDTO category) {

        List<SkuIndexDocument> skuDocuments = product.getSkus().stream()
                .map(sku -> new SkuIndexDocument(
                        sku.getId(),
                        sku.getSku(),
                        sku.getPrice(),
                        sku.getStock().getId(),
                        sku.getAttributes().stream()
                                .map(attr -> new AttributeIndexDocument(
                                        attr.getAttributeName(),
                                        attr.getAttributeValue()
                                ))
                                .toList()
                ))
                .toList();

        return new ProductIndexDocument(
                product.getId(),
                product.getName(),
                product.getDescription(),
                category.getId(),
                category.getName(),
                product.getBasePrice(),
                skuDocuments,
                toOffset(product.getCreatedAt()),
                toOffset(product.getActivatedAt())

        );

    }

    private static OffsetDateTime toOffset(LocalDateTime ldt) {
        return ldt != null
                ? ldt.atOffset(ZoneOffset.UTC)
                .truncatedTo(ChronoUnit.MILLIS)
                : null;
    }
}
