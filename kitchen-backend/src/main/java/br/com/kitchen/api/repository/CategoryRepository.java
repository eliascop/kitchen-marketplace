package br.com.kitchen.api.repository;

import br.com.kitchen.api.model.Category;

import java.util.Optional;

public interface CategoryRepository extends GenericRepository<Category, Long> {
    Optional<Category> findByName(String name);
}
