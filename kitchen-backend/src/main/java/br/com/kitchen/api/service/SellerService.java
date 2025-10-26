package br.com.kitchen.api.service;

import br.com.kitchen.api.model.Seller;
import br.com.kitchen.api.model.User;
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

    public Seller getActiveSeller(User user) {
        Seller seller = repository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Seller not found for this user"));
        if (seller.isBlocked()) {
            throw new RuntimeException("Seller is not active");
        }
        return seller;
    }

    public Seller findByUserId(User user) {
        return repository.findByUser(user).orElse(null);
    }
}