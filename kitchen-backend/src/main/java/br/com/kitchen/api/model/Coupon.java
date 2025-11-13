package br.com.kitchen.api.model;

import br.com.kitchen.api.enumerations.CouponScope;
import br.com.kitchen.api.enumerations.CouponType;
import br.com.kitchen.api.enumerations.CouponVisibility;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(
        name = "coupon",
        schema = "kitchen",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_coupon_code", columnNames = "code")
        },
        indexes = {
                @Index(name = "idx_coupon_active", columnList = "active"),
                @Index(name = "idx_coupon_scope", columnList = "scope"),
                @Index(name = "idx_coupon_seller", columnList = "seller_id"),
                @Index(name = "idx_coupon_expires_at", columnList = "expires_at")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Coupon {

    @Id
    @Column(nullable = false, updatable = false, length = 36)
    private String id;

    @Column(nullable = false, length = 64)
    @NotBlank(message = "Coupon must not be blank")
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(name = "coupon_type", nullable = false, length = 16)
    @NotBlank(message = "CouponType must not be blank")
    private CouponType couponType;

    @Column(nullable = false, precision = 18, scale = 2)
    @Min(value = 1, message = "Amount must be greater than zero")
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    @NotBlank(message = "Scope must not be blank")
    private CouponScope scope;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    @NotBlank(message = "Visibility must not be blank")
    private CouponVisibility visibility;

    @Column(name = "issuer_id")
    private Long issuerId;

    @Column(name = "seller_id")
    private Long sellerId;

    @ElementCollection
    @CollectionTable(name = "coupon_applicable_product", joinColumns = @JoinColumn(name = "coupon_id"))
    @Column(name = "product_id")
    private Set<Long> applicableProductIds;

    @ElementCollection
    @CollectionTable(name = "coupon_allowed_buyer", joinColumns = @JoinColumn(name = "coupon_id"))
    @Column(name = "buyer_id")
    private Set<Long> allowedBuyerIds;

    @Column(name = "min_order_amount", precision = 18, scale = 2)
    private BigDecimal minOrderAmount;

    @Column(name = "max_discount_amount", precision = 18, scale = 2)
    private BigDecimal maxDiscountAmount;

    @Column(name = "usage_limit_total")
    @Min(value = 1, message = "UsageLimitTotal must be greater than zero")
    private int usageLimitTotal;

    @Column(name = "usage_limit_per_buyer")
    @Min(value = 1, message = "UsageLimitPerBuyer must be greater than zero")
    private int usageLimitPerBuyer;

    @Column(name = "usage_count_total")
    @Min(value = 1, message = "UsageCountTotal must be greater than zero")
    private int usageCountTotal;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "starts_at")
    private LocalDateTime startsAt;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private boolean active;

    @PrePersist
    public void prePersist() {
        if (this.id == null) {
            this.id = UUID.randomUUID().toString();
        }

        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
