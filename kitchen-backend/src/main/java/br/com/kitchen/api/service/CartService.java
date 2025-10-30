package br.com.kitchen.api.service;

import br.com.kitchen.api.dto.CartDTO;
import br.com.kitchen.api.dto.ShippingDTO;
import br.com.kitchen.api.mapper.ShippingMapper;
import br.com.kitchen.api.model.*;
import br.com.kitchen.api.repository.jpa.CartItemRepository;
import br.com.kitchen.api.repository.jpa.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class CartService extends GenericService<Cart, Long> {

    private final AddressService addressService;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final SellerService sellerService;
    private final ProductSkuService productSkuService;

    @Autowired
    public CartService(AddressService addressService,
                       CartRepository cartRepository,
                       CartItemRepository cartItemRepository,
                       SellerService sellerService,
                       ProductSkuService productSkuService) {
        super(cartRepository, Cart.class);
        this.addressService = addressService;
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.sellerService = sellerService;
        this.productSkuService = productSkuService;
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

    private void addOrUpdateItem(Cart cart, ProductSku productSku, int quantity) {

        Optional<CartItems> existingItem = cart.getCartItems().stream()
                .filter(item -> item.getProductSku().getId().equals(productSku.getId()))
                .findFirst();

        if (productSku.getProduct().getSeller().isBlocked()) {
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
                cart.getCartItems().add(new CartItems(cart, productSku, quantity));
            } else {
                throw new IllegalArgumentException("Quantity must be positive when adding a new item.");
            }
        }
    }

    @Transactional
    public Cart manageItems(User user, Long skuId, int quantity) throws Exception{
        ProductSku productSku = productSkuService.getById(skuId);

        Cart cart = getOrCreateCart(user);
        addOrUpdateItem(cart, productSku, quantity);
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

    public Set<Shipping> validateShipping(Set<ShippingDTO> shippingSet, Cart cart){
        Set<Shipping> shipingSet = ShippingMapper.toEntityList(shippingSet);
        if(shipingSet.isEmpty()){
            throw new RuntimeException("No shipping was selected");
        }
        for (Shipping ship : shipingSet) {
            if(ship.getMethod().isEmpty() || ship.getCarrier().isEmpty())
                throw new RuntimeException("A problem was found on shipping. Review your options");

            Seller seller = sellerService.findById(ship.getSeller().getId())
                .map(s -> {if(s.isBlocked()) throw new RuntimeException("Some seller has restriction"); return s;
                }).orElseThrow(()-> new RuntimeException("No seller was found for this shipping item"));
           ship.setSeller(seller);
           ship.setCart(cart);
        }
        return shipingSet;
    }

    @Transactional
    public Cart updateShippingInfo(CartDTO cartDTO, Long userId){

        Address shippingAddress = addressService.getById(cartDTO.getShippingAddressId());
        Address billingAddress = addressService.getById(cartDTO.getBillingAddressId());

        Cart cart = getActiveCartByUserId(userId);
        Set<Shipping> shippingMethods = validateShipping(cartDTO.getShippingMethod(),cart);

        cart.setShippingAddress(shippingAddress);
        cart.setBillingAddress(billingAddress);

        cart.getShippingMethods().clear();
        cart.getShippingMethods().addAll(shippingMethods);

        return cartRepository.save(cart);
    }

    public void save(Cart cart) {
        cartRepository.save(cart);
    }
}
