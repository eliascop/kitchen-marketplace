package br.com.kitchen.api.mapper;

import br.com.kitchen.api.dto.*;
import br.com.kitchen.api.model.Cart;
import br.com.kitchen.api.model.CartItems;
import br.com.kitchen.api.model.Product;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class CartMapper {

    public static CartDTO toResponseDTO(Cart cart) {
        Set<ShippingDTO> shippingSet = cart.getCartItems().stream()
                .map(item -> item.getProduct().getSeller())
                .filter(Objects::nonNull)
                .map(seller -> ShippingDTO.builder()
                        .seller(SellerDTO.builder()
                                .id(seller.getId())
                                .storeName(seller.getStoreName())
                                .build())
                        .build())
                .collect(Collectors.toCollection(LinkedHashSet::new));

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
                ProductDTO.builder()
                        .id(cartItems.getId())
                        .name(cartItems.getProduct().getName())
                        .sku(cartItems.getProduct().getSkus().get(0).getSku())
                        .seller(SellerMapper.toDTO(cartItems.getProduct().getSeller()))
                        .build(),
                cartItems.getQuantity(),
                cartItems.getItemValue()
        );
    }

    public static Cart toEntity(CartDTO cartDTO) {
        Cart cart = new Cart();
        cart.setId(cartDTO.getId());
        List<CartItems> items = cartDTO.getCartItems().stream()
                .map(CartMapper::itemToEntity)
                .collect(Collectors.toList());

        cart.setCartItems(items);
        cart.setCreation(cartDTO.getCreation());
        cart.setCartTotal(cartDTO.getCartTotal());
        return cart;
    }

    private static CartItems itemToEntity(CartItemsDTO dto) {
        CartItems item = new CartItems();
        item.setId(dto.getId());

        Product product = new Product();
        product.setId(dto.getProductDTO().getId());
        product.setName(dto.getProductDTO().getName());
        item.setProduct(product);

        item.setQuantity(dto.getQuantity());
        item.setItemValue(dto.getItemValue());

        return item;
    }
}