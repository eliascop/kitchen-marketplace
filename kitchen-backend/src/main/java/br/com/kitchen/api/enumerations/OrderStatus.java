package br.com.kitchen.api.enumerations;

public enum OrderStatus {
    CONFIRMED,
    PREPARING,
    PENDING,
    PENDING_PROCESSING,
    PENDING_PAYMENT,
    PAYMENT_ERROR,
    CANCELLED,
    PAID,
}
