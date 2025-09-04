package br.com.kitchen.api.mapper;

import br.com.kitchen.api.dto.response.PaymentResponseDTO;
import br.com.kitchen.api.model.Payment;

public class PaymentMapper {
    public static PaymentResponseDTO toDTO(Payment payment) {
        if (payment == null) return null;

        return PaymentResponseDTO.builder()
                .id(payment.getId())
                .method(payment.getMethod().toString())
                .status(payment.getStatus().toString())
                .amount(payment.getAmount())
                .createdAt(payment.getCreatedAt())
                .build();
    }
}
