package br.com.kitchen.api.service;

import br.com.kitchen.api.dto.OrderDTO;
import br.com.kitchen.api.enumerations.OrderStatus;
import br.com.kitchen.api.model.*;
import br.com.kitchen.api.producer.SqsProducer;
import br.com.kitchen.api.repository.AddressRepository;
import br.com.kitchen.api.repository.OrderRepository;
import br.com.kitchen.api.repository.PaymentRepository;
import br.com.kitchen.api.service.payment.PaymentProviderFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService extends GenericService<Order, Long>{

    private final AddressRepository addressRepository;
    private final SqsProducer<OrderDTO> orderProducer;
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final CartService cartService;

    public OrderService(
            @Qualifier("orderSqsProducer") SqsProducer<OrderDTO> orderProducer,
            AddressRepository addressRepository,
            OrderRepository orderRepository,
            PaymentRepository paymentRepository,
            PaymentProviderFactory paymentProviderFactory,
            CartService cartService) {
        super(orderRepository, Order.class);
        this.addressRepository = addressRepository;
        this.orderProducer = orderProducer;
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
        this.cartService = cartService;
    }

    public Optional<List<Order>> findOrdersByUserId(Long userId) {
        if(userId == 1){
            return Optional.of(orderRepository.findAll());
        }else {
            return orderRepository.findOrdersByUserId(userId);
        }
    }

    public Optional<Order> findOrderByIdAndUserId(Long id, Long userId) {
        if(userId == 1){
            return orderRepository.findById(id);
        }else {
            return orderRepository.findOrderByIdAndUserId(id, userId);
        }
    }

    @Transactional
    public Order checkoutFromCart(Long userId, Long shippingAddressId, Long billingAddressId) throws Exception{
        Cart cart = getValidatedCart(userId);
        Address shipping = getAddressOrThrow(shippingAddressId, "Shipping address not found");
        Address billing = getAddressOrThrow(billingAddressId, "Billing address not found");
        if(cart.getPayment() == null){
            throw new RuntimeException("No payment selected");
        }

        Order order = createOrderFromCart(cart, shipping, billing, cart.getPayment());
        Order orderSaved = orderRepository.save(order);

        cart.setActive(false);
        cartService.save(cart);

        orderProducer.sendNotification(new OrderDTO(orderSaved.getId(), orderSaved.getStatus().toString()));

        return orderSaved;
    }

    private Cart getValidatedCart(Long userId) {
        Cart cart = cartService.getActiveCartByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Cart not found"));

        if (cart.getCartItems().isEmpty()) {
            throw new IllegalStateException("Cart is empty");
        }

        return cart;
    }

    private Address getAddressOrThrow(Long addressId, String message) {
        return addressRepository.findById(addressId)
                .orElseThrow(() -> new IllegalArgumentException(message));
    }

    private Payment getPaymentOrThrow(Long paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment method not set"));
    }

    private Order createOrderFromCart(Cart cart, Address shipping, Address billing, Payment payment) {
        Order order = new Order();
        order.setUser(cart.getUser());
        order.setStatus(OrderStatus.PENDING_PROCESSING);
        order.setShippingAddress(shipping);
        order.setBillingAddress(billing);
        order.setPayment(payment);

        for (CartItems item : cart.getCartItems()) {
            OrderItems orderItem = new OrderItems();
            orderItem.setOrder(order);
            orderItem.setProduct(item.getProduct());
            orderItem.setQuantity(item.getQuantity());
            orderItem.setItemValue(item.getItemValue());
            order.getOrderItems().add(orderItem);
        }
        order.updateOrderTotal();
        return order;
    }

}
