package br.com.kitchen.api.repository.jpa;

import br.com.kitchen.api.model.Address;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressRepository extends GenericRepository<Address, Long> {
    List<Address> findByUser_Id(Long userId);
}
