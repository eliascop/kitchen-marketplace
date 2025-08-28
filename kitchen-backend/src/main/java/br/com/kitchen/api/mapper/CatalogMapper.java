package br.com.kitchen.api.mapper;

import br.com.kitchen.api.dto.CatalogResponseDTO;
import br.com.kitchen.api.model.Catalog;

import java.util.List;
import java.util.stream.Collectors;

public class CatalogMapper {

    public static CatalogResponseDTO toDTO(Catalog catalog) {
        return new CatalogResponseDTO(
                catalog.getName(),
                catalog.getSlug());

    }

    public static List<CatalogResponseDTO> toDTOList(List<Catalog> catalog) {
        return catalog.stream()
                .map(CatalogMapper::toDTO)
                .collect(Collectors.toList());
    }
}
