package br.com.kitchen.api.service.payment.paypal;

import br.com.kitchen.api.builder.PaypalOrderBuilder;
import br.com.kitchen.api.enumerations.PaymentMethod;
import br.com.kitchen.api.enumerations.PaymentStatus;
import br.com.kitchen.api.model.Cart;
import br.com.kitchen.api.model.Payment;
import br.com.kitchen.api.repository.PaymentRepository;
import br.com.kitchen.api.service.GenericService;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PaypalClient extends GenericService<Payment, Long> {

    @Value("${paypal.client.id}")
    private String clientId;

    @Value("${paypal.client.secret}")
    private String clientSecret;

    @Value("${paypal.base.url}")
    private String baseUrl;

    private final RestTemplate restTemplate = new RestTemplate();
    private final PaymentRepository paymentRepository;

    public PaypalClient(PaymentRepository paymentRepository) {
        super(paymentRepository, Payment.class);
        this.paymentRepository = paymentRepository;
    }

    public String doPayment(Cart cart) {
        String accessToken = obtainAccessToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);

        String cartJson = PaypalOrderBuilder.buildOrderJson(cart);

        HttpEntity<String> request = new HttpEntity<>(cartJson, headers);

        ResponseEntity<JsonNode> response = restTemplate.exchange(
                baseUrl + "/v2/checkout/orders",
                HttpMethod.POST,
                request,
                JsonNode.class
        );

        JsonNode body = response.getBody();
        if (body == null || !body.has("links")) {
            throw new IllegalStateException("PayPal order response is invalid.");
        }

        for (JsonNode link : body.get("links")) {
            if ("approve".equals(link.get("rel").asText())) {
                return link.get("href").asText();
            }
        }

        throw new IllegalStateException("Approval link not found in PayPal response.");
    }

    public String confirmPayment(String orderId) {
        String accessToken = obtainAccessToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);

        HttpEntity<String> captureRequest = new HttpEntity<>(null, headers);

        ResponseEntity<JsonNode> response = restTemplate.exchange(
                baseUrl + "/v2/checkout/orders/" + orderId + "/capture",
                HttpMethod.POST,
                captureRequest,
                JsonNode.class
        );

        JsonNode body = response.getBody();
        if (body == null || !body.has("status")) {
            throw new IllegalStateException("Invalid capture response from PayPal.");
        }

        return body.get("status").asText();
    }

    private String obtainAccessToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setBasicAuth(clientId, clientSecret);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "client_credentials");

        HttpEntity<MultiValueMap<String, String>> tokenRequest = new HttpEntity<>(body, headers);

        ResponseEntity<JsonNode> response = restTemplate.exchange(
                baseUrl + "/v1/oauth2/token",
                HttpMethod.POST,
                tokenRequest,
                JsonNode.class
        );

        JsonNode responseBody = response.getBody();
        if (responseBody == null || !responseBody.has("access_token")) {
            throw new IllegalStateException("Failed to obtain access token from PayPal.");
        }

        return responseBody.get("access_token").asText();
    }

    public boolean isValidSecureToken(String token) {
        return !this.findByField("secureToken", token).isEmpty();
    }

    @Transactional
    public void createPayment(Cart cart) {
        Payment payment = Payment.builder()
                .method(PaymentMethod.PAYPAL)
                .status(PaymentStatus.PENDING)
                .amount(cart.getCartTotal())
                .secureToken(UUID.randomUUID().toString().replace("-", "").substring(0, 16))
                .cart(cart)
                .createdAt(LocalDateTime.now())
                .build();
        cart.setPayment(payment);
        paymentRepository.save(payment);
    }

}
