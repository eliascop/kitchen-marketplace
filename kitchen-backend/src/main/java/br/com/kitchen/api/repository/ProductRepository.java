package br.com.kitchen.api.repository;

import br.com.kitchen.api.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends GenericRepository<Product, Long> {
    List<Product> findBySellerId(Long sellerId);

    @Query("SELECT p FROM Product p JOIN p.skus s WHERE s.sku = :sku")
    Optional<Product> findBySku(@Param("sku") String sku);

    Page<Product> findByCatalogIdIn(List<Long> catalogIds,
                                    Pageable pageable);
}