package br.com.kitchen.api.dto.request;

import java.math.BigDecimal;

public record CreditRequest(BigDecimal amount, String description) {}
