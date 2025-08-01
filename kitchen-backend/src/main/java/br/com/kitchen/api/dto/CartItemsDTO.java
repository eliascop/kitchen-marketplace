package br.com.kitchen.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@AllArgsConstructor
@Data
public class CartItemsDTO {

        private Long id;
        private ProductDTO product;
        private int quantity;
        @JsonProperty("value")
        private BigDecimal itemValue;

}
