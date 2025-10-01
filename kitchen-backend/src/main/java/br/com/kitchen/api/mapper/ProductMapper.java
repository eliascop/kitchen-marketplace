package br.com.kitchen.api.mapper;

import br.com.kitchen.api.dto.ProductAttributeDTO;
import br.com.kitchen.api.dto.ProductDTO;
import br.com.kitchen.api.dto.ProductSkuDTO;
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
                .catalog(product.getCatalog() != null ? product.getCatalog().getName() : null)
                .category(product.getCategory() != null ? product.getCategory().getName() : null)
                .active(product.getActive())
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
                .product(toProductDTO(sku.getProduct()))
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
}
