package br.com.kitchen.api.service;

import br.com.kitchen.api.model.User;
import br.com.kitchen.api.model.Wallet;
import br.com.kitchen.api.model.WalletTransaction;
import br.com.kitchen.api.repository.jpa.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class WalletService extends GenericService<Wallet, Long> {

    private final WalletRepository walletRepository;
    private final WalletTransactionService walletTransactionService;

    @Autowired
    public WalletService(WalletRepository walletRepository,
                         WalletTransactionService walletTransactionService) {
        super(walletRepository, Wallet.class);
        this.walletRepository = walletRepository;
        this.walletTransactionService = walletTransactionService;
    }

    public Wallet getOrCreateWallet(Long userId) {
        return walletRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Wallet wallet = new Wallet();
                    wallet.setUser(User.builder().id(userId).build());
                    return walletRepository.save(wallet);
                });
    }

    public WalletTransaction createCreditTransaction(Long userId, BigDecimal amount, String description) {
        Wallet wallet = getOrCreateWallet(userId);
        return walletTransactionService.createCreditTransaction(wallet, amount, description);
    }

    public void debit(Long userId, BigDecimal amount, String description) {
        Wallet wallet = getOrCreateWallet(userId);
        if (amount.compareTo(wallet.getBalance()) > 0) {
            throw new IllegalArgumentException("Insufficient wallet balance");
        }
        walletTransactionService.debit(wallet, amount, description);
    }

    public void validateTransaction(Long id) {
        walletTransactionService.validateTransaction(id);
    }

    public boolean isValidSecureToken(String token) {
        return !walletTransactionService.findByField("secureToken", token).isEmpty();
    }

    public void cancelTransaction(Long id) {
        walletTransactionService.cancelTransaction(id);
    }

    public Optional<List<WalletTransaction>> getTransactions(Long userId) {
        Wallet wallet = getOrCreateWallet(userId);
        return walletTransactionService.getTransactions(wallet);
    }

    public BigDecimal getBalanceForUser(Long userId) {
        return walletTransactionService.getBalanceForUser(userId);
    }
}
