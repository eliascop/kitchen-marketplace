package br.com.kitchen.api.service;

import br.com.kitchen.api.enumerations.OrderStatus;
import br.com.kitchen.api.enumerations.PaymentMethod;
import br.com.kitchen.api.enumerations.PaymentStatus;
import br.com.kitchen.api.model.*;
import br.com.kitchen.api.repository.AddressRepository;
import br.com.kitchen.api.repository.OrderRepository;
import br.com.kitchen.api.repository.OutboxRepository;
import br.com.kitchen.api.repository.PaymentRepository;
import br.com.kitchen.api.service.payment.PaymentProvider;
import br.com.kitchen.api.service.payment.PaymentProviderFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collections;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class OrderServiceTest {

    @InjectMocks
    private OrderService orderService;

    @Mock private PaymentProviderFactory paymentProviderFactory;
    @Mock private AddressRepository addressRepository;
    @Mock private StockService stockService;
    @Mock private OrderRepository orderRepository;
    @Mock private PaymentRepository paymentRepository;
    @Mock private OutboxRepository outboxRepository;
    @Mock private CartService cartService;
    @Mock private PaymentProvider paymentProvider;

    private Cart mockCart;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        User user = User.builder().id(10L).name("Jonas").build();
        Address address = Address.builder().id(1L).build();
        Payment payment = Payment.builder()
                .id(5L)
                .method(PaymentMethod.PAYPAL)
                .providerOrderId("prov-123")
                .status(PaymentStatus.PENDING)
                .build();

        ProductSku sku = new ProductSku();
        sku.setId(99L);
        Product product = new Product();
        product.setId(100L);
        product.setSeller(new Seller());
        product.setSkus(Collections.singletonList(sku));

        CartItems item = new CartItems();
        item.setProduct(product);
        item.setQuantity(2);
        item.setItemValue(new BigDecimal("20.00"));

        mockCart = new Cart();
        mockCart.setId(50L);
        mockCart.setUser(user);
        mockCart.setBillingAddress(address);
        mockCart.setShippingAddress(address);
        mockCart.setPayment(payment);
        mockCart.getCartItems().add(item);

        when(cartService.getActiveCartByUserId(user.getId())).thenReturn(mockCart);
        when(addressRepository.existsById(address.getId())).thenReturn(true);
        when(paymentRepository.existsById(payment.getId())).thenReturn(true);

        when(paymentProviderFactory.getProvider("PAYPAL")).thenReturn(paymentProvider);
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));
    }
/*
    @Test
    void checkoutFromCart_shouldConfirmOrder_whenPaymentCompleted() throws Exception {
        when(paymentProvider.confirmPayment("prov-123")).thenReturn("COMPLETED");

        Order order = orderService.checkoutFromCart(mockCart.getUser().getId());

        assertThat(order.getStatus()).isEqualTo(OrderStatus.CONFIRMED);
        assertThat(order.getPayment().getStatus()).isEqualTo(PaymentStatus.SUCCESS);

        verify(stockService, times(1)).reserveStock(any(), eq(2));
        verify(stockService, times(1)).confirmReservation(any(), eq(2));
        verify(cartService, times(1)).save(mockCart);
        verify(outboxRepository, times(1)).save(any(OutboxEvent.class));
    }

    @Test
    void shouldThrowExceptionWhenUserDoesNotExist() {
        User nonExistentUser = new User();
        nonExistentUser.setId(999L);

        Order order = OrderTestBuilder.buildValidOrder(nonExistentUser);
        order.setUser(nonExistentUser);

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                orderService.createOrder(order));

        assertEquals("User not found", ex.getMessage());
    } */
}
