package br.com.kitchen.api.record;

import java.math.BigDecimal;

public record DebitRequest(BigDecimal amount, String description) {}
