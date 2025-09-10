package br.com.kitchen.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Stock", schema = "kitchen")
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "sku_id", nullable = false)
    private ProductSku sku;

    private int totalQuantity;
    private int reservedQuantity;
    private int soldQuantity;

    @ManyToOne
    @JoinColumn(name = "seller_id")
    @JsonIgnore
    private Seller seller;

    @Transient
    private String stockAction;

    public int getAvailableQuantity() {
        return totalQuantity - reservedQuantity-soldQuantity;
    }
}
