package br.com.kitchen.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class CartDTO {

    private Long id;

    @JsonProperty("items")
    private List<CartItemsDTO> cartItems = new ArrayList<>();

    private LocalDateTime creation;

    private BigDecimal cartTotal;

    @JsonProperty("shipping")
    private List<ShippingDTO> shippings = new ArrayList<>();

}
