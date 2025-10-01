package br.com.kitchen.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaypalItemDTO {
    private String name;
    private String description;
    private String quantity;
    @JsonProperty("unit_amount")
    private UnitAmountDTO unitAmount;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UnitAmountDTO {
        @JsonProperty("currency_code")
        private String currencyCode;
        private String value;
    }
}
