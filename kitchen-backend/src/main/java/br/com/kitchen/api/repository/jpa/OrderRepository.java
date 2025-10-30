package br.com.kitchen.api.repository.jpa;

import br.com.kitchen.api.model.Order;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends GenericRepository<Order, Long> {
    Optional<List<Order>> findOrdersByUserId(Long userId);
    Optional<Order> findOrderByIdAndUserId(Long id, Long userId);
    Optional<Order> findOrderByPaymentId(Long paymentId);
}

