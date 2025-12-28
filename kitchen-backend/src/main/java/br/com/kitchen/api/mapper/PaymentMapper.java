package br.com.kitchen.api.mapper;

import br.com.kitchen.api.dto.PaymentDTO;
import br.com.kitchen.api.model.Payment;

public class PaymentMapper {
    public static PaymentDTO toDTO(Payment payment) {
        if (payment == null) return null;

        return PaymentDTO.builder()
                .id(payment.getId())
                .method(payment.getMethod().toString())
                .status(payment.getStatus().toString())
                .amount(payment.getAmount())
                .createdAt(payment.getCreatedAt())
                .build();
    }
}
