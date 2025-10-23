package br.com.kitchen.api.dto.request;

import br.com.kitchen.api.dto.CartItemsDTO;
import lombok.Data;

import java.util.List;

@Data
public class ApplyCouponRequestDTO {
    private String couponCode;
    private Long buyerId;
    private List<CartItemsDTO> items;
    private String currency;
}
