package br.com.kitchen.api.service.payment.paypal;

import br.com.kitchen.api.model.WalletTransaction;
import br.com.kitchen.api.service.payment.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service("paypal")
@RequiredArgsConstructor
public class PaypalPaymentService implements PaymentService {

    private final PaypalService paypalService;

    @Override
    public String initiatePayment(WalletTransaction transaction) throws Exception {
        return paypalService.doPayment(transaction);
    }

    @Override
    public String confirmPayment(String token) throws Exception {
        return paypalService.confirmPayment(token);
    }

    @Override
    public void cancelPayment(String token) throws Exception {
        // TODO Check if PayPal has direct order cancellable support by token
    }

    @Override
    public String getName() {
        return "paypal";
    }
}
