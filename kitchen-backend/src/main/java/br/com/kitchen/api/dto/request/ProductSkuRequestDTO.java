package br.com.kitchen.api.dto.request;

import br.com.kitchen.api.dto.ProductAttributeDTO;
import br.com.kitchen.api.dto.ProductSkuDTO;
import br.com.kitchen.api.dto.response.StockResponseDTO;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


public record ProductSkuRequestDTO (
    Long id,

    @NotBlank(message = "SKU is required")
    String sku,

    @NotNull(message = "Base price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    BigDecimal price,

    @NotNull(message = "Stock is required")
    StockRequestDTO stock,

    @NotEmpty(message = "At least one Attribute is required")
    List<@NotNull ProductAttributeDTO> attributes
){}
