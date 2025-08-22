package br.com.kitchen.api.repository;

import br.com.kitchen.api.model.Order;
import br.com.kitchen.api.model.Payment;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends GenericRepository<Payment, Long>{
    Optional<Payment> findPaymentByCartId(Long cartId);
}
