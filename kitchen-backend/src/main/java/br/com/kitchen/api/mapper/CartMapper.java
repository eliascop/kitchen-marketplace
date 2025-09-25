package br.com.kitchen.api.mapper;

import br.com.kitchen.api.dto.CartDTO;
import br.com.kitchen.api.dto.CartItemsDTO;
import br.com.kitchen.api.dto.SellerDTO;
import br.com.kitchen.api.dto.ShippingDTO;
import br.com.kitchen.api.model.Cart;
import br.com.kitchen.api.model.CartItems;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class CartMapper {

    public static CartDTO toResponseDTO(Cart cart) {
        Set<ShippingDTO> shippingSet = new LinkedHashSet<>();
        if(cart.getShippingMethods().isEmpty()) {
            shippingSet = cart.getCartItems().stream()
                    .map(item -> item.getProductSku().getProduct().getSeller())
                    .filter(Objects::nonNull)
                    .map(seller -> ShippingDTO.builder()
                            .seller(SellerDTO.builder()
                                    .id(seller.getId())
                                    .storeName(seller.getStoreName())
                                    .build())
                            .build())
                    .collect(Collectors.toCollection(LinkedHashSet::new));
        }else{
            shippingSet = ShippingMapper.toDTOList(cart.getShippingMethods());
        }

        return CartDTO.builder()
                .id(cart.getId())
                .cartItems(cart.getCartItems().stream()
                        .map(CartMapper::itemToResponseDTO)
                        .collect(Collectors.toList()))
                .cartTotalItems(cart.getCartItems().size())
                .creation(cart.getCreation())
                .cartTotal(cart.getCartTotal())
                .shippingAddressId(cart.getShippingAddress() == null ? null : cart.getShippingAddress().getId())
                .billingAddressId(cart.getBillingAddress() == null ? null : cart.getBillingAddress().getId())
                .shippingMethod(shippingSet)
                .build();
    }

    private static CartItemsDTO itemToResponseDTO(CartItems cartItems) {
        return new CartItemsDTO(
                cartItems.getId(),
                ProductMapper.toProductResponseDTO(cartItems.getProductSku()),
                cartItems.getQuantity(),
                cartItems.getItemValue()
        );
    }

}