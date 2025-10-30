package br.com.kitchen.api.repository.jpa;

import br.com.kitchen.api.model.Seller;
import br.com.kitchen.api.model.User;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SellerRepository extends GenericRepository<Seller, Long> {
    Optional<Seller> findByUser(User user);
}
