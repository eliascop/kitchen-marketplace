package br.com.kitchen.api.service;

import br.com.kitchen.api.enumerations.TransactionStatus;
import br.com.kitchen.api.enumerations.TransactionType;
import br.com.kitchen.api.model.OutboxEvent;
import br.com.kitchen.api.model.Wallet;
import br.com.kitchen.api.model.WalletTransaction;
import br.com.kitchen.api.repository.jpa.OutboxRepository;
import br.com.kitchen.api.repository.jpa.WalletTransactionRepository;
import br.com.kitchen.api.util.JsonUtils;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class WalletTransactionService extends GenericService<WalletTransaction, Long> {

    private final WalletTransactionRepository walletTransactionRepository;
    private final OutboxRepository outboxRepository;

    public WalletTransactionService(WalletTransactionRepository walletTransactionRepository,
                                    OutboxRepository outboxRepository) {
        super(walletTransactionRepository, WalletTransaction.class);
        this.walletTransactionRepository = walletTransactionRepository;
        this.outboxRepository = outboxRepository;
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
        walletTransactionRepository.save(tx);
        OutboxEvent event = OutboxEvent.builder()
                .aggregateType("WALLET-TRANSACTION")
                .aggregateId(tx.getId())
                .eventType("WALLET-TRANSACTION-DEBIT_CONFIRMED")
                .payload(JsonUtils.toJson(tx))
                .build();
        outboxRepository.save(event);
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
        walletTransactionRepository.save(tx);
        OutboxEvent event = OutboxEvent.builder()
                .aggregateType("WALLET-TRANSACTION")
                .aggregateId(tx.getId())
                .eventType("WALLET-TRANSACTION-VALIDATE_CONFIRMED")
                .payload(JsonUtils.toJson(tx))
                .build();
        outboxRepository.save(event);
    }

    @Transactional
    public void cancelTransaction(Long id) {
        WalletTransaction tx = walletTransactionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found with id: " + id));
        tx.setStatus(TransactionStatus.CANCELED);
        walletTransactionRepository.save(tx);
        OutboxEvent event = OutboxEvent.builder()
                .aggregateType("WALLET-TRANSACTION")
                .aggregateId(tx.getId())
                .eventType("WALLET-TRANSACTION-CANCELLED_CONFIRMED")
                .payload(JsonUtils.toJson(tx))
                .build();
        outboxRepository.save(event);
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
