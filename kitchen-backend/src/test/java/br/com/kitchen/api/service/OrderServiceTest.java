package br.com.kitchen.api.service;

import br.com.kitchen.api.dto.OrderDTO;
import br.com.kitchen.api.model.Order;
import br.com.kitchen.api.model.User;
import br.com.kitchen.api.model.WalletTransaction;
import br.com.kitchen.api.repository.OrderRepository;
import br.com.kitchen.api.repository.UserRepository;
import br.com.kitchen.api.repository.WalletRepository;
import br.com.kitchen.api.util.OrderTestBuilder;
import br.com.kitchen.api.util.UserTestBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private WalletService walletService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WalletRepository walletRepository;

    private User mockUser;

    @BeforeEach
    void setup() {
        mockUser = userRepository.save(UserTestBuilder.buildWithoutId());
//        doNothing().when(orderKafkaProducer).sendNotification(any());
//        doNothing().when(walletKafkaProducer).sendNotification(any());
    }

    /*
    @Test
    void shouldCreateOrderWhenUserAndItemsAreValid() {
        Order orderToSave = OrderTestBuilder.buildValidOrder(mockUser);

        WalletTransaction tx = walletService.createCreditTransaction(orderToSave.getUser().getId(),orderToSave.getTotal(),"Inclusao");
        walletService.validateTransaction(tx.getId());

        Order saved = orderService.createOrder(orderToSave);

        assertNotNull(saved.getId(), "Order ID should not be null");
        assertEquals("PENDING", saved.getStatus());
        assertEquals(mockUser.getId(), saved.getUser().getId());
        assertEquals(1, saved.getOrderItems().size());
        assertEquals(BigDecimal.valueOf(100), saved.getTotal());

        verify(orderKafkaProducer).sendNotification(new OrderDTO(saved.getId(), saved.getStatus()));
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
