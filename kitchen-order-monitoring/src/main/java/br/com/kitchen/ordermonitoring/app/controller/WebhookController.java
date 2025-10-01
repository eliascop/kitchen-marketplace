package br.com.kitchen.ordermonitoring.app.controller;

import br.com.kitchen.ordermonitoring.app.dto.PayPalWebhookDTO;
import br.com.kitchen.ordermonitoring.app.service.WebhookService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payments")
public class WebhookController {

    private final WebhookService webhookService;

    public WebhookController(WebhookService webhookService) {
        this.webhookService = webhookService;
    }

    @PostMapping("/paypal")
    public ResponseEntity<Void> handleWebhook(@RequestBody PayPalWebhookDTO webhookDTO) {
        webhookService.processWebhook(webhookDTO);
        return ResponseEntity.ok().build();
    }
}

