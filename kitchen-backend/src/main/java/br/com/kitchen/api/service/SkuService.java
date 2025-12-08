package br.com.kitchen.api.service;

import br.com.kitchen.api.dto.request.ProductSkuRequestDTO;
import br.com.kitchen.api.dto.request.StockRequestDTO;
import br.com.kitchen.api.model.*;
import br.com.kitchen.api.dto.ProductAttributeDTO;
import br.com.kitchen.api.repository.jpa.ProductSkuRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SkuService extends GenericService<ProductSku, Long>{

    private final ProductSkuRepository productSkuRepository;

    public SkuService(ProductSkuRepository productSkuRepository) {
        super(productSkuRepository, ProductSku.class);
        this.productSkuRepository = productSkuRepository;
    }

    public ProductSku getById(Long skuId){
        return productSkuRepository.findById(skuId)
                .orElseThrow(()-> new RuntimeException("Sku not found"));
    }

    public List<ProductSku> createOrUpdateSkus(Product product,
                                               Seller seller,
                                               List<ProductSkuRequestDTO> skuDTOs) {
        List<ProductSku> finalSkus = new ArrayList<>();

        try {
            Map<String, ProductSkuRequestDTO> incomingSkuMap = skuDTOs.stream()
                    .collect(Collectors.toMap(
                            dto -> generateSku(product, dto.attributes()),
                            dto -> dto,
                            (dto1, dto2) -> dto1
                    ));

            for (Map.Entry<String, ProductSkuRequestDTO> entry : incomingSkuMap.entrySet()) {
                String generatedSku = entry.getKey();
                ProductSkuRequestDTO skuDTO = entry.getValue();
                ProductSku sku;
                if( skuDTO.id() != null) {
                    sku = product.getSkus().stream()
                            .filter(s -> s.getId().equals(skuDTO.id()))
                            .findFirst()
                            .orElseThrow(() -> new RuntimeException("SKU not found for update"));
                }else {
                    sku = new ProductSku();
                }

                sku.setSku(generatedSku);
                sku.setPrice(skuDTO.price());
                sku.setProduct(product);
                sku.getAttributes().clear();
                sku.getAttributes().addAll(buildAttributes(skuDTO.attributes(), sku));
                sku.setStock(buildStock(sku, seller, skuDTO.stock()));

                finalSkus.add(sku);
            }

            List<ProductSku> skuToRemove = product.getSkus().stream()
                    .filter(sku -> !incomingSkuMap.containsKey(sku.getSku()))
                    .toList();

            productSkuRepository.saveAll(finalSkus);
            if (!skuToRemove.isEmpty()) {
                productSkuRepository.deleteAll(skuToRemove);
            }
            return finalSkus;
        }catch (Exception e){
            throw new RuntimeException("Erro ao executar createOrUpdateSkus: ", e);
        }
    }

    private List<ProductAttribute> buildAttributes(List<ProductAttributeDTO> attributesDTO, ProductSku sku) {
        return attributesDTO.stream()
                .map(attrDTO -> {
                    ProductAttribute attr = new ProductAttribute();
                    attr.setAttributeName(attrDTO.getAttributeName());
                    attr.setAttributeValue(attrDTO.getAttributeValue());
                    attr.setSku(sku);
                    return attr;
                })
                .collect(Collectors.toList());
    }

    private Stock buildStock(ProductSku sku, Seller seller, StockRequestDTO stockDTO) {
        Stock stock = sku.getStock();
        if (stock == null) {
            stock = new Stock();
            stock.setSku(sku);
            stock.setSeller(seller);
        }
        stock.setTotalQuantity(stockDTO.getTotalQuantity());
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
