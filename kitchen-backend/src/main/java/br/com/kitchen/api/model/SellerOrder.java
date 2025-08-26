package br.com.kitchen.api.model;

import br.com.kitchen.api.enumerations.OrderStatus;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "seller_order", schema = "kitchen")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SellerOrder implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    @JsonBackReference("order-sellerOrders")
    private Order order;

    @ManyToOne
    @JoinColumn(name = "seller_id", nullable = false)
    private Seller seller;

    @OneToMany(mappedBy = "sellerOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("sellerOrder-items")
    private List<OrderItems> items = new ArrayList<>();

    @Column(precision = 10, scale = 2)
    private BigDecimal freightValue;

    @NotNull
    @Enumerated(EnumType.STRING)
    private OrderStatus status = OrderStatus.PREPARING;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @OneToOne
    @JoinColumn(name = "shipping_id")
    private Shipping shipping;

    public BigDecimal getTotalValue() {
        BigDecimal itemsTotal = items.stream()
                .map(OrderItems::getItemValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return itemsTotal.add(freightValue != null ? freightValue : BigDecimal.ZERO);
    }
}
