package br.com.kitchen.api.record;

import java.math.BigDecimal;

public record CreditRequest(BigDecimal amount, String description) {}
