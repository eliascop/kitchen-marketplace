package br.com.kitchen.api.service.payment.paypal;

import br.com.kitchen.api.model.Cart;
import br.com.kitchen.api.model.Payment;
import br.com.kitchen.api.service.payment.PaymentProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service("paypal")
@RequiredArgsConstructor
public class PaypalPaymentProvider implements PaymentProvider {

    private final PaypalClient paypalClient;

    @Override
    public String initiatePayment(Cart cart) throws Exception {
        return paypalClient.doPayment(cart);
    }

    @Override
    public String confirmPayment(String token) throws Exception {
        return paypalClient.confirmPayment(token);
    }

    @Override
    public void cancelPayment(String token) throws Exception {
        // TODO Check if PayPal has direct order cancellable support by token
    }

    @Override
    public boolean isValidSecureToken(String token) {
        return paypalClient.isValidSecureToken(token);
    }

    @Override
    public void createPayment(Cart cart){
        paypalClient.createPayment(cart);
    }

    @Override
    public String getName() {
        return "paypal";
    }
}
