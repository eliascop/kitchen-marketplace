package br.com.kitchen.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@Builder
public class CartDTO {

    private Long id;

    @JsonProperty("items")
    private List<CartItemsDTO> cartItems;

    @JsonProperty("totalItems")
    private int cartTotalItems;

    private LocalDateTime creation;

    private BigDecimal cartTotal;

    private Long shippingAddressId;

    private Long billingAddressId;

    private Set<ShippingDTO> shippingMethod;

}




