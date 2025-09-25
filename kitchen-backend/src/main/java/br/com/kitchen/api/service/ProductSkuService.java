package br.com.kitchen.api.service;

import br.com.kitchen.api.model.ProductSku;
import br.com.kitchen.api.repository.ProductSkuRepository;
import org.springframework.stereotype.Service;

@Service
public class ProductSkuService extends GenericService<ProductSku,Long> {

    private final ProductSkuRepository repository;

    public ProductSkuService(ProductSkuRepository repository) {
        super(repository,ProductSku.class);
        this.repository = repository;
    }

    public ProductSku getById(Long skuId){
        return repository.findById(skuId)
                .orElseThrow(()-> new RuntimeException("Sku not found"));
    }
}
