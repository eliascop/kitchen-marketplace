package br.com.kitchen.api.service;

import br.com.kitchen.api.dto.PaymentNotificationDTO;
import br.com.kitchen.api.enumerations.OrderStatus;
import br.com.kitchen.api.enumerations.PaymentStatus;
import br.com.kitchen.api.model.Cart;
import br.com.kitchen.api.model.Order;
import br.com.kitchen.api.model.Payment;
import br.com.kitchen.api.repository.jpa.PaymentRepository;
import br.com.kitchen.api.service.payment.PaymentProvider;
import br.com.kitchen.api.service.payment.PaymentProviderFactory;
import org.springframework.stereotype.Service;

@Service
public class PaymentService extends GenericService<Payment, Long> {

    private final PaymentProviderFactory paymentProviderFactory;
    private final PaymentRepository repository;
    private final OutboxService outboxService;
    private final OrderService orderService;
    private final StockService stockService;
    private final CartService cartService;

    public PaymentService(PaymentProviderFactory paymentProviderFactory,
                          PaymentRepository repository,
                          OrderService orderService, OutboxService outboxService,
                          StockService stockService,
                          CartService cartService) {
        super(repository, Payment.class);
        this.repository = repository;
        this.paymentProviderFactory = paymentProviderFactory;
        this.orderService = orderService;
        this.outboxService = outboxService;
        this.stockService = stockService;
        this.cartService = cartService;
    }

    public String initiatePayment(String provider, Long userId) throws Exception {
        PaymentProvider paymentProvider = paymentProviderFactory.getProvider(provider);

        Cart cart = cartService.getActiveCartByUserId(userId);

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

    public Payment findByCartId(Long cartId) {
        return repository.findPaymentByCartId(cartId).orElseThrow(() -> new RuntimeException("Payment not found"));
    }

    public void processPayment(PaymentNotificationDTO paymentNotification) {
        Payment payment = findByCartId(Long.valueOf(paymentNotification.getCartId()));
        Order order = orderService.findOrderByPaymentId(payment.getId());
        Cart cart = payment.getCart();
        if(order.getStatus() != OrderStatus.CONFIRMED && order.getStatus() != OrderStatus.CANCELLED) {
            switch (paymentNotification.getStatus()) {
                case "PENDING" -> {
                    payment.setStatus(PaymentStatus.PENDING);
                    System.out.println("Payment is pending");
                }
                case "COMPLETED" -> {
                    payment.setStatus(PaymentStatus.SUCCESS);
                    stockService.confirmStockFromCart(cart);
                    order.setStatus(OrderStatus.CONFIRMED);
                    outboxService.createOrderEvent(order);
                    System.out.println("Order confirmed");
                }
                default -> System.err.println("Unhandled PayPal webhook status: " + paymentNotification.getStatus());
            }
            repository.save(payment);
        }
    }
}
