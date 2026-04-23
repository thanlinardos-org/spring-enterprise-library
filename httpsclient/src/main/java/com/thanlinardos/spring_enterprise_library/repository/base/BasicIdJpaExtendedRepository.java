package com.thanlinardos.spring_enterprise_library.repository.base;

import com.thanlinardos.spring_enterprise_library.model.entity.base.BasicIdJpa;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public interface BasicIdJpaExtendedRepository<T extends BasicIdJpa> {

    void saveFoundByProperty(T entity, Supplier<Optional<T>> findEntityBy);

    <S extends BasicIdJpa> void removeRelation(T entity, Function<T, Collection<S>> getRelations, S subEntityWithId);

    <S extends BasicIdJpa> void removeRelations(T entity, Function<T, Collection<S>> getRelations, Collection<S> subEntitiesWithId);

    <S extends BasicIdJpa> void updateRelations(T entity, Function<T, Collection<S>> getRelations, Collection<S> subEntitiesWithId);
}
