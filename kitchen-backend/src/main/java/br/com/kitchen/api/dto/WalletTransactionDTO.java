package br.com.kitchen.api.dto;

import br.com.kitchen.api.enumerations.TransactionStatus;
import br.com.kitchen.api.enumerations.TransactionType;
import br.com.kitchen.api.model.Wallet;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WalletTransactionDTO {

    private Long id;

    @ManyToOne
    private Wallet wallet;
    private TransactionType type;
    private BigDecimal amount;
    private String description;
    @CreationTimestamp
    private LocalDateTime createdAt;
    private TransactionStatus status;

}
