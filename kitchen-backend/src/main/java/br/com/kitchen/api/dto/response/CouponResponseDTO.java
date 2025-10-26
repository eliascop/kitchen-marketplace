package br.com.kitchen.api.dto.response;

import br.com.kitchen.api.enumerations.CouponScope;
import br.com.kitchen.api.enumerations.CouponType;
import br.com.kitchen.api.enumerations.CouponVisibility;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class CouponResponseDTO {
    private String id;
    private String code;
    private CouponType type;
    private BigDecimal amount;
    private CouponScope scope;
    private CouponVisibility visibility;
    private Long sellerId;
    private boolean active;
    private LocalDateTime startsAt;
    private LocalDateTime expiresAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
