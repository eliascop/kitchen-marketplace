package br.com.kitchen.api.controller;

import br.com.kitchen.api.dto.response.OrderResponseDTO;
import br.com.kitchen.api.mapper.OrderMapper;
import br.com.kitchen.api.model.Order;
import br.com.kitchen.api.security.UserPrincipal;
import br.com.kitchen.api.service.OrderService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/orders/v1")
@SecurityRequirement(name = "bearer-key")
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDTO> getOrderById(@PathVariable Long id,
                                                         @AuthenticationPrincipal UserPrincipal userDetails) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(OrderMapper.toDTO(orderService.findOrderByIdAndUserId(id, userDetails.user().getId())));
    }

    @GetMapping("/search")
    public ResponseEntity<List<OrderResponseDTO>> findOrdersByUserId(@RequestParam Long userId) {
        if (userId == null || userId == 0) {
            return ResponseEntity.badRequest().build();
        }
        List<Order> ordersList = orderService.findOrdersByUserId(userId);

        if (ordersList.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(OrderMapper.toDTOList(ordersList));
    }

    @PostMapping("/checkout")
    public ResponseEntity<?> checkoutFromCart(@AuthenticationPrincipal UserPrincipal userDetails) {
        try {
            Order createdOrder = orderService.checkoutFromCart(userDetails.user().getId());
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(Map.of(
                    "orderId", createdOrder.getId(),
                    "orderStatus", createdOrder.getStatus(),
                    "lastUpdate", createdOrder.getCreation()
                    ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "message", "Checkout failed",
                    "details", e.getMessage()
            ));
        }
    }

}
