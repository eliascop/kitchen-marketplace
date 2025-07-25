package br.com.kitchen.api.controller;

import br.com.kitchen.api.model.Order;
import br.com.kitchen.api.record.CustomUserDetails;
import br.com.kitchen.api.service.OrderService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

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
    public ResponseEntity<Order> getOrderById(@PathVariable Long id,
                                              @AuthenticationPrincipal CustomUserDetails userDetails) {
        return orderService.findOrderByIdAndUserId(id, userDetails.user().getId())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public ResponseEntity<List<Order>> findOrdersByUserId(@RequestParam Long userId) {
        if (userId == null || userId == 0) {
            return ResponseEntity.badRequest().build();
        }
        Optional<List<Order>> ordersList = orderService.findOrdersByUserId(userId);

        if (ordersList.isEmpty() || ordersList.get().isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(ordersList.get());
    }

    @PostMapping("/create")
    public ResponseEntity<?> newOrder(@RequestBody Order order) {
        try{
            Order orderSaved = orderService.createOrder(order);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(Map.of(
                            "code", HttpStatus.CREATED.value(),
                            "message", "Order successfully created",
                            "orderId", orderSaved.getId()
                    ));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST.value())
                    .body(Map.of(
                            "code", HttpStatus.BAD_REQUEST.value(),
                            "message", "An error occurred while creating order",
                            "details", e.getMessage()
                    ));
        }
    }

    @PostMapping("/checkout")
    public ResponseEntity<?> checkoutFromCart(@RequestParam Long cartId,
                                              @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            Order createdOrder = orderService.checkoutFromCart(cartId, userDetails.user().getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "code", HttpStatus.CREATED.value(),
                    "message", "Order successfully created from cart",
                    "orderId", createdOrder.getId()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "code", HttpStatus.BAD_REQUEST.value(),
                    "message", "Checkout failed",
                    "details", e.getMessage()
            ));
        }
    }

}
