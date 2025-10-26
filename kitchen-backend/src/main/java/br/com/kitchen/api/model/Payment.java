package br.com.kitchen.api.model;

import br.com.kitchen.api.enumerations.PaymentMethod;
import br.com.kitchen.api.enumerations.PaymentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments", schema = "kitchen")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment implements Serializable {

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

    private String paymentApprovalUrl;

    @Column(nullable = false, unique = true, length = 32)
    private String secureToken;

    private String providerOrderId;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @OneToOne
    @JoinColumn(name = "cart_id", nullable = false, unique = true)
    private Cart cart;

}

