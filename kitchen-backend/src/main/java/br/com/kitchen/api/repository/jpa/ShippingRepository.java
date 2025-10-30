package br.com.kitchen.api.repository.jpa;

import br.com.kitchen.api.model.Shipping;
import org.springframework.stereotype.Repository;

@Repository
public interface ShippingRepository extends GenericRepository<Shipping, Long> {
}
