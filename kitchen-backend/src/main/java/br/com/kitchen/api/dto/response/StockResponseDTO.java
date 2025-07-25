package br.com.kitchen.api.dto.response;

public record StockResponseDTO(
        int totalQuantity,
        int reservedQuantity,
        int soldQuantity,
        int availableQuantity
) {}

