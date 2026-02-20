package br.com.kitchen.api.service;

import br.com.kitchen.api.dto.AddressDTO;
import br.com.kitchen.api.mapper.AddressMapper;
import br.com.kitchen.api.model.Address;
import br.com.kitchen.api.repository.jpa.AddressRepository;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public List<AddressDTO> findByUserId(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("userId must not be null");
        }

        List<Address> addresses = repository.findByUser_Id(userId);
        if (addresses == null || addresses.isEmpty()) {
            return List.of();
        }

        return AddressMapper.toDTOList(addresses);
    }
}