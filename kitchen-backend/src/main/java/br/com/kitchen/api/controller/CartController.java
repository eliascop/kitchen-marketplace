package br.com.kitchen.api.controller;

import br.com.kitchen.api.dto.CartDTO;
import br.com.kitchen.api.dto.ProductDTO;
import br.com.kitchen.api.mapper.CartMapper;
import br.com.kitchen.api.model.Cart;
import br.com.kitchen.api.security.UserPrincipal;
import br.com.kitchen.api.service.CartService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/cart/v1")
@SecurityRequirement(name = "bearer-key")
public class CartController {

    private final CartService cartService;

    @Autowired
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public ResponseEntity<CartDTO> getCart(@AuthenticationPrincipal UserPrincipal userDetails) {
        Cart cart = cartService.getOrCreateCart(userDetails.user());

        if (cart.getCartItems() == null || cart.getCartItems().isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(CartMapper.toResponseDTO(cart));
    }

    @PostMapping("/create")
    public ResponseEntity<?> createCart(@AuthenticationPrincipal UserPrincipal userDetails,
                                           @Valid @RequestBody CartDTO cartDTO) {
        try {
            Cart cartSaved = cartService.createCart(userDetails.user(), cartDTO);

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(CartMapper.toResponseDTO(cartSaved));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "code", HttpStatus.BAD_REQUEST.value(),
                    "message", "Item not included into the cart",
                    "details", e.getMessage()
            ));
        }
    }

    @PatchMapping("product/{productId}/sku/{productSku}/quantity/{quantity}")
    public ResponseEntity<?> manageCartItems(@AuthenticationPrincipal UserPrincipal userDetails,
                                             @PathVariable Long productId,
                                             @PathVariable String productSku,
                                             @PathVariable int quantity) {
        try {
            if (productId == 0) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(Map.of(
                                "errorCode", 400,
                                "message", "Product cannot be empty"
                        ));
            }

            if ("".equals(productSku)) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(Map.of(
                                "errorCode", 400,
                                "message", "ProductId cannot be empty"
                        ));
            }
            ProductDTO productDTO = new ProductDTO(productId, "",productSku);

            if (quantity == 0) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(Map.of(
                                "errorCode", 400,
                                "message", "Quantity cannot be zero"
                        ));
            }

            Cart cartSaved = cartService.manageItems(userDetails.user(), productId, quantity);

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(CartMapper.toResponseDTO(cartSaved));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "code", HttpStatus.BAD_REQUEST.value(),
                    "message", "Item not included into the cart",
                    "details", e.getMessage()
            ));
        }
    }

    @DeleteMapping("/remove")
    public ResponseEntity<?> removeItemFromCart(@AuthenticationPrincipal UserPrincipal userDetails,
                                                @RequestParam Long productId) {
        cartService.removeItem(userDetails.user(), productId);
        return ResponseEntity.ok("Item removed from cart");
    }

    @DeleteMapping("/clear")
    public ResponseEntity<?> clearCart(@AuthenticationPrincipal UserPrincipal userDetails) {
        cartService.clearCart(userDetails.user());
        return ResponseEntity.ok("Cart cleared");
    }
}
