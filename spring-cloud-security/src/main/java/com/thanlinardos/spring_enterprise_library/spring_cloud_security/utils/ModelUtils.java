package com.thanlinardos.spring_enterprise_library.spring_cloud_security.utils;

import com.thanlinardos.spring_enterprise_library.model.entity.base.BasicIdJpa;
import com.thanlinardos.spring_enterprise_library.model.mapped.base.BasicAuditableModel;
import com.thanlinardos.spring_enterprise_library.model.mapped.base.BasicIdModel;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Utility class for handling models extending BasicIdModel.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ModelUtils {

    /**
     * Safely retrieves the ID from a BasicIdModel, returning null if the model is null.
     *
     * @param model the BasicIdModel instance, which may be null
     * @return the ID of the model, or null if the model is null
     */
    public static <E extends BasicIdJpa, M extends BasicIdModel<E, M>> Long getIdFromModel(@Nullable M model) {
        return Optional.ofNullable(model)
                .map(BasicIdModel::getId)
                .orElse(null);
    }

    /**
     * Safely retrieves the ID from a nested BasicIdModel within another model, returning null if the outer model is null.
     *
     * @param model               the outer BasicIdModel instance, which may be null
     * @param nestedModelSupplier a function that extracts the nested BasicIdModel from the outer model
     * @param <M>                 the type of the outer model
     * @param <N>                 the type of the nested model
     * @return the ID of the nested model, or null if the outer model is null
     */
    public static <E extends BasicIdJpa, R extends BasicIdJpa, M extends BasicIdModel<E, M>, N extends BasicIdModel<R, N>> Long getIdFromNestedModel(@Nullable M model, Function<M, N> nestedModelSupplier) {
        return Optional.ofNullable(model)
                .map(t -> getIdFromModel(nestedModelSupplier.apply(t)))
                .orElse(null);
    }

    /**
     * Safely retrieves the ID from a nested BasicIdModel within another model, returning the ID from an alternative nested model if the outer model is null.
     *
     * @param model                 the outer BasicIdModel instance, which may be null
     * @param nestedModelSupplier   a function that extracts the primary nested BasicIdModel from the outer model
     * @param orNestedModelSupplier a function that extracts the alternative nested BasicIdModel from the outer model
     * @param <M>                   the type of the outer model
     * @param <N>                   the type of the nested models
     * @return the ID of the primary nested model, or the ID of the alternative nested model if the outer model is null
     */
    public static <E extends BasicIdJpa, R extends BasicIdJpa, T extends BasicIdJpa, M extends BasicIdModel<E, M>, N extends BasicIdModel<R, N>, L extends BasicIdModel<T, L>> Long getIdFromNestedModelOr(@Nullable M model, Function<M, N> nestedModelSupplier, Function<M, L> orNestedModelSupplier) {
        return Optional.ofNullable(model)
                .map(t -> getIdFromModel(nestedModelSupplier.apply(t)))
                .orElse(getIdFromNestedModel(model, orNestedModelSupplier));
    }

    public static <E extends BasicIdJpa, R extends BasicIdJpa, M extends BasicAuditableModel<E>, N extends BasicAuditableModel<R>> Long getIdFromNestedModel(@Nullable M model, Function<M, N> nestedModelSupplier) {
        return Optional.ofNullable(model)
                .map(t -> getIdFromModel(nestedModelSupplier.apply(t)))
                .orElse(null);
    }

    public static <E extends BasicIdJpa, R extends BasicIdJpa, T extends BasicIdJpa, M extends BasicAuditableModel<E>, N extends BasicAuditableModel<R>, L extends BasicAuditableModel<T>> Long getIdFromNestedModelOr(@Nullable M model, Function<M, N> nestedModelSupplier, Function<M, L> orNestedModelSupplier) {
        return Optional.ofNullable(model)
                .map(t -> getIdFromModel(nestedModelSupplier.apply(t)))
                .orElse(getIdFromNestedModel(model, orNestedModelSupplier));
    }

    @Nullable
    public static <E extends BasicIdJpa, M extends BasicIdModel<E, M>> M getModelFromIdOrNull(@Nullable Long entityId, Supplier<M> constructor) {
        return Optional.ofNullable(entityId)
                .map(id -> getModelFromId(id, constructor))
                .orElse(null);
    }

    public static <E extends BasicIdJpa, M extends BasicIdModel<E, M>> M getModelFromId(Long id, Supplier<M> constructor) {
        M instance = constructor.get();
        instance.setId(id);
        return instance;
    }

    @Nullable
    public static <E extends BasicIdJpa, M extends BasicIdModel<E, M>> M getModelFromEntity(@Nullable E entity, Function<E, M> modelMapper) {
        return Optional.ofNullable(entity)
                .map(modelMapper)
                .orElse(null);
    }

    public static <E extends BasicIdJpa, M extends BasicIdModel<E, M>> Set<M> getModelsSetFromEntities(@Nonnull Collection<E> entities, Function<E, M> modelMapper) {
        return entities.stream()
                .map(modelMapper)
                .collect(Collectors.toSet());
    }

    public static <E extends BasicIdJpa, M extends BasicIdModel<E, M>> Collection<M> getModelsFromEntities(@Nonnull Collection<E> entities, Function<E, M> modelMapper) {
        return entities.stream()
                .map(modelMapper)
                .toList();
    }
}
