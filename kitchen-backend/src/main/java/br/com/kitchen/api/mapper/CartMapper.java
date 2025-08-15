package br.com.kitchen.api.mapper;

import br.com.kitchen.api.dto.CartDTO;
import br.com.kitchen.api.dto.CartItemsDTO;
import br.com.kitchen.api.dto.ProductDTO;
import br.com.kitchen.api.model.Cart;
import br.com.kitchen.api.model.CartItems;
import br.com.kitchen.api.model.Product;

import java.util.List;
import java.util.stream.Collectors;

public class CartMapper {

    public static CartDTO toResponseDTO(Cart cart) {
        return new CartDTO(
                cart.getId(),
                cart.getCartItems().stream()
                        .map(CartMapper::itemToResponseDTO)
                        .collect(Collectors.toList()),
                cart.getCartItems().size(),
                cart.getCreation(),
                cart.getCartTotal(),
                ShippingMapper.toDTOList(cart.getShipping())
        );
    }

    private static CartItemsDTO itemToResponseDTO(CartItems cartItems) {
        return new CartItemsDTO(
                cartItems.getId(),
                ProductDTO.builder()
                        .id(cartItems.getId())
                        .name(cartItems.getProduct().getName())
                        .sku(cartItems.getProduct().getSkus().get(0).getSku()).build(),
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
