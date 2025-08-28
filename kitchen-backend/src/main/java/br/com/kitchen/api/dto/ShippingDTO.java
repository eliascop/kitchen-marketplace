package br.com.kitchen.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@Builder
public class ShippingDTO {

    private Long id;

    private String carrier;

    private String method;

    private BigDecimal cost;

    private Long estimatedDays;

    private SellerDTO seller;

}
