package br.com.kitchen.api.dto;

import lombok.*;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaypalOrderDTO {
    private String intent;
    private List<PurchaseUnit> purchase_units;
    private ApplicationContext application_context;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PurchaseUnit {
        private String description;
        private Amount amount;
        private List<PaypalItemDTO> items;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Amount {
        private String currency_code;
        private String value;
        private Breakdown breakdown;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Breakdown {
        private ItemTotal item_total;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ItemTotal {
        private String currency_code;
        private String value;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ApplicationContext {
        private String return_url;
        private String cancel_url;
    }
}
