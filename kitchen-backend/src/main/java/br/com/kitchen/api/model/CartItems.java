package br.com.kitchen.api.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name="CartItems", schema = "kitchen")
@Data
@NoArgsConstructor
public class CartItems implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Cart cart;

    @ManyToOne
    private Product product;

    private int quantity;

    private BigDecimal itemValue;

    public CartItems(Cart cart, Product product,int quantity){
        this.cart = cart;
        this.product = product;
        this.quantity = quantity;
        this.itemValue = product.getPrice();
    }
}
