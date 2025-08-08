package br.com.kitchen.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class CartItemsDTO {

        private Long id;
        @JsonProperty("product")
        private ProductDTO productDTO;
        private int quantity;
        @JsonProperty("value")
        private BigDecimal itemValue;
}

