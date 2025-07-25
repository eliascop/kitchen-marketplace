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
    @JsonBackReference
    private Order order;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    public int quantity;

    @JsonProperty("item_value")
    @Column(precision = 10, scale = 2)
    private BigDecimal itemValue;

    public void calculateItemValue() {
        BigDecimal totalValue = BigDecimal.ZERO;
        totalValue = totalValue.add(product.getPrice().multiply(new BigDecimal(quantity)));

        this.itemValue = totalValue;
    }

}
