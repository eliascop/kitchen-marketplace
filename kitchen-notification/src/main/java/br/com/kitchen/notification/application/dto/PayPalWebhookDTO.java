package br.com.kitchen.notification.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PayPalWebhookDTO {

    @JsonProperty("id")
    private String id;

    @JsonProperty("event_type")
    private String eventType;

    @JsonProperty("create_time")
    private String createTime;

    @JsonProperty("resource")
    private Resource resource;

    @Data
    public static class Resource {

        @JsonProperty("id")
        private String id;

        @JsonProperty("status")
        private String status;

        @JsonProperty("invoice_id")
        private String cartId;

        @JsonProperty("amount")
        private Amount amount;

        @JsonProperty("payer")
        private Payer payer;

        @Data
        public static class Amount {
            @JsonProperty("currency_code")
            private String currencyCode;

            @JsonProperty("value")
            private String value;
        }

        @Data
        public static class Payer {
            @JsonProperty("email_address")
            private String emailAddress;

            @JsonProperty("payer_id")
            private String payerId;
        }
    }
}
