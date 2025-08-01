package br.com.kitchen.api.enumerations;

public enum OrderStatus {
    PREPARING,
    PENDING,
    PENDING_PROCESSING,
    PENDING_PAYMENT,
    PAYMENT_ERROR,
    CANCELED,
    PAID,
}
