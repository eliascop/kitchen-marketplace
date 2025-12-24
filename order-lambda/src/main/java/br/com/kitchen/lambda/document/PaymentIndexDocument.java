package br.com.kitchen.lambda.document;

import br.com.kitchen.lambda.dto.PaymentDTO;
import br.com.kitchen.lambda.utils.DateUtils;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record PaymentIndexDocument(
        Long id,
        String method,
        String status,
        BigDecimal amount,
        OffsetDateTime createdAt
) {
    public static PaymentIndexDocument from(PaymentDTO payment) {
        return new PaymentIndexDocument(
                payment.getId(),
                payment.getMethod(),
                payment.getStatus(),
                payment.getAmount(),
                DateUtils.toOffset(payment.getCreatedAt())
        );
    }
}
