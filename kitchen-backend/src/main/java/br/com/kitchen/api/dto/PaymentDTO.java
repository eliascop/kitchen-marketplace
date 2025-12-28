package br.com.kitchen.api.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter @Setter
@Builder
public class PaymentDTO {

    private Long id;
    private String method;
    private String status;
    private BigDecimal amount;
    private LocalDateTime createdAt;

}

