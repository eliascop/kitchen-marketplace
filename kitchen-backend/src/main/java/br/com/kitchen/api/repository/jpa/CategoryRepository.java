package br.com.kitchen.api.repository.jpa;

import br.com.kitchen.api.model.Category;

import java.util.Optional;

public interface CategoryRepository extends GenericRepository<Category, Long> {
    Optional<Category> findByNameIgnoreCase(String name);

}
