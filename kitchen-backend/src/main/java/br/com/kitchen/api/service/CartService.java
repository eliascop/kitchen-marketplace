package br.com.kitchen.api.service;

import br.com.kitchen.api.dto.CartDTO;
import br.com.kitchen.api.dto.ProductDTO;
import br.com.kitchen.api.model.*;
import br.com.kitchen.api.repository.CartItemRepository;
import br.com.kitchen.api.repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Optional;

@Service
public class CartService extends GenericService<Cart, Long> {

    private final AddressService addressService;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductService productService;

    @Autowired
    public CartService(AddressService addressService,
                       CartRepository cartRepository,
                       CartItemRepository cartItemRepository,
                       ProductService productService) {
        super(cartRepository, Cart.class);
        this.addressService = addressService;
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productService = productService;
    }

    public Cart getOrCreateCart(User user) throws Exception{
        return cartRepository.findActiveCartByUserId(user.getId())
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    newCart.setCreation(LocalDateTime.now());
                    newCart.setActive(true);
                    newCart.setCartItems(new ArrayList<>());
                    newCart.setCartTotal(BigDecimal.ZERO);
                    newCart.setShippingMethods(new LinkedHashSet<>());
                    return cartRepository.save(newCart);
                });
    }

    @Transactional
    public Cart createCart(User user, CartDTO cartDTO) throws Exception{
        Cart cart = getOrCreateCart(user);

        cartDTO.getCartItems().forEach(itemDTO -> {
            addOrUpdateItem(cart, itemDTO.getProductDTO(), itemDTO.getQuantity());
        });

        cart.updateCartTotal();
        return cartRepository.save(cart);
    }

    private void addOrUpdateItem(Cart cart, ProductDTO productDTO, int quantity) {

        Product product = productService.findProductById(productDTO.getId());

        Optional<CartItems> existingItem = cart.getCartItems().stream()
                .filter(item -> item.getProduct().getId().equals(product.getId()))
                .findFirst();

        if (product.getSeller().isBlocked()) {
            throw new IllegalArgumentException("The seller of this product is blocked. Remove this item and send the cart again.");
        }

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
            } else {
                throw new IllegalArgumentException("Quantity must be positive when adding a new item.");
            }
        }
    }

    @Transactional
    public Cart manageItems(User user, Long productId, int quantity) throws Exception{
        Product p = productService.findProductById(productId);
        ProductDTO productDTO = ProductDTO.builder()
                .id(p.getId())
                .name(p.getName())
                .sku(p.getSkus().get(0).getSku())
                .build();

        Cart cart = getOrCreateCart(user);
        addOrUpdateItem(cart, productDTO, quantity);
        cart.updateCartTotal();
        return cartRepository.save(cart);
    }

    @Transactional
    public void removeItem(User user, Long itemId) {
        Cart cart = cartRepository.findActiveCartByUserId(user.getId())
                .orElseThrow(() -> new IllegalStateException("Cart not found for user"));

        Iterator<CartItems> iterator = cart.getCartItems().iterator();
        boolean removed = false;

        while (iterator.hasNext()) {
            CartItems item = iterator.next();
            if (item.getId().equals(itemId)) {
                iterator.remove();
                cartItemRepository.delete(item);
                removed = true;
                break;
            }
        }

        if (!removed) {
            throw new IllegalArgumentException("Item not found in cart");
        }

        if (cart.getCartItems().isEmpty()) {
            cartItemRepository.deleteByCartId(itemId);
        } else {
            cart.updateCartTotal();
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

    public Cart getActiveCartByUserId(Long userId) {
        return cartRepository.findActiveCartByUserId(userId)
                .orElseThrow(() -> new IllegalStateException("No active cart found"));
    }

    @Transactional
    public Cart updateCartAddresses(CartDTO cartDTO, Long userId){

        Address shippingAddress = addressService.getById(cartDTO.getShippingAddressId());
        Address billingAddress = addressService.getById(cartDTO.getBillingAddressId());

        Cart cart = getActiveCartByUserId(userId);
        cart.setShippingAddress(shippingAddress);
        cart.setBillingAddress(billingAddress);

        return cartRepository.save(cart);
    }

    public void save(Cart cart) {
        cartRepository.save(cart);
    }
}
