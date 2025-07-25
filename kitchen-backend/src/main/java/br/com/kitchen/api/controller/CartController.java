package br.com.kitchen.api.controller;

import br.com.kitchen.api.model.Cart;
import br.com.kitchen.api.model.Product;
import br.com.kitchen.api.record.CustomUserDetails;
import br.com.kitchen.api.service.CartService;
import br.com.kitchen.api.service.ProductService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/add")
    public ResponseEntity<?> addItemToCart(@AuthenticationPrincipal CustomUserDetails userDetails,
                                           @RequestParam Long productId,
                                           @RequestParam int quantity) {
        Optional<Product> productOpt = productService.findById(productId);
        if (productOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Product not found");
        }

        cartService.addItem(userDetails.user(), productOpt.get(), quantity);
        return ResponseEntity.ok("Item added to cart");
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
