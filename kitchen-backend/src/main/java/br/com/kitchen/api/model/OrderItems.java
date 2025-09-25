package br.com.kitchen.api.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "OrderItems", schema = "kitchen")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItems implements Serializable {

    @Serial
    private static final long serialVersionUID = 2L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    @JsonBackReference("order-orderItems")
    private Order order;

    @ManyToOne
    @JoinColumn(name = "seller_order_id")
    @JsonBackReference("sellerOrder-items")
    private SellerOrder sellerOrder;

    @ManyToOne
    @JoinColumn(name = "product_sku_id",  nullable = false)
    private ProductSku productSku;

    public int quantity;

    @JsonProperty("item_value")
    @Column(precision = 10, scale = 2)
    private BigDecimal itemValue;

    @ManyToOne
    private Seller seller;

    public void calculateItemValue() {
        BigDecimal totalValue = BigDecimal.ZERO;
        totalValue = totalValue.add(productSku.getPrice().multiply(new BigDecimal(quantity)));

        this.itemValue = totalValue;
    }

    public OrderItems(Order order, ProductSku productSku, int quantity, Seller seller) {
        this.order = order;
        this.productSku = productSku;
        this.quantity = quantity;
        this.seller = seller;
        calculateItemValue();
    }

}
