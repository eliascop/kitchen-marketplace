package br.com.kitchen.api.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CatalogRequestDTO (
        Long    id,
        @NotBlank(message = "Catalog name is required")
        String name,
        String slug
){}