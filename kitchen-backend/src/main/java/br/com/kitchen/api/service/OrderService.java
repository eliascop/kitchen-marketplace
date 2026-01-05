package br.com.kitchen.api.service;

import br.com.kitchen.api.enumerations.OrderStatus;
import br.com.kitchen.api.enumerations.PaymentStatus;
import br.com.kitchen.api.enumerations.Role;
import br.com.kitchen.api.model.*;
import br.com.kitchen.api.repository.jpa.AddressRepository;
import br.com.kitchen.api.repository.jpa.OrderRepository;
import br.com.kitchen.api.repository.jpa.PaymentRepository;
import br.com.kitchen.api.service.payment.PaymentProvider;
import br.com.kitchen.api.service.payment.PaymentProviderFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

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

    public Order findOrderByPaymentId(Long paymentId) {
        return orderRepository.findOrderByPaymentId(paymentId)
                .orElseThrow(() -> new RuntimeException("Order not found!"));
    }

    @Transactional
    public Order checkoutFromCart(Long userId) throws Exception{
        Cart cart = getValidatedCart(userId);
        Order order = createOrderFromCart(cart);

        stockService.reserveStockFromCart(cart);

        PaymentProvider paymentProvider = paymentProviderFactory.getProvider(cart.getPayment().getMethod().name());
        String paymentStatus = paymentProvider.confirmPayment(cart.getPayment().getProviderOrderId());
        cart.getPayment().setStatus(PaymentStatus.PENDING);

        if("DECLINED".equals(paymentStatus) || "DENIED".equals(paymentStatus)) {
            stockService.releaseStockFromCart(cart);
            cart.getPayment().setStatus(PaymentStatus.ERROR);
            order.setStatus(OrderStatus.CANCELLED);
        }else if("COMPLETED".equals(paymentStatus) || "APPROVED".equals(paymentStatus) || "SUCCESS".equals(paymentStatus)) {
            stockService.confirmStockFromCart(cart);
            cart.getPayment().setStatus(PaymentStatus.PAID);
            order.setStatus(OrderStatus.CREATED);
        }else {
            System.err.println("Unhandled PayPal payment status: " + paymentStatus);
        }
        cart.setActive(false);
        cartService.save(cart);
        Order orderSaved = orderRepository.save(order);
        outboxService.createOrderEvent(orderSaved, "ORDER_CREATED");
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
            if(item.getProductSku() == null){
                throw new IllegalArgumentException("Product sku not found in the cart");
            }
            stockService.validateSkuAvailability(item.getProductSku(), item.getQuantity());
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
        order.setStatus(OrderStatus.CREATED);
        order.setShippingAddress(cart.getShippingAddress());
        order.setBillingAddress(cart.getBillingAddress());
        order.setPayment(cart.getPayment());

        Map<Seller, Shipping> shippingBySeller =
                cart.getShippingMethods()
                        .stream()
                        .collect(Collectors.toMap(Shipping::getSeller, Function.identity()));

        Map<Seller, SellerOrder> sellerOrderMap = new HashMap<>();
        for (CartItems item: cart.getCartItems()) {
            Seller seller = item.getProductSku().getProduct().getSeller();
            SellerOrder sellerOrder = sellerOrderMap.computeIfAbsent(seller, s -> {
                Shipping shipping = shippingBySeller.get(s);
                if (shipping == null) {
                    throw new IllegalStateException("Shipping not found for the seller id: " + s.getId());
                }
                SellerOrder so = new SellerOrder();
                so.setOrder(order);
                so.setShipping(shipping);
                so.setFreightValue(shipping.getCost());
                so.setSeller(seller);
                so.setStatus(OrderStatus.PREPARING);
                return so;
            });

            OrderItems orderItem = new OrderItems();
            orderItem.setOrder(order);
            orderItem.setProductSku(item.getProductSku());
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

}
