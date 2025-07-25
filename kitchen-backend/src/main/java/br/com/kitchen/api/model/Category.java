package br.com.kitchen.api.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "category", schema = "kitchen")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Category implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT")
    private Long id;

    @NotBlank(message = "Name must not be blank")
    @Size(min = 4, message = "Username must be at least 8 characters long")
    private String name;

    @OneToMany(mappedBy = "category")
    private List<Product> products;

    public Category(String name){
        this.name = name;
    }
}