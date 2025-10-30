package br.com.kitchen.api.repository.jpa.impl;

import br.com.kitchen.api.repository.jpa.GenericRepository;
import jakarta.persistence.EntityManager;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import java.io.Serializable;

public class GenericRepositoryImpl<T, ID extends Serializable>
        extends SimpleJpaRepository<T, ID>
        implements GenericRepository<T, ID> {

    private final EntityManager entityManager;

    public GenericRepositoryImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
        this.entityManager = entityManager;
    }

    @Override
    public Class<T> getDomainClass() {
        return super.getDomainClass();
    }
}