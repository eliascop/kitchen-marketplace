package br.com.kitchen.api.repository;

import br.com.kitchen.api.model.WalletTransaction;

import java.util.List;
import java.util.Optional;

public interface WalletTransactionRepository extends GenericRepository<WalletTransaction, Long>{
    Optional<List<WalletTransaction>> findByWalletIdOrderByCreatedAtDesc(Long walletId);
    List<WalletTransaction> findByWallet_User_Id(Long userId);
}
