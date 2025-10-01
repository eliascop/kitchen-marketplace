package br.com.kitchen.ordermonitoring.app.mapper;

import br.com.kitchen.ordermonitoring.app.dto.PayPalWebhookDTO;
import br.com.kitchen.ordermonitoring.app.dto.PaymentNotificationDTO;

public class PaymentMapper {
    public static PaymentNotificationDTO toDTO(PayPalWebhookDTO webhookDTO) {
        return PaymentNotificationDTO.builder()
                .providerId(webhookDTO.getResource().getId())
                .createTime(webhookDTO.getCreateTime())
                .status(webhookDTO.getResource().getStatus())
                .cartId(webhookDTO.getResource().getCartId())
                .paymentAmount(webhookDTO.getResource().getAmount().getValue())
                .paymentCurrency(webhookDTO.getResource().getAmount().getCurrencyCode())
                .payerId(webhookDTO.getResource().getPayer() == null? "":webhookDTO.getResource().getPayer().getPayerId())
                .payerEmail(webhookDTO.getResource().getPayer() == null? "":webhookDTO.getResource().getPayer().getEmailAddress())
                .build();
    }
}