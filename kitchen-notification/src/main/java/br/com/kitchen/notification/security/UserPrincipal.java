package br.com.kitchen.notification.security;

public record UserPrincipal(Long userId, boolean seller, boolean customer) {
    public boolean isSeller() {
        return seller;
    }

    public boolean isCustomer() {
        return customer;
    }
}
