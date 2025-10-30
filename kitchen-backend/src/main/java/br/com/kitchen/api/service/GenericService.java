package br.com.kitchen.api.service;

import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.lang.reflect.Field;
import java.math.BigDecimal;
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

    public Page<T> findByField(String fieldName, String value, Pageable pageable) {
        try {
            T entity = domainClass.getDeclaredConstructor().newInstance();

            Field field = domainClass.getDeclaredField(fieldName);
            field.setAccessible(true);
            Class<?> fieldType = field.getType();
            Object convertedValue = convertValue(value, fieldType);
            field.set(entity, convertedValue);
            ExampleMatcher matcher = ExampleMatcher.matching()
                    .withIgnoreNullValues()
                    .withMatcher(fieldName, match -> {
                        if (fieldType.equals(String.class)) {
                            match.contains().ignoreCase();
                        } else {
                            match.exact();
                        }
                    });

            Example<T> example = Example.of(entity, matcher);
            return repository.findAll(example, pageable);

        } catch (NoSuchFieldException e) {
            throw new RuntimeException("Campo não encontrado: " + fieldName, e);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar por campo: " + fieldName, e);
        }
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

    private Object convertValue(String value, Class<?> targetType) {
        if (value == null) return null;
        if (targetType.equals(String.class)) return value;
        if (targetType.equals(Long.class) || targetType.equals(long.class)) return Long.valueOf(value);
        if (targetType.equals(Integer.class) || targetType.equals(int.class)) return Integer.valueOf(value);
        if (targetType.equals(Double.class) || targetType.equals(double.class)) return Double.valueOf(value);
        if (targetType.equals(BigDecimal.class)) return new BigDecimal(value);
        if (targetType.equals(Boolean.class) || targetType.equals(boolean.class)) return Boolean.valueOf(value);
        if (Enum.class.isAssignableFrom(targetType)) return Enum.valueOf((Class<Enum>) targetType, value.toUpperCase());
        return value;
    }
}