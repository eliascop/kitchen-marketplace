package br.com.kitchen.api.security;

import br.com.kitchen.api.model.Seller;
import br.com.kitchen.api.model.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Optional;

public record UserPrincipal(User user, Seller seller) implements UserDetails {

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.getRoles();
    }

    @Override
    @JsonIgnore
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getLogin();
    }

    public Long getUserId() {
        return user.getId();
    }

    public Optional<Seller> getSeller() {
        return Optional.ofNullable(seller);
    }

    public boolean isSeller() {
        return seller != null;
    }

    public boolean isAdmin() {
        return user.getRoles().stream()
                .anyMatch(r -> r.name().equals("ADMIN"));
    }

    public boolean isCustomer() {
        return user.getRoles().stream()
                .anyMatch(r -> r.name().equals("USER"));
    }

}
