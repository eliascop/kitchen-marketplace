package br.com.kitchen.api.service;

import br.com.kitchen.api.dto.OrderDTO;
import br.com.kitchen.api.model.Order;
import br.com.kitchen.api.model.OrderItems;
import br.com.kitchen.api.model.User;
import br.com.kitchen.api.producer.KafkaProducer;
import br.com.kitchen.api.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService extends GenericService<Order, Long>{

    private final KafkaProducer<OrderDTO> orderProducer;
    private final OrderRepository orderRepository;
    private final UserService userService;
    private final WalletService walletService;
    private final CartService cartService;

    public OrderService(
            @Qualifier("orderKafkaProducer") KafkaProducer<OrderDTO> orderProducer,
            OrderRepository orderRepository,
            UserService userService,
            WalletService walletService,
            CartService cartService) {
        super(orderRepository, Order.class);
        this.orderProducer = orderProducer;
        this.orderRepository = orderRepository;
        this.userService = userService;
        this.walletService = walletService;
        this.cartService = cartService;
    }

    @Transactional
    public Order createOrder(Order order) {
        if (order.getUser() != null && order.getUser().getId() != null) {
            User user = userService.findById(order.getUser().getId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            order.setUser(user);
        }else{
            throw new IllegalArgumentException("User must be set for the order");
        }
        walletService.debit(order.getUser().getId(), order.getTotal(), "COMPRA");
        Order orderSaved = orderRepository.save(order);
        orderProducer.sendNotification(new OrderDTO(orderSaved.getId(), orderSaved.getStatus()));
        return orderSaved;
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
    public Order checkoutFromCart(Long cartId, Long userId) {
        var cart = cartService.getCartByIdAndUserId(cartId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Cart not found or access denied"));

        if (cart.getCartItems().isEmpty()) {
            throw new IllegalStateException("Cart is empty");
        }

        Order order = new Order();
        order.setUser(cart.getUser());
        order.setStatus("PENDING_PAYMENT");

        BigDecimal total = BigDecimal.ZERO;
        for (var item : cart.getCartItems()) {
            var orderItem = new OrderItems();
            orderItem.setOrder(order);
            orderItem.setProduct(item.getProduct());
            orderItem.setQuantity(item.getQuantity());
            orderItem.setItemValue(item.getItemValue());

            total = total.add(orderItem.getItemValue().multiply(BigDecimal.valueOf(orderItem.getQuantity())));
            order.getOrderItems().add(orderItem);
        }

        order.setTotal(total);

        walletService.debit(userId, total, "COMPRA via checkout");
        Order orderSaved = orderRepository.save(order);

        cart.setActive(false);
        cartService.save(cart);
        orderProducer.sendNotification(new OrderDTO(orderSaved.getId(), orderSaved.getStatus()));

        return orderSaved;
    }

}
