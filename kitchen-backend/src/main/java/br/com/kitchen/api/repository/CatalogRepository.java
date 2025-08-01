package br.com.kitchen.api.repository;

import br.com.kitchen.api.model.Catalog;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CatalogRepository extends GenericRepository<Catalog, Long> {
    Optional<Catalog> findByNameAndSellerId(String name, Long sellerId);
}
