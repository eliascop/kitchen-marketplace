package br.com.kitchen.api.repository.jpa;

import br.com.kitchen.api.model.ProductSku;
import br.com.kitchen.api.model.Seller;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductSkuRepository extends GenericRepository<ProductSku, Long> {
    Optional<ProductSku> findBySkuAndProductId(String sku, Long productId);

    @Query("SELECT p FROM ProductSku p WHERE p.id = :productId AND p.product.seller.id = :sellerId")
    List<ProductSku> findByProductIdAndSellerId(Long productId, Long sellerId);
}
