package br.com.kitchen.api.mapper;

import br.com.kitchen.api.dto.response.*;
import br.com.kitchen.api.model.*;

public class ProductMapper {

    public static ProductResponseDTO toResponseDTO(Product product) {
        return new ProductResponseDTO(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getImageUrl(),
                product.getPrice(),
                product.getCatalog().getName(),
                product.getCategory().getName(),
                product.isActive(),
                product.getSkus().stream()
                        .map(ProductMapper::toSkuResponseDTO)
                        .toList()
        );
    }

    private static ProductSkuResponseDTO toSkuResponseDTO(ProductSku sku) {
        return new ProductSkuResponseDTO(
                sku.getId(),
                sku.getSku(),
                sku.getPrice(),
                toStockResponseDTO(sku.getStock()),
                sku.getAttributes().stream()
                        .map(ProductMapper::toAttributeResponseDTO)
                        .toList()
        );
    }

    private static StockResponseDTO toStockResponseDTO(Stock stock) {
        return new StockResponseDTO(
                stock.getTotalQuantity(),
                stock.getReservedQuantity(),
                stock.getSoldQuantity(),
                stock.getAvailableQuantity()
        );
    }

    private static ProductAttributeResponseDTO toAttributeResponseDTO(ProductAttribute attr) {
        return new ProductAttributeResponseDTO(
                attr.getName(),
                attr.getAttributeValue()
        );
    }
}
