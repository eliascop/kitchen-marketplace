package br.com.kitchen.api.repository.jpa;

import br.com.kitchen.api.model.Address;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepository extends GenericRepository<Address, Long> {
}
