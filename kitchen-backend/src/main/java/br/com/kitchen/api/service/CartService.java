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
        return cartRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    newCart.setCreation(LocalDateTime.now());
                    return cartRepository.save(newCart);
                });
    }

    @Transactional
    public void addItem(User user, Product product, int quantity) {
        Cart cart = getOrCreateCart(user);

        Optional<CartItems> existingItem = cart.getCartItems().stream()
                .filter(item -> item.getProduct().getId().equals(product.getId()))
                .findFirst();

        if (existingItem.isPresent()) {
            CartItems item = existingItem.get();
            item.setQuantity(item.getQuantity() + quantity);
        } else {
            CartItems newItem = new CartItems();
            newItem.setCart(cart);
            newItem.setProduct(product);
            newItem.setQuantity(quantity);
            cart.getCartItems().add(newItem);
        }

        cartRepository.save(cart);
    }

    @Transactional
    public void removeItem(User user, Long productId) {
        Cart cart = cartRepository.findByUserId(user.getId())
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
        Cart cart = getOrCreateCart(user);
        cart.getCartItems().clear();
        cartItemRepository.deleteByCartId(cart.getId());
        cartRepository.save(cart);
    }

    @Transactional
    public void deleteCart(Cart cart) {
        cartItemRepository.deleteByCartId(cart.getId());
        cartRepository.delete(cart);
    }

    public Optional<Cart> findById(Long cartId) {
        return cartRepository.findById(cartId);
    }

    public Optional<Cart> getCartByIdAndUserId(Long id, Long userId){
        return cartRepository.findByIdAndUserId(id,userId);
    }

    public void save(Cart cart){
        cartRepository.save(cart);
    }
}
