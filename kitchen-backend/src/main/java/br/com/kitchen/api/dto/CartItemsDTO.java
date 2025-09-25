package br.com.kitchen.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class CartItemsDTO {

        private Long id;
        private Long skuId;
        private String productName;
        private String description;
        private int quantity;
        @JsonProperty("value")
        private BigDecimal itemValue;
}

