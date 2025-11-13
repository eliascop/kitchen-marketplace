package br.com.kitchen.api.model;

import br.com.kitchen.api.dto.StockHistoryDTO;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "productSku",
        schema = "kitchen",
        uniqueConstraints = @UniqueConstraint(columnNames = {"sku", "product_id"})
)
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductSku implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String sku;

    @Min(value = 1, message = "Price must be greater than zero")
    private BigDecimal price;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @OneToOne(mappedBy = "sku", cascade = CascadeType.ALL, orphanRemoval = true)
    private Stock stock;

    @Transient
    private List<StockHistoryDTO> stockHistory = new ArrayList<>();

    @OneToMany(mappedBy = "sku", cascade = CascadeType.ALL, orphanRemoval = true,fetch=FetchType.EAGER)
    private List<ProductAttribute> attributes = new ArrayList<>();

}

