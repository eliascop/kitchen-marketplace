package br.com.kitchen.orderpreparingsimulator.app.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "orders")
@Data
public class Order {

    @Id
    private Long id;

    @Column(name = "status")
    private String status;

}
