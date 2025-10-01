package br.com.kitchen.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaypalOrderDTO {
    private String intent;
    @JsonProperty("purchase_units")
    private List<PurchaseUnit> purchaseUnits;
    @JsonProperty("application_context")
    private ApplicationContext applicationContext;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PurchaseUnit {
        private String description;
        private Amount amount;
        @JsonProperty("invoice_id")
        private String invoiceId;
        private List<PaypalItemDTO> items;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Amount {
        @JsonProperty("currency_code")
        private String currencyCode;
        private String value;
        private Breakdown breakdown;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Breakdown {
        @JsonProperty("item_total")
        private ItemTotal itemTotal;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ItemTotal {
        @JsonProperty("currency_code")
        private String currencyCode;
        private String value;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ApplicationContext {
        @JsonProperty("brand_name")
        private String brandName;

        @JsonProperty("landing_page")
        private String landingPage;

        @JsonProperty("user_action")
        private String userAction;

        @JsonProperty("return_url")
        private String returnUrl;

        @JsonProperty("cancel_url")
        private String cancelUrl;
    }
}
