package br.com.kitchen.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CatalogDTO {
    private String name;
    private String slug;
}
