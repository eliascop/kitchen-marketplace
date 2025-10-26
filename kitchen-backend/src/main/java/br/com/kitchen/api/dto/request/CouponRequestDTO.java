package br.com.kitchen.api.dto.request;

import br.com.kitchen.api.enumerations.CouponScope;
import br.com.kitchen.api.enumerations.CouponType;
import br.com.kitchen.api.enumerations.CouponVisibility;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Data
public class CouponRequestDTO {
    private String code;
    private CouponType type;
    private BigDecimal value;
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
    private LocalDateTime startsAt;
    private LocalDateTime expiresAt;
    private boolean active;

}
