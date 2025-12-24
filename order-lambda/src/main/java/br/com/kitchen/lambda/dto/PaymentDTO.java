package br.com.kitchen.lambda.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentDTO {

    private Long id;
    private String method;
    private String status;
    private BigDecimal amount;
    private LocalDateTime createdAt;

}

