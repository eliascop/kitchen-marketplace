package br.com.kitchen.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class ShippingDTO {

    private Long id;

    private String method;

    private BigDecimal cost;

    private Long estimatedDays;

    private Long sellerId;

}
