package br.com.kitchen.api.model;

import br.com.kitchen.api.enumerations.OrderStatus;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders", schema = "kitchen")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Order implements Serializable {

    @Serial
    private static final long serialVersionUID = 2L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT")
    private Long id;

    private LocalDateTime creation;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @JsonManagedReference("order-orderItems")
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<OrderItems> orderItems = new ArrayList<>();

    @JsonManagedReference("order-sellerOrders")
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SellerOrder> sellerOrders = new ArrayList<>();

    @Column(precision = 10, scale = 2)
    private BigDecimal total = BigDecimal.ZERO;

    public void updateOrderTotal() {
        if (orderItems == null || orderItems.isEmpty()) {
            this.total = BigDecimal.ZERO;
            return;
        }

        this.total = orderItems.stream()
                .map(item -> item.getProduct().getPrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @PrePersist
    public void prePersist() {
        if (creation == null) {
            creation = LocalDateTime.now();
        }
        if (status == null){
            status = OrderStatus.PREPARING;
        }
    }

    @ManyToOne
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "FK_order_user"))
    private User user;

    @OneToOne
    @JoinColumn(name = "payment_id", unique = true, foreignKey = @ForeignKey(name = "FK_order_payment"))
    private Payment payment;

    @ManyToOne
    @JoinColumn(name = "shipping_address_id", foreignKey = @ForeignKey(name = "FK_order_shipping_address"))
    private Address shippingAddress;

    @ManyToOne
    @JoinColumn(name = "billing_address_id", foreignKey = @ForeignKey(name = "FK_order_billing_address"))
    private Address billingAddress;

}
