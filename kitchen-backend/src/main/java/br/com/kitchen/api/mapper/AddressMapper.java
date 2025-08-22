package br.com.kitchen.api.mapper;

import br.com.kitchen.api.dto.AddressDTO;
import br.com.kitchen.api.model.Address;
import br.com.kitchen.api.model.User;

import java.util.List;
import java.util.stream.Collectors;

public class AddressMapper {

    public static Address toEntity(AddressDTO dto, User user) {
        Address a = new Address();
        a.setType(dto.getType());
        a.setZipCode(dto.getZipCode());
        a.setStreet(dto.getStreet());
        a.setNumber(dto.getNumber());
        a.setComplement(dto.getComplement());
        a.setDistrict(dto.getDistrict());
        a.setCity(dto.getCity());
        a.setState(dto.getState());
        a.setCountry(dto.getCountry());
        a.setUser(user);
        return a;
    }

    public static List<Address> toEntityList(List<AddressDTO> dtos, User user) {
        return dtos.stream()
                .map(dto -> toEntity(dto, user))
                .collect(Collectors.toList());
    }

    public static AddressDTO toDTO(Address address) {
        AddressDTO dto = new AddressDTO();
        dto.setId(address.getId());
        dto.setType(address.getType());
        dto.setZipCode(address.getZipCode());
        dto.setStreet(address.getStreet());
        dto.setNumber(address.getNumber());
        dto.setComplement(address.getComplement());
        dto.setDistrict(address.getDistrict());
        dto.setCity(address.getCity());
        dto.setState(address.getState());
        dto.setCountry(address.getCountry());
        return dto;
    }

    public static List<AddressDTO> toDTOList(List<Address> addresses) {
        return addresses.stream()
                .map(AddressMapper::toDTO)
                .collect(Collectors.toList());
    }
}