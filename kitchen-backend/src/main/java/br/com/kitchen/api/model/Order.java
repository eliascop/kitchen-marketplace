package br.com.kitchen.api.model;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "orders", schema = "kitchen")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Order implements Serializable {

    @Serial
    private static final long serialVersionUID = 2L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT")
    private Long id;

    @JsonProperty("creation")
    private Date creation;

    @JsonProperty("status")
    private String status;

    @JsonManagedReference
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<OrderItems> orderItems = new ArrayList<>();

    @Column(precision = 10, scale = 2)
    private BigDecimal total = BigDecimal.ZERO;

    @JsonProperty("user")
    @ManyToOne
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "FK_order_user"))
    private User user;

    @PrePersist
    public void prePersist() {
        if (creation == null) {
            creation = new Date();
        }
        if (status == null || status.isEmpty()){
            status = "PENDING";
        }
    }

    @Column(name = "payment_id")
    private String paymentId;

    public void calculateTotal() {
        this.total = orderItems.stream()
                .map(OrderItems::getItemValue)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
