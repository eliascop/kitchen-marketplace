package br.com.kitchen.api.enumerations;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    ROLE_USER,
    ROLE_SELLER,
    ROLE_ADMIN;

    @Override
    public String getAuthority() {
        return name();
    }
}
