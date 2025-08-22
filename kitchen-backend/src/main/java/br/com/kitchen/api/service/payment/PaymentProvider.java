package br.com.kitchen.api.service.payment;

import br.com.kitchen.api.model.Cart;
import br.com.kitchen.api.model.Payment;

import java.util.Map;

public interface PaymentProvider {
    Payment createPayment(Cart cart) throws Exception;
    String confirmPayment(String providerOrderId) throws Exception;
    boolean isValidSecureToken(String token);
    void cancelPayment(String token) throws Exception;
    String getName();
}