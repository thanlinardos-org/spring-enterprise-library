package com.thanlinardos.spring_enterprise_library.repository.base;

import com.thanlinardos.spring_enterprise_library.model.entity.base.BasicIdJpa;
import jakarta.persistence.EntityManager;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public class BasicIdJpaExtendedRepositoryImpl<T extends BasicIdJpa> extends SimpleJpaRepository<T, Long> implements BasicIdJpaExtendedRepository<T> {

    private final EntityManager entityManager;

    public BasicIdJpaExtendedRepositoryImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
        this.entityManager = entityManager;
    }

    @Transactional
    @Override
    public void saveFoundByProperty(T entity, Supplier<Optional<T>> findEntityBy) {
        findEntityBy.get()
                .map(BasicIdJpa::getId)
                .ifPresent(entity::setId);
        entity = super.save(entity);
        flushAndRefresh(entity);
    }

    @Transactional
    @Override
    public <S extends BasicIdJpa> void removeRelation(T entity, Function<T, Collection<S>> getRelations, S subEntityWithId) {
        getRelations.apply(entity).remove(subEntityWithId);
        entityManager.persist(entity);
        flushAndRefresh(entity);
    }

    @Transactional
    @Override
    public <S extends BasicIdJpa> void removeRelations(T entity, Function<T, Collection<S>> getRelations, Collection<S> subEntitiesWithId) {
        getRelations.apply(entity).removeAll(subEntitiesWithId);
        entityManager.persist(entity);
        flushAndRefresh(entity);
    }

    @Transactional
    @Override
    public <S extends BasicIdJpa> void updateRelations(T entity, Function<T, Collection<S>> getRelations, Collection<S> subEntitiesWithId) {
        Collection<S> relations = getRelations.apply(entity);
        relations.clear();
        relations.addAll(subEntitiesWithId);
        entityManager.persist(entity);
        flushAndRefresh(entity);
    }

    private void flushAndRefresh(T entity) {
        entityManager.flush();
        entityManager.refresh(entity);
    }
}
