package br.com.kitchen.api.dto;

import br.com.kitchen.api.enumerations.Role;
import br.com.kitchen.api.mapper.AddressMapper;
import br.com.kitchen.api.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDTO {

    private Long id;
    private String login;
    private String name;
    private String phone;
    private String email;
    private Set<Role> roles;
    private List<AddressDTO> addresses;

    public UserDTO(User user) {
        this.id = user.getId();
        this.login = user.getLogin();
        this.name = user.getName();
        this.phone = user.getPhone();
        this.email = user.getEmail();
        this.roles = user.getRoles();
        this.addresses = AddressMapper.toDTOList(user.getAddresses());
    }
}
