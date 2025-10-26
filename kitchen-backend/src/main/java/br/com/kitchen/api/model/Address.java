package br.com.kitchen.api.model;

import br.com.kitchen.api.enumerations.AddressType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Entity
@Table(name = "address", schema = "kitchen")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private AddressType type;

    @NotBlank(message = "Street must not be blank")
    private String street;

    @NotBlank(message = "Number must not be blank")
    private String number;

    private String complement;

    @NotBlank(message = "District must not be blank")
    private String district;

    @NotBlank(message = "City must not be blank")
    private String city;

    @NotBlank(message = "State must not be blank")
    private String state;

    @NotBlank(message = "ZipCode must not be blank")
    private String zipCode;

    @NotBlank(message = "Country must not be blank")
    private String country;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public boolean isShipping() {
        return AddressType.SHIPPING.equals(this.type);
    }

    public boolean isBilling() {
        return AddressType.BILLING.equals(this.type);
    }

}
