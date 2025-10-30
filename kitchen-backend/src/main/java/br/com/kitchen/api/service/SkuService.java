package br.com.kitchen.api.service;

import br.com.kitchen.api.dto.ProductSkuDTO;
import br.com.kitchen.api.dto.response.StockResponseDTO;
import br.com.kitchen.api.model.*;
import br.com.kitchen.api.dto.ProductAttributeDTO;
import br.com.kitchen.api.repository.jpa.ProductSkuRepository;
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
                                               List<ProductSkuDTO> skuDTOs) {

        return skuDTOs.stream()
                .map(skuDTO -> {
                    String generatedSku = generateSku(product, skuDTO.getAttributes());

                    ProductSku sku = productSkuRepository
                            .findBySkuAndProductId(generatedSku, product.getId())
                            .orElse(new ProductSku());

                    sku.setSku(generatedSku);
                    sku.setPrice(skuDTO.getPrice());
                    sku.setProduct(product);
                    sku.setAttributes(buildAttributes(skuDTO.getAttributes(), sku));
                    sku.setStock(buildStock(sku, seller, skuDTO.getStock()));

                    return sku;
                })
                .toList();
    }

    private List<ProductAttribute> buildAttributes(List<ProductAttributeDTO> attributesDTO, ProductSku sku) {
        return attributesDTO.stream().map(attrDTO -> {
            ProductAttribute attr = new ProductAttribute();
            attr.setAttributeName(attrDTO.getAttributeName());
            attr.setAttributeValue(attrDTO.getAttributeValue());
            attr.setSku(sku);
            return attr;
        }).toList();
    }

    private Stock buildStock(ProductSku sku, Seller seller, StockResponseDTO stockDTO) {
        Stock stock = sku.getStock();
        if (stock == null) {
            stock = new Stock();
            stock.setSku(sku);
            stock.setSeller(seller);
            stock.setReservedQuantity(0);
            stock.setSoldQuantity(0);
            stock.setTotalQuantity(stockDTO.getTotalQuantity());
        }else {
            stock.setReservedQuantity(stock.getReservedQuantity() + stockDTO.getTotalQuantity());
            stock.setSoldQuantity(stock.getSoldQuantity() + stockDTO.getTotalQuantity());
            stock.setTotalQuantity(stock.getTotalQuantity() + stockDTO.getTotalQuantity());
        }
        return stock;
    }

    private String generateSku(Product product, List<ProductAttributeDTO> attributesDTO) {
        return "PROD-" + product.getSeller().getId() + "-" + product.getId() + attributesDTO.stream()
                .sorted(Comparator.comparing(ProductAttributeDTO::getAttributeName))
                .map(attr -> "-" + normalize(attr.getAttributeValue()))
                .collect(Collectors.joining());
    }

    private String normalize(String input) {
        String normalized = input == null? "" :
                input.toUpperCase().replaceAll("[^A-Z0-9]", "");
        return normalized.substring(0, Math.min(5, normalized.length()));
    }
}
