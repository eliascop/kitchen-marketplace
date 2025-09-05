package br.com.kitchen.api.dto.request;

import br.com.kitchen.api.dto.ProductSkuDTO;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;

public record ProductRequestDTO(
        @NotBlank(message = "Product name is required")
        String name,

        @NotBlank(message = "Product description is required")
        String description,

        @NotNull(message = "Base price is required")
        @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
        BigDecimal basePrice,

        @NotBlank(message = "Catalog name is required")
        String catalog,

        @NotBlank(message = "Category name is required")
        String category,

        @NotEmpty(message = "At least one SKU is required")
        List<@NotNull ProductSkuDTO> skus
) {}
