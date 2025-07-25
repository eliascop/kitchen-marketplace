package br.com.kitchen.api.dto;

import br.com.kitchen.api.enumerations.AddressType;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AddressDTO {
    private AddressType type;
    private String street;
    private String district;
    private String number;
    private String complement;
    private String city;
    private String state;
    private String zipCode;
    private String country;
}
