package br.com.kitchen.api.repository;

import br.com.kitchen.api.model.Product;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends GenericRepository<Product, Long> {
    List<Product> findByCatalog_Seller_UserId(Long id);
}