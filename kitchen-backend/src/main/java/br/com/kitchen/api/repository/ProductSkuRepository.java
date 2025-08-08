package br.com.kitchen.api.repository;

import br.com.kitchen.api.model.ProductSku;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductSkuRepository extends GenericRepository<ProductSku, Long> {
    Optional<ProductSku> findBySkuAndProductId(String sku, Long productId);
}
