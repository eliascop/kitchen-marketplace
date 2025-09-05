package br.com.kitchen.api.dto.request;

import java.math.BigDecimal;

public record DebitRequest(BigDecimal amount, String description) {}
