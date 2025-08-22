package br.com.kitchen.api.service.payment;

import br.com.kitchen.api.model.Cart;

public interface PaymentProvider {
    void createPayment(Cart cart) throws Exception;
    String initiatePayment(Cart cart) throws Exception;
    String confirmPayment(String token) throws Exception;
    boolean isValidSecureToken(String token);
    void cancelPayment(String token) throws Exception;
    String getName();
}