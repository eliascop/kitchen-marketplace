package br.com.kitchen.api.service.payment;

import br.com.kitchen.api.model.WalletTransaction;

public interface PaymentService {
    String initiatePayment(WalletTransaction transaction) throws Exception;
    String confirmPayment(String token) throws Exception;
    void cancelPayment(String token) throws Exception;
    String getName();
}