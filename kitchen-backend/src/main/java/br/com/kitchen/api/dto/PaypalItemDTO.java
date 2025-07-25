package br.com.kitchen.api.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaypalItemDTO {
    private String name;
    private String description;
    private UnitAmountDTO unit_amount;
    private String quantity;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UnitAmountDTO {
        private String currency_code;
        private String value;
    }
}
