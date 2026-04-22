package com.thanlinardos.resource_server.service;

import com.thanlinardos.spring_enterprise_library.error.errorcodes.ErrorCode;
import com.thanlinardos.spring_enterprise_library.model.entity.base.BasicIdJpa;
import com.thanlinardos.spring_enterprise_library.model.mapped.base.BasicIdModel;
import com.thanlinardos.spring_enterprise_library.repository.base.BasicIdJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

@RequiredArgsConstructor
public class ModelServiceHelper<T extends BasicIdJpa, M extends BasicIdModel<T, M>, S extends BasicIdJpa, N extends BasicIdModel<S, N>> {

    private final BasicIdJpaRepository<T> repository;
    private final BasicIdJpaRepository<S> subRepository;

    @Transactional
    public M saveOrUpdateEntityFoundBy(M model, Supplier<Optional<T>> findEntityBy) {
        T entity = model.toEntity();
        repository.saveOrUpdateFoundByProperty(entity, findEntityBy);
        model.setId(entity.getId());
        return model;
    }

    @Transactional
    public N saveOrUpdateSubEntityFoundBy(N model, Supplier<Optional<S>> findEntityBy) {
        S entity = model.toEntity();
        subRepository.saveOrUpdateFoundByProperty(entity, findEntityBy);
        model.setId(entity.getId());
        return model;
    }

    @Transactional
    public M linkToModel(M model, Supplier<Optional<T>> findEntitySupplier, Function<T, Collection<S>> getRelations, S subEntityWithId) {
        return linkToModelPrivate(model, findEntitySupplier, getRelations, subEntityWithId);
    }

    @Transactional
    public M unlinkFromModel(M model, Supplier<Optional<T>> findEntitySupplier, Function<T, Collection<S>> getRelations, S subEntityWithId) {
        return unlinkFromModelPrivate(model, findEntitySupplier, getRelations, subEntityWithId);
    }

    @Transactional
    public M createWithLinks(M model, Collection<N> existingLinks, Set<N> newLinks, boolean unlinkOthers, Function<M, Optional<T>> findEntityBy, Function<T, Collection<S>> getRelations) {
        BiConsumer<M, Collection<N>> symmetricLinker = getSymmetricLinker(findEntityBy, getRelations);
        return ModelUtils.createWithLinks(model, existingLinks, newLinks, unlinkOthers, symmetricLinker);
    }

    @Transactional
    public N createSubWithLinks(N model, Collection<M> existingLinks, Set<M> newLinks, boolean unlinkOthers, Function<M, Optional<T>> findEntityBy, Function<T, Collection<S>> getRelations) {
        BiConsumer<N, M> linker = getSubLinker(findEntityBy, getRelations);
        BiConsumer<N, M> unlinker = getSubUnlinker(findEntityBy, getRelations);
        return ModelUtils.createSubWithLinks(model, existingLinks, newLinks, unlinkOthers, linker, unlinker);
    }

    private BiConsumer<M, Collection<N>> getSymmetricLinker(Function<M, Optional<T>> findEntityBy, Function<T, Collection<S>> getRelations) {
        return (m, subModels) -> symmetricLinkToModels(findEntityBy, getRelations, m, subModels);
    }

    private void symmetricLinkToModels(Function<M, Optional<T>> findEntityBy, Function<T, Collection<S>> getRelations, M m, Collection<N> subModels) {
        T entity = findEntity(m, () -> findEntityBy.apply(m));
        Collection<S> subEntitiesWithId = toEntityListWithId(subModels);
        repository.updateRelations(entity, getRelations, subEntitiesWithId);
    }

    private M linkToModelPrivate(M model, Supplier<Optional<T>> findEntitySupplier, Function<T, Collection<S>> getRelations, S subEntityWithId) {
        T entity = findEntity(model, findEntitySupplier);
        getRelations.apply(entity).add(subEntityWithId);
        repository.save(entity);
        return model.fromEntity(entity);
    }

    private M unlinkFromModelPrivate(M model, Supplier<Optional<T>> findEntitySupplier, Function<T, Collection<S>> getRelations, S subEntityWithId) {
        T entity = findEntity(model, findEntitySupplier);
        repository.removeRelation(entity, getRelations, subEntityWithId);
        return model.fromEntity(entity);
    }

    private BiConsumer<N, M> getSubUnlinker(Function<M, Optional<T>> findEntityBy, Function<T, Collection<S>> getRelations) {
        return (n, m) -> unlinkFromModelPrivate(m, () -> findEntityBy.apply(m), getRelations, n.toEntityOnlyId());
    }

    private BiConsumer<N, M> getSubLinker(Function<M, Optional<T>> findEntityBy, Function<T, Collection<S>> getRelations) {
        return (n, m) -> linkToModelPrivate(m, () -> findEntityBy.apply(m), getRelations, n.toEntityOnlyId());
    }

    private List<S> toEntityListWithId(Collection<N> subModels) {
        return subModels.stream()
                .map(N::toEntityOnlyId)
                .toList();
    }

    private T findEntity(M model, Supplier<Optional<T>> findEntitySupplier) {
        return findEntitySupplier.get()
                .orElseThrow(() -> ErrorCode.ILLEGAL_ARGUMENT.createCoreException("Entity of model {0} not found by method {1}", new Object[]{model.getClass().getSimpleName(), findEntitySupplier.toString()}));
    }
}
