package br.com.kitchen.api.service;

import br.com.kitchen.api.model.*;
import br.com.kitchen.api.repository.CartItemRepository;
import br.com.kitchen.api.repository.CartRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.Optional;

@Service
public class CartService extends GenericService<Cart, Long>{

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    public CartService(CartRepository cartRepository,
                       CartItemRepository cartItemRepository) {
        super(cartRepository, Cart.class);
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
    }

    public Cart getOrCreateCart(User user) {
        return cartRepository.findActiveCartByUserId(user.getId())
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    newCart.setCreation(LocalDateTime.now());
                    newCart.setActive(true);
                    return cartRepository.save(newCart);
                });
    }

    @Transactional
    public Cart manageItems(User user, Product product, int quantity) {
        Cart cart = getOrCreateCart(user);

        Optional<CartItems> existingItem = cart.getCartItems().stream()
                .filter(item -> item.getProduct().getId().equals(product.getId()))
                .findFirst();

        if (existingItem.isPresent()) {
            CartItems item = existingItem.get();
            int newQuantity = item.getQuantity() + quantity;
            if (newQuantity <= 0) {
                cart.getCartItems().remove(item);
                cartItemRepository.delete(item);
            } else {
                item.setQuantity(newQuantity);
            }
        } else {
            if (quantity > 0) {
                cart.getCartItems().add(new CartItems(cart, product, quantity));
            }else{
                throw new IllegalArgumentException("Quantity must be positive when adding a new item.");
            }
        }
        cart.updateCartTotal();
        return cartRepository.save(cart);
    }

    @Transactional
    public void removeItem(User user, Long productId) {
        Cart cart = cartRepository.findActiveCartByUserId(user.getId())
                .orElseThrow(() -> new IllegalStateException("Cart not found for user"));

        Iterator<CartItems> iterator = cart.getCartItems().iterator();
        boolean removed = false;

        while (iterator.hasNext()) {
            CartItems item = iterator.next();
            if (item.getProduct().getId().equals(productId)) {
                iterator.remove();
                cartItemRepository.delete(item);
                break;
            }
        }

        if (!removed) {
            throw new IllegalArgumentException("Product not found in cart");
        }

        if (cart.getCartItems().isEmpty()) {
            cartItemRepository.deleteByCartId(cart.getId());
            cartRepository.delete(cart);
        } else {
            cartRepository.save(cart);
        }

    }

    @Transactional
    public void clearCart(User user) {
        Cart cart = cartRepository.findActiveCartByUserId(user.getId())
                .orElseThrow(() -> new IllegalStateException("Cart not found for user"));

        cart.getCartItems().clear();
        cartItemRepository.deleteByCartId(cart.getId());
        cartRepository.save(cart);
    }

    public Optional<Cart> getActiveCartByUserId(Long userId){
        return cartRepository.findActiveCartByUserId(userId);
    }

    public void save(Cart cart){
        cartRepository.save(cart);
    }
}
