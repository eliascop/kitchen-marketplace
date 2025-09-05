package br.com.kitchen.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CatalogResponseDTO {
    private String name;
    private String slug;
}
