package br.com.kitchen.api.mapper;

import br.com.kitchen.api.dto.CatalogDTO;
import br.com.kitchen.api.model.Catalog;

import java.util.List;
import java.util.stream.Collectors;

public class CatalogMapper {

    public static CatalogDTO toDTO(Catalog catalog) {
        return new CatalogDTO(
                catalog.getId(),
                catalog.getName(),
                catalog.getSlug());

    }

    public static List<CatalogDTO> toDTOList(List<Catalog> catalog) {
        return catalog.stream()
                .map(CatalogMapper::toDTO)
                .collect(Collectors.toList());
    }
}
