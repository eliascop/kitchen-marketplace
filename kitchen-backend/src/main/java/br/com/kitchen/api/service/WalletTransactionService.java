package br.com.kitchen.api.service;

import br.com.kitchen.api.enumerations.TransactionStatus;
import br.com.kitchen.api.enumerations.TransactionType;
import br.com.kitchen.api.model.Wallet;
import br.com.kitchen.api.model.WalletTransaction;
import br.com.kitchen.api.repository.jpa.WalletTransactionRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class WalletTransactionService extends GenericService<WalletTransaction, Long> {

    private final WalletTransactionRepository walletTransactionRepository;
    private final OutboxService outboxService;

    public WalletTransactionService(WalletTransactionRepository walletTransactionRepository,
                                    OutboxService outboxService) {
        super(walletTransactionRepository, WalletTransaction.class);
        this.walletTransactionRepository = walletTransactionRepository;
        this.outboxService = outboxService;
    }

    @Transactional
    public WalletTransaction createCreditTransaction(Wallet wallet, BigDecimal amount, String description) {
        WalletTransaction tx = new WalletTransaction();
        tx.setWallet(wallet);
        tx.setAmount(amount);
        tx.setType(TransactionType.CREDIT);
        tx.setStatus(TransactionStatus.PENDING);
        tx.setDescription(description);
        return walletTransactionRepository.save(tx);
    }

    @Transactional
    public void debit(Wallet wallet, BigDecimal amount, String description) {
        wallet.setBalance(wallet.getBalance().subtract(amount));
        WalletTransaction tx = new WalletTransaction();
        tx.setWallet(wallet);
        tx.setAmount(amount);
        tx.setType(TransactionType.DEBIT);
        tx.setDescription(description);
        tx.setStatus(TransactionStatus.AUTHORIZED);
        WalletTransaction ntx = walletTransactionRepository.save(tx);
        outboxService.publishWalletTransactionEvent(ntx, "DEBIT_CONFIRMED");
    }

    public Optional<List<WalletTransaction>> getTransactions(Wallet wallet) {
        return walletTransactionRepository.findByWalletIdOrderByCreatedAtDesc(wallet.getId());
    }

    @Transactional
    public void validateTransaction(Long id) {
        WalletTransaction tx = walletTransactionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found with id: " + id));
        Wallet wallet = tx.getWallet();
        wallet.setBalance(wallet.getBalance().add(tx.getAmount()));

        tx.setStatus(TransactionStatus.AUTHORIZED);
        WalletTransaction ntx = walletTransactionRepository.save(tx);
        outboxService.publishWalletTransactionEvent(ntx, "VALIDATE_CONFIRMED");
    }

    @Transactional
    public void cancelTransaction(Long id) {
        WalletTransaction tx = walletTransactionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found with id: " + id));
        tx.setStatus(TransactionStatus.CANCELED);
        WalletTransaction ntx = walletTransactionRepository.save(tx);
        outboxService.publishWalletTransactionEvent(ntx, "CANCELLED_CONFIRMED");
    }

    public BigDecimal getBalanceForUser(Long userId) {
        List<WalletTransaction> transactions = walletTransactionRepository.findByWallet_User_Id(userId);

        return transactions.stream()
                .filter(tx -> tx.getStatus().equals(TransactionStatus.AUTHORIZED))
                .map(tx -> tx.getType() == TransactionType.CREDIT ?
                        tx.getAmount() :
                        tx.getAmount().negate())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
