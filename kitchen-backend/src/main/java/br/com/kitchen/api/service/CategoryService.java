package br.com.kitchen.api.service;

import br.com.kitchen.api.model.Category;
import br.com.kitchen.api.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CategoryService extends GenericService<Category, Long> {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        super(categoryRepository, Category.class);
        this.categoryRepository = categoryRepository;
    }

    public Category findOrCreate(String categoryName) {
        return categoryRepository.findByName(categoryName)
                .orElseGet(() -> categoryRepository.save(new Category(categoryName)));
    }

    public Optional<Category> findByName(String categoryName) {
        return categoryRepository.findByName(categoryName);
    }
}
