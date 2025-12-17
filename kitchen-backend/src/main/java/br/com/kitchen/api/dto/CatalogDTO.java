package br.com.kitchen.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class CatalogDTO {
    private Long id;
    private String name;
    private String slug;
}
