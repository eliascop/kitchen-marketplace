package br.com.kitchen.ordermonitoring.app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class PaymentNotificationDTO {
    private String providerId;
    private String createTime;
    private String status;
    private String cartId;
    private String paymentAmount;
    private String paymentCurrency;
    private String payerId;
    private String payerEmail;
}
