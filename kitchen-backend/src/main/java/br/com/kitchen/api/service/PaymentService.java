package br.com.kitchen.api.service;

import br.com.kitchen.api.enumerations.PaymentStatus;
import br.com.kitchen.api.model.Cart;
import br.com.kitchen.api.model.Payment;
import br.com.kitchen.api.repository.PaymentRepository;
import br.com.kitchen.api.service.payment.PaymentProvider;
import br.com.kitchen.api.service.payment.PaymentProviderFactory;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class PaymentService extends GenericService<Payment, Long> {

    private final PaymentProviderFactory paymentProviderFactory;
    private final PaymentRepository repository;
    private final CartService cartService;

    public PaymentService(PaymentProviderFactory paymentProviderFactory,
                          PaymentRepository repository,
                          CartService cartService) {
        super(repository, Payment.class);
        this.paymentProviderFactory = paymentProviderFactory;
        this.repository = repository;
        this.cartService = cartService;
    }

    public String initiatePayment(String provider, Long userId) throws Exception {
        PaymentProvider paymentProvider = paymentProviderFactory.getProvider(provider);

        Cart cart = cartService.getActiveCartByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Cart not found"));

        Payment p = paymentProvider.createPayment(cart);

        return p.getPaymentApprovalUrl();
    }

    public PaymentStatus processSuccess(String provider, String token, String secureToken, Long cartId) {
        PaymentProvider paymentProvider = paymentProviderFactory.getProvider(provider);

        if (!paymentProvider.isValidSecureToken(secureToken)) {
            return PaymentStatus.FAILED;
        }

        return cartService.findById(cartId)
                .map(cart -> {
                    cart.getPayment().setGatewayTransactionId(token);
                    cartService.save(cart);
                    return PaymentStatus.SUCCESS;
                })
                .orElseGet(() -> PaymentStatus.FAILED);
    }
}
