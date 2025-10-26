package br.com.kitchen.api.service;

import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public class GenericService<T, ID> {

    private final JpaRepository<T, ID> repository;
    private final Class<T> domainClass;

    public GenericService(JpaRepository<T, ID> repository, Class<T> domainClass) {
        this.repository = repository;
        this.domainClass = domainClass;
    }

    public Page<T> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public List<T> findAll() {
        return repository.findAll();
    }

    public Optional<T> findById(ID id) {
        return repository.findById(id);
    }

    public List<T> findByField(String fieldName, String value) {
        try {
            T entity = domainClass.getDeclaredConstructor().newInstance();
            entity.getClass()
                    .getMethod("set" + capitalize(fieldName), String.class)
                    .invoke(entity, value);

            ExampleMatcher matcher = ExampleMatcher.matching()
                    .withMatcher(fieldName, ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase());

            Example<T> example = Example.of(entity, matcher);
            return repository.findAll(example, Sort.by(Sort.Direction.ASC, fieldName));

        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar por campo: " + fieldName, e);
        }
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}