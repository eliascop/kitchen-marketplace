package br.com.kitchen.api.controller;

import br.com.kitchen.api.dto.OrderDTO;
import br.com.kitchen.api.model.User;
import br.com.kitchen.api.model.WalletTransaction;
import br.com.kitchen.api.security.UserPrincipal;
import br.com.kitchen.api.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@Transactional
@ActiveProfiles("test")
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    private User mockUser;

    @BeforeEach
    void setUp() {
        mockUser = userRepository.findById(1L).orElseThrow();

        UserPrincipal userDetails = new UserPrincipal(mockUser);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities())
        );

//        doNothing().when(orderKafkaProducer).sendNotification(any());
//        doNothing().when(walletKafkaProducer).sendNotification(any());
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

/*    @Test
    void shouldReturnNotFoundWhenOrderDoesNotExist() throws Exception {
        mockMvc.perform(get("/orders/v1/{id}", 9999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnOkWhenSearchByUserId() throws Exception {
        mockMvc.perform(get("/orders/v1/search")
                        .param("userId", String.valueOf(mockUser.getId())))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnNoContentWhenUserHasNoOrders() throws Exception {
        mockMvc.perform(get("/orders/v1/search")
                        .param("userId", "999"))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturnBadRequestWhenSearchWithoutUserId() throws Exception {
        mockMvc.perform(get("/orders/v1/search"))
                .andExpect(status().isBadRequest());
    } */
}
