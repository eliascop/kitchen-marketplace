package br.com.kitchen.lambda.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShippingDTO {

    private Long id;
    private String carrier;
    private String method;
    private BigDecimal cost;
    private Long estimatedDays;
    private String status;
    private SellerDTO seller;

}
