package br.com.kitchen.api.model;

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

    @ManyToOne
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @ManyToOne
    @JoinColumn(name = "seller_id")
    private Seller seller;

    private String carrier;

    private String method;

    private BigDecimal cost;

    private Long estimatedDays;

    private String trackingCode;

    private String status;
}

