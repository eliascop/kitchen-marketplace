package br.com.kitchen.api.dto;
import br.com.kitchen.api.enumerations.CouponScope;
import br.com.kitchen.api.enumerations.CouponType;
import br.com.kitchen.api.enumerations.CouponVisibility;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
public class CouponDTO {
    private String id;
    private String code;
    private CouponType couponType;
    private BigDecimal amount;
    private CouponScope scope;
    private CouponVisibility visibility;
    private Long issuerId;
    private Long sellerId;
    private Set<Long> applicableProductIds;
    private Set<Long> allowedBuyerIds;
    private BigDecimal minOrderAmount;
    private BigDecimal maxDiscountAmount;
    private int usageLimitTotal;
    private int usageLimitPerBuyer;
    private int usageCountTotal;
    private LocalDateTime startsAt;
    private LocalDateTime expiresAt;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
