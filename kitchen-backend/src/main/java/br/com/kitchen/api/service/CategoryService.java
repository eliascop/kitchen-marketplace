package br.com.kitchen.api.service;

import br.com.kitchen.api.model.Category;
import br.com.kitchen.api.repository.jpa.CategoryRepository;
import org.springframework.stereotype.Service;

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

}
