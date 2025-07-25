package br.com.kitchen.api.service.payment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PaymentServiceFactory {

    private final Map<String, PaymentService> services;

    @Autowired
    public PaymentServiceFactory(List<PaymentService> implementations) {
        this.services = implementations.stream()
                .collect(Collectors.toMap(PaymentService::getName, service -> service));
    }

    public PaymentService getService(String name) {
        return services.get(name.toLowerCase());
    }
}
