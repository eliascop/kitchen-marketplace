package br.com.kitchen.api.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Entity
@Table(name = "productAttributes", schema = "kitchen")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductAttribute implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String attributeName;

    @NotBlank
    private String attributeValue;

    @ManyToOne
    @JoinColumn(name = "sku_id", nullable = false)
    private ProductSku sku;
}

