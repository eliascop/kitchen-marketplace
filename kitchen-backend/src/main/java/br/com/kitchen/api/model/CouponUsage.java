package br.com.kitchen.api.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "coupon_usage",
        indexes = {
                @Index(name = "idx_usage_coupon_buyer", columnList = "coupon_id,buyer_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CouponUsage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "coupon_id", nullable = false, length = 36)
    private String couponId;

    @Column(name = "buyer_id", nullable = false)
    private Long buyerId;

    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "used_at", nullable = false)
    private LocalDateTime usedAt;

}
