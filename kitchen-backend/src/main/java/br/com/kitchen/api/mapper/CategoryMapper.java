package br.com.kitchen.api.mapper;

import br.com.kitchen.api.dto.CategoryDTO;
import br.com.kitchen.api.model.Category;

import java.util.List;
import java.util.stream.Collectors;

public class CategoryMapper {

    public static CategoryDTO toDTO(Category category) {
        if (category == null) {
            return null;
        }
        return new CategoryDTO(
                category.getId(),
                category.getName());

    }

    public static List<CategoryDTO> toDTOList(List<Category> category) {
        return category.stream()
                .map(CategoryMapper::toDTO)
                .collect(Collectors.toList());
    }
}
