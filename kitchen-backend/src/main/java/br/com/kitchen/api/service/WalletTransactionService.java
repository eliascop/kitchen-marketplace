package br.com.kitchen.api.service;

import br.com.kitchen.api.enumerations.TransactionStatus;
import br.com.kitchen.api.enumerations.TransactionType;
import br.com.kitchen.api.model.*;
import br.com.kitchen.api.producer.KafkaProducer;
import br.com.kitchen.api.repository.WalletTransactionRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class WalletTransactionService extends GenericService<WalletTransaction, Long> {

    private final WalletTransactionRepository walletTransactionRepository;
    private final KafkaProducer<WalletTransaction> walletTxProducer;

    @Autowired
    public WalletTransactionService(WalletTransactionRepository walletTransactionRepository,
                                    KafkaProducer<WalletTransaction> walletTxProducer) {
        super(walletTransactionRepository, WalletTransaction.class);
        this.walletTransactionRepository = walletTransactionRepository;
        this.walletTxProducer = walletTxProducer;
    }

    @Transactional
    public WalletTransaction createCreditTransaction(Wallet wallet, BigDecimal amount, String description) {
        WalletTransaction tx = new WalletTransaction();
        tx.setWallet(wallet);
        tx.setAmount(amount);
        tx.setType(TransactionType.CREDIT);
        tx.setStatus(TransactionStatus.PENDING);
        tx.setDescription(description);
        tx.setSecureToken(UUID.randomUUID().toString().replace("-", "").substring(0, 16));
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
        tx.setSecureToken(UUID.randomUUID().toString().replace("-", "").substring(0, 16));
        walletTransactionRepository.save(tx);
        walletTxProducer.sendNotification(tx);
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
        walletTxProducer.sendNotification(tx);
    }

    @Transactional
    public void cancelTransaction(Long id) {
        WalletTransaction tx = walletTransactionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found with id: " + id));
        tx.setStatus(TransactionStatus.CANCELED);
        walletTransactionRepository.save(tx);
        walletTxProducer.sendNotification(tx);
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
