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
    @JoinColumn(name = "product_sku_id",  nullable = false)
    private ProductSku productSku;

    private int quantity;

    private BigDecimal itemValue;

    public CartItems(Cart cart, ProductSku productSku,int quantity){
        this.cart = cart;
        this.productSku = productSku;
        this.quantity = quantity;
        this.itemValue = productSku.getPrice();
    }
}
