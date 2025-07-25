package br.com.kitchen.api.repository;

import br.com.kitchen.api.model.Cart;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends GenericRepository<Cart, Long> {
    Optional<Cart> findByUserId(Long id);
    Optional<Cart> findByIdAndUserId(Long id, Long userId);
}
