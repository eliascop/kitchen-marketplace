package br.com.kitchen.api.controller;

import br.com.kitchen.api.mapper.CartMapper;
import br.com.kitchen.api.model.Cart;
import br.com.kitchen.api.model.Product;
import br.com.kitchen.api.record.CustomUserDetails;
import br.com.kitchen.api.service.CartService;
import br.com.kitchen.api.service.ProductService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/cart/v1")
@SecurityRequirement(name = "bearer-key")
public class CartController {

    private final CartService cartService;
    private final ProductService productService;

    @Autowired
    public CartController(CartService cartService, ProductService productService) {
        this.cartService = cartService;
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<Cart> getCart(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Cart cart = cartService.getOrCreateCart(userDetails.user());

        if (cart.getCartItems() == null || cart.getCartItems().isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(cart);
    }

    @PatchMapping("{productId}/quantity/{quantity}")
    public ResponseEntity<?> manageCartItems(@AuthenticationPrincipal CustomUserDetails userDetails,
                                           @PathVariable Long productId,
                                           @PathVariable int quantity) {
        try {
            if (productId == 0) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(Map.of(
                        "errorCode", 400,
                        "message", "ProductId cannot be empty"
                        ));
            }

            if (quantity == 0) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(Map.of(
                        "errorCode", 400,
                        "message", "Quantity cannot be zero"
                ));
            }


            Optional<Product> productOpt = productService.findById(productId);
            if (productOpt.isEmpty()) {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(Map.of(
                                "errorCode", 404,
                                "message", "Product not found"
                        ));
            }

            Cart cartSaved = cartService.manageItems(userDetails.user(), productOpt.get(), quantity);

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
    public ResponseEntity<?> removeItemFromCart(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                @RequestParam Long productId) {
        cartService.removeItem(userDetails.user(), productId);
        return ResponseEntity.ok("Item removed from cart");
    }

    @DeleteMapping("/clear")
    public ResponseEntity<?> clearCart(@AuthenticationPrincipal CustomUserDetails userDetails) {
        cartService.clearCart(userDetails.user());
        return ResponseEntity.ok("Cart cleared");
    }
}
