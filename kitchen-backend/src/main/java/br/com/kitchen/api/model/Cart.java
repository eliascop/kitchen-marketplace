package br.com.kitchen.api.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Cart", schema = "kitchen")
@Getter @Setter
@NoArgsConstructor
public class Cart implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @JsonManagedReference
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<CartItems> cartItems = new ArrayList<>();

    private LocalDateTime creation;

    private Boolean active;

    private BigDecimal cartTotal;

    @OneToOne(mappedBy = "cart")
    private Payment payment;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Shipping> shipping = new ArrayList<>();

    public BigDecimal getCartTotal(){
        return cartTotal != null ? cartTotal : BigDecimal.ZERO;
    }

    public void updateCartTotal() {
        if (cartItems == null || cartItems.isEmpty()) {
            this.cartTotal = BigDecimal.ZERO;
            return;
        }

        this.cartTotal = cartItems.stream()
                .map(item -> item.getProduct().getPrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
