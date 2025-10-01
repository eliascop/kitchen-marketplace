package br.com.kitchen.dto;

import lombok.Data;

@Data
public class PayPalWebhookDTO {
    private String providerId;
    private String createTime;
    private String status;
    private String cartId;
    private String paymentAmount;
    private String paymentCurrency;
    private String payerId;
    private String payerEmail;
}
