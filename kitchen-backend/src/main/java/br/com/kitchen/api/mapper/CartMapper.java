package br.com.kitchen.api.mapper;

import br.com.kitchen.api.dto.CartDTO;
import br.com.kitchen.api.dto.CartItemsDTO;
import br.com.kitchen.api.dto.ProductDTO;
import br.com.kitchen.api.model.Cart;
import br.com.kitchen.api.model.CartItems;
import br.com.kitchen.api.model.Product;
import br.com.kitchen.api.model.User;

import java.util.List;

public class CartMapper {
    public static CartDTO toResponseDTO(Cart cart) {
        return new CartDTO(
                cart.getId(),
                cart.getUser().getId(),
                cart.getCartItems().stream()
                        .map(CartMapper::itemToResponseDTO)
                        .toList(),
                cart.getCreation(),
                cart.getActive(),
                cart.getCartTotal()
        );
    }

    private static CartItemsDTO itemToResponseDTO(CartItems cartItems) {
        return new CartItemsDTO(
                cartItems.getId(),
                new ProductDTO(cartItems.getProduct().getId(),
                        cartItems.getProduct().getName()),
                cartItems.getQuantity(),
                cartItems.getItemValue()
        );
    }

    public static Cart toEntity(CartDTO cartDTO) {
        Cart cart = new Cart();
        cart.setUser(new User(cartDTO.getUserId()));
        cart.setId(cartDTO.getId());
        List<CartItems> items = cartDTO.getCartItems().stream()
                .map(CartMapper::itemToEntity)
                .toList();

        cart.setCartItems(items);
        cart.setCreation(cartDTO.getCreation());
        cart.setActive(cartDTO.getActive());
        cart.setCartTotal(cartDTO.getCartTotal());
        return cart;
    }

    private static CartItems itemToEntity(CartItemsDTO dto) {
        CartItems item = new CartItems();
        item.setId(dto.getId());

        Product product = new Product();
        product.setId(dto.getProduct().getId());
        product.setName(dto.getProduct().getName());
        item.setProduct(product);

        item.setQuantity(dto.getQuantity());
        item.setItemValue(dto.getItemValue());

        return item;
    }
}