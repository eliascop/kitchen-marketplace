package br.com.kitchen.api.model;

import br.com.kitchen.api.enumerations.PaymentMethod;
import br.com.kitchen.api.enumerations.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments", schema = "kitchen")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private PaymentMethod method;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    @Column(nullable = false)
    private BigDecimal amount;

    private String gatewayTransactionId;

    private String description;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @OneToOne
    private WalletTransaction walletTransaction;
}

