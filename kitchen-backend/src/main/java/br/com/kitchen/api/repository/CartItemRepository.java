package br.com.kitchen.api.repository;

import br.com.kitchen.api.model.CartItems;
import org.springframework.stereotype.Repository;

@Repository
public interface CartItemRepository extends GenericRepository<CartItems, Long> {
    void deleteByCartId(Long id);
}
