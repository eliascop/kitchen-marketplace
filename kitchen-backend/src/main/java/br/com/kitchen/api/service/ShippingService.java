package br.com.kitchen.api.service;

import br.com.kitchen.api.model.Shipping;
import br.com.kitchen.api.repository.jpa.ShippingRepository;
import org.springframework.stereotype.Service;

@Service
public class ShippingService extends GenericService<Shipping, Long>{

    private final ShippingRepository repository;

    public ShippingService(ShippingRepository repository){
        super(repository, Shipping.class);
        this.repository = repository;
    }
}
