package br.com.kitchen.api.service;

import br.com.kitchen.api.model.Address;
import br.com.kitchen.api.repository.jpa.AddressRepository;
import org.springframework.stereotype.Service;

@Service
public class AddressService extends GenericService<Address, Long> {

    private final AddressRepository repository;

    public AddressService(AddressRepository repository) {
        super(repository, Address.class);
        this.repository = repository;
    }

    public Address getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Billing address not found"));
    }
}