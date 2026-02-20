package br.com.kitchen.api.mapper;

import br.com.kitchen.api.dto.UserDTO;
import br.com.kitchen.api.model.User;

public class UserMapper {
    public static UserDTO toDTO(User user) {
        if (user == null) return null;

        return UserDTO.builder()
                .id(user.getId())
                .login(user.getLogin())
                .name(user.getName())
                .phone(user.getPhone())
                .email(user.getEmail())
                .roles(user.getRoles())
                .addresses(AddressMapper.toDTOList(user.getAddresses()))
                .build();
    }
}
