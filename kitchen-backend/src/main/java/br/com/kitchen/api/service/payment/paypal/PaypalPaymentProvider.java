package br.com.kitchen.api.service.payment.paypal;

import br.com.kitchen.api.enumerations.PaymentMethod;
import br.com.kitchen.api.enumerations.PaymentStatus;
import br.com.kitchen.api.model.Cart;
import br.com.kitchen.api.model.Payment;
import br.com.kitchen.api.repository.PaymentRepository;
import br.com.kitchen.api.service.payment.PaymentProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaypalPaymentProvider implements PaymentProvider {

    private final PaypalClient paypalClient;
    private final PaymentRepository paymentRepository;

    @Override
    public String initiatePayment(Cart cart) {
        return paypalClient.doPayment(cart);
    }

    @Override
    public String confirmPayment(String token) {
        return paypalClient.confirmPayment(token);
    }

    @Override
    public void cancelPayment(String token) {
        // TODO Check if PayPal has direct order cancellable support by token
    }

    @Override
    public boolean isValidSecureToken(String token) {
        return paypalClient.isValidSecureToken(token);
    }

    @Override
    @Transactional
    public void createPayment(Cart cart){
        Payment payment = paymentRepository.findPaymentByCartId(cart.getId())
                .map(existing -> {
                    existing.setMethod(PaymentMethod.PAYPAL);
                    existing.setStatus(PaymentStatus.PENDING);
                    existing.setAmount(cart.getCartTotal());
                    existing.setSecureToken(UUID.randomUUID().toString().replace("-", "").substring(0, 16));
                    existing.setCart(cart);
                    existing.setCreatedAt(LocalDateTime.now());
                    return existing;
                })
                .orElseGet(() -> Payment.builder()
                        .method(PaymentMethod.PAYPAL)
                        .status(PaymentStatus.PENDING)
                        .amount(cart.getCartTotal())
                        .secureToken(UUID.randomUUID().toString().replace("-", "").substring(0, 16))
                        .cart(cart)
                        .createdAt(LocalDateTime.now())
                        .build());
        cart.setPayment(payment);
        paymentRepository.save(payment);
    }

    @Override
    public String getName() {
        return "paypal";
    }
}
