package br.com.kitchen.api.model;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import br.com.kitchen.api.enumerations.ProductStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Entity
@Table(name = "product", schema = "kitchen")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Product implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT")
    private Long id;

    @NotBlank(message = "Name must not be blank")
    private String name;

    @NotBlank(message = "Description must not be blank")
    private String description;

    @NotBlank(message = "Price must not be blank")
    private BigDecimal price;

    @Column(name = "image_url")
    private String imageUrl;

    @ManyToOne(optional = false)
    @JoinColumn(name = "seller_id", nullable = false)
    private Seller seller;

    @ManyToOne
    @JoinColumn(name = "catalog_id")
    private Catalog catalog;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @Field(type = FieldType.Nested)
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch=FetchType.EAGER)
    private List<ProductSku> skus = new ArrayList<>();

    private LocalDateTime createdAt;

    private LocalDateTime activatedAt;

    @Enumerated(EnumType.STRING)
    private ProductStatus productStatus;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (productStatus == null){
            productStatus = ProductStatus.PENDING_INDEXING;
        }
    }
    
}
