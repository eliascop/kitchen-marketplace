package br.com.kitchen.api.controller;

import br.com.kitchen.api.model.Cart;
import br.com.kitchen.api.record.CustomUserDetails;
import br.com.kitchen.api.service.CartService;
import br.com.kitchen.api.service.payment.PaymentProvider;
import br.com.kitchen.api.service.payment.PaymentProviderFactory;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/payment")
@SecurityRequirement(name = "bearer-key")
public class PaymentController {

    private final CartService cartService;
    private final PaymentProviderFactory paymentProviderFactory;

    @Autowired
    public PaymentController(CartService cartService,
                             PaymentProviderFactory paymentProviderFactory){
        this.cartService = cartService;
        this.paymentProviderFactory = paymentProviderFactory;
    }

    @Value("${frontend.base.url}")
    private String urlCheckout;

    @PostMapping("/{provider}")
    public ResponseEntity<?> initiatePayment(@AuthenticationPrincipal CustomUserDetails userDetails,
                                               @PathVariable String provider) {
        try {

            PaymentProvider paymentProvider = paymentProviderFactory.getProvider(provider);
            Cart cart = cartService.getActiveCartByUserId(userDetails.user().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Cart not found"));
            paymentProvider.createPayment(cart);
            String linkForApproval = paymentProvider.initiatePayment(cart);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of(
                    "redirectUrl", linkForApproval
            ));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "code", HttpStatus.BAD_REQUEST.value(),
                    "message", "Error on initiate payment: " + ex.getMessage()
            ));
        }
    }

    @GetMapping("/{provider}/success")
    public void onSuccess(@PathVariable String provider,
                          @RequestParam("token") String token,
                          @RequestParam("cartId") Long cartId,
                          @RequestParam("secureToken") String secureToken,
                          HttpServletResponse response) {
        try {
            PaymentProvider paymentProvider = paymentProviderFactory.getProvider(provider);
            if(!paymentProvider.isValidSecureToken(secureToken)){
                redirect(response, "error", "invalid-token", "Invalid or expired Token.");
                return;
            }
            Cart cart = cartService.findById(cartId)
                    .orElseThrow(() -> new IllegalArgumentException("Cart not found"));

            cart.getPayment().setGatewayTransactionId(token);
            cartService.save(cart);

            redirect(response, "succeeded");
        } catch (Exception e) {
            redirect(response, "error", "errortocancel", e.getMessage());
        }
    }

    @GetMapping("/{provider}/cancelled")
    public void onCancelled(@PathVariable String provider,
                            @RequestParam("token") String token,
                            @RequestParam("cartId") Long cartId,
                            HttpServletResponse response) {
        try {
            redirect(response, "cancelled");
        } catch (Exception e) {
            redirect(response, "errortocancel");
        }
    }

    private void redirect(HttpServletResponse response, String status) {
        redirect(response, status, null, null);
    }

    private void redirect(HttpServletResponse response, String status, String message, String errorDetail) {
        try {
            StringBuilder url = new StringBuilder(urlCheckout)
                    .append("/payments?paymentStatus=")
                    .append(encode(status));

            if (message != null) {
                url.append("&message=").append(encode(message));
            }

            if (errorDetail != null) {
                url.append("&errorDetail=").append(encode(errorDetail));
            }

            response.sendRedirect(url.toString());
        } catch (Exception ex) {
            log.error("Failed to redirect", ex);
        }
    }

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
