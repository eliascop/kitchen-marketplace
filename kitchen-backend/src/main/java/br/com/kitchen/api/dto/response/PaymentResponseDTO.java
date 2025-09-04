package br.com.kitchen.api.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter @Setter
@Builder
public class PaymentResponseDTO {

    private Long id;
    private String method;
    private String status;
    private BigDecimal amount;
    private LocalDateTime createdAt;

}

