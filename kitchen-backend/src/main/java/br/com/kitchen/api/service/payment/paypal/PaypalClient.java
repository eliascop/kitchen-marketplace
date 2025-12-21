package br.com.kitchen.api.service.payment.paypal;

import br.com.kitchen.api.builder.PaypalOrderBuilder;
import br.com.kitchen.api.model.Cart;
import br.com.kitchen.api.model.Payment;
import br.com.kitchen.api.repository.jpa.PaymentRepository;
import br.com.kitchen.api.service.GenericService;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.UUID;

@Service
public class PaypalClient extends GenericService<Payment, Long> {

    @Value("${paypal.client.id}")
    private String clientId;

    @Value("${paypal.client.secret}")
    private String clientSecret;

    @Value("${paypal.base.url}")
    private String baseUrl;

    private final RestTemplate restTemplate;
    private final PaypalOrderBuilder paypalOrderBuilder;

    public PaypalClient(PaymentRepository paymentRepository,
                        RestTemplate restTemplate,
                        PaypalOrderBuilder paypalOrderBuilder) {
        super(paymentRepository, Payment.class);
        this.restTemplate = restTemplate;
        this.paypalOrderBuilder = paypalOrderBuilder;
    }

    public Map<String, String> doPayment(Cart cart) {
        String accessToken = obtainAccessToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);

        String cartJson = paypalOrderBuilder.buildOrderJson(cart);

        HttpEntity<String> request = new HttpEntity<>(cartJson, headers);

        ResponseEntity<JsonNode> response = restTemplate.exchange(
                baseUrl + "/v2/checkout/orders",
                HttpMethod.POST,
                request,
                JsonNode.class
        );

        JsonNode body = response.getBody();
        if (body == null || !body.has("id")) {
            throw new IllegalStateException("PayPal order response is invalid.");
        }

        String orderId = body.get("id").asText();
        String approvalLink = null;

        for (JsonNode link : body.get("links")) {
            if ("approve".equals(link.get("rel").asText())) {
                approvalLink = link.get("href").asText();
                break;
            }
        }

        if (approvalLink == null) {
            throw new IllegalStateException("Approval link not found in PayPal response.");
        }

        return Map.of(
                "paypalOrderId", orderId,
                "approvalLink", approvalLink
        );
    }


    public String confirmPayment(String providerOrderId) {
        String accessToken = obtainAccessToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);
        headers.set("PayPal-Request-Id", UUID.randomUUID().toString());

        HttpEntity<String> captureRequest = new HttpEntity<>(null, headers);

        ResponseEntity<JsonNode> response = restTemplate.exchange(
                baseUrl + "/v2/checkout/orders/" + providerOrderId + "/capture",
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

}
