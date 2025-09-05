package br.com.kitchen.api.service;

import br.com.kitchen.api.model.*;
import br.com.kitchen.api.dto.ProductAttributeDTO;
import br.com.kitchen.api.dto.ProductSkuDTO;
import br.com.kitchen.api.repository.ProductSkuRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SkuService {

    private final ProductSkuRepository productSkuRepository;

    public SkuService(ProductSkuRepository productSkuRepository) {
        this.productSkuRepository = productSkuRepository;
    }

    public List<ProductSku> createOrUpdateSkus(Product product,
                                               Seller seller,
                                               List<ProductSkuDTO> skuDtos) {

        return skuDtos.stream()
                .map(skuDTO -> {
                    String generatedSku = generateSku(product.getId(), skuDTO.attributes());

                    ProductSku sku = productSkuRepository
                            .findBySkuAndProductId(generatedSku, product.getId())
                            .orElse(new ProductSku());

                    sku.setSku(generatedSku);
                    sku.setPrice(skuDTO.price());
                    sku.setProduct(product);
                    sku.setAttributes(buildAttributes(skuDTO.attributes(), sku));
                    sku.setStock(buildStock(sku, seller, skuDTO.stock()));

                    return sku;
                })
                .toList();
    }

    private List<ProductAttribute> buildAttributes(List<ProductAttributeDTO> attributesDTO, ProductSku sku) {
        return attributesDTO.stream().map(attrDTO -> {
            ProductAttribute attr = new ProductAttribute();
            attr.setName(attrDTO.name());
            attr.setAttributeValue(attrDTO.value());
            attr.setSku(sku);
            return attr;
        }).toList();
    }

    private Stock buildStock(ProductSku sku, Seller seller, int totalQuantity) {
        Stock stock = sku.getStock();
        if (stock == null) {
            stock = new Stock();
            stock.setSku(sku);
            stock.setSeller(seller);
            stock.setReservedQuantity(0);
            stock.setSoldQuantity(0);
        }
        stock.setTotalQuantity(totalQuantity);
        return stock;
    }

    private String generateSku(Long productId, List<ProductAttributeDTO> attributesDTO) {
        return "PROD-" + productId + attributesDTO.stream()
                .sorted(Comparator.comparing(ProductAttributeDTO::name))
                .map(attr -> "-" + normalize(attr.value()))
                .collect(Collectors.joining());
    }

    private String normalize(String input) {
        String normalized = input == null? "" :
                input.toUpperCase().replaceAll("[^A-Z0-9]", "");
        return normalized.substring(0, Math.min(5, normalized.length()));
    }
}
