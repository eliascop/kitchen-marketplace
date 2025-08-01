package br.com.kitchen.api.repository;

import br.com.kitchen.api.model.Payment;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends GenericRepository<Payment, Long>{
}
