package br.com.kitchen.api.repository.jpa;

import br.com.kitchen.api.model.Stock;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StockRepository extends GenericRepository<Stock, Long> {
    Optional<Stock> findBySku_SkuAndSeller_Id(String sku, Long sellerId);
}
