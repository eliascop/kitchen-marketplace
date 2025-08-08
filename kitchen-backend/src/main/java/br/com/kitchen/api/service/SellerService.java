package br.com.kitchen.api.service;

import br.com.kitchen.api.model.Seller;
import br.com.kitchen.api.repository.SellerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SellerService extends GenericService<Seller, Long> {

    private final SellerRepository repository;

    @Autowired
    public SellerService(SellerRepository repository){
        super(repository, Seller.class );
        this.repository = repository;
    }

    public Optional<Seller> findById(Long sellerId){
        return repository.findById(sellerId);
    }
}