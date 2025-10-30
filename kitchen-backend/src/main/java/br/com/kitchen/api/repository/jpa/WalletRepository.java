package br.com.kitchen.api.repository.jpa;

import br.com.kitchen.api.model.Wallet;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WalletRepository extends GenericRepository<Wallet, Long> {
    Optional<Wallet> findByUserId(Long userId);
}
