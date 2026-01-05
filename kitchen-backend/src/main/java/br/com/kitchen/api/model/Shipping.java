package br.com.kitchen.api.model;

import br.com.kitchen.api.enumerations.ShippingStatus;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "shipping", schema = "kitchen")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Shipping implements Serializable{

    @Serial
    private static final long serialVersionUID = 4L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String carrier;

    private String method;

    private BigDecimal cost;

    private Long estimatedDays;

    private String trackingCode;

    @Enumerated(EnumType.STRING)
    private ShippingStatus status;

    @ManyToOne
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @ManyToOne
    @JoinColumn(name = "seller_id")
    private Seller seller;

    @PrePersist
    public void prePersist() {
        if (status == null) {
            status = ShippingStatus.NOT_STARTED;
        }
    }
}

