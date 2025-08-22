package br.com.kitchen.api.controller;

import br.com.kitchen.api.enumerations.PaymentStatus;
import br.com.kitchen.api.security.UserPrincipal;
import br.com.kitchen.api.service.PaymentService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/payment")
@SecurityRequirement(name = "bearer-key")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService){
        this.paymentService = paymentService;
    }

    @Value("${app.base.url}")
    private String urlCheckout;

    @PostMapping("/{provider}")
    public ResponseEntity<?> initiatePayment(@AuthenticationPrincipal UserPrincipal userDetails,
                                               @PathVariable String provider) {
        try {
            String redirectUrl = paymentService.initiatePayment(provider, userDetails.user().getId());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("redirectUrl", redirectUrl));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "code", HttpStatus.BAD_REQUEST.value(),
                    "message", "Error on initiate payment: " + ex.getMessage()
            ));
        }
    }

    @GetMapping("/{provider}/success")
    public ResponseEntity<?> onSuccess(@PathVariable String provider,
                          @RequestParam("token") String providerToken,
                          @RequestParam("secureToken") String secureToken,
                          @RequestParam("cartId") Long cartId) {
        try {
            PaymentStatus paymentResult = paymentService.processSuccess(provider, providerToken, secureToken, cartId);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(Map.of(
                            "code", HttpStatus.OK.value(),
                            "message", paymentResult.toString()
                    ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                    .body(Map.of(
                            "code", HttpStatus.NOT_ACCEPTABLE.value(),
                            "message", "Payment was not validated."
                    ));
        }
    }

    /*
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
                    .append("/cart?paymentStatus=")
                    .append(encode(status));

            System.out.println("URL RETORNO>>>>>>"+url);
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
    */
}
