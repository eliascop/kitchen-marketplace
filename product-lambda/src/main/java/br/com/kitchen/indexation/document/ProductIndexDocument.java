package br.com.kitchen.indexation.document;

import br.com.kitchen.indexation.dto.CategoryDTO;
import br.com.kitchen.indexation.dto.ProductDTO;

import java.math.BigDecimal;
import java.util.List;

public record ProductIndexDocument(
        Long id,
        String name,
        String description,
        Long categoryId,
        String categoryName,
        BigDecimal price,
        List<SkuIndexDocument> skus
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
                product.getPrice(),
                skuDocuments
        );
    }
}
