package br.com.kitchen.api.mapper;

import br.com.kitchen.api.dto.response.*;
import br.com.kitchen.api.model.*;

public class ProductMapper {

    public static ProductResponseDTO toResponseDTO(Product product) {
        return ProductResponseDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .imageUrl(product.getImageUrl())
                .price(product.getPrice())
                .catalog(product.getCatalog() != null ? product.getCatalog().getName() : null)
                .category(product.getCategory() != null ? product.getCategory().getName() : null)
                .active(product.isActive())
                .sellerName(product.getSeller() != null ? product.getSeller().getStoreName() : null)
                .skus(product.getSkus().stream()
                        .map(ProductMapper::toSkuResponseDTO)
                        .toList())
                .build();
    }

    private static ProductSkuResponseDTO toSkuResponseDTO(ProductSku sku) {
        return ProductSkuResponseDTO.builder()
                .id(sku.getId())
                .sku(sku.getSku())
                .price(sku.getPrice())
                .stock(toStockResponseDTO(sku.getStock()))
                .attributes(sku.getAttributes().stream()
                        .map(ProductMapper::toAttributeResponseDTO)
                        .toList())
                .build();
    }

    private static StockResponseDTO toStockResponseDTO(Stock stock) {
        if (stock == null) return null;
        return StockResponseDTO.builder()
                .totalQuantity(stock.getTotalQuantity())
                .reservedQuantity(stock.getReservedQuantity())
                .soldQuantity(stock.getSoldQuantity())
                .availableQuantity(stock.getAvailableQuantity())
                .build();
    }

    private static ProductAttributeResponseDTO toAttributeResponseDTO(ProductAttribute attr) {
        return ProductAttributeResponseDTO.builder()
                .name(attr.getName())
                .attributeValue(attr.getAttributeValue())
                .build();
    }
}
