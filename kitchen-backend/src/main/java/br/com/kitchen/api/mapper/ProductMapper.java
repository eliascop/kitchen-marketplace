package br.com.kitchen.api.mapper;

import br.com.kitchen.api.dto.*;
import br.com.kitchen.api.dto.search.ProductSearchDocumentDTO;
import br.com.kitchen.api.model.Product;
import br.com.kitchen.api.model.ProductAttribute;
import br.com.kitchen.api.model.ProductSku;

import java.util.List;
import java.util.stream.Collectors;

public class ProductMapper {

    public static ProductDTO toProductResponseDTO(Product product) {
        return ProductDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .imageUrl(product.getImageUrl())
                .price(product.getPrice())
                .catalog(CatalogMapper.toDTO(product.getCatalog()))
                .category(CategoryMapper.toDTO(product.getCategory()))
                .productStatus(product.getProductStatus().toString())
                .seller(SellerMapper.toDTO(product.getSeller()))
                .skus(product.getSkus().stream()
                        .map(ProductMapper::toSkuResponseDTO)
                        .toList())
                .build();
    }

    public static ProductDTO toProductResponseDTO(ProductSku sku) {
        return ProductDTO.builder()
                .id(sku.getId())
                .name(sku.getProduct().getName())
                .description(sku.getProduct().getDescription())
                .price(sku.getPrice())
                .seller(SellerMapper.toDTO(sku.getProduct().getSeller()))
                .skus(List.of(ProductSkuDTO.builder()
                        .id(sku.getId())
                        .sku(sku.getSku())
                        .build()))
                .build();
    }

    public static ProductDTO toProductDTO(Product product) {
        return ProductDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .seller(SellerMapper.toDTO(product.getSeller()))
                .build();
    }

    public static List<ProductDTO> toDTOList(List<Product> product) {
        return product.stream()
                .map(ProductMapper::toProductResponseDTO)
                .collect(Collectors.toList());
    }

    public static ProductSkuDTO toSkuResponseDTO(ProductSku sku) {
        return ProductSkuDTO.builder()
                .id(sku.getId())
                .sku(sku.getSku())
                .price(sku.getPrice())
                .stock(StockMapper.toStockResponseDTO(sku.getStock()))
                .stockHistory(sku.getStockHistory())
                .attributes(sku.getAttributes().stream()
                        .map(ProductMapper::toAttributeResponseDTO)
                        .toList())
                .build();
    }

    public static ProductAttributeDTO toAttributeResponseDTO(ProductAttribute attr) {
        return ProductAttributeDTO.builder()
                .attributeName(attr.getAttributeName())
                .attributeValue(attr.getAttributeValue())
                .build();
    }

    public static ProductDTO fromSearchDocument(ProductSearchDocumentDTO doc) {
        return ProductDTO.builder()
                .id(doc.getId())
                .name(doc.getName())
                .description(doc.getDescription())
                .imageUrl(doc.getImageUrl())
                .price(doc.getPrice())
                .catalog(CatalogDTO.builder()
                        .id(doc.getCatalogId())
                        .name(doc.getCatalogName())
                        .build())
                .category(CategoryDTO.builder()
                        .id(doc.getCategoryId())
                        .name(doc.getCategoryName())
                        .build())
                .productStatus(doc.getProductStatus())

                .seller(SellerDTO.builder()
                        .id(doc.getSellerId())
                        .storeName(doc.getSellerName())
                        .build())

                .skus(doc.getSkus() != null ?
                        doc.getSkus().stream()
                                .map(sku -> ProductSkuDTO.builder()
                                        .id(sku.getId())
                                        .sku(sku.getSku())
                                        .price(sku.getPrice())
                                        .attributes(sku.getAttributes() != null ?
                                                sku.getAttributes().stream()
                                                        .map(attr -> ProductAttributeDTO.builder()
                                                                .attributeName(attr.getName())
                                                                .attributeValue(attr.getValue())
                                                                .build())
                                                        .toList()
                                                : null)
                                        .build()
                                )
                                .toList()
                        : null)
                .build();
    }

}
