package br.com.kitchen.api.service;

import br.com.kitchen.api.enumerations.OrderStatus;
import br.com.kitchen.api.enumerations.PaymentStatus;
import br.com.kitchen.api.enumerations.Role;
import br.com.kitchen.api.model.*;
import br.com.kitchen.api.repository.AddressRepository;
import br.com.kitchen.api.repository.OrderRepository;
import br.com.kitchen.api.repository.PaymentRepository;
import br.com.kitchen.api.service.payment.PaymentProvider;
import br.com.kitchen.api.service.payment.PaymentProviderFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class OrderService extends GenericService<Order, Long>{

    private final PaymentProviderFactory paymentProviderFactory;
    private final AddressRepository addressRepository;
    private final StockService stockService;
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final OutboxService outboxService;
    private final CartService cartService;

    public OrderService(
            AddressRepository addressRepository,
            OrderRepository orderRepository,
            PaymentRepository paymentRepository,
            PaymentProviderFactory paymentProviderFactory,
            StockService stockService,
            OutboxService outboxService,
            CartService cartService) {
        super(orderRepository, Order.class);
        this.addressRepository = addressRepository;
        this.paymentRepository = paymentRepository;
        this.paymentProviderFactory = paymentProviderFactory;
        this.orderRepository = orderRepository;
        this.cartService = cartService;
        this.stockService = stockService;
        this.outboxService = outboxService;
    }

    public List<Order> findOrdersByUserId(User user) {
        Optional<List<Order>> orderList;

        if(user.getRoles().contains(Role.ROLE_ADMIN)){
            orderList = Optional.of(orderRepository.findAll());
        }else {
            orderList = orderRepository.findOrdersByUserId(user.getId());
        }
        return orderList.orElseThrow(() -> new RuntimeException("No orders found"));
    }

    public Order findOrderByIdAndUserId(Long id, Long userId) {
        Optional<Order> order;
        if(userId == 1){
            order = orderRepository.findById(id);
        }else {
            order = orderRepository.findOrderByIdAndUserId(id, userId);
        }
        return order.orElseThrow(() -> new RuntimeException("Order not found!"));
    }

    @Transactional
    public Order checkoutFromCart(Long userId) throws Exception{
        Cart cart = getValidatedCart(userId);
        Order order = createOrderFromCart(cart);
        Order orderSaved = orderRepository.save(order);

        reserveStockForCart(cart);

        PaymentProvider paymentProvider = paymentProviderFactory.getProvider(cart.getPayment().getMethod().name());
        String paymentStatus = paymentProvider.confirmPayment(cart.getPayment().getProviderOrderId());

        if("COMPLETED".equals(paymentStatus) || "CREATED".equals(paymentStatus) || "APPROVED".equals(paymentStatus)) {
            cart.getPayment().setStatus(PaymentStatus.SUCCESS);
            confirmStockReservation(cart);

            cart.setActive(false);
            cartService.save(cart);

            outboxService.createOrderEvent(orderSaved);

            order.setStatus(OrderStatus.CONFIRMED);
        } else {
            releaseStockReservation(cart);

            cart.getPayment().setStatus(PaymentStatus.FAILED);
            order.setStatus(OrderStatus.CANCELLED);
        }
        return orderSaved;
    }

    private Cart getValidatedCart(Long userId) {
        Cart cart = cartService.getActiveCartByUserId(userId);

        if (cart.getCartItems().isEmpty()) {
            throw new IllegalStateException("Cart is empty");
        }

        validateAddress(cart.getBillingAddress(), "Billing");

        validateAddress(cart.getShippingAddress(), "Shipping");

        validatePayment(cart.getPayment());

        validateStockAvailability(cart);

        return cart;
    }

    private void validateStockAvailability(Cart cart){
        cart.getCartItems().forEach(item -> {
            if(item.getProduct().getSkus().isEmpty()){
                throw new IllegalArgumentException("Product sku not found in the cart");
            }
            item.getProduct().getSkus().forEach(sku ->{
                stockService.validateSkuAvailability(sku, item.getQuantity());
            });
        });
    }

    private void validateAddress(Address address, String type) {
        if (address == null || address.getId() == null) {
            throw new IllegalArgumentException(type + " address is not set");
        }

        if (!addressRepository.existsById(address.getId())) {
            throw new IllegalArgumentException(type + " address not found");
        }
    }

    private void validatePayment(Payment payment) {
        if (payment == null || payment.getId() == null) {
            throw new IllegalArgumentException("Payment method is not set");
        }

        if (!paymentRepository.existsById(payment.getId())) {
            throw new IllegalArgumentException("Payment method not found");
        }
    }

    private Order createOrderFromCart(Cart cart) {
        Order order = new Order();
        order.setUser(cart.getUser());
        order.setStatus(OrderStatus.PENDING_PROCESSING);
        order.setShippingAddress(cart.getShippingAddress());
        order.setBillingAddress(cart.getBillingAddress());
        order.setPayment(cart.getPayment());

        Map<Seller, SellerOrder> sellerOrderMap = new HashMap<>();

        for (CartItems item: cart.getCartItems()) {
            Seller seller = item.getProduct().getSeller();
            SellerOrder sellerOrder = sellerOrderMap.computeIfAbsent(seller, s -> {
                SellerOrder so = new SellerOrder();
                so.setOrder(order);
                so.setSeller(seller);
                so.setStatus(OrderStatus.PREPARING);
                return so;
            });

            OrderItems orderItem = new OrderItems();
            orderItem.setOrder(order);
            orderItem.setProduct(item.getProduct());
            orderItem.setQuantity(item.getQuantity());
            orderItem.setItemValue(item.getItemValue());
            orderItem.calculateItemValue();

            orderItem.setSeller(seller);
            orderItem.setSellerOrder(sellerOrder);

            order.getOrderItems().add(orderItem);
            sellerOrder.getItems().add(orderItem);
        }
        order.updateOrderTotal();
        order.setSellerOrders(new ArrayList<>(sellerOrderMap.values()));
        return order;
    }

    private void reserveStockForCart(Cart cart) {
        for (CartItems item: cart.getCartItems()) {
            for (ProductSku sku : item.getProduct().getSkus()) {
                stockService.reserveStock(sku, item.getQuantity());
            }
        }
    }

    private void confirmStockReservation(Cart cart) {
        for (CartItems item: cart.getCartItems()) {
            for (ProductSku sku : item.getProduct().getSkus()) {
                stockService.confirmReservation(sku, item.getQuantity());
            }
        }
    }

    private void releaseStockReservation(Cart cart) {
        for (CartItems item: cart.getCartItems()) {
            for (ProductSku sku : item.getProduct().getSkus()) {
                stockService.releaseReservation(sku, item.getQuantity());
            }
        }
    }

}
