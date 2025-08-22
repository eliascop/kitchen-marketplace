package br.com.kitchen.api.service.payment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PaymentProviderFactory {

    private final Map<String, PaymentProvider> services;

    @Autowired
    public PaymentProviderFactory(List<PaymentProvider> implementations) {
        this.services = implementations.stream()
                .collect(Collectors.toMap(
                        impl -> impl.getName().toLowerCase(),
                        impl -> impl
                ));
    }

    public PaymentProvider getProvider(String name) {
        return Optional.ofNullable(services.get(name.toLowerCase()))
                .orElseThrow(() -> new IllegalArgumentException("Payment provider not found: " + name));
    }

}

