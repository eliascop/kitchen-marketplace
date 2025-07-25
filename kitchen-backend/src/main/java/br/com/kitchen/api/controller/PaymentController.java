package br.com.kitchen.api.controller;

import br.com.kitchen.api.model.WalletTransaction;
import br.com.kitchen.api.record.CreditRequest;
import br.com.kitchen.api.record.CustomUserDetails;
import br.com.kitchen.api.service.WalletService;
import br.com.kitchen.api.service.payment.PaymentService;
import br.com.kitchen.api.service.payment.PaymentServiceFactory;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearer-key")
public class PaymentController {

    private final WalletService walletService;
    private final PaymentServiceFactory paymentServiceFactory;

    @Autowired
    public PaymentController(PaymentServiceFactory paymentServiceFactory,WalletService walletService){
        this.walletService = walletService;
        this.paymentServiceFactory = paymentServiceFactory;
    }

    @Value("${frontend.base.url}")
    private String urlHome;

    @PostMapping("/{provider}")
    public ResponseEntity<Map<String, Object>> initiatePayment(@PathVariable String provider,
                                                               @RequestBody CreditRequest creditRequest,
                                                               @AuthenticationPrincipal CustomUserDetails userDetails) {
        WalletTransaction transaction = null;
        try {
            transaction = walletService.createCreditTransaction(
                    userDetails.user().getId(), creditRequest.amount(), creditRequest.description());

            PaymentService paymentService = paymentServiceFactory.getService(provider);
            String approvalLink = paymentService.initiatePayment(transaction);

            return ResponseEntity.ok(Map.of(
                    "code", HttpStatus.CREATED.value(),
                    "message", approvalLink
            ));
        } catch (Exception ex) {
            if (transaction != null) {
                walletService.cancelTransaction(transaction.getId());
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "code", HttpStatus.BAD_REQUEST.value(),
                    "message", "Error on initiate payment: " + ex.getMessage()
            ));
        }
    }

    @GetMapping("/{provider}/success")
    public void onSuccess(@PathVariable String provider,
                          @RequestParam("token") String token,
                          @RequestParam("walletTxId") Long walletTxId,
                          @RequestParam("secureToken") String secureToken,
                          HttpServletResponse response) {
        try {
            if(!walletService.isValidSecureToken(secureToken)){
                redirect(response, "error", "invalid-token", "Invalid or expired Token.");
                return;
            }

            PaymentService paymentService = paymentServiceFactory.getService(provider);
            String status = paymentService.confirmPayment(token);

            if ("SUCCESS".equalsIgnoreCase(status) || "COMPLETED".equalsIgnoreCase(status)) {
                walletService.validateTransaction(walletTxId);
                redirect(response, "succeeded");
            } else {
                redirect(response, status.toLowerCase());
            }
        } catch (Exception e) {
            redirect(response, "error", "errortocancel", e.getMessage());
        }
    }

    @GetMapping("/{provider}/cancelled")
    public void onCancelled(@PathVariable String provider,
                            @RequestParam("token") String token,
                            @RequestParam("walletTxId") Long walletTxId,
                            HttpServletResponse response) {
        try {
            walletService.cancelTransaction(walletTxId);
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
            StringBuilder url = new StringBuilder(urlHome)
                    .append("/wallet?paymentStatus=")
                    .append(encode(status));

            if (message != null) {
                url.append("&message=").append(encode(message));
            }

            if (errorDetail != null) {
                url.append("&errorDetail=").append(encode(errorDetail));
            }

            response.sendRedirect(url.toString());
        } catch (Exception ignored) {
        }
    }

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
