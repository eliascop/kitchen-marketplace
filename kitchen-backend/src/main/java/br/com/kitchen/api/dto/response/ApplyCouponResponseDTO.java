package br.com.kitchen.api.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ApplyCouponResponseDTO {
    private String couponCode;
    private boolean applicable;
    private String reason;
    private BigDecimal discount;
    private BigDecimal subtotal;
    private BigDecimal totalAfter;
}

