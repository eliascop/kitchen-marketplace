package br.com.kitchen.api.controller;

import br.com.kitchen.api.enumerations.OrderStatus;
import br.com.kitchen.api.model.Order;
import br.com.kitchen.api.model.OrderItems;
import br.com.kitchen.api.model.Product;
import br.com.kitchen.api.model.User;
import br.com.kitchen.api.security.UserPrincipal;
import br.com.kitchen.api.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@Transactional
@ActiveProfiles("test")
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderService orderService;

    private UserPrincipal userPrincipal;

    private User mockUser;

    private Order mockOrder;

    @BeforeEach
    void setUp() {
        mockUser = User.builder()
                .id(3L)
                .login("jonas.cordeiro")
                .email("jonas.cordeiro@kitchen.com")
                .phone("11984031062")
                .name("Jonas Cordeiro Oliveira")
                .password("user-12345")
                .build();

        Product mockProduct = new Product();
        mockProduct.setId(3L);
        mockProduct.setPrice(new BigDecimal("15.50"));
        mockProduct.setActive(true);
        mockProduct.setName("test");
        mockProduct.setDescription("test");

        OrderItems mockOrderItem = new OrderItems();
        mockOrderItem.setId(3L);
//        mockOrderItem.setProductSku(mockProductSku);
        mockOrderItem.setQuantity(1);
        mockOrderItem.setItemValue(new BigDecimal("35.50"));

        mockOrder = new Order();
        mockOrder.setId(2L);
        mockOrder.setStatus(OrderStatus.PENDING);
        mockOrder.setCreation(LocalDateTime.now());
        mockOrder.getOrderItems().add(mockOrderItem);

        userPrincipal = new UserPrincipal(mockUser,null);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.getAuthorities())
        );
    }
/*
    @Test
    void testGetOrderById_withoutUserId() throws Exception {
            mockMvc.perform(get("/orders/v1/{id}",1)
                        .with(authentication(
                                new UsernamePasswordAuthenticationToken(
                                        userPrincipal, null, userPrincipal.getAuthorities()
                                )
                        )))

                .andExpect(status().isBadRequest());
    }

    @Test
    void testFindOrdersByUserId() throws Exception {
        List<Order> mockList = List.of(mockOrder);
        when(orderService.findOrdersByUserId(mockUser)).thenReturn(mockList);
        mockMvc.perform(get("/orders/v1/search")
                        .param("userId", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(2L));
    }

    @Test
    void testFindOrdersByUserId_noContent() throws Exception {
        mockMvc.perform(get("/orders/v1/search")
                        .param("userId", "99"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testCheckoutFromCart_success() throws Exception {

        when(orderService.checkoutFromCart(mockUser.getId())).thenReturn(mockOrder);

        mockMvc.perform(post("/orders/v1/checkout")
                        .with(authentication(
                                new UsernamePasswordAuthenticationToken(
                                        userPrincipal, null, userPrincipal.getAuthorities()
                                )
                        ))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderId").value(2L))
                .andExpect(jsonPath("$.orderStatus").value("PENDING"));
    }*/
}