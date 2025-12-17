package br.com.kitchen.api.repository.jpa;

import br.com.kitchen.api.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends GenericRepository<Product, Long> {

    @Query("SELECT p FROM Product p WHERE p.productStatus = 'ACTIVE'")
    Page<Product> findAllActiveProducts(Pageable pageable);

    Page<Product> findBySellerId(Long sellerId, Pageable pageable);

    Optional<Product> findByIdAndSellerId(Long id, Long sellerId);

    @Query("SELECT p FROM Product p WHERE p.catalog.id IN :catalogIds AND p.productStatus = 'ACTIVE'")
    Page<Product> findActiveProductsByCatalogIdIn(List<Long> catalogIds, Pageable pageable);

    @Modifying
    @Transactional
    @Query("""
        UPDATE Product p
           SET p.category.id = :categoryId,
               p.productStatus = 'ACTIVE',
               p.activatedAt = CURRENT_TIMESTAMP
         WHERE p.id = :productId
    """)
    void updateCategoryAndStatus(Long productId, Long categoryId);
}